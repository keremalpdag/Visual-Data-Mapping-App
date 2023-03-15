import java.awt.*;
import java.awt.event.*;
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
                    if (mappingElement.isToggleIconClicked(e.getX(), e.getY())) {
                        mappingElement.toggleCollapsed();
                        repaint();
                        return;
                    }
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        selectedElement = mappingElement;
                        break;
                    }
                }

                if (selectedElement == null) {
                    for (MappingElement mappingElement : rightMappingElements) {
                        if (mappingElement.isToggleIconClicked(e.getX(), e.getY())) {
                            mappingElement.toggleCollapsed();
                            repaint();
                            return;
                        }
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
                            updateElementValue(selectedElement, mappingElement);
                            addMapping(selectedElement, mappingElement);
                            break;
                        }
                    }

                    selectedElement = null;
                    mousePoint = new Point();
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (MappingElement mappingElement : leftMappingElements) {
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        mappingElement.setColor(new Color(72, 168, 197));
                    } else {
                        mappingElement.setColor(Color.BLUE);
                    }
                }

                for (MappingElement mappingElement : rightMappingElements) {
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        mappingElement.setColor(new Color(72, 168, 197));
                    } else {
                        mappingElement.setColor(Color.BLUE);
                    }
                }

                repaint();

                autoScroll(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                autoScroll(e);
            }
        });
    }

    private void autoScroll(MouseEvent e) {
        JScrollPane scrollPane = findScrollPane(MappingScreen.this);
        if (scrollPane != null) {
            JViewport viewport = scrollPane.getViewport();
            Point viewPosition = viewport.getViewPosition();
            Dimension extentSize = viewport.getExtentSize();

            int scrollMargin = 25; // Distance from the edge of the viewport that triggers scrolling
            int scrollSpeed = 10; // Number of pixels to scroll

            // Check if the mouse is near the edges and adjust the view position accordingly
            if (e.getX() - viewPosition.x < scrollMargin) {
                viewPosition.x = Math.max(0, viewPosition.x - scrollSpeed);
            } else if (viewPosition.x + extentSize.width - e.getX() < scrollMargin) {
                viewPosition.x = Math.min(getWidth() - extentSize.width, viewPosition.x + scrollSpeed);
            }
            if (e.getY() - viewPosition.y < scrollMargin) {
                viewPosition.y = Math.max(0, viewPosition.y - scrollSpeed);
            } else if (viewPosition.y + extentSize.height - e.getY() < scrollMargin) {
                viewPosition.y = Math.min(getHeight() - extentSize.height, viewPosition.y + scrollSpeed);
            }

            // Update the view position
            viewport.setViewPosition(viewPosition);
        }
    }

    private JScrollPane findScrollPane(Component component) {
        if (component.getParent() == null) {
            return null;
        } else if (component.getParent() instanceof JScrollPane) {
            return (JScrollPane) component.getParent();
        } else {
            return findScrollPane(component.getParent());
        }
    }


    private int generateMappingElements(List<XmlElement> xmlElements, List<MappingElement> mappingElements, int x, int y, int indentLevel) {
        int elementWidth = 200 - indentLevel * 20;
        for (XmlElement element : xmlElements) {
            MappingElement mappingElement = new MappingElement(element, x + indentLevel * 20, y, elementWidth, 30, this.getWidth());
            mappingElements.add(mappingElement);
            y += 35;
            if (element.getChildren() != null) {
                y = generateMappingElements(element.getChildren(), mappingElements, x, y, indentLevel + 1);
            }
        }
        return y;
    }

    private void updateMappingElementPositions(List<MappingElement> mappingElements, int newX) {
        for (MappingElement mappingElement : mappingElements) {
            int originalX = mappingElement.getX();
            int offsetX = newX - originalX;
            mappingElement.setX(newX);
            if (mapping.containsKey(mappingElement)) {
                MappingElement rightElement = mapping.get(mappingElement);
                MappingLine mappingLine = getMappingLine(mappingElement, rightElement);
                if (mappingLine != null) {
                    mappingLine.setStartX(mappingLine.getStartX() + offsetX);
                }
            }
        }
    }

    private MappingLine getMappingLine(MappingElement leftElement, MappingElement rightElement) {
        for (MappingLine mappingLine : mappingLines) {
            if (mappingLine.getSource().equals(leftElement) && mappingLine.getDestination().equals(rightElement)) {
                return mappingLine;
            }
        }
        return null;
    }


    public void init() {
        int leftX = 10;
        int leftY = 10;
        int rightX = 500;
        int rightY = 10;

        leftY = generateMappingElements(leftXmlFile.getElements(), leftMappingElements, leftX, leftY, 0);
        rightY = generateMappingElements(rightXmlFile.getElements(), rightMappingElements, rightX, rightY, 0);

        int contentHeight = Math.max(leftY, rightY) + 20;
        Dimension preferredSize = new Dimension(800, contentHeight);
        setPreferredSize(preferredSize);

        JFrame frame = new JFrame("Mapping Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = frame.getContentPane().getWidth();
                int leftX = 10;
                int rightX = frameWidth - 210;
                updateMappingElementPositions(leftMappingElements, leftX);
                updateMappingElementPositions(rightMappingElements, rightX);
                repaint();
            }
        });


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

        JButton previewFileButton = new JButton("Preview File");
        previewFileButton.addActionListener(e -> {
            PreviewFile previewFile = new PreviewFile(rightXmlFile, 2);
            previewFile.showPreview();
        });
        previewFileButton.addMouseListener(new MouseListener() {
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
        previewFileButton.setBackground(Color.white);
        previewFileButton.setForeground(new Color(2, 83, 255));
        previewFileButton.setFont(new Font("Arial", Font.BOLD, 16));
        previewFileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        previewFileButton.setFocusable(false);
        previewFileButton.setBorderPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(previewFileButton);
        buttonPanel.add(saveButton);

        JScrollPane scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(800,600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawMappingElements(g, leftMappingElements);
        drawMappingElements(g, rightMappingElements);

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

    private void drawMappingElements(Graphics g, List<MappingElement> mappingElements) {
        for (MappingElement mappingElement : mappingElements) {
            XmlElement parent = mappingElement.getXmlElement().getParent();
            boolean parentCollapsed = false;
            while (parent != null) {
                MappingElement parentMappingElement = findMappingElementByXmlElement(mappingElements, parent);
                if (parentMappingElement != null && parentMappingElement.isCollapsed()) {
                    parentCollapsed = true;
                    break;
                }
                parent = parent.getParent();
            }

            if (!parentCollapsed) {
                mappingElement.draw(g);
            }
        }
    }

    private MappingElement findMappingElementByXmlElement(List<MappingElement> mappingElements, XmlElement xmlElement) {
        for (MappingElement mappingElement : mappingElements) {
            if (mappingElement.getXmlElement() == xmlElement) {
                return mappingElement;
            }
        }
        return null;
    }

    private void updateElementValue(MappingElement leftElement, MappingElement rightElement){
        rightElement.getXmlElement().setValue(leftElement.getXmlElement().getValue());
        rightElement.getXmlElement().setAttributes(leftElement.getXmlElement().getAttributes());
    }

    private void addMapping(MappingElement leftElement, MappingElement rightElement) {
        mapping.put(leftElement, rightElement);
        MappingLine mappingLine = new MappingLine(leftElement, rightElement);
        mappingLines.add(mappingLine);
        leftElement.setMapped(true);
        rightElement.setMapped(true);
        repaint();
    }
    public Map<MappingElement, MappingElement> getMapping() {
        return mapping;
    }
}