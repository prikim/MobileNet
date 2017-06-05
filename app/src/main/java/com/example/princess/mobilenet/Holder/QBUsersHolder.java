package com.example.princess.mobilenet.Holder;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avery on 6/4/17.
 */

public class QBUsersHolder {

    private static QBUsersHolder instance;
    private SparseArray<QBUser> qbUseRSparseArray;



    public static synchronized  QBUsersHolder getInstance(){
        if(instance == null)
            instance = new QBUsersHolder();
        return instance;
    }

    public void putUsers(List<QBUser> users){
        for(QBUser user: users)
            putUser(user);
    }

    public QBUser getUserById(int id)
    {
        return qbUseRSparseArray.get(id);
    }

    public List<QBUser> getUsersByIds(List<Integer> ids)
    {
        List<QBUser> qbUser = new ArrayList<>();
        for(Integer id:ids)
        {
            QBUser user = getUserById(id);
            if(user != null)
                qbUser.add(user);
        }
        return  qbUser;
    }

    private void putUser(QBUser user) {
        qbUseRSparseArray.put(user.getId(),user);
    }



    private QBUsersHolder() {
        qbUseRSparseArray = new SparseArray<>();
    }




}
