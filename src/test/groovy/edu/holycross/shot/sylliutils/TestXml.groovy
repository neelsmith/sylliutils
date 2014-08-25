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

    String xml = parser.toXml(twoMonths)
    println xml
  }


}