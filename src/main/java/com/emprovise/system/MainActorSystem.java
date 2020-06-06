package com.emprovise.system;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.emprovise.spring.SpringExtension.SpringExtProvider;

@Component(value="actorSystem")
public class MainActorSystem implements ApplicationContextAware{

    public static final String PAYMENT_ACTOR = "paymentActor";
    public static final String LOAN_ACTOR = "loanActor";
    public static final String FINANCE_ACTOR = "financeActor";

    private ApplicationContext applicationContext;
    private ActorSystem _system = null;
    private Duration timeOutDuration = Duration.create(20, TimeUnit.SECONDS);
    private Timeout timeout = new Timeout(Duration.create(15, TimeUnit.SECONDS));
    private Timeout creationTimeout = new Timeout(Duration.create(10, TimeUnit.SECONDS));

    ActorRef financeActor = null;
    ActorRef paymentActor = null;
    ActorRef loanActor = null;

    public MainActorSystem() {
        _system = ActorSystem.create("MainActorSystem");
    }

    public ActorSystem get_system() {
        return _system;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringExtProvider.get(_system).initialize(applicationContext);
    }

    public final ActorRef getPaymentActor() {
        if(paymentActor == null){
            paymentActor = getActor(PAYMENT_ACTOR);
        }
        return paymentActor;
    }

    public final ActorRef getLoanActor() {
        if(loanActor == null){
            loanActor = getActor(LOAN_ACTOR);
        }
        return loanActor;
    }

    public final ActorRef getFinanceActor() {
        if(financeActor == null){
            financeActor = getActor(FINANCE_ACTOR);
        }
        return financeActor;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        get_system().shutdown();
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public Timeout getCreationTimeout() {
        return creationTimeout;
    }

    public Duration getTimeOutDuration() {
        return timeOutDuration;
    }

    public ActorRef getActor(String actorName){
        ActorRef actor = null;
        try{
            actor = _system.actorOf(SpringExtProvider.get(_system).props(actorName));
        }catch (Exception e){
            e.printStackTrace();
        }
        return actor;
    }

    public ActorRef getFailOverActor(String actorName){
        ActorRef actor = null;
        try{
            actor = _system.actorOf(SpringExtProvider.get(_system).propsFailOver(actorName));
        }catch (Exception e){
            e.printStackTrace();
        }
        return actor;
    }
}
