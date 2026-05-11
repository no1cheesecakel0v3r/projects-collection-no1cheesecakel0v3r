package com.example.yummypizzamaker;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yummypizzamaker.R;

public class MainActivity extends AppCompatActivity {

    // Variables to track selections (The "Calculator" memory)
    private String selectedDough = "";
    private String selectedSauce = "";
    private String selectedTopping = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. SHOW INSTRUCTIONS POPUP
        new AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setMessage("Welcome to Yummy Pizza Maker!\n\n1. Select a Dough\n2. Pick a Sauce\n3. Add a Topping\n4. Hit BAKE!")
                .setPositiveButton("Let's Cook!", (dialog, which) -> dialog.dismiss())
                .show();

        // 2. FIND THE TOP DISPLAY AREAS
        TextView resultText = findViewById(R.id.txt_result);
        ImageView displayDough = findViewById(R.id.img_dough_display);
        ImageView displaySauce = findViewById(R.id.img_sauce_display);
        ImageView displayTopping = findViewById(R.id.img_topping_display);

        // Set initial text and make font smaller (16sp is handled in XML, code just sets text)
        resultText.setVisibility(View.VISIBLE);
        resultText.setText("Recipe: Choose 1 Dough + 1 Sauce + 1 Topping");

        // 3. BUTTONS - DOUGH
        findViewById(R.id.btn_circle).setOnClickListener(v -> updateDough("Circle", R.drawable.dough_circle, displayDough, resultText));
        findViewById(R.id.btn_heart).setOnClickListener(v -> updateDough("Heart", R.drawable.dough_heart, displayDough, resultText));
        findViewById(R.id.btn_square).setOnClickListener(v -> updateDough("Square", R.drawable.dough_square, displayDough, resultText));
        findViewById(R.id.btn_rect).setOnClickListener(v -> updateDough("Rectangle", R.drawable.dough_rect, displayDough, resultText));

        // 4. BUTTONS - SAUCE
        findViewById(R.id.btn_red).setOnClickListener(v -> updateSauce("Tomato", R.drawable.sauce_red, displaySauce, resultText));
        findViewById(R.id.btn_pesto).setOnClickListener(v -> updateSauce("Pesto", R.drawable.sauce_pesto, displaySauce, resultText));
        findViewById(R.id.btn_aioli).setOnClickListener(v -> updateSauce("Aioli", R.drawable.sauce_aioli, displaySauce, resultText));
        findViewById(R.id.btn_bbq).setOnClickListener(v -> updateSauce("BBQ", R.drawable.sauce_bbq, displaySauce, resultText));

        // 5. BUTTONS - TOPPINGS
        findViewById(R.id.btn_pep).setOnClickListener(v -> updateTopping("Pepperoni", R.drawable.top_pep, displayTopping, resultText));
        findViewById(R.id.btn_mush).setOnClickListener(v -> updateTopping("Mushroom", R.drawable.top_mush, displayTopping, resultText));
        findViewById(R.id.btn_olive).setOnClickListener(v -> updateTopping("Olive", R.drawable.top_olive, displayTopping, resultText));
        findViewById(R.id.btn_onion).setOnClickListener(v -> updateTopping("Onion", R.drawable.top_onion, displayTopping, resultText));

        // 6. BAKE BUTTON (THE CALCULATOR LOGIC)
        findViewById(R.id.btn_bake).setOnClickListener(v -> {
            if (selectedDough.isEmpty() || selectedSauce.isEmpty() || selectedTopping.isEmpty()) {
                resultText.setText("Missing ingredients!");
            } else {
                String finalPizza = "The " + selectedDough + " " + selectedTopping + " " + selectedSauce + " Pizza!";
                resultText.setText("Baked: " + finalPizza);
            }
        });

        // 7. CLEAR BUTTON
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            selectedDough = ""; selectedSauce = ""; selectedTopping = "";
            displayDough.setVisibility(View.INVISIBLE);
            displaySauce.setVisibility(View.INVISIBLE);
            displayTopping.setVisibility(View.INVISIBLE);
            resultText.setText("Recipe: Choose 1 Dough + 1 Sauce + 1 Topping");
        });

        // 8. RANDOM BUTTON FIX
        findViewById(R.id.btn_random).setOnClickListener(v -> {
            // First, reset so it can bypass the "must clear" rule
            selectedDough = ""; selectedSauce = ""; selectedTopping = "";

            int[] doughs = {R.id.btn_circle, R.id.btn_heart, R.id.btn_square, R.id.btn_rect};
            int[] sauces = {R.id.btn_red, R.id.btn_pesto, R.id.btn_aioli, R.id.btn_bbq};
            int[] tops = {R.id.btn_pep, R.id.btn_mush, R.id.btn_olive, R.id.btn_onion};

            findViewById(doughs[(int)(Math.random() * 4)]).performClick();
            findViewById(sauces[(int)(Math.random() * 4)]).performClick();
            findViewById(tops[(int)(Math.random() * 4)]).performClick();
        });
    }

    // HELPER METHODS (Modified to block input if already selected)
    private void updateDough(String name, int resId, ImageView img, TextView txt) {
        if (selectedDough.isEmpty()) {
            selectedDough = name;
            img.setVisibility(View.VISIBLE);
            img.setImageResource(resId);
            txt.setText("Added: " + name + " Dough");
        } else {
            txt.setText("Clear first to change Dough!");
        }
    }

    private void updateSauce(String name, int resId, ImageView img, TextView txt) {
        if (selectedSauce.isEmpty()) {
            selectedSauce = name;
            img.setVisibility(View.VISIBLE);
            img.setImageResource(resId);
            txt.setText("Added: " + name + " Sauce");
        } else {
            txt.setText("Clear first to change Sauce!");
        }
    }

    private void updateTopping(String name, int resId, ImageView img, TextView txt) {
        if (selectedTopping.isEmpty()) {
            selectedTopping = name;
            img.setVisibility(View.VISIBLE);
            img.setImageResource(resId);
            txt.setText("Added: " + name);
        } else {
            txt.setText("Clear first to change Topping!");
        }
    }
}