package mc.wombyte.marcu.jhp_app;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mc.wombyte.marcu.jhp_app.Classes.SubjectSuggestion;

/**
 * Created by marcus on 13.08.2018.
 */
public class Subjects_suggestion_listview_adapter extends ArrayAdapter {

    private Context context;
    private ViewHolder holder;
    private ArrayList<SubjectSuggestion> list;
    private List<SubjectSuggestion> suggestions;

    public Subjects_suggestion_listview_adapter(Context context, int resource, ArrayList<SubjectSuggestion> list) {
        super(context, resource, list);
        this.list = list;
        this.context = context;
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////// Override methods ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        SubjectSuggestion p = getItem(position);

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.subjects_suggestion_listview_fragment, null);

            holder = new ViewHolder();
            holder.sym_color = view.findViewById(R.id.sym_color_subject_suggestion_listview);
            holder.tv_name = view.findViewById(R.id.tv_name_subject_suggestion_listview);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if(p != null) {
            changeBackgroundColor(holder.sym_color, p.getColor());
            holder.tv_name.setText(p.getName());
        }

        return view;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.d("SuggestionList", "perform Filtering is called");
                FilterResults results = new FilterResults();
                suggestions = new ArrayList<>();
                ArrayList<SubjectSuggestion> allItems = SubjectSuggestion.getSuggestionList(context);

                if(charSequence == null || charSequence.length() == 0) {
                    suggestions.addAll(allItems);
                }
                else {
                    String pattern = charSequence.toString().toLowerCase();
                    Log.d("SuggestionList", "perform Filtering: list size " + allItems.size());
                    for(SubjectSuggestion item : allItems) {
                        Log.d("SuggestionList", "perform Filtering: " + item.getName().toLowerCase() + " starts with " + pattern);
                        if(item.getName().toLowerCase().startsWith(pattern)) {
                            suggestions.add(item);
                        }
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clear();
                addAll((List) filterResults.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((SubjectSuggestion) resultValue).getName();
            }
        };
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SubjectSuggestion getItem(int position) {
        return list.get(position);
    }

    /**
     * returns the SubjectSuggestion of the current suggestion list on the
     * transmitted position (important, as onItemClickListener depends on
     * this suggestion list
     * @param position: position of the suggestion list
     * @return SubjectSuggestion: item on the transmitted position
     */
    public SubjectSuggestion getSuggestionItem(int position) {
        return suggestions.get(position);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// methods ////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * changes the background color of a drawable only bounded to <shape></shape>
     * @param v: view
     * @param color: new color for the background
     */
    private void changeBackgroundColor(View v, int color) {
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(color);
    }


    /*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////// subclasses //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public class ViewHolder {
        private ImageView sym_color;
        private TextView tv_name;
    }

}
