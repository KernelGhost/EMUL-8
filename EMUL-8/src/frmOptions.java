import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

//This class handles the closing of the options window.
class ClosingWindowAdapter extends WindowAdapter {
	String strMsg;
	String strTitle;
	
	public ClosingWindowAdapter(String strMsg, String strTitle) {
		this.strMsg = strMsg;
		this.strTitle = strTitle;
	}
	
    @Override
    public void windowClosing(WindowEvent e) {
    	ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
    	Image image = icon.getImage();
    	Image newimg = image.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
    	ImageIcon newicon = new ImageIcon(newimg);
        int result = JOptionPane.showConfirmDialog(null, this.strMsg, this.strTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, newicon);
        
        if (result == JOptionPane.YES_OPTION) {
        	System.exit(0);
        }
    }
}

// This class groups together an array of all allowable colors as well as a function that
// converts the name of one of these predefined colors to an RGB 'Color' variable.
class DispColors {
	// An array containing all options for the foreground and background color of the screen
	static final String[] strColors = {"White", "Black", "Very Light Red", "Light Red", "Red", "Dark Red", "Very Dark Red",
			  					"Very Light Blue", "Light Blue", "Blue", "Dark Blue", "Very Dark Blue",
			  					"Very Light Green", "Light Green", "Green", "Dark Green", "Very Dark Green",
			  					"Very Light Yellow", "Light Yellow", "Yellow", "Dark Yellow",
			  					"Light Orange", "Orange", "Gold", "Light Grey", "Grey", "Dark Grey", "Very Dark Grey",
			  					"Light Brown", "Brown", "Dark Brown", "Purple"};
	
	// This function translates between the names of colors and their RGB representations.
	// It is used to provide previews of the color selections within the options window
	// as well as to pass on Color variables matching the user's selection to other
	// components within the software.
	public static Color ColorDictionary(String strColor) {
		Color selectedColor = null;
		switch(strColor) {
			case "White":
				selectedColor = new Color(255,255,255);
				break;
			case "Black":
		  		selectedColor = new Color(0,0,0);
		  		break;
		  	case "Very Light Red":
		  		selectedColor = new Color(255,102,102);
		  		break;
		  	case "Light Red":
		  		selectedColor = new Color(255,51,51);
		  		break;
		  	case "Red":
		  		selectedColor = new Color(255,0,0);
		  		break;
		  	case "Dark Red":
		  		selectedColor = new Color(204,0,0);
		  		break;
		  	case "Very Dark Red":
		  		selectedColor = new Color(153,0,0);
		  		break;
		  	case "Very Light Blue":
		  		selectedColor = new Color(51,204,255);
		  		break;
		  	case "Light Blue":
		  		selectedColor = new Color(51,153,255);
		  		break;
		  	case "Blue":
		  		selectedColor = new Color(0,0,255);
		  		break;
		  	case "Dark Blue":
		  		selectedColor = new Color(0,0,204);
		  		break;
		  	case "Very Dark Blue":
		  		selectedColor = new Color(0,0,153);
		  		break;
		  	case "Very Light Green":
		  		selectedColor = new Color(102,255,102);
		  		break;
		  	case "Light Green":
		  		selectedColor = new Color(0,255,51);
		  		break;
		  	case "Green":
		  		selectedColor = new Color(0,204,0);
		  		break;
		  	case "Dark Green":
		  		selectedColor = new Color(0,153,0);
		  		break;
		  	case "Very Dark Green":
		  		selectedColor = new Color(0,102,0);
		  		break;
		  	case "Very Light Yellow":
		  		selectedColor = new Color(255,255,204);
		  		break;
		  	case "Light Yellow":
		  		selectedColor = new Color(255,255,153);
		  		break;
		  	case "Yellow":
		  		selectedColor = new Color(255,255,0);
		  		break;
		  	case "Dark Yellow":
		  		selectedColor = new Color(255,204,0);
		  		break;
		  	case "Light Orange":
		  		selectedColor = new Color(255,153,0);
		  		break;
		  	case "Orange":
		  		selectedColor = new Color(255,102,0);
		  		break;
		  	case "Gold":
		  		selectedColor = new Color(255,204,51);
		  		break;
		  	case "Light Grey":
		  		selectedColor = new Color(204,204,204);
		  		break;
		  	case "Grey":
		  		selectedColor = new Color(153,153,153);
		  		break;
		  	case "Dark Grey":
		  		selectedColor = new Color(102,102,102);
		  		break;
		  	case "Very Dark Grey":
		  		selectedColor = new Color(51,51,51);
		  		break;
		  	case "Light Brown":
		  		selectedColor = new Color(153,102,0);
		  		break;
		  	case "Brown":
		  		selectedColor = new Color(102,51,0);
		  		break;
		  	case "Dark Brown":
		  		selectedColor = new Color(51,0,0);
		  		break;
		  	case "Purple":
		  		selectedColor = new Color(102,0,153);
		  		break;
		}
		return selectedColor;
	}
}

