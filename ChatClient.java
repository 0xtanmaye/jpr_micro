import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Simple Chat Client");
    private JTextField textField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 40);

    public ChatClient() {
        textField.setEditable(true);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Add listener
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }
    public static boolean tryParseInt(String str) {
    try {
        Integer.parseInt(str);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
    }

    private String getServerAddress() {
	    return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Server Address Configuration", JOptionPane.QUESTION_MESSAGE);
    }

    private String getServerPort(String serverAddress) {
	    return JOptionPane.showInputDialog(frame, "Enter Server Port for " + serverAddress, "Server Port Configuration", JOptionPane.QUESTION_MESSAGE);
    }

    private String getName() {
        return JOptionPane.showInputDialog(frame, "Choose a user name:", "User Name Selection", JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {
        // Initialize connection
        String serverAddress = getServerAddress();
	if (serverAddress == null) {
		System.exit(0);
	}
	int serverPort=0; //default server port
	int attemptCount = 0;
	int maxAttempts = 3;
	while (attemptCount < maxAttempts) {
		String strPort = getServerPort(serverAddress);
		if (strPort == null) {
			System.exit(0);
		} else if (tryParseInt(strPort)) {
		       serverPort = Integer.parseInt(strPort);
		       break;
		} else {
			JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid integer port number.", "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
		}
		attemptCount++;
	}
	if (attemptCount>=maxAttempts) {
		JOptionPane.showMessageDialog(frame, "Maximum number of attempts reached. Exiting program.");
		System.exit(0);
	}
	Socket socket = null;
	try {
        socket = new Socket(serverAddress, serverPort);
	messageArea.append("Connection Succeeded. Server: " + serverAddress + ":" + serverPort + "\n");
	} catch (IOException e) {
		JOptionPane.showMessageDialog(frame, "Could not establish a connection to the specified server (" + serverAddress + ") and port (" + serverPort + ")", "Connection Failed", JOptionPane.ERROR_MESSAGE);
		run();
	}
	String username;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
		username=getName();
		if(username == null) {
			System.exit(0);
		}
		out.println(username);
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
		messageArea.append(line.substring(8) + "\n");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
