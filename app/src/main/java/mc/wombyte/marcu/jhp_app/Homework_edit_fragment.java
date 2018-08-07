package mc.wombyte.marcu.jhp_app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.Classes.HomeworkSolution;
import mc.wombyte.marcu.jhp_app.Reuseables.BooleanDialog;
import mc.wombyte.marcu.jhp_app.Reuseables.ImageListView;
import mc.wombyte.marcu.jhp_app.Reuseables.TextArea;
import mc.wombyte.marcu.jhp_app.Reuseables.ViewSwitcher;

/**
 * Created by marcus on 12.05.2018.
 */

public class Homework_edit_fragment extends Fragment {

    Context context;
    int light_color;
    int dark_color;

    ViewSwitcher vs_solution;
    ImageButton b_solution[];
    ImageButton b_finished;

    HomeworkSolution solution = new HomeworkSolution();
    int current_solution = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homework_edit_fragment, container, false);
        context = container.getContext();

        //initialization
        b_solution = new ImageButton[] {
                view.findViewById(R.id.b_text_edit_homework),
                view.findViewById(R.id.b_image_solution_homework),
                view.findViewById(R.id.b_docs_solution_homework),
                view.findViewById(R.id.b_sheets_solution_homework),
                view.findViewById(R.id.b_slides_solution_homework)
        };
        b_finished = view.findViewById(R.id.b_finished_solution_homework);
        vs_solution = ((ViewSwitcher) view.findViewById(R.id.vs_solution_homework)).createView(context);

        //action
        ((TextArea) vs_solution.getActiveView()).setText(solution.getText());

        //color
        activateView(0);
        ((TextArea) vs_solution.getActiveView()).changeBorderColor(Color.argb(0, 0, 0, 0));

        //drawable
        if(solution.isFinished()) {
            b_finished.setImageResource(R.drawable.symbol_homework_solution_true);
        }
        else {
            b_finished.setImageResource(R.drawable.symbol_homework_solution_false);
        }

        //listener
        b_solution[0].setOnClickListener((v) -> activateView(0));
        b_solution[1].setOnClickListener((v) -> activateView(1));
        b_solution[2].setOnClickListener((v) -> activateView(2));
        b_solution[3].setOnClickListener((v) -> activateView(3));
        b_solution[4].setOnClickListener((v) -> activateView(4));
        b_finished.setOnClickListener((v) -> changeState());

        ((ListView) vs_solution.getView(2)).setOnItemClickListener(
                (adapterView, v, i, l) -> onclick_list(i)
        );

        return view;
    }

    /*
     * save data
     */
    @Override public void onPause() {
        if(current_solution == 0) {
            solution.setText( ((TextArea) vs_solution.getActiveView()).getText().toString());
        }
        methods.setSolution(solution);
        super.onPause();
    }

    //******************************************************* onclick fragment *******************************************************//
    /*
     * changes the fragment to display the clicked view
     * changes the button arrays color and the view switcher
     */
    public void activateView(int new_index) {
        if(new_index >= b_solution.length) {
            return;
        }

        //action
        if(current_solution == 0) {
            solution.setText( ((TextArea) vs_solution.getActiveView()).getText().toString());
        }
        if(new_index == 1) {
            if(current_solution == new_index) {
                openImagePickerDialog();
            }
            else {
                b_solution[new_index].setImageDrawable( context.getResources().getDrawable(R.drawable.symbol_grades_add));
            }
        }
        else {
            b_solution[1].setImageDrawable( context.getResources().getDrawable(R.drawable.symbol_homework_image));
        }

        switch(new_index) {
            case 0: activateTextView(); break;
            case 1: activateImagesView(); break;
            case 2: activateDocsView(); break;
            case 3: activateSheetsView(); break;
            case 4: activateSlidesView(); break;
        }

        //color
        changeButtonColors(R.color.radio_button_inactive, R.color.colorPrimary);
        current_solution = new_index;

        int bg = 0, fg = 0;
        switch(current_solution) {
            case 0: bg = R.color.homework_solution_text_light; fg = R.color.homework_solution_text; break;
            case 1: bg = R.color.homework_solution_images_light; fg = R.color.homework_solution_images; break;
            case 2: bg = R.color.homework_solution_docs_light; fg = R.color.homework_solution_docs; break;
            case 3: bg = R.color.homework_solution_sheets_light; fg = R.color.homework_solution_sheets; break;
            case 4: bg = R.color.homework_solution_slides_light; fg = R.color.homework_solution_slides; break;
        }
        changeButtonColors(bg, fg);
        setDrawableLayerColor(
                vs_solution,
                R.drawable.solution_area,
                R.id.solution_area_border,
                context.getResources().getColor(bg)
        );
    }

    /**
     * opens a new instance of the image picker dialog
     */
    public void openImagePickerDialog() {
        new ImagePickerDialog(context, getActivity(), ImagePickerDialog.HOMEWORK_SOLUTION_IMAGE).show();
    }

    /*
     * view switcher: 0 (TextArea)
     */
    private void activateTextView() {
        vs_solution.switchToView(0);
        ((TextArea) vs_solution.getActiveView()).setText( solution.getText());
    }

    /**
     * listener, that defines what should happen if an image in the HorizontalImageListView is clicked long
     * it opens an delete dialog, where the user can decide whether the images should
     * really be deleted
     */
    ImageListView.LongItemClickListener longItemClickListener = (pos) -> {
        String question = context.getResources().getString(R.string.boolean_dialog_delete_image);
        BooleanDialog dialog = new BooleanDialog(context, question);
        dialog.setAnswerListener(new BooleanDialog.AnswerListener() {
            @Override public void onYes() {
                FileSaver.deleteImageFromUri( solution.getImages().get(pos));
                solution.getImages().remove(pos);
                activateImagesView();
            }
            @Override public void onNo() { }
        });
        dialog.show();
    };

    /*
     * view switcher: 1 (ListView)
     * creates a list of images
     */
    private void activateImagesView() {
        vs_solution.switchToView(1);
        ImageListView listview = (ImageListView) vs_solution.getActiveView();
        listview.setItemLongClickListener(longItemClickListener);
        listview.setImageList(solution.getImages());
    }

    /*
     * view switcher: 2 (ListView)
     * creates a list of links
     */
    private void activateDocsView() {
        vs_solution.switchToView(2);
        ListView listview = (ListView) vs_solution.getActiveView();

        ArrayList<String> links = (ArrayList<String>) solution.getDocs().clone();
        links.add(0, null);
        listview.setAdapter(new Homework_solution_file_listview_adapter(
                context,
                R.id.file_listview_solution_homework,
                1,
                R.drawable.symbol_homework_docs,
                R.color.homework_solution_docs,
                links
        ));
    }

    /*
     * view switcher: 2 (ListView)
     * creates a list of links
     */
    private void activateSheetsView() {
        vs_solution.switchToView(2);
        ListView listview = (ListView) vs_solution.getActiveView();

        ArrayList<String> links = (ArrayList<String>) solution.getSheets().clone();
        links.add(0, null);
        listview.setAdapter(new Homework_solution_file_listview_adapter(
                context,
                R.id.file_listview_solution_homework,
                2,
                R.drawable.symbol_homework_table,
                R.color.homework_solution_sheets,
                links
        ));
    }

    /*
     * view switcher: 2 (ListView)
     * creates a list of links
     */
    private void activateSlidesView() {
        vs_solution.switchToView(2);
        ListView listview = (ListView) vs_solution.getActiveView();

        ArrayList<String> links = (ArrayList<String>) solution.getSlides().clone();
        links.add(0, null);
        listview.setAdapter(new Homework_solution_file_listview_adapter(
                context,
                R.id.file_listview_solution_homework,
                3,
                R.drawable.symbol_homework_slides,
                R.color.homework_solution_slides,
                links
        ));
    }

    /*
     * changes the state button to the opposite state
     */
    public void changeState() {
        solution.setState( !solution.isFinished());

        int id;
        if(solution.isFinished()) {
            id = R.drawable.symbol_homework_solution_true;
        }
        else {
            id = R.drawable.symbol_homework_solution_false;
        }

        b_finished.setImageResource(id);
        methods.setState(solution.isFinished());
    }

    //******************************************************* onclick listview *******************************************************//

    /*
     * onclick for listview
     */
    private void onclick_list(int i) {
        if(current_solution == 1) {
            if(i == 0) {
                pickImage();
            }
            else {
                enlargePicture(i-1);
            }
        }
        else {
            if(i == 0) {
                openDialog(current_solution-2);
            }
            else {
                openFile(i-1);
            }
        }
    }

    /*
     * opens a new activity to let the user select a picture or take one
     */
    private void pickImage() {
        new ImagePickerDialog(context, getActivity(), ImagePickerDialog.HOMEWORK_SOLUTION_IMAGE).show();
    }

    /*
     * adds an image to the solution
     * called from homework_activity, as the image result have to be returned to activity
     */
    public void addImage(Uri uri) {
        solution.addImage(uri);
        activateImagesView();
    }

    /*
     * shows the clicked image larger and able for zooming
     */
    private void enlargePicture(int i) {

    }

    /*
     * opens a dialog in which the user can insert the name (or link) for the file
     * @param 'kind': defines the type of file that will be created
     * 0: docs
     * 1: sheets
     * 2: slides
     */
    private void openDialog(int kind) {
        Homework_solution_dialog dialog = new Homework_solution_dialog(context, kind);
        dialog.setOnResultListener(this::addLink);
        dialog.show();
    }

    /*
     * add the transmitted link to the current solution
     */
    private void addLink(String link) {
        switch(current_solution-2) {
            case 0: solution.addDoc(link, 0); break;
            case 1: solution.addSheet(link, 0); break;
            case 2: solution.addSlide(link, 0); break;
        }
        activateView(current_solution);
    }

    /*
     * opens the google clicked file
     */
    private void openFile(int i) {
        String link = "error";
        switch (current_solution-2) {
            case 0: link = solution.getDocs().get(i); break;
            case 1: link = solution.getSheets().get(i); break;
            case 2: link = solution.getSlides().get(i); break;
        }
        Intent toDriveFile = new Intent(Intent.ACTION_VIEW);

        try {
            toDriveFile.setData(Uri.parse(link));
        }
        catch(ParseException e) {
            Toast.makeText(context, "no valid link", Toast.LENGTH_LONG).show();
        }

        startActivity(toDriveFile);
    }

    //******************************************************* methods *******************************************************//

    /*
     * changes both colors of a button (background and drawable color)
     * @param 'background': bcakground color
     * @param 'foreground': color of the drawable
     */
    private void changeButtonColors(int background, int foregound) {
        //background
        setDrawableLayerColor(
                b_solution[current_solution],
                R.drawable.solution_button,
                R.id.solution_button_background,
                context.getResources().getColor(background)
        );

        //foreground
        Drawable drawable = b_solution[current_solution].getDrawable().mutate();
        drawable.setColorFilter( context.getResources().getColor(foregound), PorterDuff.Mode.SRC_ATOP);
        b_solution[current_solution].setImageDrawable(drawable);
    }

    /*
     * changes the color of the transferred drawable layer id
     */
    private void setDrawableLayerColor(View view, int drawable_id, int layer_id, int color) {
        LayerDrawable drawable = (LayerDrawable) getResources().getDrawable(drawable_id);
        GradientDrawable border = (GradientDrawable) drawable.findDrawableByLayerId(layer_id);
        border.setColor(color);
        view.setBackground(drawable);
    }

    //Setter
    public void setSolution(HomeworkSolution solution) { this.solution = solution; }
    public void setLightColor(int light_color) { this.light_color = light_color; }
    public void setDarkColor(int dark_color) { this.dark_color = dark_color; }

    //******************************************************* callback *******************************************************//
    public CallbackMethods methods;
    public void setCallbackMethods(CallbackMethods methods) { this.methods = methods; }
    public interface CallbackMethods {
        void setSolution(HomeworkSolution solution);
        void setState(boolean isfinished);
    }
}