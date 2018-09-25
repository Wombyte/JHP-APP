package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.reuseables.ViewSwitcher;

/**
 * Created by marcu on 08.10.2017.
 */

public class Settings_listview_adapter extends ArrayAdapter<ArrayList<Integer>> {

    ImageView image;
    TextView tv_description;
    TextView tv_value;
    TextView tv_long_description;
    ViewSwitcher vs_setting;

    SettingField f_p;
    Switch switcher;

    Context context;
    SettingGroup group;
    int pos;

    public Settings_listview_adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public Settings_listview_adapter(Context context, int resource, ArrayList<ArrayList<Integer>> paths) {
        super(context, resource, paths);
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.settings_listview_fragment, null);
            context = container.getContext();
        }

        image = (ImageView) view.findViewById(R.id.image_settings_listview);
        tv_description = (TextView) view.findViewById(R.id.tv_description_settings_listview);
        //tv_value = (TextView) view.findViewById(R.id.tv_value_settings_listview);
        tv_long_description = (TextView) view.findViewById(R.id.tv_long_description_settings_listview);
        vs_setting = ((ViewSwitcher) view.findViewById(R.id.vs_view_settings_listview)).createView(context);

        pos = position;

        System.out.print(Storage.settings.getStructureByPath(getItem(pos)).getClass() + " ");
        if(Storage.settings.getStructureByPath(getItem(pos)).isSettingGroup()) {
            final SettingGroup p = (SettingGroup) Storage.settings.getStructureByPath(getItem(pos));
            if (p != null) {
                //set the Image
                if(p.getSymbolId() != SettingStructure.NONE) {
                    image.setImageResource(p.getSymbolId());
                }

                //set the description
                tv_description.setText( view.getResources().getString(p.getDescriptionId()));

                //set long description
                if(p.description_long_id != SettingStructure.NONE) {
                    tv_long_description.setText( view.getResources().getString(p.getLongDescriptionId()));
                }
                else {
                    tv_long_description.setTextSize(0); //reduce the view size to 0
                }

                //set the control view
                vs_setting.switchToView(Settings.SETTINGS_VIEW_FRAGMENT);
                ((ImageView) vs_setting.getActiveView()).setImageResource(R.drawable.symbol_continue);
            }
        }
        else {
            final SettingField p = (SettingField) Storage.settings.getStructureByPath(getItem(pos));

            //set the structure is a field
            if(p.type == SettingField.INTEGER) {
                tv_value.setText(String.valueOf(p.getIntValue()));
            }
            if(p.type == SettingField.BOOLEAN) {
                switcher = (Switch) vs_setting.getView(1);
                switcher.setChecked(p.getBooleanValue());
                ((Switch) vs_setting.getView(1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Storage.settings.setChildValue(p.path, b);
                    }
                });
            }

            //set the Image
            if(p.getSymbolId() != SettingStructure.NONE) {
                image.setImageResource(p.getSymbolId());
            }

            //set the description
            tv_description.setText( view.getResources().getString(p.getDescriptionId()));

            //set long description
            if(p.description_long_id != SettingStructure.NONE) {
                tv_long_description.setText( view.getResources().getString(p.getLongDescriptionId()));
            }
            else {
                tv_long_description.setTextSize(0); //reduce the view size to 0
            }

            //set the control view
            vs_setting.switchToView(p.settings_view_id);
        }

        return view;
    }

}
