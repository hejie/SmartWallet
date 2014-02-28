package com.nicholas.smartwallet.model;

public class NFCInfoModel {
	String title;
	String value;
	
	public NFCInfoModel()
	{
		this.title = "";
		this.value = "";
	}
	
	public NFCInfoModel(String title, String value)
	{
		this.title = title;
		this.value = value;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getValue()
	{
		return this.value;
	}
}
