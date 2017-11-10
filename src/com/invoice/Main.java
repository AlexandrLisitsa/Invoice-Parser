package com.invoice;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.invoice.client.Client;
import com.invoice.client.Server;
import com.invoice.client.Service;
import com.invoice.creator.DOCXcreator;
import com.invoice.creator.InvoiceBuilder;
import com.invoice.parse.AdditionParse;
import com.invoice.parse.DeliveryParser;
import com.invoice.parse.ReloadCartridgeParser;
import com.invoice.parse.XMLparser;
import com.invoice.visual.ProgressPage;

public class Main {

	private ProgressPage progress = new ProgressPage();
	private DeliveryParser deliveryParser = new DeliveryParser(progress);
	private ReloadCartridgeParser cartridgeParser = new ReloadCartridgeParser(progress);
	private XMLparser XMLparser = new XMLparser(progress);
	private InvoiceBuilder builder = new InvoiceBuilder(progress);
	private ArrayList<Client> clients = new ArrayList<Client>();
	private Config cfg = new Config();
	private DOCXcreator docParser = new DOCXcreator();
	private AdditionParse additionParse = new AdditionParse();

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Main m = new Main();
		m.loadCreator();
		m.test();
	}


	private void test() {
		for(int i=0;i<clients.size();i++) {
			if(clients.get(i).getClientName().equalsIgnoreCase("оптидея")) {
				docParser.createInvoice(clients.get(i));
			}
			for (Server x : clients.get(i).getServers()) {
				System.out.println(x.getDescription()+" "+x.getCount()+" "+x.getPrice());
			}
			for (Service x : clients.get(i).getServices()) {
				System.out.println(x.getDescription()+" "+x.getCount()+" "+x.getPrice());
			}
		}	
	}


	private void loadCreator() {
		System.out.println("load config");
		XMLparser.configParse(cfg);
		System.out.println("config parser");
		XMLparser.parseXMLClients(clients);
		System.out.println("delivery parser");
		deliveryParser.parse(clients,cfg.getDeliveryPath());
		System.out.println("cartridge parser");
		cartridgeParser.parse(clients,cfg.getCartridgePath());
		System.out.println("addition parser");
		additionParse.parseDebt(clients, cfg.getAdditionPath());
		System.out.println("create invoices");
		builder.create(clients,cfg.getInvoiceCreationPath());
		progress.getLabel().setForeground(Color.GREEN);
		progress.getLabel().setText("Done");
		System.out.println("Done");
	}
}
