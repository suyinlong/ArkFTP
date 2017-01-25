/* ***********************
** LocalTableModel.java
** ***********************
** Local Table Model
** Build 0714
** 07-14 divide folders from files
** **********************/

package ArkFTP.bin.model;

import ArkFTP.bin.ui.StringTable;
import ArkFTP.bin.ui.ResourceTable;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class LocalTableModel extends DefaultTableModel
{
	public final int TABLE_LEN = 4;

	public final int ICON_COL = 0;
	public final int NAME_COL = 1;
	public final int SIZE_COL = 2;
	public final int DATE_COL = 3;

	public LocalTableModel()
	{
		super();
		addColumn(StringTable.labelNull);
		addColumn(StringTable.labelName);
		addColumn(StringTable.labelSize);
		addColumn(StringTable.labelDateModified);
	}

	// set editable = false
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	// clear
	public void deleteTable()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}

	public Class getColumnClass(int col)
	{
		Vector v = (Vector)dataVector.elementAt(0);
		return v.elementAt(col).getClass();
	}

	// Add file to the table
	public void addRow(File file)
	{
		if (file == null || !file.exists())
			return;

		Object [] rowData = new Object[this.TABLE_LEN];

		if (file.isDirectory())
			rowData[0] = new ImageIcon(ResourceTable.iconDir);
		else
			rowData[0] = new ImageIcon(ResourceTable.iconFile);

		rowData[this.NAME_COL] = file.getName();
		rowData[this.SIZE_COL] = file.length();
		rowData[this.DATE_COL] = new Date(file.lastModified());
		this.fireTableDataChanged();
	}

	// Add the subfolders in current folder to the table
	public void addAllChildren(File parent)
	{
		dataVector.clear();
		Object[] rowData = {new ImageIcon(), "..", "", ""};
		this.addRow(rowData);
		File[] children = parent.listFiles();
		if (children != null)
		{
			for (File file : children)
			{
				if (file == null || !file.exists())
					return;
				long millis = file.lastModified();
				if (millis != 0) {
					rowData = new Object[this.TABLE_LEN];
					SimpleDateFormat f = new SimpleDateFormat(StringTable.formatDateModified);
					rowData[this.DATE_COL] = f.format(new Date(millis));
				}
				else
					continue;
				if (file.isDirectory())
					rowData[0] = new ImageIcon(ResourceTable.iconDir);
				else
					rowData[0] = new ImageIcon(ResourceTable.iconFile);
				rowData[this.NAME_COL] = file.getName();
				rowData[this.SIZE_COL] = new Long(file.length()).toString();
				if (file.isDirectory()) this.addRow(rowData);
			}
			for (File file : children)
			{
				if (file == null || !file.exists())
					return;
				long millis = file.lastModified();
				if (millis != 0) {
					rowData = new Object[this.TABLE_LEN];
					SimpleDateFormat f = new SimpleDateFormat(StringTable.formatDateModified);
					rowData[this.DATE_COL] = f.format(new Date(millis));
				}
				else
					continue;
				if (file.isDirectory())
					rowData[0] = new ImageIcon(ResourceTable.iconDir);
				else
					rowData[0] = new ImageIcon(ResourceTable.iconFile);
				rowData[this.NAME_COL] = file.getName();
				rowData[this.SIZE_COL] = new Long(file.length()).toString();
				if (file.isDirectory() == false) this.addRow(rowData);
			}
		}
		this.fireTableDataChanged();
	}

}
