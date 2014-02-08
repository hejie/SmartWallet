package com.nicholas.smartwallet.ui;

import java.util.ArrayList;


import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.fourmob.poppyview.PoppyViewHelper;

import com.nicholas.smartwallet.model.AccountModel;
import com.nicholas.smartwallet.ui.R;

public class OverviewFragment extends Fragment {
	/**** *set up for array list ******/ 
	View V;
	ListView LV;
	AccListAdapter adapter;
	public  ArrayList<AccountModel> AccListViewValuesArr = new ArrayList<AccountModel>();
	/*** footer element ***/
	private PoppyViewHelper mPoppyViewHelper;
	
	public OverviewFragment() {
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
		
		mPoppyViewHelper = new PoppyViewHelper(getActivity());
		View poppyView = mPoppyViewHelper.createPoppyViewOnListView(R.id.accList, R.layout.footer_overview, new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.d("ListViewActivity", "onScrollStateChanged");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.d("ListViewActivity", "onScroll");
			}
		});

		poppyView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Add Account!", Toast.LENGTH_SHORT).show();
			}
		});
		
		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
		setListData();
		
		/*** show main balance in text view ***/
		TextView mainbalance_text = (TextView)V.findViewById(R.id.overview_total_value);
		mainbalance_text.setText("SGD 9000.00");
		
		/**** set up list view ***/
		Resources res = getResources();
		adapter = new AccListAdapter(getActivity(), AccListViewValuesArr, res);
		/********* assign the Listview to the AnimationAdapter ***********/
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimationAdapter.setAbsListView(LV);
		LV.setAdapter(swingBottomInAnimationAdapter);
			
	}
	
	/****** Function to set data in ArrayList *************/
	public void setListData()
	{
		AccListViewValuesArr.clear();

		AccountModel account = new AccountModel();
		/******* Firstly take data in model object ******/
		account.setAccName("Cash 1");
		account.setImage("cash");
		account.setColor("turquoise");
		account.setBalance(900);
		account.setCurrency("SGD");
		/******** Take Model Object in ArrayList **********/
		AccListViewValuesArr.add(account);

		/******* Subsequently ******/
		account = new AccountModel();
		account.setAccName("Cash 2");
		account.setImage("cash");
		account.setColor("orange");
		account.setBalance(1000);
		account.setCurrency("MYR");
		AccListViewValuesArr.add(account);

		account = new AccountModel();
		account.setAccName("Master Card");
		account.setImage("credit");
		account.setColor("amethyst");
		account.setBalance(2000);
		account.setCurrency("SGD");
		AccListViewValuesArr.add(account);

		account = new AccountModel();
		account.setAccName("POSB Bank");
		account.setImage("bank");
		account.setColor("emerald");
		account.setBalance(3000);
		account.setCurrency("SGD");
		AccListViewValuesArr.add(account);

		account = new AccountModel();
		account.setAccName("Personal Savings");
		account.setImage("saving");
		account.setColor("alizarin");
		account.setBalance(300);
		account.setCurrency("SGD");
		AccListViewValuesArr.add(account);

		account = new AccountModel();
		account.setAccName("Emergency");
		account.setImage("emergency");
		account.setColor("wetasphalt");
		account.setBalance(500);
		account.setCurrency("SGD");
		AccListViewValuesArr.add(account);
			
	}
	
	public void onItemClick(int mPosition) {
		// TODO Auto-generated method stub
		
	}
	
}
