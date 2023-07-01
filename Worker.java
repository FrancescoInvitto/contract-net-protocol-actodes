package it.unipr.desantisinvitto.contractnet;

import java.math.BigInteger;
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
	private HashMap<Integer, BigInteger> fibonacciResults; //it contains the partial results
	
	private Reference reference;	//the reference of the manager
	
	private boolean saveResults;	//whether to save the partial results or not
	private int cost;	//the cost of the last task
	private int totalGain;	//the gain of the worker performing the tasks
	
	private Random random;
	
	//message patterns
	private static final MessagePattern TASKANNOUNCEMENT = MessagePattern.contentPattern(new IsInstance(TaskAnnouncement.class));
	private static final MessagePattern TASKAWARD = MessagePattern.contentPattern(new IsInstance(TaskAward.class));
	private static final MessagePattern FINISH = MessagePattern.contentPattern(new IsInstance(Done.class));

	/**
	 * Class constructor.
	 * 
	 * @param sR	whether to save the partial results or not
	 */
	public Worker(final boolean sR) {
		this.fibonacciResults = new HashMap<>();
		this.fibonacciResults.put(0, BigInteger.ZERO);
		this.fibonacciResults.put(1, BigInteger.ONE);
		
		this.saveResults = sR;
		
		this.reference = null;
		
		this.fibonacciNum = 0;
		
		this.cost = 0;
		this.totalGain = 0;
		
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
				this.cost = 0;
				
				TaskAnnouncement message = (TaskAnnouncement) m.getContent();		
				this.fibonacciNum = message.getTaskSpecification(); //retrieve the number
				
				System.out.println("... I am available. Compute Fibonacci value for: " + this.fibonacciNum);
				
				//update properly the cost of the task
				if(!this.fibonacciResults.containsKey(this.fibonacciNum)) {
					this.cost = this.fibonacciNum - (this.fibonacciResults.size() - 1);
				}
				
				Bid costMessage = new Bid(this.cost);
				
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
			this.totalGain += this.cost;
			
			BigInteger res = BigInteger.ZERO;
			
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
		
		MessageHandler finishHandler = (m) -> {
			send(m.getSender(), this.totalGain);
			
			return null;
		};
		
		c.define(FINISH, finishHandler);
		
		MessageHandler killHandler = (m) -> {
		      send(m.getSender(), Done.DONE);

		      return Shutdown.SHUTDOWN;
		    };

		c.define(KILL, killHandler);
	}
	
	/*
	 * This method implements the iterative calculation of the Fibonacci value for
	 * the specified number.
	 */
	private BigInteger calculateFibonacci(int n) {
		
		if(this.saveResults) {
			int start = this.fibonacciResults.size();
			
	        for (int i = start; i <= n; i++) {
	            this.fibonacciResults.put(i, this.fibonacciResults.get(i - 1).add(this.fibonacciResults.get(i - 2)));
	        }
	        
	        return this.fibonacciResults.get(n);
		}
		else {
			BigInteger[] fib = new BigInteger[n + 1];
			fib[0] = BigInteger.ZERO;
	        fib[1] = BigInteger.ONE;
	        
			for (int i = 2; i <= n; i++) {
				fib[i] = fib[i - 1].add(fib[i - 2]);
	        }
			
			return fib[n];
		}
    }
}
