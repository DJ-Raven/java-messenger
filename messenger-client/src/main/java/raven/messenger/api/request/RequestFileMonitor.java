package raven.messenger.api.request;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public abstract class RequestFileMonitor {

    private static final Set<String> fileSets = new HashSet<>();

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
        if (run) {
            return;
        }
        if (fileSets.contains(fileName)) {
            return;
        }
        fileSets.add(fileName);
        new Thread(() -> {
            Path tempPath = null;
            try {
                run = true;
                //  Default 4096
                final int BUFFER_SIZE = 4096;
                byte[] downloadBuffer = new byte[BUFFER_SIZE];
                tempPath = Files.createTempFile("temp_", "_" + savePath.getName());
                File tempFile = tempPath.toFile();
                tempFile.deleteOnExit();
                OutputStream out = new FileOutputStream(tempFile);
                int downloadBytesRead;
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
                fileSets.remove(fileName);
                error(e);
            } finally {
                run = false;
                if (tempPath != null) {
                    done(tempPath);
                }
                progressThread = null;
            }
        }).start();
    }

    public void done(Path tempPath) {
        try {
            fileSets.remove(fileName);
            Files.copy(tempPath, savePath.toPath());
            done(savePath);
        } catch (IOException e) {
            error(e);
        }
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
            System.err.println(e.getMessage());
        }
    }
}
