package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GrocerySearchActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/test";
    private static final String SUBMIT_URL = "https://studev.groept.be/api/a19sd303/test2/";

    SearchView mySearchView;
    ListView myListView;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_search);


        mySearchView =findViewById(R.id.searchView);
        myListView= findViewById(R.id.lstView);

        list=new ArrayList<>();
        setMenuItem();


        adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        myListView.setAdapter(adapter);

        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (myListView.getItemAtPosition(position).toString());
                System.out.println(selectedFromList);
                post(selectedFromList.split(" ")[0]);
            }
        });
    }

    public void setMenuItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, QUEUE_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                          // lstSource = new String[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                list.add(i, product.get("idtest") + " " + product.get("eerste") + "\n");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }
    private void post(final String value) {
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, SUBMIT_URL + value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GrocerySearchActivity.this, "Order placed", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GrocerySearchActivity.this, "Unable to place the order", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}
