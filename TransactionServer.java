import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 * TransactionServer - A multithreaded server for handling live
 * banking transactions (send and request money) between users.
 */
public class TransactionServer {

    // Tracks all currently connected users and their output streams
    private static HashMap<String, PrintWriter> liveUsers = new HashMap<>();

    public static void main(String[] args) {
        int port = 5000;
        System.out.println("Transaction Server starting on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                // Handle each client in a separate thread
                new TransactionHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles communication with a single connected user.
     */
    static class TransactionHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username = "";

        public TransactionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Set up input/output streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Initial login message expected: "LOGIN:<username>"
                String loginMsg = in.readLine();
                if (loginMsg != null && loginMsg.startsWith("LOGIN:")) {
                    username = loginMsg.substring(6);
                    liveUsers.put(username, out);
                    System.out.println(username + " connected.");
                }

                // Main communication loop
                String line;
                while ((line = in.readLine()) != null) {

                    // Handle SEND:<recipient>,<amount>,<accountType>
                    if (line.startsWith("SEND:")) {
                        handleSend(line);
                    }

                    // Handle REQUEST:<recipient>,<amount>,<accountType>
                    else if (line.startsWith("REQUEST:")) {
                        handleRequest(line);
                    }
                }

                // Clean up when the user disconnects
                liveUsers.remove(username);
                socket.close();

            } catch (IOException e) {
                System.err.println("Connection error with user " + username);
                e.printStackTrace();
            }
        }

        /**
         * Processes money-sending requests.
         */
        private void handleSend(String line) {
            try {
                String[] parts = line.substring(5).split(",");
                if (parts.length < 3) {
                    out.println("ERROR: Invalid SEND format");
                    return;
                }

                String toUser = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String accountType = parts[2];

                PrintWriter toUserOut = liveUsers.get(toUser);
                if (toUserOut != null) {
                    // Notify recipient
                    toUserOut.println("MONEY_RECEIVED:" + username + "," + amount + "," + accountType);
                    out.println("SUCCESS");
                } else {
                    out.println("ERROR: User not online");
                }

            } catch (Exception ex) {
                out.println("ERROR: " + ex.getMessage());
            }
        }

        /**
         * Processes money-request messages.
         */
        private void handleRequest(String line) {
            try {
                String[] parts = line.substring(8).split(",");
                if (parts.length < 3) {
                    out.println("ERROR: Invalid REQUEST format");
                    return;
                }

                String toUser = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String accountType = parts[2];

                PrintWriter toUserOut = liveUsers.get(toUser);
                if (toUserOut != null) {
                    // Notify recipient that someone is requesting money
                    toUserOut.println("MONEY_REQUESTED:" + username + "," + amount + "," + accountType);
                    out.println("SUCCESS");
                } else {
                    out.println("ERROR: User not online");
                }

            } catch (Exception ex) {
                out.println("ERROR: " + ex.getMessage());
            }
        }
    }
}
