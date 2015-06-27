package f1;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import common.WavFile;
import common.WavGenerator;

public class FourierSpectrumTest {
	
private static String path = "/home/adam/workspace/sp1/res/";
	
	private static String[] filenames = {
		"artificial/easy/100Hz.wav",	// 0
		"artificial/easy/150Hz.wav",	// 1
		"artificial/easy/225Hz.wav",	// 2
		"artificial/easy/337Hz.wav",	// 3
		"artificial/easy/506Hz.wav",	// 4
		"artificial/easy/759Hz.wav",	// 5
		"artificial/easy/1139Hz.wav",	// 6
		"artificial/easy/1708Hz.wav",	// 7
		"artificial/med/90Hz.wav",		// 8
		"artificial/med/135Hz.wav",		// 9
		"artificial/med/202Hz.wav",		// 10
		"artificial/med/303Hz.wav",		// 11
		"artificial/med/455Hz.wav",		// 12
		"artificial/med/683Hz.wav",		// 13
		"artificial/med/1025Hz.wav",	// 14
		"artificial/med/1537Hz.wav",	// 15
		"artificial/diff/80Hz.wav",		// 16
		"artificial/diff/120Hz.wav",	// 17
		"artificial/diff/180Hz.wav",	// 18
		"artificial/diff/270Hz.wav",	// 19
		"artificial/diff/405Hz.wav",	// 20
		"artificial/diff/607Hz.wav",	// 21
		"artificial/diff/911Hz.wav",	// 22
		"artificial/diff/1366Hz.wav",	// 23
		"natural/flute/276Hz.wav",		// 24
		"natural/flute/443Hz.wav",		// 25
		"natural/flute/591Hz.wav",		// 26
		"natural/flute/887Hz.wav",		// 27
		"natural/flute/1265Hz.wav",		// 28
		"natural/flute/1779Hz.wav",		// 29
		"natural/viola/130Hz.wav",		// 30
		"natural/viola/196Hz.wav",		// 31
		"natural/viola/247Hz.wav",		// 32
		"natural/viola/294Hz.wav",		// 33
		"natural/viola/369Hz.wav",		// 34
		"natural/viola/440Hz.wav",		// 35
		"natural/viola/698Hz.wav",		// 36
		"seq/DWK_violin.wav",			// 37
		"seq/KDF_piano.wav"				// 38
	};
	
	public static void main(String[] args) {
		///test(13);
		//testAll();
		//WaveGenerator.generateWav(100, "test.wav");
		test(37);
	}
	
	@SuppressWarnings("unused")
	private static void testAll() {
		log("filename:\t\t\tfundamental [Hz]\trelative error [%]");
		log("=============================================================================");
		for (String file : filenames) {
			// expected fundamental frequency is in the name of the file
			String s = file.substring(file.lastIndexOf("/")+1, file.lastIndexOf(".wav")-2);
			int expected = Integer.valueOf(s);	
			int result = Fourier.analyzeFile(path+file);
			double relativeError = Math.abs(round((double)((result-expected))/expected*100,2));
			String spacing = (file.length() < 26) ? "   " : ""; // used to align text displayed in console
			log(file + spacing  + "\t" + result + "\t\t\t" + relativeError);
			//log(file+";"+result+";"+relativeError);
		}
	}
	
	@SuppressWarnings("unused")
	private static void test(int id) {
		try {
			//int id = 30;
			// open the wave file
			log("filename: " + filenames[id]);
	        WavFile wavFile = WavFile.openWavFile(new File(path+filenames[id]));
	     
	        // get sample rate and number of frames in file
	        int sampleRate = (int) wavFile.getSampleRate();	         
	        int numFrames = (int) wavFile.getNumFrames();
	        
	        // create array which will hold all frames of wave file
	        double[] wave = new double[numFrames];
	        
	        // put all frames from wave files into the array
	        wavFile.readFrames(wave, numFrames);
	        
	        // close wave file, it is not needed anymore
	        wavFile.close();
	        
	        //log("full: " + Fourier.analyzeFile(path+filenames[id]));
	        
	        double from = 0.5;
	        double to = 1.5;
	        
	        int s = 100;
	        int segmentFrames = numFrames/s;
	        
	        ArrayList<Integer> f0s = new ArrayList<Integer>();
	        
	        for (int i=0; i<s; i++) {
	        	double[] segmentWave = new double[segmentFrames];
	        	for (int j=0; j<segmentFrames; j++) {
	        		segmentWave[j] = wave[i*segmentFrames + j];
	        	}
	        	//log("f0 " + i + " " + Fourier.analyzeFrames(segmentWave, sampleRate));
	        	int res = Fourier.analyzeFrames(segmentWave, sampleRate);
	        	log(i+";"+res);
	        	f0s.add(res);
	        }
	        log("segments" + f0s.size());
	        //log("segmentframes" + f0s.size());
	        WavGenerator.generateWavFromFundamentals(f0s, segmentFrames, "seq.wav");
	        
	        
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}
}
