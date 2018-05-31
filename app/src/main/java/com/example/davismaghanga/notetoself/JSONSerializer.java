package com.example.davismaghanga.notetoself;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JSONSerializer
{
    private String mFilename;
    private Context mContext;

    //all the rest of the code of the class goes here
    public  JSONSerializer (String fn, Context con)
        {
            mFilename=fn;
            mContext = con;

        }

        public void save(List<Note> notes) throws IOException,JSONException{
        //make an array in json format
            JSONArray jArray = new JSONArray();
            //and load it with notes
            for (Note n: notes)
                jArray.put(n.convertToJSON());
            //now to write it to the private disk space of our app
            Writer writer =null;
            try{
                OutputStream out =mContext.openFileOutput(mFilename,mContext.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(jArray.toString());
            } finally {
                if (writer!=null){
                    writer.close();
                }
            }
        }

        public ArrayList <Note> load() throws IOException, JSONException{
        ArrayList<Note> noteList = new ArrayList<Note>();
            BufferedReader reader= null;
            try{
                InputStream in = mContext.openFileInput(mFilename);
                reader= new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    jsonString.append(line);
                }

                JSONArray jArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                for(int i=0;i<jArray.length();i++){
                    noteList.add(new Note(jArray.getJSONObject(i)));
                }
            } catch (FileNotFoundException e){
                //we can ignore this once since it happens
                // when we start fresh. you could add a log here
            }finally {// this will always run
                if (reader!=null){
                    reader.close();
                }

                return noteList;
            }
        }
}


