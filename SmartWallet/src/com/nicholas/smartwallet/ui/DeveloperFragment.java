package com.nicholas.smartwallet.ui;

import java.util.ArrayList;

import com.nicholas.smartwallet.ui.R;
import com.nicholas.smartwallet.data.*;
import com.nicholas.smartwallet.model.*;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DeveloperFragment extends Fragment {
	
	private database SQLiteAdapter;
	
	private ArrayList<String> AccKeyList = new ArrayList<String>();
	private ArrayList<String> AccNameList = new ArrayList<String>();
	
	public DeveloperFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.fragment_developer, container, false);
		
		SQLiteAdapter = new database(this.getActivity().getApplicationContext());
		SQLiteAdapter.openToWrite();
		
		Button populateDatabase = (Button) V.findViewById(R.id.populate_database_btn);
		Button clearDatabase = (Button) V.findViewById(R.id.clear_database_btn);
		
		populateDatabase.setOnClickListener(new PopulateDataButtonListener());
		clearDatabase.setOnClickListener(new ClearDataButtonListener());
		return V;
	}
	
	private class PopulateDataButtonListener implements OnClickListener 
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			/*** Populate ACCOUNT TABLE ***/
			SQLiteAdapter.insertAccount_TEST("Cash", "cash", "Cash account", "turquoise","SGD",50.00);
			SQLiteAdapter.insertAccount_TEST("OCBC Credit Card", "credit", "Credit card account, OCBC Bank", "orange","SGD",50.00);
			SQLiteAdapter.insertAccount_TEST("DBS Credit Card", "credit", "Credit card account, DBS Bank","amethyst","SGD",50.00);
			SQLiteAdapter.insertAccount_TEST("Bank", "bank", "Bank account","emerald","SGD",50.00);
			SQLiteAdapter.insertAccount_TEST("Personal Savings", "saving", "Personal savings account","alizarin","SGD",50.00);
			SQLiteAdapter.insertAccount_TEST("Emergency", "emergency", "Emergency Fund","sunflower","SGD",50.00);
			
			SQLiteAdapter.openToRead();
			Cursor acc_all_cursor = SQLiteAdapter.query_Account_ALL();
			AccKeyList.clear();
			AccNameList.clear();
			if(acc_all_cursor != null)
			{
				if(acc_all_cursor.moveToFirst())
				{
					do{
						AccKeyList.add(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_KEY_ID)));
						AccNameList.add(acc_all_cursor.getString(acc_all_cursor.getColumnIndex(database.ACC_NAME)));
					}while(acc_all_cursor.moveToNext());
				}
			}
			acc_all_cursor.close();	
			
			SQLiteAdapter.openToWrite();
			/*** Populate RECORD TABLE ****/
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("#000001", "000001", "Outgoing", 10.00, "SGD", 
						"Transport", AccKeyList.get(0),AccNameList.get(0), "SMRT", "2014-02-22 00:00:00", "Singapore","None");
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("#000001", "000001", "Outgoing", 20.00, "SGD", 
						"Transport",AccKeyList.get(0),AccNameList.get(0),  "SMRT", "2014-02-23 00:00:00", "Singapore","None");
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("#000001", "000001", "Outgoing", 30.00, "SGD", 
						"Transport",AccKeyList.get(0),AccNameList.get(0), "SMRT", "2014-02-24 00:00:00", "Singapore","None");
			
			Toast.makeText(getActivity().getApplicationContext(), "Populated database with sample data!", Toast.LENGTH_LONG).show();
		}
		
	}
	
	private class ClearDataButtonListener implements OnClickListener 
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SQLiteAdapter.delete_AccountTable();
			SQLiteAdapter.delete_RecordTable();
			
			Toast.makeText(getActivity().getApplicationContext(), "Cleared database!", Toast.LENGTH_LONG).show();
		}
		
	}
	
    @Override
    public void onDetach() {
        super.onDetach();
        SQLiteAdapter.close();
    }
    
}
