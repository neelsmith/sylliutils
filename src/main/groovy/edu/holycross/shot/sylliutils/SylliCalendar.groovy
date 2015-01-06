package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class SylliCalendar {
  
  Integer debug = 0
  Integer WARN = 1
  Integer BUG = 2
  Integer SHOUT = 3


  /** File with XML version of course topics. */
  File courseData
  /** File with XML version of dates. */
  File calData
  /** Writable file for HTML output. */
  File outFile



  /** Root of groovy XmlParser's parsing of the XML from courseData.*/
  groovy.util.Node courseXml

  /** Map of dates -> events */
  def fixedDates = [:]

  /** */
  def highlights = []


  /** String used to separate assignment key and label.
   */
  def pairingDivider = '#'

  
  /** Ordered list of daily assignments and label.
   */
  def courseDays = []



  /** String to use in setting link to main css file,
   * following link to normalize.css, and prior to
   * calendar.css.
   */
  String mainCss = ""
  String cssDir = ""


  /** Constructor requiring two input files.
   * @param courseFileName Name of XML file with sequence of topics.
   * @param calFileName Name of file with calendar data.
   */
  SylliCalendar(String courseFileName, String calFileName) 
  throws Exception {
    courseData = new File(courseFileName)
    courseXml = new XmlParser().parse(this.courseData)        
    calData = new File(calFileName)
  }


  
  /** Extracts fixed dates from XML source for
   * syllabus, and maps by date.  Note that you 
   * can only have one fixed event per date.
   * @return A map of dates to special events. 
   */
  LinkedHashMap extractFixedDates() {
    def fixedDateMap = [:]
    this.courseXml.fixeddates[0].day.each { d ->
      fixedDateMap.putAt(d.'@date', d.text())
    }
    return fixedDateMap
  }

  /** Extracts an ordered list of assignments + labels.
   * @returns List of daily assignments.
   */
  ArrayList getCourseDays() {
    def dayList = []
    this.courseXml.day.each { d ->
      def pairing = d.'@key' +  pairingDivider + d.text()
      dayList.add(pairing)
    }            
    System.err.println "Number of events to schedule = " + dayList.size()
    return dayList
  }




  
  /** Builds a table closure 
   */
  Object getHtmlTable(String calType) {
    switch (calType) {
    case "stt":
    STThCalendar sstCal = new STThCalendar(calData, getCourseDays(), extractFixedDates())
    return sstCal.getHtmlTable()
    break

    default:
    throw new Exception("Calendar:buildHtmlTable: unrecognized calendar type '" + calType + "'")
    break
    }
  }

  
}
