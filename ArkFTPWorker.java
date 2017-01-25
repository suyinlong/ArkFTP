/* ***********************
** ArkFTPWorker.java
** ***********************
** ArkFTP的FTP服务主线程
** Build 0716
** 07-14 实现日志自动滚屏功能
** 07-16 改进了滚屏方法...
** **********************/

package ArkFTP.bin.main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import ArkFTP.bin.pi.ProtocolInterface;
import ArkFTP.bin.ui.MainFrame;
import ArkFTP.bin.util.TaskQueue;

public class ArkFTPWorker extends Thread
{
// 声明一系列的私有成员域
	private ProtocolInterface pi;
	
	private MainFrame mf;
	
	private String currentDir_str;
	
	private boolean isPassive = true;	
	private boolean isNoop = false;	
	private int idleSeconds = 0;	
	private boolean isContinue = true;
	
	private TaskQueue tq = null;
	
	private String user_str;	
	private String pass_str;	
	private String address_str;	
	private int port;
	
	final private int DELAY_TIME = 5000;
	
	// doCommandandLog方法用于提交命令并写入通迅记录
	
	private String doCommandandLog(String cmd_str) throws IOException
	{
		Timer timer = new Timer();
		if (cmd_str.length() == 4 && (cmd_str.substring(0, 4).equals("RETR") || cmd_str.substring(0, 4).equals("STOR"))) {
			TimerTask timerTask = new TimerTask() {
				public void run()
				{
					if (mf != null)
					{
						mf.getLogTextArea().setForeground(Color.RED);
						mf.getLogTextArea().append("Sever Responds Time Out!");
						mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
					}
					ArkFTPWorker.this.close();
				}
			};
			timer.schedule(timerTask, DELAY_TIME);
		}
		String respond_str = pi.doCommand(cmd_str);
		timer.cancel();
		JTextArea ta = mf.getLogTextArea();
		ta.setForeground(Color.BLACK);
		
		if (cmd_str.length() > 3 && cmd_str.substring(0, 4).equals("PASS"))
			ta.append("PASS ******\n");
		else
			ta.append(cmd_str + '\n');
		
		ta.setCaretPosition(ta.getText().length());
		ta.setForeground(Color.BLUE);
		if(respond_str.substring(0, 3).equals("226"))
		{
			int n = respond_str.indexOf('\n');
			ta.append(respond_str.substring(0, n+1));
		}
		else			
			ta.append(respond_str);

		ta.setCaretPosition(ta.getText().length());
		return respond_str;
	}
	
	// insertDeleQueue 方法处理传输队列
	
	private void insertDeleQueue(String dataofList_str, String DirAbsoluteName_str)
	{
		if (dataofList_str == null) 
			return;
		
		String[] task;
		Vector<String[]> task_v = new Vector<String[]>();
		
		int i = 0, j;
		while( i < dataofList_str.length()) {
			if ( (j = i + dataofList_str.substring(i).indexOf('\n')) == -1)
				break;
			
			StringTokenizer stk = new StringTokenizer(dataofList_str.substring(i, j));
			task = new String[2];
			int count = stk.countTokens();
			if (count >= 9) {
				boolean isDir = false;
				task[0] = "DELE";
				if (stk.nextToken().charAt(0) == 'd')
				{
					isDir = true;
					task[0] = "RRMD";
				}
				// 忽略4个不关心的属性
				for (int k = 0; k < 7; k++)
					stk.nextToken();
				
				// Name
				task[1] = stk.nextToken();
				while(stk.hasMoreTokens())
					task[1] += " " + stk.nextToken();
				
				if (!task[1].equals(".") && !task[1].equals(".."))
				{
					task[1] = DirAbsoluteName_str + task[1];
					if (isDir)
						task[1] += '/';
					task_v.add(task);
				}

			}
			i = j + 1;
		}
		
		String[] last_task = {"RMD", DirAbsoluteName_str};
		task_v.add(last_task);
		tq.insertTasks(task_v);
	}
	
