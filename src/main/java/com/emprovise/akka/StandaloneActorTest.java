package com.emprovise.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.util.Timeout;
import com.emprovise.actors.PaymentActor;
import com.emprovise.messages.EntityRequestMessage;
import scala.collection.mutable.ArraySeq;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class StandaloneActorTest {

    private Timeout timeout = new Timeout(Duration.create(15, TimeUnit.SECONDS));
    private Duration timeOutDuration = Duration.create(20, TimeUnit.SECONDS);

    public static void main(String[] args) throws Exception {
        StandaloneActorTest standaloneActorTest = new StandaloneActorTest();
        standaloneActorTest.initiateParallelExecution();
    }

    private List<String> initiateParallelExecution() throws Exception  {

        ActorSystem actrSys = ActorSystem.create("CalcSystem");
        Props props = Props.create(PaymentActor.class, new ArraySeq<Object>(0));
        ActorRef jdPartsActor = actrSys.actorOf(props, "master");
        ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        List<String> partsListForThread = new ArrayList<String>();

        for (String partsList : partsListForThread) {
            EntityRequestMessage actorRequestMessage = new EntityRequestMessage(partsList);
            futures.add(ask(jdPartsActor, actorRequestMessage, timeout));
        }

        final Future<Iterable<Object>> aggregate = Futures.sequence(futures, actrSys.dispatcher());
        Future<Map<String,List<String>>> transformed = aggregate.map(new Mapper<Iterable<Object>, Map<String,List<String>>>() {
            public Map<String,List<String>> apply(Iterable<Object> result){
                Map<String,List<String>> response = new HashMap<String, List<String>>();
                final Iterator<Object> iter = result.iterator();
                while(iter.hasNext()){
                    Object value = iter.next();
                    if(value instanceof HashMap<?, ?>){
                        response.putAll((HashMap<String, List<String>>)value);
                    }
                }
                return response;
            }
        }, actrSys.dispatcher());

        Future<Map<String,List<String>>> response = pipe(transformed,actrSys.dispatcher()).future();
        Map<String,List<String>> partDTOMap = Await.result(response, timeOutDuration);
        List<String> partDTOList = new ArrayList<String>();

        for(Map.Entry<String,List<String>> partDTOEntry :partDTOMap.entrySet() ) {
            partDTOList.addAll(partDTOEntry.getValue());
        }

        return partDTOList;
    }
}
