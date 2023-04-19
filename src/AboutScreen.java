import javax.swing.*;
import java.awt.*;

public class AboutScreen extends JFrame {

    public AboutScreen() {
        setTitle("About Data Mapping Editor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Data Mapping Editor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("About", createAboutPanel());
        tabbedPane.addTab("How to Use", createHowToUsePanel());
        tabbedPane.addTab("Developer Info", createDeveloperInfoPanel());

        mainPanel.add(tabbedPane);

        getContentPane().add(mainPanel);
    }

    private JScrollPane createAboutPanel() {
        JTextArea aboutTextArea = new JTextArea();
        aboutTextArea.setEditable(false);
        aboutTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        aboutTextArea.setLineWrap(true);
        aboutTextArea.setWrapStyleWord(true);
        aboutTextArea.setText("""
                The Data Mapping Editor is an application that allows users to map XML data from two different files. It provides an intuitive graphical interface to create, edit, and visualize the mapping between the elements of the XML files.""");

        JScrollPane scrollPane = new JScrollPane(aboutTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private JScrollPane createHowToUsePanel() {
        JTextArea howToUseTextArea = new JTextArea();
        howToUseTextArea.setEditable(false);
        howToUseTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        howToUseTextArea.setLineWrap(true);
        howToUseTextArea.setWrapStyleWord(true);
        howToUseTextArea.setText("""
                1. Click the 'File 1' and 'File 2' buttons to select the XML files you want to map.
                2. You can preview the contents of the selected files by clicking the 'Preview' buttons.
                3. Click the 'Start' button to open the mapping editor.
                4. In the mapping editor, click and drag from an element in one file to an element in the other file to create a mapping line.
                5. To delete a mapping line, click on it and press the 'Delete' key on your keyboard.
                6. You can navigate the tree structure of the XML files by following the indentations or the arrow icons next to the elements.
                7. To save your mapping, click the 'Save' button in the mapping editor.
                8. To load a previously saved mapping, click the 'Load' button in the mapping editor.""");

        JScrollPane scrollPane = new JScrollPane(howToUseTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private JScrollPane createDeveloperInfoPanel() {
        JTextArea developerInfoTextArea = new JTextArea();
        developerInfoTextArea.setEditable(false);
        developerInfoTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        developerInfoTextArea.setLineWrap(true);
        developerInfoTextArea.setWrapStyleWord(true);
        developerInfoTextArea.setText("""
                Name: Kerem Alpdag
                Number: 20190702065""");

        JScrollPane scrollPane = new JScrollPane(developerInfoTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}