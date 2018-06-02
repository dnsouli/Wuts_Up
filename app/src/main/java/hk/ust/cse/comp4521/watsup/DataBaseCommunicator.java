package hk.ust.cse.comp4521.watsup;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hk.ust.cse.comp4521.watsup.models.Observer;
import hk.ust.cse.comp4521.watsup.models.Event;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk



public class DataBaseCommunicator {

    private static final String TAG = "DataBaseCommunicator";

    public static Map<String, Event> events = new HashMap<>();

    public static List<Event> eventsList = new ArrayList<>();

    public static List<Event> enrolledEvents = new ArrayList<>();

    public static List<Observer> observers = new ArrayList<>();

    public static void saveEvent(Event e, String userID){
        DatabaseReference eventDB = FirebaseDatabase.getInstance().getReference("event");
        String eventID = eventDB.push().getKey();
        e.setEventID(eventID);
        eventDB.child(eventID).setValue(e);
        enrollEvent(eventID, userID);
    }

    public static void getEventsList(){
        DatabaseReference eventDB = FirebaseDatabase.getInstance().getReference("event");
        eventDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Event Data changed updating");
                events.clear();
                eventsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Event e = ds.getValue(Event.class);
                    events.put(e.getEventID(), e);
                    eventsList.add(e);
                }
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void enrollEvent(String eventID, String userID){
        DatabaseReference enrolledDB = FirebaseDatabase.getInstance().getReference("enrolled");
        enrolledDB.child("events").child(eventID).child(userID).setValue(userID);
        enrolledDB.child("users").child(userID).child(eventID).setValue(eventID);
    }

    public static void setEnrolled(String userID){
        DatabaseReference enrolledDB = FirebaseDatabase.getInstance().getReference("enrolled/users/"+userID);
        enrolledEvents.clear();
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Data Successfully read once");
                List<String> eventIds = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String e = ds.getValue(String.class);
                    eventIds.add(e);
                }

                for(String id: eventIds){
                    enrolledEvents.add(events.get(id));
                }
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        enrolledDB.addListenerForSingleValueEvent(listener);

    }

    public static void addObserver(Observer o){
        observers.add(o);
    }

    public static void removeObserver(Observer o){
        observers.remove(o);
    }

    private static void notifyObservers(){
        for(Observer o : observers){
            o.update();
        }
    }



}
