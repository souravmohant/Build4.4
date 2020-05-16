###########################################################################################################
# Program Name		: Waymo_Migrate_CADModel_To_googCADModel.tcl
# Author			: XPLORIA
# Date				: 17-03-2020
###########################################################################################################
tcl;
eval {
	
     proc getError {outStr} {
		set errorMsg {}
		set errorInfo [split $outStr "\n"]
		foreach errorData $errorInfo {
			lappend errorMsg $errorData
		}
		return $errorMsg
	}

	set sCurrentPath [pwd]
	
	set sTime [clock format [clock seconds] -format {%h_%d_%Y_at_%H_%M_%S}]	
	puts "Start Time - $sTime"

	set fOutput [open "$sCurrentPath/RevertBackScript/Revert_Waymo_Migrate_CADModel_To_googCADModel.mql" "w"]
	puts $fOutput "trigger off;"
	
	set successTypeModifyLog [open "$sCurrentPath/SuccessLog/successLog_Waymo_Migrate_CADModel_To_googCADModel.xls" "w"]
	puts $successTypeModifyLog "CAD Model Type\tCAD Model Name\tCAD Model Revision\tSuccess"
	
	set errorTypeModifyLog [open "$sCurrentPath/ErrorLog/errorLog_Waymo_Migrate_CADModel_To_googCADModel.xls" "w"]
	puts $errorTypeModifyLog "CAD Model Type\tCAD Model Name\tCAD Model Revision\tError"
	
	mql trigger off;
    set CUSTOMTYPE "googCAD Model"
	set CADMODEL_DETAILS [split [mql temp query bus "CAD Model" "*" "*" !expandtype where "policy == 'CAD Model' || policy == 'Version'" select id dump |] "\n"]
	   
	       foreach CADMODEL_DETAIL $CADMODEL_DETAILS {
				   set eachCADMODELDATA [split $CADMODEL_DETAIL "|"]
				   set sType [lindex $eachCADMODELDATA 0]
				   set sName [lindex $eachCADMODELDATA 1]
				   set sRevision [lindex $eachCADMODELDATA 2]
				   set sID [lindex $eachCADMODELDATA 3]
				   if {"$sID" != ""} {
				     set sMqlResultBMod [catch {eval {mql mod bus "$sID" type "$CUSTOMTYPE"}} sModOutput]
					  if {$sMqlResultBMod==0} {
					      puts $fOutput "mod bus \"$sID\" \"type\" \"CAD Model\";"
					      puts $successTypeModifyLog "$sType\t$sName\t$sRevision\tSUCCESS"
					  } else {
					     set error [getError $sModOutput]
					     puts $errorTypeModifyLog "$sType\t$sName\t$sRevision\t$error"
					    }
				      }
			        }
	mql trigger on;
	puts $fOutput "trigger on;"
	close $successTypeModifyLog
	close $errorTypeModifyLog
	set sTime [clock format [clock seconds] -format {%h_%d_%Y_at_%H_%M_%S}]	
	puts "End Time - $sTime"
}
exit;
