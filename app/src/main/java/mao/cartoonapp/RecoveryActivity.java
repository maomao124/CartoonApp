package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import mao.cartoonapp.entity.UserData;

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

    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        recoveryLoadFromFile = findViewById(R.id.recovery_loadFromFile);
        recoveryLoadFromText = findViewById(R.id.recovery_loadFromText);
        recoveryEditText = findViewById(R.id.recoveryEditText);

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
                    int total = userData.getCartoonFavoritesData().size() +
                            userData.getCartoonHistoryData().size() + userData.getCartoonUpdateData().size();
                    new AlertDialog.Builder(RecoveryActivity.this)
                            .setTitle("提示")
                            .setMessage("加载成功，一共" + total + "项数据，是否恢复")
                            .setPositiveButton("恢复", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();
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