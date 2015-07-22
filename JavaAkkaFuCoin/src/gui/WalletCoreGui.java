package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import akka.actor.Address;
import akka.actor.AddressFromURIString;

public class WalletCoreGui {
	public WalletCoreGui() {
		JFrame frame = new JFrame("Manager");
		frame.setLayout(new GridLayout(3,2));
		frame.add(new JLabel("Connect to:"));
		JTextField input = new JTextField("akka://MySystem/user/main");
		frame.add(input);
		frame.add(new JLabel("Name:"));
		JTextField name = new JTextField("<Name>");
		frame.add(name);
		JButton button = new JButton("connect");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				/*MessageDispatcherConfigurator mc = null;
				String id = input.getText()+"-dispatched";
				int hroughput = 1;
				Duration d = Duration.ofSeconds(2);

				Dispatcher d = new Dispatcher(mc,id, 1, d, Executors.newSingleThreadExecutor(),1000);*/
				String path = "akka.tcp://Test@127.0.0.1:1234/user/main";
				Address addr = AddressFromURIString.parse(path);
				//RemoteScope remoteScope = new RemoteScope(addr);
				//Deploy deploy = new Deploy(remoteScope);
				//Props remoteWallet = Props.apply(deploy, Wallet.class, null);
			}
		});
		
		frame.add(button);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}