package ar.edu.itba.pdc.processor;

import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;
import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class TransformationFilter implements Filter{
	
	char[] changedVocabulary = "4B<D3FGH1JKLMN0PQRSTUVWQYZ4b<d3fgh1jklmn0pqrstuvwqyz".toCharArray();
	
	public TransformationFilter() {
		changes.put("a", "4");
		changes.put("e", "3");
		changes.put("i", "1");
		changes.put("o", "0");
		changes.put("c", "<");
	}
	
	Map<String,String> changes = new HashMap<String,String>();
	
	public void apply(Stanza stanza) {
		String s = ConfigurationCommands.getInstance().getProperty("transformation");
		if (s != null && s.equals("enabled")) {
			if (stanza.isMessage()) {
				String msg = ((Message)stanza.getElement()).getMessage();
				StringBuffer sb = new StringBuffer();
				if (msg != null) {
					for (int i = 0; i < msg.length(); i++) {
						if (changes.containsKey(msg.charAt(i))) 
							sb.append(changes.get(msg.charAt(i)));
						else
							sb.append(msg.charAt(i));
					}
					((Message)stanza.getElement()).setMessage(sb.toString());
				}
			}
		}
	}

}
