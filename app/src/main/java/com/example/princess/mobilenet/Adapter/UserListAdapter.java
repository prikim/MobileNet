package com.example.princess.mobilenet.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Avery on 6/4/17.
 */

public class UserListAdapter extends BaseAdapter{

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
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice,null);
            TextView textView = (TextView)view.findViewById(android.R.id.text1);
            textView.setText(qbUserArrayList.get(position).getLogin());

        }

        return view;
    }
}
