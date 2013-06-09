package ar.edu.itba.pdc.parser;

public enum ParsingState {
	noState,
	parsingStart,
	messageBody,
	waitingClientAuthResponse,
	authBody,
	presenceDelay
}
