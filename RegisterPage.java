//Registration page for new users
 
//Imports
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

//RegisterPage class extends frame
public class RegisterPage extends Frame {
//Declare labels
    JLabel lblTitle, lblUser, lblPass, lblSavings, lblChecking, lblPin;
//Declare text fields
    JTextField tfUser, tfSavings, tfChecking;
//Declare password fields
    JPasswordField pfPass, pfPin;
//Declare buttons
    JButton btnRegister, btnExit;

//Constructor for RegisterPage
    public RegisterPage() {
//Window title
        setTitle("Register Page");
//Main panel with vertical layout and navy blue backround
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 128));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//Sub-panels for UI organization
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel savingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel checkingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

//Create title label
        lblTitle = new JLabel("JAWA Register Page");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        titlePanel.add(lblTitle);

//Input username components
        lblUser = new JLabel("Enter a Username:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 15));
        userPanel.add(lblUser);
        tfUser = new JTextField();
        tfUser.setFont(new Font("Arial", Font.PLAIN, 13));
        tfUser.setPreferredSize(new Dimension(150, 22));
        userPanel.add(tfUser);

//Input password components
        lblPass = new JLabel("Enter a Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 15));
        passPanel.add(lblPass);
        pfPass = new JPasswordField();
        pfPass.setFont(new Font("Arial", Font.PLAIN, 16));
        pfPass.setPreferredSize(new Dimension(150, 22));
        passPanel.add(pfPass);

//Input savings account components
        lblSavings = new JLabel("Enter an initial balance in Savings Account:");
        lblSavings.setFont(new Font("SansSerif", Font.BOLD, 15));
        savingsPanel.add(lblSavings);
        tfSavings = new JTextField();
        tfSavings.setFont(new Font("Arial", Font.PLAIN, 13));
        tfSavings.setPreferredSize(new Dimension(100, 22));
        savingsPanel.add(tfSavings);

//Input checkings account components
        lblChecking = new JLabel("Enter an initial balance in Checking Account:");
        lblChecking.setFont(new Font("SansSerif", Font.BOLD, 15));
        checkingPanel.add(lblChecking);
        tfChecking = new JTextField();
        tfChecking.setFont(new Font("Arial", Font.PLAIN, 13));
        tfChecking.setPreferredSize(new Dimension(100, 22));
        checkingPanel.add(tfChecking);

//Input PIN components
        lblPin = new JLabel("Enter a 4-Digit Pin:");
        lblPin.setFont(new Font("SansSerif", Font.BOLD, 15));
        pinPanel.add(lblPin);
        pfPin = new JPasswordField();
        pfPin.setPreferredSize(new Dimension(100, 22));
        pfPin.setFont(new Font("Arial", Font.PLAIN, 25));
        pinPanel.add(pfPin);

//Register button
        btnRegister = new JButton("Register");
        btnRegister.setPreferredSize(new Dimension(100, 30));
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15));
        btnPanel.add(btnRegister);

//Add all panels to main panel
        panel.add(titlePanel);
        panel.add(userPanel);
        panel.add(passPanel);
        panel.add(savingsPanel);
        panel.add(checkingPanel);
        panel.add(pinPanel);
        panel.add(btnPanel);
//Add main panel to frame
        add(panel);
//Set window size and position
        setSize(500, 400);
//Center window
        setLocationRelativeTo(null);
//Display window
        setVisible(true);

//Close functionality
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
//Close window
                dispose();
            }
        });
//Action for register button
        btnRegister.addActionListener(e -> {
//Call register method
            register();
        });
    }
//Method for registration logic
    private void register() {
//Retrieve inputs
        String username = tfUser.getText();
        String password = new String(pfPass.getPassword());
        String savings = tfSavings.getText();
        String checking = tfChecking.getText();
        String pin = new String(pfPin.getPassword());

//Validate inputs with regex patterns 
//Username must start with a letter, with 6-13 character total
        if (username.matches("^[a-zA-Z][a-zA-Z0-9_]{5,12}$")
//Password must be at least 8 characters long, contain at least one lowercase, one uppercase, one digit, and one special character
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
//Savings must be a valid number
                && savings.matches("\\d+(\\.\\d{1,2})?") 
//Checking must be a valid number
                && checking.matches("\\d+(\\.\\d{1,2})?")
//PIN must be 4 digits
                && pin.matches("\\d{4}")) {

//Generate a random 10 digit account number
            String accountNumber = String.format("%010d", new java.util.Random().nextLong() % 1_000_000_0000L);
            if (accountNumber.startsWith("-")) {
//Remove nagatives if need be
                accountNumber = accountNumber.substring(1);
            }
//Open file in append mode
            try (FileWriter fw = new FileWriter("credentials.txt", true)) {
//Write user info into file
                fw.write(username + "," + password + "," + savings + "," + checking + "," + pin + "," + accountNumber + "\n");
//Show success message
                showMessage("Success", "Account created successfully!", JOptionPane.INFORMATION_MESSAGE);
//Close registration window
                dispose();
//Open login page
                new LoginPage();
            } catch (Exception ex) {
//Print error if writing fails
                ex.printStackTrace();
            }
//Input validation failure messages
        } else if (!username.matches("^[a-zA-Z][a-zA-Z0-9_]{5,12}$")) {
            showMessage("Error", "Username must be Alphanumeric, 5–12 characters",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            showMessage("Error", "Password must be at least 8 characters, 1 digit, 1 uppercase, 1 special character",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!savings.matches("\\d+(\\.\\d{1,2})?")) {
            showMessage("Error", "Savings Initial Balance Must be a number",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!checking.matches("\\d+(\\.\\d{1,2})?")) {
            showMessage("Error", "Checking Initial Balance Must be a number",
                    JOptionPane.ERROR_MESSAGE);
        } else if (!pin.matches("\\d{4}")) {
            showMessage("Error", "PIN must be 4-Digits",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
//Show message dialog
    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
