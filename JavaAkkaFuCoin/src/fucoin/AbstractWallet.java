package fucoin;
import java.io.Serializable;
import java.util.HashMap;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public abstract class AbstractWallet extends UntypedActor implements Serializable {

	// Used to join the network (a pre known participant/Wallet must be known)
    public static class ActionJoin implements Serializable {}
    
    // Returns some neighbors that might be used as known
    // and/or local neighbors
    public class ActionJoinAnswer implements Serializable {
        public final HashMap<String, ActorRef> someNeighbors = new HashMap<>();
    }
    
    // Used to push the state of my/a wallet to another participant
    public static class ActionStoreOrUpdate implements Serializable {
        public final AbstractWallet w;
        public ActionStoreOrUpdate(AbstractWallet w) {
            this.w = w;
        }
    }
    
    // May be used to delete a stored Wallet on another participant
    public static class ActionInvalidate implements Serializable {
        final String name;
        public ActionInvalidate(String name) {
            this.name = name;
        }
    }
    
    // Used to send (positive amount) or retreive money (negative amount) 
    public static class ActionReceiveTransaction implements Serializable {
        final public int amount;
        public ActionReceiveTransaction(int amount) {
            this.amount = amount;
        }
    }
    
    // Used to search a Wallet by name, i.e. when we want to 
    // perform a transaction on it
    public static class ActionSearchWalletReference implements Serializable {
        final String name;
        final long ttl; 
        public ActionSearchWalletReference(String name, long ttl) {
            this.name = name;
            this.ttl=ttl;
        }
    }
    
    // Used to return a Wallet reference (akka-style string which can 
    // be transformed to an ActorRef)
    public static class ActionSearchWalletReferenceAnswer implements Serializable {
        final String address;
        final String name;
        public ActionSearchWalletReferenceAnswer(String name,String address) {
            this.address = address;
            this.name=name;
        }
    }
    
    // Used to search a Wallet by name, i.e. the own wallet if we just 
    // joined the network; If a receiving participant holds the stored Wallet, 
    // he returns it, otherwise, he might use gossiping methods to go on 
    // with the search;
    // Note: You should also forward the sender (the participant who actually
    // searches for this Wallet, so that it can be returnd the direct way)
    public static class ActionSearchMyWallet implements Serializable {
        final String name;
        public ActionSearchMyWallet(String name) {
            this.name = name;
        }
    }
    
    // Used to return a searched Wallet
    public static class ActionSearchMyWalletAnswer implements Serializable {
        final AbstractWallet w;
        public ActionSearchMyWalletAnswer(AbstractWallet w) {
            this.w = w;
        }
    }
    
    // Constructor
    public AbstractWallet(String name) {
        this.name = name;
    }
    
    // Returns the name of this wallet, e.g. "Lieschen M�ller"
    public String getName() {
        return this.name;
    }
    
    // Returns the akka-style address as String, which 
    // could be converted to an ActorRef object later
    public abstract String getAddress();
    
    // Performs housekeeping operations, e.g. pushes 
    // backedUpNeighbor-entries to other neighbors
    public abstract void leave();
    
    // The which receives Action objects
    public abstract void onReceive(Object message);
    
    // Holds references to neighbors that were in 
    // contact with this wallet during runtime;
    // The key corresponds to the Wallet's name
    public transient HashMap<String, ActorRef> knownNeighbors = new HashMap<String, ActorRef>();
    
    // Holds references to neighbors this wallet 
    // synchronizes itself to (the Wallet object);
    // The key corresponds to the Wallet's name
    public transient HashMap<String, ActorRef> localNeighbors = new HashMap<String, ActorRef>();
    
    // Holds all Wallets from network participants 
    // which synchronize their state (Wallet object)
    // with us;
    // The key corresponds to the Wallet's name
    public transient HashMap<String, AbstractWallet> backedUpNeighbors = new HashMap<String, AbstractWallet>();
    
    // The name of this wallet (does never change, no 
    // duplicates in network assumed)
    public final String name;
    
    // The amount this wallet currently holds
    public int amount;
    
}