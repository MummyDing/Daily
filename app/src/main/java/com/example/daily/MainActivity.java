package com.example.daily;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends Activity implements OnClickListener,OnCheckedChangeListener{

    EditText usernameBox,passwordBox;
    Button loginBtn,signBtn;
    String username,password;
    CheckBox isRememberData;
    SharedPreferences rememberedData;
    List<User> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findView();
        compeletePassword();
    }
    private void findView(){
        usernameBox = (EditText) findViewById(R.id.username_box);
        passwordBox = (EditText) findViewById(R.id.password_box);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signBtn = (Button) findViewById(R.id.sign_btn);
        isRememberData = (CheckBox) findViewById(R.id.remeber_cb);

        rememberedData = getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        loginBtn.setOnClickListener(this);
        signBtn.setOnClickListener(this);
        isRememberData.setOnCheckedChangeListener(this);
    }

    private void compeletePassword(){
        if(rememberedData.getBoolean("ISCHECK",false)){
            usernameBox.setText(rememberedData.getString("USER_NAME",""));
            passwordBox.setText(rememberedData.getString("PASSWORD",""));
        }
    }

    private boolean getUserinfo(){
        try{
            username = usernameBox.getText().toString();
            password = passwordBox.getText().toString();
            if(username.equals("")|| password.equals("")){
                throw new Exception();
            }
        }catch (Exception ex){
            Toast.makeText(MainActivity.this,R.string.info_incomplete_Warn,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /***
     * Check the info is right or not
      * @return
     */
    private boolean verifyInfo(){
        try{
            user = DataSupport.select("username","password","id").where("username = ?", username).find(User.class);

           if(user.isEmpty() == false){
               if(user.get(0).getPassword().equals(password)){
                   Toast.makeText(getBaseContext(),R.string.login_successfully_Info,Toast.LENGTH_SHORT).show();
                   return true;
               }
               else {
                   Toast.makeText(getBaseContext(),R.string.wrong_password_Error,Toast.LENGTH_SHORT).show();
                   return false;
               }
           }
           else {
               Toast.makeText(MainActivity.this,R.string.wrong_username_Error,Toast.LENGTH_SHORT).show();

           }

        }catch (Exception ex){

               return false;
        }
        return false;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
               if(getUserinfo() == true && verifyInfo() == true){
                   if(isRememberData.isChecked()){
                       //Record the User Info
                       Editor editor = rememberedData.edit();
                       editor.putString("USER_NAME", username);
                       editor.putString("PASSWORD",password);
                       editor.commit();
                   }
                   else {
                       Editor editor = rememberedData.edit();
                       editor.putString("USER_NAME", "");
                       editor.putString("PASSWORD","");
                       editor.commit();
                   }
                   // login  jump to list Activity
                   Intent intent = new Intent(MainActivity.this,atylist.class);
                   intent.putExtra("user_id",user.get(0).getId());
                   startActivity(intent);
           }
                break;
            case R.id.sign_btn:
                //jump to Register Page
                Intent intent = new Intent(MainActivity.this,Sign.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Back to the Desktop
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    /***
     * get the info about whether record the User
     * @param compoundButton
     * @param b
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(isRememberData.isChecked()){
         rememberedData.edit().putBoolean("ISCHECK",true).commit();
        }
        else {
         rememberedData.edit().putBoolean("ISCHECK",false).commit();
        }
    }
}
