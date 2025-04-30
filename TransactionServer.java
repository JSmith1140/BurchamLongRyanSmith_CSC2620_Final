import java.io.*;
import java.net.*;
import java.util.*;

public class TransactionServer {
    private static HashMap<String, PrintWriter> liveUsers = new HashMap<>();

    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Transaction Server started on port " + port);
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
        private String username = "";

        public TransactionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Wait for login
                String loginMsg = in.readLine();
                if (loginMsg.startsWith("LOGIN:")) {
                    username = loginMsg.substring(6);
                    liveUsers.put(username, out);
                    System.out.println(username + " connected.");
                }

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("SEND:")) {
                        try {
                            String[] parts = line.substring(5).split(",");
                            if (parts.length < 3) {
                                out.println("ERROR: Invalid SEND format");
                                continue;
                            }
                    
                            String toUser = parts[0];
                            double amount = Double.parseDouble(parts[1]);
                            String accountType = parts[2];
                    
                            PrintWriter toUserOut = liveUsers.get(toUser);
                            if (toUserOut != null) {
                                toUserOut.println("MONEY_RECEIVED:" + username + "," + amount + "," + accountType);
                                out.println("SUCCESS");
                            } else {
                                out.println("ERROR: User not online");
                            }
                        } catch (Exception ex) {
                            out.println("ERROR: " + ex.getMessage());
                        }
                    }
                    else if (line.startsWith("REQUEST:")) {
                        try {
                            String[] parts = line.substring(8).split(",");
                            if (parts.length < 3) {
                                out.println("ERROR: Invalid REQUEST format");
                                continue;
                            }
                    
                            String toUser = parts[0];
                            double amount = Double.parseDouble(parts[1]);
                            String accountType = parts[2];
                    
                            PrintWriter toUserOut = liveUsers.get(toUser);
                            if (toUserOut != null) {
                                // Format: MONEY_REQUESTED:<requester>,<amount>,<accountType>
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

                liveUsers.remove(username);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
