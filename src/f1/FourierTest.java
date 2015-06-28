package f1;

import common.Wav;

public class FourierTest {
	public static void main(String[] args) {
		Wav wav = new Wav("/home/adam/CloudStation/csit/sp/artificial/easy/1708Hz2.wav");
		double[] w = Fourier.hanningWindow(wav.wave);
		double[] dft = Fourier.magnitudeFFT(w);
		for (double d : dft) {
			System.out.println(d);
		}
	}
}
