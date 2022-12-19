package mao.cartoonapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mao.cartoonapp.entity.Cartoon;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.dao
 * Class(类名): CartoonFavoritesDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 21:13
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonFavoritesDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "cartoonFavorites.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "cartoon_favorites";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile CartoonFavoritesDao cartoonFavoritesDao = null;

    /**
     * 读数据库
     */
    private SQLiteDatabase readDatabase;
    /**
     * 写数据库
     */
    private SQLiteDatabase writeDatabase;

    /**
     * 标签
     */
    private static final String TAG = "CartoonFavoritesDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CartoonFavoritesDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link CartoonFavoritesDao}
     */
    public static CartoonFavoritesDao getInstance(Context context)
    {
        if (cartoonFavoritesDao == null)
        {
            synchronized (CartoonFavoritesDao.class)
            {
                if (cartoonFavoritesDao == null)
                {
                    cartoonFavoritesDao = new CartoonFavoritesDao(context);
                }
            }
        }
        return cartoonFavoritesDao;
    }

    /**
     * 打开读连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openReadConnection()
    {
        if (readDatabase == null || !readDatabase.isOpen())
        {
            readDatabase = cartoonFavoritesDao.getReadableDatabase();
        }
        return readDatabase;
    }

    /**
     * 打开写连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openWriteConnection()
    {
        if (writeDatabase == null || !writeDatabase.isOpen())
        {
            writeDatabase = cartoonFavoritesDao.getWritableDatabase();
        }
        return readDatabase;
    }

    /**
     * 关闭数据库读连接和写连接
     */
    public void closeConnection()
    {
        if (readDatabase != null && readDatabase.isOpen())
        {
            readDatabase.close();
            readDatabase = null;
        }

        if (writeDatabase != null && writeDatabase.isOpen())
        {
            writeDatabase.close();
            writeDatabase = null;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id VARCHAR PRIMARY KEY NOT NULL," +
                "name VARCHAR NOT NULL," +
                "author VARCHAR NOT NULL," +
                "remarks VARCHAR NOT NULL," +
                "imgUrl VARCHAR NOT NULL)";
        db.execSQL(sql);
    }

    /**
     * 数据库版本更新时触发回调
     *
     * @param db         SQLiteDatabase
     * @param oldVersion 旧版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    /**
     * 查询所有
     *
     * @return {@link List}<{@link Cartoon}>
     */
    public List<Cartoon> queryAll()
    {
        List<Cartoon> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            Cartoon cartoon = new Cartoon();
            setCartoon(cursor, cartoon);
            list.add(cartoon);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id(主键)查询
     *
     * @param id id(主键)
     * @return {@link Cartoon}
     */
    public Cartoon queryById(Serializable id)
    {
        Cartoon cartoon = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            cartoon = new Cartoon();
            setCartoon(cursor, cartoon);
        }
        cursor.close();
        return cartoon;
    }


    /**
     * 插入一条数据
     *
     * @param cartoon Cartoon对象
     * @return boolean
     */
    public boolean insert(Cartoon cartoon)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoon, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<Cartoon> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (Cartoon cartoon : list)
            {
                boolean insert = this.insert(cartoon);
                if (!insert)
                {
                    throw new Exception();
                }
            }
            writeDatabase.setTransactionSuccessful();
            return true;
        }
        catch (Exception e)
        {
            writeDatabase.endTransaction();
            Log.e(TAG, "insert: ", e);
            return false;
        }
    }

    /**
     * 更新
     *
     * @param cartoon Cartoon对象
     * @return boolean
     */
    public boolean update(Cartoon cartoon)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoon, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{cartoon.getId().toString()});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param cartoon Cartoon对象
     * @return boolean
     */
    public boolean insertOrUpdate(Cartoon cartoon)
    {
        boolean insert = insert(cartoon);
        if (insert)
        {
            return true;
        }
        return update(cartoon);
    }

    /**
     * 删除
     *
     * @param id id
     * @return boolean
     */
    public boolean delete(Serializable id)
    {
        int delete = writeDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        return delete > 0;
    }

    /**
     * 删除所有
     *
     * @return boolean
     */
    public boolean deleteAll()
    {
        int delete = writeDatabase.delete(TABLE_NAME, "1=1", new String[]{});
        return delete > 0;
    }


    /**
     * 填充ContentValues
     *
     * @param cartoon       Cartoon
     * @param contentValues ContentValues
     */
    private void setContentValues(Cartoon cartoon, ContentValues contentValues)
    {
        contentValues.put("id", cartoon.getId());
        contentValues.put("name", cartoon.getName());
        contentValues.put("author", cartoon.getAuthor());
        contentValues.put("remarks", cartoon.getRemarks());
        contentValues.put("imgUrl", cartoon.getImgUrl());
    }

    /**
     * 填充Cartoon
     *
     * @param cursor  游标
     * @param cartoon Cartoon对象
     */
    private Cartoon setCartoon(Cursor cursor, Cartoon cartoon)
    {
        cartoon.setId(cursor.getString(0));
        cartoon.setName(cursor.getString(1));
        cartoon.setAuthor(cursor.getString(2));
        cartoon.setRemarks(cursor.getString(3));
        cartoon.setImgUrl(cursor.getString(4));
        return cartoon;
    }


}
