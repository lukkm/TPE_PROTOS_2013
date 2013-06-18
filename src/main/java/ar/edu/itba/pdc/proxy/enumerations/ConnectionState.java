package ar.edu.itba.pdc.proxy.enumerations;

public enum ConnectionState {
	noState,
	waitingForStream,
	negotiating,
	ready,
	connectingToServer,
	waitingForServerFeatures,
	connected	
}
