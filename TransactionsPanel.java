import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class TransactionsPanel extends JPanel {
    private BankWelcomePage parentFrame;

    private void logTransaction(String message) {
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(timestamp + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransactionsPanel(BankWelcomePage parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // Soft pastel background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        Color boxBg = new Color(250, 250, 250);
        Color radioBg = new Color(245, 245, 245);

        // Transaction Type Panel
        JPanel transactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionPanel.setBackground(boxBg);
        transactionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "Transaction Type", TitledBorder.LEFT, TitledBorder.TOP, labelFont));

        JRadioButton depositButton = new JRadioButton("Deposit");
        JRadioButton withdrawButton = new JRadioButton("Withdraw");
        JRadioButton sendButton = new JRadioButton("Send Money");   // <-- ADDED
        JRadioButton requestButton = new JRadioButton("Request Money"); // <-- ADDED
        depositButton.setBackground(radioBg);
        withdrawButton.setBackground(radioBg);
        sendButton.setBackground(radioBg);
        requestButton.setBackground(radioBg);

        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(depositButton);
        actionGroup.add(withdrawButton);
        actionGroup.add(sendButton);
        actionGroup.add(requestButton);

        transactionPanel.add(depositButton);
        transactionPanel.add(withdrawButton);
        transactionPanel.add(sendButton);
        transactionPanel.add(requestButton);

        // Account Type Panel
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountPanel.setBackground(boxBg);
        accountPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "Account Type", TitledBorder.LEFT, TitledBorder.TOP, labelFont));

        JRadioButton checkingButton = new JRadioButton("Checking");
        JRadioButton savingsButton = new JRadioButton("Savings");
        checkingButton.setBackground(radioBg);
        savingsButton.setBackground(radioBg);

        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(checkingButton);
        accountGroup.add(savingsButton);

        accountPanel.add(checkingButton);
        accountPanel.add(savingsButton);

        // Amount Panel
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.setBackground(boxBg);
        amountPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "Amount", TitledBorder.LEFT, TitledBorder.TOP, labelFont));

        JTextField amountField = new JTextField(12);
        amountField.setFont(fieldFont);
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        amountPanel.add(amountField);

        // Confirm Button
        JButton confirmButton = new JButton("OK");
        confirmButton.setBackground(new Color(30, 144, 255));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setPreferredSize(new Dimension(100, 35));

        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(transactionPanel, gbc);

        gbc.gridy = 1;
        add(accountPanel, gbc);

        gbc.gridy = 2;
        add(amountPanel, gbc);

        gbc.gridy = 3;
        add(confirmButton, gbc);

        // Action Listener
        confirmButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean isDeposit = depositButton.isSelected();
                boolean isWithdraw = withdrawButton.isSelected();
                boolean isSend = sendButton.isSelected();      // <-- Added
                boolean isRequest = requestButton.isSelected();// <-- Added
                boolean isChecking = checkingButton.isSelected();
                boolean isSavings = savingsButton.isSelected();

                if (!(isDeposit || isWithdraw || isSend || isRequest) || !(isChecking || isSavings)) {
                    JOptionPane.showMessageDialog(this, "Please select all options.", "Message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String accountType = isChecking ? "Checking" : "Savings";

                if (isDeposit) {
                    if (isChecking)
                        parentFrame.updateCheckingBalance(amount);
                    else
                        parentFrame.updateSavingsBalance(amount);

                    parentFrame.updateCredentialsFile();
                    logTransaction("Deposited $" + amount + " into " + accountType);
                    JOptionPane.showMessageDialog(this, "Deposited $" + amount + " to " + accountType);

                } else if (isWithdraw) {
                    boolean success;
                    if (isChecking)
                        success = parentFrame.updateCheckingBalance(-amount);
                    else
                        success = parentFrame.updateSavingsBalance(-amount);

                    if (success) {
                        parentFrame.updateCredentialsFile();
                        logTransaction("Withdrew $" + amount + " from " + accountType);
                        JOptionPane.showMessageDialog(this, "Withdrew $" + amount + " from " + accountType);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient funds for this withdrawal.", "Withdraw",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if (isSend) {
                    String otherUsername = JOptionPane.showInputDialog(this, "Enter username to send money to:");
                    String pin = JOptionPane.showInputDialog(this, "Enter your 4-digit PIN:");

                    if (parentFrame.verifyPin(pin)) {
                        boolean success = parentFrame.sendMoneyToUser(otherUsername, amount, isChecking);
                        if (success) {
                            logTransaction("Sent $" + amount + " to " + otherUsername + " from " + accountType);
                            JOptionPane.showMessageDialog(this, "Money sent successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to send money. Maybe insufficient balance.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Wrong PIN. Transaction cancelled.");
                    }
                } else if (isRequest) {
                    String otherUsername = JOptionPane.showInputDialog(this, "Enter username to request money from:");
                    parentFrame.logRequestToUser(otherUsername, amount);
                    logTransaction("Requested $" + amount + " from " + otherUsername);
                    JOptionPane.showMessageDialog(this, "Money request sent!");
                }

                parentFrame.goToHomeTab();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Message",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
