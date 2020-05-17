tcl;
proc logMessage {sMessage} {
		global fOutput
		puts $fOutput "$sMessage"
	}
eval {

set sInputTxt [open "DeleteList.txt" r]
set sInputFile [read $sInputTxt]

set outputLogFile "OutputLog.txt"
	set errorFlag [catch {set fOutput [open $outputLogFile w] }];

set sInputFile [split $sInputFile "\n"]
	foreach sGeneralFileInfo $sInputFile {
		if { $sGeneralFileInfo != ""} {
			set sGeneralDeleteInfo [split $sGeneralFileInfo "="]
			set sGeneralSchemaType [lindex $sGeneralDeleteInfo 0]
			set sGeneralSchemaName [lindex $sGeneralDeleteInfo 1]
			foreach schemaName [ split $sGeneralSchemaName "," ] {
		if {$sGeneralSchemaType == "table"} {
			set E [catch {mql list $sGeneralSchemaType system $schemaName} aResult]
		} else { 
			set E [catch {mql list $sGeneralSchemaType $schemaName} aResult]
		}
		if {$aResult != ""} {
			if {$sGeneralSchemaType == "table"} {
				set E [catch {mql delete $sGeneralSchemaType $schemaName system} addResult]
			} else {
				set E [catch {mql delete $sGeneralSchemaType $schemaName} addResult]
			}
			if {$E == 1} {
			logMessage "\nDelete $sGeneralSchemaType $schemaName failed because of $addResult";
			} else {
			logMessage "\nDelete $sGeneralSchemaType $schemaName successful";
			}
		} else {
			logMessage "\nDelete $sGeneralSchemaType $schemaName successful";
		}

}
}
}
}