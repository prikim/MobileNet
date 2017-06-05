package com.example.princess.mobilenet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "58675";
    static final String AUTH_KEY = "CRuVJsHVQQjqsrL";
    static final String AUTH_SECRET = "VZh2SMDzPV2-Gsk";
    static final String ACCOUNT_KEY = "XcGcx_EATaazDNXGhBBn";


    Button btnLogin,btnRegister;
    EditText edtUser,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initializeFramework();


        btnLogin = (Button)findViewById(R.id.main_btnLogin);
        btnRegister = (Button)findViewById(R.id.main_btnRegister);

        edtUser = (EditText)findViewById(R.id.main_editLogin);
        edtPassword = (EditText)findViewById(R.id.main_editPassword);


        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();

                QBUser qbUser = new QBUser(user,password);


                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>(){
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle){
                        Toast.makeText(getBaseContext(), "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this,ChatLogActivity.class);
                        intent.putExtra("user",user);
                        intent.putExtra("password",password);
                        startActivity(intent);


                    }


                    @Override
                    public void onError(QBResponseException e){
                        Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    private void initializeFramework(){
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
