package mc.wombyte.marcu.jhp_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by marcu on 03.03.2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {}

    @Override public void onReceive(Context context, Intent intent) {
        if(Storage.homework.size() == 0) {
            return;
        }

        NotificationJHP n = new NotificationJHP(context);
        n.show();
    }
}
