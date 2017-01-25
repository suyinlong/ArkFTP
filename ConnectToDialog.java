/* ***********************
** ConnectToDialog.java
** ***********************
** 连接到对话框
** Build 0718
** **********************/
package ArkFTP.bin.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import ArkFTP.bin.model.SiteListModel;

public class ConnectToDialog extends JDialog
{
	private JTextField AddressJTF, PortJTF, UsernameJTF;
	private JPasswordField PasswordJPF;
	private Color[] UsedColor = { new Color(222, 236, 255),
												new Color(255, 255, 255) };
	
	public ConnectToDialog(final MainFrame owner)
	{
		super(owner, "连接到...", true);
		
		this.setSize(350, 325);
		this.setResizable(false);
		this.setLayout(null);
		
		UIManager.put("TextField.background", UsedColor[0]);
		UIManager.put("PasswordField.background", UsedColor[0]);
		
		JLabel CTInfoLabel1 = new JLabel("快速连接");
		CTInfoLabel1.setBounds(50, 30, 200, 20);
		this.add(CTInfoLabel1);
		
		JLabel CTInfoLabel2 = new JLabel("使用 ArkFTP 连接到远程FTP服务器");
		CTInfoLabel2.setBounds(50, 60, 200, 20);
		this.add(CTInfoLabel2);
		
		JLabel AddressLabel = new JLabel("服务器地址：");
		AddressLabel.setBounds(20, 100, 200, 20);
		this.add(AddressLabel);
		
		JLabel PortLabel = new JLabel("端口：");
		PortLabel.setBounds(20, 130, 200, 20);
		this.add(PortLabel);
		
		JLabel UsernameLabel = new JLabel("用户名：");
		UsernameLabel.setBounds(20, 160, 200, 20);
		this.add(UsernameLabel);
		
		JLabel PasswordLabel = new JLabel("密码：");
		PasswordLabel.setBounds(20, 190, 200, 20);
		this.add(PasswordLabel);
		
		AddressJTF = new JTextField();
		AddressJTF.setBounds(120, 100, 200, 20);
		AddressJTF.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
						TextFieldFocusGained((JComponent)e.getSource());
				}
				public void focusLost(FocusEvent e)
				{
						TextFieldFocusLost((JComponent)e.getSource());
				}
			});
		this.add(AddressJTF);
		
		PortJTF = new JTextField();
		PortJTF.setBounds(120, 130, 50, 20);
		PortJTF.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
						TextFieldFocusGained((JComponent)e.getSource());
				}
				public void focusLost(FocusEvent e)
				{
						TextFieldFocusLost((JComponent)e.getSource());
				}
			});
		this.add(PortJTF);
		
		UsernameJTF = new JTextField();
		UsernameJTF.setBounds(120, 160, 100, 20);
		UsernameJTF.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
						TextFieldFocusGained((JComponent)e.getSource());
				}
				public void focusLost(FocusEvent e)
				{
						TextFieldFocusLost((JComponent)e.getSource());
				}
			});
		this.add(UsernameJTF);
		
		PasswordJPF = new JPasswordField();
		PasswordJPF.setBounds(120, 190, 100, 20);
		PasswordJPF.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
						TextFieldFocusGained((JComponent)e.getSource());
				}
				public void focusLost(FocusEvent e)
				{
						TextFieldFocusLost((JComponent)e.getSource());
				}
			});
		this.add(PasswordJPF);
		
		JButton ConnectButton = new JButton("连接");
		ConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String ServerStr = AddressJTF.getText();
				String PortStr = PortJTF.getText();
				String UsernameStr = UsernameJTF.getText();
				String PasswordStr = new String(PasswordJPF.getPassword());
				
				setVisible(false);
				owner.startFTP(UsernameStr, PasswordStr, ServerStr, PortStr);
			}
		});
		ConnectButton.setBounds(70, 240, 80, 25);
		this.add(ConnectButton);
		
		JButton CancelButton = new JButton("取消");
		CancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}			
		});
		CancelButton.setBounds(220, 240, 80, 25);
		this.add(CancelButton);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getHeight()/2);

	}
	public void TextFieldFocusGained(JComponent TF)
	{
		TF.setBackground(UsedColor[1]);	
	}
	public void TextFieldFocusLost(JComponent TF)
	{
		TF.setBackground(UsedColor[0]);	
	}
}
