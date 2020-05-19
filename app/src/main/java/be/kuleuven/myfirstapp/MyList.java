package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Lijst naam wijzigen
//TODO: als back knop wordt gedrukt,wordt mylist niet geupdate.
//TODO: Slechte namen moeten gewijzigd worden


import java.util.ArrayList;

public class
MyList extends AppCompatActivity {
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/getAllitemstoMyList/";
    private static final String SUBMIT_URL = "https://studev.groept.be/api/a19sd303/updateMyList/";


    private int id1;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> flag = new ArrayList<>();
    ListView listView;
    ArrayAdapter arrayAdapter;
    private String listName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_my_list);
        //get userID
        Intent intent = getIntent();
        id1 = intent.getIntExtra("id", -1);
        listName = intent.getStringExtra("list_name");
        System.out.println(listName);
        System.out.println(id1);

        //setMenuItem();
        receiveData();
        listView = findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(MyList.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                TextView text = (TextView) view;
                String t;
                if ((!text.getPaint().isStrikeThruText()) && flag.get(position).equals("0")) {
                    text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    t = "1/" + idList.get(position);


                } else {
                    text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    t = "0/" + idList.get(position);
                }
                post(t);
                arrayAdapter.notifyDataSetChanged();

            }

        });
    }

    @Override
    public boolean onSupportNavigateUp() {       //back button working
        super.onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_mylist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.addProductsScanner:
                Intent intent1 = new Intent(MyList.this, Scanner.class);
                intent1.putExtra("list_name", listName);
                intent1.putExtra("id", id1);
                MyList.this.startActivity(intent1);

                break;
            case R.id.addProductsManual:
                Intent intent2 = new Intent(MyList.this, GrocerySearchActivity.class);
                intent2.putExtra("list_name", listName);
                intent2.putExtra("id", id1);
                MyList.this.startActivity(intent2);
                finish();

                break;
        }

        return true;
    }

    private void receiveData() {


        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, QUEUE_URL + listName + "/" + id1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); ++i) {
                    JSONObject product = null;
                    try {
                        product = response.getJSONObject(i);
                        if (((String) product.get("IsBought")).equals("1")) {
                            list.add(i, "--" + product.get("name") + "(x" + product.get("quantity") + ")--\n");
                        } else {
                            list.add(i, product.get("name") + "(x" + product.get("quantity") + ")\n");
                        }
                        idList.add(i, "" + product.get("id_grocerylist"));
                        flag.add(i, "" + product.get("IsBought"));

                        arrayAdapter.notifyDataSetChanged();
                        System.out.println(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyList.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(queueRequest);
    }


    private void post(final String value) {
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, SUBMIT_URL + value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MyList.this, "Order placed", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyList.this, "Unable to place the order", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}
