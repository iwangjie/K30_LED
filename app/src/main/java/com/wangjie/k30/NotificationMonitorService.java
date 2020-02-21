package com.wangjie.k30;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;


public class NotificationMonitorService extends NotificationListenerService {

    //服务总开关
    public static boolean serviceStatus = true;

    private static Process p = null;

    private static DataOutputStream dos = null;

    // 屏幕开启状态 0.关闭 1.开启 2.解锁
    private int screenStatus = 0;

    private boolean  openStatus = false;

    // 闪烁频率
    public static int frequencyValue = 200;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            // 屏幕监听器
            new ScreenListener(this).begin(new ScreenListener.ScreenStateListener() {
                @Override
                public void onScreenOn() {
                    //屏幕打开
                    screenStatus = 1;
                }

                @Override
                public void onScreenOff() {
                    // 屏幕关闭
                    screenStatus = 0;

                }

                @Override
                public void onUserPresent() {
                    //解锁手机
                    screenStatus = 2;
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "您没有ROOT权限，或者没有授予root权限，程序奔溃...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        // 阻止重复创建线程
        if (!openStatus) {
            openStatus = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!serviceStatus) {
                        return;
                    }
                    // 屏幕关闭执行持续闪烁
                    if (screenStatus == 0) {
                        while (screenStatus == 0) {
                            scintillationLed();
                        }
                    }
                    // 屏幕开启或者解锁执行一次闪烁
                    else if (screenStatus == 1 || screenStatus == 2) {
                        scintillationLed();
                    } else {

                    }

                    openStatus = false;
                }
            }).start();
        }

    }


    /**
     * 闪烁led
     */
    public synchronized void scintillationLed() {
        try {
            for (int i = 4; i >= 0; i -= 1) {
                String cmd = "echo " + i + " > /sys/class/leds/white/brightness";
                dos.writeBytes(cmd + "\n");
                dos.flush();
                Thread.sleep(frequencyValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        p.destroy();
    }

}
