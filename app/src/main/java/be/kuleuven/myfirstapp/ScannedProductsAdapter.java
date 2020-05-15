package be.kuleuven.myfirstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScannedProductsAdapter extends RecyclerView.Adapter<ScannedProductsAdapter.MyViewHolder> {
    private List<Product> updatedProducts;
    private List<Product> newProducts;
    private List<Product> newInventoryProducts;
    private List<Product> removedProducts;
        public static class MyViewHolder extends RecyclerView.ViewHolder {

            private Button plus;
            private Button min;
            private Button delete;
            private TextView quantity;
            private TextView barcode;
            private TextView productName;

            public MyViewHolder(View view) {
                super(view);
                plus = (Button) view.findViewById(R.id.plus);
                min = (Button) view.findViewById(R.id.min);
                delete = (Button) view.findViewById(R.id.remove);
                quantity = (TextView) view.findViewById(R.id.quantity1);
                barcode = (TextView) view.findViewById(R.id.barcode1);
                productName = (TextView) view.findViewById(R.id.name1);
            }
        }
        public ScannedProductsAdapter(List<Product> updatedProducts, List<Product> newProducts, List<Product> newInventoryProducts, List<Product> removedProducts) {
            this.updatedProducts = updatedProducts;
            this.newProducts = newProducts;
            this.newInventoryProducts = newInventoryProducts;
            this.removedProducts = removedProducts;
        }

        @NonNull
        @Override
        public ScannedProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scanned_products_row, parent, false);

            return new ScannedProductsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ScannedProductsAdapter.MyViewHolder holder, int position) {
            if (updatedProducts.size()>=position+1) {
                Product product = updatedProducts.get(position);
                holder.productName.setText(product.getName());
                holder.barcode.setText(String.valueOf(product.getBarcode()));
                holder.quantity.setText(String.valueOf(product.getQuantity()));
            }
            else{
                position = position-updatedProducts.size();
                Product product1 = removedProducts.get(position);
                holder.productName.setText(product1.getName());
                holder.barcode.setText(String.valueOf(product1.getBarcode()));
                holder.quantity.setText(String.valueOf(product1.getQuantity()));
            }


        }

        @Override
        public int getItemCount() {
            return updatedProducts.size()+removedProducts.size();
        }
    }