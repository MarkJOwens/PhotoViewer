import javax.swing.*;
import java.awt.*;

/**
 * Created by mark on 10/23/14.
 */
public class LightPanel extends JPanel {

    LightPage lightPage;


    public LightPanel(AlbumController albumController){


        //setLayout(new GridLayout(0,1));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 500));
        setVisible(true);
        setOpaque(true);



        lightPage = new LightPage(getPreferredSize(), albumController);
        setBackground(Color.green);

        add(lightPage, BorderLayout.CENTER);


        revalidate();
        repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //System.out.println("Just called paintComponent on LightPanel");
        //System.out.println("lightpage preferred size = " + lightPage.getPreferredSize());
    }

    public LightPage getLightPage(){
        return lightPage;
    }
}
