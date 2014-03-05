package com.nicholas.smartwallet.model;

public class RecordModel {
	
	private double amount;
	private String transId;
	private String payeeId;
	private String direction;
	private String currency;
	private String accId;
	private String accName;
	private String payeeName;
	private String datetime;
	private String category;
	private String location;
	private String comments;
	
	public RecordModel(){
	}
	
	public void setTransID(String transId)
	{
		this.transId = transId;
	}
	
	public void setPayeeID(String payeeId)
	{
		this.payeeId = payeeId;
	}
	
	public void setDirection(String direction)
	{
		this.direction = direction;
	}
		
	public void setAmount(double amount)
	{
		this.amount = amount;
	}
	
	public void setCurrency(String currency)
	{
		this.currency = currency;
	}
	
	public void setAccID(String accId)
	{
		this.accId = accId;
	}
	
	public void setAccName(String accName)
	{
		this.accName = accName;
	}
	
	public void setDateTime(String datetime)
	{
		this.datetime = datetime;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	public void setPayee(String payeeName)
	{
		this.payeeName = payeeName;
	}
	
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	public void setComments(String comments)
	{
		this.comments = comments;
	}
	
	public String getTransId()
	{
		return this.transId;
	}
	
	public String getPayeeId()
	{
		return this.payeeId;
	}
	
	public double getAmount()
	{
		return this.amount;
	}
	
	public String getDirection()
	{
		return this.direction;
	}
	
	public String getCurrency()
	{
		return this.currency;
	}
	
	public String getAccID()
	{
		return this.accId;
	}
	
	public String getAccName()
	{
		return this.accName;
	}
	
	public String getDateTime()
	{
		return this.datetime;
	}
	
	
	public String getCategory()
	{
		return this.category;
	}
	
	public String getPayeeName()
	{
		return this.payeeName;
	}
	
	public String getLocation()
	{
		return this.location;
	}
	
	public String getComments()
	{
		return this.comments;
	}
	
}
