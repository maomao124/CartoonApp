package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mao.cartoonapp.application.MainApplication;
import mao.cartoonapp.constant.OtherConstant;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonHistoryDao;
import mao.cartoonapp.entity.CartoonHistory;

public class ContentActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "ContentActivity";
    private WebView webView;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        if (intent == null)
        {
            toastShow("获取不到数据");
            return;
        }
        Bundle bundle = intent.getExtras();
        String html = bundle.getString("html");
        String name = bundle.getString("name");
        String author = bundle.getString("author");
        String imgUrl = bundle.getString("imgUrl");
        if (html == null)
        {
            toastShow("获取不到数据");
            return;
        }
        Log.d(TAG, "onCreate: name:" + name);
        Log.d(TAG, "onCreate: author:" + author);

        webView = findViewById(R.id.WebView);

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                //Log.d(TAG, "shouldOverrideUrlLoading: 网页url：" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Log.d(TAG, "onPageStarted: 网页url:" + url);
                Pattern pattern = Pattern.compile(URLConstant.baseUrl + "\\d+/\\d+.html");
                Matcher matcher = pattern.matcher(url);
                boolean result = matcher.matches();
                if (!result)
                {
                    toastShow("观看完毕，返回");
                    finish();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                //http://m.qiman57.com/21429/1413472.html
                //Log.d(TAG, "onLoadResource: 网页url：" + url);
                Pattern pattern = Pattern.compile(URLConstant.baseUrl + "\\d+/\\d+.html");
                Matcher matcher = pattern.matcher(url);
                boolean result = matcher.matches();
                if (result)
                {
                    Log.d(TAG, "onLoadResource: 网页url：" + url);
                    String substring = url.substring(URLConstant.baseUrl.length(), url.length() - 5);
                    //System.out.println(substring);
                    String[] split = substring.split("/");
                    if (split.length == 2)
                    {
                        CartoonHistoryDao cartoonHistoryDao = CartoonHistoryDao.getInstance(ContentActivity.this);
                        MainApplication.getInstance().getThreadPool().submit(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    String id1 = split[0];
                                    String id2 = split[1];
                                    Log.d(TAG, "onLoadResource: " + id1 + "," + id2);
                                    CartoonHistory cartoonHistory = new CartoonHistory()
                                            .setId1(id1)
                                            .setId2(id2)
                                            .setName(name)
                                            .setAuthor(author)
                                            .setLastTime(new Date().getTime())
                                            .setImgUrl(imgUrl);
                                    boolean b = cartoonHistoryDao.insertOrUpdate(cartoonHistory);
                                    Log.d(TAG, "run: 历史记录：" + cartoonHistory);
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (!b)
                                            {
                                                toastShow("历史记录更新失败");
                                            }
                                            else
                                            {
                                                Log.d(TAG, "run: 历史记录更新成功");
                                                //toastShow("更新历史记录");
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
                                            toastShow("历史记录更新失败");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                //guide-download
                //xxtop
                //list3_1 similar clearfix mt10
                //read-end
                //comment-box hide
                //comment-input-box

                Log.d(TAG, "onPageFinished: " + url);
                Pattern pattern = Pattern.compile(URLConstant.baseUrl + "\\d+/\\d+.html");
                Matcher matcher = pattern.matcher(url);
                boolean result = matcher.matches();
                if (result)
                {
                    view.loadUrl("javascript:" + OtherConstant.js);
                    view.loadUrl("javascript:hideOther();");
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);//支持缩放
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存

        webView.loadUrl(html);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(1, 1, 1, "刷新");
        menu.add(1, 2, 2, "页面返回");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == 1)
        {
            webView.reload();
        }
        else if (id == 2)
        {
            if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {
                toastShow("页面不能再回退了");
            }
        }
        return super.onOptionsItemSelected(item);
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