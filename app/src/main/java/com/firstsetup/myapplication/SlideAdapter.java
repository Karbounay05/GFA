package com.firstsetup.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {

    private List<String> slideTexts;

    public SlideAdapter(List<String> slideTexts) {
        this.slideTexts = slideTexts;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.textView.setText(slideTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return slideTexts.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.slideText);
        }
    }
}
