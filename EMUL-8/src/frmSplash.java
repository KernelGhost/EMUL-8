import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class frmSplash extends JFrame {
	private static final long serialVersionUID = 7425173975053690462L;
	public static frmSplash frmSplash;
	public static frmOptions frmOptions;
	private JPanel contentPane;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Set Nimbus "Look and Feel" (will fall back to default if unavailable)
				boolean boolNimbus = false;
				for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						try {
							javax.swing.UIManager.setLookAndFeel(info.getClassName());
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
								| UnsupportedLookAndFeelException e) {
							DisplayError.ErrorBox("Could not start the application with the preferred look and feel.\nUsing system defaults.", "EMUL-8 Error");
						}
						
						boolNimbus = true;
				        break;
					}
			    }
				
				if (!boolNimbus) {
					DisplayError.ErrorBox("Could not start the application with the preferred look and feel.\nUsing system defaults.", "EMUL-8 Error");
				}
				
				// Create the splash screen
				try {
					frmSplash = new frmSplash();
					frmSplash.setVisible(true);
				} catch (Exception e) {
					DisplayError.ErrorBox("Could not create the splash screen. Exiting.", "EMUL-8 Critical Error");
					System.exit(1);
				}
			}
		});
	}
	
	// Runs when the user clicks the splash screen
	public static void CloseSplash() {
		frmSplash.setVisible(false);
		frmSplash.dispose();
		frmOptions = new frmOptions();
		frmOptions.setVisible(true);
	}
	
	public frmSplash() {
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 330);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setBackground(new Color(73,74,78));
		contentPane = new JPanel();
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				CloseSplash();
			}
		});
		contentPane.setBackground(new Color(73,74,78));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		// Splash Screen EMUL-8 Icon/Logo
		try {
			JLabel lblIcon = new JLabel("");
			lblIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					CloseSplash();
				}
			});
			lblIcon.setBackground(new Color(73,74,78));
			lblIcon.setBounds(15, 15, 300, 300);
			lblIcon.setOpaque(true);
			BufferedImage imgIcon = ImageIO.read(getClass().getResource("/resources/graphics/Icon.png"));
			Image imgIconScaled = imgIcon.getScaledInstance(lblIcon.getWidth(), lblIcon.getHeight(), Image.SCALE_SMOOTH);
			ImageIcon icoIcon = new ImageIcon(imgIconScaled);
			lblIcon.setIcon(icoIcon);
			contentPane.add(lblIcon);
		} catch (IOException e) {
			DisplayError.ErrorBox("Could not load the application icon. Exiting.", "EMUL-8 Critical Error");
			System.exit(1);
		}
		
		// Heading Label
		JLabel lblHeading = new JLabel("EMUL-8");
		lblHeading.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				CloseSplash();
			}
		});
		lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeading.setFont(new Font("Lucida Grande", Font.PLAIN, 70));
		lblHeading.setForeground(Color.WHITE);
		lblHeading.setBounds(327, 93, 310, 67);
		contentPane.add(lblHeading);
		
		// Subheading Label
		JLabel lblSubheading = new JLabel("CHIP-8 EMULATOR");
		lblSubheading.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				CloseSplash();
			}
		});
		lblSubheading.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubheading.setForeground(Color.WHITE);
		lblSubheading.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
		lblSubheading.setBounds(327, 164, 310, 50);
		contentPane.add(lblSubheading);
		
		// Author Label
		JLabel lblAuthor = new JLabel("By Rohan Barar, 2020");
		lblAuthor.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				CloseSplash();
			}
		});
		lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);
		lblAuthor.setForeground(Color.WHITE);
		lblAuthor.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblAuthor.setBounds(327, 213, 310, 29);
		contentPane.add(lblAuthor);
		
		// Click Label
		JLabel lblClick = new JLabel("Click anywhere to begin...");
		lblClick.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				CloseSplash();
			}
		});
		lblClick.setForeground(Color.WHITE);
		lblClick.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblClick.setHorizontalAlignment(SwingConstants.CENTER);
		lblClick.setBounds(327, 272, 310, 43);
		contentPane.add(lblClick);
	}
}
