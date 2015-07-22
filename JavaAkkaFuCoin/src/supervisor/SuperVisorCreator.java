package supervisor;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import akka.japi.Creator;

public class SuperVisorCreator implements Creator<SuperVisor>{

	@Override
	public SuperVisor create() throws Exception {
		
		JFrame frame = new JFrame("Server");
		frame.setLayout(new GridLayout(3, 2));
		frame.add(new Label("All Amounts:"));
		AmountTableModel amountTableModel = new AmountTableModel();
		JTable amountListView = new JTable(amountTableModel);
		frame.add(new JScrollPane(amountListView));
		frame.add(new Label("Average Amounts:"));
		Label averageamountLbl = new Label("Average Amounts:");
		frame.add(averageamountLbl);
		JButton updateBtn = new JButton("Update");
		JButton exitBtn = new JButton("exit");
		frame.add(updateBtn);
		frame.add(exitBtn);
		
		SuperVisor sv = new SuperVisor(amountTableModel,averageamountLbl);
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sv.updateValues();
			}
		});
		exitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sv.exit();
				frame.setVisible(false);
			}
		});
		frame.setSize(200, 400);
		frame.setVisible(true);
		
		return sv;
	}
}