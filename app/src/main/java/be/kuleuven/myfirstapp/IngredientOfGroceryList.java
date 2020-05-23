package be.kuleuven.myfirstapp;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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
import java.util.ArrayList;






public class
IngredientOfGroceryList extends AppCompatActivity {
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/getAllitemstoMyList/";
    private static final String SUBMIT_URL = "https://studev.groept.be/api/a19sd303/updateMyList/";
    private int id1;
    ArrayList<InnerList> list_object = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ListView listView;
    TextView textView;
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

        receiveData();
        listView = findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(IngredientOfGroceryList.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                textView = (TextView) view;
                String completeRequest;
                if ( list_object.get(position).ısBought.equals("1") ){
                    list.set(position,list.get(position).split("-")[0]);
                    list_object.get(position).ısBought="0";
                    completeRequest = "0/" + list_object.get(position).listId;
                    post(completeRequest);
                    Toast.makeText(IngredientOfGroceryList.this, "Item placed on the list again", Toast.LENGTH_SHORT).show();
                    System.out.println("if1");

                }else {
                    completeRequest = "1/" + list_object.get(position).listId;
                    list_object.get(position).ısBought="1";
                    list.set(position,list.get(position)+"------>bought");
                    post(completeRequest);
                    Toast.makeText(IngredientOfGroceryList.this, "Item is bought", Toast.LENGTH_SHORT).show();

                }
                arrayAdapter.notifyDataSetChanged();

            }

        });
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
                Intent intent1 = new Intent(IngredientOfGroceryList.this, Scanner.class);
                intent1.putExtra("list_name", listName);
                intent1.putExtra("id", id1);
                IngredientOfGroceryList.this.startActivity(intent1);
                this.finish();

                break;
            case R.id.addProductsManual:
                Intent intent2 = new Intent(IngredientOfGroceryList.this, GrocerySearchActivity.class);
                intent2.putExtra("list_name", listName);
                intent2.putExtra("id", id1);
                IngredientOfGroceryList.this.startActivity(intent2);
                this.finish();
                break;

            case android.R.id.home:
                    onBackPressed();
                    return true;
        }

        return true;
    }

    private void receiveData() {


        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, QUEUE_URL + listName + "/" + id1, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); ++i) {
                    JSONObject product = null;
                    InnerList listItem = new InnerList();
                    try {
                        product = response.getJSONObject(i);
                        if (((String) product.get("IsBought")).equals("1")) {
                            list.add(i,  product.get("name") + "(x" + product.get("quantity") + ") ----->(bought)\n");
                        } else {
                            list.add(i, product.get("name") + "(x" + product.get("quantity") + ")");
                        }

                        listItem.listId=(String)product.get("id_grocerylist");
                        listItem.ısBought=(String)product.get("IsBought");
                        list_object.add(i,listItem);
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
                Toast.makeText(IngredientOfGroceryList.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }

    private void post(final String value) {
        final StringRequest submitRequest = new StringRequest(Request.Method.GET, SUBMIT_URL + value, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IngredientOfGroceryList.this, "Unable to place the order", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
}
