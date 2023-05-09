package it.unipr.desantisinvitto.contractnet;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.BehaviorState;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.MessageHandler;

public final class Worker extends Behavior {

	private static final long serialVersionUID = 1L;

	public Worker() {
	}

	public Worker(BehaviorState i, BehaviorState[] s) {
		super(i, s);
	}

	@Override
	public void cases(CaseFactory c) {
		MessageHandler h = (m) -> {
			//System.out.format(SERVERINFO, this.id, "Follower", this.state, this.currentTerm);
			//onReceive(INITIALIZATION, initHandler);
			System.out.println("Started");
		
			return null;
		};

		c.define(START, h);
	}

}
