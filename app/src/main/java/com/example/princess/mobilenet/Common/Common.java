package com.example.princess.mobilenet.Common;

import com.example.princess.mobilenet.Holder.QBUsersHolder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by Princess on 6/4/17.
 */

public class Common {

    public static final String extra = "Dialogs";


    public static String createChatDialogName(List<Integer> qbUsers)
    {
        List <QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for(QBUser user:qbUsers1)
            name.append(user.getLogin()).append(" ");
        if(name.length() > 30)
            name = name.replace(30,name.length()-1,"...");
        return name.toString();
    }

    public static boolean isEmptyString(String content){
        return (content != null && !content.trim().isEmpty()?false:true);
    }

}
