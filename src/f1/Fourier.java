package f1;

import java.io.File;
import java.util.ArrayList;

import org.jtransforms.fft.DoubleFFT_1D;

import common.Log;
import common.WavFile;

public class Fourier {
	
	// applies Hamming window to input array
	public static double[] hammingWindow(double[] in) {
		int len = in.length;
		double[] out = new double[len];		
		for (int i=0; i<len; i++) {
			out[i] = in[i] * 0.53836 - 0.46164 * Math.cos( 2 * Math.PI * (i+1) / (len - 1) );
		}		
		return out;
	}
	
	// calculates and returns array of magnitudes of Discrete Fourier Transform of input array
	public static double[] magnitudeDFT(double[] in) {		
		int len = in.length;
		double[] out = new double[len/2];		
		for (int k=0; k<len/2; k++) {		
			double resum = 0.0;
			double imsum = 0.0;			
			for (int t=0; t<len; t++) {
				double angle = 2 * Math.PI * t * k / len;
				resum += in[t] * Math.cos(angle);
				imsum += in[t] * Math.sin(angle);
			}			
			out[k] = Math.sqrt(Math.pow(resum,2) + Math.pow(imsum,2));
		}
		return out;
	}
	
	// calculates and returns array of magnitudes of Fast Fourier Transform of input array
	public static double[] magnitudeFFT(double[] wave) {		
		int len = wave.length;
		double[] in = wave.clone();
		double[] out = new double[len/*/2*/];
		DoubleFFT_1D dfft = new DoubleFFT_1D(len);
		dfft.realForward(in);		
		for (int k=0; k<len/2; k++) {			
			double re = in[k*2];
			double im = in[k*2+1];
			out[k] = Math.sqrt(Math.pow(re,2) + Math.pow(im,2));
		}
		return out;
	}
	
	// return maximum value of input array
	public static double getMax(double[] in) {
		double max = 0.0;
			for (int i=1; i<in.length; i++) {
				if (max<in[i]) {
					max=in[i];
				}
			}
		return max;
	}
	
	public static double[] transformDFT(double[] dft) {
		double[] out = new double[44100/2];
		for (double d : out) {
			//d = 0.0;
		}		
		for (int i=0; i<dft.length; i++) {
			out[i*44100/dft.length/2] = dft[i];
			//Log.e(i + "");
		}		
		return out;
	}
	
	public static double[] bandpassFilter(double[] dft, int low, int high) {
		double[] out = dft.clone();
		for (int i=0; i<out.length; i++) {
			if (i*44100/dft.length/2 < low || i*44100/dft.length/2 > high)
				out[i] = 0.0;
		}
		return out;
	}
	
	public static ArrayList<Integer> getDominants(double[] fft, int samplingRate, int num) {
		ArrayList<Integer> dominants = new ArrayList<Integer>();
		double th = 0.5;	// starting threshold
		while (dominants.size() < num) {	// if we have less than 10 dominant frequencies
			dominants.clear();	// remove old dominants
			double cutoff = th * getMax(fft);	// calculate new cutoff magnitude
			for (int i=0; i<fft.length; i++) {	// iterate over fft magnitudes
				if (fft[i] > cutoff)	// if magnitude is greater than cutoff magniture
					dominants.add(i*samplingRate/fft.length/2);	// add frequency to list of dominant frequencies
			}
			th = th * 0.9;	// decrease threshold for next iteration
		}
		return dominants;
	}
	
	// chooses fundamental frequency (Schroeder's histogram) from list of dominant frequencies with given tolerance
	public static int getFundamental(ArrayList<Integer> dominants, int tolerance) {
		ArrayList<Integer> dominantCounts = new ArrayList<Integer>();
		
		for (int i=0; i<dominants.size(); i++) {
			int current = dominants.get(i);
			int count = 0;
			for (int j=i; j<dominants.size(); j++) {
				if (dominants.get(j) % current <= tolerance) {
					count++;
				}
			}
			dominantCounts.add(count);
			//System.err.println(">" + count);
		}
		
		int max = 0, maxIndex = 0;
		for (int i=0; i<dominantCounts.size(); i++) {
			if (dominantCounts.get(i) > max) {
				max = dominantCounts.get(i);
				maxIndex = i;
			}
		}
		
		int sum = 0, count = 0;
		for (int i : dominants) {
			if (Math.abs(i - dominants.get(maxIndex)) < tolerance) {
				sum += i;
				count++;
			}
		}	
		
		return sum/count;
	}
	
	// returns fundamental frequency of given wave wih sample rate
	public static int analyzeFrames(double[] wave, int sampleRate) {
		double[] win = Fourier.hammingWindow(wave);	// apply window function in time domain
    	double[] fft = Fourier.magnitudeFFT(win);	// calculate fft magnitudes
    	fft = Fourier.bandpassFilter(fft, 20, 20000);	// apply bandpass filter 20 Hz - 20 kHz
		ArrayList<Integer> dominants = getDominants(fft, sampleRate, 10);		
		return getFundamental(dominants, 10);
	}	
	
	// returns list of fundamental frequencies for file split into segments of equal lengths
	public static ArrayList<Integer> analyzeFile(String filename, int segments) {
		ArrayList<Integer> fundamentals = new ArrayList<Integer>();
		
		try {			
			// open the wave file
	        WavFile wavFile = WavFile.openWavFile(new File(filename));	      
	        // get sample rate and number of frames in file
	        int sampleRate = (int) wavFile.getSampleRate();	         
	        int numFrames = (int) wavFile.getNumFrames();	         
	        // create array which will hold all frames of wave file
	        double[] wave = new double[numFrames];	         
	        // put all frames from wave files into the array
	        wavFile.readFrames(wave, numFrames);	         
	        // close wave file, it is not needed anymore
	        wavFile.close();
	        
	        
	        int segmentFrames = numFrames/segments;
	        for (int i=0; i<segments; i++) {
	        	double[] segmentWave = new double[segmentFrames];
	        	for (int j=0; j<segmentFrames; j++) {
	        		segmentWave[j] = wave[i*segmentFrames + j];
	        	}
	        	fundamentals.add(analyzeFrames(segmentWave, sampleRate));
	        }	
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}		
		
		return fundamentals;
	}
	
	// wrapper for single segment (full) file analysis
	public static int analyzeFile(String filename) {
		return analyzeFile(filename,1).get(0);
	}
}
