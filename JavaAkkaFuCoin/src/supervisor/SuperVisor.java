package supervisor;

import java.awt.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import fucoin.AbstractWallet.ActionJoin;
import fucoin.AbstractWallet.ActionStoreOrUpdate;
import actions.ActionGetAmount;
import actions.ActionGetAmountAnswer;
import actions.ActionInvokeUpdate;

public class SuperVisor extends UntypedActor {

	private List<ActorRef> knownClients = new ArrayList<ActorRef>();
	private Map<String, Map<String, Integer>> amounts = new HashMap<String, Map<String, Integer>>();
	private AmountTableModel amountTableModel;
	private Label averageamountLbl;

	public SuperVisor(AmountTableModel amountTableModel, Label averageamountLbl) {
		this.amountTableModel = amountTableModel;
		this.averageamountLbl = averageamountLbl;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof ActionJoin) {
			if (!knownClients.contains(getSender())) {
				knownClients.add(getSender());
			}
		} else if (msg instanceof ActionInvokeUpdate) {
			log("" + knownClients);
			for (ActorRef neighbor : knownClients) {
				neighbor.tell(new ActionGetAmount(), getSelf());
			}
		} else if (msg instanceof ActionGetAmountAnswer) {
			ActionGetAmountAnswer agaa = (ActionGetAmountAnswer) msg;
			try {
				update(agaa.address, agaa.name, agaa.amount);
			} catch (Exception ignoreException) {
			}

		} else if (msg instanceof ActionStoreOrUpdate) {
			ActionStoreOrUpdate asou = (ActionStoreOrUpdate) msg;
			try {
				update(asou.w.getAddress(), asou.w.name, asou.w.amount);
			} catch (Exception ignoreException) {
			}

			knownClients.remove(asou.w.getAddress());
		}
	}

	private void log(String msg) {
		System.out.println(getSelf() + ": " + msg);
	}

	Semaphore mutex = new Semaphore(1);

	private void update(String address, String name, int amount)
			throws InterruptedException {
		
		//log(address + ", " + name + ", " + amount);
		if (!amounts.containsKey(address)) {
			amounts.put(address, new HashMap<String, Integer>());
		}
		amounts.get(address).put(name, amount);
		amountTableModel.clear();
		int user = 0;
		double avgAmount = 0;
		for (Entry<String, Map<String, Integer>> process : amounts.entrySet()) {
			for (Entry<String, Integer> account : process.getValue().entrySet()) {
			//	amountTableModel.addRow(new Object[] { process.getKey(),
			//			account.getKey(), account.getValue() });
				user++;
				avgAmount += account.getValue();
			}
		}
		if (user > 0) {
			avgAmount /= user;
		}
		avgAmount = ((int) (avgAmount * 100) / 100.0);
		this.averageamountLbl.setText("" + avgAmount);
		
	}

	public static Props props() {
		return Props.create(SuperVisor.class, new SuperVisorCreator());
	}

	public void updateValues() {
		getSelf().tell(new ActionInvokeUpdate(), getSelf());
	}

	public void exit() {
		getContext().stop(getSelf());
	}

	@Override
	public void postStop() throws Exception {
		int user = 0;
		double avgAmount = 0;
		//System.out.println(amounts);
		for (Entry<String, Map<String, Integer>> process : amounts.entrySet()) {
			for (Entry<String, Integer> account : process.getValue().entrySet()) {
				amountTableModel.addRow(new Object[] { process.getKey(),
						account.getKey(), account.getValue() });
				user++;
				avgAmount += account.getValue();
			}
		}
		if (user > 0) {
			avgAmount /= user;
		}
	}
}