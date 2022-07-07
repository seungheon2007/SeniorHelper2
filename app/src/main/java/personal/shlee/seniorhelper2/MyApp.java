package personal.shlee.seniorhelper2;

import android.app.Application;
import android.content.Context;

/**
 * This class is created to hold 'application's context' and to be used later
 *
 * Reference :
 *  - https://stackoverflow.com/questions/9445661/how-to-get-the-context-from-anywhere
*/

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    /**
     * The following methods and a variable are added to trace application status
     *
     * Reference :
     *  - https://stackoverflow.com/questions/3667022/checking-if-an-android-application-is-running-in-the-background/5862048#5862048
     */
    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResumed() {
        activityVisible = true;
    }
    public static void activityPaused() {
        activityVisible = false;
    }
    private static boolean activityVisible;
}
