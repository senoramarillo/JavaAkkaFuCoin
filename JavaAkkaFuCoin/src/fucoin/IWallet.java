package fucoin;

import gui.IWalletControle;

public interface IWallet extends IWalletControle{
	//Vector<WalletPointer> join();
	void storeOrUpdate(Wallet w);
	void invalidateWallet(Wallet w);
	void receiveTransaction(int amount);
	//Vector<WalletPointer> searchWallet(String adress);
}