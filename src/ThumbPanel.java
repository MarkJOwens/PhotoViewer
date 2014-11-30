

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 10/23/14.
 */
public class ThumbPanel extends JPanel {
    private Picture currentPicture;
    private List<Picture> pictureList;
    private LightPage lightPage;
    private List<Thumbnail> thumbList;
    private Mode currentMode;

    public ThumbPanel(Picture currentPicture, List<Picture> pictureList, LightPage lightPage, Mode mode){
        this.currentPicture = currentPicture;
        this.pictureList = pictureList;
        this.lightPage = lightPage;
        this.currentMode = mode;

        setBackground(Color.darkGray);

        if (currentMode == Mode.BROWSER) {
            int rows = pictureList.size()/2;
            int cols = pictureList.size() - pictureList.size()/2;
            if (rows < 5) rows = 5;
            if (cols < 5) cols = 5;
            GridLayout gl = new GridLayout(rows, cols, 10, 10);
            setLayout(gl);
        } else if (currentMode == Mode.SPLIT_MODE){
            BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
            setLayout(box);
        }
   //     setLayout(new GridLayout(pictureList.size()/2, pictureList.size() - pictureList.size()/2));
        addThumbs();
        addMouseListener(new ThumbMouseListener() );
        setVisible(true);
        revalidate();
        repaint();
    }

    private void addThumbs(){
        thumbList = new ArrayList<Thumbnail>();
        for (Picture p : pictureList){
            thumbList.add(p.getThumbnail());
            if (p.equals(currentPicture)){
                p.getThumbnail().setHighlight(true);
            } else {
                p.getThumbnail().setHighlight(false);
            }
        }

        for (Thumbnail t : thumbList){

            if (currentMode == Mode.SPLIT_MODE){
                //add(Box.createHorizontalGlue());
                add(Box.createRigidArea(new Dimension(10, 10)));
            }
            add(t);

        }
    }

    public void removeThumbs(){
        for (Thumbnail t : thumbList){
            if(thumbList.contains(t)) remove(t);
        }
        revalidate();
        repaint();
    }

    private class ThumbMouseListener extends MouseInputAdapter{

        /**
         * On double click, the selected thumbnail is the new
         * current Photo and the view changes to PhotoView
         * @param
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1){
                //first need to determine which (if any) thumbnail was clicked.

                for(Thumbnail t : thumbList){
                    System.out.println(t.getBounds());
                    if (t.getBounds().contains(e.getPoint())){
                        lightPage.setCurrentPicture(t.getParentPhoto());
                        lightPage.changeMode(Mode.PHOTO_VIEWER);
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //need to change the selected photo and the highlighting
            for(Picture p : pictureList){
                Thumbnail t = p.getThumbnail();
                if (t.getBounds().contains(e.getPoint())){
                    lightPage.setCurrentPicture(t.getParentPhoto());
                    currentPicture.getThumbnail().setHighlight(false);
                    currentPicture = p;
                    t.setHighlight(true);
                    revalidate();
                    repaint();
                }
            }
        }
    }
}
