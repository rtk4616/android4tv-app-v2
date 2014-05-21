package com.iwedia.gui.widgets;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * With this Class any object can be saved to and loaded from external location.
 * Object MUST be Serializable!!! All Methods are static.
 * 
 * @author Milos Milanovic
 */
public class FileSaveLoad {
    private static final String TAG = "FileSaveLoad";

    /**
     * Method saves object to external storage. Permission for this method is
     * "android.permission.WRITE_EXTERNAL_STORAGE".
     * 
     * @param file
     *        File to save.
     * @param fileName
     *        File Name.
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void saveToSDcard(Object file, String fileName)
            throws IOException, FileNotFoundException {
        File path = new File(Environment.getExternalStorageDirectory() + "/"
                + fileName);
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        if (!path.exists()) {
            path.createNewFile();
        }
        try {
            fos = new FileOutputStream(path);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(file);
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (oos != null) {
                oos.flush();
                oos.close();
            }
        }
        System.out.println(TAG + ": File saved successful!");
    }

    /**
     * Method Loads object from external storage. Permission for this method is
     * "android.permission.WRITE_EXTERNAL_STORAGE".
     * 
     * @param fileName
     *        File Name.
     * @return Object Loaded Object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object loadFromSDcard(String fileName) throws IOException,
            ClassNotFoundException {
        File path = Environment.getExternalStorageDirectory();
        FileInputStream fis = new FileInputStream(path + "/" + fileName);
        Object input;
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            input = (Object) ois.readObject();
            ois.close();
        } finally {
            fis.close();
        }
        System.out.println(TAG + ": File loaded successful!");
        return input;
    }

    /**
     * Method saves object to application private external storage. This method
     * is specially for Android Applications.
     * 
     * @param file
     *        File to save.
     * @param fileName
     *        File Name.
     * @param context
     *        Application Context.
     * @throws IOException
     * @throws FileNotFoundException
     */
    @SuppressWarnings("static-access")
    public static void save(Object file, String fileName, Context context)
            throws IOException, FileNotFoundException {
        FileOutputStream fos = context.openFileOutput(fileName,
                context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(file);
        oos.flush();
        oos.close();
        fos.close();
        System.out.println(TAG + ": File saved successful!");
    }

    /**
     * Method loads object from application private external storage. This
     * method is specially for Android Applications.
     * 
     * @param fileName
     *        File Name.
     * @param context
     *        Application Context.
     * @return object Loaded Object.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public static Object load(String fileName, Context context)
            throws IOException, FileNotFoundException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object input = (Object) ois.readObject();
        ois.close();
        fis.close();
        System.out.println(TAG + ": File loaded successful!");
        return input;
    }
}
