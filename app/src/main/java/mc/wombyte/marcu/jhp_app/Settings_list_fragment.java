package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by marcu on 08.10.2017.
 */

public class Settings_list_fragment extends SettingFragment {

    ListView list;

    Settings_listview_adapter adapter;
    SettingGroup settingGroup = new SettingGroup(); //must be new SettingGroup -> no NullPointerE in adapter

    Context context;
    Settings_activity parent_activity;
    int container_id;

    public Settings_list_fragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        context = container.getContext();
        parent_activity = (Settings_activity) getActivity();

        //initialization
        list = (ListView) view.findViewById(R.id.listview_settings);

        //list
        adapter = new Settings_listview_adapter(context, R.layout.settings_listview_fragment, settingGroup.getChildPaths());
        list.setAdapter(adapter);
        container_id = container.getId();

        //input_listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onclickItem_list(i);
            }
        });

        return view;
    }

    //******************************************************* input_listener *******************************************************//

    /*
     * onclick method, that is called when an item is clicked shortly
     */
    private void onclickItem_list(int i) {
        if(settingGroup.child_settings.get(i) instanceof SettingField) {
            if(((SettingField) settingGroup.child_settings.get(i)).type == SettingField.BOOLEAN) {
                return;
            }
        }

        //if there is a further fragment, it is opened
        if(settingGroup.child_settings.get(i).has_fragment) {
            changeSettingFragment( settingGroup.child_settings.get(i));
            return;
        }

        //if there is a further dialog, it is opened
        if(settingGroup.child_settings.get(i).has_dialog) {
            openSettingDialog(i);
        }
    }

    //******************************************************* methods *******************************************************//

    /*
     * opens a dialog to let the user write the wanted value (String, int, double)
     */
    private void openSettingDialog(int i) {
        final SettingField settingField = (SettingField) settingGroup.child_settings.get(i);
        Setting_dialog dialog = new Setting_dialog(context, settingField);
        dialog.setListener(new Setting_dialog.SettingDialogListener() {
            @Override public void onIntResult(int value) {
                settingField.setValue(value);
            }
            @Override public void onDoubleResult(double value) {
                settingField.setValue(value);
            }
            @Override public void onStringResult(String value) {
                settingField.setValue(value);
            }
            @Override public void onByteResult(byte value) {
                settingField.setValue(value);
                if(settingField.listener == null) {
                    return;
                }
                settingField.onOptionChange(value);
            }
        });
        dialog.show();
    }

    //******************************************************* setter *******************************************************//

    /*
     * sets the SettingsGroup needed to load the listview
     */
    public void setSettingGroup(ArrayList<Integer> path) {
        this.path = path;

        //finding the settinggroup by path (Settings = root)
        settingGroup = Storage.settings;
        for(Integer i: path) settingGroup = (SettingGroup) settingGroup.child_settings.get(i);
    }

    //******************************************************* getter *******************************************************//

    /*
     * @return: settingGroup
     */
    public SettingGroup getSettingGroup() {
        return settingGroup;
    }
}
