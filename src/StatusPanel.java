import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 */

/**
 * @author mark owens
 * This class displays the status of the application and will be used
 * to communicate to the user as needed.
 *
 */
public class StatusPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7570033374247285254L;
	private JTextField statusField;
	private JLabel statusLabel;
	
	public StatusPanel(){
		
		statusLabel = new JLabel("Status:");
		add(statusLabel);
		
		statusField = new JTextField(50);
		statusField.setEditable(false);
		statusField.setLayout(new GridLayout(1,1));
		statusField.setBackground(Color.white);
		
		add(statusField);
		setBackground(Color.white);
		
		setBorder(BorderFactory.createEtchedBorder());

	}
	
	public void setStatus(String stat){
		statusField.setText(stat);
	}
}


