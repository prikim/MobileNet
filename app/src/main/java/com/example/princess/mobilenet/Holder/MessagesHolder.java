package com.example.princess.mobilenet.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Princess on 6/5/17.
 */

public class MessagesHolder {
    private static MessagesHolder instance;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized MessagesHolder getInstance(){
        MessagesHolder messagesHolder;
        synchronized (MessagesHolder.class){
            if(instance==null)
                instance = new MessagesHolder();
            messagesHolder = instance;
        }
        return messagesHolder;
    }


    private MessagesHolder(){
        this.qbChatMessageArray = new HashMap<>();

    }

    public void addMessages(String dialogID,ArrayList<QBChatMessage> qbChatMessages){
        this.qbChatMessageArray.put(dialogID,qbChatMessages);
    }

    public void addSingleMessage(String dialogID,QBChatMessage qbChatMessage){
        List<QBChatMessage> lstResult = (List)this.qbChatMessageArray.get(dialogID);
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage>lstAdded = new ArrayList(lstResult.size());
        lstAdded.addAll(lstResult);
        addMessages(dialogID,lstAdded);
    }


    public ArrayList<QBChatMessage> getMessagesByID(String dialogID){
        return(ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogID);
    }

}
