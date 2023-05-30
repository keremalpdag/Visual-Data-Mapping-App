import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class FileUploadScreen extends JFrame {

    private JPanel panel1;
    private JButton file1Button;
    private JButton file2Button;
    private JLabel title, file1Label, file2Label;
    private JButton startButton;
    private JButton previewFileButton1;
    private JButton previewFileButton2;
    private JButton aboutButton;
    private File file1, file2;

    public FileUploadScreen() {
        add(panel1);
        setSize(850, 450);
        setTitle("Data Mapping Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buttonBuilder(startButton);
        buttonBuilder(previewFileButton1);
        buttonBuilder(previewFileButton2);
        buttonBuilder(aboutButton);
        buttonBuilder(file1Button);
        buttonBuilder(file2Button);

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(UIManager.getColor("control"));
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(UIManager.getColor("control"));
            }
        };

        startButton.addMouseListener(mouseListener);
        file1Button.addMouseListener(mouseListener);
        file2Button.addMouseListener(mouseListener);
        previewFileButton1.addMouseListener(mouseListener);
        previewFileButton2.addMouseListener(mouseListener);
        aboutButton.addMouseListener(mouseListener);

        file1Button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION && isXMLFile(fileChooser.getSelectedFile().getPath())) {
                file1 = fileChooser.getSelectedFile();
                file1Label.setText("File 1: " + file1.getName());
            }
            else{
                System.out.println("Please provide an xml file");
                JOptionPane.showMessageDialog(panel1, "Please provide an xml file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        file2Button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION && isXMLFile(fileChooser.getSelectedFile().getPath())) {
                file2 = fileChooser.getSelectedFile();
                file2Label.setText("File 2: " + file2.getName());
            }
            else{
                System.out.println("Please provide an xml file");
                JOptionPane.showMessageDialog(panel1, "Please provide an xml file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        startButton.addActionListener(e -> {
            System.out.println("deneme");
            if (file1 != null && file2 != null) {
                System.out.println("deneme1");
                XmlFile xmlFile1 = new XmlFile(file1);
                XmlFile xmlFile2 = new XmlFile(file2);
                if(!xmlFile1.exceptionThrown && !xmlFile2.exceptionThrown) {
                    MappingScreen mappingScreen = new MappingScreen(xmlFile1, xmlFile2);
                    mappingScreen.init();
                }
                else{
                    if(xmlFile1.exceptionType == 0 || xmlFile2.exceptionType == 0){
                        String errorMessage = "Failed to configure XML parser.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (xmlFile1.exceptionType == 1 || xmlFile2.exceptionType == 1){
                        String errorMessage = "Invalid XML file format. Please check the files and try again.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(xmlFile1.exceptionType == 2 || xmlFile2.exceptionType == 2){
                        String errorMessage = "Failed to read the XML files. Please ensure the files exists and are accessible.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        String errorMessage = "An error occurred while initiating the mapping. Please check your XML files.";
                        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel1, "Please select files.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        previewFileButton1.addActionListener(e -> {
            if(file1 != null){
                System.out.println("Preview 1");
                XmlFile xmlFile = new XmlFile(file1);
                if(!xmlFile.exceptionThrown) {
                    PreviewFile previewFile = new PreviewFile(xmlFile, 1);
                    previewFile.showPreview();
                }
                else {
                    if(xmlFile.exceptionType == 0){
                        String errorMessage = "Failed to configure XML parser.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(xmlFile.exceptionType == 1){
                        String errorMessage = "Invalid XML file format. Please check the file and try again.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(xmlFile.exceptionType == 2){
                        String errorMessage = "Failed to read the XML file. Please ensure the file exists and is accessible.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        String errorMessage = "An unknown error occurred while initiating the preview. Please check your XML file.";
                        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else{
                JOptionPane.showMessageDialog(panel1, "You didn't select a file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        previewFileButton2.addActionListener(e -> {
            if(file2 != null){
                System.out.println("Preview 2");
                XmlFile xmlFile = new XmlFile(file2);
                if(!xmlFile.exceptionThrown) {
                    PreviewFile previewFile = new PreviewFile(xmlFile, 2);
                    previewFile.showPreview();
                }
                else{
                    if(xmlFile.exceptionType == 0){
                        String errorMessage = "Failed to configure XML parser.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(xmlFile.exceptionType == 1){
                        String errorMessage = "Invalid XML file format. Please check the file and try again.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(xmlFile.exceptionType == 2){
                        String errorMessage = "Failed to read the XML file. Please ensure the file exists and is accessible.";
                        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        String errorMessage = "An unknown error occurred while initiating the preview. Please check your XML file.";
                        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else{
                JOptionPane.showMessageDialog(panel1, "You didn't select a file.", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        aboutButton.addActionListener(e -> {
            AboutScreen aboutScreen = new AboutScreen();
            aboutScreen.setVisible(true);
        });
    }

    public void buttonBuilder(JButton button) {
        button.setBackground(Color.white);
        button.setForeground(new Color(2, 83, 255));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusable(false);
        button.setBorderPainted(false);
    }

    public boolean isXMLFile(String filePath){
        return filePath.endsWith(".xml") || filePath.endsWith(".XML");
    }

}
