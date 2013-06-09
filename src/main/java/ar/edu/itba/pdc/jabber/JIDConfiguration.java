package ar.edu.itba.pdc.jabber;

import org.apache.commons.codec.binary.Base64;

public class JIDConfiguration extends JabberElement {

	private String JID;
	
	public void setJID(String encodedJID) {
		Base64 decoder = new Base64();
		String decodedText = new String(decoder.decode(encodedJID.getBytes()));
		String[] strArray = decodedText.split(",");
		String username = strArray[0].split("\"")[1];
		String realm = strArray[1].split("\"")[1];
		this.JID = username + "@" + realm;
	}
	
	public String getJID() {
		return JID;
	}
	
}
