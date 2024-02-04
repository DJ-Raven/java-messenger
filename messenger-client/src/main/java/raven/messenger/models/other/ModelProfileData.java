package raven.messenger.models.other;

import javax.swing.*;

public class ModelProfileData {

    public ModelImage getImage() {
        return image;
    }

    public void setImage(ModelImage image) {
        this.image = image;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public ModelProfileData(ModelImage image, Icon icon) {
        this.image = image;
        this.icon = icon;
    }

    private ModelImage image;
    private Icon icon;
}
