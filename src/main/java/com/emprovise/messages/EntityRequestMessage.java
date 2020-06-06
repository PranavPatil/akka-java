package com.emprovise.messages;

import java.util.ArrayList;
import java.util.List;

public final class EntityRequestMessage extends GenericActorRequest{

	final String identifier;
	final List<String> response = new ArrayList<String>();

	public EntityRequestMessage(String identifier) {
		super();
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public List<String> getResponse() {
		return response;
	}
}
