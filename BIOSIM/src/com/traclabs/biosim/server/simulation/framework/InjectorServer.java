package biosim.server.simulation.framework;

import biosim.idl.simulation.framework.*;
import biosim.server.framework.*;
import biosim.server.simulation.framework.*;
/**
 * The Injector Server.  Creates an instance of the Sim Environment and registers it with the nameserver.
 *
 * @author    Scott Bell
 */

public class InjectorServer extends GenericServer{
	
	/**
	* Instantiates the server and binds it to the name server.
	* @param args first argument checked for ID of server
	*/
	public static void main(String args[]) {
		InjectorServer myServer = new InjectorServer();
		InjectorImpl myInjector = new InjectorImpl(myServer.getIDfromArgs(args), myServer.getNamefromArgs(args));
		myServer.registerServerAndRun(new InjectorPOATie(myInjector), myInjector.getModuleName());
	}
}

