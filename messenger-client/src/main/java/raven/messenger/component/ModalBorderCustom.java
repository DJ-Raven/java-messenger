package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import raven.messenger.util.StyleUtil;
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
        JButton button;
        if (option.getType() != 0) {
            button = super.createButtonOption(option);
            button.putClientProperty(FlatClientProperties.STYLE_CLASS, StyleUtil.BUTTON_SIMPLE);
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[light]background:shade($Panel.background,5%);" +
                    "[dark]background:tint($Panel.background,3%);");
        } else {
            button = new JButton(option.getText());
            button.putClientProperty(FlatClientProperties.STYLE_CLASS, StyleUtil.BUTTON_DEFAULT);
        }
        return button;
    }
}
