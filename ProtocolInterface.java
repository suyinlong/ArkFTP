/* ***********************
** ProtocolInterface.java
** ***********************
** 重要的FTP协议接口
** Build 0712
** **********************/

package ArkFTP.bin.pi;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.StringTokenizer;
import java.nio.charset.Charset;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/** **************************
** ArkFTP Protocol Interface
** Build 0712
**   用于实现JFTP的协议接口，
**   处理用户FTP命令（如下列出）
** 	 [Access Commands]: 
**       USER PASS ACCT REIN QUIT ABOR
**   [File Menagement Commands]:
**       CWD CDUP DELE LIST NLIST MKD PWD RMD RNFR RNTO SMNT
**   [Data Formatting Commands]:
**       TYPE STRU MODE
**   [Port Defining Commands]:
**       PORT PASV
**   [File Transfer Commands]:
**       RENTR STOR APPE STOU ALLO REST STAT
**   [Miscellaneous Commands]:
**       HELP NOOP SITE SYST
*/
public class ProtocolInterface
{

	// 只是指定一下PORT范围
	final int PORT_LOW  = 0x1000; 
	final int PORT_HIGH = 0x7FFF;
	
	enum TransType {APPEND, RETRANS};
	
	private TransType storType = TransType.RETRANS;
	private TransType retrType = TransType.APPEND;
	
	private ServerSocket data_serversocket;	
	private Socket control_socket;
	private Socket data_socket;
	private Charset cset;	
	private BufferedWriter control_bw;	
	private BufferedReader control_br;

	
	public ProtocolInterface(String address_str, int Port) throws IOException
	{
		control_socket = new Socket(address_str, Port);
		
		cset = Charset.forName("GBK");
		control_br = new BufferedReader(
				new InputStreamReader(control_socket.getInputStream(), cset));
		control_bw = new BufferedWriter(
				new OutputStreamWriter(control_socket.getOutputStream(), cset));
		// 获取服务器初始回复。
		String s = readRespond();
		
		// server没有ready
		if(!s.substring(0, 3).equals("220"))
			throw new IOException("Server is not ready!");
	}

	private String path;
	private JLabel state_lb;
	private JProgressBar jpb;
	
	private long file_size = 0;
	
	// 是否是Passive模式。
	private boolean isPassive = true;
	
	// 向服务器发送一行命令
	private void sendRequest(String request) throws IOException
	{
		control_bw.write(request + '\r' + '\n');
		control_bw.flush();
	}
	
	/* ************************
	** 工具函数,接受一次服务器反馈信息。
	** 可能是多行的信息。
	** 返回 从FTP服务器读取当前查询的响应字符串。
	*/
	private String readRespond() throws IOException
	{
		// 如果服务器反馈的流以"XXX-"开头，则阻塞读取直到出现"XXX "为止。
		boolean isMultiLine = false;
		if (control_socket.isClosed())
			throw new IOException("Server Disconnected!");
		String str = control_br.readLine() + '\n';
		String result_str = str;
		if (str != null && str.length() >=4 && str.charAt(3) == '-')
		{
			isMultiLine = true;
			String respond_no = str.substring(0, 3);
			do
			{
				str = control_br.readLine() + '\n';
				result_str = result_str +  str; 
			} while(!(str.length() >= 4 && str.substring(0, 3).equals(respond_no) && str.charAt(3) == ' '));
		} 
		// 读取服务器反馈以"XXX "开头的剩余部分。
		while (isMultiLine && control_br.ready())
		{
			str = control_br.readLine();
			result_str = result_str +  str;
		}
		return result_str;
		
	}
	
	// 工具函数,发送并接受命令。
    //如果5秒钟没有交互完，则关闭Socket，抛出IOException.

	private String communicateCommands(String cmd_str) throws IOException
	{
		sendRequest(cmd_str);
		String respond_str = readRespond();
		return respond_str;
	}
	
	private String doCommandAbort() throws IOException
	{
		this.sendRequest("ABOR");
		try
		{
			if (this.data_socket != null && !this.data_socket.isClosed())
				data_socket.close();
			if (this.data_serversocket != null && !this.data_serversocket.isClosed())
				data_serversocket.close();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}
		return this.readRespond();
	}
	// 发送客户端无副作用的命令，并接受服务器反馈。

