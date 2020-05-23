package be.kuleuven.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryClick extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private String picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_click);

        imageView = (ImageView) findViewById(R.id.inventoryImage);
        textView =(TextView) findViewById(R.id.url);

        Intent intent = getIntent();
        textView.setText(String.valueOf(intent.getStringExtra("picture")));
        picture = String.valueOf(intent.getStringExtra("picture"));

        Picasso.get().load(picture).into(imageView);
    }
}
