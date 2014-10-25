import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 */

/**
 * @author mark owens
 * This class is the panel that will display photos.
 *
 */

public class PhotoViewPanel extends JPanel {

	private static final long serialVersionUID = 3421358377152407720L;
	private Picture currentPicture;


	public PhotoViewPanel(){
		setBackground(Color.darkGray);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void setPhoto(Picture picture){
        deletePhoto();
		currentPicture = picture;
		add(currentPicture);
		setPreferredSize(picture.getPreferredSize());
       // System.out.println("PhotoViewPanel prefered size is: " + getPreferredSize());
        setVisible(true);
        //System.out.println("made it to setPhoto in photoviewpanel \n");
		revalidate();
		repaint();
	}

	public void deletePhoto(){
        if (currentPicture != null)	remove(currentPicture);
		revalidate();
		repaint();
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
       // System.out.println("made it to paintComponent of PhotoViewPanel");
    }
}
