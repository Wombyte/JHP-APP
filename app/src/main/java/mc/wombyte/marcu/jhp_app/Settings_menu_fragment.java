package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;

/**
 * Created by marcu on 17.11.2017.
 */

public class Settings_menu_fragment extends SettingFragment {

    Context context;
    DrawLayout_MenuFragment layout;

    RelativeLayout container;
    Setting_menu_listener listener;
    BooleanDialog dialog;

    int x, y, r;
    int side;
    boolean inSettingActivity = false;

    public Settings_menu_fragment() { }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_menu_fragment, container, false);
        context = c.getContext();
        inSettingActivity = getActivity() instanceof Settings_activity;

        //initialization
        container = (RelativeLayout) view.findViewById(R.id.container_menu_setting_fragment);
        dialog = new BooleanDialog(context, getResources().getString(R.string.settings_general_scrollmenu_fragment_question));

        //add circle panel
        layout = new DrawLayout_MenuFragment(context, inSettingActivity);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
        layout.setTranslationY(10);
        container.addView(layout);

        //content
        x = Storage.settings.general_getCirclePos()[0];
        y = Storage.settings.general_getCirclePos()[1];
        r = Storage.settings.general_getCirclePos()[2];
        side = Storage.settings.general_getCirclePos()[3];
        if(x != 0 && y != 0 && r != 0) {
            if(inSettingActivity) {
                y -= context.getResources().getDimensionPixelOffset(R.dimen.setting_menu_toolbar_height);
            }
            layout.setCoordinates(x, y, r);
            if(input_listener != null) {
                input_listener.onLegitInput();
            }
        }

        //input_listener
        listener = new Setting_menu_listener(context, inSettingActivity) {
            @Override
            public void onScrollFinished(int x_circle, int y_circle, int r_circle, int side_circle) {
                drawCircle(x, y, r, side);
                dialog.show();
            }
        };
        container.setOnTouchListener(listener);
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                saveCircle();
            }
            @Override public void onNo() {
                resetCircle();
            }
        });

        return view;
    }

    /*
     * method, called when the dialog is answered with "yes"
     */
    private void saveCircle() {
        if(inSettingActivity) {
            y += context.getResources().getDimensionPixelOffset(R.dimen.setting_menu_toolbar_height);
        }
        Storage.settings.general_setCirclePos(new int[] {x, y, r, side});
        if(input_listener != null) {
            input_listener.onLegitInput();
        }
    }

    /*
     * method, called when the dialog is answered with "no"
     */
    private void resetCircle() {
        x = y = r = 0;
        listener.end = false;
        listener.points.clear();
    }

    /*
     * draws the circle & saves the coordinates
     */
    private void drawCircle(int x, int y, int r, int side) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.side = side;
        layout.setCoordinates(x, y, r);
        if(input_listener != null) {
            input_listener.onLegitInput();
        }
    }

    /*
     * defines whether the circle is draw in settings activity or not
     */
    public Settings_menu_fragment inSettingActivity() {
        this.inSettingActivity = true;
        return this;
    }
}

/********************************************* DrawLayout ********************************************/
class DrawLayout_MenuFragment extends View{

    Context context;
    boolean inSettingActivity;

    int x = 0;
    int y = 0;
    int r = 0;
    Paint paint = new Paint();

    /*
     * constructor to use it in code
     * defines and calculates all vars
     */
    public DrawLayout_MenuFragment(Context context, boolean inSettingsActivity) {
        super(context);
        this.context = context;
        this.inSettingActivity = inSettingsActivity;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor( getResources().getColor(R.color.colorPrimary));
        paint.setStrokeWidth( getResources().getDimensionPixelSize(R.dimen.setting_menu_fragment_circle_menu));
    }

    /*
     * constructor for android
     */
    public DrawLayout_MenuFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * methode to draw (is called on invalidate())
     * used general vars are changed in scrolledBy()
     */
    @Override public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(x != 0 && y != 0 && r != 0) {
            canvas.drawCircle(x, y, r, paint);
        }
    }

    /*
     * resets the coordinates of the circle
     */
    public void setCoordinates(int x, int y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.invalidate();
    }
}
