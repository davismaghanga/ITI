package com.example.davismaghanga.notetoself;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogShowNote extends DialogFragment
{
    private Note mNote;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        // receive note from main activity


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.dialog_show_note,null);

        //reference
        TextView txtTitle = (TextView)dialogView.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView)dialogView.findViewById(R.id.txtDescription);
        txtTitle.setText(mNote.getTitle());
//        txtDescription = (EditText)dialogView.findViewById(R.id.txtDescription);
        txtDescription.setText(mNote.getDescription());

        ImageView ivImportant = (ImageView)dialogView.findViewById(R.id.imageViewImportant);
        ImageView ivTodo = (ImageView)dialogView.findViewById(R.id.imageViewTodo);
        ImageView ivIdea = (ImageView)dialogView.findViewById(R.id.imageViewIdea);

        if (!mNote.isImportant()){
            ivImportant.setVisibility(View.GONE);
        }
        if (!mNote.isTodo()){
            ivTodo.setVisibility(View.GONE);
        }
        if (!mNote.isIdea()){
            ivIdea.setVisibility(View.GONE);
        }
        Button BtnOk = (Button)dialogView.findViewById(R.id.BtnOk);
        builder.setView(dialogView).setMessage("Your Note");
        BtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }
    public void sendNoteSelected(Note noteSelected)
    {
        mNote=noteSelected;
    }
}
