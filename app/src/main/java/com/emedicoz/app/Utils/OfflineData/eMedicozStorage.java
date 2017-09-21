package com.emedicoz.app.Utils.OfflineData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;


public class eMedicozStorage {
    public static final int CONTROL_STORE = 1;
    public static final int DATA_STORE = 1;
    public final ContentResolver contentResolver;
    private String appName;

    public eMedicozStorage(Context context, String appName, int stVersion) {
        this.contentResolver = context.getContentResolver();
        this.appName = appName;
        StoreProvider.eMedicoz_DATABASE_VERSION = stVersion;
        StoreProvider.eMedicoz_AUTHORITY = "com.emedicoz.app.Utils.OfflineData.StoreProvider";
    }

    public int addRecordStore(String key, Object data) {
        return addRecords(appName, key, data);
    }

    private int addRecords(String app, String key, Object data) {
        int ret = Store.STORE_SUCCESS;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(data);
            byte[] byteArray = bos.toByteArray();
            ret = addRecordData(app, key, byteArray, 0, byteArray.length,
                    CONTROL_STORE);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return ret;
    }

    private synchronized int addRecordData(String app, String key,
                                           byte[] bdata, int offset, int length, int type) {

        ContentValues values = new ContentValues();
        values.put(StoreProvider.StoreData.APP, app);
        values.put(StoreProvider.StoreData.KEY, key);
        values.put(StoreProvider.StoreData.DATA, bdata);

        contentResolver.insert(Uri.parse("content://" + StoreProvider.eMedicoz_AUTHORITY + "/data/appkey/" + app + "/" + key), values);
        return 1;
    }

    public Object getRecordObject(String key) {
        Object object = null;
        byte[] data = (byte[]) getRecordData(appName, key);
        if (data == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (in != null)
                    in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    String[] projection = {StoreProvider.StoreData.KEY, StoreProvider.StoreData.DATA};

    private Object getRecordData(String app, String key) {
        String baseURL = "";
       /* if ("".equals(app)) {
            baseURL = "content://" + StoreProvider.eMedicoz_AUTHORITY + "/data/key";
        } else {*/
        baseURL = "content://" + StoreProvider.eMedicoz_AUTHORITY + "/data/appkey/" + app + "/" + key;
//        }
        Cursor c = null;
        try {
            c = contentResolver.query(Uri.parse(baseURL), projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getBlob(1);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }

    public synchronized void deleteStore() {
        contentResolver.delete(Uri.parse("content://" + StoreProvider.eMedicoz_AUTHORITY + "/data"), null, null);
    }

    public synchronized void deleteRecord(String key) {
        contentResolver.delete(Uri.parse("content://" + StoreProvider.eMedicoz_AUTHORITY + "/data/appkey/" + appName + "/" + key), null, null);
    }

}