	private String doNoEffectiveCommands(String cmd_str) throws IOException
	{
		String s = this.communicateCommands(cmd_str);
		return s;
	}
	
	// 发送命令PORT [ip地址]，并且根据服务器反馈信息建立data_socket.
	//端口生成范围为PORT_LOW 到 PORT_HIGH之间。

	private String doCommandPort() throws IOException
	{
		// 生成范围在 PORT_LOW 到 PORT_HIGH 之间的一个ServerSocket
		int port;
		for (port = PORT_LOW; port < PORT_HIGH; port++)
		{
			try
			{
				data_serversocket = new ServerSocket(port);
			}
			catch (BindException be)
			{
				continue;
			}
			break;
		}
		
		String command_PORT = control_socket.getLocalAddress().getHostAddress();
		command_PORT = command_PORT.replace('.', ',');
		command_PORT = "PORT " + command_PORT + ',' + Integer.toString(port/256, 10) 
		  			 + ',' + Integer.toString(port%256, 10);
		return communicateCommands(command_PORT);		
	}
	
	// 发送命令PASV，并且根据服务器反馈信息建立data_socket.
	private String doCommandPasv() throws IOException
	{
		String respond_str = communicateCommands("PASV");
		if (respond_str.substring(0, 3).equals("227"))
		{
			int start = respond_str.indexOf('(') + 1;
			int end = respond_str.indexOf(')') - 1;
			int i = start + respond_str.substring(start, end).lastIndexOf(',') + 1;
			long port = Integer.parseInt(respond_str.substring(i, end+1));
			end = i - 2;
			i = start + respond_str.substring(start, end).lastIndexOf(',') + 1;
			port = port + 256 * Integer.parseInt(respond_str.substring(i, end+1));
			end = i - 2;
			String address_str = respond_str.substring(start, end+1).replace(',', '.');
			data_socket = new Socket(address_str, (int)port);
		}
		return respond_str;
	}
	
