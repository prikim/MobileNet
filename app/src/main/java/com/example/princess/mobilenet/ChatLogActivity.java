package com.example.princess.mobilenet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.princess.mobilenet.Adapter.ChatLogAdapter;
import com.example.princess.mobilenet.Common.Common;
import com.example.princess.mobilenet.Holder.QBLogHolder;
import com.example.princess.mobilenet.Holder.QBUsersHolder;
import com.example.princess.mobilenet.Holder.UnreadHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.chat.ChatMessageListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatLogActivity extends AppCompatActivity implements QBSystemMessageListener,QBChatDialogMessageListener{


    int contextMenuIndexClicked = -1;
    FloatingActionButton floatingActionButton;
    ListView lstChatlogs;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_dialog_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
       contextMenuIndexClicked = info.position;

        switch(item.getItemId())
        {
            case R.id.context_delete_dialog:
                deleteDialog(info.position);
                break;
        }
        return true;
    }

    private void deleteDialog(int index) {

        final QBChatDialog chatDialog = (QBChatDialog)lstChatlogs.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(),false)
                .performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBLogHolder.getInstance().removeDialog(chatDialog.getDialogId());
                        ChatLogAdapter adapter = new ChatLogAdapter(getBaseContext(),QBLogHolder.getInstance().getAllLogs());
                        lstChatlogs.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_log_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.chat_dialog_menu_user:
                showUserProfile();
                break;
            default:
                break;
        }
        return true;
    }

    private void showUserProfile() {
        Intent intent = new Intent(ChatLogActivity.this,UserProfile.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatLogs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);


        Toolbar toolbar = (Toolbar)findViewById(R.id.chat_dialog_toolbar);
        toolbar.setTitle("BLIZZARD MobileNet");
        setSupportActionBar(toolbar);


        createChat();

        lstChatlogs = (ListView)findViewById(R.id.lstChatLog);



        registerForContextMenu(lstChatlogs);


        lstChatlogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog)lstChatlogs.getAdapter().getItem(position);
                Intent intent = new Intent(ChatLogActivity.this, MessageActivity.class);
                intent.putExtra(Common.extra,qbChatDialog);
                startActivity(intent);

            }
        });

        loadChatLogs();



        floatingActionButton = (FloatingActionButton)findViewById(R.id.chatlog_adduser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatLogActivity.this,UserListActivity.class);
                startActivity(intent);

            }
        });



    }

    private void loadChatLogs(){

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                QBLogHolder.getInstance().placeLogs(qbChatDialogs);


                Set<String> setIDS = new HashSet<>();
                for(QBChatDialog chatDialog:qbChatDialogs)
                {
                    setIDS.add(chatDialog.getDialogId());
                }

                QBRestChatService.getTotalUnreadMessagesCount(setIDS,UnreadHolder.getInstance().getBundle())
                        .performAsync(new QBEntityCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer integer, Bundle bundle) {

                                UnreadHolder.getInstance().setBundle(bundle);

                                QBLogHolder.getInstance().placeLogs(qbChatDialogs);
                                ChatLogAdapter adapter = new ChatLogAdapter(getBaseContext(),QBLogHolder.getInstance().getAllLogs());
                                lstChatlogs.setAdapter(adapter);
                                adapter.notifyDataSetChanged();


                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });


            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());

            }
        });
    }

    private void createChat(){
        final ProgressDialog dia = new ProgressDialog(ChatLogActivity.this);
        dia.setMessage("Loading . . .");
        dia.setCanceledOnTouchOutside(false);
        dia.show();

        String user,password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");


        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user,password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        dia.dismiss();


                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ChatLogActivity.this);


                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ChatLogActivity.this);

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                QBLogHolder.getInstance().placeALog(qbChatDialog);

                ArrayList<QBChatDialog> adapterSource =  QBLogHolder.getInstance().getAllLogs();

                ChatLogAdapter adapters = new ChatLogAdapter(getBaseContext(),adapterSource);
                lstChatlogs.setAdapter(adapters);
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        Log.e("ERROR",""+e.getMessage());
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

        loadChatLogs();

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }
}
