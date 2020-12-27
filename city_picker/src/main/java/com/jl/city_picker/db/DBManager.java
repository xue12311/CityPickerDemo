package com.jl.city_picker.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.jl.city_picker.model.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author Bro0cL on 2016/1/26.
 */
public class DBManager {
    private static final int BUFFER_SIZE = 1024;
    private Context mContext;
    private File file_new;

    public DBManager(Context context) {
        this.mContext = context;
        try {
            copyDBFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyDBFile() {
        //老版本数据库
        if (!TextUtils.isEmpty(DBConfig.DB_NAME_Old)) {
            File file_old = mContext.getApplicationContext().getDatabasePath(DBConfig.DB_NAME_Old);
            //如果旧版数据库存在，则删除
            if (file_old.exists()) {
                file_old.delete();
            }
        }
        //新版本数据库
        file_new = mContext.getApplicationContext().getDatabasePath(DBConfig.DB_NAME_New);
        //如果新版本数据库不存在 创建
        if (!file_new.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getApplicationContext().getResources().getAssets().open(DBConfig.DB_NAME_New);
                os = new FileOutputStream(file_new);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<City> getAllCities() {
        List<City> result = new ArrayList<>();
        if (file_new != null && file_new.exists()) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file_new.getAbsolutePath(), null);
            Cursor cursor = db.rawQuery("select * from " + DBConfig.TABLE_NAME + " ORDER BY c_pinyin ASC", null);
            City city;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_NAME));
                String province = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_PROVINCE));
                String pinyin = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_PINYIN));
                String code = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_CODE));
                city = new City(name, province, pinyin, code);
                result.add(city);
            }
            cursor.close();
            db.close();
            //数据库排序
//            Collections.sort(result, new CityComparator());
        }
        return result;
    }

    public List<City> searchCity(final String keyword) {
        List<City> result = new ArrayList<>();
        if (file_new != null && file_new.exists()) {
            String sql = "select * from " + DBConfig.TABLE_NAME + " where "
                    + DBConfig.COLUMN_C_NAME + " like ? " + "or "
                    + DBConfig.COLUMN_C_PINYIN + " like ? " + " ORDER BY c_pinyin ASC";
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file_new.getAbsolutePath(), null);
            Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%", keyword + "%"});

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_NAME));
                String province = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_PROVINCE));
                String pinyin = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_PINYIN));
                String code = cursor.getString(cursor.getColumnIndex(DBConfig.COLUMN_C_CODE));
                City city = new City(name, province, pinyin, code);
                result.add(city);
            }
            cursor.close();
            db.close();
//            CityComparator comparator = new CityComparator();
//            Collections.sort(result, comparator);
        }
        return result;
    }

    /**
     * sort by a-z
     */
//    private class CityComparator implements Comparator<City> {
//        @Override
//        public int compare(City lhs, City rhs) {
//            String a = lhs.getPinyin().substring(0, 1);
//            String b = rhs.getPinyin().substring(0, 1);
//            return a.compareTo(b);
//        }
//    }
}
