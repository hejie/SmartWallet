package com.nicholas.smartwallet.ui;

import com.nicholas.smartwallet.ui.R;
import com.nicholas.smartwallet.data.*;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DeveloperFragment extends Fragment {
	
	private database SQLiteAdapter;
	
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
			SQLiteAdapter.insertAccount("Cash 1", "cash", "Cash account", "turquoise","SGD");
			SQLiteAdapter.insertAccount("Cash 2", "cash", "Cash account", "orange","SGD");
			SQLiteAdapter.insertAccount("Credit Card", "credit", "Credit card account","amethyst","SGD");
			SQLiteAdapter.insertAccount("Bank", "bank", "Bank account","emerald","SGD");
			SQLiteAdapter.insertAccount("Personal Savings", "saving", "Personal savings account","alizarin","SGD");
			SQLiteAdapter.insertAccount("Emergency", "emergency", "Emergency Fund","sunflower","SGD");
			SQLiteAdapter.updateAccountBalance(100.00, 0);
			SQLiteAdapter.updateAccountBalance(150.00, 1);
			SQLiteAdapter.updateAccountBalance(3000.00, 2);
			SQLiteAdapter.updateAccountBalance(880.00, 3);
			SQLiteAdapter.updateAccountBalance(200.00, 4);
			SQLiteAdapter.updateAccountBalance(500.00, 5);
			
			/*** Populate RECORD TABLE ****/
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("000001", "000001", "out", 10.00, "SGD", 
						"Transport", "Cash 1", "SMRT", "2014-02-22 00:00:00", "Singapore");
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("000001", "000001", "out", 20.00, "SGD", 
						"Transport", "Cash 1",  "SMRT", "2014-02-23 00:00:00", "Singapore");
			for(int i=0;i < 3;i++)
				SQLiteAdapter.insertRecord("000001", "000001", "out", 30.00, "SGD", 
						"Transport", "Cash 1", "SMRT", "2014-02-24 00:00:00", "Singapore");
			
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
}
