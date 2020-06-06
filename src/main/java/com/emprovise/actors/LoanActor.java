package com.emprovise.actors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "loanActor")
@Scope("prototype")
public class LoanActor extends GenericUntypedActor {

	@Override
	public Object process(Object message) throws Exception {
		System.out.println("##################### LOAN " + Thread.currentThread().getId());
		synchronized (message) {
			message.wait();
		}
		return "LOAN";
	}
}
