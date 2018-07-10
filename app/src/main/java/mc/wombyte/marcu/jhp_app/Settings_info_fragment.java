package mc.wombyte.marcu.jhp_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by marcu on 23.12.2017.
 */

public class Settings_info_fragment extends SettingFragment {

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_info_fragment, container, false);
    }
}
