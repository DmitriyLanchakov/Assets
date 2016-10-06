package Assets.MCIEX;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MCIEXXMLParser extends DefaultHandler {

	public MCIEXXMLParser(java.io.Reader reader) {
		super();
		process(reader);
	}
	public void process(java.io.Reader reader) {
		_map = null;
		try {
			_xmlReader =  XMLReaderFactory.createXMLReader();
			_xmlReader.setContentHandler(this);
			_xmlReader.setErrorHandler(this);
			_xmlReader.parse(new InputSource(reader));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public void startElement(String uri, String name,
			      String qName, Attributes atts) {
   		if (name.equalsIgnoreCase("row")) {
   			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
   				Date date = df.parse(atts.getValue("TRADEDATE"));
   				//Double price = new Double(atts.getValue("WAPRICE"));
   				Double price = new Double(atts.getValue("LEGALCLOSEPRICE"));
   				if (date==null || price==null)
   					throw new ParseException("Date="+date+" price="+price, 0);
				if (_map == null) 
					_map = new TreeMap<Date,Double>();
				_map.put(date, price);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}
    }
    public TreeMap<Date,Double> get() {
    	return _map;
    }
    private XMLReader _xmlReader = null;
    private TreeMap<Date,Double> _map = null;
}
