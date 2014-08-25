package edu.holycross.shot.sylliutils

/*
* A class for working with the string output of traditional *NIX cal.
*/

import groovy.xml.MarkupBuilder

class CalParser {

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
    System.err.println "YEAR: " + yr


    String currentMonth = ""
    def  currentWeekList = []
    txt.eachLine { ln ->
      // Look for month label: pattern is MONTH ${yr}
      if (ln ==~ /.+${yr}.*/) {
	if ((currentMonth != "") && (currentWeekList.size() > 0)) {
	  monthly[currentMonth] = currentWeekList
	}
	def cols =  ln.split(/[ ]+/)
	currentMonth = cols[1]
	currentWeekList.clear()
	
	
      } else if (ln ==~ /^Su.+/) {
	// skip day heading
	
      } else {
	ArrayList wk = parseWeekString(ln)
	currentWeekList.add(wk)
      }
    }
    // Get last one!
    monthly[currentMonth] = currentWeekList

    return monthly
  }






  String toXml(File f) {
    return toXml(f.getText())
  }

  String toXml(String calSource) {
    def currentCal = Calendar.instance
    Integer yr = currentCal.get(Calendar.YEAR)

    LinkedHashMap monthMap = parseCal(calSource, yr)


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


		    case "tue":
		    case "thu":
		    tth++;
		    mtth++;
		    day(date : d, dayname: "${weekDay}", mtth: mtth,tth: tth, month: m, year: yr, "${d}" )
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
  /*


  */



/*


*/  
/*


sub printweek {
    $daycnt = 0;
    print "\n<week>\n";
    foreach $day (@_) {
	$day =~ s/ //g;

	if ($day ne "") {
	    print "<day dayname=\"$daynames[$daycnt]\" date=\"$day\" month=\"$month\" " ;

	    if ($daynames[$daycnt] eq "tue") {
		$tth++;
		print " tth=\"$tth\"";
		$mtth++;
		print " mtth=\"$mtth\"";
	    } 

	    if  ($daynames[$daycnt] eq "thu") { 
		$tth++;
		print " tth=\"$tth\" ";
		$mtth++;
		print " mtth=\"$mtth\" ";
	    }


	    if  ($daynames[$daycnt] eq "mon") { 
		$mtth++;
		print "mtth=\"$mtth\"";
		$mw++;
		print " mw=\"$mw\"";
		$mwf++;
		print " mwf=\"$mwf\"";
	    }


	    if (($daynames[$daycnt] eq "wed")) {
		$wed++;
		print " wed=\"$wed\"";
		$mw++;
		print " mw=\"$mw\"";
		$mwf++;
		print " mwf=\"$mwf\"";
	    } 

	    if (($daynames[$daycnt] eq "fri")) {
		$mwf++;
		print " mwf=\"$mwf\"";
	    } 


            print " year=\"$yr\">$day</day>\n";
	    }
	$daycnt++;
    }
    print "\n</week>";
}
********/

