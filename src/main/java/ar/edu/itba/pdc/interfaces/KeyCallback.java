package ar.edu.itba.pdc.interfaces;

import java.nio.channels.SelectionKey;

public interface KeyCallback {

	public void connect(SelectionKey key);
	
	public void accept(SelectionKey key);
	
	public void read(SelectionKey key);
	
	public void write(SelectionKey key);
	
}
