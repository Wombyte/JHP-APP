package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 29.08.2018.
 */
public abstract class SettingDialog extends Dialog {

    public Context context;
    public SharedPreferences.Editor editor;
    public Setting setting;

    private TextView tv_heading;
    private ImageView symbol;
    private TextView tv_description;
    private LinearLayout container_content;
    private Button b_cancel;
    private Button b_ok;

    public SettingDialog(Context context, SharedPreferences.Editor editor, Setting setting) {
        super(context);
        this.context = context;
        this.editor = editor;
        this.setting = setting;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_dialog);

        //initialization
        tv_heading = findViewById(R.id.tv_heading_settings_dialog);
        symbol = findViewById(R.id.img_setting_dialog);
        tv_description = findViewById(R.id.tv_description_setting_dialog);
        container_content = findViewById(R.id.container_content_setting_dialog);
        b_ok = findViewById(R.id.b_ok_settings_dialog);
        b_cancel = findViewById(R.id.b_cancel_settings_dialog);

        //content
        tv_heading.setText(setting.getDescriptionId());
        symbol.setImageResource(setting.getSymbolId());
        tv_description.setText(setting.getLongDescriptionId());
        container_content.addView(getContentView());

        //listener
        b_ok.setOnClickListener(v -> onYes());
        b_cancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void dismiss() {
        this.setting.saveToPreference(editor);
        this.editor.apply();
        super.dismiss();
    }

    /**
     * enables the ok button by changing
     * color to R.color.first_use_next
     * and availability to true
     */
    public void enableOkButton() {
        b_ok.setTextColor(context.getResources().getColor(R.color.first_use_next));
        b_ok.setEnabled(true);
    }

    /**
     * disables the ok button by changing
     * color to R.color.first_use_next_disabled
     * and availability to false
     */
    public void disableOkButton() {
        b_ok.setTextColor(context.getResources().getColor(R.color.first_use_next_disabled));
        b_ok.setEnabled(false);
    }

    /**
     * provides a method to handle the content view for subclasses
     * @return View: view that is added to {@link this#container_content}
     */
    public abstract View getContentView();

    /**
     * provides a method to handle the action, when the ok button is clicked
     */
    public abstract void onYes();
}
