# contract-net-protocol-actodes
Final project of the Distributed Systems course of the Master Degree at University of Parma, using ActoDeS.

# Contract Net Procotol
The Contract Net Protocol is a sharing protocol of activities in multi-agent systems, introduced in 1980 by Reid G. Smith. This protocol is
used to allocate tasks between autonomous agents and involves a manager and some service providers (workers). The manager proposes the execution of a task (task announcement) to the workers he knows and the workers can make proposals (bid) or indicate their unavailability. The manager can assign the assignment to one or more workers.

# Project specification
The system must be developed using the ActoDeS software and may involve the presence of 1 to 5 workers. The tasks to be performed concern the calculation of numbers belonging to the Fibonacci sequence. The iterative execution process of a task has a cost of the order O(n). The workers and the manager can keep the information on the results obtained. Workers can use this information to reduce execution cost. The manager can use this information to accept cheaper offers. Failure and success of proposals and offers can change the weight of subsequent proposals and offers. The project includes some experimental activities. These activities must provide information on the trend of manager expenses and worker earnings. The values used to define the tasks must be randomly generated and belong to the range 2-100 (including extremes). There experimentation must define 10 trends and each trend must involve 50 tasks. These trends are defined by the following behaviors:
  - the manager and the workers decide the action to take taking into account the saved values. This pattern must be run with 1, 2, 3, 4 and 5 workers;
  - the manager and the workers decide the action to take regardless of the saved values. This pattern must be run with 1, 2, 3, 4 and 5 workers.
 
## Fibonacci expressions
```math
 F(0) = 0,    
 F(1) = 1,    
 F(n) = F(n-1) + F(n-2),    
 F(n-1) = F(n+1) - F(n),    
 F(n-2) = F(n) - F(n-1)
```
# ActoDeS
ActoDeS is a software framework for the development of large concurrent and distributed systems. This software framework takes advantage of the actor model and of an its implementation that makes easy the development of the actor code by delegating the management of events (i.e., the reception of messages) to the execution environment. Moreover, it allows the development of scalable and efficient applications through the possibility of using different implementations of the components that drive the execution of actors.

# How to install the project (using Eclipse)
Prerequisite: ActoDes must be created as a new Java project inside the eclipse workspace.
The code should be downloaded (or cloned) inside a package called "it.unipr.desantisinvitto.contractnet" inside the ActoDes Java project.

In order to run the application the only step to perform is to run the Initiator.java class; inside this class you can modify the number of workers, the number of tasks the manager will assign to them and whether the workers save or not the partial results of the computations.
After the execution of the application, a report is saved in the ActoDes folder as csv file; this report contains the gain of every worker and the total cost of the manager. The name of the file will have the following structure: report-NUMWORKERS-X.csv, where X can be:
  - withsaving, if the workers save the partial results;
  - withoutsaving, if the workers do not save the partial results.

So, for example, the execution of the application with 3 workers that do not save the partial results will produce the report saved as "report-3-withoutsaving.csv".
  

