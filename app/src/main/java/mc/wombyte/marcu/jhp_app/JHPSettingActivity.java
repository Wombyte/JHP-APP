package mc.wombyte.marcu.jhp_app;

import android.os.Bundle;

import mc.wombyte.marcu.jhp_app.reuseables.settings.IntegerSetting;
import mc.wombyte.marcu.jhp_app.reuseables.settings.SettingActivity;
import mc.wombyte.marcu.jhp_app.reuseables.settings.SettingGroup;
import mc.wombyte.marcu.jhp_app.reuseables.settings.StringSetting;

/**
 * Created by marcus on 22.08.2018.
 */
public class JHPSettingActivity extends SettingActivity {

    public static final String ST1 = "ST1";
    public static final String ST2 = "ST2";
    public static final String ST3 = "ST3";

    public static final String BOL = "BOL";
    public static final String FAL = "FAL";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public SettingGroup getSettingTree() {
        SettingGroup result = new SettingGroup();

        StringSetting name = new StringSetting(
                "PROBE",
                R.drawable.symbol_settings_menu,
                R.string.settings_general_scrollmenu,
                R.string.settings_general_scrollmenu_long,
                R.drawable.symbol_settings_dialog,
                "Probe"
        );
        name.showText();

        IntegerSetting number = new IntegerSetting(
                "INTEGERPROBE",
                R.drawable.symbol_settings_average,
                R.string.settings_subjects_average,
                R.string.settings_subjects_average_long,
                R.drawable.symbol_settings_dialog,
                6
        );
        number.setNumberList(0, 10);
        number.setSelectionByNumber(5);
        number.setMode(IntegerSetting.DESCENDING);
        number.showNumber();

        result.addChild(name);
        result.addChild(number);

        return result;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// sub methods //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * creates a {@link Settings_schedule_times_fragment} for the transmitted lesson
     * @param lc: index of the lesson
     * @return Fragment
     */
    private Settings_schedule_times_fragment getTimeFragment(int lc) {
        Settings_schedule_times_fragment result = new Settings_schedule_times_fragment();
        result.setLessonCount(lc);
        return result;
    }
}
