package t2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

import common.WavGenerator;

public class PhaseSpaceTest {

	
	private static String path = "/home/adam/workspace/sp1/res/";
	
	private static String[] filenames = {
		"artificial/easy/100Hz.wav",
		"artificial/easy/150Hz.wav",
		"artificial/easy/225Hz.wav",
		"artificial/easy/337Hz.wav",
		"artificial/easy/506Hz.wav",
		"artificial/easy/759Hz.wav",
		"artificial/easy/1139Hz.wav",
		"artificial/easy/1708Hz.wav",
		"artificial/med/90Hz.wav",
		"artificial/med/135Hz.wav",
		"artificial/med/202Hz.wav",
		"artificial/med/303Hz.wav",
		"artificial/med/455Hz.wav",
		"artificial/med/683Hz.wav",
		"artificial/med/1025Hz.wav",
		"artificial/med/1537Hz.wav",
		"artificial/diff/80Hz.wav",
		"artificial/diff/120Hz.wav",
		"artificial/diff/180Hz.wav",
		"artificial/diff/270Hz.wav",
		"artificial/diff/405Hz.wav",
		"artificial/diff/607Hz.wav",
		"artificial/diff/911Hz.wav",
		"artificial/diff/1366Hz.wav",
		"natural/flute/276Hz.wav",
		"natural/flute/443Hz.wav",
		"natural/flute/591Hz.wav",
		"natural/flute/887Hz.wav",
		"natural/flute/1265Hz.wav",
		"natural/flute/1779Hz.wav",
		"natural/viola/130Hz.wav",
		"natural/viola/196Hz.wav",
		"natural/viola/247Hz.wav",
		"natural/viola/294Hz.wav",
		"natural/viola/369Hz.wav",
		"natural/viola/440Hz.wav",
		"natural/viola/698Hz.wav"/*,
		"seq/DWK_violin.wav",
		"seq/KDF_piano.wav"*/
	};
	
	public static void main(String[] args) {
		
		// create object with default fields values
		PhaseSpace ps = new PhaseSpace();
		
		// assign parameters to fields of PhaseSpace object
		switch (args.length) {
		case 0:
			log("running with default values\n");
			break;
		case 1:
			ps.k = Integer.valueOf(args[0]);
			break;
		case 2:
			ps.k = Integer.valueOf(args[0]);
			ps.dimensions = Integer.valueOf(args[1]);
			break;
		case 3:
			ps.k = Integer.valueOf(args[0]);
			ps.dimensions = Integer.valueOf(args[1]);
			ps.threshold = Double.valueOf(args[2]);
			break;
		default:
			log("usage: PhaseSpaceTest <k> <dimensions> <threshold>");
			return;
		}
		
		ps.dimensions = 7;
		ps.threshold = 0.007;
		ps.k = 10;
		ArrayList<Integer> f = new ArrayList<Integer>();
		
		// analyze all files
		analyzeAllFiles(ps);
		
		// analyze specific file with segments
		//f = ps.analyzeFile(path+"seq/KDF_piano.wav", 10);
		//for (int ff : f)
			//log(""+ff);
		
		// analyze specific file without segments
//		int ff = ps.analyzeFile(path+filenames[0]);
//		log(""+ff);
		
		// generate wave files
//		WaveGenerator.generateWav(1000, "test.wav");
		
		
		
	}
	
	private static void analyzeAllFiles(PhaseSpace ps) {
ArrayList<Double> errorsList = new ArrayList<Double>();	// list of relative errors
		
		//log("k = " + ps.k + ", iterations per file = " + ps.iterations + ", diff = " + ps.diff);
		//log("Fundamental frequencies:");
		
		log("filename:\t\t\tmedian [Hz]:\trelative error [%]:");
		log("------------------------------------------------------------------------------");
		
		// iterate over files
		for (int i=0; i<filenames.length; i++) {
			// expected fundamental frequency is in the name of the file
			String s = filenames[i].substring(filenames[i].lastIndexOf("/")+1, filenames[i].lastIndexOf(".wav")-2);
			int expected = Integer.valueOf(s);	
			
			int result = ps.analyzeFile(path+filenames[i]);	// PhaseSpace.analyze returns two integers: [average, mean] fundamental frequency
			
			// calculate relative error and add it to error list
			double relativeError = Math.abs(round((double)((result-expected))/expected*100,2));
			errorsList.add(relativeError);
			
			String spacing = (filenames[i].length() < 26) ? "   " : ""; // used to align text displayed in console
			//log(filenames[i] + spacing + "\t" + result + "\t\t" + relativeError);
			String e = "" + relativeError;
			e = e.replace(".", ",");
			
			log(result+";"+e);
		}
		
		log("------------------------------------------------------------------------------");
		log("k = " + ps.k + ", d = " + ps.dimensions + ", t = " + ps.threshold);
		log("avg relative error: " + getAverage(errorsList) + " %");
		log("mean relative error: " + getMean(errorsList)+ " %");
	}

	public static double getAverage(ArrayList<Double> l) {
		double s = 0.0;
		for (int i=0; i<l.size(); i++) {
			s += l.get(i);
		}
		return round(s/l.size(),2);
	}
	
	public static double getMean(ArrayList<Double> l) {
		ArrayList<Double> copy = l;
		Collections.sort(copy);
		return round(copy.get(copy.size()/2), 2);
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
