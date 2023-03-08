import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class XmlFile {
    private String fileName;
    private List<XmlElement> elements;
    private File file;

    public XmlFile(File file) {
        this.fileName = file.getName();
        this.elements = new ArrayList<>();
        this.file = file;
        this.parseFile(file);
    }

    public void parseFile(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element rootElement = document.getDocumentElement();
            NodeList nodeList = rootElement.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    XmlElement xmlElement = new XmlElement(
                            element.getNodeName(),
                            element.getTextContent(),
                            element.getAttribute("type")
                    );

                    NodeList attributeNodeList = element.getChildNodes();
                    for (int j = 0; j < attributeNodeList.getLength(); j++) {
                        Node attributeNode = attributeNodeList.item(j);
                        if (attributeNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                            Element attributeElement = (Element) attributeNode;
                            XmlAttribute attribute = new XmlAttribute(attributeElement.getNodeName(), attributeElement.getTextContent());
                            xmlElement.addAttribute(attribute);
                        }
                    }

                    this.elements.add(xmlElement);

                    NodeList childNodeList = element.getChildNodes();
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        Node childNode = childNodeList.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            XmlElement childXmlElement = new XmlElement(
                                    childElement.getNodeName(),
                                    childElement.getTextContent(),
                                    childElement.getAttribute("type")
                            );

                            NodeList childAttributeNodeList = childElement.getChildNodes();
                            for (int k = 0; k < childAttributeNodeList.getLength(); k++) {
                                Node childAttributeNode = childAttributeNodeList.item(k);
                                if (childAttributeNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                                    Element childAttributeElement = (Element) childAttributeNode;
                                    XmlAttribute childAttribute = new XmlAttribute(childAttributeElement.getNodeName(), childAttributeElement.getTextContent());
                                    childXmlElement.addAttribute(childAttribute);
                                }
                            }

                            xmlElement.addChild(childXmlElement);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public String previewFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(this.fileName).append("\n");

        for (XmlElement element : this.elements) {
            sb.append(formatElement(element, 0));
        }

        return sb.toString();
    }

    private String formatElement(XmlElement element, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = getIndent(indentLevel);
        sb.append(indent).append("<").append(element.getName());

        for (XmlAttribute attribute : element.getAttributes()) {
            sb.append(" ").append(attribute.getName()).append("=\"").append(attribute.getValue()).append("\"");
        }

        if (element.getChildren().isEmpty() && !element.getValue().isEmpty()) {
            sb.append(">").append(element.getValue()).append("</").append(element.getName()).append(">\n");
        } else if (element.getChildren().isEmpty()) {
            sb.append("/>\n");
        } else {
            sb.append(">\n");

            for (XmlElement child : element.getChildren()) {
                sb.append(formatElement(child, indentLevel + 1));
            }

            sb.append(indent).append("</").append(element.getName()).append(">\n");
        }

        return sb.toString();
    }

    private String getIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<XmlElement> getElements() {
        return elements;
    }

    public void setElements(List<XmlElement> elements) {
        this.elements = elements;
    }

    public XmlElement getElement(String name) {
        for (XmlElement element : this.elements) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    public void saveToFile() throws IOException {
        int extIndex = fileName.lastIndexOf(".");
        String mappedFileName = fileName.substring(0, extIndex) + "_mapped" + fileName.substring(extIndex);
        FileWriter writer = new FileWriter(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)) + File.separator + mappedFileName);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<" + elements.get(0).getName() + ">\n");

        for (XmlElement element : elements) {
            writeElement(writer, element, 1);
        }

        writer.write("</" + elements.get(0).getName() + ">\n");
        writer.close();
    }

    private void writeElement(FileWriter writer, XmlElement element, int indentLevel) throws IOException {
        String indent = getIndent(indentLevel);
        writer.write(indent + "<" + element.getName());
        for (XmlAttribute attribute : element.getAttributes()) {
            writer.write(" " + attribute.getName() + "=\"" + attribute.getValue() + "\"");
        }
        if (element.getChildren().isEmpty() && !element.getValue().isEmpty()) {
            writer.write(">" + element.getValue() + "</" + element.getName() + ">\n");
        } else if (element.getChildren().isEmpty()) {
            writer.write("/>\n");
        } else {
            writer.write(">\n");
            for (XmlElement child : element.getChildren()) {
                writeElement(writer, child, indentLevel + 1);
            }
            writer.write(indent + "</" + element.getName() + ">\n");
        }
    }

}
