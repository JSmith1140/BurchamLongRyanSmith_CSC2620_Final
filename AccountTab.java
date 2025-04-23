import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountTab extends JPanel {
    private double checkingBalance;
    private double savingsBalance;
    private final double accountNumber;

    private JTextArea txtTransactions;

    public AccountTab(double checking, double savings, double accountNumber) {
        this.checkingBalance = checking;
        this.savingsBalance = savings;
        this.accountNumber = accountNumber;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Balances", createBalancePanel());
        tabbedPane.addTab("Transfer", createTransferPanel());
        tabbedPane.addTab("Transaction history", createTransactionPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lblChecking = new JLabel("Checking: $" + String.format("%.2f", checkingBalance));
        JLabel lblSavings = new JLabel("Savings: $" + String.format("%.2f", savingsBalance));
        panel.add(lblChecking);
        panel.add(lblSavings);
        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JRadioButton toChecking = new JRadioButton("To Checking");
        JRadioButton toSavings = new JRadioButton("To Savings");
        ButtonGroup group = new ButtonGroup();
        group.add(toChecking);
        group.add(toSavings);

        JTextField txtAmount = new JTextField();
        txtAmount.setMaximumSize(new Dimension(200, 25));

        JButton btnTransfer = new JButton("Transfer");

        btnTransfer.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                if (toChecking.isSelected()) {
                    if (savingsBalance >= amount) {
                        savingsBalance -= amount;
                        checkingBalance += amount;
                        logTransaction("Transferred $" + amount + " to Checking");
                        JOptionPane.showMessageDialog(this, "Successfully transferred to Checking.");
                        SwingUtilities.getWindowAncestor(this).dispose();
                        new BankWelcomePage(checkingBalance, savingsBalance, accountNumber).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient savings balance.");
                    }
                } else if (toSavings.isSelected()) {
                    if (checkingBalance >= amount) {
                        checkingBalance -= amount;
                        savingsBalance += amount;
                        logTransaction("Transferred $" + amount + " to Savings");
                        JOptionPane.showMessageDialog(this, "Successfully transferred to Savings.");
                        SwingUtilities.getWindowAncestor(this).dispose();
                        new BankWelcomePage(checkingBalance, savingsBalance, accountNumber).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient checking balance.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an account.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount.");
            }
        });

        panel.add(new JLabel("Enter amount to transfer:"));
        panel.add(txtAmount);
        panel.add(toChecking);
        panel.add(toSavings);
        panel.add(btnTransfer);

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        txtTransactions = new JTextArea(15, 30);
        txtTransactions.setEditable(false);

        JButton btnExit = new JButton("Exit to Home");
        btnExit.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new BankWelcomePage(checkingBalance, savingsBalance, accountNumber).setVisible(true);
        });

        panel.add(new JScrollPane(txtTransactions), BorderLayout.CENTER);
        panel.add(btnExit, BorderLayout.SOUTH);

        loadTransactions();
        return panel;
    }

    private void logTransaction(String message) {
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(timestamp + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        txtTransactions.setText("");
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                txtTransactions.append(line + "\n");
            }
        } catch (IOException e) {
            txtTransactions.setText("No transactions found.");
        }
    }
}
