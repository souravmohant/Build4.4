tcl;
#Input File Format is
#Parentname|ChildName  Or Parentname|ChildName|ADD OR  Parentname|ChildName|Remove Child OR Parentname|ChildName|Remove Child
eval {

set sInputClass [open "inputFile_Structure.txt" r]
set sInputClass [read $sInputClass]
set sGeneralClassType "General Class"
set sGeneralClassRev "-"
set sGeneralClassVault "eService Production"
set sGeneralClassPolicy "Classification"
set sGeneralClassOrganization "Google"
set sGeneralClassCS "Chauffeur V5"

global debugFileName
global errorFileName

set debugFileName "Logs_Success_Buildstructure"
append debugFileName "." "xls"

set errorFileName "Logs_Error_Buildstructure"
append errorFileName "." "xls"
set errorFile [open $errorFileName "a"]

set logFile [open $debugFileName "a"]
set ImportData "*** End of the program ***"

set sClassCount "0"
set sTitle "Libraries and Classification"
set sOriginator "creator"

set sInputClass [split $sInputClass "\n"]
	foreach sInputClassind $sInputClass {
		if { $sInputClassind != ""} {
			
			set sInputClassind [split $sInputClassind "|"]
			set sClassParent [lindex $sInputClassind 0]
			set sClassChild [lindex $sInputClassind 1]
			set sClassAction [string tolower [lindex $sInputClassind 2]]
			#puts "Item name Parent $sClassParent  Child Name $sClassChild  Action $sClassAction";
			
			if { $sClassParent == "" } {
				set sClassParent "Dummy";
			}
			#Including wild card characters to convert the name to proper format
			set sWildCardParentCls [regsub "," '$sClassParent' "*"]
			set sWildCardChildCls [regsub "," '$sClassChild' "*"]
			
			# check if parent exists, if not create then
			#set sClassParent 
			set scheckParentExists " mql temp query bus '$sGeneralClassType' $sWildCardParentCls * select id  dump "
			set mqlscheckParentExists [catch {eval $scheckParentExists} mqlscheckParentExistsExec]

			# check if child exists, if not create then
			set scheckChildExists " mql temp query bus '$sGeneralClassType' $sWildCardChildCls * select id   dump "
			set mqlscheckChildExists [catch {eval $scheckChildExists} mqlscheckChildExistsExec]
		
		if { $sClassAction == "remove child" ||  $sClassAction == "remove"} {
			if { [llength $mqlscheckChildExistsExec] != 0} {
				set scheckChildID "mql temp query bus '$sGeneralClassType' $sWildCardChildCls * select id name current dump |"
				set mqlcheckChildeval [catch {eval $scheckChildID} mqlcheckChildID]	
				set sChildIDSplit [split $mqlcheckChildID "|"]
				set sChildID [lindex  $sChildIDSplit 3]
				set sChildName [lindex  $sChildIDSplit 4]
				set sChildCurrent [lindex  $sChildIDSplit 5]
				#Perform delete action for the child object alone
				set sDeleteClass "mql delete bus '$sChildID' "
				set mqlDisconnectLib [catch {eval $sDeleteClass} mqlDeleteExec]				
				if {$mqlDisconnectLib == 0 } {
					puts $logFile " Child $sChildName DELETED FROM DB(Remove Child) \t Success \n"
				} else {
					puts $errorFile "Child $sChildName DELETE(Remove Child)  ERROR \t Failed \t $mqlDeleteExec \n"
				}		
				}				
			} elseif { $sClassAction == "new"} {
				#Creating Parent Class if no results from above
				if { [llength $mqlscheckParentExistsExec] == 0} {
					set sAddClass " mql add bus '$sGeneralClassType' '$sClassParent' - policy '$sGeneralClassPolicy' vault '$sGeneralClassVault' Organization '$sGeneralClassOrganization' Project '$sGeneralClassCS' Count '$sClassCount' Title '$sTitle' "
					set mqlAddParent [catch {eval $sAddClass} mqlAddLibExec]
					if { $mqlAddParent == 0 } {
						puts $logFile "Parent $sClassParent creation \t Success  \n"
					} else {
						puts $errorFile "Parent $sClassParent creation \t Failed  \n"
					}
				}
				
				#Creating child Class if no results from above
			if { [llength $mqlscheckChildExistsExec] == 0} {
			
			#Creating Child Class
			set sModClass "mql add bus '$sGeneralClassType' '$sClassChild' - policy '$sGeneralClassPolicy' vault '$sGeneralClassVault' Organization '$sGeneralClassOrganization' Project '$sGeneralClassCS' Count '$sClassCount' Title '$sTitle'"
			set mqlAddChildLib [catch {eval $sModClass} mqlAddChildExec]
			if {$mqlAddChildLib == 0} {
				puts $logFile "child class $sClassChild creation \t Success \n"
			} else {
				puts $errorFile "child class $sClassChild creation \t Failed \n"
			}
			}
			
			#now the parent has been created, and wil have id 
			
			set scheckParentID "mql temp query bus '$sGeneralClassType' $sWildCardParentCls * select id name current dump |"
			set mqlcheckParenteval [catch {eval $scheckParentID} mqlcheckParentID]

			set sParentIDSplit [split $mqlcheckParentID "|"]
			set sParentID [lindex  $sParentIDSplit 3]
			set sParentName [lindex  $sParentIDSplit 4]
			set sParentCurrent [lindex  $sParentIDSplit 5]
			
			#now the Child has been created, and wil have id 

			set scheckChildID "mql temp query bus '$sGeneralClassType' $sWildCardChildCls * select id name current dump |"
			set mqlcheckChildeval [catch {eval $scheckChildID} mqlcheckChildID]	
			set sChildIDSplit [split $mqlcheckChildID "|"]
			set sChildID [lindex  $sChildIDSplit 3]
			set sChildName [lindex  $sChildIDSplit 4]
			set sChildCurrent [lindex  $sChildIDSplit 5]
			
				# promoting the Parent and child classes
				
				# only promote the object if it is Inactive state
				
				#Check whether the Parent and child already connected or not
				set sConnectionExist "mql expand bus '$sParentID' relationship Subclass select bus where 'name==\"$sChildName\"' ";
				set mqlConnectExists [catch {eval $sConnectionExist} mqlConnectExec]			
								
				if {$mqlConnectExec != "" } {
					#Connnection already exists and hence do not connect
						puts $logFile "Parent $sParentName Child $sChildName connection ALREADY EXISTS (ADD) \t Success \n"
				} else {
					# Connecting the Parent and child classes					
					set sModClass "mql connect bus '$sParentID' relationship Subclass to '$sChildID'"
					set mqlAddLib [catch {eval $sModClass} mqlConectExec]
						
					if {$mqlAddLib == 0 } {
						puts $logFile "Parent $sParentName Child $sChildName connection (ADD) \t Success \n"
					} else {
						puts $errorFile "Parent $sParentName Child $sChildName connected (ADD) \t Failed \t $mqlConectExec \n"
					}
				}
				
				
			} else {
			
				puts $errorFile "Parent $sParentName Child $sChildName ERROR ACTION NOT DEFINED. VALIDATE INPUT. \t Failed  \n"
					
			}
		}
	}
	flush $logFile
	close $logFile
	
	flush $errorFile
	close $errorFile
	
puts "********End of BuildStructure.tcl program execution ***********"
}