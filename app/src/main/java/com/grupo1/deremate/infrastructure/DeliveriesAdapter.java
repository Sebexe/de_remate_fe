package com.grupo1.deremate.infrastructure;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Import TextView
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo1.deremate.R; // Import your R class
import com.grupo1.deremate.models.DeliveryDTO;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DeliveriesAdapter extends ListAdapter<DeliveryDTO, DeliveriesAdapter.DeliveryViewHolder> {

    // Date formatter (adjust format as needed)
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


    public DeliveriesAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<DeliveryDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<DeliveryDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull DeliveryDTO oldItem, @NonNull DeliveryDTO newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeliveryDTO oldItem, @NonNull DeliveryDTO newItem) {
            // Check fields you display in the list item
            return oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getDestination().equals(newItem.getDestination()) &&
                    oldItem.getId().equals(newItem.getId()); // Add more comparisons if needed
        }
    };

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the list item layout you created (e.g., list_item_delivery.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_delivery, parent, false);
        return new DeliveryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        DeliveryDTO currentDelivery = getItem(position);
        holder.bind(currentDelivery);
    }

    // --- ViewHolder Class ---
    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDeliveryId;
        private TextView textViewStatus;
        private TextView textViewDestination;
        private TextView textViewCreatedDate;
        private TextView textViewProductCount; // Example additional info


        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find views by ID from your list_item_delivery.xml
            textViewDeliveryId = itemView.findViewById(R.id.text_view_delivery_id);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
            textViewDestination = itemView.findViewById(R.id.text_view_destination);
            textViewCreatedDate = itemView.findViewById(R.id.text_view_created_date);
            textViewProductCount = itemView.findViewById(R.id.text_view_product_count);

        }

        @SuppressLint("SetTextI18n")
        public void bind(DeliveryDTO delivery) {
            textViewDeliveryId.setText("ID: " + delivery.getId());
            textViewStatus.setText("Status: " + delivery.getStatus()); // Or format the status nicely
            textViewDestination.setText("To: " + delivery.getDestination());

            if (delivery.getCreatedDate() != null) {
                textViewCreatedDate.setText("Created: " + delivery.getCreatedDate());
            } else {
                textViewCreatedDate.setText("Created: N/A");
            }

            if (delivery.getProducts() != null) {
                textViewProductCount.setText("Items: " + delivery.getProducts().size());
                textViewProductCount.setVisibility(View.VISIBLE);
            } else {
                textViewProductCount.setVisibility(View.GONE);
            }

            // Add click listeners or other interactions if needed
            // itemView.setOnClickListener(v -> { /* Handle item click */ });
        }
    }
}
