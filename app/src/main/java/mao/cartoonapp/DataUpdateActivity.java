package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;

import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.dao.CartoonUpdateDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonHistory;
import mao.cartoonapp.entity.ImageLoadResult;

public class DataUpdateActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "DataUpdateActivity";

    /**
     * 当前文本视图
     */
    private TextView textView_current;

    /**
     * 文本视图总
     */
    private TextView textView_total;

    /**
     * 文本视图
     */
    private TextView textView;

    /**
     * 按钮
     */
    private Button button;

    private CartoonFavoritesDao cartoonFavoritesDao;
    private CartoonHistoryDao cartoonHistoryDao;

    /**
     * 动画列表
     */
    private List<Cartoon> cartoonList;

    /**
     * 卡通历史列表
     */
    private List<CartoonHistory> cartoonHistoryList;

    /**
     * 当前
     */
    private int current = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);

        textView = findViewById(R.id.text);
        textView_current = findViewById(R.id.textView_current);
        textView_total = findViewById(R.id.textView_total);
        button = findViewById(R.id.load);

        button.setEnabled(false);

        cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        cartoonFavoritesDao.openReadConnection();
        cartoonFavoritesDao.openWriteConnection();
        cartoonHistoryDao = CartoonHistoryDao.getInstance(this);
        cartoonHistoryDao.openReadConnection();
        cartoonHistoryDao.openWriteConnection();


        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    cartoonList = cartoonFavoritesDao.queryAll();
                    cartoonHistoryList = cartoonHistoryDao.queryAll();
                    int total = cartoonList.size() + cartoonHistoryList.size();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            textView_total.setText(String.valueOf(total));
                            button.setEnabled(true);
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
                            Log.e(TAG, "onCreate: ", e);
                            //toastShow(e.getMessage());
                            new AlertDialog.Builder(DataUpdateActivity.this)
                                    .setTitle("错误")
                                    .setMessage("异常内容：\n" + e)
                                    .setPositiveButton("我知道了", null)
                                    .create()
                                    .show();
                        }
                    });
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int[] errorCount = {0};
                current = 0;
                button.setEnabled(false);
                MainApplication.getInstance().getThreadPool().submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            for (Cartoon cartoon : cartoonList)
                            {
                                boolean isSuccess = updateCartoonFavorites(cartoon);
                                if (!isSuccess)
                                {
                                    errorCount[0]++;
                                }
                            }
                            for (CartoonHistory cartoonHistory : cartoonHistoryList)
                            {
                                boolean isSuccess = updateCartoonHistory(cartoonHistory);
                                if (!isSuccess)
                                {
                                    errorCount[0]++;
                                }
                            }
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    button.setEnabled(true);
                                    textView.setText("");
                                    if (errorCount[0] == 0)
                                    {

                                        //没有错误
                                        int total = cartoonList.size() + cartoonHistoryList.size();
                                        toastShow(total + "项数据全部更新完成！");
                                    }
                                    else
                                    {
                                        new AlertDialog.Builder(DataUpdateActivity.this)
                                                .setTitle("提示")
                                                .setMessage(errorCount[0] + "项数据更新失败！")
                                                .setPositiveButton("我知道了", null)
                                                .create()
                                                .show();
                                    }
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
                                    button.setEnabled(true);
                                    Log.e(TAG, "onCreate: ", e);
                                    //toastShow(e.getMessage());
                                    new AlertDialog.Builder(DataUpdateActivity.this)
                                            .setTitle("错误")
                                            .setMessage("异常内容：\n" + e)
                                            .setPositiveButton("我知道了", null)
                                            .create()
                                            .show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    /**
     * 更新漫画收藏
     *
     * @param cartoon 卡通
     * @return boolean
     */
    private boolean updateCartoonFavorites(Cartoon cartoon)
    {
        try
        {
            Cartoon cartoon1 = MainApplication.getInstance().getCartoonService().getCartoonById(cartoon.getId());
            if (cartoon1 == null)
            {
                return false;
            }
            cartoon.setName(cartoon1.getName());
            cartoon.setAuthor(cartoon1.getAuthor());
            cartoon.setImgUrl(cartoon1.getImgUrl());
            String imgPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + cartoon.getId() + ".jpg";
            ImageLoadResult imageLoadResult = MainApplication.getInstance().openImageByHTTP(cartoon);
            if (imageLoadResult.isStatus())
            {
                //从网络上成功加载，保存到本地
                MainApplication.getInstance().saveImage(imgPath, imageLoadResult.getBitmap());
            }
            //保存到数据库
            cartoonFavoritesDao.update(cartoon);
            String jsonString = JSON.toJSONString(cartoon, true);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    textView_current.setText(String.valueOf((current + 1)));
                    textView.setText(jsonString);
                }
            });
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: ", e);
            return false;
        }
        finally
        {
            current++;
        }
    }

    /**
     * 更新漫画历史
     *
     * @param cartoonHistory 卡通历史
     * @return boolean
     */
    private boolean updateCartoonHistory(CartoonHistory cartoonHistory)
    {
        try
        {
            Cartoon cartoon = MainApplication.getInstance().getCartoonService().getCartoonById(cartoonHistory.getId1());
            if (cartoon == null)
            {
                return false;
            }
            cartoonHistory.setName(cartoon.getName());
            cartoonHistory.setAuthor(cartoon.getAuthor());
            cartoonHistory.setImgUrl(cartoon.getImgUrl());
            String imgPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + cartoon.getId() + ".jpg";
            ImageLoadResult imageLoadResult = MainApplication.getInstance().openImageByHTTP(cartoon);
            if (imageLoadResult.isStatus())
            {
                //从网络上成功加载，保存到本地
                MainApplication.getInstance().saveImage(imgPath, imageLoadResult.getBitmap());
            }
            //保存到数据库
            cartoonHistoryDao.update(cartoonHistory);
            String jsonString = JSON.toJSONString(cartoonHistory, true);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    textView_current.setText(String.valueOf((current + 1)));
                    textView.setText(jsonString);
                }
            });
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: ", e);
            return false;
        }
        finally
        {
            current++;
        }
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