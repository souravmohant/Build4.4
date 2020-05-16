###########################################################################################################
# Program Name		: googEBOMRelationship_Torque_Attribute_Updation.tcl
# Author			: XPLORIA
# Date				: 19-02-2020
###########################################################################################################
tcl;
eval {



###############################################################################
#
# Load MQL/Tcl utility procedures
#
###############################################################################

    set sProgName     "googEBOMRelationship_Torque_Attribute_Updation.tcl"

    set componentName     [string trim [mql get env 1]]
	set componentRevision     [string trim [mql get env 2]]
    set outStr      ""
    set mqlret      0
	set sErrorStr ""
    
    mql verbose off
	mql trigger off
	
	
	set EBOMIDVAL [split [mql expand bus "Part" "$componentName" "$componentRevision" relation "EBOM" from recurse to n select bus id name select rel id dump |] "\n"]
	   
	       foreach eachEBOMIDVAL $EBOMIDVAL {
				   set sEBOMActualDAT [split $eachEBOMIDVAL "|"]
				   set sEBOMRELID [lindex $sEBOMActualDAT 8]
				   set sEBOMOBJID [lindex $sEBOMActualDAT 6]
				   set sEBOMOBJNAME [lindex $sEBOMActualDAT 7]
				   
				   if {"$sEBOMOBJID" != "" && "$sEBOMRELID" != ""} {
				   
				    set finalAttrTorqueMnList {}
					set finalAttrTorqueCriticalityList {}
					set finalAttrTorqueMxList {}
					
				    set AttrTorqueList [split [mql print connection "$sEBOMRELID" select frommid\[VPLMInteg-VPLMProjection\].torel.attribute\[googTorque.googTorqueMn\].value frommid\[VPLMInteg-VPLMProjection\].torel.attribute\[googTorque.googTorqueCriticality\].value frommid\[VPLMInteg-VPLMProjection\].torel.attribute\[googTorque.googTorqueMx\].value dump "|"] "|"] 
					if {"$AttrTorqueList" != ""} {
					  
					  set length [llength $AttrTorqueList]
					  set lengthval [expr {$length/3}]
					  
					  for {set iCount 0} {$iCount < $lengthval} {incr iCount} {
		                  set sSupplierMn [lindex $AttrTorqueList $iCount]
		                  set sSupplierCrit [lindex $AttrTorqueList [expr {$iCount + $lengthval}]]
						  set sSupplierMx [lindex $AttrTorqueList [expr {$iCount + 2*$lengthval}]]
						  
						    if {"$sSupplierMn" != ""} {
						    lappend finalAttrTorqueMnList $sSupplierMn
						    }
						    if {"$sSupplierCrit" != ""} {
						    lappend finalAttrTorqueCriticalityList $sSupplierCrit
						    }
						    if {"$sSupplierMx" != ""} {
						    lappend finalAttrTorqueMxList $sSupplierMx
						    }
						  
						 
						 
					     }
					  
					
					   set CriticalityvalueList [join $finalAttrTorqueCriticalityList ","]
					   set MXvalueList [join $finalAttrTorqueMxList ","]
					   set MnvalueList [join $finalAttrTorqueMnList ","]
					
					  #Code To Modify the Torque Attribute on Relationship Starts here
					
					  set sMqlResultBMod [catch {eval {mql mod connection "$sEBOMRELID" "googSupplierTorqueMn" "$MnvalueList" "googSupplierTorqueCriticality" "$CriticalityvalueList" "googSupplierTorqueMx" "$MXvalueList"}} sModOutput]
					
				
					  if {$sMqlResultBMod==0} {

					    
					  } else {
					
					     #set error [getError $sModOutput]
		set error {}
		set errorInfo [split $sModOutput "\n"]
		foreach errorData $errorInfo {
			lappend error $errorData
		}
	
						  append sErrorStr "~~" $error
					    
					
					        }
				   }
				         }
				   
			          }
	
	mql trigger on
	return $sErrorStr
   
}

