package org.mycompany.dataObjects;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "payload")
@XmlType(propOrder= {"action", "amount", "addAccount", "subAccount"})
public class PayloadObject implements Serializable{
	private String action;
	private int amount;
	private int addAccount;
	private int subAccount;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAddAccount() {
		return addAccount;
	}
	public void setAddAccount(int addAccount) {
		this.addAccount = addAccount;
	}
	public int getSubAccount() {
		return subAccount;
	}
	public void setSubAccount(int subAccount) {
		this.subAccount = subAccount;
	}
}
