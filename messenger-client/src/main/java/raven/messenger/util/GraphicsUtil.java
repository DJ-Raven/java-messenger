package raven.messenger.util;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import java.awt.*;

public class GraphicsUtil {

    public static Shape getShape(int x, int y, int width, int height, int level, int type, boolean inside, boolean smallTop) {
        int smallArc = UIScale.scale(5 - (inside ? 2 : 0));
        int bigArc = UIScale.scale(15 - (inside ? 5 : 0));
        int topLeft = 0;
        int topRight = 0;
        int bottomLeft = 0;
        int bottomRight = 0;
        if (level == 0) {
            if (type == 1) {
                topLeft = topRight = bottomRight = bigArc;
                if (smallTop) {
                    topLeft = topRight = smallArc;
                }
                bottomLeft = smallArc;
            } else if (type == 2) {
                topLeft = topRight = bottomLeft = bigArc;
                bottomRight = smallArc;
            }
        } else if (level == 1) {
            if (type == 1) {
                topLeft = topRight = bottomRight = bigArc;
                bottomLeft = smallArc;
                if (smallTop) {
                    topLeft = topRight = smallArc;
                }
            } else if (type == 2) {
                topLeft = topRight = bottomLeft = bigArc;
                bottomRight = smallArc;
            }
        } else if (level == 2) {
            if (type == 1) {
                topLeft = bottomLeft = smallArc;
                topRight = bottomRight = bigArc;
            } else if (type == 2) {
                topRight = bottomRight = smallArc;
                topLeft = bottomLeft = bigArc;
            }
        } else {
            if (type == 1) {
                topRight = bottomRight = bigArc;
                topLeft = smallArc;
                bottomLeft = smallArc;
            } else if (type == 2) {
                topLeft = bottomLeft = bigArc;
                topRight = smallArc;
                bottomRight = smallArc;
            }
        }
        return FlatUIUtils.createRoundRectanglePath(x, y, width, height, topLeft, topRight, bottomLeft, bottomRight);
    }
}
