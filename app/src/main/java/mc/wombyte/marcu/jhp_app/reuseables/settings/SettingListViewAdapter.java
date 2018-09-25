package mc.wombyte.marcu.jhp_app.reuseables.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mc.wombyte.marcu.jhp_app.R;
import mc.wombyte.marcu.jhp_app.reuseables.ViewSwitcher;

/**
 * Created by marcus on 19.08.2018.
 */

public class SettingListViewAdapter extends ArrayAdapter<ArrayList<Integer>> {

    private SettingGroup root;
    SettingViewHolder holder;

    public SettingListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SettingListViewAdapter(Context context, SettingGroup root, ArrayList<ArrayList<Integer>> paths) {
        super(context, R.layout.settings_listview_fragment, paths);
        for(ArrayList<Integer> path : paths) {
            Log.d("ListViewAdapter", "Setting: " + "current setting: child: " + path);
        }
        this.root = root;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup container) {
        Context context = container.getContext();
        SettingStructure p = root.getChildByPath(getItem(position));

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.settings_listview_fragment, null);

            holder = new SettingViewHolder();
            holder.image = view.findViewById(R.id.image_settings_listview);
            holder.tv_description = view.findViewById(R.id.tv_description_settings_listview);
            holder.tv_long_description = view.findViewById(R.id.tv_long_description_settings_listview);
            holder.vs_setting = ((ViewSwitcher) view.findViewById(R.id.vs_view_settings_listview)).createView(context);
            holder.cover = view.findViewById(R.id.cover_settings_listview);

            view.setTag(holder);
        } else {
            holder = (SettingViewHolder) view.getTag();
        }

        //symbol
        if(p.getSymbolId() != 0) {
            holder.image.setImageResource(p.getSymbolId());
        }

        //description
        if(p.getDescriptionId() != 0) {
            holder.tv_description.setText(p.getDescriptionId());
        }

        //description
        if(p.getLongDescriptionId() != 0) {
            holder.tv_long_description.setText(p.getLongDescriptionId());
        }
        else {
            holder.tv_long_description.setHeight(0);
        }

        //value / action symbol
        if (p.isSetting()) {
            Setting setting = (Setting) p;
            View contentView = setting.getContentView(context);
            if (contentView != null) {
                holder.vs_setting.switchToView(1);
                RelativeLayout rl = (RelativeLayout) holder.vs_setting.getActiveView();
                rl.removeAllViews();
                rl.addView(contentView);

            } else {
                if (setting.getActionSymbolId() != Setting.NO_ACTION_ICON) {
                    holder.vs_setting.switchToView(0);
                    ((ImageView) holder.vs_setting.getActiveView()).setImageResource(setting.getActionSymbolId());
                }
            }
        } else {
            holder.vs_setting.switchToView(0);
            ((ImageView) holder.vs_setting.getActiveView()).setImageResource(p.getActionSymbolId());
        }

        //hover
        if(p.getOpenCondition() != null) {
            if(!p.getOpenCondition().checkCondition()) {
                holder.cover.setBackgroundColor( context.getResources().getColor(R.color.homework_cover));
            }
        }

        return view;
    }

    /**
     * ViewHolder for the SettingListView
     */
    private class SettingViewHolder {
        private ImageView image;
        private TextView tv_description;
        private TextView tv_long_description;
        private ViewSwitcher vs_setting;
        private RelativeLayout cover;
    }

}