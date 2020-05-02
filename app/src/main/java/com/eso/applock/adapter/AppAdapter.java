package com.eso.applock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eso.applock.R;
import com.eso.applock.interfc.AppOnClickListener;
import com.eso.applock.model.AppItem;
import com.eso.applock.utils.Utils;

import java.util.List;


public class AppAdapter extends RecyclerView.Adapter<AppAdapter.myViewHolder> {

    private List<AppItem> appItemList;
    private Context context;
    private Utils utils;
    public AppAdapter(List<AppItem> appItemList) {
        this.appItemList = appItemList;
        this.utils = new Utils(context);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_apps, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        AppItem model = appItemList.get(position);
        holder.mNameTvApp.setText(model.getName());
        holder.mIconIvApp.setImageDrawable(model.getIcon());
        String pk = model.getPackageName();
        if (utils.isLock(pk)){
            holder.mLockIvApp.setImageResource(R.drawable.ic_lock_outline_black_24dp);
        }else {
            holder.mLockIvApp.setImageResource(R.drawable.ic_lock_open_black_24dp);
        }
        holder.setListener(new AppOnClickListener() {
            @Override
            public void selectApp(int pos) {
                if (utils.isLock(pk)){
                    holder.mLockIvApp.setImageResource(R.drawable.ic_lock_open_black_24dp);
                    utils.unlock(pk);
                }else {
                    holder.mLockIvApp.setImageResource(R.drawable.ic_lock_outline_black_24dp);
                    utils.lock(pk);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (appItemList == null) return 0;
        return appItemList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIconIvApp,mLockIvApp;
        private TextView mNameTvApp;
        private AppOnClickListener listener;

        public void setListener(AppOnClickListener listener) {
            this.listener = listener;
        }

        myViewHolder(@NonNull View itemView) {
            super(itemView);
            mIconIvApp = itemView.findViewById(R.id.iconIv_App);
            mNameTvApp = itemView.findViewById(R.id.nameTv_App);
            mLockIvApp = itemView.findViewById(R.id.lockIv_App);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.selectApp(getAdapterPosition());
                }
            });
        }
    }
}
