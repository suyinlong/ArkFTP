/* ***********************
** QueueTableModel.java
** ***********************
** ���ڹ�������б�ģ��
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
		addColumn("�ļ���");
		addColumn("��С");
		addColumn("Ŀ��·��");
		addColumn("����");
	}
	
	// �Ԫ�����ݲ����޸�
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}
	
	// ���������������
	public void removeAllRows()
	{
		dataVector.clear();
		this.fireTableDataChanged();
	}

}
