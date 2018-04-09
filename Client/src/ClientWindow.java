import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ClientWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel label_ipAddr, label_port, label_chunkSize, label_fileName;
	private JTextField text_ipAddr, text_port, text_chunkSize, text_fileName;
	private JButton submit;

	public ClientWindow() {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		this.setTitle("Client");
		this.setLayout(gbl);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		label_ipAddr = new JLabel("IP-Address: "); 
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(label_ipAddr, c);

		text_ipAddr = new JTextField("127.0.0.1");
		text_ipAddr.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 20, 0, 20);
		this.add(text_ipAddr, c);

		label_port = new JLabel("Port: ");
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(label_port, c);

		text_port = new JTextField("8999");
		text_port.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 20, 0, 20);
		this.add(text_port, c);

		label_chunkSize = new JLabel("Chunksize: ");
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(label_chunkSize, c);

		text_chunkSize = new JTextField("256");
		text_chunkSize.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(10, 20, 0, 20);
		this.add(text_chunkSize, c);

		label_fileName = new JLabel("Filename: ");
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(20, 20, 0, 20);
		this.add(label_fileName, c);	
		
		text_fileName = new JTextField("myfile.txt");
		text_fileName.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 7;
		c.insets = new Insets(10, 20, 0, 20);
		this.add(text_fileName, c);

		submit = new JButton("Transfer File");
		submit.setPreferredSize(new Dimension(300, 40));
		c.gridx = 0;
		c.gridy = 8;
		c.insets = new Insets(20, 20, 20, 20);
		this.add(submit, c);

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void showError(String msg, String title) {
		JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	public void addCustomActionListener(ActionListener al) {
		this.submit.setActionCommand("submit");
		this.submit.addActionListener(al);
	}

	public String getIPAddr() {
		return this.text_ipAddr.getText();
	}

	public int getPort() {
		return Integer.parseInt(this.text_port.getText());
	}

	public int getChunkSize() {
		return Integer.parseInt(this.text_chunkSize.getText());
	}

	public String getFileName() {
		return this.text_fileName.getText();
	}
}
