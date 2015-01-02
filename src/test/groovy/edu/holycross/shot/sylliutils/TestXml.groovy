package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestXml extends GroovyTestCase {
    

  String twoMonths = """
   September 2014
Su Mo Tu We Th Fr Sa
    1  2  3  4  5  6
 7  8  9 10 11 12 13
14 15 16 17 18 19 20
21 22 23 24 25 26 27
28 29 30

    October 2014
Su Mo Tu We Th Fr Sa
          1  2  3  4
 5  6  7  8  9 10 11
12 13 14 15 16 17 18
19 20 21 22 23 24 25
26 27 28 29 30 31
"""


  @Test void testStringInput() {
    CalParser parser = new CalParser()
    assert parser

    String xml = parser.toXml(twoMonths, 2014)
    // this is ok:
    println "XML FROM STRING: \n" + xml
    println "\n\n"
  }

  /*

  String f14 = """
   September 2014
Su Mo Tu We Th Fr Sa
    1  2  3  4  5  6
 7  8  9 10 11 12 13
14 15 16 17 18 19 20
21 22 23 24 25 26 27
28 29 30

    October 2014
Su Mo Tu We Th Fr Sa
          1  2  3  4
 5  6  7  8  9 10 11
12 13 14 15 16 17 18
19 20 21 22 23 24 25
26 27 28 29 30 31

   November 2014
Su Mo Tu We Th Fr Sa
                   1
 2  3  4  5  6  7  8
 9 10 11 12 13 14 15
16 17 18 19 20 21 22
23 24 25 26 27 28 29
30

"""


String dec = """
   November 2014
Su Mo Tu We Th Fr Sa
                   1
 2  3  4  5  6  7  8
 9 10 11 12 13 14 15
16 17 18 19 20 21 22
23 24 25 26 27 28 29
30

   December 2014
Su Mo Tu We Th Fr Sa
    1  2  3  4  5  6
 7  8  9 10 11 12 13
14 15 16 17 18 19 20

"""



  @Test void testFileInput() {
    CalParser parser = new CalParser()
    assert parser

    parser.debug = 8
    File src = new File("testdata/f14.txt")

    String fileXml = parser.toXml(src)
    String stringXml = parser.toXml(src.getText())
    assert fileXml == stringXml
    println "XML FROM FILE STRING: \n" + stringXml    
    println "XML FROM FILE: \n" + fileXml    


    String inLineXml = parser.toXml(f14)
    println "INLINE XML: \n" + inLineXml    
    //assert stringXml == inLineXml


    def novdec = parser.parseCal(dec, 2015)
    println "Nov: " + novdec["November"]
    println "Dec: " + novdec["December"]

    
    String decemberXml = parser.toXml(dec)
    //println "NOV-DECEMBER XML: \n" + decemberXml

  }
  */

}
