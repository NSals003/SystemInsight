/** SysInsyte is a Graphical Application in Java designed to assist IT professionals in troubleshooting
In cases where remote access is not possible and where the end user may not know how to navigate throughout the OS.
It is compatible with most Linux distributions and modern Windows (10, 11, etc.)
Any possible additions are welcome as I'm relatively new to Java and programming as a whole :)
Created by: Noah Salsgiver */


// Import tools for GUI like Swing, AWT, and BufferedReader/InputStreamReader for reading program output
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
// Import libraries for OSHI
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

public class SysInsyte {
    // Cross-Platform Methods here
    // Makes a standalone method that will handle command running and output. Thread-Safe as well for future multithreading
    private String executeCommandGetOutput(String[] cmd) {
        try {
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputStream;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputStream = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputStream).append("\n");
            }
            bufferedReader.close();
            return stringBuffer.toString().trim();
        } catch (IOException exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Could not run " + Arrays.toString(cmd) + "!");
            return "Could not run " + Arrays.toString(cmd) + "!";
        }
    }
    // Detects Operating System
    public String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    // Displays Operating System and Mode to run in to user
    public void OSPopUpGUI() {
        JOptionPane.showMessageDialog(null, "You are Running " + getOperatingSystem() + "!" + "\nNow entering " + getOperatingSystem() + " Mode!");
    }

    public void UnsupportedOSPopUpGUI() {
        JOptionPane.showMessageDialog(null, "Your operating system " + getOperatingSystem() + " is not currently supported!");
    }

    private String getCPUInfoSimplified() {
        // Utilize OSHI library to obtain CPU info, intended for cross-compatibility with linux/windows mode.
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = si.getHardware();
        CentralProcessor processor = hardwareAbstractionLayer.getProcessor();
        ProcessorIdentifier procID = processor.getProcessorIdentifier();
        return procID.getName();
    }

    private String getDiskInfo(String mount) {
        // StringBuilder appends 3 lines read from File's output and a calculation to get the space used.
        StringBuilder partitionInfo = new StringBuilder();
        File file = new File(mount);
        if (file.exists() && file.isDirectory()) {
            long totalSpace = new File(mount).getTotalSpace();
            long usableSpace = new File(mount).getFreeSpace();
            long usedSpace = totalSpace - usableSpace;

            partitionInfo.append("Total Space: ").append(totalSpace / 1024 / 1024 / 1024).append(" GB\n");
            partitionInfo.append("Used Space: ").append(usedSpace / 1024 / 1024 / 1024).append(" GB\n");
            partitionInfo.append("Remaining Space: ").append(usableSpace / 1024 / 1024 / 1024).append(" GB\n");
        } else {
            return "Could not locate mount-point " + mount + "!";
        }
        return partitionInfo.toString().trim();
    }
    // obtains ram information using OSHI library
    private String getMemoryInformation() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        StringBuffer stringBuffer = new StringBuffer();

        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();

        stringBuffer.append("Total Memory: ").append(totalMemory / (1024 * 1024)).append(" MB");
        stringBuffer.append("\nAvailable Memory:  ").append(availableMemory / (1024 * 1024)).append(" MB");

        return stringBuffer.toString().trim();

    }
    // Linux only methods here
        // Creates main GUI for Linux mode
        public void CreateInsyteGUILinux() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Creates Frame which will be the parent component of the panel, which will hold the buttons and textAreas.
        JFrame frame = new JFrame();
        // Sets look and feel to the native OS's theme and not the default metal
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        // Defines the four buttons and their text
        JButton CPUbutton = new JButton("Get Advanced CPU Info");
        JButton OpenTerminalButton = new JButton("Open Installed Terminal Emulator in Current Directory");
        JButton FreespaceButton = new JButton("Advanced Drive Information");
        JButton updatePackageManagerButton = new JButton("Update system packages using " + getPackageManager());

        // Creates the panel with a BoxLayout which will hold everything on the frame centered on the Y Axis.
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Sets the alignment of each of the buttons to be in the center of the panel.
        CPUbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        OpenTerminalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        FreespaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updatePackageManagerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adds the buttons to the panel as well as a little barrier in between each button
        panel.add(CPUbutton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(FreespaceButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(OpenTerminalButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(updatePackageManagerButton);

        // Creates DE detection textArea, set to be non-editable
        JTextArea textArea = new JTextArea();
        textArea.setText("Desktop Envrioment: " + getTerminal().toUpperCase());
        textArea.setEditable(false);
        // Creates Distro detection textArea, set to be non-editable
        JTextArea textAreaOS = new JTextArea();
        textAreaOS.setText("Linux Distribution: " + getLinuxDistro(getLinuxDistro()) + "\n");
        textAreaOS.setEditable(false);
        // Creates Package Manager detection textArea, set to be non-editable
        JTextArea textAreaPM = new JTextArea();
        textAreaPM.setText("Package Manager: " + getPackageManager() + "\n");
        // Creates CPU detection textArea, set to be non-editable
        JTextArea textArea1 = new JTextArea();
        textArea1.setText("CPU: " + getCPUInfoSimplified() + "\n");
        textArea1.setEditable(false);
        // RAM information textarea
        JTextArea textAreaRAM = new JTextArea();
        textAreaRAM.setText(getMemoryInformation() + "\n");
        textAreaRAM.setEditable(false);
        // Currently only detects the root and home partitions, will probably iterate through a list of mount points in a future version
        JTextArea textArea2 = new JTextArea();
        textArea2.setText("Root Partition \n" + getDiskInfo("/"));
        textArea2.setEditable(false);
        JTextArea textArea3 = new JTextArea();
        textArea3.setText("\nHome Partition \n" + getDiskInfo("/home"));
        textArea3.setEditable(false);

        // adds the textAreas into the panel
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textAreaOS, BorderLayout.CENTER);
        panel.add(textAreaPM, BorderLayout.CENTER);
        panel.add(textArea1, BorderLayout.CENTER);
        panel.add(textAreaRAM, BorderLayout.CENTER);
        panel.add(textArea2, BorderLayout.CENTER);
        panel.add(textArea3, BorderLayout.CENTER);

        // Adds the panel to the parent component frame and gives it a title.
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("System Insight Utility");


        // Sets the appropriate frame size based on the components inside
        frame.pack();

        // Centers frame to screen.
        frame.setLocationRelativeTo(null);

        // Makes the frame visible ( So you can see the GUI :) )
        frame.setVisible(true);

        // Defined button actions here
        CPUbutton.addActionListener(event -> {
            String cpuInfo = getCPUInfo();
            JTextArea cpuTextArea = new JTextArea(10, 30);
            cpuTextArea.setText(cpuInfo);
            // Sets linewrap and wrapstyleWord so the textArea can be added to a scroll pane
            cpuTextArea.setLineWrap(true);
            cpuTextArea.setWrapStyleWord(true);
            cpuTextArea.setEditable(false);
            cpuTextArea.setCaretPosition(0);

            JScrollPane cpuScrollPane = new JScrollPane(cpuTextArea);
            JOptionPane.showMessageDialog(frame, cpuScrollPane, "CPU Information", JOptionPane.INFORMATION_MESSAGE);
        });
        OpenTerminalButton.addActionListener(event -> launchTerminal());
        FreespaceButton.addActionListener(event -> {
            String diskInfo = getDiskInfo();

            JTextArea diskTextArea = new JTextArea(10, 50);
            diskTextArea.setText(diskInfo);

            diskTextArea.setLineWrap(true);
            diskTextArea.setWrapStyleWord(true);
            diskTextArea.setEditable(false);
            diskTextArea.setCaretPosition(0);

            JScrollPane diskScrollPane = new JScrollPane(diskTextArea);
            JOptionPane.showMessageDialog(frame, diskScrollPane, "Partition information", JOptionPane.INFORMATION_MESSAGE);
        });
        updatePackageManagerButton.addActionListener(event -> {
            int response = JOptionPane.showConfirmDialog(frame, """
                    If you proceed, you will be prompted to enter your password through a separate graphical prompt.\

                    This is handled securely through polkit. This program does not receive or log your password.\
                                       \s
                    The program will hang until the system update is complete. Please do not try to close out of it! \

                    SysInsyte is not responsible for any system breakages as a result of you performing an update!\

                    Would you like to proceed?""", "Update Confirm Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                String packageUpdate = launchPackageManager(getPackageManager());

                JTextArea updateTextArea = new JTextArea(10, 50);
                updateTextArea.setText(packageUpdate);

                updateTextArea.setLineWrap(true);
                updateTextArea.setWrapStyleWord(true);
                updateTextArea.setEditable(false);
                updateTextArea.setCaretPosition(0);

                JScrollPane updateScrollPane = new JScrollPane(updateTextArea);
                JOptionPane.showMessageDialog(frame, updateScrollPane, "System Update Progress", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Update Cancelled... Press OK to go back to the program", "Update Cancel Prompt", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    private String getCPUInfo() {
      String[] cmd = {"lscpu"};
      return executeCommandGetOutput(cmd);
    }
    private String getTerminal() {
        // Retrieves desktop session via clever bash usage and returns output from bufferedreader. Intended only for linux mode
        String[] cmd = {"/bin/sh", "-c", "echo $DESKTOP_SESSION"};
        return executeCommandGetOutput(cmd);

    }
    private void launchTerminal() {
        // Identifies terminal using switch statement to detect desktop environment and therefore its default terminal.
        try {
            String desktopEnvironment = getTerminal().toLowerCase();
            String command = switch (desktopEnvironment) {
                case "gnome", "cinnamon", "gnome-classic" -> "gnome-terminal";
                case "xfce" -> "xfce4-terminal";
                case "kde" -> "konsole";
                case "mate" -> "mate-terminal";
                case "lxqt" -> "qterminal";
                case "lxde" -> "lxterminal";
                default -> "xterm";
            };
            new ProcessBuilder(command).start();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Could not open terminal!", exception);
        }
    }
    // Runs lsblk using processbuilder and stores it into a stringbuilder via bufferedreader
    private String getDiskInfo() {
      String[] cmd = {"lsblk"};
      return executeCommandGetOutput(cmd);
    }
    private String getLinuxDistro() {
        String[] cmd = {"/bin/sh", "-c", "cat /etc/os-release"};
        return executeCommandGetOutput(cmd);

    }
    private String getLinuxDistro(String distributionString) {
        // Trim output from the default function getLinuxDistro, so it only shows the distro
        String[] output = distributionString.split("\n");
        for (String i : output) {
            if (i.contains("NAME=")) {
                return i.replace("NAME=", "").trim();
            }
        }
        return "N/A";
    }
    private String getPackageManager() {
        String[] cmd = {"/bin/sh", "-c", "if command -v pacman >/dev/null 2>&1; then echo 'pacman'; elif command -v apt >/dev/null 2>&1; then echo 'apt'; elif command -v dnf >/dev/null 2>&1; then echo 'dnf'; else echo 'Unsupported Package Manager'; fi"};
        return executeCommandGetOutput(cmd);
    }
    private String launchPackageManager(String packageManager) {
        // Switch statement to identified system's package manager from getPackageManager and runs a root shell to update the system accordingly
        String packageManagerType;
        packageManagerType = switch (packageManager.toLowerCase()) {
            case "pacman" -> "pacman -Syu --noconfirm";
            case "apt" -> "apt update && apt upgrade -y";
            case "dnf" -> "dnf -y update && dnf -y upgrade";
            default -> "echo Your package manager is not supported!";
        };
        String[] cmd = {"/bin/sh", "-c", "pkexec /bin/sh -c \"" + packageManagerType + "\""};
        return executeCommandGetOutput(cmd);
    }
    // Windows only methods here
    public void CreateInsyteGUIWindows() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Creates Frame which will be the parent component of the panel, which will hold the buttons and textAreas.
        JFrame frame = new JFrame();
        // Sets look and feel to the native OS's theme and not the default metal
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        // Defines the four buttons and their text
        JButton CPUbutton = new JButton("Get Advanced CPU Info");
        JButton OpenCMDButton = new JButton("Open Command Prompt in Current Directory");
        JButton AdvancedDriveInformationWindows = new JButton("Advanced Drive Information");
        JButton GeneralWindowsSystemInfo = new JButton("Print General System Information");
        // Creates the panel with a BoxLayout which will hold everything on the frame centered on the Y Axis.
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Sets the alignment of each of the buttons to be in the center of the panel.
        CPUbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        OpenCMDButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        AdvancedDriveInformationWindows.setAlignmentX(Component.CENTER_ALIGNMENT);
        GeneralWindowsSystemInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adds the buttons to the panel as well as a little barrier in between each button
        panel.add(CPUbutton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(AdvancedDriveInformationWindows);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(OpenCMDButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(GeneralWindowsSystemInfo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Creates Windows version textArea, sets text to be non-editable.
        JTextArea textArea = new JTextArea();
        textArea.setText("Windows Version: " + getOperatingSystem());
        textArea.setEditable(false);
        // Creates Windows version number textArea, sets text to be non-editable.
        JTextArea textAreaVN = new JTextArea();
        textAreaVN.setText("Version number: " + getWindowsBuildVersion() + "\n");
        // Creates CPU info textArea, sets text to be non-editable.
        JTextArea textArea1 = new JTextArea();
        textArea1.setText("CPU: " + getCPUInfoSimplified() + "\n");
        textArea1.setEditable(false);
        // RAM information textarea
        JTextArea textAreaRAM = new JTextArea();
        textAreaRAM.setText(getMemoryInformation() + "\n");
        textAreaRAM.setEditable(false);

        // adds the textAreas into the panel
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textAreaVN, BorderLayout.CENTER);
        panel.add(textArea1, BorderLayout.CENTER);

        /* Since we're not sure how many drives there would be otherwise mounted, we use a list from our OSHI method
         to iterate through each detected mount point, and creates a textArea with the appropriate info.
         I may implement this for linux mode too. */
        List<String> mountPoints = getWindowsDisksOSHI();
        // For each mount in the list of mountPoints
        for (String mount : mountPoints) {
            JTextArea textAreaDisk = new JTextArea();
            textAreaDisk.setText("Mount Point: " + mount + "\n" + getDiskInfo(mount));
            textAreaDisk.setEditable(false);
            panel.add(textAreaDisk, BorderLayout.CENTER);
        }

        // Adds the panel to the parent component frame and gives it a title.
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("System Insight Utility");

        // Sets the appropriate frame size based on the components inside
        frame.pack();

        // Centers frame to screen.
        frame.setLocationRelativeTo(null);

        // Makes the frame visible ( So you can see the GUI :) )
        frame.setVisible(true);

        // Defined button actions here
        CPUbutton.addActionListener(event -> {
            String cpuInfo = getAdvancedCpuInfoWindows();
            JTextArea cpuTextArea = new JTextArea(30, 60);
            cpuTextArea.setText(cpuInfo);

            // Set line wrap and wrapstyleWord, so we can assign this to a scroll pane.
            cpuTextArea.setLineWrap(true);
            cpuTextArea.setWrapStyleWord(true);
            cpuTextArea.setEditable(false);
            cpuTextArea.setCaretPosition(0);

            JScrollPane cpuScrollPane = new JScrollPane(cpuTextArea);
            JOptionPane.showMessageDialog(frame, cpuScrollPane, "CPU Information", JOptionPane.INFORMATION_MESSAGE);
        });
        OpenCMDButton.addActionListener(event -> {
            int response = JOptionPane.showConfirmDialog(frame, """
                    Would you like to run command prompt with Administrative Privileges?
                    Selecting no will open a standard user command prompt instead. Be cautious when running command prompt as Admin!
                    SysInsyte is not responsible if you mess anything up with this!""", "Command Prompt Confirm Prompt", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                launchCMDWindows(response);

            } else if (response == JOptionPane.NO_OPTION) {
                launchCMDWindows(response);

            } else {
                JOptionPane.showMessageDialog(frame, "Cancelled command prompt... select OK to go back to main menu", "Cancelled Command Prompt", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        AdvancedDriveInformationWindows.addActionListener(event -> {
            String diskInfo = getAdvancedDiskInfoWindows();

            JTextArea diskTextArea = new JTextArea(30, 50);
            diskTextArea.setText(diskInfo);

            diskTextArea.setLineWrap(true);
            diskTextArea.setWrapStyleWord(true);
            diskTextArea.setEditable(false);
            diskTextArea.setCaretPosition(0);

            JScrollPane diskScrollPane = new JScrollPane(diskTextArea);
            JOptionPane.showMessageDialog(frame, diskScrollPane, "Partition information", JOptionPane.INFORMATION_MESSAGE);
        });
        GeneralWindowsSystemInfo.addActionListener(event -> {
            String systemInfo = getWindowsGeneralInformation();

            JTextArea systemInfoTextArea = new JTextArea(30, 60);
            systemInfoTextArea.setText(systemInfo);

            systemInfoTextArea.setLineWrap(true);
            systemInfoTextArea.setWrapStyleWord(true);
            systemInfoTextArea.setEditable(false);
            systemInfoTextArea.setCaretPosition(0);

            JScrollPane systemInfoScrollPane = new JScrollPane(systemInfoTextArea);
            JOptionPane.showMessageDialog(frame, systemInfoScrollPane, "General System Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    private String getAdvancedCpuInfoWindows() {
        // Uses annoying windows commands to get the CPU information and stores it in stringBuilder from bufferedreader
        String[] cmd = {"wmic", "cpu", "get", "caption,deviceid,name,numberofcores,maxclockspeed,status"};
        return executeCommandGetOutput(cmd);
    }
    private String getAdvancedDiskInfoWindows() {
        String[] cmd1 = {"wmic", "partition", "get", "Name,Size,Type"};
        String[] cmd2 = {"wmic", "diskdrive", "get", "model,manufacturer,size,mediaType"};
        String outputPartition = executeCommandGetOutput(cmd1);
        String outputDisks = executeCommandGetOutput(cmd2);
        return outputPartition + "\n" + outputDisks;


    }
    private void launchCMDWindows(int runAsAdmin) {
        // Detects the given input to see whether to run the command prompt as admin, and changes cmd accordingly
        String[] cmd = {""};
        if (runAsAdmin == 0) {
            cmd = new String[]{"powershell", "-Command", "Start-Process cmd -Verb RunAs"};
        } else if (runAsAdmin == 1) {
            cmd = new String[]{"powershell", "-Command", "Start-Process cmd"};
        }
        // Attempts to run the command to open command prompt
        try {
            new ProcessBuilder(cmd).start();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Unable to open Command Prompt!");
        }
    }
    private String getWindowsGeneralInformation() {
            // Uses stringbuilder to add line-by-line the output of the systeminfo command using bufferedreader.
            String[] cmd = {"systeminfo"};
            return executeCommandGetOutput(cmd);
    }

    private String getWindowsBuildVersion() {
        // Since only one line will be printed, no StringBuilder is needed here
        // Uses powershell to filter output, akin to using grep but a little more complex and hard to understand. We don't need the version header
        String[] cmd = {"powershell", "-Command", "\"(& {wmic os get Version | Select-String -Pattern '^[0-9]+'}).Line\""};
        return executeCommandGetOutput(cmd);
    }
    private List<String> getWindowsDisksOSHI() {
        // Creates an ArrayList at mountPoints, iterates through each of the detected mounted filesystems and adds them to the list.
        List<String> mountPoints = new ArrayList<>();
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        FileSystem fileSystem = os.getFileSystem();
        // This below list contains the details we need, so we iterate through it to get what we need and put it into mountPoints.
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        for (OSFileStore fileStore : fileStores) {
            mountPoints.add(fileStore.getMount());
        }
        return mountPoints;
    }
}

