package com.dynatrace.aspects.axwayapi;

import java.util.Map;

import com.dynatrace.oneagent.utils.OneAgentSDKUtils;

import com.vordel.circuit.Message;
import com.vordel.circuit.MessageProcessor;
import com.vordel.circuit.net.State;
import com.vordel.dwe.CorrelationID;
import com.vordel.dwe.http.HTTPProtocol;
import com.vordel.dwe.http.ServerTransaction;
import com.vordel.trace.Trace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AxwayAspect {

	private boolean isAPIManager;

	public AxwayAspect() {
		isAPIManager = Boolean.valueOf(System.getProperty("apimanager", "true"));
	}

	@Pointcut("call (* com.vordel.circuit.net.State.tryTransaction()) && target(t)")
	public void tryTransactionPointCut(State t) {
	}

	@Around("tryTransactionPointCut(t)")
	public void tryTransactionAroundAdvice(ProceedingJoinPoint pjp, State t) throws Throwable {

		/*
		 * System.out.
		 * println("Dynatrace  Debug in AxwayAspect:tryTransactionAroundAdvice PJP: " +
		 * pjp + " State: " + t + " requestUrl: " + requestUrl + " httpMethod: " +
		 * httpMethod);
		 */
		OneAgentSDKUtils.aroundProducer(pjp, t);
	}
	
 
	@Pointcut("call(* com.vordel.dwe.http.HTTPPlugin.invokeDispose(..)) && args (protocol, handler, txn, id, loopbackMessage)")
	public void invokeDisposePointcutGateway(HTTPProtocol protocol, HTTPProtocol handler, ServerTransaction txn,
			CorrelationID id, Map<String, Object> loopbackMessage) {

	}

	@Around("invokeDisposePointcutGateway(protocol, handler, txn, id, loopbackMessage)")
	public void invokeDisposeAroundAdvice(ProceedingJoinPoint pjp, HTTPProtocol protocol, HTTPProtocol handler,
			ServerTransaction txn, CorrelationID id, Map<String, Object> loopbackMessage) throws Throwable {
		Trace.info(" Dynatrace :: isAPIManager " + isAPIManager);
		if (!isAPIManager) {

			String[] uriSplit = txn.getRequestURI().split("/");
			String apiName = uriSplit[1];
			String apiContextRoot = "/";
			String orgName = "defaultFrontend";
			String appName = "defaultFrontend";			
			String correlationID = id.toString();

			OneAgentSDKUtils.aroundConsumer(pjp, null, apiName, apiContextRoot, appName, orgName, correlationID, protocol, handler,
					txn, id,
					loopbackMessage);

		} else {
			Trace.info("Dynatrace :: in else block");
			pjp.proceed();
		}
	}
	

	@Pointcut("call(* com.vordel.coreapireg.runtime.broker.InvokableMethod.invoke(..)) && args (txn, m, lastChance, path)")
	public void invokeDisposePointcut(ServerTransaction txn, Message m, MessageProcessor lastChance, String path) {

	}

	 @Around("invokeDisposePointcut(txn, m, lastChance, path)")
   public Object invokeAroundAdvice(ProceedingJoinPoint pjp, ServerTransaction txn, Message m,
         MessageProcessor lastChance, String path) throws Throwable {

      String[] uriSplit = OneAgentSDKUtils.getRequestURL(m).toString().split("/");
      String apiName;
      String apiContextRoot = "/";
      String orgName="default";
	  String appName = "default";
		String corelationId = "default";
	

      if (m.get("authentication.application.name") != null) {
         appName = m.get("authentication.application.name").toString();
      }

      if (m.get("authentication.organization.name") != null) {
         orgName = m.get("authentication.organization.name").toString();
      }

      if (m.get("api.name") != null) {
         apiName = m.get("api.name").toString();
		} else {
			apiName = uriSplit[1];
		}
	  
	  if(m.getIDBase() != null){
			corelationId=m.getIDBase().toString();
		}

      return OneAgentSDKUtils.aroundConsumer(pjp, m, apiName, apiContextRoot, appName,orgName,corelationId ,null, null, null,null,null);

   }

}
