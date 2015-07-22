package actions;

import java.io.Serializable;

public class ActionInvokeSentMoney2 implements Serializable{
	public final String name;
	public final int amount;
    public ActionInvokeSentMoney2(String name, int amount) {
    	this.name=name;
        this.amount = amount;
    }
}