package com.example.note_project;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    EditText searchEditText;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recycler_view);
        menuBtn = findViewById(R.id.menu_btn);
        searchEditText = findViewById(R.id.searchInput);
        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, DetailNoteActivity.class)));
        menuBtn.setOnClickListener((v) -> showMenu());
        setupRecyclerView();
        // Thêm TextWatcher để lắng nghe thay đổi trong trường tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    Utility.showToast(MainActivity.this, "Logout successfully!Hope to see you again soon!!!");
                    finish();
                    return true;
                }
                return false;
            }
        });

        };


    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForNote().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note>options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    void searchNotes(String title) {
        Query query;
        if (title.isEmpty()) {
            query = Utility.getCollectionReferenceForNote()
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            String endTitle = title + '\uf8ff'; // Kí tự đặc biệt giúp mở rộng phạm vi tìm kiếm
            query = Utility.getCollectionReferenceForNote()
                    .whereGreaterThanOrEqualTo("title", title)
                    .whereLessThanOrEqualTo("title", endTitle)
                    .orderBy("title") // Sắp xếp theo tiêu đề để tìm chính xác chuỗi
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        noteAdapter.updateOptions(options);
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}