package common;

import java.io.File;
import java.util.ArrayList;

public class WavGenerator {
		
	public static void generateWav(int frequency, String filename) {
		int DURATION = 1;	// in seconds
		int SAMPLING_RATE = 44100;
		int numFrames = DURATION*SAMPLING_RATE;
		double[] wave = new double[numFrames];
		
		
        try {
        	// open new wav file
			WavFile wavFile = WavFile.newWavFile(new File(filename), 1, (long) (numFrames), 16, SAMPLING_RATE);
			
			for (int i=0; i<numFrames; i++) {
				wave[i] = Math.sin(2.0 * Math.PI * frequency * i / SAMPLING_RATE);
			}
			
			wavFile.writeFrames(wave, numFrames);
			
			wavFile.close();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	public static void generateWavFromFundamentals(ArrayList<Integer> f0s, int segdur, String filename) {		
		int num = f0s.size();
		int numframes = segdur * num;
		System.out.println(numframes);
		
		double[] wave = new double[numframes];		
		try {			
			WavFile wavFile = WavFile.newWavFile(new File(filename), 1, (long) (numframes), 16, 44100);
			//double[] seg = new double[segdur];
			for (int i=0; i<num; i++) {
				double[] seg = new double[segdur];
				for (int j=0; j<segdur; j++) {
					seg[j] = Math.sin(2.0 * Math.PI * f0s.get(i) * j / 44100);
				}
				for (int j=0; j<segdur; j++) {
					wave[i*segdur+j] = seg[j];
				}
			}			
			wavFile.writeFrames(wave, numframes);
			wavFile.close();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void generateWavFromArray(double[] wave, int samplingRate, String filename) {
		try {			
			WavFile wavFile = WavFile.newWavFile(new File(filename), 1, (long) (wave.length), 16, samplingRate);
			wavFile.writeFrames(wave, wave.length);
			wavFile.close();			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void generateWavFromArrays(ArrayList<double[]> waves, int samplingRate, String filename) {
		int totalFrames = 0;
		for(int n=0; n<waves.size(); n++) 
			totalFrames += waves.get(n).length;
		double[] wave = new double[totalFrames];
		
		int offset = 0;
		for(int n=0; n<waves.size(); n++) {
			// TODO concatenate arrays
			System.arraycopy((Object)waves.get(n), 0, (Object)wave, offset, waves.get(n).length);
			offset += waves.get(n).length;
		}
		
		generateWavFromArray(wave, samplingRate, filename);
		
//		for (double w : wave) {
//			System.out.println(w);
//		}
		
	}
}
