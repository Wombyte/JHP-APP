package mc.wombyte.marcu.jhp_app;

import android.app.Dialog;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by marcus on 09.06.2018.
 */
public class Homework_solution_dialog extends Dialog{

    Context context;
    int kind;

    TextView tv_heading;
    TextView tv_description;
    EditText ed_text;
    Button b_ok;
    Button b_cancel;

    boolean is_link = false;
    String link_base;
    String[] link_kinds;

    /*
     * Constructor for the Homework_solution_dialog
     * @param 'kind': defines the kind of file (0: doc, 1: sheets, 2: slides)
     */
    public Homework_solution_dialog(Context context, int kind) {
        super(context);
        this.context = context;
        this.kind = kind;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.homework_solution_dialog);

        //vars
        link_base = context.getResources().getString(R.string.homework_solution_dialog_link_base);
        link_kinds = context.getResources().getStringArray(R.array.homework_solution_dialog_link_kind);

        //initialization
        tv_heading = findViewById(R.id.tv_heading_homework_solution_dialog);
        tv_description = findViewById(R.id.tv_description_homework_solution_dialog);
        ed_text = findViewById(R.id.ed_text_homework_solution_dialog);
        b_ok = findViewById(R.id.b_ok_homework_solution_dialog);
        b_cancel = findViewById(R.id.b_cancel_homework_solution_dialog);

        //text
        tv_heading.setText( context.getResources().getStringArray(R.array.homework_solution_dialog_headings)[kind]);
        tv_description.setText( context.getResources().getStringArray(R.array.homework_solution_dialog_description)[kind]);

        String clipboard = getLastClipboardEntry();
        if(isValidLink(clipboard)) {
            ed_text.setText(clipboard);
            ed_text.selectAll();
            is_link = true;
            b_ok.setText(R.string.homework_solution_dialog_take);
            b_ok.setEnabled(true);
            b_ok.setTextColor( context.getResources().getColor(R.color.first_use_next));
        }

        //listener
        b_ok.setOnClickListener((v) -> onclick_ok());
        b_cancel.setOnClickListener((v) -> onclick_cancel());
        ed_text.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_link = checkForValidDriveLink(charSequence.toString());
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
    }

    //******************************************************* listener *******************************************************//

    /*
     * onclick for b_ok
     * if text = name: creates a new doc
     * if text = link: converts the link into DriveId
     * sends the DriveId back to the fragment
     */
    private void onclick_ok() {
        String text = ed_text.getText().toString();

        //if(!is_link) id = createFile(name);

        if(listener != null) {
            listener.onResult(text);
        }
        dismiss();
    }

    /*
     * onclick for b_cancel
     * closes the dialog
     */
    private void onclick_cancel() {
        dismiss();
    }

    /*
     * textlistener for ed_text
     * checks whether the parsed text is a valid google drive link
     */
    private boolean checkForValidDriveLink(String link) {
        is_link = isValidLink(link);

        //set button text
        if(is_link) {
            //b_ok.setText(R.string.homework_solution_dialog_take);
            b_ok.setEnabled(true);
            b_ok.setTextColor( context.getResources().getColor(R.color.first_use_next));
        }
        else {
            //b_ok.setText(R.string.homework_solution_dialog_create);
            b_ok.setEnabled(false);
            b_ok.setTextColor( context.getResources().getColor(R.color.first_use_next_disabled));
        }

        return is_link;
    }

    //******************************************************* methods *******************************************************//
    /*
     * checks whether the parsed text is a valid google drive link
     */
    private boolean isValidLink(String link) {
        if(!link.startsWith(link_base)) {
            return false;
        }

        link = link.substring(24);
        String link_kind = link.substring(0, link.indexOf("/"));

        return link_kind.equals(link_kinds[kind]);
    }

    /*
     * reads the last entry of the clipboard to paste a (perhaps) copied link
     */
    private String getLastClipboardEntry() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    /*
     * creates a new file (depending on the the kind) with the transferred name
     */
    private void createFile(String name) {

    }

    //******************************************************* Drive API *******************************************************//
    //from: https://www.numetriclabz.com/integrate-google-drive-in-android-tutorial/


    //******************************************************* callback *******************************************************//
    public OnResultListener listener = null;
    public interface OnResultListener {
        void onResult(String link);
    }
    public void setOnResultListener(OnResultListener listener) { this.listener = listener; }
}
