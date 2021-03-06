package android.example.medicinescheduerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.medicinescheduerapp.ui.LoadDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header =(TextView)headerView.findViewById(R.id.textView);
        header.setText("Logged Out");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Menu nav_menu = navigationView.getMenu();


        SharedPreferences info = getApplicationContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        String isLogged = info.getString("loggedIn","No");
        String isDoc = info.getString("isDoctor","No");
        String isPat = info.getString("isPatient","No");

        if(isLogged.equals("Yes")){
            if(isDoc.equals("Yes")){
                nav_menu.findItem(R.id.nav_doctor).setVisible(true);
                nav_menu.findItem(R.id.nav_patient).setVisible(false);
                nav_menu.findItem(R.id.nav_account).setVisible(false);
                nav_menu.findItem(R.id.nav_pat_pres).setVisible(false);
                nav_menu.findItem(R.id.nav_doc_prescriptions).setVisible(true);
                header.setText("Logged In");
            }
            if(isPat.equals("Yes")){
                nav_menu.findItem(R.id.nav_patient).setVisible(true);
                nav_menu.findItem(R.id.nav_doctor).setVisible(false);
                nav_menu.findItem(R.id.nav_account).setVisible(false);
                nav_menu.findItem(R.id.nav_pat_pres).setVisible(true);
                nav_menu.findItem(R.id.nav_doc_prescriptions).setVisible(false);
                header.setText("Logged In");
            }
        }
        else {
            nav_menu.findItem(R.id.nav_doctor).setVisible(false);
            nav_menu.findItem(R.id.nav_patient).setVisible(false);
            nav_menu.findItem(R.id.nav_pat_pres).setVisible(false);
            nav_menu.findItem(R.id.nav_doc_prescriptions).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout :
                SharedPreferences info = getApplicationContext().getSharedPreferences("info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = info.edit();
                editor.putString("loggedIn","No");
                editor.apply();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
