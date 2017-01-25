/* ***********************
** ServerTableModel.java
** ***********************
** Server Table Model
** Build 0714
** 07-14 divide the folders from files
** **********************/

package ArkFTP.bin.model;

import ArkFTP.bin.ui.StringTable;
import ArkFTP.bin.ui.ResourceTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class ServerTableModel extends DefaultTableModel
{
	public final int TABLE_LEN = 5;

	public final int ICON_COL = 0;
	public final int NAME_COL = 1;
	public final int SIZE_COL = 2;
	public final int DATE_COL = 3;
	public final int ATTRIB_COL = 4;

	public ServerTableModel()
	{
		super();
		addColumn(StringTable.labelNull);
		addColumn(StringTable.labelName);
		addColumn(StringTable.labelSize);
		addColumn(StringTable.labelDateModified);
		addColumn(StringTable.labelAttributes);
	}

	// set editable = false
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	// clear the table
	public void removeAllRows()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}
	// judge if the row is a folder
	public boolean isDirRow(int row)
	{
		assert(row >= 0 && row < this.getColumnCount());

		// Row 0  = ".."
		if (row == 0)
			return true;

		String str = (String)this.getValueAt(row, this.ATTRIB_COL);
		if (str.charAt(0) == 'd')
			return true;
		return false;
	}

	public Class getColumnClass(int col)
	{
		Vector v = (Vector)dataVector.elementAt(0);
		return v.elementAt(col).getClass();
	}

	// use the data returned by LIST of the server to create rows
	public void addToTable(String dataofList_str)
	{
		if (dataofList_str == null)
			return;

		Object[] fileAttr_str_array = {new ImageIcon(), "..", "", "", ""};
		addRow(fileAttr_str_array);

		int i = 0, j;
		while( i < dataofList_str.length()) {
			if ( (j = i + dataofList_str.substring(i).indexOf('\n')) == -1)
				break;

			StringTokenizer stk = new StringTokenizer(dataofList_str.substring(i, j));
			fileAttr_str_array = new Object[TABLE_LEN];
			int count = stk.countTokens();
			if (count >= 9) {
				// Attrib
				fileAttr_str_array[this.ATTRIB_COL] = stk.nextToken();
				// ignore 3 attrubites I don't care about
				for (int k = 0; k < 3; k++)
					stk.nextToken();
				// Size
				fileAttr_str_array[this.SIZE_COL] = stk.nextToken();

				// Time
				SimpleDateFormat f1 = new SimpleDateFormat("MMM dd yyyy", new Locale("ENGLISH"));
				SimpleDateFormat f2 = new SimpleDateFormat("yyyy MMM dd hh:mm", new Locale("ENGLISH"));
				SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
				String time_str = stk.nextToken();
				for (int k = 0; k < 2; k++)
					time_str += " " + stk.nextToken();
				try
				{
					if (time_str.charAt(time_str.length() - 3) == ':')
					{
						Calendar calendar = Calendar.getInstance();
						int year = calendar.get(Calendar.YEAR);
						time_str = new Integer(year).toString() + ' ' + time_str;
						Date date = f2.parse(time_str);
						time_str = f3.format(date);
					}
					else
					{
						Date date = f1.parse(time_str);
						time_str = f3.format(date);
					}
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				fileAttr_str_array[this.DATE_COL] = time_str;

				// Name
				String name_str = stk.nextToken();
				while(stk.hasMoreTokens())
					name_str += " " + stk.nextToken();
				fileAttr_str_array[this.NAME_COL] = name_str;

				if (!fileAttr_str_array[this.NAME_COL].equals(".") && !fileAttr_str_array[this.NAME_COL].equals("..")) {
					if (((String)fileAttr_str_array[this.ATTRIB_COL]).charAt(0) == 'd')
						fileAttr_str_array[this.ICON_COL] = new ImageIcon(ResourceTable.iconDir);
					else
						fileAttr_str_array[this.ICON_COL] = new ImageIcon(ResourceTable.iconFile);
					addRow(fileAttr_str_array);
				}
			}
			i = j + 1;
		}
	}
}
