package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import mc.wombyte.marcu.jhp_app.Reuseables.TextArea;

/**
 * Created by marcu on 12.07.2017.
 */

public class Subjects_sg_fragment extends FragmentSubject {

    TextView tv_abbreviation;
    EditText ed_abbreviation;
    TextView tv_teacher;
    EditText ed_teacher;
    TextView tv_average;
    TextView tv_color1;
    Button b_color1;
    TextView tv_color2;
    Button b_color2;
    TextView tv_notice;
    TextArea ta_notice;

    Context context;
    ColorPickerDialog cpd;
    InputMethodManager imm;

    public Subjects_sg_fragment() {}

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subjects_sg_fragment, container, false);
        context = container.getContext();

        tv_abbreviation = (TextView) view.findViewById(R.id.tv_abbreviation_subject_activity);
        ed_abbreviation = (EditText) view.findViewById(R.id.ed_abbreviation_subject_activity);
        tv_teacher = (TextView) view.findViewById(R.id.tv_teacher_subject_activity);
        ed_teacher = (EditText) view.findViewById(R.id.ed_teacher_subject_activity);
        tv_average = (TextView) view.findViewById(R.id.tv_average_subject_activity);
        tv_color1 = (TextView) view.findViewById(R.id.tv_color1_subject_activity);
        b_color1 = (Button) view.findViewById(R.id.b_color1_subject_activity);
        tv_color2 = (TextView) view.findViewById(R.id.tv_color2_subject_activity);
        b_color2 = (Button) view.findViewById(R.id.b_color2_subject_activity);
        tv_notice = (TextView) view.findViewById(R.id.tv_notice_subject_activity);
        ta_notice = (TextArea) view.findViewById(R.id.ed_notice_subject_activity);

        //read infos
        ed_abbreviation.setText( Storage.subjects.get(getSubjectIndex()).getAbbreviation());
        ed_teacher.setText( Storage.subjects.get(getSubjectIndex()).getTeacher());
        ta_notice.setText( Storage.subjects.get(getSubjectIndex()).getNotice());
        ta_notice.changeBorderColor( Storage.subjects.get(getSubjectIndex()).getDarkColor());
        tv_average.setText( getResources().getString(R.string.subject_general_average) + " " + Storage.subjects.get(getSubjectIndex()).getAverage());
        b_color1.getBackground().getCurrent().setColorFilter( Storage.subjects.get(getSubjectIndex()).getColor(), PorterDuff.Mode.SRC_ATOP);
        b_color2.getBackground().getCurrent().setColorFilter( Storage.subjects.get(getSubjectIndex()).getDarkColor(), PorterDuff.Mode.SRC_ATOP);

        //input_listener
        b_color1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_color1();
            }
        });
        b_color2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onclick_color2();
            }
        });

        b_color1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_abbreviation.getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(ed_abbreviation.getWindowToken(), 0);

        return view;
    }

    /*
     * Override method from FragmentSubject to save all data
     */
    @Override public void saveData() {
        Storage.subjects.get(getSubjectIndex()).setAbbreviation(ed_abbreviation.getText().toString());
        Storage.subjects.get(getSubjectIndex()).setTeacher(ed_teacher.getText().toString());
        Storage.subjects.get(getSubjectIndex()).setNotice(ta_notice.getText().toString());
    }

    /*
     * onclick input_listener for the button for the 1st color
     */
    private void onclick_color1() {
        cpd = new ColorPickerDialog(context, Storage.subjects.get(getSubjectIndex()).getColor());
        cpd.setAlphaSliderVisible(false);
        cpd.setTitle(getResources().getString(R.string.subject_general_color1));
        cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            public void onColorChanged(int i) {
                Storage.subjects.get(getSubjectIndex()).setColor(i, Storage.settings.subjects_calculateColorsSeperated());
                b_color1.getBackground().getCurrent().setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
                b_color2.getBackground().getCurrent().setColorFilter(Storage.subjects.get(getSubjectIndex()).getDarkColor(), PorterDuff.Mode.SRC_ATOP);
                ta_notice.changeBorderColor( Storage.subjects.get(getSubjectIndex()).getDarkColor());
            }
        });
        cpd.show();
    }

    /*
     * onclick input_listener for the 2nd color button
     */
    private void onclick_color2() {
        cpd = new ColorPickerDialog(context, Storage.subjects.get(getSubjectIndex()).getDarkColor());
        cpd.setAlphaSliderVisible(false);
        cpd.setTitle(getResources().getString(R.string.subject_general_color2));
        cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            public void onColorChanged(int i) {
                Storage.subjects.get(getSubjectIndex()).setDarkColor(i);
                b_color2.getBackground().getCurrent().setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
                ta_notice.changeBorderColor( Storage.subjects.get(getSubjectIndex()).getDarkColor());
            }
        });
        cpd.show();
    }

    /*
     * change the textview of the name to a editText
     * to provide the user an opportunity to change a subjects name
     */
    @Override public void extraFunction() {

    }
}
