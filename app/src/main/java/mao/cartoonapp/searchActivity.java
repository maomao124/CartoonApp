package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import mao.cartoonapp.adapter.CartoonListViewAdapter;
import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.service.CartoonService;

public class searchActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "searchActivity";
    private List<Cartoon> cartoonList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText editText = findViewById(R.id.EditText_search);
        Button button = findViewById(R.id.Button_search);
        ListView listView = findViewById(R.id.ListView);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String keyword = editText.getText().toString();
                if (keyword.length() == 0)
                {
                    toastShow("请输入搜索关键字");
                    return;
                }
                MainApplication.getInstance().getThreadPool().submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            CartoonService cartoonService = MainApplication.getInstance().getCartoonService();
                            cartoonList = cartoonService.search(keyword);
                            Log.d(TAG, "run: 搜索结果：\n" + cartoonList);
                            if (cartoonList.size() == 0)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        toastShow("搜索结果的数量为0");
                                    }
                                });
                                return;
                            }

                            CartoonListViewAdapter cartoonListViewAdapter =
                                    new CartoonListViewAdapter(searchActivity.this, cartoonList);
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    toastShow("搜索到" + cartoonList.size() + "条结果");
                                    listView.setAdapter(cartoonListViewAdapter);
                                    cartoonListViewAdapter.notifyDataSetChanged();
                                }
                            });
                            for (Cartoon cartoon : cartoonList)
                            {
                                Bitmap bitmap = MainApplication.getInstance().loadImage(cartoon);
                                cartoon.setBitmap(bitmap);
                            }
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {

                                    listView.setAdapter(cartoonListViewAdapter);
                                    cartoonListViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "run: ", e);
                            new AlertDialog.Builder(searchActivity.this)
                                    .setTitle("错误")
                                    .setMessage("异常内容：\n" + e)
                                    .setPositiveButton("我知道了", null)
                                    .create()
                                    .show();
                        }
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList.get(position);
                String id1 = cartoon.getId();
                String name = cartoon.getName();
                String author = cartoon.getAuthor();
                String imgUrl = cartoon.getImgUrl();
                Intent intent = new Intent(searchActivity.this, CartoonItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id1);
                bundle.putString("name", name);
                bundle.putString("author", author);
                bundle.putString("imgUrl", imgUrl);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cartoon cartoon = cartoonList.get(position);
                new AlertDialog.Builder(searchActivity.this)
                        .setTitle("提示")
                        .setMessage("是否将漫画”" + cartoon.getName() + "“加入到收藏夹？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                boolean insert = CartoonFavoritesDao.getInstance(searchActivity.this).insert(cartoon);
                                if (insert)
                                {
                                    toastShow("加入成功");
                                }
                                else
                                {
                                    toastShow("加入失败");
                                }
                            }
                        })
                        .setNeutralButton("否", null)
                        .create()
                        .show();
                return true;
            }
        });
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