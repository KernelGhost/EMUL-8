import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// CUSTOM EXCEPTIONS
class NoROMSelectedException extends Exception {
	private static final long serialVersionUID = -4242158392564552576L;
	public NoROMSelectedException(String message) {
        super(message);
    }
}

class ROMExceedsAvailMemoryException extends Exception {
	private static final long serialVersionUID = 5196513789650862475L;
	public ROMExceedsAvailMemoryException(String message) {
        super(message);
    }
}

class InvalidOperationCodeException extends Exception {
	private static final long serialVersionUID = 2467209378700404273L;
	public InvalidOperationCodeException(String message) {
		super(message);
	}
}

class AtomicRun {
	// USED TO HALT THREADS
	static AtomicBoolean boolRun = new AtomicBoolean();
}

class AtomicTimer {
	// MODELLING TIMERS
	static AtomicInteger delay_timer = new AtomicInteger();
	static AtomicInteger sound_timer = new AtomicInteger();
}

class AtomicKeys {
	// MODELLING KEYS
	static AtomicBoolean[] boolKeys = new AtomicBoolean[16];
	static AtomicBoolean boolAllowKey = new AtomicBoolean();
}

public class VirtualSystem {
	// SYSTEM MEMORY MAP
	// 0x000-0x1FF - Chip 8 interpreter
	// 0x050-0x0A0 - Used for the built in 4x5 pixel font set (0-F)
	// 0x200-0xFFF - Program ROM and work RAM
		
	// MODELLING CPU & SYSTEM MEMORY
	byte[] system_memory = new byte[4096];
	short operation_code;
	byte[] V_Reg = new byte[16];
	short Index_Reg;
	short program_counter;
	short[] system_stack = new short[16];
	short stack_pointer;
	Integer intKeymap[] = new Integer[16];
	int intCPUSpeed;
	int intClockSpeed;
	
	// GRAPHICS RELATED VARIABLES
	final byte byteScreenWidth = 64;
	final byte byteScreenHeight = 32;
	int intScreenScale = 20;
	final int intCanvasWidth = byteScreenWidth * intScreenScale;
	final int intCanvasHeight = byteScreenHeight * intScreenScale;
	boolean graphics[] = new boolean[byteScreenWidth * byteScreenHeight];
	Color colForeground = Color.WHITE;
	Color colBackground = Color.BLACK;
	boolean boolDraw;
	
	// OTHER VARIABLES
	String strROMName;
	
