<%--  goog_createSupplierRoute.jsp  - Shajil : 29/05/19
   Copyright (c) 1992-2012 Dassault Systemes.
   All Rights Reserved.
   This program contains proprietary and trade secret information of Dassault Systemes
   Copyright notice is precautionary only and does not evidence any actual or
   intended publication of such program
--%>
<%@ page import="com.matrixone.servlet.Framework" %>
<%@ page import="com.technia.tvc.core.db.aef.AEFUtils" %>
<%@ page import="com.technia.tvc.core.util.RequestParameters" %>
<%@ page import="com.technia.tvc.core.util.RequestUtils" %>
<%@ page import="matrix.db.Context" %>
<%@ page import="matrix.util.StringList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.technia.tvc.core.db.select.Statement" %>

<%@ page import="com.matrixone.apps.domain.DomainObject"%>
<%@ page import="com.matrixone.apps.framework.ui.UIUtil"%>
<%@ page import="com.matrixone.apps.domain.DomainRelationship"%>
<%@ page import="com.matrixone.apps.domain.util.FrameworkUtil"%>
<%@ page import="matrix.util.StringList"%>
<%@ page import="matrix.util.Pattern"%>
<%@ page import="com.matrixone.apps.domain.util.ContextUtil"%>
<%@ page import="com.matrixone.apps.domain.util.MapList"%>
<%@ page import="com.matrixone.apps.domain.DomainConstants"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.waymo.helium.common.util.ChangeActionUtil"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.matrixone.apps.domain.util.MqlUtil"%>
<%      
	Context context = AEFUtils.getContext(session);
	String sLoggedInUser=(String)context.getUser();
	String documentId = RequestUtils.getParameter(request, RequestParameters.OBJECT_ID_PARAM);
	String caId = RequestUtils.getParameter(request, "contextObjId");
	
	StringList documentIdFormat = FrameworkUtil.split(documentId, ",");
	String errorMesg = null;
	try {
	DomainObject doj = null;
	DomainObject dpartoj = null;
	
	DomainObject dcaoj = null;
	String strModifiedDateVal = "";
	String strCurrentState = "";
	String historyCommand = "modify bus $1 add history $2 comment $3";
	
	
			
		ContextUtil.pushContext(context);


		
		
	for(int i=0;i<documentIdFormat.size();i++){
		String docId = (String)documentIdFormat.get(i);
		
		if (UIUtil.isNotNullAndNotEmpty(docId)){
			doj=DomainObject.newInstance(context,docId);
			String strImplStatusValue = doj.getInfo(context,"attribute[googImplementationStatus].value");
			
			if(UIUtil.isNotNullAndNotEmpty(strImplStatusValue) && "Not Implemented".equalsIgnoreCase(strImplStatusValue)){
				MqlUtil.mqlCommand(context,"history off");
				doj.setAttributeValue(context,"googImplementationStatus", "Implemented");
				MqlUtil.mqlCommand(context,"history on");
				 //strModifiedDateVal = doj.getInfo(context,"modified");
				//strCurrentState = doj.getInfo(context,"current");
				MqlUtil.mqlCommand(context, historyCommand, docId, "modify",
									"Modified by user: " + sLoggedInUser);
			}
		}
	}

		  if(UIUtil.isNotNullAndNotEmpty(caId))
		  {
		 dcaoj =DomainObject.newInstance(context,caId);
		String strCAImplStatusValue = dcaoj.getInfo(context,"attribute[googImplementationStatus].value");
		//strCurrentState = dcaoj.getInfo(context,"current");
		List localList4 = new ArrayList<>();
List<String> affectedItems = new ArrayList<>();
String strPrtPhysId="";
String strPrtId="";
String argsMQL[] = new String[2];
argsMQL[0] = caId;
argsMQL[1] = "from[Proposed Activities].to.paths.path[Where].element[0].physicalid";
String strResult   = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", argsMQL);
String[] proposedAffectedItemIds = strResult.split(",");
affectedItems.addAll(Arrays.asList(proposedAffectedItemIds));


String argsMQL1[] = new String[2];
argsMQL1[0] = caId;
argsMQL1[1] = "from[Realized Activities].to.paths.path[Where].element[0].physicalid";
String strResult2   = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", argsMQL1);
String[] relaizedAffectedItemIds = strResult2.split(",");
affectedItems.addAll(Arrays.asList(relaizedAffectedItemIds));


 Set<String> set1 = new LinkedHashSet<>(); 
  
        // Add the elements to set 
        set1.addAll(affectedItems); 
  
        // Clear the list 
        affectedItems.clear(); 
  
        // add the elements of set 
        // with no duplicates to the list 
        affectedItems.addAll(set1); 
 for (int ij = 0; ij < affectedItems.size(); ij++) {
     strPrtPhysId=(String)affectedItems.get(ij);
	
	String argsMQL2[] = new String[2];
argsMQL2[0] = strPrtPhysId;
argsMQL2[1] = "id";
strPrtId   = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", argsMQL2);
localList4.add(strPrtId);	
} 
		
		
		
          if ((localList4 != null) && (!localList4.isEmpty())) {
			  int totalAffectedItems=localList4.size();
			  int iNotImplementedObjs=0;
			  int iImplementedObjs=0;
			  int iPartialImplementedObjs=0;
              for (int k = 0; k < localList4.size(); k++) {
				  String strAffectedItemId=(String)localList4.get(k);
				  dpartoj=DomainObject.newInstance(context,strAffectedItemId);
				  String strAffImplStatusValue = dpartoj.getInfo(context,"attribute[googImplementationStatus].value");
				  if(UIUtil.isNotNullAndNotEmpty(strAffImplStatusValue) && "Implemented".equalsIgnoreCase(strAffImplStatusValue)){
					 iImplementedObjs++; 
				  } else {
					  iNotImplementedObjs++;
				  }
              }

			  if(iImplementedObjs==totalAffectedItems)
			  {
				  if(UIUtil.isNotNullAndNotEmpty(strCAImplStatusValue) && !"Implemented".equalsIgnoreCase(strCAImplStatusValue)){
					MqlUtil.mqlCommand(context,"history off");
					dcaoj.setAttributeValue(context,"googImplementationStatus", "Implemented");
					MqlUtil.mqlCommand(context,"history on");
					 //strModifiedDateVal = dcaoj.getInfo(context,"modified");
					MqlUtil.mqlCommand(context, historyCommand, caId, "modify",
									"Modified by user: " + sLoggedInUser);
				  
				  }
				  
			  } else {
				 if(iNotImplementedObjs==totalAffectedItems)
			  {
				  if(UIUtil.isNotNullAndNotEmpty(strCAImplStatusValue) && !"Not Implemented".equalsIgnoreCase(strCAImplStatusValue)){
					MqlUtil.mqlCommand(context,"history off");
					dcaoj.setAttributeValue(context,"googImplementationStatus", "Not Implemented");
					MqlUtil.mqlCommand(context,"history on");
					// strModifiedDateVal = dcaoj.getInfo(context,"modified");
					MqlUtil.mqlCommand(context, historyCommand, caId, "modify",
									"Modified by user: " + sLoggedInUser);		
				  
				  }
				  
			  } else {
				  
				   if(UIUtil.isNotNullAndNotEmpty(strCAImplStatusValue) && !"Partially Implemented".equalsIgnoreCase(strCAImplStatusValue)){
						MqlUtil.mqlCommand(context,"history off");
						dcaoj.setAttributeValue(context,"googImplementationStatus", "Partially Implemented");	
						MqlUtil.mqlCommand(context,"history on");
						 //strModifiedDateVal = dcaoj.getInfo(context,"modified");
						MqlUtil.mqlCommand(context, historyCommand, caId, "modify",
									"Modified by user: " + sLoggedInUser);
				  
				  }


			  }				  
				  
				  
			  }
			  
		  }
		  
	}	
	

					
	}catch (Exception e) {
		errorMesg = e.toString();
		e.printStackTrace();
	} finally{
						ContextUtil.popContext(context);
					}

			
	if (errorMesg != null) {
    	session.putValue("error.message", errorMesg);
    }
	
%>
<script language="Javascript">
	top.opener.parent.App.page.refreshWidgetById("changeactiondetails");
	top.opener.parent.App.page.refreshWidgetById("affecteditems");
	window.close();
</script>
