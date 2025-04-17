import javax.swing.*;
import java.awt.*;

public class BankPage extends JFrame {
    private double checkingBalance;
    private double savingsBalance;
    private double prevCheckingBalance = 1100.00;
    private double prevSavingsBalance = 3200.00;
    private double accountNumber;
    private String username = "User";

    public BankWelcomePage(double checkingBalance, double savingsBalance, double accountNumber) {
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
        JTabbedPane tabbedPane = new JTabbedPane();

        // Home Tab
        JPanel homePanel = createHomeTab();
        tabbedPane.addTab("🏠 Home", homePanel);

        // Placeholder Tabs
        tabbedPane.addTab("👛 Transactions", new JPanel(new FlowLayout(FlowLayout.LEFT)).add(new JLabel("Transactions coming soon.")));
        tabbedPane.addTab("👤 Accounts", new JPanel(new FlowLayout(FlowLayout.LEFT)).add(new JLabel("Account info coming soon.")));

        add(tabbedPane);
    }

    private JPanel createHomeTab() {
        double totalBalance = checkingBalance + savingsBalance;
        double checkingChange = checkingBalance - prevCheckingBalance;
        double savingsChange = savingsBalance - prevSavingsBalance;

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Top Panel (Welcome + Account)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JPanel welcomePanel = new JPanel();
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

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JLabel totalLabel = new JLabel("Total Balance: $" + String.format("%.2f", totalBalance), SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel boxesPanel = new JPanel(new GridLayout(1, 2, 30, 10));
        boxesPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 20, 80));

        JPanel checkingPanel = createAccountBox("Checking Account", checkingBalance, checkingChange);
        JPanel savingsPanel = createAccountBox("Savings Account", savingsBalance, savingsChange);

        boxesPanel.add(checkingPanel);
        boxesPanel.add(savingsPanel);

        centerPanel.add(totalLabel, BorderLayout.NORTH);
        centerPanel.add(boxesPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccountBox(String title, double balance, double change) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

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

    public static void main(String[] args) {
        double checking = 1200.75;
        double savings = 3400.25;
        double accountNumber = 123456789;

        SwingUtilities.invokeLater(() -> {
            new BankWelcomePage(checking, savings, accountNumber).setVisible(true);
        });
    }
}