	public VirtualSystem() { // Initialize
		// SET RUN FLAG
		AtomicRun.boolRun.set(true);
		
		// INITIALIZE KEYMAP
		intKeymap = new Integer[16];
		
		// STORE FONTSET (STORED FROM 0x050 TO 0x0A0 IN SYSTEM MEMORY)
		byte[] fontset = new byte[] {
			(byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, //0
		    (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, //1
		    (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, //2
		    (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //3
		    (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, //4
		    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //5
		    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, //6
		    (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, //7
		    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, //8
		    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //9
		    (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, //A
		    (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, //B
		    (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, //C
		    (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, //D
		    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, //E
		    (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  //F
	    };
		
		// RESET SPECIAL
	    this.program_counter = 0x200;
	    this.operation_code = 0x0000;
	    this.Index_Reg = 0x0000;
	    this.stack_pointer = 0x0000;
	    
	    // RESET SPEEDS
	    this.intCPUSpeed = 500;
	    this.intClockSpeed = 60;

	    // RESET GRAPHICS
	    for(short shortCtr = 0; shortCtr < 64 * 32; shortCtr++) {
	        this.graphics[shortCtr] = false;
	    }

	    // RESET REGISTERS, KEYS AND STACK
	    for(byte byteCtr = 0; byteCtr < 16; byteCtr++) {
	        this.V_Reg[byteCtr] = 0x00;
	        AtomicKeys.boolKeys[byteCtr] = new AtomicBoolean(false);
	        this.system_stack[byteCtr] = 0x0000;
	    }

	    // RESET SYSTEM MEMORY
	    for(short shortCtr = 0; shortCtr < 4096; shortCtr++) {
	        this.system_memory[shortCtr] = 0x00;
	    }
	    
	    // RESET TIMERS
	    AtomicTimer.delay_timer.set(0);
	    AtomicTimer.sound_timer.set(0);

	    // RESET FLAGS
	    this.boolDraw = false;
	    AtomicKeys.boolAllowKey.set(false);
	    
	    // LOAD FONTSET INTO SYSTEM MEMORY
	    for(byte byteCtr = 0; byteCtr < 80; byteCtr++) {
	        this.system_memory[byteCtr + 0x0A0] = fontset[byteCtr];
	    }
	}
	
	public void NextOperationCode() throws InvalidOperationCodeException {
	    //FETCH operation_code
	    operation_code = (short) (((system_memory[program_counter] & 0xFF) << 8) | (system_memory[program_counter + 1] & 0xFF)); // 0xFF REQUIRED TO REMOVE SIGN BITS OF BOTH BYTES
	    
	    //RUN operation_code
	    switch(operation_code & 0xF000) {
	    case 0x0000:
	        switch(operation_code) {
	            case 0x00E0:
	            {
	            //0x00E0 --> CLEAR DISPLAY.
	                for(short shortCtr = 0; shortCtr < 64 * 32; shortCtr++) {
	                    graphics[shortCtr] = false;
	                }
	                boolDraw = true;
	                
	                program_counter += 2;
	                break;
	            }
	            case 0x00EE:
	            {
	            //0x00EE --> RETURN FROM SUBROUTINE.
	            	stack_pointer--;
	                program_counter = system_stack[stack_pointer];
	                
	                program_counter += 2;
	                break;
	            }
	            default:
	            {
	            //0x0NNN --> CALL RCA 1802 PROGRAM AT NNN (NOT IMPLEMENTED AS NOT REQUIRED FOR MOST ROMS).
	                throw new InvalidOperationCodeException("Unrecognised CPU operation code.");
	            }
	        }
	        break;
	    case 0x1000:
	    {
	    //0x1NNN --> JUMP TO NNN.
	        program_counter = (short) (operation_code & 0x0FFF);
	        break;
	    }
	    case 0x2000:
	    {
	    //0x2NNN --> CALL SUBROUTINE AT NNN.
	    	system_stack[stack_pointer] = program_counter;
	        stack_pointer++;
	        program_counter = (short) (operation_code & 0x0FFF);
	        break;
	    }
	    case 0x3000:
	    {
	    //0x3XNN --> SKIP NEXT INSTRUCTION IF VX == NN.
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	byte byteNN = (byte) (operation_code & 0x00FF);
	    	
	        if (V_Reg[byteX] == byteNN) {
	            program_counter += 2;
	        }
	        
	        program_counter += 2;
	        break;
	    }
	    case 0x4000:
	    {
	    //0x4XNN --> SKIP NEXT INSTRUCTION IF VX != NN.
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	byte byteNN = (byte) (operation_code & 0x00FF);
	    	
	        if (V_Reg[byteX] != byteNN) {
	            program_counter += 2;
	        }
	        
	        program_counter += 2;
	        break;
	    }
	    case 0x5000:
	    {
	    //0x5XY0 --> SKIP NEXT INSTRUCTION IF VX == VY.
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	    	
	        if (V_Reg[byteX] == V_Reg[byteY]) {
	            program_counter += 2;
	        }
	        
	        program_counter += 2;
	        break;
	    }
	    case 0x6000:
	    {
	    //0x6XNN --> SET VX = NN.
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	
	    	V_Reg[byteX] = (byte) (operation_code & 0x00FF);
	    	
	        program_counter += 2;
	        break;
	    }
	    case 0x7000:
	    {
	    //0x7XNN --> ADD NN TO VX (NO CHANGE TO CARRY FLAG).
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
        	byte byteNN = (byte) (operation_code & 0x00FF);
        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
        	int intNN = byteNN & 0xFF;		 // To prevent signed 2's complement issues
        	
	    	V_Reg[byteX] = (byte) ((intVX + intNN) & 0xFF);
	    	
	        program_counter += 2;
	        break;
	    }
	    case 0x8000:
	    {
	        switch (operation_code & 0x000F) {
	        case 0x0000:
	        {
	        //0x8XY0 --> SET VX = VY.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	
	        	V_Reg[byteX] = V_Reg[byteY];
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x0001:
	        {
	        //0x8XY1 --> SET VX = (VX | VY).
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	
	        	V_Reg[byteX] = (byte) (V_Reg[byteX] | V_Reg[byteY]);
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x0002:
	        {
	        //0x8XY2 --> SET VX = (VX & VY).
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	
	        	V_Reg[byteX] = (byte) (V_Reg[byteX] & V_Reg[byteY]);
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x0003:
	        {
	        //0x8XY3 --> SET VX = (VX XOR VY).
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	
	        	V_Reg[byteX] = (byte) (V_Reg[byteX] ^ V_Reg[byteY]);
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x0004:
	        {
	        //0x8XY4 --> ADD VY TO VX (SET VF TO 1 IF CARRY).
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
	        	int intVY = V_Reg[byteY] & 0xFF; // To prevent signed 2's complement issues
	        	
	            if ((intVX + intVY) > 0xFF) {
	            	V_Reg[0xF] = 1;
	            } else {
	            	V_Reg[0xF] = 0;
	            }
	            
	            V_Reg[byteX] = (byte) ((intVX + intVY) & 0xFF);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0005:
	        {
	        //0x8XY5 --> SUBTRACT VY FROM VX (SET VF TO 0 IF BORROW).
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
	        	int intVY = V_Reg[byteY] & 0xFF; // To prevent signed 2's complement issues
	        	
	            if (intVY > intVX) {
	            	V_Reg[0xF] = 0;
	            } else {
	            	V_Reg[0xF] = 1;
	            }
	            
	            V_Reg[byteX] = (byte) ((intVX - intVY) & 0xFF);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0006:
	        {
	        //0x8XY6 --> STORE LEAST SIGNIFICANT BIT OF VX IN VF, THEN SHIFT VX RIGHT BY 1.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	V_Reg[0xF] = (byte) (V_Reg[byteX] & 0b00000001);
	            V_Reg[byteX] = (byte) (V_Reg[byteX] >>> 1);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0007:
	        {
	        //0x8XY7 --> SET VX TO VY - VX. VF IS 0 IF BORROW, 1 IF NO BORROW.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
	        	int intVY = V_Reg[byteY] & 0xFF; // To prevent signed 2's complement issues
	        	
	            if (intVY > intVX) {
	            	V_Reg[0xF] = 1;
	            } else {
	            	V_Reg[0xF] = 0;
	            }
	            
	            V_Reg[byteX] = (byte) ((intVY - intVX) & 0xFF);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x000E:
	        {
	        //0x8XYE --> STORE MOST SIGNIFICANT BIT OF VX IN VF, THEN SHIFT VX LEFT BY 1.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	V_Reg[0xF] = (byte) (V_Reg[byteX] >>> 7);
	        	V_Reg[byteX] = (byte) ((V_Reg[byteX] << 1) & 0xFF);
	        	
	            program_counter += 2;
	            break;
	        }
	        default:
	        {
	        	throw new InvalidOperationCodeException("Unrecognised CPU operation code.");
	        }
	        }
	    }
	    break;

	    case 0x9000:
	    {
	    //0x9XY0 --> SKIP NEXT INSTRUCTION IF VX != VY.
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	byte byteY = (byte) ((operation_code & 0x00F0) >>> 4);
			
	        if (V_Reg[byteX] != V_Reg[byteY]) {
	            program_counter += 2;
	        }
	        
	        program_counter += 2;
	        break;
	    }
	    case 0xA000:
	    {
	    //0xANNN --> SET I to NNN.
	        Index_Reg = (short) (operation_code & 0x0FFF);
	        
	        program_counter += 2;
	        break;
	    }
	    case 0xB000:
	    {
	    //0xBNNN --> JUMP TO ADDR NNN PLUS V0.
	        program_counter = (short) ((operation_code & 0x0FFF) + (V_Reg[0] & 0xFF));
	        break;
	    }
	    case 0xC000:
	    {
	    //0xCXNN --> SET VX TO BITWISE AND OF NN AND RANDOM INTEGER (0 TO 255).
	    	Random randomGenerator = new Random();
	    	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	    	byte byteRandom = (byte) (randomGenerator.nextInt(256));
	    	byte byteNN = (byte) (operation_code & 0x00FF);
	    	
	    	V_Reg[byteX] = (byte) (byteRandom & byteNN);
	    	
	        program_counter += 2;
	        break;
	    }
	    case 0xD000:
	    {
	    //0xDXYN --> DRAW SPRITE AT COORDINATE (VX, VY) WITH WIDTH 8 PIXELS AND HEIGHT OF N PIXELS.
	    //           EACH ROW OF 8 PIXELS IS READ FROM MEMORY AT I, I+1, ETC. VF IS SET TO 1 IF ANY
	    //           SCREEN PIXELS ARE FLIPPED FROM SET TO UNSET, AND SET TO 0 IF THEY ARE NOT.
	    //           THE STATE OF EACH PIXEL IS SET USING A BITWISE XOR OPERATION.
	    //
	    //           SPRITE VALUE | EXISTING VALUE | NEW VALUE  (XOR TRUTH TABLE)
	    //           _____________|________________|__________
	    //                 0      |        0       |     0     (NO CHANGE --> NO NEED TO CODE FOR CASE, NO CHANGE OF VF)
	    //                 0      |        1       |     1     (NO CHANGE --> NO NEED TO CODE FOR CASE, NO CHANGE OF VF)
	    //                 1      |        0       |     1     (CHANGE    --> NEED TO CODE FOR CASE,    NO CHANGE OF VF)
	    //                 1      |        1       |     0     (CHANGE    --> NEED TO CODE FOR CASE,    NEED TO CHANGE VF)
	    //
	    //          REMEMBER THAT VF IS ONLY SET TO 1 WHEN A PIXEL GOES FROM SET TO UNSET, NOT GOING FROM UNSET TO SET.

	        int intVX = V_Reg[(operation_code & 0x0F00) >>> 8] & 0xFF;
	        int intVY = V_Reg[(operation_code & 0x00F0) >>> 4] & 0xFF;
	        byte byteN = (byte) (operation_code & 0x000F);
	        byte byteNewPixel;
	        
	        V_Reg[0xF] = 0;
	        for (byte byteYCtr = 0; byteYCtr < byteN; byteYCtr++) { // For each row of pixels
	            byteNewPixel = system_memory[Index_Reg + byteYCtr]; // Store the row of pixels
	            for (byte byteXCtr = 0; byteXCtr < 8; byteXCtr++) {	// For each pixel in row
	                if ((byteNewPixel & (0b10000000 >>> byteXCtr)) != 0) { // If pixel on
	                	int intGraphicsLocation = (intVX + byteXCtr) + ((intVY + byteYCtr) * byteScreenWidth);
	                	
	                	// Catch cases where painting outside the screen may occur.
	                	// e.g. A sprite of horizontal length 4 at position (60, 31)
	                	// would go outside the screen.
	                	if (intGraphicsLocation < 2048) {
	                		if ((graphics[intGraphicsLocation]) == true) { // If pixel already on
		                    	V_Reg[0xF] = 1;	// Set VF to 1
		                    }
		                    graphics[intGraphicsLocation] = !graphics[intGraphicsLocation]; // Invert pixel on screen
	                	}
	                }
	            }
	        }
	        
	        boolDraw = true;
	        
	        program_counter += 2;
	        break;
	    }
	    case 0xE000:
	        switch (operation_code & 0x00FF) {
	        case 0x009E:
	        {
	        //0xEX9E --> SKIP NEXT INSTRUCTION IF THE KEY WITHIN VX IS PRESSED.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	if(AtomicKeys.boolKeys[V_Reg[byteX]].get()) {
	        		program_counter += 2;
				}
	        	
	        	program_counter += 2;
	            break;
	        }
	        case 0x00A1:
	        {
	        //0xEXA1 --> SKIP NEXT INSTRUCTION IF THE KEY WITHIN VX IS NOT PRESSED.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	if(!AtomicKeys.boolKeys[V_Reg[byteX]].get()) {
	        		program_counter += 2;
				}
	        	
	        	program_counter += 2;
	            break;
	        }
	        default:
	        {
	        	throw new InvalidOperationCodeException("Unrecognised CPU operation code.");
	        }
	        }
	        break;

	    case 0xF000:
	        switch (operation_code & 0x00FF) {
	        case 0x0007:
	        {
	        //0xFX07 --> SET VX TO VALUE OF DELAY TIMER.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	V_Reg[byteX] = (byte) (AtomicTimer.delay_timer.get() & 0xFF);
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x000A:
	        {
	        //0xFX0A --> WAIT FOR KEYPRESS, THEN STORE IN VX.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
				for (byte byteCtr = 0; byteCtr < AtomicKeys.boolKeys.length; byteCtr++) {
					if (AtomicKeys.boolKeys[byteCtr].get()) {
						V_Reg[byteX] = byteCtr;
						program_counter += 2;
						break;
					}
				}
				// If no key is pressed, we don't change the program counter so
				// the operation_code will be run again
				break;
	        }
	        case 0x0015:
	        {
	        //0xFX15 --> SET DELAY TIMER TO VX.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
	        	
	        	AtomicTimer.delay_timer.set(intVX);
	        	
	            program_counter += 2;
	            break;
	        }
	        case 0x0018:
	        {
	        //0xFX18 --> SET SOUND TIMER TO VX.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
    			
	            AtomicTimer.sound_timer.set(intVX);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x001E:
	        {
	        //0xFX1E --> ADD VX TO I. IF I + VX > 0xFFF, SET VF TO 1 ELSE SET TO 0.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	            if ((Index_Reg & 0xFFFF) + (V_Reg[byteX] & 0xFF) > 0x0FFF) {
	            	V_Reg[0xF] = 1;
	            } else {
	            	V_Reg[0xF] = 0;
	            }
	            
	            Index_Reg = (short) (((Index_Reg & 0xFFFF) + (V_Reg[byteX] & 0xFF)) & 0x0FFF);
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0029:
	        {
	        //0xFX29 --> SET I TO ADDR OF SPRITE FOR THE CHARACTER IN VX.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	// Characters are stored in memory from 0x0A0 to 0x0F0. Each character
	        	// is stored as a sequence of 5 bytes, however only the leading 4 bits
	        	// of these bytes contain any 1s - the trailing 4 bits are all zero.
	        	// This is because the characters used by the CHIP-8 are only 4*5 pixels
	        	// in resolution. When this operation code is run, VX stores a byte ranging
	        	// in value from 0 to 15 (representing each of the 16 possible characters).
	        	// This value must be translated to the memory location of the first byte
	        	// of the requested character.
	        	
	            Index_Reg = (short) (0x0A0 + (V_Reg[byteX] * 0x5));
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0033:
	        {
	        //0xFX33 --> STORE DECIMAL REPRESENTATION OF VX SEQUENTIALLY AT I, I+1 & I+2.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	int intVX = V_Reg[byteX] & 0xFF; // To prevent signed 2's complement issues
	        	
	        	system_memory[Index_Reg] = (byte) (intVX / 100);     			//HUNDREDS DIGIT
	            system_memory[Index_Reg + 1] = (byte) ((intVX / 10) % 10);  	//TENS DIGIT
	            system_memory[Index_Reg + 2] = (byte) (intVX % 10);  			//ONES DIGIT
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0055:
	        {
	        //0xFX55 --> STORES V0 to VX (INCLUSIVE) TO MEMORY AT I INCREMENTED BY 1 FOR EACH VALUE WRITTEN.
	        	byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	for (byte byteCtr = 0; byteCtr <= byteX; byteCtr++) {
	        		system_memory[Index_Reg + byteCtr] = V_Reg[byteCtr];
	            }
	            
	            program_counter += 2;
	            break;
	        }
	        case 0x0065:
	        {
	        //0xFX65 --> POPULATE V0 TO VX (INCLUSIVE) WITH MEMORY AT I INCREMENTED BY 1 FOR EACH VALUE WRITTEN.
	            byte byteX = (byte) ((operation_code & 0x0F00) >>> 8);
	        	
	        	for (byte byteCtr = 0; byteCtr <= byteX; byteCtr++) {
	            	V_Reg[byteCtr] = system_memory[Index_Reg + byteCtr];
	            }
	            
	            program_counter += 2;
	            break;
	        }
	        default:
	        {
	        	throw new InvalidOperationCodeException("Unrecognised CPU operation code.");
	        }
			}
	        break;

	    default:
	    {
	    	throw new InvalidOperationCodeException("Unrecognised CPU operation code.");
	    }
	    }
	}
}