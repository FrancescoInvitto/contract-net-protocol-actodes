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
	
	private int nodeCapability; //contains a specification of the capability of the node (a partial result)

	public Bid(int nodeCapability) {
		this.nodeCapability = nodeCapability;
	}

	public int getNodeCapability() {
		return nodeCapability;
	}

	public void setNodeCapability(int nodeCapability) {
		this.nodeCapability = nodeCapability;
	}

}
