package fucoin;

import java.io.File;


import akka.actor.ActorPath;
//import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class MainRemote {
	public static ActorRef remoteSuperVisorActor;

	public static void main(String[] args) throws InterruptedException {

		File file = new File("application.conf");
		System.out.println("config found? " + file.exists());
		Config config = ConfigFactory.parseFile(file);
		ActorSystem system = ActorSystem.create("Test", config);
		
		Address address = new Address("akka.tcp", "Core", "127.0.0.1", 1234);
		System.out.println(address);
		String path = "akka.tcp://Core@127.0.0.1:1234/user/Main";
		//System.out.println(system.actorSelection(ActorPath.fromPath(path)));
		//System.out.println(ActorPath.class.isValidPathElement(""+address+"/user/Main"));
		ActorRef a1 = system.actorOf(Wallet.props(null,"","Main2",remoteSuperVisorActor),"Main2");
	}
}