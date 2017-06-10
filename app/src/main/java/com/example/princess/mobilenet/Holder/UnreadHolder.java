package com.example.princess.mobilenet.Holder;

import android.os.Bundle;

/**
 * Created by Princess on 6/9/17.
 */

public class UnreadHolder {

    private static UnreadHolder instance;
    private Bundle bundle;


    public static synchronized  UnreadHolder getInstance(){
        UnreadHolder unreadHolder;
        synchronized (UnreadHolder.class){
            if(instance == null)
            {
                instance =new UnreadHolder();
            }

            unreadHolder = instance;
        }

        return unreadHolder;
    }

    //constructor
    private UnreadHolder(){
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    //bundle getter
    public Bundle getBundle(){
        return this.bundle;
    }

    public int getUnreadByID(String ID){
        return this.bundle.getInt(ID);
    }



}
