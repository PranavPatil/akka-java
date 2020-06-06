package com.emprovise.messages;

public abstract class GenericActorRequest {

	public GenericActorRequest() {
		super();
	}

	public abstract <T> Object getResponse();
}
