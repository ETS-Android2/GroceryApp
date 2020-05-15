package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.StringTokenizer;

public class Login extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button login;
    private Button register;
    private View popupViewRegister;
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerConfirmPassword;
    private Button confirm;
    private RequestQueue requestQueue;
    private Button groceryList;
    private int id;
    private Button maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        requestQueue = Volley.newRequestQueue(this);
        groceryList = (Button) findViewById(R.id.groceryList);
        maps = (Button) findViewById(R.id.maps);

        LayoutInflater layoutInflater = LayoutInflater.from(Login.this);
        popupViewRegister = layoutInflater.inflate(R.layout.register_popup, null);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLoginData(username.getText().toString());
            }
        });

        groceryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Login.this, GroceryListActivity.class);
                Login.this.startActivity(intent2);
            }
        });
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(Login.this, MapsActivity.class);
                Login.this.startActivity(intent3);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.register_popup, null);
                final Context context = view.getContext();

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, 1100, 1600, true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                // show the popup window
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 100);

                registerUsername = (EditText) popupView.findViewById(R.id.registerUsername);
                registerPassword = (EditText) popupView.findViewById(R.id.registerPassword);
                registerConfirmPassword = (EditText) popupView.findViewById(R.id.registerConfirmPassword);
                confirm = (Button) popupView.findViewById(R.id.confirm);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // TODO: 7/04/2020 alle restricties moeten nog worden geimplementeerd

                        registerAccount(registerUsername.getText().toString(),registerPassword.getText().toString(),registerConfirmPassword.getText().toString());
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    public void registerAccount(String username, String password, String confirmPassword){
        String saltString = null;
        if (password.equals(confirmPassword)) {
            MessageDigest md;
            try {
                // Select the message digest for the hash computation -> SHA-256
                md = MessageDigest.getInstance("SHA-256");

                // Generate the random salt
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                saltString = salt.toString();
                // Passing the salt to the digest for the computation
                md.update(saltString.getBytes());

                // Generate the salted hash
                byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

                StringBuilder sb = new StringBuilder();
                for (byte b : hashedPassword)
                    sb.append(String.format("%02x", b));

                password = sb.toString();
            } catch (NoSuchAlgorithmException e) {

                e.printStackTrace();
            }
            createAccount(username,saltString,password);

        }else{
            Toast.makeText(getApplicationContext(), "Passwords doesn't match.", Toast.LENGTH_SHORT).show();
        }

    }
    public void createAccount(String name, String salt, String hash) {
        System.out.println(name+"/"+salt+"/"+hash);
        final String QUEUE_URL = "https://studev.groept.be/api/a19sd303/registerAccount/"+name+"/"+salt+"/"+hash;

        final StringRequest submitRequest = new StringRequest(Request.Method.GET, QUEUE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Login.this, "Account created", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Unable to create account", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }

    public void getLoginData(String name) {
        final String[] hash = new String[1];
        final String[] salt = new String[1];

        requestQueue = Volley.newRequestQueue(this);
        String url = "https://studev.groept.be/api/a19sd303/login/" + name;
        final JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        id = object.getInt("id_users");
                        hash[0] = object.getString("hash");
                        salt[0] = object.getString("salt");
                        compareHash(hash,salt);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
                //information.setText(error.toString());
                error.printStackTrace();
            }
        });
        requestQueue.add(queueRequest);
    }

    public void compareHash(String[] hash, String[] salt){
        MessageDigest md;
        try {
            // Select the message digest for the hash computation -> SHA-256
            md = MessageDigest.getInstance("SHA-256");

            // Passing the salt to the digest for the computation
            md.update(salt[0].getBytes());

            // Generate the salted hash
            byte[] comparePassword = password.getText().toString().getBytes(StandardCharsets.UTF_8);
            byte[] hashedPassword = md.digest(comparePassword);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) sb.append(String.format("%02x", b));

            System.out.println(hash[0]+"/"+sb.toString());

            //start next intent if hash from db matches hash from password
            if (hash[0].equals( sb.toString())){
                Toast.makeText(getApplicationContext(), "Welcome " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("userId", id);
                Login.this.startActivity(intent);
            }else Toast.makeText(getApplicationContext(), "Password doesn't match username.", Toast.LENGTH_SHORT).show();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
