package com.example.princess.mobilenet.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.princess.mobilenet.Holder.UnreadHolder;
import com.example.princess.mobilenet.R;
import com.quickblox.chat.model.QBChatDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Princess on 6/4/17.
 */

public class ChatLogAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatLogAdapter(Context context,ArrayList<QBChatDialog> qbChatDialogs){
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount(){
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position){
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        View view = convertView;
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_log,null);


            TextView title,message;
            ImageView imageView,image_unread;

            message = (TextView)view.findViewById(R.id.list_chat_log_message);
            title = (TextView)view.findViewById(R.id.list_chat_log_title);
            imageView = (ImageView)view.findViewById(R.id.image_chatLog);
            image_unread = (ImageView)view.findViewById(R.id.image_Unread);

            message.setText(qbChatDialogs.get(position).getLastMessage());
            message.setText(qbChatDialogs.get(position).getName());


            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();


            //grabs first letter of chat log title for image
            //TextDrawable drawable = builder.build(title.getText().toString().substring(0,1).toUpperCase(),randomColor);
            //imageView.setImageDrawable(drawable);

            TextDrawable.IBuilder UnreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            int unreadCounter = UnreadHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if(unreadCounter > 0)
            {
                TextDrawable unread_drawable = UnreadBuilder.build(""+unreadCounter, Color.RED);
                image_unread.setImageDrawable(unread_drawable);
            }
        }
        return view;
    }

}
