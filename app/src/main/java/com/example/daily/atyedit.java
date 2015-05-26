package com.example.daily;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;


public class atyedit extends Activity implements OnClickListener{
    EditText content_box,title;
    TextView  modify_date,title_edit_Btn,title_back_Btn;
    Daily daily;
    AlertDialog.Builder reannameDiag,exitADB;

    int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Delete the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_atyedit);
        findView();
        initData();
    }
    private void findView(){
        title = (EditText) findViewById(R.id.daily_title);
        title_edit_Btn = (TextView) findViewById(R.id.edit_menu_Btn);
        title_back_Btn = (TextView) findViewById(R.id.back_menu_Btn);
        content_box = (EditText) findViewById(R.id.edit_box);
        modify_date = (TextView) findViewById(R.id.edit_time);
        title_edit_Btn.setOnClickListener(this);
        title_back_Btn.setOnClickListener(this);

    }
    private void initData(){
        Intent intent = getIntent();
        daily = DataSupport.find(Daily.class,intent.getIntExtra("daily_id", 1));
        user_id = intent.getIntExtra("user_id",1);
        title.setText(daily.getTitle());
        content_box.setText(daily.getContent());
        modify_date.setText(getString(R.string.modify_time_Label)+daily.getDate().toString());
        content_box.setInputType(InputType.TYPE_NULL);
        content_box.setSingleLine(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_atyedit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_content:
                title.setEnabled(true);
                content_box.setInputType(InputType.TYPE_CLASS_TEXT);
                content_box.setSingleLine(false);
                title.setSelection(title.getText().toString().length());
                content_box.setSelection(content_box.getText().toString().length());
                content_box.setHint("@string/write_it_Info");
                content_box.requestFocus();
                break;
        }
        return true;
    }

    /**
     * Search the Daily By title
     * @param tmptitle
     * @return the Dailies
     */
    private List<Daily> findByTitle(String tmptitle){
        return DataSupport.select("id","title","user_id").where("title = ? and user_id = ?",tmptitle,Integer.toString(user_id)).find(Daily.class);
    }
    boolean flag = true;

    /**
     * show a Rename Warning Dialog.
     * if user want to Change to this name,return true; otherwise,return false
     * @param tmpTilte
     * @param title
     * @return
     */
    private boolean renameDialog(String tmpTilte,String title){
        flag = true;
        reannameDiag = new AlertDialog.Builder(atyedit.this);
        reannameDiag.setTitle(getString(R.string.rename_Warn) + "\"" + tmpTilte + "\"" + "\n" + getString(R.string.rename_Info) + "\"" + title + "\"" + "?");
        reannameDiag.setPositiveButton(R.string.cancel_Label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flag = false;
                backToList();
            }
        });
        reannameDiag.setNeutralButton(R.string.confirm_Label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backToList();
            }
        });
        return flag;
    }
    /**
     * update the title , if the title was be used,return false,otherwise,
     * return true
     * rename (Update the Title)
     * @param
     */
    private boolean updateTitle(String tmpTitle){
        int titleNum = 0;
        while (true){
            if(titleNum == 0 && findByTitle(tmpTitle).isEmpty() == true)break;
            else if(titleNum != 0 && findByTitle(tmpTitle+Integer.toString(titleNum)).isEmpty() == true) break;
            titleNum++;
        }

        Daily updateDaily = new Daily();
        if(titleNum == 0)
            updateDaily.setTitle(tmpTitle);
        else {
            if(renameDialog(tmpTitle,tmpTitle + Integer.toString(titleNum))) {
                updateDaily.setTitle(tmpTitle + Integer.toString(titleNum));
                updateDaily.update(daily.getId());
                return false;
            }
            else return true;
        }
        updateDaily.update(daily.getId());
        return true;
    }

    /**
     * exit Dialog : show the "Save" MSG
     */
    private void exitDialog(){
        exitADB = new AlertDialog.Builder(atyedit.this);
        exitADB.setTitle(R.string.savechange_confrim_Warn);
        exitADB.setPositiveButton(R.string.cancel_Label,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        exitADB.setNeutralButton(R.string.save_Label,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(isTitleChanged()){
                    if(updateTitle(title.getText().toString())==false){
                        reannameDiag.show();
                        return;
                    }
                }
                if(isContentChanged()){
                    Daily updateDaily = new Daily();
                    updateDaily.setContent(content_box.getText().toString());
                    updateDaily.setDate(new Date());
                    updateDaily.update(daily.getId());
                }
                backToList();
            }
        });
        exitADB.setNegativeButton(R.string.insave_Label,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backToList();

            }
        }).create();
        exitADB.show();
    }

    private boolean isTitleChanged(){
        if(daily.getTitle().equals(title.getText().toString()))
            return false;
        return true;
    }
    private boolean isContentChanged(){
        if(daily.getContent().equals(content_box.getText().toString()))
            return false;
        return true;
    }
    /**
     * check the editBox changed or not
     * @return
     */
    private boolean isTextChanged(){
        if(isTitleChanged()||isContentChanged()){
            return true;
        }
        return false;
    }

    /**
     * Back to the List Activity
     */
    private void backToList(){
        Intent intent = new Intent(atyedit.this,atylist.class);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isTextChanged())exitDialog();
            else backToList();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Back btn and Edit btn
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_menu_Btn:
                title.setEnabled(true);
                content_box.setInputType(InputType.TYPE_CLASS_TEXT);
                content_box.setSingleLine(false);
                title.setSelection(title.getText().toString().length());
                content_box.setSelection(content_box.getText().toString().length());
                content_box.setHint(R.string.write_it_Info);
                content_box.requestFocus();
                break;
            case R.id.back_menu_Btn:
                if(isTextChanged())exitDialog();
                else backToList();
                break;
        }
    }
}
