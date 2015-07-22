package supervisor;

import javax.swing.table.DefaultTableModel;

public class AmountTableModel extends DefaultTableModel {
	public AmountTableModel() {
		super(new Object[] { "Address", "Name", "Amount" }, 0);
	}

	public void clear() {
		while (getRowCount() > 0) {
			removeRow(0);
		}
	}
}