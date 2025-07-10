# DevLog

##### Update 1
 - Changed the code to support JaTyC in most ways
 - Connections no longer is an integer, but a list of connected hosts
   - Gives more security compared to incrementing/decrementing a counter, as the given host can be compared
 - JatycGenericProtocol serves as wrapper for the GenericProtocol from Babel, but also has more features, such as connecting to the Peer network

##### Update 2 (Final Product)
 - Initial code was terrible. Removed JatycGenericProtocol and kept the original one. The upon methods are now public and the handlers call these methods
   - No need to re-implement them

