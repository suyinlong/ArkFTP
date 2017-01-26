/* ***********************
** AboutDialog.java
** ***********************
** About Dialog
** Build 0718
** **********************/

package ArkFTP.bin.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AboutDialog extends JDialog
{
	Timer AnimationTimer;
	int Playtime;
	int DialogX, DialogY, DialogW, DialogH;
	int ScreenW, ScreenH;

	public AboutDialog(JFrame owner)
	{
		super(owner, StringTable.dialogAboutTitle, true);

		this.setLayout(null);

		JLabel TitleName = new JLabel(StringTable.projectName);
		TitleName.setBounds(10, 20, 200, 25);
		this.add(TitleName);

		JLabel[] AboutInfoLabel = new JLabel[StringTable.dialogAboutInfo.length];;
		for (int i = 0; i < StringTable.dialogAboutInfo.length; i++)
		{
			AboutInfoLabel[i] = new JLabel(StringTable.dialogAboutInfo[i]);
			AboutInfoLabel[i].setBounds(10, 70 + 20 * i, 230, 18);
			this.add(AboutInfoLabel[i]);
		}

		JButton ok = new JButton(StringTable.buttonOK);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
					setVisible(false);
			}
		});
		ok.setBounds(75, 150, 70, 20);
		this.add(ok);

		DialogW = 250; 	DialogH = 270;
		setSize(DialogW, DialogH);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		ScreenW = d.width; ScreenH = d.height;
		DialogX = ScreenW / 2 - DialogW / 2;
		DialogY = ScreenH / 2 - DialogH / 2;
		this.setLocation( - DialogW, - DialogH);

	}
	public void StartAnimation()
	{
        this.setLocation(DialogX, DialogY);
        /*
		Playtime = 0;
        this.setLocation( - DialogW, - DialogH);
		AnimationTimer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Playtime += 10;
				int x = - DialogW + (DialogX + DialogW) * Playtime / 500;
				int y = ScreenH  - (ScreenH - DialogH) * Playtime /500;
				AboutDialog.this.setLocation(x, y);
				if (Playtime == 500) AnimationTimer.stop();
			}
		});
		AnimationTimer.setInitialDelay(0);
		AnimationTimer.setCoalesce(true);
		AnimationTimer.start();
        */

	}

}
