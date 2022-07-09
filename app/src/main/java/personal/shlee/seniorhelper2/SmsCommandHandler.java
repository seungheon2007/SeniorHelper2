package personal.shlee.seniorhelper2;

import static personal.shlee.seniorhelper2.PhoneFinderService.startPhoneFinderService;

import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

/**
 * SMS message handler will react for 2 commands. One is 'Play', the other is 'Reset'.
 * SMS text message '0000' will act as 'Play' command.
 * When 'Play' command come, it will make 'Status' to the next level.
 * I plan to make 5 levels of status like below.
 *   - LEVEL_00, // ready
 *   - LEVEL_01, // level 01 : vibrator on with default intensity repeatedly
 *   - LEVEL_02, // level 02 : media player play sound with volume 1/3 (low volume)
 *   - LEVEL_03, // level 03 : media player play sound with volume 2/3 (medium volume)
 *   - LEVEL_04, // level 04 : media player play sound with volume 3/3 (max volume)
 * For example, it's first 'Status' will be 'LEVEL_00', and it means 'ready' status.
 * In 'ready' status, no actions will be made by this handler.
 * But, if '0000' text message comes, it's status will be increased to the next level 'LEVEL_01'.
 * 'LEVEL_01' means 'vibrator' will be started with default intensity repeatedly.
 * If next '0000' comes, it's status will go to next level 'LEVEL_02', and it will start sound with
 * volume level 1/3 of max volume, and so on...
 * In any status, it'll be reset if 'Reset' command comes.
 * SMS text message 'xxxx' will act as 'Reset' command. When reset command comes, status will go to
 * 'LEVEL_00', which means that vibrator will be off and media sound will be stopped as well.
 */
public class SmsCommandHandler {
    private static final String TAG = "SeniorHelper";

    private static Status status = Status.LEVEL_00;
    private static boolean isInitialized = false;
    private static int SOUND_VOLUME_SYSTEM_DEFAULT;
    private static int SOUND_VOLUME_MAX;

    /**
     * 'Commands' enum is defined for matching enum number with String command
     *
     * Reference : enum with string matching
     *  - https://www.delftstack.com/ko/howto/java/enum-to-string-java/
    */
    enum Commands {
        PLAY("0000"),
        RESET("9999");
        private final String getCommandCode;
        Commands(String commandCode) {
            getCommandCode = commandCode;
        }
        @Override
        public String toString() {
            return getCommandCode;
        }
    }

    private enum Status {
        LEVEL_00, // ready
        LEVEL_01, // level 01 : vibrator on with default intensity repeatedly
        LEVEL_02, // level 02 : media player play sound with volume 1/3 (low volume)
        LEVEL_03, // level 03 : media player play sound with volume 2/3 (medium volume)
        LEVEL_04, // level 04 : media player play sound with volume 3/3 (max volume)
    }

    private static void init() {
        if (!isInitialized) {
            // keep system volume values to be used for getting it back to original
            SOUND_VOLUME_SYSTEM_DEFAULT = MediaManager.getSystemDefaultSoundVolume();
            SOUND_VOLUME_MAX = MediaManager.getSystemMaxSoundVolume();

            // init status
            status = Status.LEVEL_00;

            //
            isInitialized = true;
            Log.d(TAG, "SmsCommandHandler.init : initialized!");
        }
    }

    public static void reset() {
        Log.d(TAG, "SmsCommandHandler.reset : called!");
        if (!isInitialized) {
            return;
        }

        setStatus(Status.LEVEL_00);
    }

    private static void setStatus(Status s) {
        status = s;
        printStatus();
    }

    public static boolean isAllowedCommand(SmsMsg sms) {
        for (Commands cmd : Commands.values()) {
            if (cmd.toString().equalsIgnoreCase(sms.contents.toString())) {
                return true;
            }
        }
        return false;
    }

    private static void handleCommand(Commands cmd) {
        if (!isInitialized) {
            init();
        }

        if (cmd == Commands.RESET) {
            Log.d(TAG, "SmsCommandHandler.handleCommand : "+cmd.toString()+" COMMAND RECEIVED!");
            startPhoneFinderService("STOP", "Screaming stopped!");
            MediaManager.stopSound();
            MediaManager.setSoundVolume(SOUND_VOLUME_SYSTEM_DEFAULT);
            reset();
            return;
        }

        if (cmd == Commands.PLAY) {
            Log.d(TAG, "SmsCommandHandler.handleCommand : "+cmd.toString()+" COMMAND RECEIVED!");
            switch (status) {
                case LEVEL_00:
                    //TODO: vibrator start
                    setStatus(Status.LEVEL_01);
                    startPhoneFinderService("PLAY", "Screaming started!");
                    break;
                case LEVEL_01:
                    // media manager play with low volume
                    MediaManager.setSoundVolume(1* SOUND_VOLUME_MAX /3);
                    MediaManager.startSound();
                    setStatus(Status.LEVEL_02);
                    break;
                case LEVEL_02:
                    // media manager set volume to medium
                    MediaManager.setSoundVolume(2* SOUND_VOLUME_MAX /3);
                    setStatus(Status.LEVEL_03);
                    break;
                case LEVEL_03:
                    // media manager set volume to max
                    MediaManager.setSoundVolume(3* SOUND_VOLUME_MAX /3);
                    setStatus(Status.LEVEL_04);
                    break;
                case LEVEL_04:
                    // nothing to do
                    break;
                default:
                    break;
            }
        }
    }

    public static void handleSmsMsg(SmsMsg sms) {
        for (Commands cmd : Commands.values()) {
            if (cmd.toString().equalsIgnoreCase(sms.contents.toString())) {
                Log.d(TAG, "SmsCommandHandler.handleSmsMsg : MATCHED! (sms="+sms.contents.toString()+",cmd="+cmd.toString()+")");
                handleCommand(cmd);
                return;
            }
        }
        Log.d(TAG, "SmsCommandHandler.handleSmsMsg : NOT MATCHED! (sms="+sms.contents.toString()+")");
    }

    private static void printStatus() {
        Log.d(TAG, "SmsCommandHandler.handleSmsMsg : Current status="+status.toString());
    }
}
