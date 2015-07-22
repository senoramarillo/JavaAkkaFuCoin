package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WalletGui implements IWalletGuiControle {
	DefaultListModel<String> log = new DefaultListModel<String>();

	private JFrame window = new JFrame("test");
	JPanel topPanel = new JPanel();
	JLabel lblMyAddress = new JLabel("My Address:");
	JTextField txtMyAddress = new JTextField("<MyAddress>");
	JLabel lblEmpty = new JLabel("");
	JLabel lblMyAmount = new JLabel("My FUCs");
	JTextField txtMyAmount = new JTextField("<MyFUCs>");
	JPanel centerPanel = new JPanel();
	JLabel lblSendTo = new JLabel("Send to:");
	JComboBox<String> txtSendTo = new JComboBox<String>();
	JLabel lblSendAmount = new JLabel("Amount:");
	JTextField txtSendAmount = new JTextField("<Amount>");
	JButton btnSend = new JButton("Send");
	JButton btnSearch = new JButton("Search");
	JButton btnStore = new JButton("Store");
	JButton btnExit = new JButton("Exit");
	JPanel bottomPanel = new JPanel();
	JList<String> txtLog = new JList<String>(log);

	public WalletGui(IWalletControle walletControle) {

		window.setSize(400, 600);
		window.setLayout(new GridLayout(3, 1));
		topPanel.setLayout(new GridLayout(2, 3));
		// Row 1
		topPanel.add(lblMyAddress);
		topPanel.add(txtMyAddress);
		topPanel.add(lblEmpty);
		// Row 2
		topPanel.add(lblMyAmount);
		topPanel.add(txtMyAmount);
		window.add(topPanel);
		// <hr>
		centerPanel.setLayout(new GridLayout(4, 1));
		// Row 1
		JPanel centerup = new JPanel();
		centerup.setLayout(new BorderLayout());
		centerup.add(lblSendTo, BorderLayout.WEST);
		centerup.add(txtSendTo, BorderLayout.CENTER);
		centerPanel.add(centerup);

		JPanel centerup2 = new JPanel();
		centerup2.setLayout(new BorderLayout());
		JTextField sendToNewEdt = new JTextField();
		centerup2.add(sendToNewEdt, BorderLayout.CENTER);
		JButton addNewButton = new JButton("Add");
		addNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				txtSendTo.addItem(sendToNewEdt.getText());
			}
		});
		centerup2.add(addNewButton, BorderLayout.EAST);
		centerPanel.add(centerup2);

		// Row 2
		JPanel centerdown = new JPanel();
		centerdown.setLayout(new GridLayout(1, 3));
		centerdown.add(lblSendAmount);
		centerdown.add(txtSendAmount);
		centerdown.add(btnSend);
		centerPanel.add(centerdown);
		// centerPanel.add(new JLabel(""));
		// Row 3
		JPanel centerdown2 = new JPanel();
		centerdown2.setLayout(new GridLayout(1, 3));
		centerdown2.add(btnSearch);
		centerdown2.add(btnStore);
		centerdown2.add(btnExit);
		centerPanel.add(centerdown2);
		window.add(centerPanel);
		// <hr>
		bottomPanel.setLayout(new GridLayout(1, 1));
		bottomPanel.add(txtLog);
		window.add(bottomPanel);
		window.setVisible(true);

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				walletControle.send(txtSendTo.getSelectedItem().toString(),
						Integer.parseInt(txtSendAmount.getText()));
			}
		});

		btnStore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				window.dispose();
			}
		});

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("window closing");
				walletControle.leave();
			}
		});
	}

	@Override
	public void setAddress(String address) {
		txtMyAddress.setText(address);
		window.setTitle(address);
	}

	@Override
	public void setAmount(int amount) {
		txtMyAmount.setText("" + amount);
	}

	@Override
	public void addKnownAddress(String address) {

		txtSendTo.addItem(address);
	}

	@Override
	public void addLogMsg(String msg) {
		log.addElement(msg);
	}
}