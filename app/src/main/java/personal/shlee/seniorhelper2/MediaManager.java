package personal.shlee.seniorhelper2;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Reference :
 *  - Media player :
 *    > https://hello-bryan.tistory.com/83
 *  - Looping sound :
 *    > https://www.tabnine.com/code/java/methods/android.media.MediaPlayer/setLooping
 *  - Button behavior :
 *    > https://stackoverflow.com/questions/34606227/playing-audio-repeat-android-studio
 *  - String resource referencing :
 *    > https://developer.android.com/guide/topics/resources/string-resource
 *  - Play/Stop repeat, need to call 'prepare()' :
 *    > https://stackoverflow.com/questions/12266502/android-mediaplayer-stop-and-play
 */
public class MediaManager {
    private static final String TAG = "SeniorHelper";
    private static MediaPlayer mediaPlayer = null;
    private static AudioManager audioManager = null;

    public static void setSoundVolume(int level) {
        Log.d(TAG, "MediaManager.setSoundVolume : level="+level);
        getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
    }

    public static void startSound() {
        stopSound();
        mediaPlayer = getMediaPlayer();
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    public static int getSystemDefaultSoundVolume() {
        return getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getSystemMaxSoundVolume() {
        return getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private static AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = (AudioManager) MyApp.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        return audioManager;
    }

    private static MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = (MediaPlayer) MediaPlayer.create(MyApp.getContext().getApplicationContext(), R.raw.sound_dingdong);
        }
        return mediaPlayer;
    }
}
