package com.example.princess.mobilenet;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.princess.mobilenet.MainActivity;
import com.example.princess.mobilenet.Common.Common;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.model.QBBaseCustomObject;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {




    //public MainActivity UserStatus;

    EditText editPassword,editOldPassword,editBattlenet;
    Button btnUpdate,btnCancel;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_update_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.user_update_log_out:
                logOut();
                break;
            default:
                break;
        }


        return true;
    }

    private void logOut() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                        Toast.makeText(UserProfile.this, "Logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfile.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //makes it so that the user is "offline"

                        startActivity(intent);



                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar)findViewById(R.id.user_update_toolbar);
        toolbar.setTitle("BLIZZARD MobileNet");
        setSupportActionBar(toolbar);

        initViews();

        loadUserProfile();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editPassword.getText().toString();
                String oldPassword = editOldPassword.getText().toString();
                //String BattlenetID = "#" + editBattlenet.getText().toString();




                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if(!Common.isEmptyString(oldPassword))
                    user.setOldPassword(oldPassword);
                if(!Common.isEmptyString(password))
                    user.setPassword(password);

                //if(!Common.isEmptyString(BattlenetID))
                  //  user.setPhone(BattlenetID);

                final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);

                mDialog.setMessage("Updating...");
                mDialog.show();
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User: " +qbUser.getLogin()+" Updated", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();


                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile.this, "Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    private void loadUserProfile() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        String user = currentUser.getLogin();
        //String battleTag = currentUser.getCustomData();





        //editBattlenet.setText(battleTag);


    }

    private void initViews() {

        btnCancel = (Button)findViewById(R.id.cancel_update);
        btnUpdate = (Button)findViewById(R.id.Update_account);


        editPassword = (EditText)findViewById(R.id.update_password);
        editOldPassword = (EditText)findViewById(R.id.update_old_password);
        //editBattlenet = (EditText)findViewById(R.id.update_battlenetID);



    }
}
