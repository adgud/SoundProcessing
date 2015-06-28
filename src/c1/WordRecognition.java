package c1;

import java.util.HashMap;

import common.Log;
import common.Wav;
import f1.Fourier;
//import g2.generators.SigGen;

public class WordRecognition {

	private static String path = "/home/adam/CloudStation/csit/sp/speech/words/";
	
	private static int Fs = 44100;
	private static String[] words = {
		"dog1.wav",
		"dog2.wav",
		"dog3.wav",
		"dog.wav",
		"fast2.wav",
		"fast3.wav",
		"horse1.wav",
		"horse2.wav",
		"horse3.wav",
		"horse.wav",
		"monkey1.wav",
		"monkey2.wav",
		"monkey3.wav",
		"monkey.wav",
		"parrot1.wav",
		"parrot2.wav",
		"parrot3.wav",
		"parrot.wav",
	};
	
	private static HashMap<String, String[]> wordMap = new HashMap<String, String[]>();
	static {
		wordMap.put("dog", new String[] {"dog1.wav", "dog2.wav", "dog3.wav", "dog.wav"});
		wordMap.put("fast", new String[] {"fast2.wav", "fast3.wav"});
		wordMap.put("horse", new String[] {"horse1.wav", "horse2.wav", "horse3.wav", "horse.wav"});
		wordMap.put("monkey", new String[] {"monkey1.wav", "monkey2.wav", "monkey3.wav", "monkey.wav"});
		wordMap.put("parrot", new String[] {"parrot1.wav", "parrot2.wav", "parrot3.wav", "parrot.wav"});
	}
	
	
	public static void main(String[] args) {

		int k = 30, d = 100, gamma = 2;
		int selWord = 4;
		
		for (int w=0; w<words.length; w++) {
			
			//Log.i(w + ":");
			//Log.i(words[w] + " vs " + words[selWord] + ":");
			
			Wav wave1 = new Wav(path + words[w]);
			Wav wave2 = new Wav(path + words[selWord]);
	
			double[][] v1 = vectors(wave1, k, d, gamma);
			double[][] v2 = vectors(wave2, k, d, gamma);
			
			Log.i(words[w] + " vs " + words[selWord] + ": " + DTWdistance(v1, v2));
			for (double[] dd : v1) {
				for (double ddd : dd) {
					System.out.print(ddd + " ");
				}
				System.out.println();
			}
		}
		
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
	
	public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
	
	
	public static double dist(double[] a, double[] b) {
		double sum = 0.0;
		for (int i=0; i<a.length; i++) {
			sum += Math.pow(Math.abs(a[i]-b[i]), 2);
		}
		return Math.sqrt(sum);
	}
	
	public static double[][] vectors(Wav wave, int k, int d, double g) {
		//wave.removeSilence(0.05);
		double[][] fragments = wave.split(15);	// milliseconds		
		
		double[][] vectors = new double[fragments.length][12];
		for (int i=0; i<fragments.length; i++) {
//			for (int j=0; j<12; j++) {
				double[] w = Fourier.hanningWindow(fragments[i]);
				double[] dft = Fourier.magnitudeFFT(w);
//				//dft = Fourier.bandpassFilter(dft, 20, 20000);
//				//Log.e(t.length+"");
//				vectors[i][j] = C(j,k,d,dft,Fs,g);
//			}
			vectors[i] = getCoefficients(11, k, d, dft, Fs, g);
		}
		
		return vectors;
	}
}
