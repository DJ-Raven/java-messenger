package raven.messenger.plugin.swing.scroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class ScrollRefresh extends JScrollPane {

    private Thread thread;
    private Component oldComponent;
    private final ScrollRefreshModel scrollRefreshModel;

    public ScrollRefresh(ScrollRefreshModel scrollRefreshModel) {
        this.scrollRefreshModel = scrollRefreshModel;
        init();
    }

    public ScrollRefresh(ScrollRefreshModel scrollRefreshModel, Component view) {
        super(view);
        this.scrollRefreshModel = scrollRefreshModel;
        init();
    }

    private void init() {
        this.scrollRefreshModel.scrollRefresh = this;
        if (scrollRefreshModel.locations == SwingConstants.TOP || scrollRefreshModel.locations == SwingConstants.BOTTOM) {
            JScrollBar scroll = getVerticalScrollBar();
            scroll.addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    if (scrollRefreshModel.hasNext && isStartAble()) {
                        int value = scroll.getValue();
                        if (scrollRefreshModel.locations == SwingConstants.TOP && value <= 0) {
                            start();
                        } else if (scrollRefreshModel.locations == SwingConstants.BOTTOM && value + scroll.getModel().getExtent() >= scroll.getMaximum()) {
                            start();
                        }
                    }
                }
            });
        }
    }

    protected synchronized void start() {
        initOldComponent();
        thread = new Thread(() -> {
            scrollRefreshModel.hasNext = scrollRefreshModel.onRefreshNext();
            scrollRefreshModel.nextPage();
            scrollRefreshModel.onFinishRefresh();
            if (!scrollRefreshModel.hasNext) {
                scrollRefreshModel.onFinishData();
            }
            if (scrollRefreshModel.hasNext) {
                initOldLocation();
            }
        });
        thread.start();
    }

    public void stop() {
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void initOldComponent() {
        if (scrollRefreshModel.locations == SwingConstants.TOP) {
            JComponent com = (JComponent) getViewport().getView();
            if (scrollRefreshModel.initPage != scrollRefreshModel.defaultInitPage) {
                oldComponent = com.getComponent(0);
            } else {
                oldComponent = null;
            }
        }
    }

    private void initOldLocation() {
        if (scrollRefreshModel.locations == SwingConstants.TOP) {
            if (oldComponent != null) {
                getVerticalScrollBar().setValue(oldComponent.getY());
            } else {
                getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
            }
        }
    }

    private boolean isStartAble() {
        return thread == null || !thread.isAlive();
    }

    public ScrollRefreshModel getScrollRefreshModel() {
        return scrollRefreshModel;
    }
}
