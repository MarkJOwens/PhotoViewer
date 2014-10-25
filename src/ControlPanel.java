import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * 
 */

/**
 * @author mark owens
 * This panel holds the tags and navigation buttons for the 
 * application.
 *
 */
public class ControlPanel extends JPanel {
	
	private static final long serialVersionUID = -9006640331473616445L;
	private JCheckBox familyTag;
	private JCheckBox vacationTag;
	private JCheckBox schoolTag;
	private JCheckBox drunkTag;
	
	private JButton forward;
	private JButton backward;
	
	private StatusPanel statusPanel; //Text box at bottom of GUI that displays the last action
    private LightPage lightPage;
	
	public ControlPanel(StatusPanel statusPanel, LightPage lightPage){
		this.statusPanel = statusPanel;
		this.lightPage = lightPage;
		
		JPanel tagPanel = getTagPanel();
		JPanel navigationPanel = getNavigationPanel();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tagPanel);
		add(navigationPanel);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	private JPanel getTagPanel(){
		JPanel tagPanel = new JPanel();
		familyTag = new JCheckBox("Family");
		familyTag.setBackground(Color.WHITE);
		vacationTag = new JCheckBox("Vacation");
		vacationTag.setBackground(Color.white);
		schoolTag = new JCheckBox("School");
		schoolTag.setBackground(Color.white);
		drunkTag = new JCheckBox("Drunk");
		drunkTag.setBackground(Color.white);
		
		ItemListener tagListen = new TagListener();
		familyTag.addItemListener(tagListen);
		vacationTag.addItemListener(tagListen);
		schoolTag.addItemListener(tagListen);
		drunkTag.addItemListener(tagListen);
		
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.Y_AXIS));
		tagPanel.add(familyTag);
		tagPanel.add(vacationTag);
		tagPanel.add(schoolTag);
		tagPanel.add(drunkTag);
		
		tagPanel.setBackground(Color.WHITE);
		return tagPanel;
	}
	
	private class TagListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			//Determine which box was clicked
			if (e.getSource() == familyTag){
				if (familyTag.isSelected()){
					statusPanel.setStatus("Family Tag was Selected");
				} else {
					statusPanel.setStatus("Family Tag was Deselected");
				}
			} else if (e.getSource() == vacationTag){
				if (vacationTag.isSelected()){
					statusPanel.setStatus("Vacation Tag was Selected");
				} else {
					statusPanel.setStatus("Vacation Tag was Deselected");
				}
			} else if (e.getSource() == schoolTag){
				if (schoolTag.isSelected()){
					statusPanel.setStatus("School Tag was Selected");
				} else {
					statusPanel.setStatus("School Tag was Deselected");
				}
			} else if (e.getSource() == drunkTag){
				if (drunkTag.isSelected()){
					statusPanel.setStatus("Drunk Tag was Selected");
				} else {
					statusPanel.setStatus("Drunk Tag was Deselected");
				}
			}			
		}		
	}
	private JPanel getNavigationPanel(){
		JPanel buttonPanel = new JPanel();
		backward = new JButton("Go Back");
		backward.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				statusPanel.setStatus("Go Back Button was Pressed");
                lightPage.back();
			}			
		});
		forward = new JButton("Go Forward");
		forward.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				statusPanel.setStatus("Go Forward Button was Pressed");
                lightPage.forward();
			}			
		});
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(backward);
		buttonPanel.add(forward);
		
		buttonPanel.setBackground(Color.WHITE);
		
		return buttonPanel;
	}
	

}
