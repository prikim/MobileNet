package com.example.princess.mobilenet.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.princess.mobilenet.Common.Common;
import com.example.princess.mobilenet.MainActivity;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.measite.minidns.record.NS;

/**
 * Created by Princess on 6/4/17.
 */



public class UserListAdapter extends BaseAdapter{



    //public MainActivity UserStatus;
    private Context context;
    private ArrayList<QBUser> qbUserArrayList;

    public UserListAdapter(Context context,ArrayList<QBUser> qbUserArrayList){
        this.context = context;
        this.qbUserArrayList = qbUserArrayList;
    }


    @Override
    public int getCount() {
        return qbUserArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qbUserArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
        {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice,null);
            TextView textView = (TextView)view.findViewById(android.R.id.text1);
            textView.setText(qbUserArrayList.get(position).getLogin());

        }

        return view;
    }
}
