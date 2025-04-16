
/*
 * Register Page for new Users
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.awt.event.*;

public class RegisterPage extends Frame {
    JLabel lblTitle, lblUser, lblPass, lblSavings, lblCurrent, lblPin; // Labels
    JTextField tfUser, tfSavings, tfCurrent; // Text Field
    JPasswordField pfPass, pfPin; // Password Field
    JButton btnRegister, btnExit; // Buttons

    public RegisterPage() {
        setTitle("Login Page");
        JPanel panel = new JPanel();
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
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        titlePanel.add(lblTitle);

        lblUser = new JLabel("Enter a Username:");
        userPanel.add(lblUser);
        tfUser = new JTextField();
        tfUser.setPreferredSize(new Dimension(100, 20));
        userPanel.add(tfUser);

        lblPass = new JLabel("Enter a Password:");
        passPanel.add(lblPass);
        pfPass = new JPasswordField();
        pfPass.setPreferredSize(new Dimension(100, 20));
        passPanel.add(pfPass);

        lblSavings = new JLabel("Enter an initial balance in Savings Account:");
        savingsPanel.add(lblSavings);
        tfSavings = new JTextField();
        tfSavings.setPreferredSize(new Dimension(100, 20));
        savingsPanel.add(tfSavings);

        lblCurrent = new JLabel("Enter an initial balance in Current Account:");
        currentPanel.add(lblCurrent);
        tfCurrent = new JTextField();
        tfCurrent.setPreferredSize(new Dimension(100, 20));
        currentPanel.add(tfCurrent);

        lblPin = new JLabel("Enter a 4-Digit Pin:");
        pinPanel.add(lblPin);
        pfPin = new JPasswordField();
        pfPin.setPreferredSize(new Dimension(100, 20));
        pinPanel.add(pfPin);

        btnRegister = new JButton("Register");
        btnPanel.add(btnRegister);

        panel.add(titlePanel);
        panel.add(userPanel);
        panel.add(passPanel);
        panel.add(savingsPanel);
        panel.add(currentPanel);
        panel.add(pinPanel);
        panel.add(btnPanel);

        add(panel);

        setSize(400, 400);
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
