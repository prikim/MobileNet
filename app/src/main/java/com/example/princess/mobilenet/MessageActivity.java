package com.example.princess.mobilenet;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.princess.mobilenet.Adapter.MessageAdapter;
import com.example.princess.mobilenet.Common.Common;
import com.example.princess.mobilenet.Holder.MessagesHolder;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Collection;

public class MessageActivity extends AppCompatActivity implements QBChatDialogMessageListener{

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton sendButton;
    EditText editContent;

    MessageAdapter adapter;


    ImageView img_online_counter;
    TextView text_online_counter;

    int contextMenuIndexClicked = 0;
    boolean isEditing = false;
    QBChatMessage edtMessage;


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        contextMenuIndexClicked = info.position;

        switch (item.getItemId())
        {
            case R.id.chat_message_edit_message:
                EditMessage();
                return true;
            case R.id.chat_message_delete_message:
                deleteMessage();
                return true;

        }



        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context_menu,menu);
    }

    //when a user wants to delete a message theyve sent
    private void deleteMessage() {

        final ProgressDialog deleteText = new ProgressDialog(MessageActivity.this);
        deleteText.setMessage("Deleting text");
        deleteText.show();

        edtMessage = MessagesHolder.getInstance().getMessagesByID(qbChatDialog.getDialogId())
                .get(contextMenuIndexClicked);

        QBRestChatService.deleteMessage(edtMessage.getId(),false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                LoadMessages();
                deleteText.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }



    //when a user wants to edit a message theyve sent
    private void EditMessage() {


        edtMessage = MessagesHolder.getInstance().getMessagesByID(qbChatDialog.getDialogId())
            .get(contextMenuIndexClicked);
        editContent.setText(edtMessage.getBody());
        isEditing = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        StartViews();

        StartChatLogs();

        LoadMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //if user is not editting
                    if (isEditing == false) {

                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(editContent.getText().toString());
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);


                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            MessagesHolder.getInstance().addSingleMessage(qbChatDialog.getDialogId(), chatMessage);
                            ArrayList<QBChatMessage> messages = MessagesHolder.getInstance().getMessagesByID(qbChatDialog.getDialogId());

                            adapter = new MessageAdapter(getBaseContext(), messages);
                            lstChatMessages.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }


                        editContent.setText("");
                        editContent.setFocusable(true);
                    }


                    //user is editting text
                    else {

                        final ProgressDialog editText = new ProgressDialog(MessageActivity.this);
                        editText.setMessage("Editing text");
                        editText.show();


                        QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                        messageUpdateBuilder.updateText(editContent.getText().toString()).markDelivered().markRead();

                        QBRestChatService.updateMessage(edtMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder)
                                .performAsync(new QBEntityCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid, Bundle bundle) {
                                        //load, but with new changes added
                                        LoadMessages();
                                        isEditing = false;
                                        //exit out of edit mode
                                        editText.dismiss();


                                        editContent.setText("");
                                        editContent.setFocusable(true);
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }


            }
        });


    }


    private void LoadMessages() {
        QBMessageGetBuilder messageGbuilder = new QBMessageGetBuilder();
        messageGbuilder.setLimit(500); //up to 500 messages
        if(qbChatDialog !=null)
        {
            QBRestChatService.getDialogMessages(qbChatDialog,messageGbuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    MessagesHolder.getInstance().addMessages(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter = new MessageAdapter(getBaseContext(),qbChatMessages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }


    }

    private void StartChatLogs() {
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.extra);
        qbChatDialog.initForChat(QBChatService.getInstance());

        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        if(qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP)
        {
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ERROR",""+e.getMessage());
                }
            });
        }

        qbChatDialog.addMessageListener(this);


        final QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogID, QBPresence qbPresence) {

                if(dialogID == qbChatDialog.getDialogId())
                {
                    QBRestChatService.getChatDialogById(dialogID)
                            .performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    //get online users
                                    try {
                                        Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();
                                        TextDrawable.IBuilder builder = TextDrawable.builder()
                                                .beginConfig()
                                                .withBorder(4)
                                                .endConfig()
                                                .round();

                                        TextDrawable online = builder.build("", Color.RED);
                                        img_online_counter.setImageDrawable(online);

                                        text_online_counter.setText(String.format("%d/%d online",onlineList.size(),qbChatDialog.getOccupants().size()));
                                    } catch (XMPPException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });

                }
            }
        };



    }

    private void StartViews() {
        lstChatMessages = (ListView)findViewById(R.id.message_list);
        sendButton = (ImageButton) findViewById(R.id.send_button);
        editContent = (EditText)findViewById(R.id.edt_content);

        img_online_counter = (ImageView)findViewById(R.id.img_online_counter);
        text_online_counter = (TextView)findViewById(R.id.text_online_counter);

        //Edit Message
        registerForContextMenu(lstChatMessages);





    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        MessagesHolder.getInstance().addSingleMessage(qbChatMessage.getDialogId(),qbChatMessage);
        ArrayList<QBChatMessage> messages = MessagesHolder.getInstance().getMessagesByID(qbChatMessage.getDialogId());

        adapter = new MessageAdapter(getBaseContext(),messages);
        lstChatMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("ERROR",""+e.getMessage());
    }
}
