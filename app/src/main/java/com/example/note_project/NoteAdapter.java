package com.example.note_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {

    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleTextView.setText(note.title != null ? note.title : "No Title");
        holder.contentTextView.setText(note.content != null ? note.content : "No Content");
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        String docId = this.getSnapshots().getSnapshot(position).getId();

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, DetailNoteActivity.class);
            intent.putExtra("title", note.title);
            intent.putExtra("content", note.content);
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

        // Xử lý nút "Edit"
        holder.btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailNoteActivity.class);
            intent.putExtra("title", note.title);
            intent.putExtra("content", note.content);
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });

        // Xử lý nút "Delete"
        holder.btn_delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete?")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        getSnapshots().getSnapshot(position).getReference().delete();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, contentTextView, timestampTextView;
        ImageView btn_edit, btn_delete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}