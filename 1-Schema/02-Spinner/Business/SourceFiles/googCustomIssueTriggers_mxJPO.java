import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.util.StringList;

import com.matrixone.apps.common.Issue;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.engineering.EngineeringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
//Added By XPLORIA to Fix CollabSpace Issue Starts here
import com.matrixone.apps.domain.util.PersonUtil;
//Added By XPLORIA to Fix CollabSpace Issue Ends here
// ISsue Moduie
//ISsue Moduie

public class googCustomIssueTriggers_mxJPO extends googConstants_mxJPO {
	/*
	 * Default Constructor
	 */
	// Sharad added
	public static final String RANGE_PROMOTE_CONNECTED_OBJECT = "Promote Connected Object";
	
	public googCustomIssueTriggers_mxJPO(Context context, String[] args)
			throws Exception {
		
	}
	
	public googCustomIssueTriggers_mxJPO()throws Exception {
		
	}

	/*
	 * Default main method launched if JPO launched without a specified method
	 * ...
	 */
	public int mxMain(Context context, String[] args) throws Exception {
		return 0;
	}

	// [Google Custom]: Related to Issue Module - Modified by Sara on 19/12/2017
	// - Start
	/**
	 * To check the whether Part is connected to CA or not
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return int
	 * @throws Exception if the operation fails.
	 */
	public int CheckForChangeAction(Context context, String args[])
			throws Exception {
		String sObjectId = args[0];
		String langStr = context.getSession().getLanguage();
		String strMessage = null;
		StringList sPartNameList = new StringList();
		if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
			MapList sPartList = getRelatedParts(context, sObjectId);
			if (sPartList != null && sPartList.size() > 0) {
				Iterator itr = sPartList.iterator();
				while (itr.hasNext()) {
					Map childMap = (Map) itr.next();
					String sId = (String) childMap.get(SELECT_ID);
					String sPartName = (String) childMap.get(SELECT_NAME);
					MapList sConnectedChangeActionList = new googCustomFunctions_mxJPO().getRelatedCA(context, sId);

					if (sConnectedChangeActionList != null
							&& sConnectedChangeActionList.size() > 0) {
						// Do nothing
					} else {
						sPartNameList.add(sPartName);
					}
				}

				if (sPartNameList != null && sPartNameList.size() > 0) {
					strMessage = EngineeringUtil.i18nStringNow(context,
									"emxEngineeringCentral.IssueAssignPromote.ErrorMessage",
									langStr);
					String sMsg = strMessage + sPartNameList;
					emxContextUtil_mxJPO.mqlNotice(context, sMsg);
				}
			}
		}
		return 0;
	}

	/**
	 * To fetch the connected Part list from Issue context
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return MapList
	 * @throws Exception if the operation fails.
	 */

	private MapList getRelatedParts(Context context, String sObjectId)
			throws Exception {
		MapList sPartList = new MapList();
		StringList objectSelects = new StringList();
		objectSelects.addElement(SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		DomainObject doj = new DomainObject(sObjectId);
		try {
			sPartList = doj.getRelatedObjects(context, REL_ISSUE, // relationship pattern
					TYPE_PART, // object pattern
					objectSelects, // object selects
					null, // relationship selects
					false, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					null, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sPartList;
	}

	/**
	 * To connect the Change Action and issue with "Resolved To" Relationship
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return
	 * @throws Exception if the operation fails.
	 */
		public void connectChangeActionWithIssue(Context context, String args[]) throws Exception {
			String sObjectId = args[0];
			DomainObject doj = new DomainObject(sObjectId);
			if (UIUtil.isNotNullAndNotEmpty(sObjectId)){
				MapList sPartList = getRelatedParts(context, sObjectId);
				StringList objectSelects = new StringList();
				objectSelects.addElement(SELECT_ID);
				Iterator itr = sPartList.iterator();
				while(itr.hasNext()){
					Map childMap = (Map) itr.next();
					String sId = (String) childMap.get(SELECT_ID);
					MapList sConnectedChangeActionList = new googCustomFunctions_mxJPO().getRelatedCA(context, sId);
					if (sConnectedChangeActionList!=null && sConnectedChangeActionList.size()>0){
						Iterator itr1 = sConnectedChangeActionList.iterator();
						while(itr1.hasNext()){
							Map childMap1 = (Map) itr1.next();
							String sChangeId = (String) childMap1.get(SELECT_ID);
							String sChangeState = (String) childMap1.get(SELECT_CURRENT);
							if(sChangeState.equals(STATE_CHANGEACTION_PREPARE) || sChangeState.equals(STATE_CHANGEACTION_INWORK)){
							DomainObject dObj = new DomainObject(sChangeId);
							MapList sIssueList = new MapList();
							try {
								sIssueList = dObj.getRelatedObjects(context,
																	RELATIONSHIP_RESOLVED_TO, // relationship pattern
																	TYPE_ISSUE, // object pattern
																	objectSelects, // object selects
																	null, // relationship selects
																	true, // to direction
																	false, // from direction
																	(short) 1, // recursion level
																	null, // object where clause
																	null, 
																	0);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						// If CA is not connected to Issue already, then connect
						if(!isObjectAvailable(sIssueList,sObjectId)){
							DomainRelationship.connect(context, doj , RELATIONSHIP_RESOLVED_TO, DomainObject.newInstance(context, sChangeId));
						}
						}
						}
					}
					}
			}
		}

	/**
	 * To check whether the CA is already connected to Issue
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return boolean
	 * @throws Exception if the operation fails.
	 */
	public boolean isObjectAvailable(MapList sIssueList, String sObjectId) {
		boolean isAvailable = false;
		Iterator issueItr = sIssueList.iterator();
		while (issueItr.hasNext()) {
			Map issueMap = (Map) issueItr.next();
			String sIssueId = (String) issueMap.get(SELECT_ID);
			if (sIssueId.equals(sObjectId)) {
				isAvailable = true;
				break;
			}
		}
		return isAvailable;
	}

	// [Google Custom]: Related to Issue Module - Modified by Sara on 19/12/2017 - End

	// [Google Custom]: Related to Issue Module - Modified by Shajil on 22/12/2017 - Start
	/**
	 * This method used to create and Connect Route Template in Issue
	 * @param context
	 * @param arg1
	 * @return int
	 * @throws Exception
	 */
public int createRouteForIssue(Context context, String[] arg1)
			throws Exception {

		DomainObject issueObj = new DomainObject(arg1[0]);
		String tempState = arg1[1];
		//TODO : Added by Sharad - Need to be removed 
		String sUser = arg1[2];
		
		StringList relSelect = new StringList(SELECT_RELATIONSHIP_ID);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_STATE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_POLICY);
		String relWhere = SELECT_ATTRIBUTE_ROUTE_BASE_STATE + "==" + tempState;
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.add("latest");
		googCustomTriggers_mxJPO customTrigger = new googCustomTriggers_mxJPO(
				context, arg1);
		try {
			MapList routeTemplateData = issueObj.getRelatedObjects(context,
					RELATIONSHIP_OBJECT_ROUTE, TYPE_ROUTE_TEMPLATE, busSelect,
					relSelect, false, true, (short) 0, "current == Active",
					relWhere, 0);
			
			MapList routeData = issueObj.getRelatedObjects(context,
					RELATIONSHIP_OBJECT_ROUTE, TYPE_ROUTE, busSelect,
					relSelect, false, true, (short) 0, null,
					relWhere, 0);
			Iterator tempListItr = routeTemplateData.iterator();

			while (tempListItr.hasNext()) {
				Map tempInfo = (Map) tempListItr.next();
				String latest = (String) tempInfo.get("latest");
				if (latest.equalsIgnoreCase("TRUE")) {
					//tempInfo.put(ATTRIBUTE_ROUTE_COMPLETION_ACTION,"Notify Route Owner");
					tempInfo.put(ATTRIBUTE_ROUTE_COMPLETION_ACTION,
							RANGE_PROMOTE_CONNECTED_OBJECT);
					if(routeData.size()<1){
						boolean bctxpushed = false;
						//Check for the Context User - Sharad - Starts
						try{
								if(context.getUser().toString().equalsIgnoreCase("User Agent"))
								{
									bctxpushed = true;
									ContextUtil.pushContext(context, issueObj.getOwner(context).toString(),DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
								}
							//Check for the Context User - Sharad - Ends
							customTrigger.createAndStartRoute(context, issueObj, tempInfo);
						} finally {
							if(bctxpushed){
								ContextUtil.popContext(context);
							}
							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

	// [Google Custom]: Related to Issue Module - Modified by Shajil on 22/12/2017 - End

	/**
	 * This method used to validate the Actual start date validation in Issue
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author Shajil
	 */
	public int checkActualStartDate(Context context, String[] args)
			throws Exception {
		int iFlag = 0;

		try {
			String strObjectId = args[0];
			Locale strLocale = context.getLocale();
			DomainObject issueObj = new DomainObject(strObjectId);
			String strAttribActualStartDate = PropertyUtil.getSchemaProperty(context, Issue.SYMBOLIC_attribute_ActualStartDate);
			String strEstimatedStartDate = issueObj.getAttributeValue(context,strAttribActualStartDate);

			if (UIUtil.isNullOrEmpty(strEstimatedStartDate)) {
				String strDateError = EnoviaResourceBundle.getProperty(context,RESOURCE_BUNDLE_COMPONENTS_STR, strLocale, ACTUAL_START_DATE_ERROR);
				emxContextUtil_mxJPO.mqlNotice(context, strDateError);
				iFlag = 1;
			}

		} catch (Exception ex) {
			throw new FrameworkException((String) ex.getMessage());
		}
		return iFlag;
	}

	/**
	 * This method is used to send notification to DRE on Issue promotion
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	public int notifyRDE(Context context, String[] args) throws Exception {

		try {
			String strObjectId = args[0];
			StringList objList = new StringList();
			objList.addElement(SELECT_CURRENT);
			objList.addElement(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);
			Map deviationInfo = DomainObject.newInstance(context,
					strObjectId).getInfo(context,objList);
			String resposibleDesignEngg = (String)deviationInfo.get(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);
			StringList toList = new StringList(resposibleDesignEngg);
			StringList ccList = new StringList();
			StringList bccList = new StringList();
			StringList objectList = new StringList(strObjectId);
			DomainObject deviationObj = new DomainObject(strObjectId);
			String current = (String)deviationInfo.get(SELECT_CURRENT);
			String displayStateName = EnoviaResourceBundle.getProperty(context,
					RESOURCE_BUNDLE_FRAMEWORK_STR, context.getLocale(),
					"emxFramework.State.Issue." + current);
			String subject = EnoviaResourceBundle.getProperty(context,
					RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
					MESSAGE_STATUS_SUBJECT);
			String message = EnoviaResourceBundle.getProperty(context,
					RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
					MESSAGE_STATUS_BODY);
			message = message.replace("${CURRENT}", displayStateName);
			// Expanding the macros used in the message subject and body
			subject = Issue.replaceMacroWithValues(context, strObjectId,
					subject);
			message = Issue.replaceMacroWithValues(context, strObjectId,
					message);
			// Sending mail to the originator
			MailUtil.sendMessage(context, toList, ccList, bccList, subject,
					message, objectList);

		} catch (Exception ex) {
			throw new FrameworkException((String) ex.getMessage());
		}
		return 0;
	}

	/**
	 * This method is used to validate all mandatory field validation.
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	public int validateMandatoryFields(Context context, String[] args)
			throws Exception {

		try {
			String strObjectId = args[0];
			String strObjectType = args[1];
			String symbolicTypeName = FrameworkUtil.getAliasForAdmin(context,
					DomainConstants.SELECT_TYPE, strObjectType, true);
			String mandatoryAttrList = "emxComponents." + symbolicTypeName
					+ ".Attribute.MandatoryNames";
			googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
			Map<String, String> pageInfo = customIssue.getPageInfo(context, "googIssueMapping");
			String mandatoryAttrNames = pageInfo.get(mandatoryAttrList);
			Map attributeDetails = getSelectedList(context, mandatoryAttrNames);
			StringList objectList = (StringList) attributeDetails
					.get("attributeSelect");
			Map objectInfo = DomainObject.newInstance(context, strObjectId)
					.getInfo(context, objectList);
			Map<String, String> attributeMap = (Map) attributeDetails.get("attributeMap");
			Map<String, String> attributeDisplayMap = (Map) attributeDetails.get("attributeDisplayMap");
			StringBuilder errorAttrList = new StringBuilder();
			for (int i = 0; i < objectList.size(); i++) {
				String attrValue = (String) objectInfo.get(objectList.get(i));
				if (UIUtil.isNullOrEmpty(attrValue)
						|| attrValue.equalsIgnoreCase("Unassigned")) {
					errorAttrList.append(attributeDisplayMap.get(objectList.get(i)))
							.append(",");
				}
			}
			if (UIUtil.isNotNullAndNotEmpty(errorAttrList.toString())) {
				String errorMessage = errorAttrList.substring(0,
						errorAttrList.length() - 1);
				emxContextUtil_mxJPO.mqlNotice(context, "Mandatory fields("
						+ errorMessage + ") are not filled in.");
				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 1;

		}

		return 0;
	}


	/**
	 * This method used to create the selectable list for attribute mandatory validation
	 * @param context
	 * @param attrNames
	 * @return Map
	 * @author shajil
	 */
	public Map<String, Object> getSelectedList(Context context, String attrNames) {
		StringList objectSelects = new StringList();
		String[] attributeNames = attrNames.split(",");
		Map<String, String> attributeMap = new HashMap<String, String>();
		Map<String, String> attributeDisplayMap = new HashMap<String, String>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String attrSymbolicName : attributeNames) {
			if (!isBasic(context, attrSymbolicName)) {
				String attributeDispName = "emxComponents.type_Issue.MandatoryAttribute."+attrSymbolicName;
				String mandatoryAttrNames = EnoviaResourceBundle.getProperty(
						context, RESOURCE_BUNDLE_COMPONENTS_STR,
						context.getLocale(), attributeDispName);
				String attrName = PropertyUtil.getSchemaProperty(context,attrSymbolicName);
				StringBuilder selectStatement = new StringBuilder("attribute[");
				selectStatement.append(attrName).append("]");
				attributeMap.put(selectStatement.toString(), attrName);
				objectSelects.addElement(selectStatement.toString());
				if(!attributeDispName.equals(mandatoryAttrNames)){
					attributeDisplayMap.put(selectStatement.toString(), mandatoryAttrNames);
				}else{
					attributeDisplayMap.put(selectStatement.toString(), attrName);
				}
			} else {
				attributeDisplayMap.put(attrSymbolicName, attrSymbolicName);
				attributeMap.put(attrSymbolicName, attrSymbolicName);
				objectSelects.addElement(attrSymbolicName);
			}
		}
		resultMap.put("attributeSelect", objectSelects);
		resultMap.put("attributeMap", attributeMap);
		resultMap.put("attributeDisplayMap", attributeDisplayMap);
		return resultMap;
	}

	/**
	 * This method is used to validate whether field is basic or not
	 * @param context
	 * @param attrSymbolicName
	 * @return boolean
	 * @author shajil
	 */
	public boolean isBasic(Context context, String attrSymbolicName) {
		String basicFields = EnoviaResourceBundle.getProperty(context,
				RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
				BASIC_FIELD_NAME);
		String[] basicFielddName = basicFields.split(",");
		for (String basicField : basicFielddName) {
			if (basicField.equalsIgnoreCase(attrSymbolicName)) {
				return true;
			}
		}
		return false;
	}

	// [Google Custom]: Related to Issue Module - Modified by Sara on 27/12/2017 - Start
	/**
	 * To check the whether Route Template is connected to Deviation or not
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return int
	 * @throws Exception if the operation fails.
	 */
	public int checkRouteTemplateForDeviation(Context context, String[] args)
			throws Exception {
		String sObjectId = args[0];
		DomainObject issueObj = new DomainObject(sObjectId);
		String tempState = args[1];
		String langStr = context.getSession().getLanguage();
		String strMessage = "";
		StringList relSelect = new StringList(SELECT_RELATIONSHIP_ID);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_STATE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_POLICY);
		String relWhere = SELECT_ATTRIBUTE_ROUTE_BASE_STATE + "==" + tempState;
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.add("latest");
		MapList routeTemplateData = new MapList();
		try {
			routeTemplateData = issueObj.getRelatedObjects(context,
					RELATIONSHIP_OBJECT_ROUTE, TYPE_ROUTE_TEMPLATE, busSelect,
					relSelect, false, true, (short) 0, "current == Active",
					relWhere, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (tempState.equals(ISSUE_ASSIGN_STATE)) {
			strMessage = EngineeringUtil
					.i18nStringNow(
							context,
							"emxEngineeringCentral.DeviationCreatePromote.ErrorMessage",
							langStr);
		}

		// [Google Custom]: "Issue Module - Removing Implementation Field" - Modified by Sara on 09/01/2018 - Start
		if (tempState.equals(ISSUE_ACTIVE_STATE)) {
			//strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.DeviationAssignPromote.ErrorMessage",langStr);
			strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.DeviationApprovedPromote.ErrorMessage",langStr);
		}

	/*  if (tempState.equals(ISSUE_REVIEW_STATE)) {
			strMessage = EngineeringUtil
					.i18nStringNow(
							context,
							"emxEngineeringCentral.DeviationApprovedPromote.ErrorMessage",
							langStr);
		}
    */
	   // [Google Custom]: "Issue Module - Removing Implementation Field" - Modified by Sara on 09/01/2018 - End
	   
	   
		if (routeTemplateData.size() < 1) {
			emxContextUtil_mxJPO.mqlNotice(context, strMessage);
			return 1;
		}
		return 0;
	}

	/**
	 * This method is used to update Issue attribute values to CA,
	 * This method will call from connection trigger.
	 * @param context
	 * @param args
	 * @return int
	 * @author shajil
	 */
	public int updateIssueValueToCA(Context context, String[] args)
			throws Exception {
		String changeActionId = args[0];
		String issueId = args[1];
		String fromType = args[4];
		String toType = args[5];
		String symbolicTypeName = FrameworkUtil.getAliasForAdmin(context,
				DomainConstants.SELECT_TYPE, fromType, true);
		String mandatoryAttrList = "emxComponents." + symbolicTypeName
				+ ".Attribute.Copy";
		String mandatoryAttrNames = EnoviaResourceBundle.getProperty(context,
				RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
				mandatoryAttrList);
		if (fromType.equals(TYPE_ISSUE) && toType.equals(TYPE_CHANGEACTION)) {
			Map attributeDetails = getSelectedList(context, mandatoryAttrNames);
			StringList objectList = (StringList) attributeDetails
					.get("attributeSelect");
			Map<String, String> attributeMap = (Map) attributeDetails
					.get("attributeMap");
			try {
				DomainObject changeObj = new DomainObject(changeActionId);
				Map changeActionInfo = changeObj.getInfo(context, objectList);
				DomainObject deviationObject = DomainObject.newInstance(context, issueId);
				Map issueInfo = deviationObject.getInfo(context, objectList);
				Map<String, String> attrValueDetais = new HashMap<String, String>();
				for (int i = 0; i < objectList.size(); i++) {
					String attrValue = (String) changeActionInfo.get(objectList
							.get(i));
					if (UIUtil.isNullOrEmpty(attrValue)
							|| attrValue.equalsIgnoreCase("Unassigned")
							|| attrValue.equals("0.0")) {
						String attrName = attributeMap.get(objectList.get(i));
						String attrIssueValue = (String) issueInfo
								.get(objectList.get(i));
						attrValueDetais.put(attrName, attrIssueValue);
					}
				}
				if (attrValueDetais.size() > 0) {
					changeObj.setAttributeValues(context, attrValueDetais);
				}
				StringList changeSelect = new StringList(SELECT_ID);
				changeSelect.addElement(SELECT_NAME);
				changeSelect.addElement(SELECT_ATTRIBUTE_PCN_NUMBER);
				Map changeObjInfo = changeObj.getInfo(context,changeSelect );
				String pcnValue = (String)changeObjInfo.get(SELECT_ATTRIBUTE_PCN_NUMBER);
				String deviationObjName = deviationObject.getName(context);
				StringBuilder pcnValues = null;
				if(UIUtil.isNotNullAndNotEmpty(pcnValue) && (!pcnValue.contains(deviationObjName))){
					pcnValues = new StringBuilder(pcnValue);
					pcnValues.append(",").append(deviationObjName);
				}if(UIUtil.isNotNullAndNotEmpty(pcnValue) && (pcnValue.contains(deviationObjName))){
					pcnValues = new StringBuilder(pcnValue);	
				}else if(UIUtil.isNullOrEmpty(pcnValue)){
					pcnValues = new StringBuilder(deviationObjName);
				}
				changeObj.setAttributeValue(context, ATTRIBUTE_PCN_NUMBER, pcnValues.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return 1;
			}
		}
		return 0;
	}
	// [Google Custom]: Related to Issue Module - Modified by Sara on 27/12/2017 - End
	
	// [Google Custom]: "Issue Module - Removing Implementation Field" - Created by Sara on 09/01/2018 - Start
	/**
	 * To double promote the Deviation from Approved state to closed state
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return
	 * @throws Exception if the operation fails.
	 */
	public void promoteToClosedState(Context context, String args[]) throws Exception {
		String sObjectId = args[0];
		try{
			DomainObject doj = new DomainObject(sObjectId);
			doj.setState(context, STATE_ISSUE_CLOSE);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method used to handle the Route, while performing Deviation demote action.
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	public int updateRouteTask(Context context, String[] args)
			throws Exception {
		String sObjectId = args[0];
		String routeBaseState = args[1];
		DomainObject issueObj = new DomainObject(sObjectId);
		StringList relSelect = new StringList(SELECT_RELATIONSHIP_ID);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_STATE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE);
		relSelect.addElement(SELECT_ATTRIBUTE_ROUTE_BASE_POLICY);
		String relWhere = SELECT_ATTRIBUTE_ROUTE_BASE_STATE + "==" + routeBaseState;
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.addElement(SELECT_NAME);
		try {
			MapList routeData = issueObj.getRelatedObjects(context,
					RELATIONSHIP_OBJECT_ROUTE, TYPE_ROUTE, busSelect,
					relSelect, false, true, (short) 0, null,
					relWhere, 0);
			Iterator tempListItr = routeData.iterator();
			String objectIds[] = null;
			while (tempListItr.hasNext()) {
				Map routeInfo = (Map) tempListItr.next();
				String routeId = (String)routeInfo.get(SELECT_ID);
				DomainObject routeObject = DomainObject.newInstance(context, routeId);
				MapList inboxTaskData = routeObject.getRelatedObjects(context,
						RELATIONSHIP_ROUTE_TASK, TYPE_INBOX_TASK, busSelect,
						relSelect,true , false, (short) 0, null,
						null, 0);
				Iterator taskItr = inboxTaskData.iterator();
				objectIds = new String[inboxTaskData.size()];
				if(inboxTaskData.size()>0){
					for(int i=0;i<inboxTaskData.size();i++){
						
						Map taskInfo = (Map)inboxTaskData.get(i);
						String taskId = (String)taskInfo.get(SELECT_ID);
						objectIds[i] = taskId;
					}
				}
				DomainObject.deleteObjects(context, objectIds);
				routeObject.deleteObject(context);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
	/**
	 * This method used for Deviation cancel functionality.
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	public int cancelDeviation(Context context, String[] args) throws Exception {
		String deviationId = args[0];
		emxCommonIssue_mxJPO issue = new emxCommonIssue_mxJPO(context, args);
		int result = issue.disconnectPartAndCA(context,deviationId);
		return result;
		
	}
	
	/**
	 * This method is used to update PCN number.
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	public int updatePCNNumber(Context context, String[] args) throws Exception {
		emxCommonIssue_mxJPO issue = new emxCommonIssue_mxJPO(context, args);
		String changeObjId = args[0];
		String deviationObjId = args[1];
		int result = issue.updatePCNNumber(context,deviationObjId,changeObjId);
		return result;
	}
	// [Google Custom]: "Issue Module - Removing Implementation Field" - Created by Sara on 09/01/2018 - End
	
	
	
	// [Google Custom]: "Deviation Module - ClosedBy Values" - Created by Sara on 07/02/2018 - Start
	/**
	 * Trigger to alert the user that the deviation is not connected to any "Closed By" CA
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Sara
	 */
	public int CheckClosedByValues(Context context, String args[]) throws Exception {
		String sObjectId = args[0];
		String langStr = context.getSession().getLanguage();
		String strMessage = null;
		StringList objectSelects = new StringList();
		objectSelects.addElement(SELECT_ID);
		
		if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
			DomainObject doj = new DomainObject(sObjectId);
			MapList sCAList = doj.getRelatedObjects(context,
					RELATIONSHIP_RESOLVED_TO, // relationship pattern
					TYPE_CHANGEACTION, // object pattern
					objectSelects, // object selects
					null, // relationship selects
					false, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					null, 
					0);

			if (sCAList.size()<=0){
				strMessage = EngineeringUtil.i18nStringNow(context,"emxEngineeringCentral.IssueAssignPromote.ClosedBy.ErrorMessage",langStr);
				emxContextUtil_mxJPO.mqlNotice(context, strMessage);
			}
		}
		return 0;
	}
	// [Google Custom]: "Deviation Module - ClosedBy Values" - Created by Sara on 07/02/2018 - End
	
	//[Google Custom] Send mail Notification to Followers group on Issue promotion:  by Syed on 15-05-2018 - Starts
	/**
	 * This method is used to send notification to Devation group -Followers List on Issue promotion
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author syed
	 */
	public int notifyDeviationFollowersList(Context context, String[] args) throws Exception {

		try {
			String deviationId = args[0];
			StringList objList = new StringList();
			StringList groupList = new StringList();
			StringList followerPersonList = new StringList();
			objList.addElement(SELECT_CURRENT);
			objList.addElement(SELECT_ATTR_FOLLOWER_LIST);
			Map deviationInfo = DomainObject.newInstance(context, deviationId).getInfo(context, objList);
			String followerPersons = (String) deviationInfo.get(SELECT_ATTR_FOLLOWER_LIST);
			googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
			Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomMailDetails");
			String followerGroup = pageInfo.get("Deviation.Group.FollowerList");
			if (UIUtil.isNotNullAndNotEmpty(followerGroup)) {
				String personList = MqlUtil.mqlCommand(context, "print group '$1' select $2 dump", followerGroup,
						"person");
				groupList = FrameworkUtil.split(personList, ",");
			}
			if (UIUtil.isNotNullAndNotEmpty(followerPersons)) {
				//Modified by Sourav to unblock the Deviation Promotion Issue Starts
				followerPersonList = FrameworkUtil.split(followerPersons, "|");
				//Modified by Sourav to unblock the Deviation Promotion Issue Ends
			}
			StringList toList = new StringList();
			toList.addAll(groupList);
			toList.addAll(followerPersonList);
			Set set = new LinkedHashSet();
			set.addAll(toList);
			toList.clear();
			toList.addAll(set);
			StringList ccList = new StringList();
			StringList bccList = new StringList();
			StringList objectList = new StringList(deviationId);
			String current = (String) deviationInfo.get(SELECT_CURRENT);
			String displayStateName = EnoviaResourceBundle.getProperty(context, RESOURCE_BUNDLE_FRAMEWORK_STR,
					context.getLocale(), "emxFramework.State.Issue." + current);
			String subject = EnoviaResourceBundle.getProperty(context, RESOURCE_BUNDLE_COMPONENTS_STR,
					context.getLocale(), MESSAGE_STATUS_SUBJECT);
			String message = EnoviaResourceBundle.getProperty(context, RESOURCE_BUNDLE_COMPONENTS_STR,
					context.getLocale(), MESSAGE_STATUS_BODY);
			message = message.replace("${CURRENT}", displayStateName);
			// Expanding the macros used in the message subject and body
			subject = Issue.replaceMacroWithValues(context, deviationId, subject);
			message = Issue.replaceMacroWithValues(context, deviationId, message);
			// Sending mail to the originator
			MailUtil.sendMessage(context, toList, ccList, bccList, subject, message, objectList);

		} catch (Exception ex) {
			throw new FrameworkException((String) ex.getMessage());
		}
		return 0;
	}
	//[Google Custom] Send mail Notification to Followers group on Issue promotion:  by Syed on 15-05-2018 - Ends
	//[Google Custom] trigger by Preethi Rajaraman - Starts
	/**
	 * To check the whether End Item Value is according to ModuleEnditem
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return int
	 * @throws Exception if the operation fails.
	 */
	public int ModifygoogModuleEnditemChecktrigger(Context context, String args[]) throws Exception {
		StringList objList 				= new StringList();
		objList.addElement("attribute[End Item].value");
		objList.addElement("attribute[googModuleEndItem].value");
		objList.addElement("name");
		String strMessage				= "";
		try {
			String PartId 				= args[0];
			String langStr 				= context.getSession().getLanguage();
			String sModuleEndItemNewVal	= args[2];
			DomainObject domObj 		= new DomainObject(PartId);
			Map PartInfo 				= domObj.getInfo(context, objList);
			String attrValue 			= (String) PartInfo.get("attribute[End Item].value");
			String googModuleValue 		= (String) PartInfo.get("attribute[googModuleEndItem].value");
			String sPartName			= (String) PartInfo.get("name");
			/* Commented by Kalim
			if (googModuleValue.equals("Yes") && attrValue.equals("Yes")) {
				strMessage 		= "WPN "+sPartName+ " End Item and Module End item are yes hence Module End Item can't set to No";
				${CLASS:emxContextUtil}.mqlNotice(context, strMessage);
				return 1;
			} */
			if (UIUtil.isNotNullAndNotEmpty(sModuleEndItemNewVal) && sModuleEndItemNewVal.equals("Yes") && UIUtil.isNotNullAndNotEmpty(attrValue) && attrValue.equals("No") ) {				
				strMessage 		= "WPN "+sPartName+ " cannot set 'Module End item' to Yes as 'End Item' is No.  ";
				emxContextUtil_mxJPO.mqlNotice(context, strMessage);
				return 1;
			} 
			if(attrValue.equalsIgnoreCase("Unassigned") && sModuleEndItemNewVal.equals("Yes")) {
				strMessage 		= "WPN "+sPartName+ " cannot set 'Module End item' to Yes as 'End Item' is Unassigned.  ";
				emxContextUtil_mxJPO.mqlNotice(context, strMessage);
				return 1;
			}
						
		} catch (Exception ex) {
			throw new FrameworkException((String) ex.getMessage());
		}
		
		return 0;
	}
	
	/**
	 * To check the whether ModuleEnditem Value is according to End Item
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed arguments
	 * @return int
	 * @throws Exception if the operation fails.
	 */
	public int ModifyEnditemChecktrigger(Context context, String args[]) throws Exception {
		StringList objList 				= new StringList();
		String strMessage				= "";
		objList.addElement("attribute[googModuleEndItem].value");
		objList.addElement("attribute[End Item].value");
		objList.addElement("name");
		try {
			String PartId 				= args[0];
			String langStr 				= context.getSession().getLanguage();
			String PartEndItemNewValue 	= args[2];
			DomainObject domObj 		= new DomainObject(PartId);
			Map PartInfo 				= domObj.getInfo(context, objList);
			String attrValue 			= (String) PartInfo.get("attribute[googModuleEndItem].value");
			String sPartName			= (String) PartInfo.get("name");
			String EnditemValue 		= (String) PartInfo.get("attribute[End Item].value");
			/* Commented by Kalim
			if (EnditemValue.equals("No") && attrValue.equals("No")) {
				strMessage 		= "WPN "+sPartName+ " End Item and Module End item are No hence End Item can't set to Yes";
				${CLASS:emxContextUtil}.mqlNotice(context, strMessage);
				return 1;
			}*/
			if (UIUtil.isNotNullAndNotEmpty(PartEndItemNewValue) && PartEndItemNewValue.equals("No") && UIUtil.isNotNullAndNotEmpty(attrValue) && attrValue.equals("Yes") ) {
				strMessage 		= "WPN "+sPartName+ " cannot set 'End item' to No as 'Module End Item' is Yes.";
				emxContextUtil_mxJPO.mqlNotice(context, strMessage);
				return 1;
			} 
			if (UIUtil.isNotNullAndNotEmpty(PartEndItemNewValue) && PartEndItemNewValue.equals("Unassigned") && UIUtil.isNotNullAndNotEmpty(attrValue) && attrValue.equals("Yes") ) {
				strMessage 		= "WPN "+sPartName+ " cannot set 'End item' to Unassigned as 'Module End Item' is Yes.";
				emxContextUtil_mxJPO.mqlNotice(context, strMessage);
				return 1;
			}
			
		} catch (Exception ex) {
			throw new FrameworkException((String) ex.getMessage());
		}
		
		return 0;
	}
	//[Google Custom] trigger by Preethi Rajaraman - Ends
	
	/**
	 * This method is used to cehck if the context user has PLM Analyst , if yes then allow the CATIA Structure to promote even if the Catia Structure Collab Space is different. If not invoke the OOTB Code to validate the existing check
	 * @param context
	 * @param args
	 * @return int
	 * @throws Exception
	 * @author shajil
	 */
	
	public int LoginContextApproveCheck(Context context, String[] args) throws Exception {
		try {
		    String googPLMAnalyst = PropertyUtil.getSchemaProperty(context,"role_googPLMAnalyst");
			boolean hasgoogPLM = PersonUtil.hasAssignment(context, googPLMAnalyst);
			if(hasgoogPLM) {
				return 0;
			} else {
				TeamSecurityServices_mxJPO TeamSecurityServices = new TeamSecurityServices_mxJPO(context,args);
				int iRetuern = TeamSecurityServices.LoginContextApproveCheck(context, args);
				return iRetuern;
			}
		  
		} catch (Exception ex) {
			
			throw new FrameworkException((String) ex.getMessage());
			
		}
	}
	
}// end of class