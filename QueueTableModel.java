/* ***********************
** QueueTableModel.java
** ***********************
** 用于构造队列列表模型
** 
** Build 0712
** **********************/

package ArkFTP.bin.model;

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
		addColumn("文件名");
		addColumn("大小");
		addColumn("目标路径");
		addColumn("类型");
	}
	
	// 令单元格内容不可修改
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	
	// 清除队列所有内容
	public void removeAllRows()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}

}
