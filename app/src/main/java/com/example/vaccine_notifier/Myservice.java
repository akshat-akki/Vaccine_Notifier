package com.example.vaccine_notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Myservice extends Service
        {
            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
                createNotificationChannel();
                Intent intent1=new Intent(this,MainActivity.class);
                PendingIntent p=PendingIntent.getActivity(this,0 ,intent1,0);
                Notification notification=new NotificationCompat.Builder(this,"channel1")
                        .setContentTitle("Vaccine_Notifier")
                        .setContentText("Constantly checking for vacant slots!!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(p).build();
                startForeground(1,notification);
                final Handler handler = new Handler();
                final int delay = 10000; // 1000 milliseconds == 1 second

                handler.postDelayed(new Runnable() {
                    public void run() {
                         // Do your work here
                        showNotification("Vacinne available","slot 1");
                        handler.postDelayed(this, delay);
                    }
                }, delay);
                return START_STICKY;
            }

            private void createNotificationChannel() {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                {
                    NotificationChannel notificationChannel=new NotificationChannel("channel1","foregroundservice", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager=getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(notificationChannel);
                }
            }
            private void showNotification(String task, String desc) {
                Intent intent1=new Intent(this,MainActivity.class);
                PendingIntent p=PendingIntent.getActivity(this,0 ,intent1,0);

                NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                String channelId = "task_channel";
                String channelName = "task_name";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new
                            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setContentTitle(task)
                        .setContentText(desc)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(p);
                manager.notify(1, builder.build());
            }

            @Nullable
            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }

            @Override
            public void onDestroy() {
                stopForeground(true);
                stopSelf();
                super.onDestroy();
            }
        }
