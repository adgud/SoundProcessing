package c1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import common.Log;
import common.Wav;

import f1.Fourier;
//import g2.generators.SigGen;

public class WordRecognition {

	private static String path = "/home/adam/CloudStation/csit/sp/speech/words/";
	
	private static int Fs = 44100;
	
	public static void main(String[] args) {

		int k = 30, d = 100;
		double gamma = 2;
		int millis = 25;
		double band = 0.4;	// 0.0 - 1.0
				
		String r = performComparison(path+"monkey.wav", path, k, d, gamma, millis, band);
		Log.i(r);
		
	}
	
	public static double mi(double m) {
		return 700 * (Math.pow(10, (m/2595.0)) - 1);
	}
	
	public static double H(int k, double f, int d) {
		double c = mi(k*d);
		double l = mi((k-1)*d);
		double r = mi((k+1)*d);
		if (f >= l && f <= c) {
			return (f-l)/(c-l);
		} else if (f > c && f <= r) {
			return (r-f)/(r-c);
		} else {
			return 0.0;
		}
	}

	public static double S(int k, int d, double[] dft, int Fs) {
		double sum = 0.0;
		for (int i=0; i<dft.length; i++) { // TODO check
			double s = dft[i] * H(k, Fs/dft.length * i, d);
			sum += s;
			//Log.e(s);
		}
		//Log.e("S sum: " + sum);
		//if (sum == 0) sum = 0.00001;
		return sum;
	}
	
	public static double C(int n, int K, int d, double[] dft, int Fs, double g) {
		double sum = 0;
		for (int k=0; k<=K-1; k++) {
			//Log.e("" + Math.pow(Math.log(S(k,d,dft,Fs)), g) * Math.cos(2*Math.PI * ((2*k+1)*n)/(4*K)));
			//Log.e("" + Math.pow(Math.log(S(k,d,dft,Fs)), g));
			double S = S(k,d,dft,Fs);
			double Sp = Math.pow(Math.log(S), g);
			double cos = Math.cos(/*Math.toRadians(*/ 2*Math.PI*(2*k+1)*n/4/K /*)*/);	//2*Math.PI*((2*k+1)*n)/(4*K) ));
			//sum += Math.pow(Math.log(S(k,d,dft,Fs)), g) * Math.cos(Math.toRadians(2*Math.PI * ((2*k+1)*n)/(4*K)));
			sum += Sp*cos;
			//Log.e("S="+S+", Sp="+Sp+", cos="+cos);
		}
		//Log.e("C sum: " + sum);
		return sum;
	}
	
	public static double[] getCoefficients(int num, int K, int d, double[] dft, int Fs, double g) {
		double[] coeffs = new double[num];
		for (int i=1; i<=num; i++) {
			coeffs[i-1] = C(i, K, d, dft, Fs, g);
			//coeffs[i-1] = round(C(i, K, d, dft, Fs, g),2);
		}
		return coeffs;
	}
	
