package edu.holycross.shot.sylliutils

/*
* A class for working with the string output of traditional *NIX cal.
*/


class CalParser {

  static ArrayList daynames = [
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


  LinkedHashMap parseCal(File f) {
    return parseCal(f.getText())
  }

  LinkedHashMap parseCal(String txt) {
    def currentCal = Calendar.instance
    Integer yr = currentCal.get(Calendar.YEAR)
    return parseCal(txt, yr)
  }

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


}
  /*
Integer tth = 0
Integer mtth = 0
Integer wed = 0
Integer mw = 0
Integer mwf = 0

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

