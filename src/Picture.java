import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 * 
 */

/**
 * @author mark
 *
 */
public class Picture extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 386701480261224732L;
	private BufferedImage image;
	private boolean isFlipped;
	private boolean inWriteMode;
	private Dimension preferredSize;
	private int pictureHeight;
	private int pictureWidth;
    private Thumbnail thumbnail;

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    //List contains all points to draw
	private List<Line> lineList = new ArrayList<Line>();
	//List of all Strings to draw
	private List<PhotoString> stringList = new ArrayList<PhotoString>();
	
	
	public Picture(BufferedImage image) {
        System.out.println("made it to Picture");
		this.image = image;
		preferredSize = new Dimension(image.getWidth(),
				image.getHeight());
		setPreferredSize(preferredSize);

		
		pictureHeight = (int)preferredSize.getHeight();
		pictureWidth = (int)preferredSize.getWidth();
		isFlipped = false;
		
		MouseInputAdapter mouseAdapt= new PictureMouseListener();
		addMouseListener(mouseAdapt);
		addMouseMotionListener(mouseAdapt);
		addKeyListener(new KeyStringListener());
		setBackground(Color.white);	
		setFocusable(true);

        this.thumbnail = new Thumbnail(this);
        revalidate();
        repaint();
		
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		//System.out.println("paintComponent of Picture called!");
		if(! isFlipped){
			g.drawImage(image, 0, 0, null);
		} else {
			
			Graphics2D g2d = (Graphics2D) g;
			//draw the back of the picture
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, pictureWidth, pictureHeight);
			
			drawLines(g2d);
			
			g2d.setColor(Color.BLACK);
			for (PhotoString ps : stringList){
				drawString(g2d, ps.getTypedString(), ps.getStringStartX(), ps.getStringStartY());
			}
		}
	}
	
	/**
	 * This method draws all the lines on the back of the 
	 * photograph
	 * @param g2d A Graphics 2D object.
	 */
	public void drawLines(Graphics2D g2d){
					g2d.setColor(Color.BLUE);
			RenderingHints renderHint = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			renderHint.put(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHints(renderHint);
			
			BasicStroke basStr = new BasicStroke(3, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
			g2d.setStroke(basStr);
			
			
			for (Line l : lineList){
				g2d.drawLine(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
			}
	}
	/**
	 * This method draws a String on the picture and will wrap the text
	 * if the words reach the end of the picture.
	 * @param g2d Graphics2D object
	 * @param str The string to draw
	 * @param x The x coordinate of the start of the String to draw
	 * @param y The y coordinate of the start of the String to draw
	 */
	private void drawString(Graphics2D g2d, String str, final int x,final int y){
		
		String[] words = str.split("\\s"); //split string into individual words.
		int curX = x;
		int curY = y;
		
		for (String s : words){
			s+= " "; //add space to end of word for formatting
			int wordLength = g2d.getFontMetrics().stringWidth(s);
			
			//if current word will overlap, change the starting points
			if (curX + wordLength > pictureWidth){
				
				curX = x;
				curY += (g2d.getFontMetrics().getAscent() 
						+ g2d.getFontMetrics().getDescent() 
						+ g2d.getFontMetrics().getLeading());
			} 
			g2d.drawString(s, curX, curY);
			curX += wordLength;
		}
	}
	
	private void toggleFlipped(){
		if (isFlipped) isFlipped = false;
		else isFlipped = true;
	}

    public BufferedImage getImage() {
        return image;
    }
    public Dimension getPreferredSize() {
        return preferredSize;
    }
    public int getPictureHeight() {
        return pictureHeight;
    }
    public int getPictureWidth() {
        return pictureWidth;
    }
	
	private class KeyStringListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {
			/**
			 * If the user has pressed the mouse and released it without
			 * dragging, and the back of the picture is showing, then 
			 * words will be written.
			 */
			if (inWriteMode){
				PhotoString currentPhoStr = stringList.get(stringList.size() - 1);
				if ((int)e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
					currentPhoStr.removeChar();
				} else {				
					currentPhoStr.addChar(e.getKeyChar());
				}
				revalidate();
				repaint();
			}
			
		}
		
	}
	
	private class PictureMouseListener extends MouseInputAdapter{
		private int prevX;
		private int prevY;
		
		private boolean wasDragged = false;
		/**
		 * If the mouse is double clicked, the photo will flip. If
		 * the mouse is held and dragged while the back of the photo is 
		 * showing, a line will be drawn.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1){
				toggleFlipped();
				repaint();
			} 
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (isFlipped){
				wasDragged = false;
				inWriteMode = false;
			}
		}	
		
		@Override
		public void mouseDragged(MouseEvent e){
			if (isFlipped){
				/*
				 * If this is the first point in the mouse dragging event, set the 
				 * mouse dragged event flag and initialize the x and y fields, and create
				 * the first new line as a single point (there is no other point to connect to yet)
				 */
				if (!wasDragged){
					wasDragged = true;
					lineList.add(new Line(e.getX(),e.getY(),e.getX(),e.getY()));
				} else {
					lineList.add(new Line(prevX, prevY, e.getX(), e.getY()));
				}
				
				prevX = e.getX();
				prevY = e.getY();
				revalidate();
				repaint();
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			/*
			 * if the mouse was released without dragging on the flipped side,
			 * the user can type in text to display on the back.
			 */
			if ( !wasDragged && isFlipped){
				inWriteMode = true;
				stringList.add(new PhotoString(e.getX(), e.getY()));
				requestFocusInWindow();
			}
			wasDragged = false;
			revalidate();
			repaint();
		}
	}
	/**
	 * This class holds the information needed to draw the small
	 * line segments as the user drags the mouse.
	 * @author mark
	 *
	 */
	private class Line{
		private int startX;
		private int startY;
		private int endX;
		private int endY;
		
		public Line( int startX, int startY, int endX, int endY){
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
		
		public int getStartX() {
			return startX;
		}

		public int getEndX() {
			return endX;
		}

		public int getStartY() {
			return startY;
		}

		public int getEndY() {
			return endY;
		}		
	}
	/**
	 * This class holds the String typed by the user on the
	 * back of the photo, along with the x and y coordinates 
	 * pointed to by the mouse, which marks the beginning 
	 * point of the string.
	 * @author mark
	 *
	 */
	private class PhotoString{
		private int stringStartX;
		private int stringStartY;
		String typedString;
		
		public PhotoString(int stringStartX,  int stringStartY){
			this.stringStartX = stringStartX;
			this.stringStartY = stringStartY;
			typedString = "";
		}
		public int getStringStartX() {
			return stringStartX;
		}
		public int getStringStartY() {
			return stringStartY;
		}
		public String getTypedString() {
			return typedString;
		}
		public void addChar(char x){
			typedString = typedString + x;
		}
		public void removeChar(){
			int len = typedString.length();
			if (len > 0){
				typedString = typedString.substring(0, len - 1);
			}
			}
	}



}
