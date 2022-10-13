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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mao.cartoonapp.constant.URLConstant;

public class ContentActivity extends AppCompatActivity
{

    private static final String TAG = "ContentActivity";

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
        if (html == null)
        {
            toastShow("获取不到数据");
            return;
        }
        Log.d(TAG, "onCreate: name:" + name);
        Log.d(TAG, "onCreate: author:" + author);

        WebView webView = findViewById(R.id.WebView);

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
                        String id1 = split[0];
                        String id2 = split[1];
                        Log.d(TAG, "onLoadResource: " + id1 + "," + id2);
                    }
                }
                super.onLoadResource(view, url);
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