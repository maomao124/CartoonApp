package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.dao.CartoonUpdateDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonHistory;
import mao.cartoonapp.entity.CartoonUpdate;
import mao.cartoonapp.entity.UserData;

public class BackupActivity extends AppCompatActivity
{

    /**
     * 备份编辑文本
     */
    private EditText backupEditText;

    /**
     * 标签
     */
    private static final String TAG = "BackupActivity";
    /**
     * 剪贴板
     */
    private Button clipBoard;

    /**
     * 保存
     */
    private Button save;

    /**
     * 刷新
     */
    private Button refresh;

    /**
     * 加载
     */
    private LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        backupEditText = findViewById(R.id.backupEditText);
        refresh = findViewById(R.id.backup_refresh);
        clipBoard = findViewById(R.id.backup_clipBoard);
        save = findViewById(R.id.backup_save);
        loading = findViewById(R.id.loading);

        CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        cartoonFavoritesDao.openReadConnection();
        cartoonFavoritesDao.openWriteConnection();
        CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(this);
        cartoonHistoryDao.openReadConnection();
        cartoonHistoryDao.openWriteConnection();
        CartoonUpdateDao cartoonUpdateDao = CartoonUpdateDao.getInstance(this);
        cartoonUpdateDao.openReadConnection();
        cartoonUpdateDao.openWriteConnection();

        clipBoard.setOnClickListener(null);
        save.setOnClickListener(null);
        loading.setVisibility(View.VISIBLE);
        backupEditText.setVisibility(View.GONE);

        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String json = load(cartoonFavoritesDao, cartoonHistoryDao, cartoonUpdateDao);

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            backupEditText.setText(json);
                            loading.setVisibility(View.GONE);
                            backupEditText.setVisibility(View.VISIBLE);

                            clipBoard.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboardManager.setText(backupEditText.getText());
                                    toastShow("已复制到剪切板");
                                }
                            });
                            save.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    saveToFile();

                                }
                            });
                        }
                    });
                }
                catch (Exception e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            clipBoard.setOnClickListener(null);
                            save.setOnClickListener(null);
                            loading.setVisibility(View.GONE);
                            backupEditText.setVisibility(View.VISIBLE);
                            Log.e(TAG, "onCreate: ", e);
                            //toastShow(e.getMessage());
                            new AlertDialog.Builder(BackupActivity.this)
                                    .setTitle("错误")
                                    .setMessage("异常内容：\n" + e)
                                    .setPositiveButton("我知道了", null)
                                    .create()
                                    .show();
                        }
                    });
                }
            }
        };
        MainApplication.getInstance().getThreadPool().submit(task);

        refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clipBoard.setOnClickListener(null);
                save.setOnClickListener(null);
                loading.setVisibility(View.VISIBLE);
                backupEditText.setVisibility(View.GONE);
                MainApplication.getInstance().getThreadPool().submit(task);
            }
        });
    }

    /**
     * 保存到文件
     */
    private void saveToFile()
    {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try
        {
            //根路径
            String s = Environment.getExternalStorageDirectory().toString() + "/cartoonApp/";
            File dir = new File(s);
            if (!dir.exists())
            {
                //不存在就创建
                dir.mkdir();
            }
            //构建文件
            String fileName = s + "backup_" + new Date().getTime() + ".json";
            File file = new File(fileName);
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(backupEditText.getText().toString());
            outputStreamWriter.flush();
            toastShow("保存完成！文件路径：" + fileName);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: ", e);
            //toastShow(e.getMessage());
            new AlertDialog.Builder(BackupActivity.this)
                    .setTitle("错误")
                    .setMessage("异常内容：\n" + e)
                    .setPositiveButton("我知道了", null)
                    .create()
                    .show();
        }
        finally
        {
            try
            {
                if (fileOutputStream != null)
                {
                    fileOutputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                if (outputStreamWriter != null)
                {
                    outputStreamWriter.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载
     *
     * @param cartoonFavoritesDao CartoonFavoritesDao
     * @param cartoonHistoryDao   CartoonHistoryDao
     * @param cartoonUpdateDao    CartoonUpdateDao
     * @return {@link String}
     */
    private String load(CartoonFavoritesDao cartoonFavoritesDao, CartoonHistoryDao cartoonHistoryDao, CartoonUpdateDao cartoonUpdateDao)
    {
        List<Cartoon> CartoonFavoritesList = cartoonFavoritesDao.queryAll();
        List<CartoonHistory> cartoonHistoryList = cartoonHistoryDao.queryAll();
        List<CartoonUpdate> cartoonUpdateList = cartoonUpdateDao.queryAll();
        UserData userData = new UserData();
        userData.setCartoonFavoritesData(CartoonFavoritesList);
        userData.setCartoonHistoryData(cartoonHistoryList);
        userData.setCartoonUpdateData(cartoonUpdateList);
        return userData.toJson();
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}