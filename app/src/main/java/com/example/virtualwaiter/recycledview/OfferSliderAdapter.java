package com.example.virtualwaiter.recycledview;

import com.example.virtualwaiter.R;
import com.example.virtualwaiter.datatypes.OfferItem;

public class OfferSliderAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OfferSliderAdapter.OfferItemViewHolder>{
    public java.util.ArrayList<OfferItem> offerItems;
    public OfferSliderAdapter(java.util.ArrayList<OfferItem> offerItems){
        this.offerItems = offerItems;
    }
    @Override
    public OfferItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item, parent, false);
        OfferItemViewHolder offerItemViewHolder = new OfferItemViewHolder(view);
        return offerItemViewHolder;
    }
    @Override
    public void onBindViewHolder(OfferItemViewHolder holder, int position) {
        OfferItem offerItem = offerItems.get(position);
        holder.offerImage.setImageResource(offerItem.getImage());
    }
    @Override
    public int getItemCount() {
        return offerItems.size();
    }

    public static class OfferItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public android.widget.ImageView offerImage;
        public OfferItemViewHolder(android.view.View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offerImage);
        }
    }
}