	public static double[][] createDTWmatrix(double[][] mfcc1, double[][] mfcc2) {
		double[][] dtw = new double[mfcc1.length][mfcc2.length];
		
//		for (int i=1; i<stored.length; i++) {
//			dtw[i][0] = Double.POSITIVE_INFINITY;
//		}
//		for (int i=1; i<input.length; i++) {
//			dtw[0][i] = Double.POSITIVE_INFINITY;
//		}
		
		//int r = 2;//mfcc1.length/6;
		
		for (int i=0; i<mfcc1.length; i++) {
			for (int j=0; j<mfcc2.length; j++) {
				dtw[i][j] = Double.POSITIVE_INFINITY;
			}
		}
		
		dtw[0][0] = 0;
		double gmax = 0.0;
		
		for (int i=1; i<mfcc1.length; i++) {
			for (int j=1; j<mfcc2.length; j++) {
			//for (int j=max(1, i-w); j<min(mfcc2.length, i+w); j++) {
				
				//if ( j-r > i && i > j+r ) {//Math.abs(mfcc2.length/2 - j) > r ) {	// not within the band
				//	dtw[i][j] = Double.POSITIVE_INFINITY;
				//} else {	// within the band				
					double cost = dist(mfcc1[i], mfcc2[j]);
					dtw[i][j] = cost + min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]);
					
					if (dtw[i][j] > gmax) gmax = dtw[i][j];
					
					// for readability
					dtw[i][j] = round(dtw[i][j],2);
				//}
			}
		}
		
		return dtw;
	}	
	
	public static double[][] findBestPath(double[][] dtw, double bandCoeff) {
		double[][] path = new double[dtw.length][dtw[0].length];
		
		for (double[] d : path) {
			for (double dd : d) {
				dd = 0.0;
			}
		}
		int M = dtw.length, N = dtw[0].length;
		int m = dtw.length-2, n=dtw[0].length-2;
		
		int w = (int) (N * bandCoeff);
		
		//double pathCost = 0.0;
		
		while (m != 0 && n != 0) {
			//double currentCost = dtw[m][n];
			path[m][n] = 1.0;
			
			//pathCost += dtw[m][n];
			
			double leftCost = dtw[m][n-1];
			double upCost = dtw[m-1][n];
			double diagCost = dtw[m-1][n-1];
			double bestCost = min(leftCost, upCost, diagCost);
			if (bestCost == leftCost && Math.abs(n - N/2) < w && n > 0) {
				n--;
			} else if (
				bestCost == upCost && Math.abs(m - M/2) < w && m > 0) {
				m--;
			} else {
				m--;
				n--;
			}
		}		
		//Log.e(round(pathCost/(M+N),0));
		return path;
	}
	
	public static double calculateCost(double[][] dtw, double[][] path) {
		double cost = 0.0;
		for (int i=0; i<dtw.length; i++) {
			for (int j=0; j<dtw[0].length; j++) {
				if (path[i][j] != 0.0)
					cost += dtw[i][j];
			}
		}
		return cost/(dtw.length + dtw[0].length);
	}
	
	
	public static double min(double... values) {
		double min = Double.POSITIVE_INFINITY;
		for (int i=0; i<values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}
	
	public static int max(int... values) {
		int max = Integer.MIN_VALUE;//Double.NEGATIVE_INFINITY;
		for (int i=0; i<values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}
	
	public static double round(double value, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, precision);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
	
	public static double dist(double[] a, double[] b) {
		double sum = 0.0;
		for (int i=0; i<a.length; i++) {
			sum += Math.pow(a[i]-b[i], 2);
		}
		return Math.sqrt(sum);
	}
	
	public static double[][] getMFCCs(Wav wave, int k, int d, double g, int millis) {
		//wave.removeSilence(0.05);
		double[][] fragments = wave.split(millis);	// milliseconds		
		
		double[][] vectors = new double[fragments.length][12];
		for (int i=0; i<fragments.length; i++) {
//			for (int j=0; j<12; j++) {
				double[] w = Fourier.hanningWindow(fragments[i]);
				double[] dft = Fourier.magnitudeFFT(w);
//				//dft = Fourier.bandpassFilter(dft, 20, 20000);
//				//Log.e(t.length+"");
//				vectors[i][j] = C(j,k,d,dft,Fs,g);
//			}
			vectors[i] = getCoefficients(12, k, d, dft, Fs, g);
		}
		
		return vectors;
	}

	public static String[] getFilenames(String path) {
		File folder = new File(path);
        File[] files = folder.listFiles();
        String[] filenames = new String[files.length];
        for (int i=0; i<filenames.length; i++) {
        	filenames[i] = files[i].getPath();
        }
        return filenames;
	}

	public static String performComparison(String filename, String samplesPath) {
		
		// use default values
		int k = 30, d = 100;
		double gamma = 2;
		int millis = 25;
		double band = 0.4;	// 0.0 - 1.0
		
		return performComparison(filename, samplesPath, k, d, gamma, millis, band);
	}
	
	public static String performComparison(String filename, String samplesPath, int k, int d, double gamma, int millis, double band) {
		
		class Result {
			public String name;
			public double cost;
			public Result(String n, double c) {
				this.name = n;
				this.cost = c;
			}
		}
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		String result = "";
		
		Wav inputWav = new Wav(filename);
		double[][] mfccInput = getMFCCs(inputWav, k, d, gamma, millis);
		
		for (String sample : getFilenames(samplesPath)) {
						
			Wav testedWav = new Wav(sample);			
	
			double[][] mfccSample = getMFCCs(testedWav, k, d, gamma, millis);			
			double[][] dtw = createDTWmatrix(mfccSample, mfccInput);
			//double dist = dtw[dtw.length-2][dtw[0].length-2];
			double[][] path = findBestPath(dtw, band);
			double cost = round(calculateCost(dtw, path),2);
			String name = sample.substring(sample.lastIndexOf("/")+1,sample.length());
			results.add(new Result(name, cost));
		}
		
		Collections.sort(results, new Comparator<Result>() {
			@Override
			public int compare(Result r1, Result r2) {
				return (int) (r1.cost - r2.cost);
			}
			
		});
		
		for (Result r : results) {
			result += r.cost + "\t" + r.name + "\n";
		}
		
		return result;
	}
	
	
}
