package mc.wombyte.marcu.jhp_app.reuseables;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcu on 18.07.2017.
 */

public class BooleanDialog extends Dialog {

    private AnswerListener listener;

    private TextView tv_question;
    private Button b_yes;
    private Button b_no;

    public BooleanDialog(Context context, String question) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.boolean_dialog);

        tv_question = findViewById(R.id.tv_question_boolean_dialog);
        b_yes = findViewById(R.id.b_yes_boolean_dialog);
        b_no = findViewById(R.id.b_no_boolean_dialog);

        tv_question.setText(question);

        b_yes.setOnClickListener((view) -> {
            listener.onYes();
            dismiss();
        });
        b_no.setOnClickListener((view) -> {
            listener.onNo();
            dismiss();
        });
    }

    public interface AnswerListener {
        void onYes();
        void onNo();
    }
    public void setAnswerListener(AnswerListener listener) { this.listener = listener; }
}
