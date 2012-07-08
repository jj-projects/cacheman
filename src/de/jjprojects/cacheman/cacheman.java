/**
 * 
 */
package de.jjprojects.cacheman;

/**
 *  
 * @author Joerg Juenger, JJ-Projects
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;
import java.util.logging.Logger;

import org.jdom.*;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 changes Groundworks cache gpx files;<BR>
 adds comment field including Terrain, Difficulty and alike to get this information into an etrex Vista
 
 @author Copyright (C) 2012  JJ-Projects Joerg Juenger <BR>
  
<pre>
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 </pre>
 */
public class cacheman extends DefaultHandler
{
  static final String GPXTag        = "gpx";
  static final String NameTag       = "name";
  static final String TimeTag       = "time";
  static final String CachepointTag = "wpt";
  static final String CommentTag    = "cmt";
  static final String SymbolTag     = "sym";
  
  static final String TerrainTag    = "groundspeak:terrain";
  static final String DiffTag		= "groundspeak:difficulty";
  static final String TypeTag 		= "groundspeak:type";
  static final String ContainerTag  = "groundspeak:container";
  
  // string for the file name of the new cache location file
  static String destFileName;
  
  static Logger log = Logger.getLogger("JJProjects");
  
  
  public cacheman () {
     super();
     srcNodeStack = null;
     destRoot = null;
     cmtString = "";
     cmtElement = null;
     xmlGPXNS = Namespace.getNamespace("http://www.topografix.com/GPX/1/0");
     xmlGroundspeakNS = Namespace.getNamespace("http://www.groundspeak.com/cache/1/0");
  }

  public static void main( String[] argv ) {
     

    if( argv.length != 2 )
    {
      System.err.println( "Usage:" );
      System.err.println( "java cacheman.jar <XmlSourcePointsFile>  <XmlDestinationPointsFile>");
      System.err.println( "Example:" );
      System.err.println( "java cacheman.jar caches.gpx etrex_caches.gpx" );
      System.exit( 1 );
    }

    try {
       XMLReader xr = XMLReaderFactory.createXMLReader();
       cacheman handler = new cacheman();
       xr.setContentHandler(handler);
       xr.setErrorHandler(handler);
       
       destFileName = argv[1];
       FileReader r = new FileReader (argv[0]);
       
       xr.parse(new InputSource(r));

      
   } catch (SAXException  sxe ) {
      Exception e = ( sxe.getException() != null ) ? sxe.getException() : sxe;
      e.printStackTrace();
   } catch (IOException ioe) {
      ioe.printStackTrace();
   } finally {
   }
} // End of main function
  
  ////////////////////////////////////////////////////////////////////
  // Event handlers.
  ////////////////////////////////////////////////////////////////////


  public void startDocument ()
  {
     log.info("Start document");
     
     destNodeStack = new Stack<Element>();
     srcNodeStack = new Stack<String> ();
  }


  
  public void endDocument ()
  {
     log.info("End document");
      try {
         Document doc = new Document(destRoot);
         // serialize it onto System.out
         XMLOutputter serializer = new XMLOutputter();

         Format format = Format.getPrettyFormat();
         // use two space indent
         format.setIndent("  ");
         format.setLineSeparator ("\r\n"); 
         serializer.setFormat (format); 
         
         FileOutputStream outFile = new FileOutputStream (new File (destFileName));
         serializer.output(doc, outFile);
         
      } catch (IOException e) {
         System.err.println(e);
      }
  }


  ////////////////////////////////////////////////////////////////////
  // Element handling.
  ////////////////////////////////////////////////////////////////////
  public void startElement (String uri, String name,
                String qName, Attributes atts)
  {
     String nodeName;
     if ("".equals (uri)) {
        log.info("Start element: " + qName);
        nodeName = qName;
     } else {
        log.info("Start element: {" + uri + "}" + name + ", " + qName);
        nodeName = qName;
     }
     
     // handle the gpx tag
     if (GPXTag == nodeName) {
        destRoot = this.buildRootNode ();
        destNodeStack.push(destRoot);
     } 
     
     // push all other elements onto the new stack
     else {
    	 // Attention: in case the tag consists of two elements like groundspeak:container
    	 // we need to create the element with name space and name 
    	 Element destElement = null;
    	 if (nodeName.contains (":")) {
    		 int pos = nodeName.indexOf(":");
    		 destElement = new Element (nodeName.substring (pos+1), nodeName.substring(0, pos), xmlGroundspeakNS.getURI());
    	 } else
    		 destElement = new Element (nodeName, xmlGPXNS);

    	 for (int ix = 0; ix < atts.getLength(); ix++) {
    		 log.info("Start element: " + atts.getLocalName(ix) + ", "
    				 + atts.getType(ix) + ", " + atts.getValue(ix));
    		 
    	     destElement.setAttribute (new Attribute (atts.getLocalName(ix), atts.getValue(ix), Attribute.CDATA_TYPE));
    	 }
         destNodeStack.push(destElement);
     }
     
     srcNodeStack.push(nodeName);
     log.info("src stack: " + srcNodeStack); 
     log.info("dst stack: " + destNodeStack); 
  }

