package edu.holycross.shot.sylliutils

import groovy.xml.StreamingMarkupBuilder

class MonWedFriCalendar {
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


    MonWedFriCalendar(String courseFileName, String calFileName, File outputFile) 
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
        def mwf = eventDates.month.week.day.findAll { it.'@mwf' != null}
        System.err.println  "number of MWF dates available = " + mwf.size()

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
                        //System.err.println "NO MATCH IN FIXED DATES FOR ${monDateString}"
                        // no match
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
                                //System.err.println "NO FIXED DATE FOR WED ${wedDateString}"
                                // no match
                            }
                            def wedIdx =   Integer.parseInt(nextEvt.'@mwf' ) - 1


                            def friDateString =  "${thirdEvt.'@month'} ${thirdEvt.'@date'}"
                            if (fixedDates[friDateString]) {
                                if (debug) { println "FIXED DATE: " + fixedDates[friDateString] }
                                highlights.add(fixedDates[friDateString])
                            } else {
                                //System.err.println "NO FIXED DATE FOR FRI ${friDateString}"
                                // no match
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
