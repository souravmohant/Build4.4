/*
 ** ${CLASSNAME}
 ** Added by Lalitha for ChromeExtension Phase2.
 ** Copyright (c) 1993-2015 Dassault Systemes. All Rights Reserved.
 */

import matrix.util.StringList;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import java.util.HashMap;
import java.util.Arrays;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import java.util.Map;
import com.matrixone.apps.domain.util.PersonUtil;
import java.util.Iterator;
import java.util.List;
import com.matrixone.apps.domain.DomainRelationship;
import java.util.Properties;
import matrix.db.Context;
import matrix.db.JPO;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.matrixone.json.JSONArray;
import com.matrixone.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeAction;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.dassault_systemes.enovia.changeaction.interfaces.IProposedChanges;
public class googChromeExtension_mxJPO 
{
	
	
	 public String getLibraryAttributesDetails (Context context, String[] args)throws Exception  {
		String returnAttrval 				= "";
		String strObjectId					= args[0];
		MapList listLocEquivMEPs 			= new MapList();
		StringList selectStmts 				= new StringList(6);
		String RELATIONSHIP_LOCATION_EQUIVALENT = PropertyUtil.getSchemaProperty("relationship_LocationEquivalent");
		String TYPE_LOCATION_EQUIVALENT_OBJECT = PropertyUtil.getSchemaProperty("type_LocationEquivalentObject");
		String sFromRelId           		= "from.relationship["+ RELATIONSHIP_LOCATION_EQUIVALENT +"].id";
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_TYPE);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(DomainConstants.SELECT_REVISION);
		selectStmts.addElement("to[Manufacturing Responsibility].from.name");
		selectStmts.addElement("to[Manufacturing Responsibility].from.id");
		String sEstimatedCost    			= PropertyUtil.getSchemaProperty(context,"attribute_EstimatedCost");
		String  strlatestDataSheetURL			= PropertyUtil.getSchemaProperty("attribute_IHSLatestDataSheetURL");
		String  strDataSheetHistoryURL			= PropertyUtil.getSchemaProperty("attribute_IHSDatasheetURL");
		StringBuffer sbRelPattern = new StringBuffer(RELATIONSHIP_LOCATION_EQUIVALENT);
		sbRelPattern.append(',');
		sbRelPattern.append(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
		StringBuffer typePattern = new StringBuffer(DomainConstants.TYPE_PART);
		StringList selectRelStmts 			= new StringList(2);
		selectRelStmts.addElement(sFromRelId);
		selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
		StringBuffer sbTypePattern = new StringBuffer(typePattern.toString());
		sbTypePattern.append(',');
		sbTypePattern.append(TYPE_LOCATION_EQUIVALENT_OBJECT);
		Map tempMap = null;
		Map tempMap1 = null;
		String strMEPName="";
		StringList objectSelects = new StringList();
						objectSelects.add(DomainConstants.SELECT_ID);
						objectSelects.add(DomainConstants.SELECT_NAME);
						objectSelects.add(DomainConstants.SELECT_POLICY);
		StringList selectables 					= new StringList();
		selectables.add("attribute["+strlatestDataSheetURL+"].value");
		selectables.add("attribute["+strDataSheetHistoryURL+"].value");
		selectables.add(DomainConstants.SELECT_NAME);
		selectables.add("attribute["+sEstimatedCost+"].value");
		
		
		String strMEPId="";
		DomainObject partObj 				= DomainObject.newInstance(context, strObjectId);
		Map objInfo 						= partObj.getInfo(context,objectSelects);
		
		String spolicy						= (String)objInfo.get(DomainConstants.SELECT_POLICY);
		String sMEPName						= (String)objInfo.get(DomainConstants.SELECT_NAME);
		String strEstimatedCostAttrValue	= "";
		 try {
				listLocEquivMEPs 			= partObj.getRelatedObjects(context, sbRelPattern
													.toString(), // relationship pattern
													sbTypePattern.toString(), // object pattern
													selectStmts, // object selects
													selectRelStmts, // relationship selects
													false, // to direction
													true, // from direction
													(short) 2, // recursion level
													null, // object where clause
													null); // rela
				String strReturnHTML="";		
				
				int iMEPsCount=0;
				if (listLocEquivMEPs.size()>0) {
					//strReturnHTML+= "<table border='0' class='list'>";
					//strReturnHTML += "<tr>";
					//strReturnHTML += "<th >Property</th>";
				for (int i = 0; i < listLocEquivMEPs.size(); i++) {
					tempMap = (Map) listLocEquivMEPs.get(i);
					iMEPsCount=iMEPsCount+1;
					strMEPName=(String)tempMap.get(DomainConstants.SELECT_NAME);
					
					//strReturnHTML += "<th >"+"MEP "+String.valueOf(iMEPsCount)+"</th>";
				}
				if(iMEPsCount>0) {
					//strReturnHTML += "</tr>";
					strReturnHTML += "<tr>";
					strReturnHTML += "<td  ><b>MEP Name</b></td>";
					for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
						tempMap 					= (Map) listLocEquivMEPs.get(i1);
						strMEPName					=(String)tempMap.get(DomainConstants.SELECT_NAME);
						strReturnHTML 				+= "<td  >";
						strReturnHTML 				+=strMEPName;
						strReturnHTML 				+= "</td>";
						
					}
					strReturnHTML += "</td>"; 
					 strReturnHTML += "</tr>";
					strReturnHTML += "<tr>";
					strReturnHTML += "<td  ><b>IHS Datasheet URL (latest)</b></td>";
					for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
						String sFirstValue			= "";
						String sSecondValue			= "";
						int strsubDataSheetValue;
						tempMap 					= (Map) listLocEquivMEPs.get(i1);
						strMEPId					= (String)tempMap.get(DomainConstants.SELECT_ID);
						DomainObject doMEP 			= DomainObject.newInstance(context, strMEPId);
						Map issueDetails 			= doMEP.getInfo(context, selectables);
						String sDataSheet			= (String) issueDetails.get("attribute["+strlatestDataSheetURL+"].value");
						if (!UIUtil.isNullOrEmpty(sDataSheet)) {
							strsubDataSheetValue		= sDataSheet.indexOf("https");
							sFirstValue					= sDataSheet.substring(0,strsubDataSheetValue);
							sFirstValue 	   			= sFirstValue.replaceAll(":","");
							sFirstValue 	   			= sFirstValue.replaceAll("\n","");
							sSecondValue			    = sDataSheet.substring(strsubDataSheetValue,sDataSheet.length());
						}
						strReturnHTML += "<td>";
						 if (!UIUtil.isNullOrEmpty(sFirstValue) && !UIUtil.isNullOrEmpty(sSecondValue)) {
							 strReturnHTML += "<a style='color: #000000' href ="+sSecondValue+" target=_blank>"+sFirstValue+"</a><a href='javascript:showhistory();'><img border='0' src='../common/images/chromemore.png' title='History'/></a><input type='hidden' id='hiddenMEPId' value='"+strMEPId+"'/>";
						} 
					}
					  strReturnHTML += "</td>"; 
					strReturnHTML += "</tr>";
					 MapList allfieldMapList=new MapList();
					for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
						tempMap = (Map) listLocEquivMEPs.get(i1);
						strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
						DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
						

						MapList classificationList = doMEP.getRelatedObjects(context,
							"Classified Item",
							"*",
							objectSelects,
							null,
							true,//boolean getTo,
							false,//boolean getFrom,
							(short)0,
							null,
							null,
							0);
						int noOfClasses = classificationList.size();
						MapList fieldMapList=new MapList();
						MapList classificationAttributesList = new MapList();
						if(noOfClasses>0){
							Iterator itr = classificationList.iterator();
							while(itr.hasNext()){
								Map classMap = (Map)itr.next();
								MapList classificationAttributes = XORG_Util_mxJPO.getClassClassificationAttributes(context, (String)classMap.get(DomainConstants.SELECT_ID));
								 if(classificationAttributes.size()>0){
									HashMap classificationAttributesMap = new HashMap();
									classificationAttributesMap.put("className", classMap.get(DomainConstants.SELECT_NAME));
									classificationAttributesMap.put("attributes", classificationAttributes);

									classificationAttributesList.add(classificationAttributesMap);
								}
							}
							fieldMapList=getDynamicFieldsMapList(context,classificationAttributesList,false);
							allfieldMapList.addAll(fieldMapList);
						}
					}
					Map attributelistMap = new HashMap();
					for (int jj = 0; jj < allfieldMapList.size(); jj++) {
						Map tMap = (Map) allfieldMapList.get(jj);
						attributelistMap.put((String)tMap.get("name"),(String)tMap.get("label"));
					}
					
					allfieldMapList= new MapList();
					MapList FirstMapList= new MapList();
					MapList SecondMapList= new MapList();
					Iterator<Map.Entry<String, String>> itr = attributelistMap.entrySet().iterator(); 
                    String sattribute 				= "Alternate Symbols|Foot Print Reference|JEDEC Type|Library Reference";
					String[] arrOfStr 	= sattribute.split("\\|");
					List<String> list = Arrays.asList(arrOfStr);
					while(itr.hasNext()) 
					{ 
						Map.Entry<String, String> entry = itr.next(); 
						Map hMap = new HashMap();
						hMap.put("name",entry.getKey());
						hMap.put("label",entry.getValue());
						if(list.contains(entry.getValue())) {
							FirstMapList.add(hMap);
						} else {
							SecondMapList.add(hMap);
						}
					} 
					allfieldMapList.addAll(FirstMapList);
					allfieldMapList.addAll(SecondMapList);
					for (int j2 = 0; j2 < allfieldMapList.size(); j2++) {
						tempMap = (Map) allfieldMapList.get(j2);
						String strMEPClassifiedAttrLabel=(String)tempMap.get("label");
						if (!strMEPClassifiedAttrLabel.equals("IHS Datasheet URL (latest) ") && !strMEPClassifiedAttrLabel.equals("IHS Datasheet history")) {
							String strMEPClassifiedAttrName=(String)tempMap.get("name");
							strReturnHTML += "<tr>";
							strReturnHTML += "<td  ><b>"+strMEPClassifiedAttrLabel+"</b></td>";

							for (int i4 = 0; i4 < listLocEquivMEPs.size(); i4++) {
							tempMap = (Map) listLocEquivMEPs.get(i4);
							strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
							DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
							String strIHSAttrValue		= doMEP.getInfo(context, "attribute["+strMEPClassifiedAttrName+"].value");
							String sLatestUrl 			= PropertyUtil.getSchemaProperty(context,"attribute_IHSLatestDataSheetURL");
							String sdataUrl 			= PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL");
							if(strMEPClassifiedAttrLabel.equals("Foot Print Reference") && strIHSAttrValue.equals("")) {
								strReturnHTML += "<td>No Foot Print</td>";	
							} else if (strMEPClassifiedAttrLabel.equals("Library Reference") && strIHSAttrValue.equals("")){
								strReturnHTML += "<td>No Library Reference</td>";	
							} else {
							strReturnHTML += "<td  >"+strIHSAttrValue+"</td>";						
							}
												
							}
							strReturnHTML += "</tr>";
						}
						
					}
				}
				strReturnHTML += "<tr>";
				strReturnHTML 				+= "<td  ><b>Estimated Cost($)</b></td>";
				for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
					tempMap 					= (Map) listLocEquivMEPs.get(i1);
					strMEPId					=(String)tempMap.get(DomainConstants.SELECT_ID);
					DomainObject dmMEPobj		= new DomainObject(strMEPId);
					strEstimatedCostAttrValue	= dmMEPobj.getAttributeValue(context,sEstimatedCost);
					strReturnHTML 				+= "<td>"+strEstimatedCostAttrValue+"</td>";

				}
				strReturnHTML += "</tr>";
				returnAttrval = strReturnHTML;
				} else {
					String sFirstValue			= "";
					String sSecondValue			= "";
					int strsubDataSheetValue;
					MapList allfieldMapList1=new MapList();
					DomainObject doMEP1 = DomainObject.newInstance(context, strObjectId);
					Map issueDetails 			= doMEP1.getInfo(context, selectables);
					String strMEPName1			= (String) issueDetails.get(DomainConstants.SELECT_NAME);
					String sDataSheet			= (String) issueDetails.get("attribute["+strlatestDataSheetURL+"].value");
					String sHistory				= (String) issueDetails.get("attribute["+strDataSheetHistoryURL+"].value");
					strEstimatedCostAttrValue	= (String) issueDetails.get("attribute["+sEstimatedCost+"].value");
					
					MapList classificationList1 = doMEP1.getRelatedObjects(context,
							"Classified Item",
							"*",
							objectSelects,
							null,
							true,//boolean getTo,
							false,//boolean getFrom,
							(short)0,
							null,
							null,
							0);
					if (classificationList1.size()>0) {	
						int noOfClasses1 = classificationList1.size();
						MapList fieldMapList1=new MapList();
						MapList classificationAttributesList1 = new MapList();
						if(noOfClasses1>0){
							Iterator itr = classificationList1.iterator();
							while(itr.hasNext()){
								Map classMap1 = (Map)itr.next();
								MapList classificationAttributes1 = XORG_Util_mxJPO.getClassClassificationAttributes(context, (String)classMap1.get(DomainConstants.SELECT_ID));
								 if(classificationAttributes1.size()>0){
									HashMap classificationAttributesMap1 = new HashMap();
									classificationAttributesMap1.put("className", classMap1.get(DomainConstants.SELECT_NAME));
									classificationAttributesMap1.put("attributes", classificationAttributes1);

									classificationAttributesList1.add(classificationAttributesMap1);
								}
							}
							fieldMapList1=getDynamicFieldsMapList(context,classificationAttributesList1,false);
							allfieldMapList1.addAll(fieldMapList1);
						}
						//strReturnHTML+= "<table width='100%' cellpadding='0' cellspacing='0'";
						//strReturnHTML += "<tr>";
						//strReturnHTML += "<th >Property</th>";
						//strReturnHTML += "<th  >MEP1</th>";
						//strReturnHTML += "</tr>";
						strReturnHTML += "<tr>";
						strReturnHTML += "<td ><b>MEP Name</b></td>";
						strReturnHTML += "<td >";
						strReturnHTML +=strMEPName1;
						strReturnHTML += "</td>";
						strReturnHTML += "</tr>";
						strReturnHTML += "<tr>";
						strReturnHTML += "<td  ><b>IHS Datasheet URL (latest)</b></td>";
						if (!UIUtil.isNullOrEmpty(sDataSheet)) {
							strsubDataSheetValue		= sDataSheet.indexOf("https");
							sFirstValue					= sDataSheet.substring(0,strsubDataSheetValue);
							sFirstValue 	   			= sFirstValue.replaceAll(":","");
							sFirstValue 	   			= sFirstValue.replaceAll("\n","");
							sSecondValue			    = sDataSheet.substring(strsubDataSheetValue,sDataSheet.length());
						}
						strReturnHTML += "<td>";
						 if (!UIUtil.isNullOrEmpty(sFirstValue) && !UIUtil.isNullOrEmpty(sSecondValue)) {
							 strReturnHTML += "<a style='color: #000000' href ="+sSecondValue+" title='Latest Url' target=_blank>"+sFirstValue+"</a><a href='javascript:showhistory()'><img border='0' src='../common/images/chromemore.png' title='History'/></a><input type='hidden' id='hiddenMEPId' value='"+strObjectId+"' />";
						} 
						strReturnHTML += "</td></tr>";
						
						for (int j2 = 0; j2 < allfieldMapList1.size(); j2++) {
						tempMap = (Map) allfieldMapList1.get(j2);
						String strMEPClassifiedAttrLabel1=(String)tempMap.get("label");
							if (!strMEPClassifiedAttrLabel1.equals("IHS Datasheet URL (latest) ") && !strMEPClassifiedAttrLabel1.equals("IHS Datasheet history")) {
								String strMEPClassifiedAttrName1=(String)tempMap.get("name");
								strReturnHTML += "<tr>";
								strReturnHTML += "<td  ><b>"+strMEPClassifiedAttrLabel1+"</b></td>";
								String strIHSAttrValue		= doMEP1.getInfo(context, "attribute["+strMEPClassifiedAttrName1+"].value");
								String sLatestUrl 			= PropertyUtil.getSchemaProperty(context,"attribute_IHSLatestDataSheetURL");
								String sdataUrl 			= PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL");
								if(strMEPClassifiedAttrLabel1.equals("Foot Print Reference") && strIHSAttrValue.equals("")) {
									strReturnHTML += "<td>No Foot Print</td>";	
								} else if (strMEPClassifiedAttrLabel1.equals("Library Reference") && strIHSAttrValue.equals("")){
									strReturnHTML += "<td>No Library Reference</td>";	
								} else {
								strReturnHTML += "<td  >"+strIHSAttrValue+"</td>";	
							}						
						}
						}
						strReturnHTML += "</tr>";
						strReturnHTML += "<tr>";
						strReturnHTML += "<td  ><b>Estimated Cost($)</b></td>";
						strReturnHTML += "<td>"+strEstimatedCostAttrValue+"</td>";						
						strReturnHTML += "</tr>";
						//strReturnHTML += "</table>";
						returnAttrval		= strReturnHTML;
							
					} else {
						
						if (spolicy.equals("EC Part")) {
							strReturnHTML += "<p>";
							strReturnHTML += "No Library attributes found";
							strReturnHTML += "</p>";
						} else {
							strReturnHTML += "<tr>";
							strReturnHTML += "<td ><b>MEP Name</b></td>";
							strReturnHTML += "<td >";
							strReturnHTML += sMEPName;
							strReturnHTML += "</td>";
							strReturnHTML += "</tr>";
							strReturnHTML += "<tr>";
							strReturnHTML += "<td  ><b>IHS Datasheet URL (latest)</b></td>";
							if (!UIUtil.isNullOrEmpty(sDataSheet)) {
								strsubDataSheetValue		= sDataSheet.indexOf("https");
								sFirstValue					= sDataSheet.substring(0,strsubDataSheetValue);
								sFirstValue 	   			= sFirstValue.replaceAll(":","");
								sFirstValue 	   			= sFirstValue.replaceAll("\n","");
								sSecondValue			    = sDataSheet.substring(strsubDataSheetValue,sDataSheet.length());
							}
							strReturnHTML += "<td>";
							 if (!UIUtil.isNullOrEmpty(sFirstValue) && !UIUtil.isNullOrEmpty(sSecondValue)) {
								 strReturnHTML += "<a style='color: #000000' href ="+sSecondValue+" title='Latest Url' target=_blank>"+sFirstValue+"</a><a href='javascript:showhistory();'><img border='0' src='../common/images/chromemore.png' title='History'/></a><input type='hidden' id='hiddenMEPId' value='"+strObjectId+"'/>";
							} 
							strReturnHTML += "</td></tr>";
							strReturnHTML += "<tr>";
							strReturnHTML += "<td  ><b>Estimated Cost($)</b></td>";
							strReturnHTML += "<td>"+strEstimatedCostAttrValue+"</td>";						
							strReturnHTML += "</tr>";
						}
						
						returnAttrval		= strReturnHTML;
					}				
				}
				
		 }catch (Exception e) {
            System.out.println("The exception : "+e);
         }
		 return returnAttrval;
	 }
	 
	 
	 public String getAllAttributeDetails (Context context, String[] args)throws Exception  {
		 
		String objectid					= args[0];
		DomainObject dmPartId			= new DomainObject(objectid);
		StringList objectSelects1		= new StringList();
		objectSelects1.addElement(DomainConstants.SELECT_POLICY);
		objectSelects1.addElement(DomainConstants.SELECT_NAME);
		objectSelects1.addElement(DomainConstants.SELECT_TYPE);
		Map objInfo 					= dmPartId.getInfo(context,objectSelects1);
		String functionality			= args[1];
		String sTempQuery				= "";
		String releasedRevision			= "";
		String sObjectType				= "";
		String spolicy					= "";
		String sObjectName				= (String)objInfo.get(DomainConstants.SELECT_NAME);
		sObjectType						= (String)objInfo.get(DomainConstants.SELECT_TYPE);
		spolicy							= (String)objInfo.get(DomainConstants.SELECT_POLICY);
		
		StringBuilder whereExpression 	= new StringBuilder("policy=='EC Part' && current=='Release' && next.current!='Release' && next.current!='Obsolete'");
		sTempQuery 						= MqlUtil.mqlCommand(context, "temp query bus $1 $2 $3 where $4 select $5 dump $6 ", "Part", sObjectName,"*",whereExpression.toString(),"revision","|");
		if (UIUtil.isNotNullAndNotEmpty(sTempQuery)) {     
		StringList slobjectsplit 		= FrameworkUtil.split(sTempQuery, "|");
		releasedRevision				= (String)slobjectsplit.get(3);
		}
		String returnAttrval			= "";
		StringBuffer sbHtmlOutput 		= new StringBuffer();
		String MQLResult  				= MqlUtil.mqlCommand(context, "print page $1 select content dump", "googChromeExtensioMapping");
		byte[] bytes 					= MQLResult.getBytes("UTF-8");
		InputStream input 				= new ByteArrayInputStream(bytes);
		Properties prop 				= new Properties();
		StringList lstAttributeName 	= new StringList();
		prop.load(input);		
		DomainObject db 				= new DomainObject(objectid);
		String BasicAttributes 			= prop.getProperty("PartProperties");
		String BasicNotAttributes 		= prop.getProperty("PartPropertiesNotAttributes");
		String sPartProperties			= prop.getProperty("AllPartProperties");
		String GTMAttributes 			= prop.getProperty("GTMProperties");
		String GTMAttributes1 			= prop.getProperty("GTMProperties1");
		String DeviationAttributes 		= prop.getProperty("DeviationAttributes");
		String changeActionAttributes 	= prop.getProperty("ChangeActionAttributes");
		StringList bsplitPipe 			= FrameworkUtil.split(BasicAttributes, "|");
		StringList gsplitPipe 			= FrameworkUtil.split(GTMAttributes, "|");
		StringList gsplitPipe1 			= FrameworkUtil.split(GTMAttributes1, "|");
		StringList slPartPro 			= FrameworkUtil.split(sPartProperties, "|");
		StringList devsplitPipe 		= FrameworkUtil.split(DeviationAttributes, "|");
		StringList CAsplitPipe 			= FrameworkUtil.split(changeActionAttributes, "|");
		
		try {
			ContextUtil.pushContext(context);
			if (functionality.equalsIgnoreCase("Basic")) {
				if (sObjectType.equals("Part")) {
					for (int j=0; j < bsplitPipe.size(); j++){
						String bsplitComma 		= (String) bsplitPipe.get(j);
						String bAttrenoviaName 	= (String) FrameworkUtil.split(bsplitComma, ",").get(1);
						lstAttributeName.addElement("attribute["+bAttrenoviaName+"].value");
						
					}
					Map mAttribute = db.getInfo(context, lstAttributeName);
					for (int j=0; j < bsplitPipe.size(); j++){
						String bsplitComma 		= (String) bsplitPipe.get(j);
						String bAttrLabelName 	= (String) FrameworkUtil.split(bsplitComma, ",").get(0);
						String bAttrenoviaName 	= (String) FrameworkUtil.split(bsplitComma, ",").get(1);
						String bAttrenoviaValue  = (String)mAttribute.get("attribute["+bAttrenoviaName+"].value");
						bAttrenoviaValue 			= bAttrenoviaValue.replaceAll("\n", "");
						//Added for CA Display in ChromeExtension by Preethi Rajaraman -- Starts
						if (bAttrenoviaName!=null && bAttrenoviaName.equals("XORGQCA Number")) {
								String CAName			= args[2];
								if (bAttrenoviaValue!=null && !bAttrenoviaValue.equals("")) {
									bAttrenoviaValue=CAName+"/"+bAttrenoviaValue;
								} else {
									bAttrenoviaValue=CAName;
								}
							}
						//Added for CA Display in ChromeExtension by Preethi Rajaraman -- Ends
						sbHtmlOutput.append("<tr>");
							if (spolicy.equals("EC Part")) {
								if (!bAttrLabelName.equals("Estimated Cost")) {
									if (!bAttrLabelName.equals("Last Released Revision")) {
										sbHtmlOutput.append("<td ><b>"+bAttrLabelName+":</b></td><td >"+bAttrenoviaValue+"</td>");                          
									} else {	
										sbHtmlOutput.append("<td ><b>"+bAttrLabelName+":</b></td><td >"+releasedRevision+"</td>");
									}
								} 
							} else {
								if (!bAttrLabelName.equals("Last Released Revision")) {
									sbHtmlOutput.append("<td ><b>"+bAttrLabelName+":</b></td><td >"+bAttrenoviaValue+"</td>");                          
								} else {	
									sbHtmlOutput.append("<td ><b>"+bAttrLabelName+":</b></td><td >"+releasedRevision+"</td>");
								}
							}
						sbHtmlOutput.append("</tr>");
						returnAttrval =    sbHtmlOutput.toString();
					}
				}else if(sObjectType.equals("Issue")) {
					for (int j=0; j < devsplitPipe.size(); j++){
						String dsplitComma 		= (String) devsplitPipe.get(j);
						String dAttrenoviaName 	= (String) FrameworkUtil.split(dsplitComma, ",").get(1);
						lstAttributeName.addElement("attribute["+dAttrenoviaName+"].value");
					}
					Map mAttribute = db.getInfo(context, lstAttributeName);
					for (int j=0; j < devsplitPipe.size(); j++){
						String dsplitComma 		= (String) devsplitPipe.get(j);
						String dAttrLabelName 	= (String) FrameworkUtil.split(dsplitComma, ",").get(0);
						String dAttrenoviaName 	= (String) FrameworkUtil.split(dsplitComma, ",").get(1);
						String dAttrenoviaValue  = (String)mAttribute.get("attribute["+dAttrenoviaName+"].value");
						dAttrenoviaValue 			= dAttrenoviaValue.replaceAll("\n", "");
						sbHtmlOutput.append("<tr>");
						if (!dAttrLabelName.equals("Last Released Revision")) {
							sbHtmlOutput.append("<td ><b>"+dAttrLabelName+":</b></td><td >"+dAttrenoviaValue+"</td>");                          
						}
						sbHtmlOutput.append("</tr>");
						returnAttrval =    sbHtmlOutput.toString();
					}
				} else {
					for (int j=0; j < CAsplitPipe.size(); j++){
						String csplitComma 		= (String) CAsplitPipe.get(j);
						String cAttrenoviaName 	= (String) FrameworkUtil.split(csplitComma, ",").get(1);
						lstAttributeName.addElement("attribute["+cAttrenoviaName+"].value");
					}
					Map mAttribute = db.getInfo(context, lstAttributeName);
					for (int j=0; j < CAsplitPipe.size(); j++){
						String csplitComma 		= (String) CAsplitPipe.get(j);
						String cAttrLabelName 	= (String) FrameworkUtil.split(csplitComma, ",").get(0);
						String cAttrenoviaName 	= (String) FrameworkUtil.split(csplitComma, ",").get(1);
						String cAttrenoviaValue  = (String)mAttribute.get("attribute["+cAttrenoviaName+"].value");
						cAttrenoviaValue 			= cAttrenoviaValue.replaceAll("\n", "");
						sbHtmlOutput.append("<tr>");
						if (!cAttrLabelName.equals("Last Released Revision")) {
							sbHtmlOutput.append("<td ><b>"+cAttrLabelName+":</b></td><td >"+cAttrenoviaValue+"</td>");                          
						}
						sbHtmlOutput.append("</tr>");
						returnAttrval =    sbHtmlOutput.toString();
					}
				}
			
			} 
			if(functionality.equalsIgnoreCase("GTM")) {
				for (int j1=0; j1 < gsplitPipe.size(); j1++){
					String gsplitComma 		= (String) gsplitPipe.get(j1);
					String gAttrenoviaName 	= (String) FrameworkUtil.split(gsplitComma, ",").get(1);
					lstAttributeName.addElement("attribute["+gAttrenoviaName+"].value");
				}
				for (int j1=0; j1 < gsplitPipe1.size(); j1++){
					String gsplitComma1 		= (String) gsplitPipe1.get(j1);
					String gAttrenoviaName1 	= (String) FrameworkUtil.split(gsplitComma1, ",").get(1);
					lstAttributeName.addElement("to[Part Revision].from.attribute["+gAttrenoviaName1+"].value");
				}
				Map mAttribute = db.getInfo(context, lstAttributeName);
				for (int j1=0; j1 < gsplitPipe.size(); j1++){
					String gsplitComma 		= (String) gsplitPipe.get(j1);
					String gAttrLabelName 	= (String) FrameworkUtil.split(gsplitComma, ",").get(0);
					String gAttrenoviaName 	= (String) FrameworkUtil.split(gsplitComma, ",").get(1);
					String gAttrenoviaValue  = (String)mAttribute.get("attribute["+gAttrenoviaName+"].value");
					sbHtmlOutput.append("<tr>");
					sbHtmlOutput.append("<td ><b>"+gAttrLabelName+":</b></td><td >"+gAttrenoviaValue+"</td>");																  
					sbHtmlOutput.append("</tr>");
					
					returnAttrval =    sbHtmlOutput.toString();
				}
				Map mAttribute1 = db.getInfo(context, lstAttributeName);
				for (int j1=0; j1 < gsplitPipe1.size(); j1++){
					String gsplitComma1 		= (String) gsplitPipe1.get(j1);
					String gAttrLabelName1 	= (String) FrameworkUtil.split(gsplitComma1, ",").get(0);
					String gAttrenoviaName1 	= (String) FrameworkUtil.split(gsplitComma1, ",").get(1);
					String gAttrenoviaValue1  = (String)mAttribute1.get("to[Part Revision].from.attribute["+gAttrenoviaName1+"].value");
					sbHtmlOutput.append("<tr>");
					sbHtmlOutput.append("<td ><b>"+gAttrLabelName1+":</b></td><td >"+gAttrenoviaValue1+"</td>");																  
					sbHtmlOutput.append("</tr>");
					
					returnAttrval =    sbHtmlOutput.toString();
				}
			} 
			if (functionality.equalsIgnoreCase("RangeValues")) {
				int iCount1 							= 0;
				DomainObject doMEP = DomainObject.newInstance(context, objectid);
				StringList objectSelects = new StringList();
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_NAME);

				MapList classificationList = doMEP.getRelatedObjects(context,
												"Classified Item",
												"*",
												objectSelects,
												null,
												true,//boolean getTo,
												false,//boolean getFrom,
												(short)0,
												null,
												null,
												0);
				if (classificationList.size() >0) {
					Iterator itr 				= classificationList.iterator();
					while(itr.hasNext()){
						Map classMap 			= (Map)itr.next();
						String strToolName 		= (String)classMap.get(DomainConstants.SELECT_NAME);
						String strPropertyKey 	= "Chrome.IHS.ITEMSubClass_"+strToolName.replace(" ", "_");
						if (UIUtil.isNotNullAndNotEmpty(strPropertyKey)) {
							String Rangevalues 		= prop.getProperty(strPropertyKey);
							String[] sListOfRangeVals1 			= Rangevalues.split(",");
							StringList eachvalue 	= new StringList(sListOfRangeVals1);
							sbHtmlOutput.append("<select name='WaymoType"+iCount1+"' id='WaymoType"+iCount1+"'>");
							for(int i=0;i<eachvalue.size();i++) {
								String sRangeValue 					= (String)eachvalue.get(i);
								sbHtmlOutput.append("<option value='"+sRangeValue+"'>"+sRangeValue+"</option>");
								iCount1++;
							}
							sbHtmlOutput.append("</select>"); 
							returnAttrval 						=   sbHtmlOutput.toString();
						} else {
							returnAttrval 						=   "";
						}	
					}
				} else {
					/* String sRangeValue					= "";
					int iCount 							= 0;
					String sWaymoPartType 				= MqlUtil.mqlCommand(context, "print attribute $1 select $2 dump ", "googCommodityCode", "range");
					String[] sListOfRangeVals 			= sWaymoPartType.split(",");
					StringList slwaymoPartrangeList 	= new StringList(sListOfRangeVals);
					sbHtmlOutput.append("<select name='WaymoType"+iCount+"' id='WaymoType"+iCount+"'>");
					for(int i=0;i<slwaymoPartrangeList.size();i++) {
						String svalue 					= (String)slwaymoPartrangeList.get(i);
						String[] sList 					= svalue.split("=");
						sRangeValue 					= sList[1];
						sRangeValue 					= sRangeValue.trim();
						sbHtmlOutput.append("<option value='"+sRangeValue+"'>"+sRangeValue+"</option>");
						iCount++;
					}
					sbHtmlOutput.append("</select>"); 
					returnAttrval 						=   sbHtmlOutput.toString(); */
					returnAttrval 						=   ""; 
				}
			}				
			if (functionality.equalsIgnoreCase("AllBasic")) {

				for (int j2=0; j2 < slPartPro.size(); j2++){
					String PartsplitComma 		= (String) slPartPro.get(j2);
					String gAttrenoviaName 	= (String) FrameworkUtil.split(PartsplitComma, ",").get(1);
					lstAttributeName.addElement("attribute["+gAttrenoviaName+"].value");
				}
				Map mAttribute = db.getInfo(context, lstAttributeName);
				for (int j2=0; j2 < slPartPro.size(); j2++){
					String gsplitComma 		= (String) slPartPro.get(j2);
					String gAttrLabelName 	= (String) FrameworkUtil.split(gsplitComma, ",").get(0);
					String gAttrenoviaName 	= (String) FrameworkUtil.split(gsplitComma, ",").get(1);
					String gAttrenoviaValue  = (String)mAttribute.get("attribute["+gAttrenoviaName+"].value");
					sbHtmlOutput.append("<tr>");
					sbHtmlOutput.append("<td ><b>"+gAttrLabelName+":</b></td><td >"+gAttrenoviaValue+"</td>");																  
					sbHtmlOutput.append("</tr>");
					
					returnAttrval =    sbHtmlOutput.toString();
				}
			
			} 
			if (functionality.equalsIgnoreCase("Classification")) 	{
				
				String[] sargs			= new String[1];
				sargs[0]				= objectid;
				returnAttrval			= getLibraryAttributesDetails(context,sargs);
			}
			ContextUtil.popContext(context);
		}catch (Exception e) {
            System.out.println("Error " + e);
        }
		return returnAttrval;
	 }
	
	 public String getPartDetails(Context context, String[] args)throws Exception  {
		 
        StringBuffer sbHtmlOutput = new StringBuffer();
        String PartDetails = "";
        String sPartName 				= args[0];
		sPartName						= sPartName.replace("%23", "#");
        String sUserName 				= args[1];
        String sWaymoURL                = args[2];
        String sImageUrl                = args[3];
        String sPhasekey                = args[4];
		String BasicAttribute			= "";
		String AllBasicAttribute		= "";
		String GTMAttributes			= "";
		String LibraryAttributes		= "";
		String sSpecFiles				= "";
		String RangeVale				= "";
		String Imagepath				= "";
		String sResult					= "";
		String role						= "";
        MapList PartList        		= new MapList();
        StringList resultSelects 		= new StringList();
        String strMEPName				= "";
        String strERPStatus				= "";
        String sUOMValue                = PropertyUtil.getSchemaProperty(context,"attribute_UnitofMeasure");
        String sQCA    					= PropertyUtil.getSchemaProperty(context,"attribute_XORGQCANumber");
        String sCountryAttr   			= PropertyUtil.getSchemaProperty(context,"attribute_googGTMClassificationCountry");
        MapList Relatedobjects          = new MapList();
        String Manufacturerelationship    	    = PropertyUtil.getSchemaProperty(context,"relationship_ManufacturerEquivalent");
        resultSelects.addElement("name");
        resultSelects.addElement("type");
        resultSelects.addElement("id");
        resultSelects.addElement("revision");
        resultSelects.addElement("description");
        resultSelects.addElement("policy");
        resultSelects.addElement(DomainConstants.SELECT_OWNER);
		resultSelects.addElement(DomainConstants.SELECT_MODIFIED);
        resultSelects.addElement(DomainConstants.SELECT_CURRENT);
		resultSelects.addElement(DomainConstants.SELECT_POLICY);
        resultSelects.addElement("attribute["+sUOMValue+"].value");
        resultSelects.addElement("attribute["+sCountryAttr+"].value");
        resultSelects.addElement("attribute["+sQCA+"].value");
		resultSelects.addElement("to[Manufacturing Responsibility].from.name");
		resultSelects.addElement("to[Manufacturer Equivalent].from.id");
		resultSelects.addElement("to[Manufacturer Equivalent].from.name");
        String sECPolicy 				= PropertyUtil.getSchemaProperty(context,"policy_ECPart");
        StringBuilder whereExpression 	= new StringBuilder("revision==last");
        //String sType  				= PropertyUtil.getSchemaProperty(context,"type_Part");
		String sType  					= "Part,Change Action,Issue";
		int	limit						= 1;
		if (args.length > 5) {
			whereExpression.append(" && id=='"+args[5]+"'");
		}
        try
        {	
			ContextUtil.pushContext(context);
            PartList = DomainObject.findObjects(context, sType, // type pattern
														sPartName,
														DomainConstants.QUERY_WILDCARD,
														DomainConstants.QUERY_WILDCARD,
														DomainConstants.QUERY_WILDCARD,
														whereExpression.toString(),
														DomainConstants.QUERY_WILDCARD,
														false,
														resultSelects,
														(short)limit);
                if (PartList!=null && PartList.size()>0) {
                    Map dataMap = (Map) PartList.get(0);
                    String sPartId = (String) dataMap.get("id");
					DomainObject dmPartId   = new DomainObject(sPartId);
					String sName	= (String) dataMap.get("name");
					String strType			= (String) dataMap.get("type");
                    String sDesc 			= (String) dataMap.get("description");
					sDesc 			= sDesc.replaceAll("\n", "");
                    String sRevision = (String) dataMap.get("revision");
                    String strUOMValue = (String) dataMap.get("attribute[Unit of Measure].value");
                    String strGTMCountry = (String) dataMap.get("attribute[googGTMClassificationCountry].value");
                    String strState 		= (String) dataMap.get( DomainConstants.SELECT_CURRENT);
					String strPolicy 		= (String)dataMap.get(DomainConstants.SELECT_POLICY);
					String strOwner 		= (String)dataMap.get(DomainConstants.SELECT_OWNER);
					String strModified 		= (String)dataMap.get(DomainConstants.SELECT_MODIFIED);
                    if (strState.equalsIgnoreCase("Release")) {
                        strERPStatus			= "Published";
                    }else {
                        String sQCAvalue		= (String) dataMap.get("attribute["+sQCA+"].value");
                        if (sQCAvalue != null && !sQCAvalue.equals("")) {
                            strERPStatus			= "Published";
                        } else {
                            strERPStatus			= "Not Published";
                        }
                    }
                    Map tempMap                         = null;
                    Relatedobjects                      = dmPartId.getRelatedObjects(context,
                                                                Manufacturerelationship,              	// relationship pattern
                                                                "Part",              					// object pattern
                                                                resultSelects,                 			// object selects
                                                                null,              						// relationship selects
                                                                false,                        			// to direction
                                                                true,                       			// from direction
                                                                (short) 0,                   			// recursion level
                                                                null,                        			// object where clause
                                                                null);
                    for(int i=0;i<Relatedobjects.size();i++) {
						
                        tempMap = (Map) Relatedobjects.get(i);
                        String sMEPName = (String)tempMap.get("name");
                        String sSuppilerName = (String)tempMap.get("to[Manufacturing Responsibility].from.name");
                        if (i==0) {
                            strMEPName = sMEPName+"["+sSuppilerName+"]";
                        } else {
                            strMEPName = strMEPName+","+sMEPName+"["+sSuppilerName+"]";
                        }
                    }
					HashMap programMap 		= new HashMap();
					programMap.put("objectId", sPartId);
					String CAName = "";
					if (strPolicy.equals("EC Part")) {
						sSpecFiles				= getSpecificationFieldsMapList(context,JPO.packArgs(programMap));	
						XORGERPIntegration_mxJPO integrationjpo = new XORGERPIntegration_mxJPO(context,args);
						StringList slCAIDs = integrationjpo.getCAIdsFromPart(context,sPartId);
						if (slCAIDs!=null && slCAIDs.size() > 0) {
							String sCAId = (String) slCAIDs.get(0);
							DomainObject doCA = new DomainObject(sCAId);
							CAName = doCA.getInfo(context, DomainConstants.SELECT_NAME);
							
						}
					}
					String[] sargs			= new String[3];
					sargs[0]				= sPartId;
					sargs[1]				= "Basic";
					sargs[2]				= CAName;
					
					String[] sargs3			= new String[2];
					sargs3[0]				= sPartId;
					sargs3[1]				= "Classification";
					
					String[] sargs4			= new String[2];
					sargs4[0]				= sPartId;
					sargs4[1]				= "RangeValues";
					BasicAttribute			= getAllAttributeDetails(context,sargs);
					LibraryAttributes		= getAllAttributeDetails(context,sargs3);
						if (strPolicy.equals("EC Part")) {
						PartDetails 			= sPartId+"|"+sRevision+"|"+sDesc+"|"+strUOMValue+"|"+strState+"|"+strERPStatus+"|"+strMEPName+"|"+strGTMCountry+"|"+sName+"|"+"PartDetails"+"|"+BasicAttribute+"|"+LibraryAttributes+"|"+RangeVale+"|"+sImageUrl+"|"+strOwner+"|"+strModified+"|"+strType+"|"+sSpecFiles+"|"+strPolicy;
						} else {
						String WPNname			= (String)dataMap.get("to[Manufacturer Equivalent].from.name");
						String WPNId			= (String)dataMap.get("to[Manufacturer Equivalent].from.id");
						//Modified for same MEP and ECPart Name issue by Preethi Rajaraman -- Starts
						StringList splitWPNId					= FrameworkUtil.split(WPNId, "\u0007");
						StringList splitWPNName					= FrameworkUtil.split(WPNname, "\u0007");
						if (splitWPNId.size() > 0) {
							WPNId			= (String)splitWPNId.get(splitWPNId.size()-1);
						} 
						if (splitWPNName.size() > 0) {
							WPNname			= (String)splitWPNName.get(splitWPNName.size()-1);
						} 
						//Modified for same MEP and ECPart Name issue by Preethi Rajaraman -- Ends
							if (UIUtil.isNotNullAndNotEmpty(WPNname)) {
								String[] args1 			= new String[6];
								args1[0] 				= WPNname;
								args1[1]				= sUserName;
								args1[2]				= sWaymoURL;
								args1[3]				= sImageUrl;
								args1[4]				= sPhasekey;
								args1[5]				= WPNId;
								PartDetails 			= getPartDetails (context,args1);
							} else {
								String[] args2 			= new String[1];
								args2[0] 				= sUserName;
								PartDetails				= getUserDetails(context,args2);
							strMEPName				= (String)dataMap.get(DomainConstants.SELECT_NAME) +"["+(String)dataMap.get("to[Manufacturing Responsibility].from.name")+"]";
								if (!PartDetails.contains("User exists in waymo")) {
								PartDetails 			= sPartId+"|"+sRevision+"|"+sDesc+"|"+strUOMValue+"|"+strState+"|"+strERPStatus+"|"+strMEPName+"|"+strGTMCountry+"|"+sName+"|"+"No WPN found"+"|"+BasicAttribute+"|"+LibraryAttributes+"|"+RangeVale+"|"+sImageUrl+"|"+strOwner+"|"+strModified+"|"+strType+"|"+sSpecFiles+"|"+strPolicy;
								} else {
								RangeVale				= getAllAttributeDetails(context,sargs4);
								PartDetails 			= sPartId+"|"+sRevision+"|"+sDesc+"|"+strUOMValue+"|"+strState+"|"+strERPStatus+"|"+strMEPName+"|"+strGTMCountry+"|"+sName+"|"+"Create WPN"+"|"+BasicAttribute+"|"+LibraryAttributes+"|"+RangeVale+"|"+sImageUrl+"|"+strOwner+"|"+strModified+"|"+strType+"|"+sSpecFiles+"|"+strPolicy;
							}
						}
					}
					
                    sWaymoURL 						= sWaymoURL+"/common/emxNavigator.jsp?objectId="+sPartId;
                    sbHtmlOutput.append("<img src='"+sImageUrl+"' style= 'float:left;margin-right:15px;margin-bottom:10px;' height='86' width='86'/>");
                    sbHtmlOutput.append("<P>");
                    sbHtmlOutput.append("<a style='padding: 5px;font-size: 15px;color: #000000;text-decoration: none;' target='_blank' href='"+sWaymoURL+"'><b>"+sPartName+"</b></a><br>");
                    sbHtmlOutput.append("<font color='dimgray' style='font-family:verdana;font-size: 13px'>"+sDesc+"</font><br>");
                    sbHtmlOutput.append("<a style='padding: 5px;font-size: 15px;' target='_blank' href='"+sWaymoURL+"'>Open with Waymo</a></P>");
                    sbHtmlOutput.append("&nbsp<font color='dimgray' style='font-family:verdana;font-size: 13px'><p><b>Latest Revision: </b>"+sRevision+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b>Part State: </b>"+strState+"<br><b>UOM: </b>"+strUOMValue+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<b>ERP Push Status: </b>"+strERPStatus+"<br><br><b>MPN(S): </b>"+strMEPName+"</p></font>");
                    sbHtmlOutput.append("</P>" );
					if(sPhasekey != null && sPhasekey.equalsIgnoreCase("Hightlight")) {
						PartDetails =    sbHtmlOutput.toString();
					}else if(sPhasekey != null && sPhasekey.equalsIgnoreCase("SearchByPartName")) {
						PartDetails =    PartDetails;
					} else {
						PartDetails = "";
					}
                }
				if (UIUtil.isNullOrEmpty(PartDetails)) {
						sPartName=sPartName.replaceAll("[^a-zA-Z0-9\\s+]", "*");
						PartList = DomainObject.findObjects(context, sType, // type pattern
														sPartName,
														DomainConstants.QUERY_WILDCARD,
														DomainConstants.QUERY_WILDCARD,
														DomainConstants.QUERY_WILDCARD,
														whereExpression.toString(),
														DomainConstants.QUERY_WILDCARD,
														false,
														resultSelects,
														(short)100);
						PartDetails = "No Object Found";
						PartList.addSortKey("name","ascending", "String");
						PartList.sort();
						for(int ij=0; ij<PartList.size();ij++) {
							 Map tMap = (Map) PartList.get(ij);
							 String sName = (String)tMap.get("name");
							 String sDesc = (String)tMap.get("description");
							 sDesc=sDesc.replaceAll("\n", "<newline>");
							 PartDetails=PartDetails+"|"+sName+"~"+sDesc;
							 
						}
				}
            ContextUtil.popContext(context);
        }catch (Exception e) {
            System.out.println("Error " + e);
        }
		PartDetails=PartDetails.replaceAll("\n", " ");
		PartDetails=PartDetails.replace("\"", " ");
        return PartDetails;
    }

    public String DownloadMEP(Context context, String[] args) throws Exception {
        String sPartName 				= args[0];
		sPartName						= sPartName.replace("%23", "#");
        String sUserName 				= args[1];
		String WaymoURL					= "";
        String sRev 					= "01";
        MapList PartList        		= new MapList();
		String SFileName   				= "";
        String SFileFormat   			= "";
		String sResult					= "";
        String isObjExists				= "";
		String strWorkspace 			= context.createWorkspace();
        StringList resultSelects 		= new StringList();
        resultSelects.addElement(DomainConstants.SELECT_NAME);
        resultSelects.addElement(DomainConstants.SELECT_ID);
        StringList slBusSelect 			= new StringList();
        slBusSelect.add("id");
        slBusSelect.add("attribute[Primary Image].value");
        String sECPolicy 				= PropertyUtil.getSchemaProperty(context,"policy_ECPart");
        StringBuilder whereExpression 	= new StringBuilder("revision == last");
        String sType  					= PropertyUtil.getSchemaProperty(context,"type_Part");
        String sREL 					= PropertyUtil.getSchemaProperty(context,"relationship_ImageHolder");
		MapList DocumnetList        	= new MapList();
		String sDocId					= "";
		ContextUtil.pushContext(context);
		DocumnetList 					= DomainObject.findObjects(context, "Document", // type pattern
														"ForChromeExtensionImage",
														"0",
														DomainConstants.QUERY_WILDCARD,
														DomainConstants.QUERY_WILDCARD,
														"",
														DomainConstants.QUERY_WILDCARD,
														false,
														resultSelects,
														(short)0);
		if (DocumnetList!=null && DocumnetList.size()>0) {
                    Map dataMap 	= (Map)DocumnetList.get(0);
                    sDocId 			= (String)dataMap.get("id");
		}

        if(UIUtil.isNullOrEmpty(isObjExists) || "false".equalsIgnoreCase(isObjExists))
        {
            try
            {
                PartList = DomainObject.findObjects(context,
                        "Part,Change Action,Issue",
                        sPartName,
                        DomainConstants.QUERY_WILDCARD,
                        DomainConstants.QUERY_WILDCARD,
                        DomainConstants.QUERY_WILDCARD,
                        whereExpression.toString(),
                        DomainConstants.QUERY_WILDCARD,
                        false,
                        resultSelects,
                        (short)0);
                if (PartList!=null && PartList.size()>0) {
                    Map dataMap 	= (Map)PartList.get(0);
                    String sPartId 	= (String)dataMap.get("id");
                    DomainObject domobj = new DomainObject(sPartId);
                    Map mapPartMaster = domobj.getRelatedObject(context,sREL,false,slBusSelect,null);
                    if(mapPartMaster!=null && mapPartMaster.size()>0) {
                        
                        String sImageId  = (String)mapPartMaster.get("id");
                        String FileName   = (String)mapPartMaster.get("attribute[Primary Image].value");
                        DomainObject doSpec = new DomainObject(sImageId);
                        doSpec.open(context);
                        matrix.db.FileList fileList = doSpec.getFiles(context);
                        for(int i=0;i<fileList.size();i++){
                            matrix.db.File fileTemp = (matrix.db.File) fileList.get(i);
                            SFileName=(String)fileTemp.getName();
                            SFileFormat=(String)fileTemp.getFormat();
                            if (fileList.size()>0) {
                                if (FileName.equals(SFileName)) {
                                    doSpec.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
                                    sResult = strWorkspace + java.io.File.separator + SFileName;
                                    return sResult;
                                }
                            } else {								
                                DomainObject doSpec1 = new DomainObject(sDocId);
								doSpec1.open(context);
								matrix.db.FileList fileList1 = doSpec1.getFiles(context);
								matrix.db.File fileTemp1 = (matrix.db.File) fileList1.get(0);
								SFileName=(String)fileTemp1.getName();
								SFileFormat=(String)fileTemp1.getFormat();
								doSpec1.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
								sResult = strWorkspace + java.io.File.separator + SFileName;
								doSpec1.close(context);
								return sResult;
                            }
                        }
                        doSpec.close(context);

                    } else {
						DomainObject doSpec = new DomainObject(sDocId);
						doSpec.open(context);
                        matrix.db.FileList fileList = doSpec.getFiles(context);
						matrix.db.File fileTemp = (matrix.db.File) fileList.get(0);
                        SFileName=(String)fileTemp.getName();
						SFileFormat=(String)fileTemp.getFormat();
						doSpec.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
						sResult = strWorkspace + java.io.File.separator + SFileName;
						doSpec.close(context);
						return sResult;
                    }
                }
            } catch (Exception e)
            {
                System.out.println("The exception : "+e);
            }
        } else if(UIUtil.isNullOrEmpty(isObjExists) || "true".equalsIgnoreCase(isObjExists)) {
            String sPartId 	= sPartName;
            DomainObject domobj = new DomainObject(sPartId);
            Map mapPartMaster = domobj.getRelatedObject(context,sREL,false,slBusSelect,null);
            if(mapPartMaster!=null && mapPartMaster.size()>0) {
                String sImageId  = (String)mapPartMaster.get("id");
                String FileName   = (String)mapPartMaster.get("attribute[Primary Image].value");
                DomainObject doSpec = new DomainObject(sImageId);
                doSpec.open(context);
                matrix.db.FileList fileList = doSpec.getFiles(context);
                for(int i=0;i<fileList.size();i++){
                    matrix.db.File fileTemp = (matrix.db.File) fileList.get(i);
                    SFileName=(String)fileTemp.getName();
                    SFileFormat=(String)fileTemp.getFormat();
                    if (fileList.size()>0) {
                        if (FileName.equals(SFileName)) {
                            doSpec.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
                            sResult = strWorkspace + java.io.File.separator + SFileName;
                            return sResult;
                        }
                    } else {
                       DomainObject doSpec2 = new DomainObject(sDocId);
						doSpec2.open(context);
						matrix.db.FileList fileList2 = doSpec2.getFiles(context);
						matrix.db.File fileTemp2 = (matrix.db.File) fileList2.get(0);
						SFileName=(String)fileTemp2.getName();
						SFileFormat=(String)fileTemp2.getFormat();
						doSpec2.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
						sResult = strWorkspace + java.io.File.separator + SFileName;
						doSpec2.close(context);
						return sResult;
                    }
                }
                doSpec.close(context);
            } else {
                DomainObject doSpec = new DomainObject(sDocId);
				doSpec.open(context);
				matrix.db.FileList fileList = doSpec.getFiles(context);
				matrix.db.File fileTemp = (matrix.db.File) fileList.get(0);
				SFileName=(String)fileTemp.getName();
				SFileFormat=(String)fileTemp.getFormat();
				doSpec.checkoutFile(context, false, SFileFormat, SFileName, strWorkspace);
				sResult = strWorkspace + java.io.File.separator + SFileName;
				doSpec.close(context);
				return sResult;
            }
        }
        ContextUtil.popContext(context);
        return "Part does not exist.";
    }
	
	 public String createWPN(Context context, String[] args)throws Exception  {
		 String returnNewPartDetails				= "";
		 String MEPObjectId 						= args[0];
		 String Description 						= args[1];
		 String waymoName 		    				= args[2];
		 String sOwner								= args[3];
		 String strNewPartId						= "";
		 String sType 								= "Part";
		 String orgId								= "";
		 String MEPProject							= "";
		 String MEPOrganization						= "";
		 String Result								= "";
		 String sManufacturerEquivalent			= PropertyUtil.getSchemaProperty("relationship_ManufacturerEquivalent");
		 String sDesignResponsibility				= PropertyUtil.getSchemaProperty("relationship_DesignResponsibility");
		 DomainRelationship drPartToMEP 			= new DomainRelationship();
		 String  strCommodityAttr 					= PropertyUtil.getSchemaProperty("attribute_googCommodityCode");
		 String strDesignCollaboration 				= PropertyUtil.getSchemaProperty("attribute_isVPMVisible");
		 String strSpecTitle 						= PropertyUtil.getSchemaProperty("attribute_V_Name");
		 String strRDE 								= PropertyUtil.getSchemaProperty("attribute_ResponsibleDesignEngineer");
		 String stOrginator 						= PropertyUtil.getSchemaProperty("attribute_Originator");
		 String sECPolicy							= PropertyUtil.getSchemaProperty(context,"policy_ECPart");
		 String strPartName 						= "";
		 try
        {
			 ContextUtil.pushContext(context);
			 //Creating Part Object
			 strNewPartId								= FrameworkUtil.autoName(context,"type_Part",waymoName,"policy_ECPart","vault_eServiceProduction");
			DomainObject dObj 		 					= new DomainObject(strNewPartId);
			strPartName 								= dObj.getInfo(context,DomainObject.SELECT_NAME);
			Map attributes 								= new HashMap(4);
			attributes.put( strCommodityAttr, waymoName );
			attributes.put( strDesignCollaboration, "FALSE" );
			attributes.put( strSpecTitle, strPartName);
			attributes.put( strRDE, sOwner );
			attributes.put( stOrginator, sOwner );
			dObj.setAttributeValues(context, attributes);
			dObj.setOwner(context,sOwner);
			String defaultOrg     						= PersonUtil.getDefaultOrganization(context, sOwner);
			String defualtProj    						= PersonUtil.getDefaultProject(context, sOwner);
			String cmd 									= "mod bus $1 project $2 organization $3";
			String cmd1 								= "print bus $1 select $2 $3 dump $4";
			DomainObject doMEP 							= new DomainObject(MEPObjectId);
			String MEPDesc                              = doMEP.getDescription(context);
			dObj.setDescription(context,MEPDesc);
			if (UIUtil.isNotNullAndNotEmpty(defaultOrg) && UIUtil.isNotNullAndNotEmpty(defaultOrg)) {
				 MqlUtil.mqlCommand(context, cmd, strNewPartId, defualtProj, defaultOrg);
				 orgId									= MqlUtil.mqlCommand(context, "Print bus $1 $2 $3 select $4 dump", "Company", defaultOrg, "-","id");
				 drPartToMEP.connect(context,new DomainObject(orgId),sDesignResponsibility,new DomainObject(strNewPartId));
			} else {
				Result 									= MqlUtil.mqlCommand(context, cmd1, MEPObjectId, "project", "organization","|");
				StringList splitPipe 					= FrameworkUtil.split(Result, "|");
				MEPProject								= (String)splitPipe.get(0);
				if(UIUtil.isNotNullAndNotEmpty(MEPProject)) {
					MEPOrganization						= (String)splitPipe.get(1);
					MqlUtil.mqlCommand(context, cmd, strNewPartId, MEPProject, MEPOrganization);
					orgId								= MqlUtil.mqlCommand(context, "Print bus $1 $2 $3 select $4 dump", "Company", MEPOrganization, "-","id");
					drPartToMEP.connect(context,new DomainObject(orgId),sDesignResponsibility,new DomainObject(strNewPartId));
				} else {
					MEPProject							= "Default";
					MEPOrganization						= "Google";
					MqlUtil.mqlCommand(context, cmd, strNewPartId, MEPProject, MEPOrganization);
					orgId									= MqlUtil.mqlCommand(context, "Print bus $1 $2 $3 select $4 dump", "Company", MEPOrganization, "-","id");
					drPartToMEP.connect(context,new DomainObject(orgId),sDesignResponsibility,new DomainObject(strNewPartId));
				}
			}
			 //Connecting to MEP
			 drPartToMEP.connect(context,new DomainObject(strNewPartId),sManufacturerEquivalent,doMEP);
			 ContextUtil.popContext(context);
			
			 returnNewPartDetails =	strPartName;
		} catch (Exception e) {
                System.out.println("The exception : "+e);
        }
		 return returnNewPartDetails;
	 }

    public String getUserDetails(Context context, String[] args) throws Exception {
        String message              = "";
        String Username 		    = args[0];
        String sCmd                 = "print businessobject $1 $2 $3 select $4 dump $5";
        String personTypeName       = PropertyUtil.getSchemaProperty(context,"type_Person");
        String personExistName      = com.matrixone.apps.domain.util.MqlUtil.mqlCommand(context,sCmd,personTypeName,Username,"-","exists",";");
        if(personExistName.startsWith("TRUE"))
        {
            String personCurrentState  = com.matrixone.apps.domain.util.MqlUtil.mqlCommand(context,sCmd,personTypeName,Username,"-","current",";");
            if ( personCurrentState != null && (personCurrentState.trim().equals("Active")) ) {
                String Cmd           = "print person $1 select $2 dump $3 $4";
                String sProduct  	= com.matrixone.apps.domain.util.MqlUtil.mqlCommand(context,Cmd,Username,"assignment","|",";");
                String[] arrOfStr 	= sProduct.split("\\|");
                List<String> list 	= Arrays.asList(arrOfStr);
                if (list.contains("googChromeExtension Parts author")){
					
                    message = "User exists in waymo";
                } else {
                    message = "No googChromeExtension Parts author role";
                }
            } else {
                message = "Your User Account is Not Active in Waymo PLM Application. Please contact Administrator for further assistance.";
            }
        } else {
				message = "Your User Account Does Not Exist in Waymo PLM Application. Please contact Administrator for further assistance.";
		}
        return message;
    }

	private MapList getDynamicFieldsMapList(Context context,MapList classificationAttributesList,boolean isCreate) throws Exception{

        //Define a new MapList to return.
		Map FirstMap= new HashMap();
		Map SecondMap= new HashMap();
        MapList fieldMapList = new MapList();
        String strLanguage =  context.getSession().getLanguage();
		String sattribute 				= "Alternate Symbols|Foot Print Reference|JEDEC Type|Library Reference";
		String[] arrOfStr 	= sattribute.split("\\|");
		List<String> list = Arrays.asList(arrOfStr);
        // attributeAttributeGroupMap contains all the attribute group names to which each attribute belongs
        HashMap attributeAttributeGroupMap = new HashMap();

        if(classificationAttributesList == null)
            return fieldMapList;

        Iterator classItr = classificationAttributesList.iterator();
        while(classItr.hasNext()){

            Map classificationAttributesMap = (Map)classItr.next();


            MapList classificationAttributes = (MapList)classificationAttributesMap.get("attributes");
           
        for(int i=0;i<classificationAttributes.size();i++){
            HashMap attributeGroup = (HashMap)classificationAttributes.get(i);
            MapList attributes = (MapList)attributeGroup.get("attributes");
             for(int j=0;j<attributes.size();j++){
                HashMap attribute =  (HashMap)attributes.get(j);
                String attributeQualifiedName = (String)attribute.get("qualifiedName");
                String attributeName = (String)attribute.get("name");
                HashMap fieldMap = new HashMap();
				   
				   if(list.contains(i18nNow.getAttributeI18NString(attributeName,strLanguage))) {
						FirstMap.put(attributeQualifiedName,i18nNow.getAttributeI18NString(attributeName,strLanguage));
					} else {
						SecondMap.put(attributeQualifiedName,i18nNow.getAttributeI18NString(attributeName,strLanguage));
            }
				   
        }

        }
        }
		FirstMap.putAll(SecondMap);
		Iterator<Map.Entry<String, String>> itr = FirstMap.entrySet().iterator(); 
		MapList FirstMapList = new MapList();
		MapList SecondMapList = new MapList();
		while(itr.hasNext()) 
		{ 
			Map.Entry<String, String> entry = itr.next(); 
			Map hMap = new HashMap();
			hMap.put("name",entry.getKey());
			hMap.put("label",entry.getValue());
			if(list.contains(entry.getValue())) {
				FirstMapList.add(hMap);
			} else {
				SecondMapList.add(hMap);
			}
		} 

		fieldMapList.addAll(FirstMapList);
		fieldMapList.addAll(SecondMapList);
        return fieldMapList;
    }
	
	// List of all Tasks assigned to the context User -- Starts
	public String getActiveAndTasksToBeAcceptedOfContextUser(Context context, String[] args) throws Exception {
		
		String sUserName 							= args[0];
		String sFunctionality 						= args[1];
		context 									= ContextUtil.getAnonymousContext();
		ContextUtil.pushContext(context, sUserName, null, null);
		String retStr 								= "";
		String sTaskId 								= "";
		String sTaskName 							= "";
		String sTitle 								= "";
		String sType 								= "";
		String sState 								= "";
		String sInstruction 						= "";
		String sDueDate	 							= "";
		String sContext	 							= "";
		String sRouteContext	 					= "";
		String sRouteContextId	 					= "";
		String sRouteContextDesc	 				= "";
		JSONArray jArr 								= new JSONArray();
		
		try {
			if (UIUtil.isNotNullAndNotEmpty(sFunctionality) && sFunctionality.equals("getTask") ) {
				MapList mlItems 						= (MapList)JPO.invoke(context, "emxInboxTask", null, "getActiveAndTasksToBeAccepted", null, MapList.class);
				for(int j=0;j<mlItems.size();j++){	
				   HashMap mapList 						= (HashMap)mlItems.get(j);
				   sTaskId 								= (String)mapList.get("id");
				   sTaskName 							= (String)mapList.get("name");
				   sTitle 								= (String)mapList.get("attribute[Title]");
				   sType 								= (String)mapList.get("attribute[Route Action]");
				   sState 								= (String)mapList.get("current");
				   sInstruction 						= (String)mapList.get("attribute[Route Instructions]");
				   sDueDate	 							= (String)mapList.get("attribute[Scheduled Completion Date]");
				   sContext	 							= (String)mapList.get("from[Route Task].to.name");
				   DomainObject dmTaskId				=  new DomainObject(sTaskId);
				   sRouteContext	 					= (String)dmTaskId.getInfo(context,"from[Route Task].to.to[Object Route].from.name");
				   sRouteContextId	 					= (String)dmTaskId.getInfo(context,"from[Route Task].to.to[Object Route].from.id");
				   DomainObject dmroute					= new DomainObject(sRouteContextId);
				   sRouteContextDesc					= dmroute.getDescription(context);
				   JSONObject jTaskDetails 				= new JSONObject();
				   jTaskDetails.put("TaskId",sTaskId);
				   jTaskDetails.put("TaskName",sTaskName);
				   jTaskDetails.put("Title",sTitle);
				   jTaskDetails.put("Type",sType);
				   jTaskDetails.put("State",sState);
				   jTaskDetails.put("Instructions",sInstruction);
				   jTaskDetails.put("Due Date",sDueDate);
				   jTaskDetails.put("Context",sContext);
				   jTaskDetails.put("Route Context",sRouteContext);
				   jTaskDetails.put("Route Description",sRouteContextDesc);
				   jArr.put(jTaskDetails);
				}
			}
		} catch (Exception e) {
            System.out.println("The exception : "+e);
        }
		retStr = jArr.toString();
		return retStr;
	}
	// List of all Tasks assigned to the context User -- Ends
	//Added by Preethi Rajaraman for IHSDataSheetURL in Chrome Extension -- Starts
	public String getIHSURLHistory (Context context, String[] args)throws Exception  {
		String result ="";
		String sIHSUrl 	= PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL");
		try {
			String objectId      	= args[0];
			DomainObject domObj		= new DomainObject(objectId);
			String strIHSAttrValue  = domObj.getInfo(context, "attribute["+sIHSUrl+"].value");
			StringList slDataURL = FrameworkUtil.split(strIHSAttrValue, "\n");
			String sMain = "";
			for(int i=0;i<slDataURL.size();i++) {
				 String sValue = (String)slDataURL.get(i);
				 sMain 	   	= sMain.replaceAll(":","");
				 if (sValue.startsWith("https:")) {
					result +="<a href ="+sValue+" style='color: #000000' target=_blank>"+sMain+"</a>";
					result +="<BR/>";
				 } else {
					sMain = sValue;
				 }
				 
			}
			
		} catch (Exception exception)
		{
			throw exception;
		}
		return result;
	}
	//Added by Preethi Rajaraman for IHSDataSheetURL in Chrome Extension -- Ends
	//Added by Preethi Rajaraman for Specification Tab in Chrome Extension -- Starts
	private String getSpecificationFieldsMapList(Context context,String[] args) throws Exception{
		StringList slList 				= new StringList();
		StringBuffer sbHtmlOutput 		= new StringBuffer();
		String sName					= "";
		String sType					= "";
		StringList objectSelects		= new StringList(DomainConstants.SELECT_TYPE);
		objectSelects.add(DomainConstants.SELECT_NAME);
		String strWorkspace 			= context.createWorkspace();
		try {
			
			MapList mpSpecFiles 		= (MapList) JPO.invoke(context, "emxQuickFileAccess", null,"getPartRelatedItemsList",args, MapList.class);
			for(int i=0;i<mpSpecFiles.size();i++) {
				Map objectMap 			= (Map) mpSpecFiles.get(i);
				String strPartId 		= (String) objectMap.get("id");
				emxQuickFileAccess_mxJPO FileDetails = new emxQuickFileAccess_mxJPO(context,null);
				MapList mpFiles 		= FileDetails.getFileForRelatedObjects(context, strPartId);
				if (mpFiles != null && mpFiles.size()>0) {
				
					Map tempMap 			= (Map) mpFiles.get(0);
					MapList mpObject 		= (MapList) tempMap.get("objectList");
					String sFileName		= "";
					for(int ii=0;ii<mpObject.size();ii++) {
						Map tMap 			= (Map)mpObject.get(ii);
						String sId			= (String) tMap.get("masterId");
						DomainObject domObj = new DomainObject(sId);
						if(ii==0) {
							Map objInfo 	= domObj.getInfo(context, objectSelects);
							sName 			= (String)objInfo.get(DomainConstants.SELECT_NAME);
							sType 			= (String)objInfo.get(DomainConstants.SELECT_TYPE);
						}
						String FName 		= (String) tMap.get("format.file.name");
						String SFileFormat 	= (String) tMap.get("format.file.format");
						String extension 	= FName.substring(FName.lastIndexOf("."));
						if (extension != null && extension.equalsIgnoreCase(".pdf") && extension.equalsIgnoreCase(".PDF")) {
							
							try{
								domObj.checkoutFile(context, false, SFileFormat, FName, strWorkspace);
							} catch (Exception e) {
								
							}
							FName = URLEncoder.encode(FName, StandardCharsets.UTF_8.name());
							String sResult = strWorkspace + java.io.File.separator ;
							sResult = URLEncoder.encode(sResult, StandardCharsets.UTF_8.name());
							sResult = sResult.replace(java.io.File.separatorChar,'/');
							sFileName           += "<b><a href='googChromeFileDownload.jsp?FilePath=" + sResult+"&Filename="+FName
												+ "' style='color: #000000' target = 'popup' onclick='window.open(this.href,'MyWindow','width=600,height=600'); return false;'>" + (String) tMap.get("format.file.name") + "</a></b><br/>";
						}
						
																		  
						
					} 
					if(mpObject.size()>0 && !sFileName.equals("")) {
					sbHtmlOutput.append("<tr class='tableborder'><td class='tableborder'>"+sType+"</td><td class='tableborder'>"+sName+"</td><td class='tableborder'>"+sFileName+"</td></tr>");
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return sbHtmlOutput.toString();
	}
	//Added by Preethi Rajaraman for Specification Tab in Chrome Extension -- Ends
	//Added by Preethi Rajaraman for Affected Item Tab in Chrome Extension -- Starts
	public String getAffectedItemList(Context context,String[] args) throws Exception{
		String retStr = "";
		StringList Selects = new StringList(DomainConstants.SELECT_TYPE);
		Selects.add(DomainConstants.SELECT_POLICY);
		Selects.add(DomainConstants.SELECT_NAME);
		Selects.add(DomainConstants.SELECT_ID);
		Selects.add(DomainConstants.SELECT_REVISION);
		Selects.add(DomainConstants.SELECT_CURRENT);
		Selects.add(DomainConstants.SELECT_DESCRIPTION);
		Selects.add("attribute[Reason for Change]");
		Selects.add("physicalid");
		StringList objectSelects = new StringList(DomainConstants.SELECT_TYPE);
		objectSelects.addElement(DomainConstants.SELECT_POLICY);
		objectSelects.addElement(DomainConstants.SELECT_NAME);
		objectSelects.addElement(DomainConstants.SELECT_REVISION);
		objectSelects.addElement(DomainConstants.SELECT_CURRENT);
		objectSelects.addElement(DomainConstants.SELECT_DESCRIPTION);
        objectSelects.add("paths.path");
        objectSelects.add("attribute[Reason for Change]");
        objectSelects.add("attribute[googAfffectedItemAction]");
        objectSelects.add("attribute[googTrueEndItem]");
        objectSelects.add("attribute[googQuantityPerInstance]");
        objectSelects.add("attribute[googInstancePerVehicle]");
        objectSelects.add("attribute[googQtyInInventory]");
        objectSelects.add("attribute[googQtyInField]");
        objectSelects.add("attribute[googQtyToModify]");
        objectSelects.add("attribute[Disposition (In Stock)]");
        objectSelects.add("attribute[Disposition (In Field)]");
        objectSelects.add("attribute[Disposition (On Order)]");
        objectSelects.add("attribute[Disposition (Field Return)]");
        objectSelects.add("attribute[Disposition (In Process)]");
        objectSelects.add("attribute[googSerializationImpacted]");
        objectSelects.add("attribute[googIsTorqueAffected]");
        objectSelects.add("attribute[googIsTorqueCriticalityImpacted]");
        objectSelects.add("attribute[googPartReleaseMaturity]");
	
		StringBuffer sbHtmlOutput 		= new StringBuffer();
		try {
			ContextUtil.pushContext(context);
			String sObjectId 			= args[0];
			DomainObject domObject 		= new DomainObject(sObjectId);
			Map objInfoMap 				= domObject.getInfo(context, Selects);
			String sPolicy 				= (String)objInfoMap.get(DomainConstants.SELECT_POLICY);
			String CAName 				= "";
			String sCAId 				= "";
			String sCADesc				= "";
			String sCAReason			= "";
			if (sPolicy.equals("EC Part")) {
				XORGERPIntegration_mxJPO integrationjpo = new XORGERPIntegration_mxJPO(context,args);
				StringList slCAIDs = integrationjpo.getCAIdsFromPart(context,sObjectId);
				if (slCAIDs!=null && slCAIDs.size() > 0) {
					for(int i=0;i<slCAIDs.size();i++) {
						sCAId = (String) slCAIDs.get(i);
						DomainObject domCA		= new DomainObject(sCAId);
						Map InfoMap 				= domCA.getInfo(context, Selects);
						String strPhysicalId = (String)objInfoMap.get("physicalid");
						String requestedChange 	= new ChangeAction().getRequestedChangeFromChangeAction(context, strPhysicalId, sCAId);
						if (UIUtil.isNotNullAndNotEmpty(requestedChange) && requestedChange.equals("For Release")) {
							CAName		  = (String)InfoMap.get(DomainConstants.SELECT_NAME);
							sCADesc		  = (String)InfoMap.get(DomainConstants.SELECT_DESCRIPTION);
							sCAReason	  = (String)InfoMap.get("attribute[Reason for Change]");
							break;
						}
					}
					sbHtmlOutput.append("<tr><td colspan='23'><b>CA Name : </b>"+CAName+"</td></tr><tr><td colspan='23'><b>Description : </b>"+sCADesc+"</td></tr><tr><td colspan='23'><b>Reason for Change : </b>"+sCAReason+"</td></tr>" );
				} 
			}
			if (UIUtil.isNotNullAndNotEmpty(sCAId)) {
				sObjectId = sCAId;
			}
			DomainObject domCA = new DomainObject(sObjectId);
			StringList slProposedIds = domCA.getInfoList(context,"from[Proposed Activities].to.id");
			if (slProposedIds.size()>0) {
				sbHtmlOutput.append("<tr><th>Identifier</th><th>Revision</th><th>Type</th><th>State</th><th>Release Maturity</th><th>Description</th><th>Reason for Change</th><th>Requested Change</th><th>Action</th><th>True End Item</th><th>Quantity Per Instance</th><th>Instance Per Vehicle</th><th>Qty in Inventory</th><th>Qty in Field</th><th>Qty to Modify</th><th>Disposition (In Stock)</th><th>Disposition (In Field)</th><th>Disposition (On Order)</th><th>Disposition (Field Return)</th><th>Disposition (In Process)</th><th>Is Torque Affected?</th><th>Torque Criticality Impacted?</th><th>Is Serialization Impacted?</th></tr>");
			}
			for(int i=0;i<slProposedIds.size();i++) {
				String proposedId = (String) slProposedIds.get(i);
				DomainObject domProposedObject 	= new DomainObject(proposedId); 
				Map objInfo 					= domProposedObject.getInfo(context, objectSelects);
				if(!objInfo.isEmpty()){
					String sPaths 					= (String)objInfo.get("paths[Where].path");
					String sReasonChange			= (String)objInfo.get("attribute[Reason for Change]");
					String sAction					= (String)objInfo.get("attribute[googAfffectedItemAction]");
					String sEndItem					= (String)objInfo.get("attribute[googTrueEndItem]");
					String sQuantityPerInstance		= (String)objInfo.get("attribute[googQuantityPerInstance]");
					String sInstancepervehicle		= (String)objInfo.get("attribute[googInstancePerVehicle]");
					String sQtyinInventory			= (String)objInfo.get("attribute[googQtyInInventory]");
					String sQtyinField				= (String)objInfo.get("attribute[googQtyInField]");
					String sQtyintoModify			= (String)objInfo.get("attribute[googQtyToModify]");
					String sDisinStock				= (String)objInfo.get("attribute[Disposition (In Stock)]");
					String sDisinField				= (String)objInfo.get("attribute[Disposition (In Field)]");
					String sDisonOrder				= (String)objInfo.get("attribute[Disposition (On Order)]");
					String sDisFieldRet				= (String)objInfo.get("attribute[Disposition (Field Return)]");
					String sDisinProcess			= (String)objInfo.get("attribute[Disposition (In Process)]");
					String sTorqueAffected			= (String)objInfo.get("attribute[googIsTorqueAffected]");
					String sTorqueCritical			= (String)objInfo.get("attribute[googIsTorqueCriticalityImpacted]");
					String sSerImpact				= (String)objInfo.get("attribute[googSerializationImpacted]");
					
					String Paths[] 					= sPaths.split(",");
					String strPhysicalId				= Paths[2];
					DomainObject domPartObject 	= new DomainObject(strPhysicalId); 
					String requestedChange = new ChangeAction().getRequestedChangeFromChangeAction(context, strPhysicalId, sObjectId);
					Map partInfo 					= domPartObject.getInfo(context, objectSelects);
					if(!partInfo.isEmpty()){
						String sName 			= (String)partInfo.get(DomainConstants.SELECT_NAME);
						String sRevision 		= (String)partInfo.get(DomainConstants.SELECT_REVISION);
						String sType	 		= (String)partInfo.get(DomainConstants.SELECT_TYPE);
						String sState	 		= (String)partInfo.get(DomainConstants.SELECT_CURRENT);
						String sDesc	 		= (String)partInfo.get(DomainConstants.SELECT_DESCRIPTION);
						sDesc 					= sDesc.replace("\"", " ");
						String sReleaseMaturity	= (String)partInfo.get("attribute[googPartReleaseMaturity]");
						sbHtmlOutput.append("<tr><td>"+sName+"</td><td>"+sRevision+"</td><td>"+sType+"</td><td>"+sState+"</td><td>"+sReleaseMaturity+"</td><td>"+sDesc+"</td><td>"+sReasonChange+"</td><td>"+requestedChange+"</td><td>"+sAction+"</td><td>"+sEndItem+"</td><td>"+sQuantityPerInstance+"</td><td>"+sInstancepervehicle+"</td><td>"+sQtyinInventory+"</td><td>"+sQtyinField+"</td><td>"+sQtyintoModify+"</td><td>"+sDisinStock+"</td><td>"+sDisinField+"</td><td>"+sDisonOrder+"</td><td>"+sDisFieldRet+"</td><td>"+sDisinProcess+"</td><td>"+sTorqueAffected+"</td><td>"+sTorqueCritical+"</td><td>"+sSerImpact+"</td></tr>");
						
					}
				}
			}
			
		} catch (Exception e) {
			//throw e;
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
		retStr = sbHtmlOutput.toString();
		retStr 					=retStr .replace("\"", " ");
		return retStr ;
	}
	//Added by Preethi Rajaraman for Affected Item Tab in Chrome Extension -- Ends
}