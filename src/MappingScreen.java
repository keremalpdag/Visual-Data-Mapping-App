import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class MappingScreen extends JPanel {
    private XmlFile leftXmlFile;
    private XmlFile rightXmlFile;
    private List<MappingElement> leftMappingElements;
    private List<MappingElement> rightMappingElements;
    private List<MappingLine> mappingLines;
    private MappingElement selectedElement;
    private Point mousePoint;
    private Map<MappingElement, MappingElement> mapping;

    public MappingScreen(XmlFile leftXmlFile, XmlFile rightXmlFile) {
        this.leftXmlFile = leftXmlFile;
        this.rightXmlFile = rightXmlFile;
        this.leftMappingElements = new ArrayList<>();
        this.rightMappingElements = new ArrayList<>();
        this.mappingLines = new ArrayList<>();
        this.selectedElement = null;
        this.mousePoint = new Point();
        this.mapping = new HashMap<>();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (MappingElement mappingElement : leftMappingElements) {
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        selectedElement = mappingElement;
                        break;
                    }
                }

                if (selectedElement != null) {
                    mousePoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedElement != null) {
                    for (MappingElement mappingElement : rightMappingElements) {
                        if (mappingElement.withinBounds(e.getX(), e.getY())) {
                            addMapping(selectedElement, mappingElement);
                            break;
                        }
                    }

                    selectedElement = null;
                    mousePoint = new Point();
                }
            }
        });
    }

    public void init() {
        int leftX = 10;
        int leftY = 10;
        int rightX = 500;
        int rightY = 10;

        for (XmlElement element : leftXmlFile.getElements()) {
            MappingElement mappingElement = new MappingElement(element, leftX, leftY, 200, 30);
            leftMappingElements.add(mappingElement);
            if(element.getChildren() != null){
                for (int i = 0; i < element.getChildren().size(); i++) {
                    leftY += 35;
                    MappingElement mappingElementChild = new MappingElement(element.getChildren().get(i), leftX, leftY, 150, 30);
                    leftMappingElements.add(mappingElementChild);
                }
            }
            leftY += 40;
        }

        for (XmlElement element : rightXmlFile.getElements()) {
            MappingElement mappingElement = new MappingElement(element, rightX, rightY, 200, 30);
            rightMappingElements.add(mappingElement);
            if(element.getChildren() != null){
                for(int i = 0; i < element.getChildren().size(); i++){
                    rightY += 35;
                    MappingElement mappingElementChild = new MappingElement(element.getChildren().get(i), rightX, rightY, 150, 30);
                    rightMappingElements.add(mappingElementChild);
                }
            }
            rightY += 40;
        }

        setPreferredSize(new Dimension(800, Math.max(leftY, rightY) + 10));

        JFrame frame = new JFrame("Mapping Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                rightXmlFile.saveToFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            frame.dispose();
        });
        saveButton.addMouseListener(new MouseListener() {
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
        saveButton.setBackground(Color.white);
        saveButton.setForeground(new Color(2, 83, 255));
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setFocusable(false);
        saveButton.setBorderPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(this, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (MappingElement mappingElement : leftMappingElements) {
            mappingElement.draw(g);
        }

        for (MappingElement mappingElement : rightMappingElements) {
            mappingElement.draw(g);
        }

        for (MappingLine mappingLine : mappingLines) {
            mappingLine.draw(g);
        }

        if (selectedElement != null) {
            g.setColor(Color.RED);
            g.drawLine(selectedElement.getX() + selectedElement.getWidth(),
                    selectedElement.getY() + selectedElement.getHeight() / 2,
                    mousePoint.x, mousePoint.y);
        }
    }

    private void addMapping(MappingElement leftElement, MappingElement rightElement) {
        mapping.put(leftElement, rightElement);
        MappingLine mappingLine = new MappingLine(leftElement, rightElement);
        mappingLines.add(mappingLine);
        repaint();
    }
    public Map<MappingElement, MappingElement> getMapping() {
        return mapping;
    }
}