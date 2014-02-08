package com.nicholas.smartwallet.model;

import java.util.ArrayList;

public class TransactionModel {
	private int id;
	private int type;
	private double value;
	private String direction;
	private String accName;
	private String date;
	private String time;
	private ArrayList<String> categories;
	
	public TransactionModel(){
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public void setDirection(String direction)
	{
		this.direction = direction;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}
	
	public void setAccName(String accName)
	{
		this.accName = accName;
	}
	
	public void setDate(String date)
	{
		this.date = date;
	}
	
	public void setTime(String time)
	{
		this.time = time;
	}
	
	public void setCategories(ArrayList<String> categories)
	{
		this.categories = categories;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public double getValue()
	{
		return this.value;
	}
	
	public String getDirection()
	{
		return this.direction;
	}
	
	public String getAccName()
	{
		return this.accName;
	}
	
	public String getDate()
	{
		return this.date;
	}
	
	public String getTime()
	{
		return this.time;
	}
	
	public ArrayList<String> getCategories()
	{
		return this.categories;
	}
}
