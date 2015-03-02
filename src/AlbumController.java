import javafx.scene.effect.Light;

import java.awt.image.BufferedImage;

/**
 * Created by mark on 11/12/14.
 * The purpose of this class is to centralize the communication
 * between components of the Album in order to lessen the amount
 * of coupling between classes.
 */
public class AlbumController {

    private HomePage homePage;
    private ControlPanel controlPanel;
    private LightPage lightPage;
    private StatusPanel statusPanel;
    private ThumbPanel thumbPanel;


    public void setStatus (String status){
        statusPanel.setStatus(status);
    }

    public void setThumbPanel(ThumbPanel thumbPanel){this.thumbPanel = thumbPanel;}

    public void addMagnet(Tag tag){
        thumbPanel.addMagnet(tag);
    }

    public void removeMagnet(Tag tag){
        thumbPanel.removeMagnet(tag);
    }

    public void setPhotoTag(Tag tag, Boolean isSelected){
        Picture picture = lightPage.getCurrentPicture();
        picture.setTag(tag, isSelected);
    }

    public void setTagBoxes(Picture picture){
        controlPanel.setTag(Tag.DRUNK, picture.isDrunkTag());
        controlPanel.setTag(Tag.SCHOOL, picture.isSchoolTag());
        controlPanel.setTag(Tag.VACATION, picture.isVacationTag());
        controlPanel.setTag(Tag.FAMILY, picture.isFamilyTag());
    }

    public void toggleTag(Tag tag){
        controlPanel.toggleTag(tag);
    }

    public void changeMode(Mode newMode){
        lightPage.changeMode(newMode);
    }

    public void importPhoto(BufferedImage bufImg){
        lightPage.importPhoto(bufImg);
    }

    public void disableAllowed(boolean disable){
        homePage.disableAllowed(disable);
    }

    public void setStatusPanel(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public void updateView(Mode newMode){
        homePage.updateView(newMode);
    }

    public void deletePhoto(){
        lightPage.deletePhoto();
    }

    public void nextPhoto(){
        lightPage.forward();
    }

    public void previousPhoto(){
        lightPage.back();
    }

    public void setHomePage(HomePage homePage) {
        this.homePage = homePage;
    }

    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public void setLightPage(LightPage lightPage) {
        this.lightPage = lightPage;
    }
}
