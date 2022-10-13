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

import mao.cartoonapp.entity.CartoonHistory;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.dao
 * Class(类名): CartoonHistoryDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/13
 * Time(创建时间)： 13:58
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonHistoryDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "CartoonHistory.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "CartoonHistory";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile CartoonHistoryDao cartoonHistoryDao = null;

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
    private static final String TAG = "CartoonHistoryDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CartoonHistoryDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link CartoonHistoryDao}
     */
    public static CartoonHistoryDao getInstance(Context context)
    {
        if (cartoonHistoryDao == null)
        {
            synchronized (CartoonHistoryDao.class)
            {
                if (cartoonHistoryDao == null)
                {
                    cartoonHistoryDao = new CartoonHistoryDao(context);
                }
            }
        }
        return cartoonHistoryDao;
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
            readDatabase = cartoonHistoryDao.getReadableDatabase();
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
            writeDatabase = cartoonHistoryDao.getWritableDatabase();
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
                "id1 VARCHAR PRIMARY KEY NOT NULL," +
                "id2 VARCHAR NOT NULL," +
                "name VARCHAR NOT NULL," +
                "author VARCHAR NOT NULL," +
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
     * @return {@link List}<{@link CartoonHistory}>
     */
    public List<CartoonHistory> queryAll()
    {
        List<CartoonHistory> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            CartoonHistory cartoonHistory = new CartoonHistory();
            setCartoonHistory(cursor, cartoonHistory);
            list.add(cartoonHistory);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id1(主键)查询
     *
     * @param id1 id1(主键)
     * @return {@link CartoonHistory}
     */
    public CartoonHistory queryById(Serializable id1)
    {
        CartoonHistory cartoonHistory = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id1=?", new String[]{String.valueOf(id1)}, null, null, null);
        if (cursor.moveToNext())
        {
            cartoonHistory = new CartoonHistory();
            setCartoonHistory(cursor, cartoonHistory);
        }
        cursor.close();
        return cartoonHistory;
    }


    /**
     * 插入一条数据
     *
     * @param cartoonHistory CartoonHistory对象
     * @return boolean
     */
    public boolean insert(CartoonHistory cartoonHistory)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoonHistory, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<CartoonHistory> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (CartoonHistory cartoonHistory : list)
            {
                boolean insert = this.insert(cartoonHistory);
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
     * @param cartoonHistory CartoonHistory对象
     * @return boolean
     */
    public boolean update(CartoonHistory cartoonHistory)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoonHistory, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id1=?", new String[]{cartoonHistory.getId1().toString()});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param cartoonHistory CartoonHistory对象
     * @return boolean
     */
    public boolean insertOrUpdate(CartoonHistory cartoonHistory)
    {
        boolean insert = insert(cartoonHistory);
        if (insert)
        {
            return true;
        }
        return update(cartoonHistory);
    }

    /**
     * 删除
     *
     * @param id1 id1
     * @return boolean
     */
    public boolean delete(Serializable id1)
    {
        int delete = writeDatabase.delete(TABLE_NAME, "id1=?", new String[]{String.valueOf(id1)});
        return delete > 0;
    }


    /**
     * 填充ContentValues
     *
     * @param cartoonHistory CartoonHistory
     * @param contentValues  ContentValues
     */
    private void setContentValues(CartoonHistory cartoonHistory, ContentValues contentValues)
    {
        contentValues.put("id1", cartoonHistory.getId1());
        contentValues.put("id2", cartoonHistory.getId2());
        contentValues.put("name", cartoonHistory.getName());
        contentValues.put("author", cartoonHistory.getAuthor());
        contentValues.put("imgUrl", cartoonHistory.getImgUrl());
    }

    /**
     * 填充CartoonHistory
     *
     * @param cursor         游标
     * @param cartoonHistory CartoonHistory对象
     */
    private CartoonHistory setCartoonHistory(Cursor cursor, CartoonHistory cartoonHistory)
    {
        cartoonHistory.setId1(cursor.getString(0));
        cartoonHistory.setId2(cursor.getString(1));
        cartoonHistory.setName(cursor.getString(2));
        cartoonHistory.setAuthor(cursor.getString(3));
        cartoonHistory.setImgUrl(cursor.getString(4));
        return cartoonHistory;
    }


}
