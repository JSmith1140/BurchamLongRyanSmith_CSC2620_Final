import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionsPanel extends JPanel {
    private BankWelcomePage parentFrame;

    public TransactionsPanel(BankWelcomePage parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel actionLabel = new JLabel("Select Transaction:");
        JRadioButton depositButton = new JRadioButton("Deposit");
        JRadioButton withdrawButton = new JRadioButton("Withdraw");
        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(depositButton);
        actionGroup.add(withdrawButton);

        JLabel accountLabel = new JLabel("Choose Account:");
        JRadioButton checkingButton = new JRadioButton("Checking");
        JRadioButton savingsButton = new JRadioButton("Savings");
        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(checkingButton);
        accountGroup.add(savingsButton);

        JLabel amountLabel = new JLabel("Enter Amount:");
        JTextField amountField = new JTextField(10);

        JButton confirmButton = new JButton("OK");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; add(actionLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(depositButton, gbc);
        gbc.gridx = 2; gbc.gridy = 0; add(withdrawButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(accountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(checkingButton, gbc);
        gbc.gridx = 2; gbc.gridy = 1; add(savingsButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(amountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; add(amountField, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1; add(confirmButton, gbc);

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

                // Redirect to Home
                parentFrame.goToHomeTab();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.");
            }
        });
    }
}
