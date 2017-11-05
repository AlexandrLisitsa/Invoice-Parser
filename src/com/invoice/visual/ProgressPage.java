package com.invoice.visual;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ProgressPage {
	
	private JFrame frame = new JFrame("Invoice Creator");
	private JProgressBar bar = new JProgressBar(0, 100);
	private JProgressBar extendetBar = new JProgressBar();
	private JLabel label = new JLabel();
	
	public void updateBar(int max, int curent,String labeltext,boolean inPercent) {
		label.setText(labeltext);
		bar.setMaximum(max);
		bar.setValue(curent);
		if(inPercent) {
			bar.setStringPainted(true);
			bar.setString("Clients:"+curent+"/"+max);
		}
	}
	
	public void updateExtendetBar(int max,int current,String work) {
		extendetBar.setMaximum(max);
		extendetBar.setValue(current);
		extendetBar.setString(work+": "+current+"/"+max);
	}
	
	public ProgressPage() {
		initFrame();
		initProgressBar();
		initLabel();
		frame.setVisible(true);
	}
	public void initExtBar() {
		extendetBar.setSize(500, 25);
		extendetBar.setLocation(0,162-24);
		extendetBar.setStringPainted(true);
		frame.add(extendetBar);
	}

	private void initLabel() {
		label.setSize(500, 150);
		Font f = new Font("ITALIC", Font.CENTER_BASELINE, 30);
		label.setFont(f);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(label);
	}
	private void initProgressBar() {
		bar.setSize(500, 50);
		bar.setLocation(0, 162);
		frame.add(bar);
	}
	private void initFrame() {
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(516, 250);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setLayout(null);
	}
	public JProgressBar getBar() {
		return bar;
	}
	public void setBar(JProgressBar bar) {
		this.bar = bar;
	}
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	public JLabel getLabel() {
		return label;
	}
	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JProgressBar getExtendetBar() {
		return extendetBar;
	}

	public void setExtendetBar(JProgressBar extendetBar) {
		this.extendetBar = extendetBar;
	}

}
