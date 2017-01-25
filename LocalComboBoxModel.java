/* ***********************
** LocalComboBoxModel.java
** ***********************
** Local combobox model, for local drive and current path
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

	// replace the item of the combobox
	public void replace(int index, String new_str)
	{
		this.removeElementAt(index);
		this.insertElementAt(new_str, index);
		this.fireContentsChanged(this, index, index);
	}

	// get the index in the combobox
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
