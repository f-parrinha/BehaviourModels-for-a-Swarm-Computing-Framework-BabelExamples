# DevLog

The designed code abstracts a protocol whose main purpose is to distribute a number set over a network of N peers.
The NumberSetProtocol relies on a leader to do the operations and then share the result over the network. Each operation
consists of adding a unique number to a Set. The peers can then add these numbers to their own set. Due to the 
non-repetitive entry property of sets, adding new numbers is equivalent to making the union between their set and the 
received set of numbers. By the end of the protocols execution, all peers in the network should have the same numbers,
with the same order, in their set.

## Pseudocode
Note: although the algorithm requires a leader, there is no leader election. For that another protocol would need to be 
implemented (Multi-Paxos, for example)

<br>

Networking:
 - int connections
 - Host myself
 - Host leader
 - List<Host> peers
 - SessionController sessionController

State:
 - Set<Integer> numberSet;
 - List<Integer> uniqueNumbers;

Timers:
 - ConnectionRetryTimer
 - SendTimer

<br>

The protocol starts by registering all messages, serializers, event handlers, reading configs, and creating the initial 
state. This also includes setting all network variables. The leader also creates a "SessionController" to manage 
peers' "Sessions". Each session contains a history of sent numbers a and a list of numbers yet to be sent. The leader
is configured by a designated port. The protocol is set up to execute in localhost, the process with the given port is
considered to be the leader. A list of unique numbers is also created. This allows for a faster unique number calculation
on a small universe of numbers. The list is a set of all number up to M (i.e. if M == 10, the list is {0,1,...,10}). 
This method is faster in contrary to picking a random number from 0 to M, and then checking if the number was already
picked, retrying until a unique number is found.

<br>
On startup, every peers tries to open connections to all other peers. When these out connections fail, the handler
for the event "OutConnectionUp" sets up a ConnectionRetry timer. Upon that timer, the peer retries again to reconnect to 
the host whose connection failed. The process continues until the connection is open.

<br>

Once all connections are set up, the leader starts its execution, setting up the SendTimer. Upon SendTimer, it picks a
number from 0 to uniqueNumbers.size(), selecting the number in the corresponding position in uniqueNumbers list, then, 
it removes that number from that list. It updates the sessions for all peers, adding the number to the queue of numbers 
to be sent. Upon each SendTimer event, the leader continuously sends all numbers in that queue to the peer of the 
corresponding session. When a peer receives an update, it updates its own Set, responding with an ACK message, allowing
the leader to remove the ACKed numbers from the toSend list in the corresponding session. The procedure keeps going until
all unique numbers have been successfully sent.

##### Update 1
 - Broadcasts are sent to everyone except "myself"
 - Connections are only opened to different peers (excludes "myself")
 - Crash recovery happens uponConnectionUp if the peer's numberSet is empty. Otherwise, the procedure continues (The session contains a list of numbers to be updated for each peer).

#### Notes
The protocol supports crash faults, by storing a history of sent numbers in all sessions. When a host reconnects, it 
requests a crash recovery message, where the leader will then send the entire history. This can be improved in several
fronts:
 - The history in all sessions is too expansive and redundant, as they are always going tob e equal. One history is enough
 - Each update can have its own version. Upon a new update, the peers inspect their latest version and the current one to be inserted, checking for inconsistencies in their history. When one is detected, only the history from to the last known consistent update is sent
 - For crash recovery, a periodic timer can write a file with the most recent history. On the protocol startup, it can check the latest version and request only the history since the last consistent update.