	/**
	 * 函数listFileAllStr()，用来获取指定服务器文件夹内所有文件的所有信息。
	 * @param serverDir_str 服务器上的文件夹绝对目录
	 * @return Vector<String[]> ： 其中Vector的每个元素是一个文件的全部属性<br>
	 * 0  --->  权限属性
	 * 1  --->  ?
	 * 2  --->  用户
	 * 3  --->  组
	 * 4  --->  大小
	 * 5  --->  时间
	 * 6  --->  文件名 
	 */
	private Vector<String[]> listFileAllStr(String serverDir_str)
	{
		String dataofList_str = null;
		try
		{
			dataofList_str = this.doListNoEffect(serverDir_str);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.close();
		}
		
		if (dataofList_str == null || dataofList_str == "")
			return null;
		else
		{
			int i = 0, j;
			Vector<String[]> fileAll_v = new Vector<String[]>();
			while( i < dataofList_str.length())
			{
				if ( (j = i + dataofList_str.substring(i).indexOf('\n')) == -1)
					break;
				
				StringTokenizer stk = new StringTokenizer(dataofList_str.substring(i, j));
				int count = stk.countTokens();
				if (count >= 9)
				{
					String[] all_array = new String[7];
					boolean isDir = false;
					for (int k = 0; k < 5; k++)
						all_array[k] = stk.nextToken();
					if (all_array[0].charAt(0) == 'd') 
						isDir = true;
					
					all_array[5] = stk.nextToken();
					for (int k = 0; k < 2; k++)
						all_array[5] = all_array[5] + " " + stk.nextToken();
					
					// Name
					String fileName_str = stk.nextToken();
					while(stk.hasMoreTokens())
						fileName_str += " " + stk.nextToken();					
					
					if (!fileName_str.equals(".") && !fileName_str.equals(".."))
					{
						if (isDir)
							fileName_str += '/';
						all_array[6] = fileName_str;
						fileAll_v.add(all_array);
					}

				}
				i = j + 1;
			}
			return fileAll_v;
		}
	}
	
	private Vector<String> listFileNameStr(String serverDir_str)
	{
		String dataofList_str = null;
		try
		{
			dataofList_str = this.doListNoEffect(serverDir_str);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.close();
		}
		
		if (dataofList_str == null || dataofList_str == "")
			return null;
		else
		{
			int i = 0, j;
			Vector<String> fileNameStr_v = new Vector<String>();
			while( i < dataofList_str.length())
			{
				if ( (j = i + dataofList_str.substring(i).indexOf('\n')) == -1)
					break;
				
				StringTokenizer stk = new StringTokenizer(dataofList_str.substring(i, j));
				int count = stk.countTokens();
				if (count >= 9)
				{
					boolean isDir = false;
					if (stk.nextToken().charAt(0) == 'd')
						isDir = true;
					// 忽略7个不关心的属性
					for (int k = 0; k < 7; k++)
						stk.nextToken();
					
					// Name
					String fileName_str = stk.nextToken();
					while(stk.hasMoreTokens())
						fileName_str += " " + stk.nextToken();
					
					if (!fileName_str.equals(".") && !fileName_str.equals(".."))
					{
						if (isDir)
							fileName_str += '/';
						fileNameStr_v.add(fileName_str);
					}

				}
				i = j + 1;
			}
			return fileNameStr_v;
		}		
	}

// 刷新功能的实现
	private void updateDir() throws IOException
	{
		String str = doCommandandLog("PWD");
		if (str.length() > 3 && str.charAt(0) == '2')
		{
			int i = str.indexOf('\"');
			int j = i + 1 + str.substring(i+1).indexOf('\"');
			currentDir_str = str.substring(i+1, j);
			if(currentDir_str.charAt(currentDir_str.length() - 1) != '/')
				currentDir_str += '/';
		}
	}
	
	// 用于比较字串
	
