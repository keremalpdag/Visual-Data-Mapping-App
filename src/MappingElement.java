import java.awt.Color;
import java.awt.Graphics;

public class MappingElement {
    private int x;
    private int y;
    private int width;
    private int height;
    private XmlElement xmlElement;
    private Color color;

    public MappingElement(XmlElement xmlElement, int x, int y, int width, int height) {
        this.xmlElement = xmlElement;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        color = Color.BLUE;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawString(xmlElement.getName(), x + 10, y + 20);
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
}
