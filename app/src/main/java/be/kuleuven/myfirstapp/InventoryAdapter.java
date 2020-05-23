package be.kuleuven.myfirstapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {
    private List<Product> productList;
    private RecyclerView myRecyclerView;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView productName;
        private TextView barcode;
        private TextView quantity;
        private TextView price;
        private RelativeLayout parentLayout;


        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.name);
            barcode = (TextView) view.findViewById(R.id.barcode);
            quantity = (TextView) view.findViewById(R.id.quantity);
            price = (TextView) view.findViewById(R.id.price);
            parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);

        }
    }
    public InventoryAdapter(List<Product> myDataset, Context context) {
        productList = myDataset;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.barcode.setText(String.valueOf(product.getBarcode()));
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        holder.price.setText(String.valueOf(product.getPrice()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InventoryClick.class);
                intent.putExtra("picture", product.getPicture());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        myRecyclerView = recyclerView;
    }

}
