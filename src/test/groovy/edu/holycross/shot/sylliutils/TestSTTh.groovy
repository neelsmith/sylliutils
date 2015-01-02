package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestSTTh extends GroovyTestCase {
    
  String courseFile = "testdata/syll.xml"
  String calFile = "testdata/f14.xml"


  /*
  @Test void testFixed() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"cal.html")

    STThCalendar stt = new STThCalendar(courseFile, calFile, outFile)
    System.err.println stt.extractFixedDates()
  }
  */

  @Test void testCal() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"cal.html")

    STThCalendar stt = new STThCalendar(courseFile, calFile, outFile)
    assert stt
    stt.debug = 8

    //LinkedHashMap fixedDates = mwf.extractFixedDates()
    //System.err.println mwf.getCourseDays()
    //def tab =     mwf.buildHtmlTable()

    stt.printCal()
    println "\n\nHERE's YOUR CALENDAR:"
    print stt.outFile.getText("UTF-8")
  }


}
