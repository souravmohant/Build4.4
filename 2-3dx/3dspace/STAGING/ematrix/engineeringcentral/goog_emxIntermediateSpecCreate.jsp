<%--  emxIntermediateSpecCreate.jsp -  This is an intermediate jsp to invoke webform
   Copyright (c) 1992-2018 Dassault Systemes.
   All Rights Reserved.
   This program contains proprietary and trade secret information of Dassault Systemes
   Copyright notice is precautionary only and does not evidence any actual or
   intended publication of such program
--%>

<%@include file = "emxDesignTopInclude.inc"%>
<%@include file = "emxEngrVisiblePageInclude.inc"%>
<%@include file = "../common/emxTreeUtilInclude.inc"%>
<%@include file = "../common/emxNavigatorTopErrorInclude.inc"%>
<%@include file = "../common/enoviaCSRFTokenValidation.inc"%>

<%

		String objectId = emxGetParameter(request, "objectId");
		String parentOID = emxGetParameter(request, "parentOID");
		String relId = emxGetParameter(request, "relId");
		
		String ecoName = "";
		String ecoId = "";
		
		if (parentOID != null && !"".equals(parentOID)) {
		    HashMap requestMap = new HashMap();
		    requestMap.put("objectId", parentOID);
		    
		    MapList list = (MapList) JPO.invoke(context, "emxPart", null, "getConnectedECOFromPart", (String[]) JPO.packArgs(requestMap), MapList.class);
		    
		    if (list.size() > 0) {
		        Map map = (Map) list.get(0);
		        ecoName = (String) map.get(DomainConstants.SELECT_NAME);
		        ecoId = (String) map.get(DomainConstants.SELECT_ID);
		    }
		}		
		
		//Modified by XPLORIA to add the googCADModelCustomType
		
		String actionURL = "../common/emxCreate.jsp?form=type_CreateSpecification&type=type_PartSpecification,type_googCADModel,type_DrawingPrint&policy=policy_CADDrawing&nameField=both&typeChooser=true&submitAction=refreshCaller&header=emxEngineeringCentral.Common.CreateSpec&HelpMarker=emxhelppartspecificationscreate&postProcessJPO=emxPart:performPostProcessConnect&preProcessJavaScript=preProcessInCreateSpecFromIntermediate&createJPO=emxECPartBase:createSpecification&suiteKey=EngineeringCentral&StringResourceFileId=emxEngineeringCentralStringResource&SuiteDirectory=engineeringcentral&parentOID=" + parentOID + "&hdnECOName=" + ecoName + "&hdnECOId=" + ecoId + "&objectId=" + objectId + "&relId=" + relId;

%>
<script language="javascript" src="../common/scripts/emxUICore.js"></script>
   <script language="javascript">        
   //XSSOK       
        getTopWindow().showSlideInDialog("<%=XSSUtil.encodeForJavaScript(context,actionURL)%>", true);           
   </script>         
		
