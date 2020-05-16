/*
 ** ${CLASSNAME}
 ** Added by Ravindra for googPersonUtilities
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
import java.io.File;
import java.io.*;
import java.io.BufferedReader;
import matrix.db.BusinessObject;
import com.matrixone.apps.domain.Job;


public class googPersonUtilities_mxJPO
{
	
	public MapList updateTorqueAttributes(Context context, String[] args) throws Exception {
		MapList resultList 		= new MapList();
		Map paramMap 			= (Map) JPO.unpackArgs(args);
		java.io.File oFile 		= (File) paramMap.get("oFile");
		Map  mapDetails 		= null;
		String lineJustFetched 	= null;
		String strStatus		= "";
		String strWPNNamePostion		= "";
		String strWPNRevPostion		= "";
		String[] wordsArray;
		try {
			if (oFile.length() == 0) {
			} else {
				BufferedReader buf 	= new BufferedReader(new FileReader(oFile));
				lineJustFetched 	= buf.readLine();
				wordsArray 			= lineJustFetched.split("\\t");
				mapDetails 			= new HashMap();
				
				if(wordsArray.length==2)
				{
					mapDetails.put("WPNNumber","0");
					mapDetails.put("WPNRevision","1");
				} else if(wordsArray.length==1)
				{
					mapDetails.put("WPNNumber","0");
				}
				if (!mapDetails.isEmpty()) {
					resultList.add(mapDetails);
				}
				buf.close();
			}
			if(resultList != null && resultList.size()>0) {
				Map mMap1 		= (Map) resultList.get(0);
				strWPNNamePostion 		= (String)mMap1.get("WPNNumber");
				strWPNRevPostion 		= (String)mMap1.get("WPNRevision");
				if(UIUtil.isNotNullAndNotEmpty(strWPNRevPostion))
				{
					resultList 		= getBulkEBOM(context,oFile,strWPNNamePostion,strWPNRevPostion);	
				} else {
					
					resultList 		= getBulkEBOM(context,oFile,strWPNNamePostion);	
					
				}
				
			} else {
				resultList 	= resultList;
			}
		} catch(Exception e) { 
            e.printStackTrace();
        }
		return resultList;
	}
	
	
	public MapList getBulkEBOM(Context context, File inputFile ,String strWPNNamePostion , String strWPNRevPostion) throws Exception {
		MapList FinalMapList 		= new MapList();
		MapList mlMEPPartList 		= new MapList();
		BufferedReader br 			= new BufferedReader(new FileReader(inputFile)) ;
		String line;
		Map  mapDetails 			= null;
		int icount				    = 2;
		try {
			while ((line = br.readLine()) != null) 
			{
				mapDetails 			= new HashMap();
				String[] tokens		= line.split("\\t");
				if (!tokens[Integer.parseInt(strWPNNamePostion)].contains("Name")) {
					String strRevVal="";
					try
					{
				 strRevVal=tokens[Integer.parseInt(strWPNRevPostion)];
					}catch(Exception exe)
					{
						
					}
				if(UIUtil.isNullOrEmpty(strRevVal))
				{
					strRevVal="*";
					
				}
					mapDetails.put("PartNumber",tokens[Integer.parseInt(strWPNNamePostion)]);
					mapDetails.put("PartRev",strRevVal);
					mapDetails.put("RowNumber",Integer.toString(icount));
					icount ++;
				if (!mapDetails.isEmpty()) {
					mlMEPPartList.add(mapDetails);
				}
				}
			}
			br.close();
			FinalMapList			= FinalProcess(context,mlMEPPartList);
			FinalMapList.sort("Status","descending","String");
		}catch(Exception e) { 
            e.printStackTrace();
        }
		return FinalMapList;
	}
	
		
	public MapList FinalProcess(Context context, MapList mlMEPPartList) throws Exception {
		MapList mapListFinalProcess		= new MapList();
		String strWPNObjRev			= "";
		String strWPNObjName			= "";
		Map  mapDetails 				= null;
		String validatedMEP				= "";
		String strStatus				= "";
		String strMessage				= "";
		String strMPNName				= "";
		String strRowNumber				= "";
		String StrDesignator			= "";
		String StrQuantity				= "";
		String StrFnumber				= "";
		StringList strList				= new StringList();
		String ObjectCreated			= "";
		String connectedWPNName			= "";
		StringList slNoConnectionRequired = new StringList();
		String strConnectionrequired 	= "";
		StringList strList1				= new StringList();
		StringList strList2				= new StringList();
		String cmd ="";
		String result="";
		try {
			if(mlMEPPartList != null && mlMEPPartList.size()>0) {
				for (int i=0; i< mlMEPPartList.size(); i++) {
					mapDetails 			= new HashMap();
					Map mMap1 			= (Map) mlMEPPartList.get(i);
					strWPNObjName 		= (String)mMap1.get("PartNumber");
					strRowNumber 		= (String)mMap1.get("RowNumber");
					strWPNObjRev 		= (String)mMap1.get("PartRev");
					if (UIUtil.isNotNullAndNotEmpty(strWPNObjName) && UIUtil.isNotNullAndNotEmpty(strWPNObjRev)) {
						if(!strWPNObjRev.equals("*"))
						{
							String mqlArgs1[] = new String[2];
							mqlArgs1[0]		  = strWPNObjName;
							mqlArgs1[1]		  = strWPNObjRev;
							String mqlCmdOIDExists = "print bus Part $1 $2 select exists dump";
							String isObjExists  	= MqlUtil.mqlCommand(context, mqlCmdOIDExists, mqlArgs1);
							if (!UIUtil.isNullOrEmpty(isObjExists) && "true".equalsIgnoreCase(isObjExists)) {
								cmd                          = "exec program $1 $2 $3";
								result                       = MqlUtil.mqlCommand(context, cmd, "googEBOMRelationship_Torque_Attribute_Updation.tcl", strWPNObjName,strWPNObjRev);	
							} else {
								strStatus="Failed";
								strMessage="WPN does not Exist.";
							}
						} else {
							StringBuilder whereExpression 	= new StringBuilder("((policy=='EC Part' )&& ((current=='Release') && (!((next.current=='Release') || (next.current=='Obsolete')))))");
							String sTempQuery 						= MqlUtil.mqlCommand(context, "temp query bus $1 $2 $3 where $4 select $5 dump $6 ", "Part", strWPNObjName,"*",whereExpression.toString(),"revision","|");
							if (UIUtil.isNotNullAndNotEmpty(sTempQuery)) {     
								StringList slobjectsplit 		= FrameworkUtil.split(sTempQuery, "|");
								strWPNObjRev	= (String)slobjectsplit.get(3);
								cmd                          = "exec program $1 $2 $3";
								result                       = MqlUtil.mqlCommand(context, cmd, "googEBOMRelationship_Torque_Attribute_Updation.tcl", strWPNObjName,strWPNObjRev);	
							} else {
								strStatus="Failed";
								strMessage="WPN does not Exist.";
							}
						}
						if(result.equals(""))
						{
							strStatus="Success";
							strMessage="Successfully Updated the Torque Attributes for the EBOM";
						} else {
							strStatus="Failed";
							strMessage=result;
						}
						mapDetails.put("Status",strStatus);
						mapDetails.put("Message",strMessage);
						mapDetails.put("WPNRevision",strWPNObjRev);
						mapDetails.put("Row Number",strRowNumber);
						mapDetails.put("WPNName",strWPNObjName);
						mapListFinalProcess.add(mapDetails);
					}
				}
			}
		}catch(Exception e) { 
            e.printStackTrace();
        }
		return mapListFinalProcess;
	}
	
	
	public MapList getBulkEBOM(Context context, File inputFile ,String strWPNNamePostion) throws Exception {
		MapList FinalMapList 		= new MapList();
		MapList mlMEPPartList 		= new MapList();
		BufferedReader br 			= new BufferedReader(new FileReader(inputFile)) ;
		String line;
		Map  mapDetails 			= null;
		int icount				    = 2;
		try {
			while ((line = br.readLine()) != null) 
			{
				mapDetails 			= new HashMap();
				String[] tokens		= line.split("\\t");
				if (!tokens[Integer.parseInt(strWPNNamePostion)].contains("Name")) {
					
				
					mapDetails.put("PartNumber",tokens[Integer.parseInt(strWPNNamePostion)]);
					mapDetails.put("PartRev","*");
					mapDetails.put("RowNumber",Integer.toString(icount));
					icount ++;
				if (!mapDetails.isEmpty()) {
					mlMEPPartList.add(mapDetails);
				}
				}
			}
			br.close();
			FinalMapList			= FinalProcess(context,mlMEPPartList);
			FinalMapList.sort("Status","descending","String");
		}catch(Exception e) { 
            e.printStackTrace();
        }
		return FinalMapList;
	}
	
	public void recalculateTorqueAttributesUpdate(Context context, String[] args) throws Exception
    {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String sObjectId=(String)programMap.get("objectId");
		try {
			String mqlArgs1[] = new String[2];
			mqlArgs1[0]		  = sObjectId;
			mqlArgs1[1]		  = "|";
			String mqlCmdOIDExists = "print bus $1 select name revision dump $2";
			String mqlCmdResult  	= MqlUtil.mqlCommand(context, mqlCmdOIDExists, mqlArgs1);
			String[] tokens		= mqlCmdResult.split("\\|");
			String cmd                          = "exec program $1 $2 $3";
			String result                       = MqlUtil.mqlCommand(context, cmd, "googEBOMRelationship_Torque_Attribute_Updation.tcl", tokens[0],tokens[1]);
		} catch(Exception e) { 
            e.printStackTrace();
        }
	}
	
}