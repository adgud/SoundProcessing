package g2;

import g2.generators.SigGen;

import java.util.ArrayList;

import common.Log;
import common.WavGenerator;
import common.WavPlayer;

public class SynthTest {
	static int Fs = 44100;
	public static void main(String[] args) {
		ArrayList<double[]> waves = new ArrayList<double[]>();
		
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 391.9, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 329.6, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 329.6, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 349.6, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 293.7, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.2), 261.6, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.2), 329.6, Fs));
//		waves.add(SigGen.sineWave((int)(Fs*0.4), 391.9, Fs));
//		
		waves.add(SigGen.synth((int)(Fs*0.4), 391.9, Fs));
		waves.add(SigGen.synth((int)(Fs*0.4), 329.6, Fs));
		waves.add(SigGen.synth((int)(Fs*0.4), 329.6, Fs));
		waves.add(SigGen.synth((int)(Fs*0.4), 349.6, Fs));
		waves.add(SigGen.synth((int)(Fs*0.4), 293.7, Fs));
		waves.add(SigGen.synth((int)(Fs*0.2), 261.6, Fs));
		waves.add(SigGen.synth((int)(Fs*0.2), 329.6, Fs));
		waves.add(SigGen.synth((int)(Fs*0.4), 391.9, Fs));
		
		//WavGenerator.generateWavFromArrays(waves, Fs, "output.wav");
		
		double[] w = SigGen.concatenateArrays(waves);
		//w = ResonantFilter.resonantLowPassFilter(w, 200, 1.8, Fs);
		//double[] mix = SignalGenerator.mix(l,null);
		
		//w = SignalGenerator.squareWave(Fs*2, 300, Fs);
		//w = SignalGenerator.sineWave(Fs*2, 2000, Fs);
//		double[] c1 = SigGen.sawtootheWave((int)(Fs*0.5), 200, Fs);
//		double[] c2 = SigGen.sawtootheWave((int)(Fs*0.5), 201, Fs);
		
		//w = ResonantFilter.resonantLowPassFilter(w, 250, 1.8, Fs);
		
//		w = SigGen.mix(new double[][] {c1, c2}, null);
//		
//		waves.clear();
//		waves.add(w);
//		
//		c1 = SigGen.sawtootheWave(Fs*1, 101, Fs);
//		c2 = SigGen.sawtootheWave(Fs*1, 100, Fs);
//		w = SigGen.mix(new double[][] {c1, c2}, null);
//		
//		w = SigGen.amplify(w, 1.5);
//		
//		waves.add(w);
//		
//		c1 = SigGen.sawtootheWave(Fs*2, 510, Fs);
//		c2 = SigGen.sawtootheWave(Fs*2, 505, Fs);
//		w = SigGen.mix(new double[][] {c1, c2}, null);
//		w = ResonantFilter.resonantLowPassFilter(w, 400, 0.9, Fs);
//		waves.add(w);
//		
//		w = SigGen.concatenateArrays(waves);
		
		WavGenerator.generateWavFromArray(w, 44100, "output2.wav");
		WavPlayer.playFile("output2.wav");
	
		
	}

}
