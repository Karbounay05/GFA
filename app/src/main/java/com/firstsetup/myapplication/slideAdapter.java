package com.firstsetup.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class slideAdapter extends RecyclerView.Adapter<slideAdapter.SlideViewHolder> {

    // SlideItem class moved inside SlideAdapter
    public static class SlideItem {
        private String text;
        private int imageResId;
        private int imageResId2;

        public SlideItem(String text, int imageResId, int imageResId2) {
            this.text = text;
            this.imageResId = imageResId;
            this.imageResId2 = imageResId2;
        }

        public String getText() {
            return text;
        }

        public int getImageResId() {
            return imageResId;
        }
        public int getImageResId2() {
            return imageResId2;
        }
    }

    private List<SlideItem> slideItems;

    public slideAdapter(List<SlideItem> slideItems) {
        this.slideItems = slideItems;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.introduction_activity, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        SlideItem item = slideItems.get(position);
        holder.textView.setText(item.getText());
        holder.imageView.setImageResource(item.getImageResId());
        holder.imageView2.setImageResource(item.getImageResId2());  // Set second image resource here

        // Set image resource here
    }

    @Override
    public int getItemCount() {
        return slideItems.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ImageView imageView2;


        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.slideText);
            imageView = itemView.findViewById(R.id.slideImage);
            imageView2 = itemView.findViewById(R.id.slideImage2);
        }
    }
}
