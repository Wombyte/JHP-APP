package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.ArrayList;

/**
 * Created by marcu on 13.03.2018.
 */

public class Subject_dialog extends Dialog {

    Context context;
    ColorPickerDialog cpd;
    Subjects_color_gridview_adapter adapter;
    ArrayList<Integer> colors = new ArrayList<>();

    EditText ed_name;
    Button b_color;
    Button b_cancel;
    Button b_ok;
    GridView gridView;

    int color = Color.BLACK;

    public Subject_dialog(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.subjects_dialog);

        //initialization
        ed_name = findViewById(R.id.ed_name_subject_dialog);
        b_color = findViewById(R.id.b_color_subjects_dialog);
        b_cancel = findViewById(R.id.b_cancel_subject_dialog);
        b_ok = findViewById(R.id.b_finish_subject_dialog);
        gridView = findViewById(R.id.gridview_color_subject_dialog);

        //content
        changeCurrentColor(color);

        //gridview adapter
        colors = Storage.getSubjectsColorList();
        adapter = new Subjects_color_gridview_adapter(context, R.id.gridview_color_subject_dialog, colors);
        gridView.setAdapter(adapter);

        //listener
        ed_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = charSequence.toString();
                b_cancel.setEnabled( !string.equals("") );
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                changeCurrentColor(colors.get(i));
            }
        });
        b_color.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_color();
            }
        });
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_cancel_subject();
            }
        });
        b_ok.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_ok_subject();
            }
        });
    }

    //******************************************************* onclick listener *******************************************************//

    /*
     * onClick-Listener for the add_subject Button
     * creates the Fragment, where the datas for a new Subject can be added
     * changes to a finish button afterwards
     */
    public void onclick_ok_subject() {
        String name = ed_name.getText().toString();

        if(listener != null) {
            listener.onCreateSubject(name, color);
        }

        dismiss();
    }

    /*
     * onClick-Listener for the cancel-button appearing after New-Button is clicked
     */
    public void onclick_cancel_subject() {
        dismiss();
    }

    /*
     * onClick-Listener for the Color button in the add-container
     * provides a colorpicker
     */
    public void onclick_color() {
        cpd = new ColorPickerDialog(context, color);
        cpd.setAlphaSliderVisible(false);
        cpd.setTitle("Farbe des Faches:");
        cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            public void onColorChanged(int i) {
                changeCurrentColor(i);
            }
        });
        cpd.show();
    }

    //******************************************************* methods *******************************************************//

    /*
     * changes the current color to the transferred one
     * changes: the background color of the color button
     */
    private void changeCurrentColor(int color) {
        this.color = color;
        Drawable gd = b_color.getBackground().getCurrent();
        gd.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    //******************************************************* callback *******************************************************//

    public OnCreateSubjectListener listener = null;
    public interface OnCreateSubjectListener {
        void onCreateSubject(String name, int color);
    }
    public void setCreateSubjectListener(OnCreateSubjectListener listener) { this.listener = listener; }
}
