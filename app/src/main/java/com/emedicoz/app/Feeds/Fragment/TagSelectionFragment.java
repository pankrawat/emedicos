package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jikoobaruah on 10/02/16.
 */
public class TagSelectionFragment extends AbsSearchListViewFragment implements NetworkCall.MyNetworkCallBack {

    private TagAdapter mAdapter;
    private ITagSelectionListener selectionListener;
    NetworkCall networkCall;

    ArrayList<Tags> tagsArrayList;

    @Override
    public void onAttach(Activity activity) {
        tagsArrayList = new ArrayList<>();
        super.onAttach(activity);
        if (!(activity instanceof ITagSelectionListener)) {
            throw new IllegalArgumentException(activity.getLocalClassName() + " must implement ITagSelectionListener");
        }
        selectionListener = (ITagSelectionListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkCall = new NetworkCall(this, getContext());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Choose Tags");
    }

    @Override
    protected void fetchList() {
        /*  if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.TAGLIST_OFFLINE))){
        }
        else
            networkCall.NetworkAPICall(API.API_GET_TAG_LISTS, true);*/
        if (tagsArrayList.size() > 0)
            mAdapter.addAllItems(tagsArrayList);
        else
            networkCall.NetworkAPICall(API.API_GET_TAG_LISTS, true);
    }

    @Override
    protected RecyclerView.Adapter getListAdapter() {
        mAdapter = new TagAdapter();
        return mAdapter;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_tag_selection;
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_TAG_LISTS:
                return Ion.with(getContext())
                        .load(API.API_GET_TAG_LISTS + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(10 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        Log.e("tagselection", jsonstring.toString());
        switch (apitype) {
            case API.API_GET_TAG_LISTS:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONArray dataarray = jsonstring.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            Tags tag = gson.fromJson(dataarray.get(i).toString(), Tags.class);
                            tagsArrayList.add(tag);
                            i++;
                        }
                        mAdapter.addAllItems(tagsArrayList);
                    }
                } else {
                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE, getActivity().getString(R.string.exception_api_error_message)), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(getActivity(), jsonstring, Toast.LENGTH_SHORT).show();
    }

    private class TagAdapter extends AbsSearchListViewFragment.ListAdapter<Tags> {

        @Override
        protected List<Tags> getFilteredList(String query) {
            ArrayList<Tags> filteredList = new ArrayList<>();
            if (query == null || query.trim().length() == 0)
                filteredList = masterItems;
            else {
                String tagName;
                int size = masterItems.size();
                for (int i = 0; i < size; i++) {
                    tagName = masterItems.get(i).getText();
                    if (tagName.toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(masterItems.get(i));
                    }
                }
            }
            return filteredList;
        }

        @Override
        protected RecyclerView.ViewHolder createViewholder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_tag, parent, false);

            return new ViewHolder(v);
        }

        @Override
        protected void bindViewholder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder tagHolder = (ViewHolder) holder;
            tagHolder.settag(getItem(position));
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView nameTV;
            private Tags tag;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTV = (TextView) itemView.findViewById(R.id.nameTV);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectionListener != null && tag != null)
                            selectionListener.ontagSelected(tag);
                        TagSelectionFragment.this.dismiss();
                    }
                });
            }

            public void settag(Tags tags) {
                this.tag = tags;

                if (query != null && query.trim().length() > 0) {
                    Spannable wordtoSpan = new SpannableString(tags.getText());
                    int spanStartIndex = tags.getText().toLowerCase().indexOf(query.toLowerCase());
                    if (spanStartIndex >= 0) {
                        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), spanStartIndex, spanStartIndex + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), spanStartIndex, spanStartIndex + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    nameTV.setText(wordtoSpan);
                } else {
                    nameTV.setText(tags.getText());
                }
            }
        }
    }

    public interface ITagSelectionListener {
        void ontagSelected(Tags tag);
    }
}
