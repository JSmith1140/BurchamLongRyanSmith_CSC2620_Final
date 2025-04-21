import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class LoginPage extends JFrame {

    JLabel lblTitle, lblUser, lblPass; // Labels
    JTextField tfUser; // Text Field
    JPasswordField pfPass; // Password Field
    JButton btnRegister, btnLogin; // Buttons

    public LoginPage() {
        setTitle("Login Page");

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 128)); // Navy blue background
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        lblTitle = new JLabel("Welcome to JAWA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        titlePanel.add(lblTitle);

        lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 15));
        userPanel.add(lblUser);

        tfUser = new JTextField();
        tfUser.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(tfUser);

        lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 15));
        passPanel.add(lblPass);

        tfUser.setPreferredSize(new Dimension(175, 25));
        pfPass = new JPasswordField();
        pfPass.setFont(new Font("Arial", Font.PLAIN, 16));
        passPanel.add(pfPass);
        pfPass.setPreferredSize(new Dimension(175, 25));

        btnLogin = new JButton("Login"); // Login button
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(btnLogin);

        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnRegister.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(btnRegister);

        panel.add(titlePanel);
        panel.add(userPanel);
        panel.add(passPanel);
        panel.add(btnPanel);

        add(panel);

        btnRegister.addActionListener(e -> {
            register();
        });

        btnLogin.addActionListener(e -> {
            login();
        });

        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void register() {
        dispose();
        new RegisterPage();
    }

    private void login() {
        String username = tfUser.getText(); // get username text
        String password = new String(pfPass.getPassword()); // get password text
        boolean correct = false;

        try (Scanner scanner = new Scanner(new File("credentials.txt"))) {
            while (scanner.hasNextLine()) {
                String[] usertxt = scanner.nextLine().split(",");
                if (usertxt[0].equals(username) && usertxt[1].equals(password)) {
                    correct = true;
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR: File Not Found", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (correct) {
            JOptionPane.showMessageDialog(this, "Successfully Logged In", "Login",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            // new BankPage(checkingBalance, savingsBalance, accountNumber);
        } else {
            JOptionPane.showMessageDialog(this, "ERROR: Incorrect Username or Password", "Login",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
