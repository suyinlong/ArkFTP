/* ***********************
** ConnectLogDialog.java
** ***********************
** ������־�Ի���
** Build 0714
** 07-14 ������¹���
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
	private Color DialogBackground = new Color(191, 219,255);
	
	public ConnectLogDialog(final MainFrame parent)
	{
		super(parent, "������־", true);
		
		this.setSize(520, 360);
		this.setResizable(false);

		UIManager.put("OptionPane.background", DialogBackground);
				
		Box MainBox = Box.createVerticalBox();
						
		JPanel TitlePanel = new JPanel();
		JLabel CLDInfo = new JLabel("�ô�������־��");
		TitlePanel.add(CLDInfo);
		
		ConnectLogJTA = new JTextArea();
		UpdateInfo(parent);
		ConnectLogJTA.setBackground(DialogBackground);
		ConnectLogJTA.setRows(10);
		ConnectLogJTA.setLineWrap(true);
		JScrollPane ConnectLogJSP = new JScrollPane(ConnectLogJTA);
				
		JPanel IconButtonPanel = new JPanel();
		JButton[] IconButton = { new JButton("ȷ��",  new ImageIcon("ArkFTP/res/Okay.png")),
														new JButton("���Ƶ�������",  new ImageIcon("ArkFTP/res/CopyLog.png")),
														new JButton("����Ϊ�ļ�",  new ImageIcon("ArkFTP/res/SaveLog.png")) };
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
    		    JOptionPane.showMessageDialog(null, "��־�����Ѿ����Ƶ�������!");
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
			FileWriter fout = new FileWriter("ArkFTP/Log.txt");
			BufferedWriter f = new BufferedWriter(fout);
			char[] b = ConnectLogJTA.getText().toCharArray();
			f.write(b);
			f.flush();
			f.close();
			JOptionPane.showMessageDialog(null, "��־�����Ѿ�������ArkFTPĿ¼�µ�Log.txt�С�");		
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "�����ļ�ArkFTP/Log.txtʱ���ִ�����ȷ���Ƿ������Ȩ�ޡ�");
		}	
	}
	public void UpdateInfo(MainFrame parent)
	{
		String CLInfo = parent.getLogTextArea().getText();
		ConnectLogJTA.setText(CLInfo);
		ConnectLogJTA.select(0, 0);
	}
}
