package com.nicholas.smartwallet.model;

public class AccountModel{
	
	private double balance;
	private double income;
	private double expense;
	private double budget;
	private String currency;
	private String accID;
	private String accName;
	private String type;
	private String color;
	private String description;
	
	public AccountModel()
	{
		balance = 0;
		income = 0;
		budget = 0;
		expense = 0;
		accName = "";
		type = "";
		color = "peterriver";
		description = "Default Description";
	}
	/*********** Set Methods ******************/
	public void setAccID(String accID)
	{
		this.accID = accID;
	}
	
	public void setAccName(String accName)
	{
		this.accName = accName;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setColor(String color)
	{
		this.color = color;
	}
	
	public void setBudget(double budget)
	{
		this.budget = budget;
	}
	
	public void setIncome(double income)
	{
		this.income = income;
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
	public String getAccID()
	{
		return this.accID;
	}
	
	public String getAccName()
	{
		return this.accName;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getColor()
	{
		return this.color;
	}
	
	public double getBudget()
	{
		return this.budget;
	}
	
	public double getIncome()
	{
		return this.income;
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
