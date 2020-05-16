	/*
 ** ${CLASS:XORG_Util}
 ** All required methods for Google Loon Project
 ** Added for Release Elixir 1.0
 ** Copyright notice is precautionary only and does not evidence any actual
 ** or intended publication of such program
 */
import com.matrixone.apps.domain.DomainConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
//import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;

import matrix.db.BusinessObject;

import com.dassault_systemes.enovia.changeaction.factory.ChangeActionFactory;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeAction;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeActionServices;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeActionServices.Proposed;
import com.dassault_systemes.enovia.changeaction.interfaces.IRealizedChange;
import com.dassault_systemes.enovia.changeaction.interfaces.IRealizedActivity;
import com.dassault_systemes.enovia.changeaction.interfaces.IProposedActivity;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;

import java.util.Map.Entry;

import com.matrixone.apps.domain.util.EnoviaResourceBundle;

import java.util.Locale;

import com.dassault_systemes.enovia.changeaction.interfaces.IProposedChanges;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.matrixone.apps.domain.DomainRelationship;

import matrix.util.Pattern;

import java.util.Date;

import com.matrixone.apps.common.Person;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.engineering.EngineeringUtil;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.util.DateUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.eMatrixDateFormat;

import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;

import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;														  
import com.matrixone.apps.common.Route;
//Added for Issue #527 Starts
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.library.LibraryCentralConstants;
import java.util.Set;
import java.io.BufferedReader;
import java.io.StringReader;
//Added for Issue #527 Ends
import java.util.Collections;
import matrix.db.AttributeType;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class ${CLASSNAME}
{
	public static final String ATTRIBUTE_XORG_FFF = PropertyUtil.getSchemaProperty("attribute_XORG_FFF");
	public static final String ATTRIBUTE_XORG_FLOAT = PropertyUtil.getSchemaProperty("attribute_XORG_Float");
	public static final String RELATIONSHIP_PROPOSED_ACTIVITIES = PropertyUtil.getSchemaProperty("relationship_ProposedActivities");
	public static final String RELATIONSHIP_REALIZED_ACTIVITIES = PropertyUtil.getSchemaProperty("relationship_RealizedActivities");
	public static final String TYPE_PROPOSED_ACTIVITY = PropertyUtil.getSchemaProperty("type_ProposedActivity");
	public static final String TYPE_REALIZED_ACTIVITY = PropertyUtil.getSchemaProperty("type_RealizedActivity");
	public static final String ATTR_ERP_PART_NUMBER = PropertyUtil.getSchemaProperty("attribute_XORGERPPartNumber");
	public static final String ATTR_IEF_LOCK_INFORMATION = PropertyUtil.getSchemaProperty("attribute_IEF-LockInformation");
	
	//Added by XPLORIA
	public static final String VPM_STATE_RELEASED ="RELEASED";
	public static final String INTERFACE_CHANGE_CONTROL ="Change Control";
	/**
	* Constructor.
	*
	* @param context the eMatrix <code>Context</code> object.
	* @param args holds no arguments.
	* @throws Exception if the operation fails.
	*/
	public ${CLASSNAME} (Context context, String[] args)
	  throws Exception
	{
	}

	/**
	 * Method added by TCS (Shashank) to show the FFF/Float value in table column
	 * Code to show the FFF/Realized value in table column
	 * @param context
	 * @param args
	 * @return StringList of column values
	 * @throws Exception
	 */	
	public StringList showProposedRealizedActivityValue(Context context,String[] args) throws Exception{
		StringList slReturnList = new StringList();
		try {
			ContextUtil.startTransaction(context, false);
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			//HashMap paramList      = (HashMap) programMap.get("paramList");
			//String strCAObjectId = (String) paramList.get("objectId");
			//DomainObject domCAObject = DomainObject.newInstance(context, strCAObjectId);
			//String strCAPhysicalId = domCAObject.getInfo(context, "physicalid");
			HashMap hmSettingsMap      = (HashMap) columnMap.get("settings");
			//String strRealizedOrProposedOrBothObjects = (String)hmSettingsMap.get("AFFECTED_ITEM_TYPE");
			String strAttributeSymbolicName = (String)hmSettingsMap.get("ATTRIBUTE_SYMBOLIC_NAME");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			MapList objectList = (MapList)programMap.get("objectList");
			StringList slActivityObjectSelectable = new StringList(2);
			slActivityObjectSelectable.add("to.attribute["+strAttributeName+"]");
			slActivityObjectSelectable.add("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");
			for(int i=0; i<objectList.size(); i++) {
				Map mObjectListMap = (Map)objectList.get(i);
				//System.out.println("a..................mObjectListMap..............."+mObjectListMap);
				String strConnectionId = (String)mObjectListMap.get(DomainObject.SELECT_RELATIONSHIP_ID);

				String arrRelationshipIds[] = new String[1];
				arrRelationshipIds[0] = strConnectionId;
				MapList mlProposedActivityObjectDetails = DomainRelationship.getInfo(context, arrRelationshipIds, slActivityObjectSelectable);

				String strAttributeValueToDisplay = "";
				for(int j=0; j<mlProposedActivityObjectDetails.size(); j++) {
					Map mProposedActivityObjectDetail = (Map)mlProposedActivityObjectDetails.get(j);
					String strTempAttributeValue = (String)mProposedActivityObjectDetail.get("to.attribute["+strAttributeName+"]");
					if(null == strTempAttributeValue || "".equals(strTempAttributeValue)) {
						strTempAttributeValue = (String)mProposedActivityObjectDetail.get("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");
						if(null == strTempAttributeValue) {
							strTempAttributeValue = "";
						}
					}
					if(!"".equals(strTempAttributeValue)) {
						if(!"".equals(strAttributeValueToDisplay)) {
							strAttributeValueToDisplay += ", ";
						}
						strAttributeValueToDisplay += strTempAttributeValue;
					}
				}
				slReturnList.add(strAttributeValueToDisplay);
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slReturnList;
	}
	
	/**
	 * Method added by TCS (Shashank) to show the Proposed FFF/Float value in Realized table column
	 * @param context
	 * @param args
	 * @return StringList of column values
	 * @throws Exception
	 */	
	public StringList showActivityValueFromProposedRealized(Context context,String[] args) throws Exception{
		StringList slReturnList = new StringList(10);
		try {
			ContextUtil.startTransaction(context, false);
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			HashMap hmSettingsMap      = (HashMap) columnMap.get("settings");
			String strAttributeSymbolicName = (String)hmSettingsMap.get("ATTRIBUTE_SYMBOLIC_NAME");
			String strRealizedOrProposedOrBothObjects = (String)hmSettingsMap.get("AFFECTED_ITEM_TYPE");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			MapList objectList = (MapList)programMap.get("objectList");
			StringList slActivityObjectSelectable = new StringList(2);
			slActivityObjectSelectable.add("attribute["+strAttributeName+"]");
			slActivityObjectSelectable.add("from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");
			DomainObject domProposedActivity = DomainObject.newInstance(context);
			for(int i=0; i<objectList.size(); i++) {
				Map mObjectListMap = (Map)objectList.get(i);
				//System.out.println("a..................mObjectListMap..............."+mObjectListMap);
				String strAffectedItemId = (String)mObjectListMap.get(DomainObject.SELECT_ID);
				String strCAObjectId = (String)mObjectListMap.get("id[parent]");
				DomainObject domCAObject = DomainObject.newInstance(context, strCAObjectId);
				String strCAPhysicalId = domCAObject.getInfo(context, "physicalid");
				
				if("Proposed".equals(strRealizedOrProposedOrBothObjects)) {
					DomainObject domRevisedAffectedItem = DomainObject.newInstance(context, strAffectedItemId);
					String strPreviousId = domRevisedAffectedItem.getInfo(context,"previous.id");
					if(null != strPreviousId) {
						strAffectedItemId = strPreviousId;
					}
				}
				
				StringList slActivityIdsList = getCAOrOtherRelatedObjectIdsFromPart(context, strAffectedItemId, strRealizedOrProposedOrBothObjects, false, strCAPhysicalId);
				//System.out.println("b..................slActivityIdsList..............."+slActivityIdsList);

				String strAttributeValueToDisplay = "";
				for(int j=0; j<slActivityIdsList.size(); j++) {
					String strProposedActivityId = (String)slActivityIdsList.get(j);
					domProposedActivity.setId(strProposedActivityId);
					Map mProposedActivityObjectDetail = domProposedActivity.getInfo(context, slActivityObjectSelectable);
					String strTempAttributeValue = (String)mProposedActivityObjectDetail.get("attribute["+strAttributeName+"]");
					if(null == strTempAttributeValue || "".equals(strTempAttributeValue)) {
						strTempAttributeValue = (String)mProposedActivityObjectDetail.get("from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");
						if(null == strTempAttributeValue) {
							strTempAttributeValue = "";
						}
					}
					if(!"".equals(strTempAttributeValue)) {
						if(!"".equals(strAttributeValueToDisplay)) {
							strAttributeValueToDisplay += ", ";
						}
						strAttributeValueToDisplay += strTempAttributeValue;
					}
				}
				slReturnList.add(strAttributeValueToDisplay);
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slReturnList;
	}
	
	/**
	 * Method added by TCS (Shashank) to update the FFF/Float value of Proposed/Realized object associated with CA
	 * Table Update Function code to update the FFF/Float value of Proposed/Realized object associated with CA
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */	
	public void updateProposedRealizedValue(Context context,String[] args) throws Exception{
		boolean boolIsContextPushed = false;
		try {
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap paramMap      = (HashMap) programMap.get("paramMap");
			String strObjectID = (String)paramMap.get("objectId");
			String strProposedActivityRelId = (String)paramMap.get(ChangeConstants.SELECT_REL_ID);
			HashMap requestMap      = (HashMap) programMap.get("requestMap");
			String strCAObjectId = (String)requestMap.get("objectId");
			ContextUtil.startTransaction(context, true);
			DomainObject domCAObject = DomainObject.newInstance(context, strCAObjectId);
			String strCAPhysicalId = domCAObject.getInfo(context, "physicalid");
			String strNewAttrValue = (String) paramMap.get("New Value");
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			HashMap hmSettingsMap      = (HashMap) columnMap.get("settings");
			String strRealizedOrProposedOrBothObjects = (String)hmSettingsMap.get("AFFECTED_ITEM_TYPE");
			String strAttributeSymbolicName = (String)hmSettingsMap.get("ATTRIBUTE_SYMBOLIC_NAME");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			
			StringList slActivityObjectSelectable = new StringList(4);
			slActivityObjectSelectable.add("to.attribute["+strAttributeName+"]");
			slActivityObjectSelectable.add("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");slActivityObjectSelectable.add("to."+DomainObject.SELECT_ID);
			slActivityObjectSelectable.add("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to."+DomainObject.SELECT_ID);
			
			ContextUtil.pushContext(context);
			boolIsContextPushed = true;
			String arrRelationshipIds[] = new String[1];
			arrRelationshipIds[0] = strProposedActivityRelId;
			MapList mlProposedActivityObjectDetails = DomainRelationship.getInfo(context, arrRelationshipIds, slActivityObjectSelectable);
			for(int j=0; j<mlProposedActivityObjectDetails.size(); j++) {
				Map mProposedActivityObjectDetail = (Map)mlProposedActivityObjectDetails.get(j);
				String strTempProposedActivityId = (String)mProposedActivityObjectDetail.get("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to."+DomainObject.SELECT_ID);
				String strTempAttributeValue = (String)mProposedActivityObjectDetail.get("to.from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.attribute["+strAttributeName+"]");
				if(null == strTempProposedActivityId || "".equals(strTempProposedActivityId)) {
					strTempProposedActivityId = (String)mProposedActivityObjectDetail.get("to."+DomainObject.SELECT_ID);
					strTempAttributeValue = (String)mProposedActivityObjectDetail.get("to.attribute["+strAttributeName+"]");
				}
				if(null != strTempProposedActivityId) {
					DomainObject domActivityObject = DomainObject.newInstance(context, strTempProposedActivityId);
					if(!strTempAttributeValue.equals(strNewAttrValue)) {
						domActivityObject.setAttributeValue(context, strAttributeName, strNewAttrValue);
					}
				}
			}
			ContextUtil.popContext(context);
			boolIsContextPushed = false;
			ContextUtil.commitTransaction(context);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	
	/**
	 * Method to get connected CAs or Realized Activity object or Proposed Activity object from Part
	 * @param context
	 * @param args
	 * @return StringList
	 */
	public StringList getCAOrOtherRelatedObjectIdsFromPart(Context context, String sPartId, String strRealizedOrProposedOrBothObjects, boolean boolIsAssociatedCAObjectRequested, String strCurrentChangeActionPhysicalId) throws Exception {
		StringList slCAsList = new StringList();
		BusinessObject busObjePart = new BusinessObject(sPartId) ;
		List<BusinessObject> lBusoBject = new ArrayList<BusinessObject>();
		lBusoBject.add(busObjePart);
		try {
			//ContextUtil.startTransaction(context,true);
			ChangeActionFactory factory = new ChangeActionFactory();
			IChangeActionServices changeAction = factory.CreateChangeActionFactory();
			
			///////// Realized			
			if("Realized".equalsIgnoreCase(strRealizedOrProposedOrBothObjects) || "Both".equalsIgnoreCase(strRealizedOrProposedOrBothObjects)) {
				Map<String, Map<IChangeAction, List<IRealizedChange>>> mapRealizedAndCaLinked = changeAction.getRealizedAndCaFromListObjects(context, lBusoBject, false, true, false);
				
				for(Entry<String, Map<IChangeAction,List<IRealizedChange>>> mapOutput : mapRealizedAndCaLinked.entrySet()){
					for(Entry<IChangeAction,List<IRealizedChange>> mapOutput2: mapOutput.getValue().entrySet()){
						// Code to get the Realized CA object and validate if its the current CA object
						String strChangeActionID = "";
						IChangeAction iChangeAction = mapOutput2.getKey();
						if(iChangeAction!= null){
							BusinessObject busChangeAction =  iChangeAction.getCaBusinessObject();
							strChangeActionID = busChangeAction.getObjectId();
							if(null != strCurrentChangeActionPhysicalId && !(strCurrentChangeActionPhysicalId).equals(strChangeActionID)) {
								continue;
							}
						}
						DomainObject domChangeActionObject = DomainObject.newInstance(context, strChangeActionID);
						String requestedChange = new ChangeAction().getRequestedChangeFromChangeAction(context, sPartId, domChangeActionObject.getInfo(context, DomainObject.SELECT_ID));
						if(requestedChange.equalsIgnoreCase("For Revise") || requestedChange.equalsIgnoreCase("For Major Revise") || requestedChange.equalsIgnoreCase("For Release")) {
							if(!boolIsAssociatedCAObjectRequested) {
								// Code to get the Realized Activity object
								List<IRealizedChange> realizedList = mapOutput2.getValue();
								for(int k=0; k<realizedList.size(); k++) {
									IRealizedChange objIRealizedChangeObject = (IRealizedChange)realizedList.get(k);
									List<IRealizedActivity> realizedActivityList = objIRealizedChangeObject.getActivites();
									for(int l=0; l<realizedActivityList.size(); l++) {
										IRealizedActivity objIRealizedActivity = (IRealizedActivity)realizedActivityList.get(l);
										BusinessObject boRealizedActivityObject = objIRealizedActivity.getBusinessObject();
										String strRealizedActivityID = boRealizedActivityObject.getObjectId();
										slCAsList.add(strRealizedActivityID);
									}
								}
							} else {
								// Code to get the Realized CA object
								if(iChangeAction!= null){
									slCAsList.add(strChangeActionID);
								}
							}
						}
					}
				}
			}
			
			///////// Proposed
			if("Proposed".equalsIgnoreCase(strRealizedOrProposedOrBothObjects) || "Both".equalsIgnoreCase(strRealizedOrProposedOrBothObjects)) {
				Map<String,Map<IChangeAction,Proposed>> ProposedAndCaLinked = changeAction.getProposedAndCaFromListObject(context, lBusoBject);
				for(Entry <String,Map<IChangeAction,Proposed>> objectMapEntry : ProposedAndCaLinked.entrySet()){
					for(Entry <IChangeAction,Proposed> proposedEntry : objectMapEntry.getValue().entrySet()){
						// Code to get the Proposed CA object and validate if its the current CA object
						String strChangeActionID = "";
						IChangeAction iChangeAction = proposedEntry.getKey();
						if(iChangeAction!= null){
							BusinessObject busChangeAction =  iChangeAction.getCaBusinessObject();
							strChangeActionID = busChangeAction.getObjectId();
							if(null != strCurrentChangeActionPhysicalId && !(strCurrentChangeActionPhysicalId).equals(strChangeActionID)) {
								continue;
							}
						}
						//DomainObject domChangeActionObject = DomainObject.newInstance(context, strChangeActionID);
						//String requestedChange = new ChangeAction().getRequestedChangeFromChangeAction(context, sPartId, domChangeActionObject.getInfo(context, DomainObject.SELECT_ID));
						//if(requestedChange.equalsIgnoreCase("For Revise") || requestedChange.equalsIgnoreCase("For Major Revise") || requestedChange.equalsIgnoreCase("For Release")) {
							if(!boolIsAssociatedCAObjectRequested) {
								// Code to get the Proposed Activity object
								Proposed objIChangeActionServicesDotProposed = proposedEntry.getValue();
								List<IProposedActivity> proposedActivityList = objIChangeActionServicesDotProposed._activities;
								if(null == proposedActivityList) {
									List<IProposedChanges> proposedChangesList = iChangeAction.getProposedChanges(context);
									for(int l=0; l<proposedChangesList.size(); l++) {
										IProposedChanges objIProposedChanges = (IProposedChanges)proposedChangesList.get(l);
										proposedActivityList = objIProposedChanges.getActivites();
										if(null == proposedActivityList || proposedActivityList.size()==0) {
											BusinessObject boAffectedItemObject = objIProposedChanges.getWhere();
											String strAffectedItemPhysicalID = boAffectedItemObject.getObjectId();
											DomainObject domPartObject = new DomainObject(busObjePart);
											if(strAffectedItemPhysicalID.equals(domPartObject.getInfo(context, "physicalid"))) {
												BusinessObject boProposedObject = objIProposedChanges.getBusinessObject();
												String strProposedID = boProposedObject.getObjectId();
												if(!slCAsList.contains(strProposedID)) {
													slCAsList.add(strProposedID);
												}
											}
										}
									}
								}
								if(null != proposedActivityList) {
									for(int l=0; l<proposedActivityList.size(); l++) {
										IProposedActivity objIProposedActivity = (IProposedActivity)proposedActivityList.get(l);
										BusinessObject boProposedActivityObject = objIProposedActivity.getBusinessObject();
										String strProposedActivityID = boProposedActivityObject.getObjectId();
										if(!slCAsList.contains(strProposedActivityID)) {
											slCAsList.add(strProposedActivityID);
										}
									}
								}
							} else {
								// Code to get the Proposed CA object
								if(iChangeAction!= null){
									slCAsList.add(strChangeActionID);
								}
							}
						//}
					}
				}
			}
			//ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slCAsList;
	}
	
	/**
	 * Generic table range function to get attribute ranges for attribute name passed
	 * @param context
	 * @param args
	 * @return Map of attribute ranges
	 * @throws Exception
	 */
	public Map getAttributeRanges(Context context, String args[]) throws Exception {
		Map mReturnRangeMap = new HashMap();
		try {
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			HashMap hmSettingsMap      = (HashMap) columnMap.get("settings");
			String strAttributeSymbolicName = (String)hmSettingsMap.get("ATTRIBUTE_SYMBOLIC_NAME");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);

			StringList slAttributeRanges = new StringList(5);
			slAttributeRanges = FrameworkUtil.getRanges(context, strAttributeName);
			mReturnRangeMap.put("field_choices", slAttributeRanges);
			mReturnRangeMap.put("field_display_choices", slAttributeRanges);
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return mReturnRangeMap;
    }
	
	/**
	 * Check trigger to validate the FFF and Float value of the Proposed Activity object 
	 * on promotion of the CA.
	 * @param context
	 * @param args
	 * @return integer value
	 * @throws Exception
	 */	
	public int validateFFFFloatValue(Context context, String args[]) throws Exception {
		int iReturnValue = 0;
		try {
			ContextUtil.startTransaction(context, false);
			StringList slAffectedItemSelectable = new StringList(2);
			slAffectedItemSelectable.add(DomainObject.SELECT_NAME);
			slAffectedItemSelectable.add(DomainObject.SELECT_TYPE);
			StringList slSelectable = new StringList(5);
			slSelectable.add(DomainObject.SELECT_ID);
			slSelectable.add("attribute[" + ATTRIBUTE_XORG_FFF + "]");
			slSelectable.add("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
			slSelectable.add("from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.paths["+TYPE_PROPOSED_ACTIVITY+".What].path.element[0].physicalid");
			slSelectable.add("paths["+TYPE_PROPOSED_ACTIVITY+".What].path.element[0].physicalid");
			String strChangeActionObjectId = args[0];
			ContextUtil.pushContext(context);
			String strConnectedParts = "";
			DomainObject domPartObject = DomainObject.newInstance(context);
			ChangeAction caChangeActionObject = new ChangeAction(strChangeActionObjectId);
			MapList mlAffectedItemsObjectList = caChangeActionObject.getAffectedItems(context);
			for(int i=0; i<mlAffectedItemsObjectList.size(); i++) {
				Map mAffectedItemsObjectMap = (Map)mlAffectedItemsObjectList.get(i);
				String strAffectedItemObjectId = (String)mAffectedItemsObjectMap.get(DomainObject.SELECT_ID);
				String strAffectedItemObjectType = (String)mAffectedItemsObjectMap.get(DomainObject.SELECT_TYPE);
				String strAffectedItemObjectName = (String)mAffectedItemsObjectMap.get(DomainObject.SELECT_NAME);
				if((DomainObject.TYPE_PART).equals(strAffectedItemObjectType)) {
					String strCAPhysicalId = caChangeActionObject.getInfo(context, "physicalid");
					StringList slActivityIdsList = getCAOrOtherRelatedObjectIdsFromPart(context, strAffectedItemObjectId, "Proposed", false, strCAPhysicalId);
					DomainObject domActivityObject = DomainObject.newInstance(context);
					boolean boolIsValidAffectedItem = false;
					for(int j=0; j<slActivityIdsList.size(); j++) {
						domActivityObject.setId((String)slActivityIdsList.get(j));
						Map mProposedRealizedActivityMap = domActivityObject.getInfo(context, slSelectable);
						String strFFFValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FFF + "]");
						String strFloatValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
						if(null!=strFFFValue && null!=strFloatValue && !"".equals(strFFFValue) && !"".equals(strFloatValue)) {
							boolIsValidAffectedItem = true;
						}
					}
					if(!boolIsValidAffectedItem) {
						if(!"".equals(strConnectedParts)) {
							strConnectedParts += ", ";
						}
						strConnectedParts += strAffectedItemObjectName;
						iReturnValue = 1;
					}
				}
			}
			if(1 == iReturnValue) {
				String strFFFFloatValueNotSetMessage = EnoviaResourceBundle.getProperty(context,"emxEnterpriseChangeMgtStringResource", new Locale(context.getSession().getLanguage()),"EnterpriseChangeMgt.Alert.FloatOnRevise.FFFAndFloatValueNotSet") + strConnectedParts;
				${CLASS:emxContextUtil}.mqlNotice(context, strFFFFloatValueNotSetMessage);
			}
			ContextUtil.commitTransaction(context);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
		return iReturnValue;
	}
	
	/**
	 * Method added by TCS (Shashank) for Float on Revise functionality
	 * Action trigger on attribute FFF and Float modification to update the same attribute on the Realized Activity object for the same value 
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void updateRealizedFFFFloatValueOnProposedUpdate(Context context, String args[]) throws Exception {
		boolean boolIsContextPushed = false;
		try {
			ContextUtil.startTransaction(context, true);
			String strObjectId = args[0];
			String strNewAttributeValue = args[1];
			String strObjectType = args[2];
			String strObjectState = args[3];
			String strAttributeSymbolicName = args[4];
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			if((TYPE_PROPOSED_ACTIVITY).equals(strObjectType)) {
				ContextUtil.pushContext(context);
				boolIsContextPushed = true;
				DomainObject domActivityObject = DomainObject.newInstance(context, strObjectId);
				String strConnectedPartObject = domActivityObject.getInfo(context, "from["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].to.paths["+TYPE_PROPOSED_ACTIVITY+".What].path.element[0].physicalid");
				String strConnectedCAPhysicalId = domActivityObject.getInfo(context, "to["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].from.physicalid");
				if(null == strConnectedPartObject) {
					strConnectedPartObject = domActivityObject.getInfo(context, "paths["+TYPE_PROPOSED_ACTIVITY+".What].path.element[0].physicalid");
					strConnectedCAPhysicalId = domActivityObject.getInfo(context, "to["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].from.to["+RELATIONSHIP_PROPOSED_ACTIVITIES+"].from.physicalid");
				}
				if(null != strConnectedPartObject) {
					StringList slAllRealizedActivityObjectIds = new StringList(5);
					DomainObject domAffectedItemObject = DomainObject.newInstance(context, strConnectedPartObject);
					MapList mlAllAffectedItemsRevisions = domAffectedItemObject.getRevisionsInfo(context, new StringList(DomainObject.SELECT_ID), new StringList(1));
					for(int i=0; i<mlAllAffectedItemsRevisions.size(); i++) {
						Map mAllAffectedItemsRevisionDetailsMap = (Map)mlAllAffectedItemsRevisions.get(i);
						String strAffectedItemObjectId = (String)mAllAffectedItemsRevisionDetailsMap.get(DomainObject.SELECT_ID);
						slAllRealizedActivityObjectIds.addAll(getCAOrOtherRelatedObjectIdsFromPart(context, strAffectedItemObjectId, "Realized", false, strConnectedCAPhysicalId));
					}
					for(int j=0; j<slAllRealizedActivityObjectIds.size(); j++) {
						String strRealizedObjectId = (String)slAllRealizedActivityObjectIds.get(j);
						domActivityObject.setId(strRealizedObjectId);
						domActivityObject.setAttributeValue(context, strAttributeName, strNewAttributeValue);
					}
				}
				ContextUtil.popContext(context);
				boolIsContextPushed = false;
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Method added by TCS (Shashank) for Float on Revise functionality
	 * Action trigger on Part revise to float the relationship to new revision 
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	/*public void floatEBOMToEndOnRevise(Context context, String args[]) throws Exception {
		boolean boolIsContextPushed = false;
		try {
			ContextUtil.startTransaction(context, true);
			String strNewRevisedObjectId = args[0];
			String strObjectType = args[1];
			String strObjectName = args[2];
			DomainObject domNewRevisedObject = DomainObject.newInstance(context, strNewRevisedObjectId);
			String strPreviousId = domNewRevisedObject.getInfo(context,"previous.id");
			if(null == strPreviousId || "".equals(strPreviousId)) {
				String strNewPartNumber = generateERPPartNumber(context, domNewRevisedObject);
				domNewRevisedObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strNewPartNumber);
				return;
			}
			StringList slActivityObjectsIdList = getCAOrOtherRelatedObjectIdsFromPart(context, strPreviousId, "Proposed", false, null);
			//if(slActivityObjectsIdList.size() == 0) {
			//	slActivityObjectsIdList = getCAOrOtherRelatedObjectIdsFromPart(context, strPreviousId, "Realized", false, null);
			//}

			if(slActivityObjectsIdList.size() > 0) {
				ContextUtil.pushContext(context);
				boolIsContextPushed = true;
				StringList slSelectable = new StringList(2);
				slSelectable.add("attribute[" + ATTRIBUTE_XORG_FFF + "]");
				slSelectable.add("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
				String strActivityObjectId = (String)slActivityObjectsIdList.get(0);
				DomainObject domActivity = DomainObject.newInstance(context, strActivityObjectId);
				Map mProposedRealizedActivityMap = domActivity.getInfo(context, slSelectable);
				String strFFFValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FFF + "]");
				String strFloatValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
				 // Float Start 
				if("Yes".equals(strFloatValue)) {
					String arrPartObjectId[] = new String[1];
					arrPartObjectId[0] = strNewRevisedObjectId;
					${CLASS:emxBOMPartManagement} objBOMPartManagement = new ${CLASS:emxBOMPartManagement}(context, arrPartObjectId);
					objBOMPartManagement.floatEBOMToEnd(context, args);
				} // Float Yes End
				// FFF Start  
					if("Yes".equals(strFFFValue)) {
						String strNewPartNumber = generateERPPartNumber(context, domNewRevisedObject);
						domNewRevisedObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strNewPartNumber);
					} else {
						DomainObject domPreviousRevisionObject = DomainObject.newInstance(context, strPreviousId);
						String strPreviousERPPartNumber = domPreviousRevisionObject.getInfo(context,"attribute["+ATTR_ERP_PART_NUMBER+"]");
						domNewRevisedObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strPreviousERPPartNumber);
					}
				// FFF End
				ContextUtil.popContext(context);
				boolIsContextPushed = false;
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}*/
	
	/**
	 * Method added by TCS (Shashank) for Float on Revise functionality to generate the new ERP Part number
	 * @param context
	 * @param DomainObject domPartObject
	 * @return String of new generated ERP Part Number
	 * @throws Exception
	 */	
	public String generateERPPartNumber(Context context, DomainObject domPartObject) {
		String strNewERPPartNumber = "";
		try {
			// Get all Revisions information for getting the next sequence number
			StringList slPartBusSelect = new StringList(DomainObject.SELECT_ID);
			slPartBusSelect.add("attribute["+ATTR_ERP_PART_NUMBER+"]");
			StringList multiValueSelects = new StringList();
			/*MapList mlAllPartRevisions = domPartObject.getRevisionsInfo(context, slPartBusSelect,multiValueSelects);
			StringList slAllPartRevisionERPPartNumberList = new StringList();
			for(int i=0;i<mlAllPartRevisions.size();i++){
				Map mAllPartRevisionsMap =(Map)mlAllPartRevisions.get(i);
				String strERPPartNumber = (String)mAllPartRevisionsMap.get("attribute["+ATTR_ERP_PART_NUMBER+"]");
				if(!slAllPartRevisionERPPartNumberList.contains(strERPPartNumber) && strERPPartNumber != null && !"".equals(strERPPartNumber)) {
					slAllPartRevisionERPPartNumberList.add(strERPPartNumber);
				}
			}
			String strMaxSeqNumber = String.format("%02d",(slAllPartRevisionERPPartNumberList.size() + 1));*/
			String strPreviousRevisionObjectId = domPartObject.getInfo(context, "previous.id");
			int iECounterValue = 0;
			if(null != strPreviousRevisionObjectId) {
				DomainObject domPreviousRevisionObject = DomainObject.newInstance(context, strPreviousRevisionObjectId);
				String strPreviousRevisionERPPartNumber = domPreviousRevisionObject.getInfo(context, "attribute["+ATTR_ERP_PART_NUMBER+"]");
				int iStartIndexOfCounterInValue = strPreviousRevisionERPPartNumber.lastIndexOf("-E") + 2;
				if(iStartIndexOfCounterInValue != -1) {
					String strECounterValue = strPreviousRevisionERPPartNumber.substring(iStartIndexOfCounterInValue);
					try {
						iECounterValue = Integer.parseInt(strECounterValue);
					} catch(NumberFormatException nfe) {
						iECounterValue = 1;
						//nfe.printStackTrace();
					}
				} else {
					iECounterValue = 1;
				}
			}
			String strMaxSeqNumber = String.format("%02d",(iECounterValue + 1));

			// Incrementing the existing ERP Part Number value
			String strERPPartSuffix = "E";
			slPartBusSelect.add(DomainObject.SELECT_NAME);
			Map mPartobjInfo = domPartObject.getInfo(context,slPartBusSelect);
			String strPartName = (String)mPartobjInfo.get(DomainObject.SELECT_NAME);
			//String strOldERPPartNumber = (String)mPartobjInfo.get("attribute["+ATTR_ERP_PART_NUMBER+"]");
			strNewERPPartNumber = strPartName + "-" + strERPPartSuffix + strMaxSeqNumber;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strNewERPPartNumber;
	}


	  /**
	 * Method added by TCS (Shashank) for Float on Revise functionality
	 * Action trigger on attribute Requested Change modification if the new value is not 
	 * "For Revise", "For Major Revise" or "For Release" then update the FFF and Float value to no.
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void updateRealizedFFFFloatValueOnRequestedChangeUpdate(Context context, String args[]) throws Exception {
		boolean boolIsContextPushed = false;
		try {
			String strObjectId = args[0];
			String strNewAttributeValue = args[1];
			if(!"For Revise".equals(strNewAttributeValue) && !"For Major Revise".equals(strNewAttributeValue) && !"For Release".equals(strNewAttributeValue)) {
				ContextUtil.pushContext(context);
				boolIsContextPushed = true;
				DomainObject domActivityObject = DomainObject.newInstance(context, strObjectId);
				domActivityObject.setAttributeValue(context, ATTRIBUTE_XORG_FFF, "No");
				domActivityObject.setAttributeValue(context, ATTRIBUTE_XORG_FLOAT, "No");
				ContextUtil.popContext(context);
				boolIsContextPushed = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Method added by TCS (Shashank) for Adding context user as assignee on Demotion of a CA
	 * Action trigger on CA demotion from In Approval to In Work state
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */	
	
	
	/**
	 * Method added by TCS (Shashank) for Float on Revise functionality
	 * Action trigger on CA Completion to float the Parts EBOM relationship to new revision 
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	public void floatEBOMToEndOnRevise_FromCA(Context context, String args[]) throws Exception {
		boolean boolIsContextPushed = false;
		try {
			ContextUtil.startTransaction(context, true);
			ContextUtil.pushContext(context);
			boolIsContextPushed = true;
			String strCAObjectId = args[0];
			ChangeAction caChangeActionObject = new ChangeAction(strCAObjectId);
			
			DomainObject domCAObject = DomainObject.newInstance(context, strCAObjectId);
			String strCAPhysicalId = domCAObject.getInfo(context, "physicalid");
			MapList mlAffectedItemsObjectList = caChangeActionObject.getAffectedItems(context);

			StringList slSelectable = new StringList(2);
			slSelectable.add("attribute[" + ATTRIBUTE_XORG_FFF + "]");
			slSelectable.add("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
			DomainObject domPreviousObject = DomainObject.newInstance(context);
			DomainObject domActivity = DomainObject.newInstance(context);
			DomainObject domNewRevisedObject = DomainObject.newInstance(context);
			for(int i=0; i<mlAffectedItemsObjectList.size(); i++) {
				String strPartRequestedChange = (String)((Map)mlAffectedItemsObjectList.get(i)).get("Requested Change");
				String strPreviousId = (String)((Map)mlAffectedItemsObjectList.get(i)).get(DomainObject.SELECT_ID);
				String strPreviousObjectType = (String)((Map)mlAffectedItemsObjectList.get(i)).get(DomainObject.SELECT_TYPE);
				if((DomainObject.TYPE_PART).equals(strPreviousObjectType)) {
					domPreviousObject.setId(strPreviousId);
					StringList slActivityObjectsIdList = getCAOrOtherRelatedObjectIdsFromPart(context, strPreviousId, "Proposed", false, strCAPhysicalId);
					String strFFFValue = "";
					String strFloatValue = "";
					if(slActivityObjectsIdList.size() > 0) {
						String strActivityObjectId = (String)slActivityObjectsIdList.get(0);
						domActivity.setId(strActivityObjectId);
						Map mProposedRealizedActivityMap = domActivity.getInfo(context, slSelectable);
						strFFFValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FFF + "]");
						strFloatValue = (String)mProposedRealizedActivityMap.get("attribute[" + ATTRIBUTE_XORG_FLOAT + "]");
					}
					if("For Revise".equals(strPartRequestedChange) || "For Major Revise".equals(strPartRequestedChange)) {
						String strNewRevisedObjectId = domPreviousObject.getInfo(context, "next.id");
						domNewRevisedObject.setId(strNewRevisedObjectId);

						if(slActivityObjectsIdList.size() > 0) {
							 // Float Start 
							if("Yes".equals(strFloatValue)) {
								String arrPartObjectId[] = new String[1];
								arrPartObjectId[0] = strNewRevisedObjectId;
								${CLASS:emxBOMPartManagement} objBOMPartManagement = new ${CLASS:emxBOMPartManagement}(context, arrPartObjectId);
								objBOMPartManagement.floatEBOMToEnd(context, args);
							} // Float Yes End
							// FFF Start  
							if("Yes".equals(strFFFValue)) {
								String strNewPartNumber = generateERPPartNumber(context, domNewRevisedObject);
								domNewRevisedObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strNewPartNumber);
								populateCADObjectswithERPPartNumber(context,strNewPartNumber,domNewRevisedObject);
							} else {
								String strPreviousERPPartNumber = domPreviousObject.getInfo(context,"attribute["+ATTR_ERP_PART_NUMBER+"]");
								domNewRevisedObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strPreviousERPPartNumber);
								populateCADObjectswithERPPartNumber(context,strPreviousERPPartNumber,domNewRevisedObject);
							}
							// FFF End
						}
					} else if("For Release".equals(strPartRequestedChange)) {
						String strExistingERPPartNumberAttributeValue = domPreviousObject.getAttributeValue(context, ATTR_ERP_PART_NUMBER);
						if(null == strExistingERPPartNumberAttributeValue || "".equals(strExistingERPPartNumberAttributeValue)) {
							String strPreviousRevisionObjectId = domPreviousObject.getInfo(context, "previous.id");
							String strNewPartNumber = "";
							if(null != strPreviousRevisionObjectId) {
								DomainObject domPreviousRevisionObject = DomainObject.newInstance(context, strPreviousRevisionObjectId);
								if("Yes".equals(strFFFValue)) {
									strNewPartNumber = generateERPPartNumber(context, domPreviousObject);
								} else {
									strNewPartNumber = domPreviousRevisionObject.getInfo(context, "attribute["+ATTR_ERP_PART_NUMBER+"]");
								}
							} else {
								strNewPartNumber = generateERPPartNumber(context, domPreviousObject);
							}
							domPreviousObject.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strNewPartNumber);
							populateCADObjectswithERPPartNumber(context,strNewPartNumber,domPreviousObject);
						}
					}
				}
			}
			ContextUtil.popContext(context);
			boolIsContextPushed = false;
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Access Function to check if the selected object attribute value is not blank
	 * @param context
	 * @param args
	 * @return boolean true/false
	 * @throws Exception
	 */
	public boolean isAttributeValueNonBlank(Context context, String args[]) {
		boolean boolReturnValue = false;
		try {
			HashMap programMap = (HashMap)JPO.unpackArgs(args);
			HashMap hmSettingMap = (HashMap)programMap.get("SETTINGS");
			String strAttributeSymbolicName = (String)hmSettingMap.get("XORG_ATTRIBUTE_SYMBOLIC_NAME");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			String strObjectId = (String)programMap.get("objectId");
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			String strAttributeValue = domObject.getInfo(context, "attribute["+strAttributeName+"]");
			if(null != strAttributeValue && !"".equals(strAttributeValue)) {
				boolReturnValue = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return boolReturnValue;
	}
	
	/**
	 * Method added by TCS (Shashank) for enhacement Issue #107
	 * Action trigger on relationship Part Specification creation to update the Title attribute on the Specification
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void updateDocumentTitleToConnectedPartName(Context context, String args[]) throws Exception {
		boolean boolIsContextPushed = false;
		try {
			String strPartObjectId = args[0];
			String strSpecObjectId = args[1];
			String strPartObjectName = args[2];
			String strSpecObjectName = args[3];
			ContextUtil.pushContext(context);
			boolIsContextPushed = true;
			DomainObject domDocument = DomainObject.newInstance(context, strSpecObjectId);
			String strExistingTitleValue = domDocument.getInfo(context, "attribute["+DomainObject.ATTRIBUTE_TITLE+"]");
			//StringList slExistingTitleValueList = FrameworkUtil.split(strExistingTitleValue, ",");
			if("".equals(strExistingTitleValue)) {
				domDocument.setAttributeValue(context, DomainObject.ATTRIBUTE_TITLE, strPartObjectName);
			} /*else if(!slExistingTitleValueList.contains(strPartObjectName)) {
				//domDocument.setAttributeValue(context, DomainObject.ATTRIBUTE_TITLE, strExistingTitleValue + "," + strPartObjectName);
				domDocument.setAttributeValue(context, DomainObject.ATTRIBUTE_TITLE, strPartObjectName);
			}*/
			ContextUtil.popContext(context);
			boolIsContextPushed = false;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Generic Webform function that returns the attribute readonly field for attribute name passed
	 * @param context
	 * @param args
	 * @return String
	 * @throws Exception
	 */
	public String getAttributeFieldForWebform(Context context, String args[]) {
		String strReturnValue = "";
		try {
			HashMap programMap = (HashMap)JPO.unpackArgs(args);
			HashMap requestMap = (HashMap)programMap.get("requestMap");
			String strObjectId = (String)requestMap.get("objectId");
			String strMode = (String)requestMap.get("mode");
			HashMap fieldMap = (HashMap)programMap.get("fieldMap");
			String strFieldName = (String)fieldMap.get("name");
			HashMap hmSettingMap = (HashMap)fieldMap.get("settings");
			String strAttributeName = PropertyUtil.getSchemaProperty(context, (String)hmSettingMap.get("Admin Type"));
			if(null != strObjectId) {
				DomainObject domObject = DomainObject.newInstance(context, strObjectId);
				String strDefaultAttributeValue = domObject.getInfo(context, DomainObject.SELECT_NAME);
				strReturnValue = "<input type='text' name='"+strFieldName+"' id='"+strFieldName+"' value='"+strDefaultAttributeValue+"' readonly='true'/>";
			} else {
				strReturnValue = "<input type='text' name='"+strFieldName+"' id='"+strFieldName+"' value=''/>";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturnValue;
	}
	
	
	/**
	 * Method added by TCS (Shashank) for Issue #190
	 * Generic action trigger on Promote/Demote to send notification to specific users
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void notifyUserOnPromoteDemote(Context context, String args[]) throws Exception {
		try {
			String strStateLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.State");
			String strAssigneeLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.Assignee");
			String strTaskOrSignatureLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.TaskOrSignature");
			String strTaskTitleLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.TaskTitle");
			String strCommentsOrInstructionsLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.CommentsOrInstructions");
			String strActionLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.Action");
			String strDueDateLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.DueDate");
			String strCompletedDateLabel = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(context.getSession().getLanguage()),"emxFramework.LifecycleTasks.CompletedDate");
			//#463- CA Notifications 'From' should be changed to service account-start
			String strPageFileName = "emxComponents.properties";
			String MQLResult  	= MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes 		= MQLResult.getBytes("UTF-8");
			InputStream MQLinput 	= new ByteArrayInputStream(bytes);
			Properties prop = new Properties();
			prop.load(MQLinput);
			String strFromServiceAccount = prop.getProperty("eServiceSuiteComponents.CA_Notification_Email_From_Agent");
			//#463- CA Notifications 'From' should be changed to service account-end
			String strObjectId = args[0];
			String strSubjectKey = args[1];
			String strMessageKey = args[2];
			String strPropertyKey = args[3];
			String strUserCategory = args[4];
			String strObjectType = args[5];
			String strObjectName = args[6];
			String strObjectState = args[7];
			String strCATaskStatus = args[8];
			String strCAReviewerMessageKey = args[9];
			List toList = null;
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			String strOriginator = domObject.getInfo(context, "attribute["+DomainObject.ATTRIBUTE_ORIGINATOR+"]");
			String strLanguage = context.getSession().getLanguage();
			String[] subjectValues = new String[2];
			subjectValues[0] = strObjectName;
			String strHTMLMailMessage = "<br><html><body><br>";
			strHTMLMailMessage += "<style>body, th, td {font-family:Verdana;font-size:11px;text-align:left;padding:5px;}</style>";
			String strIconMailMessage = "";
			String strBaseURL = ${CLASS:emxMailUtilBase}.getBaseURL(context, null);
			String agentName = ${CLASS:emxMailUtil}.getAgentName(context, null);
			int iDateFormat = eMatrixDateFormat.getEMatrixDisplayDateFormat();
			DateFormat dateFormat = DateFormat.getDateInstance(iDateFormat, new Locale(context.getSession().getLanguage()));
			if(null != strUserCategory && "Follower".equals(strUserCategory)) {
				//#463- CA Notifications 'From' should be changed to service account-start
				agentName = strFromServiceAccount;
				//#463- CA Notifications 'From' should be changed to service account-end
				IChangeAction iChangeAction=ChangeAction.getChangeAction(context, strObjectId);
				toList = iChangeAction.GetFollowers(context);
				
				String strTitle = domObject.getInfo(context, "attribute["+ChangeConstants.ATTRIBUTE_SYNOPSIS+"]");
				String strDueDate = domObject.getInfo(context, "attribute["+ChangeConstants.ATTRIBUTE_ESTIMATED_COMPLETION_DATE+"]");
				if(null != strDueDate) {
					Date dtDueDate = eMatrixDateFormat.getJavaDate(strDueDate);
					strDueDate = dateFormat.format(dtDueDate);
				}
				String[] messageValues = new String[6];
                messageValues[0] = strObjectType;
                messageValues[1] = strBaseURL + "?objectId=" + strObjectId;
                messageValues[2] = strObjectState;
                messageValues[3] = strTitle;
                messageValues[4] = strDueDate;
                messageValues[5] = strCATaskStatus;
				subjectValues[1] = strCATaskStatus;
                strIconMailMessage = MessageUtil.getMessage(context,strObjectId, strMessageKey, messageValues,null, new Locale(context.getSession().getLanguage()), strPropertyKey);
				
				messageValues[1] = "<a href='"+strBaseURL + "?objectId=" + strObjectId + "'>"+strObjectName+"</a>";
				
				strHTMLMailMessage += "<table border = 1><thead><th>Type</th><th>Name</th><th>"+strStateLabel+"</th><th>Title</th><th>"+strDueDateLabel+"</th><th>Status</th></thead><tbody><tr>";
				strHTMLMailMessage += MessageUtil.getMessage(context,strObjectId, strMessageKey + "HTML", messageValues,null, new Locale(context.getSession().getLanguage()), strPropertyKey);
				strHTMLMailMessage += "</tr></tbody></table><br>";
			} else if(null != strUserCategory && "Reviewer".equals(strUserCategory)) {
				//#463- CA Notifications 'From' should be changed to service account-start
				//agentName = strOriginator; 
				agentName = strFromServiceAccount;
				//#463- CA Notifications 'From' should be changed to service account-end
				subjectValues[1] = strCATaskStatus;
				IChangeAction iChangeAction=ChangeAction.getChangeAction(context, strObjectId);
				String strType = domObject.getInfo(context,DomainObject.SELECT_TYPE);
				toList = iChangeAction.GetReviewers(context);
				String baseURLFromProp = EnoviaResourceBundle.getProperty(context, "eServiceSuiteXORGDeviation.AccessRequest.baseURL");	
				String strCADescription = domObject.getInfo(context, DomainObject.SELECT_DESCRIPTION);				
				strHTMLMailMessage += "<table><tbody><tr><td>"+ strType +" :</td> <td><a href='" + baseURLFromProp + "?objectId=" + strObjectId + "'>"+strObjectName+"</a></td></tr><tr><td>Description :</td> <td>"+strCADescription+"</td></tr></table><br><br>";
				
				strHTMLMailMessage += "<table border = 1><thead><th>"+strAssigneeLabel+"</th><th>"+strTaskOrSignatureLabel+"</th><th>"+strTaskTitleLabel+"</th><th>"+strCommentsOrInstructionsLabel+"</th><th>"+strActionLabel+"</th><th>"+strDueDateLabel+"</th><th>"+strCompletedDateLabel+"</th></thead><tbody>";
				Map programMap = new HashMap(2);
				programMap.put("objectId", strObjectId);
				programMap.put("languageStr", strLanguage);
				String[] arrCustomArgs = JPO.packArgs(programMap);
				${CLASS:emxLifecycle} objemxLifecycleObject = new ${CLASS:emxLifecycle}(context, args);
				MapList mlInboxTaskDetails = objemxLifecycleObject.getAllTaskSignaturesOnObject(context, arrCustomArgs);
				//System.out.println("a.......b..............."+mlInboxTaskDetails);
				
				Map programMap1 = new HashMap(2);
				programMap1.put("objectList", mlInboxTaskDetails);
				Map paramList = new HashMap(6);
				paramList.put("reportFormat", "");
				paramList.put("parentOID", strObjectId);
				paramList.put("objectId", strObjectId);
				paramList.put("jsTreeID", "");
				paramList.put("suiteKey", "Framework");
				paramList.put("languageStr", strLanguage);
				programMap1.put("paramList", paramList);
				String[] arrCustomArgs1 = JPO.packArgs(programMap1);
				Vector slAssigneeList = objemxLifecycleObject.getAssigneeForApprovals(context, arrCustomArgs1);
				Vector slTaskOrSignatureList = objemxLifecycleObject.getTaskOrSignatureForApprovals(context, arrCustomArgs1);
				Vector slCommentsOrInstructionsList = objemxLifecycleObject.getCommentsOrInstructionForApprovals(context, arrCustomArgs1);
				Vector slActionList = objemxLifecycleObject.getActionForApprovals(context, arrCustomArgs1);
				
				String[] arrReviewerMessageValues = new String[7];
				
				for(int i=0; i<mlInboxTaskDetails.size(); i++) {
					Map mInboxTaskDetailMap = (Map)mlInboxTaskDetails.get(i);
					String strInfoType = (String)mInboxTaskDetailMap.get("infoType");
					if("emptyRow".equals(strInfoType)) {
						continue;
					}
					String strAssigneeName = (String)slAssigneeList.get(i);
					String strTaskOrSignature = (String)slTaskOrSignatureList.get(i);
					String strCommentsOrInstructions = (String)slCommentsOrInstructionsList.get(i);
					String strActions = (String)slActionList.get(i);
					/*String strParentObjectState = (String)mInboxTaskDetailMap.get("parentObjectState");
					String strParentObjectPolicy = (String)mInboxTaskDetailMap.get("parentObjectPolicy");
					String strParentObjectTranslatedState = i18nNow.getStateI18NString(strParentObjectPolicy, strParentObjectState, strLanguage);*/
					String strTaskTitle = (String)mInboxTaskDetailMap.get("title");
					
					String strOriginalDueDate = (String)mInboxTaskDetailMap.get("dueDate");
					String strDueDate = "";
					if(null != strOriginalDueDate) {
						Date dtDueDate = eMatrixDateFormat.getJavaDate(strOriginalDueDate);
						strDueDate = dateFormat.format(dtDueDate);
					}
					//arrReviewerMessageValues[0] = strParentObjectTranslatedState;
					arrReviewerMessageValues[0] = strAssigneeName;
					arrReviewerMessageValues[1] = strTaskOrSignature;
					arrReviewerMessageValues[2] = strTaskTitle;
					arrReviewerMessageValues[3] = strCommentsOrInstructions;
					arrReviewerMessageValues[4] = strActions;
					arrReviewerMessageValues[5] = strDueDate;
					arrReviewerMessageValues[6] = (String)mInboxTaskDetailMap.get("completionDate");
					
					strHTMLMailMessage += "<tr>";
					strHTMLMailMessage += MessageUtil.getMessage(context,strObjectId, strCAReviewerMessageKey + "HTML", arrReviewerMessageValues,null, new Locale(context.getSession().getLanguage()), strPropertyKey);
					strHTMLMailMessage += "</tr>";
				}
				strHTMLMailMessage += "</tbody></table><br>";
				
			} else {
				String strObjectOwner = domObject.getInfo(context, DomainObject.SELECT_OWNER);
				toList = new StringList(1);
				toList.add(strObjectOwner);
			}
			if(toList.size() > 0) {
				String strSubject = MessageUtil.getMessage(context,strObjectId, strSubjectKey, subjectValues, null, new Locale(context.getSession().getLanguage()), strPropertyKey);
				
				strHTMLMailMessage += "<br><p>Note: this is an automatically generated email please do not reply to this message.</p></div>";
				strHTMLMailMessage += "</body></html>";
				
				if("".equals(strIconMailMessage)) {
					strIconMailMessage = strHTMLMailMessage;
				}
				//System.out.println("strHTMLMailMessage::"+strHTMLMailMessage);
				try {
					${CLASS:emxNotificationUtil}.sendJavaMail(context, new StringList((ArrayList)toList), new StringList(), new StringList(), strSubject, strIconMailMessage, strHTMLMailMessage, agentName, null , new StringList(), "both");
				} catch(Exception eMailException) {
					// If SMTP is disabled, only send iconmail
					${CLASS:emxNotificationUtil}.sendJavaMail(context, new StringList((ArrayList)toList), new StringList(), new StringList(), strSubject, strIconMailMessage, strHTMLMailMessage, agentName, null , new StringList(), "iconmail");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method added by TCS (Shashank) for Issue #192
	 * Generic action trigger on Inbox Task Rejection to demote the associated object
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void demoteObjectOnTaskRejection(Context context, String args[]) throws Exception {
		//boolean boolIsContextPushed = false;
		try {
			String strObjectId = args[0];
			String strRequiredObjectTypeSymbolicName = args[1];
			StringList slRequiredObjectTypeSymbolicNameList = FrameworkUtil.split(strRequiredObjectTypeSymbolicName, "|");
			StringList slRequiredObjectTypeList = new StringList(5);
			for(int i=0; i<slRequiredObjectTypeSymbolicNameList.size(); i++) {
				String strRequiredObjectType = PropertyUtil.getSchemaProperty(context, (String)slRequiredObjectTypeSymbolicNameList.get(i));
				slRequiredObjectTypeList.add(strRequiredObjectType);
			}
			StringList slSelectable = new StringList(3);
			slSelectable.add("attribute[" + DomainObject.ATTRIBUTE_APPROVAL_STATUS + "]");
			slSelectable.add("from[" + DomainObject.RELATIONSHIP_ROUTE_TASK + "].to.to[" + DomainObject.RELATIONSHIP_OBJECT_ROUTE + "].from." + DomainObject.SELECT_TYPE);
			slSelectable.add("from[" + DomainObject.RELATIONSHIP_ROUTE_TASK + "].to.to[" + DomainObject.RELATIONSHIP_OBJECT_ROUTE + "].from." + DomainObject.SELECT_ID);
			DomainObject domInboxTask = DomainObject.newInstance(context, strObjectId);
			Map mTaskDetails = domInboxTask.getInfo(context, slSelectable);
			String strApprovalStatus = (String)mTaskDetails.get("attribute[" + DomainObject.ATTRIBUTE_APPROVAL_STATUS + "]");
			if("Reject".equals(strApprovalStatus)) {
				String strConnectedObjectType = (String)mTaskDetails.get("from[" + DomainObject.RELATIONSHIP_ROUTE_TASK + "].to.to[" + DomainObject.RELATIONSHIP_OBJECT_ROUTE + "].from." + DomainObject.SELECT_TYPE);
				if(slRequiredObjectTypeList.contains(strConnectedObjectType)) {
					//ContextUtil.pushContext(context);
					//boolIsContextPushed = true;
					String strConnectedObjectId = (String)mTaskDetails.get("from[" + DomainObject.RELATIONSHIP_ROUTE_TASK + "].to.to[" + DomainObject.RELATIONSHIP_OBJECT_ROUTE + "].from." + DomainObject.SELECT_ID);
					DomainObject domConnectedObject = DomainObject.newInstance(context, strConnectedObjectId);
					domConnectedObject.demote(context);
					//ContextUtil.popContext(context);
					//boolIsContextPushed = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			if(boolIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}*/
	}
	
	/**
	 * Method added(Cloned from emxPartDefinitionBase:ensureECOConnected) by TCS (Shashank) for Issue #104
	 * Check trigger on Family object promotion to Obsolete, if the promote is via CA or not
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */	
	public int ensureECOConnected(Context context, String[] args) throws Exception {
    // Get the RPE variable MX_SKIP_PART_PROMOTE_CHECK, if it is not null and equal to "true"
    // it indicates that object is getting promoted because of ECO promotion to "release" state
    // in this case, no need to do the checks specified in this trigger logic, skip it.
    // In other words, when ECO gets promoted to Release state all the connected items get promoted, these can be many objects
    // this check trigger gets fired for each of these objects being promoted, which is not needed in this case.
    // This also results in performance improvment for ECO promote action

      String skipTriggerCheck = PropertyUtil.getRPEValue(context, "MX_SKIP_PART_PROMOTE_CHECK", false);
      if(skipTriggerCheck != null && "true".equals(skipTriggerCheck))
      {
      return 0;
    }

      String sObjectId = args[0];
	  DomainObject domObject = DomainObject.newInstance(context, sObjectId);
      String noChangeManagementIfReferenceOnly = args[1];

      StringList selectStmts  = new StringList(2);
      selectStmts.addElement(DomainObject.SELECT_ID);

      // Rel selects

      StringList selectRelStmts = new StringList(1);
      selectRelStmts.addElement(DomainObject.SELECT_RELATIONSHIP_ID);

		String strRelPattern = DomainObject.RELATIONSHIP_AFFECTED_ITEM;
		//strRelPattern = strRelPattern + "," +  ChangeConstants.RELATIONSHIP_CHANGE_AFFECTED_ITEM + "," + ChangeConstants.RELATIONSHIP_IMPLEMENTED_ITEM;

		String strTypePattern = DomainObject.TYPE_ECO;
		strTypePattern = strTypePattern + "," + ChangeConstants.TYPE_CHANGE_ACTION ;
		//String sObjectId = getId(context);
		Map proposedCAData  = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil.getChangeObjectsInProposed(context, selectStmts, new String[]{sObjectId}, 1);
		MapList proposedchangeActionList = (MapList)proposedCAData.get(sObjectId);
		Map  realizedCAData = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil.getChangeObjectsInRealized(context, selectStmts, new String[]{sObjectId}, 1);
		MapList realizedchangeActionList = (MapList)realizedCAData.get(sObjectId);

            MapList mapListECOs =
                                    domObject.getRelatedObjects(context,
                                          strRelPattern,
                                          strTypePattern, // object pattern
                                          selectStmts, // object selects
                                          selectRelStmts, // relationship selects
                                          true, // to direction
                                          false, // from direction
                                          (short) 1, // recursion level
                                          null, // object where clause
                                          null); // relationship where clause
			
            if (mapListECOs.size() > 0)
            {
                  return 0;
            }
			
			for(int i=0; i<proposedchangeActionList.size(); i++) {
				Map mProposedchangeActionMap = (Map)proposedchangeActionList.get(i);
				String strCAObjectId = (String)mProposedchangeActionMap.get(DomainObject.SELECT_ID);
				String requestedChange = new ChangeAction().getRequestedChangeFromChangeAction(context, sObjectId, strCAObjectId);
				if("For Obsolescence".equals(requestedChange)) {
					return 0;
				}
			}
			for(int i=0; i<realizedchangeActionList.size(); i++) {
				Map mRealizedchangeActionMap = (Map)realizedchangeActionList.get(i);
				String strCAObjectId = (String)mRealizedchangeActionMap.get(DomainObject.SELECT_ID);
				String requestedChange = new ChangeAction().getRequestedChangeFromChangeAction(context, sObjectId, strCAObjectId);
				if("For Obsolescence".equals(requestedChange)) {
					return 0;
				}
			}

            //Modified for IR-169021 start
           // StringList ecPartList = getInfoList(context,"to["+ DomainObject.RELATIONSHIP_PART_SPECIFICATION +"|from.policy.property[PolicyClassification].value ==Production].from.id");
            StringList ecPartList = domObject.getInfoList(context,"to["+ DomainObject.RELATIONSHIP_PART_SPECIFICATION +"|from.policy.property[PolicyClassification].value ==Production && from."+EngineeringConstants.ATTRIBUTE_RELEASE_PHASE_VALUE+" == "+EngineeringConstants.PRODUCTION+"].from.id");
            StringList devPartList = domObject.getInfoList(context,"to["+ DomainObject.RELATIONSHIP_PART_SPECIFICATION +"|from.policy.property[PolicyClassification].value ==Development].from.id");

        //Change management is not required if reference only
      if ( (devPartList != null && (devPartList.size() == 0)) && (ecPartList != null && (ecPartList.size() == 0)))
      {
            if("true".equalsIgnoreCase(noChangeManagementIfReferenceOnly)){
                return 0;
            }else{
              // If this option is made false, it needs an ECO.---
              String langStr = context.getSession().getLanguage();
             // String strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.CheckIfECOConnected.Message1",langStr) + " " + EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.ChangeOrderChangeAction",langStr);
              String strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.CheckIfChangeConnected.Message",langStr);
              ${CLASS:emxContextUtil}.mqlNotice(context,strMessage);
              return 1;
            }
      }
      else if(ecPartList.size() > 0)
      {
          // If this object is connected as a specification, it needs an ECO.
          String langStr = context.getSession().getLanguage();
          //String strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.CheckIfECOConnected.Message1",langStr) + " " + EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.ChangeOrderChangeAction",langStr) + " " + EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.CheckIfECOConnected.Message2",langStr) +" "+  ChangeConstants.RELATIONSHIP_CHANGE_AFFECTED_ITEM + "," + ChangeConstants.RELATIONSHIP_IMPLEMENTED_ITEM;;
          String strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.CheckIfChangeConnected.Message",langStr);
          ${CLASS:emxContextUtil}.mqlNotice(context,strMessage);
          return 1;
      } else
    	  return 0;
    //Modified for IR-169021 end
   }
   
   /**

	 * Method to validate and verify if ebom sync can be done
	 * Fixed by TCS for Bug#212,214,239,243
	 * @param context
	 * @param partObjId
	 * @return boolean
	 * @throws Exception
	 */

	public static boolean canDoSync(Context context, BusinessObject boPartObject)
			throws Exception
	{
		boolean allowSync = false;
		if ( boPartObject != null)
		{
			DomainObject domPartObj = new DomainObject(boPartObject);
			String strPartState = domPartObj.getInfo(context,
					DomainConstants.SELECT_CURRENT);			
			// If the Part is not in Preliminary state then do not allow the
			// do not allow the Sync
			if (Part.STATE_PART_PRELIMINARY.equalsIgnoreCase(strPartState))
			{
				allowSync = true;
			}
		}
		return allowSync;
	}

	/**
	 * Check trigger to validate the Category of Change/Reason For Change/Description value of the CA/Proposed Activity object 
	 * on promotion of the CA.
	 * @param context
	 * @param args
	 * @return integer value
	 * @throws Exception
	 */	
	public int validateMandatoryCAAttributesOnPromotion(Context context, String args[]) throws Exception {
		int iReturnValue = 0;
		String listofAttr = "";
		try {
			ContextUtil.startTransaction(context, false);
			StringList slSelectable = new StringList(4);
			slSelectable.add("physicalid");
			slSelectable.add(DomainObject.SELECT_DESCRIPTION);
			slSelectable.add("attribute[" + DomainObject.ATTRIBUTE_CATEGORY_OF_CHANGE + "]");
			slSelectable.add("attribute[" + DomainObject.ATTRIBUTE_REASON_FOR_CHANGE + "]");
			String strChangeActionObjectId = args[0];
			String strConnectedParts = "";
			DomainObject caChangeActionObject = new DomainObject(strChangeActionObjectId);
			//ChangeAction caChangeActionObject = new ChangeAction(strChangeActionObjectId);
			Map mCADetails = caChangeActionObject.getInfo(context, slSelectable);
			String strCAPhysicalId = (String)mCADetails.get("physicalid");
			String strCADescription = (String)mCADetails.get(DomainObject.SELECT_DESCRIPTION);
			String strCACategoryOfChange = (String)mCADetails.get("attribute[" + DomainObject.ATTRIBUTE_CATEGORY_OF_CHANGE + "]");
			String strCAReasonForChange = (String)mCADetails.get("attribute[" + DomainObject.ATTRIBUTE_REASON_FOR_CHANGE + "]");
			String strAttributesNotSet = "";

			if("".equals(strCADescription)) {
				strAttributesNotSet += "'";
				strAttributesNotSet += DomainObject.SELECT_DESCRIPTION;
				iReturnValue = 1;
			}
			if("".equals(strCACategoryOfChange) || "Unassigned".equals(strCACategoryOfChange)) {
				if(!"".equals(strAttributesNotSet)) {
					strAttributesNotSet += ", ";
				} else {
					strAttributesNotSet += "'";
				}
				strAttributesNotSet += DomainObject.ATTRIBUTE_CATEGORY_OF_CHANGE;
				iReturnValue = 1;
			}
			if("".equals(strCAReasonForChange)) {
				if(!"".equals(strAttributesNotSet)) {
					strAttributesNotSet += ", ";
				} else {
					strAttributesNotSet += "'";
				}
				strAttributesNotSet += DomainObject.ATTRIBUTE_REASON_FOR_CHANGE;
				iReturnValue = 1;
			}
			String strProposedAffectedItemsValueNotSet = "";
			/*
			if(0 == iReturnValue) {
				ChangeAction caChangeActionObject1 = new ChangeAction(strChangeActionObjectId);
				MapList mlAffectedItemsObjectList = caChangeActionObject1.getAffectedItems(context);
				for(int i=0; i<mlAffectedItemsObjectList.size(); i++) {
					Map mAffectedItemsObjectMap = (Map)mlAffectedItemsObjectList.get(i);
					String strAffectedItemObjectId = (String)mAffectedItemsObjectMap.get(DomainObject.SELECT_ID);
					String strAffectedItemObjectName = (String)mAffectedItemsObjectMap.get(DomainObject.SELECT_NAME);
					String strProposedReasonForChange = new ChangeAction().getReasonForChangeFromChangeAction(context, strAffectedItemObjectId, strCAPhysicalId);
					if("".equals(strProposedReasonForChange)) {
						if("".equals(strAttributesNotSet)) {
							strAttributesNotSet += "'";
							strAttributesNotSet += DomainObject.ATTRIBUTE_REASON_FOR_CHANGE;
							strAttributesNotSet += "'";
						}
						if(!"".equals(strProposedAffectedItemsValueNotSet)) {
							strProposedAffectedItemsValueNotSet += ", ";
						} else {
							strProposedAffectedItemsValueNotSet += "'";
						}
						strProposedAffectedItemsValueNotSet += strAffectedItemObjectName;
						iReturnValue = 1;
						//break;
					}
				}
				if(1 == iReturnValue) {
					strProposedAffectedItemsValueNotSet += "'";
				}
			} else {
				strAttributesNotSet += "'";
			}
			*/
			if(1 == iReturnValue) {
				String strCAMandatortAttributeNotSetMessage = EnoviaResourceBundle.getProperty(context,"emxEnterpriseChangeMgtStringResource", new Locale(context.getSession().getLanguage()),"EnterpriseChangeMgt.Alert.MandatoryAttributesNotSetForCA1");
				strCAMandatortAttributeNotSetMessage = strCAMandatortAttributeNotSetMessage.replaceAll("<ATTRIBUTE_NAMES>", strAttributesNotSet);
				/*if(!"".equals(strProposedAffectedItemsValueNotSet)) {
					strCAMandatortAttributeNotSetMessage += EnoviaResourceBundle.getProperty(context,"emxEnterpriseChangeMgtStringResource", new Locale(context.getSession().getLanguage()),"EnterpriseChangeMgt.Alert.MandatoryAttributesNotSetForCA2");
					strCAMandatortAttributeNotSetMessage = strCAMandatortAttributeNotSetMessage.replaceAll("<AFFECTED_ITEMS>", strProposedAffectedItemsValueNotSet);
				} */
				${CLASS:emxContextUtil}.mqlNotice(context, strCAMandatortAttributeNotSetMessage);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.commitTransaction(context);
		}
		return iReturnValue;
	}
	
	/**
	 * Method added by TCS (Shashank) for Issue #196
	 * Action trigger on Promote to update the CA due date to X days from current date
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */		
	public void addXDaysFromSystemDateToCADueDate(Context context, String args[]) throws Exception {
		try {
			String strObjectId = args[0];
			String strNumberOfDays = args[1];
			
			// Convert the number of days to miliseconds in long
			int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
			int iNumberOfDays = Integer.parseInt(strNumberOfDays);
			long lNumberOfDaysiNumberOfDays = (MILLIS_IN_DAY * iNumberOfDays);
			
			// Get the new date by adding X number of days to the current date
			Date dCurrentDate = new Date();
			Date dNewDueDate = DateUtil.computeFinishDate(dCurrentDate, lNumberOfDaysiNumberOfDays);
			
			// Convert the new due date to String in Enovia accepted format
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
			String strNewDueDate = sdf.format(dNewDueDate);
			
			// Update the new String date to the CA Due Date attribute			
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			domObject.setAttributeValue(context, ChangeConstants.ATTRIBUTE_ESTIMATED_COMPLETION_DATE, strNewDueDate);
			
			// Update the new String date to the Inbox Task Due Date attribute connected to the CA
			StringList slSelectable = new StringList(1);
			slSelectable.add(DomainObject.SELECT_ID);			
			MapList mlRouteList = domObject.getRelatedObjects(context, DomainObject.RELATIONSHIP_OBJECT_ROUTE, DomainObject.TYPE_ROUTE, slSelectable, null, false, true, (short)1, "", "", 0);

			for(int i=0; i<mlRouteList.size(); i++) {
				Map mRouteMap = (Map)mlRouteList.get(i);
				String strRouteId = (String)mRouteMap.get(DomainObject.SELECT_ID);
				Route rRouteObj = new Route(strRouteId);
				MapList mlInboxTaskList = rRouteObj.getRouteTasks(context, slSelectable, null, null, false);
				for(int j=0; j<mlInboxTaskList.size(); j++) {
					Map mInboxTaskMap = (Map)mlInboxTaskList.get(j);
					String strInboxTaskId = (String)mInboxTaskMap.get(DomainObject.SELECT_ID);
					domObject.setId(strInboxTaskId);
					domObject.setAttributeValue(context, DomainObject.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strNewDueDate);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Check trigger to validate if the objects checked in file is not locked
	 * on promotion of the SW object.
	 * @param context
	 * @param args
	 * @return integer value
	 * @throws Exception
	 */	
	public int isCheckedInFileLocked(Context context, String args[]) throws Exception {
		int iReturnValue = 0;
		String strObjectId = args[0];
		String strObjectType = args[1];
		String strObjectName = args[2];
		String strObjectRevision = args[3];
		try {
			CommonDocument comDocumentObject = new CommonDocument(strObjectId);
			String strIsObjectLocked = comDocumentObject.getInfo(context, DomainObject.SELECT_LOCKED);
			if(null != strIsObjectLocked && "TRUE".equalsIgnoreCase(strIsObjectLocked)) {
				iReturnValue = 1;
				String strFileIsLockedMessage = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(context.getSession().getLanguage()),"emxComponents.Event.Message.File_Checked_Out");
				strFileIsLockedMessage = strFileIsLockedMessage.replaceAll("<TYPE>", strObjectType);
				strFileIsLockedMessage = strFileIsLockedMessage.replaceAll("<NAME>", strObjectName);
				strFileIsLockedMessage = strFileIsLockedMessage.replaceAll("<REVISION>", strObjectRevision);
				${CLASS:emxContextUtil}.mqlNotice(context, strFileIsLockedMessage);
				throw new Exception(strFileIsLockedMessage);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return iReturnValue;
	}
	
	/**
	 * Method added by TCS (Deepak) for Issue #347 - Mass MEP Create
	 * programHTMLOutput method to display MEP Part column in Edit Part table
	 * @param context
	 * @param args
	 * @return StringList
	 * @throws Exception
	 */		
	public StringList displayMEPPartColumn(Context context, String[] args) throws Exception {

		StringList returnStringList = new StringList();
		try{
			Map programMap = (Map) JPO.unpackArgs(args);
			MapList objectList = (MapList) programMap.get("objectList");

			String rtName = "";
			Locale sLocalObj = context.getLocale();
			
			String strPartId = DomainConstants.EMPTY_STRING;
			String strMEPName = DomainConstants.EMPTY_STRING;
			DomainObject doPart = null;
			//String strClear = EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", sLocalObj, "emxEngineeringCentral.Common.Clear");
			
			if(objectList!=null && !objectList.isEmpty()){
				Iterator objectListItr = objectList.iterator();
				int iCount = 0;
				while(objectListItr.hasNext()){
					StringBuffer stbTNR = new StringBuffer();
					Map object = (Map)objectListItr.next();
					if(object!=null && !object.isEmpty()){
						strPartId = (String) object.get(DomainConstants.SELECT_ID);

						doPart = DomainObject.newInstance(context, strPartId.trim());
						strMEPName = doPart.getInfo(context, "from["+DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT+"].to.name");
						if(UIUtil.isNullOrEmpty(strMEPName)){
							strMEPName = DomainConstants.EMPTY_STRING;
						}

						StringBuffer returnString = new StringBuffer();
						returnString.append("<input type=\"hidden\" name=\"MEP"+iCount+"\" id=\"MEP"+iCount+"\" value=\"\" />");
						returnString.append("<input value=\"\" name=\"MEPOID"+iCount+"\" type=\"hidden\"/>");
						returnString.append("<input value='"+strMEPName+"' name='MEPDisplay"+iCount+"' type='text' readonly='readonly' />");

						returnString.append("<input type='button' class='button' size='200' value='...' alt ='...' name='btnOriginatorChooser' onClick=\"javascript:showModalDialog('../common/emxFullSearch.jsp?field=TYPES=type_Part:POLICY=policy_ManufacturerEquivalent&amp;fieldNameActual=MEP"+iCount+"&amp;searchMode=fullTextSearch&amp;showInitialResults=false&amp;chooserType=FormChooser&amp;HelpMarker=emxhelpfullsearch&amp;selection=single&amp;populateRevision=false&amp;suiteKey=Components&amp;fieldNameOID=MEPOID"+iCount+"&amp;table=MEPAddExistingResults&amp;clearManuLoc=false&amp;clearCustomRev=false&amp;fieldNameDisplay=MEPDisplay"+iCount+"&amp;submitURL=../engineeringcentral/XORGSearchSubmit.jsp&amp;partId="+strPartId+"&amp;mode=Chooser&amp;chooserType=FormChooser','600','600','true','','MEP"+iCount+"')\" /> <br />");
						
						//returnString.append("<a href=\"javascript:clearMEPValue("+iCount+")\">");
						//returnString.append(strClear);
						//returnString.append("</a>&#160;");

						
						returnString.append("<a href=\"javascript:showModalDialog('../common/emxCreate.jsp?type=type_Part&amp;partId="+strPartId+"&amp;form=type_CreateMEP&amp;policy=policy_ManufacturerEquivalent&amp;header=emxManufacturerEquivalentPart.Shortcut.CreateMEP&amp;HelpMarker=emxhelpmepcreate&amp;postProcessJPO=XORG_Util:connectMEP&amp;submitAction=doNothing&amp;preProcessJavaScript=preProcessInCreateMEPPart&amp;suiteKey=ManufacturerEquivalentPart&amp;StringResourceFileId=emxManufacturerEquivalentPartStringResource&amp;SuiteDirectory=manufacturerequivalentpart&amp;ExclusionList=type_ManufacturingPart,type_ShopperProduct,type_POA,type_Trim,type_Label,type_Packaging,type_RawGoods,type_HardGoodsMaterial,type_TBD,type_Fabric,type_Finishings&amp;postProcessURL=../engineeringcentral/XORG_MassMEPCreatePostProcess.jsp&amp;fieldNameDisplay=MEPDisplay"+iCount+"&amp;fieldNameActual=MEP"+iCount+"&amp;&amp;fieldNameOID=MEPOID"+iCount+"','850','630','true','','MEP"+iCount+"')\">");

						returnString.append(EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", sLocalObj, "emxEngineeringCentral.Common.CreateNew"));
						returnString.append("</a>");

						returnStringList.addElement(returnString.toString());
						iCount++;
					}
				}
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}finally{
			return returnStringList;
		}
	}
	
	
	/**
	* Method added by TCS (Deepak) for Issue #347 - Mass MEP Create
	* Connects the created Manufacturer Equivalent Part for the given
	* Enterprise Part
	* @param context
	*            The Matrix Context.
	* @param args
	*            holds a packed HashMap which contains usagelcoation,mepid ep
	*            id,manufacturer,mfg location,loc status
	* @throws FrameworkException
	*             If the operation fails.
	*/
	@com.matrixone.apps.framework.ui.PostProcessCallable
	public void connectMEP(Context context, String[] args) throws Exception {
		
		HashMap programMap 	= (HashMap)JPO.unpackArgs(args);
		String strMEPId 	= DomainConstants.EMPTY_STRING;
		String strPartId 	= DomainConstants.EMPTY_STRING;
		
		String strFromChooser 	= (String) programMap.get("FromChooser");
		if(UIUtil.isNotNullAndNotEmpty(strFromChooser) && "true".equals(strFromChooser)){
			strMEPId = (String) programMap.get("MEPId");
			strPartId = (String) programMap.get("partId");
		}
		else{
			// calling the OOTB postprocess JPO which handles location object connections
			jpo.manufacturerequivalentpart.Part_mxJPO PartJPO = new jpo.manufacturerequivalentpart.Part_mxJPO(context, args);
			PartJPO.createMfg(context, args);
		
			HashMap paramMap 	= (HashMap) programMap.get("paramMap");
			HashMap requestMap 	= (HashMap)programMap.get("requestMap");
			strMEPId 	= (String) paramMap.get("objectId");
			strPartId 	= (String) requestMap.get("partId");
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPartId)){
			try {
				DomainObject doPart = DomainObject.newInstance(context, strPartId.trim());
				
				String strExistingMEPRelId = doPart.getInfo(context, "from["+DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT+"].id");
				if(UIUtil.isNotNullAndNotEmpty(strExistingMEPRelId)){
					DomainRelationship.disconnect(context, strExistingMEPRelId);
				}
				
				DomainObject doMEP 	= DomainObject.newInstance(context, strMEPId);
				DomainRelationship.connect(context, doPart, DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT, doMEP);
			}
			catch (Exception e) {
				throw e;
			} 
		}
	}
	
	
	
	/**
	* Method added by TCS (Deepak) for Issue #186 - MEP associated for the supplier
	* Gets the Manufacturer Equivalent Parts attached to a Company.
	 *
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            holds a HashMap of the following entries: objectId - a String
	 *            containing the Enterprise Part id.
	 * @return a MapList of Manufaturer Equivalent Part object ids and
	 *         relationship ids.
	 * @throws Exception
	 *             if the operation fails.
	 */
	@com.matrixone.apps.framework.ui.ProgramCallable
	public MapList getManufacturerEquivalents(Context context,
			String[] args) throws Exception {
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		String objectId = (String) paramMap.get("objectId");
		String isMPN = (String) paramMap.get("isMPN");

		MapList equivList = new MapList();

		MapList listCorpMEPs = new MapList();

		try {
			DomainObject companyObj = DomainObject.newInstance(context, objectId);

			StringList selectStmts = new StringList(4);
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_TYPE);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement(DomainConstants.SELECT_REVISION);

			StringList selectRelStmts = new StringList(1);
			selectRelStmts.addElement(DomainObject.SELECT_RELATIONSHIP_ID);

			StringBuffer typePattern = new StringBuffer(DomainConstants.TYPE_PART);

			// fetching list of related MEPs via Manufacturing Responsiblity
			listCorpMEPs = companyObj.getRelatedObjects(context,
														DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY , // relationship  pattern
														typePattern.toString(), // object pattern
														selectStmts, // object selects
														selectRelStmts, // relationship selects
														false, // to direction
														true, // from direction
														(short) 1, // recursion level
														null, // object where clause
														null); // relationship where clause

		} catch (Exception Ex) {
			throw Ex;
		}

		return listCorpMEPs;
	}
	
	/**
	 * Method added by TCS (Deepak) for Issue #448 - Undo Checkout functionality
	 * Access function for 'Undo Checkout' functionality, checks if the SW Family object is locked
	 *
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            holds a HashMap of the following entries: objectId - a String
	 *            containing the Enterprise Part id.
	 * @return boolean
	 * @throws Exception
	 *             if the operation fails.
	 */
	public boolean isSWFamilyLocked(Context context, String[] args) throws Exception {
		
		boolean bReturn = false;
		try{
			Map programMap 		= (Map)JPO.unpackArgs(args);
			String strObjectId 	= (String)programMap.get("objectId");


			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				DomainObject domObject = DomainObject.newInstance(context, strObjectId);
				StringList slSelectable = new StringList(5);
				slSelectable.add("locked");
				slSelectable.add("to[Instance Of].from.locked");
				slSelectable.add("to[Associated Drawing].from.to[Instance Of].from.locked");

				Map mpObjectFamilyInfo = domObject.getInfo(context, slSelectable);
				if(null != mpObjectFamilyInfo && !mpObjectFamilyInfo.isEmpty()){
					String strFamilyLock = (String)mpObjectFamilyInfo.get("locked");
					if("TRUE".equalsIgnoreCase(strFamilyLock)){
						return true;
					}
					
					strFamilyLock = (String)mpObjectFamilyInfo.get("to[Instance Of].from.locked");
					if("TRUE".equalsIgnoreCase(strFamilyLock)){
						return true;
					}
					else {
						strFamilyLock = (String)mpObjectFamilyInfo.get("to[Associated Drawing].from.to[Instance Of].from.locked");
							if("TRUE".equalsIgnoreCase(strFamilyLock)){
							return true;
						}
					}
					
				}
			}
			
		} catch (Exception Ex) {
			throw Ex;
		}
		return bReturn;
		
	}
	
	/**
	 * Method added by TCS (Deepak) for Issue #448 - Undo Checkout functionality
	 * To get the Family object and unlock and set 'IEF Lock Information' attribute as blank
	 *
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            holds a HashMap of the following entries: objectId - a String
	 *            containing the Enterprise Part id.
	 * @return String
	 * @throws Exception
	 *             if the operation fails.
	 */
	public String undoCheckout(Context context, String[] args) throws Exception {
	
		String strStatus = DomainConstants.EMPTY_STRING;
		Locale locale = new Locale(context.getSession().getLanguage());
		String strSucessStatus = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", locale,"emxComponents.Alert.UndoCheckout.Success");
		String strFailureStatus = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", locale,"emxComponents.Alert.UndoCheckout.Success");
		try{
			String strObjectId = args[0];
			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				
				DomainObject domObject 	= DomainObject.newInstance(context, strObjectId);
				DomainObject doObjectFromInstance 	= null;
				DomainObject doObjectFromDrawing 	= null;
				StringList slSelectable = new StringList(5);
				slSelectable.add("locked");
				slSelectable.add("to[Instance Of].from.locked");
				slSelectable.add("to[Instance Of].from.id");
				slSelectable.add("to[Associated Drawing].from.to[Instance Of].from.locked");
				slSelectable.add("to[Associated Drawing].from.to[Instance Of].from.id");

				Map mpObjectFamilyInfo = domObject.getInfo(context, slSelectable);
				
				if(null != mpObjectFamilyInfo && !mpObjectFamilyInfo.isEmpty()){
					String sCurrentObjLock = (String)mpObjectFamilyInfo.get("locked");
					if("TRUE".equalsIgnoreCase(sCurrentObjLock)){
						unLockObject(context, domObject);
					}
					
					String sFamilyFromInstance = (String)mpObjectFamilyInfo.get("to[Instance Of].from.locked");
					if("TRUE".equalsIgnoreCase(sFamilyFromInstance)){
						strObjectId = (String)mpObjectFamilyInfo.get("to[Instance Of].from.id");
						doObjectFromInstance 	= DomainObject.newInstance(context, strObjectId);
						unLockObject(context, doObjectFromInstance);
					}

					String	sFamilyFromDrawing = (String)mpObjectFamilyInfo.get("to[Associated Drawing].from.to[Instance Of].from.locked");
					if("TRUE".equalsIgnoreCase(sFamilyFromDrawing)){
						strObjectId = (String)mpObjectFamilyInfo.get("to[Associated Drawing].from.to[Instance Of].from.id");
						doObjectFromDrawing 	= DomainObject.newInstance(context, strObjectId);
						unLockObject(context, doObjectFromDrawing);
					}
					
				}
				strStatus = strSucessStatus;
				
			}
			else{
				strStatus = strFailureStatus;
			}
			
			return strStatus;
		} 
		catch (Exception Ex) {
			strStatus = strFailureStatus;
			return strStatus;
		}
	
	}
	
	/**
	 * Method added by TCS (Deepak) for Issue #448 - Undo Checkout functionality
	 * To unlock object and set 'IEF Lock Information' attribute as blank
	 * @param context
	 *            the eMatrix <code>Context</code> object
	 * @return void
	 * @throws Exception
	 *             if the operation fails.
	 */
	public void unLockObject(Context context, DomainObject domObject) throws Exception {
		try{
			domObject.unlock(context);
			domObject.setAttributeValue(context, ATTR_IEF_LOCK_INFORMATION, DomainConstants.EMPTY_STRING);
		} 
		catch (Exception Ex) {
			throw Ex;
		}
	}
	
	//Added for req#464
	/** 
	 * Method added by TCS(kranthi) to populate the ERP Part Number on CAD objects too up on population on EC Part
	 * @return void
	 * @throws Exception
	 *@param context, ATTR_ERP_PART_NUMBER, StrPartID
	 */
	public void populateCADObjectswithERPPartNumber(Context context, String strERPPartNumber,DomainObject doPart) throws Exception{
		
		String TYPE_SW_ASSEMBLY_FAMILY = PropertyUtil.getSchemaProperty("type_SWAssemblyFamily");	
		String TYPE_SW_COMPONENT_FAMILY = PropertyUtil.getSchemaProperty("type_SWComponentFamily");	
		String TYPE_SW_ASSEMBLY_INSTANCE = PropertyUtil.getSchemaProperty("type_SWAssemblyInstance");	
		String TYPE_SW_COMPONENT_INSTANCE = PropertyUtil.getSchemaProperty("type_SWComponentInstance");
		String TYPE_SW_DRAWING = PropertyUtil.getSchemaProperty("type_SWDrawing");
		
		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		
		String strCADObjectId = "";
		
		Map<String, String> objMap = null;
		try{
				
		MapList mlObjects = doPart.getRelatedObjects(
				context,
				DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // relationship pattern
				TYPE_SW_ASSEMBLY_FAMILY+","+TYPE_SW_COMPONENT_FAMILY+","+TYPE_SW_ASSEMBLY_INSTANCE+","+TYPE_SW_COMPONENT_INSTANCE+","+TYPE_SW_DRAWING, // Type pattern
				selectStmts, // object selects
				null, // relationship selects
				true, // to direction
				true, // from direction
				(short) 1, // recursion level
				null, // object where clause
				DomainConstants.EMPTY_STRING,
				0);
		Iterator<Map<String,String>> objItr =(Iterator) mlObjects.iterator();
		  
		  while (objItr.hasNext()) {
			  objMap = (Map<String, String>) objItr.next();
			  strCADObjectId = (String)objMap.get(DomainConstants.SELECT_ID);
			  DomainObject doCAD = new DomainObject(strCADObjectId);
			  doCAD.setAttributeValue(context, ATTR_ERP_PART_NUMBER, strERPPartNumber);
			  
		  }
		
		}
		catch(Exception Ex){
			throw Ex;
		}
	}
	
	/**
	 * Method added by TCS (Shashank) for Issue #487, 488, 489 to check if any object task assigned to this user
	 * @param context
	 *            the eMatrix <code>Context</code> object
	 * @return boolean
	 * @throws Exception
	 *             if the operation fails.
	 */
	public boolean hasAssignedTask(Context context, String args[]) throws Exception {
		boolean boolHasTask = false;
		try {
			Map programMap =   (Map)JPO.unpackArgs(args);
			String strObjectId = (String)programMap.get("objectId");
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			String strObjectState = domObject.getInfo(context, DomainObject.SELECT_CURRENT);
			if((ChangeConstants.STATE_CHANGE_ACTION_INAPPROVAL).equals(strObjectState)) {
				${CLASS:emxLifecycle} objemxLifecycleObject = new ${CLASS:emxLifecycle}(context, args);
				//MapList mlInboxTaskDetails = objemxLifecycleObject.getAllTaskSignaturesOnObject(context, args);
				MapList mlInboxTaskDetails = objemxLifecycleObject.getCurrentAssignedTaskSignaturesOnObject(context, args);
				for(int i=0; i<mlInboxTaskDetails.size(); i++) {
					Map mInboxTaskDetailMap = (Map)mlInboxTaskDetails.get(i);
					String strInfoType = (String)mInboxTaskDetailMap.get("infoType");
					if(!"emptyRow".equals(strInfoType)) {
						boolHasTask = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return boolHasTask;
	}
	
		 /**
      * This method returns the file list that are connected to the object with the specified relationships
      * in the properties
      *
      * @param context the eMatrix <code>Context</code> object.
      * @param args holds no arguments.
      * @return MapList containing quick file access objects
      * @throws Exception if the operation fails.
      * @since 10.6.SP1
      */
    @com.matrixone.apps.framework.ui.ProgramCallable
    public MapList getCAAffectedItemsList(Context context, String[] args) throws Exception {

         MapList returnList = new MapList();
		 MapList mlColumns = new MapList();
		 Map  mapColumn = null;
		 String strCARequestedChangeValue = "";
		 String sAttrReasonForChangeValue  = "";
		 String sConnectionId = "";
		 String sAttrReqChange = PropertyUtil.getSchemaProperty(context,"attribute_RequestedChange");
		 String sAttrReasonForChange = PropertyUtil.getSchemaProperty(context,"attribute_ReasonforChange");
		
        try {

            HashMap programMap         = (HashMap) JPO.unpackArgs(args);
            String  masterObjectId     = (String) programMap.get("objectId");
			programMap.put("objectId", masterObjectId);
			DomainObject dMastObj = DomainObject.newInstance(context,masterObjectId);
			String strMasterObjType = dMastObj.getInfo(context,DomainConstants.SELECT_TYPE);
			Map mapDevColumn = new HashMap();
			String[] init				= new String[]{};
			String mCAAffectedItemID = null;
			String mCAAffectedItemName = null;
			MapList mCARequestedItemList = (MapList) JPO.invoke(context, "enoECMChangeAction", init, "getAffectedItems", JPO.packArgs(programMap), MapList.class);
			Iterator itr = mCARequestedItemList.iterator();
			while (itr.hasNext()) {
				Map objectMap = (Map) itr.next();
				mapColumn = new HashMap();
				String strCARequestedItemObjectId = (String) objectMap.get(DomainConstants.SELECT_ID);
				String strCARequestedItemObjectName = (String) objectMap.get(DomainConstants.SELECT_NAME);
				String strCARequestedItemObjectType = (String) objectMap.get(DomainConstants.SELECT_TYPE);
				 strCARequestedChangeValue = (String) objectMap.get(sAttrReqChange);
				 sAttrReasonForChangeValue = (String)objectMap.get(sAttrReasonForChange);
				 sConnectionId = (String)objectMap.get("id[connection]");
				if("For Revise".equalsIgnoreCase(strCARequestedChangeValue)) {
					MapList mCAImplementedItemList = (MapList) JPO.invoke(context, "enoECMChangeAction", init, "getImplementedItems", JPO.packArgs(programMap), MapList.class);
					if (mCAImplementedItemList.size()<=0) {
						mapColumn.put(DomainConstants.SELECT_ID,strCARequestedItemObjectId);
						mapColumn.put("Reason for Change",sAttrReasonForChangeValue);
						mapColumn.put("Requested Change",strCARequestedChangeValue);
						mapColumn.put("id[connection]",sConnectionId);
						 
				
					} else {
							Iterator itrCAImplementedItemIterator = mCAImplementedItemList.iterator();
							while (itrCAImplementedItemIterator.hasNext()){
								Map objectImplementedItemMap = (Map) itrCAImplementedItemIterator.next();
								String strCAImplementedItemObjectId = (String) objectImplementedItemMap.get(DomainConstants.SELECT_ID);
							   String strCAImplementedItemObjectName = (String) objectImplementedItemMap.get(DomainConstants.SELECT_NAME);
								String strCAImplementedItemObjectType = (String) objectImplementedItemMap.get(DomainConstants.SELECT_TYPE);
								 strCARequestedChangeValue = (String) objectMap.get(sAttrReqChange);
				                sAttrReasonForChangeValue = (String)objectMap.get(sAttrReasonForChange);
			 					//sConnectionId = (String)objectMap.get("id[connection]");
								if(strCAImplementedItemObjectName!=null && strCAImplementedItemObjectName.equalsIgnoreCase(strCARequestedItemObjectName) && strCAImplementedItemObjectType.equalsIgnoreCase(strCARequestedItemObjectType)) {
									mapColumn.put(DomainConstants.SELECT_ID,strCAImplementedItemObjectId);
									mapColumn.put("Reason for Change",sAttrReasonForChangeValue);
								  mapColumn.put("Requested Change",strCARequestedChangeValue);
								  mapColumn.put("id[connection]",sConnectionId);
								}
							}
						}
					} 
					else {
								mapColumn.put(DomainConstants.SELECT_ID,strCARequestedItemObjectId);
								mapColumn.put("Reason for Change",sAttrReasonForChangeValue);
								mapColumn.put("Requested Change",strCARequestedChangeValue);
								mapColumn.put("id[connection]",sConnectionId);
								
							
					}
			returnList.add(mapColumn);
		}
			
        } catch(Exception ex) {
         
        }
        return returnList;
    }
	
	
	
	/**
	 * Method added by TCS to previous revision object state in custom content Summary page
	 * @param context
	 * @param args
	 * @return StringList of column values
	 * @throws Exception
	 */	
	public StringList getPrevRevState(Context context,String[] args) throws Exception{
		StringList slReturnList = new StringList(1);
		try {
			
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			MapList objectList = (MapList)programMap.get("objectList");
			String strAffectedItemId = "";
			String sPrevRevState = "";
			DomainObject doPart = null;
			for(int i=0; i<objectList.size(); i++) {
				Map mObjectListMap = (Map)objectList.get(i);
				strAffectedItemId = (String)mObjectListMap.get(DomainObject.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(strAffectedItemId)){
				doPart = DomainObject.newInstance(context, strAffectedItemId.trim());
				sPrevRevState = doPart.getInfo(context, "previous.current");
				 if(UIUtil.isNotNullAndNotEmpty(sPrevRevState)){
					   slReturnList.add(sPrevRevState);
				 }
				 else{
					   slReturnList.add("");
				 }
			}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slReturnList;
	}
	
	public StringList getOldERPINVNumber(Context context,String[] args) throws Exception{
		StringList slReturnList = new StringList(1);
		try {
			Map programMap =   (Map)JPO.unpackArgs(args);
			HashMap columnMap      = (HashMap) programMap.get("columnMap");
			MapList objectList = (MapList)programMap.get("objectList");
			String strAffectedItemId = "";
			String sPrevRevERPINVNumber = "";
			DomainObject doPart = null;
			String strPreviousId = "";
			DomainObject domPrevObj = null;
		//	String strERPPartNumber = propertyUtil.getSchemaproperty(
			for(int i=0; i<objectList.size(); i++) {
				Map mObjectListMap = (Map)objectList.get(i);
				strAffectedItemId = (String)mObjectListMap.get(DomainObject.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(strAffectedItemId)){
				doPart = DomainObject.newInstance(context, strAffectedItemId.trim());
				strPreviousId = doPart.getInfo(context,"previous.id");
					
				if(UIUtil.isNotNullAndNotEmpty(strPreviousId)){
					domPrevObj =DomainObject.newInstance(context, strPreviousId.trim());
				
				sPrevRevERPINVNumber = domPrevObj.getInfo(context,"attribute["+ATTR_ERP_PART_NUMBER+"]");
				 if(UIUtil.isNotNullAndNotEmpty(sPrevRevERPINVNumber)){
					   slReturnList.add(sPrevRevERPINVNumber);
				 }else{
					   slReturnList.add("");
				 }
				}  else{
					 slReturnList.add("");
				}
			}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return slReturnList;
	}	
	
	/** 
	 * Method added by Ravi C to get the person preference for FTS
	 * @return String
	 * @throws Exception
	 *@param context, String[]
	 */
	public String getExcludedTypes(Context context, String[] args) throws Exception{
		String sExcludeTypes = "";
		String sType = "";
		String sTypeSymbolicName = "";
		try{
			String sUserName = context.getUser();
			if(sUserName!=null && !"".equals(sUserName)) {
				
				String sExcludedTypesInFTS = MqlUtil.mqlCommand(context, "print person '$1' select $2 dump",sUserName,"property[ExcludeTypesForFTS].value");
				StringList slExcludeTypesList = FrameworkUtil.split(sExcludedTypesInFTS, ",");
				for(int i=0;i<slExcludeTypesList.size();i++) {
					sTypeSymbolicName = (String) slExcludeTypesList.get(i);
					sType 	= PropertyUtil.getSchemaProperty(context,sTypeSymbolicName);
					sType =  " OR [ds6w:type]:\""+sType+"\" ";
					sExcludeTypes = sExcludeTypes + sType;
				}
			}

		
		}
		catch(Exception Ex){
			throw Ex;
		}
		return sExcludeTypes;
	}
	
	/** 
	 * Method added by Ravi C to get the existing person preference for FTS and exclude types list
	 * @return HashMap
	 * @throws Exception
	 *@param context, String[]
	 */
    public HashMap getExcludeTypeDetails(Context context, String[] args)
        throws Exception
    {
		HashMap hmExcludeTypes = new HashMap();
		try {
			String sActualType = "";
			String sSymbolicType = "";
			StringList slSymbolicExcludeTypes = new StringList();
			StringList slActualExcludeTypes = new StringList();
			StringList slExcludeTypes = new StringList();
			String sUserName = context.getUser();

			ContextUtil.pushContext(context);

			String MQLResult  			= MqlUtil.mqlCommand(context, "list page XORGExcludeTypesInFTS");
			if (MQLResult != null && MQLResult.length() !=0)
			{
				MQLResult  = MqlUtil.mqlCommand(context, "print page XORGExcludeTypesInFTS select content dump");
				slSymbolicExcludeTypes = FrameworkUtil.split(MQLResult, ",");
				for(int i=0;i<slSymbolicExcludeTypes.size();i++) {
					sSymbolicType 	= (String) slSymbolicExcludeTypes.get(i);
					sActualType 	= PropertyUtil.getSchemaProperty(context,sSymbolicType);
					slActualExcludeTypes.add(sActualType);
				}
			}
			String sExcludedTypesInFTS 	= MqlUtil.mqlCommand(context, "print person '$1' select $2 dump",sUserName,"property[ExcludeTypesForFTS].value");
			StringList slSymbolicExcludeTypes1 		= FrameworkUtil.split(sExcludedTypesInFTS, ",");
			for(int i=0;i<slSymbolicExcludeTypes1.size();i++) {
				sSymbolicType 	= (String) slSymbolicExcludeTypes1.get(i);
				sActualType 	= PropertyUtil.getSchemaProperty(context,sSymbolicType);
				slExcludeTypes.add(sActualType);
			}
			hmExcludeTypes.put("SystemExcludeTypesList",slActualExcludeTypes);
			hmExcludeTypes.put("PersonExcludeTypes",slExcludeTypes);
			hmExcludeTypes.put("SystemExcludeSymbolicTypesList",slSymbolicExcludeTypes);
		} catch (Exception exception)
		{
			throw exception;
		} finally {
			ContextUtil.popContext(context);

		}
		return hmExcludeTypes;
	}
/** 
 * Method added by Ravi C to update the person preference for FTS
 * @return String
 * @throws Exception
 *@param context, String[]
 */
    public void updatePersonPreferenceForExcludeTypes(Context context, String[] args)
        throws Exception
    {
		try {
			String sTemp = "";
			String sSymbolicType = "";
			String sUserName = context.getUser();

			ContextUtil.pushContext(context);
			if(args!=null) {
				for(int i=0;i<args.length;i++) {
					sTemp 	= (String) args[i];
					if(!"".equals(sSymbolicType)) {
						sSymbolicType = sSymbolicType + ",";
					}
					sSymbolicType = sSymbolicType + sTemp;
				}
			}
			MqlUtil.mqlCommand(context, "mod person '$1' property $2 value $3 ",sUserName,"ExcludeTypesForFTS",sSymbolicType);

		} catch (Exception exception)
		{
			throw exception;
		} finally {
			ContextUtil.popContext(context);

		}
	}

//Added for Issue #527 Starts
	public String getMEPsInformation(Context context,String[] args) throws Exception{
		HashMap hmProgramMap = (HashMap) JPO.unpackArgs(args);
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) paramMap.get("objectId");
        String isMPN = (String) paramMap.get("isMPN");
		String strExportToExcel = (String)paramMap.get("exportToExcel");
		String TYPE_MPN = PropertyUtil.getSchemaProperty("type_MPN");
		String RELATIONSHIP_LOCATION_EQUIVALENT = PropertyUtil.getSchemaProperty("relationship_LocationEquivalent");
		String TYPE_LOCATION_EQUIVALENT_OBJECT = PropertyUtil.getSchemaProperty("type_LocationEquivalentObject");
		String ATTRIBUTE_IHS_STATUS 			= PropertyUtil.getSchemaProperty("attribute_IHSStatus");
        MapList equivList = new MapList();
        MapList listLocEquivMEPs = new MapList();
        MapList listCorpMEPs = new MapList();
        DomainObject partObj = DomainObject.newInstance(context, objectId);
		String strParentPartERPNumber=partObj.getInfo(context,"attribute[XORGERP Part Number].value");
		
		//Added by Lalitha for Issue-215 --starts
		String REL_MANUFACTURE_EQUVALENT  	= PropertyUtil.getSchemaProperty("relationship_ManufacturerEquivalent");
		String ATTRIBUTE_WAYMO 				= PropertyUtil.getSchemaProperty("attribute_waymoPreferedMPN");
		String whereclause					= "(to["+REL_MANUFACTURE_EQUVALENT+"].attribute["+ATTRIBUTE_WAYMO+"].value != \"Non Prefered\" )";
		//Added by Lalitha for Issue-215 --Ends
		
        StringList selectStmts = new StringList(6);
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_REVISION);
		//selectStmts.addElement("to[Manufacturer Equivalent].from.attribute[XORGERP Part Number].value");
		selectStmts.addElement("to[Manufacturing Responsibility].from.name");
		selectStmts.addElement("to[Manufacturing Responsibility].from.id");
        Map mapLocIds               = new HashMap();
        String sFromRelId           = "from.relationship["+ RELATIONSHIP_LOCATION_EQUIVALENT +"].id";
        StringList selectRelStmts = new StringList(2);
        selectRelStmts.addElement(sFromRelId);
        selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);

        StringBuffer sbRelPattern = new StringBuffer(RELATIONSHIP_LOCATION_EQUIVALENT);
        sbRelPattern.append(',');
        sbRelPattern.append(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
        StringBuffer typePattern = new StringBuffer(DomainConstants.TYPE_PART);
        if (isMPN == null || isMPN.equalsIgnoreCase("True")) {
            typePattern.append(',');
            typePattern.append(TYPE_MPN);
        }
        StringBuffer sbTypePattern = new StringBuffer(typePattern.toString());
        sbTypePattern.append(',');
        sbTypePattern.append(TYPE_LOCATION_EQUIVALENT_OBJECT);
		// fetching list of related MEPs via location Equivalent Object
		//Modified by Lalitha for Issue-215 --starts
        listLocEquivMEPs = partObj.getRelatedObjects(context, sbRelPattern
                    .toString(), // relationship pattern
                    sbTypePattern.toString(), // object pattern
                    selectStmts, // object selects
                    selectRelStmts, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 2, // recursion level
                    whereclause, // object where clause
                    null); // relationship where clause
		//Modified by Lalitha for Issue-215 --Ends

        // fetching list of related MEPs via Manufacturer Equivalent
        /* listCorpMEPs = partObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT, // relationship
                    // pattern
                    typePattern.toString(), // object pattern
                    selectStmts, // object selects
                    selectRelStmts, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null); // relationship where clause
					 */
					Map tempMap = null;
					String strMEPName="";
					String strMEPId="";
					String strManufacturerId="";
					String strReturnHTML="";
					//Added by Lalitha for Issue-215 --starts
					String sDescription = "";
					//Added by Lalitha for Issue-215 --Ends
					 strReturnHTML+="<script language=\"javascript\" src=\"../common/scripts/emxUITableUtil.js\"></script>";
					 strReturnHTML+= "<table border='1' class='list'>";
					strReturnHTML += "<tr>";
					strReturnHTML += "<th class='sorted' style='width: 200px;'>Property</th>";
					
					if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
						strReturnHTML="Property\t";
					}
					
		int iMEPsCount=0;
		
		for (int i = 0; i < listLocEquivMEPs.size(); i++) {
            tempMap = (Map) listLocEquivMEPs.get(i);
			iMEPsCount=iMEPsCount+1;
			strMEPName=(String)tempMap.get(DomainConstants.SELECT_NAME);
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
						strReturnHTML +="MEP "+String.valueOf(iMEPsCount)+"\t";
					} else {
			strReturnHTML += "<th class='sorted' style='width: 200px;'>"+"MEP "+String.valueOf(iMEPsCount)+"</th>";
					}
		}
		if(iMEPsCount>0)
		{
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML += "\n";
				} else {
			strReturnHTML += "</tr>";
				}
			String strCSSClass = "even";
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML +="MEP Name"+"\t";
				} else {
					strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>MEP Name</b></td>";
				}
			for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
				tempMap = (Map) listLocEquivMEPs.get(i1);
				strMEPName=(String)tempMap.get(DomainConstants.SELECT_NAME);
				strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML +=strMEPName+"\t";
				} else {
				strReturnHTML += "<td colspan='1'>";
				strReturnHTML +=" <img border=\"0\" src=\"";
				strReturnHTML +="../common/images/iconSmallPart.png";
				strReturnHTML +="\" /> ";
				strReturnHTML +="<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=";
				strReturnHTML +="engineeringcentral";
				strReturnHTML +="&amp;suiteKey=";
				strReturnHTML +="EngineeringCentral";
				strReturnHTML +="&amp;objectId=";
				strReturnHTML +=strMEPId;
				strReturnHTML +="', '450', '300', 'true', 'popup')\" class=\"object\">";
				strReturnHTML +=XSSUtil.encodeForHTML(context,strMEPName);
				strReturnHTML +="</A>";
				strReturnHTML += "</td>";
				}
					
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML +="\n";
				} else {
			strReturnHTML += "</tr>";
				}
			if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
				} else {
					strCSSClass = "even";
				}
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="Manufacturer\t";
			} else {
				strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>Manufacturer</b></td>";
			}
			for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
				tempMap = (Map) listLocEquivMEPs.get(i1);
				strMEPName=(String)tempMap.get("to[Manufacturing Responsibility].from.name");
				strManufacturerId=(String)tempMap.get("to[Manufacturing Responsibility].from.id");
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+=strMEPName+"\t";
			} else {
				strReturnHTML += "<td colspan='1'>";
				strReturnHTML +=" <img border=\"0\" src=\"";
				strReturnHTML +="../common/images/iconSmallCompany.gif";
				strReturnHTML +="\" /> ";
				strReturnHTML +="<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=";
				strReturnHTML +="engineeringcentral";
				strReturnHTML +="&amp;suiteKey=";
				strReturnHTML +="EngineeringCentral";
				strReturnHTML +="&amp;objectId=";
				strReturnHTML +=strManufacturerId;
				strReturnHTML +="', '450', '300', 'true', 'popup')\" class=\"object\">";
				strReturnHTML +=XSSUtil.encodeForHTML(context,strMEPName);
				strReturnHTML +="</A>";
				strReturnHTML += "</td>";
			}
			}
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML +="\n";
			} else {
			strReturnHTML += "</tr>";
			}
			if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
				} else {
					strCSSClass = "even";
				}
				
			//Added by Lalitha for Issue-215 --starts
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="Description\t";
			} else {
				strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>Description</b></td>";
			}
			
			for (int i2 = 0; i2 < listLocEquivMEPs.size(); i2++) {
				tempMap 			= (Map) listLocEquivMEPs.get(i2);
				strMEPId			=(String)tempMap.get(DomainConstants.SELECT_ID);
				DomainObject   MEPdomainObject 	= new DomainObject(strMEPId);
				sDescription					= MEPdomainObject.getDescription(context);
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+=sDescription+"\t";
				} else {
					strReturnHTML += "<td colspan='1'>";
					strReturnHTML += sDescription;
					strReturnHTML += "</td>";
				}
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML +="\n";
			} else {
				strReturnHTML += "</tr>";
			}
			if("even".equals(strCSSClass)) {
				strCSSClass = "odd";
			} else {
				strCSSClass = "even";
			}
			//Added by Lalitha for Issue-215 --Ends
			
			
			//Added by Lalitha for Issue-215 --starts
			String GeneralClassName 		= "";
			String LibraryClassName 		= "";
			String GeneralClassId 			= "";
			String LibraryClassId 			= "";
			String strToolName				= "";
			String strToolId				= "";
			StringList IHSList				= new StringList();
			StringList GeneralList			= new StringList();
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="Classification Path\t";
			} else {
				strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>Classification Path</b></td>";
			}
			for (int i6 = 0; i6 < listLocEquivMEPs.size(); i6++) {
				tempMap = (Map) listLocEquivMEPs.get(i6);
				strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
				DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
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
					Iterator itr = classificationList.iterator();
					while(itr.hasNext()){
						Map classMap 		= (Map)itr.next();
						strToolName 		= (String)classMap.get(DomainConstants.SELECT_NAME);
						strToolId 			= (String)classMap.get(DomainConstants.SELECT_ID);
						//Modified for All Level SubClass by Preethi Rajaraman -- Starts
						Vector vecResult = getAllSubclasses(context, strToolId+"|"+strToolName);
						Collections.reverse(vecResult);
						Iterator value = vecResult.iterator();
						strReturnHTML += "<td colspan='1'>";						
						while (value.hasNext()) { 
							String sSubclass 					= (String) value.next();
							StringList StrObjIdNameList 		= FrameworkUtil.split(sSubclass, "|");
							int size 							= StrObjIdNameList.size();
							String strClassId      				= "";
							String strClassName      				= "";
							if (size == 2) {
								strClassId      = (String)StrObjIdNameList.get(0);
								strClassName      = (String)StrObjIdNameList.get(1);
							} else if (size == 3){
								strClassId      = (String)StrObjIdNameList.get(1);
								strClassName      = (String)StrObjIdNameList.get(2);
							}
							strReturnHTML +="<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=";
							strReturnHTML +="engineeringcentral";
							strReturnHTML +="&amp;suiteKey=";
							strReturnHTML +="EngineeringCentral";
							strReturnHTML +="&amp;objectId=";
							strReturnHTML +=strClassId;
							strReturnHTML +="', '450', '300', 'true', 'popup')\" class=\"object\">";
							strReturnHTML +=XSSUtil.encodeForHTML(context,strClassName);
							strReturnHTML +="</A>";
							if (value.hasNext()) { 
								strReturnHTML +=" <img border=\"0\" src=\"";
								strReturnHTML +="../common/images/iconTreeToArrow.gif";
								strReturnHTML +="\" /> "; 
							}
						}
						strReturnHTML += "</td>";
						
					}
					//Modified for All Level SubClass by Preethi Rajaraman -- Ends			
				} else {
					if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+="\t";
					} else {
						strReturnHTML += "<td colspan='1'>";
						strReturnHTML += "";
						strReturnHTML += "</td>";
					}
				}
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML +="\n";
			} else {
			strReturnHTML += "</tr>";
			}
			if("even".equals(strCSSClass)) {
				strCSSClass = "odd";
			} else {
				strCSSClass = "even";
			}
			//Added by Lalitha for Issue-215 --Ends
			
			/* if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="ERP/INV Number\t";
			} else {
				strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>ERP/INV Number</b></td>";
			} */
		/* 	for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
				tempMap = (Map) listLocEquivMEPs.get(i1);
				//strMEPName=(String)tempMap.get("to[Manufacturer Equivalent].from.attribute[XORGERP Part Number].value");
				strMEPName=strParentPartERPNumber;
				
				/* Object objMEPName = (Object)tempMap.get("to[Manufacturer Equivalent].from.attribute[XORGERP Part Number].value");
								if(objMEPName instanceof StringList){
									StringList strMEPNameList=(StringList)tempMap.get("to[Manufacturer Equivalent].from.attribute[XORGERP Part Number].value");
									strMEPName = (String) strMEPNameList.get(0);
								} else if(objMEPName instanceof String){
									strMEPName = (String) tempMap.get("to[Manufacturer Equivalent].from.attribute[XORGERP Part Number].value");
								} */
								
								
				
				/*strMEPName=XSSUtil.encodeForHTML(context,strMEPName);
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+=strMEPName+"\t";
			} else {
				strReturnHTML += "<td colspan='1'>"+strMEPName+"</td>";
			}
			} */
		/* 	if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="\n";
			} else {
			strReturnHTML += "</tr>";
			}
			if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
				} else {
					strCSSClass = "even";
				} */
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="IHS Status"+"\t";
			} else {
				strReturnHTML += "<tr class='"+strCSSClass+"'>";
			strReturnHTML += "<td colspan='1'><b>IHS Status</b></td>";
			}
			for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
				tempMap = (Map) listLocEquivMEPs.get(i1);
				strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
				DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
				String strIHSStatus 		= doMEP.getInfo(context, "attribute["+ATTRIBUTE_IHS_STATUS+"]");
				String sIHSStatus			= strIHSStatus.replace(" ", "");
				String strIHSStatusTitle 	= EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", context.getLocale(),"emxEngineeringCentral.MEPPart.IHSStatus."+sIHSStatus.trim());
				if(UIUtil.isNullOrEmpty(strIHSStatusTitle) || strIHSStatusTitle.contains("emxEngineeringCentral")){
					strIHSStatusTitle = strIHSStatus;
				}
				String strIHSSynchLink = "<div title=\" "+strIHSStatusTitle+" \">"+strIHSStatus+ "</div>";
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+=strIHSStatus+"\t";
				} else {
				strReturnHTML += "<td colspan='1'>"+strIHSSynchLink+"</td>";
				}
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+="\n";
				} else {
			strReturnHTML += "</tr>";
				}
			
			
				
		
			 MapList allfieldMapList=new MapList();
			for (int i1 = 0; i1 < listLocEquivMEPs.size(); i1++) {
				tempMap = (Map) listLocEquivMEPs.get(i1);
				strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
				DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
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
					
					int noOfClasses = classificationList.size();
					 MapList fieldMapList=new MapList();
					  MapList classificationAttributesList = new MapList();
					 if(noOfClasses>0){
				Iterator itr = classificationList.iterator();
				while(itr.hasNext()){
					Map classMap = (Map)itr.next();

					MapList classificationAttributes = getClassClassificationAttributes(context, (String)classMap.get(DomainConstants.SELECT_ID));
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
			
				String MQLResult  			= MqlUtil.mqlCommand(context, "list page XORGGenericIncludeAttributesInMEP");
					StringList slSymbolicIncludeAttributes  = new StringList();
					String sActualAttribute = "";
					String sSymbolicAttribute = "";
					if (MQLResult != null && MQLResult.length() !=0)
					{
						MQLResult  = MqlUtil.mqlCommand(context, "print page XORGGenericIncludeAttributesInMEP select content dump");
						slSymbolicIncludeAttributes = FrameworkUtil.split(MQLResult, ",");
						 MapList fieldMapList2=new MapList();
						for(int i=0;i<slSymbolicIncludeAttributes.size();i++) {
							sSymbolicAttribute 	= (String) slSymbolicIncludeAttributes.get(i);
							sActualAttribute 	= PropertyUtil.getSchemaProperty(context,sSymbolicAttribute);
							String strLanguage =  context.getSession().getLanguage();
							HashMap fieldMap = new HashMap();
							fieldMap.put("name",sActualAttribute);
							fieldMap.put("label",sActualAttribute);
							fieldMapList2.add(fieldMap);
						}
						allfieldMapList.addAll(fieldMapList2);
						
					}
					
			StringList attrList=new StringList();
			//Added by Lalitha for Issue-215 --starts
				if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
				} else {
					strCSSClass = "even";
				}
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="Part Descp"+"\t";
				} else {
					strReturnHTML += "<tr class='"+strCSSClass+"'>";
					strReturnHTML += "<td colspan='1'><b>Part Descp</b></td>";
				}
				for (int i5 = 0; i5 < listLocEquivMEPs.size(); i5++) {
						tempMap = (Map) listLocEquivMEPs.get(i5);
						strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
						DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
						String strIHSAttrValue1		= doMEP.getInfo(context, "attribute[IHSPart Description].value");
						
						if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
							strReturnHTML+="\""+strIHSAttrValue1+"\"\t";
						} else {
								strReturnHTML += "<td colspan='1'>"+strIHSAttrValue1+"</td>";
						}
				}
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
							strReturnHTML+="\n";
				} else {
						strReturnHTML += "</tr>";
				}

			//Added by Lalitha for Issue-215 --Ends

			for (int j2 = 0; j2 < allfieldMapList.size(); j2++) {
				//commented by Lalitha for Issue-215 --starts
				/* 	if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
				} else {
					strCSSClass = "even";
				} */
				//commented by Lalitha for Issue-215 --Ends
				tempMap = (Map) allfieldMapList.get(j2);
				String strMEPClassifiedAttrLabel=(String)tempMap.get("label");
				String strMEPClassifiedAttrName=(String)tempMap.get("name");
				
				if(!attrList.contains(strMEPClassifiedAttrName))
				{
					attrList.addElement(strMEPClassifiedAttrName);
					
					
					
					String MQLResult2  			= MqlUtil.mqlCommand(context, "list page XORGGenericExcludeAttributesInMEP");
					StringList slSymbolicExcludeTypes  = new StringList();
					String sActualType = "";
					String sSymbolicType = "";
					boolean isExcludeAttribute=false;
					if (MQLResult2 != null && MQLResult2.length() !=0)
					{
						MQLResult2  = MqlUtil.mqlCommand(context, "print page XORGGenericExcludeAttributesInMEP select content dump");
						slSymbolicExcludeTypes = FrameworkUtil.split(MQLResult2, ",");
						for(int i=0;i<slSymbolicExcludeTypes.size();i++) {
							sSymbolicType 	= (String) slSymbolicExcludeTypes.get(i);
							sActualType 	= PropertyUtil.getSchemaProperty(context,sSymbolicType);
							if(strMEPClassifiedAttrName.equals(sActualType))
							{
								isExcludeAttribute=true;
							}
						}
					}
					
					
					if (!isExcludeAttribute)
					{
					
					if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
						//Modified by Lalitha for Issue-215 --starts
						if (!strMEPClassifiedAttrLabel.equals("Part Descp")) {
								strReturnHTML+=strMEPClassifiedAttrLabel+"\t";
						}
						//Modified by Lalitha for Issue-215 --Ends
								
					} else {
						//Modified by Lalitha for Issue-215 --starts
						if (!strMEPClassifiedAttrLabel.equals("Part Descp")) {
							if("even".equals(strCSSClass)) {
							strCSSClass = "odd";
							} else {
							strCSSClass = "even";
							}
							strReturnHTML += "<tr class='"+strCSSClass+"'>";
							strReturnHTML += "<td colspan='1'><b>"+strMEPClassifiedAttrLabel+"</b></td>";
						}
						//Modified by Lalitha for Issue-215 --Ends
					}
					
					for (int i4 = 0; i4 < listLocEquivMEPs.size(); i4++) {
						tempMap = (Map) listLocEquivMEPs.get(i4);
						strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
						DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
						String strIHSAttrValue		= doMEP.getInfo(context, "attribute["+strMEPClassifiedAttrName+"].value");
						//Added by Lalitha on 11/9/2018 --starts
						String sIHSUrl 	= PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL");
						String strIHSAttValue  = doMEP.getInfo(context, "attribute["+sIHSUrl+"].value");
						StringList strlDataURL = FrameworkUtil.split(strIHSAttValue, "\n");
						//Added by Lalitha on 11/9/2018 --Ends
						if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
							//Modified by Lalitha for Issue-215 --starts
							if (!strMEPClassifiedAttrName.equals("IHSPart Description")) {
								strReturnHTML+="\""+strIHSAttrValue+"\"\t";
							}
							//Modified by Lalitha for Issue-215 --Ends
						} else {
							
							//Modified by Preethi Rajaraman for Issue-62 -- Starts
							String sLatestUrl 	= PropertyUtil.getSchemaProperty(context,"attribute_IHSLatestDataSheetURL");
							if(strMEPClassifiedAttrName.equalsIgnoreCase(sLatestUrl)) {
								//Added by Ravindra on 11/14/2018 for Issue #85 Fix --starts
								if(strIHSAttrValue!=null && !strIHSAttrValue.isEmpty())
								{
								//Added by Ravindra on 11/14/2018 for Issue #85 Fix --ends
								int lastindex =strIHSAttrValue.length();
								int index=strIHSAttrValue.indexOf("https");
								String sValue1 = strIHSAttrValue.substring(0, index);
								//Added by Lalitha for #68 Issue on 12/5/2018 --starts
								sValue1 	   = sValue1.replaceAll(":","");
								//Added by Lalitha for #68 Issue on 12/5/2018 --ends
								String sValue2 = strIHSAttrValue.substring(index, lastindex);
								String strIHSAttrValue1=XSSUtil.encodeForHTML(context,sValue1);
								String strIHSAttrValue2=XSSUtil.encodeForHTML(context,sValue2);
								//Modified by Lalitha --starts
								strReturnHTML += "<td colspan='1'><a href ="+strIHSAttrValue2+" target=_blank>"+strIHSAttrValue1+"</a><a href=\"JavaScript:emxTableColumnLinkClick('../common/emxForm.jsp?form=type_MepLatestHistory&amp;header=IHS Datasheet history&amp;objectId=" + XSSUtil.encodeForHTMLAttribute(context, strMEPId) + "', '800', '575')\">";
								//Modified by Lalitha --Ends
								//Added by Lalitha --starts
								String strHistory = "History";
								//Modified by Lalitha on 11/9/2018 --starts
								if (!UIUtil.isNullOrEmpty(strIHSAttrValue2) && !UIUtil.isNullOrEmpty(strIHSAttrValue1) && strlDataURL.size() != 2) {
									strReturnHTML +="<img border=\"0\" src=\"";
									strReturnHTML +="../common/images/More.png";
									strReturnHTML +="\" title=\""+strHistory ;
									strReturnHTML +="\" /> ";
									strReturnHTML +="</a> ";
								}
								//Modified by Lalitha on 11/9/2018 --Ends
								strReturnHTML +="</td>";
								//Added by Lalitha --Ends
								} else {
									//Added by Ravindra on 11/14/2018 for Issue #85 Fix --starts
									strReturnHTML += "<td colspan='1'>"+""+"</td>";
								}
							} else {
								if(strIHSAttrValue!=null && !strIHSAttrValue.isEmpty())
								{
									//Added by Ravindra on 11/14/2018 for Issue #85 Fix --ends
								strIHSAttrValue=XSSUtil.encodeForHTML(context,strIHSAttrValue);
								//Modified by Lalitha for Issue-215 --starts
								if (!strMEPClassifiedAttrName.equals("IHSPart Description")) {
									strReturnHTML += "<td colspan='1'>"+strIHSAttrValue+"</td>";
								}
								//Modified by Lalitha for Issue-215 --Ends
								//Added by Ravindra on 11/14/2018 for Issue #85 Fix --starts
								} else {
									strReturnHTML += "<td colspan='1'>"+""+"</td>";
								}
								
							}
							//Added by Ravindra on 11/14/2018 for Issue #85 Fix --ends
							//Modified by Preethi Rajaraman for Issue-62 -- Ends
						}
						}
						if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
							strReturnHTML+="\n";
						} else {
						strReturnHTML += "</tr>";
						}
					}
				} else {
						
				}
			}
			
			//Added by Lalitha for #68 Issue	--starts
			if("even".equals(strCSSClass)) {
					strCSSClass = "odd";
			} else {
				strCSSClass = "even";
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML	+= "Product Change Notifications\t";
			} else {
				strReturnHTML 	+= "<tr class='"+strCSSClass+"'>";
				strReturnHTML 	+= "<td colspan='1'><b>Product Change Notifications</b></td>";
			}
			for (int i4 = 0; i4 < listLocEquivMEPs.size(); i4++) {
				HashMap programMap 					= new HashMap();
				HashMap requestMap 					= new HashMap();
				requestMap.put("objectId",strMEPId);
				programMap.put("requestMap",requestMap);
				String[] sargs 						= JPO.packArgs (programMap);
				XORGIHSIntegration_mxJPO  jpoCustom = new XORGIHSIntegration_mxJPO(context,sargs);
				String PCNValue 					= jpoCustom.displayProductChangeNotification(context,sargs); 
				if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
					strReturnHTML+="\""+PCNValue+"\"\t";
				} else {
					strReturnHTML += "<td colspan='1'>"+PCNValue+"</td>";
				}
			}
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML+="\n";
			} else {
				strReturnHTML += "</tr>";
			}
			//Added by Lalitha for #68 Issue	--Ends
			
			if(null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML	+="\n";
			} else {
				strReturnHTML += "</table>";
			}
		} else {
			strReturnHTML="";
		}
		return strReturnHTML;
	}
	
	public MapList getClassClassificationAttributes(Context context,String[] args) throws Exception{

    	String classObjectId = (String)JPO.unpackArgs(args);

    	return getClassClassificationAttributes(context, classObjectId);
    }
    /**
     * method to get the classification attributes of all the attribute groups connected to the class
     * @param context the eMatrix <code>Context</code> object
     * @param objectId - objectId of the class whose attributes are to be returned
     * @returns - a MapList of Attributes Groups, each map with following key-Value Pairs
                   1) "name" - String containing AttributeGroup name
                   2) "attributes" - a MapList of Attributes, each map with following key-Value Pairs
                                    1) "qualifiedName" - String containing qualified name of the Attribute
                                    2) "name"          - String containing name of the Attribute
                                    3) "type"          - String containing type of the Attribute
                                    4) "default"       - String containing default value of the Attribute
                                    5) "description"   - String containing description of the Attribute
                                    6) "maxlength"     - String containing maxlength of the Attribute
                                    7) "dimension"     - name of the dimension
                                    8) "range"         - StringList of range Values
                                    Note: no key-Value pair exists in this map if there is no value for that key
     */
    protected static MapList getClassClassificationAttributes(Context context, String objectId)throws Exception{
        DomainObject classObj   = new DomainObject(objectId);
        StringList selectables  = new StringList();
        String attribute_mxsysInterface = "attribute["+LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE+"].value";
        selectables.add(attribute_mxsysInterface);
        Map classInfo = classObj.getInfo(context, selectables);
        String mxsysInterface = (String)classInfo.get(attribute_mxsysInterface);
        if(UIUtil.isNullOrEmpty(mxsysInterface)){
            // if mxsysInterface is empty then there will be no classification attributes associated with this object
            return new MapList();
        }

        StringList slAttributeGroups = new StringList();

        //get all the AttributeGroups of this class using mxsysInterface
        String mqlQuery = "print interface $1 select $2";
        String sAllParentInterfaces = MqlUtil.mqlCommand(context, mqlQuery, mxsysInterface,"allparents.derived");
        // iterate the values and check for Classification Attribute Groups
        // and then form Attribute groups

        HashMap hmAllParentInterfaces = parseMqlOutput(context, sAllParentInterfaces);
        Set setAllParentInterfaces = hmAllParentInterfaces.keySet();
        Iterator itr = setAllParentInterfaces.iterator();
        while(itr.hasNext()){
            String parentInterfaceName   = (String)itr.next();
            HashMap tempParentInterface  = (HashMap)hmAllParentInterfaces.get(parentInterfaceName);
            if(tempParentInterface != null){
                String parentInterfaceDerived = (String)tempParentInterface.get("derived");
                if(!UIUtil.isNullOrEmpty(parentInterfaceDerived) && parentInterfaceDerived.equals("Classification Attribute Groups")){
                    slAttributeGroups.add(parentInterfaceName);
                }
            }
        }

        MapList attributeGroups = new MapList();
        selectables = new StringList();
        selectables.add("type");
        selectables.add("range");
        selectables.add("multiline");
        selectables.add("valuetype");
        selectables.add("default");
        selectables.add("description");
        selectables.add("maxlength");
        selectables.add("dimension");
        
        //for each attribute group
        for(int i=0;i< slAttributeGroups.size();i++){
            HashMap attibuteGroup = new HashMap();
            String attibuteGroupName = (String)slAttributeGroups.get(i);
            attibuteGroup.put("name", attibuteGroupName);
            MapList attributes = getAttributeGroupAttributesDetails(context, attibuteGroupName, selectables);
            attibuteGroup.put("attributes", attributes);
            attributeGroups.add(attibuteGroup);
        }
        return attributeGroups;
    }
	
	protected static HashMap parseMqlOutput(Context context,String output) throws Exception{
		String UOM_ASSOCIATEDWITHUOM     = "AssociatedWithUOM";
     String DB_UNIT                   = "DB Unit";
     String UOM_UNIT_LIST             = "DB UnitList";
     String UOM_INPUT_UNIT            = "Input Unit";
     String PROPNAME_START_DELIMITER  = "[";
     String PROPNAME_END_DELIMITER    = "]";
     String RESULT_DELIMITER          = " =";
     String RANGE_START_DELIMITER     = "[";
     String SETTING_REMOVE_RANGE_BLANK = "Remove Range Blank";
        BufferedReader in = new BufferedReader(new StringReader(output));
        String resultLine;
        HashMap mqlResult = new HashMap();
        while((resultLine = in.readLine()) != null){
            String property = null;
            String propertyName = null;
            String subProperty = null;
            String result = null;

            try{
                //identify property propertyValue subProperty subPropertyValue  - start
                boolean hasRanges = false;
                int propNameStartDelimIndex = resultLine.indexOf(PROPNAME_START_DELIMITER);
                int resultDelimIndex        = resultLine.indexOf(RESULT_DELIMITER);

                property                    = resultLine.substring(0, propNameStartDelimIndex);

                int propNameEndDelimIndex   = resultLine.indexOf(PROPNAME_END_DELIMITER, propNameStartDelimIndex);
                propertyName                = resultLine.substring(propNameStartDelimIndex+1, propNameEndDelimIndex);

                String propertyAndValue     = property + PROPNAME_START_DELIMITER+propertyName+PROPNAME_END_DELIMITER;
                String remainingResultLine  = resultLine.substring(propertyAndValue.length());

                // if remaining result starts with .
                int rangeStartDelimIndex    = remainingResultLine.indexOf(RANGE_START_DELIMITER);
                resultDelimIndex            = remainingResultLine.indexOf(RESULT_DELIMITER);
                if((rangeStartDelimIndex != -1) && (rangeStartDelimIndex < resultDelimIndex)){
                    // if [ exists and comes before = , then anything before [ is the subProperty and subProperty contains range of results
                    subProperty = remainingResultLine.substring(1,rangeStartDelimIndex);
                    hasRanges   = true;
                }else{
                    // else , anything Before = is the subProperty
                    subProperty = remainingResultLine.substring(1,resultDelimIndex);
                }

                result   = remainingResultLine.substring(resultDelimIndex+RESULT_DELIMITER.length());

                property = property.trim();
                result   = result.trim();

                //identify property propertyValue subProperty subPropertyValue  - end

                //start building HashMap
                HashMap hmPropertyName;
                StringList slSubProperty;

                hmPropertyName = (HashMap)mqlResult.get(propertyName);
                if(hmPropertyName == null){
                    hmPropertyName = new HashMap();
                    mqlResult.put(propertyName, hmPropertyName);
                }
                if(hasRanges){
                    slSubProperty = (StringList)hmPropertyName.get(subProperty);
                    if(slSubProperty == null){
                        slSubProperty = new StringList();
                        hmPropertyName.put(subProperty,slSubProperty);
                    }
                    slSubProperty.add(result);
                }else{
                    hmPropertyName.put(subProperty,result);
                }

            }catch(Exception e){
                // if there is exception during parsing a line , proceed to next line
            }
        }

        return mqlResult;
    }
	
	    public static MapList getAttributeGroupAttributesDetails(Context context,String agName,StringList selectables)throws Exception{
        StringBuffer cmd = new StringBuffer("print interface \"$1\" select "); // Move select
        
        selectables.add("owner");
        selectables.add("hidden");
        
        String[] newArgs = new String[selectables.size()+1];
        newArgs[0] = agName;
        for(int i=0;i<selectables.size();i++){
            cmd.append("\"$"+(i+2)+"\" ");
            newArgs[i+1] = "attribute."+(String)selectables.get(i);
        }

        String result = MqlUtil.mqlCommand(context,cmd.toString(),true,newArgs);

        HashMap hmAllAttributeDetails = parseMqlOutput(context, result);
        MapList agAttributesDetails = new MapList();

        Set setAllAttributeDetails = hmAllAttributeDetails.keySet();
        Iterator itr = setAllAttributeDetails.iterator();
        while(itr.hasNext()){
            String attributeName = (String)itr.next();
            HashMap hmAttributeDetails = (HashMap)hmAllAttributeDetails.get(attributeName);
            if(hmAttributeDetails != null){
            	//Fix for TF4_Agile_Bug_18- Start
            	//String hidden = (String)hmAllAttributeDetails.get("hidden");
            	String hidden = (String)hmAttributeDetails.get("hidden");
            	//Fix for TF4_Agile_Bug_18- End
                if("true".equalsIgnoreCase(hidden)){
                    continue;
                }
            	String owner = (String)hmAttributeDetails.get("owner");
            	String qualifiedName = attributeName;
            	if(UIUtil.isNotNullAndNotEmpty(owner)){
            		qualifiedName = owner+"."+attributeName;
            	}
                hmAttributeDetails.put("qualifiedName", qualifiedName);
                hmAttributeDetails.put("name", attributeName);
                
                // convert range StringList to MapList
                StringList range = (StringList)hmAttributeDetails.get("range");
                if(range != null){
                	MapList mlRange = new MapList();
                	Iterator<String> rangeItr = range.iterator();
                	while(rangeItr.hasNext()){
                		String rangeItem = rangeItr.next();
                		int rangeValueIndex = rangeItem.indexOf(" ");
                		if(rangeValueIndex == -1){
                			rangeItem = rangeItem + " ";
                			rangeValueIndex = rangeItem.indexOf(" ");
                			}
                		String rangeOperator = rangeItem.substring(0, rangeValueIndex);
                		if(rangeOperator.equals("uses")){
                			rangeOperator = "program";
                			rangeValueIndex = rangeItem.indexOf(" ", rangeValueIndex+1);
                		}
                		
                		String rangeValue = rangeItem.substring(rangeValueIndex+1);
                		
                		HashMap hmRangeItem = new HashMap();
                		hmRangeItem.put("operator", rangeOperator);
                		hmRangeItem.put("value", rangeValue);
                		
                		mlRange.add(hmRangeItem);
                	}
                	hmAttributeDetails.put("range", mlRange);
                }
                agAttributesDetails.add(hmAttributeDetails);
            }
        }

        return agAttributesDetails;
    }
	
	 private MapList getDynamicFieldsMapList(Context context,MapList classificationAttributesList,boolean isCreate) throws Exception{

        //Define a new MapList to return.
        MapList fieldMapList = new MapList();
        String strLanguage =  context.getSession().getLanguage();

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
               fieldMap.put("name",attributeQualifiedName);
               fieldMap.put("label",i18nNow.getAttributeI18NString(attributeName,strLanguage));
			   fieldMapList.add(fieldMap);
            }
        }

        }



        return fieldMapList;
    }
	//Added for Issue #527 Ends	
	
	//Added for Issue 63 by Ravindra Starts
	public Boolean  checkAccessForLibraryAttributes(Context context, String []args) throws Exception
    {
        HashMap paramMap = (HashMap)JPO.unpackArgs(args);
        String objectId      = (String) paramMap.get("objectId");
		boolean hasCreateAccess = false;
		if (!"null".equalsIgnoreCase(objectId) && !"".equalsIgnoreCase(objectId) && objectId != null)
        {
			DomainObject endItemObj = new DomainObject(objectId);
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			MapList classificationList = endItemObj.getRelatedObjects(context,
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
			if(noOfClasses>0){
				hasCreateAccess = true;
			} else {
				hasCreateAccess = false;
			}
        }
        return Boolean.valueOf(hasCreateAccess);
    }
	//Added for Issue 63 by Ravindra Ends
	//Added by Preethi Rajaraman for Issue-62 -- Starts
	public String getIHSURL(Context context,String[] args) throws Exception {
		String result ="";
		String sIHSUrl 	= PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL");
		try {
			HashMap paramMap 		= (HashMap)JPO.unpackArgs(args);
			HashMap requestMap 	= (HashMap)paramMap.get("requestMap");
			String objectId      	= (String) requestMap.get("objectId");
			DomainObject domObj		= new DomainObject(objectId);
			String strIHSAttrValue  = domObj.getInfo(context, "attribute["+sIHSUrl+"].value");
			StringList slDataURL = FrameworkUtil.split(strIHSAttrValue, "\n");
			String sMain = "";
			for(int i=0;i<slDataURL.size();i++) {
				 String sValue = (String)slDataURL.get(i);
				 //Added by Lalitha for #68 Issue on 12/5/2018 --starts
				 sMain 	   	= sMain.replaceAll(":","");
				//Added by Lalitha for #68 Issue on 12/5/2018 --ends
				 if (sValue.startsWith("https:")) {
					result +="<a href ="+sValue+" target=_blank>"+sMain+"</a>";
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
	//Added by Preethi Rajaraman for Issue-62 -- Ends
	
	//Added by Ravindra for Customer Visible Attribute Value Check - Starts
	public int checkForCustomerVisibleValue(Context context,String args[]) throws Exception {
		String googCustomerVisible 		 		= PropertyUtil.getSchemaProperty("attribute_googCustomerVisible");
		//Added for Issue-307 by Preethi Rajaraman -- Starts
		String googCustomerVisiblePartDetails	= PropertyUtil.getSchemaProperty("attribute_googCustomerVisiblePartDetails");
		StringList objectSelects				= new StringList();
		objectSelects.add("attribute["+googCustomerVisible+"].value");
		objectSelects.add("attribute["+googCustomerVisiblePartDetails+"].value");
		//Added for Issue-307 by Preethi Rajaraman -- Ends
		try {
			String sCAID 						= args[0];
			DomainObject domObject 				= new DomainObject(sCAID);
			//Modified for Issue-307 by Preethi Rajaraman -- Starts
			Map objInfo							= domObject.getInfo(context,objectSelects);
			String sCustomerVisibleValue 		= "";
			String sCustomerVisiblePartValue	= "";
			if(!objInfo.isEmpty()){
				sCustomerVisibleValue 			= (String)objInfo.get("attribute["+googCustomerVisible+"].value");
				sCustomerVisiblePartValue 		= (String)objInfo.get("attribute["+googCustomerVisiblePartDetails+"].value");
			}
			//Modified for Issue-307 by Preethi Rajaraman -- Ends	
			if(null == sCustomerVisibleValue || "".equals(sCustomerVisibleValue))
			{
				String strSetMessage 		= "Please fill a Valid Value for Impact to Customer Visible Parts? to Proceed with Promotion.";
				${CLASS:emxContextUtil}.mqlNotice(context, strSetMessage);
				return 1;
			} else if("Unassigned".equalsIgnoreCase(sCustomerVisibleValue)) {
				String strSetMessage 		= "Please fill a Valid Value for Impact to Customer Visible Parts? to Proceed with Promotion.";
				${CLASS:emxContextUtil}.mqlNotice(context, strSetMessage);
				return 1;
			//Modified for Issue-307 by Preethi Rajaraman -- Starts
			} else if("Yes".equalsIgnoreCase(sCustomerVisibleValue)) {
				if ("".equalsIgnoreCase(sCustomerVisiblePartValue)) {
					String strSetMessage 		= "Please enter Customer Visible Part Details for the Change Action to Proceed with Promotion.";
					${CLASS:emxContextUtil}.mqlNotice(context, strSetMessage);
					return 1;
				}
			}
			//Modified for Issue-307 by Preethi Rajaraman -- Ends
		} catch (Exception ex) {
            throw ex;
        }
		return 0;
	}
	//Added by Ravindra for Customer Visible Attribute Value Check - Ends
	//Added by Preethi Rajaraman  for Issue -313  - Starts
	public String getMEPClassificationPath(Context context, String[] args) throws Exception{
		String strReturnHTML 				= "";
		StringBuffer sbRelPattern			= new StringBuffer(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
		StringBuffer typePattern 			= new StringBuffer(DomainConstants.TYPE_PART);
		StringBuffer sbTypePattern 			= new StringBuffer(typePattern.toString());
		MapList listLocEquivMEPs 			= new MapList();
		StringList selectStmts 				= new StringList(6);
        selectStmts.addElement(DomainConstants.SELECT_ID);
		StringList selectRelStmts 			= new StringList(2);
		String REL_MANUFACTURE_EQUVALENT  	= PropertyUtil.getSchemaProperty("relationship_ManufacturerEquivalent");
		String ATTRIBUTE_WAYMO 				= PropertyUtil.getSchemaProperty("attribute_waymoPreferedMPN");
		String whereclause					= "(to["+REL_MANUFACTURE_EQUVALENT+"].attribute["+ATTRIBUTE_WAYMO+"].value != \"Non Prefered\" )";
        Map tempMap 						= null;
		String GeneralClassName 			= "";
		String LibraryClassName 			= "";
		String GeneralClassId 				= "";
		String LibraryClassId 				= "";
		String strToolName					= "";
		String strToolId					= "";
		String strMEPId						= "";
		StringList IHSList					= new StringList();
		StringList GeneralList				= new StringList();
		StringList IdList					= new StringList();
		
		try {
			HashMap paramMap 				= (HashMap)JPO.unpackArgs(args);
			HashMap requestMap 				= (HashMap)paramMap.get("requestMap");
			String objectId      			= (String) requestMap.get("objectId");
			DomainObject partObj 			= DomainObject.newInstance(context, objectId);
			listLocEquivMEPs 				= partObj.getRelatedObjects(context, sbRelPattern
                    .toString(), // relationship pattern
                    sbTypePattern.toString(), // object pattern
                    selectStmts, // object selects
                    selectRelStmts, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 1, // recursion level
                    whereclause, // object where clause
                    null); // relationship where clause
			for (int i = 0; i < listLocEquivMEPs.size(); i++) {
				tempMap = (Map) listLocEquivMEPs.get(i);
				strMEPId=(String)tempMap.get(DomainConstants.SELECT_ID);
				DomainObject doMEP = DomainObject.newInstance(context, strMEPId);
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
				   strReturnHTML += "<table width=\"100%\" cellspacing=\"0\" cell padding=\"0\">";
					Iterator itr = classificationList.iterator();
					while(itr.hasNext()){
						Map classMap 		= (Map)itr.next();
						strToolName 		= (String)classMap.get(DomainConstants.SELECT_NAME);
						strToolId 			= (String)classMap.get(DomainConstants.SELECT_ID);
						//Modified for All Level SubClass by Preethi Rajaraman -- Starts
						if (!IdList.contains(strToolId)) {
							IdList.add(strToolId);
							Vector vecResult = getAllSubclasses(context, strToolId+"|"+strToolName);
							Collections.reverse(vecResult);
							Iterator value = vecResult.iterator();
							strReturnHTML += "<tr><td>";
							while (value.hasNext()) { 
								String sSubclass 					= (String) value.next();
								StringList StrObjIdNameList 		= FrameworkUtil.split(sSubclass, "|");
								int size 							= StrObjIdNameList.size();
								String strClassId      				= "";
								String strClassName      				= "";
								if (size == 2) {
									strClassId      = (String)StrObjIdNameList.get(0);
									strClassName      = (String)StrObjIdNameList.get(1);
								} else if (size == 3){
									strClassId      = (String)StrObjIdNameList.get(1);
									strClassName      = (String)StrObjIdNameList.get(2);
								}
								
								strReturnHTML +="<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=";
								strReturnHTML +="engineeringcentral";
								strReturnHTML +="&amp;suiteKey=";
								strReturnHTML +="EngineeringCentral";
								strReturnHTML +="&amp;objectId=";
								strReturnHTML +=strClassId;
								strReturnHTML +="', '450', '300', 'true', 'popup')\" class=\"object\">";
								strReturnHTML +=XSSUtil.encodeForHTML(context,strClassName);
								strReturnHTML +="</A>";
								if (value.hasNext()) { 
									strReturnHTML +=" <img border=\"0\" src=\"";
									strReturnHTML +="../common/images/iconTreeToArrow.gif";
									strReturnHTML +="\" /> "; 
								}
							}
							strReturnHTML += "</td></tr>";
						}							
					}
					strReturnHTML += "</table>";	
			   }
			   
			   //Modified for All Level SubClass by Preethi Rajaraman -- Ends
			}	
		} catch (Exception ex) {
			throw ex;
		}
		
		return strReturnHTML;
	}
	//Added by Preethi Rajaraman  for Issue -313  - Ends
	
	//Added by Ravindra for modifying EC Part description to Upper Case - Starts
	
	public void changedescriptiontouppercase(Context context, String[] args) throws Exception
    {
		String POLICY_EC_PART = PropertyUtil.getSchemaProperty("policy_ECPart");
		String POLICY_MANUFACTURER_EQUIVALENT_PART = PropertyUtil.getSchemaProperty("policy_ManufacturerEquivalent");
        String partId = args[0];
		String[] oids = new String[1];
		oids[0] = partId;
		${CLASS:emxDomainObject} partObject = new ${CLASS:emxDomainObject}(context,oids);
		String partPolicy = partObject.getInfo(context,"policy");
		if (partObject.isKindOf(context, DomainConstants.TYPE_PART) && (partPolicy.equals(POLICY_EC_PART) || partPolicy.equals(POLICY_MANUFACTURER_EQUIVALENT_PART))) {
		String partdesc = partObject.getInfo(context,"description");
		String partdescUpper = partdesc.toUpperCase(); 
		partObject.setDescription(context,partdescUpper);
		}
    }
	
	public void changegoogPropdescriptiontouppercase(Context context, String[] args) throws Exception
    {
		String POLICY_EC_PART = PropertyUtil.getSchemaProperty("policy_ECPart");
		String POLICY_MANUFACTURER_EQUIVALENT_PART = PropertyUtil.getSchemaProperty("policy_ManufacturerEquivalent");
		String sgoogPropDesc  = PropertyUtil.getSchemaProperty(context,"attribute_googPropsedDescription");
        String partId = args[0];
		String partGoogDesc = args[1];
		String[] oids = new String[1];
		oids[0] = partId;
		${CLASS:emxDomainObject} partObject = new ${CLASS:emxDomainObject}(context,oids);
		String partPolicy = partObject.getInfo(context,"policy");
		if (partObject.isKindOf(context, DomainConstants.TYPE_PART) && (partPolicy.equals(POLICY_EC_PART) || partPolicy.equals(POLICY_MANUFACTURER_EQUIVALENT_PART))) {
		String partdescUpper = partGoogDesc.toUpperCase(); 
		partObject.setAttributeValue(context,sgoogPropDesc,partdescUpper);
		}
    }
	
	public void changeVPMdescriptiontouppercase(Context context, String[] args) throws Exception
    {
		String TYPE_VPM_REFERENCE = PropertyUtil.getSchemaProperty("type_VPMReference");
		String sVPMDesc  = PropertyUtil.getSchemaProperty(context,"attribute_PLMEntity.V_description");
        String partId = args[0];
		String partVPMDesc = args[1];
		String[] oids = new String[1];
		oids[0] = partId;
		${CLASS:emxDomainObject} partObject = new ${CLASS:emxDomainObject}(context,oids);
		if (partObject.isKindOf(context, TYPE_VPM_REFERENCE)) {
		String partVPMDescUpper = partVPMDesc.toUpperCase(); 
		partObject.setAttributeValue(context,sVPMDesc,partVPMDescUpper);
		}
    }
	//Added by Ravindra for modifying EC Part description to Upper Case - Ends
	//Modified for All Level SubClass by Preethi Rajaraman -- Starts
	public Vector getAllSubclasses(Context context,String strObjIdName) throws Exception {
        Vector vecOldRevisions = new Vector();
        if (strObjIdName != null) {
			vecOldRevisions.addElement(strObjIdName);
			StringList StrObjIdNameList 		=  FrameworkUtil.split(strObjIdName, "|");
			String strObjId      = (String)StrObjIdNameList.get(0);
            String strObjectIdName = getPrevSubclass(context, strObjId);
			while (strObjectIdName != null && !strObjectIdName.equals("")) {
				vecOldRevisions.addElement(strObjectIdName);
				StringList StrObjIdNameList2 		=  FrameworkUtil.split(strObjectIdName, "|");
				String strObjectId      = (String)StrObjIdNameList2.get(1);
                strObjectIdName = getPrevSubclass(context, strObjectId);
            }
        }
        return vecOldRevisions;
    }
	
	public String getPrevSubclass(Context context, String strObjId)throws Exception {
        String strObjectId = "";
		String MQLResult1  	=  MqlUtil.mqlCommand(context, "query connection type $1 where $2 select  $3 $4 dump $5", "Subclass","to.id=='"+strObjId+"'","from.id","from.name","|");
		if (MQLResult1 != null && !MQLResult1.equals("")) {
            strObjectId = MQLResult1;
        }
        return strObjectId;
    }
	//Modified for All Level SubClass by Preethi Rajaraman -- Ends
	//Added for Id_22 by Preethi Rajaraman  -- Starts
	@SuppressWarnings({ "rawtypes", "unchecked" })
    @com.matrixone.apps.framework.ui.PostProcessCallable 
	public Map updateBOM(Context context, String [] args) throws Exception {
		Map <String, String> actionMap = new HashMap <String, String> ();
		enoUnifiedBOMBase_mxJPO  Unified = new enoUnifiedBOMBase_mxJPO(context,new String[1]);
		actionMap = Unified.updateBOM(context,args);
		return actionMap;
		
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StringBuffer  updateBOMInView(Context context, String [] args) throws Exception {
		StringBuffer sbUIChangeXML = new StringBuffer();
		enoUnifiedBOMBase_mxJPO  Unified = new enoUnifiedBOMBase_mxJPO(context,new String[1]);
		sbUIChangeXML=Unified.updateBOMInView(context,args);
		return sbUIChangeXML;
	}
	//Added for Id_22 by Preethi Rajaraman  -- Ends
	
	//Added for Id_29 by Ravindra  -- Starts
	public Map connectAffectedItems(Context context, String[] args)
				throws Exception 
	{
			
		Map mpInvalidObjects = new HashMap();
		googCustomFunctions_mxJPO  googCustom = new googCustomFunctions_mxJPO();
		mpInvalidObjects=googCustom.connectAffectedItems(context,args);
		return mpInvalidObjects;
	}
	//Added for Id_29 by Ravindra  -- Ends
	//Added for id_33 by Preethi Rajaraman  -- Starts
	public String removeChangeControl(Context context, String [] args)throws Exception {
		
		try {
			HashMap paramMap 		= (HashMap)JPO.unpackArgs(args);
			String objectId      	= (String) paramMap.get("objectId");
			MqlUtil.mqlCommand(context, "mod bus $1 remove interface '$2'",objectId,"Change Control");
		}catch(Exception e) {
			return "Failed";
		}
		return "Success";
	}
	//Added for id_33 by Preethi Rajaraman  -- Ends
	//Added for Id_24 by Ravindra  -- Starts
	public int createAndStartRouteFromTemplateOrReviewers(Context context, String[] args)
				throws Exception 
	{
		int returnVal=0;	
		Map mpInvalidObjects = new HashMap();
		googCustomTriggers_mxJPO  googCustom = new googCustomTriggers_mxJPO(context,args);
		returnVal=googCustom.createAndStartRouteFromTemplateOrReviewers(context,args);
		return returnVal;
	}
	//Added for Id_24 by Ravindra  -- Ends
	//Added for 82 issue by Preethi Rajaraman -- Starts
	public void updateReleaseMaturity(Context context,String[] args) throws Exception{
		String sResult 	   							= "";
		String sAttrName 							= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
		com.matrixone.apps.common.Person origPerson = com.matrixone.apps.common.Person.getPerson(context, context.getUser());
		String sPLMAnalystRoleName 					= PropertyUtil.getSchemaProperty(context,"role_googPLMAnalyst");
		StringList strList 							= new StringList(3);
        strList.add(DomainConstants.SELECT_REVISION);
        strList.add(DomainConstants.SELECT_CURRENT);
		strList.add("attribute["+sAttrName+"]");
		strList.add("revisions.id");
		try{
			HashMap programMap 				= (HashMap) JPO.unpackArgs(args);
			HashMap requestMap 				= (HashMap) programMap.get("paramMap");
			String strPartId 				= (String) requestMap.get("objectId");
			String sNewValue 				= (String) requestMap.get("New Value");
			DomainObject domObj 			= new DomainObject(strPartId);
			Map map 						= domObj.getInfo(context, strList);
			String sCurrent 				= (String) map.get(DomainConstants.SELECT_CURRENT);
			String sRevision 				= (String) map.get(DomainConstants.SELECT_REVISION);
			String sReleaseMaturity 		= (String) map.get("attribute["+sAttrName+"]");
			boolean bflag = false;
			
			StringList slIds = getPreviousRevisions(context,domObj);
			for(int i=0; i<slIds.size();  i++) {
				String sId 			= (String) slIds.get(i);
				DomainObject dObj	= new DomainObject(sId); 
				Map tmap 			= dObj.getInfo(context, strList);
				String sRevisionReleaseMaturity = (String) tmap.get("attribute["+sAttrName+"]");
				if (sRevisionReleaseMaturity!=null && sRevisionReleaseMaturity.equals("Production")) {
					bflag = true;
					break;
				}
			}
			
			
			 if (!sCurrent.equals("Release") && !bflag) {
				domObj.setAttributeValue(context, sAttrName, sNewValue);
			} else if (bflag || sCurrent.equals("Release")){
				if(origPerson.hasRole(context, sPLMAnalystRoleName)) {
					domObj.setAttributeValue(context, sAttrName, sNewValue);
				} else {
					if (sNewValue.equals("Production")) {
						domObj.setAttributeValue(context, sAttrName, sNewValue);
					} else {
					String strMessage = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",new Locale("en"),"emxFramework.ReleaseMaturity.ModifyErrorMessage");
					emxContextUtil_mxJPO.mqlNotice(context, strMessage);
				}
					
				}
			}
		}catch (Exception e) {
            e.printStackTrace();
        }
	}
	public String displayReleaseMaturity(Context context, String[] args) throws Exception {
		String sResult 	   = "";
		StringList strList 	= new StringList(2);
		String sAttrName 	= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
        strList.add("attribute["+sAttrName+"]");
		try{
			HashMap programMap 	= (HashMap) JPO.unpackArgs(args);
			Map requestMap 		= (Map) programMap.get("requestMap");
			String strPartId 	= (String) requestMap.get("objectId");
			DomainObject domObj = new DomainObject(strPartId);
			Map map 			= domObj.getInfo(context, strList);
			sResult 			= (String) map.get("attribute[googPartReleaseMaturity]");
		}catch (Exception e) {
            e.printStackTrace();
        }
		return sResult;
	}
	public Vector<String> displayReleaseMaturityfortable(Context context, String[] args) throws Exception {
		String sResult 	   		= "";
		StringList strList 		= new StringList(2);
		String sAttrName 		= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
        strList.add("attribute["+sAttrName+"]");
		Map programMap 			= JPO.unpackArgs(args);
    	MapList objectList 		= (MapList) programMap.get("objectList");
		Vector<String> result 	= new Vector<String>(objectList.size());
		try{
			Iterator itr 		= objectList.iterator();
    	
			while(itr.hasNext()){
				Map partDetails 		= (Map)itr.next();
				String objectId 		= (String)partDetails.get("id");
				DomainObject partObject = new DomainObject(objectId);
				Map map 				= partObject.getInfo(context, strList);
				sResult 				= (String) map.get("attribute["+sAttrName+"]");
				result.add(sResult);
			}
		}catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}
	public HashMap RangesReleaseMaturityforNew(Context context, String[] args) throws Exception {
		HashMap tempMap								= new HashMap();
		StringList attrList 						= new StringList();
		String sAttrName 							= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
		String strAttrNameSym 						=  "emxFramework."+sAttrName+".Range";
		String sLanguage       						= context.getLocale().getLanguage();
		String strRangeValues						= EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", strAttrNameSym,  sLanguage);
		StringTokenizer stRange 					= new StringTokenizer(strRangeValues,"|");
		while(stRange.hasMoreTokens()) {
			attrList.add(stRange.nextToken());
		}
		tempMap.put("field_choices", attrList);
		tempMap.put("field_display_choices", attrList);
		return tempMap;
	}
	public HashMap RangesReleaseMaturityforOthers(Context context, String[] args) throws Exception {
		HashMap tempMap								= new HashMap();
		StringList attrList 						= new StringList();
		String sAttrName 							= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
		String strAttrNameSym 						=  "emxFramework."+sAttrName+".Range";
		String sLanguage       						= context.getLocale().getLanguage();
		String strRangeValues						= EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", strAttrNameSym,  sLanguage);
		StringTokenizer stRange 					= new StringTokenizer(strRangeValues,"|");
		while(stRange.hasMoreTokens()) {
			String svalue = stRange.nextToken();
			if (!svalue.equals("Unassigned")) {
				attrList.add(svalue);
			}
			
		}
		tempMap.put("field_choices", attrList);
		tempMap.put("field_display_choices", attrList);
		return tempMap;
	}
	public HashMap RangesReleaseMaturity(Context context, String[] args) throws Exception {
		String sAttrName 							= PropertyUtil.getSchemaProperty(context,"attribute_googPartReleaseMaturity");
		com.matrixone.apps.common.Person origPerson = com.matrixone.apps.common.Person.getPerson(context, context.getUser());
		String sPLMAnalystRoleName 					= PropertyUtil.getSchemaProperty(context,"role_googPLMAnalyst");
		String strAttrNameSym 						=  "emxFramework."+sAttrName+".Range";
		String sLanguage       						= context.getLocale().getLanguage();
		String strRangeValues						= EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", strAttrNameSym,  sLanguage);
		HashMap tempMap = new HashMap();
		StringList strList = new StringList(3);
        strList.add(DomainConstants.SELECT_REVISION);
        strList.add(DomainConstants.SELECT_CURRENT);
		strList.add("attribute["+sAttrName+"]");
		strList.add("revisions.id");
		boolean brevise = false;
		boolean bflag 						= false;
		try {
			Map programMap 						= (Map) JPO.unpackArgs(args);
			Map requestMap 						= (Map) programMap.get("requestMap");
			String strPartId 					= (String) requestMap.get("objectId");
			if(UIUtil.isNullOrEmpty(strPartId)){
				strPartId 					= (String) requestMap.get("copyObjectId");
				brevise = true;
			}
			DomainObject domObj 				= new DomainObject(strPartId);
			StringList slIds 					= getPreviousRevisions(context,domObj);
			Map map 							= domObj.getInfo(context, strList);
			String sCurrent 					= (String) map.get(DomainConstants.SELECT_CURRENT);
			String sReleaseMaturity 			= (String) map.get("attribute["+sAttrName+"]");
			if (brevise) {
				if(sReleaseMaturity.equals("Production")) {
					bflag = true;
				}
			}
			
			StringList attrList 				= new StringList();
			for(int i=0; i<slIds.size();  i++) {
					String sId 						= (String) slIds.get(i);
					DomainObject dObj 				= new DomainObject(sId); 
					Map tmap 						= dObj.getInfo(context, strList);
					String sRevisionReleaseMaturity = (String) tmap.get("attribute["+sAttrName+"]");
					if (sRevisionReleaseMaturity!=null && sRevisionReleaseMaturity.equals("Production")) {
						bflag = true;
						break;
					}
			}
			StringTokenizer stRange = new StringTokenizer(strRangeValues,"|");
			  if (bflag || sCurrent.equals("Release")){
				if(origPerson.hasRole(context, sPLMAnalystRoleName)) {
				while(stRange.hasMoreTokens()) {
					String svalue = stRange.nextToken();
					if(!svalue.equals("Unassigned")) {
						attrList.add(svalue);
					}
				}
				} 
			} else if (!sCurrent.equals("Release") && !bflag) {
					while(stRange.hasMoreTokens()) {
						String svalue = stRange.nextToken();
						if(!svalue.equals("Unassigned")) {
							attrList.add(svalue);
						}
					}
				} 
			tempMap.put("field_choices", attrList);
			tempMap.put("field_display_choices", attrList);
		}catch (Exception e) {
            e.printStackTrace();
        }

		return tempMap;
	}
	public String getPreviousRevision(Context context, DomainObject doObj)
           throws Exception {
		
       String strObjectId = "";
	   try {
		   BusinessObject busObj = (BusinessObject) doObj
				   .getPreviousRevision(context);
		   if (busObj != null) {
			   strObjectId = busObj.getObjectId(context);
		   }
	   } catch (Exception e) {
		   strObjectId="";
	   }
       return strObjectId;
   }
   public StringList getPreviousRevisions(Context context,
           DomainObject doObj) throws Exception {
       StringList slOldRevisions = new StringList();
       if (doObj != null) {
           String strObjectId = getPreviousRevision(context, doObj);
           while (strObjectId != null && !strObjectId.equals("")) {
               slOldRevisions.add(strObjectId);
               strObjectId = getPreviousRevision(context, new DomainObject(
                       strObjectId));
           }
       }
       return slOldRevisions;
   }
	//Added for 82 issue by Preethi Rajaraman -- Ends
	/**
	 * The Action trigger  method on (Implementation Review --> Approved) to remove the interface on VPM Reference
	 * @param context
	 * @param args (Change Action Id)
	 * @throws Exception
	 * @author XPLORIA
	 */
	public void unsetChangeControlInterfaceonVPMReference(Context context, String[] args)throws Exception
	{
		String POLICY_EC_PART =PropertyUtil.getSchemaProperty(context, "policy_ECPart");
	    String TYPE_VPM_REFERENCE =PropertyUtil.getSchemaProperty(context, "type_VPMReference");
		String REL_PART_SPECIFICATION =PropertyUtil.getSchemaProperty(context, "relationship_PartSpecification");
		String strObjId = DomainConstants.EMPTY_STRING;
		StringList slPartLists = new StringList();
		String strPolicy = null;
		String strPartId = null;
		DomainObject doPart = null;
		String objectWhere = null;
		StringList objectSelects = new StringList(2);
		objectSelects.add(DomainObject.SELECT_ID);
		
		try
		{
			if (args == null || args.length < 1)
			{
				throw (new IllegalArgumentException());
			}
			 strObjId = args[0];
			// objectWhere = "interface == '"+INTERFACE_CHANGE_CONTROL+"' && current == "+VPM_STATE_RELEASED;
			 objectWhere = "interface == '"+INTERFACE_CHANGE_CONTROL+"'";
			
			 if(UIUtil.isNotNullAndNotEmpty(strObjId)){
				 XORGERPIntegration_mxJPO integrationjpo = new XORGERPIntegration_mxJPO(context,args);
				 slPartLists = integrationjpo.getPartIdsFromCA(context, strObjId);
				 for(int iSpec=0;iSpec<slPartLists.size();iSpec++){
					  strPartId = (String)slPartLists.get(iSpec);
					  strPolicy = DomainObject.newInstance(context,strPartId).getInfo(context, DomainObject.SELECT_POLICY);
					  if(strPolicy.equalsIgnoreCase(POLICY_EC_PART)){
						  doPart =  DomainObject.newInstance(context, strPartId);
						  MapList vpmreferenceList = doPart.getRelatedObjects(context, 
								                                              REL_PART_SPECIFICATION, 
                                                                              TYPE_VPM_REFERENCE, 
					                                                          objectSelects,
					                                                          null, 
					                                                          false, 
					                                                          true, 
					                                                          (short) 1, 
					                                                          objectWhere, 
					                                                          null, 
					                                                          (short) 0);
						  
						  
						  if(!vpmreferenceList.isEmpty()){
							 for(int iMap = 0;iMap<vpmreferenceList.size();iMap++){
								 Map vpmMap = (Map)vpmreferenceList.get(iMap);
								 String strVPMId = (String)vpmMap.get(DomainObject.SELECT_ID);
								 String strCommand = "mod bus $1 remove interface $2";
									try{
										//ContextUtil.pushContext(context);
										MqlUtil.mqlCommand(context,strCommand,strVPMId,INTERFACE_CHANGE_CONTROL);
									 } catch(Exception exs){
										 exs.printStackTrace();
									 } finally{
										// ContextUtil.popContext(context);
									 }
							  }
						   }
					   }
					  
				  } 
			  }
			
		 }
		catch(Exception Ex)
		{
			Ex.printStackTrace();
			throw Ex;
		}
	}
	
	public String getNextRevision(Context context, DomainObject doObj)
           throws Exception {
		
       String strObjectId = "";
	   try {
		   BusinessObject busObj = (BusinessObject) doObj
				   .getNextRevision(context);
		   if (busObj != null) {
			   strObjectId = busObj.getObjectId(context);
		   }
	   } catch (Exception e) {
		   strObjectId="";
	   }
       return strObjectId;
   }
   public StringList getNextRevisions(Context context,
           DomainObject doObj) throws Exception {
       StringList slOldRevisions = new StringList();
       if (doObj != null) {
           String strObjectId = getNextRevision(context, doObj);
           while (strObjectId != null && !strObjectId.equals("")) {
               slOldRevisions.add(strObjectId);
               strObjectId = getNextRevision(context, new DomainObject(
                       strObjectId));
           }
       }
       return slOldRevisions;
   }
	
	public void propagateValueToLaterRevisions(Context context, String[] args)throws Exception
	{
		String strObjId = DomainConstants.EMPTY_STRING;
		String strAttrValue = DomainConstants.EMPTY_STRING;
		String strAttrName = DomainConstants.EMPTY_STRING;
		String objectWhere = null;
		try
		{
			DomainObject dObj = null;
			if (args == null || args.length < 3)
			{
				throw (new IllegalArgumentException());
			}
			strObjId = args[0];
			strAttrValue = args[1];
			strAttrName = args[2];
			DomainObject domObj =  DomainObject.newInstance(context, strObjId);
			StringList slIds = getNextRevisions(context,domObj);
			for(int i=0; i<slIds.size();  i++) {
				String sId 			= (String) slIds.get(i);
				dObj	= new DomainObject(sId); 
				String strMqlCommand = "trigger off";
				MqlUtil.mqlCommand(context,strMqlCommand,true);
				dObj.setAttributeValue(context, strAttrName, strAttrValue);
				strMqlCommand = "trigger on";
				MqlUtil.mqlCommand(context,strMqlCommand,true);
			}
		 }
		catch(Exception Ex)
		{
			Ex.printStackTrace();
			throw Ex;
		}
	}
	//Added for 70 issue by Preethi Rajaraman -- Starts
	public MapList importBOMStructure(Context context, String[] args) throws Exception {
			googCustomFunctions_mxJPO Custom = new googCustomFunctions_mxJPO();
			MapList resultList = Custom.importBOMStructure(context,args);
			return resultList;
		}
	//Added for 70 issue by Preethi Rajaraman -- Ends
	
	public Hashtable getMappedTypeAndPolicy(Context context, String[] args) throws Exception
	{
		String TYPE_PART = PropertyUtil.getSchemaProperty(context,
                          "type_Part");
		String POLICY_ECPART = PropertyUtil.getSchemaProperty("policy_ECPart");
		Hashtable hmpTMPObj = new Hashtable();
		Hashtable argTable = JPO.unpackArgs(args);
        String rootObjectId = (String)
        argTable.get("OBJECT_ID");
		hmpTMPObj.put("TYPE_NAME",TYPE_PART);
		hmpTMPObj.put("POLICY_NAME",POLICY_ECPART);
		hmpTMPObj.put("RELEASE_PHASE","Production");
		return hmpTMPObj;
	}
   
}
