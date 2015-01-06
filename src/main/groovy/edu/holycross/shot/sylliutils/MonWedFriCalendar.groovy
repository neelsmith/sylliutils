package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class MonWedFriCalendar {
  boolean debug = false


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
  MonWedFriCalendar(File calFile, ArrayList courseEvents, LinkedHashMap specialEvents) 
  throws Exception {
    calData = calFile
    courseDays = courseEvents
    fixedDates = specialEvents
  }




  Object buildHtmlTable() {
    def eventDates = new XmlParser().parse(calData)
    def mwf = eventDates.month.week.day.findAll { it.'@mwf' != null}
    if (debug) {
      System.err.println  "number of MWF dates available = " + mwf.size()
    }

    // special events to highlight at end of week
    def highlights = []


    def builder = new StreamingMarkupBuilder()
    // INCLUDE XML DECLARATAION
    def t = {
      table {
	mwf.eachWithIndex { evt, i ->
	  if (debug) { System.err.println "Proess EVT " + evt }
	  def monDateString = "${evt.'@month'} ${evt.'@date'}"
	  if (debug) {System.err.println "Cehck on MON date ${monDateString}" }
	  if (fixedDates[monDateString]) {
	    
	    if (debug) { System.err.println "FOUND FIXED DATE for ${monDateString}:  " + fixedDates[monDateString]}
	    highlights.add(fixedDates[monDateString])
	  } else {
	  }
                    
	  if (i.mod(3) == 0) {
	    // Index into events array:
	    def monIdx = Integer.parseInt(evt.'@mwf' ) - 1
	    // peek ahead to Wed, so we can lay out
	    // whole week as one row:
	    def nextEvt= mwf[i + 1]
	    def thirdEvt= mwf[i + 2]
	    if (!nextEvt) {
	      System.err.println "ERROR! No next event at index ${i + 1}."
                            
	    } else if (!thirdEvt) {
	      System.err.println "ERROR! No next event at index ${i + 2}."
                            
	      
	    } else {
	      def wedDateString =  "${nextEvt.'@month'} ${nextEvt.'@date'}"
	      if (fixedDates[wedDateString]) {
		if (debug) { println "FIXED DATE: " + fixedDates[wedDateString] }
		highlights.add(fixedDates[wedDateString])
	      } else {
	      }
	      def wedIdx =   Integer.parseInt(nextEvt.'@mwf' ) - 1


	      def friDateString =  "${thirdEvt.'@month'} ${thirdEvt.'@date'}"
	      if (fixedDates[friDateString]) {
		if (debug) { println "FIXED DATE: " + fixedDates[friDateString] }
		highlights.add(fixedDates[friDateString])
	      } else {

	      }
	      def friIdx =   Integer.parseInt(thirdEvt.'@mwf' ) - 1


	      tr {
		// MONDAY:
		td () {
		  span(class : "dateLabel", "${monDateString}") 
		  if (debug) {println "Mon ${monIdx}-${monDateString} "}
		  if (monIdx < courseDays.size()) {
		    def keyValArr = courseDays[monIdx].split(pairingDivider)
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

		// WEDNESDAY:
		td () {
		  span(class : "dateLabel", "${wedDateString}") 
		  if (debug) {println "Thurs ${wedIdx}-${wedDateString} "}
		  if (wedIdx < courseDays.size()) {
		    def keyValArr = courseDays[wedIdx].split(pairingDivider)
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

		// FRIDAY:
		td() {
		  span(class : "dateLabel", "${friDateString}") 
		  if (debug) {println "FRI ${friIdx}-${friDateString} "}
		  if (friIdx < courseDays.size()) {
		    def keyValArr = courseDays[friIdx].split(pairingDivider)
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
