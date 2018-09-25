package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.reuseables.ViewSwitcher;

/**
 * Created by marcu on 14.10.2017.
 */

public class Setting_dialog extends Dialog {

    Context context;

    SettingField settingField;

    TextView tv_heading;
    TextView tv_description;
    ViewSwitcher vs_value;
    EditText ed_value;
    RadioGroup radioGroup_value;
    String[] radionGroup_options;
    Button b_ok;
    Button b_cancel;

    byte selected_option = 0;

    //lister
    SettingDialogListener listener = null;

    public Setting_dialog(Context context, SettingField settingField) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.settings_old_dialog);

        //initialization
        this.settingField = settingField;
        tv_heading = (TextView) findViewById(R.id.tv_heading_settings_dialog);
        tv_description = (TextView) findViewById(R.id.tv_description_setting_dialog);
        vs_value = ((ViewSwitcher) findViewById(R.id.vs_value_setting_dialog)).createView(context);
        b_ok = (Button) findViewById(R.id.b_ok_settings_dialog);
        b_cancel = (Button) findViewById(R.id.b_cancel_settings_dialog);

        //content
        tv_heading.setText(settingField.description_id);

        if(settingField.description_long_id != SettingStructure.NONE) {
            tv_description.setText(settingField.description_long_id);
        }
        else {
            //"remove" tv_description
            tv_description.setHeight(0);
            tv_description.setPadding(0, 0, 0, 0);
            tv_heading.setPadding(0, 0, 0, 0);
        }

        if(settingField.type == SettingField.INTEGER) {
            vs_value.switchToView(0);
            ed_value = (EditText) vs_value.getActiveView();
            ed_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            ed_value.setText( String.valueOf(settingField.getIntValue()));
        }
        if(settingField.type == SettingField.DOUBLE) {
            vs_value.switchToView(0);
            ed_value = (EditText) vs_value.getActiveView();
            ed_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed_value.setText( String.valueOf(settingField.getDoubleValue()));
        }
        if(settingField.type == SettingField.STRING) {
            vs_value.switchToView(0);
            ed_value = (EditText) vs_value.getActiveView();
            ed_value.setInputType(InputType.TYPE_CLASS_TEXT);
            ed_value.setText(settingField.getStringValue());
        }
        if(settingField.type == SettingField.BYTE) {
            vs_value.switchToView(1);
            radioGroup_value = (RadioGroup) vs_value.getActiveView();
            radionGroup_options = context.getResources().getStringArray(settingField.getArrayId());

            for(String string: radionGroup_options) {
                RadioButton rb = new RadioButton(context);
                rb.setText(string);
                radioGroup_value.addView(rb);
            }
            selected_option = settingField.getByteValue();
            radioGroup_value.check(selected_option);

            radioGroup_value.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    selected_option = (byte) i;
                }
            });
        }

        //input_listener
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_ok();
                dismiss();
            }
        });
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
            }
        });
    }

    //******************************************************* input_listener *******************************************************//
    /*
     * onclick input_listener for the button ok
     */
    private void onclick_ok() {
        //if the settingfield requires an integer
        if(settingField.type == SettingField.INTEGER) {
            String text = ed_value.getText().toString();
            int value_int;
            try {
                value_int = Integer.parseInt(text);
            } catch (Exception e) {
                return;
            }

            if(value_int >= settingField.min && value_int <= settingField.max) {
                listener.onIntResult(value_int);
            }
        }
        //if the settingfield requires a double value
        if(settingField.type == SettingField.DOUBLE) {
            String text = ed_value.getText().toString();
            double value_double;
            try {
                value_double = Double.parseDouble(text);
            } catch (Exception e) {
                return;
            }

            if(value_double >= settingField.min && value_double <= settingField.max) {
                listener.onDoubleResult(value_double);
            }
        }
        //if the settingfield requires a String
        if(settingField.type == SettingField.STRING) {
            String text = ed_value.getText().toString();
            listener.onStringResult(text);
        }
        //if the settingField requires a Byte value
        if(settingField.type == SettingField.BYTE) {
            listener.onByteResult(selected_option);
        }
    }

    //******************************************************* callback *******************************************************//
    public interface SettingDialogListener {
        void onIntResult(int value);
        void onDoubleResult(double value);
        void onStringResult(String value);
        void onByteResult(byte value);
    }
    public void setListener(SettingDialogListener listener) {
        this.listener = listener;
    }

}
