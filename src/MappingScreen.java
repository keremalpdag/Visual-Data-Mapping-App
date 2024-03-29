import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
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
    private boolean showDeleteButton = false;
    private Rectangle deleteButton = new Rectangle(0, 0, 65, 25);
    private static final int MARGIN = 50;
    private boolean autoMappingEnabled;
    private int maxIndentLevel = 5;
    private boolean warningUpdated = false;

    public MappingScreen(XmlFile leftXmlFile, XmlFile rightXmlFile) {
        this.leftXmlFile = leftXmlFile;
        this.rightXmlFile = rightXmlFile;
        this.leftMappingElements = new ArrayList<>();
        this.rightMappingElements = new ArrayList<>();
        this.mappingLines = new ArrayList<>();
        this.selectedElement = null;
        this.mousePoint = new Point();
        this.mapping = new HashMap<>();
        this.autoMappingEnabled = true;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showDeleteButton && deleteButton.contains(e.getPoint())) {
                    for (Iterator<MappingLine> iterator = mappingLines.iterator(); iterator.hasNext(); ) {
                        MappingLine line = iterator.next();
                        if (line.isNear(deleteButton.x, deleteButton.y)) {
                            // Update the isMapped variable of the source and destination elements
                            line.getSource().setMapped(false);
                            line.getDestination().setMapped(false);
                            // Remove the line from the list
                            iterator.remove();
                            deleteMappedElementValue(line.getDestination());
                        }
                    }
                    showDeleteButton = false;
                    repaint();
                    return;
                }

                boolean lineClicked = false;

                for (MappingLine line : mappingLines) {
                    if (line.isNear(e.getX(), e.getY())) {
                        showDeleteButton = true;
                        deleteButton.setLocation(e.getX(), e.getY());
                        lineClicked = true;
                        repaint();
                        break;
                    }
                }

                if (!lineClicked) {
                    showDeleteButton = false;
                } else {
                    // If a line is clicked, return immediately to avoid selecting elements.
                    return;
                }

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

                repaint();
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

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean isCursorOverElement = false;

                for (MappingElement mappingElement : leftMappingElements) {
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        mappingElement.setColor(new Color(72, 168, 197));
                        isCursorOverElement = true;
                    } else {
                        mappingElement.setColor(Color.BLUE);
                    }
                }

                for (MappingElement mappingElement : rightMappingElements) {
                    if (mappingElement.withinBounds(e.getX(), e.getY())) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        mappingElement.setColor(new Color(72, 168, 197));
                        isCursorOverElement = true;
                    } else {
                        mappingElement.setColor(Color.BLUE);
                    }
                }

                boolean lineNearCursor = false;
                for (MappingLine line : mappingLines) {
                    if (line.isNear(e.getX(), e.getY())) {
                        lineNearCursor = true;
                        break;
                    }
                }

                if (lineNearCursor || isCursorOverDeleteButton(e.getX(), e.getY())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else if(!isCursorOverElement) {
                    setCursor(Cursor.getDefaultCursor());
                }

                repaint();

                autoScroll(e);

                updateWarningToolTipText(e);
                updateElementToolTipText(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                autoScroll(e);
            }
        });
    }

    private boolean isCursorOverDeleteButton(int x, int y) {
        if (showDeleteButton) {
            return deleteButton.contains(x, y);
        }
        return false;
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
        int elementWidth;

        if (indentLevel == 0) {
            elementWidth = 210; // Set a larger width for the root element
        } else if (indentLevel == 1) {
            elementWidth = 200 - indentLevel * 10; // Set a smaller indentation size for the first level
        } else {
            elementWidth = 200 - indentLevel * 15; // Reduce the indentation size to make the elements bigger for the other levels
        }

        for (XmlElement element : xmlElements) {
            int xOffset = (indentLevel == 1) ? 10 : indentLevel * 15; // Apply the smaller indentation size for the first level
            MappingElement mappingElement = new MappingElement(element, x + xOffset, y, elementWidth, 30, this.getWidth(), indentLevel);
            mappingElements.add(mappingElement);
            y += 35;

            if (element.getChildren() != null) {
                // Do not increase the indentation level if it has reached the maximum
                int nextIndentLevel = indentLevel < maxIndentLevel ? indentLevel + 1 : indentLevel;
                y = generateMappingElements(element.getChildren(), mappingElements, x, y, nextIndentLevel);
            }
        }
        return y;
    }

    private void drawArrow(Graphics g, int x, int y) {
        int[] xPoints = new int[] {x, x + 5, x};
        int[] yPoints = new int[] {y, y + 5, y + 10};
        g.fillPolygon(xPoints, yPoints, 3);
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
        int leftX = 10 + MARGIN;
        int leftY = 10;
        int rightX = 500 - MARGIN;
        int rightY = 10;

        leftY = generateMappingElements(leftXmlFile.getElements(), leftMappingElements, leftX, leftY, 0);
        rightY = generateMappingElements(rightXmlFile.getElements(), rightMappingElements, rightX, rightY, 0);

        int contentHeight = Math.max(leftY, rightY) + 20;
        Dimension preferredSize = new Dimension(800, contentHeight);
        setPreferredSize(preferredSize);

        JFrame frame = new JFrame("Mapping Screen");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the mapping? All data will be lost.", "Exit Mapping", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    frame.dispose();
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = frame.getContentPane().getWidth();
                int leftX = 10 + MARGIN;
                int rightX = frameWidth - 210 - MARGIN;
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

        JButton turnOfAutoMapping = new JButton("Toggle Auto-Mapping");
        turnOfAutoMapping.addActionListener(e -> {
            this.setAutoMappingEnabled(!this.isAutoMappingEnabled());
            JOptionPane.showMessageDialog(null, "Auto mapping is now " + (this.isAutoMappingEnabled() ? "enabled" : "disabled") + ".");
        });
        turnOfAutoMapping.addMouseListener(new MouseListener() {
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
        turnOfAutoMapping.setBackground(Color.white);
        turnOfAutoMapping.setForeground(new Color(2, 83, 255));
        turnOfAutoMapping.setFont(new Font("Arial", Font.BOLD, 16));
        turnOfAutoMapping.setCursor(new Cursor(Cursor.HAND_CURSOR));
        turnOfAutoMapping.setFocusable(false);
        turnOfAutoMapping.setBorderPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(turnOfAutoMapping);
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
        if (showDeleteButton) {
            g.setColor(Color.BLACK);
            g.fillRect(deleteButton.x, deleteButton.y, deleteButton.width, deleteButton.height);
            g.setColor(Color.WHITE);
            g.drawString("Delete", deleteButton.x + 10, deleteButton.y + 15);
        }

        g.setColor(Color.BLACK);

        // Add the arrow drawing for leftMappingElements
        for (MappingElement mappingElement : leftMappingElements) {
            if (mappingElement.getIndentLevel() == maxIndentLevel && mappingElement.getXmlElement().hasChildren()) {
                drawArrow(g, mappingElement.getX() - 10, mappingElement.getY() + 10);
            }
        }

        // Add the arrow drawing for rightMappingElements
        for (MappingElement mappingElement : rightMappingElements) {
            if (mappingElement.getIndentLevel() == maxIndentLevel && mappingElement.getXmlElement().hasChildren()) {
                drawArrow(g, mappingElement.getX() - 10, mappingElement.getY() + 10);
            }
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

    private MappingElement findWarningIcon(int x, int y) {
        for (MappingElement mappingElement : leftMappingElements) {
            if (!mappingElement.isMapped() && mappingElement.withinWarningIconBounds(x, y)) {
                return mappingElement;
            }
        }
        for (MappingElement mappingElement : rightMappingElements) {
            if (!mappingElement.isMapped() && mappingElement.withinWarningIconBounds(x, y)) {
                return mappingElement;
            }
        }
        return null;
    }

    private void updateElementValue(MappingElement leftElement, MappingElement rightElement){
        rightElement.getXmlElement().setValue(leftElement.getXmlElement().getValue());
        rightElement.getXmlElement().setAttributes(leftElement.getXmlElement().getAttributes());

        if (autoMappingEnabled) {
            autoMapChildren(leftElement, rightElement);
        }
    }

    private  void deleteMappedElementValue(MappingElement rightElement){
        List<XmlAttribute> emptyList = new ArrayList<>();
        rightElement.getXmlElement().setValue("");
        rightElement.getXmlElement().setAttributes(emptyList);
    }

    private void addMapping(MappingElement leftElement, MappingElement rightElement) {
        if(!autoMappingEnabled && rightElement.isMapped()){
            JOptionPane.showMessageDialog(null, "Element " + "'" + rightElement.getXmlElement().getName() + "'" +
                    " is already mapped. Delete the previous mapping to map it to a new element.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updateElementValue(leftElement, rightElement);
        mapping.put(leftElement, rightElement);
        MappingLine mappingLine = new MappingLine(leftElement, rightElement);
        mappingLines.add(mappingLine);
        leftElement.setMapped(true);
        rightElement.setMapped(true);
        repaint();

        if (autoMappingEnabled) {
            autoMapChildren(leftElement, rightElement);
        }
    }

    public Map<MappingElement, MappingElement> getMapping() {
        return mapping;
    }

    private String normalizeElementName(String elementName) {
        return elementName.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private void autoMapChildren(MappingElement leftParent, MappingElement rightParent) {
        if (leftParent.getXmlElement().getChildren() == null || rightParent.getXmlElement().getChildren() == null) {
            return;
        }

        Map<String, XmlElement> leftChildren = new HashMap<>();
        Map<String, XmlElement> rightChildren = new HashMap<>();

        for (XmlElement leftChild : leftParent.getXmlElement().getChildren()) {
            String normalizedLeftChildName = normalizeElementName(leftChild.getName());
            if (!leftChildren.containsKey(normalizedLeftChildName)) {
                leftChildren.put(normalizedLeftChildName, leftChild);
            }
        }

        for (XmlElement rightChild : rightParent.getXmlElement().getChildren()) {
            String normalizedRightChildName = normalizeElementName(rightChild.getName());
            if (!rightChildren.containsKey(normalizedRightChildName)) {
                rightChildren.put(normalizedRightChildName, rightChild);
            }
        }

        for (Map.Entry<String, XmlElement> leftChildEntry : leftChildren.entrySet()) {
            XmlElement rightChild = rightChildren.get(leftChildEntry.getKey());
            if (rightChild != null) {
                MappingElement leftChildElement = findMappingElement(leftMappingElements, leftChildEntry.getValue());
                MappingElement rightChildElement = findMappingElement(rightMappingElements, rightChild);
                if (leftChildElement != null && rightChildElement != null) {
                    updateElementValue(leftChildElement, rightChildElement);
                    addMapping(leftChildElement, rightChildElement);
                }
            }
        }
    }

    private MappingElement findMappingElement(List<MappingElement> mappingElements, XmlElement xmlElement) {
        for (MappingElement mappingElement : mappingElements) {
            if (mappingElement.getXmlElement() == xmlElement) {
                return mappingElement;
            }
        }
        return null;
    }

    public boolean isAutoMappingEnabled() {
        return autoMappingEnabled;
    }

    public void setAutoMappingEnabled(boolean autoMappingEnabled) {
        this.autoMappingEnabled = autoMappingEnabled;
    }

    private void updateWarningToolTipText(MouseEvent e) {
        MappingElement warningIconElement = findWarningIcon(e.getX(), e.getY());
        if (warningIconElement != null) {
            setToolTipText("Element not used");
            warningUpdated = true;
        } else {
            setToolTipText(null);
            warningUpdated = false;
        }
    }

    private void updateElementToolTipText(MouseEvent e){
        MappingElement hoverElement = null;

        if(warningUpdated){
            return;
        }

        for (MappingElement mappingElement : leftMappingElements) {
            if (mappingElement.withinBounds(e.getX(), e.getY())) {
                hoverElement = mappingElement;
                break;
            }
        }

        if (hoverElement == null) {
            for (MappingElement mappingElement : rightMappingElements) {
                if (mappingElement.withinBounds(e.getX(), e.getY())) {
                    hoverElement = mappingElement;
                    break;
                }
            }
        }

        if (hoverElement != null) {
            XmlElement xmlElement = hoverElement.getXmlElement();
            if (xmlElement.getChildren() != null && !xmlElement.getChildren().isEmpty()) {
                if (xmlElement.getParent() == null) {
                    setToolTipText("Root element");
                } else {
                    setToolTipText("Parent element with children");
                }
            } else {
                String value = xmlElement.getValue();
                if (value != null && !value.isEmpty()) {
                    setToolTipText("Value: " + "'" + value + "'");
                } else {
                    setToolTipText(null);
                }
            }
        } else {
            setToolTipText(null);
        }
    }
}