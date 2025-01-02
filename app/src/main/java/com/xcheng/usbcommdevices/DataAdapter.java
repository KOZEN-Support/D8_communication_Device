package com.xcheng.usbcommdevices;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<String> mList;
    private Context context;

    public DataAdapter(List<String> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.data_item, null);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.tvData.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void update(String tip) {
        mList.add(tip);
        notifyDataSetChanged();
    }


    public static class DataViewHolder extends RecyclerView.ViewHolder {

        public TextView tvData;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_data);
        }
    }
}
