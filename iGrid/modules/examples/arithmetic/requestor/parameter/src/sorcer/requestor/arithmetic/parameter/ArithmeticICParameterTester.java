package sorcer.requestor.arithmetic.parameter;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Jobber;
import sorcer.core.signature.NetSignature;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;

/**
 * Testing parameter passing between tasks within the same service job. Two
 * numbers are added by the first task, then two numbers are multiplied by the
 * second one. The results of the first task and the second task are passed on
 * to the third task that subtracts the result of task two from the result of
 * task one. The {@link sorcer.core.context.ArrayContext} is used for
 * requestor's data in this test.
 * 
 * @see ArithmeticParameterTester
 * @author Mike Sobolewski
 */

public class ArithmeticICParameterTester implements SorcerConstants {

	private static Logger log = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		Job job = new ArithmeticICParameterTester().getJob();
		Exertion result = job.exert(null);
		log.info("result: \n" + result);
		// Show result the data contexts of component tasks
		log.info("result: \n" + result.getExertions().get(0).getContext());
		log.info("result: \n" + result.getExertions().get(1).getContext());
		log.info("result: \n" + result.getExertions().get(2).getContext());
	}

	private Job getJob() throws Exception {
		NetTask task1 = getAddTask();
		NetTask task2 = getMultiplyTask();
		NetTask task3 = getSubtractTask();
		
		NetJob job = new NetJob("Arithmetic Job");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		
		log.info("context 1: " + task1.getContext());
		log.info("context 2: " + task2.getContext());
		log.info("context 3: " + task3.getContext());
		// pipe the result of second task as the first argument of task
		// three
		task2.getContext().connect(ArrayContext.ovp(3), ArrayContext.ivp(1),
				task3.getContext());
		// pipe the result of the first task as the second argument of task
		// three
		task1.getContext().connect(ArrayContext.ovp(3), ArrayContext.ivp(2),
				task3.getContext());
		// job.getControlContext().setMonitorEnabled(true);
		return job;
	}

	private NetTask getAddTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 20.0);
		context.ivc(1, "arg1");
		context.iv(2, 80.0);
		context.ivc(2, "arg2");
		context.ov(3, 0.0);
		context.ovc(3, "result of adding arg1 and arg2");

		NetSignature method = new NetSignature("add",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("arithmethic-add", method);
		task.setContext(context);
		return task;
	}

	private NetTask getMultiplyTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 10.0);
		context.ivc(1, "arg1");
		context.iv(2, 50.0);
		context.ov(3, 0.0);
		context.ovc(3, "result of multiplying arg1 and arg2");

		NetSignature method = new NetSignature("multiply",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("arithmethic-multiply", method);
		task.setContext(context);
		return task;
	}

	private NetTask getSubtractTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 0.0);
		context.ivc(1, "arg1: result of task 2");
		context.iv(2, 0.0);
		context.ivc(2, "arg2: result of task 1");
		context.ov(3, 0.0);
		context.ovc(3, "result of subtracting arg2 from arg1");

		NetSignature method = new NetSignature("subtract",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("arithmethic-subtract", method);
		task.setContext(context);
		return task;
	}
}
