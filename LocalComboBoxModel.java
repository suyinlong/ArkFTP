/* ***********************
** LocalComboBoxModel.java
** ***********************
** ���ڹ��챾����Ͽ�ģ��, ��ʾ�����������Լ���ǰ·��
** Build 0712
** **********************/
 
package ArkFTP.bin.model;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;



public class LocalComboBoxModel extends DefaultComboBoxModel
{
	public LocalComboBoxModel(Vector<String> v)
	{
		super(v);
	}
	
	// �����滻��Ͽ����ض�λ�õ�Item
	public void replace(int index, String new_str)
	{
		this.removeElementAt(index);
		this.insertElementAt(new_str, index);
		this.fireContentsChanged(this, index, index);
	}

	// �����Ͽ�����item_str��ȵĵ�һ���±�ֵ
	public int getIndexOf(String item_str)
	{
		for (int i = 0; i < this.getSize(); i++)
		{
			if (this.getElementAt(i).equals(item_str))
				return i;
		}
		return -1;
	}
}
