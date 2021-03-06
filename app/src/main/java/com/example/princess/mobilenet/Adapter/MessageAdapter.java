package com.example.princess.mobilenet.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.princess.mobilenet.Holder.QBUsersHolder;
import com.example.princess.mobilenet.R;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

/**
 * Created by Avery on 6/5/17.
 */

public class MessageAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public MessageAdapter(Context context,ArrayList<QBChatMessage> qbChatMessages){
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
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
            if(qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId()))
            {
                view = inflater.inflate(R.layout.send_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());

            }

            else
            {
                view = inflater.inflate(R.layout.receieve_message, null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
                TextView Tname = (TextView)view.findViewById(R.id.message__user);
                Tname.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getLogin());
            }



        }
        return view;
    }
}
