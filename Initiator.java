package it.unipr.desantisinvitto.contractnet;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.configuration.Configuration;
import it.unipr.sowide.actodes.controller.SpaceInfo;
import it.unipr.sowide.actodes.executor.active.PoolCoordinator;
import it.unipr.sowide.actodes.filtering.constraint.IsInstance;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Kill;
import it.unipr.sowide.actodes.actor.Shutdown;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.actodes.service.logging.ConsoleWriter;
import it.unipr.sowide.actodes.service.logging.Logger;
import it.unipr.sowide.actodes.service.logging.util.NoCycleProcessing;

/**
 * The {@code Initiator} class defines a behavior that simply creates the actors populating
 * the application. A certain number of {@code Worker} actors and {@code Manager} actors
 * are created.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public final class Initiator extends Behavior {
	
	private static final long serialVersionUID = 1L;

	private int nWorkers;	//the total number of workers
	private int nTasks;	//the number of tasks the manager will ask to perform
	private boolean saveResults;	//whether to save the partial results or not
	
	private Reference[] references; //the references of the workers
	
	private int terminated;	//number of terminated actors
	
	private static final MessagePattern DONE = MessagePattern.contentPattern(new IsInstance(Done.class));

	/**
	 * Class constructor.
	 * 
	 * @param nW	the number of workers
	 * @param nT	the number of tasks to perform
	 * @param sR	whether to save the partial results or not
	 */
	public Initiator(final int nW, final int nT, final boolean sR) {
		this.nWorkers = nW;
		this.nTasks = nT;
		this.terminated = 0;
		this.saveResults = sR;
	}

	/** {@inheritDoc} **/
	@Override
	public void cases(CaseFactory c) {
		MessageHandler h = (m) -> {
			if ((this.nWorkers > 0)){

				this.references = new Reference[this.nWorkers];

				for (int i = 0; i < this.nWorkers; ++i){
					this.references[i] = actor(new Worker(this.saveResults));
				}
				
				actor(new Manager(this.references, this.nTasks, this.saveResults));	
			}
			else {
				System.out.println("Error: the number of workers must be > 0");
				return Shutdown.SHUTDOWN;
			}

			return null;
		};

		c.define(START, h);

		MessageHandler k = (m) -> {
			
			send(m.getSender(), Kill.KILL);
			
			for(int i = 0; i < this.nWorkers; ++i) {
				send(this.references[i], Kill.KILL);
			}
			
			return null;
		};

		c.define(KILL, k);
		
		MessageHandler d = (m) -> {
			this.terminated++;
			
			if(this.terminated == nWorkers + 1) {
				send(APP, Kill.KILL);
				send(SpaceInfo.INFO.getBroker(), Kill.KILL);
				
				return Shutdown.SHUTDOWN;
			}
			return null;
		};
		
		c.define(DONE, d);
	}

	public static void main(String[] args) {
		final int nWorkers = 5; //1, 2, 3, 4, 5
		final int nTasks = 50;
		final boolean saveResults = true;

		Configuration c = SpaceInfo.INFO.getConfiguration();

		c.setFilter(Logger.ACTIONS);
		c.setLogFilter(new NoCycleProcessing());
		c.addWriter(new ConsoleWriter());

		c.setExecutor(new PoolCoordinator(new Initiator(nWorkers, nTasks, saveResults)));

		c.start();
	}
}
