package com.example.davismaghanga.notetoself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private NoteAdapter mNoteAdapter;
    private boolean mSound;
    private int mAnimOption;
    private SharedPreferences mPrefs;




    public void createNewNote(Note n)
    {
        mNoteAdapter.addNote(n);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoteAdapter = new NoteAdapter();

        ListView listNote=(ListView) findViewById(R.id.listView);

        listNote.setAdapter(mNoteAdapter);

        //handles clicks on the listview
        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int whichItem, long id) {
//                create a temporary note which is a reference
//                to the note that has just been clicked
                Note tempNote = mNoteAdapter.getItem(whichItem);

                //create a new dialog window
                DialogShowNote dialog = new DialogShowNote();
                //send in reference to the note to be shown
                dialog.sendNoteSelected(tempNote);

                //show the dialog window with the note in it
                dialog.show(getFragmentManager(),"");

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == R.id.action_add)
        {
            DialogNewNote dialog = new DialogNewNote();
            dialog.show(getFragmentManager(),"");
            return true;
        }
        if (id== R.id.action_settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //this is the noteadapter class which is an inner class in this case since its defined inside another class
    public class NoteAdapter extends BaseAdapter
    {
        private JSONSerializer mSerializer;
    List<Note>noteList = new ArrayList<Note>();

    public NoteAdapter(){
        mSerializer = new JSONSerializer("NoteToSelf.json",MainActivity.this.getApplicationContext());
        try{
            noteList=mSerializer.load();
        }catch (Exception e){
            noteList= new ArrayList<Note>();
            Log.e("Error loading notes: ","",e);
        }
    }

    public void saveNotes(){
        try {
            mSerializer.save(noteList);
        }catch (Exception e){
            Log.e("Error saving notes","",e);
        }
    }
    //end of the saveNotes method

    @Override
        public int getCount(){
        return noteList.size();

    }
    @Override
            public Note getItem(int whichItem)
        {
            return noteList.get(whichItem);
        }
        @Override
                public long getItemId(int whichItem){
                return whichItem;
    }
    @Override
        public View getView(int whichItem, View view, ViewGroup viewGroup){
        //implement this method next
        //has view been inflated already
        if(view==null)
        {
            //if not do so here
            //first create a layout inflater
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //now instantiate view using inflater.inflate
            //using the listitem layout
            view = inflater.inflate(R.layout.listitem, viewGroup, false);
            //the false parameter is necessary because of the way we want to use listitem
        }//end if
        //grab reference to all our textviews and image widgets
        TextView txtTitle= (TextView)view.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescription);
        ImageView ivImportant = (ImageView)view.findViewById(R.id.imageViewImportant);
        ImageView ivTodo = (ImageView)view.findViewById(R.id.imageViewTodo);
        ImageView ivIdea = (ImageView)view.findViewById(R.id.imageViewIdea);

        //hide any image view widgets that are not relevant
        Note tempNote =noteList.get(whichItem);
        if(!tempNote.isImportant()){
            ivImportant.setVisibility(View.GONE);
        }
        if(!tempNote.isTodo()){
            ivTodo.setVisibility(View.GONE);
        }
        if(!tempNote.isIdea()){
            ivIdea.setVisibility(View.GONE);
        }

        //add the text to the title and description
        txtTitle.setText(tempNote.getTitle());
        txtDescription.setText(tempNote.getDescription());


        return view;
    }
    public void addNote (Note n)
    {
        noteList.add(n);
        notifyDataSetChanged();
    }

    }


    @Override
    protected void onResume(){
        super.onResume();
        mPrefs = getSharedPreferences("Note to self",MODE_PRIVATE);
        mSound= mPrefs.getBoolean("sound",true);
        mAnimOption= mPrefs.getInt("anim option",SettingsActivity.FAST);

    }
    @Override
    protected void onPause(){
        super.onPause();
        mNoteAdapter.saveNotes();
    }




}
