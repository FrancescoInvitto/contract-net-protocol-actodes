# contract-net-protocol-actodes
Final project of the Distributed Systems course of the Master Degree at University of Parma, using ActoDeS.

# Contract Net Procotol
The Contract Net Protocol is a sharing protocol of activities in multi-agent systems, introduced in 1980 by Reid G. Smith. This protocol is
used to allocate tasks between autonomous agents and involves a manager and some service providers (workers). The manager proposes the execution of a task (task announcement) to the workers he knows and the workers can make proposals (bid) or indicate their unavailability. The manager can assign the assignment to one or more workers.

# Project specification
The system must be developed using the ActoDeS software and may involve the presence of 1 to 5 workers. The tasks to be performed concern the calculation of numbers belonging to the Fibonacci sequence. The execution process of a task has a cost of the order O(n). The workers and the manager can keep the information on the results obtained. Workers can use this information to reduce execution cost. The manager can use this information to accept cheaper offers. Failure and success of proposals and offers can change the weight of subsequent proposals and offers. The project includes some experimental activities. These activities must provide information on the trend of manager expenses and worker earnings. The values used to define the tasks must be randomly generated and belong to the range 2-100 (including extremes). There experimentation must define 10 trends and each trend must involve 50 tasks. These trends are defined by the following behaviors:
  - the manager and the workers decide the action to take taking into account the saved values. This pattern must be run with 1, 2, 3, 4 and 5 workers;
  - the manager and the workers decide the action to take regardless of the saved values. This pattern must be run with 1, 2, 3, 4 and 5 workers.
  

