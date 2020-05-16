package be.kuleuven.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {
    GridLayout mainGrid;
    private CardView shoppingList;
    private CardView nearbyGroceries;
    private CardView scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mainGrid=(GridLayout)findViewById(R.id.mainGrid);
        shoppingList=(CardView) findViewById(R.id.shoppingList);
        nearbyGroceries=(CardView) findViewById(R.id.nearbyGroceries);
        scanner=(CardView) findViewById(R.id.scanner);

        //Set Event
        setSingleEvent(mainGrid);

    }

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i <mainGrid.getChildCount() ; i++) {
            //all child are cardView so they are casted cardView
            CardView cardView=(CardView)mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {

                        case R.id.nearbyGroceries:
                            Intent intent2 = new Intent(HomePage.this, MapsActivity.class);
                            HomePage.this.startActivity(intent2);
                            break;
                        case R.id.shoppingList:
                            Intent intent3 = new Intent(HomePage.this, GroceryListActivity.class);
                            HomePage.this.startActivity(intent3);
                            break;
                        case R.id.scanner:
                            Intent intent1 = new Intent(HomePage.this, MainActivity.class);
                            HomePage.this.startActivity(intent1);
                            break;
                        default:
                            break;
                    }
                }
            });
            
        }
    }
}
