import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clients = new HashSet<>(); //HashSet to store the clients' Output Streams
    private static Set<String> clientNames = new HashSet<>();
    private static int port = 8888; //Default Server Port: 8888

    public static boolean tryParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static void main(String[] args) throws Exception {
        if(args.length>0) {
            if(tryParseInt(args[0])) {
                port = Integer.parseInt(args[0]);
            } else {
                System.out.println("Usage: java ChatServer <listening port number> OR the port defaults to 8888 if not specified");
                System.exit(1);
            }
        }
        ServerSocket listener = new ServerSocket(port);
        System.out.println("The chat server is running and listening on port " + port);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Keep requesting a unique name from this client.
                System.out.println("[*]New client connection initiated. Info: " + clientSocket);
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    synchronized (clients) {
                        if (!name.isBlank() && !name.isEmpty() && !clientNames.contains(name)) {
                            clients.add(out);
                            clientNames.add(name);
                            System.out.println("[+]New client with name " + name + " added. Info: " + clientSocket);
                            break;
                        }
                    }
                }

                // Notify everyone that a new client has joined the chat
                synchronized (clients) {
                    for (PrintWriter client : clients) {
                        client.println("MESSAGE " + name + " has joined the chat");
                    }
                }

                // Accept messages from this client and broadcast them
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    synchronized (clients) {
                        for (PrintWriter client : clients) {
                            client.println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Remove the client from the HashSets of connected clients
                // and notify everyone that the client has left the chat.
                if (name != null) {
                    clients.remove(out);
                    clientNames.remove(name);
                    System.out.println("[-]Client removed: " + name);
                    synchronized (clients) {
                        for (PrintWriter client : clients) {
                            client.println("MESSAGE " + name + " has left the chat");
                        }
                    }
                }
                try {
                    clientSocket.close();
                    System.out.println("[x]Connection closed with " + name + " successfully. Info: " + clientSocket);
                } catch (IOException e) {
                    System.out.println("[!]Failed to close connection with " + name + " Info: " + clientSocket);
                    e.printStackTrace();
                }
            }
        }
    }
}
