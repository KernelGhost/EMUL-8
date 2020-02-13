import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// A simple class that creates a 2-tuple (used for mapping memory to the memory diagram)
class CellGrid {
    public int x;
    public int y;
 
    public CellGrid(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class frmLog extends JFrame {
	private static final long serialVersionUID = -4237553851784082444L;
	private JPanel contentPane;
	
	public static VirtualSystem currState;
	public static JLabel[] lblRAMCells;
	public static JLabel[] lblVReg;
	public static JLabel[] lblStack;
	public static JLabel[] lblKeys;
	public static JLabel lblO;
	public static JLabel lblI;
	public static JLabel lblStackP;
	public static JLabel lblPC;
	public static JLabel lblDelay;
	public static JLabel lblSound;
	
	// This function converts an array index to a Cartesian representation [i --> (x, y)]
	public static CellGrid TransformCG(int i) {
		int x = 0;
		int y = 0;
		// 64 since the graphical representation of memory on the window is 64x64 in size (memory size is 4096 bytes)
		x = (i % 64);
		y = (i - x)/64;
		
		return new CellGrid(x,y);
	}
	
	public frmLog(VirtualSystem CHIP8) {
		currState = CHIP8;
		lblRAMCells = new JLabel[currState.system_memory.length];
		lblVReg = new JLabel[currState.V_Reg.length];
		lblStack = new JLabel[currState.system_stack.length];
		lblKeys = new JLabel[AtomicKeys.boolKeys.length];
		
		setResizable(false);
		setTitle("EMUL-8: System Monitor");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(855, 455));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// JFrame icon for Windows Operating Systems
		ImageIcon imgOldIcon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
		Image oldimg = imgOldIcon.getImage();
		Image newimg = oldimg.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		ImageIcon imgNewIcon = new ImageIcon(newimg);
		setIconImage(imgNewIcon.getImage());
		
		// Pack JFrame
		pack();
		
		// Operation Code Label
		lblO = new JLabel("Operation Code   = 0x");
		lblO.setFont(new Font("Courier", Font.PLAIN, 14));
		lblO.setHorizontalAlignment(SwingConstants.LEFT);
		lblO.setBounds(16, 6, 211, 16);
		contentPane.add(lblO);
		
		// Program Counter Label
		lblPC = new JLabel("Program Counter  = 0x");
		lblPC.setFont(new Font("Courier", Font.PLAIN, 14));
		lblPC.setHorizontalAlignment(SwingConstants.LEFT);
		lblPC.setBounds(16, 26, 211, 16);
		contentPane.add(lblPC);
		
		// Index Register Label
		lblI = new JLabel("Index Register   = 0x");
		lblI.setFont(new Font("Courier", Font.PLAIN, 14));
		lblI.setHorizontalAlignment(SwingConstants.LEFT);
		lblI.setBounds(16, 46, 211, 16);
		contentPane.add(lblI);
		
		// Stack Pointer Label
		lblStackP = new JLabel("Stack Pointer    = 0x");
		lblStackP.setFont(new Font("Courier", Font.PLAIN, 14));
		lblStackP.setHorizontalAlignment(SwingConstants.LEFT);
		lblStackP.setBounds(16, 66, 211, 16);
		contentPane.add(lblStackP);
		
		// General Registers Label
		JLabel lblV = new JLabel("General Registers:");
		lblV.setFont(new Font("Courier", Font.PLAIN, 14));
		lblV.setHorizontalAlignment(SwingConstants.LEFT);
		lblV.setBounds(16, 108, 168, 16);
		contentPane.add(lblV);
		
		// System Stack Label
		JLabel lblSysStack = new JLabel("System Stack:");
		lblSysStack.setFont(new Font("Courier", Font.PLAIN, 14));
		lblSysStack.setHorizontalAlignment(SwingConstants.LEFT);
		lblSysStack.setBounds(233, 108, 127, 16);
		contentPane.add(lblSysStack);
		
		// System Memory Label
		JLabel lblSystemMemory = new JLabel("System Memory:");
		lblSystemMemory.setHorizontalAlignment(SwingConstants.LEFT);
		lblSystemMemory.setFont(new Font("Courier", Font.PLAIN, 14));
		lblSystemMemory.setBounds(450, 6, 168, 16);
		contentPane.add(lblSystemMemory);
		
		// Update Memory Diagram Button
		JButton btnUpdate = new JButton("Update Memory Diagram");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateRAM();
			}
		});
		btnUpdate.setBounds(450, 418, 384, 29);
		contentPane.add(btnUpdate);
		
		// Delay Timer Label
		lblDelay = new JLabel("delay_timer = ");
		lblDelay.setHorizontalAlignment(SwingConstants.LEFT);
		lblDelay.setFont(new Font("Courier", Font.PLAIN, 14));
		lblDelay.setBounds(233, 6, 180, 16);
		contentPane.add(lblDelay);
		
		// Sound Timer Label
		lblSound = new JLabel("sound_timer = ");
		lblSound.setHorizontalAlignment(SwingConstants.LEFT);
		lblSound.setFont(new Font("Courier", Font.PLAIN, 14));
		lblSound.setBounds(233, 26, 180, 16);
		contentPane.add(lblSound);
		
		// Keyboard Keys Label
		JLabel lblKey = new JLabel("Keys:");
		lblKey.setHorizontalAlignment(SwingConstants.LEFT);
		lblKey.setFont(new Font("Courier", Font.PLAIN, 14));
		lblKey.setBounds(233, 46, 170, 16);
		contentPane.add(lblKey);
		
		for(int intCtr = 0; intCtr < AtomicKeys.boolKeys.length; intCtr++) {
			// Keyboard Keys Diagram
			lblKeys[intCtr] = new JLabel();
			lblKeys[intCtr].setBounds(233 + (12 * intCtr), 66, 12, 12);
			lblKeys[intCtr].setOpaque(true);
			lblKeys[intCtr].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			lblKeys[intCtr].setBackground(Color.BLACK);
			lblKeys[intCtr].setForeground(Color.WHITE);
			lblKeys[intCtr].setHorizontalAlignment(SwingConstants.CENTER);
			lblKeys[intCtr].setFont(new Font("Courier", Font.PLAIN, 12));
			lblKeys[intCtr].setText(Integer.toHexString(intCtr).toUpperCase());
			contentPane.add(lblKeys[intCtr]);
		}
		
		for(int intMCtr = 0; intMCtr < CHIP8.V_Reg.length; intMCtr++) {
			// General Register Value Labels
			lblVReg[intMCtr] = new JLabel();
			lblVReg[intMCtr].setFont(new Font("Courier", Font.PLAIN, 14));
			lblVReg[intMCtr].setHorizontalAlignment(SwingConstants.LEFT);
			lblVReg[intMCtr].setBounds(16, 127 + (20 * intMCtr), 129, 16);
			contentPane.add(lblVReg[intMCtr]);
			
			// Stack Value Labels
			lblStack[intMCtr] = new JLabel();
			lblStack[intMCtr].setFont(new Font("Courier", Font.PLAIN, 14));
			lblStack[intMCtr].setHorizontalAlignment(SwingConstants.LEFT);
			lblStack[intMCtr].setBounds(233, 127 + (20 * intMCtr), 168, 16);
			contentPane.add(lblStack[intMCtr]);
		}
		
		for(int intMCtr = 0; intMCtr < CHIP8.system_memory.length; intMCtr++) { 
			// System Memory Diagram
			lblRAMCells[intMCtr] = new JLabel();
			lblRAMCells[intMCtr].setOpaque(true);
			lblRAMCells[intMCtr].setBounds((450 + (6 * TransformCG(intMCtr).x)), (30 + (6 * TransformCG(intMCtr).y)), 6, 6);
			lblRAMCells[intMCtr].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			contentPane.add(lblRAMCells[intMCtr]);
		}
	}
	
	// This subroutine updates the system memory diagram
	private static void UpdateRAM() {
		for(int intMCtr = 0; intMCtr < currState.system_memory.length; intMCtr++) { 
			lblRAMCells[intMCtr].setBackground(new Color (currState.system_memory[intMCtr] & 0xFF, 0, 0));
			lblRAMCells[intMCtr].setToolTipText(String.format("0x%04X", intMCtr) + ": " + String.format("%02X", currState.system_memory[intMCtr]));
		}
	}
	
	// This subroutine is called each CPU cycle and updates the information on the monitor window
	static void UpdateLog(VirtualSystem CHIP8) {
		currState = CHIP8;
		lblO.setText("Operation Code   = " + String.format("0x%04X", currState.operation_code));
		lblPC.setText("Program Counter  = " + String.format("0x%04X", currState.program_counter));
		lblI.setText("Index Register   = " + String.format("0x%04X", currState.Index_Reg));
		lblStackP.setText("Stack Pointer    = " + String.format("0x%04X", currState.stack_pointer));
		lblDelay.setText("delay_timer = " + String.format("0x%04X", AtomicTimer.delay_timer.get()));
		lblSound.setText("sound_timer = " + String.format("0x%04X", AtomicTimer.sound_timer.get()));
		
		for(int intMCtr = 0; intMCtr < currState.V_Reg.length; intMCtr++) {
			lblVReg[intMCtr].setText("V[" + String.format("0x%01X", intMCtr) + "] = " + String.format("0x%02X", currState.V_Reg[intMCtr]));
			lblStack[intMCtr].setText("Stack[" + String.format("0x%01X", intMCtr) + "] = " + String.format("0x%04X", currState.system_stack[intMCtr]));
			
			if (AtomicKeys.boolKeys[intMCtr].get()) {
				lblKeys[intMCtr].setBackground(new Color(0,204,0)); // Green
			} else {
				lblKeys[intMCtr].setBackground(Color.BLACK);
			}
		}
	}
}