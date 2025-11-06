package raven.messenger.util;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorReporter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void handleException(String message, Throwable throwable, boolean showDialog) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);

        String errorDetails = String.format(
                "Time: %s%nMessage: %s%nError: %s%nStack Trace:%n%s",
                DATE_FORMAT.format(new Date()),
                message,
                throwable.getMessage(),
                sw.toString()
        );

        // Log to console
        System.err.println("=== ERROR REPORT ===");
        System.err.println(errorDetails);
        System.err.println("====================");

        // Show dialog if requested
        if (showDialog && !SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showErrorDialog(message, errorDetails));
        } else if (showDialog) {
            showErrorDialog(message, errorDetails);
        }
    }

    public static void reportError(String title, String message, boolean showDialog) {
        String errorDetails = String.format(
                "Time: %s%nTitle: %s%nMessage: %s",
                DATE_FORMAT.format(new Date()),
                title,
                message
        );

        System.err.println("=== ERROR ===");
        System.err.println(errorDetails);
        System.err.println("=============");

        if (showDialog) {
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(() -> showErrorDialog(title, message));
            } else {
                showErrorDialog(title, message);
            }
        }
    }

    private static void showErrorDialog(String title, String message) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);
        textArea.setBackground(UIManager.getColor("Panel.background"));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

        JOptionPane.showMessageDialog(
                null,
                scrollPane,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}