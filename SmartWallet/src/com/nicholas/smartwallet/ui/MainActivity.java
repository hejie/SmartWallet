package com.nicholas.smartwallet.ui;

import java.util.ArrayList;

import com.nicholas.smartwallet.model.*;
import com.nicholas.smartwallet.ui.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class MainActivity extends Activity{
	// static value
	private static final int transactionActivityID = 0;
	private static final int nfcActivityID = 1;
	protected static final int RESULT_SPEECH = 2;
	
	// UI components
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    
    // slide menu title
    private CharSequence mSlideMenuTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu sections
    private String[]  slideMenuSectionArr;
    private ArrayList<String> mSlideMenuSectionTitles = new ArrayList<String>();
    private ArrayList<Integer> mSlideMenuSectionPos = new ArrayList<Integer>();
    private ArrayList<Section> mSlideMenuSections;
    // slide menu items
    private String[] slideMenuMainArr, slideMenuViewArr, slideMenuToolsArr, slideMenuOptionsArr;
    private TypedArray slideMenuMainIcons, slideMenuViewIcons, slideMenuToolsIcons, slideMenuOptionsIcons;
    private ArrayList<String> mSlideMenuItemTitles = new ArrayList<String>();
    private ArrayList<SlideMenuItem> mSlideMenuItems;
    // slide menu adapter
    private SlideMenuAdapter mSlideMenuAdapter;

    
	public void setupActionBar()
	{
		// Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        mTitle = mSlideMenuTitle = getTitle();
        
        // load slide menu string
        slideMenuSectionArr= getResources().getStringArray(R.array.slide_sections);
        slideMenuMainArr = getResources().getStringArray(R.array.slide_main_items);
        slideMenuViewArr= getResources().getStringArray(R.array.slide_view_items);
        slideMenuToolsArr = getResources().getStringArray(R.array.slide_tools_items);
        slideMenuOptionsArr = getResources().getStringArray(R.array.slide_options_items);
 
        // slide menu icons from resources
        slideMenuMainIcons = getResources().obtainTypedArray(R.array.slide_main_icons);
        slideMenuViewIcons = getResources().obtainTypedArray(R.array.slide_view_icons);
        slideMenuToolsIcons = getResources().obtainTypedArray(R.array.slide_tools_icons);
        slideMenuOptionsIcons = getResources().obtainTypedArray(R.array.slide_options_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidemenu);
 
        mSlideMenuItems = new ArrayList<SlideMenuItem>();
 
        // adding slide menu items to array
        int curSectionPos = 0;
        // Main section
        mSlideMenuSectionPos.add(curSectionPos);
        mSlideMenuSectionTitles.add(slideMenuSectionArr[0]);
        for(int i=0; i < slideMenuMainArr.length; i++)
        {
        	mSlideMenuItems.add(new SlideMenuItem(slideMenuMainArr[i], slideMenuMainIcons.getResourceId(i, -1)));
        	mSlideMenuItemTitles.add(slideMenuMainArr[i]);
        }
        curSectionPos += slideMenuMainArr.length;
        // View section
        mSlideMenuSectionPos.add(curSectionPos);
        mSlideMenuSectionTitles.add(slideMenuSectionArr[1]);
        for(int i=0; i < slideMenuViewArr.length; i++)
        {
        	mSlideMenuItems.add(new SlideMenuItem(slideMenuViewArr[i], slideMenuViewIcons.getResourceId(i, -1)));
        	mSlideMenuItemTitles.add(slideMenuViewArr[i]);
        }
        curSectionPos += slideMenuViewArr.length;
//        // Tools section 
//        mSlideMenuSectionPos.add(curSectionPos);
//        mSlideMenuSectionTitles.add(slideMenuSectionArr[2]);
//        for(int i=0; i < slideMenuToolsArr.length; i++)
//        {
//        	mSlideMenuItems.add(new SlideMenuItem(slideMenuToolsArr[i], slideMenuToolsIcons.getResourceId(i, -1)));
//        	mSlideMenuItemTitles.add(slideMenuToolsArr[i]);
//		  }
//        curSectionPos += slideMenuToolsArr.length;
        // Options section  
        mSlideMenuSectionPos.add(curSectionPos);
        mSlideMenuSectionTitles.add(slideMenuSectionArr[3]);
//        for(int i=0; i < slideMenuOptionsArr.length; i++)
//        {
//        	mSlideMenuItems.add(new SlideMenuItem(slideMenuOptionsArr[i], slideMenuOptionsIcons.getResourceId(i, -1)));
//          mSlideMenuItemTitles.add(slideMenuOptionsArr[i]);
//    	  }
        mSlideMenuItems.add(new SlideMenuItem(slideMenuOptionsArr[2], slideMenuOptionsIcons.getResourceId(2, -1)));
        mSlideMenuItemTitles.add(slideMenuOptionsArr[2]);
        
        // adding sections to array
        mSlideMenuSections = new ArrayList<Section>();
		for (int i = 0; i < mSlideMenuSectionPos.size(); i++)
		{
			mSlideMenuSections.add(new Section(mSlideMenuSectionPos.get(i), mSlideMenuSectionTitles.get(i)));
		}
        
        // Recycle the typed array
        slideMenuMainIcons.recycle();
        slideMenuViewIcons.recycle();
        slideMenuToolsIcons.recycle();
        slideMenuOptionsIcons.recycle();
        
        
        // setting the slide menu adapter
        mSlideMenuAdapter = new SlideMenuAdapter(this,mSlideMenuItems);
		/**** assign the Listview to the SimpleSectionedAdapter ***/
		SimpleSectionedListAdapter sectionedSMAdapter = new SimpleSectionedListAdapter(getApplicationContext(), mSlideMenuAdapter,
				R.layout.listitem_slidemenu_section, R.id.slideSection_text);
		sectionedSMAdapter.setSections(mSlideMenuSections.toArray(new Section[0]));
        mDrawerList.setAdapter(sectionedSMAdapter);
        
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
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fragmentManager = getFragmentManager();

        setupActionBar();
		
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            chooseFragment(0);
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
        	  //Create the intent
        	  Intent transactionAcvitiyIntent = new Intent(this, TransactionActivity.class);
        	  //Create the bundle
        	  Bundle transBundle = new Bundle();
        	  //Add data to bundle
        	  transBundle.putString("payeeID", "1120-733");
        	  transBundle.putString("payeeName", "Nicholas");
        	  transBundle.putString("category", "Person");
        	  transBundle.putString("location", "Singapore");
        	  //Add the bundle to the intent
        	  transactionAcvitiyIntent.putExtras(transBundle);
        	startActivityForResult(transactionAcvitiyIntent, transactionActivityID);
            return true;
        case R.id.action_mic:
        	Intent intent = new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice Command(Payment)\n"
					+ "Ex. '10 Cash' to pay $10 cash");
			try {
				startActivityForResult(intent, RESULT_SPEECH);
			} catch (ActivityNotFoundException a) {
				Toast t = Toast.makeText(getApplicationContext(),
						"Oops! Your device doesn't support Speech to Text",
						Toast.LENGTH_SHORT);
				t.show();
			}
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Slide menu item click listener
     * */
    public void onSlideMenuItemClick(int position)
    {
    	chooseFragment(position);
    }
    
    /**
     * Displaying fragment view for selected slide menu item
     * */
    private void chooseFragment(int position)
    {
        Fragment fragment = null;
        switch (position) {
        case 0:
            fragment = new OverviewFragment();
            break;
        case 1:
        	fragment = new RecordFragment(0);	// by default display for all accounts
        	break;
        case 2:
            fragment = new ChartFragment();
            break;
        case 3:
            fragment = new ReportFragment();
            break;
        case 4:
        	fragment = new DeveloperFragment();
        default:
            break;
        }
        if(fragment != null)
        {
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i)   
                fragmentManager.popBackStack();
            
        	displayView(position, fragment);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    
    private void displayView(int position, Fragment fragment) {
    	// update the main content by replacing fragments
    	if(position == 0)
    		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    	else	// enable back view to overview fragment
    		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment,"TAG_FRAGMENT").addToBackStack(null).commit();
    	
    	// update selected item and title, then close the drawer
    	setTitle(mSlideMenuItemTitles.get(position));
    	int totSec = 0;
    	for (int i = 0; i < mSlideMenuSectionPos.size(); i++)
    	{
    		if(position >= mSlideMenuSectionPos.get(i))
    			totSec += 1;
    	}
    	position += totSec;
    	mDrawerList.setItemChecked(position, true);
    	mDrawerList.setSelection(position);
    	mDrawerLayout.closeDrawer(mDrawerList);
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
    
    
	public void onAccItemClick(int mPosition) {
		/**** switch to record fragment, provide account name as filter ***/
		Fragment fragment = new RecordFragment(mPosition+1);
		
		displayView(1,fragment);

	}
    
	@Override
	public void onBackPressed() {
    	// update selected item and title to overview
    	mDrawerList.setItemChecked(1, true);
    	mDrawerList.setSelection(1);
    	setTitle(mSlideMenuItemTitles.get(0));
    	
	    super.onBackPressed();
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {   
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
		// if transaction activity succeed, starts nfc activity
		case (transactionActivityID) :  
			if (resultCode == Activity.RESULT_OK) { 
				//Create the intent
				Intent nfcActivityIntent = new Intent(this, NFCActivity.class);
				//Get the bundle from Transaction Activity
				Bundle bundle = data.getExtras();
				nfcActivityIntent.putExtras(bundle);
				startActivityForResult(nfcActivityIntent, nfcActivityID);	        
			}
		break;
		// if nfc activity succeed, go to overview fragment 
		case(nfcActivityID):
			chooseFragment(0);
		break;
		case RESULT_SPEECH: 
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Crouton.makeText(this,  text.get(0), Style.CONFIRM).show();
			}
		break;
		} 
	}
}
