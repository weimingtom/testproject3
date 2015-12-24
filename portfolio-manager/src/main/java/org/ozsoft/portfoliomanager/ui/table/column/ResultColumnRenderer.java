package org.ozsoft.portfoliomanager.ui.table.column;

import java.awt.Color;

import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.ui.UIConstants;

public class ResultColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -2184616889371886011L;

    private static final int DEFAULT_DECIMAL_PRECISION = 2;

    private final String positiveFormat;

    private final String negativeFormat;

    private Color textColor;

    public ResultColumnRenderer() {
        this(DEFAULT_DECIMAL_PRECISION);
    }

    public ResultColumnRenderer(int decimalPrecision) {
        if (decimalPrecision < 0) {
            throw new IllegalArgumentException("Invalid decimalPrecision; must be equal to 0 or greater");
        }
        positiveFormat = String.format("$ %%,.%df", decimalPrecision);
        negativeFormat = String.format("($ %%,.%df)", decimalPrecision);
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof Double) {
            double numericValue = (double) value;
            if (numericValue > 0.0) {
                textColor = UIConstants.DARKER_GREEN;
                return String.format(positiveFormat, numericValue);
            } else if (numericValue < 0.0) {
                textColor = Color.RED;
                return String.format(negativeFormat, Math.abs(numericValue));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Color getForeground() {
        return textColor;
    }
}
