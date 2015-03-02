import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;


/**
 * @author Mark Owens
 * This class creates the Framework and menu for the Photo Viewer
 * Application.
 */
public class HomePage extends JFrame{

	private static final long serialVersionUID = 1L;
	private final String title = "Photo Viewer";
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	private final int MIN_WIDTH = 400;
	private final int MIN_HEIGHT = 200;
	
	private StatusPanel statusPanel;
    //private LightPage lightPage;
    private JMenuItem del;
    JMenu magnetMenu;
    private AlbumController albumController;
    private ControlPanel controlPanel;
	
	JRadioButtonMenuItem phoView;
	JRadioButtonMenuItem browser;
	JRadioButtonMenuItem splitMode;
    JRadioButtonMenuItem magnetMode;

    JCheckBoxMenuItem familyMagnet;
    JCheckBoxMenuItem vacationMagnet;
    JCheckBoxMenuItem schoolMagnet;
    JCheckBoxMenuItem drunkMagnet;


	private File file;


	
	public HomePage(){
		
		setTitle(title);
		setJMenuBar(addMenuBar());
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		setLayout(new BorderLayout());

        albumController = new AlbumController();
        albumController.setHomePage(this);

		LightPanel lightPanel = new LightPanel(albumController);

        add(lightPanel, BorderLayout.CENTER);
        albumController.setLightPage(lightPanel.getLightPage());


		statusPanel = new StatusPanel();
        albumController.setStatusPanel(statusPanel);
		add(statusPanel, BorderLayout.SOUTH);

        controlPanel = new ControlPanel(statusPanel, albumController);
		add(controlPanel, BorderLayout.WEST);
        albumController.setControlPanel(controlPanel);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
        pack();
        revalidate();

	}
	public void updateView(Mode mode){
        //System.out.println("updateView called in Homepage");
        if (mode == Mode.SPLIT_MODE){
            splitMode.setSelected(true);
        } else if (mode == Mode.BROWSER){
            browser.setSelected(true);
        } else if (mode == Mode.PHOTO_VIEWER){
            phoView.setSelected(true);
        } else if (mode == Mode.MAGNET){
            magnetMode.setSelected(true);
            drunkMagnet.setSelected(false);
            vacationMagnet.setSelected(false);
            schoolMagnet.setSelected(false);
            familyMagnet.setSelected(false);
        }
    }

    public void disableAllowed(boolean isEnabled){
        del.setEnabled(isEnabled);
    }

    private JMenuBar addMenuBar(){
		JMenuBar returnBar = new JMenuBar();
		returnBar.add(getFileMenu());
		returnBar.add(getViewMenu());
        returnBar.add(getMagnetMenu());

		return returnBar;
	}
	
	private JMenu getFileMenu(){
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem imp = new JMenuItem("Import");
		imp.setMnemonic(KeyEvent.VK_I);
		imp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				albumController.setStatus("Import Menu Item Selected");
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(HomePage.this);
				file = chooser.getSelectedFile();
				try {
					BufferedImage img = ImageIO.read(file);
					albumController.importPhoto(img);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
			}
			
		});
		
		del = new JMenuItem("Delete");
        del.setEnabled(false);
		del.setMnemonic(KeyEvent.VK_D);
		del.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				albumController.setStatus("Delete Menu Item Selected");
				albumController.deletePhoto();
			}
			
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}			
		});
		
		fileMenu.add(imp);
		fileMenu.add(del);
		fileMenu.add(exit);
		
		return fileMenu;
	}
	private JMenu getViewMenu(){
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		RadioMenuListener radioMenuListener = new RadioMenuListener();
		
		phoView = new JRadioButtonMenuItem("Photo Viewer", true);
		phoView.addItemListener(radioMenuListener);
		browser = new JRadioButtonMenuItem("Browser");
		browser.addItemListener(radioMenuListener);
		splitMode = new JRadioButtonMenuItem("Split Mode");
		splitMode.addItemListener(radioMenuListener);
        magnetMode = new JRadioButtonMenuItem("Magnet Mode");
        magnetMode.addItemListener(radioMenuListener);
		
		ButtonGroup b = new ButtonGroup();
		b.add(phoView);
		b.add(browser);
		b.add(splitMode);
        b.add(magnetMode);
		
		viewMenu.add(phoView);
		viewMenu.add(browser);
		viewMenu.add(splitMode);
        viewMenu.add(magnetMode);


		
		return viewMenu;
	}

    private JMenu getMagnetMenu(){
        magnetMenu = new JMenu("Magnet");
        magnetMenu.setEnabled(false);
        magnetMenu.setMnemonic(KeyEvent.VK_M);

        CheckBoxMenuListener cb = new CheckBoxMenuListener();
        familyMagnet = new JCheckBoxMenuItem("Family Magnet");
        familyMagnet.addItemListener(cb);
        vacationMagnet = new JCheckBoxMenuItem("Vacation Magnet");
        vacationMagnet.addItemListener(cb);
        schoolMagnet = new JCheckBoxMenuItem("School Magnet");
        schoolMagnet.addItemListener(cb);
        drunkMagnet = new JCheckBoxMenuItem("Drunk Magnet");
        drunkMagnet.addItemListener(cb);

        magnetMenu.add(familyMagnet);
        magnetMenu.add(vacationMagnet);
        magnetMenu.add(schoolMagnet);
        magnetMenu.add(drunkMagnet);

        return magnetMenu;
    }

    private class CheckBoxMenuListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getSource() == familyMagnet){
                if (familyMagnet.isSelected()){
                    albumController.addMagnet(Tag.FAMILY);
                } else {
                    albumController.removeMagnet(Tag.FAMILY);
                }
            } else if (e.getSource() == vacationMagnet){
                if (vacationMagnet.isSelected()){
                    albumController.addMagnet(Tag.VACATION);
                } else {
                    albumController.removeMagnet(Tag.VACATION);
                }
            } else if (e.getSource() == schoolMagnet){
                if (schoolMagnet.isSelected()){
                    albumController.addMagnet(Tag.SCHOOL);
                } else {
                    albumController.removeMagnet(Tag.SCHOOL);
                }
            } else if (e.getSource() == drunkMagnet){
                if (drunkMagnet.isSelected()){
                    albumController.addMagnet(Tag.DRUNK);
                } else {
                    albumController.removeMagnet(Tag.DRUNK);
                }
            }
        }
    }
	private class RadioMenuListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == phoView){
				if (phoView.isSelected()){
					albumController.setStatus("Photo Viewer was Selected");
                    albumController.changeMode(Mode.PHOTO_VIEWER);
				} else{
					albumController.setStatus("Photo Viewer was Deselected");
				}
			} else if (e.getSource() == browser){
				if (browser.isSelected()){
					albumController.setStatus("Browser was Selected");
                    albumController.changeMode(Mode.BROWSER);
				} else {
					albumController.setStatus("Browser was Deselected");
				}
			} else if (e.getSource() == splitMode){
				if (splitMode.isSelected()){
					albumController.setStatus("Split Mode was Selected");
                    albumController.changeMode(Mode.SPLIT_MODE);
				} else {
					albumController.setStatus("Split Mode was Deselected");
				}
			} else if (e.getSource() == magnetMode){
                if (magnetMode.isSelected()){
                    albumController.setStatus("Magnet Mode was Selected");
                    albumController.changeMode(Mode.MAGNET);
                    magnetMenu.setEnabled(true);
                } else {
                    albumController.setStatus("Magnet Mode was Deselected");
                    magnetMenu.setEnabled(false);
                }
            }
			
		}
		
	}
	

	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new HomePage();
	}
	

}

