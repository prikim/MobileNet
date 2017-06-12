package com.example.princess.mobilenet;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.princess.mobilenet.Adapter.UserListAdapter;
import com.example.princess.mobilenet.Common.Common;
import com.example.princess.mobilenet.Holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ListView lstUsers;
    Button btnNewChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        getAllUsers();

        lstUsers = (ListView)findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        btnNewChat = (Button)findViewById(R.id.btn_open_chat);
        btnNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int countChoice = lstUsers.getCount();

                //picking a single user to chat with
                if(lstUsers.getCheckedItemPositions().size() == 1)
                {
                    createPrivateChat(lstUsers.getCheckedItemPositions());
                }

                //choosing more than one users to chat with
                else if(lstUsers.getCheckedItemPositions().size() > 1)
                    createGroupChat(lstUsers.getCheckedItemPositions());

                //no users are chosen, so all chat is instantiated
                else {
                    //no users are chosen, so make it so a group chat is formed, but with every online user

                    createALLChat(lstUsers.getCheckedItemPositions());
                }

            }
        });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mdialog = new ProgressDialog(UserListActivity.this);
        mdialog.setMessage("Loading . . .");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for(int i = 0;i<countChoice;i++)
        {
            if(checkedItemPositions.get(i))
            {
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }

        final QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);


        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mdialog.dismiss();
                Toast.makeText(getBaseContext(),"Group Session created successfully", Toast.LENGTH_SHORT).show();

                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();

                for(int i = 0; i <qbChatDialog.getOccupants().size(); i++)
                {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }




                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());

            }
        });
    }

    private void createALLChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mdialog = new ProgressDialog(UserListActivity.this);
        mdialog.setMessage("Loading . . .");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();


        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for(int i = 0;i<countChoice;i++)
        {
            if(checkedItemPositions.get(i))
            {
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }

        final QBChatDialog dialog = new QBChatDialog();
        dialog.setName("All Chat");
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);


        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mdialog.dismiss();
                Toast.makeText(getBaseContext(),"Group Session created successfully", Toast.LENGTH_SHORT).show();

                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();

                for(int i = 0; i <qbChatDialog.getOccupants().size(); i++)
                {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }




                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());

            }
        });




    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions){
        final ProgressDialog mdialog = new ProgressDialog(UserListActivity.this);
        mdialog.setMessage("Loading . . .");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        int countChoice = lstUsers.getCount();
        for(int i = 0;i<countChoice;i++)
        {
            if(checkedItemPositions.get(i))
            {
                final QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mdialog.dismiss();
                        Toast.makeText(getBaseContext(),"Session created successfully", Toast.LENGTH_SHORT).show();


                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(user.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());
                        try {
                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",e.getMessage());

                    }
                });
            }
        }

    }


    private void getAllUsers(){
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUsersHolder.getInstance().putUsers(qbUsers);
                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for(QBUser user : qbUsers)
                {
                    if(!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {
                        qbUserWithoutCurrent.add(user);
                    }
                }

                UserListAdapter adapter = new UserListAdapter(getBaseContext(),qbUserWithoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());


            }
        });

    }
}
