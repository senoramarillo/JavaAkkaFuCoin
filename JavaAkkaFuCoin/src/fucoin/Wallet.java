package fucoin;

import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import akka.actor.ActorRef;
import akka.actor.Props;
import actions.ActionGetAmount;
import actions.ActionGetAmountAnswer;
import actions.ActionInvokeLeave;
import actions.ActionInvokeRevive;
import actions.ActionInvokeSentMoney;
import actions.ActionInvokeSentMoney2;
import gui.IWalletControle;
import gui.IWalletGuiControle;

public class Wallet extends AbstractWallet implements IWalletControle{
	
	private ActorRef preknownNeighbour;
	private ActorRef remoteSuperVisorActor;
	private IWalletGuiControle gui;
	private String preknownNeighbourName;
	private boolean isActive;

	public Wallet(ActorRef preknownNeighbour, String preknownNeighbourName, String walletName, ActorRef remoteSuperVisorActor) {
		super(walletName);
		this.preknownNeighbourName=preknownNeighbourName;
		this.preknownNeighbour=preknownNeighbour;
		this.remoteSuperVisorActor=remoteSuperVisorActor;
	}
	
	@Override
	public String getAddress() {
		return getAddress(getSelf());
	}

	private String getAddress(ActorRef self) {
		return self.path().toSerializationFormatWithAddress(self.path().address());
	}

