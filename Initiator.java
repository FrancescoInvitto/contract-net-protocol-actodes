package it.unipr.desantisinvitto.contractnet;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.configuration.Configuration;
import it.unipr.sowide.actodes.controller.SpaceInfo;
import it.unipr.sowide.actodes.executor.active.PoolCoordinator;
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
	
	private static int fsfs = 1;

	private static final long serialVersionUID = 1L;

	private int nWorkers;	//number of total workers
	private Reference[] references;	
	//private int terminated;		//number of terminated clients

	public Initiator(final int n) {
		this.nWorkers = n;
		//this.terminated = 0;
	}

	/** {@inheritDoc} **/
	@Override
	public void cases(CaseFactory c) {
		MessageHandler h = (m) -> {
			if ((this.nWorkers > 0)){

				this.references = new Reference[this.nWorkers];

				for (int i = 0; i < this.nWorkers; ++i){
					System.out.println("Starting worker");
					this.references[i] = actor(new Worker());
				}

				for(int l = 0; l < this.nWorkers; ++l)
					send(this.references[l], this.references);

				actor(new Manager(this.references));

			}

			return null;
		};

		c.define(START, h);

//		MessageHandler k = (n) -> {
//			this.terminated++;
//			if(this.terminated == clients) {
//				send(APP, Kill.KILL);
//				return Shutdown.SHUTDOWN;
//			}
//			return null;
//		};

		//c.define(KILL, k);

		//		MessagePattern mp = MessagePattern.contentPattern(
		//				new IsInstance(Done.class));
		//
		//		h = (m) -> {
		//			this.killed++;
		//
		//			if (this.killed == this.actors)
		//			{
		//				send(APP, Kill.KILL);
		//				send(SpaceInfo.INFO.getBroker(), Kill.KILL);
		//
		//				return Shutdown.SHUTDOWN;
		//			}
		//
		//			return null;
		//		};
		//
		//		c.define(mp, h);
	}

	public static void main(String[] args) {
		final int workers = 5; //1, 2, 3, 4, 5

		Configuration c = SpaceInfo.INFO.getConfiguration();

		c.setFilter(Logger.ACTIONS);
		c.setLogFilter(new NoCycleProcessing());
		c.addWriter(new ConsoleWriter());

		c.setExecutor(new PoolCoordinator(new Initiator(workers)));

		c.start();
	}
}
