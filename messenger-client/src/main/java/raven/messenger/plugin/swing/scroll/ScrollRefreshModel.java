package raven.messenger.plugin.swing.scroll;

import javax.swing.*;

public abstract class ScrollRefreshModel {

    protected int defaultInitPage;
    protected int initPage;
    protected int locations = SwingConstants.BOTTOM;
    protected boolean hasNext = true;
    protected ScrollRefresh scrollRefresh;

    private ScrollRefreshModel() {
        this(1, SwingConstants.BOTTOM);
    }

    public ScrollRefreshModel(int initPage, int locations) {
        this.initPage = initPage;
        this.locations = locations;
        this.defaultInitPage = initPage;
    }

    public void resetPage() {
        initPage = defaultInitPage;
        hasNext = true;
        scrollRefresh.start();
    }

    public void stop() {
        scrollRefresh.stop();
        hasNext = false;
    }

    public void nextPage() {
        initPage++;
    }

    public int getPage() {
        return initPage;
    }

    public abstract boolean onRefreshNext();

    public abstract void onFinishRefresh();

    public abstract void onFinishData();

    public abstract void onError(Exception e);
}
