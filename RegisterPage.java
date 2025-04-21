/*
 * Register Page for new Users
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

public class RegisterPage extends Frame {
    JLabel lblTitle, lblUser, lblPass, lblSavings, lblCurrent, lblPin; // Labels
    JTextField tfUser, tfSavings, tfCurrent; // Text Field
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
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

        lblCurrent = new JLabel("Enter an initial balance in Current Account:");
        lblCurrent.setFont(new Font("SansSerif", Font.BOLD, 15));
        currentPanel.add(lblCurrent);
        tfCurrent = new JTextField();
        tfCurrent.setFont(new Font("Arial", Font.PLAIN, 13));
        tfCurrent.setPreferredSize(new Dimension(100, 22));
        currentPanel.add(tfCurrent);

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
        panel.add(currentPanel);
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
        String current = tfCurrent.getText();
        String pin = new String(pfPin.getPassword());
        if (username.matches("^[a-zA-Z][a-zA-Z0-9_]{5,12}$")
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
                && savings.matches("\\d+(\\.\\d{1,2})?") && current.matches("\\d+(\\.\\d{1,2})?")
                && pin.matches("\\d{4}")) {
            try (FileWriter fw = new FileWriter("credentials.txt", true)) {
                fw.write(username + "," + password + "," + savings + "," + current + "," + pin + "\n");
                showMessage("Success", "Account created successfully!",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginPage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            showMessage("Error", "Invalid username or password format.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
