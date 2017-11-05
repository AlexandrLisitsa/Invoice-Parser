package com.invoice.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.invoice.Client;
import com.invoice.Config;
import com.invoice.visual.ProgressPage;

public class XMLparser {

	private ProgressPage bar;
	private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder = null;
	private Document doc;
	private Node node;
	private NodeList nodeList;

	public XMLparser(ProgressPage bar) {
		this.bar = bar;
	}

	public void parseXMLClients(ArrayList<Client> clients) {

		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc = null;
		try {
			doc = builder.parse(new File("xml/Clients.xml"));
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node = doc.getDocumentElement();
		nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).hasAttributes()) {
				NamedNodeMap tmpAtt = nodeList.item(i).getAttributes();
				for (int att = 0; att < tmpAtt.getLength(); att++) {
					if (tmpAtt.item(att).getNodeValue() != null) {
						clients.add(new Client());
					}
				}
				clients.get(clients.size() - 1).setClientName(nodeList.item(i).getAttributes().item(0).getNodeValue());
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bar.updateBar(nodeList.getLength(), i,
						"Parsing Client:" + clients.get(clients.size() - 1).getClientName(), false);
			}
			if (nodeList.item(i).hasChildNodes()) {
				NodeList locList = nodeList.item(i).getChildNodes();
				for (int tmpList = 0; tmpList < locList.getLength(); tmpList++) {
					if (locList.item(tmpList).getNodeName().equals("location")) {
						clients.get(clients.size() - 1).setLocation(locList.item(tmpList).getTextContent());
					}
				}
			}
		}
	}

	public void configParse(Config cfg) {

		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc = null;
		try {
			doc = builder.parse(new File("xml/config.xml"));
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node = doc.getDocumentElement();
		nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).hasAttributes()) {
				NamedNodeMap tmp = nodeList.item(i).getAttributes();
				for (int k = 0; k < tmp.getLength(); k++) {
					if (tmp.item(k).getNodeName().equals("CartridgePath")) {
						cfg.setCartridgePath(tmp.item(k).getNodeValue());
					}
					if (tmp.item(k).getNodeName().equals("DeliveryPath")) {
						cfg.setDeliveryPath(tmp.item(k).getNodeValue());
					}
					if (tmp.item(k).getNodeName().equals("InvoiceCreationPath")) {
						if (new File(tmp.item(k).getNodeValue()).exists()) {
							cfg.setInvoiceCreationPath(tmp.item(k).getNodeValue());
						} else {
							JOptionPane.showMessageDialog(null, "������ ����� ��� ����������");
							JFileChooser fc = new JFileChooser();
							fc.setDialogTitle("����� ����������");
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
								cfg.setInvoiceCreationPath(fc.getSelectedFile().getAbsolutePath().replace("\\","\\\\")+"\\");
							}
						}
					}
				}
			}
		}
	}
}