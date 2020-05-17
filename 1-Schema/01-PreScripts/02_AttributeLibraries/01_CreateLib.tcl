tcl;
eval {

set sInputLib [open "inputFile_GenLib.txt" r]
set sInputLib [read $sInputLib]

set sGeneralLibRev "-"
set sGeneralLibVault "eService Production"
set sGeneralLibOrganization "Google"
set sGeneralLibCS "Chauffeur V5"

set sInputLib [split $sInputLib "\n"]
	foreach sGeneralLibInfo $sInputLib {
		if { $sGeneralLibInfo != ""} {
			set sGeneralLibInfo [split $sGeneralLibInfo "|"]
			set sGeneralLibName [lindex $sGeneralLibInfo 0]
			set sGeneralLibType [lindex $sGeneralLibInfo 1]
			set sGeneralLibPolicy [lindex $sGeneralLibInfo 2]
		
		set mqlCreateLib "mql add bus '$sGeneralLibType' '$sGeneralLibName' $sGeneralLibRev policy $sGeneralLibPolicy vault '$sGeneralLibVault' organization '$sGeneralLibOrganization' project '$sGeneralLibCS' "
		#puts "mqlCreateLib:::$mqlCreateLib"

		set mqlCheckLibExists "mql temp query bus '$sGeneralLibType' '$sGeneralLibName' '*' dump |"
		#puts "mqlCheckLibExists::$mqlCheckLibExists"
		set mqlCheckLibExists [catch {eval $mqlCheckLibExists} mqlCheckLibExistsResult]
			
			if { [llength $mqlCheckLibExistsResult] == 0} {
				#puts "Library '$sGeneralLibName' doesn't exist"
				set mqlCreateLibReturnCode [catch {eval $mqlCreateLib} mqlCreateLibResult]
				
				if { $mqlCreateLibReturnCode == 0} {
				puts "$mqlCreateLibResult"
				
				} else {
				puts "$mqlCreateLibResult"
				}
				}	else {
				puts "Library '$sGeneralLibName' already exists.No need to create again!!"
				}

}
}
}