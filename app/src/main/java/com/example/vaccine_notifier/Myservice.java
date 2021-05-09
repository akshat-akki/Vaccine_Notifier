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
            Runnable run;
            Handler handler;
            int count=0;
            int precount=0;
            String centre_id="637",date_para="05-05-2021";
            int age=45;
            public static ArrayList<String> centre_name=new ArrayList<String>();
            public static ArrayList<String> date=new ArrayList<String>();
            public static ArrayList<String> capacity_array=new ArrayList<String>();
            int noti=0;
            String districtnew="";
            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
                precount=0;
               int stop=intent.getIntExtra("stop",0);
                if(stop==1) {
                    noti=0;
                    handler.removeCallbacksAndMessages(run);
                   stopForeground(true);
                   stopSelfResult(startId);

               }
                else {
                    //  Log.d("date",formattedDate);
                    // MainActivity mainActivity=new MainActivity();
                    age = intent.getIntExtra("age", 45);
                    //  Toast.makeText(this,Integer.toString(age), Toast.LENGTH_SHORT).show();
                    //  age=18;
                    centre_id = intent.getStringExtra("disid");
                    noti = intent.getIntExtra("notif", 1);
                    //  Toast.makeText(this, Integer.toString(noti), Toast.LENGTH_LONG).show();
                    date.clear();
                    centre_name.clear();
                    capacity_array.clear();
                    loadSlots();
                    precount = count;

                    createNotificationChannel();
                    Intent intent1 = new Intent(this, MainActivity.class);
                    PendingIntent p = PendingIntent.getActivity(this, 0, intent1, 0);
                    if (noti == 1) {
                        Notification notification = new NotificationCompat.Builder(this, "channel1")
                                .setContentTitle("Vaccine_Notifier")
                                .setContentText("Constantly checking for vacant slots!!")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(p).build();
                        startForeground(1, notification);
                    } else {
                        Notification notification = new NotificationCompat.Builder(this, "channel1")
                                    .setContentTitle("Vaccine_Notifier")
                                    .setContentText("Tap to check for vacant slots!!")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(p).build();
                            startForeground(1, notification);
                        }
                    if (noti == 1) {
                    handler = new Handler();
                    final int delay = 10000; // 1000 milliseconds == 1 second

                        handler.postDelayed(run =new Runnable() {
                            public void run() {
                                Log.v("entered","entry");
                                date.clear();
                                centre_name.clear();
                                capacity_array.clear();
                                if(noti==1) {

                                    loadSlots();
                                    if (precount < count) {
                                        showNotification("VACCINE NOTIFIER", "NEW SLOTS AVAILABLE!! TAP TO CHECK NOW");
                                    }
                                }
                                handler.postDelayed(this, delay);
                            }
                        }, delay);
//                Intent i=new Intent(getApplicationContext(),Results.class);
//                i.putExtra("centreName",centre_name);
//                i.putExtra("date",date);
//                i.putExtra("availability",capacity_array);
//                startActivity(i);
                    }
                }
                return START_STICKY;
            }
            private void loadSlots()
            {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c.getTime());
                    Log.d("date",formattedDate);
                    date_para=formattedDate;
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=" + centre_id + "&date=" + date_para;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("slots","called");
                            Log.v("slots",centre_id);
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
                                   // Log.i("name",name);
                                 if (capacity > 0 && min_age == age)
                                 {
                                        centre_name.add(name);
                                        capacity_array.add(Integer.toString(capacity));
                                        Log.i("centre name",name);
                                        Log.i("centre date",date_session);
                                        date.add(date_session);
                                 }
                                }
                                count =centre_name.size();

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
