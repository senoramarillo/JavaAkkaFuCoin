package fucoin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import supervisor.SuperVisor;
import actions.ActionInvokeLeave;
import actions.ActionInvokeRevive;
import actions.ActionInvokeSentMoney;
import actions.ActionInvokeUpdate;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		File file = new File("application.conf");
		System.out.println("config found? " + file.exists());
		Config config = ConfigFactory.parseFile(file);
		ActorSystem system = ActorSystem.create("Core", config);
		ActorRef superVisorActor = system.actorOf(SuperVisor.props(), "SuperVisor");
		List<ActorRef> activeActors = new ArrayList<ActorRef>();
		ActorRef a1 = system.actorOf(Wallet.props(null, "", "Main", superVisorActor), "Main");
		// ActorRef a2 = system.actorOf(Wallet.props(a1,"Main","Main2",superVisorActor),"Main2");

		// activeActors.add(a1);
		int maxrounds = 1000;
		int maxactors = 100;
		for (int actor = 0; actor < maxactors; actor++) {
			activeActors.add(system.actorOf(Wallet.props(a1, "Main", "Main" + actor, superVisorActor), "Main" + actor));
		}
		List<List<ActorRef>> offline = new ArrayList<List<ActorRef>>();

		for (int listnr = 0; listnr < maxrounds; listnr++) {
			offline.add(new ArrayList<ActorRef>());
		}

		for (int timestep = 0; timestep < maxrounds; timestep++) {
			System.out.println("timestamp:" + timestep);
			List<ActorRef> removedActors = new ArrayList<ActorRef>();
			for (ActorRef actor : activeActors) {
				if (Math.random() < 0.6) {
					actor.tell(
							new ActionInvokeSentMoney("Main" + (int) Math.floor(Math.random() * 10),
						    (int) (Math.round(Math.random() * 100) - 50)), actor);
				}
				if (Math.random() < 0.2) {
					removedActors.add(actor);
					int offtime = timestep + (int) (Math.random() * 6) + 2;
					offline.get(Math.min(offtime, maxrounds)).add(actor);
				}
			}
			activeActors.removeAll(removedActors);
			for (ActorRef actorName : offline.get(timestep)) {
				actorName.tell(new ActionInvokeRevive(), actorName);
				activeActors.add(actorName);
			}
			for (ActorRef removedActor : removedActors) {
				removedActor.tell(new ActionInvokeLeave(), removedActor);
			}
			Thread.sleep(1000);
			System.out.println("timestamp end:" + timestep);
			System.out.println("activeActors:" + activeActors);
			System.out.println("revived" + offline.get(timestep));
		}
		superVisorActor.tell(new ActionInvokeUpdate(), superVisorActor);
	}
}