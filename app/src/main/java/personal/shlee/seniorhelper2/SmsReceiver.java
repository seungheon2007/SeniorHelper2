package personal.shlee.seniorhelper2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Reference :
 *  - https://hongku.tistory.com/209
 *  - https://ju-hy.tistory.com/50
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SeniorHelper";

    @Override
    public void onReceive(Context context, Intent intent) {
        // SMS_RECEIVED에 대한 액션일때 실행
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(TAG, "SmsReceiver.onReceive : 호출됨");

            // Bundle을 이용해서 메세지 내용을 가져옴
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);
            // 메세지가 있을 경우 내용을 로그로 출력해 봄
            if (messages.length > 0) {
                // 메세지의 내용을 가져옴
                String sender = messages[0].getOriginatingAddress();
                String contents = messages[0].getMessageBody().toString();
                Date receivedDate = new Date(messages[0].getTimestampMillis());

                // 로그를 찍어보는 과정이므로 생략해도 됨
                Log.d(TAG, "SmsReceiver.onReceive : sender=" + sender);
                Log.d(TAG, "SmsReceiver.onReceive : contents=" + contents);
                Log.d(TAG, "SmsReceiver.onReceive : receivedDate=" + receivedDate);

                SmsMsg smsMsg = new SmsMsg(sender,contents, receivedDate);

                if (SmsCommandHandler.isAllowedCommand(smsMsg)) {
                    Log.d(TAG, "SmsReceiver.onReceive : '"+smsMsg.contents+"' is allowed command!");
                }
                //
                SmsCommandHandler.handleSmsMsg(smsMsg);
                Toast.makeText(context.getApplicationContext(), "MSG : " + contents, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }

        return messages;
    }

}