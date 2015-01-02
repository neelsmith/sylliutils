package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestCalImp extends GroovyTestCase {
    

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
    LinkedHashMap calendar = parser.parseCal(twoMonths)

    assert calendar.keySet().size() == 2
    def septWeeks = calendar["September"]
    assert septWeeks.size()  == 5
  }



  
  @Test  void testFileInput() {
    File calIn = new File("testdata/f14.txt")
    CalParser parser = new CalParser()
    assert parser
    LinkedHashMap calendar = parser.parseCal(calIn.getText()) 
    def septWeeks = calendar["September"]
    assert septWeeks.size()  == 5
  }

}
