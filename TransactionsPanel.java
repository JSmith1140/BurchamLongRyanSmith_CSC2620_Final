import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TransactionsPanel extends JPanel {
    private BankWelcomePage parentFrame;

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
        depositButton.setBackground(radioBg);
        withdrawButton.setBackground(radioBg);

        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(depositButton);
        actionGroup.add(withdrawButton);

        transactionPanel.add(depositButton);
        transactionPanel.add(withdrawButton);

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
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        amountPanel.add(amountField);

        // Confirm Button
        JButton confirmButton = new JButton("OK");
        confirmButton.setBackground(new Color(30, 144, 255));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setPreferredSize(new Dimension(100, 35));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
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
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }

                boolean isDeposit = depositButton.isSelected();
                boolean isWithdraw = withdrawButton.isSelected();
                boolean isChecking = checkingButton.isSelected();
                boolean isSavings = savingsButton.isSelected();

                if (!(isDeposit || isWithdraw) || !(isChecking || isSavings)) {
                    JOptionPane.showMessageDialog(this, "Please select all options.");
                    return;
                }

                if (isDeposit) {
                    if (isChecking) parentFrame.updateCheckingBalance(amount);
                    else parentFrame.updateSavingsBalance(amount);
                    JOptionPane.showMessageDialog(this, "Deposited $" + amount + " to " + (isChecking ? "Checking" : "Savings"));
                } else {
                    boolean success;
                    if (isChecking) success = parentFrame.updateCheckingBalance(-amount);
                    else success = parentFrame.updateSavingsBalance(-amount);

                    if (success) {
                        JOptionPane.showMessageDialog(this, "Withdrew $" + amount + " from " + (isChecking ? "Checking" : "Savings"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient funds for this withdrawal.");
                        return;
                    }
                }

                parentFrame.goToHomeTab();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.");
            }
        });
    }
}
