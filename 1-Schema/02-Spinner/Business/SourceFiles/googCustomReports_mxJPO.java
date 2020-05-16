import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
//[Google Custom]:  Enovia Check Report issue Added by : Subbu on 24/08/2018 - Start
import matrix.util.MatrixException;
//[Google Custom]:  Enovia Check Report issue Added by : Subbu on 24/08/2018 - End
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.jsystem.util.StringUtils;
import java.util.Vector;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//Added by XPLORIA to FIX Deviation Report Issue STARTS Here
import com.matrixone.apps.domain.util.PropertyUtil;
//Added by XPLORIA to FIX Deviation Report Issue STARTS Here

public class googCustomReports_mxJPO extends googConstants_mxJPO {

	public static final String COMMA_DELIMITER = ",";
	public static final String NEW_LINE_SEPARATOR = "\n";
    public static final String TARGET_FILE_FOLDER = "C:/temp/";
    public static final String ENDITEM_TARGET_FILE_FOLDER = "C:/temp/ENDITEMREPORT";
    public static String strFormat = "MMddyyyy_HHmmss";
	public static String strTimeStamp = new SimpleDateFormat(strFormat).format(Calendar.getInstance().getTime());
	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
    static BufferedWriter bwSucessLog = null;
    static BufferedWriter bwFailureLog = null;
	//Added by XPLORIA to FIX Deviation Report Issue STARTS Here
	String ATTRIBUTE_APPROVED_TO = PropertyUtil.getSchemaProperty("attribute_EstimatedEndDate");
	String SELECT_ATTRIBUTE_APPROVED_TO="attribute["+ATTRIBUTE_APPROVED_TO+"]";
	//Added by XPLORIA to FIX Deviation Report Issue STARTS Here
	@SuppressWarnings("rawtypes")
	public googCustomReports_mxJPO() {

	}

