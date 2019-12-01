package com.mobica.speedlock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobica.speedlock.model.Hit;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    //    private int mSelectedPos = -1;
    private int index = -1;
    private List<Hit> mHits;
    private Context context;
    private boolean isVisible = true;

    public NewsAdapter(List<Hit> mHits, Context context) {
        this.mHits = mHits;
        this.context = context;
    }

    public void setVisible(int index, boolean isVisible) {
        this.index = index;
        this.isVisible = isVisible;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Hit model = mHits.get(position);
        holder.bind(model, position);
    }

    @Override
    public int getItemCount() {
        return mHits.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mRoot;
        TextView mTitle;
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.recyclerView_Main);
            mTitle = itemView.findViewById(R.id.textView_Item_Title);
            mImageView = itemView.findViewById(R.id.imageView_Item_Image);
        }

        public void bind(Hit data, int position) {
            mTitle.setText(data.getTitle());
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.mobica_img_logo)
                    .error(R.drawable.mobica_img_logo);
            if (data.getElements().isEmpty()) {
                Glide.with(context).load(R.mipmap.ic_launcher_round).apply(options).into(mImageView);
            } else {
                Glide.with(context).load(data.getElements().get(0).getUrl()).apply(options).into(mImageView);
            }
            mTitle.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            if (index != position && !isVisible) {
                mTitle.setAlpha(0.2f);
                mImageView.setAlpha(0.2f);
            } else {
                mTitle.setAlpha(1f);
                mImageView.setAlpha(1f);
            }

//            recycleView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    int pos = getAdapterPosition();
//                    if (mSelectedPos == -1) {           // no selection
//                        mSelectedPos = pos;
//                    } else if (mSelectedPos == pos) {   // select already selected
//                        // do nothing
//                        mSelectedPos = -1;
//                    } else {                            // select new, remove old selection
//                        int lastPos = mSelectedPos;
//                        mSelectedPos = pos;
//                        notifyItemChanged(lastPos);
//                    }
//                    notifyItemChanged(mSelectedPos);
//                }
//            });

        }
    }

}



