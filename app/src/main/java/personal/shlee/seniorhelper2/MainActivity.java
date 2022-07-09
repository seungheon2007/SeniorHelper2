package personal.shlee.seniorhelper2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Reference :
         *  - Notification example
         *    > https://o7planning.org/10427/android-notification
         *  - Notification icon creation
         *    > http://zeany.net/37
         *  - Notification icon image source
         *    > https://www.visualpharm.com/free-icons/alert
         *    > https://www.visualpharm.com/free-icons/notification-595b40b75ba036ed117d6e38
         */
        this.button_reset = (Button) this.findViewById(R.id.button_reset);
        this.button_reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SmsCommandHandler.reset();
            }
        });

        requireSmsPerm();
    }
    /**
     * Reference :
     *  - https://ju-hy.tistory.com/50
     */
    private void requireSmsPerm(){
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }
}