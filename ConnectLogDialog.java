/* ***********************
** ConnectLogDialog.java
** ***********************
** ConnectLog Dialog
** Build 0714
** 07-14 Add new functionality
** **********************/
package ArkFTP.bin.ui;

import javax.swing.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import javax.swing.JOptionPane;

public class ConnectLogDialog extends JDialog
{
	private JTextArea ConnectLogJTA;

	public ConnectLogDialog(final MainFrame parent)
	{
		super(parent, StringTable.dialogConnectionLogTitle, true);

		this.setSize(520, 360);
		this.setResizable(false);

		UIManager.put("OptionPane.background", ResourceTable.colorDialogBackground);

		Box MainBox = Box.createVerticalBox();

		JPanel TitlePanel = new JPanel();
		JLabel CLDInfo = new JLabel(StringTable.dialogConnectionLogSessionLog);
		TitlePanel.add(CLDInfo);

		ConnectLogJTA = new JTextArea();
		UpdateInfo(parent);
		ConnectLogJTA.setBackground(ResourceTable.colorDialogBackground);
		ConnectLogJTA.setRows(10);
		ConnectLogJTA.setLineWrap(true);
		JScrollPane ConnectLogJSP = new JScrollPane(ConnectLogJTA);

		JPanel IconButtonPanel = new JPanel();
		JButton[] IconButton = { new JButton(StringTable.buttonOK,  new ImageIcon(new ImageIcon(ResourceTable.iconDialogConnectionLogButtonOkay).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT))),
														new JButton(StringTable.dialogConnectionLogButtonCopyLog,  new ImageIcon(new ImageIcon(ResourceTable.iconDialogConnectionLogButtonCopyLog).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT))),
														new JButton(StringTable.dialogConnectionLogButtonSaveLog,  new ImageIcon(new ImageIcon(ResourceTable.iconDialogConnectionLogButtonSaveLog).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT))) };
		IconButton[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		IconButton[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				StringSelection ss = new StringSelection(ConnectLogJTA.getText());
    		    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    		    JOptionPane.showMessageDialog(null, StringTable.dialogConnectionLogCopyLogText);
			}
		});
		IconButton[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
					ConnectLogDialog.this.SaveLogFile();
			}
		});
		IconButtonPanel.add(IconButton[0]);
		IconButtonPanel.add(IconButton[1]);
		IconButtonPanel.add(IconButton[2]);

		MainBox.add(TitlePanel);
		Component BoxGlue = Box.createVerticalGlue();
		BoxGlue.setVisible(false);
		MainBox.add(BoxGlue);
		MainBox.add(ConnectLogJSP);
		MainBox.add(IconButtonPanel);

		this.add(new JPanel(), BorderLayout.WEST);
		this.add(new JPanel(), BorderLayout.EAST);
		this.add(MainBox, BorderLayout.CENTER);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getWidth()/2);

	}
	private void SaveLogFile()
	{
		try
		{
			FileWriter fout = new FileWriter(StringTable.dialogConnectionLogSaveLogPath);
			BufferedWriter f = new BufferedWriter(fout);
			char[] b = ConnectLogJTA.getText().toCharArray();
			f.write(b);
			f.flush();
			f.close();
			JOptionPane.showMessageDialog(null, StringTable.dialogConnectionLogSaveLogText);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, StringTable.dialogConnectionLogSaveLogErrorText);
		}
	}
	public void UpdateInfo(MainFrame parent)
	{
		String CLInfo = parent.getLogTextArea().getText();
		ConnectLogJTA.setText(CLInfo);
		ConnectLogJTA.select(0, 0);
	}
}
