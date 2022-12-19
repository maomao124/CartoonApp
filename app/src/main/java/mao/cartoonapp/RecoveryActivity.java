package mao.cartoonapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.dao.CartoonUpdateDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonHistory;
import mao.cartoonapp.entity.CartoonUpdate;
import mao.cartoonapp.entity.UserData;
import mao.cartoonapp.utils.UriUtil;

public class RecoveryActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "RecoveryActivity";

    /**
     * 从文件恢复按钮
     */
    private Button recoveryLoadFromFile;
    /**
     * 从文本恢复按钮
     */
    private Button recoveryLoadFromText;
    /**
     * 恢复编辑文本
     */
    private EditText recoveryEditText;

    /**
     * json
     */
    private String json;

    /**
     * 用户数据
     */
    private UserData userData;
    /**
     * 当前文本视图
     */
    private TextView textView_current;
    /**
     * 文本视图总
     */
    private TextView textView_total;

    private CartoonFavoritesDao cartoonFavoritesDao;
    private CartoonHistoryDao cartoonHistoryDao;
    private CartoonUpdateDao cartoonUpdateDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        recoveryLoadFromFile = findViewById(R.id.recovery_loadFromFile);
        recoveryLoadFromText = findViewById(R.id.recovery_loadFromText);
        recoveryEditText = findViewById(R.id.recoveryEditText);

        textView_current = findViewById(R.id.textView_current);
        textView_total = findViewById(R.id.textView_total);

        cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        cartoonFavoritesDao.openReadConnection();
        cartoonFavoritesDao.openWriteConnection();
        cartoonHistoryDao = CartoonHistoryDao.getInstance(this);
        cartoonHistoryDao.openReadConnection();
        cartoonHistoryDao.openWriteConnection();
        cartoonUpdateDao = CartoonUpdateDao.getInstance(this);
        cartoonUpdateDao.openReadConnection();
        cartoonUpdateDao.openWriteConnection();

        recoveryLoadFromText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (recoveryEditText.getText().toString().length() == 0)
                    {
                        toastShow("下方的文本框还没有数据，请先将json数据粘贴到下方的文本框中再点击此按钮");
                        return;
                    }
                    //json
                    json = recoveryEditText.getText().toString();
                    //转对象
                    userData = JSON.parseObject(json, UserData.class);
                    recoveryData(cartoonFavoritesDao, cartoonHistoryDao, cartoonUpdateDao);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "onCreate: ", e);
                    //toastShow(e.getMessage());
                    new AlertDialog.Builder(RecoveryActivity.this)
                            .setTitle("错误")
                            .setMessage("异常内容：\n" + e)
                            .setPositiveButton("我知道了", null)
                            .create()
                            .show();
                    textView_current.setText(String.valueOf(0));
                }
            }
        });

        recoveryLoadFromFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 13347);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 13347)
        {
            if (data.getData() != null)
            {
                FileInputStream fileInputStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                try
                {
                    Uri uri = data.getData();
                    String path = UriUtil.getPath(this, uri);

                    String fileType = path.substring(path.indexOf(".") + 1);
                    if (!fileType.contains("json"))
                    {
                        toastShow("请选择文件后缀为json的文件");
                        //不是想要的json文件
                        return;
                    }
                    toastShow(path);
                    fileInputStream = new FileInputStream(path);
                    inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String str;
                    StringBuilder stringBuilder = new StringBuilder(500);
                    while ((str = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(str).append("\n");
                    }
                    json = stringBuilder.toString();
                    userData = JSON.parseObject(json, UserData.class);
                    recoveryEditText.setText(json);
                    recoveryData(cartoonFavoritesDao, cartoonHistoryDao, cartoonUpdateDao);
                }
                catch (Exception e)
                {
                    Log.e(TAG, "onCreate: ", e);
                    //toastShow(e.getMessage());
                    new AlertDialog.Builder(RecoveryActivity.this)
                            .setTitle("错误")
                            .setMessage("异常内容：\n" + e)
                            .setPositiveButton("我知道了", null)
                            .create()
                            .show();
                    textView_current.setText(String.valueOf(0));
                }
                finally
                {
                    try
                    {
                        if (fileInputStream != null)
                        {
                            fileInputStream.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (inputStreamReader != null)
                        {
                            inputStreamReader.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if (bufferedReader != null)
                        {
                            bufferedReader.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void recoveryData(CartoonFavoritesDao cartoonFavoritesDao, CartoonHistoryDao
            cartoonHistoryDao, CartoonUpdateDao cartoonUpdateDao)
    {
        int total = userData.getCartoonFavoritesData().size() +
                userData.getCartoonHistoryData().size() + userData.getCartoonUpdateData().size();
        textView_current.setText(String.valueOf(0));
        textView_total.setText(String.valueOf(total));
        new AlertDialog.Builder(RecoveryActivity.this)
                .setTitle("提示")
                .setMessage("加载成功，一共" + total + "项数据，是否恢复? 这将会覆盖原来的所有用户数据")
                .setPositiveButton("恢复", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //toastShow("开始恢复");
                        try
                        {
                            CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(RecoveryActivity.this);
                            CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(RecoveryActivity.this);
                            CartoonUpdateDao cartoonUpdateDao = CartoonUpdateDao.getInstance(RecoveryActivity.this);

                            cartoonFavoritesDao.openWriteConnection().beginTransaction();
                            cartoonHistoryDao.openWriteConnection().beginTransaction();
                            cartoonUpdateDao.openWriteConnection().beginTransaction();

                            cartoonFavoritesDao.deleteAll();
                            cartoonHistoryDao.deleteAll();
                            cartoonUpdateDao.deleteAll();
                            int current = 0;
                            List<Cartoon> cartoonFavoritesData = userData.getCartoonFavoritesData();
                            List<CartoonHistory> cartoonHistoryData = userData.getCartoonHistoryData();
                            List<CartoonUpdate> cartoonUpdateData = userData.getCartoonUpdateData();
                            for (Cartoon cartoon : cartoonFavoritesData)
                            {
                                cartoonFavoritesDao.insert(cartoon);
                                current++;
                                textView_current.setText(String.valueOf(current));
                            }
                            for (CartoonHistory cartoonHistory : cartoonHistoryData)
                            {
                                cartoonHistoryDao.insert(cartoonHistory);
                                current++;
                                textView_current.setText(String.valueOf(current));
                            }
                            for (CartoonUpdate cartoonUpdate : cartoonUpdateData)
                            {
                                cartoonUpdateDao.insert(cartoonUpdate);
                                current++;
                                textView_current.setText(String.valueOf(current));
                            }
                            cartoonFavoritesDao.openWriteConnection().setTransactionSuccessful();
                            cartoonHistoryDao.openWriteConnection().setTransactionSuccessful();
                            cartoonUpdateDao.openWriteConnection().setTransactionSuccessful();
                            toastShow("恢复成功");
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "onCreate: ", e);
                            //toastShow(e.getMessage());
                            new AlertDialog.Builder(RecoveryActivity.this)
                                    .setTitle("错误")
                                    .setMessage("异常内容：\n" + e)
                                    .setPositiveButton("我知道了", null)
                                    .create()
                                    .show();

                        }
                        finally
                        {
                            cartoonFavoritesDao.openWriteConnection().endTransaction();
                            cartoonHistoryDao.openWriteConnection().endTransaction();
                            cartoonUpdateDao.openWriteConnection().endTransaction();
                        }

                    }
                })
                .setNeutralButton("取消", null)
                .create()
                .show();
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