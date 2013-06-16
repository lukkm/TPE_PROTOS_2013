package ar.edu.itba.pdc.parser.enumerations;

public enum ParsingState {
	noState,
	parsingStart,
	messageBody,
	waitingClientAuthResponse,
	authBody,
	presenceDelay,
	activeState
}
