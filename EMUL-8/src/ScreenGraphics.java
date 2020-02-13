import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// This class handles the closing of the emulator screen window.
class ScreenWindowAdapter extends WindowAdapter {
	String strMsg;
	String strTitle;
	JFrame currFrame;
	
	public ScreenWindowAdapter(String strMsg, String strTitle, JFrame currFrame) {
		this.strMsg = strMsg;
		this.strTitle = strTitle;
		this.currFrame = currFrame;
	}
	
    @Override
    public void windowClosing(WindowEvent e) {
    	ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
    	Image image = icon.getImage();
    	Image newimg = image.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
    	ImageIcon newicon = new ImageIcon(newimg);
        int result = JOptionPane.showConfirmDialog(null, this.strMsg, this.strTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, newicon);
        
        if (result == JOptionPane.YES_OPTION) {
        	AtomicRun.boolRun.set(false);	// This will halt the CPU and Timer threads
        	
        	if (ScreenGraphics.frmLog.isVisible()) {
        		ScreenGraphics.frmLog.setVisible(false);
        		ScreenGraphics.frmLog.dispose();
        	}
        	
        	currFrame.setVisible(false);
        	currFrame.dispose();
        	
        	frmOptions frmOptions = new frmOptions();
        	frmOptions.setVisible(true);
        }
    }
}

class KeyListener extends KeyAdapter {
	// Detect if a key has been pressed or released, and send this information along with
	// the keycode of the detected key to the 'GetKeys' function.
	
	public void keyPressed(KeyEvent e) {
		SystemWrapper.GetKeys(e.getKeyCode(), true);
		e.consume();
		ScreenGraphics.keyLogger.setText("");
	}
	
	public void keyReleased (KeyEvent e) {
		SystemWrapper.GetKeys(e.getKeyCode(), false);
		e.consume();
		ScreenGraphics.keyLogger.setText("");
	}
}

public class ScreenGraphics extends JPanel {
	private static final long serialVersionUID = 3282096321243110587L;
	
	public static JFrame frmLog;
	JFrame frmScreen;
	static JTextField keyLogger;
	final String strROMName;
	final byte byteScreenWidth;
	final byte byteScreenHeight;
	final int intScreenScale;
	Color colForeground;
	Color colBackground;
	boolean boolGraphics[];
	
	public ScreenGraphics(VirtualSystem virtual_system, boolean boolSystemMonitor) {
		// Load specified settings
		setDoubleBuffered(true);
		setBackground(virtual_system.colBackground);
		this.strROMName = virtual_system.strROMName;
		this.byteScreenWidth = virtual_system.byteScreenWidth;
		this.byteScreenHeight = virtual_system.byteScreenHeight;
		this.intScreenScale = virtual_system.intScreenScale;
		this.boolGraphics = new boolean[this.byteScreenWidth * this.byteScreenHeight];
		this.colForeground = virtual_system.colForeground;
		this.colBackground = virtual_system.colBackground;
		int intReqWidth = this.byteScreenWidth * this.intScreenScale;
		int intReqHeight = this.byteScreenHeight * this.intScreenScale;
		this.setPreferredSize(new Dimension(intReqWidth, intReqHeight));
		this.setLayout(null);
		
		// Create a JFrame to place 'ScreenGraphics' on
		frmScreen = new JFrame();
		frmScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmScreen.setResizable(false);
		frmScreen.setTitle("EMUL-8: Running " + this.strROMName);
		frmScreen.add(this);
		frmScreen.pack();
		frmScreen.setLocationRelativeTo(null);
		frmScreen.addWindowListener(new ScreenWindowAdapter("Are you sure you want to terminate emulation and select a new ROM?", "Stop Emulation?", frmScreen));
		
		// If specified, open the system monitor
		frmLog = new frmLog(virtual_system);
		if (boolSystemMonitor) {
			frmLog.setVisible(true);
		} else {
			frmLog.setVisible(false);
		}
		
		// JFrame icon for Windows Operating Systems
		ImageIcon imgOldIcon = new ImageIcon(this.getClass().getClassLoader().getResource("resources/graphics/Icon.png"));
		Image oldimg = imgOldIcon.getImage();
		Image newimg = oldimg.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		ImageIcon imgNewIcon = new ImageIcon(newimg);
		frmScreen.setIconImage(imgNewIcon.getImage());
		
		// Add JTextField to capture when keys are pressed and released
		keyLogger = new JTextField();
		keyLogger.addKeyListener(new KeyListener());
		keyLogger.setBounds(-1,-1,1,1);
		keyLogger.setFocusTraversalKeysEnabled(false);
		keyLogger.setVisible(true);
		this.add(keyLogger);
		
		// Make the screen visible
		frmScreen.setVisible(true);
	}
	
	public void UpdateScreen(boolean boolGraphics[]) {
		// Locally store the new screen state and call for the screen to be repainted
		this.boolGraphics = boolGraphics;
		frmScreen.revalidate();
		frmScreen.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Iterate through each 'pixel' and paint it on the screen using either the
        // foreground or background color depending on if it is set to true or false.
        
        for (int intCtrY = 0; intCtrY < this.byteScreenHeight; intCtrY++) {
            for (int intCtrX = 0; intCtrX < this.byteScreenWidth; intCtrX++) {	
            	if (this.boolGraphics[intCtrX + (intCtrY * this.byteScreenWidth)] == true) {
            		g2.setColor(this.colForeground);
            	} else {
            		g2.setColor(this.colBackground);
            	}
            	g2.fillRect(intCtrX * this.intScreenScale, intCtrY * this.intScreenScale, this.intScreenScale, this.intScreenScale);
            }
        }
    }
}