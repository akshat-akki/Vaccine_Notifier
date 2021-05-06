package com.example.vaccine_notifier;

import android.app.AlarmManager;
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
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Myservice extends Service
        {
            Calendar c = Calendar.getInstance();


            String centre_id="637",date_para="05-05-2021";
            int age=45;
            public ArrayList<String> centre_name=new ArrayList<String>();
            public ArrayList<String> date=new ArrayList<String>();
            public ArrayList<String> capacity_array=new ArrayList<String>();

            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
              //  Log.d("date",formattedDate);
              // MainActivity mainActivity=new MainActivity();
              age=intent.getIntExtra("age",45);
              centre_id=intent.getStringExtra("disid");
                loadSlots();

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
                final int delay = 60*1000; // 1000 milliseconds == 1 second

                handler.postDelayed(new Runnable() {
                    public void run() {
                         // Do your work here
                        showNotification("Vacinne available","slot 1");
                        handler.postDelayed(this, delay);
                    }
                }, delay);
                return START_STICKY;
            }
            private void loadSlots()
            {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                for(int k=0;k<1;k++)
                {
                    if(k!=0)
                    c.add(Calendar.DATE,7);
                    String formattedDate = df.format(c.getTime());
                    Log.d("date",formattedDate);
                    date_para=formattedDate;
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=" + centre_id + "&date=" + date_para;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("slots","called");
                            try {
                                //getting the whole json object from the response
                                JSONObject obj = response;
                                //we have the array named tutorial inside the object
                                //so here we are getting that json array
                                JSONArray centresArray = obj.getJSONArray("centers");
                                //now looping through all the elements of the json array
                                for (int i = 0; i < centresArray.length(); i++) {
                                    //getting the json object of the particular index inside the array
                                    JSONObject centresObject = centresArray.getJSONObject(i);
                                    String name = (String) centresObject.get("name");
                                    JSONArray sessions_array = centresObject.getJSONArray("sessions");
                                    JSONObject session_obj = sessions_array.getJSONObject(0);
                                    int capacity = session_obj.getInt("available_capacity");
                                    String date_session = session_obj.getString("date");
                                    int min_age = session_obj.getInt("min_age_limit");
                                    Log.i("name",name);
                                 if (capacity > 0 && min_age == age) {
                                        centre_name.add(name);
                                        capacity_array.add(Integer.toString(capacity));
                                        Log.i("name",name);
                                        Log.i("centre date",date_session);
                                        date.add(date_session);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //displaying the error in toast if occur
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }){
                        @Override
                        public Map<String, String> getHeaders(){
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("accept","application/json");
                            headers.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
                            return headers;
                        }
                    };

                    //creating a request queue
                    MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


                    //   Log.d("Centre names", centre_name.toString());

                }
            }
            @Override
            public void onTaskRemoved(Intent rootIntent) {

             /*   Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
                restartServiceTask.setPackage(getPackageName());
                PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                myAlarmService.set(
                        AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + 1000,
                         restartPendingIntent);
*/
                Intent i=new Intent(this,Myservice.class);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                {
                    startForegroundService(i);
                }
                else
                {
                    startService(i);
                }
                super.onTaskRemoved(rootIntent);
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

                stopForeground(false);
                stopSelf();
                super.onDestroy();
            }
        }