// This class creates an object capable of storing all the settings chosen by the user. This
// includes whether they have chosen to inject additional data into the virtual memory (incl.
// the specified memory offset), if they have elected for the system monitor to be enabled
// at runtime, the foreground and background color of the emulation screen, the name of
// the selected ROM file, the scale of the emulator screen, the speed of the CPU clock
// and timers, and which physical keyboard keys are mapped to which virtual CHIP-8 keys.
class UserOptions {
	boolean boolAddData;					// Stores if additional data has been injected into memory
	boolean boolSystemMonitor;				// Stores if the system monitor should be displayed
	Color colForeground;					// Stores the foreground color selected
	Color colBackground;					// Stores the background color selected
	String strROMName;						// Stores the name of the selected ROM
	String strOffset;						// Stores the specified offset for additional data (if present)
	int intOffset;							// Stores the specified offset for additional data (if present)
	int intScale;							// Stores the selected scale for the emulation screen
	int intCPUSpeed;						// Stores the specified CPU speed
	int intClockSpeed;						// Stores the specified timer speed
	Integer intKeymap[] = new Integer[16];	// Stores which CHIP-8 keys are mapped to which keyboard keys
											// (Default is 1,2,3,4,Q,W,E,R,A,S,D,F,Z,X,C,V --> 0 to F respectively)
	
	public UserOptions() {
		// Load default options (These match the defaults of the Swing components)
		this.boolAddData = false;
		this.boolSystemMonitor = false;
		this.colForeground = new Color(255,255,255); // White
		this.colBackground = new Color(0,0,0);		 // Black
		this.strROMName = "";
		this.strOffset = "0x";
		this.intOffset = 0;
		this.intScale = 10;
		this.intCPUSpeed = 500;
		this.intClockSpeed = 60;
		this.intKeymap = new Integer[] {49, 50, 51, 52, 81, 87, 69, 82, 65, 83, 68, 70, 90, 88, 67, 86};
	}
}

public class frmOptions extends JFrame {
	private static final long serialVersionUID = 1928564671771414740L;
	
	public static frmOptions frmOptions;
	private JPanel contentPane;
	public static  JTextField txtOffset;
	public static JLabel lblForeColor;
	public static JLabel lblBackColor;
	
	public static frmControls frmControls = new frmControls();
	
	public static final int intMemoryLength = 4096;
	public static UserOptions selectedOptions = new UserOptions();
	public static byte[] byteROM = null;		// Will store the ROM
	public static byte[] byteAddData = null;	// Will store additional data
	
	// This function checks if the specified additional data file is valid.
	// It checks if:
		// 1) That a file was selected in the JFileChooser
		// 2) That the file can be read into a byte array
		// 3) That the length of the file is not larger than virtual memory
	// The function returns a byte array containing the selected additional file if
	// all tests are passed, and returns a null byte array in all other cases.
	private static byte[] CheckValidFile(String strAddDataPath) {
		byte byteResult[] = null;
		
		if (strAddDataPath != null) { // If a file was actually chosen in the JFileChooser
			try { // Try and read the contents of the file into a byte array
				byte byteAddDataBuffer[] = ParseData(strAddDataPath);
				
				if (byteAddDataBuffer.length <= intMemoryLength) {
					byteResult = byteAddDataBuffer;
				} else {  // In the case that the file is inherently longer than the virtual memory
					DisplayError.ErrorBox("The specified additional data is too large to fit within memory.", "EMUL-8 Error");
				}
				
			} catch (IOException e) {  // In the case that the file cannot be opened
				DisplayError.ErrorBox("There was an error opening the additional file.", "EMUL-8 Error");
			}
		}
		
		return byteResult;
	}
	
