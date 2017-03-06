package in.ac.iiit.cvit.heritage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by HOME on 03-03-2017.
 */

public class PackageReader {
    public static final String LOGTAG = "PackageReader";
    private final String dataLocation;
    private final String xmlFile;
    public String _packageName;
    InterestPoint interestPoint;
    ArrayList<InterestPoint> MonumentsList;
    ArrayList<InterestPoint> KingsList;



    private Context context;

    /**
     * This is the class used to read content of the xml file
     * @param packageName name of the package name i.e. heritage site
     * @param _context
     */
    public PackageReader(String packageName, Context _context){
        context = _context;
        _packageName = packageName;
        dataLocation = context.getString(R.string.extracted_location);

        String prevLanguage = Locale.getDefault().getLanguage();
        xmlFile = _packageName + "_" + prevLanguage + context.getString(R.string.xml_extension);
        Log.v(LOGTAG, "Name of the xml file is " + xmlFile);

        //xmlFile = context.getString(R.string.xml_file);
        MonumentsList = new ArrayList<InterestPoint>();
        KingsList = new ArrayList<InterestPoint>();
        readFromFile();
    }

    /**
     * This is the method  is accessible from outside and gives data on monuments
     * @return Array list of all the InterestPoints info which is already calculated when this class is initialised
     */
    public ArrayList<InterestPoint> getMonumentsList(){
        return MonumentsList;
    }

    /**
     *
     *This is the method  is accessible from outside and gives data on kings
     * @return Array list of all the KingsList info which is already calculated when this class is initialised
     */
    public ArrayList<InterestPoint> getKingsList(){
        return KingsList;
    }


    /**
     * This method is called from readFromFile
     * This method extracts information from xml file according to their tags in xml file
     * This method calls InterestPoint object to set the interest points
     * It stores obtained interest points in InterestPoint array
     * @param xml It is the string containing all the data from the d.xml file
     */
    private void readContentsFromString(String xml){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(xml);
            ByteArrayInputStream xmlfile = null;
            try {
                xmlfile = new ByteArrayInputStream(
                        xmlStringBuilder.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Document doc = null;
            try {
                doc = builder.parse(xmlfile);

                //get the first element
                Element root = doc.getDocumentElement();

                Node monument = root.getElementsByTagName("monument").item(0);
                Node ruler = root.getElementsByTagName("ruler").item(0);

                //get all the child elements
                NodeList ips = monument.getChildNodes();
                NodeList kings = ruler.getChildNodes();

                //Following for loop is for ip tagged objects
                for(int i=0; i<ips.getLength(); i++){

                    if(ips.item(i).getNodeType() == Node.ELEMENT_NODE){

                        //We are creating an interest point object to store all the relevant data available
                        interestPoint = new InterestPoint();

                        //getting a list of all the child elements
                        NodeList keys = ips.item(i).getChildNodes();

                        for(int j=0; j<keys.getLength(); j++){
                            if(keys.item(j).getNodeType() == Node.ELEMENT_NODE){
                                Element key = (Element)keys.item(j);
                                ///This interest point contains all the data relevant to particular interest point
                                interestPoint.setMonument(key.getNodeName(), key.getTextContent());
                            }
                        }
                        //Here we are storing each InterestPoint object in an InterestPoint array
                        //So InterestPoints has all the information on all the interest points available for a heritage site
                        MonumentsList.add(interestPoint);
                    }
                }

                for(int i=0; i<kings.getLength(); i++){

                    if(kings.item(i).getNodeType() == Node.ELEMENT_NODE){

                        //We are creating an interest point object to store all the relevant data available
                        interestPoint = new InterestPoint();

                        //getting a list of all the child elements
                        NodeList keys = kings.item(i).getChildNodes();

                        for(int j=0; j<keys.getLength(); j++){
                            if(keys.item(j).getNodeType() == Node.ELEMENT_NODE){
                                Element key = (Element)keys.item(j);
                                ///This interest point contains all the data relevant to particular interest point
                                interestPoint.setRoyal(key.getNodeName(), key.getTextContent());
                            }
                        }
                        //Here we are storing each InterestPoint object in an InterestPoint array
                        //So InterestPoints has all the information on all the interest points available for a heritage site
                        KingsList.add(interestPoint);
                    }
                }



            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when this class is initialised.
     * It calls readTextFile and readContentsFromString to get the contents from heritage storage folder
     */
    private void readFromFile(){
        File baseLocal = Environment.getExternalStorageDirectory();

        File xmlfile = new File(baseLocal, dataLocation + _packageName + "/" + xmlFile );
        try {
            FileInputStream xmlStream = new FileInputStream(xmlfile);
            String contents = readTextFile(xmlStream);
            readContentsFromString(contents);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called from readFromFile
     *This class reads the entire d.xml file into a string. THis string is used as input to readContentsFromString
     * @param inputStream It is an InputStream file containing location of the file to be read into string
     * @return String containing the contents of the d.xml file
     */
    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
