package com.invoice;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.invoice.creator.InvoiceBuilder;
import com.invoice.parse.ClientXMLParser;
import com.invoice.parse.DeliveryParser;
import com.invoice.parse.ReloadCartridgeParser;
import com.invoice.visual.ProgressPage;

public class Main {

	private ProgressPage progress = new ProgressPage();
	private DeliveryParser deliveryParser = new DeliveryParser(progress);
	private ReloadCartridgeParser cartridgeParser = new ReloadCartridgeParser(progress);
	private ClientXMLParser clientParser = new ClientXMLParser(progress);
	private InvoiceBuilder builder = new InvoiceBuilder(progress);
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	private String deliveryPath,refillingPath,savePath;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Main m = new Main();
		m.initPath();	
		System.out.println("config parser");
		m.clientParser.parseXMLClients(m.clients);
		System.out.println("delivery parser");
		m.deliveryParser.parse(m.clients,m.deliveryPath);
		System.out.println("cartridge parser");
		m.cartridgeParser.parse(m.clients, m.refillingPath);
		System.out.println("create invoices");
		m.builder.create(m.clients);
		m.progress.getLabel().setForeground(Color.GREEN);
		m.progress.getLabel().setText("Done");
		System.out.println("Done");
	}

	private void initPath() {
		//поставки
		JOptionPane.showMessageDialog(null, "Выбери файл с поставками");
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Файл поставок");
		if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			deliveryPath=fc.getSelectedFile().getAbsolutePath().replace("\\","\\\\");
			System.out.println(deliveryPath);
		}
		
		//заправки
		JOptionPane.showMessageDialog(null, "Выбери файл с заправками");
		fc = new JFileChooser();
		fc.setDialogTitle("Файл заправок");
		if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			refillingPath=fc.getSelectedFile().getAbsolutePath().replace("\\","\\\\");
			System.out.println(refillingPath);
		}
		
		//savePath
		JOptionPane.showMessageDialog(null, "Куда будем сохранять");
		fc = new JFileChooser();
		fc.setDialogTitle("Папка для сохранения");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			savePath=fc.getSelectedFile().getAbsolutePath().replace("\\","\\\\");
			System.out.println(savePath);
		}
	}
}
