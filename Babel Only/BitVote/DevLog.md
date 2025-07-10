# DevLog

The developed protocol allows for a network of N peers to decide a bit. The protocol consists of voting rounds,
identified by a unique ID, where each round can have a status. To achieve consensus, a quorum is used, although the
protocol tries to capture as many votes as possible.

### Pseudo-code
Messages:
 - VoteRequest
 - VoteAck
 - VoteWriteBack

Timers
 - ConnectionRetryTimer
 - VoteTimer

State
 - Map<String, VoteRound> voteRounds;
 - Map<String, VoteRecord> voteHistory;
 - String currentRoundID;



<br>

The code starts by deciding who is the leader, using a config line called "leader_port". All nodes, currently,
run in localhost so the address is the same. Who has the same port as "leader_port" is considered to be the leader.
Later, it tries to establish connections to all peers. When it receives "OutConnectionFailed" event, it triggers a
"ConnectionRetryTimer". Upon this timer, the leader retries reconnecting to the corresponding peer, whose connection
failed.

<br>

Now the protocol may start. When all connections have been established, a "VoteTimer" is set up. Upon this timer
the leader gets the "VoteRound" with "currentRoundID" (if not found, creates a new one and updates the "currentRoundID")
and then sends a "VoteRequest" with that round ID. All peers that receive this message, collect the corresponding 
"VoteRecord" in their history with the given round ID, and reply back using a "VoteAck" message, with their old 
vote. If no record is found in their history, they create a new VoteRecord with their new decision.

<br>

For each VoteRound, their can be up to N retries, with N > 0. This is why a vote history is so important, to make sure 
peers that have already decided on a bit don't change their vote. The minimum requirement for a VoteRound to be 
successful is a quorum of N/2 + 1 VoteAcks, although, even if the quorum is met, the leader will still retry sending 
VoteRequests in order to try to collect as many votes as possible, reaching a more accurate decision. If the quorum is 
not met, the VoteRound is considered to have failed. At the end of a VoteRound, the leader sends a "VoteWriteBack" 
message to all peers, containing the round ID, decided bit, and the status of the given round.

##### Update 1
 - Simplified round status evaluation code. Evaluation is handled in uponVoteTimer (no longer in ACK message handler)
   - This allows for a much cleaner code. The downside is that it takes at least one second longer to send the writeback
 - WriteBack and VoteRequest retry is also handled in uponVoteTimer. This allows to do a writeback in the case of failure, so the voting nodes can get a more consistent vote history
 - Added evaluate() method to the VoteRound class and removed fail() and succeed() methods
 - Added a restriction to decide() method to only return a valid bit if the round is already finished

### Features
The protocol gives the following guarantees:
 - A voting round will always finish, with either SUCCESS or FAIL status
 - Tolerates lost VoteAck messages. A voting round can have up to M retries (configurable on the config.properties file)
 - Tolerates missing participants, via a quorum decision. To achieve a quorum, N/2 + 1 VoteAck messages need to be received by the leader
 - A peer cannot change its vote during a round retry (TODO)