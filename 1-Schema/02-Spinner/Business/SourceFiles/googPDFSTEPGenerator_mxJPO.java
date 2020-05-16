/**
 * This JPO used for generating PDF/STEP file in batch mode.
 * @author shajil
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.matrixone.MCADIntegration.utils.URLDecoder;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MatrixLogWriter;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class googPDFSTEPGenerator_mxJPO extends googDerivedOutputConstants_mxJPO {

	public static final String PART_SPECIFICATION_ID = "to[Part Specification].from.id";
	public static final String PART_SPECIFICATION_REL_ID = "to[Part Specification].id";
	public static final String ORGANIZATION = "organization";
	public static final String PROJECT = "project";
	public static final String FILE_FORMAT_STP = ".stp";
	public static final String FILE_FORMAT_PDF = ".pdf";
	//Added by XPLORIA for 3DXML File Generation Starts here
	public static final String FILE_FORMAT_3DXML= ".3dxml";
	//Added by XPLORIA for 3DXML File Generation Ends here
	public static final String STEP_GENERATOR = "STEPGenerator";
	public static final String PDF_GENERATOR = "PDFGenerator";
	public static final String ATTR_BEGIN = "attribute[";
	public static final String ATTR_END = "]";
	//Added by XPLORIA to use googCAD Model insted of CAD Model
	public static final String type_googCADModel = PropertyUtil.getSchemaProperty("type_googCADModel");
	public static final String ATTR_PLM_EXTERNAL_ID = PropertyUtil.getSchemaProperty("attribute_PLMEntity.PLM_ExternalID" );
	String VAULT_ESERVICE_PRODUCTION = null;
	MatrixLogWriter Log = null;
	HashMap<String, String> pageMap;
	String dirPath;
	String dirSTP;
	String agentName;
	//Added by XPLORIA for 3DXML File Generation Starts here
	String dir3DXML;
	String TASKLIST = "tasklist";
	String serviceName = "CATSysDemon.exe";
	String KILL = "taskkill /IM ";
	String FORMAT_3DXML = "3DXML";
	//Added by XPLORIA for 3DXML File Generation Ends here
	StringList busSelects = new StringList();

	public googPDFSTEPGenerator_mxJPO() {
	}

	public googPDFSTEPGenerator_mxJPO(Context context, String[] args) throws Exception {
		Log = new MatrixLogWriter(context);
		pageMap = getPageMap(context, "googSTEPPDFBatchConfiguration");
		dirPath = pageMap.get("goog_PDFGenerator.dirPDF");
		dirSTP = pageMap.get("goog_STPGenerator.dirSTP");
		//Added by XPLORIA for 3DXML File generation Starts here
		dir3DXML = pageMap.get("goog_STPGenerator.dir3DXML");
		//Added by XPLORIA for 3DXML File generation Ends here
		agentName = emxMailUtil_mxJPO.getAgentName(context, null);
		
		xmlEncoded = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.xmlEncoded");
		xmlFilterObjEncoded = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.xmlFilterObjEncoded");
		WAYMO_minorRev = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.WAYMO_minorRev");
		WAYMO_majorRev = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.WAYMO_majorRev");
		WAYMO_plmExtId = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.WAYMO_plmExtId");
		WAYMOSTRING1 = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.WAYMOSTRING1");
		WAYMOSTRING2 = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.WAYMOSTRING2");
		plmObjXmlEncoded = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_STPGenerator.plmObjXmlEncoded");
		MINOREVISION = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.MINOREVISION");
		MAJORREVISION = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.MAJORREVISION");
		format = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.format");
		store = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.store");
		unlock = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.unlock");
		server = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.server");
		comments = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.comments");

		cmd = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.cmd");
		checkinBus = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.checkinBus");
		catBatchStarter = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.catBatchStarter");
		mqlCommandArgs = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.mqlCommandArgs");
		VAULT_ESERVICE_PRODUCTION =	PropertyUtil.getSchemaProperty(context,"vault_eServiceProduction");
		
	}

	public int mxMain(Context context, String[] args) throws Exception {
		return 0;
	}

	/**
	 * This method will initiate the File generation process
	 * @param context
	 * @param args
	 * @param format
	 * @return excludedList
	 * @throws Exception
	 * @author shajil
	 */
	public ArrayList<String> initiatePDFGeneration(Context context, String[] args, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-format-"+format);
		String[] selectedIdsList = args;
		String strPartId = null;
		MapList cadObjectsList;
		DomainObject domPart = new DomainObject();
		ArrayList<String> excludedList = new ArrayList<String>();

		busSelects.addElement(DomainObject.SELECT_ID);
		busSelects.addElement(DomainObject.SELECT_TYPE);
		busSelects.addElement(DomainObject.SELECT_NAME);
		busSelects.addElement(DomainObject.SELECT_REVISION);
		busSelects.addElement(MINOREVISION);
		busSelects.addElement(MAJORREVISION);
		busSelects.addElement("attribute[" + ATTR_VNAME + "]");
		busSelects.addElement("attribute[" + ATTRIBUTE_TITLE + "]");
		busSelects.addElement("attribute[V_description]");
		

		busSelects.addElement("latest");
		busSelects.addElement(SELECT_LAST_ID);
		busSelects.addElement("last.current");
		busSelects.addElement(SELECT_CURRENT);

		String type = "";
		String relationshipName = "";
		short level = (short) 1;
		String path = "";
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			type = type_VPMReference + "," + type_Drawing + "," + type_DrawingPrint;
			relationshipName = DomainObject.RELATIONSHIP_PART_SPECIFICATION + "," + relVPMRepInstance;
			level = (short) 2;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			//Modified by XPLORIA for using googCAD Model insted of CAD Model
			//type = type_VPMReference + "," + type_CADModel;
			type = type_VPMReference + "," + type_googCADModel;
			relationshipName = DomainObject.RELATIONSHIP_PART_SPECIFICATION + "," + relVPMRepInstance;
			path = dirSTP;
		}

		Map<String, MapList> cadObjectMap = new HashMap<String, MapList>();
		MapList allCADObjectsList = new MapList();
		for (int i = 0; i < selectedIdsList.length; i++) {
			strPartId = (String) selectedIdsList[i];
			Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-strPartId-"+strPartId);
			domPart.setId(strPartId);
			cadObjectsList = domPart.getRelatedObjects(context, relationshipName, type, busSelects,
					new StringList(SELECT_RELATIONSHIP_ID), false, true, level, "", "", 0);
			if (cadObjectsList.size() > 0) {
				Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-before-validateCADAndDrawingExists-");
				if (validateCADAndDrawingExists(context, cadObjectsList, format)) {
					Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-validateCADAndDrawingExists-");
					allCADObjectsList.addAll(cadObjectsList);
					cadObjectMap.put(strPartId, cadObjectsList);
				} else {
					Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-not-validateCADAndDrawingExists-");
					excludedList.add(strPartId);
				}
			} else {
				Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-validateCADAndDrawingExists-2-");
				excludedList.add(strPartId);
			}
		}
		if (allCADObjectsList.size() > 0) {
			boolean isSuccess;
			Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-before-prepareInputsForBatch-");
			isSuccess = prepareInputsForBatch(context, allCADObjectsList, cadObjectMap, format);
			Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-prepareInputsForBatch-isSuccess-"+isSuccess);
			if (isSuccess) {
				Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-before-createFileFromCATIA-");
				isSuccess = createFileFromCATIA(context, format); // Geneate PDF
				Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-createFileFromCATIA-isSuccess-"+isSuccess);
				if (isSuccess) {
					Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-before-renameFiles-");
					renameFiles(context, allCADObjectsList, format); // Rename the PDF Files to WaymoSpecific
					Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-renameFiles-");
					isSuccess = checkinGeneratedFile(context, cadObjectMap, excludedList, format); 
					Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-after-checkinGeneratedFile-isSuccess-"+isSuccess);
				}
			}
		}
		return excludedList;
	}

	/**
	 * This method used for send mail notification to the JOB owner and support team
	 * @param context
	 * @throws Exception
	 * @author shajil
	 */
	public void sendMailNotification(Context context, MapList mailDetails) throws Exception {
	Log.write("googPDFSTEPGenerator--inside sendMailNotification--");
		Iterator itr = mailDetails.iterator();
		while (itr.hasNext()) {
			Map<String, String> mailInfo = (Map) itr.next();
			StringList toMailIds = FrameworkUtil.split(mailInfo.get("Email.toList"), ",");
			StringList ccMailIds = FrameworkUtil.split(mailInfo.get("Email.toList"), ",");
			String sSubject = mailInfo.get("Email.subject");
			String message = mailInfo.get("Email.Body");
			try {
				emxNotificationUtil_mxJPO.sendJavaMail(context, toMailIds, // StringList toList,
						ccMailIds, // StringList ccList,
						null, // StringList bccList,
						sSubject, // String subject,
						message, // String messageText,
						message, // String messageHTML,
						agentName, // String fromAgent,
						null, // StringList replyTo,
						null, // StringList objectIdList,
						"Email");// String notifyType)
			Log.write("googPDFSTEPGenerator--Exit sendMailNotification--");
			} catch (Exception e) {
				Log.write("Issue on mail configuration");
				Log.write(e.getMessage());
			}
		}
	}

	/**
	 * This method is used to rename the file generated by CATIA.
	 * @param context
	 * @param allCADObjectsList
	 * @param format
	 * @throws Exception
	 * @author shajil
	 */
	public void renameFiles(Context context, MapList allCADObjectsList, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter renameFiles-");
		Map objMap;
		String strFileName;
		String path = "";
		File[] listOfFiles;
		String type = "";
		String fileFormat = "";
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			type = type_Drawing;
			fileFormat = FILE_FORMAT_PDF;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			type = type_VPMReference;
			fileFormat = FILE_FORMAT_STP;
			path = dirSTP;
		}
		File directory = new File(path);
		
		try {
			for (int i = 0; i < allCADObjectsList.size(); i++) {
				objMap = (Map) allCADObjectsList.get(i);
				if (type.equals(objMap.get(DomainConstants.SELECT_TYPE).toString())) {
					String fileName = (String) objMap.get("attribute[" + ATTR_VNAME + "]");
					Log.write("googPDFSTEPGenerator--In renameFiles-fileName--"+fileName);
					fileName = fileName.replace(" ", "_");
					strFileName = fileName + "_REV_" + objMap.get("majorrevision").toString() + fileFormat;
					Log.write("googPDFSTEPGenerator--In renameFiles-strFileName-1-"+strFileName);
					if(type.equals(type_VPMReference)) {
						String tilte = fileName;
						fileName = (String) objMap.get(SELECT_NAME);
						strFileName = tilte + "_REV_" + objMap.get("majorrevision").toString() + fileFormat;
					}
					Log.write("googPDFSTEPGenerator--In renameFiles-strFileName-2-"+strFileName);
					listOfFiles = directory.listFiles();
					for (int k = 0; k < listOfFiles.length; k++) {
						String availableFile = listOfFiles[k].getName();
						if (availableFile.contains(fileName) && availableFile.contains(fileFormat)) {
							File fileRename = new File(path + strFileName);		
							listOfFiles[k].renameTo(fileRename);
							Log.write("googPDFSTEPGenerator--In renameFiles-File Renamed--");
							
						}
					}
				}
			}
			Log.write("googPDFSTEPGenerator--exit renameFiles-");
		} catch (Exception e) {
			Log.write("Failed to rename the file.............");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to prepare input files for CATIA.
	 * @param context
	 * @param cadObjectsList
	 * @param cadObjectMap
	 * @param format
	 * @return
	 * @throws Exception
	 * @author shajil
	 */
	public boolean prepareInputsForBatch(Context context, MapList cadObjectsList, Map<String, MapList> cadObjectMap,
			String format) throws Exception {
				Log.write("googPDFSTEPGenerator--enter prepareInputsForBatch-");
		String dirReadWriteFinalBatchXml = pageMap.get("goog_PDFGenerator.dirReadWriteFinalBatchXml");
		String dirReadWriteFinalBatchCopyXml = pageMap.get("goog_PDFGenerator.dirReadWriteFinalBatchCopyXml");
		String xmlDecoded = URLDecoder.decode(xmlEncoded);
		String xmlFilterObjDecoded = URLDecoder.decode(xmlFilterObjEncoded);
		String type = "";
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			type = type_Drawing;
			dirReadWriteFinalBatchXml = pageMap.get("goog_PDFGenerator.dirReadWriteFinalBatchXml");
			dirReadWriteFinalBatchCopyXml = pageMap.get("goog_PDFGenerator.dirReadWriteFinalBatchCopyXml");
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			type = type_VPMReference;
			dirReadWriteFinalBatchXml = pageMap.get("goog_STPGenerator.dirReadWriteFinalBatchXmlSTP");
			dirReadWriteFinalBatchCopyXml = pageMap.get("goog_STPGenerator.dirReadWriteFinalBatchCopyXml");
			xmlDecoded = URLDecoder.decode(plmObjXmlEncoded);
		} else if(UIUtil.isNotNullAndNotEmpty(format) && FORMAT_3DXML.equalsIgnoreCase(format)) {
			type = type_VPMReference;
			dirReadWriteFinalBatchXml = pageMap.get("goog_3DGenerator.dirReadWriteFinalBatchXml3D");
			dirReadWriteFinalBatchCopyXml = pageMap.get("goog_3DGenerator.dirReadWriteFinalBatchCopy3DXml");
			xmlDecoded = URLDecoder.decode(plmObjXmlEncoded);
		}
		boolean isSuccess = true;
		try {
			Reader fileReaderFinalBatchCopyXml = new FileReader(dirReadWriteFinalBatchCopyXml);
			BufferedReader bufReaderFinalBatchCopyXml = new BufferedReader(fileReaderFinalBatchCopyXml);
			FileWriter fileWriterFinalBatchXml = new FileWriter(dirReadWriteFinalBatchXml);
			BufferedWriter bufferedWriterFinalBatchXml = new BufferedWriter(fileWriterFinalBatchXml);
			String lineFinalBatchCopy = bufReaderFinalBatchCopyXml.readLine();
			StringBuilder sbFinalBatchCopy = new StringBuilder(lineFinalBatchCopy);
			Map cadDwgObjMap;
			String helperStrPLMObj = null;
			String helperFilter = null;
			while (null != lineFinalBatchCopy) {
				lineFinalBatchCopy = bufReaderFinalBatchCopyXml.readLine();
				if (UIUtil.isNotNullAndNotEmpty(lineFinalBatchCopy)) {
					sbFinalBatchCopy.append(lineFinalBatchCopy).append("\n");
				}
			}
			bufReaderFinalBatchCopyXml.close();
			final String waymoStringBackup = sbFinalBatchCopy.toString();
			Set<String> keySet = cadObjectMap.keySet();
			Iterator keyItr = keySet.iterator();
			StringBuilder filterObj = new StringBuilder();
			StringBuilder plmObj = new StringBuilder();
			while (keyItr.hasNext()) {
				MapList drawingList = (MapList) cadObjectMap.get(keyItr.next());
				for (int cad = 0; cad < drawingList.size(); cad++) {
					cadDwgObjMap = (Map) drawingList.get(cad);
					if (type.equals(cadDwgObjMap.get(DomainConstants.SELECT_TYPE).toString())) {
						helperStrPLMObj = xmlDecoded
								.replaceAll(WAYMO_minorRev, cadDwgObjMap.get(MINOREVISION).toString())
								.replaceAll(WAYMO_majorRev, cadDwgObjMap.get(MAJORREVISION).toString())
								.replaceAll(WAYMO_plmExtId, cadDwgObjMap.get(DomainObject.SELECT_NAME).toString());

						helperFilter = xmlFilterObjDecoded
								.replaceAll(WAYMO_minorRev, cadDwgObjMap.get(MINOREVISION).toString())
								.replaceAll(WAYMO_majorRev, cadDwgObjMap.get(MAJORREVISION).toString())
								.replaceAll(WAYMO_plmExtId, cadDwgObjMap.get(DomainObject.SELECT_NAME).toString());
						filterObj.append(helperFilter);
						filterObj.append("\n");
						plmObj.append(helperStrPLMObj);
						plmObj.append("\n");
					}
				}
			}
			String helperRemSemiColon = plmObj.toString().replaceAll("\";", "");
			String helperFinalXmlWaymoStringReplaced = waymoStringBackup.replaceAll(WAYMOSTRING1, helperRemSemiColon)
					.replaceAll(WAYMOSTRING2, filterObj.toString());
			bufferedWriterFinalBatchXml.write(helperFinalXmlWaymoStringReplaced);
			bufferedWriterFinalBatchXml.close();
			Log.write("googPDFSTEPGenerator--exit prepareInputsForBatch-");
		} catch (Exception e) {
			isSuccess = false;
			Log.write("Error occured during Input file generation");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	/**
	 * This method is used to execute CATIA batch process for creating the PDF/STP Files
	 * @param context
	 * @param format
	 * @return
	 * @throws Exception
	 * @author shajil
	 */
	public boolean createFileFromCATIA(Context context, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter createFileFromCATIA--");
		Process batchProcess = null;
		boolean isSuccess = true;
		//Addes by XPLORIA for the STEP PDF Issue Starts here
		Map pdfTimerMap = new HashMap();
		int i = 0;
		//Added by XPLORIA for the STEP PDF Issue Ends here
		
		try {
			//Addes by XPLORIA for the STEP PDF Issue Starts here
			pdfTimerMap = getPageMap(context, "googSTEPPDFTIMERConfiguration");	
			String eServiceRetryCountStr = (String)pdfTimerMap.get("goog_PDFGenerator.eServiceRetryCount");
            String timeOutStr = (String)pdfTimerMap.get("goog_PDFGenerator.ThreadSleep");
            int eServiceRetryCount = Integer.parseInt(eServiceRetryCountStr);
            long timeout = Long.parseLong(timeOutStr);
			//Added by XPLORIA for the STEP PDF Issue Ends here
			Runtime rs = Runtime.getRuntime();
			if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
				Log.write("googPDFSTEPGenerator--Inside createFileFromCATIA-batch execution 1 for pdf-");
				batchProcess = rs.exec("cmd /c start /wait " +pageMap.get("goog_PDFGenerator.dirPdfBat"));
				Log.write("googPDFSTEPGenerator--Inside createFileFromCATIA-batch execution 2 for pdf-");
			} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
				Log.write("googPDFSTEPGenerator--Inside createFileFromCATIA-batch execution 1 for STEP-");
				batchProcess = rs.exec("cmd /c start /wait " +pageMap.get("goog_STPGenerator.dirSTPBat"));
				Log.write("googPDFSTEPGenerator--Inside createFileFromCATIA-batch execution 2 for STEP-");
			}
			
			//Added by XPLORIA for the STEP PDF Issue Starts here
			 while(i < eServiceRetryCount){
				 Log.write("googPDFSTEPGenerator--Inside Retry Count-");
                  if (batchProcess.isAlive()) {
					  Log.write("googPDFSTEPGenerator--Inside Retry Count-Alive"+i);
                      Thread.sleep(timeout);
                       i++;
	                   continue;
                  } else {
					   Log.write("googPDFSTEPGenerator--Inside Retry Count-Not Alive");
                      batchProcess.destroyForcibly();
	                  break;
                    }
	           }
             if (i == eServiceRetryCount) {
				 Log.write("googPDFSTEPGenerator--Inside Retry Count-Destroy as it reaches the timeout parameter");
                 batchProcess.destroyForcibly();
              }	 
			  // batchProcess.waitFor();
			//Added by XPLORIA for the STEP Issue Ends here
			
			
			Log.write("googPDFSTEPGenerator--Inside createFileFromCATIA-batch execution wait over-");
			/* if (!batchProcess.isAlive()) {

			} */
			Log.write("googPDFSTEPGenerator--exit createFileFromCATIA-");
		} catch (Exception e) {
			isSuccess = false;
			Log.write("Error occured during file generation");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	

	/**
	 * This method is used to initiate the file checkin
	 * @param context
	 * @param cadObjectMap
	 * @param excludedList
	 * @param format
	 * @return isSuccess
	 * @throws Exception
	 * @author shajil
	 */
	public boolean checkinGeneratedFile(Context context, Map<String, MapList> cadObjectMap,
			ArrayList<String> excludedList, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter checkinGeneratedFile-");
		boolean isSuccess = true;
		String strPartId;
		String drwaingType = "";
		String fileFormat = "";
		String path = "";
		String printType = "";

		//String policy = null;
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--format-"+format);
			drwaingType = type_DrawingPrint;
			printType = type_Drawing;
			fileFormat = FILE_FORMAT_PDF;
			//policy  = policy_DrawingPrint;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--format-"+format);
			//Modified by XPLORIA to use googCAD Model Insted of CAD Model
			//drwaingType = type_CADModel;
			drwaingType = type_googCADModel;
			printType = type_VPMReference;
			fileFormat = FILE_FORMAT_STP;
		//	policy = policy_DrawingPrint;
			path = dirSTP;
		}
		DomainObject domPart = new DomainObject();
		busSelects.addElement("latest");
		busSelects.addElement(SELECT_LAST_ID);
		busSelects.addElement("last.current");
		busSelects.addElement(SELECT_CURRENT);
		busSelects.addElement(SELECT_REVISION);
		busSelects.addElement(SELECT_OWNER);
		busSelects.addElement(SELECT_ORIGINATOR);
		busSelects.addElement(PROJECT);
		busSelects.addElement(ORGANIZATION);
		busSelects.addElement(PART_SPECIFICATION_REL_ID);
		busSelects.addElement(PART_SPECIFICATION_ID);
		
		
		Set keySet = cadObjectMap.keySet();
		Iterator keyItr = keySet.iterator();
		Map drawingDetails = new HashMap();
		
		while (keyItr.hasNext()) {
			strPartId = (String) keyItr.next();			
			domPart.setId(strPartId);			
			String partName = domPart.getInfo(context, SELECT_NAME);
			MapList partDetails = cadObjectMap.get(strPartId);
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--before getDocuments-");
			MapList cadDocuments = getDocuments(context,partName,drwaingType) ;
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--before isDocumentExist-");
			boolean isCADDocumentExist = isDocumentExist(context,cadDocuments,partDetails);
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--before updatePartRevisionObjects-");
			updatePartRevisionObjects(context, partName, drwaingType);
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--before getDrawingInfo-");
			drawingDetails = getDrawingInfo(partDetails, drwaingType);
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile--before getDrawingInfoList-");
			MapList vpmRefInfo = getDrawingInfoList(partDetails, printType);
			boolean isCreated = true;
			Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-"+isCADDocumentExist);
			if (!isCADDocumentExist) {
				if(cadDocuments.size()>0) {
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-not isCADDocumentExist-before getDrawingInfo-");
					Map specInfo  = getDrawingInfo(partDetails, type_VPMReference);
					String cadDocRev = (String)specInfo.get("majorrevision");
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-not isCADDocumentExist-before createMissingDocuments-");				   
					isCreated = createMissingDocuments(context, domPart, vpmRefInfo, excludedList, format,cadDocRev);
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-not isCADDocumentExist-after createMissingDocuments-isCreated-"+isCreated);
				}else {
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-not isCADDocumentExist-before createAndConnectDrawingPrint-");				   
					isCreated = createAndConnectDrawingPrint(context, domPart, vpmRefInfo, excludedList, format);
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-not isCADDocumentExist-after createAndConnectDrawingPrint-isCreated-"+isCreated);
									  
				}
				if (!isCreated) {
					isSuccess = false;
				}
			} else {
				try {
					String drawingId = null;
					String drwaingState = null;					
					StringBuilder file = new StringBuilder();
					Map drawingInfo = (Map)vpmRefInfo.get(0);
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-before updateDrawingDetails-");
					drawingDetails = updateDrawingDetails(context,strPartId,cadDocuments,drawingInfo);
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-after updateDrawingDetails-");
					drawingId = (String) drawingDetails.get(SELECT_ID);
					drwaingState = (String) drawingDetails.get(SELECT_CURRENT);
					
					String title = ((String)drawingInfo.get("attribute[" + ATTR_VNAME + "]")).replace(" ", "_");
					file.append(title).append("_REV_")
							.append(drawingInfo.get("majorrevision")).append(fileFormat);
Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-before isFileExists-");							
					boolean fileExist = isFileExists(vpmRefInfo,format);
					Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-before isFileExists-fileExist-"+fileExist);
					if (!fileExist) {
						excludedList.add(strPartId);
						isSuccess = false;
						Log.write("Checked in file(" + file + ") does not exist");
					}
					if (fileExist) {
						Iterator drawingItr = vpmRefInfo.iterator();
						while (drawingItr.hasNext()) {

							Map cadInfo = (Map) drawingItr.next();
							Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-before checkinBus-fileExist-");
							isCreated = checkinBus(context, cadInfo, drawingId, format);
							Log.write("googPDFSTEPGenerator--inside checkinGeneratedFile-isCADDocumentExist-after checkinBus-fileExist-isCreated-"+isCreated);
							if (!isCreated) {
								excludedList.add(strPartId);
								isSuccess = false;
							}
						}
					}
				Log.write("googPDFSTEPGenerator--exit checkinGeneratedFile-");
				} catch (Exception e) {
					excludedList.add(strPartId);
					isSuccess = false;
					Log.write("Error occured during "+drwaingType+" object update");
					Log.write(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return isSuccess;
	}

	public boolean createMissingDocuments(Context context, DomainObject domPart, MapList drawingList,
			ArrayList<String> excludedList, String format,String cadDocRev) throws Exception {
				Log.write("googPDFSTEPGenerator--enter createMissingDocuments-");
		String fileFormat = "";
		String policy = "";
		String type = "";
		String path = "";
		StringList objectSelect = new StringList();
		objectSelect.addElement(SELECT_ID);
		objectSelect.addElement(SELECT_NAME);
		objectSelect.addElement(SELECT_OWNER);
		objectSelect.addElement(SELECT_ORIGINATOR);
		objectSelect.addElement(PROJECT);
		objectSelect.addElement(ORGANIZATION);
		
		Map partInfo = domPart.getInfo(context, objectSelect);
		String partOwner = (String)partInfo.get(SELECT_OWNER);
		String partOriginaror = (String)partInfo.get(SELECT_ORIGINATOR);
		String partId = (String)partInfo.get(SELECT_ID);
		String partName = (String)partInfo.get(SELECT_NAME);
		boolean isSuccess = true;
		boolean fileExists = false;
		Map<String, String> attrDetails = new HashMap<String, String>();
		attrDetails.put(ATTR_TITLE, partName);
		attrDetails.put(ATTRIBUTE_ORIGINATOR, partOriginaror);
		
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside createMissingDocuments-format-"+format);
			policy = policy_DrawingPrint;
			type = TYPE_DRAWINGPRINT;
			fileFormat = FILE_FORMAT_PDF;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside createMissingDocuments-format-"+format);
			//Modified by XPLORIA to use googCAD Model insted of CAD Model
			//type = TYPE_CAD_MODEL;
			type = type_googCADModel;
			policy = policy_CADModel;
			fileFormat = FILE_FORMAT_STP;
			path = dirSTP;
		}
		Log.write("googPDFSTEPGenerator--inside createMissingDocuments-before isFileExists-");
		boolean fileExist = isFileExists(drawingList,format);
		Log.write("googPDFSTEPGenerator--inside createMissingDocuments-after isFileExists-fileExist-"+fileExist);
		if (fileExist) {
			DomainObject doCon = DomainObject.newInstance(context);
			
			try {
				MqlUtil.mqlCommand(context, "trigger off");
				doCon.createObject(context, type, partName, cadDocRev, policy, VAULT_ESERVICE_PRODUCTION);
				String project = (String) partInfo.get(PROJECT);
				String organization = (String) partInfo.get(ORGANIZATION);
				doCon.setOwner(context, partOwner);
				doCon.setPrimaryOwnership(context, project, organization);
				MqlUtil.mqlCommand(context, "trigger on");
			} catch (Exception e) {
				Log.write("googPDFSTEPGenerator--inside createMissingDocuments-exception in Create-");
				e.printStackTrace();
			}
			Iterator drawigItr = drawingList.iterator();
			while (drawigItr.hasNext()) {
				Map vpmRefInfo = (Map) drawigItr.next();
				StringBuilder file = new StringBuilder();
				String productRev = (String) vpmRefInfo.get(SELECT_REVISION);
				String productMajorRev = (String) vpmRefInfo.get("majorrevision");
				StringBuilder drawingName = new StringBuilder(
						((String) vpmRefInfo.get("attribute[" + ATTR_VNAME + "]")).replace(" ", "_"));
				String productMajorRevision = (String) vpmRefInfo.get("majorrevision");
				file.append(drawingName).append("_REV_").append(productMajorRev).append(fileFormat);
				drawingName.append("_").append(productRev);
				try {
					StringBuilder fileName = new StringBuilder(path);
					fileName.append(file);
					File dir = new File(fileName.toString());
					if (dir.exists()) {
						fileExists = true;
						try {							
							String documentId = doCon.getObjectId();
							Log.write("googPDFSTEPGenerator--inside createMissingDocuments-before checkinBus-");
							isSuccess = checkinBus(context, vpmRefInfo, documentId, format);
							Log.write("googPDFSTEPGenerator--inside createMissingDocuments-after checkinBus-isSuccess-"+isSuccess);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (!isSuccess) {
							excludedList.add(partId);
						}
					} else {
						excludedList.add(partId);
						Log.write("Checked in file(" + file + ") does not exist");
					}
				} catch (Exception e) {
					isSuccess = false;
					excludedList.add(partId);
					Log.write("Error occured during " + policy + " creation");
					Log.write(e.getMessage());
				}
			}
			if (fileExists && isSuccess) {
				Log.write("googPDFSTEPGenerator--inside createMissingDocuments-before part spec connect-");
				DomainRelationship.connect(context, domPart, RELATIONSHIP_PART_SPECIFICATION, doCon);
				doCon.setAttributeValues(context, attrDetails);
				Log.write("googPDFSTEPGenerator--inside createMissingDocuments-after part spec connect-");
			} else {
				Log.write("googPDFSTEPGenerator--inside createMissingDocuments-before delete object-");
				doCon.deleteObject(context);
				Log.write("googPDFSTEPGenerator--inside createMissingDocuments-after delete object-");
			}
		}else {
			excludedList.add(partId);
		}
		
		return isSuccess;
	}

	private boolean isDocumentExist(Context context, MapList cadDocuments, MapList partDetails) throws Exception {
		Log.write("googPDFSTEPGenerator--enter isDocumentExist-");
		Map drawingDetails = getDrawingInfo(partDetails, type_VPMReference);
		Log.write("googPDFSTEPGenerator--inside isDocumentExist-after getDrawingInfo--");
		String specRev = (String)drawingDetails.get("majorrevision");
		Iterator itr = cadDocuments.iterator();
		while(itr.hasNext()) {
			Map cadInfo = (Map)itr.next();
			String cadRev = (String)cadInfo.get(SELECT_REVISION);
			if(cadRev.equals(specRev)) {
				Log.write("googPDFSTEPGenerator--exit isDocumentExist-true--");
				return true;
			}
		}
		Log.write("googPDFSTEPGenerator--exit isDocumentExist-false--");
		return false;
	}

	/**
	 * This method is used to get Part Spec objects(with all revision)
	 * @param context
	 * @param partName
	 * @param drwaingType
	 * @return MapList
	 * @throws FrameworkException
	 * @author shajil
	 */
	public MapList getDocuments(Context context, String partName, String drwaingType)
			throws FrameworkException, Exception {
				Log.write("googPDFSTEPGenerator--enter getDocuments-");
		MapList cadDocuments = new MapList();
		if (UIUtil.isNotNullAndNotEmpty(partName)) {
			cadDocuments = DomainObject.findObjects(context, drwaingType, // Type
					partName, // name
					DomainObject.QUERY_WILDCARD, // revision
					DomainObject.QUERY_WILDCARD, // owner
					DomainObject.QUERY_WILDCARD, // Vault
					null, // Where
					false, // expand sub type
					busSelects);
		}
		Log.write("googPDFSTEPGenerator--exit getDocuments-");
		return cadDocuments;
	}

	/**
	 * This method is used to update all revised Parts and its Spec.
	 * @param context
	 * @param partName
	 * @param drwaingType
	 * @throws FrameworkException
	 * @throws IOException
	 * @author shajil
	 */
	public void updatePartRevisionObjects(Context context, String partName, String drwaingType)
			throws FrameworkException, IOException, Exception {
Log.write("googPDFSTEPGenerator--enter updatePartRevisionObjects-");
		short level = (short) 1;
		String type = type_VPMReference + "," + drwaingType;
		String relationshipName = RELATIONSHIP_PART_SPECIFICATION;
		MapList partList = DomainObject.findObjects(context, TYPE_PART, // Type
				partName, // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				null, // Where
				false, // expand sub type
				busSelects);
		Iterator itr = partList.iterator();
		while (itr.hasNext()) {
			Map partInfo = (Map) itr.next();
			String partId = (String) partInfo.get(SELECT_ID);
			DomainObject domPart = DomainObject.newInstance(context, partId);
			MapList specObjects = domPart.getRelatedObjects(context, relationshipName, type, busSelects,
					new StringList(SELECT_RELATIONSHIP_ID), false, true, level, "", "", 0);
			if (specObjects.size() > 0) {
				Log.write("googPDFSTEPGenerator--inside updatePartRevisionObjects-before-getDrawingInfo-");
				Map physicalProdInfo = getDrawingInfo(specObjects, type_VPMReference);
				String minorRevision = (String) physicalProdInfo.get("majorrevision");
				Log.write("googPDFSTEPGenerator--inside updatePartRevisionObjects-before-updatePartSpecObject-");
				updatePartSpecObject(context, specObjects, partName, drwaingType, minorRevision);
				Log.write("googPDFSTEPGenerator--inside updatePartRevisionObjects-after-updatePartSpecObject-");
			}
		}
		Log.write("googPDFSTEPGenerator--exit updatePartRevisionObjects-");
	}

	/**
	 * This method is used to update all revised Parts and its Spec.
	 * @param context
	 * @param specObjects
	 * @param partName
	 * @param drwaingType
	 * @param rev
	 * @throws FrameworkException
	 * @throws IOException
	 * @author shajil
	 */
	public void updatePartSpecObject(Context context, MapList specObjects, String partName, String drwaingType, String rev) throws FrameworkException, IOException, Exception {
		Log.write("googPDFSTEPGenerator--enter updatePartSpecObject-");
		Iterator itr = specObjects.iterator();
		while(itr.hasNext()) {
			Map specInfo = (Map)itr.next();
			String specType = (String)specInfo.get(SELECT_TYPE);
			String specName = (String)specInfo.get(SELECT_NAME);
			String specRev = (String)specInfo.get(SELECT_REVISION);
			String docRelId = (String)specInfo.get(SELECT_RELATIONSHIP_ID);
			if(specType.equals(drwaingType) && specName.equals(partName) && !specRev.equals(rev)) {
				try {
					Log.write("googPDFSTEPGenerator--inside updatePartSpecObject-before disconnect-");
				DomainRelationship.disconnect(context, docRelId);
				Log.write("googPDFSTEPGenerator--inside updatePartSpecObject-after disconnect-");
				}catch(Exception e) {
					Log.write("Failed to disconnect "+specType+" ("+partName+")");
				}
			}
		}	
Log.write("googPDFSTEPGenerator--exit updatePartSpecObject-");		
	}

	/**
	 * This method is used to check the file availability
	 * @param vpmRefInfo
	 * @param format
	 * @return
	 * @throws IOException
	 * @author shajil
	 */
	public boolean isFileExists(MapList vpmRefInfo, String format) throws IOException, Exception {
		Log.write("googPDFSTEPGenerator--enter isFileExists-");
		String path = null;
		String fileFormat = null;
		StringBuilder file = null;
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside isFileExists-format-"+format);
			path = dirPath;
			fileFormat = FILE_FORMAT_PDF;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside isFileExists-format-"+format);
			path = dirSTP;
			fileFormat = FILE_FORMAT_STP;
		}
		Iterator drawingItr = vpmRefInfo.iterator();
		while (drawingItr.hasNext()) {		
			file = new StringBuilder();
			Map drawingInfo = (Map) drawingItr.next();
			String title = ((String)drawingInfo.get("attribute[" + ATTR_VNAME + "]")).replace(" ", "_");
			file.append(title).append("_REV_").append(drawingInfo.get("majorrevision")).append(fileFormat);
			
			StringBuilder fileName = new StringBuilder(path);
			fileName.append(file);
			File dir = new File(fileName.toString());
			if (dir.exists()) {
				Log.write("googPDFSTEPGenerator--inside isFileExists-exit-return true-");
				return true;
			}else {
				Log.write("googPDFSTEPGenerator--inside isFileExists-checked in file (" + file.toString() + ") does not exist-");
				Log.write("Checked in file(" + file.toString() + ") does not exist");
			}
		}	
		Log.write("googPDFSTEPGenerator--inside isFileExists-exit-return false-");
		return false;
	}

	/**
	 * This method used to update Drawing Print/CAD Model objects.
	 * @param context
	 * @param strPartId
	 * @param cadDocuments
	 * @param drawingInfo
	 * @return Map
	 * @author shajil
	 * @throws Exception 
	 */
	public Map updateDrawingDetails(Context context, String strPartId, MapList cadDocuments, Map drawingInfo)
			throws Exception {
				Log.write("googPDFSTEPGenerator--enter updateDrawingDetails-");
		cadDocuments.sort(SELECT_REVISION, "ascending", "String");
		Iterator drawingItr = cadDocuments.iterator();
		boolean isRevContains = false;
		String rev = (String) drawingInfo.get("majorrevision");
		int intMajorRev = convertStringToAsciiInt(rev);
		String drawingRev = null;
		Map drawingDetails = null;
		while (drawingItr.hasNext()) {
			drawingDetails = (Map) drawingItr.next();
			drawingRev = (String) drawingDetails.get(SELECT_REVISION);
			int intDwgRev = convertStringToAsciiInt(drawingRev);
			if (rev.equals(drawingRev) || intDwgRev >= intMajorRev) {
				isRevContains = true;
				break;
			}
		}
								   
		String docRelId = null;
		String docId = (String) drawingDetails.get(SELECT_ID);
		if (!isRevContains) {
			if (!rev.equals(drawingRev)) {
				try {
					docRelId = (String) drawingDetails.get(PART_SPECIFICATION_REL_ID);
					Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-before reviseObject-");
					DomainObject revDoc = reviseObject(context, DomainObject.newInstance(context, docId), rev,
							drawingDetails);
							Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-after reviseObject-");
					drawingDetails = revDoc.getInfo(context, busSelects);
					drawingRev = (String) drawingDetails.get(SELECT_REVISION);
					docId = (String) drawingDetails.get(SELECT_ID);
				} catch (Exception e) {
					Log.write("Failed to revise the Specification object : "+docId);
					e.printStackTrace();
				}
			}
		} else {
			if (!drawingDetails.containsKey(PART_SPECIFICATION_REL_ID) && UIUtil.isNullOrEmpty(docRelId)) {
				Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-before connect 1-");
				DomainRelationship.connect(context, strPartId, RELATIONSHIP_PART_SPECIFICATION, docId, true);
				Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-after connect 1-");
			}
		}

		if (!drawingDetails.containsKey(PART_SPECIFICATION_REL_ID) && UIUtil.isNotNullAndNotEmpty(docId)) {
			Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-before connect 2-");
			DomainRelationship.connect(context, strPartId, RELATIONSHIP_PART_SPECIFICATION, docId, true);
			Log.write("googPDFSTEPGenerator--inside updateDrawingDetails-after connect 2-");
		}
		Log.write("googPDFSTEPGenerator--exit updateDrawingDetails-");									 
		return drawingDetails;
	}

	public static void main(String[] args) throws Exception {
		googPDFSTEPGenerator_mxJPO goog = new googPDFSTEPGenerator_mxJPO();
		goog.convertStringToAsciiInt("BB");
	}

	public int convertStringToAsciiInt(String text) throws Exception {
		Log.write("googPDFSTEPGenerator--enter convertStringToAsciiInt-");
		text = text.toUpperCase();
		// converting String to ASCII value in Java
		int intRev = 0;
		int i = 0;
		for (char c : text.toCharArray()) {
			i = i + 1;
			int tempCharInt = ((int) c - 64);
			if (i == 1 && text.length() > 1)
				tempCharInt = tempCharInt * 26;
			intRev = intRev + tempCharInt;
		}

		// Log.write("Text:"+text+" IntRev:"+intRev);
		Log.write("googPDFSTEPGenerator--exit convertStringToAsciiInt-");
		return intRev;
}

	/**
	 * This method is used to revise the Drawing Print/CAD Model if it is in Release state
	 * @param context
	 * @param domPart
	 * @param drawingDetails
	 * @return partSpecRevisedObj
	 * @throws Exception
	 * @author shajil
	 */
	public String reviseDrawingPrint(Context context, DomainObject domPart, Map drawingDetails) throws Exception {
		Log.write("googPDFSTEPGenerator--enter reviseDrawingPrint-");
		DomainObject partSpecRevisedObj = null;
		try {
			String drawingId = (String) drawingDetails.get(SELECT_ID);
			String latestRevId = (String) drawingDetails.get(SELECT_LAST_ID);
			String latestRevState = (String) drawingDetails.get("last.current");
			String relationshipId = (String) drawingDetails.get(DomainRelationship.SELECT_ID);
			if (!latestRevState.equals("Release")) {
				partSpecRevisedObj = DomainObject.newInstance(context, latestRevId);
			} else {
				DomainObject partSpecObj = DomainObject.newInstance(context, drawingId);
				Log.write("getRevision      :"+partSpecObj.getRevision(context));
				String strPartSpecRev =  partSpecObj.getRevision(context).toUpperCase();
				int intPartRev = Integer.parseInt(domPart.getRevision());
				int intPartSpecRev = convertStringToAsciiInt(strPartSpecRev);
				Log.write("strPartSpecRev:"+strPartSpecRev+" intPartRev:"+intPartRev+"  PartSpecwithminus:"+intPartSpecRev);
				if(intPartRev <= intPartSpecRev) {
					Log.write("Revising the specssssssssssssssssss");
					BusinessObject partSpecRevisedBusObj = null;
					partSpecRevisedBusObj = partSpecObj.reviseObject(context, false);
					partSpecRevisedObj = DomainObject.newInstance(context, partSpecRevisedBusObj);
				}
			}
			DomainRelationship.disconnect(context, relationshipId);
			DomainRelationship.connect(context, domPart, RELATIONSHIP_PART_SPECIFICATION, partSpecRevisedObj);			
		} catch (Exception e) {
			Log.write("Error occured during revising the object");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
		Log.write("googPDFSTEPGenerator--exit reviseDrawingPrint-");
		return partSpecRevisedObj.getId(context);
	}

	/**
	 * This method is used to get the Drawing object information
	 * @param allCADObjectsList
	 * @param drwaingType
	 * @return cadInfo
	 * @author shajil
	 */
	public Map getDrawingInfo(MapList allCADObjectsList ,String drwaingType) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getDrawingInfo-");
		Iterator itr = allCADObjectsList.iterator();
		while (itr.hasNext()) {
			Map cadInfo = (Map) itr.next();
			String type = (String) cadInfo.get(SELECT_TYPE);
			if (type.equals(drwaingType)) {
				Log.write("googPDFSTEPGenerator--exit getDrawingInfo-1-");
				return cadInfo;
			}
		}
		Log.write("googPDFSTEPGenerator--exit getDrawingInfo-2-");
		return null;
	}
	
	/**
	 * This method is used to get the Drawing object information
	 * @param allCADObjectsList
	 * @param drawingType
	 * @return drawingList
	 * @author shajil
	 */
	public MapList getDrawingInfoList(MapList allCADObjectsList, String drawingType) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getDrawingInfoList--");
		MapList drawingList = new MapList();
		Iterator itr = allCADObjectsList.iterator();
		String majorRev = getPhysicalProductMajorRevision(allCADObjectsList);
		while (itr.hasNext()) {
			Map cadInfo = (Map) itr.next();
			String type = (String) cadInfo.get(SELECT_TYPE);			
			if (type.equals(drawingType)) {
				if(drawingType.equals(type_Drawing)) {
				//	cadInfo.put("majorrevision", majorRev);
				}
				drawingList.add(cadInfo);
			}
		}
		Log.write("googPDFSTEPGenerator--exit getDrawingInfoList--");
		return drawingList;
	}

	public String getPhysicalProductMajorRevision(MapList allCADObjectsList) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getPhysicalProductMajorRevision--");
		Iterator itr = allCADObjectsList.iterator();
		while (itr.hasNext()) {
			Map cadInfo = (Map) itr.next();
			String type = (String) cadInfo.get(SELECT_TYPE);
			if(type.equals(type_VPMReference)) {
				Log.write("googPDFSTEPGenerator--exit getPhysicalProductMajorRevision-1-");
				return (String)cadInfo.get("majorrevision");
			}			
		}
		Log.write("googPDFSTEPGenerator--exit getPhysicalProductMajorRevision-2-");
		return null;
	}

	/**
	 * This method is used to checkin the PDF/STP file to the object
	 * @param context
	 * @param vpmRefInfo
	 * @param drawingId
	 * @param format
	 * @return isSuccess
	 * @throws Exception
	 * @author shajil
	 */
	public boolean checkinBus(Context context, Map vpmRefInfo, String drawingId, String format) throws Exception {
Log.write("googPDFSTEPGenerator--enter checkinBus--");
		String args[] = new String[0];
		String path = null;
		String fileFormat = null;
		boolean isSuccess = true;
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside checkinBus-format-"+format);
			fileFormat = FILE_FORMAT_PDF;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside checkinBus-format-"+format);
			fileFormat = FILE_FORMAT_STP;
			path = dirSTP;
		}
		String current = (String) vpmRefInfo.get(SELECT_CURRENT);
		String description = (String) vpmRefInfo.get("attribute[PLMEntity.V_description]");
		emxCommonDocument_mxJPO doc = new emxCommonDocument_mxJPO(context, args);
		DomainObject docObj = DomainObject.newInstance(context, drawingId);
		StringList objectSelects = new StringList(SELECT_ID);
		objectSelects.addElement(SELECT_ATTRIBUTE_TITLE);

		StringBuilder fileName = new StringBuilder();
		String title = ((String) vpmRefInfo.get("attribute[" + ATTR_VNAME + "]")).replace(" ", "_");
		fileName.append(title).append("_REV_").append(vpmRefInfo.get("majorrevision")).append(fileFormat);

		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside checkinBus-before initiateWatermarkPDF-fileName-"+fileName.toString());
			initiateWatermarkPDF(path, fileName.toString(), current);
			Log.write("googPDFSTEPGenerator--inside checkinBus-after initiateWatermarkPDF--");
		}
		MapList documentdetails = docObj.getRelatedObjects(context, CommonDocument.RELATIONSHIP_ACTIVE_VERSION,
				CommonDocument.TYPE_DOCUMENTS, objectSelects, null, false, true, (short) 1,
				"attribute[Title]=='" + fileName.toString() + "'", null, 0);
		Iterator itr = documentdetails.iterator();
		BusinessObject document = null;
		if (documentdetails.size() > 0) {
			Map docInfo = (Map) documentdetails.get(0);
			String docId = (String) docInfo.get(SELECT_ID);
			document = new BusinessObject(docId);
		} else {
			document = new BusinessObject(drawingId);
		}

		String params[] = new String[8];
		params[0] = drawingId;
		params[1] = path;
		params[2] = fileName.toString();
		params[3] = "generic";
		params[4] = "STORE";
		params[5] = "true";
		params[6] = "server";
		params[7] = description;

		try {
			Log.write("googPDFSTEPGenerator--inside checkinBus-before doc.checkinBus-");
			document.open(context);
			if (!document.isLocked(context)) {
				document.lock(context);
			}
			document.close(context);
			doc.checkinBus(context, params);
			Log.write("googPDFSTEPGenerator--inside checkinBus-after doc.checkinBus-");
		} catch (Exception e) {
			Log.write("googPDFSTEPGenerator--inside checkinBus-Checkin bus failed for " + fileName.toString());
			isSuccess = false;
			e.printStackTrace();
			Log.write("Checkin bus failed for " + fileName.toString());
			Log.write(e.getMessage());
		} finally {
			document.unlock(context);
			if (isSuccess) {
				File fileToDelete = new File(path + fileName.toString());
				fileToDelete.delete();
				Log.write("googPDFSTEPGenerator--inside checkinBus-after delete-");
			}
		}
Log.write("googPDFSTEPGenerator--exit checkinBus--");
		return isSuccess;
	}

	/**
	 * This method is used to create and connect Drawing Print/CAD Model object with Part
	 * @param context
	 * @param domPart
	 * @param drawingList
	 * @param excludedList
	 * @param format
	 * @return isSuccess
	 * @throws Exception
	 * @author shajil
	 */
	public boolean createAndConnectDrawingPrint(Context context, DomainObject domPart, MapList drawingList,
			ArrayList<String> excludedList, String format) throws Exception {
				Log.write("googPDFSTEPGenerator--enter createAndConnectDrawingPrint--");
		String fileFormat = "";
		String policy = "";
		String type = "";
		String path = "";
		StringList objectSelect = new StringList();
		objectSelect.addElement(SELECT_ID);
		objectSelect.addElement(SELECT_NAME);
		objectSelect.addElement(SELECT_OWNER);
		objectSelect.addElement(SELECT_ORIGINATOR);
		objectSelect.addElement(PROJECT);
		objectSelect.addElement(ORGANIZATION);
		
		Map partInfo = domPart.getInfo(context, objectSelect);
		String partOwner = (String)partInfo.get(SELECT_OWNER);
		String partOriginaror = (String)partInfo.get(SELECT_ORIGINATOR);
		String partId = (String)partInfo.get(SELECT_ID);
		String partName = (String)partInfo.get(SELECT_NAME);
		boolean isSuccess = true;
		boolean fileExists = false;
		Map<String, String> attrDetails = new HashMap<String, String>();
		attrDetails.put(ATTR_TITLE, partName);
		attrDetails.put(ATTRIBUTE_ORIGINATOR, partOriginaror);
		
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-format-"+format);
			policy = policy_DrawingPrint;
			type = TYPE_DRAWINGPRINT;
			fileFormat = FILE_FORMAT_PDF;
			path = dirPath;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-format-"+format);
			//Modified by XPLORIA to use googCAD Model insted of CAD Model
			//type = TYPE_CAD_MODEL;
			type = type_googCADModel;
			policy = policy_CADModel;
			fileFormat = FILE_FORMAT_STP;
			path = dirSTP;
		}
		boolean fileExist = isFileExists(drawingList,format);
		Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-fileExist-"+fileExist);
		if (fileExist) {
			DomainObject doCon = DomainObject.newInstance(context);
			
			try {
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-before createobject-");
				doCon.createObject(context, type, partName, "A", policy, VAULT_ESERVICE_PRODUCTION);
				String project = (String) partInfo.get(PROJECT);
				String organization = (String) partInfo.get(ORGANIZATION);
				MqlUtil.mqlCommand(context, "trigger off");
				doCon.setOwner(context, partOwner);
				MqlUtil.mqlCommand(context, "trigger on");
				doCon.setPrimaryOwnership(context, project, organization);
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-after createobject-");
			} catch (Exception e) {
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-error in createobject-");
				e.printStackTrace();
			}
			boolean isExecuted = false;
			Iterator drawigItr = drawingList.iterator();
			while (drawigItr.hasNext()) {
				Map vpmRefInfo = (Map) drawigItr.next();
				StringBuilder file = new StringBuilder();
				String productRev = (String) vpmRefInfo.get(SELECT_REVISION);
				String productMajorRev = (String) vpmRefInfo.get("majorrevision");
				StringBuilder drawingName = new StringBuilder(
						((String) vpmRefInfo.get("attribute[" + ATTR_VNAME + "]")).replace(" ", "_"));
				String productMajorRevision = (String) vpmRefInfo.get("majorrevision");
				file.append(drawingName).append("_REV_").append(productMajorRev).append(fileFormat);
				drawingName.append("_").append(productRev);
				try {
					StringBuilder fileName = new StringBuilder(path);
					fileName.append(file);
					File dir = new File(fileName.toString());
					
					if (dir.exists()) {
						fileExists = true;
						try {
							if (!productMajorRevision.equalsIgnoreCase("A") && !isExecuted) {
								isExecuted = true;
								Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-before reviseObject-");
								doCon = reviseObject(context, doCon, productMajorRevision, partInfo);
								Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-after reviseObject-");
								
							}
							String documentId = doCon.getObjectId();
							Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-before checkinBus-");
							isSuccess = checkinBus(context, vpmRefInfo, documentId, format);
							Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-after checkinBus-isSuccess-"+isSuccess);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (!isSuccess) {
							excludedList.add(partId);
						}
					} else {
						excludedList.add(partId);
						Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-Checked in file(" + file + ") does not exist");
						Log.write("Checked in file(" + file + ") does not exist");
					}
				} catch (Exception e) {
					isSuccess = false;
					excludedList.add(partId);
					Log.write("Error occured during " + policy + " creation");
					Log.write(e.getMessage());
				}
			}
			if (fileExists && isSuccess) {
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-before connect");
				DomainRelationship.connect(context, domPart, RELATIONSHIP_PART_SPECIFICATION, doCon);
				doCon.setAttributeValues(context, attrDetails);
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-after connect");
			} else {
				doCon.deleteObject(context);
				Log.write("googPDFSTEPGenerator--inside createAndConnectDrawingPrint-after delete");
			}
		}else {
			excludedList.add(partId);
		}
		Log.write("googPDFSTEPGenerator--exit createAndConnectDrawingPrint-");
		return isSuccess;
	}

	public DomainObject reviseObject(Context context, DomainObject doCon, String productMajorRevision, Map partInfo) throws FrameworkException, IOException, Exception {
		Log.write("googPDFSTEPGenerator--enter reviseObject-");
		BusinessObject reviseDoc = null;
		String partOwner = (String)partInfo.get(SELECT_OWNER);
		try {
			Log.write("googPDFSTEPGenerator--inside reviseObject-before doCon.reviseObject-");
			reviseDoc =   doCon.reviseObject(context, false);
			doCon = DomainObject.newInstance(context, reviseDoc.getObjectId());
			MqlUtil.mqlCommand(context, "trigger $1", true, "off");
			doCon.setOwner(context, partOwner);
			MqlUtil.mqlCommand(context, "trigger $1", true, "on");
			doCon.setPrimaryOwnership(context, (String) partInfo.get(PROJECT),
					(String) partInfo.get(ORGANIZATION));
			String rev = reviseDoc.getRevision();
			if(!rev.equals(productMajorRevision)) {
				doCon = reviseObject(context,doCon,productMajorRevision,partInfo);
			}
			Log.write("googPDFSTEPGenerator--inside reviseObject-after doCon.reviseObject-");
			
		}catch(Exception e) {
			Log.write("googPDFSTEPGenerator--inside reviseObject-Error occured during revision of Document(CAD Model/Drawing Print)-");
			Log.write("Error occured during revision of Document(CAD Model/Drawing Print)");
		}
		Log.write("googPDFSTEPGenerator--exit reviseObject-");
		return doCon;
	}

	/**
	 * This method is used to validate the Part for file generation
	 * We can exclude this logic as we are validating before JOB creation
	 * @param context
	 * @param cadObjectsList
	 * @param format
	 * @return hasCADDwg
	 * @throws Exception
	 */
	public boolean validateCADAndDrawingExists(Context context, MapList cadObjectsList, String format)
			throws Exception {
				Log.write("googPDFSTEPGenerator--enter validateCADAndDrawingExists-");
		Map objMap;
		boolean hasDrawing = false;
		boolean hasCAD = false;
		boolean hasCADDwg = false;
		String type = "";
		if (UIUtil.isNotNullAndNotEmpty(format) && PDF_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside validateCADAndDrawingExists-format-"+format);
			type = type_Drawing;
		} else if (UIUtil.isNotNullAndNotEmpty(format) && STEP_GENERATOR.equalsIgnoreCase(format)) {
			Log.write("googPDFSTEPGenerator--inside validateCADAndDrawingExists-format-"+format);
			hasDrawing = true;
		}
		for (int cad = 0; cad < cadObjectsList.size(); cad++) {
			objMap = (Map) cadObjectsList.get(cad);
			if (type.equals(objMap.get(DomainConstants.SELECT_TYPE).toString())) {
				hasDrawing = true;
			} else if (type_VPMReference.equals(objMap.get(DomainConstants.SELECT_TYPE).toString())) {
				hasCAD = true;
			}
			if (hasDrawing && hasCAD)
				hasCADDwg = true;
		}
		Log.write("googPDFSTEPGenerator--exit validateCADAndDrawingExists-hasCADDwg-"+hasCADDwg);
		return hasCADDwg;
	}

	/**
	 * Check if BatchStarter already is there in the tasklist 
	 * @param context
	 * @return
	 * @throws MatrixException
	 * @throws IOException
	 */
	public int checkBatchStarter(Context context) throws MatrixException, IOException, Exception {
		Log.write("googPDFSTEPGenerator--enter checkBatchStarter--");
		int flagBatchStarter = 0;
		String line;
		String catBatchStarter = EnoviaResourceBundle.getProperty(context, "googDOGStringResource", context.getLocale(),
				"goog_PDFGenerator.catBatchStarter");
		Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
			if (line.contains(catBatchStarter)) {
				flagBatchStarter = 1;
				break;
			}
		}

		input.close();
		Log.write("googPDFSTEPGenerator--exit checkBatchStarter--");
		return flagBatchStarter;
	}

	/**
	 * Get Page Map.
	 * @param context
	 * @param pageName
	 * @return pageMap
	 * @throws MatrixException
	 * @throws IOException
	 */
	public HashMap<String, String> getPageMap(Context context, String pageName) throws MatrixException, IOException, Exception {
		Log.write("googPDFSTEPGenerator--enter getPageMap--");
		HashMap<String, String> pageMap = new HashMap<String, String>();
		InputStream page = Page.getContentsAsStream(context, pageName);
		Properties properties = new Properties();
		properties.load(page);
		if (properties.keySet() != null) {
			Iterator<Object> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				pageMap.put(key, properties.getProperty(key));
			}
		}
		Log.write("googPDFSTEPGenerator--exit pageMap--");
		return pageMap;
	}

	/**
	 * This method is used to perform both PDF/STP file generation using seperate Task scheduler
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author shajil
	 */
	public void getJobDetails(Context context, String[] args) throws MatrixException, IOException, Exception {
Log.write("googPDFSTEPGenerator--enter getJobDetails--");
		if (checkBatchStarter(context) == 0) {
			String jobOID = DomainConstants.EMPTY_STRING;
			String partIds = DomainConstants.EMPTY_STRING;
			String jobTitle = null;
			String format = args[0];
			Map<String, String> jobDetails = new HashMap<String, String>();
			ArrayList<String> excludedList = new ArrayList<String>();
			try {
				StringList objectSelects = new StringList(DomainObject.SELECT_ID);
				objectSelects.add("attribute[Program Arguments]");
				objectSelects.add("attribute[Title]");
				String objectWhere = "(attribute[Title]==" + format + ") && current==Created";
				MapList jobList = DomainObject.findObjects(context, "JOB", // Type
						DomainObject.QUERY_WILDCARD, // name
						DomainObject.QUERY_WILDCARD, // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						objectWhere, // Where
						false, // expand sub type
						objectSelects);
				StringBuilder consolidatedPartIds = new StringBuilder();
				if (jobList.size() > 0) {
					Iterator<Map> it = jobList.iterator();
					while (it.hasNext()) {
						Map mpPartInfo = (Map) it.next();
						jobOID = (String) mpPartInfo.get(DomainObject.SELECT_ID);
						partIds = (String) mpPartInfo.get("attribute[Program Arguments]");
						jobTitle = (String) mpPartInfo.get("attribute[Title]");
						if (UIUtil.isNotNullAndNotEmpty(jobTitle) && format.equalsIgnoreCase(jobTitle)) {
							jobDetails.put(jobOID, getUniqueIds(partIds).toString());
							consolidatedPartIds.append(partIds).append(",");
						}
					}
				}
				if (!jobDetails.isEmpty()) {
					consolidatedPartIds = getUniqueIds(consolidatedPartIds.toString());
					excludedList = promotejobs(context, jobDetails, consolidatedPartIds.toString(), format);// "PDFGenerator"
					String[] selectedPartIds = consolidatedPartIds.toString().split(",");
					StringList objList = new StringList(SELECT_NAME);
					objList.addElement(SELECT_ID);
					MapList selectedPartInfo = DomainObject.getInfo(context, selectedPartIds, objList);
					updateJOBStatus(context, jobDetails, excludedList, selectedPartInfo);
				}
				Log.write("googPDFSTEPGenerator--exit getJobDetails--");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			Log.write("googPDFSTEPGenerator--exit getJobDetails-2-");
			Log.write("CATIA execution still in progress");
		}
	}
	
	/**
	 * This method is used to perform both PDF/STP file generation using single Task scheduler
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author shajil
	 */
	public void executeJOBs(Context context, String[] args) throws IOException, MatrixException, Exception {
		Log.write("googPDFSTEPGenerator--enter executeJOBs--");

		if (checkBatchStarter(context) == 0) {
			String jobOID = DomainConstants.EMPTY_STRING;
			String partIds = DomainConstants.EMPTY_STRING;
			String jobTitle = null;
			Map<String, String> pdfJobDetails = new HashMap<String, String>();
			Map<String, String> stpJobDetails = new HashMap<String, String>();
			ArrayList<String> excludedList = null;
			Map<String, String> PDFGeneratorJobDetails = new HashMap<String, String>();
			Map<String, String> STEPGeneratorJobDetails = new HashMap<String, String>();
			try {
				StringList objectSelects = new StringList(DomainObject.SELECT_ID);
				objectSelects.add("attribute[Program Arguments]");
				objectSelects.add("attribute[Title]");
				String objectWhere = "(attribute[Title]==PDFGenerator || attribute[Title]==STEPGenerator) && current==Created";
				MapList jobList = DomainObject.findObjects(context, "JOB", // Type
						DomainObject.QUERY_WILDCARD, // name
						DomainObject.QUERY_WILDCARD, // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						objectWhere, // Where
						false, // expand sub type
						objectSelects);
				StringBuilder PDFGeneratorIds = new StringBuilder();
				StringBuilder STEPGeneratorIds = new StringBuilder();
				if (jobList.size() > 0) {
					Iterator<Map> it = jobList.iterator();
					while (it.hasNext()) {
						Map mpPartInfo = (Map) it.next();
						jobOID = (String) mpPartInfo.get(DomainObject.SELECT_ID);
						partIds = (String) mpPartInfo.get("attribute[Program Arguments]");
						jobTitle = (String) mpPartInfo.get("attribute[Title]");
						if (UIUtil.isNotNullAndNotEmpty(jobTitle) && "PDFGenerator".equalsIgnoreCase(jobTitle)) {
							PDFGeneratorJobDetails.put(jobOID, getUniqueIds(partIds).toString());
							PDFGeneratorIds.append(partIds).append(",");
							pdfJobDetails.put(jobOID, getUniqueIds(partIds).toString());
						}
						if (UIUtil.isNotNullAndNotEmpty(jobTitle) && "STEPGenerator".equalsIgnoreCase(jobTitle)) {
							STEPGeneratorJobDetails.put(jobOID, getUniqueIds(partIds).toString());
							STEPGeneratorIds.append(partIds).append(",");
							stpJobDetails.put(jobOID, getUniqueIds(partIds).toString());
						}
					}
				}
				if (!PDFGeneratorJobDetails.isEmpty()) {
					excludedList = new ArrayList<String>();
					excludedList = promotejobs(context, PDFGeneratorJobDetails, PDFGeneratorIds.toString(),
							"PDFGenerator");
					String[] selectedPartIds = PDFGeneratorIds.toString().split(",");
					StringList objList = new StringList(SELECT_NAME);
					objList.addElement(SELECT_ID);
					MapList selectedPartInfo = DomainObject.getInfo(context, selectedPartIds, objList);
					updateJOBStatus(context, pdfJobDetails, excludedList, selectedPartInfo);
				}
				if (!STEPGeneratorJobDetails.isEmpty()) {
					excludedList = new ArrayList<String>();
					excludedList = promotejobs(context, STEPGeneratorJobDetails, STEPGeneratorIds.toString(),
							"STEPGenerator");
					String[] selectedPartIds = STEPGeneratorIds.toString().split(",");
					StringList objList = new StringList(SELECT_NAME);
					objList.addElement(SELECT_ID);
					MapList selectedPartInfo = DomainObject.getInfo(context, selectedPartIds, objList);
					updateJOBStatus(context, stpJobDetails, excludedList, selectedPartInfo);
				}
				Log.write("googPDFSTEPGenerator--exit executeJOBs--");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.write("CATIA execution still in progress");
			Log.write("CATIA execution still in progress");
		}
	}

	/**
	 * This method is used to update the JOB
	 * @param context
	 * @param jobDetails
	 * @param excludedList
	 * @param selectedPartInfo
	 * @throws Exception
	 * @author shajil
	 */
	public void updateJOBStatus(Context context, Map<String, String> jobDetails, ArrayList<String> excludedList,
			MapList selectedPartInfo) throws Exception {
				Log.write("googPDFSTEPGenerator--enter updateJOBStatus--");
		Set jobIds = jobDetails.keySet();
		Iterator keyItr = jobIds.iterator();
		Map<String, String> partInfo = getObjectDetails(selectedPartInfo);
		StringList objectSelect = new StringList(SELECT_ID);
		objectSelect.addElement(SELECT_NAME);
		objectSelect.addElement(SELECT_OWNER);
		MapList mailDetails = new MapList();
		while (keyItr.hasNext()) {
			String jobId = (String) keyItr.next();
			DomainObject jobObject = new DomainObject(jobId);
			Map jobInfo = jobObject.getInfo(context, objectSelect);
			String jobName = (String)jobInfo.get(SELECT_NAME);
			String jobOwner = (String)jobInfo.get(SELECT_OWNER);
			String partIds[] = jobDetails.get(jobId).split(",");
			StringBuilder jobFailureDescription = new StringBuilder("<b>Failed Part Info : </b>\n");
			StringBuilder failedParts = new StringBuilder("");
			StringBuilder jobSuccessDescription = new StringBuilder("<b>Success Part info : </b>\n");
			StringBuilder jobStatusDescription = new StringBuilder();
			StringBuilder mailSubject = new StringBuilder("Batch job (");
			mailSubject.append(jobName).append(" ) ").append("execution");
			//Batch job Execution Status
			boolean isFailed = false;
			boolean isSuccess = false;
			StringBuilder failedIds = new StringBuilder();
			for (String partId : partIds) {
				String partName = partInfo.get(partId);
				if (excludedList.contains(partId)) {
					jobFailureDescription.append(partName);
					failedParts.append(partName).append(",");
					jobFailureDescription.append("\n");
					failedIds.append(partId);
					failedIds.append("%0A");
					isFailed = true;
				} else {
					jobSuccessDescription.append(partName);
					jobSuccessDescription.append("\n");
					isSuccess = true;
				}
			}
			Map<String, String> attrValue = new HashMap<String, String>();
			if (isSuccess) {
				jobStatusDescription.append(jobSuccessDescription.toString());
				jobStatusDescription.append("\n");
				attrValue.put("Completion Status", "Succeeded");
				
			}
			if (isFailed) {
				jobStatusDescription.append(jobFailureDescription.toString());
				jobStatusDescription.append("\n");
				attrValue.put("Completion Status", "Failed");
				attrValue.put("Program Arguments", failedIds.toString());
				Log.write("Failed Parts : "+failedParts.toString());
			}
			
			attrValue.put("Error Message", jobStatusDescription.toString());
			jobObject.setAttributeValues(context, attrValue);
			Map<String,String> mailInfo = new HashMap<String,String>();
			try {
				MqlUtil.mqlCommand(context, "trigger off");
				jobObject.setState(context, "Completed");
				MqlUtil.mqlCommand(context, "trigger on");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(isSuccess && !isFailed) {
				mailSubject.append(" Success");
				mailInfo.put("Email.ccList",jobOwner);
			}else {
				mailSubject.append(" failed");
				mailInfo.put("Email.ccList",pageMap.get("Email.ccList"));
			}
			mailInfo.put("Email.toList",jobOwner);
			mailInfo.put("Email.subject",mailSubject.toString());
			mailInfo.put("Email.Body",jobStatusDescription.toString());				
			mailDetails.add(mailInfo);			
		}
		sendMailNotification(context,mailDetails);
		Log.write("googPDFSTEPGenerator--exit updateJOBStatus--");
	}

	/**
	 * This method is used to prepare Part information
	 * @param selectedPartInfo
	 * @return partDetails
	 * @author shajil
	 */
	public Map<String, String> getObjectDetails(MapList selectedPartInfo) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getObjectDetails--");
		Iterator partItr = selectedPartInfo.iterator();
		Map<String, String> partDetails = new HashMap<String, String>();
		while (partItr.hasNext()) {
			Map partInfo = (Map) partItr.next();
			String id = (String) partInfo.get(SELECT_ID);
			String name = (String) partInfo.get(SELECT_NAME);
			partDetails.put(id, name);
		}
		Log.write("googPDFSTEPGenerator--exit getObjectDetails--");
		return partDetails;
	}

	/**
	 * This method is used to create a unique part list to avoid generating PDF/STEP
	 * multiple times if request is raised through different Jobs
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Shajil
	 */
	public StringBuilder getUniqueIds(String partIds) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getUniqueIds--");
		partIds = partIds.replaceAll("%0A", ",");
		StringList slPartID = FrameworkUtil.split(partIds, ",");
		StringBuilder sb = new StringBuilder();
		String strObjectId = DomainConstants.EMPTY_STRING;
		StringList uniqueList = new StringList();
		for (int i = 0; i < slPartID.size(); i++) {
			strObjectId = (String) slPartID.get(i);
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				if (!uniqueList.contains(strObjectId)) {
					uniqueList.add(strObjectId);
					sb.append(strObjectId);
					if (i != slPartID.size() - 1) {
						sb.append(",");
					}
				}
			}
		}
		Log.write("googPDFSTEPGenerator--exit getUniqueIds--");
		return sb;
	}

	/**
	 * This method will pass the unique parts list form Jobs created for PDF & STEP
	 * also complete the Job & update its attributes
	 * @param context
	 * @param args
	 * @return excludedList
	 * @throws Exception
	 * @author Shajil
	 */
	public ArrayList<String> promotejobs(Context context, Map jobDetails, String partIds, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter promotejobs--");
		HashMap<String, String> objectIdDetails = new HashMap();
		objectIdDetails.put("ids", partIds);
		ArrayList<String> excludedList = null;
		try {
			String objectIds[] = partIds.split(",");
			if (UIUtil.isNotNullAndNotEmpty(format)) {
				excludedList = initiatePDFGeneration(context, objectIds, format);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.write("googPDFSTEPGenerator--exit promotejobs--");
		return excludedList;
	}

	/**
	 * This method is used to create background job
	 * @param context
	 * @param args
	 * @return resultData
	 * @throws Exception
	 * @author shajil
	 */
	public Map createBackGroundProcess(Context context, String[] args) throws Exception {
		Log.write("googPDFSTEPGenerator--enter createBackGroundProcess--");
		HashMap<String, String> objectIdDetails = JPO.unpackArgs(args);
		
		String objectId = null;
		String selectIds = null;
		String outputFormat = null;
		Map resultData = new HashMap();
		String alertMessage = null;
		StringList formatList = new StringList(PDF_GENERATOR);
		formatList.addElement(STEP_GENERATOR);
		try {
			selectIds = (String) objectIdDetails.get("ids");
			outputFormat = (String) objectIdDetails.get("OutputFormat");
			StringList slobjectIds = FrameworkUtil.split(selectIds, ",");
			String[] objArray = new String[slobjectIds.size()];
			for (int i = 0; i < slobjectIds.size(); i++) {
				objArray[i] = (String) slobjectIds.get(i);
			}
			if(outputFormat.equals(PDF_GENERATOR) || outputFormat.equals(STEP_GENERATOR)) {
				validateAndCreateJOB(context, outputFormat, objArray,resultData);
			}else {
				for(int count=0;count<formatList.size();count++) {
					outputFormat = (String)formatList.get(count);
					
					validateAndCreateJOB(context, outputFormat, objArray,resultData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.write("googPDFSTEPGenerator--exit createBackGroundProcess--");
		return resultData;
	}

	/**
	 * This method is used to validate and create the Job
	 * @param context
	 * @param outputFormat
	 * @param objArray
	 * @param resultData
	 * @return Map
	 * @author shajil
	 * @throws MatrixException 
	 */
	public Map validateAndCreateJOB(Context context, String outputFormat, String[] objArray,Map resultData)
			throws MatrixException, Exception {
				Log.write("googPDFSTEPGenerator--enter validateAndCreateJOB--");
		Job job = null;
		String alertMessage;
		String keyLabelId = outputFormat+"JobId";
		String keyLabelAlertMessage = outputFormat+"AlertMessage";
		validatePart(context, objArray, outputFormat,resultData);
		String[] selObjs = (String[]) resultData.get("SelectedIds");
		if (selObjs.length > 0) {
			job = new Job("googPDFGeneratorJPO", "createPDFgenerator", selObjs, true);
			job.setTitle(outputFormat);
			job.create(context);
			job.setAllowreexecute("No");
			job.setStartDate(context);
			//job.setContextObject(objectId);
			alertMessage = "A background job" + job.getName()
					+ " is created for generating Files and it will execute in batch as per schedule";
			resultData.put(keyLabelId, job.getName());
		} else {
			alertMessage = "No object to perform background operation";
		}
		resultData.put(keyLabelAlertMessage, alertMessage);
		Log.write("googPDFSTEPGenerator--exit validateAndCreateJOB--");
		return resultData;
	}

	/**
	 * This method is used to validate the selected Part from the UI
	 * @param context
	 * @param objArray
	 * @param outputFormat
	 * @return resultData
	 * @author shajil
	 * @throws MatrixException 
	 */
	public Map validatePart(Context context, String[] objArray, String outputFormat,Map<String, Object> resultData) throws MatrixException, Exception {
		Log.write("googPDFSTEPGenerator--enter validatePart--");
		StringList objSelect = new StringList(SELECT_NAME);
		objSelect.addElement(SELECT_CURRENT);
		objSelect.addElement(SELECT_ID);
		objSelect.addElement(SELECT_TYPE);
		objSelect.addElement(SELECT_REVISION);
		objSelect.addElement(
				"from[Part Specification|to.type==VPMReference].to.attribute[V_Name]");
		objSelect.addElement(
				"from[Part Specification|to.type==VPMReference].to.majorrevision");
		String statement = null;
		String statementForFileLocked = "from[Part Specification].to.from[Active Version].to.locked";
		String type = type_DrawingPrint;
		if (outputFormat.equals(PDF_GENERATOR)) {
			objSelect.addElement(
					"from[Part Specification|to.type==VPMReference].to.from[VPMRepInstance|to.type==Drawing]");
			objSelect.addElement(
					"from[Part Specification|to.type=='"+type_DrawingPrint+"'].to.from[Active Version].to.locked");
			statement = "from[Part Specification].to.from[VPMRepInstance]";
		} else if (outputFormat.equals(STEP_GENERATOR)) {
			objSelect.addElement("from[Part Specification|to.type==VPMReference]");
			objSelect.addElement(
					"from[Part Specification|to.type=='"+type_CADModel+"'].to.from[Active Version].to.locked");
			statement = "from[Part Specification]";
			type = type_CADModel;
		}
		objSelect.addElement(SELECT_CURRENT);
		DomainObject.newInstance(context);
		MapList dataList = DomainObject.getInfo(context, objArray, objSelect);
		Iterator itr = dataList.iterator();
		ArrayList<String> objectToProcess = new ArrayList<String>();
		while (itr.hasNext()) {
			Map<String, String> dataInfo = (Map<String, String>) itr.next();
			String state = (String) dataInfo.get(SELECT_CURRENT);
			String name = (String) dataInfo.get(SELECT_NAME);
			String id = (String) dataInfo.get(SELECT_ID);
			String majorRev = (String) dataInfo.get("from[Part Specification].to.majorrevision");
			boolean isDrwingAvailable = BooleanUtils.toBoolean((String) dataInfo.get(statement));
			boolean isFileLocked = false;
			if(dataInfo.containsKey(statementForFileLocked)) {		
				isFileLocked = BooleanUtils.toBoolean((String) dataInfo.get(statementForFileLocked));
			}			
			if (UIUtil.isNotNullAndNotEmpty(name) && name.contains("TMP")) {
				dataInfo.put("Message",
						"TMP Parts cannot be processed");
				dataInfo.put("Status", "Failure");
			}else if (!STATE_PART_PRELIMINARY.equals(state) && !STATE_PART_REVIEW.equals(state)) {
				dataInfo.put("Message",
						"Part should be in " + STATE_PART_PRELIMINARY + " or " + STATE_PART_REVIEW + " state");
				dataInfo.put("Status", "Failure");
			} else if (!isDrwingAvailable) {
				dataInfo.put("Message", "Part should contain Drawing object to perform PDF generation");
				dataInfo.put("Status", "Failure");
			} else if (isFileLocked) {
				dataInfo.put("Message", "Lock the "+type+" files for file generation");
				dataInfo.put("Status", "Failure");
			}else {
				boolean hasReleasedDocument = isDocumentReleased(context,type,name,majorRev);
				if(hasReleasedDocument) {
					dataInfo.put("Message", type+" is in Release state and hence the file cannot be uploaded. Please demote "+type+" and reinitiate the Job");
					dataInfo.put("Status", "Failure");
				}else {
					dataInfo.put("Status", "Success");
					dataInfo.put("Message", "File Generation in progress");
					objectToProcess.add(id);
				}
			}
		}
		String[] objects = new String[objectToProcess.size()];
		for (int i = 0; i < objectToProcess.size(); i++) {
			objects[i] = objectToProcess.get(i);
		}
		resultData.put("SelectedIds", objects);
		resultData.put(outputFormat, dataList);
		Log.write("googPDFSTEPGenerator--exit validatePart--");
		return resultData;
	}

	public boolean isDocumentReleased(Context context, String type, String name, String majorRev) throws FrameworkException, MatrixException, Exception {
		Log.write("googPDFSTEPGenerator--enter isDocumentReleased--");
		StringList objectList = new StringList(SELECT_ID);
		objectList.addElement(SELECT_CURRENT);
		MapList resultList = DomainObject.findObjects(context, type, name,
				majorRev, QUERY_WILDCARD, QUERY_WILDCARD, null, false,
				objectList);
		Iterator itr = resultList.iterator();
		boolean isReleased = false;
		while(itr.hasNext()) {
			Map docInfo = (Map)itr.next();
			String state = (String)docInfo.get(SELECT_CURRENT);
			if(state.equals("Release")) {
			isReleased = true;
			break;
			}
			
		}
		Log.write("googPDFSTEPGenerator--exit isDocumentReleased--");
		return isReleased;
	}

	/**
	 * This method is used for JOB filter(Inprogress and Failed) in UI
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public MapList getCurrentBackgroundJobs(Context context, String[] args) throws Exception {
		return getBackgroundJobs(context,"Current");
	}
	
	/**
	 * This method is used for JOB filter(Completed) in UI
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public MapList getCompletedBackgroundJobs(Context context, String[] args) throws Exception {
		return getBackgroundJobs(context,"Completed");
	}
	
	/**
	 * This method is used to display all JOBs in UI
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public MapList getAllBackgroundJobs(Context context, String[] args) throws Exception {
		return getBackgroundJobs(context,"");
	}
	
	/**
	 * This method is used for JOB filter in UI
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public MapList getBackgroundJobs(Context context, String selectedState) throws Exception {
		Log.write("googPDFSTEPGenerator--enter getBackgroundJobs--");
		StringBuilder whereExp = new StringBuilder();
		 if(selectedState.equalsIgnoreCase("Current"))
	        {
	        	whereExp.append(" && ( ((");
	            whereExp.append("attribute[");
	            whereExp.append("Completion Status");
	            whereExp.append("]!='Succeeded')");
	            whereExp.append(") && (");
	            whereExp.append("current==" + Job.STATE_JOB_CREATED +" || current==" + Job.STATE_JOB_COMPLETED +"))");
	        }else if(selectedState.equalsIgnoreCase("Completed")) {
	        	whereExp.append(" && ( ((");
	            whereExp.append("attribute[");
	            whereExp.append("Completion Status");
	            whereExp.append("]=='Succeeded')");
	            whereExp.append(") && (");
	            whereExp.append("current==" + Job.STATE_JOB_COMPLETED +"))");
	        }
		String user = context.getUser();
		StringList objectSelects = new StringList();
		String role_PLMSupportAdmin = PropertyUtil.getSchemaProperty(context, "role_googPLMSupportAdmin");
		if (context.isAssigned(role_PLMSupportAdmin)) {
			user = "*";
		}
		String typeJob = PropertyUtil.getSchemaProperty(context, "type_Job");
		String attrCompletionStatus = PropertyUtil.getSchemaProperty(context, "attribute_CompletionStatus");
		String attrAbortRequested = PropertyUtil.getSchemaProperty(context, "attribute_AbortRequested");
		String attrNextStepCommand = PropertyUtil.getSchemaProperty(context, "attribute_NextStepCommand");
		String attrProgressPercent = PropertyUtil.getSchemaProperty(context, "attribute_ProgressPercent");
		String formatLog = PropertyUtil.getSchemaProperty(context, "format_Log");

		MapList resultList = new MapList();
		objectSelects.add(SELECT_ID);
		objectSelects.add(SELECT_NAME);
		objectSelects.add(SELECT_CURRENT);
		objectSelects.add(ATTR_BEGIN + attrCompletionStatus + ATTR_END);
		objectSelects.add(ATTR_BEGIN + attrAbortRequested + ATTR_END);
		objectSelects.add(ATTR_BEGIN + attrNextStepCommand + ATTR_END);
		objectSelects.add(ATTR_BEGIN + attrProgressPercent + ATTR_END);
		objectSelects.add("format[" + formatLog + "].hasfile");
		objectSelects.add("modified");

		String objectWhere = "(attribute[Title]==STEPGenerator|| attribute[Title]==PDFGenerator)"+whereExp.toString();
		try {
			resultList = DomainObject.findObjects(context, typeJob, DomainConstants.QUERY_WILDCARD,
					DomainConstants.QUERY_WILDCARD, user, DomainConstants.QUERY_WILDCARD, objectWhere, false,
					objectSelects);
			resultList.sort("modified", "descending", "Date");

		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		Log.write("googPDFSTEPGenerator--exit getBackgroundJobs--");
		return resultList;
	}

	/**
	 * This method is used to re-initiate the JOB after failure
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Map massUpdateFailedJobs(Context context, String[] args) throws Exception {
		Log.write("googPDFSTEPGenerator--enter massUpdateFailedJobs--");
		Map statusMap = new HashMap();
		Map paramMap = (Map) JPO.unpackArgs(args);
		String[] selectedObjectList = (String[]) paramMap.get("SelectedIds");
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_CURRENT);
		busSelects.add(DomainConstants.SELECT_ID);
		busSelects.add("attribute[Completion Status]");
		String destState = "Created";
		String statusKey = "Status";
		String success = "Sucess";
		String failed = "Failed";
		String keyFailedList = "FailedList";
		String failureReason = "FailureReason";
		String failedMessage = "Failed to Reinitiate Job:";
		String strValue = "None";
		MapList partList = new MapList();
		statusMap.put(keyFailedList, "");
		try {
			String contextUser = context.getUser();
			String updateHistory = "Job Reinitiated by " + contextUser;
			ContextUtil.pushContext(context);
			MqlUtil.mqlCommand(context, "trigger off");
			MapList selectedJobInfo = DomainObject.getInfo(context, selectedObjectList, busSelects);
			if (selectedJobInfo.size() > 0) {
				Iterator<Map> jobItr = selectedJobInfo.iterator();
				while (jobItr.hasNext()) {
					Map objectMap = (Map) jobItr.next();
					String objState = (String) objectMap.get(SELECT_CURRENT);
					String complStatus = (String) objectMap.get("attribute[Completion Status]");
					String objectID = (String) objectMap.get(DomainConstants.SELECT_ID);
					try {
						if ("Failed".equals(complStatus) && "Completed".equals(objState)) {
							String historyCmd = "modify bus " + objectID + "  'Completion Status' " + strValue
									+ " current " + destState + " add history 'demote' comment '" + updateHistory + "  "
									+ contextUser + "';";
							MqlUtil.mqlCommand(context, historyCmd);
							statusMap.put(statusKey, success);
						}
					} catch (Exception e) {
						e.printStackTrace();
						statusMap.put(statusKey, failed);
						statusMap.put(failureReason, failedMessage);
						partList.add(objectMap);
						statusMap.put(keyFailedList, partList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMap.put(statusKey, failed);
			statusMap.put(failureReason, failedMessage);
		} finally {
			MqlUtil.mqlCommand(context, "trigger on");
			ContextUtil.popContext(context);
		}
		Log.write("googPDFSTEPGenerator--exit massUpdateFailedJobs--");
		return statusMap;
	}

	/**
	 * This method used for updating the Water mark in PDF file
	 * @param filePath
	 * @param srcFileName
	 * @param current
	 * @return srcFileName
	 * @throws Exception
	 * @author shajil
	 */
	public String initiateWatermarkPDF(String filePath, String srcFileName, String current) throws Exception {
		Log.write("googPDFSTEPGenerator--enter initiateWatermarkPDF-srcFileName-"+srcFileName);
		File fsrc = new File(filePath + srcFileName);
		String sTempchange = "TODO_" + fsrc.getName();
		String sDest = filePath + srcFileName;
		File fTemp = new File(filePath + sTempchange);
		fsrc.renameTo(fTemp);
		try {
			manipulatePdf(fTemp.getAbsolutePath(), sDest, current);
		} catch (Exception e) {
			Log.write("Error occured during PDF water mark update");
			Log.write(e.getMessage());
			e.printStackTrace();
		}finally {
			fTemp.delete();
		}
		Log.write("googPDFSTEPGenerator--exit initiateWatermarkPDF-srcFileName-"+srcFileName);
		return srcFileName;
	}

	/**
	 * This method used for updating the Water mark in PDF file
	 * @param path
	 * @param fileName
	 * @param state
	 * @throws IOException
	 * @throws DocumentException
	 * @author shajil
	 */
	public void manipulatePdf(String path, String fileName, String state) throws IOException, DocumentException, Exception {
		Log.write("googPDFSTEPGenerator--inside manipulatePdf-fileName-"+fileName);
		PdfReader reader = new PdfReader(path);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fileName));
		Font f = new Font(FontFamily.HELVETICA, 60);
		Phrase pOver;
		PdfGState gsOver;
		Rectangle pagesize;
		PdfContentByte over;
		float x, y;
		for (int i = 1; i < reader.getNumberOfPages() + 1; i++) {
			gsOver = new PdfGState();
			if (!state.equalsIgnoreCase("RELEASED")) {
				gsOver.setFillOpacity(0.03f);
			} else {
				gsOver.setFillOpacity(0.05f);
			}
			pagesize = reader.getPageSize(i);
			x = pagesize.getRight() - 80;
			y = pagesize.getBottom() + 250;
			over = stamper.getOverContent(i);
			pOver = new Phrase(state, f);
			over.saveState();
			over.setGState(gsOver);
			if (!state.equalsIgnoreCase("RELEASED")) {
				ColumnText.showTextAligned(over, Element.ALIGN_CENTER, pOver, x, y, 90);
				ColumnText.showTextAligned(over, Element.ALIGN_CENTER, pOver,
						(pagesize.getLeft() + pagesize.getRight()) / 2, pagesize.getBottom() + 20, 0);
			} else {
				ColumnText.showTextAligned(over, Element.ALIGN_CENTER, pOver, x, y, 90);
			}
			ColumnText.showTextAligned(over, Element.ALIGN_CENTER, pOver, x, y, 90);
			over.restoreState();
		}
		stamper.close();
		reader.close();
		Log.write("googPDFSTEPGenerator--exit manipulatePdf--");
	}
	
	
	/**
	 * This method is used to perform both 3DXML file generation using seperate Task scheduler
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	public void get3DXMLInputFiles(Context context, String[] args) throws MatrixException, IOException, Exception {
       Log.write("googPDFSTEPGenerator--enter get3DXMLInputFiles--");
		if (checkBatchStarter(context) == 0) {
			try {
		    String format = args[0];
			String dirInputFile = pageMap.get("goog_3DGenerator.InputFile");
			Reader dirinputFileReader = new FileReader(dirInputFile);
			BufferedReader xmlinputFileReader = new BufferedReader(dirinputFileReader);
			String line;
			StringList slInputList = new StringList();
			while ((line = xmlinputFileReader.readLine()) != null) {
				slInputList.add(line);
			    }
			 
			  if(!slInputList.isEmpty()) {
				  initiate3DXMLGeneration(context,slInputList,format);
			  } else {
				  Log.write("googPDFSTEPGenerator--No Information in the Input File");
			  }
			  
			  
		   } catch(Exception e) {
				e.printStackTrace();
			}
			
		}else {
			Log.write("googPDFSTEPGenerator--exit get3DXMLInputFiles-2-");
			Log.write("CATIA execution still in progress");
		}
	}
	

	/**
	 * This method is used to Initiate 3DXML File Generation
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	
	public void initiate3DXMLGeneration(Context context, StringList cadList,String format) throws Exception {
		try {
			StringList slCadList =  cadList;
			MapList allCADObjectList = new MapList();
			Map<String, MapList> cadObjectMap = new HashMap<String, MapList>();
			for(int iList = 0;iList<slCadList.size();iList++) {
				String strCADobject = (String)slCadList.get(iList);
				if(!strCADobject.isEmpty()) {
					String strCADID = fetchCADObjectID(context,strCADobject);
					MapList validateList = validateCADObject(context,strCADobject);
					if(!validateList.isEmpty()) {
						allCADObjectList.addAll(validateList);
						cadObjectMap.put(strCADID,validateList);
					}
				}	
			}
			
			if(allCADObjectList.size()>0) {
				boolean isSuccess;
				Log.write("googPDFSTEPGenerator--inside initiatePDFGeneration-before-prepareInputsForBatch-");
				Set<String> keySet = cadObjectMap.keySet();
				Iterator keyItr = keySet.iterator();
				while (keyItr.hasNext()) {
					String strKey = (String)keyItr.next();
					MapList mlist = (MapList)cadObjectMap.get(strKey);
					if(!strKey.isEmpty() && strKey !=null && !mlist.isEmpty() && mlist !=null) {
						Map cdMap = new HashMap();
						cdMap.put(strKey, mlist);
						isSuccess = prepareInputsForBatch(context, allCADObjectList, cdMap, format);
						boolean hasCatSysdemo = false;
						if (isSuccess) { 
							isSuccess = create3DXMLFileFromCATIA(context, format);
							hasCatSysdemo = isCatsysDemonRunning(TASKLIST,serviceName);
							if(hasCatSysdemo) {
								killCatsysDemon(TASKLIST,serviceName,KILL);
							}
							rename3DXMLFiles(context, mlist, format);
						}
						
					}
				}
				
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method is used to kill the CatsysDemon.exe if its running
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	private void killCatsysDemon(String tASKLIST, String serviceName,String KILL) throws Exception{
		try {
			Runtime.getRuntime().exec(KILL + serviceName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

    /**
	 * This method is to check if the CatsysDemon.exe running or not
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	public boolean isCatsysDemonRunning(String tasklist,String serviceName) throws Exception{
		boolean breturn = false;
		try {
			
			
			Process pro = Runtime.getRuntime().exec(tasklist);
			BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(serviceName)) {
					breturn = true;
					return breturn;
				}
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
		return breturn;
		
	}

	/**
	 * This method is used to fetch the physical ID of the CAD Object
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	private String fetchCADObjectID(Context context, String strCADobject) {
		String strFetchID = "";
       try {
    	   String whereExpression = "current == RELEASED && revision == last";
    	   StringList objectSelects = new StringList();
		   objectSelects.addElement(DomainObject.SELECT_ID);
		   MapList resultList = DomainObject. findObjects(context, 
					                              type_VPMReference, 
					                              strCADobject, 
					                              "*", 
					                              "*", 
					                              "*", 
					                              whereExpression, 
					                              false, 
					                              objectSelects);
		   if(!resultList.isEmpty()) {
			   Map resultMap = (Map)resultList.get(0);
			   strFetchID = (String)resultMap.get(DomainObject.SELECT_ID);
			   
		   }
       } catch(Exception e) {
    	   e.printStackTrace();
       }

		return strFetchID;
	}

	/**
	 * This method is used to Validate if the the CAD ID defined in the Input File Exists,if exists return the details
	 * @param context
	 * @param args
	 * @throws IOException
	 * @throws MatrixException
	 * @author XPLORIA
	 */
	public MapList validateCADObject(Context context, String cadName) throws Exception {
		MapList resultList = new MapList();
		try {
			boolean validate = false;
			StringList objectSelects = new StringList();
			 
			objectSelects.addElement(DomainObject.SELECT_ID);
			objectSelects.addElement(DomainObject.SELECT_TYPE);
			objectSelects.addElement(DomainObject.SELECT_NAME);
			objectSelects.addElement(DomainObject.SELECT_REVISION);
			objectSelects.addElement(MINOREVISION);
			objectSelects.addElement(MAJORREVISION);
			objectSelects.addElement("attribute[" + ATTR_VNAME + "]");
			objectSelects.addElement("attribute[" + ATTRIBUTE_TITLE + "]");
			objectSelects.addElement("attribute[" + ATTR_PLM_EXTERNAL_ID + "]");
			objectSelects.addElement("attribute[V_description]");
			
			

			objectSelects.addElement("latest");
			objectSelects.addElement(SELECT_LAST_ID);
			objectSelects.addElement("last.current");
			objectSelects.addElement(SELECT_CURRENT);
			String strCADObjectName = cadName;
			System.out.println(strCADObjectName);
			String whereExpression = "current == RELEASED && revision == last";
			
			resultList = DomainObject. findObjects(context, 
					                              type_VPMReference, 
					                              cadName, 
					                              "*", 
					                              "*", 
					                              "*", 
					                              whereExpression, 
					                              false, 
					                              objectSelects);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
		
	}
	
	/**
	 * This method is used to rename the file generated by CATIA.
	 * @param context
	 * @param allCADObjectsList
	 * @param format
	 * @throws Exception
	 * @author XPLORIA
	 */
	public void rename3DXMLFiles(Context context, MapList allCADObjectsList, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter rename3DXMLFiles-");
		Map objMap;
		String strFileName;
		String path = "";
		File[] listOfFiles;
		String type = "";
		String fileFormat = "";
		String batchgeneratedFileName = "Export_Kister.3dxml";
		
		if (UIUtil.isNotNullAndNotEmpty(format) && FORMAT_3DXML.equalsIgnoreCase(format)) {
			type = type_VPMReference;
			fileFormat = FILE_FORMAT_3DXML;
			path = dir3DXML;
		}
		File directory = new File(path);
		try {
			System.out.println(allCADObjectsList);
			for (int i = 0; i < allCADObjectsList.size(); i++) {
				
				objMap = (Map) allCADObjectsList.get(i);
				if (type.equals(objMap.get(DomainConstants.SELECT_TYPE).toString())) {
					
					String fileName = (String) objMap.get("attribute[" + ATTR_VNAME + "]");
					String strPRDName = (String) objMap.get("attribute[" + ATTR_PLM_EXTERNAL_ID + "]");
                    if(strPRDName.isEmpty() && strPRDName == null) {
                    	strPRDName = (String)objMap.get(DomainObject.SELECT_NAME);
					}
					if(!fileName.isEmpty() && fileName !=null) {
						fileName = fileName.replace(" ", "_");
						strFileName = fileName +"_"+ strPRDName + "_REV_" + objMap.get("majorrevision").toString() + fileFormat;
					} else {
						strFileName = strPRDName + "_REV_" + objMap.get("majorrevision").toString() + fileFormat;
					}
					Log.write("googPDFSTEPGenerator--In rename3DXMLFiles-fileName--"+fileName);
					
					Log.write("googPDFSTEPGenerator--In rename3DXMLFiles-strFileName-1-"+strFileName);
					
		
					listOfFiles = directory.listFiles();
					for (int k = 0; k < listOfFiles.length; k++) {
						String availableFile = listOfFiles[k].getName();
						if (availableFile.contains(batchgeneratedFileName) && availableFile.contains(FILE_FORMAT_3DXML)) {
							File fileRename = new File(path + strFileName);		
							listOfFiles[k].renameTo(fileRename);
							Log.write("googPDFSTEPGenerator--In rename3DXMLFiles-File Renamed--");
							
						}
					}
				}
			}
			Log.write("googPDFSTEPGenerator--exit rename3DXMLFiles-");
		} catch (Exception e) {
			Log.write("Failed to rename3DXMLFiles the file.............");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to execute CATIA batch process for creating the 3DXML Files
	 * @param context
	 * @param format
	 * @return
	 * @throws Exception
	 * @author XPLORIA
	 */
	public boolean create3DXMLFileFromCATIA(Context context, String format) throws Exception {
		Log.write("googPDFSTEPGenerator--enter create3DXMLFileFromCATIA--");
		Process batchProcess = null;
		boolean isSuccess = true;

		try {
	
			Runtime rs = Runtime.getRuntime();
			if (UIUtil.isNotNullAndNotEmpty(format) && FORMAT_3DXML.equalsIgnoreCase(format)) {
				Log.write("googPDFSTEPGenerator--Inside create3DXMLFileFromCATIA-batch execution 1 for 3DXML-");
				batchProcess = rs.exec("cmd /c start /wait " +pageMap.get("goog_3DGenerator.dir3DXMLBat"));
				Log.write("googPDFSTEPGenerator--Inside create3DXMLFileFromCATIA-batch execution 2 for 3DXML-");
			  }
			batchProcess.waitFor();
			Log.write("googPDFSTEPGenerator--Inside create3DXMLFileFromCATIA-batch execution wait over-");
			Log.write("googPDFSTEPGenerator--exit create3DXMLFileFromCATIA-");
		} catch (Exception e) {
			isSuccess = false;
			Log.write("Error occured during file generation");
			Log.write(e.getMessage());
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
}
