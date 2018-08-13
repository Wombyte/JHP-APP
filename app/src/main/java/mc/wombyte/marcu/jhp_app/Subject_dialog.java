package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.SubjectSuggestion;

/**
 * Created by marcu on 13.03.2018.
 */

public class Subject_dialog extends Dialog {

    private Context context;
    private ColorPickerDialog cpd;
    private Subjects_suggestion_listview_adapter suggestion_adapter;
    private Subjects_color_gridview_adapter adapter;
    private ArrayList<Integer> colors;

    private AutoCompleteTextView ed_name;
    private Button b_color;
    private Button b_cancel;
    private Button b_ok;
    private GridView gridView;

    private int color = Color.BLACK;

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

        //auto complete name
        suggestion_adapter = new Subjects_suggestion_listview_adapter(
                context,
                R.layout.subjects_suggestion_listview_fragment,
                SubjectSuggestion.getSuggestionList(context)
        );
        ed_name.setAdapter(suggestion_adapter);
        ed_name.setOnItemClickListener((adapterView, view, i, l) ->
            changeCurrentColor(suggestion_adapter.getSuggestionItem(i).getColor())
        );

        //color
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
                b_ok.setEnabled( !string.equals("") );
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
        gridView.setOnItemClickListener(((adapterView, view, i, l) -> changeCurrentColor(colors.get(i))));
        b_color.setOnClickListener((v) -> onclick_color());
        b_cancel.setOnClickListener((v) -> dismiss());
        b_ok.setOnClickListener((v) -> onclick_ok_subject());
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// onclick listener ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * onClick-Listener for the add_subject Button
     * creates the Fragment, where the datas for a new Subject can be added
     * changes to a finish button afterwards
     */
    private void onclick_ok_subject() {
        String name = ed_name.getText().toString();

        if(listener != null) {
            listener.onCreateSubject(name, color);
        }

        dismiss();
    }

    /**
     * onClick-Listener for the Color button in the add-container
     * provides a colorpicker
     */
    private void onclick_color() {
        cpd = new ColorPickerDialog(context, color);
        cpd.setAlphaSliderVisible(false);
        cpd.setTitle("Farbe des Faches:");
        cpd.setOnColorChangedListener(this::changeCurrentColor);
        cpd.show();
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * changes the current color to the transferred one
     * changes the background color of the color button
     */
    private void changeCurrentColor(int color) {
        this.color = color;
        Drawable gd = b_color.getBackground().getCurrent();
        gd.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////// interface ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public OnCreateSubjectListener listener = null;
    public interface OnCreateSubjectListener {
        void onCreateSubject(String name, int color);
    }
    public void setCreateSubjectListener(OnCreateSubjectListener listener) { this.listener = listener; }
}
