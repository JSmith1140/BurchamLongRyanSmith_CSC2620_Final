//Imports
import java.net.Socket;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
//Main class
public class BankWelcomePage extends JFrame {
//Account and user info
    private double checkingBalance;
    private double savingsBalance;
    private double checkingChange = 0;
    private double savingsChange = 0;
    private double prevCheckingBalance = 0;
    private double prevSavingsBalance = 0;
    private double accountNumber;
    private String username;
    //GUI for swithcing tabs
    private JTabbedPane tabbedPane;
//Server connection
    private static final String SERVER_IP = "10.2.147.237"; // <-- Put your IP here :)
    private static final int SERVER_PORT = 5000;
    private final BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
    private Socket liveSocket;
    private BufferedReader liveIn;
    private PrintWriter liveOut;

//Observer pattern to update balance view
    public interface BalanceObserver {
        void onBalanceChanged();
    }
    private final List<BalanceObserver> observers = new ArrayList<>();
    public void addObserver(BalanceObserver observer) {
        observers.add(observer);
    }
    public void notifyObservers() {
        for (BalanceObserver observer : observers) {
            observer.onBalanceChanged();
        }
    }
//Starts a socket connection to server 
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
//Response recieved from server processed by sender method
    public void enqueueServerResponse(String response) {
        responseQueue.offer(response); // won't block
    }
//Sends money to another user using server
    public boolean sendMoneyToUser(String recipient, double amount, boolean fromChecking) {
//Ensure enough balance exists before sending 
        if (fromChecking && checkingBalance < amount)
            return false;
        if (!fromChecking && savingsBalance < amount)
            return false;

        try {
            String accountType = fromChecking ? "checking" : "savings";
//Clear old responses
            responseQueue.clear(); 
//Send from checking
            liveOut.println("SEND:" + recipient + "," + amount + "," + "checking"); 
                                                                                

//Wait max of 5 seconds for confirmation or error
            String response = responseQueue.poll(5, java.util.concurrent.TimeUnit.SECONDS);
            if (response == null) {
                JOptionPane.showMessageDialog(this, "No response from server. Try again.");
                return false;
            }

            if (response.equals("SUCCESS")) {
//Update balance locally
                if (fromChecking) {
                    checkingBalance -= amount;
                    checkingChange -= amount;
                } else {
                    savingsBalance -= amount;
                    savingsChange -= amount;
                }

                updateCredentialsFile();
                notifyObservers();
                return true;
            }

            else {
                JOptionPane.showMessageDialog(this, "Transfer failed: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
//Requests money from another user
    public boolean sendRequestToUser(String recipient, double amount, boolean toChecking) {
        try {
            String accountType = toChecking ? "checking" : "savings";
            responseQueue.clear();
            liveOut.println("REQUEST:" + recipient + "," + amount + "," + "checking"); // Recipient will pay from
                                                                                       // checking

            String response = responseQueue.poll(5, java.util.concurrent.TimeUnit.SECONDS);
            if ("SUCCESS".equals(response)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Request failed: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
//constuctor 
    public BankWelcomePage(String username, double checkingBalance, double savingsBalance, double accountNumber) {
        this.username = username;
        this.checkingBalance = checkingBalance;
        this.savingsBalance = savingsBalance;
        this.accountNumber = accountNumber;

        setTitle("JAWA Online Banking System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//Create UI tabs
        initUI();
//Register for balance updates
        addObserver(this::goToHomeTab);
    }
//Creates all tabs for the GUI
    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🏠 Home", createHomeTab());
        tabbedPane.addTab("👛 Transactions", new TransactionsPanel(this));
        tabbedPane.addTab("👤 Accounts",
                new AccountTab(username, checkingBalance, savingsBalance, accountNumber, this));
        add(tabbedPane);
    }
//verifies PIN from credentials file
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
//Logs a request to local requests file
    public void logRequestToUser(String otherUsername, double amount) {
        try (FileWriter fw = new FileWriter("requests.txt", true)) {
            fw.write(username + " requested $" + amount + " from " + otherUsername + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//Handles incoming transfer of money from server
    public void receiveLiveMoney(String sender, double amount, String accountType) {
        if (accountType.equalsIgnoreCase("checking")) {
            checkingBalance += amount;
            checkingChange += amount;
        } else {
            savingsBalance += amount;
            savingsChange += amount;
        }

        updateCredentialsFile();
        JOptionPane.showMessageDialog(this,
                "You received $" + amount + " from " + sender + " into your " + accountType + " account!");
        notifyObservers();
    }
//UI construcotr for home tab
    private JPanel createHomeTab() {
        double totalBalance = checkingBalance + savingsBalance;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));

//Top Panel welcome message
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

//Load and add the image
        ImageIcon originalIcon = null;
        try {
            originalIcon = new ImageIcon(getClass().getResource("JAWABankImage.jpg")); // <-- Download the JPG Image in
                                                                                       // the GITHUB and put it in your
                                                                                       // folder
        } catch (Exception e) {
            System.out.println("Image not found!");
        }

        if (originalIcon != null) {
            Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Margin between image and text
            infoPanel.add(imageLabel);
        }

//Welcome Text
        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setBackground(new Color(240, 248, 255));
        JLabel welcomeLabel = new JLabel("Welcome to JAWA, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel accountNumberLabel = new JLabel("Account #: " + String.format("%.0f", accountNumber));
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        welcomeTextPanel.add(welcomeLabel);
        welcomeTextPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        welcomeTextPanel.add(accountNumberLabel);

        infoPanel.add(welcomeTextPanel);

//Add the infoPanel to the west of topPanel
        topPanel.add(infoPanel, BorderLayout.WEST);

//Center Panel total balance and accounut boxes
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setLayout(new BorderLayout());

        JLabel totalLabel = new JLabel("Total Balance: $" + String.format("%.2f", totalBalance), SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel boxesPanel = new JPanel(new GridLayout(1, 2, 30, 10));
        boxesPanel.setBackground(new Color(240, 248, 255)); // Updated background color
        boxesPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 20, 80));

        JPanel checkingPanel = createAccountBox("Checking Account");
        JPanel savingsPanel = createAccountBox("Savings Account");

        boxesPanel.add(checkingPanel);
        boxesPanel.add(savingsPanel);

        centerPanel.add(totalLabel, BorderLayout.NORTH);
        centerPanel.add(boxesPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }
//Creates a box to show account balance and changes
    private JPanel createAccountBox(String title) {
        double balance = title.contains("Checking") ? checkingBalance : savingsBalance;
        double change = title.contains("Checking") ? checkingChange : savingsChange;

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
        changeLabel.setForeground(change > 0 ? Color.GREEN : (change < 0 ? Color.RED : Color.GRAY));
        changeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(balanceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(changeLabel);
        return panel;
    }
//Methods for modifying account balances
    public boolean updateCheckingBalance(double amount) {
        if (checkingBalance + amount < 0)
            return false;
        checkingBalance += amount;
        checkingChange += amount;
        return true;
    }

    public boolean updateSavingsBalance(double amount) {
        if (savingsBalance + amount < 0)
            return false;
        savingsBalance += amount;
        savingsChange += amount;
        return true;
    }
//Reloads the home tab
    public void goToHomeTab() {
        tabbedPane.setSelectedIndex(0);
        getContentPane().removeAll();
        initUI();
        revalidate();
        repaint();
    }
//Updates stored credentials
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
//Setters for external use
    public void setCheckingBalance(double checkingBalance) {
        this.checkingBalance = checkingBalance;
    }

    public void setSavingsBalance(double savingsBalance) {
        this.savingsBalance = savingsBalance;
    }
//Main method (for testing)
    public static void main(String[] args) {
    }
}
