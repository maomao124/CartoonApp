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

import mao.cartoonapp.entity.CartoonUpdate;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.dao
 * Class(类名): CartoonUpdateDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/21
 * Time(创建时间)： 15:32
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartoonUpdateDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "cartoonUpdate.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "cartoon_update";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile CartoonUpdateDao cartoonUpdate = null;

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
    private static final String TAG = "CartoonUpdateDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CartoonUpdateDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link CartoonUpdateDao}
     */
    public static CartoonUpdateDao getInstance(Context context)
    {
        if (cartoonUpdate == null)
        {
            synchronized (CartoonUpdateDao.class)
            {
                if (cartoonUpdate == null)
                {
                    cartoonUpdate = new CartoonUpdateDao(context);
                }
            }
        }
        return cartoonUpdate;
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
            readDatabase = cartoonUpdate.getReadableDatabase();
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
            writeDatabase = cartoonUpdate.getWritableDatabase();
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
                "itemCount INTEGER NOT NULL)";
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
     * @return {@link List}<{@link CartoonUpdate}>
     */
    public List<CartoonUpdate> queryAll()
    {
        List<CartoonUpdate> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            CartoonUpdate cartoonUpdate = new CartoonUpdate();
            setCartoonUpdate(cursor, cartoonUpdate);
            list.add(cartoonUpdate);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id(主键)查询
     *
     * @param id id(主键)
     * @return {@link CartoonUpdate}
     */
    public CartoonUpdate queryById(Serializable id)
    {
        CartoonUpdate cartoonUpdate = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            cartoonUpdate = new CartoonUpdate();
            setCartoonUpdate(cursor, cartoonUpdate);
        }
        cursor.close();
        return cartoonUpdate;
    }


    /**
     * 插入一条数据
     *
     * @param cartoonUpdate CartoonUpdate对象
     * @return boolean
     */
    public boolean insert(CartoonUpdate cartoonUpdate)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoonUpdate, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<CartoonUpdate> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (CartoonUpdate cartoonUpdate : list)
            {
                boolean insert = this.insert(cartoonUpdate);
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
     * @param cartoonUpdate CartoonUpdate对象
     * @return boolean
     */
    public boolean update(CartoonUpdate cartoonUpdate)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartoonUpdate, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{cartoonUpdate.getId().toString()});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param cartoonUpdate CartoonUpdate对象
     * @return boolean
     */
    public boolean insertOrUpdate(CartoonUpdate cartoonUpdate)
    {
        boolean insert = insert(cartoonUpdate);
        if (insert)
        {
            return true;
        }
        return update(cartoonUpdate);
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
     * 填充ContentValues
     *
     * @param cartoonUpdate CartoonUpdate
     * @param contentValues ContentValues
     */
    private void setContentValues(CartoonUpdate cartoonUpdate, ContentValues contentValues)
    {
        contentValues.put("id", cartoonUpdate.getId());
        contentValues.put("itemCount", cartoonUpdate.getItemCount());
    }

    /**
     * 填充CartoonUpdate
     *
     * @param cursor        游标
     * @param cartoonUpdate CartoonUpdate对象
     */
    private CartoonUpdate setCartoonUpdate(Cursor cursor, CartoonUpdate cartoonUpdate)
    {
        cartoonUpdate.setId(cursor.getString(0));
        cartoonUpdate.setItemCount(cursor.getInt(1));
        return cartoonUpdate;
    }

}
