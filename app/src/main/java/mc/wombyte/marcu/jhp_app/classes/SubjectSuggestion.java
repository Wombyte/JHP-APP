package mc.wombyte.marcu.jhp_app.classes;

import android.content.Context;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 14.08.2018.
 */
public class SubjectSuggestion {
    String name = "";
    int color = 0;

    public SubjectSuggestion(String name, int color) {
        this.name = name;
        this.color = color;
    }

    /**
     * creates the SuggestionList from the name and color array resource
     * getting the arrays via the id, iterating over there size and fill
     * the result arrayList, which is returned at the end
     * @param context: to get the resource
     * @return ArrayList: suggestion list
     */
    public static ArrayList<SubjectSuggestion> getSuggestionList(Context context) {
        int[] colors = context.getResources().getIntArray(R.array.subject_suggestion_color);
        String[] names = context.getResources().getStringArray(R.array.subject_predefined_subjects);

        ArrayList<SubjectSuggestion> result = new ArrayList<>();
        for(int i = 0; i < Math.min(colors.length, names.length); i++) {
            result.add(new SubjectSuggestion(names[i], colors[i]));
        }
        return result;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setColor(int color) { this.color = color; }
    public int getColor() { return color; }
}
