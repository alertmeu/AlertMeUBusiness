package in.alertmeu.a4b.FirebaseNotification;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.activity.BusinessProfileSettingActivity;
import in.alertmeu.a4b.activity.HomePageActivity;
import in.alertmeu.a4b.activity.LSelectActivity;
import in.alertmeu.a4b.activity.LSplashScreenActivity;
import in.alertmeu.a4b.activity.ShowNotificationActivity;
import in.alertmeu.a4b.activity.SplashActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Intent intent = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            if (title.equals("Users Alert")) {
                // intent = new Intent(getApplicationContext(), MapsActivity.class);
            } else if (title.equals("Expiring Advertisement")) {
                intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                intent.putExtra("active", 0);
            } else if (title.equals("Your ad has expired")) {
                intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                intent.putExtra("active", 1);
            } else if (title.equals("Your ad was viewed")) {
                intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                intent.putExtra("active", 0);
            } else if (title.equals("Inactivity Notice")) {
                if (message.contains("You have not advertised for several days. Please advertise to attract customers")) {
                    intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                    intent.putExtra("active", 2);
                } else {
                    intent = new Intent(getApplicationContext(), BusinessProfileSettingActivity.class);
                    //intent.putExtra("active", 2);
                }
            } else {
                intent = new Intent(getApplicationContext(), HomePageActivity.class);
            }

            //if there is no image
            if (imageUrl.equals("null")) {
                //displaying small notification
                if (title.equals("Your ad has expired")) {

                    /****** Create Thread that will sleep for 5 seconds****/
                    Thread background = new Thread() {
                        public void run() {
                            try {
                                // Thread will sleep for 5 seconds
                                sleep(60 * 1000);
                                mNotificationManager.showSmallNotification(title, message, intent);
                            } catch (Exception e) {
                            }
                        }
                    };
                    // start thread
                    background.start();
                } else {
                    mNotificationManager.showSmallNotification(title, message, intent);
                }
            } else {
                //if there is an image
                //displaying a big notification
                //intent = new Intent(getApplicationContext(), ShowNotificationActivity.class);
               // intent.putExtra("url", imageUrl);
                Uri uri = Uri.parse(imageUrl); // missing 'http://' will cause crashed
                intent = new Intent(Intent.ACTION_VIEW, uri);
                mNotificationManager.showSmallNotification(title, message, intent);
                //mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}