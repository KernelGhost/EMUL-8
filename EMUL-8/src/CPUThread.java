// CPU THREAD
// The code within this class is executed once emulation has commenced at a frequency
// specified by the user. This code is responsible for requesting the Virtual CPU to
// run the next operation code, updating the screen when required, updating the system monitor
// if the user has enabled it, and allowing for updating the status of which virtual keys
// are being pressed when the CPU is not running an operation code via use of the 'boolAllowKey'
// atomic boolean variable. This code runs within its own thread such that the thread can be
// slept at regular intervals to slow the frequency to best adhere to the requested CPU
// frequency (the default being 500Hz).

public class CPUThread extends Thread {
	VirtualSystem CHIP8;
	boolean boolSysMon;
	ScreenGraphics Screen;
	int intMCPUSpeed; // Store the millisecond component
	int intNCPUSpeed; // Store the nanosecond component
	
	public CPUThread(VirtualSystem CHIP8, boolean boolSysMon, ScreenGraphics Screen) {
		this.CHIP8 = CHIP8;
		this.boolSysMon = boolSysMon;
		this.Screen = Screen;
		
		// Convert Hertz to Nanoseconds
		this.intNCPUSpeed = (int) 1000000000/CHIP8.intCPUSpeed;
		
		// Convert "Milliseconds" to "Milliseconds and Nanoseconds"
		this.intMCPUSpeed = (int) Math.floor(this.intNCPUSpeed / 1000000); // Store Milliseconds
		this.intNCPUSpeed = (int) (this.intNCPUSpeed % 1000000); // Store Nanoseconds Remainder
	}
	
    public void run() {
    	while (AtomicRun.boolRun.get()) {
    		// Stop allowing keyboard input before executing the next opcode
    		AtomicKeys.boolAllowKey.set(false);
    		
			try {
				// Execute the next operation code
				CHIP8.NextOperationCode();
			} catch (InvalidOperationCodeException e) {
				DisplayError.ErrorBox("An unrecognised operation code was encountered. Exiting.", "EMUL-8 Critical Error");
				System.exit(1);
			}
			
			// Start allowing keyboard input after executing the opcode
			AtomicKeys.boolAllowKey.set(true);
			
			// Update the screen
			if (CHIP8.boolDraw) {
				Screen.UpdateScreen(CHIP8.graphics);
	            CHIP8.boolDraw = false;
	        }
			
			// Update the system monitor if it is enabled
			if (boolSysMon) {
				frmLog.UpdateLog(CHIP8);
			}
			
			try {
				// Sleep the thread in order to approximate the requested CPU frequency
				Thread.sleep(this.intMCPUSpeed, this.intNCPUSpeed);
			} catch (InterruptedException e) {
				DisplayError.ErrorBox("There was an error sleeping the CPU thread. Exiting.", "EMUL-8 Critical Error");
				System.exit(1);
			}
		}
    	// Terminate the thread
    	return;
    }
}