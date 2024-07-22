import javax.swing.*;
public class SysInsyteMain {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Assigns Instance of SysInsyte Object to SystemInsightRUn
        SysInsyte SystemInsightRun = new SysInsyte();
        // Detects Operating System
        String OS = SystemInsightRun.getOperatingSystem();
        // A little bit of logic to see which mode to use, or whether it's supported at all.
        SystemInsightRun.OSPopUpGUI();
        switch (OS) {
            case "Windows 10":
            case "Windows 11":
                SystemInsightRun.CreateInsyteGUIWindows();
                break;
            case "Linux":
                SystemInsightRun.CreateInsyteGUILinux();
                break;
            default:
                SystemInsightRun.UnsupportedOSPopUpGUI();
                break;
        }


    }
}