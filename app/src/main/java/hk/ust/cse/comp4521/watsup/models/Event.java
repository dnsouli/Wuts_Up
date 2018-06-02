package hk.ust.cse.comp4521.watsup.models;

//# COMP 4521    #  YOUR FULL NAME        STUDENT ID          EMAIL ADDRESS
//         1.       Ivan Bardarov         20501426            iebardarov@connect.ust.hk
//         2.       Danny Nsouli          20531407            dmansouli@connect.ust.hk


public class Event {


    private String eventID;
    private String userID;
    private String name;
    private String capacity;
    private String coordinates;
    private String date;
    private String description;
    private String type;
    private String time;

    public static String EVENT_NAME = "name";
    public static String EVENT_CAPACITY = "capacity";
    public static String EVENT_COORDINATES = "coordinates";
    public static String EVENT_DATE = "date";
    public static String EVENT_TIME = "time";
    public static String EVENT_DESCRIPTION = "description";
    public static String EVENT_TYPE = "type";


    public Event(){}

    public Event(String name, String userID, String capacity, String coordinates, String date, String time, String description, String type) {
        this.name = name;
        this.userID = userID;
        this.capacity = capacity;
        this.coordinates = coordinates;
        this.date = date;
        this.time = time;
        this.description = description;
        this.type = type;
    }

    public void setEventID(String eventID){
        this.eventID = eventID;
    }

    public String getEventID() {
        return eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent(){
        return "Event Name:" + name + "\n" +
                "Event Date and Time:" + date + " - " + time + "\n" +
                "Event Type:" + type + "\n" +
                "Event Description:\n" + description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