	/**
	 * This method used generate Part(csv) report.
	 * This will report only the Part which is having "isVPMVisible" value true and don't have any VPMReference Documents attached
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	public void createVPMPartReport(Context context, String[] args) throws Exception {
		// [Google Custom]: #33753674:Added below code to fetch the Part info and write data to a CSV file or update the attribute based on user input -- Modified by Subbu -Start		
		String fileLoc = "C:\\temp\\";
		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		StringBuilder fileName = new StringBuilder(fileLoc);	
		fileName.append("\\").append("DesignCollaborationReport").append("_").append(fileCreateTimeStamp).append(".csv");
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		String strPartOID = null;
		String strpartName = null;
		String strpartRev = null;
		String strUserChoice = null;
		String strpartType = null;
		
		try {
			if(args.length>0)
			{			
			strUserChoice = args[0];
			if ("Analyse".equalsIgnoreCase(strUserChoice) || "Commit".equalsIgnoreCase(strUserChoice)) {
				
				MapList mlEBOMList = new MapList();
				String objectWhere = "policy== '"+POLICY_EC_PART+"' && "+SELECT_ATTRIBUTE_ISVPMVISIBLE+"==True && from["+RELATIONSHIP_PART_SPECIFICATION+"|to.type=="+TYPE_VPMREFERENCE+"]!=True";
				MapList partList = DomainObject.findObjects(context, DomainObject.TYPE_PART, // Type
						DomainObject.QUERY_WILDCARD, // name
						DomainObject.QUERY_WILDCARD, // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						objectWhere, // Where
						false, // expand sub type
						objectSelects);
				
				if (partList.size() > 0) {
					Iterator<Map> it = partList.iterator();
					while (it.hasNext()) {
						Map mpPartInfo = (Map) it.next();
						strPartOID = (String) mpPartInfo.get(DomainObject.SELECT_ID);
						strpartName = (String) mpPartInfo.get(SELECT_NAME);
						strpartRev = (String) mpPartInfo.get(SELECT_REVISION);
						strpartType = (String) mpPartInfo.get(SELECT_TYPE);
						MapList mlpartList = getEBOMRel(context, strPartOID, strpartName, strpartRev, strpartType);
						if (mlpartList.size() > 0) {
							Iterator<Map> partitr = mlpartList.iterator();
							while (partitr.hasNext()) {
								Map mpPart = (Map) partitr.next();
								mlEBOMList.add(mpPart);
							}
							// If Input Argument is 'Analyse' --> Generate Report
							if ("Analyse".equalsIgnoreCase(strUserChoice)) {
								createCSVFile(fileName.toString(), mlEBOMList, "DesignCollaborationReport");
							}
							// If Input Argument is 'Commit' --> Update the 'Design Collaboration' attribute value as FALSE on EBOM Relationship
							else if ("Commit".equalsIgnoreCase(strUserChoice)) {
								updateEBOMRel(context, mlEBOMList);
							}
						}
					}
				}
				if ("Commit".equalsIgnoreCase(strUserChoice)) {
					//System.out.println("Since you have choosen 'Commit', OutPut Logs will be generated: SucessfullyModified_TimeStamp.txt and Failed_TimeStamp.txt at path C:\\temp\\");
				} else if ("Analyse".equalsIgnoreCase(strUserChoice)) {
					//System.out.println("Since you have choosen 'Analyse', a Report will be ganerated: DesignCollaborationReport_TimeStamp.csv at path C:\\temp\\");
				}
				} else {
					//System.out.println(" Process Skipped :: Invalid Argument. Accepted values are:   Analyse      Commit ");
				}
			} else {
				//System.out.println(" Process Skipped :: This method expects an argumnet. Please enter one value from accepted values :   Analyse      Commit ");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		// [Google Custom]: #33753674:Added below code to fetch the Part info and write data to a CSV file or update the attribute based on user input -- Modified by Subbu -End
	}
	
	/**
	 * This method used generate Part(csv) report.
	 * This will report only the Part which is having "Unit of Measure" value as "g".
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	public void createPartUOMReport(Context context, String[] args)
			throws Exception {
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		objectSelects.add(SELECT_ATTRIBUTE_UNITOFMEASURE);
		String objectWhere = "attribute[Unit of Measure]==g";
		MapList partList = DomainObject
				.findObjects(context,
						DomainObject.TYPE_PART, // Type
						DomainObject.QUERY_WILDCARD, // name
						DomainObject.QUERY_WILDCARD, // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						objectWhere,// Where
						true, // expand sub type
						objectSelects);
		String filePath = "c:\\Temp\\EBOM_UOM_CSV_Report.csv";
		String scriptPath = "c:\\Temp\\UOM_Migration_Script.mql";
		createCSVFile(filePath,partList,"uomReport");
		createMigrationScript(scriptPath,partList);
	}
	
	/**
	 * This method used to create a migration script to update UOM value to kg.
	 * @param scriptPath
	 * @param partList
	 * @throws IOException
	 * @author shajil
	 */
	public void createMigrationScript(String scriptPath, MapList partList) throws IOException {
		
		File file = new File(scriptPath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			Iterator itr = partList.iterator();
			while (itr.hasNext()) {
				StringBuilder query = new StringBuilder();
				Map ebomDetails = (Map) itr.next();
				String id = (String) ebomDetails.get(DomainObject.SELECT_ID);
				query.append("mod bus ").append(id).append(" '").append(ATTRIBUTE_UNIT_OF_MEASURE).append("' ").append("kg;").append("\n");
				bw.write(query.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
	}

	/**
	 * This method used to create CSV file.
	 * @param filePath
	 * @param ebomList
	 * @return File
	 * @throws IOException
	 * @author shajil
	 */
	public File createCSVFile(String filePath, MapList ebomList,String reportName)
			throws IOException {
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Subbu on 24/08/2018 -- Start 
		googCustomFunctions_mxJPO googCustomFunctions = new googCustomFunctions_mxJPO(); 
		ebomList= googCustomFunctions.formatMaplistStringValues(ebomList);
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Subbu on 24/08/2018 -- End 
		String FILE_HEADER = "Type,Name,Revision,id";
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("uomReport")){
			FILE_HEADER = "Type,Name,Revision,id,UOM";
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("MEPReport")){
			FILE_HEADER = "Type,Name,Revision,id,MEP Names,MEP Revisions,MEP IDs";
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("CollabSpace")){
			FILE_HEADER = "ProductType,ProductName,ProductRevision,ProductId,Product Ownership,Type,Name,Revision,Part Id,Part Ownersip";
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("VPMReferenceReport")){
			FILE_HEADER = "Part Type, Part Name, Part Revision, Part Id,ProductTypes,ProductNames,ProductRevisions,ProductIds";
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("PartWhereUsedReport")){
			FILE_HEADER = "Type,Name,Revision,ObjectId,Description,Level,State,FindNumber,Release Phase,Change Controlled";
		}
	//[Google Custom]: #33753674: Added method to modify the Rel Attr 'Design Collaboration' to FALSE for all those parts not connected to Phyical Product -- Modified by Subbu Start
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("DesignCollaborationReport")){
			FILE_HEADER = "PartType,PartName,PartRevision,PartOID,EBOMRelationshipId,EBOMRelAttribute_IsVPMVisible,ParentPartName,ParentPartRevision,ParentPartOID";
		}
		//[Google Custom]: #33753674: Added method to modify the Rel Attr 'Design Collaboration' to FALSE for all those parts not connected to Phyical Product -- Modified by Subbu -End
// [Google Custom]: End Item Report generation - Modified by Syed on 20/07/2018 - Start
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("endItemReport")){
			FILE_HEADER = "Type,Name,Revision,Id,End Item,Level,Result";
		}
// [Google Custom]: End Item Report generation - Modified by Syed on 20/07/2018 - End

		// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on 10/05/2018 - Start		
		if (UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("buildEventReport")) {
			FILE_HEADER = "Type,Name,Revision,id,Old Attribute Value,New Attribute value,Modification Status";
		}
		// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on 10/05/2018 - End		
		
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write(FILE_HEADER);
			bw.write(NEW_LINE_SEPARATOR);
			Iterator itr = ebomList.iterator();
			while (itr.hasNext()) {
				Map ebomDetails = (Map) itr.next();
				String id = (String) ebomDetails.get(DomainObject.SELECT_ID);
				String rowDetails = updateRow(ebomDetails,reportName);
				bw.write(rowDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		return file;
	}

	/**
	 * This method used to write row details to csv file.
	 * @param ebomDetails
	 * @return String
	 * @throws IOException
	 * @author shajil
	 */
	public String updateRow(Map ebomDetails,String reportName) throws IOException {
		String id = (String) ebomDetails.get(DomainObject.SELECT_ID);
		String type = (String) ebomDetails.get(DomainObject.SELECT_TYPE);
		String name = (String) ebomDetails.get(DomainObject.SELECT_NAME);
		String revision = (String) ebomDetails.get(DomainObject.SELECT_REVISION);
		StringBuilder fileWriter = new StringBuilder();
		fileWriter.append(type);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(name);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(revision);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(id);
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("uomReport")){
			updateUOMDetails(ebomDetails,fileWriter);
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("MEPReport")){
			updateMEPDetails(ebomDetails,fileWriter);
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("CollabSpace")){
			updateCollabSpaceDetails(ebomDetails,fileWriter);
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("VPMReferenceReport")){
			updateVPMReferenceDetails(ebomDetails,fileWriter);
		}
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("PartWhereUsedReport")){
			updatePartWhereUsedDetails(ebomDetails,fileWriter);
		}
// [Google Custom]: End Item Report generation - Modified by Syed on 20/07/2018 - Start		
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("endItemReport")){
			updateEndItemListReport(ebomDetails,fileWriter);
		}
// [Google Custom]: End Item Report generation - Modified by Syed on 20/07/2018 - End

// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on 10/05/2018 - Start
		if (UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("buildEventReport")) {
			updatebuildEventDetails(ebomDetails, fileWriter);
		}
// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on 10/05/2018 - End
		
//[Google Custom]: #33753674: Added method to modify the Rel Attr 'Design Collaboration' to FALSE for all those parts not connected to Phyical Product -- Modified by Subbu Start
		if(UIUtil.isNotNullAndNotEmpty(reportName) && reportName.equals("DesignCollaborationReport")){
			fileWriter = new StringBuilder();
			updateDesignCollaborationReport(ebomDetails,fileWriter);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(name);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(revision);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(id);
		}
		//[Google Custom]: #33753674: Added method to modify the Rel Attr 'Design Collaboration' to FALSE for all those parts not connected to Phyical Product -- Modified by Subbu -End
		fileWriter.append(NEW_LINE_SEPARATOR);
		return fileWriter.toString();
	}

	/**
	 * This method to update UOM details to report
	 * @param ebomDetails
	 * @param fileWriter
	 * @author shajil
	 */
	public void updateUOMDetails(Map ebomDetails, StringBuilder fileWriter) {
		String uom = (String) ebomDetails.get(SELECT_ATTRIBUTE_UNITOFMEASURE);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(uom);
	}
	
	/**
	 * This method to update MEP details to report
	 * @param ebomDetails
	 * @param fileWriter
	 * @author shajil
	 */
	public void updateMEPDetails(Map ebomDetails, StringBuilder fileWriter) {
		String mepNames = (String) ebomDetails.get("MEPNames");
		String mepRevisionss = (String) ebomDetails.get("MEPRevisions");
		String mepIds = (String) ebomDetails.get("MEPIds");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(mepNames);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(mepRevisionss);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(mepIds);
	}
	
	/**
	 * This method used to create Part and MEP report.
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	public void createMEPReport(Context context, String[] args)
			throws Exception {
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		String objectWhere = "from[Manufacturer Equivalent]==True";
		MapList partList = DomainObject
				.findObjects(context,
						DomainObject.TYPE_PART, // Type
						DomainObject.QUERY_WILDCARD, // name
						DomainObject.QUERY_WILDCARD, // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						objectWhere,// Where
						true, // expand sub type
						objectSelects);
		Iterator partListItr = partList.iterator();
		int i=0;
		MapList mepReport = new MapList();
		while(partListItr.hasNext()){
			Map partInfo = (Map)partListItr.next();
			String id = (String)partInfo.get(SELECT_ID);
			DomainObject partObje = new DomainObject(id);
			MapList mepList = partObje.getRelatedObjects(context,
					RELATIONSHIP_MANUFACTURER_EQUIVALENT, TYPE_PART,
					objectSelects, null, false, true,
					(short) 1, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING, 0);
			if(mepList.size()>1){
				Iterator mepItr = mepList.iterator();
				StringBuilder mepNames = new StringBuilder();
				StringBuilder mepRevs = new StringBuilder();
				StringBuilder mepIds = new StringBuilder();
				while(mepItr.hasNext()){
					Map mepInfo = (Map)mepItr.next();
					String mepName = (String)mepInfo.get(SELECT_NAME);
					String mepRevision = (String)mepInfo.get(SELECT_REVISION);
					String mepId = (String)mepInfo.get(SELECT_ID);
					mepNames.append(mepName).append(" | ");
					mepRevs.append(mepRevision).append(" | ");
					mepIds.append(mepId).append(" | ");
				}
				partInfo.put("MEPNames",mepNames.substring(0, mepNames.length()-2));
				partInfo.put("MEPRevisions", mepRevs.substring(0, mepRevs.length()-2));
				partInfo.put("MEPIds", mepIds.substring(0, mepIds.length()-2));
				mepReport.add(partInfo);
			i++;
			}
			
		}
		String filePath = "c:\\Temp\\MEP_CSV_Report.csv";
		createCSVFile(filePath,mepReport,"MEPReport");
	}

	//[Google Custom]: Deviation Report in CSV Format - Modified by Sara on 14/03/2018 - Start
	/**
	 * To create Deviation report in CSV format
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author sara
	 */
	public void createDeviationReport(Context context, String[] args) throws Exception {
		
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_DESCRIPTION);
		objectSelects.add(SELECT_ATTRIBUTE_PROBLEM_TYPE);
		objectSelects.add(SELECT_ATTRIBUTE_ESTIMATED_START_DATE);
		//Added by XPLORIA to FIX Deviation Report Issue STARTS Here
		objectSelects.add(SELECT_ATTRIBUTE_APPROVED_TO);
		//Added by XPLORIA to FIX Deviation Report Issue ENDS Here
		objectSelects.add(DomainObject.SELECT_CURRENT);
		objectSelects.add(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);
		objectSelects.add(SELECT_ATTRIBUTE_RESOLUTION_DATE);
		objectSelects.add(SELECT_ATTRIBUTE_ENGINEERING_DIVISION);
		objectSelects.add(SELECT_ATTRIBUTE_REPORTED_AGAINST);
		objectSelects.add("current.actual");
		
		StringList objSelects = new StringList(DomainObject.SELECT_ID);
		objSelects.add(DomainObject.SELECT_NAME);
		
		StringList relSelects = new StringList(SELECT_RELATIONSHIP_ID);
		relSelects.add(SELECT_ATTRIBUTE_ACTION);
		
		StringBuilder sb1 = new StringBuilder();
		String sValues = null;
		
		Map<String, String> pageInfo = new googCustomIssue_mxJPO().getPageInfo(context, "googDeviationExport");
		String sLastRunDate = (String)pageInfo.get("DeviationReport.LastRun");
		
		/*StringBuilder sbNew = new StringBuilder();
		sbNew.append("project=='Chauffeur SelfDriveSystems'");
		sbNew.append("&&");
		sbNew.append("(current==Active||current==Review)");*/
		StringBuilder sbNew = new StringBuilder();
		// In QA below line has been removed.
		//sbNew.append("project=='Chauffeur SelfDriveSystems'");
		
		//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - Start
		sbNew.append("(attribute["+ATTRIBUTE_ENGINEERING_DIVISION+"].value == 'SDS')");
		//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - End
		
		sbNew.append("&&");
		sbNew.append("(current==Active||current==Review)");
		
		//[Google Custom]:- Modify output of Deviation Report by Sharad on 06-11-2018- Start
		/*if (!UIUtil.isNullOrEmpty(sLastRunDate)){
		sbNew.append("&&");
		sbNew.append("current.actual");
		sbNew.append(">");
		sbNew.append("\"" + sLastRunDate + "\"");
		}*/
		//[Google Custom]:- Modify output of Deviation Report by Sharad on 06-11-2018- Ends
		
		String objectWhere = sbNew.toString();
        ContextUtil.pushContext(context);
		MapList deviationList = DomainObject.findObjects(context, TYPE_ISSUE, // Type
				DomainObject.QUERY_WILDCARD, // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				objectWhere, // Where
				true, // expand sub type
				objectSelects);
		ContextUtil.popContext(context);
		//System.out.println("Total Deviations: "+deviationList.size());
		// [Google Custom]: 32480988 - Exclude deviations with Particular Route Template from deviation extract - Modified by Sara on 08/05/2018 - Start
		if (deviationList.size() > 0) {
			//[Google Custom]:- Modify output of Deviation Report by Sharad on 06-11-2018- Starts
			//commented as the logic for Route Templates is Deprecated
			//Added the Sorting of the Deviation list based on name
			//deviationList = getFilteredList(context, deviationList);
			deviationList.sort(SELECT_NAME, "ascending", "String");
		}
		// [Google Custom]: 32480988 - Exclude deviations with Particular Route Template from deviation extract - Modified by Sara on 08/05/2018 - End
		
		if (deviationList.size() > 0) {
			Iterator itr = deviationList.iterator();
			while (itr.hasNext()) {
				Map childMap = (Map) itr.next();
				String sObjectId = (String) childMap.get(SELECT_ID);
				DomainObject doj = new DomainObject(sObjectId);
				MapList sReportedAgainstList = doj.getRelatedObjects(context, REL_ISSUE, // relationship pattern
						TYPE_PART, // object pattern
						objSelects, // object selects
						relSelects, // relationship selects
						false, // to direction
						true, // from direction
						(short) 1, // recursion level
						null, // object where clause
						null, 0);
				StringBuilder sb2 = new StringBuilder();
				StringBuilder sb3 = new StringBuilder();
				StringBuilder sb4 = new StringBuilder();
				String sPartValues = null;
				String sAddValues=null;
				String sDeleteValues=null;
				
				
				if (sReportedAgainstList.size() > 0) {
					Iterator itr1 = sReportedAgainstList.iterator();
					while (itr1.hasNext()) {
						Map sMap = (Map) itr1.next();
						String sPartName = (String) sMap.get(SELECT_NAME);
						String sAction = (String)sMap.get(SELECT_ATTRIBUTE_ACTION);
						sb2.append(sPartName);
						sb2.append(",");
						sPartValues = sb2.toString();
						
						if(sAction.equals("Add")) {
							sb3.append(sPartName);
							sb3.append(",");
							sAddValues=sb3.toString();
						}
						
						if(sAction.equals("Delete")) {
							sb4.append(sPartName);
							sb4.append(",");
							sDeleteValues=sb4.toString();
						}
					}
					childMap = validateValues(childMap, sPartValues, sAddValues, sDeleteValues);
				}
				// To format the values in csv format
				sValues = getFormattedValues(sb1, childMap);
			}
			// To generate CSV Report
			createCSVReport(context, sValues, "DeviationReport");
		} else {
			//System.out.println("======================= No objects found =======================");
		}
	}
	
	/**
	 * To format the Parts/Add/Delete values
	 * @param childMap
	 * @param sPartValues
	 * @param sAddValues
	 * @param sDeleteValues
	 * @return Map
	 * @author Sara
	 */
	public Map validateValues(Map<String, String> childMap, String sPartValues, String sAddValues, String sDeleteValues) {
		String sContent = null;
		String sAdd = null;
		String sDelete = null;

		if (sPartValues != null) {
			if (sPartValues.endsWith(",")) {
				sContent = sPartValues.substring(0, sPartValues.length() - 1);
			}
			childMap.put("sPartValues", sContent);
		}

		if (sAddValues != null) {
			if (sAddValues.endsWith(",")) {
				sAdd = sAddValues.substring(0, sAddValues.length() - 1);
			}
			childMap.put("Add", sAdd);
		}

		if (sDeleteValues != null) {
			if (sDeleteValues.endsWith(",")) {
				sDelete = sDeleteValues.substring(0, sDeleteValues.length() - 1);
			}
			childMap.put("Delete", sDelete);
		}
		return childMap;
	}

	/**
	 * To format the values in csv
	 * @param sb1
	 * @param childMap
	 * @return String
	 * @throws Exception
	 * @author sara
	 */
	public String getFormattedValues(StringBuilder sb1, Map childMap) throws Exception {
		String sDeviationName = (String) childMap.get(SELECT_NAME);
		String sDescription = (String) childMap.get(SELECT_DESCRIPTION);
		sDescription = sDescription.replaceAll("\"", "");
		String sProblemType = (String) childMap.get(SELECT_ATTRIBUTE_PROBLEM_TYPE);
		String sApprovedFrom = (String) childMap.get(SELECT_ATTRIBUTE_ESTIMATED_START_DATE);
		//Added by XPLORIA to Fix the Deviation Report Issue Starts here
		String sApprovedTO = (String) childMap.get(SELECT_ATTRIBUTE_APPROVED_TO);
		//Added by XPLORIA to Fix the Deviation Report Issue Ends here
		String sCurrent = (String) childMap.get(SELECT_CURRENT);
		String sClosureDate = (String) childMap.get(SELECT_ATTRIBUTE_RESOLUTION_DATE);
		String sDRE = (String) childMap.get(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);
		//String sContent = (String) childMap.get("sPartValues");
		String sContent = (String) childMap.get(SELECT_ATTRIBUTE_REPORTED_AGAINST);
		String sAdd = (String) childMap.get("Add");
		String sDelete = (String) childMap.get("Delete");
		
		if (sCurrent.equals("Active")) {
			sCurrent = "Approved";
		}

		if (sCurrent.equals("Review")) {
			sCurrent = "Closed";
		}
		
		if (!UIUtil.isNullOrEmpty(sAdd)){
		// do nothing
		} else {
			sAdd="";
		}  
		
		if (!UIUtil.isNullOrEmpty(sDelete)){
			//do nothing
		} else {
			sDelete="";
		}

		sb1.append("\n");
		sb1.append("\"" + sDeviationName + "\"");
		sb1.append(",");
		sb1.append("\"" + sProblemType + "\"");
		sb1.append(",");
		sb1.append("\"" + sDescription + "\"");
		sb1.append(",");
		sb1.append("\"" + sContent + "\"");
		sb1.append(",");
		sb1.append("\"" + sDelete + "\"");
		sb1.append(",");
		sb1.append("\"" + sAdd + "\"");
		sb1.append(",");
		sb1.append("\"" + sApprovedFrom + "\"");
		sb1.append(",");
		//Added by XPLORIA to Fix the Deviation Report Issue Starts here
		sb1.append("\"" + sApprovedTO + "\"");
		sb1.append(",");
		//Added by XPLORIA to Fix the Deviation Report Issue Ends here
		sb1.append("\"" + sCurrent + "\"");
		sb1.append(",");
		sb1.append("\"" + sClosureDate + "\"");
		sb1.append(",");
		sb1.append("\"" + sDRE + "\"");
		//sb1.append("\n");
		return sb1.toString();
	}
	
    /**
     * To generate the file with deviation details in CSV format
     * @param sValues
     * @param reportName
     * @return File
     * @author Sara
     * @throws Exception 
     */
	public File createCSVReport(Context context,String sValues, String reportName) throws Exception {
		String FILE_HEADER1="Deviation_Number,Deviation_Type,Deviation_Description,Reported_Against,Affected_Components,Deviation_Components,Effective_Date,Approved_TO,Deviation_Status,Closure_Date,Deviation_DRE";																				
		
		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		//String filePath = "c:\\Temp";
		StringBuffer filename = new StringBuffer(50);
		//filename.append(filePath);
		//filename.append("\\");
		filename.append(reportName);
		filename.append("_");
		filename.append(fileCreateTimeStamp);
		filename.append(".csv");
		//String sFilename = filename.toString();
		String sFilename = TARGET_FILE_FOLDER+filename.toString();
		
		// File Creation Part
		File file = new File(sFilename);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		Map<String, String> pageInfo;
		try {
			bw.write(FILE_HEADER1);
			//bw.write("\n");
			bw.write(sValues);
			setDateInDeviationPageObject(context);
			pageInfo = new googCustomIssue_mxJPO().getPageInfo(context, "googCustomMailDetails");
			//generateEmail(context, sFilename,pageInfo);
		} finally {
			bw.close();
			
		}
		
		generateEmail(context, sFilename,pageInfo);
		
		return file;
	}

	/**
	 * @param context
	 * @param sFilename
	 * @throws FrameworkException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 * @author Sara
	 * @param pageInfo2 
	 */
	public void generateEmail(Context context, String sFilename, Map<String, String> pageInfo) throws FrameworkException, UnsupportedEncodingException, IOException, Exception {
		//Map<String, String> pageInfo = new ${CLASS:googCustomIssue}().getPageInfo(context, "googCustomMailDetails");
		String sToList = (String)pageInfo.get("Email.toList");
		String sCcList = (String)pageInfo.get("Email.ccList");
		String sSubject = (String)pageInfo.get("Email.subject");
		String sMessage = (String)pageInfo.get("Email.Body");
		//String[] toList = new String[] {sToList};
		String[] toList = sToList.split(",", -1);
		
		String[] ccList = new String[] {sCcList};
		StringBuilder sbBody = new StringBuilder();
		sbBody.append(sMessage);
		googCustomFunctions_mxJPO custom = new googCustomFunctions_mxJPO();
		custom.sendEMailToUser(context,sFilename,toList,ccList,sSubject,sbBody);
	}
	

	/**
	 * To set the last run date in deviation page object
	 * @param context
	 * @throws FrameworkException
	 * @author Sara
	 */
	public void setDateInDeviationPageObject(Context context) throws FrameworkException {
		String formattedDate = null;
		Date nowDate = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			formattedDate = formatter.format(nowDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("DeviationReport.LastRun=");
		sb.append(formattedDate);
		String sLastModifiedValue = sb.toString();
		String strCommand = "mod Page $1 content $2";
		MqlUtil.mqlCommand(context, strCommand, "googDeviationExport", sLastModifiedValue);
	}
	//[Google Custom]: Deviation Report in CSV Format - Modified by Sara on 14/03/2018 - End
	
	//[Google Custom]: EBOM Report in CSV Format - Created by Shajil on 04/04/2018 - Start
	
	//[Google Custom]: EBOM Report in CSV Format - Modified by Syed on 24/09/2018 - Start
	
	/**
	 * This method used to generate EBOM report
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - Start
	public File generateEBOMReport(Context context, Map argMap) throws Exception {

		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		String filePath = context.createWorkspace();
		googCustomFunctions_mxJPO custom = new googCustomFunctions_mxJPO();

		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);

		String objectId = (String) argMap.get("objectId");
		String process = (String) argMap.get("process");
		String excludedColumns = (String) argMap.get("excludedColumnsList");
		String expandLevel = (String) argMap.get("expandLevel");
		if (UIUtil.isNotNullAndNotEmpty(expandLevel) && "All".equals(expandLevel)) {
			expandLevel = "0";
		}
		int levelToExpand = Integer.parseInt(expandLevel);

		Map partInfo = DomainObject.newInstance(context, objectId).getInfo(context, objectSelects);

		String name = (String) partInfo.get(SELECT_NAME);
		String revision = (String) partInfo.get(SELECT_REVISION);
		StringBuilder fileName = new StringBuilder(name);
		fileName.append("_").append(revision).append("_").append(fileCreateTimeStamp);
		filePath = filePath + "\\" + fileName.toString();
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googEBOMReportMapping");
		String headers = pageInfo.get("EBOM.Report.Header");
		StringList headersList = FrameworkUtil.split(headers, ",");
		boolean formatList = false;
		StringList excludedColumnsList = FrameworkUtil.split(excludedColumns, ",");
		headers = updatePageInfoData(headersList,excludedColumnsList,pageInfo,formatList);
		objectSelects = createSelectStatements(context, pageInfo, headers).get("objectSelect");
		objectSelects.add(DomainObject.SELECT_ID);
		objectSelects.addElement("from[Part Specification].to[VPMReference].current");
		objectSelects.addElement("from[Part Specification].to[SW Component Instance For Team].current");
		objectSelects.addElement("from[Part Specification].to[SW Assembly Instance For Team].current");
		objectSelects.addElement("from[Part Specification].to[VPMReference].majorrevision");
		objectSelects.addElement("from[Part Specification].to[SW Component Instance For Team].majorrevision");
		objectSelects.addElement("from[Part Specification].to[SW Assembly Instance For Team].majorrevision");
		StringList relSelects = createSelectStatements(context, pageInfo, headers).get("relationSelect");

		MapList ebomList = DomainObject.newInstance(context, objectId).getRelatedObjects(context, RELATIONSHIP_EBOM,
				TYPE_PART, objectSelects, relSelects, false, true, (short) levelToExpand, DomainConstants.EMPTY_STRING,
				DomainConstants.EMPTY_STRING, 0);
		File outPutcsv = generateCSVFile(context, objectId, getFormatedList(context, ebomList, pageInfo,excludedColumnsList), pageInfo,
				filePath);

		try {
			File inputDirectory = new File(filePath);
			if (UIUtil.isNotNullAndNotEmpty(process) && "foreground".equals(process)) {
				return inputDirectory;
			}
			// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - Start
			File resultZipFile = createZipFile(inputDirectory);
			//Modified for Email issue
			String sToList =context.getUser();
			String[] toList = sToList.split(",", -1);	
			String[] ccList = new String[] {sToList};
			StringBuilder sbBody = new StringBuilder();
			sbBody.append("EBOM CSV Export report generated successfully");
			custom.sendGenericMailToUser(context,resultZipFile.getAbsolutePath(),toList,ccList,"EBOM CSV Export",sbBody,resultZipFile.getName());
			FileUtils.deleteDirectory(inputDirectory);
			resultZipFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outPutcsv;
	}
	//[Google Custom]: EBOM Report in CSV Format - Modified by Syed on 24/09/2018 - End
	
	/**
	 * This method used to format the resul list
	 * @param context
	 * @param ebomList
	 * @param pageInfo
	 * @return
	 * @author shajil
	 */
	public MapList getFormatedList(Context context, MapList ebomList, Map<String, String> pageInfo,
			StringList excludedList) {
		MapList formattedList = new MapList();
		Iterator itr = ebomList.iterator();
		String formatedNames = pageInfo.get("Names.Formatted.List");
		StringList formatedStringList = FrameworkUtil.split(formatedNames, ",");
		boolean formatList = true;
		
		formatedNames = updatePageInfoData(formatedStringList,excludedList,pageInfo,formatList);
		
		String[] formatedList = formatedNames.split(",");
		String changeInfo = pageInfo.get("changeInfo");
		String cadInfo = pageInfo.get("cadInfo");	
		googCustomFunctions_mxJPO custom = new googCustomFunctions_mxJPO();
		try {
			while (itr.hasNext()) {
				Map details = (Map) itr.next();
				String id = (String) details.get(SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(changeInfo) && "true".equalsIgnoreCase(changeInfo)) {
					MapList affectedObjects = custom.getConnectedChanges(context, id);
					updateChangeActionDetails(context, details, affectedObjects);
				}
				if (UIUtil.isNotNullAndNotEmpty(cadInfo) && "true".equalsIgnoreCase(cadInfo)) {
					updateCADInfo(context, details);
				}
				for (String formatedName : formatedList) {
					String keyName = pageInfo.get(formatedName);
					updateMapDetails(details, keyName);
				}
				formattedList.add(details);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formattedList;
	}

	/**
	 * This method used to get CAD related information
	 * @param context
	 * @param details
	 * @author shajil
	 */
	public void updateCADInfo(Context context, Map details) {
		StringList objectSelects = new StringList();
		objectSelects.addElement("from[Part Specification].to[VPMReference].current");
		objectSelects.addElement("from[Part Specification].to[SW Component Instance For Team].current");
		objectSelects.addElement("from[Part Specification].to[SW Assembly Instance For Team].current");
		StringList maturityVersion = new StringList();
		maturityVersion.addElement("from[Part Specification].to[VPMReference].majorrevision");
		maturityVersion.addElement("from[Part Specification].to[SW Component Instance For Team].majorrevision");
		maturityVersion.addElement("from[Part Specification].to[SW Assembly Instance For Team].majorrevision");
		StringBuilder cadInfo = new StringBuilder();
		String cadInfoVal = "";
		for (int i = 0; i < objectSelects.size(); i++) {
			updateMapDetails(details, (String) objectSelects.get(i));
			String cadValue = (String) details.get(objectSelects.get(i));
			if (UIUtil.isNotNullAndNotEmpty(cadValue)) {
				cadInfo.append(cadValue);
				cadInfo.append(",");
			}
		}

		if (cadInfo.toString().endsWith(",")) {
			cadInfoVal = cadInfo.substring(0, cadInfo.length() - 1);
		}
		StringBuilder cadMaturityVersion = new StringBuilder();
		for (int i = 0; i < maturityVersion.size(); i++) {
			updateMapDetails(details, (String) maturityVersion.get(i));
			String cadValue = (String) details.get(maturityVersion.get(i));
			if (UIUtil.isNotNullAndNotEmpty(cadValue)) {
				cadMaturityVersion.append(cadValue);
				cadMaturityVersion.append(",");
			}
		}
		String cadVerVal = "";
		if (cadMaturityVersion.toString().endsWith(",")) {
			cadVerVal = cadMaturityVersion.substring(0, cadMaturityVersion.length() - 1);
		}
		details.put("CADDetails", cadInfoVal);
		details.put("CADVersion", cadVerVal);
	}

	public void updateMapDetails(Map details, String keyName) {
		Object keyValueObj = details.get(keyName);
		if (null != keyValueObj && (keyValueObj instanceof StringList)) {
			StringBuilder keyValues = new StringBuilder();
			StringList objectList = (StringList) keyValueObj;
			String keyValue = "";
			for(int i=0;i<objectList.size();i++) {
				String keyVal = (String) objectList.get(i);
				if(UIUtil.isNotNullAndNotEmpty(keyVal)) {
					keyValues.append(keyVal).append(",");
				}
				
			}
			if (keyValues.toString().endsWith(",")) {
				keyValue = keyValues.substring(0, keyValues.length() - 1);
			}
			details.put(keyName, keyValue);
			/*keyValues.append(details.get(keyName).toString());
			String value = keyValues.toString().replace("[", "").replace("]", "");
			details.put(keyName, value);*/
		} else if (null != keyValueObj && (keyValueObj instanceof String)) {
			details.put(keyName, details.get(keyName).toString());
		} else {
			details.put(keyName, "");
		}
	}

	/**
	 * This method to get the CA related information
	 * @param context
	 * @param details
	 * @param affectedObjects
	 * @author shajil
	 */
	public void updateChangeActionDetails(Context context, Map details, MapList affectedObjects) {
		Iterator changeItr = affectedObjects.iterator();
		StringBuilder changeStates = new StringBuilder();
		StringBuilder changeNames = new StringBuilder();
		while (changeItr.hasNext()) {
			Map affectedData = (Map) changeItr.next();
			String state = (String) affectedData.get(SELECT_CURRENT);
			String itemName = (String) affectedData.get("name");
			String itemId = (String) affectedData.get("id");
			changeStates.append(state);
			changeStates.append(" [");
			changeStates.append(itemName);
			changeStates.append(" ]");
			changeStates.append("  ");
			changeNames.append(" [");
			changeNames.append(itemName);
			changeNames.append(" ]");
		}
		details.put("Change", changeNames.toString());
		details.put("ChangeState", changeStates.toString());

	}

	/**
	 * This method used to generate csv file
	 * @param context
	 * @param objectId
	 * @param ebomList
	 * @param pageInfo
	 * @param outPutLocation
	 * @return
	 * @throws IOException
	 * @throws FrameworkException
	 * @author shajil
	 */
	public File generateCSVFile(Context context, String objectId, MapList ebomList, Map<String, String> pageInfo, String outPutLocation) throws IOException, FrameworkException {
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Sandeep on 27/08/2018 -- Start 
		googCustomFunctions_mxJPO googCustomFunctions = new googCustomFunctions_mxJPO(); 
		ebomList= googCustomFunctions.formatMaplistStringValues(ebomList);
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Sandeep on 27/08/2018 -- End
	
		String headers = pageInfo.get("EBOM.Report.Header");
		StringList objList = new StringList(SELECT_ID);
		objList.addElement(SELECT_NAME);
		objList.addElement(SELECT_REVISION);
		Map partInfo = DomainObject.newInstance(context, objectId).getInfo(context, objList);
		String name = (String)partInfo.get(SELECT_NAME);
		String revision = (String)partInfo.get(SELECT_REVISION);
		StringBuilder fileName = new StringBuilder(name);
		fileName.append("_").append(revision).append(".csv");
		File pathLoc = new File(outPutLocation);
		if(!pathLoc.exists()) {
			pathLoc.mkdirs();
		}
		String filePath = outPutLocation +"\\"+ fileName.toString();
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write("Name");
			bw.append(COMMA_DELIMITER);
			bw.write(name);
			bw.write(NEW_LINE_SEPARATOR);
			bw.write("Revision");
			bw.append(COMMA_DELIMITER);
			bw.write(revision);
			bw.write(NEW_LINE_SEPARATOR);
			bw.write(NEW_LINE_SEPARATOR);
			bw.write(headers);
			bw.write(NEW_LINE_SEPARATOR);
			Iterator itr = ebomList.iterator();
			while (itr.hasNext()) {
				Map ebomDetails = (Map) itr.next();
				String id = (String) ebomDetails.get(DomainObject.SELECT_ID);
				String rowDetails = createRowDetails(ebomDetails,pageInfo);
				bw.write(rowDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		return pathLoc;
	}

	/**
	 * This method create row details for csv file
	 * @param ebomDetails
	 * @param pageInfo
	 * @return
	 * @author shajil
	 */
	public String createRowDetails(Map ebomDetails, Map<String, String> pageInfo) {
		String headers = pageInfo.get("EBOM.Report.Header");
		StringList headerNames = FrameworkUtil.split(headers, ",");
		Iterator itr = headerNames.iterator();
		StringBuilder fileWriter = new StringBuilder();
		try {
			while (itr.hasNext()) {
				String headerName = itr.next().toString().trim();
				headerName = headerName.replace(" ", "");
				String keyName = headerName;
				if (pageInfo.containsKey("relationship." + keyName)) {
					headerName = "relationship." + keyName;
				}
				String cellValue = "";
				String keyValue = pageInfo.get(headerName);
				if (pageInfo.containsKey(headerName)) {
					cellValue = (String) ebomDetails.get(keyValue);
					//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Sandeep on 27/08/2018 -- Start 
					/*if (UIUtil.isNotNullAndNotEmpty(cellValue) && cellValue.contains(",")) {
						cellValue = "\"" + cellValue + "\"";
					}*/
					//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Sandeep on 27/08/2018 -- Start 
					fileWriter.append(cellValue);
					fileWriter.append(COMMA_DELIMITER);
				} else {
					fileWriter.append(cellValue);
					fileWriter.append(COMMA_DELIMITER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileWriter.append(NEW_LINE_SEPARATOR);
		return fileWriter.toString();
	}

	/**
	 * This method will generate select statement from page object
	 * @param context
	 * @param pageInfo
	 * @param headers
	 * @return
	 * @author shajil
	 */
	public Map<String,StringList> createSelectStatements(Context context, Map<String, String> pageInfo, String headers) {
		StringList objectSelect = new StringList();
		StringList relSelect = new StringList();
		StringList headerNames = FrameworkUtil.split(headers, ",");
		Iterator itr = headerNames.iterator();
		Map<String,StringList> selectStatements = new HashMap<String,StringList>();
		try {
		while(itr.hasNext()) {
			String headerName = itr.next().toString().trim();
			headerName = headerName.replace(" ", "");
			String relSel = "relationship."+headerName;
			if(pageInfo.containsKey(headerName)) {
				String selectValue = pageInfo.get(headerName);
				if(UIUtil.isNotNullAndNotEmpty(selectValue)) {
					objectSelect.addElement(selectValue);
				}
			}else if(pageInfo.containsKey(relSel)) {
				String selectValue = pageInfo.get(relSel);
				if(UIUtil.isNotNullAndNotEmpty(selectValue)) {
					relSelect.addElement(selectValue);
				}
			}
		}
		
		selectStatements.put("objectSelect", objectSelect);
		selectStatements.put("relationSelect", relSelect);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return selectStatements;
	}
	
	/**
	 * This method used to generate zip file
	 * @param inputDirectory
	 * @return
	 * @throws IOException
	 * @author shajil
	 */
	public File createZipFile(File inputDirectory)
			throws IOException {
		String outZipFolder = inputDirectory.getPath() + ".zip";
		File outputZip = new File(outZipFolder);
		outputZip.getParentFile().mkdirs();
		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZip));
		List<File> listFiles = listFiles(inputDirectory);
		for (File file : listFiles) {
			String filePath = file.getCanonicalPath();
			int lengthDirectoryPath = inputDirectory.getCanonicalPath().length();
			int lengthFilePath = file.getCanonicalPath().length();
			String zipFilePath = filePath.substring(lengthDirectoryPath + 1, lengthFilePath);
			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zipOutputStream.putNextEntry(zipEntry);
			FileInputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes)) >= 0) {
				zipOutputStream.write(bytes, 0, length);
			}
			zipOutputStream.closeEntry();
			inputStream.close();
		}
		zipOutputStream.close();
		return outputZip;
	}
	
	/**
	 * This method to list the files available in directory
	 * @param inputDirectory
	 * @return
	 * @throws IOException
	 * @author shajil
	 */
	public List<File> listFiles(File inputDirectory) throws IOException {
		List<File> listFiles = new ArrayList<File>();
		File[] allFiles = inputDirectory.listFiles();
		for (File file : allFiles) {
			listFiles.add(file);
		}
		return listFiles;
	}
	
	//[Google Custom]: EBOM Report in CSV Format - Created by Shajil on 04/04/2018 - Ends
	
		//[Google Custom]: CollabSpace Report in CSV Format - Created by Shajil on 04/26/2018 - Starts
	
	/**
	 * This method used to generate CollabSpace report
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	public void generateCollabSpaceShareReport(Context context, String[] args) throws Exception {
		String sFilePath = args[0];
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		Date strtDate = new Date();
		long startTime = strtDate.getTime();
		String level = args[1];
		try {
			File file = new File(sFilePath);
			String fileLoc = file.getParent();
			StringBuilder fileName = new StringBuilder(fileLoc);
			fileName.append("\\").append("CollabSpaceReport");
			fileName.append("_").append(startTime).append(".csv");
			if (file.exists()) {
				br = new BufferedReader(new FileReader(file));
				String productName;
				StringBuilder productNames = new StringBuilder();
				while ((productName = br.readLine()) != null) {
					productNames.append(productName);
					productNames.append(",");
				}
				MapList productDetails = updatePartDetails(context, getProductDetails(context, productNames.toString()),
						level);
				createCSVFile(fileName.toString(), productDetails, "CollabSpace");
			} else {
				//System.out.println("Input file not found....");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
	}

	/**
	 * This method used to update Part details for CollabSpace report.
	 * @param context
	 * @param productDetails
	 * @param level
	 * @return MapList
	 * @throws FrameworkException
	 * @author shajil
	 */
	public MapList updatePartDetails(Context context, MapList productDetails, String level) throws FrameworkException {
		Iterator prodItr = productDetails.iterator();
		MapList resultList = new MapList();
		while (prodItr.hasNext()) {
			Map prodInfo = (Map) prodItr.next();
			String prodId = (String) prodInfo.get(SELECT_ID);
			String partId = (String) prodInfo.get("to[Part Specification].from.id");
			prodInfo.put("ProductOwnersip", updateOwnershipValue(context, prodId));
			updateMapDetails(prodInfo, "ProductOwnersip");
			updatePartdetails(context, prodInfo);
			if (UIUtil.isNotNullAndNotEmpty(level)) {
				prodInfo.put("EBOMDetail", getEBOMDetails(context, prodInfo, partId, level));
			}
			resultList.add(prodInfo);
		}
		return resultList;
	}

	/**
	 * This method to get the EBOM information for CallabSpace report
	 * @param context
	 * @param prodInfo
	 * @param partId
	 * @param level
	 * @return MapList
	 * @throws FrameworkException
	 * @author shajil
	 */
	public MapList getEBOMDetails(Context context, Map prodInfo, String partId, String level)
			throws FrameworkException {
		StringList objectSelects = new StringList(SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_REVISION);
		objectSelects.addElement("from[Part Specification|to.type==VPMReference].to.id");
		objectSelects.addElement("from[Part Specification|to.type==VPMReference].to.name");
		objectSelects.addElement("from[Part Specification|to.type==VPMReference].to.type");
		objectSelects.addElement("from[Part Specification|to.type==VPMReference].to.revision");
		short expandLevel = Short.parseShort(level);
		MapList ebomList = DomainObject.newInstance(context, partId).getRelatedObjects(context, RELATIONSHIP_EBOM,
				TYPE_PART, objectSelects, null, false, true, expandLevel, DomainConstants.EMPTY_STRING,
				DomainConstants.EMPTY_STRING, 0);
		Iterator bomItr = ebomList.iterator();
		MapList resultList = new MapList();
		while (bomItr.hasNext()) {
			Map bomInfo = (Map) bomItr.next();
			String objId = (String) bomInfo.get(SELECT_ID);
			String type = (String) bomInfo.get(SELECT_TYPE);
			String name = (String) bomInfo.get(SELECT_NAME);
			String revision = (String) bomInfo.get(SELECT_REVISION);

			bomInfo.put("PartName", name);
			bomInfo.put("PartType", type);
			bomInfo.put("PartRevision", revision);
			bomInfo.put("PartID", objId);
			bomInfo.put("PartOwnersip", updateOwnershipValue(context, objId));
			updateMapDetails(bomInfo, "PartOwnersip");

			if (bomInfo.containsKey("from[Part Specification].to.id")) {
				String prodId = (String) bomInfo.get("from[Part Specification].to.id");
				String prodType = (String) bomInfo.get("from[Part Specification].to.type");
				String prodName = (String) bomInfo.get("from[Part Specification].to.name");
				String prodRevision = (String) bomInfo.get("from[Part Specification].to.revision");

				bomInfo.put(SELECT_ID, prodId);
				bomInfo.put(SELECT_NAME, prodName);
				bomInfo.put(SELECT_TYPE, prodType);
				bomInfo.put(SELECT_REVISION, prodRevision);
				bomInfo.put("ProductOwnersip", updateOwnershipValue(context, prodId));
				updateMapDetails(bomInfo, "ProductOwnersip");
			}else {
				bomInfo.put(SELECT_ID, "");
				bomInfo.put(SELECT_NAME, "");
				bomInfo.put(SELECT_TYPE, "");
				bomInfo.put(SELECT_REVISION, "");
				bomInfo.put("ProductOwnersip","");
			}
			resultList.add(bomInfo);
		}
		return resultList;
	}

	/**
	 * This method used to update Part details for CollabSpace report.
	 * @param context
	 * @param prodInfo
	 * @return Map
	 * @throws FrameworkException
	 * @author shajil
	 */
	public Map updatePartdetails(Context context, Map prodInfo) throws FrameworkException {
		String partId = (String) prodInfo.get("to[Part Specification].from.id");
		StringList objectSelects = new StringList(SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_REVISION);
		Map partInfo = DomainObject.newInstance(context, partId).getInfo(context, objectSelects);
		prodInfo.put("PartName", partInfo.get(SELECT_NAME));
		prodInfo.put("PartType", partInfo.get(SELECT_TYPE));
		prodInfo.put("PartRevision", partInfo.get(SELECT_REVISION));
		prodInfo.put("PartID", partInfo.get(SELECT_ID));
		prodInfo.put("PartOwnersip", updateOwnershipValue(context, partId));
		updateMapDetails(prodInfo, "PartOwnersip");
		return prodInfo;
	}

	/**
	 * This method used to update CollabSpace details
	 * @param ebomDetails
	 * @param fileWriter
	 * @throws IOException
	 * @author shajil
	 */
	public void updateCollabSpaceDetails(Map ebomDetails, StringBuilder fileWriter) throws IOException {
		String productOwnersip = (String) ebomDetails.get("ProductOwnersip");
		String partType = (String) ebomDetails.get("PartType");
		String partName = (String) ebomDetails.get("PartName");
		String partRevision = (String) ebomDetails.get("PartRevision");
		String partID = (String) ebomDetails.get("PartID");
		String partOwnersip = (String) ebomDetails.get("PartOwnersip");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append("\"" + productOwnersip + "\"");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(partType);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(partName);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(partRevision);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(partID);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append("\"" + partOwnersip + "\"");
		if (ebomDetails.containsKey("EBOMDetail")) {
			MapList bomList = (MapList) ebomDetails.get("EBOMDetail");
			if (bomList.size() > 0) {
				fileWriter.append(NEW_LINE_SEPARATOR);
				Iterator bomItr = bomList.iterator();
				while (bomItr.hasNext()) {
					Map bomInfo = (Map) bomItr.next();
					fileWriter.append(updateRow(bomInfo, "CollabSpace"));
				}
			} else {
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		}
	}

	/**
	 * This method used to update Ownership details
	 * @param context
	 * @param prodId
	 * @return
	 * @throws FrameworkException
	 * @author shajil
	 */
	public Object updateOwnershipValue(Context context, String prodId) throws FrameworkException {

		StringList ownershipValues = DomainObject.newInstance(context, prodId).getInfoList(context, "ownership");
		StringBuilder ownersipDetails = new StringBuilder();
		StringList ownershipNames = new StringList();
		for (int i = 0; i < ownershipValues.size(); i++) {
			StringList ownerships = new StringList();
			ownerships = FrameworkUtil.split((String) ownershipValues.get(i), "|");
			for (int j = 1; j < ownerships.size(); j++) {
				ownershipNames.add(ownerships.get(j));
				j = j + 2;
			}
		}
		return ownershipNames;
	}

	/**
	 * This method used to get Product details
	 * @param context
	 * @param productNames
	 * @return MapList
	 * @throws FrameworkException
	 * @author shajil
	 */
	public MapList getProductDetails(Context context, String productNames) throws FrameworkException {

		StringList objectSelects = new StringList(SELECT_ID);
		objectSelects.add("to[Part Specification].from.id");
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_REVISION);
		MapList productList = DomainObject.findObjects(context, DomainObject.QUERY_WILDCARD, // Type
				productNames, // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				null, // Where
				false, // expand sub type
				objectSelects);
		productList.sort(SELECT_NAME, "ascending", "String");
		return productList;
	}
	
	//[Google Custom]: CollabSpace Report in CSV Format - Created by Shajil on 04/26/2018 - Ends
	
	// [Google Custom]: 32480988 - Exclude deviations with Particular Route Template from deviation extract - Created by Sara on 08/05/2018 - Start
	/**
	 * To resolve Deviation extraction issue - 32480988
	 * @param context
	 * @param deviationList
	 * @return
	 * @throws FrameworkException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 * @author Sara
	 */
	private MapList getFilteredList(Context context, MapList deviationList) throws FrameworkException, UnsupportedEncodingException, IOException, Exception {
		MapList sTempList = new MapList();
		googCustomIssue_mxJPO custom = new googCustomIssue_mxJPO();
		Map<String, String> pageInformation = custom.getPageInfo(context, "googIssueMapping");
		String routeNames = pageInformation.get("emxComponents.type_Issue.Exclude.RouteNames");
		//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - Start
		deviationList.sort(SELECT_NAME, "ascending", "String");
		//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - End
		String[] routeTemplateNames = routeNames.split(",");
		//[Google Custom]:- Modify output of Deviation Report by Sharad - Starts
		//Commented the below validation as Route template are not used any more but Engineering Division is used for this.
		/*
		if (deviationList.size() > 0) {
			Iterator<Map> it = deviationList.iterator();
			while (it.hasNext()) {
				boolean sFlag = false;
				Map childMap1 = it.next();
				String sDeviationId = (String) childMap1.get(SELECT_ID);
				DomainObject dojDeviation = new DomainObject(sDeviationId);
				StringList sRouteList = dojDeviation.getInfoList(context,"from[Object Route].to.name");
				// [Google Custom]: 32480988 - Excluded "Vehicle Deviation Approval Route,Vehicle Deviation Closure Approval Route"/Empty RouteList - Modified by Sara on 23/05/2018 - Start
				if(sRouteList.size()>0) {
				for (int i=0;i<routeTemplateNames.length;i++){
					String sName = routeTemplateNames[i];
					if(sRouteList.contains(sName)) {
						sFlag=true;
						break;
					}
				}} else {
					sFlag=true;
					//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - Start
					//break;
					//[Google Custom]: 33154659- Modify output of Deviation Report based on "Engineering Division" - Modified by Sara on 06/06/2018 - End
				}
				// [Google Custom]: 32480988 - Excluded "Vehicle Deviation Approval Route,Vehicle Deviation Closure Approval Route"/Empty RouteList - Modified by Sara on 23/05/2018 - End
																																												
				if(!sFlag) {
					sTempList.add(childMap1);
				}
			}
		deviationList = sTempList;
		}
		*/
		//[Google Custom]:- Modify output of Deviation Report by Sharad - Ends
		return deviationList;
	}
	// [Google Custom]: 32480988 - Exclude deviations with Particular Route Template from deviation extract - Created by Sara on 08/05/2018 - End
	
	//[Google Custom]: VPMReference Report in CSV Format - Created by Syed on 05/15/2018 - Starts
	/**
	 * This method used generate Part(csv) report.
	 * This will report only the Part which is having more than one VPMReference Documents attached
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public void createVPMPartConnectionReport(Context context, String[] args) throws Exception {
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		String objectWhere = "from["+RELATIONSHIP_PART_SPECIFICATION+"|to.type=="+TYPE_VPMREFERENCE+"]==True";
		//System.out.println("Generating Report will take some time. Please wait........");
		MapList partList = DomainObject.findObjects(context, DomainObject.TYPE_PART, // Type
				DomainObject.QUERY_WILDCARD, // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				objectWhere, // Where
				true, // expand sub type
				objectSelects);
		if (partList.size() > 0) {
			partList = getFilteredPartList(context, partList);
		}
		String filePath = "c:\\Temp\\VPMReference_CSV_Report.csv";
		createCSVFile(filePath, partList, "VPMReferenceReport");
		//System.out.println("Report Generation successful........");
	}
	
	/**
	 * This method used generate Part(csv) report.
	 * This will get filtered Part list which is having more than one VPMReference Documents attached
	 * @param context
	 * @throws Exception
	 * @param partList
	 * @return partList
	 * @throws FrameworkException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 * @author Syed
	 */
	private MapList getFilteredPartList(Context context, MapList partList)
			throws FrameworkException, UnsupportedEncodingException, IOException, Exception {
		MapList tempList = new MapList();
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_TYPE);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		if (partList.size() > 0) {
			Iterator<Map> partItr = partList.iterator();
			while (partItr.hasNext()) {
				Map partInfo = partItr.next();
				String partObjId = (String) partInfo.get(SELECT_ID);
				DomainObject partObj = new DomainObject(partObjId);
				MapList vpmReferenceList = partObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // relationship
						// pattern
						TYPE_VPMREFERENCE, // object pattern
						objectSelects, // object selects
						null, // relationship selects
						false, // to direction
						true, // from direction
						(short) 1, // recursion level
						null, // object where clause
						null, 0);
				if (vpmReferenceList.size() > 1) {
					StringBuilder VPMNames = new StringBuilder();
					StringBuilder VPMRevs = new StringBuilder();
					StringBuilder VPMIds = new StringBuilder();
					Iterator<Map> objItr = vpmReferenceList.iterator();
					while (objItr.hasNext()) {
						Map partMap = objItr.next();
						String VPMName = (String) partMap.get(SELECT_NAME);
						String VPMRevision = (String) partMap.get(SELECT_REVISION);
						String VPMId = (String) partMap.get(SELECT_ID);
						VPMNames.append(VPMName).append(" | ");
						VPMRevs.append(VPMRevision).append(" | ");
						VPMIds.append(VPMId).append(" | ");
					}
					partInfo.put("VPMNames", VPMNames.substring(0, VPMNames.length() - 2));
					partInfo.put("VPMRevisions", VPMRevs.substring(0, VPMRevs.length() - 2));
					partInfo.put("VPMIds", VPMIds.substring(0, VPMIds.length() - 2));
					tempList.add(partInfo);
				}
			}
		}
		return tempList;
	}
	
	/**
	 * This method used to update VPMReference details
	 * @param ebomDetails
	 * @param fileWriter
	 * @throws IOException
	 * @author Syed
	 */
	public void updateVPMReferenceDetails(Map ebomDetails, StringBuilder fileWriter) throws IOException {
		String VPMNames = (String) ebomDetails.get("VPMNames");
		String VPMRevisionss = (String) ebomDetails.get("VPMRevisions");
		String VPMIds = (String) ebomDetails.get("VPMIds");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(TYPE_VPMREFERENCE);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(VPMNames);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(VPMRevisionss);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(VPMIds);
	}
	//[Google Custom]: VPMReference Report in CSV Format - Created by Syed on 05/15/2018 - Ends
	
	//[Google Custom]:32895054 - PartWhereUsed Report in CSV Format - Created by Subbu on 29/05/2018 - Starts
	/**
	 * This method is used to read Part Info from input sheet and generate PartWhereUsed report 
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Subbu
	 */
	public void getPartInfo(Context context, String[] args) throws Exception {
		String strFilePath = args[0];
		String struserInput = args[1];
		String strusrChoice = args[2];
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		BufferedReader br = null;
		Date strtDate = new Date();
		long startTime = strtDate.getTime();
		try {
			File file = new File(strFilePath);
			String fileLoc = file.getParent();
			
			if (file.exists()) {
				br = new BufferedReader(new FileReader(file));
				String partName = null;
				String partRev = null;
				StringBuilder partNames = new StringBuilder();
				StringBuilder fileName = new StringBuilder();
				while ((partName = br.readLine()) != null) {
				partNames.append(partName);
				partNames.append(",");	
				}
				MapList mlPartList = DomainObject.findObjects(context, DomainConstants.TYPE_PART, // Type
				partNames.toString(), // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				null, // Where
				false, // expand sub type
				objectSelects);
				if (mlPartList.size() > 0) {
					Iterator<Map> partitr = mlPartList.iterator();
					while (partitr.hasNext()) {
						Map mpPartInfo = partitr.next();
						partName = (String) mpPartInfo.get(SELECT_NAME);
						partRev = (String) mpPartInfo.get(SELECT_REVISION);
						fileName = new StringBuilder(fileLoc);
						fileName.append("\\").append("PartWhereUsedReport");					
						fileName.append("_").append(partName).append("_").append(partRev).append("_").append(fileCreateTimeStamp).append(".csv");	
					    mlPartList = getPartDetails(context, mpPartInfo , struserInput, strusrChoice);	
						if(mlPartList.size() > 0) {
						createCSVFile(fileName.toString(), mlPartList, "PartWhereUsedReport");	
						}
					}						
						
				}
					
			} else {
				//System.out.println("Input file not found....");
			}
				Date endDate = new Date();
				long endTime = endDate.getTime();
			//System.out.println("TOTAL TIME TAKEN TO GENERATE THE REPORT (ms): " + (endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		
	}
	
	
	/**
	 * This method used to get the PartWhereUsed Information
	 * @param ebomDetails
	 * @param fileWriter
	 * @throws IOException
	 * @author Subbu
	 * @param struserInput 
	 * @param strusrChoice 
	 * @throws Exception 
	 */
	public MapList getPartDetails(Context context, Map mpPartInfo, String struserInput, String strusrChoice)
			throws Exception {

		StringList objectSelects = new StringList();
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_REVISION);
		objectSelects.add(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_CURRENT);
		objectSelects.add(DomainObject.SELECT_DESCRIPTION);
		objectSelects.add(SELECT_ATTRIBUTE_RELEASE_PHASE);
		objectSelects.add(SELECT_ATTRIBUTE_CHANGE_CONTROLLED);
		objectSelects.add(REL_TO_EBOM_EXISTS);
		objectSelects.add(POLICY_CLASSIFICATION);
		StringList relSelects = new StringList(SELECT_RELATIONSHIP_ID);
		relSelects.add(SELECT_ATTRIBUTE_FIND_NUMBER);
		MapList mlPartEBOMList = new MapList();
		String strPartId = null;
		strPartId = (String) mpPartInfo.get(SELECT_ID);
		DomainObject domObjPart = new DomainObject(strPartId);
		boolean boolAddEndItemsToList = false;
		Short shRecurseToLevel = 1;
		if ("Highest".equals(struserInput)) {
			shRecurseToLevel = -1;
		} else if ("All".equals(struserInput)) {
			shRecurseToLevel = 0;
		} else if (("UpTo".equals(struserInput)) || ("UpToAndHighest".equals(struserInput))) {
			if (UIUtil.isNotNullAndNotEmpty(strusrChoice)) {
				shRecurseToLevel = Short.parseShort(strusrChoice);
			} else {
				shRecurseToLevel = 0;
			}
		}

		MapList mlPartList = new MapList();
		mlPartEBOMList = domObjPart.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relationship
																									// pattern
				DomainConstants.TYPE_PART, objectSelects, relSelects, true, false, shRecurseToLevel, null, null,
				(short) 0, false, false, (short) 0, null, null, null, null, null);
		if (("UpToAndHighest".equals(struserInput))) {
			boolAddEndItemsToList = true;
			mlPartList = domObjPart.getRelatedObjects(context, RELATIONSHIP_EBOM, DomainConstants.TYPE_PART,
					objectSelects, relSelects, true, false, (short) -1, null, null, (short) 0, false, false, (short) 0,
					null, null, null, null, null);

		}

		MapList finalListReturn = mergeList(mlPartEBOMList, mlPartList, new MapList(), null, boolAddEndItemsToList, "",
				"");
		return finalListReturn;
	}
	
	 	/**
	 * This method used to update PartWhereUsed details
	 * @param ebomDetails
	 * @param fileWriter
	 * @throws IOException
	 * @author Subbu
	 */
	public void updatePartWhereUsedDetails(Map ebomDetails, StringBuilder fileWriter) throws IOException {

		String strPartType = (String) ebomDetails.get(SELECT_TYPE);
		String strPartName = (String) ebomDetails.get(SELECT_NAME);
		String strPartRev = (String) ebomDetails.get(SELECT_REVISION);
		String strPartState = (String) ebomDetails.get(SELECT_CURRENT);
		//String strLevel = "";
		StringBuilder levelDetails = new StringBuilder("-");
		if (ebomDetails.containsKey("objectLevel")) {
			levelDetails.append(ebomDetails.get("objectLevel")).toString();
		}
		String strFindNumber = (String) ebomDetails.get(SELECT_ATTRIBUTE_FIND_NUMBER);
		String strReleasePhase = (String) ebomDetails.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
		String strChangeControlled = (String) ebomDetails.get(SELECT_ATTRIBUTE_CHANGE_CONTROLLED);
		String strDescription = (String) ebomDetails.get(SELECT_DESCRIPTION);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strDescription);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(levelDetails.toString());
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strPartState);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strFindNumber);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strReleasePhase);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strChangeControlled);
	}
	
	private MapList mergeList(MapList whereUsedList, MapList endItemList, MapList spareSubAltPartList, MapList ebomSubList, boolean boolAddEndItemsToList, String refDesFilter, String fnFilter) {
		int iWhereUsedListSize = getListSize(whereUsedList);
		int iEndItemListSize   = getListSize(endItemList);
		int iEbomSubListSize   = getListSize(ebomSubList);
		int iSpareSubAltSize   = getListSize(spareSubAltPartList);

        StringList sListEndItemId = getDataForThisKey(endItemList, DomainConstants.SELECT_ID);

		MapList listReturn = new MapList(iWhereUsedListSize);

        Map map;

		String objectId;
		String strLevel;
		String strRelEBOMExists;

		for (int i = 0; i < iWhereUsedListSize; i++) {
			map = (Map) whereUsedList.get(i);

			objectId = getStringValue(map, DomainConstants.SELECT_ID);

            if (isFNAndRefDesFilterPassed(map, refDesFilter, fnFilter)) {
                strLevel = getStringValue(map, "level");
                map.put("objectLevel", strLevel);

                strRelEBOMExists = getStringValue(map, REL_TO_EBOM_EXISTS);
                if ("False".equals(strRelEBOMExists)) {
                    map.put("EndItem", "Yes");
                    sListEndItemId.remove(objectId);
                }
                if ("Unresolved".equals(getStringValue(map, POLICY_CLASSIFICATION))) {
                	map.put("RowEditable", "readonly");
                	map.put("disableSelection", "true");
                }
                listReturn.add(map);
            }
        }

		for (int i = 0; i < iEbomSubListSize; i++) {
			map = (Map) ebomSubList.get(i);

        	if (isFNAndRefDesFilterPassed(map, refDesFilter, fnFilter)) {
                strLevel = getStringValue(map, "level");

                map.put("objectLevel", strLevel);
                map.put("relationship", RELATIONSHIP_EBOM_SUBSTITUE);

                listReturn.add(map);
        	}
        }

		if (boolAddEndItemsToList) {
            for (int i = 0; i < iEndItemListSize; i++) {
                map = (Map) endItemList.get(i);
                objectId = getStringValue(map, DomainConstants.SELECT_ID);

            	if (sListEndItemId.contains(objectId) && isFNAndRefDesFilterPassed(map, refDesFilter, fnFilter)) {
                    if ("Unresolved".equals(getStringValue(map, POLICY_CLASSIFICATION))) {
                    	map.put("RowEditable", "readonly");
                    	map.put("disableSelection", "true");
                    }
            		
                    map.put("EndItem", "Yes");
                    listReturn.add(map);
                }
            }
        }

		for (int i = 0; i < iSpareSubAltSize; i++) {
			map = (Map) spareSubAltPartList.get(i);

            strLevel = getStringValue(map, "level");
            map.put("objectLevel", strLevel);

            listReturn.add(map);
        }

		return listReturn;
	}
	
	private int getListSize(List list) {
		return list == null ? 0 : list.size();
	}
	
	private StringList getDataForThisKey(MapList list, String key) {
		int size = getListSize(list);

		StringList listReturn = new StringList(size);

		String strTemp;

		for (int i = 0; i < size; i++) {
			strTemp = (String) ((Map) list.get(i)).get(key);
			if (!isValidData(strTemp)) {
				strTemp = "";
			}
			listReturn.addElement(strTemp);
		}

		return listReturn;

	}
	
	@SuppressWarnings("rawtypes")
	private String getStringValue(Map map, String key) {
		return (String) map.get(key);
	}
	private boolean isValidData(String data) {
		return ((data == null || "null".equals(data)) ? 0 : data.trim().length()) > 0;
	}
	
	private boolean isFNAndRefDesFilterPassed(Map map, String refDes, String findNumber) {
		boolean boolRefDesFilterPass = true;
		boolean boolFNFilterPass = true;

		String strRefDes = getStringValue(map, DomainConstants.SELECT_ATTRIBUTE_REFERENCE_DESIGNATOR);
		String strFindNumber = getStringValue(map, DomainConstants.SELECT_ATTRIBUTE_FIND_NUMBER);

		if (isValidData(refDes) && !refDes.equals(strRefDes)) {
            boolRefDesFilterPass = false;
        }

        if (isValidData(findNumber) && !findNumber.equals(strFindNumber)) {
            boolFNFilterPass = false;
        }

		return (boolRefDesFilterPass && boolFNFilterPass);
	}
	//[Google Custom]: PartWhereUsed Report in CSV Format - Created by Subbu on 05/15/2018 - Ends
	
	// [Google Custom]: 33809570 - Enovia Check Report(Specs and MPNs state) - Created by Syed on 20/07/2018 - Start
	/**
	 * To generate Enovia Check Report
	 * @return String
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */

	public File generateEnoviaCheckReport(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/04/2018 -- Start
		String objectId = (String) paramMap.get("objectId");
		String selObjId = (String) paramMap.get("selectedIds");
		StringList objectSelects = new StringList();
		MapList finalEbomList = new MapList();
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googEnoviaCheckReportMapping");
		String headers = pageInfo.get("EBOMCheck.Report.Header");
		objectSelects = createSelectStatements(context, pageInfo, headers).get("objectSelect");
		objectSelects.add(DomainObject.SELECT_ID);
		StringList relSelects = createSelectStatements(context, pageInfo, headers).get("relationSelect");
		StringList selectedList = FrameworkUtil.split(selObjId, ",");
		for (int i = 0; i < selectedList.size(); i++) {
			Map topPartInfo = new HashMap();
			String objId = ((String) selectedList.get(i)).trim();
			DomainObject partObj = DomainObject.newInstance(context, objId);
			topPartInfo = partObj.getInfo(context, objectSelects);
			topPartInfo.put("level", "0");
			topPartInfo.put(SELECT_ATTRIBUTE_GOOGLE_REFERENCE_DESIGNATOR, "");
			MapList ebomList = partObj.getRelatedObjects(context, RELATIONSHIP_EBOM, TYPE_PART, objectSelects,
					relSelects, false, true, (short) 0, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, 0);
			finalEbomList.add(topPartInfo);
			finalEbomList.addAll(ebomList);

		}

		File outPutcsv = generateEnoviaCheckCSVFile(context, objectId,
				getFormatedEnoviaCheckList(context, finalEbomList), pageInfo);
		//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/04/2018 -- End

		return outPutcsv;
	}
	
	//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/03/2018 -- Start
	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */

	public MapList getFormatedEnoviaCheckList(Context context, MapList ebomList) {
		MapList formattedList = new MapList();
		Iterator objItr = ebomList.iterator();
		try {
			while (objItr.hasNext()) {
				Map details = (Map) objItr.next();
				MapList specMEPList = updateMEPandSpecInfo(context, details);
				formattedList.addAll(specMEPList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formattedList;
	}

	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */

	public MapList updateMEPandSpecInfo(Context context, Map details) {
		String partObjId = (String) details.get(SELECT_ID);
		String finalspecInfoVal = "";
		String objInfoVal = "";
		String finalmepInfoVal = "";
		MapList specList = new MapList();
		MapList mepList = new MapList();
		MapList finalList = new MapList();
		MapList specMEPList = new MapList();
		// String mepInfoVal = "";
		StringList objectSelects = new StringList();
		objectSelects.addElement(DomainConstants.SELECT_TYPE);
		objectSelects.addElement(DomainConstants.SELECT_NAME);
		objectSelects.addElement(DomainConstants.SELECT_ID);
		objectSelects.addElement(DomainConstants.SELECT_REVISION);
		objectSelects.addElement(DomainConstants.SELECT_CURRENT);
		Pattern relPattern = new Pattern(RELATIONSHIP_PART_SPECIFICATION);
		relPattern.addPattern(RELATIONSHIP_MANUFACTURER_EQUIVALENT);
		try {
			if (UIUtil.isNotNullAndNotEmpty(partObjId)) {
				DomainObject partObj = new DomainObject(partObjId);

				MapList objectList = partObj.getRelatedObjects(context, relPattern.getPattern(), // String relPattern
						DomainObject.QUERY_WILDCARD, // String typePattern
						objectSelects, // StringList objectSelects,
						null, // StringList relationshipSelects,
						false, // boolean getTo,
						true, // boolean getFrom,
						(short) 1, // short recurseToLevel,
						"", // String objectWhere,
						"", // String relationshipWhere,
						0);
				Iterator<Map> objItr = objectList.iterator();
				while (objItr.hasNext()) {
					Map objMap = objItr.next();
					String relationship = (String) objMap.get("relationship");

					if (relationship.equals(RELATIONSHIP_PART_SPECIFICATION)) {
						specList.add(objMap);
					} else if (relationship.equals(RELATIONSHIP_MANUFACTURER_EQUIVALENT)) {
						mepList.add(objMap);
					}
				}

				finalList = (specList.size() > mepList.size()) ? specList : mepList;

				if (finalList.size() == 0) {
					details.put("PartSpecInfoName", "");
					details.put("PartSpecInfoState", "");
					details.put("PartSpecInfoType", "");
					details.put("PartSpecInfoRevision", "");
					details.put("PartMEPInfoName", "");
					details.put("PartMEPInfoState", "");
					specMEPList.add(details);
				} else {
					for (int i = 0; i < finalList.size(); i++) {
						Map ebomInfo = new HashMap();
						ebomInfo.putAll(details);
						if (specList.size() > i) {
							Map specListInfo = (Map) specList.get(i);
							String objSpecType = (String) specListInfo.get(DomainConstants.SELECT_TYPE);
							String objSpecName = (String) specListInfo.get(DomainConstants.SELECT_NAME);
							String objSpecRevision = (String) specListInfo.get(DomainConstants.SELECT_REVISION);
							String objSpecState = (String) specListInfo.get(DomainConstants.SELECT_CURRENT);
							ebomInfo.put("PartSpecInfoName", objSpecName);
							ebomInfo.put("PartSpecInfoState", objSpecState);
							ebomInfo.put("PartSpecInfoType", objSpecType);
							ebomInfo.put("PartSpecInfoRevision", objSpecRevision);
						} else {
							ebomInfo.put("PartSpecInfoName", "");
							ebomInfo.put("PartSpecInfoState", "");
							ebomInfo.put("PartSpecInfoType", "");
							ebomInfo.put("PartSpecInfoRevision", "");
						}

						if (mepList.size() > i) {
							Map mepListInfo = (Map) mepList.get(i);
							String objMEPName = (String) mepListInfo.get(DomainConstants.SELECT_NAME);
							String objMEPState = (String) mepListInfo.get(DomainConstants.SELECT_CURRENT);
							ebomInfo.put("PartMEPInfoName", objMEPName);
							ebomInfo.put("PartMEPInfoState", objMEPState);
						} else {
							ebomInfo.put("PartMEPInfoName", "");
							ebomInfo.put("PartMEPInfoState", "");
						}
						specMEPList.add(ebomInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return specMEPList;
	}
	//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/03/2018 -- End
	
	//[Google Custom] : Enovia Check Report Issue Modified by Subbu on 24/08/2018 -- Start
	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public File generateEnoviaCheckCSVFile(Context context, String objectId, MapList ebomList,
			Map<String, String> pageInfo) throws IOException, MatrixException {
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Subbu on 24/08/2018 -- Start 
		googCustomFunctions_mxJPO googCustomFunctions = new googCustomFunctions_mxJPO(); 
		ebomList= googCustomFunctions.formatMaplistStringValues(ebomList);
		//[Google Custom]: Modified to overcome issue with Report extract if any cell contains ',' or '/n' by Subbu on 24/08/2018 -- End 
		
		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		String filePath = context.createWorkspace();
		String headers = pageInfo.get("EBOMCheck.Report.Header");
		StringList objList = new StringList(SELECT_ID);
		objList.addElement(SELECT_NAME);
		objList.addElement(SELECT_REVISION);
		Map partInfo = DomainObject.newInstance(context, objectId).getInfo(context, objList);
		String name = (String) partInfo.get(SELECT_NAME);
		String revision = (String) partInfo.get(SELECT_REVISION);
		StringBuilder fileName = new StringBuilder(name);
		fileName.append("_").append(revision).append("_").append(fileCreateTimeStamp).append(".csv");
		filePath = filePath + "\\" + fileName.toString();
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/04/2018 -- Start
			bw.write(headers);
			bw.write(NEW_LINE_SEPARATOR);
			//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/04/2018 -- End
			Iterator itr = ebomList.iterator();
			while (itr.hasNext()) {
				Map ebomDetails = (Map) itr.next();
				String id = (String) ebomDetails.get(DomainObject.SELECT_ID);
				String rowDetails = createEnoviaCheckRowDetails(ebomDetails, pageInfo);
				bw.write(rowDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		return file;
	}
	//[Google Custom] : Enovia Check Report Issue Modified by Subbu on 24/08/2018 -- End
	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public String createEnoviaCheckRowDetails(Map ebomDetails, Map<String, String> pageInfo) {
		String headers = pageInfo.get("EBOMCheck.Report.Header");
		StringList headerNames = FrameworkUtil.split(headers, ",");
		Iterator itr = headerNames.iterator();
		StringBuilder fileWriter = new StringBuilder();
		try {
			while (itr.hasNext()) {
				String headerName = itr.next().toString().trim();
				headerName = headerName.replace(" ", "");
				String keyName = headerName;
				if (pageInfo.containsKey("relationship." + keyName)) {
					headerName = "relationship." + keyName;
				}
				String cellValue = "";
				String keyValue = pageInfo.get(headerName);
				if (pageInfo.containsKey(headerName)) {
					if (ebomDetails.containsKey(keyValue)) {
						cellValue = (String) ebomDetails.get(keyValue);
					}
					
					fileWriter.append(cellValue);
					fileWriter.append(COMMA_DELIMITER);
				} else {
					fileWriter.append(cellValue);
					fileWriter.append(COMMA_DELIMITER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileWriter.append(NEW_LINE_SEPARATOR);
		return fileWriter.toString();
	}
	// [Google Custom]: 33809570 - Enovia Check Report(Specs and MPNs state) - Created by Syed on 20/07/2018 - End

	/**
	 * This method used to get the End Item Structure
	 * 
	 * @param context
	 * @param ebomList
	 * @param endItemList
	 * @return MapList
	 * @author shajil
	 * @throws Exception
	 */
	public MapList getEndItemStructure(Context context, MapList ebomList, MapList endItemList, String topPartId)
			throws Exception {

		StringList selectStmts = new StringList(6);
		StringList selectRelStmts = new StringList(6);

		MapList sortEBOMList = new MapList();
		sortEBOMList.addAll(ebomList);

		MapList checkFindEndItemList = new MapList();

		Part partObject = new Part(topPartId);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(SELECT_PROJECT);
		selectStmts.addElement(DomainConstants.SELECT_REVISION);
		selectStmts.addElement(EngineeringConstants.SELECT_END_ITEM);
		selectStmts.addElement("attribute[" + ATTRIBUTE_MAKE_BUY + "]");
		selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_QUANTITY);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_FIND_NUMBER);
		selectRelStmts.addElement("from.id");

		MapList resultList = new MapList();
		MapList finalResultList = new MapList();
		MapList checkFinalResultList = new MapList();

		checkFindEndItemList = partObject.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relationship
				// pattern
				DomainConstants.TYPE_PART, // object pattern
				selectStmts, // object selects
				selectRelStmts, // relationship selects
				false, // to direction
				true, // from direction
				(short) 1, // recursion level
				null, null);

		Iterator checkFindEndItemItr = checkFindEndItemList.iterator();
		while (checkFindEndItemItr.hasNext()) {

			Map checkFindEndItemInfo = (Map) checkFindEndItemItr.next();
			String checkfindEndItemid = (String) checkFindEndItemInfo.get(SELECT_ID);
			DomainObject partSubObj = new DomainObject(checkfindEndItemid);

			resultList.clear();
			MapList findEndItemList = partSubObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relationship
					// pattern
					DomainConstants.TYPE_PART, // object pattern
					selectStmts, // object selects
					selectRelStmts, // relationship selects
					false, // to direction
					true, // from direction
					(short) 0, // recursion level
					null, null);

			findEndItemList.sort(SELECT_LEVEL, "descending", "String");

			MapList findEndItemCheckList = new MapList();
			findEndItemCheckList.addAll(findEndItemList);

			findEndItemCheckList.sort(SELECT_LEVEL, "descending", "String");

			Iterator findEndItemItr = findEndItemList.iterator();

			while (findEndItemItr.hasNext()) {

				Map findEndItemInfo = (Map) findEndItemItr.next();
				String findEndItemid = (String) findEndItemInfo.get(SELECT_ID);
				Iterator checkEndItemItr = endItemList.iterator();

				while (checkEndItemItr.hasNext()) {

					Map findEndItemDetails = (Map) checkEndItemItr.next();
					String parentId = (String) findEndItemDetails.get("parent");
					String endItemId = (String) findEndItemDetails.get("id");

					if (findEndItemid.equalsIgnoreCase(endItemId)) {

						DomainObject part = DomainObject.newInstance(context, parentId);
						MapList endItemStructure = part.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relationship
																														// pattern
								DomainConstants.TYPE_PART, // object pattern
								selectStmts, // object selects
								selectRelStmts, // relationship selects
								false, // to direction
								true, // from direction
								(short) 0, // recursion level
								null, null, 0); // relationship where clause

						endItemStructure.sort(SELECT_LEVEL, "descending", "String");

						Iterator endItemItr = endItemStructure.iterator();
						int endItemLevel = 0;
						String parentPartId = null;
						boolean containsEndItem = false;
						MapList sortresultTempList = new MapList();
						while (endItemItr.hasNext()) {
							Map endItemInfo = (Map) endItemItr.next();
							String id = (String) endItemInfo.get(SELECT_ID);
							String level = (String) endItemInfo.get("level");
							if (id.equals(endItemId) && !containsEndItem) {
								endItemLevel = Integer.parseInt(level);
								endItemInfo.put("EndItem", "Yes");
								sortresultTempList.add(endItemInfo);
								parentPartId = (String) endItemInfo.get("from.id");
								containsEndItem = true;
							} else if (UIUtil.isNotNullAndNotEmpty(parentPartId) && parentPartId.equals(id)) {
								endItemInfo.put("EndItem", "No");
								sortresultTempList.add(endItemInfo);
								parentPartId = (String) endItemInfo.get("from.id");
							}
						}

						MapList sortTempList = new MapList();
						int count = 0;
						int flaged = 0;
						while (count == 0) {
							count = 1;
							Iterator findEbomItr = findEndItemCheckList.iterator();
							while (findEbomItr.hasNext()) {
								Map findEbomDetails = (Map) findEbomItr.next();
								String partId = (String) findEbomDetails.get(SELECT_ID);
								MapList tempList = new MapList();
								if (partId.equals(parentId)) {
									count = 0;
									flaged++;
									// resultList.addAll(sortTempList);
									sortTempList.clear();
									findEbomDetails.put("level", "-");
									findEbomDetails.put("EndItem", "No");
									sortTempList.add(findEbomDetails);
									String topParentPartId = (String) findEbomDetails.get("from.id");
									tempList.add(findEbomDetails);
									findEndItemCheckList.removeAll(tempList);
									sortEBOMList.sort(SELECT_LEVEL, "descending", "String");
									Iterator sortEbomItr = sortEBOMList.iterator();
									while (sortEbomItr.hasNext()) {
										Map sortEbomDetails = (Map) sortEbomItr.next();
										String eachId1 = (String) sortEbomDetails.get(SELECT_ID);
										if (UIUtil.isNotNullAndNotEmpty(topParentPartId)
												&& topParentPartId.equals(eachId1)) {
											sortEbomDetails.put("level", "-");
											sortEbomDetails.put("EndItem", "No");
											sortTempList.add(sortEbomDetails);
											topParentPartId = (String) sortEbomDetails.get("from.id");
										}
									}

									break;
								}
							}
						}

						if (checkfindEndItemid.equals(parentId)) {
							flaged++;
							sortTempList.clear();
							checkFindEndItemInfo.put("EndItem", "No");
							sortTempList.add(checkFindEndItemInfo);
						}

						if (UIUtil.isNotNullAndNotEmpty(topPartId)) {
							Map topPartInfo;
							try {
								topPartInfo = DomainObject.newInstance(context, topPartId).getInfo(context,
										selectStmts);
								topPartInfo.put("EndItem", "No");
								topPartInfo.put("level", "Root Node");
								if (flaged > 0) {
									resultList.clear();
									resultList.addAll(sortresultTempList);
									resultList.addAll(sortTempList);
									resultList.add(topPartInfo);
								}

							} catch (FrameworkException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
					}

				}
			}
			checkFinalResultList.addAll(resultList);
		}
		return checkFinalResultList;
	}
	
	/**
	 * To include the End Item, Result and LEvel infos in the report
	 * 
	 * @param prdDetails
	 * @param fileWriter
	 * @author Syed
	 */
	public void updateEndItemListReport(Map prdDetails, StringBuilder fileWriter) {
		String endItem = (String) prdDetails.get("EndItem");
		String level = (String) prdDetails.get("level");
		String result = (String) prdDetails.get("Result");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(endItem);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(level);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(result);
	}
	
	/**
	 * To update ownership details of Part
	 * 
	 * @param prdDetails
	 * @param fileWriter
	 * @author Syed
	 */
	public boolean updateMultipleOwnership(Context context, String objectId, String collabSpace) throws Exception {
		try {
			String defaultAccess = "";
			StringList accessNames = DomainAccess.getLogicalNames(context, objectId);
			defaultAccess = (String) accessNames.get(0);
			DomainAccess.createObjectOwnership(context, objectId, "Google", collabSpace, defaultAccess,
					DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	// [Google Custom]: - End Item Report generation and ownership update- Modified by Syed on 24/07/2018 - End	
	
	// [Google Custom]: #33753674: Added method to modify the Rel Attr 'Design
	// Collaboration' to FALSE for all those parts not connected to Phyical Product
	// -- Modified by Subbu -Start
	/**
	 * This method is used to fetch the EBOM Relationship Id and IsVPMVisible
	 * attribute value
	 * 
	 * @param context
	 * @param strPartOID
	 * @throws Exception
	 * @author Subbu
	 * @param strpartName
	 * @param strpartRev
	 */
	public MapList getEBOMRel(Context context, String strPartOID, String strpartName, String strpartRev, String strpartType)
			throws Exception {

		StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
		objectSelects.addElement(DomainConstants.SELECT_NAME);
		objectSelects.addElement(DomainConstants.SELECT_REVISION);

		StringList relSelects = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		relSelects.addElement(SELECT_ATTRIBUTE_ISVPMVISIBLE);

		String relWhere = ""+SELECT_ATTRIBUTE_ISVPMVISIBLE+"==TRUE";

		MapList mlPartEBOMList = new MapList();
		try {
			DomainObject domObjPart = new DomainObject(strPartOID);

			mlPartEBOMList = domObjPart.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relationship
																										// pattern
					DomainConstants.TYPE_PART, // object pattern
					objectSelects, // object selects
					relSelects, // relationship selects
					true, // to direction
					false, // from direction
					(short) 1, // recursion level
					null, relWhere, 0); // relationship where clause
			if (mlPartEBOMList.size() > 0) {
				Iterator<Map> partitr = mlPartEBOMList.iterator();
				while (partitr.hasNext()) {
					Map mpPartInfo = (Map) partitr.next();
					mpPartInfo.put("PART_NAME", strpartName);
					mpPartInfo.put("PART_REV", strpartRev);
					mpPartInfo.put("PART_ID", strPartOID);
					mpPartInfo.put("PART_TYPE", strpartType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mlPartEBOMList;
	}

	/**
	 * This method used to update Part details with EBOM Rel Attribute 'Design Collaboration' values 
	 * @param ebomDetails
	 * @param fileWriter
	 * @throws IOException
	 * @author Subbu
	 */
	public void updateDesignCollaborationReport(Map ebomDetails, StringBuilder fileWriter) throws IOException {
		String strEBOMRel = (String) ebomDetails.get("id[connection]");
		String strDesignCollabValue = (String) ebomDetails.get(SELECT_ATTRIBUTE_ISVPMVISIBLE);
		String strPartName = (String) ebomDetails.get("PART_NAME");
		String strPartRev = (String) ebomDetails.get("PART_REV");
		String strPartID = (String) ebomDetails.get("PART_ID");
		String strPartType = (String) ebomDetails.get("PART_TYPE");
		fileWriter.append(strPartType);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strPartName);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strPartRev);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strPartID);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strEBOMRel);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(strDesignCollabValue);
	}
	
	/**
	 * This method used to update EBOM Rel Attribute 'Design Collaboration' values
	 * 
	 * @param ebomDetails
	 * @throws IOException
	 * @author Subbu
	 */
	private void updateEBOMRel(Context context, MapList mlEBOMList) throws Exception {
		String strEBOMRelID = null;
		String strPartId = null;
		String strDesignCollaborationValue = "FALSE";
		String FilePath = "C:\\temp\\";
		File fpSucessLog = new File(FilePath + "SucessfullyModified_" + strTimeStamp + ".log");
		bwSucessLog = new BufferedWriter(new FileWriter(fpSucessLog.getAbsoluteFile(), false));
		File fpFailureLog = new File(FilePath + "Failed_" + strTimeStamp + ".log");
		bwFailureLog = new BufferedWriter(new FileWriter(fpFailureLog.getAbsoluteFile(), false));
		HashMap attrMap = new HashMap();
		try {
			MqlUtil.mqlCommand(context, "trigger off");
			if (mlEBOMList.size() > 0) {
				Iterator<Map> partitr = mlEBOMList.iterator();
				while (partitr.hasNext()) {
					Map mpPartInfo = (Map) partitr.next();
					strEBOMRelID = (String) mpPartInfo.get("id[connection]");
					strPartId = (String) mpPartInfo.get("PART_ID");
					attrMap.put(ATTRIBUTE_ISVPMVISIBLE, strDesignCollaborationValue);
					// update Attribute Design Collaboration value as FALSE on EBOM Relationship 
					try {						
							DomainRelationship.setAttributeValues(context, strEBOMRelID, attrMap);
							writeLog(bwSucessLog, "Rel Id " + strEBOMRelID + " is sucessfully modified for attribute "
									+ ATTRIBUTE_ISVPMVISIBLE + " with value " + strDesignCollaborationValue + "");
					} catch (Exception e) {
						writeLog(bwFailureLog, "Rel Id " + strEBOMRelID + " Failed to modify for attribute "
								+ ATTRIBUTE_ISVPMVISIBLE + " with value " + strDesignCollaborationValue + "");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MqlUtil.mqlCommand(context, "trigger on");
			bwFailureLog.close();
			bwSucessLog.close();
		}
	}
	
	/**
	 * This method used to Write information to the Log Files
	 * @param strMessage
	 * @param bw
	 * @throws Exception
	 * @author Subbu
	 */
	public static void writeLog(BufferedWriter bw,String strMessage) throws Exception
	{

		try {
			bw.write("["+simpleDateFormat.format(new Date()) + "] : " + strMessage+ "\r\n");
			bw.flush();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on
	// 10/05/2018 - Start
	/**
	 * This method used update Build Event attribute value
	 * 
	 * @return void
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public void migrateBuildEventAttribute(Context context, String[] args) throws Exception {

		try {

			String changeOID = "";
			String buildEventValue = "";
			String newValue = "";
			String filePath = "c:\\Temp\\CA_BUILDEVENT_CSV_Report.csv";
			StringList objectSelects = new StringList(DomainObject.SELECT_ID);
			objectSelects.add(DomainObject.SELECT_TYPE);
			objectSelects.add(DomainObject.SELECT_NAME);
			objectSelects.add(DomainObject.SELECT_REVISION);
			objectSelects.add(SELECT_ATTRIBUTE_BUILD_EVENT);

			/*
			 * String objectWhere = "(current!= '" + STATE_CHANGEACTION_COMPLETE + "') && ("
			 * + SELECT_ATTRIBUTE_BUILD_EVENT + "=='Beta 1.1' || " +
			 * SELECT_ATTRIBUTE_BUILD_EVENT + "=='Beta 1.2' || " +
			 * SELECT_ATTRIBUTE_BUILD_EVENT + "=='Production' || " +
			 * SELECT_ATTRIBUTE_BUILD_EVENT + "~~'*,*')";
			 */
			String objectWhere = "(" + SELECT_ATTRIBUTE_BUILD_EVENT + "=='Beta 1.1' || " + SELECT_ATTRIBUTE_BUILD_EVENT
					+ "=='Beta 1.2' || " + SELECT_ATTRIBUTE_BUILD_EVENT + "=='Production' || "
					+ SELECT_ATTRIBUTE_BUILD_EVENT + "~~'*,*')";

			MapList changeActionList = DomainObject.findObjects(context, TYPE_CHANGEACTION, // Type
					DomainObject.QUERY_WILDCARD, // name
					DomainObject.QUERY_WILDCARD, // revision
					DomainObject.QUERY_WILDCARD, // owner
					DomainObject.QUERY_WILDCARD, // Vault
					objectWhere, // Where
					false, // expand sub type
					objectSelects);

			Iterator<Map> changeItr = changeActionList.iterator();
			while (changeItr.hasNext()) {
				Map mapChangeInfo = (Map) changeItr.next();
				changeOID = (String) mapChangeInfo.get(DomainObject.SELECT_ID);
				buildEventValue = (String) mapChangeInfo.get(SELECT_ATTRIBUTE_BUILD_EVENT);

				if (UIUtil.isNotNullAndNotEmpty(buildEventValue)) {
					if (buildEventValue.contains("Beta 1.1")) {
						buildEventValue = buildEventValue.replace("Beta 1.1", "V5 Beta 1.1");
					}
					if (buildEventValue.contains("Beta 1.2")) {
						buildEventValue = buildEventValue.replace("Beta 1.2", "V5 Beta 1.2");
					}
					if (buildEventValue.contains("Production")) {
						buildEventValue = buildEventValue.replace("Production", "V5 Production");
					}
					if (buildEventValue.contains("V5 V5")) {
						buildEventValue = buildEventValue.replace("V5 V5", "V5");
					}
					if (buildEventValue.contains("Liberty V5 Production")) {
						buildEventValue = buildEventValue.replace("Liberty V5 Production", "Liberty Production");
					}
					newValue = buildEventValue;
				}

				MQLCommand mqlCommand = new MQLCommand();
				try {
					mqlCommand.executeCommand(context, false, true, "mod bus $1 $2 $3", changeOID,
							ATTRIBUTE_BUILD_EVENT, newValue);
					mapChangeInfo.put("New Attribute Value", newValue);
					mapChangeInfo.put("Status", "Success");

				} catch (Exception e) {
					e.printStackTrace();
					mapChangeInfo.put("New Attribute Value", newValue);
					mapChangeInfo.put("Status", "Failed");

				}
			}
			createCSVFile(filePath, changeActionList, "buildEventReport");
			//System.out.println("-----------------------Operation Completed--------------------");
			//System.out.println(
			//		"-----------------------Please check the logs in C:\\Temp\\CA_BUILDEVENT_CSV_Report.csv--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method used update Build Event attribute value
	 * 
	 * @return void
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public void updatebuildEventDetails(Map ebomDetails, StringBuilder fileWriter) {
		String oldBuildEventValue = (String) ebomDetails.get(SELECT_ATTRIBUTE_BUILD_EVENT);
		String newBuildEventValue = (String) ebomDetails.get("New Attribute Value");
		String status = (String) ebomDetails.get("Status");
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(oldBuildEventValue);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(newBuildEventValue);
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(status);
	}
	
	/**
	 * This method used to update the page Info googEBOMReportMapping
	 * @param context
	 * @param ebomList
	 * @param pageInfo
	 * @return
	 * @author Syed
	 */
	public String updatePageInfoData(StringList headersList, StringList excludedColumnsList,
			Map<String, String> pageInfo, boolean formatList) {
		int headersListSize = headersList.size();
		String headers = "";
		String formatedReplaceColumns = "";
		StringBuilder headerBuilder = new StringBuilder();
		StringList excludedStringList = new StringList();
		String headerColumns = "";
		String columnInfo = "";

		if (formatList) {
			pageInfo.put("changeInfo", "true");
			pageInfo.put("cadInfo", "true");
			for (int i = 0; i < excludedColumnsList.size(); i++) {
				columnInfo = (String) excludedColumnsList.get(i);
				excludedStringList.add(columnInfo.replaceAll("\\s+", ""));
				if ("Change".equals(columnInfo) || "Change State".equals(columnInfo)) {
					pageInfo.put("changeInfo", "false");
				}
				if ("CAD Maturity".equals(columnInfo) || "CAD Version".equals(columnInfo)) {
					pageInfo.put("cadInfo", "false");
				}
			}
			excludedColumnsList = excludedStringList;
		}
		for (int k = 0; k < headersListSize; k++) {
			headerColumns = (String) headersList.get(k);
			formatedReplaceColumns = headerColumns.replace("relationship.", "");

			if (!excludedColumnsList.contains(formatedReplaceColumns)) {
				headerBuilder.append(headerColumns);
				headerBuilder.append(",");
			}
		}

		if (headerBuilder.toString().endsWith(",")) {
			headers = headerBuilder.substring(0, headerBuilder.length() - 1);
			if (formatList) {
				pageInfo.put("Names.Formatted.List", headers);
			} else {
				pageInfo.put("EBOM.Report.Header", headers);
			}
		}

		return headers;
	}
	// [Google Custom]: Migration of Build Event Attribute - Modified by Syed on 10/05/2018 - End
	
	
		/**
	 * To generate Enovia Extract Report
	 * @return String
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Sourav
	 */

	public File generateEnoviaExtractReport(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String selObjId = (String) paramMap.get("selectedIds");
		StringList objectSelects = new StringList();
		MapList finalEbomList = new MapList();
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googEnoviaExtractReportMapping");
		String headers = pageInfo.get("EBOMCheck.Report.Header");
		objectSelects = createSelectStatements(context, pageInfo, headers).get("objectSelect");
		objectSelects.add(DomainObject.SELECT_ID);
		StringList relSelects = createSelectStatements(context, pageInfo, headers).get("relationSelect");
		relSelects.add(DomainRelationship.SELECT_ID);
		StringList selectedList = FrameworkUtil.split(selObjId, ",");
		for (int i = 0; i < selectedList.size(); i++) {
			Map topPartInfo = new HashMap();
			//Added by XPLORIA to fix the Level Issue Starts here
			MapList bomList = new MapList();
			//Added by XPLORIA to fix the Level Issue Ends here
			String objId = ((String) selectedList.get(i)).trim();
			DomainObject partObj = DomainObject.newInstance(context, objId);
			topPartInfo = partObj.getInfo(context, objectSelects);
			//Added by XPLORIA to fix the Level Issue Starts here
			topPartInfo.put("level", "1");
			//Added by XPLORIA to fix the Level Issue Ends here
			topPartInfo.put("attribute[Quantity]", "");
			topPartInfo.put("attribute[Component Location]", "");
			topPartInfo.put("attribute[Find Number]", "");
			topPartInfo.put("attribute[Reference Designator]", "");
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
      
			MapList ebomList = partObj.getRelatedObjects(context, RELATIONSHIP_EBOM, TYPE_PART, objectSelects,
					relSelects, false, true, (short) 0, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, 0);
			//Added by XPLORIA to fix the Level Issue Starts here		
			for(int iEBOM = 0;iEBOM<ebomList.size();iEBOM++){
				Map mpEBOM = (Map)ebomList.get(iEBOM);
				if (mpEBOM.containsKey("level")) {
					String steLevel = (String)mpEBOM.get("level");
					if(steLevel!=null && !steLevel.equals("")){
					  int iLevel=Integer.parseInt(steLevel);
					  iLevel = iLevel + 1;
					  String strLVL = Integer.toString(iLevel);
					  mpEBOM.put("level",strLVL);
					  bomList.add(mpEBOM);
				   }
				}
			}				
			//Added by XPLORIA to fix the Level Issue Ends here		
					
			finalEbomList.add(topPartInfo);
			//Added by XPLORIA to fix the Level Issue Starts here	
			finalEbomList.addAll(bomList);
			//Added by XPLORIA to fix the Level Issue Ends here	
			

		}
		//File outPutcsv = generateEnoviaCheckCSVFile(context, objectId,finalEbomList, pageInfo);
		
		File outPutcsv = generateEnoviaCheckCSVFile(context, objectId,getFormatedEnoviaExtractCheckList(context, finalEbomList), pageInfo);
		//[Google Custom] : Enovia Check Report Issue Modified by Syed on 10/04/2018 -- End

		return outPutcsv;
	}
	
	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Sourav : updateMEPandSpecInfo
	 */

	public MapList getFormatedEnoviaExtractCheckList(Context context, MapList ebomList) {
		MapList formattedList = new MapList();
		Iterator objItr = ebomList.iterator();
		try {
			while (objItr.hasNext()) {
				Map details = (Map) objItr.next();
				Map specMEPList = updateSpecificInfo(context, details);
				formattedList.add(specMEPList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return formattedList;
	}
	
	
	/**
	 * To generate Enovia Check Report
	 * 
	 * @return MapList
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Sourav
	 */

	public Map updateSpecificInfo(Context context, Map details) {
		String partObjId = (String) details.get(SELECT_ID);
		String partrelId = (String) details.get(DomainRelationship.SELECT_ID);
	
		StringList revisionList = new StringList();
		String strRevisionList = "";
		try {
			if (UIUtil.isNotNullAndNotEmpty(partObjId)) {
				
				   HashMap paramMap = new HashMap();
				  
				   MapList objectListVal = new MapList();
				   Map objectValMap = new HashMap();
				   objectValMap.put("id",partObjId);
				   objectListVal.add(objectValMap);
				   Map objectParamMap = new HashMap();
				   objectParamMap.put("exportFormat","CSV");
				   objectParamMap.put("isIndentedView","False");
        	       paramMap.put("objectList", objectListVal);
        	       paramMap.put("paramList", objectParamMap);
				   
				  
				   
				   String[] plcArgs = JPO.packArgs(paramMap);
				   enoFloatOnEBOMBase_mxJPO eno = new enoFloatOnEBOMBase_mxJPO(context,plcArgs);
				   revisionList = eno.showRevisionStatus(context, plcArgs);
				   

				   for(int indexRev=0; indexRev < revisionList.size(); indexRev++){
                       String strRevindex = (String)revisionList.get(indexRev);
					     if (!UIUtil.isNullOrEmpty(strRevindex)){
							 if (!UIUtil.isNullOrEmpty(strRevisionList)){
					           strRevisionList+= "," + strRevindex;
				               } else { 
							    strRevisionList = strRevindex;
							   }
						    }
                        }
					   
				    if (!UIUtil.isNullOrEmpty(strRevisionList)){
					   details.put("NotLastRevision", strRevisionList);
				      } else {
					   details.put("NotLastRevision", "");
				      }
        	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return details;
	}
	
}// end of class