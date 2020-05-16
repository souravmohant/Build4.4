##################################################################
# File Name		: 02-ConnectObjectGeneratorToNumberGenerator.mql
# Author		: XPLORIA
# 
###################################################################

tcl;
eval {

      set sCurrentPath [pwd]
	  set sTime [clock format [clock seconds] -format {%h_%d_%Y_at_%H_%M_%S}]	
	  puts "Start Time - $sTime"
	 
      set successLog [open "$sCurrentPath/successLog_Connect_ObjectGenerator_To_NumberGenerator.xls" "w"]
	  puts $successLog "Object Generator Name\tObject Generator Revision\tNumber Generator Name\tNumber Generator Revision\tStatus"
	 
	  set errorLog [open "$sCurrentPath/errorLog_Connect_ObjectGenerator_To_NumberGenerator.xls" "w"]
	  puts $errorLog "Object Generator Name\tObject Generator Revision\tNumber Generator Name\tNumber Generator Revision\tStatus"
	  
	  set Log [open "$sCurrentPath/Log_Connect_ObjectGenerator_To_NumberGenerator.xls" "w"]
	  puts $Log "Object Generator Name\tObject Generator Revision\tNumber Generator Name\tNumber Generator Revision\tStatus"
	 
	 
	  set CONNECTIONID [lindex [split [mql expand bus "eService Object Generator" "type_googCADModel" "A Size" relation "eService Number Generator" select rel id select bus id where "name=='type_Part'" dump |] "|"] 7]
	 
	  if {"$CONNECTIONID" == ""} {
	      set sMqlResult [catch {eval {mql connect bus "eService Object Generator" "type_googCADModel" "A Size" rel "eService Number Generator" to "eService Number Generator" "type_Part" "Temporary Part Placeholder"}} sOutStr]
	  
	      if {$sMqlResult==0} {
	           puts $successLog "type_googCADModel\tA Size\ttype_Part\tTemporary Part Placeholder\tSuccess"			 
		   } else {
		       puts $errorLog "type_googCADModel\tA Size\ttype_Part\tTemporary Part Placeholder\tError"				 
		    }
	      } else {
	        puts $Log "type_googCADModel\tA Size\ttype_Part\tTemporary Part Placeholder\tAlready Connected"
	      }
	 
	  set CONNECTIONID1 [lindex [split [mql expand bus "eService Object Generator" "type_googCADModel" "B Size" relation "eService Number Generator" select rel id select bus id where "name=='type_Part.orig'" dump |] "|"] 7]
	 
	    if {"$CONNECTIONID1" == ""} {
	        set sMqlResult1 [catch {eval {mql connect bus "eService Object Generator" "type_googCADModel" "B Size" rel "eService Number Generator" to "eService Number Generator" "type_Part.orig" "B Size"}} sOutStr1]
	   
	       if {$sMqlResult1==0} {
	           puts $successLog "type_googCADModel\tB Size\ttype_Part.orig\tB Size\tSuccess"				 
		    } else {
		       puts $errorLog "type_googCADModel\tB Size\ttype_Part.orig\tB Size\tError"		 
		    }
	   
	      } else {
		      puts $Log "type_googCADModel\tB Size\ttype_Part.orig\tB Size\tAlready Connected"
		  }
	 
	 set CONNECTIONID2 [lindex [split [mql expand bus "eService Object Generator" "type_googCADModel" "C Size" relation "eService Number Generator" select rel id select bus id where "name=='type_Part.orig'" dump |] "|"] 7]
	   if {"$CONNECTIONID2" == ""} {
	 
	      set sMqlResult2 [catch {eval {mql connect bus "eService Object Generator" "type_googCADModel" "C Size" rel "eService Number Generator" to "eService Number Generator" "type_Part.orig" "C Size"}} sOutStr2]
	  
	      if {$sMqlResult2==0} {
	          puts $successLog "type_googCADModel\tC Size\ttype_Part.orig\tC Size\tSuccess"					 
		     } else {
		      puts $errorLog "type_googCADModel\tC Size\ttype_Part.orig\tC Size\tError"			 
		    }
	      } else {
	         puts $Log "type_googCADModel\tC Size\ttype_Part.orig\tC Size\tAlready Connected"	
	      }
	 
	  set CONNECTIONID3 [lindex [split [mql expand bus "eService Object Generator" "type_googCADModel" "D Size" relation "eService Number Generator" select rel id select bus id where "name=='type_Part.orig'" dump |] "|"] 7]
	   if {"$CONNECTIONID3" == ""} {
	 
	     set sMqlResult3 [catch {eval {mql connect bus "eService Object Generator" "type_googCADModel" "D Size" rel "eService Number Generator" to "eService Number Generator" "type_Part.orig" "D Size"}} sOutStr3]
	 
	     if {$sMqlResult3==0} {
	         puts $successLog "type_googCADModel\tD Size\ttype_Part.orig\tD Size\tSuccess"				 
		  } else {
	         puts $errorLog "type_googCADModel\tD Size\ttype_Part.orig\tD Size\tError"			 
		 }

	    } else {
	       puts $Log "type_googCADModel\tD Size\ttype_Part.orig\tD Size\tAlready Connected"	
	     }
	  
	  set CONNECTIONID4 [lindex [split [mql expand bus "eService Object Generator" "type_googCADModel" "xPrd Series" relation "eService Number Generator" select rel id select bus id where "name=='type_googCADModel'" dump |] "|"] 7]

	    if {"$CONNECTIONID4" == ""} {
	       set sMqlResult4 [catch {eval {mql connect bus "eService Object Generator" "type_googCADModel" "xPrd Series" rel "eService Number Generator" to "eService Number Generator" "type_googCADModel" "xPrd Series"}} sOutStr4]
	      if {$sMqlResult4==0} {
	         puts $successLog "type_googCADModel\txPrd Series\ttype_googCADModel\txPrd Series\tSuccess"				 
		    } else {
	         puts $errorLog "type_googCADModel\txPrd Series\ttype_googCADModel\txPrd Series\tError"		 
		   }
	  
	     } else {
	       puts $Log "type_googCADModel\txPrd Series\ttype_googCADModel\txPrd Series\tAlready Connected"	
	    }

close $successLog
close $errorLog
set sTime [clock format [clock seconds] -format {%h_%d_%Y_at_%H_%M_%S}]	
puts "End Time - $sTime"
}
exit;	