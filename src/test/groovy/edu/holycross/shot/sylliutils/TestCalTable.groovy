package edu.holycross.shot.sylliutils

import static org.junit.Assert.*
import org.junit.Test


class TestCalTable extends GroovyTestCase {
    
  String courseFile = "testdata/syll.xml"
  String calFile = "testdata/f14.xml"

  @Test void testCalStrings() {
    SylliCalendar stt = new SylliCalendar(courseFile, calFile)
    assert stt
    String sttTab =  stt.getHtmlTable("stt")
    // test that this is well-formed HTML  with root element <table> ...

    String mwfTab =  stt.getHtmlTable("mwf")
    // test that this is well-formed HTML  with root element <table> ...
  }



}
