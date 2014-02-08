package com.nicholas.smartwallet.model;

public class AccountModel {
	
	private double balance;
	private double budget;
	private double expense;
	private String currency;
	private String accName;
	private String image;
	private String color;
	private String description;
	
	public AccountModel()
	{
		balance = 0;
		budget = 0;
		expense = 0;
		accName = "";
		image = "";
		color = "peterriver";
		description = "Default Description";
	}
	/*********** Set Methods ******************/
	public void setAccName(String accName)
	{
		this.accName = accName;
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public void setColor(String color)
	{
		this.color = color;
	}
	
	public void setBudget(double budget)
	{
		this.budget = budget;
	}
	
	public void setExpense(double expense)
	{
		this.expense = expense;
	}
	
	public void setBalance(double balance)
	{
		this.balance = balance;
	}
	
	public void setCurrency(String currency)
	{
		this.currency = currency;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	/*********** Get Methods ****************/
	public String getAccName()
	{
		return this.accName;
	}
	
	public String getImage()
	{
		return this.image;
	}
	
	public String getColor()
	{
		return this.color;
	}
	
	public double getBudget()
	{
		return this.budget;
	}
	
	public double getExpense()
	{
		return this.expense;
	}
	
	public double getBalance()
	{
		return this.balance;
	}
	
	public String getCurrency()
	{
		return this.currency;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
