import javax.swing.*;
import java.awt.*;

/**
 * Created by mark on 11/30/14.
 */
public class Magnet extends JLabel {

    private Tag tag;

    public Magnet(Tag tag){
        this.tag = tag;
        setPreferredSize(new Dimension(100, 100));
        setText(tag.toString());
        setOpaque(true);
        setBackground(Color.green);
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
    }

    public Tag getTag() {
        return tag;
    }

}
