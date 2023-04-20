import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PreviewFile {
    private final XmlFile xmlFile;
    private final int fileNumber;

    public PreviewFile(XmlFile xmlFile, int fileNumber) {
        this.xmlFile = xmlFile;
        this.fileNumber = fileNumber;
    }

    public void showPreview() {
        // Create a new JFrame to display the preview
        JFrame frame = new JFrame("Preview File " + fileNumber);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

        // Create a JLabel to display the file name
        JLabel fileNameLabel = new JLabel("File: " + xmlFile.getFileName(), SwingConstants.CENTER);
        fileNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create a JTextArea to display the preview text
        JTextArea previewTextArea = new JTextArea();
        previewTextArea.setEditable(false);
        previewTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        previewTextArea.setBackground(new Color(240, 240, 240));
        previewTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create a JScrollPane to wrap the preview text area
        JScrollPane scrollPane = new JScrollPane(previewTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a JPanel to hold the preview text area and scroll pane
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(scrollPane, BorderLayout.CENTER);
        previewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a JButton to close the preview window
        JButton closeButton = new JButton("Close Preview");
        closeButton.addActionListener(e -> frame.dispose());
        closeButton.addMouseListener(new MouseListener() {
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
        });
        closeButton.setBackground(Color.white);
        closeButton.setForeground(new Color(2, 83, 255));
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);

        // Create a JPanel to hold the close button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        buttonPanel.setBackground(new Color(2, 83, 255));

        // Add the panels to the frame
        frame.getContentPane().add(fileNameLabel, BorderLayout.NORTH);
        frame.getContentPane().add(previewPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Generate the preview text
        String previewText = xmlFile.previewFile();

        // Set the preview text in the text area
        previewTextArea.setText(previewText);

        // Set the caret position to the beginning of the text area
        previewTextArea.setCaretPosition(0);

        // Pack the frame and display it
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

