package it.unipr.desantisinvitto.contractnet;

import java.util.HashMap;
import java.util.Random;

import it.unipr.desantisinvitto.contractnet.messages.Bid;
import it.unipr.desantisinvitto.contractnet.messages.TaskAnnouncement;
import it.unipr.desantisinvitto.contractnet.messages.TaskAward;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.actor.Shutdown;
import it.unipr.sowide.actodes.filtering.constraint.IsInstance;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * The {@code Worker} class defines the behavior of the workers.
 * 
 * A worker receives task announcements from the manager and randomly decides its availability.
 * If the worker is available it sends to the manager the cost required to perform the task.
 * 
 * If a worker is awarded a task it computes the Fibonacci value for the specified number.
 * 
 * A worker may save the partial results to reduce the total cost for subsequent tasks.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public final class Worker extends Behavior {

	private static final long serialVersionUID = 1L;
	
	private int fibonacciNum;
	private HashMap<Integer, Long> fibonacciResults; //it contains the partial results
	
	private Reference reference;	//the reference of the manager
	
	private boolean saveResults;	//whether to save the partial results or not
	
	private Random random;
	
	//message patterns
	private static final MessagePattern TASKANNOUNCEMENT = MessagePattern.contentPattern(new IsInstance(TaskAnnouncement.class));
	private static final MessagePattern TASKAWARD = MessagePattern.contentPattern(new IsInstance(TaskAward.class));
	
	/**
	 * Class constructor.
	 * 
	 * @param sR	whether to save the partial results or not
	 */
	public Worker(final boolean sR) {
		this.fibonacciResults = new HashMap<>();
		this.fibonacciResults.put(0, (long) 0);
		this.fibonacciResults.put(1, (long) 1);
		
		this.saveResults = sR;
		
		this.reference = null;
		
		this.fibonacciNum = 0;
		
		this.random = new Random();
	}

	@Override
	public void cases(CaseFactory c) {
		
		MessageHandler announcementHandler = (m) -> {
			if(this.reference == null) {	//the first time saves the reference of the manager 
				this.reference = m.getSender();
			}
			
			System.out.println("New task announcement...");
	
			int choice = random.nextInt(2);
			
			if(choice == 1) {
				int cost = 0;
				
				TaskAnnouncement message = (TaskAnnouncement) m.getContent();		
				this.fibonacciNum = message.getTaskSpecification(); //retrieve the number
				
				System.out.println("... I am available. Compute Fibonacci value for: " + this.fibonacciNum);
				
				//update properly the cost of the task
				if(!this.fibonacciResults.containsKey(this.fibonacciNum)) {
					cost = this.fibonacciNum - (this.fibonacciResults.size() - 1);
				}
				
				Bid costMessage = new Bid(cost);
				
				send(reference, costMessage); //send the bid to the manager
			}
			else {
				System.out.println("... I am not available.");
				
				Bid refuseMessage = new Bid(-1);
				
				send(reference, refuseMessage); //notifies its unavailability to the manager
			}
			
			return null;
		};
		
		c.define(TASKANNOUNCEMENT, announcementHandler);
		
		MessageHandler awardHandler = (m) -> {
			long res = 0;
			
			if(this.fibonacciResults.containsKey(this.fibonacciNum)) {
				res = this.fibonacciResults.get(this.fibonacciNum); //Fibonacci value already computed
			}
			else {
				res = calculateFibonacci(this.fibonacciNum); //compute the Fibonacci value
			}
			
			System.out.println("Fibonacci value for " + this.fibonacciNum + " is: " + res);
			
			send(this.reference, res);
			
			return null;
		};
		
		c.define(TASKAWARD, awardHandler);
		
		MessageHandler killHandler = (m) -> {
		      send(m.getSender(), Done.DONE);

		      return Shutdown.SHUTDOWN;
		    };

		c.define(KILL, killHandler);
	}
	
	/*
	 * This method implements the recursive calculation of the Fibonacci value for
	 * the specified number.
	 */
	private long calculateFibonacci(int n) {
        long n1, n2;
        
        //result for n-1 is available
		if(this.fibonacciResults.containsKey(n - 1)) {
			n1 = this.fibonacciResults.get(n - 1);
			n2 = this.fibonacciResults.get(n - 2); //the result for n-2 is available for sure
			
			if(this.saveResults) {
				this.fibonacciResults.put(n, n1 + n2);
			}
			
			return n1 + n2;
		}
		else if(this.fibonacciResults.containsKey(n - 2)) { //result for n-2 is available
			n2 = this.fibonacciResults.get(n - 2);
			n1 = calculateFibonacci(n - 1);
			
			if(this.saveResults) {
				this.fibonacciResults.put(n - 1, n1);
				this.fibonacciResults.put(n, n1 + n2);
			}
			
			return n1 + n2;
		}
		
		//both results for n-2 and n-1 are not available
		n1 = calculateFibonacci(n - 1);
		n2 = calculateFibonacci(n - 2);
		
		if(this.saveResults) {
			this.fibonacciResults.put(n - 2, n2);
			this.fibonacciResults.put(n - 1, n1);
			this.fibonacciResults.put(n, n1 + n2);
		}
		
        return n1 + n2;
    }
}
