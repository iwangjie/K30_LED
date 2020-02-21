package com.wangjie.k30;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.DataOutputStream;

public class MainActivity extends Activity {


    Process p = null;
    DataOutputStream dos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            p = Runtime.getRuntime().exec("su");
//            dos = new DataOutputStream(p.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//        }
//
//        //Bug按钮监听
//        Button bugBtn = findViewById(R.id.bug_btn);
//        bugBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Thread.sleep(200);
//                    dos.writeBytes("echo 0 > /sys/class/leds/white/brightness" + "\n");
//                    dos.flush();
//                    Toast.makeText(getApplicationContext(), "程序退出....", Toast.LENGTH_LONG);
//                    Thread.sleep(200);
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


        // 通知授权
        if (!isEnabledNotification()) {
            Toast toast = Toast.makeText(getApplicationContext(), "请授权通知读取权限", Toast.LENGTH_LONG);
            toast.show();
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "已授予通知权限", Toast.LENGTH_SHORT);
            toast.show();
        }


        // 开关按钮
        final Switch service_status = findViewById(R.id.service_status);

        service_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                NotificationMonitorService.serviceStatus = b;
            }
        });


        // 注册广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_PRESENT");

        registerReceiver(new Receiver_1(),intentFilter);

        //频率进度条

//        SeekBar frequencySeekBar = findViewById(R.id.frequency_seekBar);
//        final TextView frequencyText = findViewById(R.id.frequency_text);
//        frequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                frequencyText.setText("闪烁频率(" + i * 5 + ")");
//                NotificationMonitorService.frequencyValue = i * 5;
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

    }


    /**
     * 判断是否打开了通知监听权限
     *
     * @return
     */
    private boolean isEnabledNotification() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;


    }


    class Receiver_1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "网络发生变化", Toast.LENGTH_SHORT).show();
            NotificationMonitorService notificationMonitorService = new NotificationMonitorService();
            notificationMonitorService.scintillationLed();
        }


    }

}
