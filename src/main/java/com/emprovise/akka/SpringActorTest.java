package com.emprovise.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.Recover;
import com.emprovise.messages.EntityRequestMessage;
import com.emprovise.spring.AppConfig;
import com.emprovise.system.MainActorSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static akka.pattern.Patterns.pipe;
import static akka.pattern.Patterns.ask;
public class SpringActorTest {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MainActorSystem actorSystem = context.getBean(MainActorSystem.class);

        EntityRequestMessage message = new EntityRequestMessage("Abc");
        ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        addFuture(actorSystem, futures,actorSystem.getPaymentActor(), MainActorSystem.PAYMENT_ACTOR, message);
        addFuture(actorSystem, futures,actorSystem.getFinanceActor(), MainActorSystem.FINANCE_ACTOR, message);

        final Future<Iterable<Object>> aggregate = Futures.sequence(futures, actorSystem.get_system().dispatcher());
        Future<List<String>> transformed = aggregate.map(new Mapper<Iterable<Object>, List<String>>() {
            public List<String> apply(Iterable<Object> result){
                List<String> response = new ArrayList<String>();
                final Iterator<Object> iter = result.iterator();
                while(iter.hasNext()){
                    Object value = iter.next();
                    if(value instanceof String){
                        String dto = (String)value;
                        response.add(dto);
                    }
                }
                return response;
            }
        }, actorSystem.get_system().dispatcher());

        Future<List<String>> response = pipe(transformed,actorSystem.get_system().dispatcher()).future();
        List<String> parallelActivityList = Await.result(response, actorSystem.getTimeOutDuration());
        System.out.println(parallelActivityList);
        actorSystem.get_system().shutdown();
    }

    public static void addFuture(MainActorSystem actorSystem, ArrayList<Future<Object>> futures, ActorRef actor, final String actorName, Object message) {
        if(actor == null && actorName != null){
            actor = actorSystem.getFailOverActor(actorName);
        }
        if(actor != null && futures != null){
            final Future<Object> future = ask(actor, message, actorSystem.getTimeout()).recover(new Recover<Object>() {
                @Override
                public Object recover(Throwable failure) throws Throwable {
                    return new Object();
                }
            },actorSystem.get_system().dispatcher());
            futures.add(future);
        }
    }
}
