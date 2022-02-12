package dev.morganv.ezbarber.Models;

import java.util.Objects;

public class Appointment implements Comparable<Appointment>{
    private String date;
    private int year, month, day;
    private String time;
    private int hours, minutes;
    private boolean isComplete;
    private String treatment;
    private boolean addedToList;

    public Appointment(){
        date = "";
        time = "";
        isComplete = false;
        addedToList = false;
    }

    public void setDate(String date) {
        this.date = date;
        String[] splitDate = date.split("/", 3);
        day = Integer.parseInt(splitDate[0]);
        month = Integer.parseInt(splitDate[1]);
        year = Integer.parseInt(splitDate[2]);
    }

    public void setTime(String time) {
        this.time = time;
        String[] splitHour = time.split(":", 2);
        hours = Integer.parseInt(splitHour[0]);
        minutes = Integer.parseInt(splitHour[1]);
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setAddedToList(boolean addedToList) {
        this.addedToList = addedToList;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isComplete() {
        if (!date.isEmpty() && !time.isEmpty())
            setComplete(true);
        return isComplete;
    }

    public String getTreatment() {
        return treatment;
    }

    public boolean isAddedToList() {
        return addedToList;
    }

    @Override
    public String toString() {
        return date + " at " + time + "\nTreatment: " + treatment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return year == that.year && month == that.month && day == that.day && hours == that.hours && minutes == that.minutes;
    }

    @Override
    public int compareTo(Appointment app) {
        if(app.year > this.year)
            return 1;
        else if(app.year < this.year)
            return -1;
        else{
            if(app.month > this.month)
                return 1;
            else if(app.month < this.month)
                return -1;
            else{
                if(app.day > this.day)
                    return 1;
                else if(app.day < this.day)
                    return -1;
                else{
                    if(app.hours > this.hours)
                        return 1;
                    else if(app.minutes < this.minutes)
                        return -1;
                    else
                        return 0;
                }
            }
        }
    }
}