/* ***********************
** ArkFTP.java
** ***********************
** �������, ������ʾ������
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
