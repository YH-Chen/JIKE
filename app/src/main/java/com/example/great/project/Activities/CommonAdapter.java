package com.example.great.project.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by great on 2017/10/22.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder>{

    protected Context mComText;
    protected int mLayoutId;
    protected List<T> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public CommonAdapter(Context context, int id, List<T> datas){
        this.mComText = context;
        this.mLayoutId = id;
        this.mDatas = datas;
    }

    public abstract void convert(ViewHolder viewHolder, T t);

    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        ViewHolder viewHolder = ViewHolder.get(this.mComText, parent, this.mLayoutId);
        return viewHolder;
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        convert(holder, this.mDatas.get(position));
        if(this.mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonAdapter.this.mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CommonAdapter.this.mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }


    public int getItemCount(){
        return mDatas.size();
    }

    public void removeItem(int which){
        this.mDatas.remove(which);
        notifyItemRemoved(which);
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public void addItem(int index, T t){
        mDatas.add(index, t);
        notifyDataSetChanged();
    }
    public void addItem(T t){
        mDatas.add(t);
        notifyDataSetChanged();
    }
    public T getItem(int index){
        return mDatas.get(index);
    }

    public void resetList(List<T> newList){
        mDatas = newList;
        notifyDataSetChanged();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder(Context context, View itemView, ViewGroup parent){
        super(itemView);
        this.mConvertView = itemView;
        this.mViews = new SparseArray();
    }

    public static ViewHolder get(Context context, ViewGroup parent, int layoutId){
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView, parent);
        return holder;
    }

    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = this.mConvertView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }
        return (T) view;
    }
}
