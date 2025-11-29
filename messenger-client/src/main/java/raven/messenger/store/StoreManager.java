package raven.messenger.store;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    private final static String USER_PATH = System.getProperty("user.home");
    private final static String SYSTEM_PATH = "/.rv/ms/data";
    private static StoreManager instance;
    private final ModelFolder[] folders = {
            new ModelFolder("files", new String[]{"file"}),
            new ModelFolder("photos", new String[]{"photo", "profile"}),
            new ModelFolder("voices", new String[]{"voice"}),
            new ModelFolder("others", new String[]{"other"})
    };

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }
        return instance;
    }

    private StoreManager() {
    }

    public File createFile(String name) {
        File file = new File(USER_PATH + SYSTEM_PATH + "/" + name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public File getFile(String name) {
        File file = createFile(name);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public void addFile(File file, String name) {
        try {
            File f = createFile(name);
            FileOutputStream out = new FileOutputStream(f);
            Files.copy(file.toPath(), out);
            out.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void clearData(int index) {
        for (String folder : folders[index].folder) {
            File file = new File(USER_PATH + SYSTEM_PATH + "/" + folder);
            clearData(file);
        }
    }

    public List<ModelFile> getStorageInfo() {
        List<ModelFile> data = new ArrayList<>();
        for (ModelFolder folder : folders) {
            ModelFile model = new ModelFile(folder.name, 0, 0);
            for (String f : folder.folder) {
                File file = new File(USER_PATH + SYSTEM_PATH + "/" + f);
                calculateFolderSize(model, file);
            }
            data.add(model);
        }
        return data;
    }

    private void clearData(File file) {
        if (file.exists()) {
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    clearData(f);
                }
            }
        }
    }

    private void calculateFolderSize(ModelFile model, File file) {
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    model.size += f.length();
                    model.qty++;
                } else if (f.isDirectory()) {
                    calculateFolderSize(model, f);
                }
            }
        }
    }

    private static class ModelFolder {

        public ModelFolder(String name, String[] folder) {
            this.name = name;
            this.folder = folder;
        }

        private final String name;
        private final String[] folder;
    }

    public static class ModelFile {

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public ModelFile(String name, int qty, long size) {
            this.name = name;
            this.qty = qty;
            this.size = size;
        }

        private String name;
        private int qty;
        private long size;
    }
}
