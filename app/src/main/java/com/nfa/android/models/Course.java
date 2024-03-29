package com.nfa.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Course implements Parcelable {

    private String id;
    private String name;
    private String number;
    private String section;
    private CourseType type;
    private List<String> dates;
    private String startTime;
    private String endTime;
    private String location;
    private String prof;

    public Course(String id, String name, String number, String section,
            CourseType type, List<String> dates, String startTime, String endTime,
            String location, String prof) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.section = section;
        this.type = type;
        this.dates = dates;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.prof = prof;
    }

    protected Course(Parcel in) {
        id = in.readString();
        name = in.readString();
        number = in.readString();
        section = in.readString();
        dates = in.createStringArrayList();
        startTime = in.readString();
        endTime = in.readString();
        location = in.readString();
        prof = in.readString();
        type = CourseType.valueOf(in.readString());
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getProf(){return prof;}

    public void setProf(String prof){this.prof = prof;}

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String translateDays() {
        StringBuilder sb = new StringBuilder();
        for (String date : dates) {
            sb.append(date);
            if (!date.equals(dates.get(dates.size() - 1))) {
                sb.append(" - ");
            }
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(section);
        parcel.writeStringList(dates);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(location);
        parcel.writeString(prof);
        parcel.writeString(type.name());
    }
}
