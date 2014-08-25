package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestMWF extends GroovyTestCase {
    
  String courseFile = "testdata/syll.xml"
  String calFile = "testdata/f13.xml"

  @Test void testFixed() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"cal.html")

    MonWedFriCalendar mwf = new MonWedFriCalendar(courseFile, calFile, outFile)
    System.err.println mwf.extractFixedDates()
  }

  @Test void testCal() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"cal.html")

    MonWedFriCalendar mwf = new MonWedFriCalendar(courseFile, calFile, outFile)
    assert mwf
    //LinkedHashMap fixedDates = mwf.extractFixedDates()
    //System.err.println mwf.getCourseDays()
    //def tab =     mwf.buildHtmlTable()
    mwf.printCal()
  }


}
