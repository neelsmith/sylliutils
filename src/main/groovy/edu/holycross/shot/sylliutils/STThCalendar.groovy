package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class STThCalendar {
  
  Integer debug = 0
  Integer WARN = 1
  Integer BUG = 2
  Integer SHOUT = 3


  /** File with XML version of dates. */
  File calData

  /** Map of dates -> events */
  def fixedDates = [:]

  /** ordered list of pairings, key + label
   */
  def courseDays = []

  /** 
   */
  def pairingDivider = '#'



  /** Constructor.*/
  STThCalendar(File calFile, ArrayList courseEvents, LinkedHashMap specialEvents) 
  throws Exception {
    calData = calFile
    courseDays = courseEvents
    fixedDates = specialEvents

  }




  /// NEEDS:
  //  1.  calData.  XML calendar source
  // fixedDates
  // courseDays
  /** Builds a closure creating an HTML table element
   * suitable for using with a groovy markup builder.
   */
  Object buildHtmlTable() {
    def eventDates = new XmlParser().parse(calData)
    def stt = eventDates.month.week.day.findAll { it.'@stth' != null}

    // special events to highlight at end of week
    def highlights = []


    //def builder = new StreamingMarkupBuilder()
    // INCLUDE XML DECLARATAION
    def t = {
      table {
	tr {
	  th("Sunday")
	  th("Tuesday")
	  th("Thursday")
	  th("NB")
	  
	}
	stt.eachWithIndex { evt, i ->
	  if (debug > 0) { System.err.println "Proess EVT " + evt }
	  def sunDateString = "${evt.'@month'} ${evt.'@date'}"
	  if (debug > 0) {System.err.println "Check on SUN date ${sunDateString}" }
	  if (fixedDates[sunDateString]) {
                
	    if (debug > 0) { System.err.println "FOUND FIXED DATE for ${sunDateString}:  " + fixedDates[sunDateString]}
	    highlights.add(fixedDates[sunDateString])
	  } else {
	    //System.err.println "NO MATCH IN FIXED DATES FOR ${monDateString}"
	    // no match
	  }
	  
	  if (i.mod(3) == 0) {
	    // Index into events array:
	    def sunIdx = Integer.parseInt(evt.'@stth' ) - 1
	    // peek ahead to Tues, so we can lay out
	    // whole week as one row:
	    def nextEvt= stt[i + 1]
	    def thirdEvt= stt[i + 2]
	    if (!nextEvt) {
	      System.err.println "ERROR! No next event at index ${i + 1}."
              
	    } else if (!thirdEvt) {
	      System.err.println "ERROR! No next event at index ${i + 2}."
                            

	    } else {
	      def tuesDateString =  "${nextEvt.'@month'} ${nextEvt.'@date'}"
	      if (fixedDates[tuesDateString]) {
		if (debug > 0) { println "FIXED DATE: " + fixedDates[tuesDateString] }
		highlights.add(fixedDates[tuesDateString])
	      } else {
		//System.err.println "NO FIXED DATE FOR WED ${wedDateString}"
		// no match
	      }
	      def tuesIdx =   Integer.parseInt(nextEvt.'@stth' ) - 1


	      def thursDateString =  "${thirdEvt.'@month'} ${thirdEvt.'@date'}"
	      if (fixedDates[thursDateString]) {
		if (debug > 0) { println "FIXED DATE: " + fixedDates[thursDateString] }
		highlights.add(fixedDates[thursDateString])
	      } else {
		//System.err.println "NO FIXED DATE FOR FRI ${friDateString}"
		// no match
	      }
	      def thursIdx =   Integer.parseInt(thirdEvt.'@stth' ) - 1


	      tr {
		// SUNDAY:
		td () {
		  span(class : "dateLabel", "${sunDateString}") 
		  if (debug > 0) {println "Sun ${sunIdx}-${sunDateString} "}
		  if (sunIdx < courseDays.size()) {
		    def keyValArr = courseDays[sunIdx].split(pairingDivider)
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

		// TUESDAY:
		td () {
		  span(class : "dateLabel", "${tuesDateString}") 
		  if (debug > 0) {println "Tues ${tuesIdx}-${tuesDateString} "}
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
		td() {
		  span(class : "dateLabel", "${thursDateString}") 
		  if (debug > 0) {println "Thurs ${thursIdx}-${thursDateString} "}
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
	    }

	  } else {
	    highlights.clear()
	  }
	}
      }
    }
    return t
  }

  /**
   * Creates a String with the HTML for a Sun/Tues/Thurs calendar.
   * @return A String of well-formed HTML consisting of a single
   * <table> element.
   */
  String getHtmlTable() {
    def tab  = buildHtmlTable()
    def builder = new StreamingMarkupBuilder()
    def htmlOut = builder.bind() {
      out << tab
    }
    return htmlOut.toString()
  }
}
