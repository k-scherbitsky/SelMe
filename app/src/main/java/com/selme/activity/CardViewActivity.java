package com.selme.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.selme.R;

public class CardViewActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView descriptionText;
    private ImageView picture1;
    private ImageView picture2;
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        titleText = findViewById(R.id.titlePostTextView);
        descriptionText = findViewById(R.id.descriptionPostTextView);
        picture1 = findViewById(R.id.postImage1);
        picture2 = findViewById(R.id.postImage2);
        button1 = findViewById(R.id.thisOneButton1);
        button2 = findViewById(R.id.thisOneButton2);

    }
}
