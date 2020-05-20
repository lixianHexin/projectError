package com.hexin.plat.huaweiphone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Test(View v){
        try {
            copyFile("classes.dex", "classes.dex");
            File dir = new File(getFilesDir().getAbsolutePath() + "/dex");
            addElement(getClassLoader(),new File(dir, "classes.dex").getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFile(String assetName, String fileName) {
        try {
            File dir = new File(getFilesDir().getAbsolutePath() + "/dex");
            if (!dir.exists()) dir.mkdirs();
            File lxFile = new File(dir, fileName);
            if (lxFile.exists()) {
                boolean result = lxFile.delete();
                Log.e("LxInfo", "delete file:" + lxFile.getPath() + "," + result);
            }
            InputStream mInputStream = getAssets().open(assetName);
            FileOutputStream outputStream = new FileOutputStream(lxFile);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = mInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteCount);
            }
            outputStream.flush();
            outputStream.close();
            mInputStream.close();
            Log.e("LxInfo", "copyFile success");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LxInfo", "copyFile failed:" + e.getMessage());
        }
    }


    public static synchronized void addElement(ClassLoader classLoader, String filePath) throws Exception {
        Object objPathList = getPathListFromClassLoader(classLoader);
        Method method = objPathList.getClass().getMethod("addDexPath", new Class[]{String.class, File.class});
        method.invoke(objPathList, new Object[]{filePath, null});
    }
    private static Object getPathListFromClassLoader(ClassLoader loader) {
        try {
            Object objPathList = getPathList(loader);
            return objPathList;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Object getPathList(Object baseDexClassLoader) throws IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, ClassNotFoundException {
        Field field = findField(baseDexClassLoader, "pathList");
        return field.get(baseDexClassLoader);
    }

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field e = clazz.getDeclaredField(name);
                if (!e.isAccessible()) {
                    e.setAccessible(true);
                }
                return e;
            } catch (NoSuchFieldException var4) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

}
