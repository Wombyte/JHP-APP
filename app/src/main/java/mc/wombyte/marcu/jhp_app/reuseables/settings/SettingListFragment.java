package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import mc.wombyte.marcu.jhp_app.R;

/**
 * Created by marcus on 21.08.2018.
 */
public class SettingListFragment extends Fragment {

    private SettingGroup group;
    private SettingGroup root;

    private SettingActivity parentActivity;

    public SettingListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        Context context = container.getContext();
        parentActivity = (SettingActivity) getActivity();

        ListView list = view.findViewById(R.id.listview_settings);
        list.setAdapter(new SettingListViewAdapter(context, root, group.getChildPaths()));
        list.setOnItemClickListener(((adapterView, view1, i, l) -> onItemClick(i)));

        return view;
    }

    /**
     * calls {@link SettingActivity#openSetting(int)} for the clicked setting
     * @param i: index of the setting in the current group
     */
    private void onItemClick(int i) {
        parentActivity.openSetting(i);
    }

    /**
     * handling {@link this#root}: Getter & Setter
     */
    public void setRootElement(SettingGroup root) { this.root = root; }
    public SettingGroup getRootElement() { return root; }

    /**
     * handling {@link this#group}: Getter & Setter
     */
    public SettingListFragment setSettingGroup(SettingGroup root, SettingGroup group) {
        this.root = root;
        this.group = group;
        return this;
    }
    public SettingGroup getSettingGroup() { return group; }

}
