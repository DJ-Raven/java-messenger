package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import raven.modal.component.SimpleModalBorder;
import raven.modal.listener.ModalCallback;
import raven.modal.option.ModalBorderOption;

import javax.swing.*;
import java.awt.*;

public class ModalBorderCustom extends SimpleModalBorder {

    public ModalBorderCustom(Component component, String title, ModalBorderOption option, Option optionsType[], ModalCallback callback) {
        super(component, title, option, optionsType, callback);
    }

    public ModalBorderCustom(Component component, String title, Option optionsType[], ModalCallback callback) {
        super(component, title, optionsType, callback);
    }

    @Override
    protected JButton createButtonOption(Option option) {
        JButton button = super.createButtonOption(option);
        if (button.isDefaultButton()) {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "arc:10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;");
        } else {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "arc:10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "[light]background:shade($Panel.background,5%);" +
                    "[dark]background:tint($Panel.background,3%);");
        }
        return button;
    }
}
