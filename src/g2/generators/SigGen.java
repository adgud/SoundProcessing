package g2.generators;

import g2.filters.ResonantFilter;

import java.util.ArrayList;

import common.Log;

public class SigGen {

	
	public static double[] sineWave(int length, double frequency, int samplingFrequency) {
		double[] out = new double[length];
		for (int i = 0; i < length; i++) {
			out[i] = Math.sin(2 * Math.PI * frequency / samplingFrequency * i);
		}
		return out;
	}
	
	public static double[] whiteNoise(int length) {
		double[] out = new double[length];
		for (int i = 0; i < length; i++) {
			out[i] = Math.random() * 2 - 1.0;
		}
		return out;
	}
	
	public static double[] redNoise(int length, int m) {
		double out[] = whiteNoise(length);
		
		for (int i=0; i<length/m; i++) {
			int k = i*m;
			int l = ((i+1)*m <= length) ? (i+1)*m : length;
			double d = (out[l] - out[k])/((double)(m));
			for (int j=1; j<m; j++) {
				out[k+j] = out[k] + d*j;
			}
		}		
		return out;
	}
	
	public static double[] triangleWave(int length, double frequency, int samplingFrequency) {
		double[] out = new double[length];
		out[0] = 0.0;
		for (int i=1; i<length; i++) {
			double sum = 0;
			for (int k=0; k<10; k++) {
				sum += Math.pow(-1.0, k) * Math.sin(2 * Math.PI * (2 * k +1) * frequency/samplingFrequency * i);
			}
			out[i] = 8 / Math.pow(Math.PI, 2) * sum;
		}
		return out;
	}
	
	public static double[] sawtootheWave(int length, double frequency, int samplingFrequency) {
		double[] out = new double[length];
		out[0] = 0.0;
		for (int i=1; i<length; i++) {
			out[i] = 2 * (i*frequency/samplingFrequency - Math.floor(0.5 + i*frequency/samplingFrequency));
		}
		return out;
	}
	
	public static double[] squareWave(int length, double frequency, int samplingFrequency) {
		double[] out = new double[length];
		for (int i=0; i<length; i++) {
			out[i] = Math.signum(Math.sin(2 * Math.PI * frequency/samplingFrequency * i));
		}
		return out;
	}
	
	public static double[] mix(double[][] waves, double[] gains) {
		// check if lenghts are the same
		int len = waves[0].length;
		for (double[] w : waves) {
			if (w.length != len)
				return null;
		}
		// check if there is enough gains
		if (gains != null) {	// null gains are also accepted
			if (waves.length != gains.length)
				return null;
		}		
		// init out wave
		double[] out = new double[len];
		for (int j = 0; j < out.length; j++) {
			out[j] = 0.0;
		}		
		// add waves up
		for (int n=0; n<waves.length; n++) {
			for (int i=0; i<out.length; i++) {
				out[i] += waves[n][i] * ((gains != null) ? gains[n] : 1.0);
			}
		}		
		return out;
	}
	
	public static double[] mix(ArrayList<double[]> waves, ArrayList<Double> gains) {
		// check if lenghts are the same
		int len = waves.get(0).length;
		for (double[] w : waves) {
			if (w.length != len)
				return null;
		}		
		// check if there is enough gains
		if (gains != null) {	// null gains are also accepted
			if (waves.size() != gains.size())
				return null;
		}		
		// init out wave
		double[] out = new double[len];
		for (int j = 0; j < out.length; j++) {
			out[j] = 0.0;
		}		
		// add waves up
		for (int n=0; n<waves.size(); n++) {
			for (int i=0; i<out.length; i++) {
				out[i] += waves.get(n)[i] * ((gains != null) ? gains.get(n) : 1.0);
			}
		}		
		return out;
	}
	
	// amplify signal by multiplying it by gain
	public static double[] amplify(double[] in, double gain) {
		double[] out = new double[in.length];
		for (int i=0; i<in.length; i++) {
			out[i] = in[i] * gain;
		}
		return out;
	}
	
	// concatenate array of arrays into one long array
	public static double[] concatenateArrays(double[][] waves) {
		int totalFrames = 0;
		for (int n=0; n<waves.length; n++)
			totalFrames += waves[n].length;
		double[] wave = new double[totalFrames];
		
		int offset = 0;
		for(int n=0; n<waves.length; n++) {
			System.arraycopy((Object)waves[n], 0, (Object)wave, offset, waves[n].length);
			offset += waves[n].length;
		}
		return wave;		
	}
	
	// concatenate list of arrays int one long array
	public static double[] concatenateArrays(ArrayList<double[]> waves) {
		int l = waves.size();
		double[][] w = new double[l][];
		for (int i=0; i<l; i++) {
			w[i] = waves.get(i);
		}
		return concatenateArrays(w);
	}
	
	// shifts a signal forward by number of seconds
	public static double[] shift(double[] in, int Fs, double seconds) {
		int offset = (int)(Fs * seconds);
		if (in.length < offset)
			return null;
		
		double out[] = new double[in.length];		
		
		for (int i=0; i<offset; i++)
			out[i] = in[in.length-offset+i-1];
			
		for (int i=offset+1; i<in.length; i++)
			out[i] = in[i-offset-1];
		
		return out;
	}
	
	// fade out to zero
	public static double[] fade(double[] in, int Fs) {
		double[] out = in;
		for (int i=0; i<in.length; i++) {
			out[i] = out[i] * (in.length - i)/in.length;
		}
		return out;
	}
	
	public static double[] synth(int length, double frequency, int Fs) {
		int detune = 2;
		
		double[] c1 = triangleWave(length, frequency-detune, Fs);
		double[] c2 = triangleWave(length, frequency+detune, Fs);
		double[] c3 = squareWave(length, frequency, Fs);
		double[] c4 = triangleWave(length, frequency, Fs);
		double[] c5 = sineWave(length, frequency/2, Fs);
		
		double[] out = SigGen.mix(new double[][] {c1, c2, c3, c4, c5}, new double[] {0.03, 0.03, 0.0, 0.0, 0.0});
		out = ResonantFilter.resonantLowPassFilter(out, (int)(frequency * 5), 20.1, Fs);
		//out = amplify(out, 1.4);
		out = fade(out, Fs);
		return out;
	}
}
