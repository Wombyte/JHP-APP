package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by marcu on 23.12.2017.
 */

public class Setup_text_fragment extends SettingFragment {

    Context context;

    TextView tv_message;

    int message_id = R.string.first_use_welcome;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_text_fragment, container, false);
        context =  container.getContext();

        tv_message = (TextView) view.findViewById(R.id.tv_message_setup_text_fragment);
        tv_message.setText( context.getResources().getString(message_id));

        input_listener.onLegitInput();

        return view;
    }

    public void setMessageText(int string_id) {
        message_id = string_id;
    }
}
