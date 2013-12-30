package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public interface Adder {

	public Context add(Context context) throws RemoteException,
			ContextException;
}
