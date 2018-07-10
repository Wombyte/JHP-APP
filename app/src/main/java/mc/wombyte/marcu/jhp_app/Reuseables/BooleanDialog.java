package mc.wombyte.marcu.jhp_app.Reuseables;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcu on 18.07.2017.
 */

public class BooleanDialog extends Dialog {

    Context context;
    AnswerListener listener;

    TextView tv_question;
    Button b_yes;
    Button b_no;

    public BooleanDialog(Context context, String question) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //disables a title bar, must be called before setContentView
        setContentView(R.layout.boolean_dialog);
        this.context = context;

        tv_question = (TextView) findViewById(R.id.tv_question_boolean_dialog);
        b_yes = (Button) findViewById(R.id.b_yes_boolean_dialog);
        b_no = (Button) findViewById(R.id.b_no_boolean_dialog);


        tv_question.setText(question);

        b_yes.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                listener.onYes();
                dismiss();
            }
        });
        b_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNo();
                dismiss();
            }
        });
    }

    public interface AnswerListener {
        void onYes();
        void onNo();
    }
    public void setAnswerListener(AnswerListener listener) { this.listener = listener; }
}