	// This function checks if the specified offset is valid.
	// It checks for the following conditions:
		// 1) The offset is invalid (i.e. an integer value cannot be decoded)
		// 2) The offset is negative
		// 3) The offset exceeds the size of the virtual memory
		// 4) The data is too large to fit in virtual memory given the specified offset
		// If all 4 test cases are false, the function returns the integer value of
		// the offset specified by the user. In all other cases, it returns -1.
	private static int CheckValidOffset() {
		int intResult = -1;
		String strOffset = txtOffset.getText();
		
		try {
			int intOffset = Integer.decode(strOffset);
			
			if (intOffset >= 0) {
				if (intOffset < intMemoryLength) {
					if (intOffset + byteAddData.length <= intMemoryLength) {
						intResult = intOffset;
					} else {  // In the case that the data is too large to fit within memory at the specified offset
						DisplayError.ErrorBox("Selected additional data is too large to fit within memory at the specified offset.", "EMUL-8 Error");
					}
				} else {  // In the case that the specified offset exceeds length of memory
					DisplayError.ErrorBox("Specified offset exceeds the size of memory. Please ensure the offset is between 0x0000 and 0x0FFF inclusive.", "EMUL-8 Error");
				}
			} else {  // In the case that the specified offset is negative
				DisplayError.ErrorBox("Specified offset is negative. Please ensure the offset is between 0x0000 and 0x0FFF inclusive.", "EMUL-8 Error");
			}
		} catch (Exception e) {  // In the case that an integer value could not be decoded
			DisplayError.ErrorBox("Invalid memory offset. Please ensure the input format matches '0xNNNN'.", "EMUL-8 Error");
		}
		
		return intResult;
	}
	
	// This function allows the user to select the file containing the additional data, and
	// returns the path of the selected file as a string. The string is null if no file
	// was chosen.
	private static String OpenData() {
		JFileChooser jfChooser = new JFileChooser();
		jfChooser.setDialogTitle("Select File Containing Additional Data");
		String strAddDataPath = null;
		int int_return_val = jfChooser.showOpenDialog(frmOptions); // Passing the JFrame as an argument sets the JFileChooser icon
		
		if (int_return_val == JFileChooser.APPROVE_OPTION) {
	    	strAddDataPath = jfChooser.getSelectedFile().getAbsolutePath();
	    }
		
		return strAddDataPath;
	}
	
	// This function takes in the path of a selected file as a string, and returns a byte
	// array containing the contents of that selected file.
	private static byte[] ParseData(String strPath) throws IOException {
		File fileAddData;
		byte[] byteAddDataBuffer = null;
	    
		fileAddData = new File(strPath);
		byteAddDataBuffer = Files.readAllBytes(fileAddData.toPath());
	    return byteAddDataBuffer;
	}
	
	// This subroutine is called when the user desires to begin emulation with the
	// selected settings.
	private void StartEmulation(UserOptions selectedOptions) {
		SystemWrapper.BootSystem(selectedOptions, byteROM, byteAddData);
		
		this.setVisible(false);
		this.dispose();
		frmControls.setVisible(false);
		frmControls.dispose();
	}
	
	// This subroutine is called automatically to allow the user to select a CHIP-8 ROM.
	public void ImportROM(JFrame frame) throws IOException, NoROMSelectedException, ROMExceedsAvailMemoryException {
		JFileChooser jfChooser = new JFileChooser();
		jfChooser.setDialogTitle("EMUL-8: Select CHIP-8 ROM");
		File fileROM;
	    int int_return_val = jfChooser.showOpenDialog(frame);
	    
	    if (int_return_val == JFileChooser.APPROVE_OPTION) {
	    	fileROM = new File(jfChooser.getSelectedFile().getAbsolutePath());
		    byte[] ROMbuffer = Files.readAllBytes(fileROM.toPath());
		    
		    if (ROMbuffer.length <= (4095 - 512 + 1)) {
		    	selectedOptions.strROMName = jfChooser.getSelectedFile().getName();
		    	byteROM = ROMbuffer;
		    } else {
		    	throw new ROMExceedsAvailMemoryException("The selected ROM is too large to fit within the 4096 bytes of virtual memory. Exiting.");
		    }
	    } else {
	    	throw new NoROMSelectedException("No CHIP-8 ROM was selected!");
	    }
	}
	
	// This subroutine updates the color preview labels when new colors are chosen.
	private void UpdateColors(String strColor, JLabel lblObject) {
		lblObject.setBackground(DispColors.ColorDictionary(strColor));
	}

