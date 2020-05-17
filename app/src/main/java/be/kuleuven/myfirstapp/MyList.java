package be.kuleuven.myfirstapp;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
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

public class MyList extends AppCompatActivity {
    private RequestQueue requestQueue;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/getAllitemstoMyList/";
    private static final String SUBMIT_URL = "https://studev.groept.be/api/a19sd303/test2/";
     int id1;
    ArrayList<String> list = new ArrayList<>();
    ListView list_view;
    ArrayAdapter arrayAdapter;
    private String listname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_my_list);
        //get userID
        Intent intent=getIntent();
        id1 = intent.getIntExtra("id", -1);
        listname=intent.getStringExtra("list_name");
        System.out.println(listname);
        System.out.println(id1);

       setMenuItem();
        list_view = findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(MyList.this, android.R.layout.simple_list_item_1, list);
        list_view.setAdapter(arrayAdapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {

                PopupMenu popupMenu = new PopupMenu(MyList.this, view);
                System.out.println("name "+list.get(position).toString());
                popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu_2, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {



                            case R.id.itemDel:
                                //fucntion for del
                                Toast.makeText(MyList.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                                list.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                break;
                            case R.id.lineTrough:
                                TextView text = (TextView) view;
                                if(!text.getPaint().isStrikeThruText()) {
                                    text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                }else{
                                    text.setPaintFlags(text.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                }
                                arrayAdapter.notifyDataSetChanged();

                                break;


                        }


                        return true;
                    }
                });

                //don't forgot this
                popupMenu.show();

            }
        });
    }

    public void setMenuItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, QUEUE_URL+listname+"/"+id1,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);
                            // lstSource = new String[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);
                                list.add(i, product.get("name")  + "\n");
                                arrayAdapter.notifyDataSetChanged();
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
}