  public void endElement (String uri, String name, String qName)
  {
    String nodeName;
     if ("".equals (uri)) {
        log.info("End element: " + qName);
        nodeName = qName;
     } else {
        log.info("End element:   {" + uri + "}" + qName);
        nodeName = qName;
     }
         
    if (CachepointTag == nodeName) {
    	 // add comment tag string
    	 if (null != cmtElement)
    		 cmtElement.setText (cmtString);
    	 cmtElement = null;
    	 cmtString = "";
     }

     Element child = destNodeStack.pop ();
     if (! destNodeStack.isEmpty ())
        destNodeStack.peek().addContent(child);
     else if (child != destRoot)
        destRoot.addContent (child);
     
       
     srcNodeStack.pop();
     
     if (NameTag ==nodeName && ! srcNodeStack.isEmpty() &&srcNodeStack.peek().startsWith("wpt")){
       	// add comment tag after the name tag
       	assert (null == cmtElement);
   		cmtString = "";
   		cmtElement = new Element(CommentTag, xmlGPXNS);

   		// install the comment as a child of the topmost element on stack
   		destNodeStack.peek().addContent(cmtElement);

      }

  }


  public void characters (char ch[], int start, int length)
  {
      String str = "";
      log.finest("Characters:    \"");
      for (int i = start; i < start + length; i++) {
         switch (ch[i]) {
            case '\\':
               log.finest("\\\\");
               break;
            case '"':
               log.finest("\\\"");
               break;
            case '\n':
               log.finest("\\n");
               break;
            case '\r':
               log.finest("\\r");
               break;
            case '\t':
               log.finest("\\t");
               break;
            default:
               str = str.concat(Character.toString(ch[i]));
               break;
         }
      }
      
      log.finest(str + "\"\n");
      
      log.info("Stack Element: " + srcNodeStack.peek() + " == " + str);
      
      // build the cmt string here while looking into the tags values
      if (TerrainTag == srcNodeStack.peek()) {
    	  addToCmt("T" + str); 	  		
      }
      if (DiffTag == srcNodeStack.peek()) {
    	  addToCmt("D" + str);	  		
      }
      
      if (TypeTag == srcNodeStack.peek()) {
    	  if (str.equalsIgnoreCase("Geocache|Traditional Cache"))
    		  addToCmt("TC"); 	  		else
    			  if (str.equalsIgnoreCase("Geocache|Multi-Cache"))
    				  addToCmt("MC"); 	  		else
    					  if (str.equalsIgnoreCase("Geocache|Event Cache"))
    						  addToCmt("EC"); 	  		else
    							  if (str.equalsIgnoreCase("Geocache|Letterbox Hybrid"))
    								  addToCmt("LC"); 	  		else
    									  if (str.equalsIgnoreCase("Geocache|Webcam Cache"))
    										  addToCmt("WC"); 	  		else
    											  if (str.equalsIgnoreCase("Geocache|Virtual Cache"))
    												  addToCmt("VC"); 	  		
      }

      if (ContainerTag == srcNodeStack.peek()) {
    	  if (str.equalsIgnoreCase("Small"))
    		  addToCmt("CS"); 	  		else
    			  if (str.equalsIgnoreCase("Micro"))
    				  addToCmt("CM"); 	  		else
    					  if (str.equalsIgnoreCase("Regular"))
    						  addToCmt("CR"); 	  		else
    							  if (str.equalsIgnoreCase("Large"))
    								  addToCmt("CL"); 	  		else
    									  addToCmt("CU");
      }

      // set string value of each element
      if (null != destNodeStack.peek()) {
         destNodeStack.peek().addContent(str);
      }
  }

  private Element buildRootNode () {
     Element root =  new Element (GPXTag, xmlGPXNS);
     root.setAttribute("version", "1.0");
     root.setAttribute("creator", "cacheman by Joerg Juenger, JJ-Projects");
     Namespace xsiNS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
     root.addNamespaceDeclaration (xsiNS);

     root.setAttribute (new Attribute("schemaLocation",
              "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd",
                xsiNS));

    // Element metaData = new Element ("metadata");
    // metaData.addContent (timeNowElement());
    // root.addContent (metaData);
     
     return root;
  }
  
  @SuppressWarnings("unused")
  private Element timeNowElement () {
     Element timeEle = new Element (TimeTag);
     Calendar cal = Calendar.getInstance();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
     timeEle.setText(sdf.format(cal.getTime()));

     return timeEle;
  };
  
  private void addToCmt (String str) {
	 if (null == str)
		 return;
	 
	 if (null == cmtString)
		 cmtString = "";
	 
	 if (! cmtString.isEmpty()) 
		  cmtString += "-";
	 cmtString += str;
  }
  
  private Stack<String> srcNodeStack;
  private Stack<Element> destNodeStack;
  private Element destRoot;
  private Namespace xmlGPXNS;
  private Namespace xmlGroundspeakNS;
  private String cmtString;
  private Element cmtElement;
  
}  // End of Class Body
  

/*
 * Sample for cmt element:

   <cmt>TC-CU-D1.5-T1</cmt>

Description:

TC = Traditional Cache
MC = Multi-Cache
VC = Virtual Cache


CU = Container Unknown
CM = Container Micro
CS = Container Small
CR = Container Regular
CL = Container Large

D<num> = Difficulty <Zahl>

T<num> = Terrain <Zahl>

*/
 

