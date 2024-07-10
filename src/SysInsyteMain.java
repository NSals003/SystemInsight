import javax.swing.*;

public class SysInsyteMain
{
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Assigns Instance of SysInsyte Object to SystemInsightRUn
        SysInsyte SystemInsightRun = new SysInsyte();

        // Detects Operating System
        String OS = SystemInsightRun.getOperatingSystem();

        SystemInsightRun.OSPopUpGUI();
        SystemInsightRun.CreateInsyteGUILinux();


    }
}
