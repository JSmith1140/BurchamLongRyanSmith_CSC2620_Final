import java.io.*;
import java.net.*;
import java.util.HashMap;

public class TransactionServer {
    private static HashMap<String, PrintWriter> liveUsers = new HashMap<>();

    public static void main(String[] args) {
        try {
            String serverIp = "10.1.40.19"; // Replace with your server's IP address
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            ServerSocket serverSocket = new ServerSocket(5000, 0, serverAddress);
            
            System.out.println("Transaction Server running on IP: " + serverIp + " and port 5000...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new TransactionHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TransactionHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public TransactionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message should be "LOGIN:<username>"
                String login = in.readLine();
                if (login.startsWith("LOGIN:")) {
                    String username = login.substring(6);
                    liveUsers.put(username, out);
                    System.out.println(username + " connected.");
                }

                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received: " + input);
                    String[] parts = input.split(",");
                    if (parts.length == 4) {
                        String sender = parts[0];
                        String recipient = parts[1];
                        double amount = Double.parseDouble(parts[2]);
                        String accountType = parts[3];

                        boolean success = updateRecipientBalance(recipient, amount, accountType);
                        if (success) {
                            sendLiveNotification(recipient, sender, amount, accountType);
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR: Recipient not found or balance update failed");
                        }
                    } else {
                        out.println("ERROR: Invalid request format");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean updateRecipientBalance(String username, double amount, String accountType) {
            try {
                File inputFile = new File("credentials.txt");
                File tempFile = new File("credentials_temp.txt");

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                boolean found = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] user = line.split(",");
                    if (user.length >= 6 && user[0].equals(username)) {
                        found = true;
                        double savings = Double.parseDouble(user[2]);
                        double checking = Double.parseDouble(user[3]);

                        if (accountType.equalsIgnoreCase("checking")) {
                            checking += amount;
                        } else {
                            savings += amount;
                        }

                        writer.write(user[0] + "," + user[1] + "," + savings + "," + checking + "," + user[4] + "," + user[5]);
                        writer.newLine();
                    } else {
                        writer.write(line);
                        writer.newLine();
                    }
                }

                writer.close();
                reader.close();

                inputFile.delete();
                tempFile.renameTo(inputFile);

                return found;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void sendLiveNotification(String recipient, String sender, double amount, String accountType) {
            PrintWriter recipientOut = liveUsers.get(recipient);
            if (recipientOut != null) {
                recipientOut.println("MONEY_RECEIVED:" + sender + "," + amount + "," + accountType);
            }
        }
    }
}
