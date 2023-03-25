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

    public int getStartX() {
        return source.getX() + source.getWidth();
    }

    public void setStartX(int startX) {
        int offsetX = startX - getStartX();
        source.setX(source.getX() + offsetX);
    }

    public boolean isNear(int x, int y) {
        final int NEAR_DISTANCE = 5; // Tolerance distance in pixels
        int x1 = getStartX();
        int y1 = getStartY();
        int x2 = getEndX();
        int y2 = getEndY();

        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double distance = Math.abs((x - x1) * (y2 - y1) - (y - y1) * (x2 - x1)) / lineLength;

        return distance < NEAR_DISTANCE;
    }

    public int getStartY() {
        return source.getY() + source.getHeight() / 2;
    }

    public int getEndX() {
        return destination.getX();
    }

    public int getEndY() {
        return destination.getY() + destination.getHeight() / 2;
    }

}
