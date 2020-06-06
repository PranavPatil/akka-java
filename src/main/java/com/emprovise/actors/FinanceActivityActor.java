package com.emprovise.actors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "financeActor")
@Scope("prototype")
public class FinanceActivityActor extends GenericUntypedActor {

	@Override
	public Object process(Object message) throws Exception {
		System.out.println("##################### FINANCE " + Thread.currentThread().getId());
		return "FINANCE";
	}
}
