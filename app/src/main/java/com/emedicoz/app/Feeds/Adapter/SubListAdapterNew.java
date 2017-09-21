package com.emedicoz.app.Feeds.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 08/03/17.
 */

public class SubListAdapterNew extends RecyclerView.Adapter<SubListAdapterNew.ViewHolder> {

    private ArrayList<Tags> ListTitle;
    Context context;


    public SubListAdapterNew(ArrayList<Tags> ListTitle, Context context) {
        this.ListTitle = ListTitle;
        this.context = context;
    }

    public void OnsubTextClick(Tags tag) {

    }

    public void OnViewBtnClick(boolean is) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Tags subListText = (Tags) ListTitle.get(position);
        holder.listTV.setTag(subListText);
        holder.viewBtn.setTag(subListText);

        /*if (getItemCount() < 6) {
            holder.listTV.setVisibility(View.VISIBLE);
            holder.viewBtn.setVisibility(View.GONE);
            holder.listTV.setText(subListText.getText());
        } else if (position == getItemCount() - 1) {
            holder.viewBtn.setVisibility(View.VISIBLE);
            holder.listTV.setVisibility(View.GONE);
            holder.viewBtn.setText(subListText.getText());
        } else {*/
        holder.viewBtn.setVisibility(View.GONE);
        holder.listTV.setVisibility(View.VISIBLE);
        holder.listTV.setText(subListText.getText());
//        }
        holder.listTV.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 OnsubTextClick((Tags) view.getTag());
                                             }
                                         }
        );
        /*holder.viewBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  OnViewBtnClick((((Tags) view.getTag()).getText().equals(Const.VIEWMORE) ? true : false));
                                              }
                                          }
        );*/
    }

    @Override
    public int getItemCount() {
        return ListTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTV, viewBtn;

        public ViewHolder(final View view) {
            super(view);
            listTV = (TextView) view.findViewById(R.id.listItem);
            viewBtn = (TextView) view.findViewById(R.id.viewBtn);
        }
    }
}

