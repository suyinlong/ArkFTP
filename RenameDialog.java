package ArkFTP.bin.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RenameDialog extends JDialog
{
	JTextField jtf = null;
	
	public RenameDialog(Frame fm, final String[] value)
	{
		super(fm, "重命名", true);
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("输入新文件名:"));
		jtf = new JTextField();
		jtf.setPreferredSize(new Dimension(100, 20));
		panel1.add(jtf);
		JPanel panel2 = new JPanel();
		final JButton ok = new JButton("确认");
		ok.setPreferredSize(new Dimension(60, 20));
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String name_str = jtf.getText();
				if (!name_str.equals(""))
				{
					value[0] = name_str;
					ok.setFocusable(false);
					RenameDialog.this.setVisible(false);
					jtf.setText("");
				}
			}			
		});
		panel2.add(ok);
		this.add(panel1, BorderLayout.NORTH);
		this.add(panel2, BorderLayout.SOUTH);
		this.setSize(250, 100);
		this.setResizable(false);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getHeight()/2);
	}
}

