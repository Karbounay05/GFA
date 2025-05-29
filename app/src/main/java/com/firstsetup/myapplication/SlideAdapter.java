package com.firstsetup.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

    private List<SlideItem> slideItems;
    private ViewPager2 viewPager2;

    // Nested SlideItem class
    public static class SlideItem {
        private final String text;
        private final String title;
        private final int imageResId;
        private final int imageResId2;
        private final int buttonResId;

        public SlideItem(String text,String title, int imageResId, int imageResId2, int buttonResId) {
            this.text = text;
            this.title = title;
            this.imageResId = imageResId;
            this.imageResId2 = imageResId2;
            this.buttonResId = buttonResId;
        }

        public String getText() {
            return text;
        }

        public String getTitle() {
            return title;
        }

        public int getImageResId() {
            return imageResId;
        }

        public int getImageResId2() {
            return imageResId2;
        }

        public int getButtonResId() {
            return buttonResId;
        }
    }

    // ✅ Fixed: assign viewPager2 in constructor
    public SlideAdapter(List<SlideItem> slideItems, ViewPager2 viewPager2) {
        this.slideItems = slideItems;
        this.viewPager2 = viewPager2;
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

        // Set text and title to their respective TextViews
        holder.textView.setText(item.getText());
        holder.titleView.setText(item.getTitle());
        holder.imageView.setImageResource(item.getImageResId());
        holder.imageView2.setImageResource(item.getImageResId2());

        if (position == slideItems.size() - 1) {
            // Last slide
            holder.button.setText("Finish");
            holder.button.setOnClickListener(v -> {
                android.content.Context context = v.getContext();
                android.content.Intent intent = new android.content.Intent(context, LoginActivity.class);
                context.startActivity(intent);
            });
        } else {
            // All other slides
            holder.button.setText("Next");
            holder.button.setOnClickListener(v -> {
                int nextItem = holder.getAdapterPosition() + 1;
                if (nextItem < slideItems.size()) {
                    viewPager2.setCurrentItem(nextItem);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return slideItems.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView titleView;
        ImageView imageView;
        ImageView imageView2;
        Button button;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.slideText);
            titleView = itemView.findViewById(R.id.slideTitle);
            imageView = itemView.findViewById(R.id.slideImage);
            imageView2 = itemView.findViewById(R.id.slideImage2);
            button = itemView.findViewById(R.id.slideButton);
        }
    }
}