	public void send(String name, int amount){
		//System.out.println("search wallet"+name+" in "+knownNeighbors.keySet());
		if(knownNeighbors.containsKey(name)){
			addAmount(-amount);
			knownNeighbors.get(name).tell(new ActionReceiveTransaction(amount), getSelf());
		}else{
			for(ActorRef neighbor : knownNeighbors.values()){
				neighbor.tell(new ActionSearchWalletReference(name,System.currentTimeMillis()+10), getSelf());
			}
			
			try {
				getContext().unwatch(getSelf());
				Thread.sleep(200);
				getContext().watch(getSelf());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//getContext().unwatch(getSelf());
			getSelf().tell(new ActionInvokeSentMoney(name, amount), getSelf());
			
		}
	
	}
	

	private void addAmount(int amount) {
		setAmount(this.amount+amount);
		log(" My amount is now "+this.amount);
		if(gui!=null){
			gui.setAmount(this.amount);
		}
	}

	@Override
	public void leave() {
		for(ActorRef neighbor : knownNeighbors.values()){
			if(getSelf().compareTo(neighbor)!=0){
				neighbor.tell(new ActionStoreOrUpdate(this), getSelf());
			}
		}
		remoteSuperVisorActor.tell(new ActionStoreOrUpdate(this), getSelf());
		isActive=false;
		backedUpNeighbors.clear();
		knownNeighbors.clear();
		knownNeighbors.put(preknownNeighbourName,preknownNeighbour);
	}

	@Override
	public void onReceive(Object message) {
		log(getSender().path().name()+" invokes "+getSelf().path().name()+" to do "+message.getClass().getSimpleName());
		if(message instanceof ActionInvokeRevive){
			isActive=true;
			
			preknownNeighbour.tell(new ActionJoin(), getSelf());
			
		}
		if(!isActive)return;
		//System.out.println(message);
		if(message instanceof ActionJoin){
			ActionJoinAnswer aja = new ActionJoinAnswer();
			aja.someNeighbors.putAll(knownNeighbors);
			getSender().tell(aja, getSelf());
		}else if(message instanceof ActionJoinAnswer){
			ActionJoinAnswer aja = (ActionJoinAnswer) message;
			for(Entry<String, ActorRef> neighbor : knownNeighbors.entrySet()){
				addKnownNeighbor(neighbor.getKey(),neighbor.getValue());
				neighbor.getValue().tell(new ActionSearchMyWallet(name), getSelf());
			}
			
			
		}else if(message instanceof ActionSearchMyWallet){
			ActionSearchMyWallet asmw = (ActionSearchMyWallet) message;
			//If I know that somebody is searching himself, 
			//I can store him under the searched wallet name
			addKnownNeighbor(asmw.name, getSender());
			
			AbstractWallet storedWallet = backedUpNeighbors.get(asmw.name);
			log(" "+knownNeighbors);
			if(storedWallet!=null){
				getSender().tell(new ActionSearchMyWalletAnswer(storedWallet), getSelf());
			}
		}else if(message instanceof ActionSearchMyWalletAnswer){
			ActionSearchMyWalletAnswer asmwa = (ActionSearchMyWalletAnswer) message;
			setAmount(asmwa.w.amount);
			getSender().tell(new ActionInvalidate(name), getSelf());
		}else if(message instanceof ActionInvalidate){
			ActionInvalidate ai = (ActionInvalidate) message;
			backedUpNeighbors.remove(ai.name);
		}else if(message instanceof ActionSearchWalletReference){
			ActionSearchWalletReference aswr = (ActionSearchWalletReference) message;
			System.out.println("I search for you"+aswr.name);
			if(this.name.equals(aswr.name)){
				getSender().tell(new ActionSearchWalletReferenceAnswer(aswr.name,getAddress()),getSelf());
			}else if(backedUpNeighbors.containsKey(aswr.name)){
				getSender().tell(new ActionSearchWalletReferenceAnswer(aswr.name,backedUpNeighbors.get(aswr.name).getAddress()),getSelf());
			} else if(knownNeighbors.containsKey(aswr.name)){
				getSender().tell(new ActionSearchWalletReferenceAnswer(aswr.name,getAddress(knownNeighbors.get(aswr.name))),getSelf());
			} else if (System.currentTimeMillis()<aswr.ttl){
				//for(ActorRef actor : knownNeighbors.values()){
				//	actor.tell(aswr,getSelf());
				//}
				//Because Sender is maybe unknown
				//getSender().tell(aswr, getSelf());
			}
		}else if(message instanceof ActionSearchWalletReferenceAnswer){
			ActionSearchWalletReferenceAnswer aswra = (ActionSearchWalletReferenceAnswer) message;
			ActorRef target = getContext().actorSelection(aswra.address).anchor();
			addKnownNeighbor(aswra.name,target);
		}else if(message instanceof ActionInvokeSentMoney){
			ActionInvokeSentMoney aism = (ActionInvokeSentMoney) message;
			
			send(aism.name, aism.amount);
		}else if(message instanceof ActionInvokeSentMoney2){
			if(knownNeighbors.containsKey(name)){
				addAmount(-amount);
				knownNeighbors.get(name).tell(new ActionReceiveTransaction(amount), getSelf());
			}
		}else if(message instanceof ActionReceiveTransaction){
			ActionReceiveTransaction art = (ActionReceiveTransaction) message;
			System.out.println(message.getClass().getSimpleName()+" "+art.amount);
			addAmount(art.amount);
		}else if(message instanceof ActionStoreOrUpdate){
			ActionStoreOrUpdate asou = (ActionStoreOrUpdate) message;
			backedUpNeighbors.put(asou.w.name, asou.w);
		}else if(message instanceof ActionGetAmount){
			ActionGetAmountAnswer agaa = new ActionGetAmountAnswer(getAddress(),getName(),amount);
			getSender().tell(agaa, getSelf());
		}else if(message instanceof ActionInvokeLeave){
			leave();
		}else if(message instanceof ActionInvokeRevive){
			
		}else{
			unhandled(message);
			System.err.println("Unexpected Error: "+message+" not handeld");
		}
	}
	
	private void addKnownNeighbor(String key, ActorRef value) {
		if(!knownNeighbors.containsKey(key)){
			System.out.println(knownNeighbors.keySet()+" does not contain "+key);
			knownNeighbors.put(key,value);
			if(gui!=null){
				gui.addKnownAddress(key);
			}
			System.out.println(key+"-->"+value);
		}
	}

	private void log(String string) {
		System.out.println(getSelf()+": "+string);
	}

	@Override
	public void preStart() throws Exception {
		isActive=true;
		if(gui!=null){
			gui.setAddress(getAddress());
		}
		String path = "akka.tcp://Core@127.0.0.1:1234/user/Main";
		System.out.println(getContext().provider().getExternalAddressFor(getSelf().path().address()));
		//log("my address should be "+getAddress());
		//log(""+preknownNeighbour);
		//knownNeighbors.put(getName(),getSelf());
		
		System.out.println(knownNeighbors);
		if(preknownNeighbour!=null){
			knownNeighbors.put(preknownNeighbourName,preknownNeighbour);
			preknownNeighbour.tell(new ActionJoin(), getSelf());
			ActionJoinAnswer aja = new ActionJoinAnswer();
			aja.someNeighbors.putAll(knownNeighbors);
			preknownNeighbour.tell(aja, getSelf());
			
		}
		setAmount(100);
		remoteSuperVisorActor.tell(new ActionJoin(), getSelf());
	}
	
	@Override
	public void postStop() throws Exception {
		leave();
		super.postStop();
		
	}
	
	
	public static Props props(ActorRef preknownNeighbour, String preknownNeighbourName, String walletName, ActorRef remoteSuperVisorActor) {
		return Props.create(Wallet.class,new WalletCreator(preknownNeighbour,preknownNeighbourName,walletName,remoteSuperVisorActor));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Wallet){
			Wallet wobj = (Wallet) obj;
			return amount==wobj.amount&&name.equals(wobj.name);
		}
		return false;
	}

	public void setGui(IWalletGuiControle gui) {
		this.gui=gui;
	}
	
	Semaphore mutex = new Semaphore(1);
	public void setAmount(int amount){
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.amount = amount;
		if(remoteSuperVisorActor != null){
			remoteSuperVisorActor.tell(new ActionGetAmountAnswer(getAddress(), getName(), amount), getSelf());
		}
		if(gui!=null){
			gui.setAmount(this.amount);
		}
		mutex.release();
	}

}