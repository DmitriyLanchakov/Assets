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
	/**
	 * Parses data gotten from MCIEX in XML format
	 * @param reader data reader (object that reads data from MCIEX site (www.moex.com))
	 */
	public MCIEXXMLParser(java.io.Reader reader) {
		super();
		process(reader);
	}
	/**
	 * Starts the parser
	 * @param reader data reader
	 */
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
	/**
	 * Processes the element in XML tree. Stores processed elements data to TreeMap<Date, Double>
	 * where Double represents the price of the element for the Date
	 * @param uri the URI
	 * @param name the local name
	 * @param qName the qualified (prefixed) name
	 * @param atts the attributes for the element
	 */
	@Override
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
	/**
	 * Returns parsed data
	 * @return map of asset's prices located by date growing order
	 */
    public TreeMap<Date,Double> get() {
    	return _map;
    }
    private XMLReader _xmlReader = null;
    private TreeMap<Date,Double> _map = null;
}
