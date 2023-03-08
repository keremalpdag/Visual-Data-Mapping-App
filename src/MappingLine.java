import java.awt.Color;
import java.awt.Graphics;

public class MappingLine {
    private MappingElement source;
    private MappingElement destination;

    public MappingLine(MappingElement source, MappingElement destination) {
        this.source = source;
        this.destination = destination;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(source.getX() + source.getWidth(), source.getY() + source.getHeight() / 2,
                destination.getX(), destination.getY() + destination.getHeight() / 2);
    }

    public MappingElement getSource() {
        return source;
    }

    public void setSource(MappingElement source) {
        this.source = source;
    }

    public MappingElement getDestination() {
        return destination;
    }

    public void setDestination(MappingElement destination) {
        this.destination = destination;
    }
}
