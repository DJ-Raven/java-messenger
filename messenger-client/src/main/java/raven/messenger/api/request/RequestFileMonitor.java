package raven.messenger.api.request;

import raven.messenger.util.ErrorReporter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RequestFileMonitor {

    private static Set<String> fileSets = new HashSet<>();

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
    private volatile boolean run;
    private AtomicLong downloadSize = new AtomicLong(0);
    private AtomicLong oldDownloadSize = new AtomicLong(0);
    private Thread progressThread;

    public synchronized void save(InputStream inputStream, long length) {
        if (run) {
            System.err.println("File monitor is already running for: " + fileName);
            return;
        }
        if (fileSets.contains(fileName)) {
            System.err.println("File is already being processed: " + fileName);
            return;
        }

        fileSets.add(fileName);
        new Thread(() -> {
            Path tempPath = null;
            try {
                run = true;
                final int BUFFER_SIZE = 4096;
                byte[] downloadBuffer = new byte[BUFFER_SIZE];

                // Create temp file - FIXED: Use system temp directory instead of trying to use savePath's parent
                String prefix = "temp_" + System.currentTimeMillis() + "_";
                tempPath = Files.createTempFile(prefix, "_" + savePath.getName());
                File tempFile = tempPath.toFile();
                tempFile.deleteOnExit();

                System.out.println("Starting file download: " + fileName + " -> " + savePath.getAbsolutePath());
                System.out.println("Temp file: " + tempFile.getAbsolutePath());
                System.out.println("Expected size: " + length + " bytes");

                try (OutputStream out = new FileOutputStream(tempFile)) {
                    initEvent(length);
                    int downloadBytesRead;
                    while ((downloadBytesRead = inputStream.read(downloadBuffer)) != -1) {
                        out.write(downloadBuffer, 0, downloadBytesRead);
                        downloadSize.addAndGet(downloadBytesRead);
                    }
                }

                inputStream.close();
                run = false;

                if (progressThread != null) {
                    progressThread.join(5000); // Wait up to 5 seconds for progress thread
                }

                System.out.println("File download completed: " + fileName);

            } catch (IOException | InterruptedException e) {
                ErrorReporter.handleException("Error during file download: " + fileName, e, false);
                fileSets.remove(fileName);
                error(e);
            } finally {
                run = false;
                if (tempPath != null) {
                    done(tempPath);
                } else {
                    fileSets.remove(fileName);
                }
                progressThread = null;
            }
        }, "FileDownload-" + fileName).start();
    }

    public void done(Path tempPath) {
        try {
            // Verify temp file exists and has content
            if (!Files.exists(tempPath) || Files.size(tempPath) == 0) {
                throw new IOException("Temp file is empty or doesn't exist");
            }

            // Ensure parent directory exists - FIXED: Use proper File methods
            File parentDir = savePath.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Move temp file to final location
            Files.move(tempPath, savePath.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            fileSets.remove(fileName);

            System.out.println("File successfully saved: " + savePath.getAbsolutePath());
            System.out.println("Final file size: " + savePath.length() + " bytes");

            done(savePath);
        } catch (IOException e) {
            fileSets.remove(fileName);
            ErrorReporter.handleException("Failed to finalize file: " + fileName, e, false);
            error(e);
        }
    }

    private void initEvent(long length) {
        progressThread = new Thread(() -> {
            System.out.println("Starting progress monitoring for: " + fileName);
            while (run && !Thread.currentThread().isInterrupted()) {
                sleep(100); // Reduced frequency to avoid excessive CPU usage
                long currentSize = downloadSize.get();
                long oldSize = oldDownloadSize.get();

                if (oldSize != currentSize) {
                    oldDownloadSize.set(currentSize);
                    responseMonitor(length, currentSize);

                    // Log progress every 10% or 1MB
                    if (length > 0) {
                        int percent = (int) ((currentSize * 100) / length);
                        if (percent % 10 == 0 && percent > 0) {
                            System.out.println(String.format("Download progress: %d%% (%d/%d bytes)",
                                    percent, currentSize, length));
                        }
                    }
                }
            }
            System.out.println("Progress monitoring ended for: " + fileName);
        }, "ProgressMonitor-" + fileName);
        progressThread.start();
    }

    public boolean isRunning() {
        return run;
    }

    public abstract void responseMonitor(long length, long bytesWritten);

    public abstract void done(File file);

    public abstract void error(Exception e);

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Progress monitoring interrupted for: " + fileName);
        }
    }
}