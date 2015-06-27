package g2;

import common.WavGenerator;
import common.WavPlayer;

import g2.filters.ResonantFilter;
import g2.generators.SigGen;

public class OceanTest {
	static int Fs = 44100;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int numFrames = Fs * 10;
		
		double o1[] = generateOceanWave(numFrames, 0.5, 400, 1000, 2.7, 0.1);
		double o2[] = generateOceanWave(numFrames, 0.22, 200, 1200, 3.7, 0.1);
		double o3[] = generateOceanWave(numFrames, 0.05, 300, 600, 0.8, 0.008);
		
		o2 = SigGen.shift(o2, Fs, 0.5);
		o3 = SigGen.shift(o3, Fs, 1.2);
		
		double o[] = SigGen.mix( new double[][] {o1, o2, o3}, null);
		o = SigGen.amplify(o, 1.5);
		
		WavGenerator.generateWavFromArray(o, Fs, "o.wav");
		WavPlayer.playFile("o.wav");
	}
	
	public static double[] generateOceanWave(int numFrames, double fLFO, int minCutoff, int maxCutoff, double q, double gain) {
		int Fs = 44100;
		double[] whiteNoise = SigGen.whiteNoise(numFrames);
		double[] lfo = SigGen.sineWave(numFrames, fLFO, Fs);
		int amplitiude = (maxCutoff - minCutoff)/2;
		double[] cutoffFrequencies = SigGen.amplify(lfo, amplitiude);
		
		for(int i=0; i<cutoffFrequencies.length; i++) {
			cutoffFrequencies[i] = cutoffFrequencies[i] + minCutoff + amplitiude;
		}
		
		double[] oceanWave = ResonantFilter.resonantLowPassFilter(whiteNoise, cutoffFrequencies, q, Fs);
		oceanWave = SigGen.amplify(oceanWave, gain);
		return oceanWave;
	}
}
