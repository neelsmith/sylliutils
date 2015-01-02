package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class STThCalendar {
    boolean debug = false

    File courseData
    File calData
    File outFile

    def courseXml

    // map of dates -> events
    def fixedDates = [:]

    // ordered list of pairings, key + label
    def courseDays = []
    def pairingDivider = '#'


    STThCalendar(String courseFileName, String calFileName, File outputFile) 
    throws Exception {
        courseData = new File(courseFileName)
        courseXml = new XmlParser().parse(this.courseData)        
        calData = new File(calFileName)
        this.outFile = outputFile
    }

    public static void main(String[] args) {
        MonWedFriCalendar tt = new MonWedFriCalendar(args[0], args[1], args[2])
        tt.printCal()
    }

    LinkedHashMap extractFixedDates() {
        def fixedDateMap = [:]
        this.courseXml.fixeddates[0].day.each { d ->
            fixedDateMap.putAt(d.'@date', d.text())
        }
        return fixedDateMap
    }

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
    def stt = eventDates.month.week.day.findAll { it.'@stth' != null}
    System.err.println  "number of STTh dates available = " + stt.size()

    // special events to highlight at end of week
    def highlights = []


    def builder = new StreamingMarkupBuilder()
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
	  if (debug) { System.err.println "Proess EVT " + evt }
	  def sunDateString = "${evt.'@month'} ${evt.'@date'}"
	  if (debug) {System.err.println "Check on SUN date ${sunDateString}" }
	  if (fixedDates[sunDateString]) {
                
	    if (debug) { System.err.println "FOUND FIXED DATE for ${sunDateString}:  " + fixedDates[sunDateString]}
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
		if (debug) { println "FIXED DATE: " + fixedDates[tuesDateString] }
		highlights.add(fixedDates[tuesDateString])
	      } else {
		//System.err.println "NO FIXED DATE FOR WED ${wedDateString}"
		// no match
	      }
	      def tuesIdx =   Integer.parseInt(nextEvt.'@stth' ) - 1


	      def thursDateString =  "${thirdEvt.'@month'} ${thirdEvt.'@date'}"
	      if (fixedDates[thursDateString]) {
		if (debug) { println "FIXED DATE: " + fixedDates[thursDateString] }
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
		  if (debug) {println "Sun ${sunIdx}-${sunDateString} "}
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
		  if (debug) {println "Thurs ${tuesIdx}-${tuesDateString} "}
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
		  if (debug) {println "THURS ${thursIdx}-${thursDateString} "}
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
	    link (rel : "stylesheet", type : "text/css", href : "css/greek.css")
	    link (rel : "stylesheet", type : "text/css", href : "css/calendar.css")
	  }
	  body {
	    header {
                    }
                    nav {
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

                    article {
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