	private String doCommandStor(String cmd_str) throws IOException
	{
		long start_time = System.currentTimeMillis();
		String respond_str;
		// filename 是文件名，没有路径。
		String filename_str;
		BufferedInputStream data_bis = null;
		BufferedOutputStream data_bos = null;
		
		char seperator = File.separatorChar;
		int i = cmd_str.substring(5).lastIndexOf(seperator);
		if (i < 0)
			filename_str = cmd_str.substring(5);
		else
			filename_str = cmd_str.substring(6 + i);
		
		File file = new File(cmd_str.substring(5));
		data_bis = new BufferedInputStream(new FileInputStream(file));
		
		cmd_str = "STOR " + filename_str;
		
		doCommand("TYPE I");
		// 建立data_socket
		if (!isPassive)
		{
			respond_str = doCommand("PORT");
			if (respond_str.substring(0, 3).equals("200"))
			{
				respond_str = communicateCommands(cmd_str);
				if (respond_str.substring(0, 3).equals("150"))
					data_socket = data_serversocket.accept();
				else
					return respond_str;
			}
		}
		else
		{
			respond_str = doCommand("PASV");
			if (respond_str.substring(0, 3).equals("227"))
			{
				respond_str = communicateCommands(cmd_str);
				if (!respond_str.substring(0, 3).equals("150"))
				{
					data_socket.close();
					return respond_str;
				}
			}
		}
		
		try
		{
			file_size = file.length();
			data_bos = new BufferedOutputStream(data_socket.getOutputStream());
			long hasRead = 0;
			int read = 0;
			
			byte[] buffer = new byte[1000000];
			int readPerSecond = 0;
			long old_time = start_time;
			while (true)
			{
				String state_str = cmd_str;
				read = data_bis.read(buffer, 0, buffer.length);
				hasRead += read;
				readPerSecond += read;
			
				if (read == -1)
					break;
				data_bos.write(buffer, 0, read);
				long current_time = System.currentTimeMillis();
				long eclipsed_time = current_time - old_time;
				if (eclipsed_time / 1000 >= 1)
				{
					long speed = (long)readPerSecond * 1000 / (long) eclipsed_time;
					old_time = current_time;					
					// 更新速度
					String speed_str;
					if (speed < 1000)
						speed_str = Long.toString(speed) + "B/s";
					else if (speed < 1000000)
						speed_str = Long.toString(speed/1000) + '.' + Long.toString( (speed%1000)/10 ) + "KB/s";
					else
						speed_str = Long.toString(speed/1000000) + '.' + Long.toString( (speed%1000000) /10000) + "MB/s";
					// 更新进度条
					if (file_size > 0)
					{
						final int cnt = (int)( (hasRead * 100) / file_size);
						state_str = state_str.concat("(" + cnt + "%)");
						long passed_seconds = (current_time - start_time)/1000;
						long expected_seconds = (file_size - hasRead) / speed + 1;
						state_str = state_str.concat("   已耗时：" + passed_seconds + "秒  仍需要：" + expected_seconds + "秒");
						final String stateDisplay_str = state_str;
						final String speedDisplay_str = speed_str;
						if (jpb != null && state_lb != null)
						{
							Runnable runnable = new Runnable()
							{
								public void run()
								{
									jpb.setVisible(true);
									jpb.setValue(cnt);
									jpb.setStringPainted(true);
									jpb.setString(speedDisplay_str);
									state_lb.setText(stateDisplay_str);
								}
							};
							SwingUtilities.invokeLater(runnable);
						}							
					}					
					readPerSecond = 0;
				}
			}
			data_bos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jpb != null)
			{
				Runnable runnable = new Runnable()
				{
					public void run()
					{
						jpb.setVisible(false);
					}
				};
				SwingUtilities.invokeLater(runnable);
			}

			// 清理
			try
			{
				if (data_serversocket != null && !data_serversocket.isClosed())
					data_serversocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				if (data_socket != null && !data_socket.isClosed())
					data_socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				data_bos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				data_bis.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}	
		
		respond_str = readRespond();
		return respond_str;
	}
	
