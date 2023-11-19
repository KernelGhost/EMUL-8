import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

// This class handles the closing of the keyboard controls window.
class HidingWindowAdapter extends WindowAdapter {
	String strMsg;
	String strTitle;
	JFrame frame;
	
	public HidingWindowAdapter(String strMsg, String strTitle, JFrame frame) {
		this.strMsg = strMsg;
		this.strTitle = strTitle;
		this.frame = frame;
	}
	
    @Override
    public void windowClosing(WindowEvent e) {
    	ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
    	Image image = icon.getImage();
    	Image newimg = image.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
    	ImageIcon newicon = new ImageIcon(newimg);
        int result = JOptionPane.showConfirmDialog(null, this.strMsg, this.strTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, newicon);
        
        if (result == JOptionPane.YES_OPTION) {
        	frame.setVisible(false);
        }
    }
}

// Deliver the key pressed if a change has been requested.
class ControlKeyListener extends KeyAdapter {
	public void keyPressed(KeyEvent e) {
		if (frmControls.boolKeyChange) {
			frmControls.WriteKey(e);
		}
		
		e.consume();
		frmControls.keyLogger.setText("");
	}
}

// A class that extends the JButton to include an Index (i.e. a byte ID). This is required
// since clicking a 'Modify' button should also change the text in the corresponding label
// (which states which key is mapped where). Since both the labels and the 'Modify' buttons
// are stored in arrays of equal length and each button and label have one-to-one
// correspondence, the button can provide its own Index in order to modify the text of the
// associated label.
class iJButton extends JButton {
	private static final long serialVersionUID = 3549461546164867211L;
	byte byteIndex;

	public iJButton(byte byteIndex, String strText) {
		this.byteIndex = byteIndex;
		this.setText(strText);
	}
}

public class frmControls extends JFrame {
	private static final long serialVersionUID = 4497454554696886353L;
	private JPanel contentPane;
	public static frmControls frmControls;
	
	// Display the true CHIP-8 keys in a small blue font
	public static JLabel lblRKeys[] = new JLabel[16];
	
	// Display the new mapped keys in a large black font
	public static JLabel lblNKeys[] = new JLabel[16];
	
	// Store the CHIP-8 keys
	public static final String strRKeys[] = new String[] {"1","2","3","C","4","5","6","D","7","8","9","E","A","0","B","F"};
	
	// Store the mapped keyboard keys
	public static String strNKeys[] = new String[] {"1","2","3","4","Q","W","E","R","A","S","D","F","Z","X","C","V"};
	
	// Store the key codes of the new mapped keys
	public static Integer strNKeysID[] = new Integer[] {49, 50, 51, 52, 81, 87, 69, 82, 65, 83, 68, 70, 90, 88, 67, 86};
	
	// Stores a HashMap of all allowable keys (A string representation of them and
	// their associated key codes)
	public static Map<Integer, String> Keymap = new HashMap<Integer, String>();
	
	// Display which physical keyboard keys are mapped to which CHIP-8 keys
	public static JLabel lblKeymap[] = new JLabel[16];
	
	// Allow the user to modify which keys are mapped to each of the 16 CHIP-8 keys
	public static iJButton btnKeymap[] = new iJButton[16];
	
	// A JTextField with a KeyListener to capture which keys are pressed by the user
	public static JTextField keyLogger;
	
	// Stores if a 'Modify' button has been pressed or not
	public static boolean boolKeyChange = false;
	
	// Stores which of the 16 CHIP-8 keys want to be changed (0 to 15)
	public static byte byteKeyChange;
	
	// This subroutine runs whenever the user requests to modify which keyboard keys
	// are mapped to which CHIP-8 keys. Its purpose is to prevent any additional
	// 'Modify' buttons from being pressed, as well as set the boolKeyChange flag to true
	// such that key presses are sent for processing by the KeyListener.
	private static void ChangeKey(byte byteIndex) {
		// Set the flag to true
		boolKeyChange = true;
		
		// Store which of the 16 (0 - 15) CHIP-8 keys is being modified.
		byteKeyChange = byteIndex;
		
		// Update the text on screen to notify the user to press the desired keyboard key.
		lblKeymap[byteIndex].setText("Press the desired key...");
		
		// Disable all 'Modify' buttons since a modification is currently underway.
		for (byte byteCtr = 0; byteCtr < 16; byteCtr++) {
			btnKeymap[byteCtr].setEnabled(false);
		}
		
		// Set focus to the keyLogger so the key can actually be captured.
		keyLogger.requestFocus();
	}
	
