package com.plane.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.plane.comparators.AtrrQuantityComparator;
import com.plane.comparators.DefaultAtrrNameComparator;

public class App {

	public static void main(String[] args) {

		int option = 0;
		boolean exit = false;
		Scanner input = new Scanner(System.in);

		try {

			File xmlFile = new File("plane.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			while (!exit) {

				printMenu();
				System.out.print("Choose option:");
				option = input.nextInt();
				input.nextLine();

				switch (option) {
				case 1: // sort by name asc

					sortChildNodes(doc.getDocumentElement(), false, null);
					printNode(doc.getDocumentElement(), "");

					break;

				case 2: // sort by quantity asc

					sortChildNodes(doc.getDocumentElement(), false, new AtrrQuantityComparator());
					printNode(doc.getDocumentElement(), "");

					break;

				case 3: // sort by quantity desc

					sortChildNodes(doc.getDocumentElement(), true, new AtrrQuantityComparator());
					printNode(doc.getDocumentElement(), "");

					break;
				case 4: // add new part

					printNode(doc.getDocumentElement(), "");

					Part newPart = new Part();
					String parent;

					System.out.print("Part name:");
					newPart.setName(input.nextLine());

					System.out.print("Quantity:");
					newPart.setQuantity(input.nextInt());
					input.nextLine();

					System.out.print("Parent:");
					parent = input.nextLine();

					addNode(doc, doc.getDocumentElement(), newPart, parent);
					printNode(doc.getDocumentElement(), "");

					break;
				case 5: // remove part

					printNode(doc.getDocumentElement(), "");

					System.out.print("Part name:");

					removeNode(doc.getDocumentElement(), input.nextLine());
					printNode(doc.getDocumentElement(), "");

					break;
				case 6: // change quantity

					String parentName, partName;
					int value;

					printNode(doc.getDocumentElement(), "");

					System.out.print("Part name:");
					partName = input.nextLine();

					System.out.print("Parent name:");
					parentName = input.nextLine();

					System.out.print("Value of change (can be negative):");
					value = input.nextInt();
					input.nextLine();

					changeQuantity(doc.getDocumentElement(), parentName, partName, value);

					printNode(doc.getDocumentElement(), "");

					break;
				case 7: // where part is present

					String partToCheck;

					System.out.print("Part name:");
					partToCheck = input.nextLine();

					List<String> lista = new ArrayList<>();
					lista = wherePartIsPresent(doc.getDocumentElement(), partToCheck, lista);
					for (int i = 0; i < lista.size(); i++) {
						System.out.println(lista.get(i));
					}

					break;
				case 8: // total number of each part

					LinkedHashMap<String, Integer> rep = new LinkedHashMap<String, Integer>();
					printReport(doc.getDocumentElement(), rep);
					System.out.println(rep);

					break;
				case 0: // exit
					exit = true;
					input.close();
					System.out.println("Goodbye");

					break;

				default:
					break;
				}

			}

			saveXmlFile(doc);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void printMenu() {

		System.out.println("+-------------------------------------------------+");
		System.out.println("|  Display sorted by:                             |");
		System.out.println("|      1. Name                                    |");
		System.out.println("|      2. Quantity - ascending                    |");
		System.out.println("|      3. Quantity - descending                   |");
		System.out.println("|                                                 |");
		System.out.println("|  Edit structure                                 |");
		System.out.println("|      4. Add new part                            |");
		System.out.println("|      5. Remove existing part                    |");
		System.out.println("|      6. Change quantity of existing part        |");
		System.out.println("|                                                 |");
		System.out.println("|  Reports                                        |");
		System.out.println("|      7. Where given part is present             |");
		System.out.println("|      8. Total number of each part in structure  |");
		System.out.println("|                                                 |");
		System.out.println("|    0. EXIT                                      |");
		System.out.println("+-------------------------------------------------+");

	}

	private static void printNode(Node rootNode, String tab) {
		if (rootNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) rootNode;

			System.out.println(tab + eElement.getAttribute("name") + " x" + eElement.getAttribute("quantity"));

			NodeList nl = rootNode.getChildNodes();

			for (int i = 0; i < nl.getLength(); i++)
				printNode(nl.item(i), tab + "    ");
		}
	}

	public static void sortChildNodes(Node node, boolean descending, Comparator comparator) {

		List<Node> nodes = new ArrayList<Node>();
		NodeList childNodeList = node.getChildNodes();
		Element childNode = null;

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;

			sortChildNodes(childNode, descending, comparator);

			nodes.add(childNode);

		}
		Comparator comp = (comparator != null) ? comparator : new DefaultAtrrNameComparator();

		if (descending) {
			// if descending is true, get the reverse ordered comparator
			Collections.sort(nodes, Collections.reverseOrder(comp));
		} else {
			Collections.sort(nodes, comp);
		}

		// for (Iterator<Node> iter = nodes.iterator(); iter.hasNext();) {
		// Node element = (Node) iter.next();
		// node.appendChild(element);
		// }
		for (Node node2 : nodes) {
			if ((((Element) node2).hasAttribute("name"))) {
				node.appendChild(node2);
			}
		}
	}

