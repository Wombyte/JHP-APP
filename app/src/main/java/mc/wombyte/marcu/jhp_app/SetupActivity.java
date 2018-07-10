package mc.wombyte.marcu.jhp_app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by marcu on 23.12.2017.
 */

public class SetupActivity extends Activity {

    RelativeLayout container;
    Button b_back;
    Button b_next;

    SettingFragment[] fragment = new SettingFragment[5];

    int current_fragment_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);

        //initialization
        container = (RelativeLayout) findViewById(R.id.container_setup_activity);
        b_back = (Button) findViewById(R.id.b_back_setup_activity);
        b_next = (Button) findViewById(R.id.b_next_setup_activity);

        fragment[0] = new Setup_text_fragment();
        fragment[1] = new Setup_rating_fragment();
        fragment[2] = new Settings_schedule_times_fragment();
        fragment[3] = new Settings_menu_fragment();
        fragment[4] = new Setup_text_fragment();

        //load first setup
        disabledBackButton();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ((Setup_text_fragment) fragment[0]).setMessageText(R.string.first_use_welcome);
        ft.add(R.id.container_setup_activity, fragment[0]);
        ft.commit();

        //input_listener
        for(SettingFragment f: fragment) {
            f.setInputListener(new SettingFragment.OnInputListener() {
                @Override public void onLegitInput() {
                    enabledNextButton();
                }
            });
        }
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onClick_back();
            }
        });
        b_next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onClick_next();
            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        Storage.settings.general_setNewSetup(false);
    }

    //******************************************************* onclick input_listener *******************************************************//

    /*
     * onclick input_listener for the 'back' button
     * returns to the last setup fragment
     */
    private void onClick_back() {
        if(current_fragment_id == fragment.length-1) {
            b_next.setText( getResources().getString(R.string.first_use_next));
        }

        int new_fragment_id = current_fragment_id-1;
        if(new_fragment_id == 0) {
            disabledBackButton();
        }

        loadFragment(new_fragment_id);
        current_fragment_id = new_fragment_id;
    }

    /*
     * onClick input_listener for the 'next' button
     * loads the next fragment
     */
    private void onClick_next() {
        disabledNextButton();

        if(current_fragment_id == 0) {
            enabledBackButton();
        }
        if(current_fragment_id == fragment.length-1) {
            openMainActivity();
            return;
        }

        int new_fragment_id = current_fragment_id+1;
        if(new_fragment_id == fragment.length-1) {
            b_next.setText( getResources().getString(R.string.first_use_finish));
        }

        loadFragment(new_fragment_id);
        current_fragment_id = new_fragment_id;
    }

    //******************************************************* methods *******************************************************//

    /*
     * enables the 'back' button and sets a new font color
     */
    private void enabledBackButton() {
        b_back.setEnabled(true);
        b_back.setTextColor( getResources().getColor(R.color.first_use_back));
    }

    /*
     * disables the 'back' button and sets a new font color
     */
    private void disabledBackButton() {
        b_back.setEnabled(false);
        b_back.setTextColor( getResources().getColor(R.color.first_use_back_disabled));
    }

    /*
     * enables the 'next' button and sets a new font color
     */
    private void enabledNextButton() {
        b_next.setEnabled(true);
        b_next.setTextColor( getResources().getColor(R.color.first_use_next));
    }

    /*
     * disables the 'back' button and sets a new font color
     */
    private void disabledNextButton() {
        b_next.setEnabled(false);
        b_next.setTextColor( getResources().getColor(R.color.first_use_next_disabled));
    }

    /*
     * opens main activity
     */
    private void openMainActivity() {
        Intent toMainActivity = new Intent();
        toMainActivity.setClass(this, MainActivity.class);
        this.startActivity(toMainActivity);
    }

    /*
     * loads the fragment with the transferred id
     */
    private void loadFragment(int new_fragment_id) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(new_fragment_id == 0) {
            ((Setup_text_fragment) fragment[new_fragment_id]).setMessageText(R.string.first_use_welcome);
        }
        if(new_fragment_id == fragment.length-1) {
            ((Setup_text_fragment) fragment[new_fragment_id]).setMessageText(R.string.first_use_go);
        }

        ft.replace(R.id.container_setup_activity, fragment[new_fragment_id]);
        ft.commit();
    }
}
