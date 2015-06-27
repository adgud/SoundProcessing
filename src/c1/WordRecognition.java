package c1;

import common.Log;
import common.Wav;
import f1.Fourier;
//import g2.generators.SigGen;

public class WordRecognition {

	private static String path = "/home/adam/CloudStation/csit/sp/speech/";
	
	private static int Fs = 44100;
	
	private static String[] words = {
		"zero1.wav",
		"zero2.wav",
		"zero3.wav",
		"one1.wav",
		"one2.wav",
		"one3.wav",
		"two1.wav",
		"two2.wav",
		"two3.wav",
		"three1.wav",
		"three2.wav",
		"three3.wav",
		"four1.wav",
		"four2.wav",
		"four3.wav",
		"five1.wav",
		"five2.wav",
		"five3.wav",
		"six1.wav",
		"six2.wav",
		"six3.wav",
		"seven1.wav",
		"seven2.wav",
		"seven3.wav",
		"eight1.wav",
		"eight2.wav",
		"eight3.wav",
		"nine1.wav",
		"nine2.wav",
		"nine3.wav",
		"parrot1.wav",
		"parrot2.wav",
		"parrot3.wav",
		"horse1.wav",
		"horse2.wav",
		"horse3.wav"};	
	public static void main(String[] args) {

		
		//int w = 5;
		
		for (int w=10; w<12; w++) {
			
			//Log.i(w + ":");
			Log.i("");
			
			Wav wave1 = new Wav(path + words[3*w]);
			Wav wave2 = new Wav(path + words[3*w+1]);
			Wav wave3 = new Wav(path + words[3*w+2]);
	
			
			double[][] v1 = vectors(wave1, 25, 100, 2);
			double[][] v2 = vectors(wave2, 25, 100, 2);
			double[][] v3 = vectors(wave3, 25, 100, 2);
			
			Log.i(DTWdistance(v1, v2));
			Log.i(DTWdistance(v1, v3));
			Log.i(DTWdistance(v2, v3));
		}
		
		
	}
	
	public static double mi(double m) {
		return 700 * (Math.pow(10, (m/2395)) - 1);
	}
	
	public static double H(int k, double f, int d) {
		double c = mi(k*d);
		double l = mi((k-1)*d);
		double r = mi((k+1)*d);
		if (f >= l && f <= c) {
			return (f-l)/(c-l);
		} else if (f >= c && f <= r) {
			return (r-f)/(r-c);
		} else {
			return 0;
		}
	}

	public static double S(int k, int d, double[] dft, int Fs) {
		double sum = 0;
		for (int i=0; i<dft.length/2; i++) { // TODO check
			sum += dft[i] * H(k, Fs/dft.length * i, d);
			//Log.e(dft[i] * H(k, Fs/dft.length * i, d));
		}
		//Log.e("S sum: " + sum);
		if (sum == 0) sum = 0.01;
		return sum;
	}
	
	public static double C(int n, int K, int d, double[] dft, int Fs, double g) {
		double sum = 0;
		for (int k=0; k<K; k++) {
			//Log.e("" + Math.pow(Math.log(S(k,d,dft,Fs)), g) * Math.cos(2*Math.PI * ((2*k+1)*n)/(4*K)));
			//Log.e("" + Math.pow(Math.log(S(k,d,dft,Fs)), g));
			sum += Math.pow(Math.log(S(k,d,dft,Fs)), g) * Math.cos(2*Math.PI * ((2*k+1)*n)/(4*K));
		}
		//Log.e("C sum: " + sum);
		return sum;
	}
	
	public static double[] getCoefficients(int num, int K, int d, double[] dft, int Fs, double g) {
		double[] coeffs = new double[num];
		for (int i=0; i<num; i++) {
			coeffs[i] = C(i, K, d, dft, Fs, g);
		}
		return coeffs;
	}
	
	public static double DTWdistance(double[][] stored, double[][] input) {
		double[][] dtw = new double[stored.length][input.length];
		
//		for (int i=1; i<stored.length; i++) {
//			dtw[i][0] = Double.POSITIVE_INFINITY;
//		}
//		for (int i=1; i<input.length; i++) {
//			dtw[0][i] = Double.POSITIVE_INFINITY;
//		}
		
		int w = stored.length/1;
		
		for (int i=0; i<stored.length; i++) {
			for (int j=0; j<input.length; j++) {
				dtw[i][j] = Double.POSITIVE_INFINITY;
			}
		}
		
		dtw[0][0] = 0;
		
		for (int i=1; i<stored.length; i++) {
			//for (int j=1; j<input.length; j++) {
			for (int j=max(1, i-w); j<min(input.length, i+w); j++) {
				double cost = dist(stored[i], input[j]);
				dtw[i][j] = cost + min(dtw[i-1][j], dtw[i][j-1], dtw[i-1][j-1]);
			}
		}
		
		return dtw[stored.length-1][input.length-1];
	}
	
	public static double min(double a, double b, double c) {
		if (a < b && a < c)
			return a;
		else if (b < a && b < c)
			return b;
		else 
			return c;
	}
	
	public static int min(int a, int b) {
		if (a<b) return a;
		else return b;
	}
	
	public static int max(int a, int b) {
		if (a>b) return a;
		else return b;
	}
	
	public static double dist(double[] a, double[] b) {
		double sum = 0.0;
		for (int i=0; i<a.length; i++) {
			sum += Math.pow(Math.abs(a[i]-b[i]), 2);
		}
		return Math.sqrt(sum);
	}
	
	public static double[][] vectors(Wav wave, int k, int d, double g) {
		wave.removeSilence(0.05);
		double[][] fragments = wave.split(10);	// milliseconds		
		
		double[][] vectors = new double[fragments.length][12];
		for (int i=0; i<fragments.length; i++) {
			for (int j=0; j<12; j++) {
				double[] w = Fourier.hammingWindow(fragments[i]);
				double[] dft = Fourier.magnitudeFFT(w);
				dft = Fourier.bandpassFilter(dft, 20, 20000);
				//Log.e(t.length+"");
				vectors[i][j] = C(j,k,d,dft,Fs,g);
			}
		}
		
		return vectors;
	}
}
