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
    private String username;
    private BankWelcomePage parentFrame;

    private JTextArea txtTransactions;

    public AccountTab(String username, double checking, double savings, double accountNumber,
                      BankWelcomePage parentFrame) {
        this.username = username;
        this.checkingBalance = checking;
        this.savingsBalance = savings;
        this.accountNumber = accountNumber;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255)); // Soft pastel background

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Transfer", createTransferPanel());
        tabbedPane.addTab("Transaction history", createTransactionPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Exit Button
        JButton btnExit = new JButton("Exit to Home");
        btnExit.setBackground(new Color(30, 144, 255)); // Blue background
        btnExit.setForeground(Color.WHITE);              // White text
        btnExit.setFocusPainted(false);
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setPreferredSize(new Dimension(150, 40));
        btnExit.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new BankWelcomePage(username, checkingBalance, savingsBalance, accountNumber).setVisible(true);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.add(btnExit);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));

        JRadioButton toChecking = new JRadioButton("To Checking");
        JRadioButton toSavings = new JRadioButton("To Savings");
        ButtonGroup group = new ButtonGroup();
        group.add(toChecking);
        group.add(toSavings);

        JTextField txtAmount = new JTextField();
        txtAmount.setMaximumSize(new Dimension(200, 25));

        JButton btnTransfer = new JButton("Transfer");
        btnTransfer.setBackground(new Color(30, 144, 255));
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFocusPainted(false);
        btnTransfer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTransfer.setPreferredSize(new Dimension(150, 40));

        btnTransfer.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                if (toChecking.isSelected()) {
                    if (savingsBalance >= amount) {
                        savingsBalance -= amount;
                        checkingBalance += amount;
                        logTransaction("Transferred $" + amount + " to Checking");

                        parentFrame.setCheckingBalance(checkingBalance);
                        parentFrame.setSavingsBalance(savingsBalance);

                        parentFrame.updateCredentialsFile();
                        JOptionPane.showMessageDialog(this, "Successfully transferred to Checking.");
                        SwingUtilities.getWindowAncestor(this).dispose();
                        new BankWelcomePage(username, checkingBalance, savingsBalance, accountNumber).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient Savings Balance.", "Transfer",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else if (toSavings.isSelected()) {
                    if (checkingBalance >= amount) {
                        checkingBalance -= amount;
                        savingsBalance += amount;
                        logTransaction("Transferred $" + amount + " to Savings");

                        parentFrame.setCheckingBalance(checkingBalance);
                        parentFrame.setSavingsBalance(savingsBalance);

                        parentFrame.updateCredentialsFile();
                        JOptionPane.showMessageDialog(this, "Successfully transferred to Savings.");
                        SwingUtilities.getWindowAncestor(this).dispose();
                        new BankWelcomePage(username, checkingBalance, savingsBalance, accountNumber).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient Checking Balance.", "Transfer",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an account.", "Transfer",
                            JOptionPane.ERROR_MESSAGE);
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
        panel.setBackground(new Color(240, 248, 255));

        txtTransactions = new JTextArea(15, 30);
        txtTransactions.setEditable(false);
        txtTransactions.setBackground(new Color(240, 248, 255));

        panel.add(new JScrollPane(txtTransactions), BorderLayout.CENTER);

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