	// A subroutine that takes the key code of the pressed key and checks if the key is
	// valid (i.e. in the Keymap HashMap) and if the key is not currently mapped to an
	// additional CHIP-8 key. If both conditions are true, the mapping is updated.
	public static void WriteKey(KeyEvent e) {
		if (Keymap.containsKey(e.getKeyCode())) {
			if (Arrays.asList(strNKeys).contains(Keymap.get(e.getKeyCode()))) {
				DisplayError.ErrorBox("Key already mapped!", "CHIP-8 Emulator Keyboard Controls");
			} else {
				strNKeys[byteKeyChange] = Keymap.get(e.getKeyCode());
				strNKeysID[byteKeyChange] = e.getKeyCode();
				lblNKeys[byteKeyChange].setText(strNKeys[byteKeyChange]);
			}
		} else {
			DisplayError.ErrorBox("Invalid key selected!", "CHIP-8 Emulator Keyboard Controls");
		}
		
		// Change the label back from "Press the desired key..." to display the mapping information
		lblKeymap[byteKeyChange].setText("Virtual key " + strRKeys[byteKeyChange] + " is mapped to " + strNKeys[byteKeyChange] + ".");
		
		// Re-enable all the 'Modify' buttons
		for (byte byteCtr = 0; byteCtr < 16; byteCtr++) {
			btnKeymap[byteCtr].setEnabled(true);
		}
		
		// Turn the boolKeyChange flag off so no additional keypresses are dealt with
		boolKeyChange = false;
	}
	
	// This subroutine runs when the user indicates they are finished modifying the mapping
	// of keyboard keys to virtual keys by choosing to save their changes.
	private void SaveKeys() {
		// Send the updated Keymap back to the options window
		frmOptions.selectedOptions.intKeymap = strNKeysID;
		
		// Hide the controls modification window
		this.setVisible(false);
	}
	
	// Initialize the HashMap that stores all allowable keys and a string representation of them.
	private void InitializeKeymap() {
		Keymap.put(192, "~");
		Keymap.put(45, "-");
		Keymap.put(61, "=");
		Keymap.put(91, "[");
		Keymap.put(93, "]");
		Keymap.put(92, "\\");
		Keymap.put(59, ";");
		Keymap.put(222, "'");
		Keymap.put(44, ",");
		Keymap.put(46, ".");
		Keymap.put(47, "/");
		Keymap.put(16, "⇧");
		Keymap.put(32, "' '");
		Keymap.put(37, "⬅");
		Keymap.put(38, "⬆");
		Keymap.put(39, "⮕");
		Keymap.put(40, "⬇");
		
		for (byte byteCtr = 0; byteCtr < 10; byteCtr++) {
			Keymap.put(48 + byteCtr, String.valueOf((char) (48 + byteCtr)));
		}
		
		for (byte byteCtr = 0; byteCtr < 26; byteCtr++) {
			Keymap.put(65 + byteCtr, String.valueOf((char) (65 + byteCtr)));
		}
	}

	public frmControls() {
		setResizable(false);
		setTitle("EMUL-8: Keyboard Controls");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(703, 410));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addWindowListener(new HidingWindowAdapter("Are you sure you want to stop modifying keyboard controls?\nChanges will not be saved!", "Close Keyboard Controls?", this));
		
		// JFrame icon for Windows Operating Systems
		ImageIcon imgOldIcon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
		Image oldimg = imgOldIcon.getImage();
		Image newimg = oldimg.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		ImageIcon imgNewIcon = new ImageIcon(newimg);
		setIconImage(imgNewIcon.getImage());
		
