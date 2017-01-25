/* ***********************
** ServerManagerDialog.java
** ***********************
** Server Manager
** Build 0714
** 07-14 Fix a bug: out of index when refreshing
** 07-15 Change site data from WriteObject to XML
** **********************/
package ArkFTP.bin.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import ArkFTP.bin.model.SiteListModel;

public class ServerManagerDialog extends JDialog
{
	private SiteListModel model;

	private AddDialog add_dlg;

	private JTextField siteName_jtf;
	private JTextField address_jtf;
	private JTextField port_jtf;
	private JTextField user_jtf;
	private JPasswordField pass_jpf;

	public void addSite(String siteName)
	{
		model.addSite(siteName);
	}

	public void removeSite(String siteName)
	{
		model.removeSite(siteName);
	}

	public ServerManagerDialog(final MainFrame owner)
	{
		super(owner, StringTable.dialogServerManagerTitle, true);
		model = new SiteListModel();

		this.setSize(520, 330);
		this.setResizable(false);

		JPanel mainPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel panel = new JPanel();

		JLabel ServerListLabel = new JLabel(StringTable.dialogServerManagerServerList);

		panel.add(ServerListLabel);
		panel.setPreferredSize(new Dimension(220, 20));

		westPanel.add(panel, BorderLayout.NORTH);

		final JList sites_list = new JList(model);
		JScrollPane list_scroll = new JScrollPane(sites_list);
		list_scroll.setPreferredSize(new Dimension(220, 200));

		westPanel.add(list_scroll, BorderLayout.CENTER);
		westPanel.setPreferredSize(new Dimension(240, 247));

		//Border border1 = BorderFactory.createLineBorder(Color.BLUE);
		//westPanel.setBorder(border1);
		mainPanel.add(westPanel, BorderLayout.WEST);

		JPanel eastPanel = new JPanel();
		Box vbox = Box.createVerticalBox();
		eastPanel.add(vbox);
		// -> SiteName
		JPanel panel1 = new JPanel();
		panel1.setPreferredSize(new Dimension(240, 30));

		JLabel SiteNameLabel = new JLabel(StringTable.labelNameColon);
		panel1.add(SiteNameLabel);

		siteName_jtf = new JTextField();
		siteName_jtf.setPreferredSize(new Dimension(150, 20));
		panel1.add(siteName_jtf);
		// -> Address & Port
		JPanel panel2 = new JPanel();
		panel2.setPreferredSize(new Dimension(240, 30));

		JLabel AddressLabel = new JLabel(StringTable.labelAddressColon);
		panel2.add(AddressLabel);

		address_jtf = new JTextField();
		address_jtf.setPreferredSize(new Dimension(100, 20));
		panel2.add(address_jtf);

		JLabel PortLabel = new JLabel(StringTable.labelPortColon);
		panel2.add(PortLabel);

		port_jtf = new JTextField();
		port_jtf.setPreferredSize(new Dimension(35, 20));
		panel2.add(port_jtf);

		// -> UserName
		JPanel panel3 = new JPanel();
		panel3.setPreferredSize(new Dimension(240, 30));

		JLabel UsernameLabel = new JLabel(StringTable.labelUsernameColon);
		panel3.add(UsernameLabel);

		user_jtf = new JTextField();
		user_jtf.setPreferredSize(new Dimension(150, 20));
		panel3.add(user_jtf);
		// -> PassWord
		JPanel panel4 = new JPanel();
		panel4.setPreferredSize(new Dimension(240, 30));

		JLabel PasswordLabel = new JLabel(StringTable.labelPasswordColon);
		panel4.add(PasswordLabel);

		pass_jpf = new JPasswordField();
		pass_jpf.setPreferredSize(new Dimension(150, 20));
		panel4.add(pass_jpf);

		vbox.add(Box.createVerticalStrut(20));
		vbox.add(panel1);
		vbox.add(Box.createVerticalStrut(25));
		vbox.add(panel2);
		vbox.add(Box.createVerticalStrut(25));
		vbox.add(panel3);
		vbox.add(Box.createVerticalStrut(25));
		vbox.add(panel4);
		vbox.add(Box.createVerticalStrut(20));
		//Border border2 = BorderFactory.createLineBorder(Color.BLUE);
		//eastPanel.setBorder(border2);
		mainPanel.add(eastPanel, BorderLayout.EAST);

		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(600, 30));

		final JButton add_btn = new JButton(StringTable.buttonAddSite);
		add_dlg = new AddDialog(this);
		add_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				add_dlg.setVisible(true);
				add_dlg.setFocusable(false);
			}
		});
		add_btn.setPreferredSize(new Dimension(120, 20));

		final JButton del_btn = new JButton(StringTable.buttonDeleteSite);
		del_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int[] indices = sites_list.getSelectedIndices();
				Vector<String> str_v = new Vector<String>();
				for (int i : indices)
				{
					String s[] = model.getAll(i);
					str_v.add(s[0]);
				}
				for (String str : str_v)
				{
					model.removeSite(str);
				}
				model.save();
			}
		});
		del_btn.setPreferredSize(new Dimension(120, 20));

		final JButton connect_btn = new JButton(StringTable.buttonConnect);
		connect_btn.setPreferredSize(new Dimension(120, 20));
		connect_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String user_str = user_jtf.getText();
				String pass_str = new String(pass_jpf.getPassword());
				String server_str = address_jtf.getText();
				String port_str = port_jtf.getText();
				ServerManagerDialog.this.setVisible(false);
				owner.startFTP(user_str, pass_str, server_str, port_str);
			}
		});

		final JButton update_btn = new JButton(StringTable.buttonRefresh);
		update_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int i = sites_list.getSelectedIndex();
				if (i >= 0)
				{
					String[] s = model.getAll(i);
					s[0] = siteName_jtf.getText();
					s[1] = address_jtf.getText();
					s[2] = port_jtf.getText();
					s[3] = user_jtf.getText();
					s[4] = new String(pass_jpf.getPassword());
					model.save();
				}
			}
		});
		update_btn.setPreferredSize(new Dimension(120, 20));
		southPanel.setLayout(new FlowLayout());
		southPanel.add(add_btn);
		southPanel.add(del_btn);
		southPanel.add(connect_btn);
		southPanel.add(update_btn);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		this.add(mainPanel, BorderLayout.CENTER);

		sites_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				int[] selected = sites_list.getSelectedIndices();
				if (selected.length == 1)
				{
					String[] s = ((SiteListModel)sites_list.getModel()).getAll(selected[0]);
					siteName_jtf.setEditable(true);
					address_jtf.setEditable(true);
					port_jtf.setEditable(true);
					user_jtf.setEditable(true);
					pass_jpf.setEditable(true);
					siteName_jtf.setText(s[0]);
					address_jtf.setText(s[1]);
					port_jtf.setText(s[2]);
					user_jtf.setText(s[3]);
					pass_jpf.setText(s[4]);
					update_btn.setEnabled(true);
					connect_btn.setEnabled(true);
				}
				else
				{
					siteName_jtf.setEditable(false);
					address_jtf.setEditable(false);
					port_jtf.setEditable(false);
					user_jtf.setEditable(false);
					pass_jpf.setEditable(false);
					siteName_jtf.setText("");
					address_jtf.setText("");
					port_jtf.setText("");
					user_jtf.setText("");
					pass_jpf.setText("");
					update_btn.setEnabled(false);
					connect_btn.setEnabled(false);
				}
			}
		});

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(d.width/2 - this.getWidth()/2, d.height/2 - this.getWidth()/2);

	}



}
