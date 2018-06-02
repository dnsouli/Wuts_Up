package hk.ust.cse.comp4521.watsup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import hk.ust.cse.comp4521.watsup.models.MyEventRecyclerViewAdapter;
import hk.ust.cse.comp4521.watsup.models.Observer;
import hk.ust.cse.comp4521.watsup.models.Event;

import java.util.ArrayList;
import java.util.List;

import static hk.ust.cse.comp4521.watsup.models.Activities.CALLING_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.ENROLLED_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.EXPLORE_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.HOSTED_ACTIVITY;
import static hk.ust.cse.comp4521.watsup.models.Activities.OPTIONS_ACTIVITY;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk





public class EventListActivity extends AppCompatActivity implements Observer {


    private static final String TAG = "EventListActivity";

    private boolean mTwoPane;
    ProgressDialog nDialog;
    public static List<Event> eventsToBeShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        DataBaseCommunicator.addObserver(this);
        initializeEventList();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Log.d(TAG, getTitle().toString());
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventListActivity.this, MapActivity.class);
                i.putExtra(CALLING_ACTIVITY, EXPLORE_ACTIVITY);
                startActivity(i);
            }
        });

        if (findViewById(R.id.event_detail_container) != null) {
            mTwoPane = true;
        }

    }

    private void initializeEventList() {
        int callingActivity = getIntent().getIntExtra(CALLING_ACTIVITY, -1);
        if (callingActivity == OPTIONS_ACTIVITY) {
            nDialog = new ProgressDialog(EventListActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Getting Data");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            eventsToBeShown = DataBaseCommunicator.eventsList;
        } else if (callingActivity == ENROLLED_ACTIVITY) {
            eventsToBeShown = DataBaseCommunicator.enrolledEvents;
        } else if (callingActivity == HOSTED_ACTIVITY) {
            eventsToBeShown = new ArrayList<>();
            String userID = FirebaseAuth.getInstance().getUid();
            for (Event e : DataBaseCommunicator.eventsList) {
                if (e.getUserID().equals(userID)) {
                    eventsToBeShown.add(e);
                }
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: add observer");
        DataBaseCommunicator.getEventsList();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: remove observer");
        DataBaseCommunicator.removeObserver(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MyEventRecyclerViewAdapter(this, eventsToBeShown, mTwoPane));
    }

    @Override
    public void update() {
        Log.d(TAG, "update: update the recycle view");
        View recyclerView = findViewById(R.id.event_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        if(nDialog != null)
            nDialog.dismiss();

    }
}