	private boolean checkNo(String check_str, String no_str)
	{
		return check_str.substring(0, 3).equals(no_str);
	}
	
	//向状态栏写入当前空闲时间
	
	private void setStateIdleTime(int seconds)
	{
		int secs = seconds % 60;
		int mins = seconds / 60;
		String secs_str = Integer.toString(secs);
		String mins_str = Integer.toString(mins);
		if (secs < 10)
			secs_str = "0" + secs_str;
		if (mins < 10)
			mins_str = "0" + mins_str;
		mf.printState("空闲时间 (" + mins_str + ":" + secs_str + ")");
	}
	
	// getCurrentDir 返回当前目录
	
	public String getCurrentDir()
	{
		return this.currentDir_str;
	}
	
	// setTaskQueue 重设传输队列对象
	
	public void setTaskQueue(TaskQueue tq)
	{
		this.tq = tq;
	}
	
	// 构造方法
	
	public ArkFTPWorker(String address_str, int port, String user_str, String pass_str,  MainFrame mf, TaskQueue tq) throws IOException
	{
		super("ArkFTPWorker");
		this.address_str = address_str;
		this.user_str = user_str;
		this.pass_str = pass_str;
		this.port = port;
		this.mf = mf;
		this.tq = tq;
		this.start();
	}
	
	// 设定线程工作不再继续
	public synchronized void terminate()
	{
		isContinue = false;
	}
	
	// 返回线程工作是否继续
	public synchronized boolean isContinue()
	{
		return isContinue;
	}
	
