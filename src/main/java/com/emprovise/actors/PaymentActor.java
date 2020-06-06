package com.emprovise.actors;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.Recover;
import com.emprovise.system.MainActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.Future;
import scala.concurrent.Await;

import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;

@Component(value = "paymentActor")
@Scope("prototype")
public class PaymentActor extends GenericUntypedActor {

	@Autowired
	@Qualifier(value = "actorSystem")
	private MainActorSystem actorSystem;

	@Override
	public Object process(Object message) throws Exception {
		System.out.println("##################### PAYMENT " + Thread.currentThread().getId());

		Future<Object> paymentsFuture = ask(actorSystem.getLoanActor(), message, actorSystem.getTimeout());

		/*OnFailure onFailure = new OnFailure() {
			@Override
			public void onFailure(Throwable failure) throws Throwable {
				System.out.println("Actor paymentsFuture failed: " + failure.getMessage() + "  for actor " + this.toString());
			}
		};*/

		/*Recover<Object> recover = new Recover<Object>() {
			@Override
			public Future<Object> recover(Throwable failure) throws Throwable {
				if (failure instanceof TimeoutException) {
					throw failure;
				} else {
					throw failure; //there is actually an issue.
				}
			}
		};
*/
		Future<Object> future = ask(actorSystem.getLoanActor(), message, actorSystem.getTimeout());
		future.onFailure(new FailureLogCallback("abc"), actorSystem.get_system().dispatcher());
		//future.recover(recover, actorSystem.get_system().dispatcher());

		String loan = "";

		try {
			loan = (String) Await.result(paymentsFuture, actorSystem.getTimeOutDuration());
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return loan + " PAYMENT";
	}
}
