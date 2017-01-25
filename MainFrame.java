/* ***********************
** MainFrame.java
** ***********************
** 主界面
** Build 0718
** 07-13 修正了一个问题, 使得断开连接后再退出不再发生错误
** 07-14 增加了Toolbar, 并认定了默认的UI字体, 不再对单个组件进行字体设置
** 07-15 增加了最小化到系统托盘以及托盘菜单
** **********************/
package ArkFTP.bin.ui;

import ArkFTP.bin.main.ArkFTPWorker;

import ArkFTP.bin.model.LocalComboBoxModel;
import ArkFTP.bin.model.LocalTableModel;
import ArkFTP.bin.model.QueueTableModel;
import ArkFTP.bin.model.ServerTableModel;
import ArkFTP.bin.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.plaf.FontUIResource;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

public class MainFrame extends JFrame
{
	final int HEIGHT;
	final int WIDTH;
	
	public Font MainFrameFont = null;
	public Color[] UsedColor = { new Color(191, 219,255),
												new Color(222, 236, 255),
												new Color(255, 181, 74) };
	public String VersionInfoStr= "ArkFTP - V1.0";
	// 设定通用字体, 颜色, 版本信息
	
	private ArkFTPWorker jw;
	private TaskQueue tq;
	
	private JTextArea printer_ta;
	private JLabel state_lb;
	private JProgressBar jpb;
	private JMenuBar menubar;
	
	private JComboBox server_jcb;
	private JComboBox local_jcb;

	private ServerTableModel server_table_model;
	private LocalTableModel local_table_model;
	private LocalComboBoxModel local_comboBox_model;
	private QueueTableModel queue_table_model;
	private JTable local_table;
	private JTable server_table;
	private JTable queue_table;
	private JFileChooser jfc;
	
	private AboutDialog about_dlg;
	private ServerManagerDialog manager_dlg;
	private ConnectToDialog connect_dlg;
	private ConnectLogDialog connectlog_dlg;
	
