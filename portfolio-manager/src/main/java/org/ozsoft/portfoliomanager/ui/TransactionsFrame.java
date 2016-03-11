package org.ozsoft.portfoliomanager.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ozsoft.portfoliomanager.ui.table.TransactionsTable;

public class TransactionsFrame extends JDialog {

    private static final long serialVersionUID = -4942077142767084610L;

    private final TransactionsTable transactionsTable;

    public TransactionsFrame(MainFrame mainFrame) {
        super(mainFrame, "Transactions", true);

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton button = new JButton("Add");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });
        buttonPanel.add(button);

        add(buttonPanel, BorderLayout.NORTH);

        transactionsTable = new TransactionsTable(mainFrame);
        add(new JScrollPane(transactionsTable), BorderLayout.CENTER);

        transactionsTable.update();

        setSize(800, 600);
        setLocationRelativeTo(mainFrame);
    }

    private void addTransaction() {
        transactionsTable.addTransaction();
    }
}
