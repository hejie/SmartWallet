package com.nicholas.smartwallet.ui;

import java.util.ArrayList;


import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.fourmob.poppyview.PoppyViewHelper;
import com.fourmob.poppyview.PoppyViewHelper.PoppyViewPosition;

import com.nicholas.smartwallet.model.AccountModel;
import com.nicholas.smartwallet.data.*;
import com.nicholas.smartwallet.ui.R;

public class OverviewFragment extends Fragment {
	/**** *set up for array list ******/ 
	View V;
	ListView LV;
	AccListAdapter adapter;
	private database SQLiteAdapter;
	
	public  ArrayList<AccountModel> AccListViewValuesArr = new ArrayList<AccountModel>();
	/*** footer element ***/
	private PoppyViewHelper mPoppyViewHelper;
	
	public OverviewFragment() {
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
		V = inflater.inflate(R.layout.fragment_overview, container, false);	
		LV = (ListView) V.findViewById(R.id.accList);	
		
		return V;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mPoppyViewHelper = new PoppyViewHelper(getActivity(),PoppyViewPosition.TOP);
		mPoppyViewHelper.createPoppyViewOnListView(R.id.accList, R.layout.header_overview, new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.d("ListViewActivity", "onScrollStateChanged");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.d("ListViewActivity", "onScroll");
			}
		});
		
		
		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
		setAccListData();
		
		/*** show main balance in text view ***/
		double mainbalance = 0;
		for(AccountModel acc : AccListViewValuesArr)
			mainbalance += acc.getBalance();
		TextView mainbalance_text = (TextView)V.findViewById(R.id.overview_total_value);
		mainbalance_text.setText("SGD " + String.format("%.2f", mainbalance));
		
		/**** set up list view ***/
		Resources res = getResources();
		adapter = new AccListAdapter(getActivity(), AccListViewValuesArr, res);
		/********* assign the Listview to the AnimationAdapter ***********/
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimationAdapter.setAbsListView(LV);
		LV.setAdapter(swingBottomInAnimationAdapter);

	}
	
	/****** Function to set data in ArrayList *************/
	public void setAccListData()
	{
		AccListViewValuesArr.clear();
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
					account.setDescription(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_DESC)));
					account.setType(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_TYPE)));
					account.setBalance(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_BAL)));
					account.setBudget(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_BUDG)));
					account.setExpense(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_EXP)));
					account.setIncome(acc_all_cursor.getFloat(acc_all_cursor.getColumnIndex(database.ACC_INC)));
					AccListViewValuesArr.add(account);
				}while(acc_all_cursor.moveToNext());
			}
		}
		acc_all_cursor.close();	
			
	}
	
	
}
