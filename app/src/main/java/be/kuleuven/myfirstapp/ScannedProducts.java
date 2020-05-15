package be.kuleuven.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScannedProducts extends AppCompatActivity {
    private List<Product> newProducts = new ArrayList<>();
    private List<Product> updatedProducts = new ArrayList<>();
    private List<Product> removedProducts = new ArrayList<>();
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
        removedProducts = intent.getParcelableArrayListExtra("remove");
        inventoryProducts = intent.getParcelableArrayListExtra("newInventory");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapter = new ScannedProductsAdapter(updatedProducts,newProducts,inventoryProducts,removedProducts);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        testView();
    }
    private void testView(){

        Product product = new Product(123,"werkt het?",20.0);
        newProducts.add(product);

        product = new Product(987,"werkt het nog",10.0);
        newProducts.add(product);

        product = new Product(123,"en nu?",0.0);
        newProducts.add(product);

        adapter.notifyDataSetChanged();

    }

}
