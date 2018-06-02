package hk.ust.cse.comp4521.watsup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk



public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.enrollButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int index = getIntent().getIntExtra(EventDetailFragment.ARG_ITEM_ID, -1);
                if(index != -1){
                    final String userID = FirebaseAuth.getInstance().getUid();
                    final String eventID = EventListActivity.eventsToBeShown.get(index).getEventID();

                    DatabaseReference enrolledDB = FirebaseDatabase.getInstance().getReference("enrolled/events/"+eventID);
                    enrolledDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount() < Integer.parseInt(EventListActivity.eventsToBeShown.get(index).getCapacity())){
                                Log.d(TAG, "onClick: Enrolled event successfully");
                                DatabaseReference enrolledDB = FirebaseDatabase.getInstance().getReference("enrolled");
                                enrolledDB.child("events").child(eventID).child(userID).setValue(userID);
                                enrolledDB.child("users").child(userID).child(eventID).setValue(eventID);
                                Toast.makeText(EventDetailActivity.this, "You have enrolled the event successfully.", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(EventDetailActivity.this, OptionsActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(EventDetailActivity.this, "The event is full. You cannot enroll.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(EventDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(EventDetailFragment.ARG_ITEM_ID, -1));
            EventDetailFragment fragment = new EventDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, EventListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