	public frmOptions() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(750, 170));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addWindowListener(new ClosingWindowAdapter("Are you sure you want to quit EMUL-8?", "Quit EMUL-8?"));
		
		// Set JFrame icon for Windows Operating Systems
		ImageIcon imgOldIcon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
		Image oldimg = imgOldIcon.getImage();
		Image newimg = oldimg.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		ImageIcon imgNewIcon = new ImageIcon(newimg);
		setIconImage(imgNewIcon.getImage());
		
		// Pack and center the JFrame
		pack();
		setLocationRelativeTo(null);
		
		// Hide the keyboard controls JFrame
		frmControls.setVisible(false);
		
		// Import CHIP-8 ROM
		try {
			ImportROM(this); // Passing the JFrame as an argument sets the JFileChooser icon
		} catch (NoROMSelectedException e) {
			System.exit(0);			
		} catch (IOException e) {
			DisplayError.ErrorBox("There was an error opening the ROM. Exiting.", "EMUL-8 Critical Error");
			System.exit(1);
		} catch (ROMExceedsAvailMemoryException e) {
			DisplayError.ErrorBox(e.getMessage(), "EMUL-8 Critical Error");
			System.exit(1);
		}
		
		// Set frame title
		setTitle("EMUL-8: " + selectedOptions.strROMName + " Emulation Options");
		
		// HEADINGS
			// Memory Options
			JLabel lblMemoryOptions = new JLabel("Memory Options");
			lblMemoryOptions.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			lblMemoryOptions.setBounds(16, 6, 160, 25);
			contentPane.add(lblMemoryOptions);
			
			// Graphics Options
			JLabel lblGraphicsOptions = new JLabel("Graphics Options");
			lblGraphicsOptions.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			lblGraphicsOptions.setBounds(236, 6, 237, 25);
			contentPane.add(lblGraphicsOptions);
			
			// Additional Options
			JLabel lblAdditionalOptions = new JLabel("Additional Options");
			lblAdditionalOptions.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			lblAdditionalOptions.setBounds(500, 6, 237, 25);
			contentPane.add(lblAdditionalOptions);
		
		// LABELS
			// Additional Data
			JLabel lblAdditionalData = new JLabel("Write Additional Data");
			lblAdditionalData.setHorizontalAlignment(SwingConstants.LEFT);
			lblAdditionalData.setBounds(16, 34, 140, 16);
			contentPane.add(lblAdditionalData);
			
			// Offset
			JLabel lblOffset = new JLabel("Write at Offset:");
			lblOffset.setBounds(16, 67, 110, 16);
			contentPane.add(lblOffset);
			
			// Scale
			JLabel lblScale = new JLabel("Scaling Factor:");
			lblScale.setBounds(236, 34, 117, 16);
			contentPane.add(lblScale);
			
			// Foreground
			JLabel lblForeground = new JLabel("Foreground:");
			lblForeground.setBounds(236, 67, 84, 16);
			contentPane.add(lblForeground);
			
			// Background
			JLabel lblBackground = new JLabel("Background:");
			lblBackground.setBounds(236, 99, 84, 16);
			contentPane.add(lblBackground);
			
			// CPU Speed
			JLabel lblCPUSpeed = new JLabel("Processor Speed (Hz):");
			lblCPUSpeed.setBounds(500, 34, 160, 16);
			contentPane.add(lblCPUSpeed);
			
			// Clock Speed
			JLabel lblClockSpeed = new JLabel("Timer Speed (Hz):");
			lblClockSpeed.setBounds(500, 67, 160, 16);
			contentPane.add(lblClockSpeed);
			
			// System Monitor
			JLabel lblSystemMonitor = new JLabel("Start with System Monitor");
			lblSystemMonitor.setBounds(500, 99, 170, 16);
			contentPane.add(lblSystemMonitor);
			
			// Fore Color
			lblForeColor = new JLabel("");
			lblForeColor.setOpaque(true);
			lblForeColor.setBounds(325, 67, 40, 16);
			lblForeColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			contentPane.add(lblForeColor);
			
			// Back Color
			lblBackColor = new JLabel("");
			lblBackColor.setOpaque(true);
			lblBackColor.setBounds(325, 99, 40, 16);
			lblBackColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			contentPane.add(lblBackColor);
		
		// SPINNERS
			// Scale
			SpinnerNumberModel spinmodScale = new SpinnerNumberModel(10, 1, 20, 1);
			JSpinner spinScale = new JSpinner(spinmodScale);
			spinScale.setBounds(372, 29, 101, 26);
			contentPane.add(spinScale);
			
			// CPU Speed
			SpinnerNumberModel spinmodCPUSpeed = new SpinnerNumberModel(500, 1, 100000, 10);
			JSpinner spinCPUSpeed = new JSpinner(spinmodCPUSpeed);
			spinCPUSpeed.setBounds(659, 29, 78, 26);
			contentPane.add(spinCPUSpeed);
			
			// Clock Speed
			SpinnerNumberModel spinmodClockSpeed = new SpinnerNumberModel(60, 1, 100000, 10);
			JSpinner spinClockSpeed = new JSpinner(spinmodClockSpeed);
			spinClockSpeed.setBounds(659, 62, 78, 26);
			contentPane.add(spinClockSpeed);
		
		// CHECKBOXES
			// Additional Data
			JCheckBox chkAdditionalData = new JCheckBox("");
			chkAdditionalData.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chkAdditionalData.isSelected()) {
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog (null, "WARNING: The program may overwrite existing data in memory (including the CHIP-8 fontset and selected ROM).\nThis may result in unexpected behaviour. Are you sure you want to continue?", "EMUL-8 Warning", dialogButton);
						if(dialogResult == JOptionPane.YES_OPTION) {
							byteAddData = CheckValidFile(OpenData());
							if (byteAddData == null) { // Is null in error cases
								chkAdditionalData.setSelected(false);
							}
						} else {
							chkAdditionalData.setSelected(false);
						}
					}
					
					txtOffset.setEnabled(chkAdditionalData.isSelected());
				}
			});
			chkAdditionalData.setHorizontalAlignment(SwingConstants.RIGHT);
			chkAdditionalData.setBounds(158, 29, 50, 26);
			contentPane.add(chkAdditionalData);
			
			// System Monitor
			JCheckBox chkSystemMonitor = new JCheckBox("");
			chkSystemMonitor.setHorizontalAlignment(SwingConstants.RIGHT);
			chkSystemMonitor.setBounds(669, 97, 68, 20);
			contentPane.add(chkSystemMonitor);
			
		// COMBOBOXES
			// Foreground
			JComboBox<String> comboForeground = new JComboBox<String>(DispColors.strColors);
			comboForeground.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String strForeColor = (String) comboForeground.getSelectedItem();
					UpdateColors(strForeColor, lblForeColor);
				}
			});
			comboForeground.setBounds(372, 63, 101, 27);
			comboForeground.setSelectedItem("White");
			contentPane.add(comboForeground);
			
			// Background
			JComboBox<String> comboBackground = new JComboBox<String>(DispColors.strColors);
			comboBackground.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String strBackColor = (String) comboBackground.getSelectedItem();
					UpdateColors(strBackColor, lblBackColor);
				}
			});
			comboBackground.setBounds(372, 95, 101, 27);
			comboBackground.setSelectedItem("Black");
			contentPane.add(comboBackground);
		
		// TEXTBOXES
			// Offset
			txtOffset = new JTextField();
			txtOffset.setEnabled(false);
			txtOffset.setText("0x");
			txtOffset.setBounds(118, 62, 90, 26);
			txtOffset.setToolTipText("The CHIP-8 has 4K of RAM. Please specify a memory offset between 0x0000 and 0x0FFF inclusive.");
			contentPane.add(txtOffset);
			txtOffset.setColumns(10);
		
		// BUTTONS
			// Start
			JButton btnStart = new JButton("Start Emulation");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Grab all user settings
					selectedOptions.boolAddData = chkAdditionalData.isSelected();
					selectedOptions.boolSystemMonitor = chkSystemMonitor.isSelected();
					selectedOptions.colForeground = DispColors.ColorDictionary((String) comboForeground.getSelectedItem());
					selectedOptions.colBackground = DispColors.ColorDictionary((String) comboBackground.getSelectedItem());
					selectedOptions.strOffset = txtOffset.getText();
					selectedOptions.intScale = (int) spinScale.getValue();
					selectedOptions.intCPUSpeed = (int) spinCPUSpeed.getValue();
					selectedOptions.intClockSpeed = (int) spinClockSpeed.getValue();

					if (selectedOptions.boolAddData) {
						if (CheckValidOffset() != -1) {
							selectedOptions.intOffset = CheckValidOffset();
							StartEmulation(selectedOptions);
						} else {
							txtOffset.setText("0x");
						}
					} else {
						StartEmulation(selectedOptions);
					}
				}
			});
			btnStart.setBounds(6, 131, 738, 29);
			contentPane.add(btnStart);
			
			// Controls
			JButton btnControls = new JButton("Modify Keyboard Controls");
			btnControls.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Only open the window if it is not already open
					if (!frmControls.isVisible()) {
						frmControls = new frmControls();
						frmControls.setVisible(true);
					}
				}
			});
			btnControls.setBounds(16, 94, 192, 29);
			contentPane.add(btnControls);
	}
}