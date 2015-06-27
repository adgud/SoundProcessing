package g2;

import java.awt.Dimension;

import g2.filters.ResonantFilter;
import g2.generators.SigGen;

import javax.swing.JFrame;

import common.WavGenerator;
import common.WavPlayer;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class GuiOcean extends JFrame {
	private JTextField tfDuration;
	private JTextField tfMaxCutoff;
	private JTextField tfMinCutoff;
	private JTextField tfLFO;
	private JTextField tfQ;
	public GuiOcean() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(320, 230));
		pack();
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {160, 60};
		gbl_panel.rowHeights = new int[] {20, 20, 20, 20, 20};
		gbl_panel.columnWeights = new double[]{0.0, 1.0};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panel.setLayout(gbl_panel);
		
		JLabel lblDuration = new JLabel("Duration [s]:");
		GridBagConstraints gbc_lblDuration = new GridBagConstraints();
		gbc_lblDuration.anchor = GridBagConstraints.EAST;
		gbc_lblDuration.insets = new Insets(0, 0, 5, 5);
		gbc_lblDuration.gridx = 0;
		gbc_lblDuration.gridy = 0;
		panel.add(lblDuration, gbc_lblDuration);
		
		tfDuration = new JTextField();
		tfDuration.setText("5");
		GridBagConstraints gbc_tfDuration = new GridBagConstraints();
		gbc_tfDuration.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDuration.insets = new Insets(0, 0, 5, 5);
		gbc_tfDuration.gridx = 1;
		gbc_tfDuration.gridy = 0;
		panel.add(tfDuration, gbc_tfDuration);
		tfDuration.setColumns(10);
		
		JLabel lblMaxCutoffhz = new JLabel("Max cutoff [Hz]:");
		GridBagConstraints gbc_lblMaxCutoffhz = new GridBagConstraints();
		gbc_lblMaxCutoffhz.anchor = GridBagConstraints.EAST;
		gbc_lblMaxCutoffhz.insets = new Insets(0, 0, 5, 5);
		gbc_lblMaxCutoffhz.gridx = 0;
		gbc_lblMaxCutoffhz.gridy = 1;
		panel.add(lblMaxCutoffhz, gbc_lblMaxCutoffhz);
		
		tfMaxCutoff = new JTextField();
		tfMaxCutoff.setText("500");
		GridBagConstraints gbc_tfMaxCutoff = new GridBagConstraints();
		gbc_tfMaxCutoff.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfMaxCutoff.insets = new Insets(0, 0, 5, 5);
		gbc_tfMaxCutoff.gridx = 1;
		gbc_tfMaxCutoff.gridy = 1;
		panel.add(tfMaxCutoff, gbc_tfMaxCutoff);
		tfMaxCutoff.setColumns(10);
		
		JLabel lblMinCutoffhz = new JLabel("Min cutoff [Hz]:");
		GridBagConstraints gbc_lblMinCutoffhz = new GridBagConstraints();
		gbc_lblMinCutoffhz.anchor = GridBagConstraints.EAST;
		gbc_lblMinCutoffhz.insets = new Insets(0, 0, 5, 5);
		gbc_lblMinCutoffhz.gridx = 0;
		gbc_lblMinCutoffhz.gridy = 2;
		panel.add(lblMinCutoffhz, gbc_lblMinCutoffhz);
		
		tfMinCutoff = new JTextField();
		tfMinCutoff.setText("300");
		GridBagConstraints gbc_tfMinCutoff = new GridBagConstraints();
		gbc_tfMinCutoff.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfMinCutoff.insets = new Insets(0, 0, 5, 5);
		gbc_tfMinCutoff.gridx = 1;
		gbc_tfMinCutoff.gridy = 2;
		panel.add(tfMinCutoff, gbc_tfMinCutoff);
		tfMinCutoff.setColumns(10);
		
		JLabel lblLfoFrequencyhz = new JLabel("LFO frequency [Hz]:");
		GridBagConstraints gbc_lblLfoFrequencyhz = new GridBagConstraints();
		gbc_lblLfoFrequencyhz.anchor = GridBagConstraints.EAST;
		gbc_lblLfoFrequencyhz.insets = new Insets(0, 0, 5, 5);
		gbc_lblLfoFrequencyhz.gridx = 0;
		gbc_lblLfoFrequencyhz.gridy = 3;
		panel.add(lblLfoFrequencyhz, gbc_lblLfoFrequencyhz);
		
		tfLFO = new JTextField();
		tfLFO.setText("0.5");
		GridBagConstraints gbc_tfLFO = new GridBagConstraints();
		gbc_tfLFO.insets = new Insets(0, 0, 5, 5);
		gbc_tfLFO.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfLFO.gridx = 1;
		gbc_tfLFO.gridy = 3;
		panel.add(tfLFO, gbc_tfLFO);
		tfLFO.setColumns(10);
		
		JLabel lblQ = new JLabel("Q:");
		GridBagConstraints gbc_lblQ = new GridBagConstraints();
		gbc_lblQ.anchor = GridBagConstraints.EAST;
		gbc_lblQ.insets = new Insets(0, 0, 0, 5);
		gbc_lblQ.gridx = 0;
		gbc_lblQ.gridy = 4;
		panel.add(lblQ, gbc_lblQ);
		
		tfQ = new JTextField();
		tfQ.setText("0.8");
		GridBagConstraints gbc_tfQ = new GridBagConstraints();
		gbc_tfQ.insets = new Insets(0, 0, 0, 5);
		gbc_tfQ.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfQ.gridx = 1;
		gbc_tfQ.gridy = 4;
		panel.add(tfQ, gbc_tfQ);
		tfQ.setColumns(10);
		
		JButton btnPlay = new JButton("Play");
		getContentPane().add(btnPlay, BorderLayout.SOUTH);
		btnPlay.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				createAndPlay(
						Integer.parseInt(tfDuration.getText()), 
						Double.parseDouble(tfLFO.getText()), 
						Integer.parseInt(tfMinCutoff.getText()),
						Integer.parseInt(tfMaxCutoff.getText()), 
						Double.parseDouble(tfQ.getText()));			
			}
		});
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GuiOcean().setVisible(true);
			}
		});
	}
	
	private static void createAndPlay(int duration, double fLFO, int minCutoff, int maxCutoff, double q) {
		int Fs = 44100;
		int numFrames = duration * Fs; // 5 seconds
		double[] whiteNoise = SigGen.whiteNoise(numFrames);
		double[] lfo = SigGen.sineWave(numFrames, fLFO, Fs);
		int amplitiude = (maxCutoff - minCutoff)/2;
		double[] cutoffFrequencies = SigGen.amplify(lfo, amplitiude);
		
		for(int i=0; i<cutoffFrequencies.length; i++) {
			cutoffFrequencies[i] = cutoffFrequencies[i] + minCutoff + amplitiude;
		}
		
		double[] oceanWave = ResonantFilter.resonantLowPassFilter(whiteNoise, cutoffFrequencies, q, Fs);
					
		
		WavGenerator.generateWavFromArray(oceanWave, Fs, "ocean.wav");
		WavPlayer.playFile("ocean.wav");	
	}
}
