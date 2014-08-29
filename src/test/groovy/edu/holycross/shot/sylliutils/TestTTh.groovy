package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestTTh extends GroovyTestCase {
    
  String courseFile = "testdata/tthsyll.xml"
  String calFile = "testdata/f13.xml"

  @Test void testFixed() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"tthcal.html")

    TThCalendar tth = new TThCalendar(courseFile, calFile, outFile)
    Integer expectedFixedDates = 8
    assert tth.extractFixedDates().size() == expectedFixedDates
  }


  @Test void testCal() {
    File outDir = new File("testout")
    if (outDir.exists()) {
      outDir.deleteDir()
    }
    outDir.mkdir()
    File outFile = new File(outDir,"tthcal.html")

    TThCalendar tth = new TThCalendar(courseFile, calFile, outFile)
    assert tth
    //LinkedHashMap fixedDates = mwf.extractFixedDates()
    //System.err.println mwf.getCourseDays()
    //def tab =     mwf.buildHtmlTable()
    tth.printCal()
  }


}
