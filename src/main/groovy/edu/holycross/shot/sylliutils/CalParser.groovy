package edu.holycross.shot.sylliutils

/*
* A class for working with the string output of traditional *NIX cal.
*/

import groovy.xml.MarkupBuilder

class CalParser {

  Integer debug = 0
  Integer WARN = 1
  Integer BUG = 2
  Integer SHOUT = 3



  /** Ordered list of Strings as spelled
   * by cal.
   */
  static ArrayList monthNames = [
    "January", "February", "March",
    "April", "May", "June",
    "July", "August", "September",
    "October", "November", "December"
  ]

  /** Ordered list of Strings used as attribute values
   * in XML serialization of course syllabus.
   */
  static ArrayList dayNameValues = [
    "sun",
    "mon",
    "tue",
    "wed",
    "thu",
    "fri",
    "sat"
  ]

  /** Empty constructor.*/
  CalParser() {
  }


  /** Parses one line of dates from cal output,
   * and returns the contents a possibly empty ArrayList 
   * of up to 7 elements.  
   * @param line The string of text to parse.
   * @returns An ArrayList of strings.
   */
  ArrayList parseWeekString(String line) {
    ArrayList week = []

    Integer days =  ((line.size() + 1) / 3) as Integer
    Integer startIdx = 0
    Integer count = 0
    while (count < days) {
      String dayStr = line.substring(startIdx, (startIdx + 2)).replaceAll(/[ ]+/,'')
      week[count] = dayStr
      startIdx +=  3
      count++
	}
    return week
  }



  /** Parses a file of data as output by
   * cal, and creates a hash of month names to an ordered
   * list of week data.  The parser only recognizes data
   * from the current year.
   * @param txt Source text, in format output by cal.
   * @returns A map keyed by month name.  Each value in the map
   * is a week structure, as returned by parseWeekString.
   */
  LinkedHashMap parseCal(File f) {
    return parseCal(f.getText())
  }

  LinkedHashMap parseCal(File f, Integer yr) {
    return parseCal(f.getText(), yr)
  }

  

  /** Parses multiple lines of String as output from
   * cal, and creates a hash of month names to an ordered
   * list of week data.  The parser only recognizes data
   * from the current year.
   * @param txt Source text, in format output by cal.
   * @returns A map keyed by month name.  Each value in the map
   * is a week structure, as returned by parseWeekString.
   */
  LinkedHashMap parseCal(String txt) {
    def currentCal = Calendar.instance
    Integer yr = currentCal.get(Calendar.YEAR)
    return parseCal(txt, yr)
  }


  /** Parses multiple lines of String as output from
   * cal, and creates a hash of month names to an ordered
   * list of week data.  The parser only recognizes data
   * from a specified year.
   * @param txt Source text, in format output by cal.
   * @param yr Year value.
   * @returns A map keyed by month name.  Each value in the map
   * is a week structure, as returned by parseWeekString.
   */
  LinkedHashMap parseCal(String txt, Integer yr) {
    LinkedHashMap monthly = [:]

    if (debug >= WARN) { System.err.println "CalParser:parseCal: parsing text with yr = " + yr }

    String currentMonth = ""
    def  currentWeekList = []
    txt.eachLine { ln ->
      if (debug > WARN) { System.err.println "\tmonthly " + monthly}
      // Look for month label: pattern is MONTH ${yr}
      if (ln ==~ /.+${yr}.*/) {
	if (debug >= WARN) {
	  System.err.println "Parsing month " + ln
	}
	if ((currentMonth != "") && (currentWeekList.size() > 0)) {

	  def lsCopy = []
	  currentWeekList.each {
	    lsCopy.add(it)
	  }
	  monthly[currentMonth] = lsCopy
	  if (debug > WARN) {
	    System.err.println "STORE CAL FOR MONTH : " + currentMonth
	    System.err.println "its cal is " + monthly[currentMonth] 
	    System.err.println " and montly is now " + monthly + "\n"
	    
	  }
	}
	def cols =  ln.split(/[ ]+/)
	String prevMonth = currentMonth
	currentMonth = cols[1]
	currentWeekList.clear()
	if (debug > WARN) {
	  System.err.println "\nRESET month to new month" + currentMonth
	  System.err.println "monthly for prev ${prevMonth} is " + monthly
	}
	
      } else if (ln ==~ /^Su.+/) {
	// skip day heading
	
      } else {
	ArrayList wk = parseWeekString(ln)
	currentWeekList.add(wk)
	if (debug > BUG) {
	  System.err.println "parse week " + wk
	  System.err.println "currentWeekList now " + currentWeekList + "\n"
	}
      }
    }
    if (debug > WARN) { System.err.println "AT END OF FIEL LOOP: montly " + monthly}



    // Get last one!
    if (debug > WARN) {
      System.err.println "at end: currentWeekList: " + currentWeekList
      System.err.println "Before final assignment to monthly: " + monthly
    }
    monthly[currentMonth] = currentWeekList
    if (debug > WARN) {

      System.err.println "STORE CAL FOR FINAL MONTH : " + currentMonth      
      System.err.println monthly[currentMonth]
      System.err.println "\nReturning entire monthly struct " + monthly
    }

    
    
    return monthly
  }



