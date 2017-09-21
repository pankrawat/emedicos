package com.emedicoz.app.Feeds.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emedicoz.app.Courses.Adapter.SubListCourseAdapter;
import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cbc-03 on 08/01/17.
 */

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private List<String> expandableListTitle;
    ArrayList<Tags> tagsList;
    ArrayList<String> CoursesSubList;

    public MyListAdapter(Context context, List<String> expandableListTitle, ArrayList<Tags> tagsList) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.tagsList = tagsList;
    }

    @Override
    public int getCount() {
        return expandableListTitle.size();
    }

    @Override
    public Object getItem(int i) {
        return expandableListTitle.get(i);
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    boolean isfeedsexpanded = true;
    boolean iscoursesexpanded = false;

    public void OnFeedsIndicatorClick(View v) {
        if (isfeedsexpanded) {
            (v.findViewById(R.id.navRV)).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("+");
            isfeedsexpanded = false;
        } else {
            (v.findViewById(R.id.navRV)).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.iconadd)).setText("-");
            sublistadapter.notifyDataSetChanged();
            isfeedsexpanded = true;
        }
    }

    public void OnCoursesIndicatorClick(View v) {
        if (iscoursesexpanded) {
            (v.findViewById(R.id.navRV)).setVisibility(View.GONE);
            iscoursesexpanded = false;
        } else {
            (v.findViewById(R.id.navRV)).setVisibility(View.VISIBLE);
            subListCourseAdapter.notifyDataSetChanged();
            iscoursesexpanded = true;
        }
    }

    public void OnTextClick(String title, int type) {
    }

    RecyclerView subRecyclerView;
    SubListAdapterNew sublistadapter;
    SubListCourseAdapter subListCourseAdapter;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String listTitle = (String) expandableListTitle.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.single_row_list_group, null);
        }
        RelativeLayout RL = (RelativeLayout) convertView.findViewById(R.id.RL1);
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        ImageView image = (ImageView) convertView.findViewById(R.id.icon);
        TextView iconadd = (TextView) convertView.findViewById(R.id.iconadd);
        listTitleTextView.setText(listTitle);

        subRecyclerView = (RecyclerView) convertView.findViewById(R.id.navRV);
        subRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (listTitle.equals(Const.FEEDS)) {
            if (tagsList == null)
                tagsList = new ArrayList<>();
            sublistadapter = new SubListAdapterNew(getTags(tagsList), context) {
                @Override
                public void OnsubTextClick(Tags tag) {
                    SharedPreference.getInstance().putString(Const.SUBTITLE, new Gson().toJson(tag, Tags.class));
                    OnTextClick(Const.FEEDS, 1);
                }
            };
            subRecyclerView.setNestedScrollingEnabled(false);
            subRecyclerView.setAdapter(sublistadapter);
            if (isfeedsexpanded) {
                convertView.findViewById(R.id.navRV).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.iconadd)).setText("-");
            }
        }
        if (listTitle.equals(Const.COURSES)) {
            if (CoursesSubList == null)
                CoursesSubList = new ArrayList<>();
            subListCourseAdapter = new SubListCourseAdapter(Helper.getcourseSubList(context), context) {
                @Override
                public void OnsubTextClick(String str) {
                    SharedPreference.getInstance().putString(Const.SUBTITLECOURSE, str);
                    OnTextClick(str, 0);
                }

            };
            subRecyclerView.setNestedScrollingEnabled(false);
            subRecyclerView.setAdapter(subListCourseAdapter);
            if (iscoursesexpanded) {
                convertView.findViewById(R.id.navRV).setVisibility(View.VISIBLE);
            }
        }


        RL.setTag(convertView);
        iconadd.setTag(convertView);

        iconadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnFeedsIndicatorClick((View) view.getTag());
            }
        });

        RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = (View) view.getTag();
                TextView tv = (TextView) v.findViewById(R.id.listTitle);
                if (tv.getText().toString().equals(Const.COURSES))
                    OnCoursesIndicatorClick(v);
                else OnTextClick(tv.getText().toString(), 0);
                SharedPreference.getInstance().putString(Const.SUBTITLE, null);
            }
        });

        iconadd.setVisibility(View.GONE);

        switch (listTitle) {
            case Const.FEEDS:
                iconadd.setVisibility(View.VISIBLE);
                image.setImageResource(R.mipmap.feed_blue);
                break;
            case Const.COURSES:
            case Const.SAVEDNOTES:
                image.setImageResource(R.mipmap.saved_notes_blue);
                break;
            case Const.FEEDBACK:
                image.setImageResource(R.mipmap.help_blue);
                break;
            case Const.RATEUS:
                image.setImageResource(R.mipmap.rate_blue);
                break;
            case Const.APPSETTING:
                image.setImageResource(R.mipmap.settings_blue);
                break;
            case Const.LOGOUT:
                image.setImageResource(R.mipmap.logout_blue);
                break;
        }

        return convertView;
    }


    public ArrayList<Tags> getTags(ArrayList<Tags> tags) {
        ArrayList<Tags> tag = new ArrayList<Tags>();
        if (tags.size() > 0) {
            int j = 0;
            /*if (!is) {
                if (tags.size() > 6) {
                    while (j < 6) {
                        tag.add(tags.get(j));
                        j++;
                    }
                    Tags tg = new Tags();
                    tg.setText(Const.VIEWMORE);
                    tag.add(tg);
                } else {
                    tag.addAll(tags);
                }
            } else {*/
            while (j < tags.size()) {
                tag.add(tags.get(j));
                j++;
            }
                /*Tags tg = new Tags();
                tg.setText(Const.VIEWLESS);
                tag.add(tg);*/

        }
        return tag;
    }
}

