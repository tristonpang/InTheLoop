package com.llawl.tristonpang.intheloop;

public class EventInfo {
    private String mName;
    private String mDate;
    private String mTime;
    private String mVenue;
    private String mDesc;
    private String mImageName;

    public EventInfo() {
    }

    public EventInfo(String name, String date, String time, String venue, String desc, String imageName) {
        mName = name;
        mDate = date;
        mTime = time;
        mVenue = venue;
        mDesc = desc;
        mImageName = imageName;
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
}
