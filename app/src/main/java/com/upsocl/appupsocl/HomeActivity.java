package com.upsocl.appupsocl;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.upsocl.appupsocl.domain.Interests;
import com.upsocl.appupsocl.keys.CustomerKeys;
import com.upsocl.appupsocl.keys.Preferences;
import com.upsocl.appupsocl.notification.QuickstartPreferences;
import com.upsocl.appupsocl.ui.DownloadImage;
import com.upsocl.appupsocl.ui.adapters.PagerAdapter;
import com.upsocl.appupsocl.ui.fragments.BookmarksFragment;
import com.upsocl.appupsocl.ui.fragments.HelpFragment;
import com.upsocl.appupsocl.ui.fragments.InterestsFragment;
import com.upsocl.appupsocl.ui.fragments.PreferencesFragment;


import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener  {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SearchView searchView;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TabLayout tabs;
    private ViewPager viewPager;
    private FrameLayout frameLayout;
    private SharedPreferences prefs, prefsInterests, prefsUser;
    private ArrayList<Interests> categoryArrayList;
    private Gson gs = new Gson();
    private boolean isReceiverRegistered;

    private View headerView;
    private TextView tv_username;
    private ActionBarDrawerToggle toggle;

    private PagerAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    boolean exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Content Faceboook
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Fin content facebook

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_username = (TextView) headerView.findViewById(R.id.tv_username);
        frameLayout= (FrameLayout) findViewById(R.id.content_frame);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        prefs =  getSharedPreferences(Preferences.BOOKMARKS, Context.MODE_PRIVATE);
        prefsInterests =  getSharedPreferences(Interests.INTERESTS, Context.MODE_PRIVATE);
        prefsUser =  getSharedPreferences(Preferences.DATA_USER, Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);
        setDrawer(toolbar);

        tabs = createTabLayout();

        uploadPager();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        uploadPreferences();
        selectDrawerOption();
        selectTabsOption();

        //GOOGLE
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        //GOOGLE

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }

    private void uploadPager() {
        adapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        adapter.setPrefs(prefsInterests);
        adapter.setHome(true);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    }

    private void setDrawer(Toolbar toolbar) {
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }



    private void selectTabsOption() {
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void selectDrawerOption() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {


                boolean fragmentTransacction = false;
                Fragment fragment = null;
                FragmentManager fragmentManager =  null;
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                if (countPreference()<=3){
                    Toast.makeText(HomeActivity.this, R.string.msg_select_category, Toast.LENGTH_SHORT).show();
                    drawer.closeDrawers();
                    return false;
                }

                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragmentTransacction = false;
                        tabs.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.INVISIBLE);
                        uploadPager();

                        getSupportActionBar().setTitle(item.getTitle());
                        break;

                    case R.id.nav_bookmarks:
                        visibleGoneElement();
                        fragment =  BookmarksFragment.newInstance(prefs);
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();

                        fragmentTransacction = true;

                        break;
                    case R.id.nav_interests:

                        visibleGoneElement();
                        SharedPreferences prefs =  getSharedPreferences(Interests.INTERESTS, Context.MODE_PRIVATE);
                        fragment =  new InterestsFragment( prefs);
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();

                        fragmentTransacction = true;
                        break;
                    case R.id.nav_manage:

                        visibleGoneElement();
                        fragment =  new PreferencesFragment(prefsUser);

                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();

                        fragmentTransacction = true;

                        break;
                    case R.id.nav_us:

                        visibleGoneElement();
                        fragment =  new HelpFragment();
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();

                        fragmentTransacction = true;

                        break;

                }
                if (fragmentTransacction){
                    getSupportActionBar().setTitle(item.getTitle());

                }

                drawer.closeDrawers();
                return true;
            }
        });
    }

    private int countPreference() {
        SharedPreferences prefs =  getSharedPreferences(Interests.INTERESTS, Context.MODE_PRIVATE);
        Map<String, ?> map = prefs.getAll();
        map.size();
        int i = 1;

        for (Map.Entry<String, ?> e: map.entrySet()) {

            if (e.getKey().equals(Interests.INTERESTS_SIZE)==false && e.getValue().equals(true)){
               i++;
            }
        }

        return i;

    }

    private void visibleGoneElement() {
        frameLayout.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.INVISIBLE);
        tabs.setVisibility(View.GONE);
        viewPager.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.GONE);
    }

    @NonNull
    private TabLayout createTabLayout() {
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.forYou));
        tabs.addTab(tabs.newTab().setText(R.string.lastNews));
        tabs.addTab(tabs.newTab().setText(R.string.green));
        tabs.addTab(tabs.newTab().setText(R.string.creativity));
        tabs.addTab(tabs.newTab().setText(R.string.women));
        tabs.addTab(tabs.newTab().setText(R.string.food));
        tabs.addTab(tabs.newTab().setText(R.string.populary));
        tabs.addTab(tabs.newTab().setText(R.string.quiz));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        return tabs;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        // Associate searchable configuration with the SearchView
        MenuItem menuItem =  menu.findItem(R.id.menu_item_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()){
            case R.id.nav_bookmarks:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_interests:

                break;
            case R.id.nav_manage:

                break;
            case R.id.nav_us:
                drawer.openDrawer(GravityCompat.START);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_notification:
                goNotifications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void goActivityLogin() {
        Intent login = new Intent(HomeActivity.this, CreatePerfil.class);
        startActivity(login);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    public void btnNotification(View view){

        goNotifications();
    }

    private void goNotifications() {

        if (getNotificationId()!= null){
            visibleGoneElement();
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(HomeActivity.this, R.string.msg_notification_empty, Toast.LENGTH_SHORT).show();
    }

    private String getNotificationId() {

        SharedPreferences prefs =  getSharedPreferences(Preferences.NOTIFICATIONS, Context.MODE_PRIVATE);
        return  prefs.getString(Preferences.NOTI_DATA,null);
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private void uploadPreferences() {

        SharedPreferences prefs2 =  getSharedPreferences(Preferences.DATA_USER, Context.MODE_PRIVATE);
        String userName = null, userLastName = null, imagenURL=null;

        userName = prefs2.getString(CustomerKeys.DATA_USER_FIRST_NAME,"Nombre");
        userLastName = prefs2.getString(CustomerKeys.DATA_USER_LAST_NAME,"Apellido");
        imagenURL = prefs2.getString(CustomerKeys.DATA_USER_IMAGEN_URL,null);

        tv_username.setText(userName +" " +userLastName);
        categoryArrayList = gs.fromJson(getIntent().getStringExtra("listCategory"), ArrayList.class);
        if (imagenURL!=null)
            new DownloadImage((ImageView)headerView.findViewById(R.id.img_profile),getResources()).execute(imagenURL);
    }

    public void logout(View view){
        createSimpleDialog();
        if (exit){
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken((AccessToken) null);
            Profile.setCurrentProfile((Profile)null);
            goActivityLogin();
        }
    }

    //google
    public void  logoutGoogle(View view){
        if (mGoogleApiClient.isConnected()){
            createSimpleDialog();
            if (exit){
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                goActivityLogin();
            }
        }
    }

    //end google

    //twitter
    public void  logoutTwitter(View view){
        createSimpleDialog();
       if (exit){
           goActivityLogin();
       }
    }
    //end twitter

    //Create dialogeMessage
    //Create dialogeMessage
    public void createSimpleDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cerrar sesión");
        dialog.setMessage("Esta seguro que desea cerrar sessión?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exit = true;
                        goActivityLogin();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("aun no finaliza");
                    }
                });

        AlertDialog alertDialog =  dialog.create();
        alertDialog.show();
    }
    //End dialogeMessage
}
