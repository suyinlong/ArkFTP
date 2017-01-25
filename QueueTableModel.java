/* ***********************
** QueueTableModel.java
** ***********************
** Queue table model
**
** Build 0712
** **********************/

package ArkFTP.bin.model;

import ArkFTP.bin.ui.StringTable;
import ArkFTP.bin.ui.ResourceTable;

import javax.swing.table.DefaultTableModel;

public class QueueTableModel extends DefaultTableModel
{
	final public int TABLE_LEN = 4;

	final public int NAME_COL = 0;
	final public int SIZE_COL = 1;
	final public int TARGET_COL = 2;
	final public int TYPE_COL = 3;

	public QueueTableModel()
	{
		addColumn(StringTable.labelName);
		addColumn(StringTable.labelSize);
		addColumn(StringTable.labelTargetPath);
		addColumn(StringTable.labelType);
	}

	// set editable = false
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	// clear table
	public void removeAllRows()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}

}
