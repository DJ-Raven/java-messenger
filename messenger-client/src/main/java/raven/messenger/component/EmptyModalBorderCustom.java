package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.modal.component.Modal;

import javax.swing.*;
import java.awt.*;

public class EmptyModalBorderCustom extends Modal {

    public EmptyModalBorderCustom(Component component) {
        setLayout(new MigLayout("fill,insets 1", "[fill,200::]", "[fill,200::]"));
        add(component);
        add(createActionTitle(), "pos visual.x2-pref-5 visual.y+5", 0);
    }

    private Component createActionTitle() {
        ToolBarTitle panel = new ToolBarTitle();
        FlatSVGIcon icon = new FlatSVGIcon("raven/modal/icon/close.svg", 0.4f);
        FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
        colorFilter.add(Color.decode("#969696"), Color.decode("#646464"), Color.decode("#F0F0F0"));
        JButton buttonClose = new JButton(icon);
        buttonClose.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:50,50,50,50;");
        buttonClose.addActionListener(e -> getController().closeModal());
        panel.add(buttonClose);
        return panel;
    }

    private static class ToolBarTitle extends JToolBar {

        public ToolBarTitle() {
            setBorder(new ScaledEmptyBorder(3, 3, 3, 3));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            float arc = UIScale.scale(10f);
            g2.setColor(getBackground());
            FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0f, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
