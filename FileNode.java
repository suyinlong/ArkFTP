/* ***********************
** FileNode.java
** ***********************
** Create FileNode Class, for upload/download
** Build 0712
** **********************/
package ArkFTP.bin.util;

import javax.swing.tree.*;
import java.io.File;


public class FileNode extends DefaultMutableTreeNode
{
	private boolean explored = false;

	private boolean isDirectory()
	{
		return getFile().isDirectory();
	}

	public FileNode(File file)
	{
		setUserObject(file);
	}

	public boolean getAllowsChildren()
	{
		return isDirectory();
	}

	public boolean isLeaf()
	{
		return !isDirectory();
	}

	private File getFile()
	{
		return (File)this.getUserObject();
	}

	public boolean isExplored()
	{
		return explored;
	}

	public String toString()
	{
		String filename_str = getFile().toString();
		int index = filename_str.lastIndexOf(File.separatorChar);

		// if found separatorChar and the idndex is not the last one -> absolute path, only get the filename part
		// 	 otherwise -> relative path, use the whold string
		filename_str = (index != -1 && index != filename_str.length()-1) ? filename_str.substring(index + 1) : filename_str;
		if (isDirectory() && filename_str.charAt(filename_str.length()-1) != File.separatorChar)
		{
			filename_str += File.separatorChar;
		}
		return filename_str;
	}

	public void explore()
	{
		if (!isDirectory())
			return;

		if (!isExplored())
		{
			File file = getFile();
			File[] children_file = file.listFiles();

			for(int i = 0; i < children_file.length; i++)
				add(new FileNode(children_file[i]));
			explored = true;
		}
	}
}
