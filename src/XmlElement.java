import java.util.ArrayList;
import java.util.List;

public class XmlElement {
    private String name;
    private String value;
    private String type;
    private List<XmlAttribute> attributes;
    private List<XmlElement> children;

    public XmlElement(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<XmlAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<XmlAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(XmlAttribute attribute) {
        this.attributes.add(attribute);
    }

    public XmlAttribute getAttribute(String name) {
        for (XmlAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    public void setAttribute(String name, String value) {
        for (XmlAttribute attribute : this.attributes) {
            if (attribute.getName().equals(name)) {
                attribute.setValue(value);
                return;
            }
        }
        this.attributes.add(new XmlAttribute(name, value));
    }

    public List<XmlElement> getChildren() {
        return children;
    }

    public void setChildren(List<XmlElement> children) {
        this.children = children;
    }

    public void addChild(XmlElement child) {
        this.children.add(child);
    }
}
