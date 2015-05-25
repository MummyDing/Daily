package com.example.daily;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class atylist extends ListActivity implements OnClickListener{
    User user;
    List<Daily> user_daily;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
     int clickCounter=0,count=1;

    ImageView newDaily;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylist);
        listItems=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        findView();
        deleteDaily();
        autoInitView();
    }
    private void findView(){
        Intent intent = getIntent();
        user = DataSupport.find(User.class,intent.getIntExtra("user_id",1));
        newDaily = (ImageView) findViewById(R.id.new_daily);
        newDaily.setOnClickListener(this);
    }

    /**
     * Delete the Daily or Enter the Daily
     */
    private void deleteDaily(){
        ListView tmplv = (ListView) findViewById(android.R.id.list);
        tmplv.setAdapter(adapter);
        /***
         * Delet the Daily
         */
        tmplv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * Confirm Dialog
                 */
                AlertDialog.Builder adb = new AlertDialog.Builder(atylist.this);
                adb.setTitle(R.string.delete_Label+"?");
                adb.setMessage(getString(R.string.delete_confirm_Warn) +"\""+ listItems.get(i)+ "\"?");
                final int positionToRemove = i;
                adb.setPositiveButton(R.string.cancel_Label,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.setNegativeButton(R.string.confirm_Label, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Daily> daily = findByTitle(listItems.get(positionToRemove));
                        if(daily.isEmpty() == false)
                            DataSupport.delete(Daily.class,daily.get(0).getId());
                        listItems.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }
                }).show();
                return true;
            }
        });
        /**
         * Enter the Daily
         */
        tmplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int positionToRemove = i;
                List<Daily> daily = findByTitle(listItems.get(positionToRemove));
                //Open the Edit Page
                Intent intent = new Intent(atylist.this,atyedit.class);
                intent.putExtra("daily_id",daily.get(0).getId());
                intent.putExtra("user_id",user.getId());
                startActivity(intent);
            }
        });
    }


    /**
     * Init the Data
     */
    private void autoInitView(){
        count = user.getDailyNum();
        user_daily = DataSupport.select("title","user_id").where("user_id = ?", Integer.toString(user.getId())).find(Daily.class);
        for(Daily d :user_daily){
            listItems.add(d.getTitle());
            count++;
        }
        adapter.notifyDataSetChanged();
    }
    /**
     * Search the Daily By title
     * @param title
     * @return the Dailies
     */
    private List<Daily> findByTitle(String title){
        return DataSupport.select("id","title","user_id").where("title = ? and user_id = ?",title,Integer.toString(user.getId())).find(Daily.class);
    }

    /**
     * add a daily
     * @return the Daily Title
     */
    private String addItem(){
        Daily daily = new Daily();
        Date date = new Date();
        daily.setContent("");
        daily.setDate(date);
        while (findByTitle(getString(R.string.daily_Label)+Integer.toString(count)).isEmpty() == false) {
            count++;
        }
        daily.setTitle(getString(R.string.daily_Label)+Integer.toString(count));
        daily.save();
        user.getDailylist().add(daily);
        user.setDailyNum(count);
        user.save();
        return  getString(R.string.daily_Label)+Integer.toString(count);
    }

    /**
     * Create a new Daily
     * @param
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.new_daily:
                listItems.add(addItem());
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * Back to the Home Page
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(atylist.this,MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