	public static void addNode(Document doc, Node rootNode, Part part, String parent) {

		NodeList childNodeList = rootNode.getChildNodes();
		Element childNode = null;
		Element broNode = null;
		int intTemp;
		boolean done = false;

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;
			addNode(doc, childNode, part, parent);

			String name = childNode.getAttribute("name");
			if (name.equals(parent)) {

				NodeList bro = childNode.getChildNodes();

				for (int j = 0; j < bro.getLength(); j++) {

					if (bro.item(j).getNodeType() == Node.ELEMENT_NODE) {
						broNode = (Element) bro.item(j);
					} else
						continue;

					String broName = broNode.getAttribute("name");
					if (broName.equals(part.getName())) {
						intTemp = Integer.parseInt(broNode.getAttribute("quantity"));
						intTemp += part.getQuantity();
						broNode.setAttribute("quantity", "" + intTemp);
						done = true;
						break;
					}
				}

				if (!done) {
					Element newPart = doc.createElement("part");
					newPart.setAttribute("name", part.getName());
					newPart.setAttribute("quantity", "" + part.getQuantity());
					childNode.appendChild(newPart);

				}
			}
		}
	}

	public static void removeNode(Node rootNode, String name) {
		NodeList childNodeList = rootNode.getChildNodes();
		Element childNode = null;

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;
			removeNode(childNode, name);

			String atrName = childNode.getAttribute("name");
			if (atrName.equals(name)) {
				childNode.getParentNode().removeChild(childNode);

			}
		}
	}

	public static void changeQuantity(Node rootNode, String parentName, String partName, int changeVal) {

		NodeList childNodeList = rootNode.getChildNodes();
		NodeList broNodes = null;
		Element childNode = null;
		Element broNode = null;
		int intTemp;

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;

			changeQuantity(childNode, parentName, partName, changeVal);

			String parName = childNode.getAttribute("name");

			if (parName.equals(parentName)) {

				broNodes = childNode.getChildNodes();

				for (int j = 0; j < broNodes.getLength(); j++) {

					if (broNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						broNode = (Element) broNodes.item(j);
					} else
						continue;

					if (broNode.getAttribute("name").equals(partName)) {
						intTemp = Integer.parseInt(broNode.getAttribute("quantity"));
						intTemp += changeVal;
						broNode.setAttribute("quantity", "" + intTemp);
					}
				}
			}
		}
	}

	public static List<String> wherePartIsPresent(Node rootNode, String partName, List<String> parents) {

		NodeList childNodeList = rootNode.getChildNodes();
		NodeList broList = null;
		Element childNode = null;

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;

			wherePartIsPresent(childNode, partName, parents);

			broList = childNode.getChildNodes();
			if (contains(broList, partName)) {
				parents.add(childNode.getAttribute("name"));
			}
		}
		return parents;
	}

	public static boolean contains(NodeList list, String partName) {
		Element childNode = null;
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) list.item(i);
			} else
				continue;
			if (childNode.getAttribute("name").equals(partName)) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Integer> printReport(Node rootNode, LinkedHashMap<String, Integer> report) {

		String currentPartName, currentPartQuantity;
		Element childNode = null;

		NodeList childNodeList = rootNode.getChildNodes();

		for (int i = 0; i < childNodeList.getLength(); i++) {
			if (childNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				childNode = (Element) childNodeList.item(i);
			} else
				continue;

			printReport(childNode, report);

			currentPartName = childNode.getAttribute("name");
			currentPartQuantity = childNode.getAttribute("quantity");

			if (report.get(childNode.getAttribute("name")) == null) {
				report.put(currentPartName, Integer.parseInt(currentPartQuantity));
			} else {
				report.put(currentPartName, report.get(currentPartName) + Integer.parseInt(currentPartQuantity));
			}
		}
		return report;
	}

	public static void saveXmlFile(Document doc) throws TransformerException {

		DOMSource source = new DOMSource(doc);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult("plane.xml");
		transformer.transform(source, result);
	}
}
