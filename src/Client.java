import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class Client {

	JFrame clientFrame;
	JTextArea textArea_Messages;
	JTextField textField_ClientMessage;
	JTextField textField_Username;
	JScrollPane scrollPane_Messages;

	Socket client;
	PrintWriter writer;
	BufferedReader reader;

	JButton Login;
	JTextField UserName;
	JPasswordField Passwort;

	JTextArea OnlineClients;
	JScrollPane scrollPane_OnlineClients;


	public static int port = 333;
	public static String ip = "localhost";
	static boolean loggedin = false;
	static boolean failed = false;
	static boolean error = false;
	static String username = "";
	static String password = "";
	Draw draw = new Draw();

	public static void main(String[] args) {


		Client c = new Client();
		c.LogginMenu();
	}

	public void LogginMenu() {
		clientFrame = new JFrame("TeamChat");
		clientFrame.setSize(900, 600);
		clientFrame.setResizable(false);

		textArea_Messages = new JTextArea();
		textArea_Messages.setEditable(false);
		textArea_Messages.setBounds(50, 5, 700, 500);
		textArea_Messages.setVisible(false);
		textField_ClientMessage = new JTextField(38);
		textField_ClientMessage.addKeyListener(new SendPressEnterListener());
		textField_ClientMessage.setBounds(170, 510, 580, 22);
		textField_ClientMessage.setVisible(false);

		textField_Username = new JTextField(10);
		textField_Username.setEditable(false);
		textField_Username.setBounds(50, 510, 115, 22);
		textField_Username.setText(username);
		textField_Username.setVisible(false);
		OnlineClients = new JTextArea();
		OnlineClients.setEditable(false);
		OnlineClients.setBounds(755, 5, 100, 300);
		OnlineClients.setVisible(false);
		scrollPane_Messages = new JScrollPane(textArea_Messages);
		scrollPane_Messages.setBounds(50, 5, 700, 500);

		scrollPane_Messages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_Messages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_Messages.setVisible(false);
		
		scrollPane_OnlineClients = new JScrollPane(OnlineClients);
		scrollPane_OnlineClients.setBounds(755, 5, 100, 300);
		
		scrollPane_OnlineClients.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_OnlineClients.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_OnlineClients.setVisible(false);
		if (!connectToServer()) {
			appendTextMessages("Netzwerkverbindung konnte nicht hergestellt werden");
		}

		Login = new JButton("Login");
		Login.setBounds(300, 400, 200, 50);
		Login.setBorderPainted(false);
		Login.setFocusPainted(false);
		Login.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				String pw = new String(Passwort.getPassword());
				if (!UserName.getText().isEmpty() && !pw.isEmpty()) {
					Thread t = new Thread(new MessagesFromServerListener());
					t.start();
				username = UserName.getText().toLowerCase();
				password = pw;
				writer.println("login;" + username+":"+pw);
				writer.flush();				
				}
				
					

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});

		Login.setVisible(true);
		UserName = new JTextField("");
		UserName.setBounds(250, 200, 300, 50);
		UserName.setEditable(true);
	

		UserName.setVisible(true);
		clientFrame.add(UserName);
		Passwort = new JPasswordField("");
		Passwort.setBounds(250, 255, 300, 50);
		Passwort.setEditable(true);
	

		Passwort.setVisible(true);
		clientFrame.add(Login);
		clientFrame.add(Passwort);

		draw.setBounds(0, 0, 800, 600);
		draw.setVisible(true);

		clientFrame.add(scrollPane_Messages);
		clientFrame.add(scrollPane_OnlineClients);
		clientFrame.add(textField_Username);
		clientFrame.add(textField_ClientMessage);
		clientFrame.add(draw);

		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.setVisible(true);
	}

	

	public boolean connectToServer() {
		try {
			client = new Socket(ip, port);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			appendTextMessages("Netzwerkverbindung hergestellt");

			return true;
		} catch (Exception e) {
			appendTextMessages("Netzwerkverbindung konnte nicht hergestellt werden");
			error = true;
			e.printStackTrace();

			return false;
		}
	}

	public static String insertString(String originalString, String stringToBeInserted, int index) {

		String newString = new String();

		for (int i = 0; i < originalString.length(); i++) {

			newString += originalString.charAt(i);

			if (i == index) {

				newString += stringToBeInserted;
			}
		}

		return newString;
	}

	public void sendMessageToServer() {
		if(!textField_ClientMessage.getText().isEmpty()) {
			String textToSend = textField_ClientMessage.getText();

				int splits = Math.round(textField_ClientMessage.getText().length() / 70);
				for(int x = 1;x<=splits;x++) {
					textToSend = insertString(textToSend, "\n", x*70);
				}
				writer.println(textToSend);
				writer.flush();

				textField_ClientMessage.setText("");
				textField_ClientMessage.requestFocus();
		

		}
	}

	public void appendTextMessages(String message) {
		textArea_Messages.append(message + "\n");
	}

	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				sendMessageToServer();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

	}

	public class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			sendMessageToServer();
		}

	}

	public class MessagesFromServerListener implements Runnable {

		@Override
		public void run() {
			String message;

			try {
				while ((message = reader.readLine()) != null) {
					if(loggedin == false) {
					
							if(message.equalsIgnoreCase("true")) {
								OnlineClients.append("");
								loggedin = true;
								failed = false;
								textField_Username.setText(username);
								textField_Username.setVisible(true);
								scrollPane_Messages.setVisible(true);
								scrollPane_OnlineClients.setVisible(true);
								textField_ClientMessage.setVisible(true);
								textArea_Messages.setVisible(true);
								OnlineClients.setVisible(true);
								Login.setVisible(false);
								UserName.setVisible(false);
								Passwort.setVisible(false);

								clientFrame.setBackground(Color.blue);
								draw.setVisible(false);
	
												
							}else {
								failed = true;
							}
							
						
					}else {
						if(!message.startsWith("Online;")) {
						appendTextMessages(message);
						textArea_Messages.setCaretPosition(textArea_Messages.getText().length());
						}else {
							String c = message.substring(7);
							String[] clients = c.split(",");
							OnlineClients.setText("");
							OnlineClients.setText(null);
							for(int x = 0;x<clients.length;x++) {
								OnlineClients.append(clients[x]+"\n");
							}
							OnlineClients.setCaretPosition(OnlineClients.getText().length());

						}
					}

				}
			} catch (IOException e) {
				appendTextMessages("Nachricht konnte nicht empfangen werden!");
				e.printStackTrace();
			}
		}

	}

	public class Draw extends JLabel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(new Color(0, 120, 200, 150));
			g.fillRect(0, 0, 800, 600);
			g.setColor(Color.BLACK);
			g.drawString("Username: ", 180, 230);
			g.drawString("Password: ", 180, 285);
			if (failed) {
				g.setColor(Color.RED);
				g.setFont(new Font("IMPACT", Font.BOLD, 32));
				g.drawString("Loggin Failed", 300, 180);
			}
			if (error) {
				g.setColor(Color.RED);
				g.setFont(new Font("IMPACT", Font.BOLD, 32));
				g.drawString("ERROR", 350, 180);
			}
			repaint();
		}
	}

}