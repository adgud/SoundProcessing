package t2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import common.WavFile;

public class PhaseSpace {
	
	
	public int k = 20;	// default k value
	public int dimensions = 3;	// default value of how many dimensions are used in calculations
	public double threshold = 0.01;	// threshold below which points will be assumed to be the same
	private final int iterations = 100;	// default number of iterations per file - the bigger, the more accurate fundamental frequency	
	public int segments = 10;	// default value of segments in a file
	
	
	// constructors
	public PhaseSpace() {}
	
	public PhaseSpace(int k) {
		this.k = k;
	}
	
	public PhaseSpace(int k, int dimensions) {
		this.k = k;
		this.dimensions = dimensions;
	}
	
	public PhaseSpace(int k, int dimensions, double threshold) {
		this.k = k;
		this.dimensions = dimensions;
		this.threshold = threshold;
	}

	
	public int analyzeFrames(double[] wave, int sampleRate) {		
		int len = wave.length;
		ArrayList<Double> fundamentals = new ArrayList<Double>();
		int offset = len/iterations;	// offset in a loop for iterations
		
		// create two dimensional array: first index is dimension, second will be filled with values
        double[][] X = new double[dimensions][];
		
		for (int i=0; i<iterations; i++) {
			// starting point of iteration depends on number of dimensions, so that the indices are correct 
			int start = (dimensions-1)*k + i*offset;
			// allocate space for number of frames to be looped over
	       	for (int d=0; d<dimensions; d++) {
	       		X[d] = new double[len - start];
	       	}
	       	
	       	// fill in coordinates
	       	for (int j=0; j<(len-start); j++) {
	       		// for each dimension (axis)
	       		for (int d=0; d<dimensions; d++) {
	       			X[d][j] = wave[start+j - d*k];
	        	}
	       		
	       		if (j>30) {	// dont check if loop is closed at the begging where values can be similar
	       			 
	       			// calculate differences between current point and starting point of the iteration for every dimension (axis)
	       			double[] diffs = new double[dimensions];
	       			for (int d=0; d<dimensions; d++) {
	       				diffs[d] = Math.abs( X[d][j] - wave[start-d*k] );
			       	}
	       			 
	       			// see how many differences fulfill the condition of having similar value
	       			int c = 0;
	       			for (int d=0; d<dimensions; d++) {
	       				if (diffs[d] < threshold)
			        	c++;
			        }
	       			 
	       			// if all differences fulfill the conditions, it means that the starting point was reached
	       			// calculate fundamental frequency, add it to list and start another iteration for this file
	       			if (c == dimensions) {
		       			double f0 = ((double)(sampleRate)) / j;
		       			fundamentals.add(f0);
		       			break;
	       			}
	       		}
	       		//log("f0 not found");
	       	 }
		}
		
		// sort fundamentals to find median
		Collections.sort(fundamentals);
		
		// calculate median fundamental frequency
        double median;
        if (fundamentals.size() % 2 == 1)	// if number of elements is uneven, median is middle element
        	median = fundamentals.get(fundamentals.size() / 2 );	
        else	// else, its average of two middle elements;
        	median = (fundamentals.get(fundamentals.size()/2) + fundamentals.get(fundamentals.size()/2+1)) / 2;	
		
		return (int) median;
	}
	
	public ArrayList<Integer> analyzeFile(String filename, int segments) {
		
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
	
	public int analyzeFile(String filename) {
		return analyzeFile(filename,1).get(0);
	}
	
	@SuppressWarnings("unused")
	private static void log(String msg) {
		System.out.println(msg);
	}

}
