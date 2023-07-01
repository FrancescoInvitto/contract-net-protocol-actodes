package it.unipr.desantisinvitto.contractnet;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
import it.unipr.sowide.actodes.interaction.Kill;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * The {@code Manager} class defines the behavior of the manager.
 * 
 * Its goal is to assign the execution of a task to one or more workers.
 * The task is the computation of the Fibonacci value for randomly generated numbers.
 * 
 * The manager keeps track of the availability of the workers during the publication
 * of the task announcements, penalizing those that are not available for subsequent announcements.
 * 
 * The manager uses the cost information received by the workers to assign the task to the
 * ones that offer the minimum cost. When more than one worker offers the minimum cost, their
 * "weights" are checked and only those with the maximum one are awarded the task.
 * 
 * So when a worker notifies its availability it "gains" an advantage, while when it notifies its
 * unavailability it will be penalized.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public final class Manager extends Behavior {

	private static final long serialVersionUID = 1L;

	//the range into which generate the random number
	private static final int MIN = 2;	
	private static final int MAX = 100;

	private Reference[] references;	//the references of the workers
	private int remainingTasks;	//the number of remaining tasks

	private Random random;

	private int totalCost;	//the total cost required to the manager to perform all the tasks

	private HashMap<Reference, Integer> workersCosts;	//the cost of the workers to perform the last requested task
	private HashMap<Reference, Integer> workersWeights;	//the "weights" of the workers
	private HashMap<Reference, Integer> workersGain;	//the final total gain of each worker

	private int countResponses;	//counts how many responses are received
	private int countAssigned;	//counts how many workers are involved into the last task
	private boolean atLeastOneAvailable;	//true if at least one worker is available
	private int num;	//contains the last randomly generated number

	private int receivedReports;

	private boolean saveResults;	//whether the workers save the partial results or not

	//message patterns
	private static final MessagePattern BID = MessagePattern.contentPattern(new IsInstance(Bid.class));
	private static final MessagePattern RESULT = MessagePattern.contentPattern(new IsInstance(BigInteger.class));
	private static final MessagePattern REPORT = MessagePattern.contentPattern(new IsInstance(Integer.class));

	/**
	 * Class constructor.
	 * 
	 * @param ref	the references of the workers
	 * @param nTasks	the total number of tasks to assign
	 */
	public Manager(final Reference[] ref, final int nTasks, final boolean sR) {
		this.references = ref;

		this.random = new Random();

		this.remainingTasks = nTasks;

		this.receivedReports = 0;

		this.totalCost = 0;
		this.countResponses = 0;
		this.countAssigned = 0;

		this.workersCosts = new HashMap<>();
		for(int i = 0; i < this.references.length; i++) {
			workersCosts.put(this.references[i], Integer.MAX_VALUE);
		}

		this.workersWeights = new HashMap<>();
		for(int i = 0; i < this.references.length; i++) {
			workersWeights.put(this.references[i], 0);
		}

		this.atLeastOneAvailable = false;

		this.workersGain = new HashMap<>();	

		this.saveResults = sR;
	}

	@Override
	public void cases(CaseFactory c) {
		MessageHandler h = (m) -> {
			// first execution
			startFibonacci(MIN, MAX);

			return null;
		};

		c.define(START, h);

		MessageHandler bidHandler = (m) -> {			
			this.countResponses++;
			Bid message = (Bid) m.getContent();

			int cost = message.getNodeCapability();

			if(cost == -1) {
				System.out.println(m.getSender().getName() + " is not available.");
				this.workersWeights.replace(m.getSender(), this.workersWeights.get(m.getSender()) - 1); //decrement the weight of the worker
			}
			else {
				this.atLeastOneAvailable = true;
				System.out.println(m.getSender().getName() + " is available. Cost to perform the task: " + cost);
				this.workersCosts.replace(m.getSender(), cost); //keep track of the cost to perform the task for this worker
				this.workersWeights.replace(m.getSender(), this.workersWeights.get(m.getSender()) + 1); //increment the weight of the worker
			}

			if(this.countResponses == this.references.length) {
				this.countResponses = 0;

				//all the workers have indicated their (un)availability
				if(this.atLeastOneAvailable) {
					this.atLeastOneAvailable = false;

					int min = Collections.min(this.workersCosts.values());	//select the minimum cost to perform the task

					System.out.println("Minimum cost to perform the task: " + min);

					// iterate over the entries of the HashMap
					ArrayList<Reference> supportList = new ArrayList<Reference>(); //contains the reference of the workers with minimum cost
					int max = Integer.MIN_VALUE; 

					for (Map.Entry<Reference, Integer> entry : this.workersCosts.entrySet()) {
						if (entry.getValue().equals(min)) {
							supportList.add(entry.getKey());
							int weight = this.workersWeights.get(entry.getKey());
							max = Math.max(max, weight);
						}
						entry.setValue(Integer.MAX_VALUE); //reset the cost to the maximum possible value (for next iteration)
					}

					//int max = Collections.max(this.workersWeights.values());

					for (Reference ref : supportList) {	//among the workers with minimum cost, only those with maximum weight are selected
						if(this.workersWeights.get(ref) == max) {
							this.totalCost += min;
							this.countAssigned++;

							TaskAward taskAward = new TaskAward();
							System.out.println("Task awarded to " + ref.getName());
							send(ref, taskAward);
						}
					}
				}
				else { //if no one is available it retries to send the announcement
					System.out.println("No workers available... I repeat the announcement");

					TaskAnnouncement announcement = new TaskAnnouncement(this.num);

					for(int l = 0; l < this.references.length; l++) {
						send(this.references[l], announcement);
					}

				}

			}

			return null;
		};

		c.define(BID, bidHandler);

		MessageHandler resultHandler = (m) -> {
			System.out.println("Result received from " + m.getSender().getName() + ": " + m.getContent());
			this.countAssigned--;

			if(this.countAssigned == 0) {
				if(this.remainingTasks > 0) {
					System.out.println("Workers weights");
					for(Map.Entry<Reference, Integer> entry : workersWeights.entrySet()) {
						System.out.println(entry.getKey().getName() + " -- " + entry.getValue());
					}

					startFibonacci(MIN, MAX);
				}
				else {
					for(int i = 0; i < this.references.length; ++i) {
						send(this.references[i], Done.DONE);
					}	
				}

			}

			return null;
		};

		c.define(RESULT, resultHandler);

		MessageHandler reportHandler = (m) -> {
			this.receivedReports++;

			this.workersGain.put(m.getSender(), (Integer) m.getContent());

			if(this.receivedReports == this.references.length) {
				String fileName = "report-" + this.references.length + "-" + (this.saveResults ? "with" : "without") + "saving.csv";
				
				saveReport(fileName);

				send(getParent(), Kill.KILL); //notifies the Initiator it has finished
			}

			return null;
		};

		c.define(REPORT, reportHandler);

		MessageHandler killHandler = (m) -> {
			send(m.getSender(), Done.DONE);

			return Shutdown.SHUTDOWN;
		};

		c.define(KILL, killHandler);

	}

	/*
	 * This method is used to send the announcement of a new task.
	 */
	private void startFibonacci(int min, int max) {
		this.num = random.nextInt(max - min) + min;

		System.out.println("Remaining tasks: " + this.remainingTasks);
		System.out.println("Generated number: " + this.num);

		TaskAnnouncement announcement = new TaskAnnouncement(this.num);

		for(int l = 0; l < this.references.length; l++) {
			send(this.references[l], announcement);
		}

		this.remainingTasks--;
	}

	/*
	 * This method is used to save the report.
	 */
	private void saveReport(String fileName) {
		String header = "";
		String values = "";

		for(int i = 0; i < this.references.length; ++i) 
			header += "Gain worker " + (i + 1) + ";";


		header += "Total manager cost";

		for (Map.Entry<Reference, Integer> entry : this.workersGain.entrySet()) 
			values += String.valueOf(entry.getValue()) + ";";

		values += String.valueOf(this.totalCost);


		try(PrintWriter outputStream = new PrintWriter(new FileWriter(fileName)))
		{
			outputStream.println(header);

			outputStream.println(String.join(";", values));

			System.out.println("Report correctly saved in the file: " + fileName);	      
		}
		catch (Exception e)
		{
			System.out.println("An error occurred while saving the file: " + e.getMessage());
			return;
		}
	}
}
