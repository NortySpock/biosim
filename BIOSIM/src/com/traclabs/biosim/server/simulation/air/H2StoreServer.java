package biosim.server.simulation.air;

import biosim.idl.simulation.air.*;
import biosim.server.framework.*;
/**
 * The H2 Store Server.  Creates an instance of the H2Store and registers it with the nameserver.
 *
 * @author    Scott Bell
 */
public class H2StoreServer extends GenericServer{
	
	/**
	* Instantiates the server and binds it to the name server.
	* @param args aren't used for anything
	*/
	public static void main(String args[]) {
		H2StoreServer myServer = new H2StoreServer();
		H2StoreImpl myH2Store = new H2StoreImpl(myServer.getIDfromArgs(args));
		myServer.registerServerAndRun(new H2StorePOATie(myH2Store), myH2Store.getModuleName());
	}
}
