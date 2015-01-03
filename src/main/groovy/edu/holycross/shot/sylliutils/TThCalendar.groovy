package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class TThCalendar {

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

  /** Map of dates -> events. */
  def fixedDates = [:]

  def highlights = []

  // ordered list of pairings, key + label
  def courseDays = []

  
  String pairingDivider = '#'

  /** String to use in setting link to main css file,
   * following link to normalize.css, and prior to
   * calendar.css.
   */
  String mainCss = ""


  /** Constructor requiring two input files and an output file.
   * @param courseFileName Name of XML file with sequence of topics.
   * @param calFileName Name of file with calendar data.
   * @param outputFile Writable file for resulting HTML.
   */
  TThCalendar(String courseFileName, String calFileName, File outputFile) 
  throws Exception {
    courseData = new File(courseFileName)
    courseXml = new XmlParser().parse(this.courseData)        
    calData = new File(calFileName)
    this.outFile = outputFile
  }



  /** Determines parity of an integer.
   * @param n The number to assess.
   * @returns True if n is even.
   */
  boolean even(int n) {
    return (n.mod(2) == 0)
  }


  /** Creates map of special or extracurricular events 
   * keyed by date string.
   */
  LinkedHashMap extractFixedDates() {
    def fixedDateMap = [:]
    this.courseXml.fixeddates[0].day.each { d ->
      fixedDateMap.putAt(d.'@date', d.text())
    }
    return fixedDateMap
  }


  /** Creates ordered list of daily topics
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



  Object buildHtmlTable() {
    def eventDates = new XmlParser().parse(calData)
    def tth = eventDates.month.week.day.findAll { it.'@tth' != null}
    System.err.println  "number of TTh dates available = " + tth.size()

    // special events to highlight at end of week
    def highlights = []


    def builder = new StreamingMarkupBuilder()
    // INCLUDE XML DECLARATAION
    def t = {
      table {
	tth.eachWithIndex { evt, i ->
	  if (debug > WARN) { System.err.println "Process EVT " + evt }
	  def tuesDateString = "${evt.'@month'} ${evt.'@date'}"
	  if (debug > WARN) {System.err.println "Cehck on TUES date ${tuesDateString}" }
	  if (fixedDates[tuesDateString]) {
	    if (debug > WARN) { System.err.println "FOUND FIXED DATE for ${tuesDateString}:  " + fixedDates[tuesDateString]}
	    highlights.add(fixedDates[tuesDateString])
	  } else {
	    if (debug > WARN) { System.err.println "NO MATCH IN FIXED DATES FOR ${tuesDateString}" }
	    // no match
	  }
                    
	  if (even(i)) {
	    // Index into events array
	    Integer tuesIdx = Integer.parseInt(evt.'@tth' ) - 1
	    // peek ahead to Thurs, so we can lay out
	    // whole week as one row:
	    def nextEvt= tth[i + 1]
	    Integer thursIdx = Integer.parseInt(nextEvt.'@tth' ) - 1
	    def thursDateString =  "${nextEvt.'@month'} ${nextEvt.'@date'}"
	    if (fixedDates[thursDateString]) {
	      if (debug > WARN) { println "FIXED DATE: " + fixedDates[thursDateString] } 
	      highlights.add(fixedDates[thursDateString])
	    }

	    tr {
	      //TUESDAY
	      td () {
		span(class : "dateLabel", "${tuesDateString}") 
		if (debug > WARN) {println "Tues ${tuesIdx}-${tuesDateString} "}
		if (tuesIdx < courseDays.size()) {
		  def keyValArr = courseDays[tuesIdx].split(pairingDivider)
		  switch (keyValArr[0]) {
		  case 'none': 
		  mkp.yield keyValArr[1]
		  break;
		  
		  case 'null':
		  span (class: 'skip', "${keyValArr[1]}")
		  break;

		  default:
		  if (keyValArr.size() > 1) {
		    a (href : "assignments/${keyValArr[0]}", "${keyValArr[1]}") 
		  } else {
		    mkp.yield "${keyValArr}"
		  }
		  break;
		  }
		}
	      } 

	      // THURSDAY
	      td () {
		span(class : "dateLabel", "${thursDateString}") 
		if (debug > WARN) {println "Thurs ${thursIdx}-${thursDateString} "}
		if (thursIdx < courseDays.size()) {
		  def keyValArr = courseDays[thursIdx].split(pairingDivider)
		  switch (keyValArr[0]) {
		  case 'none': 
		  mkp.yield keyValArr[1]
		  break;
		  case 'null':
		  span (class: 'skip', "${keyValArr[1]}")
		  break;

		  default:
		  if (keyValArr.size() > 1) {
		    a (href : "assignments/${keyValArr[0]}", "${keyValArr[1]}") 
		  } else {
		    mkp.yield "${keyValArr}"
		  }
		  break;
		  }
		}
	      }
                                
	      // special notes for the week:
	      td {
		highlights.each {
		  span(class : "highlight", "${it}")
		}
	      }
	    }
	  
	  } else {
	    highlights.clear()
	  }
	}
      }
    }
  }

  void printCal() {
    this.fixedDates = extractFixedDates()

    def coursename = courseXml.coursename.text()
    def lastmod = courseXml.lastmod.text()

    def tab  = buildHtmlTable()
    
    def builder = new StreamingMarkupBuilder()
    def htmlOut = builder.bind() {
      //mkp.xmlDeclaration()
      html {
	head {
	  meta(charset : "UTF-8")
	  title ("${coursename}: schedule")
	  link (rel : "stylesheet", type : "text/css", href : "css/normalize.css")
	  link (rel : "stylesheet", type : "text/css", href : "${mainCss}" )
	  link (rel : "stylesheet", type : "text/css", href : "css/calendar.css")
	}

	body {
	  header (role: "banner") {
	    nav(role: "navigation") {
	      ul {
		li {
		  mkp.yield "Course "
		  a (href : "index.html","home page")
		}
		li {
		  a (href : "requirements.html", "requirements")
		}
		li {
		  a (href : "resources.html", "resources")
		}
	      }
	    }
	  }

	  article(role: "main") {
	    h1("Course schedule: ${coursename}")
	    p (class : "lastmod", "${lastmod}")
	    out << tab
	  }
	  footer {}
	}
      }
    }
    outFile <<  htmlOut
  }

}

  

