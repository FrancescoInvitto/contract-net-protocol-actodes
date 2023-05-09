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
	
	private int taskSpecification; //contains the Fibonacci value to compute

	public TaskAnnouncement() {
	}

	public int getTaskSpecification() {
		return taskSpecification;
	}

	public void setTaskSpecification(int taskSpecification) {
		this.taskSpecification = taskSpecification;
	}

}
