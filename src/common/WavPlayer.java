package common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class WavPlayer {
	public static void playFile(String filename) {
		
	    try {
	    	// open the sound file as a Java input stream
			InputStream in = new FileInputStream(filename);
 
			// create an audiostream from the inputstream
			AudioStream audioStream = new AudioStream(in);
 
			// play the audio clip with the audioplayer class
			AudioPlayer.player.start(audioStream);
			
		} catch (FileNotFoundException e) {
			Log.e(e.getMessage());
		} catch (IOException e) {
			Log.e(e.getMessage());
		}
	}
}