		// Pack and center the JFrame
		pack();
		setLocationRelativeTo(null);
		
		// Initialize Keymap
		InitializeKeymap();
		
		// Add JTextField to capture when keys are pressed and released
		keyLogger = new JTextField();
		keyLogger.addKeyListener(new ControlKeyListener());
		keyLogger.setBounds(-1,-1,1,1);
		keyLogger.setFocusTraversalKeysEnabled(false); // Prevent pressing the 'TAB' key from breaking the keypress detection
		keyLogger.setVisible(true);
		contentPane.add(keyLogger);
		
		// Create the labels on the graphical keymap, along with the descriptive
		// mapping labels and 'Modify' buttons
		for (byte byteCtr = 0; byteCtr < 16; byteCtr++) {
			// CHIP-8 Key Labels
			lblRKeys[byteCtr] = new JLabel(strRKeys[byteCtr]);
			lblRKeys[byteCtr].setFont(new Font("Lucida Grande", Font.PLAIN, 18));
			lblRKeys[byteCtr].setHorizontalAlignment(SwingConstants.LEFT);
			lblRKeys[byteCtr].setBounds(22 + (byteCtr % 4) * 78, 56 + ((byteCtr - (byteCtr % 4))/4) * 78, 20, 20);
			lblRKeys[byteCtr].setForeground(Color.BLUE);
			contentPane.add(lblRKeys[byteCtr]);
			
			// Keyboard Key Labels
			lblNKeys[byteCtr] = new JLabel(strNKeys[byteCtr]);
			lblNKeys[byteCtr].setFont(new Font("Lucida Grande", Font.PLAIN, 40));
			lblNKeys[byteCtr].setHorizontalAlignment(SwingConstants.CENTER);
			lblNKeys[byteCtr].setBounds(18 + (byteCtr % 4) * 78, 18 + ((byteCtr - (byteCtr % 4))/4) * 78, 60, 60);
			contentPane.add(lblNKeys[byteCtr]);
			
			// Keymap Labels
			lblKeymap[byteCtr] = new JLabel("Virtual key " + strRKeys[byteCtr] + " is mapped to " + strNKeys[byteCtr] + ".");
			lblKeymap[byteCtr].setFont(new Font("Lucida Grande", Font.PLAIN, 14));
			lblKeymap[byteCtr].setBounds(330, 15 + (24 * byteCtr), 250, 16);
			contentPane.add(lblKeymap[byteCtr]);
			
			// Keymap Buttons
			btnKeymap[byteCtr] = new iJButton(byteCtr, "Modify");
			btnKeymap[byteCtr].setBounds(580, 10 + (24 * byteCtr), 110, 25);
			btnKeymap[byteCtr].setFont(new Font("Lucida Grande", Font.PLAIN, 14));
			btnKeymap[byteCtr].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ChangeKey(((iJButton) e.getSource()).byteIndex);
				}
			});
			contentPane.add(btnKeymap[byteCtr]);
		}
		
		// Save Changes Button
		JButton btnChange = new JButton("Save Keymap");
		btnChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SaveKeys();
			}
		});
		btnChange.setBounds(15, 325, 300, 69);
		contentPane.add(btnChange);
		
		// Keypad Graphic
		try {
			JLabel lblKeypad = new JLabel("");
			lblKeypad.setBounds(15, 15, 300, 300);
			lblKeypad.setOpaque(true);
			BufferedImage imgKeypad = ImageIO.read(getClass().getResource("/resources/graphics/Keypad.png"));
			Image imgKeypadScaled = imgKeypad.getScaledInstance(lblKeypad.getWidth(), lblKeypad.getHeight(), Image.SCALE_SMOOTH);
			ImageIcon icoKeypad = new ImageIcon(imgKeypadScaled);
			lblKeypad.setIcon(icoKeypad);
			contentPane.add(lblKeypad);
		} catch (IOException e) {
			DisplayError.ErrorBox("Could not load keypad graphic. Exiting.", "EMUL-8 Critical Error");
			System.exit(1);
		}
	}
}