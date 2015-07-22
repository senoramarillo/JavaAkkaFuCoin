package gui;

import fucoin.Wallet;

public class WalletControle implements IWalletControle{

	private Wallet wallet;

	public WalletControle(Wallet wallet) {
		this.wallet=wallet;
	}

	@Override
	public void leave() {
		wallet.leave();
	}

	@Override
	public void send(String name, int amount) {
		wallet.send(name, amount);
	}
}