import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * 
 */

/**
 * @author mark
 *
 */
public class Thumbnail extends JComponent {


	private static final long serialVersionUID = 8420976692855804511L;
	
	private Picture parentPhoto;
	private BufferedImage image;
	final private int WIDTH = 150;
	final private int HEIGHT = 150;
    private double widthScale;
    private double heightScale;
    private Dimension size;
    private boolean focus;

	
	public Thumbnail(Picture parentPhoto){
		this.parentPhoto = parentPhoto;
		this.image = parentPhoto.getImage();
        this.widthScale = (double)((double)parentPhoto.getPictureWidth()/(double)WIDTH);
        this.heightScale = (double)((double)parentPhoto.getPictureHeight()/(double)HEIGHT);
        widthScale = 1/widthScale;
        heightScale = 1/heightScale;
        //setBorder(BorderFactory.createLineBorder(Color.magenta));

        size = new Dimension(WIDTH, HEIGHT);

        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);

	}
    public void setHighlight(boolean focus){
        this.focus = focus;
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.scale(widthScale,heightScale);
        g2d.drawImage(image, 0, 0, null);
        if(focus) {
            int h = image.getHeight();
            int w = image.getWidth();
            g2d.setColor(Color.red);
            g2d.drawRect(0, 0, w, h);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }

    public Picture getParentPhoto() {
        return parentPhoto;
    }

}
