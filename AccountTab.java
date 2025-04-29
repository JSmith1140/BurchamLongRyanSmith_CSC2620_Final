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
