/* ***********************
** ServerTableModel.java
** ***********************
** 用于构造服务器列表模型
** 待处理: 目录与文件分开显示, 返回图标
** Build 0714
** 07-14 目录与文件分开显示完成
** **********************/

package ArkFTP.bin.model;

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
		addColumn("");
		addColumn("名称");
		addColumn("大小");
		addColumn("修改日期");
		addColumn("属性");
	}
	
	// 令单元格不可修改
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	// 删除表格中的所有行。
	public void removeAllRows()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}
	// 判断该行是否表示目录。
	public boolean isDirRow(int row)
	{
		assert(row >= 0 && row < this.getColumnCount());
		
		// Row 0 永远表示 ".." 目录。
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
	
	// 根据LIST命令服务器返回的数据生成表行，并加入表格。
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
				// Attrib 属性
				fileAttr_str_array[this.ATTRIB_COL] = stk.nextToken();
				// 跳过3个不关心的属性
				for (int k = 0; k < 3; k++)
					stk.nextToken();
				// Size 大小
				fileAttr_str_array[this.SIZE_COL] = stk.nextToken();
				
				// Time	修改时间
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
				
				// Name 文件名
				String name_str = stk.nextToken();
				while(stk.hasMoreTokens())
					name_str += " " + stk.nextToken();		
				fileAttr_str_array[this.NAME_COL] = name_str;
				
				if (!fileAttr_str_array[this.NAME_COL].equals(".") && !fileAttr_str_array[this.NAME_COL].equals("..")) {
					if (((String)fileAttr_str_array[this.ATTRIB_COL]).charAt(0) == 'd')
						fileAttr_str_array[this.ICON_COL] = new ImageIcon("ArkFTP/res/dirIcon.png");
					else
						fileAttr_str_array[this.ICON_COL] = new ImageIcon("ArkFTP/res/fileIcon.png");
					addRow(fileAttr_str_array);
				}
			}
			i = j + 1;
		}
	}
}
