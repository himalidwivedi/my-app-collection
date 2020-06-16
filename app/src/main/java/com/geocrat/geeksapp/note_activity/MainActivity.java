package com.geocrat.geeksapp.note_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geocrat.geeksapp.Splash;
import com.geocrat.geeksapp.ath.LoginActivity;
import com.geocrat.geeksapp.ath.SigninActivity;
import com.geocrat.geeksapp.note_fragment.AddNoteFragment;
import com.geocrat.geeksapp.note_fragment.NotesFragment;
import com.geocrat.geeksapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    CoordinatorLayout coordinator;
    AppBarLayout appbar;
    Toolbar toolbar;
    NavigationView navView;
    MenuItem previousItem;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        drawer = findViewById(R.id.drawer);
        coordinator = findViewById(R.id.coordinator);
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.navView);

        View header_view = navView.getHeaderView(0);
        TextView username = header_view.findViewById(R.id.username);
        TextView email = header_view.findViewById(R.id.email);

        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            username.setText("Temporary user");
            email.setVisibility(View.GONE);
        }else{
            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        setToolbar();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawer, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(previousItem != null){
                    previousItem.setChecked(false);
                    previousItem.setCheckable(false);
                }
                menuItem.setCheckable(true);
                menuItem.setChecked(true);
                previousItem = menuItem;

                switch (menuItem.getItemId()) {
                    case R.id.notes:
                        openNotes();
                        drawer.closeDrawers();
                        break;

                    case R.id.addNote:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AddNoteFragment()).addToBackStack("add notes").commit();
                        drawer.closeDrawers();
                        break;

                    case R.id.sync:
                        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "You are already connected", Toast.LENGTH_SHORT).show();
                        }
                        drawer.closeDrawers();
                        break;

                    case R.id.logout:
                        checkUser();
                        drawer.closeDrawers();
                        break;
                }
                return true;
            }
        });

        openNotes();

        getIncomingIntent();
    }

    private void checkUser(){
        //check if the user is real or not
        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            displayAlert();
        }
        else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, Splash.class));
        }
    }

    private void displayAlert(){
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Exit?").setMessage("You are logged in with the temporary account. Logging out will delete all the notes")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: need to delete all the notes created by annonymous dialog

                        //TODO: need to delete the annonymous user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), Splash.class));
                                finish();
                            }
                        });
                    }
                });
        warning.create();
        warning.show();
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AddNoteFragment()).commit();
        }
    }

    public void openNotes(){
        NotesFragment frag = new NotesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, frag).addToBackStack("notes").commit();
        drawer.closeDrawers();
        navView.setCheckedItem(R.id.notes);
        getSupportActionBar().setTitle("Notes");
    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Geek's App");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
