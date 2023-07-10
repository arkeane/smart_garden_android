package com.maiot.smart_garden_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.maiot.smart_garden_android.fragments.AddPlantFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.toolbar); //recupero la toolbar
        setSupportActionBar(toolbar); //setto la toolbar

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); //setto il listener per il menu laterale

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close); //creo il toggle per aprire e chiudere il menu laterale
        drawerLayout.addDrawerListener(toggle); //setto il listener per il menu laterale
        toggle.syncState();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPlantFragment()).commit(); //setto il fragment iniziale
            navigationView.setCheckedItem(R.id.nav_plant_add); //setto il menu laterale iniziale
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int nav_add_plant = R.id.nav_plant_add;

        if (item.getItemId() == nav_add_plant) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPlantFragment()).commit(); //setto il fragment iniziale
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) { //se il menu laterale è aperto
            drawerLayout.closeDrawer(GravityCompat.START); //lo chiudo
        } else {
            super.onBackPressed(); //altrimenti chiudo l'app
        }
    }
}