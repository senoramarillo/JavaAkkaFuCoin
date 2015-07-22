package fucoin;

import akka.actor.ActorRef;
import akka.japi.Creator;
import gui.IWalletGuiControle;
import gui.WalletGui;

public class WalletCreator implements Creator<Wallet> {

	private ActorRef preknownNeighbour;
	private String walletName;
	private ActorRef remoteSuperVisorActor;
	private String preknownNeighbourName;

	public WalletCreator(ActorRef preknownNeighbour, String preknownNeighbourName, String walletName, ActorRef remoteSuperVisorActor) {
		this.preknownNeighbour=preknownNeighbour;
		this.preknownNeighbourName=preknownNeighbourName;
		this.walletName=walletName;
		this.remoteSuperVisorActor=remoteSuperVisorActor;
		
	}

	@Override
	public Wallet create() throws Exception {
		Wallet wallet = new Wallet(preknownNeighbour,preknownNeighbourName, walletName,remoteSuperVisorActor);
		
//		IWalletGuiControle gui = new WalletGui(wallet);
//		wallet.setGui(gui);
		return wallet;
	}
}