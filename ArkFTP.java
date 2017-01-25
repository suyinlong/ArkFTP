/* ***********************
** ArkFTP.java
** ***********************
** 程序入口, 用于显示主窗口
** **********************/



import java.awt.Frame;
import ArkFTP.bin.ui.MainFrame;
import javax.swing.*;

public class ArkFTP
{
	public static void main(String[] args)
	{
		MainFrame appframe = new MainFrame();
		appframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		appframe.setExtendedState(Frame.MAXIMIZED_BOTH);
		appframe.setVisible(true);
	}
}
