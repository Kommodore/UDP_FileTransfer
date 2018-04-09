import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;

	private DatagramSocket cSocket;

	// JLabel label_ipAddr, labl_port, label_chunkSize, label_fileName;
	JTextField text_ipAddr, text_port, text_chunkSize, text_fileName;
	JButton submit;

	public Client(String serverAddr, int chunkSize, String fileName) {
		initWindow();
	}

	private void initWindow() {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		this.setTitle("Client");
		this.setLayout(gbl);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add buttons
		text_ipAddr = new JTextField("127.0.0.1");
		text_ipAddr.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(text_ipAddr, c);

		text_port = new JTextField("8999");
		text_port.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(text_port, c);

		text_chunkSize = new JTextField("256");
		text_chunkSize.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(text_chunkSize, c);

		text_fileName = new JTextField("myfile.txt");
		text_fileName.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(text_fileName, c);

		submit = new JButton("Submit");
		submit.setPreferredSize(new Dimension(300, 40));
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startFileTransfer(text_ipAddr.getText(), Integer.parseInt(text_port.getText()),
						Integer.parseInt(text_chunkSize.getText()), text_fileName.getText());
			}
		});
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(20, 20, 20, 20);
		this.add(submit, c);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void startFileTransfer(String serverAddr, int port, int chunkSize, String fileName) {
		String action, param;
		InetAddress ipAddr;

		byte[] recData = new byte[256];

		String initConnection = "HSOSSTP_INITX;" + chunkSize + ";" + fileName;
		System.out.println("> " + initConnection);

		try {
			ipAddr = InetAddress.getByName(serverAddr);
			cSocket = new DatagramSocket();

			DatagramPacket sendPacket = new DatagramPacket(initConnection.getBytes(), initConnection.getBytes().length,
					ipAddr, port);
			cSocket.send(sendPacket);

			DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
			cSocket.setSoTimeout(5000);
			cSocket.receive(recPacket);

			String recString = new String(recPacket.getData()).replaceAll("\0", "");
			System.out.println(recString);

			Scanner sc = new Scanner(recString);
			sc.useDelimiter(";");
			action = sc.next();
			param = sc.next();
			sc.close();
			
			switch (action) {
			case "HSOSSTP_ERROR":
				// FILE NOT FOUND
				System.out.println("Param:" + param);
				break;

			default:
				System.out.println(recString);
				break;
			}

		} catch (SocketTimeoutException e) {
			System.out.println("Timed out!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cSocket.close();
	}

	public static void main(String args[]) {
		Client client = new Client("127.0.0.1", 256, "myfile.txt");
	}

}
