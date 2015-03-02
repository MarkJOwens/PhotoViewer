

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by mark on 10/23/14.
 */
public class ThumbPanel extends JPanel {
    private Picture currentPicture;
    private List<Picture> pictureList;
    private LightPage lightPage;
    private List<Thumbnail> thumbList;
    private List<Magnet> magnetList;
    private Map<Thumbnail, Deque<Step>> thumbStepMap;
    private Mode currentMode;
    private final int PANEL_WIDTH = 400;
    private final int PANEL_HEIGHT = 300;
    private final int THIRTIETH_OF_SECOND = 33;
    private final int THUMB_Z_ORDER = -1;
    private final int MAGNET_Z_ORDER = 0;
    private Timer timer;
    boolean timerRunning = false;


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
        } else if (currentMode == Mode.MAGNET){
            setLayout(null);
            magnetList = new ArrayList<Magnet>();
            thumbStepMap = new HashMap<Thumbnail, Deque<Step>>();
        }

        addThumbs();
        ThumbMouseListener thumbMouseListener = new ThumbMouseListener();
        addMouseListener(thumbMouseListener);
        addMouseMotionListener(thumbMouseListener);
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

        if (currentMode != Mode.MAGNET) {
            for (Thumbnail t : thumbList){
                if (currentMode == Mode.SPLIT_MODE){
                    //add(Box.createHorizontalGlue());
                    add(Box.createRigidArea(new Dimension(10, 10)));
                }
                add(t);
            }
        }

        if (currentMode == Mode.MAGNET){
            System.out.println("about to add thumbs in magnet mode");

            setSize(PANEL_WIDTH,PANEL_HEIGHT);
            setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            Insets insets = getInsets();
            System.out.println(insets.toString());

            int buffer = 10;
            int col = 0;
            int row = 0;
            for (int i = 0; i < thumbList.size(); i++){
                Thumbnail th = thumbList.get(i);
                Dimension dim = th.getPreferredSize();
                int width = (int)dim.getWidth();
                int height = (int)dim.getHeight();
                System.out.println(dim.toString());

                if ((height + buffer) * row > PANEL_HEIGHT){
                    col++;
                    row = 0;
                }
                th.setBounds((col * width + buffer),
                        (row * height + buffer),
                        (int)dim.getWidth(),
                        (int)dim.getHeight());
                row++;
                add(th);
            }
        }
    }

    public void removeThumbs(){
        for (Thumbnail t : thumbList){
            if(thumbList.contains(t)) remove(t);
        }
        revalidate();
        repaint();
    }

    public void addMagnet(Tag tag){
        System.out.println("made it to addMagnet");
        Magnet magnet = new Magnet(tag);
        magnetList.add(magnet);
        Dimension dim = magnet.getPreferredSize();
        magnet.setVisible(true);
        add(magnet);
        //setComponentZOrder(magnet,MAGNET_Z_ORDER);
        magnet.setBounds(0, PANEL_HEIGHT/2, (int)dim.getWidth(), (int)dim.getHeight());
        //magnet.setBounds(0, 0, 100, 100);
        setZOrder();
        repaint();
    }

    public void removeMagnet(Tag tag){

        if (magnetList != null) {
            Iterator<Magnet> iter = magnetList.iterator();
            while (iter.hasNext()){
                Magnet mag = iter.next();
                if (mag.getTag() == tag){
                   // magnetList.remove(mag);
                    iter.remove();
                    remove(mag);
                    setZOrder();
                    repaint();
                    break;
                }
            }
        }
    }

    public void setZOrder(){
        int zOrder = 0;
        for (Magnet m : magnetList){
            setComponentZOrder(m, zOrder);
            zOrder++;
        }
        for (Thumbnail t : thumbList){
            setComponentZOrder(t,zOrder);
            zOrder++;
        }
    }

    private void animateSteps(Magnet movedMagnet){
        System.out.println("animateSteps");
        List<Thumbnail> thumbsToCalculate = new LinkedList<Thumbnail>();
        Tag movedTag = movedMagnet.getTag();
        for (Thumbnail t : thumbList){
            Picture p = t.getParentPhoto();
            if (p.isTagged(movedTag)){
                thumbsToCalculate.add(t);
            }
        }

        if (thumbsToCalculate.size() == 0){
            return;
        }

        Map<Thumbnail, List<Magnet>> magnetThumbMap = getMagnetsForSelectedThumbs(thumbsToCalculate);
        updateThumbStepMap(magnetThumbMap);
        //System.out.println("about to see if the timer is running");
        if (!timerRunning){
            //System.out.println("timer not running, about to start it");
            timer = new Timer(THIRTIETH_OF_SECOND, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //System.out.println("made it to the timer!");
                    timerRunning = true;
                    //private Map<Thumbnail, Deque<Step>> thumbStepMap;
                    Iterator<Thumbnail> iter = thumbStepMap.keySet().iterator();
                    //for (Thumbnail t : thumbSet){
                    try {
                        while (iter.hasNext()){
                            Thumbnail t = iter.next();
                            Deque<Step> stepDeque = thumbStepMap.get(t);
                            Step nextStep = stepDeque.removeFirst();
                            if (stepDeque.isEmpty()){
                                //thumbStepMap.remove(t);
                                iter.remove();
                            }
                            int newX = t.getX() + (int)nextStep.getX();
                            int newY = t.getY() + (int)nextStep.getY();
                            t.setLocation(newX, newY);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    repaint();
                    if (thumbStepMap.isEmpty()){
                        timerRunning = false;
                        System.out.println("stopping timer");
                        timer.stop();
                    }
                }
            });
            timer.start();
        }
    }

    private void updateThumbStepMap(Map<Thumbnail, List<Magnet>> magnetThumbMap){
        //Map<Thumbnail, Deque<Step>> thumbStepMap;
        System.out.println("made it to updateThumbStepMap");
        Set<Thumbnail> thumbSet = magnetThumbMap.keySet();
        for (Thumbnail t : thumbSet){
            Deque<Step> stepDeque;
            List<Magnet> magList = magnetThumbMap.get(t);
            if (thumbStepMap.containsKey(t)){
                thumbStepMap.remove(t);
            }
            stepDeque = new LinkedList<Step>();
            thumbStepMap.put(t,stepDeque);

            calculateSteps(magList, t, stepDeque);
        }
    }

    private void calculateSteps(List<Magnet> magList, Thumbnail thumbnail, Deque<Step> stepDeque){
        System.out.println("calculating steps");
        int finalX = 0;
        int finalY = 0;
        int startX = 0;
        int startY = 0;


        for (Magnet m : magList){
            finalX += m.getX();
            finalY += m.getY();

        }
        /*Average the location of the x and y coordinates
        for all of magnets that correspond to the tags marked
         on the given thumbnail.
         */
        finalX = finalX / magList.size();
        finalY = finalY / magList.size();
        double xStep;
        double yStep;

        if (stepDeque.isEmpty()) {
            xStep = ((double)finalX - (double)thumbnail.getX()) / 30.0;
            yStep = ((double)finalY - (double)thumbnail.getY()) / 30.0;
        } else {
            Step last = stepDeque.peekLast();
            xStep = ((double)finalX - last.getX()) / 30.0;
            yStep = ((double)finalY - last.getY()) / 30.0;
        }

        for (int i = 0 ; i < 30; i++){
            Step step = new Step(xStep, yStep);
            stepDeque.addLast(step);
        }
    }

    private Map<Thumbnail, List<Magnet>> getMagnetsForSelectedThumbs(List<Thumbnail> selectedThumbs){
        System.out.println("made it to getMagnetsForSelectedThumbs");
        Map<Thumbnail, List<Magnet>> returnMap = new HashMap<Thumbnail, List<Magnet>>();
        for (Thumbnail t : selectedThumbs){
            Picture curPicture = t.getParentPhoto();
            List<Magnet> magList = new LinkedList<Magnet>();
            for (Magnet m : magnetList){
                if (curPicture.isTagged(m.getTag())){
                    magList.add(m);
                }
            }
            returnMap.put(t, magList);
        }

        return returnMap;
    }

    private class ThumbMouseListener extends MouseInputAdapter{
        boolean magnetPickedUp = false;
        boolean magnetMoved = false;
        Magnet pickedUpMagnet;
        int xOffset = 0;
        int yOffset = 0;
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
        public void mousePressed(MouseEvent e){
            for (Magnet m : magnetList){
                if (m.getBounds().contains(e.getPoint())){
                    System.out.println("mouse Pressed detected inside magnet");
                    magnetPickedUp = true;
                    pickedUpMagnet = m;
                    xOffset = m.getX() - e.getX();
                    yOffset = m.getY() - e.getY();
                    break;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e){
            //System.out.println("Inside mouseDragged, about to see if magnet picked up");
            if (magnetPickedUp){
                magnetMoved = true;
                //System.out.println("made it to mouse dragged");
                pickedUpMagnet.setBounds(e.getX() + xOffset, e.getY() + yOffset, 100, 100);
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            magnetPickedUp = false;
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

            if (magnetMoved){
                animateSteps(pickedUpMagnet);
                magnetMoved = false;
            }
        }
    }
    private class Step{
        private double x;
        private double y;

        /**
         *
         * @param x the amount to increment X
         * @param y the amount to increment Y
         */
        public Step(double x, double y){
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
