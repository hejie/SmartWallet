package com.nicholas.smartwallet.ui;

import java.util.ArrayList;

import com.nicholas.smartwallet.model.SlideMenuItem;
import com.nicholas.smartwallet.ui.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends Activity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    // slide menu title
    private CharSequence mSlideMenuTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] slideMenuTitles;
    private TypedArray slideMenuIcons;
 
    private ArrayList<SlideMenuItem> mSlideMenuItems;
    private SlideMenuAdapter mSlideMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        mTitle = mSlideMenuTitle = getTitle();
        
        // load slide menu items
        slideMenuTitles = getResources().getStringArray(R.array.slide_main_items);
 
        // slide menu icons from resources
        slideMenuIcons = getResources().obtainTypedArray(R.array.slide_main_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidemenu);
 
        mSlideMenuItems = new ArrayList<SlideMenuItem>();
 
        // adding slide menu items to array
        // Overview
        mSlideMenuItems.add(new SlideMenuItem(slideMenuTitles[0], slideMenuIcons.getResourceId(0, -1)));
        // Charts
        mSlideMenuItems.add(new SlideMenuItem(slideMenuTitles[1], slideMenuIcons.getResourceId(1, -1)));
        // Reports
        mSlideMenuItems.add(new SlideMenuItem(slideMenuTitles[2], slideMenuIcons.getResourceId(2, -1)));
        
        // Recycle the typed array
        slideMenuIcons.recycle();
 
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        // setting the slide menu adapter
        mSlideMenuAdapter = new SlideMenuAdapter(getApplicationContext(),mSlideMenuItems);
        mDrawerList.setAdapter(mSlideMenuAdapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_action_drawer, //slide menu toggle icon
                R.string.app_name, // slide drawer open - description for accessibility
                R.string.app_name // slide drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mSlideMenuTitle);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }
    
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected slide menu item
            displayView(position);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_transaction:
        	startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Displaying fragment view for selected slide menu item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
        case 0:
            fragment = new OverviewFragment();
            break;
        case 1:
            fragment = new ChartFragment();
            break;
        case 2:
            fragment = new ReportFragment();
            break;
        default:
            break;
        }
 
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(slideMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	public void onItemClick(int mPosition) {
		// TODO Auto-generated method stub
		
	}
    
}
