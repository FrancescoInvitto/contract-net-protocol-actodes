package it.unipr.desantisinvitto.contractnet.messages;

import java.io.Serializable;

/**
 * The {@code TaskAnnouncement} class defines the messages sent by the manager to the
 * workers in order to ask for the execution of a task.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public class TaskAnnouncement implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int taskSpecification; //contains the number specified by the manager

	/**
	 * Class constructor.
	 * 
	 * @param taskSpecification	the number specified by the manager
	 */
	public TaskAnnouncement(int taskSpecification) {
		this.taskSpecification = taskSpecification;
	}

	/**
	 * This method is used to get the number specified by the manager.
	 * 
	 * @return the number
	 */
	public int getTaskSpecification() {
		return taskSpecification;
	}
}
