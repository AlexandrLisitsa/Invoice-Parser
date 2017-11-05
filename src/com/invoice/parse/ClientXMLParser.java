package com.invoice.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.invoice.Client;
import com.invoice.visual.ProgressPage;

public class ClientXMLParser {

	private ProgressPage bar;
	
	public ClientXMLParser(ProgressPage bar) {
		this.bar=bar;
	}
	
	public void parseXMLClients(ArrayList<Client> clients) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = builder.parse(new File("C:\\Users\\wypik\\eclipse-workspace\\Invoice_Creator\\src\\Clients.xml"));
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node = doc.getDocumentElement();
		NodeList tmp = node.getChildNodes();
		for (int i = 0; i < tmp.getLength(); i++) {
			if (tmp.item(i).hasAttributes()) {
				NamedNodeMap tmpAtt = tmp.item(i).getAttributes();
				for (int att = 0; att < tmpAtt.getLength(); att++) {
					if (tmpAtt.item(att).getNodeValue() != null) {
						clients.add(new Client());
					}
				}
				clients.get(clients.size() - 1).setClientName(tmp.item(i).getAttributes().item(0).getNodeValue());
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bar.updateBar(tmp.getLength(), i,"Parsing Client:" +clients.get(clients.size()-1).getClientName(),false);
			}
			if (tmp.item(i).hasChildNodes()) {
				NodeList locList = tmp.item(i).getChildNodes();
				for (int tmpList = 0; tmpList < locList.getLength(); tmpList++) {
					if (locList.item(tmpList).getNodeName().equals("location")) {
						clients.get(clients.size() - 1).setLocation(locList.item(tmpList).getTextContent());
					}
				}
			}
		}
	}
}
