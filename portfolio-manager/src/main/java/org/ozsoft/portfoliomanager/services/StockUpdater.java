// This file is part of the 'portfolio-manager' (Portfolio Manager)
// project, an open source stock portfolio manager application
// written in Java.
//
// Copyright 2015 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.domain.Exchange;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Thread that updates the share price of a single stock. <br />
 * <br />
 * 
 * Grabs the latest, real-time share price from the stock's Yahoo! Finance stock summary page (HTML).
 * 
 * @author Oscar Stigter
 */
public class StockUpdater extends Thread {

    private static final String YAHOO_QUOTE_URI = "http://finance.yahoo.com/q?s=%s";

    private static final String MORNINGSTAR_QUOTE_URI = "http://www.morningstar.com/stocks/%s/%s/quote.html";

    private static final Pattern YAHOO_QUOTE_PATTERN = Pattern
            .compile("<span class=\"rtq_exch\">.*?(NYSE|NasdaqGS).*?</span>.*<span id=\"yfs_l84_.*?\">(.*?)</span>.*<span id=\"yfs_c63_.*?\">.*? class=\"(?:neg_arrow|pos_arrow)\" alt=\"(Up|Down)\">.*<span id=\"yfs_p43_.*?\">\\((.*?)%\\)</span>(?s).*>P/E <.*? class=\"yfnc_tabledata1\">(.*?)</td>.*>Div &amp; Yield:</th><td class=\"yfnc_tabledata1\">(.*?) \\(");

    private static final Pattern MORNINGSTAR_QUOTE_PATTERN = Pattern.compile("\"starRating\":([0-9])");

    private static final Logger LOGGER = LogManager.getLogger(StockUpdater.class);

    private final Stock stock;

    private final HttpPageReader httpPageReader;

    private boolean isFinished = false;

    private boolean isUpdated = false;

    public StockUpdater(Stock stock, HttpPageReader httpPageReader) {
        this.stock = stock;
        this.httpPageReader = httpPageReader;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Stock getStock() {
        return stock;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public void run() {
        try {
            String content = httpPageReader.read(String.format(YAHOO_QUOTE_URI, stock.getSymbol()));
            Matcher m = YAHOO_QUOTE_PATTERN.matcher(content);
            if (m.find()) {
                String exchangeText = m.group(1);
                if (exchangeText.equals("NYSE")) {
                    stock.setExchange(Exchange.NYSE);
                } else if (exchangeText.equals("NasdaqGS")) {
                    stock.setExchange(Exchange.NASDAQ);
                } else {
                    stock.setExchange(Exchange.UNKNOWN);
                }
                double price = Double.parseDouble(m.group(2));
                double changePerc = Double.parseDouble(m.group(4));
                if (m.group(3).equals("Down")) {
                    changePerc *= -1;
                }
                String peRatioText = m.group(5);
                double peRatio = peRatioText.equals("N/A") ? -1.0 : Double.parseDouble(peRatioText);
                String divRateString = m.group(6);
                double divRate = divRateString.equals("N/A") ? 0.0 : Double.parseDouble(divRateString);

                if (price != stock.getPrice()) {
                    stock.setPrice(price);
                    stock.setChangePerc(changePerc);
                    stock.setPeRatio(peRatio);
                    stock.setDivRate(divRate);
                    isUpdated = true;
                }

                int starRating = -1;
                String exchangeId = (stock.getExchange() == Exchange.NYSE) ? "xnys" : "xnas";
                content = httpPageReader.read(String.format(MORNINGSTAR_QUOTE_URI, exchangeId, stock.getSymbol()));
                m = MORNINGSTAR_QUOTE_PATTERN.matcher(content);
                if (m.find()) {
                    starRating = Integer.parseInt(m.group(1));
                }
                stock.setStarRating(starRating);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Could not get update for stock '%s'", stock), e);
        }

        isFinished = true;
    }
}