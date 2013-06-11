package ar.edu.itba.pdc.proxy;

public enum ConnectionState {
	noState,
	waitingForStream,
	negotiating,
	ready,
	connectingToServer,
	waitingForServerFeatures,
	connected	
}
