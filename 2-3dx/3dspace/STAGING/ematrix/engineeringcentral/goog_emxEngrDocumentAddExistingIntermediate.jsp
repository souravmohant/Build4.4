<%--  emxEngrDocumentAddExisitngIntermediate.jsp  - 
   Copyright (c) 1992-2018 Dassault Systemes.
   All Rights Reserved.
   This program contains proprietary and trade secret information of Dassault Systemes
   Copyright notice is precautionary only and does not evidence any actual or
   intended publication of such program
--%>
<%@include file = "emxDesignTopInclude.inc"%>
<%@include file = "emxEngrVisiblePageInclude.inc"%>
<%@include file = "eServiceUtil.inc"%>
<%@include file = "../emxUICommonHeaderBeginInclude.inc" %>
<script type="text/javascript" src="../common/scripts/jquery-latest.js"></script>
<%@ page import="com.matrixone.apps.engineering.EngineeringUtil"%>
<%@ page import = "matrix.db.Command" %>
<%@ page import = "matrix.dbutil.SelectSetting" %>

<%!
	public boolean isTypeExists(Context context, String type) throws Exception {
	    /*boolean boolTypeExists = false;
	    if (type != null && !"".equals(type)) {
	        String strType = MqlUtil.mqlCommand(context, "list type $1",PropertyUtil.getSchemaProperty(context,type));
	        if (!"".equals(strType)) {
	        	boolTypeExists = true;	            
	        }
	    }
	    return boolTypeExists;*/
	    
		 if (type == null || "".equals(type)){ 
	    	return false;	 	    
	    }
	    StringList types=FrameworkUtil.split(type, ",");
	    String strType="";
	    
	    for(int i=0;i<types.size();i++) {
	    	strType = MqlUtil.mqlCommand(context, "list type $1",PropertyUtil.getSchemaProperty(context,(String)types.get(i)));
	    	if ("".equals(strType)) {
		        	return false;            
		       }	    		
	    }	
	 return true;		
	}
%>


<%  
String contentURL = "";
boolean boolTypeExists = true;
String sMode = emxGetParameter(request, "mode");
String objectId = emxGetParameter(request, "objectId");
String suiteKey = emxGetParameter(request, "suiteKey");

String specAddExisting = emxGetParameter(request, "SpecificationAddExisting");
String sCommandName = emxGetParameter(request, "CommandName");
String sSettingName = emxGetParameter(request, "sSettingName");


