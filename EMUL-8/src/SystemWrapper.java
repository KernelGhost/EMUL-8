import javax.swing.JFrame;

public class SystemWrapper extends JFrame {
	private static final long serialVersionUID = -1585129476472012081L;
	public static frmLog frmLog;
	public static Integer intKeymap[] = new Integer[16];
	
	public static void GetKeys(int intKeyCode, boolean boolDown) {
		// Set or remove which keys are flagged based on if the key was pressed
		// or if the key was released. Additionally, only modify the atomic boolean
		// array when the CPU is NOT processing an operation code, as indicated by the
		// boolAllowKey atomic boolean variable.
		
		if (AtomicKeys.boolAllowKey.get()) {
			for (byte byteCtr = 0; byteCtr < 16; byteCtr++) {
				if (intKeymap[byteCtr] == intKeyCode) {
					if (boolDown) {
						AtomicKeys.boolKeys[byteCtr].set(true);
					} else {
						AtomicKeys.boolKeys[byteCtr].set(false);
					}
				}
			}
		}
	}
	
	public static void BootSystem(UserOptions selectedOptions, byte[] byteROM, byte[] byteAddData) {
		// This section of code runs once to start the virtual CPU and system timers.
		// Both of these are started within their own threads such that control over
		// the speed of code execution can be maintained via sleeping the threads for a
		// specified duration of time. In addition, this code is also responsible for
		// initial creation of the screen on which the CHIP-8 graphics are to be displayed,
		// along with creation of the System Monitor if the user chose to enable it. The
		// Keymap is also stored locally to allow the 'GetKeys' subroutine to run when
		// user keyboard input begins.
		
		// Create the CHIP-8 virtual system
		VirtualSystem CHIP8 = new VirtualSystem();
		
		// Load the required settings
		CHIP8.intScreenScale = selectedOptions.intScale;
		CHIP8.intCPUSpeed = selectedOptions.intCPUSpeed;
		CHIP8.intClockSpeed = selectedOptions.intClockSpeed;
		CHIP8.colForeground = selectedOptions.colForeground;
		CHIP8.colBackground = selectedOptions.colBackground;
		CHIP8.intKeymap = selectedOptions.intKeymap;
		CHIP8.strROMName = selectedOptions.strROMName;
		
		// Load the ROM into system memory
		for (int intCtr = 0; intCtr < byteROM.length; intCtr++) {
			CHIP8.system_memory[0x200 + intCtr] = byteROM[intCtr];
		}
		
		// If selected, load the additional data into system memory
		if (selectedOptions.boolAddData) {
			for (int intCtr = 0; intCtr < byteAddData.length; intCtr++) {
	        	CHIP8.system_memory[selectedOptions.intOffset + intCtr] = byteAddData[intCtr];
	        }
		}
		
		// Prepare the screen and the system monitor (if selected)
		ScreenGraphics Screen = new ScreenGraphics(CHIP8, selectedOptions.boolSystemMonitor);
		
		// Store the keymap
		intKeymap = CHIP8.intKeymap;
		
		// Start a new thread for the CPU
		CPUThread cpu_thread = new CPUThread(CHIP8, selectedOptions.boolSystemMonitor, Screen);
		cpu_thread.start();
		
		// Start a new thread for the system timers
		TimerThread timer_thread = new TimerThread(CHIP8.intClockSpeed);
		timer_thread.start();
	}
}