	// 线程主内容
	public void run()
	{
		mf.setForeground(Color.BLUE);
		mf.getLogTextArea().setText("");
		try
		{
			pi = new ProtocolInterface(address_str, port);
			// 建立ProtocolInterface对象,尝试连接服务器
		}
		catch (IOException e)
		{
			if (pi != null)
				pi.close();
			mf.getLogTextArea().setForeground(Color.RED);
			mf.getLogTextArea().append("Cannot connect Server [" + address_str + ":" + port + "]\n");
			mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
			return;
		}
		
		// 服务器连接后列出目录内容
		String[] task = {"LIST", "/"};
		tq.putTask(task);

		if (!this.log_in(user_str, pass_str))
			return ;
		mf.getServerComboBox().setEnabled(true);
		mf.getServerComboBox().setEditable(false);
		
		while (isContinue)
		{
			task = null;
			synchronized(tq)
			{
				while (tq.isEmpty() && isContinue)
				{
					try
					{
						setStateIdleTime(idleSeconds);
						tq.wait(1000);
						this.idleSeconds += 1;
					}
					catch (InterruptedException e)
					{
					}
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							mf.getQueueTable().setSelectionBackground(Color.ORANGE);
						}
					});
					if ((idleSeconds + 1) % 15 == 0)
					{
						if (isNoop && !this.checkAlive())
						{
							isContinue = false;
							tq.clear();
						}
						if (!isNoop)
						{								
							try
							{
								String respond_str = pi.hasRespondofServerTerminate();
								if (respond_str != null && this.checkNo(respond_str, "426"))
								{
									mf.getLogTextArea().append(respond_str + '\n');
									mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
									isContinue = false;
									tq.clear();
								}
							}
							catch (IOException e)
							{
								e.printStackTrace();
								this.close();
							}
						}
					}
				}
				if (!tq.isEmpty())
					task = tq.getTask();
			}
			
			if (task != null)
			{
				mf.setViewEnabled(false);
				if (task[0].equals("LIST"))
				{
					assert(task.length == 2);
					mf.printState(task[0] + " -al " + task[1]);
					this.listFileNameStr(task[1]);
					listFiles(task[1]);
					idleSeconds = 0;
				}
				else if (task[0].equals("MKD"))
				{
					assert(task.length == 2);
					mf.printState(task[0] + " " + task[1]);
					try
					{
						this.doCommandandLog(task[0] + " " + task[1]);
						listFiles(this.currentDir_str);
					}
					catch (IOException e)
					{
						this.close();
					}
					idleSeconds = 0;
				}
				else if (task[0].equals("DELE") || task[0].equals("CWD"))
				{
					mf.printState(task[0] + " " + task[1]);
					try
					{
						this.doCommandandLog(task[0] + " " + task[1]);
					}
					catch (IOException e)
					{
						this.close();
					}
					idleSeconds = 0;
				}
				else if (task[0].equals("RMD"))
				{
					mf.printState(task[0] + " " + task[1]);
					try
					{
						this.doCommandandLog(task[0] + " " + task[1]);
					}
					catch (IOException e)
					{
						this.close();
					}
					idleSeconds = 0;
				}
				else if (task[0].equals("RRMD"))
				{
					// 递归删除文件夹
					mf.printState(task[0] + " " + task[1]);
					final String dataOfList_str;
					try
					{
						dataOfList_str = this.doListNoEffect(task[1]);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						this.close();
						return;
					}
					
					// 数据连接出错,什么也不做.
					if (dataOfList_str.equals(""))
					{
						return;
					}
					
					this.insertDeleQueue(dataOfList_str, task[1]);									
					idleSeconds = 0;
				}
				else if (task[0].equals("STOR"))
				{
					pi.setProgressBar(mf.getProgressBar());
					pi.setStateLabel(mf.getStateLabel());
					File file = new File(task[1]);
					String pathSave_str = this.currentDir_str;
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							mf.getQueueTable().setSelectionBackground(Color.GREEN);
						}
					});
					try
					{
						if (!file.isDirectory())
						{
							this.doCommandandLog("CWD " + task[2]);
							storeFiles(file.getAbsolutePath());
						}
						else
						{
							// 此时的按照文件结构生成完成上传文件夹的任务队列taskQueue，
							// 然后将此任务队列插入到任务队列tq前面去。
							Vector<String []> taskQueue = new Vector<String []>();
							Vector<File> dir_v = new Vector<File>();
							Vector<String> serverPathStr_v = new Vector<String>();
							dir_v.add(file);
							serverPathStr_v.add(task[2]);
							while (!dir_v.isEmpty())
							{
								File dir = dir_v.remove(0);
								String serverPath_str = serverPathStr_v.remove(0);
								String[] task1 = {"MKD", serverPath_str + dir.getName()};
								taskQueue.add(task1);
								File[] files = dir.listFiles();
								if (files != null)
								{
									for (File f : files)
									{
										if (f.isFile())
										{
											String[] task2 = {"STOR", f.getAbsolutePath(), serverPath_str + dir.getName() + '/'};
											taskQueue.add(task2);
										}
										else
										{
											dir_v.add(f);
											serverPathStr_v.add(serverPath_str + dir.getName() + '/');
										}
									}
								}
							}
							String[] task3 = {"LIST", pathSave_str};
							taskQueue.add(task3);
							tq.insertTasks(taskQueue);
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
						this.close();
					} 
					idleSeconds = 0;
				}
				else if (task[0].equals("RENAME"))
				{
					mf.printState(task[0] + " " + task[2] + " to " + task[1]);
					try
					{
						if (this.checkNo(this.doCommandandLog("RNFR " + task[1]), "350"))
						{
							if (this.checkNo(this.doCommandandLog("RNTO " + task[2]), "250"))
								listFiles(this.currentDir_str);
						}
					}
					catch (IOException e)
					{
						this.close();
					}
					idleSeconds = 0;
				}
				else if (task[0].equals("RETR"))
				{
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							mf.getQueueTable().setSelectionBackground(Color.GREEN);
						}
					});
					
					if (task[1].charAt(task[1].length()-1) == '/')
					{
						Vector<String[]> task_v = new Vector<String[]>();
						Vector<String> serverDirStr_v = new Vector<String>();
						Vector<String> localDirStr_v = new Vector<String>();
						serverDirStr_v.add(0, task[1]);
						localDirStr_v.add(0, task[2]);
						while (!serverDirStr_v.isEmpty())
						{
							String serverDir_str = serverDirStr_v.remove(0);
							String localDir_str = localDirStr_v.remove(0);
							int i = serverDir_str.substring(0, serverDir_str.length()-1).lastIndexOf('/');
							localDir_str += serverDir_str.substring(0,  serverDir_str.length()-1).substring(i+1) + File.separatorChar;
							File path_file = new File(localDir_str);
							path_file.mkdir();
							Vector<String[]> fileAll_v = this.listFileAllStr(serverDir_str);
							if (fileAll_v != null)
							{
								for (String[] fileAll_array : fileAll_v)
								{
									String fileName_str = fileAll_array[6]; 
									String fileSize_str = fileAll_array[4];
									if (fileName_str.charAt(fileName_str.length()-1) != '/')
									{
										String[] task1 = {"RETR", serverDir_str + fileName_str, localDir_str, fileSize_str};
										task_v.add(task1);
									}
									else
									{
										serverDirStr_v.add(0, serverDir_str + fileName_str);
										localDirStr_v.add(0, localDir_str);
									}
								}
							}
						}
						tq.insertTasks(task_v);
					}
					else
					{
						pi.setProgressBar(mf.getProgressBar());
						pi.setStateLabel(mf.getStateLabel());
						pi.setFileLen(Long.parseLong(task[3]));
						downloadFile(task[1], task[2]);
						idleSeconds = 0;
					}
				}
				else if (task[0].equals("DELE_ENTRY"))
				{
					final int entryNo = Integer.parseInt(task[1]);
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							mf.getQueueTableModel().removeRow(entryNo);
						}
					});
				}
				else if (task[0].equals("REFRESH_LOCAL"))
				{
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							mf.getLocalTableModel().addAllChildren(new File((String)mf.getLocalComboBox().getSelectedItem()));
						}
					});
				}
				mf.setViewEnabled(true);
			}
		}
	}
	
	public void changeFTPServer(String address_str, int port) throws IOException
	{
		pi.close();
		pi = new ProtocolInterface(address_str, port);			
	}
	
	public boolean log_in(String name_str, String pass_str)
	{
		try
		{
			String respond_str = doCommandandLog("USER " + name_str);
			if (respond_str.length() > 3 && checkNo(respond_str, "331"))
			{
				respond_str = doCommandandLog("PASS " + pass_str);
				if (respond_str.length() > 3 && checkNo(respond_str, "230"))
					return true;
			}
			return false;
		}
		catch (IOException e)
		{
			pi.close();
			return false;
		}
	}
	
	/**
	 * 
	 * @param absoluteDir_str 需要List的目录的服务器绝对路径
	 * @return (1)读取成功，返回多行信息，如果目录下没有子项，返回空行。
	 * 		   (2)其它，返回null;
	 * @throws IOException 控制连接出错
	 */
	private String doListNoEffect(String absoluteDir_str) throws IOException
	{
		String respond_str;
		if (isPassive)
		{
			pi.setPassive();
			respond_str = doCommandandLog("PASV");
			if ( respond_str.substring(0, 3).equals("227") )
			{
				respond_str = pi.doCommand("LIST -al " + absoluteDir_str);
			}
		}
		else
		{
			pi.setNonPassive();
			respond_str = doCommandandLog("PORT");
			if ( respond_str.substring(0, 3).equals("200") )
			{
				respond_str = pi.doCommand("LIST -al " + absoluteDir_str);
			}
		}
		
		if (respond_str != null)
			mf.getLogTextArea().append(respond_str);
		
		if (respond_str == null)
			mf.getLogTextArea().append("LIST " + absoluteDir_str + " Error!\n");
		else if (respond_str.length() > 3 && checkNo(respond_str, "226"))
			return respond_str.substring((respond_str.indexOf('\n') + 1));
		
		mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
		return null;
	}
	
	/**
	 * 仅仅用于List服务器当前目录下的目录，并且修改当前目录为List后的当前目录。<br>
	 * 由于修改了全局的currentDir_str,所以不能用来递归下载文件夹。
	 * @param dir_str 目录名
	 * @return String (1)返回成功，返回多行的目录信息，如果为空目录，则返回一空行，修改currentDir_str；
	 * 				  (2)数据连接错误，返回空字符串；
	 * 				  (3)数据连接异常，返回null。
	 * @throws IOException 控制连接出错
	 */
	private String doList(String dir_str) throws IOException
	{
		String respond_str;
		
		if (dir_str != null && dir_str.equals(".."))
			doCommandandLog("CDUP");
		else if (dir_str != null)
			doCommandandLog("CWD " + dir_str);

		
		if (isPassive)
		{
			pi.setPassive();
			respond_str = doCommandandLog("PASV");
			if ( respond_str.substring(0, 3).equals("227") )
			{
				respond_str = doCommandandLog("LIST -al");
			}
		}
		else
		{
			pi.setNonPassive();
			respond_str = doCommandandLog("PORT");
			if ( respond_str.substring(0, 3).equals("200") )
			{
				respond_str = doCommandandLog("LIST");
			}
		}
			
		if (respond_str == null)
			return null;
		else if (respond_str != null && respond_str.length() > 3 && checkNo(respond_str, "226")) {
				return respond_str.substring((respond_str.indexOf('\n') + 1));
		}
		else return "";
	}
	
	public void listFiles(String dir_str)
	{
		final String dataOfList_str;
		try
		{
			dataOfList_str = doList(dir_str);
			this.updateDir();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.close();
			return;
		}
		
		// 数据连接异常，什么也不做。
		if (dataOfList_str == null)
			return;
		
		// 数据连接错误,什么也不做。
		if (dataOfList_str.equals(""))
			return;
		
		// 没有出错，dataOfList_str就是List命令服务器返回的数据。
		Runnable runnable = new Runnable() {
			public void run()
			{
				mf.getServerTableModel().removeAllRows();
				mf.getServerTableModel().addToTable(dataOfList_str);
				mf.getServerComboBox().removeAllItems();
				mf.getServerComboBox().addItem(ArkFTPWorker.this.currentDir_str);
			}
		};
		SwingUtilities.invokeLater(runnable);
	}	
	
	public void storeFiles(String filename_str)
	{
		try
		{
			mf.getStateLabel().setText("STOR " + filename_str);
			doCommandandLog("STOR " + filename_str);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			close();
		}
	}
	
	public void doAbort()
	{
		try
		{
			doCommandandLog("ABOR");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			close();
		}
	}
	
	private void downloadFile(String filename_str, String path_str)
	{
		pi.setPath(path_str);
		try
		{
			doCommandandLog("RETR " + filename_str);
		}
		catch (FileNotFoundException e)
		{
			mf.getLogTextArea().append("File error!\n");
			mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
		}
		catch (IOException e)
		{
			close();
		}
	}
	
	private boolean checkAlive()
	{
		try
		{
			String respond_str = doCommandandLog("NOOP");
			if (this.checkNo(respond_str, "200"))
				return true;
		}
		catch (IOException e)
		{
			close();
		}
		return false;
	}
	
	public void close()
	{
		tq.clear();
		this.terminate();
		this.interrupt();
		Runnable runnable = new Runnable() {
			public void run()
			{
				mf.getServerTableModel().removeAllRows();
				mf.getQueueTableModel().removeAllRows();
				mf.getServerComboBox().removeAllItems();
				mf.getServerComboBox().setEnabled(false);
				mf.getStateLabel().setText("(连接已断开)");
				mf.getLogTextArea().append("Disconnect.\n");
				mf.getLogTextArea().setCaretPosition(mf.getLogTextArea().getText().length());
				mf.setEnabled(true);
			}
		};
		SwingUtilities.invokeLater(runnable);
		if (pi != null)
			pi.close();
	}
}

