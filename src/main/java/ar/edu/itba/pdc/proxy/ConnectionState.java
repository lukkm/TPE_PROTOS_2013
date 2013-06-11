package ar.edu.itba.pdc.proxy;

public enum ConnectionState {
	noState,
	waitingForStream,
	starting,
	negotiating,
	ready,
	connected
}
