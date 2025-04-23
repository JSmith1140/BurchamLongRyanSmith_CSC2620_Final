
/*
 * Register Page for new Users
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

public class RegisterPage extends Frame {
    JLabel lblTitle, lblUser, lblPass, lblSavings, lblChecking, lblPin; // Labels
    JTextField tfUser, tfSavings, tfChecking; // Text Field
    JPasswordField pfPass, pfPin; // Password Field
    JButton btnRegister, btnExit; // Buttons

    public RegisterPage() {
        setTitle("Login Page");
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 128)); // Navy blue background
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel savingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel checkingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        lblTitle = new JLabel("JAWA Register Page");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        titlePanel.add(lblTitle);

        lblUser = new JLabel("Enter a Username:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 15));
        userPanel.add(lblUser);
        tfUser = new JTextField();
        tfUser.setFont(new Font("Arial", Font.PLAIN, 13));
        tfUser.setPreferredSize(new Dimension(150, 22));
        userPanel.add(tfUser);

        lblPass = new JLabel("Enter a Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 15));
        passPanel.add(lblPass);
        pfPass = new JPasswordField();
        pfPass.setFont(new Font("Arial", Font.PLAIN, 16));
        pfPass.setPreferredSize(new Dimension(150, 22));
        passPanel.add(pfPass);

        lblSavings = new JLabel("Enter an initial balance in Savings Account:");
        lblSavings.setFont(new Font("SansSerif", Font.BOLD, 15));
        savingsPanel.add(lblSavings);
        tfSavings = new JTextField();
        tfSavings.setFont(new Font("Arial", Font.PLAIN, 13));
        tfSavings.setPreferredSize(new Dimension(100, 22));
        savingsPanel.add(tfSavings);

        lblChecking = new JLabel("Enter an initial balance in Checking Account:");
        lblChecking.setFont(new Font("SansSerif", Font.BOLD, 15));
        checkingPanel.add(lblChecking);
        tfChecking = new JTextField();
        tfChecking.setFont(new Font("Arial", Font.PLAIN, 13));
        tfChecking.setPreferredSize(new Dimension(100, 22));
        checkingPanel.add(tfChecking);

        lblPin = new JLabel("Enter a 4-Digit Pin:");
        lblPin.setFont(new Font("SansSerif", Font.BOLD, 15));
        pinPanel.add(lblPin);
        pfPin = new JPasswordField();
        pfPin.setPreferredSize(new Dimension(100, 22));
        pfPin.setFont(new Font("Arial", Font.PLAIN, 25));
        pinPanel.add(pfPin);

        btnRegister = new JButton("Register");
        btnRegister.setPreferredSize(new Dimension(100, 30));
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15));
        btnPanel.add(btnRegister);

        panel.add(titlePanel);
        panel.add(userPanel);
        panel.add(passPanel);
        panel.add(savingsPanel);
        panel.add(checkingPanel);
        panel.add(pinPanel);
        panel.add(btnPanel);

        add(panel);

        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        btnRegister.addActionListener(e -> {
            register();
        });
    }

    private void register() {
        String username = tfUser.getText();
        String password = new String(pfPass.getPassword());
        String savings = tfSavings.getText();
        String checking = tfChecking.getText();
        String pin = new String(pfPin.getPassword());

        if (username.matches("^[a-zA-Z][a-zA-Z0-9_]{5,12}$")
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
                && savings.matches("\\d+(\\.\\d{1,2})?") && checking.matches("\\d+(\\.\\d{1,2})?")
                && pin.matches("\\d{4}")) {
            String accountNumber = String.format("%010d", new java.util.Random().nextLong() % 1_000_000_0000L);
            if (accountNumber.startsWith("-")) {
                accountNumber = accountNumber.substring(1);
            }
            try (FileWriter fw = new FileWriter("credentials.txt", true)) {
                fw.write(username + "," + password + "," + savings + "," + checking + "," + pin + "," + accountNumber
                        + "\n");
                showMessage("Success", "Account created successfully!",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginPage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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

    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
