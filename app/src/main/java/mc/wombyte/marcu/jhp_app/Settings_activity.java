package mc.wombyte.marcu.jhp_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by marcu on 08.10.2017.
 */

public class Settings_activity extends JHP_Activity {

    ImageButton b_back;
    SettingFragment fragment;
    final static int CONTAINER_ID = R.id.fragment_container_settings;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //reading data, if the app is started new
        if(!Storage.read_already) {
            Storage.read_already = true;
            FileLoader fileReader = new FileLoader();
            fileReader.readData();
        }
        Storage.settings.refresh();

        try {
            FileLoader fileLoader = new FileLoader();
            fileLoader.readSettings();
        } catch(IOException e) {
            e.printStackTrace();
        }

        //initialization
        b_back = (ImageButton) findViewById(R.id.b_back_settings);
        fragment = new Settings_list_fragment();
        ((Settings_list_fragment) fragment).setSettingGroup(new ArrayList<Integer>());

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container_settings, fragment);
        ft.commit();

        //input_listener
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_back();
            }
        });

        //options
        setSettingOptions();
        setMenuContainerId(R.id.setting_scroll_container);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// options ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
    /**
     * defines the actions that should be performed after the click on an option
     * 1. go back
     * 2. return to parent
     * >2. go the "id"th group of this child
     * @param id: id od the option
     */
    @Override public void optionActions(int id) {
        switch(id) {
            case 0: onclick_back(); break;
            case 1: returnToParent(); break;
            default:
                Settings_list_fragment f;
                try {
                    f = (Settings_list_fragment) fragment;
                } catch(Exception e) {
                    e.printStackTrace();
                    break;
                }
                id--;
                int group_count = 0;
                SettingGroup new_settingGroup = new SettingGroup();
                for(int i = 0; i < f.getSettingGroup().getChildSettings().size(); i++) {
                    SettingStructure child = f.getSettingGroup().getChildAt(i);
                    if(child instanceof SettingGroup) {
                        group_count++;
                        if(group_count == id) {
                            new_settingGroup = (SettingGroup) child;
                        }
                    }
                }
                f.changeSettingFragment(new_settingGroup); //setSettingOptions included
        }
    }

    /*
     * set all new options depending on the current settinggroup
     */
    public void setSettingOptions() {
        options.clear();
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_back),
                getResources().getString(R.string.option_back_home)
        ));
        options.add(new Option(
                Color.rgb(200, 200, 200),
                Color.rgb(120, 120, 120),
                getResources().getDrawable(R.drawable.symbol_arrow_left),
                getResources().getString(R.string.settings_option_back)
        ));


        if(fragment instanceof Settings_list_fragment) {
            Settings_list_fragment f = (Settings_list_fragment) fragment;
            SettingGroup settingGroup = f.getSettingGroup();
            for(SettingStructure s: settingGroup.getChildSettings()) {
                if(s instanceof SettingGroup) {
                    options.add(new Option(
                            Color.rgb(200, 200, 200),
                            Color.rgb(120, 120, 120),
                            getResources().getDrawable(s.getSymbolId()),
                            getResources().getString(s.getDescriptionId())
                    ));
                }
            }
        }

        setOptionId(1);
        setOptions();
    }

    /*
     * returns to the parent settinggroup
     */
    public void returnToParent() {
        ArrayList<Integer> path = (ArrayList<Integer>) fragment.getPath().clone();
        if(path.equals(Storage.settings.path)) {
            return;
        }

        //getting parent
        path.remove(path.size()-1);
        SettingGroup new_settingGroup = (SettingGroup) Storage.settings.getStructureByPath(path);

        //update fragment
        fragment.changeSettingFragment(new_settingGroup);
        setSettingOptions();
    }

    @Override protected void onPause() {
        super.onPause();
        FileSaver fileSaver = new FileSaver(this);
        fileSaver.saveData();
    }

    //******************************************************* input_listener *******************************************************//

    /*
     * onclick input_listener for the button back
     */
    private void onclick_back() {
        Intent toMainActivity = new Intent();
        toMainActivity.setClass(this, MainActivity.class);
        this.startActivity(toMainActivity);
    }
}
