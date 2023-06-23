package it.unipr.desantisinvitto.contractnet.messages;

import java.io.Serializable;

/**
 * The {@code TaskAward} class defines the messages sent by the manager to one (or more) worker(s)
 * to let them know the award of a specific task.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public class TaskAward implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Class constructor
	 */
	public TaskAward() {
	}

}
