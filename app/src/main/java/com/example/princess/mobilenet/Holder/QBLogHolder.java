package com.example.princess.mobilenet.Holder;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBChatDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Avery on 6/7/17.
 */

public class QBLogHolder {

    private static QBLogHolder instance;
    private HashMap<String,QBChatDialog> qbChatDialogHashMap;

    public static synchronized  QBLogHolder getInstance(){
        QBLogHolder qbLogHolder;
        synchronized (QBLogHolder.class)
        {
            if(instance == null)
            {
                instance = new QBLogHolder();
            }

        }
        qbLogHolder = instance;
        return qbLogHolder;
    }

    public QBLogHolder(){

     this.qbChatDialogHashMap = new HashMap<>();
    }


    public void placeLogs(List<QBChatDialog> dialogs) {
        for(QBChatDialog qbChatDialog:dialogs)
            placeALog(qbChatDialog);
    }

    public void placeALog(QBChatDialog qbChatDialog) {
        this.qbChatDialogHashMap.put(qbChatDialog.getDialogId(),qbChatDialog);

    }

    public QBChatDialog getChatLog_ID(String DialogID) {
        return (QBChatDialog)qbChatDialogHashMap.get(DialogID);
    }

    public List<QBChatDialog> getChatLogs_IDs(List<String> DialogIds){
        List<QBChatDialog> chatDialogs = new ArrayList<>();
        for(String id:DialogIds)
        {
            QBChatDialog chatDialog = getChatLog_ID(id);
            if(chatDialog != null)
            {
                chatDialogs.add(chatDialog);
            }

        }
        return chatDialogs;
    }

    public ArrayList<QBChatDialog> getAllLogs(){
        ArrayList<QBChatDialog> qbChat = new ArrayList<>();
        for(String key:qbChatDialogHashMap.keySet())
            qbChat.add(qbChatDialogHashMap.get(key));

            return qbChat;

    }

    public void removeDialog(String id)
    {
        qbChatDialogHashMap.remove(id);
    }


}
