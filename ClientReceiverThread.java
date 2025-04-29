import java.io.BufferedReader;

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
                }
            }
        } catch (Exception e) {
            System.out.println("Connection closed.");
        }
    }
}
