package com.android.mig.popularmovie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private ArrayList<String> mReviewsArray;

    public ReviewsAdapter(){
        mReviewsArray = new ArrayList<>();
    }

    public void setReviewsData(ArrayList<String> reviewsArray){
        mReviewsArray = reviewsArray;
        notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        String review = mReviewsArray.get(position);
        holder.mReviewLabel.setText(holder.mReviewLabel.getText() + " " + String.valueOf(position + 1));
        holder.mReviewText.setText(review);
    }

    @Override
    public int getItemCount() {
        return mReviewsArray.size();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView mReviewText;
        private TextView mReviewLabel;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewLabel = (TextView) itemView.findViewById(R.id.tv_review_label);
            mReviewText = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}
