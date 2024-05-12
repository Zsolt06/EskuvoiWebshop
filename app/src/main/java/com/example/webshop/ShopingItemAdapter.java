package com.example.webshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShopingItemAdapter extends RecyclerView.Adapter<ShopingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShopingItem> ShopingItemsData;
    private ArrayList<ShopingItem> ShopingItemsDataAll;
    private Context context;
    private int lastPosition = -1;
    private String currentUserType;

    public void setCurrentUserType(String userType) {
        this.currentUserType = userType;
        notifyDataSetChanged();
    }

    ShopingItemAdapter(Context context, ArrayList<ShopingItem> itemsData, String currentUserType) {
        this.ShopingItemsData = itemsData;
        this.ShopingItemsDataAll = itemsData;
        this.context = context;
        this.currentUserType = currentUserType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShopingItemAdapter.ViewHolder holder, int position) {
        ShopingItem currentItem = ShopingItemsData.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return ShopingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShopingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = ShopingItemsDataAll.size();
                results.values = ShopingItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ShopingItem item : ShopingItemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ShopingItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TitleText;
        private TextView DescriptionText;
        private TextView PriceText;
        private ImageView ItemImage;
        private Button deleteButton;

         ViewHolder(View itemView) {
            super(itemView);

            TitleText = itemView.findViewById(R.id.itemTitle);
            DescriptionText = itemView.findViewById(R.id.itemDescription);
            PriceText = itemView.findViewById(R.id.itemPrice);
            ItemImage = itemView.findViewById(R.id.itemImage);
            deleteButton = itemView.findViewById(R.id.delete);

             itemView.findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Toast.makeText(context, "Termék hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();

                     Log.d("Activity", "Termék hozzáadva a kosárhoz!");

                     Animation animation = AnimationUtils.loadAnimation(context, R.anim.add_to_cart);
                     itemView.startAnimation(animation);
                 }
             });

         }

        public void bindTo(ShopingItem currentItem) {
            TitleText.setText(currentItem.getName());
            DescriptionText.setText(currentItem.getDescription());
            PriceText.setText(currentItem.getPrice());
            Glide.with(context).load(currentItem.getImageResource()).into(ItemImage);

            if (currentUserType.equals("Eladó")) {
                deleteButton.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.delete).setOnClickListener(view -> ((WebshopListActivity) context).deleteItem(currentItem));
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}

