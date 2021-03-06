package com.nicholas.smartwallet.ui;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph.OnBarClickedListener;

import com.nicholas.smartwallet.ui.R;

public class ChartFragment extends Fragment {

	public ChartFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.fragment_chart, container, false);

		/*** draw a pie graph ****/
		PieGraph pg = (PieGraph)V.findViewById(R.id.graph);
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#99CC00"));
		slice.setTitle("Transport");
		slice.setValue(2);
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FFBB33"));
		slice.setTitle("Shopping");
		slice.setValue(3);
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#AA66CC"));
		slice.setTitle("Food");
		slice.setValue(8);
		pg.addSlice(slice);

		/*** construct summary bar graph ***/
		ArrayList<Bar> points = new ArrayList<Bar>();
		Bar d = new Bar();
		d.setColor(Color.parseColor("#99CC00"));
		d.setName("Income");
		d.setValue(10);
		Bar d2 = new Bar();
		d2.setColor(Color.parseColor("#FFBB33"));
		d2.setName("Expense");
		d2.setValue(20);
		points.add(d);
		points.add(d2);

		BarGraph g = (BarGraph)V.findViewById(R.id.barchart_graph);
		g.setBars(points);
		
		g.setOnBarClickedListener(new OnBarClickedListener(){

			@Override
			public void onClick(int index) {
				
			}
			
		});
		
		return V;
	}
}