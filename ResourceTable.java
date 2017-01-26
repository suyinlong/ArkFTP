/*
* @Author: Yinlong Su
* @Date:   2017-01-25 09:12:32
* @Last Modified by:   Yinlong Su
* @Last Modified time: 2017-01-25 20:13:07
*/

package ArkFTP.bin.ui;

import java.awt.*;

public class ResourceTable {

    public static Font fontMainFrame;
    public static Font fontLog;

    public final static String fontMainFrameName = "Dialog";
    public final static String fontLogName = "Consolas";

    public final static String cursorName = "Arror";
    public final static String cursorPath = "ArkFTP/res/Arrow.png";

    public final static Color colorPanelBackground = new Color(240, 240, 240);
    public final static Color colorTableBackground = new Color(255, 255, 255);
    public final static Color colorTableForeground = new Color(0, 0, 0);
    public final static Color colorTableHeaderBackground = new Color(252, 252, 252);
    public final static Color colorTableSelectionBackground = new Color(51, 153, 255);
    public final static Color colorTableSelectionForeground = new Color(255, 255, 255);
    public final static Color colorComboBoxBackground = new Color(255, 255, 255);
    public final static Color colorLogBackground = new Color(255, 255, 255);
    public final static Color colorMenuBackground = new Color(240, 240, 240);
    public final static Color colorToolbarBackground = new Color(240, 240, 240);
    public final static Color colorToolbarFocusBackground = new Color(218, 235, 252);
    public final static Color colorToolbarFocusBorder = new Color(51, 153, 255);
    public final static Color colorStateBarBackground = new Color(255, 255, 255);
    public final static Color colorDialogBackground = new Color(240, 240, 240);
    public final static Color colorButtonBackground = new Color(229, 229, 229);
    public final static Color colorScrollBackground = new Color(255, 255, 255);
    public final static Color colorFieldBackground = new Color(240, 240, 240);
    public final static Color colorFieldFocusBackground = new Color(255, 255, 255);

    public final static String iconTray = "/ArkFTP/res/MainIcon.png";

    public final static String iconDir = "ArkFTP/res/dirIcon.png";
    public final static String iconFile = "ArkFTP/res/fileIcon.png";

    public final static String iconMenuFileConnectTo = "ArkFTP/res/MenuConnectTo.png";
    public final static String iconMenuFileDisconnect = "ArkFTP/res/MenuDisconnect.png";
    public final static String iconMenuFileMinimize = "ArkFTP/res/MenuSystemTray.png";
    public final static String iconMenuFileExit = "ArkFTP/res/MenuExit.png";

    public final static String iconMenuToolsSiteManager = "ArkFTP/res/MenuServerManager.png";
    public final static String iconMenuToolsLog = "ArkFTP/res/MenuConnectLog.png";

    public final static String iconMenuHelpAbout = "ArkFTP/res/MenuAbout.png";

    public final static String iconPopupMenuServerDownload = "ArkFTP/res/MenuDownload.png";
    public final static String iconPopupMenuServerSaveAs = "ArkFTP/res/MenuSaveas.png";
    public final static String iconPopupMenuServerDelete = "ArkFTP/res/MenuDelete.png";
    public final static String iconPopupMenuServerRename = "ArkFTP/res/MenuRename.png";
    public final static String iconPopupMenuServerNewFolder = "ArkFTP/res/MenuNewfolder.png";
    public final static String iconPopupMenuServerRefresh = "ArkFTP/res/MenuRefresh.png";

    public final static String iconPopupMenuLocalUpload = "ArkFTP/res/MenuUpload.png";
    public final static String iconPopupMenuLocalRename = "ArkFTP/res/MenuRename.png";
    public final static String iconPopupMenuLocalNewFolder = "ArkFTP/res/MenuNewfolder.png";
    public final static String iconPopupMenuLocalDelete = "ArkFTP/res/MenuDelete.png";
    public final static String iconPopupMenuLocalRefresh = "ArkFTP/res/MenuRefresh.png";

    public final static String iconToolbarConnect = "ArkFTP/res/ConnectTo.png";
    public final static String iconToolbarDisconnect = "ArkFTP/res/Disconnect.png";
    public final static String iconToolbarSiteManager = "ArkFTP/res/ServerManager.png";
    public final static String iconToolbarLog = "ArkFTP/res/ConnectLog.png";
    public final static String iconToolbarMinimize = "ArkFTP/res/SystemTray.png";

    public final static String iconDialogConnectionLogButtonOkay = "ArkFTP/res/Okay.png";
    public final static String iconDialogConnectionLogButtonCopyLog = "ArkFTP/res/CopyLog.png";
    public final static String iconDialogConnectionLogButtonSaveLog = "ArkFTP/res/SaveLog.png";

    public static void loadFontMainFrame() {
        Font _fontMainFrame = null;
        boolean loadFont = true;
        try {
            _fontMainFrame = new Font(ResourceTable.fontMainFrameName, Font.PLAIN, 12);
        }
        catch (Exception e) {
            loadFont = false;
        }
        if (loadFont == true)
            fontMainFrame = _fontMainFrame;
        else
            fontMainFrame = null;
    }

    public static void loadFontLog() {
        Font _fontLog = null;
        boolean loadFont = true;
        try {
            _fontLog = new Font(ResourceTable.fontLogName, Font.PLAIN, 12);
        }
        catch (Exception e) {
            loadFont = false;
        }
        if (loadFont == true)
            fontLog = _fontLog;
        else
            fontLog = null;
    }
}