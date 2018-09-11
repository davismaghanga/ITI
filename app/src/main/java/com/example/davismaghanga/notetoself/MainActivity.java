package com.example.davismaghanga.notetoself;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    {
        Animation mAnimFlash;
        Animation mFadeIn;

        int mIdBeep =-1;
        SoundPool mSp;

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

        // instantiate our soundpool with respect to the version of android the device is using
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else{
            mSp= new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }

        try{
            //create objects of the 2 required classes
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;

            //Load our fx in  memory  ready for use
            descriptor =assetManager.openFd("beep.ogg");
            mIdBeep =mSp.load(descriptor,0);
        } catch (IOException e){

            // print error message to console when there is a problem reading the sound file
            Log.e("error","failed to load sound files");
        }


        //we create a reference to the noteadapter object, we also create a reference to the listview so that
        //we bind them together. this helps tell the list view that we have a list of notes and we want them
        // arranged in your list view. (the base adapter class does this for us in the background)
        mNoteAdapter = new NoteAdapter();

        ListView listNote=(ListView) findViewById(R.id.listView);

        listNote.setAdapter(mNoteAdapter);

        //so we can long click
        listNote.setLongClickable(true);

        //now to detect long clicks and delete the note

        listNote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //ask noteadapter to delete this entry
                mNoteAdapter.deleteNote(position);
                return true;
            }
        });

        //handles clicks on the listview
        listNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int whichItem, long id) {
//                create a temporary note which is a reference
//                to the note that has just been clicked

                if(mSound){
                    mSp.play(mIdBeep,1,1,0,0,1);
                }


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
//            if not do so here
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


        //the first line shown below is for getting that particular note(list item) that we want displayed eventually
        Note tempNote =noteList.get(whichItem);


        //check if  the note is not important using a method we defined earlier, if it is not important then remove it from the list item view
        if(!tempNote.isImportant()){
            ivImportant.setVisibility(View.GONE);
        }
        if(!tempNote.isTodo()){
            ivTodo.setVisibility(View.GONE);
        }
        if(!tempNote.isIdea()){
            ivIdea.setVisibility(View.GONE);
        }

        //add the text to the title and description using their respective methods
        txtTitle.setText(tempNote.getTitle());
        txtDescription.setText(tempNote.getDescription());


        return view;
    }
    public void addNote (Note n)
    {
        noteList.add(n);
        notifyDataSetChanged();
    }

    public void deleteNote(int n)
    {

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
