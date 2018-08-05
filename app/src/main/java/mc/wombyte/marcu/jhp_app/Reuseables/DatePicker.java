package mc.wombyte.marcu.jhp_app.Reuseables;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mc.wombyte.marcu.jhp_app.Date_spinner_adapter;
import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcu on 05.03.2018.
 */

public class DatePicker extends LinearLayout {

    LayoutInflater inflater;
    Context context;

    ViewSwitcher vs_date;
    ImageButton b_picker;

    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d. MMM yyyy", Locale.GERMANY);

    ArrayList<String> dates = new ArrayList<>();
    Date_spinner_adapter adapter;
    AdapterView adapterView_date;
    int color_dark;
    int color_light;
    Date selected_date = new Date();


    /*
     * constructors needed for xml
     */
    public DatePicker(Context context) {
        super(context);
        this.context = context;
        onCreateView();
    }

    public DatePicker(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        onCreateView();
    }

    public DatePicker(Context context, AttributeSet set, int defStyle) {
        super(context, set, defStyle);
        this.context = context;
        onCreateView();
    }

    /*
     * method which shows the time picker
     */
    private void onCreateView() {
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.date_picker, this, true);

        //initialization
        (vs_date = findViewById(R.id.vs_date_picker)).createView(context);
        b_picker = findViewById(R.id.b_dialog_date_picker);

        //color
        color_light = context.getResources().getColor(R.color.colorPrimary);
        color_dark = context.getResources().getColor(R.color.colorPrimaryDark);
        adapter = new Date_spinner_adapter(context, android.R.layout.simple_spinner_dropdown_item, dates, color_dark);

        //onclick-listener
        b_picker.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                onclick_b_picker();
            }
        });
        ((Spinner) vs_date.getView(0)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_date = stringToDate(dates.get(i));
                adapterView_date = adapterView;
                onDateSelected();
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }

    //******************************************************* listener *******************************************************//

    /*
     * onclick listener for the button
     * opens the a date picker dialog
     */
    private void onclick_b_picker() {
        Calendar c = Calendar.getInstance();
        c.setTime(selected_date);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
                setCustomDate(i, i1, i2);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                listener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    /*
     * onclick input_listener for the event that an item is selected in date_spinner
     */
    private void onDateSelected() {
        if(adapterView_date != null) {
            TextView textView = (TextView) adapterView_date.getChildAt(0);
            if(textView != null) {
                textView.setTextColor(color_light);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        if(listener != null) {
            listener.onDateChanged(selected_date);
        }
    }

    /*
     * onclick listener called after the date dialog has been closed
     */
    private void setCustomDate(int year, int month, int day) {
        switchToTextView();
        selected_date = getCustomDate(year, month, day);
        onDateSelected();

        TextView textView = (TextView) vs_date.getActiveView();
        textView.setText( sdf.format(selected_date));
    }

    //******************************************************* methods *******************************************************//

    /*
     * opens the dialog from outside
     */
    public void openDateDialog() {
        onclick_b_picker();
    }

    /*
     * updates the list of date-strings
     */
    public void updateList(ArrayList<String> dates) {
        this.dates = dates;
        switchToSpinner();
    }

    /*
     * sets the current date
     */
    public void setDate(Date date) {
        selected_date = date;
        switchToTextView();
    }

    /*
     * sets the text to ""
     */
    public void setDateToTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_YEAR, 1);
        setDate(c.getTime());
    }

    /*
     * @return: selected date
     */
    public Date getDate() { return selected_date; }

    /*
     * switches the vs to the Spinner and updates its list
     */
    public void switchToSpinner() {
        vs_date.switchToView(0);

        adapter = new Date_spinner_adapter(context, android.R.layout.simple_spinner_dropdown_item, dates, color_dark);

        Spinner spinner = (Spinner) vs_date.getActiveView();
        spinner.setAdapter(adapter);
    }

    /*
     * sets the selection of the spinner
     */
    public void setSelection(int i) {
        if(vs_date.isView(0)) {
            Spinner spinner = (Spinner) vs_date.getActiveView();
            spinner.setSelection(i);
        }
    }

    /*
     * switches the vs to TextView
     */
    public void switchToTextView() {
        vs_date.switchToView(1);
        TextView textView = (TextView) vs_date.getActiveView();
        textView.setTextColor(color_light);
        textView.setText(sdf.format(selected_date));
    }

    /*
     * changes all components including the light color
     */
    public void setLightColor(int color_light) {
        this.color_light = color_light;
        if(vs_date.isView(1)) {
            ((TextView) vs_date.getView(1)).setTextColor(color_light);
        }
        onDateSelected();
    }

    /*
     * changes all components including the dark color
     */
    public void setDarkColor(int color_dark) {
        this.color_dark = color_dark;
        changeImageButtonColor(b_picker, color_dark);
    }

    /*
     * date to string
     */
    private Date stringToDate(String date) {
        try {
            Date d = sdf.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(selected_date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return d;
        }
        catch(ParseException e) {
            e.getStackTrace();
            return new Date();
        }
    }

    /*
     * creates a date with the year, month and day transmitted
     */
    private Date getCustomDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /*
     * changes the color of the Drawable of an Imagebutton
     */
    private void changeImageButtonColor(ImageButton button, int color) {
        Drawable symbol = button.getDrawable();
        symbol.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        button.setImageDrawable(symbol);
    }

    //******************************************************* callback *******************************************************//
    public onDateChangeListener listener = null;
    public interface onDateChangeListener {
        void onDateChanged(Date date);
    }
    public void setOnDateChangeListener(onDateChangeListener listener) {
        this.listener = listener;
    }
}