  void toXml(File calFile, File outputFile) { 
    toXml(calFile.getText(), outputFile)
  }

  /** Writes to outputFile the serialization
   * as XML of a multiline source string
   * as formatted by cal.
   * @param calSource String as output by cal.
   * @param outputFile File where output should be
   * written.
   */
  void toXml(String calSource, File outputFile) { 
    outputFile.setText(toXml(calSource))
  }


  /** Serializes to XML data as
   * formatted by cal, read from a file.
   * @param f File with soruce data.
   * @returns A well-formed XML string using the
   * idiosyncratic markup of this project.
   */
  String toXml(File f) {
    return toXml(f.getText())
  }


  /** Serializes to XML a multiline source string
   * as formatted by cal.
   * @param calSource String as output by cal.
   * @returns A well-formed XML string using the
   * idiosyncratic markup of this project.
   */
  String toXml(String calSource) {
    def currentCal = Calendar.instance
    Integer yr = currentCal.get(Calendar.YEAR)
    return toXml(calSource, yr)
  }

  String toXml(String calSource, Integer yr) {
    
    LinkedHashMap monthMap = parseCal(calSource, yr)


    Integer stth = 0
    Integer tth = 0
    Integer mtth = 0
    Integer wed = 0
    Integer mw = 0
    Integer mwf = 0

    StringWriter writer = new StringWriter()
    MarkupBuilder xml = new MarkupBuilder(writer)

    xml.calendar() {
      monthNames.each { m ->
	if (monthMap.keySet().contains(m)) {
	  month(name: m) {
	    monthMap[m].each { wk ->
	      week {
		wk.eachWithIndex { d, idx ->

		  if (d.size() > 0) {


		    String weekDay = dayNameValues[idx]
		    switch (weekDay) {
		    case "sun":
		    stth++;
		    day(date : d, dayname: "${weekDay}", month: m, year: yr, stth: stth, "${d}" )
		    break
		    
		    case "tue":
		    case "thu":
		    stth++;
		    tth++;
		    mtth++;
		    day(date : d, dayname: "${weekDay}", mtth: mtth,tth: tth, month: m, year: yr, stth: stth, "${d}" )
		    break


		    case "mon":
		    mtth++;
		    mw++;
		    mwf++;
		    day(date : d, dayname: "${weekDay}", mwf: mwf, mw: mw, mtth: mtth, month: m, year: yr, "${d}" )
		    break

		    case "wed":
		    wed++;
		    mwf++;
		    day(date : d, dayname: "${weekDay}", mwf: mwf, wed: wed, month: m, year: yr, "${d}" )
		    break

		    case "fri":
		    mwf++;
		    day(date : d, dayname: "${weekDay}", mwf: mwf, month: m, year: yr, "${d}" )
		    break
		      
		    case "sat":
		    case "sun":
		    day(date : d, dayname: "${weekDay}", month: m, year: yr, "${d}" )
		    break
		    }
		  }
		}
	      }
	    }
	  }
	}
      }
    }
    return writer.toString()
  }



}


