/* ***********************
** AddDialog.java
** ***********************
** AddSite Dialog
** Build 0714
** **********************/
package ArkFTP.bin.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddDialog extends JDialog
{
	JTextField jtf = null;

	public AddDialog(final ServerManagerDialog smd)
	{
		super(smd, StringTable.dialogAddSiteTitle, true);
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel(StringTable.dialogAddSiteFTPSiteName));
		jtf = new JTextField();
		jtf.setPreferredSize(new Dimension(100, 20));
		panel1.add(jtf);
		JPanel panel2 = new JPanel();
		final JButton ok = new JButton(StringTable.buttonOK);
		ok.setPreferredSize(new Dimension(60, 20));
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String name_str = jtf.getText();
				if (!name_str.equals(""))
				{
					smd.addSite(name_str);
					ok.setFocusable(false);
					AddDialog.this.setVisible(false);
					jtf.setText("");
				}
			}
		});
		final JButton cancel = new JButton(StringTable.buttonCancel);
		cancel.setPreferredSize(new Dimension(60, 20));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				AddDialog.this.setVisible(false);
			}
		});

		panel2.add(ok);
		panel2.add(cancel);
		this.add(new JPanel(), BorderLayout.NORTH);
		this.add(panel1, BorderLayout.CENTER);
		this.add(panel2, BorderLayout.SOUTH);

		this.setSize(250, 120);
		this.setResizable(false);
		this.setLocation(smd.getWidth()/2 - this.getWidth()/2 + smd.getLocation().x,
						 smd.getWidth()/2 - this.getWidth()/2 + smd.getLocation().y);
	}
}
