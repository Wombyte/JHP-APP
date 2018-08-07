package mc.wombyte.marcu.jhp_app;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by marcu on 23.12.2017.
 */

public class Settings_info_fragment extends SettingFragment {

    TextView tv_code;
    TextView tv_privacy_policy;
    TextView tv_plugins;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.settings_info_fragment, container, false);

        //initialization
        tv_code = view.findViewById(R.id.tv_project_settings_info_fragment);
        tv_privacy_policy = view.findViewById(R.id.tv_privacy_policy_settings_info_fragment);
        tv_plugins = view.findViewById(R.id.tv_plugins_settings_info_fragment);

        //content
        tv_code.setMovementMethod(LinkMovementMethod.getInstance());
        tv_privacy_policy.setMovementMethod(LinkMovementMethod.getInstance());
        tv_plugins.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
