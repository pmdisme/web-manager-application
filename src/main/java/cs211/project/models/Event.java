package cs211.project.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Comparable {
    private String ownerUsername;
    private String name;
    private String picture;
    private String info;
    private String category;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxParticipants;
    private LocalDate startJoinDate;
    private LocalDate closeJoinDate;
    private LocalTime startJoinTime;
    private LocalTime closeJoinTime;
    private String eventUUID;

    public Event(String name, String picture, String info, String category, String place,
                 LocalDate startDate, LocalDate endDate, LocalTime startTime,
                 LocalTime endTime, String ownerUsername, int maxParticipants,
                 LocalDate startJoinDate, LocalDate closeJoinDate,
                 LocalTime startJoinTime, LocalTime closeJoinTime, String eventUUID) {
        this.name = name;
        this.picture = picture;
        this.info = info;
        this.category = category;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ownerUsername = ownerUsername;
        this.maxParticipants = maxParticipants;
        this.startJoinDate = startJoinDate;
        this.closeJoinDate = closeJoinDate;
        this.startJoinTime = startJoinTime;
        this.closeJoinTime = closeJoinTime;
        this.eventUUID = eventUUID;
    }

    public String getName() { return name; }

    public String getInfo() { return info; }

    public String getCategory() { return category; }

    public String getPlace() { return place; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public LocalTime getStartTime() { return startTime; }

    public LocalTime getEndTime() { return endTime; }

    public int getMaxParticipants() { return maxParticipants; }

    public String getPicture() {
        return picture;
    }

    public String getEventUUID() { return eventUUID; }

    public void setMaxParticipant(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public LocalDate getStartJoinDate() {
        return startJoinDate;
    }

    public void setStartJoinDate(LocalDate startJoinDate) {
        this.startJoinDate = startJoinDate;
    }

    public LocalDate getCloseJoinDate() {
        return closeJoinDate;
    }

    public void setCloseJoinDate(LocalDate closeJoinDate) {
        this.closeJoinDate = closeJoinDate;
    }

    public String getOwnerUsername() { return ownerUsername; }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartJoinTime() {
        return startJoinTime;
    }

    public void setStartJoinTime(LocalTime startJoinTime) {
        this.startJoinTime = startJoinTime;
    }

    public LocalTime getCloseJoinTime() {
        return closeJoinTime;
    }

    public void setCloseJoinTime(LocalTime closeJoinTime) {
        this.closeJoinTime = closeJoinTime;
    }

    @Override
    public int compareTo(Object o) {
        Event event = (Event) o;
        try{
            return Integer.parseInt(this.name) - Integer.parseInt(event.name);
        }
        catch (NumberFormatException e){

            if (this.name.compareTo(event.name) > 0){
                if (this.name.length() < event.name.length()){
                    return -1;
                }
            }
            if (this.name.compareTo(event.name) < 0){
                if (this.name.length() > event.name.length()){
                    return 1;
                }
            }
            return this.name.compareTo(event.name);
        }
    }
}
