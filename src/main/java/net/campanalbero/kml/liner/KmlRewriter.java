package net.campanalbero.kml.liner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KmlRewriter {
	private static final XPath XPATH = XPathFactory.newInstance().newXPath();

	private final Document doc;
	private final File out;
	
	private final boolean isUnified;

	public KmlRewriter(String input, String output, boolean isUnified) throws ParserConfigurationException, SAXException, IOException {
		out = new File(output);
		this.isUnified = isUnified;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(new File(input));
	}

	private void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(out));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<Line> generateLineList(NodeList nodes) {
		List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < nodes.getLength() - 1; i++) {
			Line line = generateLine(nodes.item(i), nodes.item(i + 1));
			lines.add(line);
		}
		return lines;
	}

	private double calculateAverageLength(List<Line> lines) {
		double average = 0.0;
		for (Line line : lines) {
			average += line.getLength();
		}
		average /= lines.size();
		return average;
	}

	private double calculateStandardDiviation(List<Line> lines, double average) {
		double tmp = 0;
		for (Line line : lines) {
			tmp += Math.pow(average - line.getLength(), 2);
		}
		return Math.sqrt(tmp / (double)(lines.size()));
	}

	private void addCoordinate(Node documentNode, String coordinate) {
		Node coordinates = doc.createElement("coordinates");
		coordinates.setTextContent(coordinate);

		Node lineString = doc.createElement("LineString");
		lineString.appendChild(coordinates);

		Node placemark = doc.createElement("Placemark");
		placemark.appendChild(lineString);

		documentNode.appendChild(placemark);
	}
	
	private Node generateDocumentNode(List<Line> lines) {
		Node document = doc.createElement("Document");
		
		StringBuilder sb = new StringBuilder();
		for (Line line : lines) {
			sb.append(line.getV0().getX() + "," + line.getV0().getY() + "," + line.getV0().getZ());
			sb.append("\n");
		}
		addCoordinate(document, sb.toString());
		
		return document;
	}
	
	private Node generateDocumentNode(List<Line> lines, double average, double standardDiviation) {
		Node document = doc.createElement("Document");
		
		StringBuilder sb = new StringBuilder();
		for (Line line : lines) {
			sb.append(line.getV0().getX() + "," + line.getV0().getY() + "," + line.getV0().getZ());
			sb.append("\n");
			
			double length = line.getLength();
			if (length > average + 3.0 * standardDiviation) {
				addCoordinate(document, sb.toString());
				sb = new StringBuilder();
			}
		}
		addCoordinate(document, sb.toString());
		
		return document;
	}
	
	private void removeDocumentNode(Element root) {
		NodeList list = root.getElementsByTagName("Document");
		Node parent = list.item(0).getParentNode();
		parent.removeChild(list.item(0));
	}

	private void generateSplitedLine() {
		Element root = doc.getDocumentElement();
		List<Line> lines = generateLineList(root.getElementsByTagName("Placemark"));
		double average = calculateAverageLength(lines);
		double standardDiviation = calculateStandardDiviation(lines, average);
		removeDocumentNode(root);
		root.appendChild(generateDocumentNode(lines, average, standardDiviation));
	}
	
	private void generateUnifiedLine() {
		Element root = doc.getDocumentElement();
		List<Line> lines = generateLineList(root.getElementsByTagName("Placemark"));
		removeDocumentNode(root);
		root.appendChild(generateDocumentNode(lines));
	}
	
	private Line generateLine(Node initialNode, Node terminalNode) {
		Vertex v0 = generateVertex(initialNode);
		Vertex v1 = generateVertex(terminalNode);
		return new Line(v0, v1);
	}

	private Vertex generateVertex(Node placemark) {
		String raw = null;
		try {
			raw = XPATH.evaluate("Point/coordinates", placemark);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		String[] coordinates = raw.split(",");
		return new Vertex(coordinates);
	}

	public void run() {
		if (isUnified) {
			generateUnifiedLine();
		} else {
			generateSplitedLine();
		}
		save();
	}
}
