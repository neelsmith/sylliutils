package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestLineParser extends GroovyTestCase {
    
  String leadingZeroes = " 2  3  4  5  6  7  8"
  String lateStart = "          1  2  3  4"
  String sixDays = "26 27 28 29 30 31"


  @Test void testCalParser() {
    CalParser parser = new CalParser()
    assert parser
    
    def weekArray = parser.parseWeekString(leadingZeroes)
    assert weekArray.size() == 7
    
    weekArray.clear()
    weekArray = parser.parseWeekString(lateStart)
    assert weekArray.size() == 7
    
    String sunday = weekArray[0]
    assert sunday.size() == 0

    weekArray.clear()
    weekArray = parser.parseWeekString(sixDays)
    assert weekArray.size() == 6
  }


}
