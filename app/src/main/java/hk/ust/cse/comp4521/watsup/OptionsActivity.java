package hk.ust.cse.comp4521.watsup;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import hk.ust.cse.comp4521.watsup.models.Activities;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk



public class OptionsActivity extends AppCompatActivity {

    private static final String TAG = "OptionsActivity";


    View.OnClickListener addEventButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(OptionsActivity.this, AddEventActivity.class);
            startActivity(i);
        }
    };

    View.OnClickListener exploreButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), EventListActivity.class);
            i.putExtra(Activities.CALLING_ACTIVITY, Activities.OPTIONS_ACTIVITY);
            startActivityForResult(i,1);
        }
    };

    View.OnClickListener profileButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(OptionsActivity.this, ProfileActivity.class);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: OptionActivity started");
        setContentView(R.layout.activity_options);

        ImageButton addEventImageButton = (ImageButton) findViewById(R.id.imageButtonAddEvent);
        ImageButton exploreImageButton  = (ImageButton) findViewById(R.id.imageButtonExplore);
        ImageButton profileImageButton = (ImageButton) findViewById(R.id.imageButtonProfile);

        addEventImageButton.setOnClickListener(addEventButtonListener);
        exploreImageButton.setOnClickListener(exploreButtonListener);
        profileImageButton.setOnClickListener(profileButtonListener);

        }

}
