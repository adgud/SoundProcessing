package common;

import java.io.File;

public class Wav {
	public String filename = null;
	public double[] wave = null;
	public int sampleRate = 0;
	public int numFrames = 0;
	
	public Wav(String filename) {
		this.filename = filename;
		try {
			WavFile wavFile = WavFile.openWavFile(new File(filename));
	        this.sampleRate = (int) wavFile.getSampleRate();	         
	        this.numFrames = (int) wavFile.getNumFrames();
	        this.wave = new double[numFrames];
	        wavFile.readFrames(wave, numFrames);
	        wavFile.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public double[][] split(double fragmentDuration) {
		int fragmentFrames = (int) (fragmentDuration/1000 * sampleRate);
		//int remainingFrames = numFrames;
		int fragmentsNum = (int) Math.ceil((double)(numFrames)/fragmentFrames);
		//Log.i("" + fragmentsNum + " " + fragmentFrames);
		double[][] out = new double[fragmentsNum][];
		for (int i=0; i< fragmentsNum; i++) {
			int start = i*fragmentFrames;
			int stop = ((i+1)*fragmentFrames <= numFrames) ? (i+1)*fragmentFrames : numFrames-1;	//(i+i)*fragmentFrames-1;
			double[] f = new double[stop-start]; 
			System.arraycopy(wave, start, f, 0, stop-start);
			out[i] = f;
		}
		return out;
	}
	
	public static double getMax(double[] in) {
		double max = 0.0;
			for (int i=1; i<in.length; i++) {
				if (max<in[i]) {
					max=in[i];
				}
			}
		return max;
	}
	
	public void removeSilence(double th) {
		double max = getMax(wave);
		double threshold = max * th;
//		int start, end;
//		for (start=0;;start++) {
//			if (Math.abs(wave[start]) > threshold) break;
//		}
//		for (end=wave.length-1;;end--) {
//			if (Math.abs(wave[start]) > threshold) break;
//		}
//		int len = end-start+1;
//		double[] out = new double[len];
//		System.arraycopy(wave, start, out, 0, len);
//		wave = out;
		for (int i=0; i<wave.length; i++) {
			if (Math.abs(wave[i]) < threshold) wave[i] = 0.0;
		}
	}
}
