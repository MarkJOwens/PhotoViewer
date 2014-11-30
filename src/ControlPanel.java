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
    private AlbumController albumController;
	
	public ControlPanel(StatusPanel statusPanel, AlbumController albumController){
		this.statusPanel = statusPanel;
		this.albumController = albumController;
		
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

    private void toggleFamilyTag(){
        if (familyTag.isSelected()){
            familyTag.setSelected(false);
            statusPanel.setStatus("Family Tag was Deselected");
        } else {
            familyTag.setSelected(true);
            statusPanel.setStatus("Family Tag was Selected");
        }
    }

    private void toggleDrunkTag(){
        if (drunkTag.isSelected()){
            drunkTag.setSelected(false);
            statusPanel.setStatus("Drunk Tag was Deselected");
        } else {
            drunkTag.setSelected(true);
            statusPanel.setStatus("Drunk Tag was Selected");
        }
    }

    private void toggleVacationTag(){
        if (vacationTag.isSelected()){
            vacationTag.setSelected(false);
            statusPanel.setStatus("Vacation Tag was Deselected");
        } else {
            vacationTag.setSelected(true);
            statusPanel.setStatus("Vacation Tag was Selected");
        }
    }

    private void toggleSchoolTag(){
        if (schoolTag.isSelected()){
            schoolTag.setSelected(false);
            statusPanel.setStatus("School Tag was Deselected");
        } else {
            schoolTag.setSelected(true);
            statusPanel.setStatus("School Tag was Selected");
        }
    }

    public void toggleTag(Tag tag){
        if (tag == Tag.DRUNK){
            toggleDrunkTag();
        } else if (tag == Tag.FAMILY){
            toggleFamilyTag();
        } else if (tag == Tag.SCHOOL){
            toggleSchoolTag();
        } else if (tag == Tag.VACATION){
            toggleVacationTag();
        }
    }

    public void setTag(Tag tag, boolean isChecked){
        if (tag == Tag.DRUNK){
            drunkTag.setSelected(isChecked);
        } else if (tag == Tag.FAMILY){
            familyTag.setSelected(isChecked);
        } else if (tag == Tag.SCHOOL){
            schoolTag.setSelected(isChecked);
        } else if (tag == Tag.VACATION){
            vacationTag.setSelected(isChecked);
        }
    }
	
	private class TagListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			//Determine which box was clicked
			if (e.getSource() == familyTag){
				if (familyTag.isSelected()){
					albumController.setStatus("Family Tag was Selected");
                    albumController.setPhotoTag(Tag.FAMILY, true);
				} else {
					albumController.setStatus("Family Tag was Deselected");
                    albumController.setPhotoTag(Tag.FAMILY, false);
				}
			} else if (e.getSource() == vacationTag){
				if (vacationTag.isSelected()){
					albumController.setStatus("Vacation Tag was Selected");
                    albumController.setPhotoTag(Tag.VACATION, true);
				} else {
					albumController.setStatus("Vacation Tag was Deselected");
                    albumController.setPhotoTag(Tag.VACATION, false);
				}
			} else if (e.getSource() == schoolTag){
				if (schoolTag.isSelected()){
					albumController.setStatus("School Tag was Selected");
                    albumController.setPhotoTag(Tag.SCHOOL, true);
				} else {
					albumController.setStatus("School Tag was Deselected");
                    albumController.setPhotoTag(Tag.SCHOOL, false);
				}
			} else if (e.getSource() == drunkTag){
				if (drunkTag.isSelected()){
					albumController.setStatus("Drunk Tag was Selected");
                    albumController.setPhotoTag(Tag.DRUNK, true);
				} else {
					albumController.setStatus("Drunk Tag was Deselected");
                    albumController.setPhotoTag(Tag.DRUNK, false);
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
                albumController.previousPhoto();
			}			
		});
		forward = new JButton("Go Forward");
		forward.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				statusPanel.setStatus("Go Forward Button was Pressed");
                albumController.nextPhoto();
			}			
		});
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(backward);
		buttonPanel.add(forward);
		
		buttonPanel.setBackground(Color.WHITE);
		
		return buttonPanel;
	}
	

}
