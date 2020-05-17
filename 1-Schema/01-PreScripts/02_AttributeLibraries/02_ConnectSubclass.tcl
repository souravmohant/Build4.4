tcl;
eval {


set sInputLib [open "inputFile_ConnectStructure.txt" r]
set sInputLib [read $sInputLib]

set sInputLib [split $sInputLib "\n"]
	foreach sGeneralLibInfo $sInputLib {
		if { $sGeneralLibInfo != ""} {
			set sGeneralLibInfo [split $sGeneralLibInfo "|"]
			set sFromType [lindex $sGeneralLibInfo 0]
			set sFromName [lindex $sGeneralLibInfo 1]
			set sToType [lindex $sGeneralLibInfo 2]
			set sToName [lindex $sGeneralLibInfo 3]
			
			set mqlValidateFromLib "mql temp query bus '$sFromType' '$sFromName' '*' select id dump |"
            set mqlValidateFromLibCode [catch {eval $mqlValidateFromLib} mqlValidateFromLibResult]
			
			set mqlValidateToLib "mql temp query bus '$sToType' '$sToName' '*' select id dump |"
            set mqlValidateToLibCode [catch {eval $mqlValidateToLib} mqlValidateToLibResult]
			
			if { [llength $mqlValidateToLibResult] != 0 && [llength $mqlValidateFromLibResult] != 0} {
			
				 set mqlValidateFromLibResultSplit [split $mqlValidateFromLibResult "|"]
	             set FromId [lindex  $mqlValidateFromLibResultSplit 3]
				
				 set mqlValidateToLibResultSplit [split $mqlValidateToLibResult "|"]
	             set ToId [lindex  $mqlValidateToLibResultSplit 3]
				
				 if { $FromId != "" && $ToId != ""} {
				
				     set ConnectID [lindex [split [mql expand bus "$FromId" relation "Subclass" select rel id select bus id where "id=='$ToId'" dump |] "|"] 7]
				     if {"$ConnectID" == ""} {
					    set mqlConnectSubclass "mql connect bus $FromId rel Subclass to $ToId"
	                    set mqlConnectSubclassResultCode [catch {eval $mqlConnectSubclass} mqlConnectSubclassResult]
	
	                    if { $mqlConnectSubclassResultCode == "0"}  {
	                       puts "'$sToName' is subclassed to '$sFromName'"
	                     }
					   }
				     }
			      }
	          }
           }
}