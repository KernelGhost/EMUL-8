import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// TIMER THREAD
// The code within this class is executed once emulation has commenced at a frequency
// specified by the user. This code is responsible for checking if both the delay_timer
// and sound_timer variables are positive and non-zero, decrementing them if this
// is the case. In addition, a buzzer sound is produced whenever the sound_timer variable
// is a positive non-zero value. This code runs within its own thread such that the thread
// can be slept at regular intervals to slow the frequency to best adhere to the requested
// timer frequency (the default being 60Hz).

class TimerThread extends Thread {
	int intMClockSpeed; 				// Store the millisecond component
	int intNClockSpeed; 				// Store the nanosecond component
	InputStream isAudio;				// Store the buzzer sound resource as InputStream
	static AudioInputStream audioIn;	// Store the buzzer sound resource as AudioInputStream
	static Clip clip;					// Store the buzzer sound resource as Clip
	
	public TimerThread(int intClockSpeed) {
		// Convert Hertz to Nanoseconds
		this.intNClockSpeed = (int) 1000000000/intClockSpeed;
		
		// Convert "Milliseconds" to "Milliseconds and Nanoseconds"
		this.intMClockSpeed = (int) Math.floor(this.intNClockSpeed / 1000000); // Store Milliseconds
		this.intNClockSpeed = (int) (this.intNClockSpeed % 1000000); // Store Nanoseconds Remainder
	
		// Load Buzzer Sound
		try {
			isAudio = TimerThread.class.getResourceAsStream("resources/sounds/Sound.wav");
			audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(isAudio)); 
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (Exception e) {
			DisplayError.ErrorBox("There was an error loading the buzzer sound. Exiting", "EMUL-8 Critical Error");
			System.exit(1);
		}
	}
	
	public void run() {
 		while (AtomicRun.boolRun.get()) {
 			// Decrement the delay_timer if it is positive and non-zero
		    if (AtomicTimer.delay_timer.get() > 0) {
		    	AtomicTimer.delay_timer.decrementAndGet();
		    }
		    
		    // Decrement the sound_timer and produce a sound if it is positive and non-zero
		    if (AtomicTimer.sound_timer.get() > 0) {
	        	PlaySound();
	        	AtomicTimer.sound_timer.decrementAndGet();
		    }
	 		
			try {
				// Sleep the thread in order to approximate the requested timer frequency
				// Convert "Milliseconds" to "Milliseconds and Nanoseconds"
				Thread.sleep(this.intMClockSpeed, this.intNClockSpeed);
			} catch (InterruptedException e) {
				DisplayError.ErrorBox("There was an error sleeping the timer thread. Exiting", "EMUL-8 Critical Error");
				System.exit(1);
			}
 		}
 		// Terminate the thread
 		return;
	}
	
	private static void PlaySound() {
		clip.setMicrosecondPosition(0);
		clip.start();
	}
}