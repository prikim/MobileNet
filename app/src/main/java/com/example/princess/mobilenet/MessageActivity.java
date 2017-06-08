package com.example.princess.mobilenet;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.princess.mobilenet.Adapter.MessageAdapter;
import com.example.princess.mobilenet.Common.Common;
import com.example.princess.mobilenet.Holder.MessagesHolder;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements QBChatDialogMessageListener{

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton sendButton;
    EditText editContent;

    MessageAdapter adapter;


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
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(editContent.getText().toString());
                chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                chatMessage.setSaveToHistory(true);


                try {
                    qbChatDialog.sendMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

                if(qbChatDialog.getType() == QBDialogType.PRIVATE)
                {
                    MessagesHolder.getInstance().addSingleMessage(qbChatDialog.getDialogId(),chatMessage);
                    ArrayList<QBChatMessage> messages = MessagesHolder.getInstance().getMessagesByID(qbChatDialog.getDialogId());

                    adapter = new MessageAdapter(getBaseContext(),messages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }



                editContent.setText("");
                editContent.setFocusable(true);

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

    }

    private void StartViews() {
        lstChatMessages = (ListView)findViewById(R.id.message_list);
        sendButton = (ImageButton) findViewById(R.id.send_button);
        editContent = (EditText)findViewById(R.id.edt_content);

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
