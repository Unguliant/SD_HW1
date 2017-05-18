package il.ac.technion.cs.sd.book.test;

import il.ac.technion.cs.sd.book.app.DomXMLParser;
import il.ac.technion.cs.sd.book.app.XPathXMLParser;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.SortedMap;


public class XMLParserTest extends SdHw1Test{

  private static String getXMLStringFromFile(String fileName) throws FileNotFoundException {
    return new Scanner(new File(XMLParserTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
  }

  @Test
  public void testSimple() throws Exception {
    System.out.println("DOMParser says:");
    String xml = getXMLStringFromFile("xmlParserTest.xml");
    SortedMap[] sortedMaps = DomXMLParser.parseXMLToSortedMap(xml);
    System.out.println(sortedMaps[0]);
    System.out.println(sortedMaps[1]);
  }

  @Test
  public void testSimple2() throws Exception {
    System.out.println("XPATH says:");
    String xml = getXMLStringFromFile("xmlParserTest.xml");
    SortedMap[] sortedMaps = XPathXMLParser.parseXMLToSortedMap(xml);
    System.out.println(sortedMaps[0]);
    System.out.println(sortedMaps[1]);
  }
}
