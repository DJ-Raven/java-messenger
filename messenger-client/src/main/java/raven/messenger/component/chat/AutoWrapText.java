package raven.messenger.component.chat;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.text.*;

public class AutoWrapText extends StyledEditorKit {

    private final int space;

    public AutoWrapText() {
        this(0);
    }

    public AutoWrapText(int space) {
        this.space = space;
    }

    @Override
    public ViewFactory getViewFactory() {
        return new WarpColumnFactory();
    }

    private class WarpColumnFactory implements ViewFactory {

        @Override
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WarpLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return space == 0 ? new ParagraphView(elem) : new CustomParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }
            return new LabelView(elem);
        }
    }


    private class CustomParagraphView extends ParagraphView {

        public CustomParagraphView(Element elem) {
            super(elem);
        }

        @Override
        protected short getBottomInset() {
            short inset = super.getBottomInset();
            int index = getViewCount() - 1;
            if (index >= 0) {
                float size = getFlowSpan(index) - getView(index).getPreferredSpan(X_AXIS);
                if (size <= UIScale.scale(space)) {
                    int v = (inset + UIScale.scale(20));
                    return (short) v;
                }
            }
            super.setInsets((short) 1, (short) 1, (short) 1, (short) 1);
            return inset;
        }
    }

    private static class WarpLabelView extends LabelView {

        public WarpLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                default:
                    return super.getMinimumSpan(axis);
            }
        }
    }
}
