package raven.messenger.store;

import io.restassured.http.Cookie;
import io.restassured.http.Cookies;

import java.io.*;

public class CookieManager {

    private final static String USER_PATH = System.getProperty("user.home");
    private final static String SYSTEM_PATH = "/.rv/ms/";
    private static CookieManager instance;
    private String cookieString;

    public static CookieManager getInstance() {
        if (instance == null) {
            instance = new CookieManager();
        }
        return instance;
    }

    private CookieManager() {
        File file = createFile("");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private File createFile(String fileName) {
        File file = new File(USER_PATH + SYSTEM_PATH + fileName);
        return file;
    }

    private void writeObject(File file, Object object) throws IOException {
        OutputStream out = null;
        ObjectOutput objOut = null;
        try {
            out = new FileOutputStream(file);
            objOut = new ObjectOutputStream(out);
            objOut.writeObject(object);
        } finally {
            if (objOut != null) {
                objOut.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private Object readObject(File file) throws IOException, ClassNotFoundException {
        InputStream in = null;
        ObjectInput objIn = null;
        try {
            in = new FileInputStream(file);
            objIn = new ObjectInputStream(in);
            return objIn.readObject();
        } finally {
            if (objIn != null) {
                objIn.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    public void storeCookie(Cookies cookie) throws IOException {
        File file = createFile("cookie");
        if (cookie == null) {
            if (file.exists()) {
                file.delete();
            }
        } else {
            writeObject(file, cookie.toString());
        }
    }

    public Cookies getCookie() throws IOException, ClassNotFoundException {
        File file = createFile("cookie");
        if (!file.exists()) {
            return null;
        }
        Cookie cookie = new Cookie.Builder(readObject(file).toString()).build();
        return new Cookies(cookie);
    }

    public String getCookieString() {
        return getToken(cookieString);
    }

    public void setCookieString(String cookieString) {
        this.cookieString = cookieString;
    }

    public void clearCookie() {
        try {
            storeCookie(null);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private String getToken(String inputString) {
        String[] keyValuePairs = inputString.split(";");
        for (String pair : keyValuePairs) {
            if (pair.contains("accessToken")) {
                String[] keyValue = pair.split("=");
                return keyValue[1];
            }
        }
        return null;
    }
}
