package g2.filters;

public class ResonantFilter {
	public static double[] resonantLowPassFilter(double[] in, int cutOffFrequency, double q, int samplingFrequency) {
		double[] out = new double[in.length];
		
		double s = Math.sin(2 * Math.PI * cutOffFrequency / samplingFrequency);
		double c = Math.cos(2 * Math.PI * cutOffFrequency / samplingFrequency);
		double a = s / (2 * q);
		double r = 1.0 / (1.0 + a);
		
		double a0 = 0.5 * (1 - c) * r;
		double a1 = (1 - c) * r;
		double a2 = a0;
		double b1 = -2 * c * r;
		double b2 = (1 - a) * r;
		
		for (int i = 0; i < out.length; i++) {
			if (i < 2) {
				out[i] = 0.0;
				continue;
			}
			out[i] = a0 * in[i] + a1 * in[i-1] + a2 * in[i-2] - b1 * out[i-1] - b2 * out[i-2];
		}
		
		return out;
	}
	
	public static double[] resonantLowPassFilter(double[] in, double[] cutOffFrequencies, double q, int samplingFrequency) {
		double[] out = new double[in.length];
		
		for (int i = 0; i < out.length; i++) {
			if (i < 2) {
				out[i] = 0.0;
				continue;
			}
			
			double s = Math.sin(2 * Math.PI * cutOffFrequencies[i] / samplingFrequency);
			double c = Math.cos(2 * Math.PI * cutOffFrequencies[i] / samplingFrequency);
			double a = s / (2 * q);
			double r = 1.0 / (1.0 + a);
			
			double a0 = 0.5 * (1 - c) * r;
			double a1 = (1 - c) * r;
			double a2 = a0;
			double b1 = -2 * c * r;
			double b2 = (1 - a) * r;
		
			out[i] = a0 * in[i] + a1 * in[i-1] + a2 * in[i-2] - b1 * out[i-1] - b2 * out[i-2];
		}
		
		return out;
	}
}
