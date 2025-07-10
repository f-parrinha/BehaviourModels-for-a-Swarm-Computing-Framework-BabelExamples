# DevLog

This project contains a heavily modified implementation of the original protocol without JaTyC. 
Most of the changes revolve around using new callback handler objects and serializer classes. 
Another important change is the removal of "upon" methods, integrating their content directly on the "handle()" method, method that exists in the handler classes.

<br>

### Required modifications:
- Creation of "PublicGenericProtocol" that contains public methods, exposing GenericProtocol's protected methods such as openConnection, setupTimer, sendMessage, etc...
    - (WARNING) Very bad idea...
- Creation of other methods in the base protocol class, that exposes certain properties:
    - Getters for state and networking (myself, leader, ...) info
    - Methods that may check certain properties (isLeader)
    - Methods that may do "jobs" of certain tasks, updating their state



