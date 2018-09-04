package com.example.davismaghanga.notetoself;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class DialogNewNote extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_note,null);

        final EditText editTitle = (EditText)dialogView.findViewById(R.id.editTitle);
        final EditText editDescription = (EditText)dialogView.findViewById(R.id.editDescription);
        final CheckBox checkBoxIdea = (CheckBox)dialogView.findViewById(R.id.checkBoxIdea);
        final CheckBox checkBoxImportant = (CheckBox)dialogView.findViewById(R.id.checkBoxImportant);
        final CheckBox checkBoxTodo = (CheckBox)dialogView.findViewById(R.id.checkBoxTodo);

        Button btnCancel =(Button)dialogView.findViewById(R.id.btnCancel);
        Button btnOK = (Button)dialogView.findViewById(R.id.btnOK);

        builder.setView(dialogView).setMessage("Add a new Note");
        //handle the cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a new note
                Note newNote = new Note();
                //set its variables to match the user's entries in the form
                newNote.setTitle(editTitle.getText().toString());
                newNote.setDescription(editDescription.getText().toString());
                newNote.setIdea(checkBoxIdea.isChecked());
                newNote.setTodo(checkBoxTodo.isChecked());
                newNote.setImportant(checkBoxImportant.isChecked());

                //get reference to the main activity by creating an object of the mainactivity
                MainActivity callingActivity = (MainActivity)getActivity();

                //pass new note back to main activity. we can use the method since it was defined in the mainActivity class and we are using
                //its object
                callingActivity.createNewNote(newNote);
                //quit the dialog
                dismiss();
            }
        });
        return builder.create();
    }
}