	private String doCommandRetr(String cmd_str) throws IOException
	{
		long start_time = System.currentTimeMillis();
		String respond_str;
		String filename_str;
		BufferedInputStream data_bis = null;
		// 应该在创建data套接口之前先检查是否能够创建文件输出流。
		// 如果创建文件输出流时发生了异常，则应该区别于data线路的异常，应该上抛。
		BufferedOutputStream data_bos = null;
		
		// filename_str对应本地文件绝对路径，调用Retr需要先设置Path
		int i = cmd_str.substring(5).lastIndexOf('/');
		if (i < 0)
			filename_str = cmd_str.substring(5);
		else
			filename_str = cmd_str.substring(6 + i);		
		if (path != null)
			filename_str = path + filename_str;
		
		//doCommand("HELP");
		respond_str = doCommand("SIZE " + cmd_str.substring(5));
		communicateCommands("STAT " + cmd_str.substring(5));
		if (respond_str.substring(0, 3).equals("213"))
			file_size = Long.parseLong(respond_str.substring(4, respond_str.length() - 1));
		
		doCommand("TYPE I");
		
		File file = new File(filename_str);
		if (file.exists() && retrType == TransType.APPEND)
		{ 
			data_bos = new BufferedOutputStream(new FileOutputStream(file, true));
			// 在ArkFTPWorker中确保服务器文件长度要大于本地文件长度。
			communicateCommands("REST " + Long.toString(file.length()));
			file_size = file_size - file.length();
		}
		else 
			data_bos = new BufferedOutputStream(new FileOutputStream(file, false));
		
		// 建立data_socket
		if (!isPassive)
		{
			respond_str = doCommand("PORT");
			if (respond_str.substring(0, 3).equals("200"))
			{
				respond_str = communicateCommands(cmd_str);
				if (respond_str.substring(0, 3).equals("150"))
					data_socket = data_serversocket.accept();
				else
					return respond_str;
			}
		}
		else
		{
			respond_str = doCommand("PASV");
			if (respond_str.substring(0, 3).equals("227"))
			{
				respond_str = communicateCommands(cmd_str);
				if (!respond_str.substring(0, 3).equals("150"))
				{
					data_socket.close();
					return respond_str;
				}
			}
		}
		
		try
		{
			long hasRead = 0;
			int read = 0;
			this.state_lb.setText(cmd_str);
			data_bis = new BufferedInputStream(data_socket.getInputStream());
			
			byte[] buffer = new byte[1000000];
			int readPerSecond = 0;
			long old_time = start_time;
			while (true)
			{
				String state_str = cmd_str;
				read = data_bis.read(buffer, 0, buffer.length);
				hasRead += read;
				readPerSecond += read;
			
				if (read == -1)
					break;
				data_bos.write(buffer, 0, read);
				long current_time = System.currentTimeMillis();
				long eclipsed_time = current_time - old_time;
				if (eclipsed_time / 1000 >= 1)
				{
					long speed = (long)readPerSecond * 1000 / (long) eclipsed_time;
					old_time = current_time;					
					// 更新速度
					String speed_str;
					if (speed < 1000)
						speed_str = Long.toString(speed) + "B/s";
					else if (speed < 1000000)
						speed_str = Long.toString(speed/1000) + '.' + Long.toString( (speed%1000)/10 ) + "KB/s";
					else
						speed_str = Long.toString(speed/1000000) + '.' + Long.toString( (speed%1000000) /10000) + "MB/s";
					// 更新进度条
					if (file_size > 0)
					{
						final int cnt = (int)( (hasRead * 100) / file_size);
						state_str = state_str.concat("(" + cnt + "%)");
						long passed_seconds = (current_time - start_time)/1000;
						long expected_seconds = (file_size - hasRead) / speed + 1;
						state_str = state_str.concat("   已耗时：" + passed_seconds + "秒  仍需要：" + expected_seconds + "秒");
						final String stateDisplay_str = state_str;
						final String speedDisplay_str = speed_str;
						if (jpb != null && state_lb != null)
						{
							Runnable runnable = new Runnable()
							{
								public void run()
								{
									jpb.setVisible(true);
									jpb.setValue(cnt);
									jpb.setStringPainted(true);
									jpb.setString(speedDisplay_str);
									state_lb.setText(stateDisplay_str);
								}
							};
							SwingUtilities.invokeLater(runnable);
						}							
					}					
				
					readPerSecond = 0;
				}
			}
			data_bos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (jpb != null)
			{
				Runnable runnable = new Runnable()
				{
					public void run()
					{
						jpb.setVisible(false);
					}
				};
				SwingUtilities.invokeLater(runnable);
			}

			file_size = 0;
			// 清理
			try
			{
				if (data_serversocket != null && !data_serversocket.isClosed())
					data_serversocket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				if (data_socket != null && !data_socket.isClosed())
					data_socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				data_bos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				data_bis.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}	
		
		respond_str = readRespond();
		return respond_str;
	}
	

	class ListHelper extends Thread
	{
		private String[] strArray;
		public ListHelper(String[] strArray)
		{
			this.strArray = strArray;
		}
		public void run()
		{	
			BufferedReader data_br = null;
			try
			{
				if (!isPassive)
				{
					data_serversocket.setSoTimeout(1000);
					data_socket = data_serversocket.accept();
				} 
				data_br = new BufferedReader(new InputStreamReader(data_socket.getInputStream(), cset));
				long start_time = System.currentTimeMillis();
				String line;
				String lines_str = "";

				while (true)
				{
					line = data_br.readLine();
					// 如果line为null，则说明读到了EOF。
					if (line == null)
						break;
					lines_str += line + '\n';
				}
				if (lines_str == "")
					lines_str += '\n';

				strArray[0] = lines_str;
				long end_time = System.currentTimeMillis();
			}
			catch (InterruptedIOException e)
			{
				// 连接超时，退出。
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{						
					if (data_serversocket != null && !data_serversocket.isClosed())
						data_serversocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					if (data_socket != null && !data_socket.isClosed())
						data_socket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					if (data_br != null)
						data_br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	

	private String doCommandList(String cmd_str) throws IOException
	{
		String respond_str;
		doCommand("TYPE A");
		
		String[] strArray = new String[1];
		strArray[0] = null;
		Thread listhelper_thread = new ListHelper(strArray);
		listhelper_thread.start();
		respond_str = communicateCommands(cmd_str);
		if (!respond_str.substring(0, 3).equals("150"))
		{
			return respond_str;
		}
		
		try
		{
			listhelper_thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		if (strArray[0] == null)
			return null;
		
		String confirm_str = readRespond();

		if (!confirm_str.substring(0, 3).equals("226"))
		{
			return confirm_str;
		}
		else
		{
			return confirm_str + strArray[0];
		}
	}
	// 工具函数，发送QUIT并且清理。
	private String doCommandQuit() throws IOException
	{
		String s = this.communicateCommands("QUIT");
		s.substring(0, 3);
		this.close();
		return s;
	}
	
	public boolean isServerColsed()
	{
		return control_socket.isClosed();
	}
	
	public String hasRespondofServerTerminate() throws IOException
	{
		if (control_br.ready())
			return this.readRespond();
		return null;
	}
	
	public void setStateLabel(JLabel state_lb)
	{
		this.state_lb = state_lb;
	}
	
	public void setProgressBar(JProgressBar jpb)
	{
		this.jpb = jpb;
	}
	
	public void setPath(String path)
	{
		if (path != null)
			this.path = path;
	}
	
	public void setPassive()
	{
		isPassive = true;
	}
	
	public void setFileLen(long file_size)
	{
		this.file_size = file_size;
	}
	
	public void setNonPassive()
	{
		isPassive = false;
	}
	
	public String doCommand(String cmdline_str) throws IOException
	{
		String respond_str = "000 ";
		Vector<String> v = new Vector<String>(); 
		StringTokenizer st = new StringTokenizer(cmdline_str);
		if (st.hasMoreTokens())
			v.add(st.nextToken());
		if (v.size() == 0)
			return respond_str;
		
		if (v.elementAt(0).equals("QUIT"))
		{
			respond_str = doCommandQuit();
		}
		else if (v.elementAt(0).equals("LIST") || v.elementAt(0).equals("NLIST"))
		{
			respond_str = doCommandList(cmdline_str);			
		}
		else if (v.elementAt(0).equals("PORT"))
		{
			respond_str = doCommandPort();
		}
		else if (v.elementAt(0).equals("PASV"))
		{
			respond_str = doCommandPasv();
		}
		else if (v.elementAt(0).equals("RETR"))
		{
			respond_str = doCommandRetr(cmdline_str);
		}
		else if (v.elementAt(0).equals("STOR"))
		{
			respond_str = doCommandStor(cmdline_str);
		}
		else if (v.elementAt(0).equals("ABOR"))
		{
			respond_str = doCommandAbort();
		}
		else if (v.elementAt(0).equals("SYST") || v.elementAt(0).equals("HELP") || v.elementAt(0).equals("USER")
				|| v.elementAt(0).equals("CWD")  || v.elementAt(0).equals("PASS") || v.elementAt(0).equals("PWD")
				|| v.elementAt(0).equals("CDUP") || v.elementAt(0).equals("DELE") || v.elementAt(0).equals("MKD")
				|| v.elementAt(0).equals("RMD")  || v.elementAt(0).equals("RNFR") || v.elementAt(0).equals("RNTO")
				|| v.elementAt(0).equals("TYPE") || v.elementAt(0).equals("STRU") || v.elementAt(0).equals("NOOP")
				|| v.elementAt(0).equals("SIZE") || v.elementAt(0).equals("ABOR")) 
		{
			respond_str = doNoEffectiveCommands(cmdline_str);
		}
		return respond_str;
	}
	
	public void close()
	{	
		try
		{
			this.sendRequest("QUIT");
			if (control_socket != null)// && !control_socket.isClosed())
				control_socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			if (control_br != null)
				control_br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			if (control_bw != null)
				control_bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

