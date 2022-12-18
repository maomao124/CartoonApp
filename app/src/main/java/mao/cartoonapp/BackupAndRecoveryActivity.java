package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BackupAndRecoveryActivity extends AppCompatActivity
{
    /**
     * 备份按钮
     */
    private Button backupButton;

    /**
     * 恢复按钮
     */
    private Button recoveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_and_recovery);

        backupButton = findViewById(R.id.backupButton);
        recoveryButton = findViewById(R.id.recoveryButton);
    }
}