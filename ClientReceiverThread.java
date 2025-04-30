//Imports
import java.io.BufferedReader;
import javax.swing.JOptionPane;

//Thread continuously listens for messages from the server
public class ClientReceiverThread extends Thread {
//References the GUI class to update UI based on messages recieved
    private BankWelcomePage parent;
//Read messages from server
    private BufferedReader in;

//constructor to initialize the thread 
    public ClientReceiverThread(BankWelcomePage parent, BufferedReader in) {
        this.parent = parent;
        this.in = in;
    }

//Run method
    @Override
public void run() {
    try {
        String line;
//Continously read lines from server
        while ((line = in.readLine()) != null) {
//Handle incoming money transfer
            if (line.startsWith("MONEY_RECEIVED:")) {
                String[] parts = line.substring(15).split(",");
                String sender = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String accountType = parts[2];
//Notify GUI about money
                parent.receiveLiveMoney(sender, amount, accountType);
//Handle success and error messages
            } else if (line.equals("SUCCESS") || line.startsWith("ERROR")) {
                parent.enqueueServerResponse(line);
            }
//Handle money request from another user
            else if (line.startsWith("MONEY_REQUESTED:")) {
    String[] parts = line.substring(16).split(",");
    String requester = parts[0];
    double amount = Double.parseDouble(parts[1]);
    String accountType = parts[2];
    boolean fromChecking = accountType.equalsIgnoreCase("checking");
//Launch dialog in new thread. Avoids blocking reciever thread 
    new Thread(() -> {
        int result = JOptionPane.showConfirmDialog(null,
            requester + " is requesting $" + amount + " from your " + accountType + " account.\nApprove?",
            "Money Request", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
//Ask user for PIN
                String pin = JOptionPane.showInputDialog(null, "Enter your 4-digit PIN to approve the request:");
            
                if (pin != null && parent.verifyPin(pin)) {
//Attempt to send money if PIN is valid
                    boolean success = parent.sendMoneyToUser(requester, amount, true);
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
//Start dialog interaction
    }).start(); 
}

        }
    } catch (Exception e) {
//Handle disconnection
        System.out.println("Connection closed.");
    }
}

}
