import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class DisplayError {
	// A simple subroutine to display errors in the form of error message boxes.
	public static void ErrorBox(String strMessage, String strTitle) {
		ImageIcon icon = new ImageIcon(frmOptions.class.getResource("resources/graphics/Icon.png"));
    	Image image = icon.getImage();
    	Image newimg = image.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
    	ImageIcon newicon = new ImageIcon(newimg);
        JOptionPane.showMessageDialog(null, strMessage, strTitle, JOptionPane.ERROR_MESSAGE, newicon);
    }
}
