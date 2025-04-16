import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class LoginPage extends JFrame {

    JLabel lblTitle, lblUser, lblPass; // Labels
    JTextField tfUser; // Text Field
    JPasswordField pfPass; // Password Field
    JButton btnRegister, btnLogin, btnExit; // Buttons

    public LoginPage() {
        setTitle("Login Page");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        lblTitle = new JLabel("Welcome to JAWA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        titlePanel.add(lblTitle);

        lblUser = new JLabel("Username:");
        userPanel.add(lblUser);

        tfUser = new JTextField();
        userPanel.add(tfUser);

        lblPass = new JLabel("Password:");
        passPanel.add(lblPass);

        tfUser.setPreferredSize(new Dimension(100, 20));
        pfPass = new JPasswordField();
        passPanel.add(pfPass);
        pfPass.setPreferredSize(new Dimension(100, 20));

        btnLogin = new JButton("Login"); // Login button
        btnPanel.add(btnLogin);

        btnRegister = new JButton("Register");
        btnPanel.add(btnRegister);

        // btnExit = new JButton("Exit"); // Exit button
        // btnExit.setBackground(Color.RED);
        // btnPanel.add(btnExit);

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

        // btnExit.addActionListener(e -> {
        // System.exit(0);
        // });

        setSize(300, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void register() {
        String username = tfUser.getText(); // get username text
        String password = new String(pfPass.getPassword()); // get password text
        if (username.matches("^[a-zA-Z][a-zA-Z0-9_]{5,12}$")
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            try (FileWriter fw = new FileWriter("user.txt", true)) {
                fw.write(username + "," + password + "\n");
                JOptionPane.showMessageDialog(this, "Welcome " + username, "Register", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "ERROR: Invalid Username or Password", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void login() {
        String username = tfUser.getText(); // get username text
        String password = new String(pfPass.getPassword()); // get password text
        boolean correct = false;

        try (Scanner scanner = new Scanner(new File("user.txt"))) {
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
            // new ChatClient(username);
        } else {
            JOptionPane.showMessageDialog(this, "ERROR: Incorrect Username or Password", "Login",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}