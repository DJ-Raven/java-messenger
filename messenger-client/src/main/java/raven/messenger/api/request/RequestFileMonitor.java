package raven.messenger.api.request;

import java.io.*;

public abstract class RequestFileMonitor {

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getSavePath() {
        return savePath;
    }

    public void setSavePath(File savePath) {
        this.savePath = savePath;
    }

    public RequestFileMonitor(String fileName, File savePath) {
        this.fileName = fileName;
        this.savePath = savePath;
    }

    private String fileName;
    private File savePath;
    private boolean run;
    private double downloadSize;
    private double oldDownloadSize;
    private Thread progressThread;

    public synchronized void save(InputStream inputStream, long length) {
        new Thread(() -> {
            try {
                //  Default 4096
                final int BUFFER_SIZE = 4096;
                byte[] downloadBuffer = new byte[BUFFER_SIZE];
                OutputStream out = new FileOutputStream(savePath);
                int downloadBytesRead;
                run = true;
                initEvent(length);
                while ((downloadBytesRead = inputStream.read(downloadBuffer)) != -1) {
                    out.write(downloadBuffer, 0, downloadBytesRead);
                    downloadSize += downloadBytesRead;
                }
                out.close();
                inputStream.close();
                run = false;
                progressThread.join();
            } catch (IOException | InterruptedException e) {
                error(e);
            } finally {
                run = false;
                done(savePath);
                progressThread = null;
            }
        }).start();
    }

    private void initEvent(long length) {
        progressThread = new Thread(() -> {
            while (run) {
                sleep(1);
                if (oldDownloadSize != downloadSize) {
                    oldDownloadSize = downloadSize;
                    responseMonitor(length, (long) downloadSize);
                }
            }
        });
        progressThread.start();
    }

    public boolean isRunning() {
        return run;
    }

    public abstract void responseMonitor(long length, long bytesWritten);

    public abstract void done(File file);

    public abstract void error(Exception e);

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
