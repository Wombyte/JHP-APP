package mc.wombyte.marcu.jhp_app.reuseables;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by marcus on 04.06.2018.
 */
public class ViewHolder {
    private ArrayList<ArrayList<? extends View>> views;

    public ViewHolder() {
        views = new ArrayList<>();
    }

    //Getter
    public ArrayList<ArrayList<? extends View>> getViewList() { return views; }
    public ArrayList<? extends View> getViewList(int i) { return views.get(i); }
    public <T extends View> T getView(int i, int j) { return (T) views.get(i).get(j); }

    //Adder
    public void addViewList(ArrayList<? extends View> list) { views.add(list); }
    public <T extends View> void addView(T view, int i) {
        ((ArrayList<T>) views.get(i)).add(view);
    }
}
