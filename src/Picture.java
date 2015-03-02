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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
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
    private boolean rightClick;
    private boolean vacationTag, drunkTag, schoolTag, familyTag;
	private Dimension preferredSize;
	private int pictureHeight;
	private int pictureWidth;
    private Thumbnail thumbnail;
    private AlbumController albumController;

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    //List contains all points to draw
	private List<Line> lineList = new ArrayList<Line>();
    private List<Line> gestureList;
	//List of all Strings to draw
	private List<PhotoString> stringList = new ArrayList<PhotoString>();
    /**
     * The following strings are used for the REGEX gesture
     * recognition.
     */
    private final String NORTH = "N";
    private final String NORTH_EAST = "A";
    private final String EAST = "E";
    private final String SOUTH_EAST = "B";
    private final String SOUTH = "S";
    private final String SOUTH_WEST = "C";
    private final String WEST = "W";
    private final String NORTH_WEST = "D";

    private final String NORTHS = "[AND]+";
    private final String NORTH_WESTS = "[NDW]+";
    private final String NORTH_EASTS = "[NAE]+";
    private final String EASTS = "[AEB]+";
    private final String SOUTHS = "[BSC]+";
    private final String SOUTH_WESTS = "[SCW]+";
    private final String SOUTH_EASTS = "[SEB]+";
    private final String WESTS = "[DWC]+";

    private final String START = "^.{0,2}+";
    private final String END = ".{0,2}+$";
    private final String NOISE = ".{0,4}+";

    //advance to next photo
    private final Pattern RIGHT_ANGLE = Pattern.compile(START + SOUTH_EASTS + NOISE + SOUTH_WESTS + END);
    //go to previous photo
    private final Pattern LEFT_ANGLE = Pattern.compile(START + SOUTH_WESTS + NOISE + SOUTH_EASTS + END);
    //delete photo
    private final Pattern PIG_TAIL = Pattern.compile(START + EASTS + SOUTHS + WESTS + NORTHS + EASTS + SOUTHS + END);
    //drunk tag
    private final Pattern QUESTION_MARK = Pattern.compile(START + NORTHS + EASTS + SOUTHS + WESTS + SOUTHS + END);
    //vacation tag
    private final Pattern STAR = Pattern.compile(START + NORTH_EASTS + SOUTH_EASTS + NORTH_WESTS + EASTS + SOUTH_WESTS + END);
    //school tag
    private final Pattern UP_ANGLE = Pattern.compile(START + NORTH_EASTS + SOUTH_EASTS + END);
    //family tag
    private final Pattern DOWN_ANGLE = Pattern.compile(START + SOUTH_EASTS + NORTH_EASTS + END);

	
	public Picture(BufferedImage image, AlbumController albumController) {
        //System.out.println("made it to Picture");
		this.image = image;
        this.albumController = albumController;

		preferredSize = new Dimension(image.getWidth(), image.getHeight());
		setPreferredSize(preferredSize);
		
		pictureHeight = (int)preferredSize.getHeight();
		pictureWidth = (int)preferredSize.getWidth();
		isFlipped = false;
        rightClick = false;
        drunkTag = false;
        schoolTag = false;
        familyTag = false;
        vacationTag = false;

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
        Graphics2D g2d = (Graphics2D) g;
		//System.out.println("paintComponent of Picture called!");
		if(! isFlipped){
			g2d.drawImage(image, 0, 0, null);

		} else {
			

			//draw the back of the picture
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, pictureWidth, pictureHeight);

			g2d.setColor(Color.BLACK);
			for (PhotoString ps : stringList){
				drawString(g2d, ps.getTypedString(), ps.getStringStartX(), ps.getStringStartY());
			}
		}
        drawLines(g2d);
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
			
			if(isFlipped) {
                for (Line l : lineList) {
                    g2d.drawLine(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
                }
            }

            if(rightClick){
                g2d.setColor(Color.RED);
                for (Line l : gestureList){
                    g2d.drawLine(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
                }
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



    private void processGesture(){
        String gestures = buildGestureString();
        if (gestures == null) return;
        System.out.println(gestures);
        //Matcher m = rightAngle.matcher(gestures);
        if (RIGHT_ANGLE.matcher(gestures).find()){
            System.out.println("right angle detected");
            if (!isFlipped) albumController.nextPhoto();
        } else if (LEFT_ANGLE.matcher(gestures).find()){
            System.out.println("left angle detected");
            if (!isFlipped) albumController.previousPhoto();
        } else if (PIG_TAIL.matcher(gestures).find()){
            System.out.println("pigtail detected");
            if (!isFlipped) albumController.deletePhoto();
        } else if (QUESTION_MARK.matcher(gestures).find()){
            System.out.println("question mark detected");
           if(!isFlipped) albumController.toggleTag(Tag.DRUNK);

        } else if (STAR.matcher(gestures).find()){
            System.out.println("star detected");
           if(!isFlipped) albumController.toggleTag(Tag.VACATION);
        } else if (UP_ANGLE.matcher(gestures).find()){
            System.out.println("up angle detected");
            if (!isFlipped) albumController.toggleTag(Tag.SCHOOL);
        } else if (DOWN_ANGLE.matcher(gestures).find()){
            System.out.println("down angle detected");
            if(!isFlipped) albumController.toggleTag(Tag.FAMILY);
        }

    }

    /*
    This method translates the drawn gesture into letters that can
    be used by the REGEX pattern matcher to recognize shapes
     */
    private String buildGestureString(){
        StringBuilder str = new StringBuilder();
        for (Line l : gestureList){
            if(l.getStartX() < l.getEndX()){ //moving East
                if (l.getStartY() < l.getEndY()){
                    str.append(SOUTH_EAST);
                } else if (l.getStartY() > l.getEndY()){
                    str.append(NORTH_EAST);
                } else if (l.getStartY() == l.getEndY()){
                    str.append(EAST);
                }
            } else if (l.getStartX() > l.getEndX()){//moving West
                if (l.getStartY() < l.getEndY()){
                    str.append(SOUTH_WEST);
                }else if (l.getStartY() > l.getEndY()){
                    str.append(NORTH_WEST);
                } else {
                    str.append(WEST);
                }
            } else if (l.getStartX()==l.getEndX())
                if (l.getStartY() > l.getEndY()) {//moving North
                    str.append(NORTH);
                } else if (l.getStartY() < l.getEndY()){//moving South
                    str.append(SOUTH);
                }
        }
        return str.toString();
    }

    public void setTag(Tag tag, boolean isSelected){
        if (tag == Tag.DRUNK){
            drunkTag = isSelected;
        } else if (tag == Tag.FAMILY){
            familyTag = isSelected;
        } else if (tag == Tag.SCHOOL){
            schoolTag = isSelected;
        } else if (tag == Tag.VACATION){
            vacationTag = isSelected;
        }
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

    public boolean isVacationTag() {
        return vacationTag;
    }

    public void setVacationTag(boolean vacationTag) {
        this.vacationTag = vacationTag;
    }

    public boolean isDrunkTag() {
        return drunkTag;
    }

    public void setDrunkTag(boolean drunkTag) {
        this.drunkTag = drunkTag;
    }

    public boolean isSchoolTag() {
        return schoolTag;
    }

    public void setSchoolTag(boolean schoolTag) {
        this.schoolTag = schoolTag;
    }

    public boolean isFamilyTag() {
        return familyTag;
    }

    public void setFamilyTag(boolean familyTag) {
        this.familyTag = familyTag;
    }

    public boolean isTagged(Tag tag){
        if (tag == Tag.DRUNK){
            return isDrunkTag();
        } else if (tag == Tag.FAMILY){
            return isFamilyTag();
        } else if (tag == Tag.SCHOOL){
            return isSchoolTag();
        } else if (tag == Tag.VACATION){
            return isVacationTag();
        }

        return false;
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

            wasDragged = false;
            inWriteMode = false;

            if (SwingUtilities.isRightMouseButton(e)){
                rightClick = true;
                gestureList = new ArrayList<Line>();
               // System.out.println("right click detected");
            }
		}	
		
		@Override
		public void mouseDragged(MouseEvent e){
            if (rightClick){
               	/*
				 * If this is the first point in the mouse dragging event, set the
				 * mouse dragged event flag and initialize the x and y fields, and create
				 * the first new line as a single point (there is no other point to connect to yet)
				 */
                if (!wasDragged){
                    wasDragged = true;
                    gestureList.add(new Line(e.getX(),e.getY(),e.getX(),e.getY()));
                } else {
                    gestureList.add(new Line(prevX, prevY, e.getX(), e.getY()));
                }
            } else if (isFlipped){

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

			}
            prevX = e.getX();
            prevY = e.getY();
            revalidate();
            repaint();
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
            if(rightClick){
                processGesture();
                gestureList = null;
                rightClick = false;
            }
			/*
			 * if the mouse was released without dragging on the flipped side,
			 * the user can type in text to display on the back.
			 */
			else if ( !wasDragged && isFlipped){
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
