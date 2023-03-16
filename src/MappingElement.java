import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MappingElement {
    private int x;
    private int y;
    private int width;
    private int height;
    private XmlElement xmlElement;
    private Color color;
    private int panelWidth;
    private boolean collapsed;
    private boolean isMapped;
    private Image notMappedIcon;

    public MappingElement(XmlElement xmlElement, int x, int y, int width, int height, int panelWidth) {
        this.xmlElement = xmlElement;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.panelWidth = panelWidth;
        this.color = Color.BLUE;
        this.collapsed = false;
        this.isMapped = false;

        try {
            notMappedIcon = ImageIO.read(new File("warning_icon.png")).getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawString(xmlElement.getName(), x + 10, y + 20);

        if (xmlElement.getChildren() != null && !xmlElement.getChildren().isEmpty()) {
            g.drawRect(x + width - 20, y, 10, 10);
            g.drawLine(x + width - 18, y + 5, x + width - 12, y + 5);
            if (collapsed) {
                g.drawLine(x + width - 15, y + 2, x + width - 15, y + 8);
            }
        }

        if(!isMapped){
            int imageX = x + width + 5;
            int imageY = y + 2;
            int imageWidth = notMappedIcon.getWidth(null);
            int imageHeight = notMappedIcon.getHeight(null);
            g.drawImage(notMappedIcon, imageX, imageY, imageWidth, imageHeight, null);
        }
    }

    public XmlElement getXmlElement() {
        return xmlElement;
    }

    public void setXmlElement(XmlElement xmlElement) {
        this.xmlElement = xmlElement;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean withinBounds(int x, int y) {
        return (this.x <= x && x <= this.x + width && this.y <= y && y <= this.y + height);
    }

    public boolean isLeftElement() {
        return getX() < panelWidth / 2;
    }

    public boolean isRightElement() {
        return getX() >= panelWidth / 2;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public void toggleCollapsed() {
        this.collapsed = !this.collapsed;
    }

    public boolean isToggleIconClicked(int mouseX, int mouseY) {
        int iconX = x + width - 20;
        int iconY = y;
        int iconSize = 10;
        return mouseX >= iconX && mouseX <= iconX + iconSize
                && mouseY >= iconY && mouseY <= iconY + iconSize;
    }

    public boolean isMapped() {
        return isMapped;
    }

    public void setMapped(boolean isMapped) {
        this.isMapped = isMapped;
    }

    public boolean withinWarningIconBounds(int mouseX, int mouseY) {
        int imageX = x + width + 5;
        int imageY = y + 2;
        int imageWidth = notMappedIcon.getWidth(null);
        int imageHeight = notMappedIcon.getHeight(null);

        return mouseX >= imageX && mouseX <= imageX + imageWidth
                && mouseY >= imageY && mouseY <= imageY + imageHeight;
    }
}
