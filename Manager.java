package it.unipr.desantisinvitto.contractnet;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.BehaviorState;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.registry.Reference;

public final class Manager extends Behavior {
	
	private static final long serialVersionUID = 1L;
	private Reference[] references;

	public Manager(final Reference[] ref) {
		this.references = ref;
	}

	public Manager(BehaviorState i, BehaviorState[] s) {
		super(i, s);
	}

	@Override
	public void cases(CaseFactory c) {
		MessageHandler h = (m) -> {
			//System.out.format(SERVERINFO, this.id, "Follower", this.state, this.currentTerm);
			//onReceive(INITIALIZATION, initHandler);
			System.out.println("Started manager");
		
			return null;
		};

		c.define(START, h);
	}

}
