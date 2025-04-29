import java.net.Socket; 
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Scanner;

public class BankWelcomePage extends JFrame {
    private double checkingBalance;
    private double savingsBalance;
    private double prevCheckingBalance = 0;
    private double prevSavingsBalance = 0;
    private double accountNumber;
    private String username;
    private JTabbedPane tabbedPane;

    private static final String SERVER_IP = "10.1.40.19"; // <-- Set to your server machine IP
    private static final int SERVER_PORT = 5000;

    private Socket liveSocket;
    private BufferedReader liveIn;
    private PrintWriter liveOut;

    public void startLiveConnection() {
        try {
            liveSocket = new Socket(SERVER_IP, SERVER_PORT);
            liveOut = new PrintWriter(liveSocket.getOutputStream(), true);
            liveIn = new BufferedReader(new InputStreamReader(liveSocket.getInputStream()));
            liveOut.println("LOGIN:" + username);
            new ClientReceiverThread(this, liveIn).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server at " + SERVER_IP,
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendMoneyAsync(String recipient, double amount, boolean fromChecking, String accountType) {
        if (liveOut == null || liveIn == null) {
            JOptionPane.showMessageDialog(this, "Live connection not established.");
            return;
        }

        new Thread(() -> {
            try {
                if (fromChecking && checkingBalance < amount || !fromChecking && savingsBalance < amount) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Insufficient funds."));
                    return;
                }

                liveOut.println("SEND:" + recipient + "," + amount + "," + accountType);
                String response = liveIn.readLine();

                if ("SUCCESS".equalsIgnoreCase(response)) {
                    if (fromChecking) checkingBalance -= amount;
                    else savingsBalance -= amount;
                    updateCredentialsFile();

                    try (FileWriter writer = new FileWriter("transactions.txt", true)) {
                        String msg = String.format("%s - Sent $%.2f to %s [%s account]%n",
                                java.time.LocalDateTime.now(), amount, recipient, accountType);
                        writer.write(msg);
                    }

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Money sent successfully.");
                        goToHomeTab();
                    });
                } else {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Failed: " + response));
                }

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Connection error."));
                e.printStackTrace();
            }
        }).start();
    }

    public BankWelcomePage(String username, double checkingBalance, double savingsBalance, double accountNumber) {
        this.username = username;
        this.checkingBalance = checkingBalance;
        this.savingsBalance = savingsBalance;
        this.accountNumber = accountNumber;

        setTitle("JAWA Online Banking System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("\uD83C\uDFE0 Home", createHomeTab());
        tabbedPane.addTab("\uD83D\uDCB3 Transactions", new TransactionsPanel(this));
        tabbedPane.addTab("\uD83D\uDC64 Accounts",
                new AccountTab(username, checkingBalance, savingsBalance, accountNumber, this));
        add(tabbedPane);
    }

    public boolean verifyPin(String pin) {
        try (Scanner scanner = new Scanner(new File("credentials.txt"))) {
            while (scanner.hasNextLine()) {
                String[] user = scanner.nextLine().split(",");
                if (user.length >= 6 && user[0].equals(username)) {
                    return user[4].equals(pin);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logRequestToUser(String otherUsername, double amount) {
        try (FileWriter fw = new FileWriter("requests.txt", true)) {
            fw.write(username + " requested $" + amount + " from " + otherUsername + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveLiveMoney(String sender, double amount, String accountType) {
        if (accountType.equalsIgnoreCase("checking")) {
            checkingBalance += amount;
        } else {
            savingsBalance += amount;
        }
        updateCredentialsFile();
        logTransaction("Received $" + amount + " from " + sender + " into " + accountType);
        JOptionPane.showMessageDialog(this,
                "You received $" + amount + " from " + sender + " into your " + accountType + " account!");
        goToHomeTab();
    }

    private void logTransaction(String message) {
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            String timestamp = java.time.LocalDateTime.now().toString();
            writer.write(timestamp + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createHomeTab() {
        double totalBalance = checkingBalance + savingsBalance;
        double checkingChange = checkingBalance - prevCheckingBalance;
        double savingsChange = savingsBalance - prevSavingsBalance;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(240, 248, 255));
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        JLabel welcomeLabel = new JLabel("Welcome to JAWA, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel accountNumberLabel = new JLabel("Account #: " + String.format("%.0f", accountNumber));
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        welcomePanel.add(accountNumberLabel);
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(welcomePanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));

        JLabel totalLabel = new JLabel("Total Balance: $" + String.format("%.2f", totalBalance), SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel boxesPanel = new JPanel(new GridLayout(1, 2, 30, 10));
        boxesPanel.setBackground(new Color(240, 248, 255));
        boxesPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 20, 80));

        boxesPanel.add(createAccountBox("Checking Account", checkingBalance, checkingChange));
        boxesPanel.add(createAccountBox("Savings Account", savingsBalance, savingsChange));

        centerPanel.add(totalLabel, BorderLayout.NORTH);
        centerPanel.add(boxesPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAccountBox(String title, double balance, double change) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 248, 255));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel balanceLabel = new JLabel("$" + String.format("%.2f", balance), SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel changeLabel = new JLabel("Change: $" + String.format("%.2f", change), SwingConstants.CENTER);
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        changeLabel.setForeground(change >= 0 ? Color.GREEN : Color.RED);
        changeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(balanceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(changeLabel);
        return panel;
    }

    public boolean updateCheckingBalance(double amount) {
        if (checkingBalance + amount < 0) return false;
        checkingBalance += amount;
        return true;
    }

    public boolean updateSavingsBalance(double amount) {
        if (savingsBalance + amount < 0) return false;
        savingsBalance += amount;
        return true;
    }

    public void goToHomeTab() {
        tabbedPane.setSelectedIndex(0);
        getContentPane().removeAll();
        initUI();
        revalidate();
        repaint();
    }

    public void updateCredentialsFile() {
        try {
            File file = new File("credentials.txt");
            Scanner scanner = new Scanner(file);
            StringBuilder updatedAccounts = new StringBuilder();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals(username)) {
                    parts[2] = String.format("%.2f", savingsBalance);
                    parts[3] = String.format("%.2f", checkingBalance);
                    line = String.join(",", parts);
                }
                updatedAccounts.append(line).append(System.lineSeparator());
            }

            scanner.close();
            FileWriter writer = new FileWriter(file);
            writer.write(updatedAccounts.toString());
            writer.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating credentials file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCheckingBalance(double checkingBalance) {
        this.checkingBalance = checkingBalance;
    }

    public void setSavingsBalance(double savingsBalance) {
        this.savingsBalance = savingsBalance;
    }

    public static void main(String[] args) {
        // For testing
    }
}
