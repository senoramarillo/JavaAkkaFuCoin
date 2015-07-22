package gui;

public interface IWalletGuiControle {
	public void setAddress(String address);
	public void setAmount(int amount);
	public void addKnownAddress(String address);
	public void addLogMsg(String msg);
}