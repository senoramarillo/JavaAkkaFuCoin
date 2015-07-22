package actions;

import java.io.Serializable;

public class ActionInvokeSentMoney implements Serializable{
	public final String name;
	public final int amount;
    public ActionInvokeSentMoney(String name, int amount) {
    	this.name=name;
        this.amount = amount;
    }
}