	private void InitTopPanel()
	{
		// 这里是顶端菜单与工具栏
		
		JPanel login2Menu_pnl = new JPanel();
		
		JPanel QuickToolBar = new JPanel();
		QuickToolBar.setBackground(UsedColor[0]);
		
		QuickToolBar.setLayout(new BoxLayout(QuickToolBar, BoxLayout.X_AXIS));
		
		/*
			07-14 新功能 Toolbar
		*/
		
		JToolBar TaskBar = new JToolBar();
		TaskBar.setBackground(UsedColor[0]);
		TaskBar.setMargin(new Insets(3, 3, 3, 3));
		TaskBar.setBorderPainted(false);
		TaskBar.setFloatable(false);
		
		JButton[] TaskBarButton = { new JButton("连接到...", new ImageIcon("ArkFTP/res/ConnectTo.png")),
													new JButton("断开连接", new ImageIcon("ArkFTP/res/Disconnect.png")),
													new JButton("站点管理器", new ImageIcon("ArkFTP/res/ServerManager.png")),
													new JButton("连接日志", new ImageIcon("ArkFTP/res/ConnectLog.png")),
													new JButton("后台工作", new ImageIcon("ArkFTP/res/SystemTray.png")) };
		for (int it = 0; it < TaskBarButton.length; it++)
		{
			TaskBarButton[it].setBackground(UsedColor[0]);
			TaskBarButton[it].setBorderPainted(false);
			TaskBarButton[it].setFocusPainted(false);
			TaskBarButton[it].addMouseListener(new MouseAdapter()
			{
				public void mouseEntered(MouseEvent e)
				{
						ToolBarFocusGained((JButton)e.getSource());
				}
				public void mouseExited(MouseEvent e)
				{
						ToolBarFocusLost((JButton)e.getSource());
				}
			});
		}
		 
		TaskBarButton[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.connect_dlg.setVisible(true);
			}
		});
			
		TaskBarButton[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (jw != null)
				{
					jw.close();
					jw = null;
				}
			}
		});
		
		TaskBarButton[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.manager_dlg.setVisible(true);
			}
		});
		
		TaskBarButton[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.connectlog_dlg.UpdateInfo(MainFrame.this);
				MainFrame.this.connectlog_dlg.setVisible(true);				
			}
		});
		
		TaskBarButton[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.setVisible(false);
			}
		});
		
		TaskBar.addSeparator(new Dimension(50,64)); 
		TaskBar.add(TaskBarButton[0]);
		TaskBar.add(TaskBarButton[1]);
		TaskBar.addSeparator(new Dimension(50,64)); 
		TaskBar.add(TaskBarButton[2]);
		TaskBar.add(TaskBarButton[3]); 
		TaskBar.addSeparator(new Dimension(50,64)); 
		TaskBar.add(TaskBarButton[4]);
		  
		QuickToolBar.add(TaskBar);
		
		// 这里开始添加下拉菜单 待改写
		menubar = new JMenuBar();
		menubar.setBackground(UsedColor[0]);
		
		char[][] menuchar = { {'F', 'T', 'H'}, {'C', 'D', 'X'}, {'S', 'L'}, {'A'} };
		
		JMenu menu1 = new JMenu("功能(F)");
		
		menu1.setMnemonic(menuchar[0][0]);
		JMenuItem[] menuItem1 = { new JMenuItem("连接到(C)...", new ImageIcon("ArkFTP/res/MenuConnectTo.png")),
													new JMenuItem("断开连接(D)", new ImageIcon("ArkFTP/res/MenuDisconnect.png")),
													new JMenuItem("最小化至系统托盘(M)", new ImageIcon("ArkFTP/res/MenuSystemTray.png")),
													new JMenuItem("退出(X)", new ImageIcon("ArkFTP/res/MenuExit.png")) };
		for (int it = 0; it < menuchar[1].length; it++)
			menuItem1[it].setMnemonic(menuchar[1][it]);
		menuItem1[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.connect_dlg.setVisible(true);
			}
		});	
		menuItem1[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (jw != null)
				{
					jw.close();
					jw = null; // 07-13 修正添加 解决断开连接后退出程序的错误BUG
				}
			}
		});
		menuItem1[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.setVisible(false);
			}
		});
		menuItem1[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int ExitOrNot = JOptionPane.showConfirmDialog(
						MainFrame.this, "确实要退出吗？", VersionInfoStr, JOptionPane.YES_NO_OPTION);
				if (ExitOrNot == JOptionPane.YES_OPTION) {
					if (jw != null) {
						jw.close();
						try
						{
							jw.join();
						}
						catch (InterruptedException ie)
						{
							ie.printStackTrace();
						}
					}
					System.exit(0);
				}
			}
		});
		menu1.setPreferredSize(new Dimension(60, 20));
		menu1.add(menuItem1[0]);
		menu1.add(menuItem1[1]);
		menu1.addSeparator();
		menu1.add(menuItem1[2]);
		menu1.add(menuItem1[3]);
		
		JMenu menu2 = new JMenu("工具(T)");
		menu2.setMnemonic(menuchar[0][1]);
		menu2.setPreferredSize(new Dimension(60, 20));
		JMenuItem[] menuItem2 = { new JMenuItem("站点管理器(S)", new ImageIcon("ArkFTP/res/MenuServerManager.png")),
													new JMenuItem("连接日志(L)", new ImageIcon("ArkFTP/res/MenuConnectLog.png"))};
		for (int it = 0; it < menuchar[2].length; it++)
			menuItem2[it].setMnemonic(menuchar[2][it]);
		menuItem2[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.manager_dlg.setVisible(true);
			}
		});
		menuItem2[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.connectlog_dlg.UpdateInfo(MainFrame.this);
				MainFrame.this.connectlog_dlg.setVisible(true);			
			}
		});
		
		menu2.add(menuItem2[0]);
		menu2.addSeparator();
		menu2.add(menuItem2[1]);

		JMenu menu3 = new JMenu("帮助(H)");
		menu3.setMnemonic(menuchar[0][2]);
		menu3.setPreferredSize(new Dimension(60, 20));
		JMenuItem[] menuItem3 = { new JMenuItem("关于(A)...", new ImageIcon("ArkFTP/res/MenuAbout.png")) };
		
		for (int it = 0; it < menuchar[3].length; it++)
			menuItem3[it].setMnemonic(menuchar[3][it]);
		menuItem3[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				MainFrame.this.about_dlg.StartAnimation();
				MainFrame.this.about_dlg.setVisible(true);
			}
			
		});
		menu3.add(menuItem3[0]);
		
		menubar.add(menu1);
		menubar.add(menu2);
		menubar.add(menu3);
		this.add(menubar);		
		
		login2Menu_pnl.setLayout(new BorderLayout());
		login2Menu_pnl.add(QuickToolBar, BorderLayout.SOUTH);
		login2Menu_pnl.add(menubar, BorderLayout.NORTH);
		this.add(login2Menu_pnl, BorderLayout.NORTH);
	}
	
	private void InitStatePanel() {
		// 这里是底端状态栏
		JPanel state_pnl = new JPanel();
		state_pnl.setBackground(UsedColor[0]);
		
		JPanel left_pnl = new JPanel();
		left_pnl.setBackground(UsedColor[0]);
		left_pnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		state_lb = new JLabel("(没有连接)");
		
		left_pnl.setPreferredSize(new Dimension(800, 22));
		left_pnl.add(state_lb, BorderLayout.WEST);
		left_pnl.add(new JSeparator(JSeparator.VERTICAL));
		
		JPanel right_pnl = new JPanel();
		right_pnl.setBackground(UsedColor[0]);
		right_pnl.setLayout(new FlowLayout(FlowLayout.LEFT));
		jpb = new JProgressBar();
		jpb.setBackground(Color.YELLOW);
		jpb.setForeground(Color.GREEN);
		jpb.setPreferredSize(new Dimension(150, 18));
		right_pnl.add(jpb);
		right_pnl.setPreferredSize(new Dimension(180, 20));
		
		state_pnl.setLayout(new FlowLayout());		
		state_pnl.add(left_pnl);		
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		state_pnl.add(separator);
		state_pnl.add(right_pnl);
		jpb.setVisible(false);
		this.add(state_pnl, BorderLayout.SOUTH);
	}
	
	private void InitViews() {			
		/**
		 * 这里开始->服务器文件视图
		 * */ 
		final JScrollPane server_scroll;
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		server_jcb = new JComboBox();
		
		server_jcb.setBackground(UsedColor[1]);
		server_jcb.setForeground(Color.BLACK);
		
		server_jcb.setPreferredSize(new Dimension(0, 20));
		server_table_model = new ServerTableModel();
		server_table = new JTable(this.server_table_model);
				
		server_table.setRowHeight(24);
		server_table.setFillsViewportHeight(true);
		server_table.setBackground(Color.WHITE);
		server_table.setSelectionBackground(UsedColor[2]);
		server_table.setShowGrid(true);
		server_table.setGridColor(Color.white);
		server_table.setDragEnabled(false);
		
		// 设置Icon列的长度。
		TableColumn iconColumn = server_table.getColumn("");
		iconColumn.setMaxWidth(server_table.getRowHeight());
		iconColumn.setMinWidth(server_table.getRowHeight());

		server_table.setDefaultRenderer(new String().getClass(), new AlignmentRenderer());
		server_table.setShowGrid(true);
		server_scroll = new JScrollPane(server_table);
		JPanel panel_scroll = new JPanel();
		panel_scroll.add(server_scroll);
		panel1.add(server_jcb, BorderLayout.NORTH);
		panel1.add(server_scroll, BorderLayout.CENTER);
		server_jcb.setEnabled(false);
	
		/**
		 * 这里开始->本机文件视图
		 * */ 
		final JScrollPane local_scroll;
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.setBackground(Color.WHITE);
		local_table_model = new LocalTableModel();
		
		// ComboBox中存的是系统的各个Root下访问的当前目录.
		File [] files = File.listRoots();
		File currentDir_file = new File(System.getProperty("user.dir"));
		String currentDir_str = currentDir_file.getAbsolutePath();
		Vector<String> v = new Vector<String>();
		if (currentDir_str.charAt(currentDir_str.length()-1) != File.separatorChar)
			currentDir_str += File.separatorChar;
		for (File f : files)
		{ 
			String rootDir_str = f.getAbsolutePath();
			if (rootDir_str.equals(currentDir_str.substring(0, rootDir_str.length())))
				v.addElement(currentDir_str);
			else
				v.addElement(f.getAbsolutePath());
		}
		this.local_comboBox_model = new LocalComboBoxModel(v);
		local_jcb = new JComboBox(local_comboBox_model);
		int i = this.local_comboBox_model.getIndexOf(currentDir_str);
		local_jcb.setSelectedIndex(i);
	
		local_jcb.setForeground(Color.BLACK);
		local_jcb.setBackground(UsedColor[1]);
		
		local_jcb.setPreferredSize(new Dimension(0, 20));
		local_table_model.addAllChildren(currentDir_file);
		local_table = new JTable(this.local_table_model);
		local_table.setDefaultRenderer(new String().getClass(), new AlignmentRenderer());
		
		local_table.setRowHeight(24);
		local_table.setFillsViewportHeight(true);
		local_table.setBackground(Color.WHITE);
		local_table.setSelectionBackground(UsedColor[2]);
		local_table.setShowGrid(true);
		local_table.setGridColor(Color.white);
		local_table.setDragEnabled(false);
		
		// 设置Icon列的长度。
		iconColumn = local_table.getColumn("");
		iconColumn.setMaxWidth(local_table.getRowHeight());
		iconColumn.setMinWidth(local_table.getRowHeight());
		
		local_scroll = new JScrollPane(local_table);
		local_scroll.setBackground(Color.WHITE);
		panel_scroll = new JPanel();
		panel_scroll.setBackground(Color.WHITE);
		panel_scroll.add(local_scroll);
		panel2.add(local_jcb, BorderLayout.NORTH);
		panel2.add(local_scroll, BorderLayout.CENTER);
				
		JSplitPane top_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, panel2, panel1);
		top_split.setResizeWeight(0.5);
		top_split.setDividerSize(4); 
		
		/**
		 * 这里开始->传输队列视图
		 * */ 
		JScrollPane queue_scroll;
		queue_table_model = new QueueTableModel();
		queue_table = new JTable(queue_table_model);
		
		queue_table.setRowHeight(24);
		queue_table.setFillsViewportHeight(true);
		queue_table.setBackground(Color.WHITE);
		queue_table.setSelectionBackground(UsedColor[2]);
		queue_table.setShowGrid(true);
		queue_table.setGridColor(Color.white);
		queue_table.setDragEnabled(false);
		
		queue_scroll = new JScrollPane(queue_table);
		queue_scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "传输队列"));
		queue_scroll.setBackground(UsedColor[0]);
		/**
		 * 这里开始->用户和服务器交互消息视图
		 * */ 
		printer_ta = new JTextArea(0, 40);
		printer_ta.setEditable(false);
		printer_ta.setBackground(UsedColor[0]);
		JScrollPane printer_scroll = new JScrollPane(printer_ta);
		
		
		printer_scroll.setBackground(UsedColor[0]);
	
		printer_scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "通迅记录"));
		JSplitPane bottom_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, queue_scroll, printer_scroll);
		bottom_split.setDividerSize(0);
		bottom_split.setResizeWeight(0.5);

		JSplitPane total_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, top_split, bottom_split);
		total_split.setDividerSize(2);
		total_split.setResizeWeight(0.8);
		add(total_split);
		
		/**
		 * 	server_table鼠标右击产生的弹出菜单。
		 */
		final JPopupMenu server_popup = new JPopupMenu();
		char[] item1C = { 'O', 'S', 'D', 'R', 'N' , 'E' };
		final JMenuItem[] item1 = {	new JMenuItem("下载 (O)", new ImageIcon("ArkFTP/res/MenuDownload.png")),
													new JMenuItem("目标另存为... (S)", new ImageIcon("ArkFTP/res/MenuSaveas.png")),
													new JMenuItem("删除 (D)", new ImageIcon("ArkFTP/res/MenuDelete.png")),
													new JMenuItem("重命名 (R)", new ImageIcon("ArkFTP/res/MenuRename.png")),
													new JMenuItem("新建文件夹 (N)", new ImageIcon("ArkFTP/res/MenuNewfolder.png")),
													new JMenuItem("刷新 (E)", new ImageIcon("ArkFTP/res/MenuRefresh.png")) };
		for (int it = 0; it < item1.length; it++)
		{
			item1[it].setPreferredSize(new Dimension(150, 38));
			item1[it].setMnemonic(item1C[it]);
		}
		item1[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				int[] selected_array = server_table.getSelectedRows();
				for (int n : selected_array)
				{
					if (n > 0 && (server_table_model.isDirRow(n) == false))
					{
						String[] queueRow = {	jw.getCurrentDir() + (String)server_table_model.getValueAt(n, server_table_model.NAME_COL),
											(String)server_table_model.getValueAt(n, server_table_model.SIZE_COL),
											(String)local_jcb.getSelectedItem(),
											"RETR",
										};
						queue_table_model.insertRow(0, queueRow);
						queue_table.setRowSelectionInterval(0, 0);
						String[] task1 = {	"RETR", 
							jw.getCurrentDir() + (String)server_table_model.getValueAt(n, server_table_model.NAME_COL),
							(String)local_jcb.getSelectedItem(),
							(String)server_table_model.getValueAt(n, server_table_model.SIZE_COL),
						};
						String[] task2 = {"DELE_ENTRY", "0"};
						String[] task3 = {"REFRESH_LOCAL"};
						tq.putTask(task1);
						tq.putTask(task2);
						tq.putTask(task3);
					}
				}
			}
		});
		server_popup.add(item1[0]);
				
		item1[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				jfc.setDialogTitle("选择保存目录..");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int state = jfc.showOpenDialog(null);
				File file = jfc.getSelectedFile();
				
				if (file != null && file.isDirectory() && state == JFileChooser.APPROVE_OPTION) {
					int selected[] = server_table.getSelectedRows();
					for (int i = 0; i < selected.length; i++)
					{
						String fileName_str = jw.getCurrentDir() + (String)server_table_model.getValueAt(selected[i], server_table_model.NAME_COL);
						if (server_table_model.isDirRow(selected[i]))
							fileName_str += '/';
							
						String[] str_array = {	fileName_str, 
											  	(String)server_table_model.getValueAt(selected[i], server_table_model.SIZE_COL), 
											  	file.getAbsolutePath() + File.separatorChar,
											  	"RETR"
											 };
						queue_table_model.addRow(str_array);
					}
					server_table.clearSelection();
				}
			}			
		});
		server_popup.add(item1[1]);
		server_popup.addSeparator();
		item1[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int selected[] = server_table.getSelectedRows();
				for (int i = 0; i < selected.length; i++)
				{
					int DeleteOrNot = JOptionPane.showConfirmDialog(
						MainFrame.this, "确实要删除选中的文件与目录吗？", "删除操作确认", JOptionPane.YES_NO_OPTION);
					if (DeleteOrNot == JOptionPane.YES_OPTION)
					{
						String fileName_str = jw.getCurrentDir() + (String)server_table_model.getValueAt(selected[i], server_table_model.NAME_COL);					
						if (server_table_model.isDirRow(selected[i]))
						{
							String[] task1 = {"RRMD", fileName_str + '/'};
							tq.putTask(task1);
							String[] task2 = {"LIST", jw.getCurrentDir()};
							tq.putTask(task2);
						}
						else
						{						
							String[] task1 = {"DELE", fileName_str};
							tq.putTask(task1);
							String[] task2 = {"LIST", jw.getCurrentDir()};
							tq.putTask(task2);
						}
					}					
				}
			}
		});
		server_popup.add(item1[2]);
		item1[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int row = server_table.getSelectedRow();
				String oldName_str = (String)server_table_model.getValueAt(row, server_table_model.NAME_COL);
				String[] dir_value = new String[1];
				JDialog putin_dlg = new RenameDialog(MainFrame.this, dir_value);
				putin_dlg.setVisible(true);
				if (dir_value[0] != null)
				{
					String[] task = {"RENAME", oldName_str, dir_value[0]};
					tq.putTask(task);
				}
			}
		});
		server_popup.add(item1[3]);
		item1[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String[] dir_value = new String[1];
				JDialog putin_dlg = new PutInDialog(MainFrame.this, dir_value);
				putin_dlg.setVisible(true);
				if (dir_value[0] != null)
				{
					String[] task = {"MKD", dir_value[0]};
					tq.putTask(task);
				}
			}
		});
		server_popup.add(item1[4]);
		server_popup.addSeparator();
		item1[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String[] task = {"LIST", jw.getCurrentDir()};
				tq.putTask(task);
			}
		});
		server_popup.add(item1[5]);	
		
		/**
		 * local_table鼠标右击产生的弹出菜单。
		 */
		final JPopupMenu local_popup = new JPopupMenu();
		char[] item2C = { 'U', 'R', 'N', 'D', 'E' };
		final JMenuItem[] item2 = {	new JMenuItem("上传 (U)", new ImageIcon("ArkFTP/res/MenuUpload.png")),
													new JMenuItem("重命名 (R)", new ImageIcon("ArkFTP/res/MenuRename.png")),
													new JMenuItem("新建文件夹 (N)", new ImageIcon("ArkFTP/res/MenuNewfolder.png")),
													new JMenuItem("删除 (D)", new ImageIcon("ArkFTP/res/MenuDelete.png")),
													new JMenuItem("刷新 (E)", new ImageIcon("ArkFTP/res/MenuRefresh.png")) };
		for (int it = 0; it < item2.length; it++)
		{
			item2[it].setPreferredSize(new Dimension(150, 38));
			item2[it].setMnemonic(item2C[it]);
		}
		item2[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (jw != null && jw.isAlive())
				{
					int[] selected_array = local_table.getSelectedRows();
					for (int index : selected_array)
					{
						String fileName_str = (String)local_table_model.getValueAt(index, local_table_model.NAME_COL);
						File file = new File((String)local_jcb.getItemAt(local_jcb.getSelectedIndex()) + fileName_str);
						if (file.isDirectory())
							fileName_str = file.getName() + File.separatorChar;
						String[] str_array = {	(String)local_jcb.getItemAt(local_jcb.getSelectedIndex()) + fileName_str, 
				  								(String)local_table_model.getValueAt(index, local_table_model.SIZE_COL), 
				  								jw.getCurrentDir(),
				  								"STOR"
				  							 };
						queue_table_model.addRow(str_array);
					}
				}
			}			
		});
		local_popup.add(item2[0]);
		local_popup.addSeparator();
		
		item2[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int row = local_table.getSelectedRow();
				String currentDir_str = (String)local_jcb.getSelectedItem();
				String oldName_str = (String)local_table_model.getValueAt(row, server_table_model.NAME_COL);
				File file = new File(currentDir_str + oldName_str);
				String[] dir_value = new String[1];
				JDialog putin_dlg = new RenameDialog(MainFrame.this, dir_value);
				putin_dlg.setVisible(true);
				if (dir_value[0] != null)
				{
					file.renameTo(new File(currentDir_str, dir_value[0]));
				}
				local_table_model.addAllChildren(new File(currentDir_str));
			}
		});
		local_popup.add(item2[1]);
		
		item2[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String currentDir_str = (String)local_jcb.getSelectedItem();
				String[] dir_value = new String[1];
				JDialog putin_dlg = new PutInDialog(MainFrame.this, dir_value);
				putin_dlg.setVisible(true);
				if (dir_value[0] != null)
				{
					File dir_file = new File(currentDir_str + dir_value[0]);
					dir_file.mkdir();
				}
				local_table_model.addAllChildren(new File(currentDir_str));
			}
		});
		local_popup.add(item2[2]);
		
		item2[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int DeleteOrNot = JOptionPane.showConfirmDialog(
						MainFrame.this, "确实要删除选中的文件与目录吗？", "删除操作确认", JOptionPane.YES_NO_OPTION);
				if (DeleteOrNot == JOptionPane.YES_OPTION)
				{
					int row = local_table.getSelectedRow();
					String currentDir_str = (String)local_jcb.getSelectedItem();
					String oldName_str = (String)local_table_model.getValueAt(row, server_table_model.NAME_COL);
					File file = new File(currentDir_str + oldName_str);
					if (file.delete())
						local_table_model.addAllChildren(new File(currentDir_str));
				}
			}
		});
		local_popup.add(item2[3]);
		local_popup.addSeparator();
				
		item2[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				local_table_model.addAllChildren(new File((String)local_jcb.getSelectedItem()));
			}
		});
		local_popup.add(item2[4]);
		
		
		/**
		 *  queue_table 鼠标右击产生的弹出菜单。
		 */
		final JPopupMenu queue_popup = new JPopupMenu();
		char[] item3C = { 'A', 'S', 'D', 'E', 'C' };
		final JMenuItem[] item3 = { new JMenuItem("传输全部 (A)"),
													new JMenuItem("传输所选 (S)"),
													new JMenuItem("删除全部 (D)"),
													new JMenuItem("删除所选 (E)"),
													new JMenuItem("放弃传输 (C)") };
		for (int it = 0; it < item3.length; it++)
		{
			item3[it].setPreferredSize(new Dimension(120, 32));
			item3[it].setMnemonic(item3C[it]);
		}
		
		queue_popup.add(item3[0]);
		queue_popup.add(item3[1]);
		queue_popup.addSeparator();
		queue_popup.add(item3[2]);
		queue_popup.add(item3[3]);
		queue_popup.addSeparator();
		queue_popup.add(item3[4]);
		item3[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				queue_table.selectAll();
				int[] indexs = queue_table.getSelectedRows();
				for (int i = 0; i < indexs.length; i++)
				{
					String type_str = (String)queue_table_model.getValueAt(indexs[i], queue_table_model.TYPE_COL);
					if (type_str.equals("RETR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL),
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
					else if (type_str.equals("STOR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL)
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
				}
			}	
		});
		item3[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int[] indexs = queue_table.getSelectedRows();
				for (int i = 0; i < indexs.length; i++)
				{
					String type_str = (String)queue_table_model.getValueAt(indexs[i], queue_table_model.TYPE_COL);
					if (type_str.equals("RETR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL),
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
					else if (type_str.equals("STOR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL)
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
				}
			}	
		});
		item3[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				queue_table_model.removeAllRows();
			}			
		});
		item3[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				while(queue_table.getSelectedRowCount() != 0)
				{
					int index = queue_table.getSelectedRow();
					queue_table_model.removeRow(index);
				}
			}	
		});
		item3[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				synchronized (tq)
				{
					tq.clear();
				}
				if (jw != null) jw.doAbort(); // 07-15 加入判断, 解决无连接此功能出现异常的问题
			}
		});
 		
		/**
		 * 这里开始->添加监听器
		 * */ 
		local_jcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String AbstractName_str = (String)local_jcb.getSelectedItem();
				if (AbstractName_str != null)
				{
					File dir_file = new File(AbstractName_str);
					local_table_model.addAllChildren(dir_file);
					local_table.requestFocus();
				}
			}
			
		});
		server_table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event)
			{
				Point p = event.getPoint();
				int n = server_table.rowAtPoint(p);
				if (event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1)
				{
					if (n == 0)
					{
						String[] task = {"LIST", ".."};
						tq.putTask(task);
					}
					else if (n > 0 && server_table_model.isDirRow(n))
					{
						String[] task = {"LIST", (String)server_table_model.getValueAt(n, server_table_model.NAME_COL) + "/"};
						tq.putTask(task);
					}
					else if (n > 0)
					{
						String[] queueRow = {	jw.getCurrentDir() + (String)server_table_model.getValueAt(n, server_table_model.NAME_COL),
												(String)server_table_model.getValueAt(n, server_table_model.SIZE_COL),
												(String)local_jcb.getSelectedItem(),
												"RETR",
											};
						queue_table_model.insertRow(0, queueRow);
						queue_table.setRowSelectionInterval(0, 0);
						
						String[] task1 = {	"RETR", 
											jw.getCurrentDir() + (String)server_table_model.getValueAt(n, server_table_model.NAME_COL),
											(String)local_jcb.getSelectedItem(),
											(String)server_table_model.getValueAt(n, server_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", "0"};
						String[] task3 = {"REFRESH_LOCAL"};
						tq.putTask(task1);
						tq.putTask(task2);
						tq.putTask(task3);
					}
				}
				
				if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON3)
				{
					item1[0].setEnabled(true);
					item1[1].setEnabled(true);
					item1[2].setEnabled(true);
					item1[3].setEnabled(true);
					if (n >= 0)
					{
						if (server_table.getSelectedRowCount() <= 1)
						{
							server_table.clearSelection();
							server_table.addRowSelectionInterval(n, n);
							if (server_table.isRowSelected(0))
							{
								item1[0].setEnabled(false);
								item1[1].setEnabled(false);
								item1[2].setEnabled(false);
							}
							server_table.addRowSelectionInterval(n, n);
						}
						else
						{
							// 如果选中多项的话，所做的操作与".."无关。
							item1[2].setEnabled(false);
							item1[3].setEnabled(false);
							server_table.addRowSelectionInterval(n, n);
							if (server_table.isRowSelected(0))
							{
								server_table.removeRowSelectionInterval(0, 0);
							}
						}
						if (server_table.isEnabled())
							server_popup.show(server_table, p.x, p.y);
					}
				}
			}
		});
		local_table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event)
			{ 
				Point p = event.getPoint();
				int n = local_table.rowAtPoint(p);
				if (event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1)
				{
					if (n == 0)
					{
						int index = local_jcb.getSelectedIndex();
						String oldDir_str = (String)local_comboBox_model.getElementAt(index);
						File oldDir_file = new File(oldDir_str);
						if (oldDir_file.getParent() != null)
						{
							String newDir_str = oldDir_file.getParent();
							if (newDir_str.charAt(newDir_str.length()-1) != File.separatorChar)
								newDir_str += File.separatorChar;
							local_table_model.addAllChildren(oldDir_file.getParentFile());
							local_comboBox_model.replace(index, newDir_str);
							local_jcb.setSelectedIndex(index);
						}
					}
					else if (n > 0)
					{
						int index = local_jcb.getSelectedIndex();
						String oldDir_str = (String)local_comboBox_model.getElementAt(index);
						String newDir_str = oldDir_str + (String)local_table_model.getValueAt(n, local_table_model.NAME_COL);
						File f = new File(newDir_str);
						if (f != null && f.isDirectory())
						{
							local_table_model.addAllChildren(f);
							local_comboBox_model.replace(index, newDir_str + File.separatorChar);
							local_jcb.setSelectedIndex(index);
						}
						else if (f != null && !f.isDirectory() && server_table.isEnabled())
						{
							String[] queueRow = { f.getAbsolutePath(),
														Long.toString(f.length(), 10),
														jw.getCurrentDir(),
														"STOR",
													  };
							queue_table_model.insertRow(0, queueRow);
							queue_table.setRowSelectionInterval(0, 0);
							String[] task1 = {	"STOR", 
											 	(String)local_jcb.getItemAt(local_jcb.getSelectedIndex()) + (String)local_table_model.getValueAt(n, local_table_model.NAME_COL), 
											 	jw.getCurrentDir(),
											};
							String[] task2 = {"DELE_ENTRY", "0"};
							String[] task3 = {"LIST", jw.getCurrentDir()};
							tq.putTask(task1);
							tq.putTask(task2);
							tq.putTask(task3);
						}
					}
				}
				else if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON3)
				{
					local_popup.show(local_table, p.x, p.y);
					item2[0].setEnabled(true);
					item2[1].setEnabled(true);
					item2[2].setEnabled(true);
					item2[3].setEnabled(true);
					if (n >= 0)
					{
						if (local_table.getSelectedRowCount() <= 1)
						{
							local_table.clearSelection();
							local_table.addRowSelectionInterval(n, n);
							if (local_table.isRowSelected(0))
							{
								item2[0].setEnabled(false);
								item2[1].setEnabled(false);
								item2[3].setEnabled(false);
							}							
							local_popup.show(local_table, p.x, p.y);
						}
						else
						{
							// 如果选中多项的话，所做的操作与".."无关。
							item2[1].setEnabled(false);
							item2[2].setEnabled(false);
							item2[3].setEnabled(false);
							local_table.addRowSelectionInterval(n, n);
							if (local_table.isRowSelected(0))
							{
								local_table.removeRowSelectionInterval(0, 0);
							}
						}
						if (!server_table.isEnabled())
							item2[0].setEnabled(false);							
						local_popup.show(local_table, p.x, p.y);
					}
				}
			}
		});
		queue_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event)
			{
				Point p = event.getPoint();
				int n = queue_table.rowAtPoint(p);
				if (event.getClickCount() >= 2 && event.getButton() == MouseEvent.BUTTON1)
				{
					String type_str = (String)queue_table_model.getValueAt(n, queue_table_model.TYPE_COL);
					if (type_str.equals("RETR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(n, queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(n, queue_table_model.TARGET_COL),
											(String)queue_table_model.getValueAt(n, queue_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", new Integer(n).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
					else if (type_str.equals("STOR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(n, queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(n, queue_table_model.TARGET_COL),
											(String)queue_table_model.getValueAt(n, queue_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", new Integer(n).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
					queue_table.setSelectionBackground(Color.GREEN);
				} 
				if (event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON3)
				{
					item3[0].setEnabled(true);
					item3[1].setEnabled(true);
					item3[2].setEnabled(true);
					item3[3].setEnabled(true);
					item3[4].setEnabled(true);
					if (!queue_table.isEnabled())
					{
						item3[0].setEnabled(false);
						item3[1].setEnabled(false);
						item3[2].setEnabled(false);
						item3[3].setEnabled(false);
					}
					queue_popup.show(queue_table, p.x, p.y);
				}
			} 
		});
	}
	
	
	public MainFrame()
	{
		boolean LoadFont = true;
		try
		{
			MainFrameFont = Font.createFont(0, new File("ArkFTP/res/YuanTi.ttf"));
			MainFrameFont = MainFrameFont.deriveFont(12.0F);
		}
		catch (Exception e)
		{
			LoadFont = false;
			JOptionPane.showMessageDialog(MainFrame.this, "载入字体库时发生错误，界面显示可能不正常！");
		}
		if (LoadFont == true)
		{
			// 如果载入字体成功，对全局字体进行设置
			FontUIResource fontRes = new FontUIResource(MainFrameFont);
			for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();)
			{
	        	Object key = keys.nextElement();
	        	Object value = UIManager.get(key);
	        	if (value instanceof FontUIResource)
	            	UIManager.put(key, fontRes);
	    	}
	    }

		UIManager.put("TableHeader.background", UsedColor[0]);
		UIManager.put("Panel.background", UsedColor[0]);

		Toolkit kit = Toolkit.getDefaultToolkit();
		Cursor ArkCursor = kit.createCustomCursor(new ImageIcon("ArkFTP/res/Arrow.png").getImage(), new Point(0, 0), "Arrow");
  		this.setCursor(ArkCursor);
		Dimension screenSize = kit.getScreenSize();
		WIDTH = screenSize.width;
		HEIGHT = screenSize.height;
		this.setSize(WIDTH, HEIGHT);
		this.setLayout(new BorderLayout());
		this.setTitle(VersionInfoStr);
		
		this.InitTopPanel();
		this.InitViews();
		this.InitStatePanel();
		this.InitTrayFunction();
		jfc = new JFileChooser();
		tq = new TaskQueue();

		this.about_dlg = new AboutDialog(this);
		this.manager_dlg = new ServerManagerDialog(this);
		this.connect_dlg = new ConnectToDialog(this);
		this.connectlog_dlg = new ConnectLogDialog(this);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				int ExitOrNot = JOptionPane.showConfirmDialog(
						MainFrame.this, "确实要退出吗？", VersionInfoStr, JOptionPane.YES_NO_OPTION);
				if (ExitOrNot == JOptionPane.YES_OPTION) {
					if (jw != null) {
						jw.close();
						try
						{
							jw.join();
						}
						catch (InterruptedException ie)
						{
							ie.printStackTrace();
						}
					}
					System.exit(0);
				}
			}
		});
	}	
	
	public void ToolBarFocusGained(JButton ToolButton)
	{
		ToolButton.setBackground(UsedColor[2]);	
	}
	public void ToolBarFocusLost(JButton ToolButton)
	{
		ToolButton.setBackground(UsedColor[0]);	
	}
	public void InitTrayFunction()
    {
    	PopupMenu TrayMenu = new PopupMenu();
    	MenuItem[] TrayItem = { new MenuItem("显示 / 隐藏"),
    											new MenuItem("传输全部"),
    											new MenuItem("取消传输"),
    											new MenuItem("断开连接"),
    											new MenuItem("退出") };
    	TrayItem[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
            	if (MainFrame.this.isVisible())
  					MainFrame.this.setVisible(false);
				else
			   		MainFrame.this.setVisible(true);
			}
		});
		TrayItem[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				queue_table.selectAll();
				int[] indexs = queue_table.getSelectedRows();
				for (int i = 0; i < indexs.length; i++)
				{
					String type_str = (String)queue_table_model.getValueAt(indexs[i], queue_table_model.TYPE_COL);
					if (type_str.equals("RETR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL),
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.SIZE_COL),
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
					else if (type_str.equals("STOR"))
					{
						String[] task1 = {	type_str, 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.NAME_COL), 
											(String)queue_table_model.getValueAt(indexs[i], queue_table_model.TARGET_COL)
										};
						String[] task2 = {"DELE_ENTRY", new Integer(indexs[i] - i).toString()};
						tq.putTask(task1);
						tq.putTask(task2);
					}
				}
			}	
		});
		TrayItem[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				synchronized (tq)
				{
					tq.clear();
				}
				if (jw != null) jw.doAbort();
			}
		});
		TrayItem[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				if (jw != null)
				{
					jw.close();
					jw = null; // 07-13 修正添加 解决断开连接后退出程序的错误BUG
				}
			}
		});
		TrayItem[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				int ExitOrNot = JOptionPane.showConfirmDialog(
						MainFrame.this, "确实要退出吗？", VersionInfoStr, JOptionPane.YES_NO_OPTION);
				if (ExitOrNot == JOptionPane.YES_OPTION) {
					if (jw != null)
					{
						jw.close();
						try
						{
							jw.join();
						}
						catch (InterruptedException ie)
						{
							ie.printStackTrace();
						}
					}
					System.exit(0);
				}
			}
		});
		
    			
		TrayMenu.add(TrayItem[0]);
		TrayMenu.addSeparator();
		TrayMenu.add(TrayItem[1]);
		TrayMenu.add(TrayItem[2]);
		TrayMenu.addSeparator();
		TrayMenu.add(TrayItem[3]);
		TrayMenu.add(TrayItem[4]);
		
    	try
		{
			if (java.awt.SystemTray.isSupported())
			{
				java.awt.SystemTray  st = java.awt.SystemTray.getSystemTray();
				Image image = Toolkit.getDefaultToolkit().getImage(
                        getClass().getResource("/ArkFTP/res/MainIcon.png"));
     	        java.awt.TrayIcon ti = new java.awt.TrayIcon( image);
        	    ti.setToolTip(VersionInfoStr);
            	ti.setPopupMenu(TrayMenu);    //为托盘添加右键菜单
            	ti.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent e)
            		{
            			if (MainFrame.this.isVisible())
  							MainFrame.this.setVisible(false);
						else
			   				MainFrame.this.setVisible(true);
			   		}
              	});
				st.add(ti);         
			}
		}
		catch (Exception e)
		{
              
		}       
	}
	
	public JLabel getStateLabel()
	{
		return this.state_lb;
	}
	
	public JTextArea getLogTextArea()
	{
		return this.printer_ta;
	}
	
	public JComboBox getServerComboBox()
	{
		return this.server_jcb;
	}
	
	public JProgressBar getProgressBar()
	{
		return this.jpb;
	}
	
	public ServerTableModel getServerTableModel()
	{
		return this.server_table_model;
	}
	
	public QueueTableModel getQueueTableModel()
	{
		return this.queue_table_model;
	}
	
	public LocalTableModel getLocalTableModel()
	{
		return this.local_table_model;
	}
	
	public JComboBox getLocalComboBox()
	{
		return this.local_jcb;
	}
	
	public JTable getQueueTable()
	{
		return this.queue_table;
	}
	
	public void printState(String state_str)
	{
		state_lb.setText(state_str);
	}
	
	public void setViewEnabled(boolean b)
	{
		queue_table.setEnabled(b);
		server_table.setEnabled(b);	
	}
	
	public void startFTP(String user_str, String pass_str, String server_str, String port_str)
	{
		int port;
		
		if (server_str.length() == 0)
		{
			MainFrame.this.printer_ta.append("必须输入一个合法的FTP服务器地址\n");
			return;
		}
		if (user_str.length() == 0)
		{
			user_str = "anonymous";
		}
		if (pass_str.length() == 0)
		{
			pass_str = "anonymous";
		}
		if (port_str.length() == 0)
		{
			port_str = "21";
			port = 21;
		}
		else
		{
			try
			{
				port = Integer.parseInt(port_str);
			}
			catch (Exception e)
			{
				port_str = "21";
				port = 21;
			}
		}
		
		try
		{
			if (jw != null)
			{
				jw.close();	
				jw.interrupt();
				try
				{
					jw.join();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					System.exit(0);
				}
			}
			jw = new ArkFTPWorker(server_str, port, user_str, pass_str, MainFrame.this, tq);
			this.server_jcb.setEditable(true);
		}
		catch (IOException e)
		{
		}
	}
} 
