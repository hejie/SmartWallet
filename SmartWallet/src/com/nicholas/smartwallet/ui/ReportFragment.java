package com.nicholas.smartwallet.ui;

import com.nicholas.smartwallet.ui.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReportFragment extends Fragment {
	
	public ReportFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.fragment_report, container, false);
		ListView LV = (ListView) V.findViewById(R.id.manageList);
		String List[] = new String[4];
		List[0] = "Transfer Fund";
		List[1] ="Payment";
		List[2] ="Savings";
		List[3] ="Budget";
		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, List);
		LV.setAdapter(aa);
		return V;
	}
}
