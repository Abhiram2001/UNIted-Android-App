package com.example.united.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.united.ChatActivity;
import com.example.united.FindFriendActivity;
import com.example.united.FriendActivity;
import com.example.united.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    UserStatus userStatus = new UserStatus();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()>0){
            Map<String,String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("message");
            String hisId = map.get("hisId");
            String hisImage = map.get("hisImage");
            String chatId = map.get("chatId");

            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                createOreoNotification(title,message,hisId,hisImage,chatId);
            }
            else {
                createNormalNotification(title,message,hisId,hisImage,chatId);
            }
        }




    }

    @Override
    public void onNewToken(@NonNull String s) {
        if(userStatus.tokenbefore.startsWith("$")){
            userStatus.tokenbefore = "$"+s;
        }
        else{
            updateToken(s);
        }
        super.onNewToken(s);
    }

    public void updateToken(String token)
    {
        Log.d("tag",userStatus.getUid());
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userStatus.getUid());
        databaseReference.child("Token").setValue(token);
    }

    public void createNormalNotification(String title, String message, String hisId, String hisImage, String chatId)
    {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1000");
        builder.setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setColor(ResourcesCompat.getColor(getResources(),R.color.purple_500,null))
                .setSound(uri);

        PendingIntent pendingIntent;
        if(hisImage.equals("friends")){
            Intent intent = new Intent(this, FindFriendActivity.class);
            intent.putExtra("chatId",chatId);
            intent.putExtra("OtherUserID",hisId);
            intent.putExtra("hisImage",hisImage);
            pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        }
        else if(hisImage.equals("friend request"))
        {
            Intent intent = new Intent(this, FriendActivity.class);
            intent.putExtra("chatId",chatId);
            intent.putExtra("OtherUserID",hisId);
            intent.putExtra("hisImage",hisImage);
            pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        }
        else{
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatId",chatId);
            intent.putExtra("OtherUserID",hisId);
            intent.putExtra("hisImage",hisImage);
            pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        }


        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85-65),builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createOreoNotification(String title, String message, String hisId, String hisImage, String chatId)
    {
        NotificationChannel channel =new NotificationChannel("1000", "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Message Description");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this,FriendActivity.class);
        intent.putExtra("OtherUserID",hisId);
        intent.putExtra("hisImage",hisImage);
        intent.putExtra("chatId",chatId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(this,"1000")
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.purple_700,null))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        manager.notify(new Random().nextInt(85-65),notification);
    }

}
