/* ***********************
** AlignmentRenderer.java
** ***********************
** 用于调整表格列的各列水平对齐方式
** Build 0714
** 07-14 增加隔行背景色不同功能
** **********************/
package ArkFTP.bin.ui;

import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class AlignmentRenderer extends DefaultTableCellRenderer
{
	public Component getTableCellRendererComponent(JTable 	table,
												   Object 	value,
	                                               boolean  isSelected,
	                                               boolean  hasFocus,
	                                               int      row,
	                                               int      column)
	{
		String s = value.toString();
	    setText(s);
	    if (isSelected)
	    {
	    	setBackground(table.getSelectionBackground());
	    	setForeground(table.getSelectionForeground());
	    }
	    else {
	    	if (row % 2 == 0)
	    	{
  				this.setBackground(Color.WHITE);
   			}
   			else
   			{
    			this.setBackground(new Color(222, 236, 255));
  			}
	    	setForeground(table.getForeground());
	    }
	    setEnabled(table.isEnabled());
	    setFont(table.getFont());
	    setOpaque(true);
		if (column == 0)
			setHorizontalAlignment(SwingConstants.CENTER);
		else if (column == 1)
			setHorizontalAlignment(SwingConstants.LEFT);
		else
			setHorizontalAlignment(SwingConstants.RIGHT);
		return this;
	}
}