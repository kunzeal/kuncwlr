package kuncwlr;

import java.io.IOException;
import java.util.Set;

import org.xml.sax.SAXException;

public interface HTMLExtractor {
	//extracte Page and enqueue into lq
	public Set<String> XMLextract(Page page) throws SAXException, IOException;
}
