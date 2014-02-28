package com.nicholas.smartwallet.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import com.nicholas.smartwallet.data.database;
import com.nicholas.smartwallet.model.AccountModel;
import com.nicholas.smartwallet.model.RecordModel;
import com.nicholas.smartwallet.ui.R;

import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment {
	int firstIndex;
	View V;
	ListView LV;
	RecordListAdapter rlAdapter;
	ArrayAdapter<String> accAdapter;
	ArrayList<String> AccListViewValuesArr = new ArrayList<String>();
	ArrayList<RecordModel> RecListViewValuesArr = new ArrayList<RecordModel>();
	ArrayList<Integer> mSectionPositions = new ArrayList<Integer>();
	ArrayList<String> mSectionNames = new ArrayList<String>();
	ArrayList<Section> sections = new ArrayList<Section>();
	
	private database SQLiteAdapter;
	
	public RecordFragment()
	{
		firstIndex = 0;
	}
	
	public RecordFragment(int mPosition) {
		firstIndex = mPosition;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
		SQLiteAdapter = new database(this.getActivity().getApplicationContext());
		SQLiteAdapter.openToRead();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		V = inflater.inflate(R.layout.fragment_record, container, false);	
		LV = (ListView) V.findViewById(R.id.recordList);	
		
		return V;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	
		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
		setAccListData();
		setRecListData();
		
		/**** set up for spinner ****/ 
		Spinner spinner = (Spinner) V.findViewById(R.id.record_acc_type);
		// Create an ArrayAdapter using the string array and a default spinner layout
		accAdapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, AccListViewValuesArr);
		// Specify the layout to use when the list of choices appears
		accAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(accAdapter);
		spinner.setSelection(firstIndex);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
					     
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
		}});
		
		/**** set up list view ***/
		rlAdapter = new RecordListAdapter(this.getActivity(), RecListViewValuesArr);
		/**** assign the Listview to the SimpleSectionedAdapter ***/
		SimpleSectionedListAdapter sectionedListAdapter = new SimpleSectionedListAdapter(this.getActivity().getApplicationContext(), rlAdapter,
				R.layout.listitem_record_section, R.id.recDate_text);
		sectionedListAdapter.setSections(sections.toArray(new Section[0]));
		/******** assign the Listview to the AnimationAdapter ***********/
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(sectionedListAdapter);
		swingBottomInAnimationAdapter.setAbsListView(LV);
		LV.setAdapter(swingBottomInAnimationAdapter);
			
	}
	
	/****** Function for item click listener **************/

	
	/****** Function to set data in ArrayList *************/
	public void setAccListData()
	{	
		AccListViewValuesArr.clear();
		AccListViewValuesArr.add("All");
		
		Cursor acc_all_cursor = SQLiteAdapter.query_Account_ALL();	
		if(acc_all_cursor != null)
		{
			if(acc_all_cursor.moveToFirst())
			{
				do{
					AccountModel account = new AccountModel();
					account.setAccID(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_KEY_ID)));
					account.setAccName(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_NAME)));
					account.setColor(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_COL)));
					account.setCurrency(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_CUR)));
					AccListViewValuesArr.add(account.getAccName());
				}while(acc_all_cursor.moveToNext());
			}
		}
		acc_all_cursor.close();	
		
		if(firstIndex > AccListViewValuesArr.size())
			firstIndex = 0;
	}
	
	public void setRecListData()
	{
		RecListViewValuesArr.clear();
		mSectionPositions.clear();
		mSectionNames.clear();
		sections.clear();
		
		
		/*** group by date ****/
		Cursor rec_dategroup_cursor = SQLiteAdapter.query_Record_byDateGroup();
		int accSecPosition = 0;	
		if(rec_dategroup_cursor != null)
		{
			if(rec_dategroup_cursor.moveToFirst())
			{
				do{
					Date convertedDate = new Date();
					String datetime = rec_dategroup_cursor.getString(rec_dategroup_cursor.getColumnIndex(database.REC_DATETIME));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						convertedDate = sdf.parse(datetime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String date = sdf.format(convertedDate);
					mSectionNames.add(date);
					mSectionPositions.add(accSecPosition);
					accSecPosition += rec_dategroup_cursor.getInt(rec_dategroup_cursor.getColumnIndex("COUNT("+database.REC_KEY_ID+")"));
				}while(rec_dategroup_cursor.moveToNext());
			}
		}
		rec_dategroup_cursor.close();	
		for (int i = 0; i < mSectionPositions.size(); i++) {
			sections.add(new Section(mSectionPositions.get(i), mSectionNames.get(i)));
		}
		
		
		/*** query respective records ***/	
		Cursor rec_all_cursor = SQLiteAdapter.query_Record_ALL();	
		if(rec_all_cursor != null)
		{
			if(rec_all_cursor.moveToFirst())
			{
				do{
					RecordModel record = new RecordModel();
					record.setAmount(rec_all_cursor.getFloat(rec_all_cursor.getColumnIndex(database.REC_AMT)));
					record.setTransID(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_TRANS_ID)));
					record.setPayeeID(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_PAY_ID)));
					record.setDirection(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_DIR)));
					record.setCurrency(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_CUR)));
					record.setAccName(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_ACC_NAME)));
					record.setPayee(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_PAY_NAME)));
					record.setDateTime(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_DATETIME)));
					record.setCategory(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_CAT)));
					record.setLocation(rec_all_cursor.getString(rec_all_cursor.getColumnIndex(database.REC_LOC)));
					RecListViewValuesArr.add(record);
				}while(rec_all_cursor.moveToNext());
			}
		}
		rec_all_cursor.close();	
	}	
}
