/* ***********************
** LocalComboBoxModel.java
** ***********************
** 用于构造本地组合框模型, 显示本地驱动器以及当前路径
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
	
	// 用于替换组合框中特定位置的Item
	public void replace(int index, String new_str)
	{
		this.removeElementAt(index);
		this.insertElementAt(new_str, index);
		this.fireContentsChanged(this, index, index);
	}

	// 获得组合框中与item_str相等的第一个下标值
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
