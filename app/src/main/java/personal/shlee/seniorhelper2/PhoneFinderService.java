package personal.shlee.seniorhelper2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/**
 * Why I need to create a foreground service?
 * I tried many times and many ways to play vibrator in the context of broadcast service,
 * it's working find if the application in foreground,
 * but all failed when the application is in background or not being launched.
 * To resolve this problem, I googled many times, but I can not find out how I can resolve it.
 * It seems that it's because there are some limitations when the application is in background.
 * So, I decided to use foreground service to play vibrator by the result of google search.
 *
 * Reference :
 *  - Foreground service
 *    > https://androidwave.com/foreground-service-android-example/
 *  - How to process commands or events in foreground service
 *    > https://jizard.tistory.com/217
 *    > https://jizard.tistory.com/216
 */
public class PhoneFinderService extends Service {
    private static final String TAG = "SeniorHelper";

    public PhoneFinderService() {
    }

    public static final String CHANNEL_ID = "SmsServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SeniorHelper Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                /**
                 * Reference : background color
                 *  - https://cazimirroman.medium.com/android-how-to-set-the-background-color-for-a-notification-in-a-foreground-service-eaa505e2b82d
                 */
                .setColor(getResources().getColor(R.color.colorNotificationBackground))
                .setColorized(true)
                //.setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        String action = intent.getAction();
        Log.d(TAG, "PhoneFinderService.onStartCommand(), action="+action);
        if (action.equalsIgnoreCase("PLAY")) {
            vibratorStart();
        } else if (action.equalsIgnoreCase("STOP")) {
            vibratorStop();
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    /**
     * Vibrator start / stop
     *
     * Reference :
     *  - https://parkho79.tistory.com/122
     */
    private static void vibratorStart() {
        Vibrator vibrator = (Vibrator) MyApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.cancel();
        // 0.5초 대기 -> 1초 진동 -> 0.5초 대기 -> 1초 진동
        final long[] vibratePattern = new long[]{1000, 1000, 1000, 1000};
        // 반복 없음
        final int repeat = 0;
        Log.d(TAG, "PhoneFinderService.vibratorStart : Vibrator started!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "PhoneFinderService.vibratorStart : vibrate with new api");
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, repeat));
        } else {
            Log.d(TAG, "PhoneFinderService.vibratorStart : vibrate with old api");
            Log.d(TAG, "PhoneFinderService.vibratorStart : Vibrator started!");
            vibrator.vibrate(vibratePattern, repeat);
        }
    }
    private static void vibratorStop() {
        Log.d(TAG, "PhoneFinderService.vibratorStop : Vibrator stopped!");
        Vibrator vibrator = (Vibrator) MyApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
        vibrator = null;
    }
    public static void startPhoneFinderService(String action, String message) {
        Intent serviceIntent = new Intent(MyApp.getContext(), PhoneFinderService.class);
        serviceIntent.putExtra("inputExtra", message);
        serviceIntent.setAction(action);
        ContextCompat.startForegroundService(MyApp.getContext(), serviceIntent);
        Log.d(TAG, "PhoneFinderService.startPhoneFinderService : " + action + ", " + message);
    }
}