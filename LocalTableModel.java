/* ***********************
** LocalTableModel.java
** ***********************
** 用于构造本地文件列表模型, 还略为简陋
** 待增加功能: 目录文件分开显示, 返回图标
** Build 0714
** 07-14 使目录文件分开显示
** **********************/

package ArkFTP.bin.model;

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
		addColumn("");
		addColumn("名称");
		addColumn("大小");
		addColumn("修改日期");
	}
	
	// 令表格单元格不可修改
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	// 清楚整张表格
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

	// 在本地窗格中添加文件项	
	public void addRow(File file)
	{
		if (file == null || !file.exists())
			return;
		
		Object [] rowData = new Object[this.TABLE_LEN];
		
		if (file.isDirectory())
			rowData[0] = new ImageIcon("ArkFTP/res/dirIcon.png");
		else
			rowData[0] = new ImageIcon("ArkFTP/res/fileIcon.png");
		
		rowData[this.NAME_COL] = file.getName();
		rowData[this.SIZE_COL] = file.length();
		rowData[this.DATE_COL] = new Date(file.lastModified());	
		this.fireTableDataChanged();
	}
	
	// 添加当前目录中的所有文件目录
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
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
					rowData[this.DATE_COL] = f.format(new Date(millis));
				}
				else
					continue;
				if (file.isDirectory())
					rowData[0] = new ImageIcon("ArkFTP/res/dirIcon.png");
				else
					rowData[0] = new ImageIcon("ArkFTP/res/fileIcon.png");
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
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
					rowData[this.DATE_COL] = f.format(new Date(millis));
				}
				else
					continue;
				if (file.isDirectory())
					rowData[0] = new ImageIcon("ArkFTP/res/dirIcon.png");
				else
					rowData[0] = new ImageIcon("ArkFTP/res/fileIcon.png");
				rowData[this.NAME_COL] = file.getName();
				rowData[this.SIZE_COL] = new Long(file.length()).toString();
				if (file.isDirectory() == false) this.addRow(rowData);
			}
		}
		this.fireTableDataChanged();
	}	
	
}
