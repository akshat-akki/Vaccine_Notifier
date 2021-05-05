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

import java.util.ArrayList;

public class Myservice extends Service
        {
            String centre_id="637",date_para="05-05-2021";
            public ArrayList<String> centre_name;
            public ArrayList<String> date;

            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
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
            public void loadSlots()
            {

                RequestQueue queue = Volley.newRequestQueue(this);
                String url ="https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+centre_id+"&date="+date_para;
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


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
                                        String name=(String)centresObject.get("name");
                                        //creating a tutorial object and giving them the values from json object
                                        Log.i("name",name);
                                        //adding the tutorial to tutoriallist

                                    }

                                    //creating custom adapter object


                                    //adding the adapter to listview


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
                        });

                //creating a request queue
                RequestQueue requestQueue = Volley.newRequestQueue(this);

                //adding the string request to request queue
                requestQueue.add(stringRequest);
/*               JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                if(response==null)
//                                {
//                                    Log.d("error","null");
//                                    return;
//                                }
//                                else
//                                {
//                                    JSONObject obj = null;
//                                    try {
//                                        obj = response;
//                                        Log.i("response",obj.toString());
//                                        String name= (String) obj.get("name");
//                                        Log.d("name",name);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//
//                                }
                             /*   for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject obj = response.getJSONObject(i);
                                        String name= (String) obj.get("name");
                                        JSONArray session=new JSONArray(obj.get("sessions"));
                                        JSONObject session_obj=session.getJSONObject(0);
                                        int capacity=session_obj.getInt("available_capacity");
                                        String date_session=session_obj.getString("date");
                                        int min_age=session_obj.getInt("min_age_limit");
                                        if(capacity>0 && min_age>18)
                                        {
                                            centre_name.add(name);
                                            date.add(date_session);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }*/
                            }
//                        }, new Response.ErrorListener() {
//
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                //
//                                    error.printStackTrace();
//                            }
//                        });
               // queue.add(jsonObjectRequest);
            /*    for(int i=0;i<centre_name.size();i++)
                {
                    Log.d("Centre names",centre_name.get(i));
                }*/
           // }
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
