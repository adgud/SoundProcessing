package g2;

import g2.filters.ResonantFilter;
import g2.generators.SigGen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.WavGenerator;
import common.WavPlayer;

public class GuiSynthesis extends JFrame {
	public static JTextField tfCutoff;
	public static JTextField tfQ;
	public GuiSynthesis() {
		setTitle("Sound synthesis");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600, 400));
		pack();
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 233, 71);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(20, 20));
		
		JLabel lblResonantLowpassFilter = new JLabel("Resonant low-pass filter parameters:");
		panel.add(lblResonantLowpassFilter, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {140, 60, 0};
		gbl_panel_2.rowHeights = new int[]{20, 20, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel lblCutoffFrequencyhz = new JLabel("Cut-off frequency [Hz]:");
		GridBagConstraints gbc_lblCutoffFrequencyhz = new GridBagConstraints();
		gbc_lblCutoffFrequencyhz.fill = GridBagConstraints.BOTH;
		gbc_lblCutoffFrequencyhz.insets = new Insets(0, 0, 5, 5);
		gbc_lblCutoffFrequencyhz.gridx = 0;
		gbc_lblCutoffFrequencyhz.gridy = 0;
		panel_2.add(lblCutoffFrequencyhz, gbc_lblCutoffFrequencyhz);
		
		tfCutoff = new JTextField();
		tfCutoff.setText("300");
		GridBagConstraints gbc_tfCutoff = new GridBagConstraints();
		gbc_tfCutoff.fill = GridBagConstraints.BOTH;
		gbc_tfCutoff.insets = new Insets(0, 0, 5, 0);
		gbc_tfCutoff.gridx = 1;
		gbc_tfCutoff.gridy = 0;
		panel_2.add(tfCutoff, gbc_tfCutoff);
		tfCutoff.setColumns(10);
		
		JLabel lblQ = new JLabel("Q:");
		GridBagConstraints gbc_lblQ = new GridBagConstraints();
		gbc_lblQ.fill = GridBagConstraints.BOTH;
		gbc_lblQ.insets = new Insets(0, 0, 0, 5);
		gbc_lblQ.gridx = 0;
		gbc_lblQ.gridy = 1;
		panel_2.add(lblQ, gbc_lblQ);
		
		tfQ = new JTextField();
		tfQ.setText("0.8");
		GridBagConstraints gbc_tfQ = new GridBagConstraints();
		gbc_tfQ.fill = GridBagConstraints.BOTH;
		gbc_tfQ.gridx = 1;
		gbc_tfQ.gridy = 1;
		panel_2.add(tfQ, gbc_tfQ);
		tfQ.setColumns(10);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setBounds(283, 276, 67, 23);
		getContentPane().add(btnPlay);
		btnPlay.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
                createAndPlay();
            }
        });    
		
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GuiSynthesis().setVisible(true);
			}
		});
	}
	
	private void createAndPlay() {
		int Fs = 44100;
		
		ArrayList<double[]> waves = new ArrayList<double[]>();
		
		waves.add(SigGen.sineWave((int)(Fs*0.4), 391.9, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.4), 329.6, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.4), 329.6, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.4), 349.6, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.4), 293.7, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.2), 261.6, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.2), 329.6, Fs));
		waves.add(SigGen.sineWave((int)(Fs*0.4), 391.9, Fs));
		
		double[] w = SigGen.concatenateArrays(waves);
		
		int resonantFilterCutoff = Integer.parseInt(GuiSynthesis.tfCutoff.getText());
		double resonantFilterQ = Double.parseDouble(GuiSynthesis.tfQ.getText());
		w = ResonantFilter.resonantLowPassFilter(w, resonantFilterCutoff, resonantFilterQ, Fs);
		
		
		
		
		WavGenerator.generateWavFromArray(w, Fs, "output.wav");
		WavPlayer.playFile("output.wav");		
	}
}
