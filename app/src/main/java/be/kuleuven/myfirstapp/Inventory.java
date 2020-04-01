package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Activity {

    private List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapter = new InventoryAdapter(productList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        testView();
    }
    private void testView(){

        Product product = new Product(123,"werkt het?",20);
        productList.add(product);

        product = new Product(987,"werkt het nog",10);
        productList.add(product);

        product = new Product(123,"en nu?",0);
        productList.add(product);

        adapter.notifyDataSetChanged();

    }
}
