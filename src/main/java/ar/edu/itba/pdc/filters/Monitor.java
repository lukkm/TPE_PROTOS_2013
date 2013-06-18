package ar.edu.itba.pdc.filters;

public class Monitor {
	
	private static Monitor instance = null;
	
	public Monitor getInstance() {
		if (instance == null)
			instance = new Monitor();
		return instance;
	}
	
	public String execute() {
		String ans = "";
		
		return ans;
	}
}
