package g2;

import g2.generators.SigGen;

import common.Log;

public class RocketTest {

	static int Fs = 44100;
	
	public static void main(String[] args) {
		
		int duration = Fs * 5; // seconds
		int interpolatedframes = 10;
		double[] rednoise1 = SigGen.redNoise(duration, interpolatedframes);
		
		// make values of rednoise1 positive and multiply by 50 to get values from 0 to 50
		for (double d : rednoise1)
			d = Math.abs(d) * 50;
		
		
	}

}
