package it.unipr.desantisinvitto.contractnet.messages;

import java.io.Serializable;

/**
 * The {@code Bid} defines the messages sent by a worker to the manager in order to let it
 * know its availability to execute the task.
 * 
 * @author De Santis Fabrizio, Invitto Francesco
 */
public class Bid implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Contains a specification of the capability of the node (a partial result).
	 * If it is equal to -1, the worker is not available.
	 */
	private int nodeCapability; 

	/**
	 * Class constructor.
	 * 
	 * @param nodeCapability	the cost of the worker to perform the task
	 */
	public Bid(int nodeCapability) {
		this.nodeCapability = nodeCapability;
	}

	/**
	 * This method is used to get the cost of the worker to perform the task.
	 * 
	 * @return the cost
	 */
	public int getNodeCapability() {
		return nodeCapability;
	}

}
