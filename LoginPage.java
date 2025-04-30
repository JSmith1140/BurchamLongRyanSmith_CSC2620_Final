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

        // Register button calls register function
        btnRegister.addActionListener(e -> {
            register();
        });

        // Login button calls login function
        btnLogin.addActionListener(e -> {
            login();
        });

        // Canvas size
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Register function that opens RegisterPage.java
    private void register() {
        dispose();
        new RegisterPage();
    }

    // Login function that scans username and password and looks for a match in text file
    private void login() {
        String username = tfUser.getText();
        String password = new String(pfPass.getPassword());

        try (Scanner scanner = new Scanner(new File("credentials.txt"))) {
            while (scanner.hasNextLine()) {
                String[] usertxt = scanner.nextLine().split(",");

                if (usertxt.length >= 6 && usertxt[0].equals(username) && usertxt[1].equals(password)) {
                    double savings = Double.parseDouble(usertxt[2]);
                    double checking = Double.parseDouble(usertxt[3]);
                    double accountNumber = Double.parseDouble(usertxt[5]);

                    JOptionPane.showMessageDialog(this, "Successfully Logged In", "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    BankWelcomePage page = new BankWelcomePage(username, checking, savings, accountNumber);
page.startLiveConnection();
page.setVisible(true);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "ERROR: Incorrect Username or Password", "Login",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "ERROR: File Not Found", "Login", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ERROR: " + e.getMessage(), "Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
