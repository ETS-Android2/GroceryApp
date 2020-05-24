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

        public static class MyViewHolder extends RecyclerView.ViewHolder {

            public View viewForeground;
            private Button plus;
            private Button min;
            private TextView quantity;
            private TextView barcode;
            private TextView productName;

            public MyViewHolder(View view) {
                super(view);
                viewForeground = (View) view.findViewById(R.id.view_foreground);
                plus = (Button) view.findViewById(R.id.plus);
                min = (Button) view.findViewById(R.id.min);
                quantity = (TextView) view.findViewById(R.id.quantity1);
                barcode = (TextView) view.findViewById(R.id.barcode1);
                productName = (TextView) view.findViewById(R.id.name1);
            }
        }
        public ScannedProductsAdapter(List<Product> updatedProducts, List<Product> newProducts, List<Product> newInventoryProducts) {
            this.updatedProducts = updatedProducts;
            this.newProducts = newProducts;
            this.newInventoryProducts = newInventoryProducts;
        }

         public void removeItem(int position) {
            //TODO andere lijsten
             updatedProducts.remove(position);
             notifyItemRemoved(position);
             notifyItemRangeChanged(position, updatedProducts.size());
    }

        @NonNull
        @Override
        public ScannedProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scanned_products_row, parent, false);
            return new ScannedProductsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ScannedProductsAdapter.MyViewHolder holder, final int position) {
            Product product;
            System.out.println(getItemCount() + ":" + updatedProducts.size() +":" + newInventoryProducts.size() +":" + newProducts.size() + ":" +position);

            if (position<updatedProducts.size()&&updatedProducts.size()!=0){
                product = updatedProducts.get(position);
            }else {
                if (position>=updatedProducts.size()&&position<(newInventoryProducts.size()+updatedProducts.size())){
                    product = newInventoryProducts.get(position-updatedProducts.size());
                }else product = newProducts.get(position-updatedProducts.size()-newInventoryProducts.size());
                System.out.println(product.getName() + ":" + product.getBarcode()+":"+product.getPicture()+":"+product.getBrand());
            }
            System.out.println(product.getName());
                holder.productName.setText(product.getName());
                holder.barcode.setText(String.valueOf(product.getBarcode()));
                holder.quantity.setText(String.valueOf(product.getQuantity()));

            final Product finalProduct = product;
            holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalProduct.setQuantityPlus();
                        notifyItemChanged(position);
                    }
                });

            final Product finalProduct1 = product;
            holder.min.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalProduct1.setQuantityMin();
                        notifyItemChanged(position);
                    }
                });
        }

        @Override
        public int getItemCount() {
            return (updatedProducts.size()+newInventoryProducts.size()+newProducts.size());
        }
    }