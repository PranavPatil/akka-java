package com.emprovise.actors;

import akka.dispatch.OnFailure;

public class FailureLogCallback extends OnFailure {

    private String actor;

    public FailureLogCallback(String actorName) {
        this.actor = actorName;
    }

    @Override
    public void onFailure(Throwable throwable) throws Throwable {
        System.out.println("Actor " + this.getClass() +" failed: " + throwable.getMessage());
    }
}
