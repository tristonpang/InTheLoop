package com.llawl.tristonpang.intheloop;

public class EventInfo {
    private String mName;
    private String mDate;
    private String mTime;
    private String mVenue;
    private String mDesc;
    private String mImageName;
    private String mOrganiser;

    public EventInfo() {
    }

    public EventInfo(String name, String date, String time, String venue, String desc, String imageName, String organiser) {
        mName = name;
        mDate = date;
        mTime = time;
        mVenue = venue;
        mDesc = desc;
        mImageName = imageName;
        mOrganiser = organiser;
    }

    public String getName() {
        return mName;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getVenue() {
        return mVenue;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getImageName() {
        return mImageName;
    }

    public String getOrganiser() {
        return mOrganiser;
    }
}
