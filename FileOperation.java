/* ***********************
** FileOperation.java
** ***********************
** ���ڹ����ļ���, ʵ���������ļ�����
** Build 0712
** **********************/
package ArkFTP.bin.util;

import java.io.File;

public class FileOperation
{
	private FileOperation() {}
	
	public static String listDir(File file)
	{
		if (file.exists() && file.isDirectory())
		{
			String s = "";
			File[] fs = file.listFiles();
			for (File f : fs)
			{
				s += f.getName() + '\n';
			}
			return s;
		}
		else return null;
	}

}
