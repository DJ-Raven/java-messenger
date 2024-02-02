package test;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.StringIcon;
import raven.messenger.component.chat.ChatProfile;

import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {

    public Test() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIScale.scale(new Dimension(800, 600)));
        setLocationRelativeTo(null);
        setLayout(new MigLayout());

        JButton change = new JButton("Change");
        change.addActionListener(e -> {
            // panelTimePicker.set24HourView(!panelTimePicker.is24HourView());
        });
        add(change);
        Icon icon=new StringIcon("RV", Color.decode("#40AD18"), 35, 35);
        ChatProfile p=new ChatProfile();
        p.setImage(icon);
        add(p,"width 35!,height 35::");
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Test().setVisible(true));
    }
}