if(sMode != null && sMode.equals("ECRAddExisting")){
String calledMethod = emxGetParameter(request, "calledMethod");
String languageStr = request.getHeader("Accept-Language");
  
    String type        = emxGetParameter(request, "partType");
    String partType = emxGetParameter(request,"PartType");
    if (partType.startsWith("relationship_"))
    {
        type=PropertyUtil.getSchemaProperty(context,partType);
    }

    RelationshipType relType = new RelationshipType(type);

    //String sRevisedRelName     = PropertyUtil.getSchemaProperty(context,"relationship_RequestPartRevision");
    //String sObsoleteRelName    = PropertyUtil.getSchemaProperty(context,"relationship_RequestPartObsolescence");
    //String sRevisedSpecRelName = PropertyUtil.getSchemaProperty(context,"relationship_RequestSpecificationRevision");
    String sSketchType         = PropertyUtil.getSchemaProperty(context,"type_Sketch");
    String sRelType            = PropertyUtil.getSchemaProperty(context,"relationship_ECRSupportingDocument");

    BusinessTypeItr typeItr = new BusinessTypeItr(relType.getToTypes(context));
    StringList typeList = new StringList();
    String sPartType="";
    while (typeItr.next())
    {
        BusinessType busType = typeItr.obj();
        Vault vault          = busType.getVault();
        String typeName      = busType.getName();
        //Modified for IR-080028V6R2012 
        String parent        = FrameworkUtil.getBaseType(context,typeName,vault);

        if(sSketchType.equalsIgnoreCase(parent) && sRelType.equalsIgnoreCase(type))
        {
            sPartType = sSketchType;
        } 

        String name = FrameworkUtil.getAliasForAdmin(context,"type",typeName,true);
        typeList.addElement(name);
    }
    // Dynamically pick up the types defined in the emxEngineeringCentral.properties

    StringList types = JSPUtil.getCentralProperties(application, session, "eServiceEngineeringCentral", "Types");


    types.addElement("type_EBOMMarkup");
    Vector sortedTypes = new Vector();

    for (int i=0; i < types.size(); i++)
    {
        String typeRegName = (String)types.get(i);
        String sTypeName = PropertyUtil.getSchemaProperty(context,typeRegName);
        if (sTypeName != null && sTypeName.length() > 0)
        {
            StringItr strItr = new StringItr(typeList);
            while(strItr.next())
            {
                String newType = strItr.obj();
                if (typeRegName.equalsIgnoreCase(newType))
                {
                    sortedTypes.add(sTypeName);
                    if("".equals(sPartType))
                    {
                        sPartType = sTypeName;
                    }
                }
            }
        }
    }

    StringBuffer arrTypeArrayContent = new StringBuffer();

    int sortedTypeSize = sortedTypes.size();
     
    for (int j=0; j<sortedTypeSize; j++)
    {
        String sTypeName = (String)sortedTypes.get(j);
        if(arrTypeArrayContent.length() > 0)
        {
            arrTypeArrayContent.append(",");
        }
        arrTypeArrayContent.append("type_"+sTypeName.replace(" ",""));
    }

    if (arrTypeArrayContent.toString().endsWith(","))
    {
        arrTypeArrayContent.delete(arrTypeArrayContent.toString().length()-1,arrTypeArrayContent.toString().length());
    }
    
    contentURL ="../common/emxFullSearch.jsp?field=TYPES="+arrTypeArrayContent.toString()+"&showInitialResults=false&excludeOIDprogram=emxPart:excludeConnectedObjects&table=ENCPartSearchResult&selection=multiple&submitAction=refreshCaller&hideHeader=true&HelpMarker=emxhelpfullsearch&submitURL=../engineeringcentral/SearchUtil.jsp&mode=AddExisting&relName="+partType+"&from=true&objectId="+objectId+"&sMode=ECRAddExisting&suiteKey="+suiteKey;

 } else if(sMode!=null && sMode.equals("PartRefDocAddExisting")){
     String sDocumentType = (String)com.matrixone.apps.domain.util.FrameworkProperties.getProperty(context,"eServiceEngineeringCentral.ReferenceDocumentTypes");
     boolTypeExists = isTypeExists(context, sDocumentType);
     contentURL ="../common/emxFullSearch.jsp?field=TYPES="+sDocumentType+":Policy!=policy_Version&showInitialResults=false&excludeOIDprogram=emxPart:excludeConnectedObjects&table=ENCPartSearchResult&selection=multiple&submitAction=refreshCaller&hideHeader=true&HelpMarker=emxhelpfullsearch&submitURL=../engineeringcentral/SearchUtil.jsp&mode=AddExisting&relName=relationship_ReferenceDocument&from=true&formInclusionList=Description&objectId="+objectId+"&from=true&suiteKey="+suiteKey+"&submittedFrom=AddExistingDocument";
 }
 else if(sMode!=null && sMode.equals("PartFamilyRefDocAddExisting")){//Added this block to fix IR-093282V6R2012
     String sDocumentType = (String)com.matrixone.apps.domain.util.FrameworkProperties.getProperty(context,"eServiceEngineeringCentral.ReferenceDocumentTypes");
     boolTypeExists = isTypeExists(context, sDocumentType);
     contentURL ="../common/emxFullSearch.jsp?field=TYPES="+sDocumentType+":Policy!=policy_Version&showInitialResults=false&excludeOIDprogram=emxPartFamily:excludeConnectedObjects&table=ENCPartSearchResult&selection=multiple&submitAction=refreshCaller&hideHeader=true&HelpMarker=emxhelpfullsearch&submitURL=../engineeringcentral/SearchUtil.jsp&mode=AddExistingInPartFamily&relName=relationship_PartFamilyReferenceDocument&from=true&formInclusionList=Description&objectId="+objectId+"&suiteKey="+suiteKey;
 }
 else if("true".equals(specAddExisting))
 {
	 StringList excludeTypeList = EngineeringUtil.getSettingFromCommand(context,sCommandName,sSettingName);
	 //Modified By XPLORIA to remove CAD Model and CAD Drawing from the Selection Starts here
	 if(!excludeTypeList.contains("type_CADModel") || !excludeTypeList.contains("type_CADDrawing")){
		 excludeTypeList.add("type_CADModel");
		 excludeTypeList.add("type_CADDrawing");
	 }
	 //Modified By XPLORIA to remove CAD Model and CAD Drawing from the Selection Ends here
	 
	 StringList toTypeList = EngineeringUtil.getRelationshipToTypeList(context,EngineeringConstants.RELATIONSHIP_PART_SPECIFICATION);
	 String typeNames = FrameworkUtil.join(EngineeringUtil.getRelToTypeListWithExcludeTypes(context,toTypeList,excludeTypeList),EngineeringConstants.COMMA);
	 //Modified by Preethi Rajaraman for id_27 -- Starts
	 //contentURL ="../common/emxFullSearch.jsp?field=TYPES=" +typeNames+ ":Policy!=policy_Version&table=ENCDocumentSummary&selection=multiple&submitAction=refreshCaller&AddExistingSpecification=true&submitURL=../engineeringcentral/emxEngrAddExistingSpecsAndMarkupsConnect.jsp&srcDestRelName=relationship_PartSpecification&excludeOIDprogram=emxPartBase:excludeOIDPartSpecificationConnectedItems&HelpMarker=emxhelpfullsearch&showInitialResults=false&objectId="+objectId+"&suiteKey="+suiteKey;
 	 contentURL ="../common/emxFullSearch.jsp?field=TYPES=" +typeNames+ ":Policy!=policy_Version&selection=multiple&submitAction=refreshCaller&AddExistingSpecification=true&submitURL=../engineeringcentral/emxEngrAddExistingSpecsAndMarkupsConnect.jsp&srcDestRelName=relationship_PartSpecification&excludeOIDprogram=emxPartBase:excludeOIDPartSpecificationConnectedItems&HelpMarker=emxhelpfullsearch&showInitialResults=false&objectId="+objectId+"&suiteKey="+suiteKey;
	//Modified by Preethi Rajaraman for id_27 -- Ends 
}

    if (!boolTypeExists) {
%>
         <script language="javascript">               
               alert("<emxUtil:i18nScript localize="i18nId">emxEngineeringCentral.ReferenceDocuments.InvalidType</emxUtil:i18nScript>");
               self.closeWindow();               
         </script>
<%       
return;
	}	 
%>


<script language="javascript">
	var frameName = parent.name;
	//XSSOK

     <%--  window.location.href = '<%=XSSUtil.encodeForJavaScript(context,contentURL)%>'; --%>
     var contentFrame = findFrame(getTopWindow(),frameName);
     contentFrame.showModalDialog('<%=XSSUtil.encodeForJavaScript(context,contentURL)%>'+'&frameName='+frameName); 
     
	</script>    
<%@include file = "emxDesignBottomInclude.inc"%>
<%@include file = "../emxUICommonEndOfPageInclude.inc"%>


