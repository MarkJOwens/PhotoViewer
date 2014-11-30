import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mark on 10/23/14.
 */

public class LightPage extends JComponent {

    private Picture currentPicture;
    private List<Picture> pictureList;
    private Mode currentMode;
    private PhotoViewPanel photoViewPanel;
    private JScrollPane topScroll, bottomScroll;
    //private Dimension preferredSize;
    private ThumbPanel thumbPanel;
    private AlbumController albumController;

    public LightPage(Dimension preferredSize, AlbumController albumController) {
        this.albumController = albumController;
        setPreferredSize(preferredSize);
        setLayout(new BorderLayout());
        pictureList = new LinkedList<Picture>();

        photoViewPanel = new PhotoViewPanel();
        currentMode = Mode.PHOTO_VIEWER;
        topScroll = new JScrollPane(photoViewPanel);
        add(topScroll, BorderLayout.CENTER);

        setOpaque(true);
        setVisible(true);
        setBackground(Color.blue);
        revalidate();
        repaint();

    }

    public Picture getCurrentPicture() {
        return currentPicture;
    }

    public void setCurrentPicture(Picture newPhoto) {
        this.currentPicture = newPhoto;
        if (currentMode == Mode.PHOTO_VIEWER
                || currentMode == Mode.SPLIT_MODE) {
            photoViewPanel.setPhoto(currentPicture);
            albumController.setTagBoxes(newPhoto);

            revalidate();
            repaint();
        }
    }

    public void importPhoto(BufferedImage bufImg) {
        Picture newPhoto = new Picture(bufImg, albumController);
        pictureList.add(0, newPhoto);
        setCurrentPicture(newPhoto);
        changeMode(Mode.PHOTO_VIEWER);
        albumController.disableAllowed(true);
    }

    /**
     * This method deletes the currently selected photo
     */

    public void deletePhoto() {
        if (currentPicture == null) return;
        int pictureIndex = pictureList.indexOf(currentPicture);
        pictureList.remove(pictureIndex);
        /*
        try to get the next picture in list if there is one, otherwise try to get the
        one before it
         */

       // System.out.println("PictureList size is " + pictureList.size());
        if (pictureList.isEmpty()) {
            currentPicture = null;
            albumController.disableAllowed(false);
            changeMode(Mode.PHOTO_VIEWER);
        } else if (pictureList.size() >= pictureIndex + 1) {
            currentPicture = pictureList.get(pictureIndex);
        } else {
            currentPicture = pictureList.get(pictureIndex - 1);
        }
        changeMode(currentMode);
        /**
        if (currentMode == Mode.PHOTO_VIEWER
                || currentMode == Mode.SPLIT_MODE) {
            photoViewPanel.deletePhoto();
            if (currentPicture != null) {
               photoViewPanel.setPhoto(currentPicture);
            }

        }
        **/
    }

    public void changeMode(Mode newMode) {

        //if (newMode == currentMode) return;
        destroyMode();
        albumController.updateView(newMode);
        this.currentMode = newMode;

        if (currentMode == Mode.PHOTO_VIEWER) {
            photoViewPanel = new PhotoViewPanel();

            if (currentPicture != null) {
                photoViewPanel.setPhoto(currentPicture);
            }

            topScroll = new JScrollPane(photoViewPanel);
            //topScroll.add();
            setLayout(new BorderLayout());
            add(topScroll, BorderLayout.CENTER);
            System.out.println("just added photoViewPanel");
            revalidate();
            repaint();
            System.out.println(getSize());

        } else if (currentMode == Mode.SPLIT_MODE) {
            photoViewPanel = new PhotoViewPanel();

            if (currentPicture != null) {
                photoViewPanel.setPhoto(currentPicture);
            }

            topScroll = new JScrollPane(photoViewPanel);
            setLayout(new BorderLayout());
            add(topScroll, BorderLayout.CENTER);

            thumbPanel = new ThumbPanel(currentPicture, pictureList, this, currentMode);
            bottomScroll = new JScrollPane(thumbPanel);

            add(bottomScroll,BorderLayout.SOUTH);
            System.out.println("just added bottomScroll");
            revalidate();
            repaint();

        } else if (currentMode == Mode.BROWSER) {
            setLayout(new BorderLayout());
            thumbPanel = new ThumbPanel(currentPicture, pictureList, this, currentMode);
            topScroll = new JScrollPane(thumbPanel);
            add(topScroll,BorderLayout.CENTER);
            System.out.println("just added topScroll");
            revalidate();
            repaint();

        }
    }

    private void destroyMode() {
        if (currentMode == Mode.PHOTO_VIEWER) {
            if (photoViewPanel != null) photoViewPanel.deletePhoto();
            remove(topScroll);
        } else if(currentMode == Mode.SPLIT_MODE){
            if (photoViewPanel != null) photoViewPanel.deletePhoto();
            thumbPanel.removeThumbs();
            remove(topScroll);
            remove(bottomScroll);
        } else if (currentMode == Mode.BROWSER){
            thumbPanel.removeThumbs();
            remove(topScroll);
        }
    }

    /**
     * This method gets called if the user presses the go forward button.
     * It should advance the current photo if possible.
     */
    public void forward(){
        if (currentPicture == null) return;
        //first see if there are more photos after this one
        int index = pictureList.indexOf(currentPicture);
        if (index >= (pictureList.size() - 1)) return;
        index++;
        currentPicture = pictureList.get(index);
        changeMode(currentMode);
        albumController.setTagBoxes(currentPicture);
    }

    /**
     * this method gets called if the user presses the go back button.
     */
    public void back(){

        if (currentPicture == null) return;
        //first see if this is the first photo
        int index = pictureList.indexOf(currentPicture);
        if (index == 0) return;
        index--;
        currentPicture = pictureList.get(index);
        changeMode(currentMode);
        albumController.setTagBoxes(currentPicture);

    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());
        System.out.println("made it to paint Component in lightPage");
    }
}
