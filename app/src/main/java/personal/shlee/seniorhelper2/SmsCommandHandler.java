package personal.shlee.seniorhelper2;

import android.util.Log;

/**
 * SMS message handler will react for 2 commands. One is 'Play', the other is 'Reset'.
 * SMS text message '0000' will act as 'Play' command.
 * When 'Play' command come, it will make 'Status' to the next level.
 * I plan to make 5 levels of status like below.
 *         LEVEL_00, // ready
 *         LEVEL_01, // level 01 : vibrator on with default intensity repeatedly
 *         LEVEL_02, // level 02 : media player play sound with volume 1/3 (low volume)
 *         LEVEL_03, // level 03 : media player play sound with volume 2/3 (medium volume)
 *         LEVEL_04, // level 04 : media player play sound with volume 3/3 (max volume)
 * As an first implementation, I'll just implement status changes by SMS message first.
 */
public class SmsCommandHandler {
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
    private static Status status = Status.LEVEL_00;
    private static boolean isInitialized = false;
    private static final String TAG = "SeniorHelper";

    private static void init() {
        if (!isInitialized) {
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
            reset();
            return;
        }

        if (cmd == Commands.PLAY) {
            Log.d(TAG, "SmsCommandHandler.handleCommand : "+cmd.toString()+" COMMAND RECEIVED!");
            switch (status) {
                case LEVEL_00:
                    //TODO: vibrator start
                    setStatus(Status.LEVEL_01);
                    break;
                case LEVEL_01:
                    //TODO: media manager play with low volume
                    setStatus(Status.LEVEL_02);
                    break;
                case LEVEL_02:
                    //TODO: media manager set volume to medium
                    setStatus(Status.LEVEL_03);
                    break;
                case LEVEL_03:
                    //TODO: media manager set volume to max
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
