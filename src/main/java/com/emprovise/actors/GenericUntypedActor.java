package com.emprovise.actors;

import akka.actor.UntypedActor;
import com.emprovise.messages.GenericActorRequest;

public abstract class GenericUntypedActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		Object response = new Object();
		try{
			if(message instanceof GenericActorRequest){
				response = process(message);
				if(response == null){
					response = new Object();
				}
			}else{
				unhandled(message);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		getSender().tell(response, getSelf());
	}
	
	public abstract Object process(Object message) throws Exception;

	@Override
	public void unhandled(Object message) {
		//We are going change the implentation as we learn more
		//super.unhandled(message);
		if(message!=null){
			System.out.println("==>"+this.getClass().getSimpleName()+" was invoked with meesage type :"+message.getClass().getSimpleName());
		}else{
			System.out.println("==>"+this.getClass().getSimpleName()+" was invoked with meesage type 'null'.");
		}
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
	}
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
	}
}
