tcl;
eval {
set systemTime1 [clock seconds]
set pwdir [pwd]
set RootDirs [glob -nocomplain *]
puts -nonewline "Enter Target Environment name LOCAL/DEV/UAT/PROD: "
#flush stdout
#set envname [gets stdin]
#set envname [string toupper $envname]
set envname "LOCAL"
if {$envname == "DEV" || $envname == "UAT" || $envname == "PROD" || $envname == "LOCAL"} {
puts "Deploying code as $envname environment"
foreach RFDirs $RootDirs {
puts "Evaluating $RFDirs Directory..."
		set RFType [file type $RFDirs]
	if {$RFType == "directory"} {
		cd $RFDirs
		if {$RFDirs == "02-Spinner"} {
		# Delete Log file start
		file delete -force logs
		set logFolderPath [glob -nocomplain *]
		puts "Log file deleted $logFolderPath"
		# Deleted Log files
			puts "Started Executing Spinner..."
			mql exec prog emxSpinnerAgent.tcl;
			puts "Executing Spinner Completed..."
			if {$envname != "LOCAL"} {
				set sourcefiledir "$pwdir/$RFDirs/Business/SourceFiles"
				puts "Checking if any JPOs modified for compilation..."
				set sourcefile_list [glob -nocomplain -dir $sourcefiledir *.java]
				if {[llength $sourcefile_list] != 0} {
					puts "Started force compiling all the programs..."
					mql compile program * force update;
					puts "Completed force compiling all the programs..."
				}
			}
		} elseif {$RFDirs == "06-EUIP"} {
		# Delete Log file start
		file delete -force logs
		set logFolderPath [glob -nocomplain *]
		puts "Log file deleted $logFolderPath"
		# Deleted Log files
			puts "Started Executing Spinner..."
			mql exec prog emxSpinnerAgent.tcl;
			puts "Executing Spinner Completed..."
		} elseif {$RFDirs == "05-Config"} {
			if {$envname != "LOCAL"} {
				cd $envname
				mql set system searchindex file config.xml;
				cd ..
				}
			} else {
			set filesToRun [glob -nocomplain *]
			foreach f $filesToRun {
			#puts "executing $f "
			set ftype [file type $f]
				if {$ftype == "directory"} {
				cd $f
				set subDirFiles [glob -nocomplain *]
					foreach sDF $subDirFiles {
					set SDfileExtension [file extension $sDF]
						if {$SDfileExtension == ".tcl" || $SDfileExtension == ".mql"} {
						puts "Executing File: $sDF";
						mql run $sDF;
						#puts "Completed Execution File: $sDF";
						}
					}
				cd ..
				}
			set fileExtension [file extension $f]
				if {$fileExtension == ".tcl" || $fileExtension == ".mql"} {
				puts "Executing File1: $f";
				mql run $f;
				#puts "Completed Executing File1: $f";
				}
			}	
		}
	}
	cd "$pwdir"
	# Returned to Root Folder
	}
puts "Code deployment Completed. Pl check log files from each folder"
puts "\n\n\n"
puts "Please complete manually 04-MiscConfigurations Folder contents"
 } else {
 puts "Can not execute due to invalid Environment $envname"
 }
 # end RootDirs
 
 
	set systemTime2 [clock seconds]
	puts "Execution Ended at::: [clock format $systemTime2 -format %Y-%m-%dT%H:%M:%S]"
	puts "~~~~~~~~~~Script execution completed~~~~~~~~~~~"

	set diff_in_seconds [expr {$systemTime2 - $systemTime1}] 
	set cpu_ms   [expr { int(fmod($diff_in_seconds, 1.0) * 1000) }]
	set cpu_secs [expr { int(floor($diff_in_seconds)) % 60 }]
	set cpu_hrs  [expr { int(floor($diff_in_seconds / 3600)) }]
	set cpu_mins [expr { int(floor($diff_in_seconds / 60)) % 60 }]
	puts "Time taken(hh:mm:ss): [format "%d:%02d:%02d" $cpu_hrs $cpu_mins $cpu_secs]"
}

