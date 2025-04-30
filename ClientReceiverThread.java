import java.io.BufferedReader;

import javax.swing.JOptionPane;

public class ClientReceiverThread extends Thread {
    private BankWelcomePage parent;
    private BufferedReader in;

    public ClientReceiverThread(BankWelcomePage parent, BufferedReader in) {
        this.parent = parent;
        this.in = in;
    }

    @Override
public void run() {
    try {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("MONEY_RECEIVED:")) {
                String[] parts = line.substring(15).split(",");
                String sender = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String accountType = parts[2];
                parent.receiveLiveMoney(sender, amount, accountType);
            } else if (line.equals("SUCCESS") || line.startsWith("ERROR")) {
                parent.enqueueServerResponse(line);
            }

            else if (line.startsWith("MONEY_REQUESTED:")) {
    String[] parts = line.substring(16).split(",");
    String requester = parts[0];
    double amount = Double.parseDouble(parts[1]);
    String accountType = parts[2];
    boolean fromChecking = accountType.equalsIgnoreCase("checking");

    new Thread(() -> {
        int result = JOptionPane.showConfirmDialog(null,
            requester + " is requesting $" + amount + " from your " + accountType + " account.\nApprove?",
            "Money Request", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                String pin = JOptionPane.showInputDialog(null, "Enter your 4-digit PIN to approve the request:");
            
                if (pin != null && parent.verifyPin(pin)) {
                    boolean success = parent.sendMoneyToUser(requester, amount, true); // Always pay from checking
                    if (success) {
                        JOptionPane.showMessageDialog(null, "You sent $" + amount + " to " + requester);
                    } else {
                        JOptionPane.showMessageDialog(null, "Transaction failed: Insufficient funds or connection issue.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect PIN. Transaction cancelled.");
                }
            } else {
            JOptionPane.showMessageDialog(null, "You declined the money request from " + requester + ".");
        }
    }).start(); 
}

        }
    } catch (Exception e) {
        System.out.println("Connection closed.");
    }
}

}
