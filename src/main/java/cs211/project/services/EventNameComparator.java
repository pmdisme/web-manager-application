package cs211.project.services;

import cs211.project.models.Event;

import java.util.Comparator;

public class EventNameComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        try{
            return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
        }
        catch (NumberFormatException e){

            if (o1.getName().compareTo(o2.getName()) > 0){
                if (o1.getName().length() < o2.getName().length()){
                    return -1;
                }
            }
            if (o1.getName().compareTo(o2.getName()) < 0){
                if (o1.getName().length() > o2.getName().length()){
                    return 1;
                }
            }
            return o1.getName().compareTo(o2.getName());
        }
    }
}
