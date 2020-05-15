package be.kuleuven.myfirstapp;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScannedProducts extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private List<Product> newProducts = new ArrayList<>();
    private List<Product> updatedProducts = new ArrayList<>();
    private List<Product> inventoryProducts = new ArrayList<>();

    private RecyclerView recyclerView;
    private ScannedProductsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Intent intent = getIntent();
        updatedProducts = intent.getParcelableArrayListExtra("update");
        newProducts = intent.getParcelableArrayListExtra("new");
        inventoryProducts = intent.getParcelableArrayListExtra("newInventory");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        adapter = new ScannedProductsAdapter(updatedProducts,newProducts,inventoryProducts);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ScannedProductsAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = updatedProducts.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Product deletedItem = updatedProducts.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

        }

    }
}
