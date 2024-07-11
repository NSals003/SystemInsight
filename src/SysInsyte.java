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
import java.util.logging.Level;
import java.util.logging.Logger;

// Import libraries for OSHI
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.hardware.HardwareAbstractionLayer;

public class SysInsyte {

    // Creates the main Utility GUI Frame for Linux Mode
    public void CreateInsyteGUILinux() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame();

        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());


        JButton CPUbutton = new JButton("Get Advanced CPU Info");
        JButton OpenTerminalButton = new JButton("Open Installed Terminal Emulator in Current Directory");
        JButton FreespaceButton = new JButton("Advanced Drive Information");
        JButton updatePackageManagerButton = new JButton("Update system packages using " + getPackageManager());

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        CPUbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        OpenTerminalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        FreespaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updatePackageManagerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(CPUbutton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(FreespaceButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(OpenTerminalButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(updatePackageManagerButton);

        JTextArea textArea = new JTextArea();
        textArea.setText("Desktop Envrioment: " + getTerminal().toUpperCase());
        textArea.setEditable(false);

        JTextArea textAreaOS = new JTextArea();
        textAreaOS.setText("Linux Distribution: " + getLinuxDistro(getLinuxDistro()) + "\n");
        textAreaOS.setEditable(false);

        JTextArea textAreaPM = new JTextArea();
        textAreaPM.setText("Package Manager: " + getPackageManager() + "\n");

        JTextArea textArea1 = new JTextArea();
        textArea1.setText("CPU: " + getCPUInfoSimplified() + "\n");
        textArea1.setEditable(false);

        JTextArea textArea2 = new JTextArea();
        textArea2.setText("Root Partition \n" + getDiskInfo("/"));
        textArea2.setEditable(false);

        JTextArea textArea3 = new JTextArea();
        textArea3.setText("\nHome Partition \n" + getDiskInfo("/home"));
        textArea3.setEditable(false);


        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textAreaOS, BorderLayout.CENTER);
        panel.add(textAreaPM, BorderLayout.CENTER);
        panel.add(textArea1, BorderLayout.CENTER);
        panel.add(textArea2, BorderLayout.CENTER);
        panel.add(textArea3, BorderLayout.CENTER);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("System Insight Utility");


        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        CPUbutton.addActionListener(_ -> {
            String cpuInfo = getCPUInfo();
            // Sets a dedicated Text Area which will
            JTextArea cpuTextArea = new JTextArea(10, 30);
            cpuTextArea.setText(cpuInfo);

            cpuTextArea.setLineWrap(true);
            cpuTextArea.setWrapStyleWord(true);
            cpuTextArea.setEditable(false);
            cpuTextArea.setCaretPosition(0);

            JScrollPane cpuScrollPane = new JScrollPane(cpuTextArea);
            JOptionPane.showMessageDialog(frame, cpuScrollPane, "CPU Information", JOptionPane.INFORMATION_MESSAGE);
        });
        OpenTerminalButton.addActionListener(_ -> launchTerminal());
        FreespaceButton.addActionListener(_ -> {
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
        updatePackageManagerButton.addActionListener(_ -> {
            int response = JOptionPane.showConfirmDialog(frame, """
                    If you proceed, you will be prompted to enter your password through a separate graphical prompt.\

                    This is handled securely through polkit. This program does not receive or log your password.\
                                       \s
                    The program will hang until the system update is complete. Please do not try to close out of it! \

                    SysInsyte is not responsible for any system breakages as a result of you performing an update!\

                    Would you like to proceed?""", "Update Confirm Prompt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                String packageUpdate = updatePackageManager(getPackageManager());

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
// Cross-Platform Methods here

    // Detects Operating System
    public String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    // Displays Operating System and Mode to run in to user
    public void OSPopUpGUI() {
        JOptionPane.showMessageDialog(null, "You are Running " + getOperatingSystem() + "!" + "\nNow entering " + getOperatingSystem() + " Mode!");
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

    // Linux only methods here
    private String getCPUInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Creates ProcessBuilder Instance to launch lscpu command
            ProcessBuilder processBuilder = new ProcessBuilder("lscpu");
            Process lscpuProcess = processBuilder.start();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lscpuProcess.getInputStream()));

            String lineOutputStorer;
            while ((lineOutputStorer = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineOutputStorer).append("\n");
            }
            bufferedReader.close();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Error! Could not retrieve CPU information! Is lscpu installed or accessible?", exception);
            return "Error: Could not retrieve CPU Information! Is lscpu installed or accessible?";
        }
        return stringBuilder.toString().trim();
    }

    private String getTerminal() {
        // Retrieves desktop session via clever bash usage and returns output from bufferedreader. Intended only for linux mode
        try {
            String[] cmd = {"/bin/sh", "-c", "echo $DESKTOP_SESSION"};
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return bufferedReader.readLine();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Error! Could not retrieve Desktop Session!", exception);
            return "Error: Could not retrieve Desktop Session.";
        }
    }

    private void launchTerminal() {
        // Identifies terminal using switch statement to detect desktop environment and therefore its default terminal.
        try {
            String desktopEnvironment = getTerminal().toLowerCase();
            String command = switch (desktopEnvironment) {
                case "gnome", "cinnamon" -> "gnome-terminal";
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

    @org.jetbrains.annotations.NotNull
    // Runs lsblk using processbuilder and stores it into a stringbuilder via bufferedreader
    private String getDiskInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("lsblk");
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String lineOutput;
            while ((lineOutput = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineOutput).append("\n");
            }
            bufferedReader.close();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Could not retrieve partition/disk info!", exception);
            return "Could not retrieve partition/disk info";
        }
        return stringBuilder.toString().trim();
    }

    private String getLinuxDistro() {
        try {
            String[] cmd = {"/bin/sh", "-c", "cat /etc/os-release"};
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return bufferedReader.readLine().trim();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Error! Could not retrieve OS type!", exception);
            return "Error! Could not retrieve OS type!";
        }
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
        try {
            // This bash line will determine if the package manager is apt, pacman, dnf, or unsupported (We don't like portage or nixpkgs!!!)
            String[] cmd = {"/bin/sh", "-c", "if command -v pacman >/dev/null 2>&1; then echo 'pacman'; elif command -v apt >/dev/null 2>&1; then echo 'apt'; elif command -v dnf >/dev/null 2>&1; then echo 'dnf'; else echo 'Unsupported Package Manager'; fi"};
            // Runs a process that runs that long bash command and then reads it with bufferedreader, returning the line read.
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return bufferedReader.readLine().trim().toLowerCase();
        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Could not retrieve package manager", exception);
            return "Could not retrieve package manager!";
        }
    }

    private String updatePackageManager(String packageManager) {
        // Since more than one line has to be printed out, Bufferedreader will read the stream from method getProcess and store it in a stringbuffer.
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process process = getProcess(packageManager);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String lineoutput;
            while ((lineoutput = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineoutput).append("\n");
            }

        } catch (Exception exception) {
            Logger.getLogger(SysInsyte.class.getName()).log(Level.SEVERE, "Error! Could not update system!", exception);
            return "Error! Could not update system!";
        }
        return stringBuilder.toString().trim();
    }

    private static Process getProcess(String packageManager) throws IOException {
        // Switch statement to identified system's package manager from getPackageManager and runs a root shell to update the system accordingly
        String packageManagerType;
        packageManagerType = switch (packageManager.toLowerCase()) {
            case "pacman" -> "pacman -Syu --noconfirm";
            case "apt" -> "apt update && apt upgrade -y";
            case "dnf" -> "dnf -y update && dnf -y upgrade";
            default -> "echo Your package manager is not supported!";
        };
        String[] cmd = {"/bin/sh", "-c", "pkexec /bin/sh -c \"" + packageManagerType + "\""};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        return processBuilder.start();
    }

    // Windows only methods here
}