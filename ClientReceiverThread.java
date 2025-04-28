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
            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("MONEY_RECEIVED:")) {
                    String[] parts = input.substring(15).split(",");
                    String sender = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String accountType = parts[2];
                    parent.receiveLiveMoney(sender, amount, accountType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
