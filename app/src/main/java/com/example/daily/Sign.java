package com.example.daily;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.litepal.crud.DataSupport;

import java.util.List;


public class Sign extends Activity implements OnClickListener,OnCheckedChangeListener{

    EditText usernameBox,passwordBox,confirmPasswordBox;
    Button signBtn;
    CheckBox ShowPassword;
    String username,password,confirmPassword;
    List<User> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        findView();
    }
    private void findView(){
        usernameBox = (EditText) findViewById(R.id.username_box);
        passwordBox = (EditText) findViewById(R.id.password_box);
        confirmPasswordBox = (EditText) findViewById(R.id.confirm_password_box);
        ShowPassword = (CheckBox) findViewById(R.id.show_password);
        signBtn = (Button) findViewById(R.id.sign_btn);
        signBtn.setOnClickListener(this);
        ShowPassword.setOnCheckedChangeListener(this);
    }

    private boolean getUserinfo(){
        try{
            username = usernameBox.getText().toString();
            password = passwordBox.getText().toString();
            confirmPassword =confirmPasswordBox.getText().toString();
            if(username.equals("")|| password.equals("")||confirmPassword.equals("")){
                throw new Exception();
            }
            else if(password.equals(confirmPassword) == false){
                Toast.makeText(Sign.this,R.string.confirm_password_Warn,Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception ex){
            Toast.makeText(Sign.this, R.string.info_incomplete_Warn, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean signInfo(){
        try{
            user = DataSupport.select("username").where("username = ?", username).find(User.class);
            if(user.isEmpty() == true){
              User signUser = new User();
              signUser.setUsername(username);
              signUser.setPassword(password);
              signUser.setDailyNum(1);
               try {
                   signUser.save();
               }catch (Exception ex){
                   return false;
               }
              return true;
            }
            else {
                Toast.makeText(Sign.this,R.string.register_rename_Warn,Toast.LENGTH_LONG).show();
                return false;
            }
        }catch (Exception ex){
            return false;
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_btn:

                if(getUserinfo() == true && signInfo() == true){
                    Toast.makeText(Sign.this,R.string.sign_successfully_Info,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Sign.this,MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Sign.this,R.string.sign_fail_Error,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(ShowPassword.isChecked()){
            passwordBox.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPasswordBox.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else {
            passwordBox.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPasswordBox.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
