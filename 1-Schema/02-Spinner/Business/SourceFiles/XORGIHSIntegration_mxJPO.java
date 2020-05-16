
/*
 ** ${CLASS:XORGIHSIntegration}
 ** All required methods for IHS Intgration
 ** Added for Release Elixir 1.0
 ** Copyright notice is precautionary only and does not evidence any actual
 ** or intended publication of such program
 */

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;

import generated.Alerts;
import generated.Details;
import generated.Doc;
import generated.Partdetails;
import generated.Result;
import generated.XMLResult;
import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MatrixWriter;
import matrix.util.SelectList;
import matrix.util.StringList;

//Added by kranthikiranc for 'IHS Updated Parts Report'- End
/**
 * The <code>emxPart</code> class contains code for the "Part" business type.
 *
 * @version EC 9.5.JCI.0 - Copyright (c) 2002, MatrixOne, Inc.
 */
public class XORGIHSIntegration_mxJPO extends emxPartBase_mxJPO {
	/**
	 * Constructor.
	 *
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            holds no arguments.
	 * @throws Exception
	 *             if the operation fails.
	 * @since EC 9.5.JCI.0.
	 */
	protected SimpleDateFormat _mxDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(),
			Locale.US);

	public XORGIHSIntegration_mxJPO(Context context, String[] args) throws Exception {
		super(context, args);
	}

	public String syncWithIHS(Context context, String[] args) throws Exception {

		String returnString = "";
		Map<String, String> objMap = null;
		// HashMap<String, String> programMap = (HashMap) JPO.unpackArgs(args);
		// String sObjectId=(String)programMap.get("objectId");

		String sObjectId = args[0];

		String strPartName = "";
		String strSupplierName = "";
		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_NAME);

		StringList slClassification = new StringList();
		String strType = "";

		try {

			DomainObject doMEP = new DomainObject(sObjectId);

			strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);

			MapList mlObjects = doMEP.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + ","
							+ LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
					DomainConstants.TYPE_COMPANY + "," + LibraryCentralConstants.TYPE_GENERAL_CLASS, // Type pattern
					selectStmts, // object selects
					null, // relationship selects
					true, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					DomainConstants.EMPTY_STRING, 0);
			Iterator<Map<String, String>> objItr = (Iterator) mlObjects.iterator();

			while (objItr.hasNext()) {
				objMap = (Map<String, String>) objItr.next();
				strType = (String) objMap.get(DomainConstants.SELECT_TYPE);
				if (strType.equalsIgnoreCase(DomainConstants.TYPE_COMPANY)) {
					strSupplierName = (String) objMap.get(DomainConstants.SELECT_NAME);
				} else if (strType.equalsIgnoreCase(LibraryCentralConstants.TYPE_GENERAL_CLASS)) {
					slClassification.add((String) objMap.get(DomainConstants.SELECT_NAME));
				}
			}
			returnString = (String) retrieveIHSData(context, doMEP, strSupplierName, slClassification);

		} catch (Exception E) {
			E.printStackTrace();

		}
		return returnString;
	}

	// This method is to be called manually from tcl/mql script to Sync a MEP with
	// IHS Manually.
	public String syncWithIHSManual(Context context, String[] args) throws Exception {

		String returnString = "";
		Map<String, String> objMap = null;
		// HashMap<String, String> programMap = (HashMap) JPO.unpackArgs(args);
		// String sObjectId=(String)programMap.get("objectId");

		String sObjectId = args[0];

		String strPartName = "";
		String strSupplierName = "";
		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_NAME);

		StringList slClassification = new StringList();
		String strType = "";
		MatrixWriter _mxWriter = new MatrixWriter(context);
		try {

			DomainObject doMEP = new DomainObject(sObjectId);

			strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);

			MapList mlObjects = doMEP.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + ","
							+ LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
					DomainConstants.TYPE_COMPANY + "," + LibraryCentralConstants.TYPE_GENERAL_CLASS, // Type pattern
					selectStmts, // object selects
					null, // relationship selects
					true, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					DomainConstants.EMPTY_STRING, 0);
			Iterator<Map<String, String>> objItr = (Iterator) mlObjects.iterator();

			while (objItr.hasNext()) {
				objMap = (Map<String, String>) objItr.next();
				strType = (String) objMap.get(DomainConstants.SELECT_TYPE);
				if (strType.equalsIgnoreCase(DomainConstants.TYPE_COMPANY)) {
					strSupplierName = (String) objMap.get(DomainConstants.SELECT_NAME);
				} else if (strType.equalsIgnoreCase(LibraryCentralConstants.TYPE_GENERAL_CLASS)) {
					slClassification.add((String) objMap.get(DomainConstants.SELECT_NAME));
				}
			}
			returnString = (String) retrieveIHSData(context, doMEP, strSupplierName, slClassification);

		} catch (Exception E) {
			E.printStackTrace();

		}
		returnString = returnString.replace("\n\r", "");
		String replacedStr = returnString.replace("Part sync with IHS done successfully. ", "SUCCESS");
		replacedStr = replacedStr.replace("Part sync with IHS done successfully", "SUCCESS");
		replacedStr = replacedStr.replace(
				"Invalid Characters found for below parameters. Please report the issue to your System Administrator",
				"|");

		_mxWriter.write(replacedStr);

		return returnString;
	}

	// Method to populate IHS data on MEP from XML.
	public String populateIHSdataOnMEP(Context context, String[] args) throws Exception {

		String strReturn = "";

		String sMEPInfo = args[0];
		String strMEPName = "";
		String strSupplierName = "";
		String strMEPID = "";
		String strXMLPath = "";

		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		Map<String, String> objMap = null;

		String strType = "";

		StringList slClassification = new StringList();

		String MQLResult = "";
		String strPageFileName = "XORGIHSIntegrationMapping";

		Date date = new Date();

		BufferedWriter bw = null;
		MatrixWriter _mxWriter = new MatrixWriter(context);

		String strWhere = "";
		StringList slSelectable = new StringList();
		try {
			sMEPInfo = sMEPInfo.replace("_hash_", "#");
			SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");
			StringList slSplit = FrameworkUtil.split(sMEPInfo, "|");
			strMEPName = (String) slSplit.get(1);
			strSupplierName = (String) slSplit.get(2);
			strMEPName = strMEPName.replace("_slash_", "/");
			strMEPName = strMEPName.replace("_colon_", ":");

			strXMLPath = (String) slSplit.get(4);

			MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			InputStream input = new ByteArrayInputStream(bytes);
			Properties prop = new Properties();
			prop.load(input);
			// creating the error log file
			String strLogFilePath = prop.getProperty("XORGIHS.Log.path");
			String strErrorLogHeader = prop.getProperty("XORGIHS.RealTime.Error.Log.header");
			File errorLog = new File(strLogFilePath + "IHS_Error" + "_" + dateForLog.format(date) + ".log");
			// to create folder structure
			errorLog.getParentFile().mkdirs();
			if (!errorLog.exists()) {
				FileWriter fw = new FileWriter(errorLog, true);
				bw = new BufferedWriter(fw);
				bw.write(strErrorLogHeader);
				bw.newLine();
			} else {
				FileWriter fw = new FileWriter(errorLog, true);
				bw = new BufferedWriter(fw);
			}
			strMEPName = strMEPName.trim();
			strWhere = "name==" + "'" + strMEPName + "'" + "&&" + "relationship["
					+ DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name==" + "'"
					+ strSupplierName + "'";
			slSelectable.add(DomainConstants.SELECT_ID);
			slSelectable.add(DomainConstants.SELECT_NAME);
			slSelectable.add(DomainConstants.SELECT_REVISION);
			slSelectable
					.add("relationship[" + DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name");

			MapList mlMEPs = DomainObject.findObjects(context, // context
					DomainConstants.TYPE_PART, // typePattern
					DomainConstants.QUERY_WILDCARD, // namePattern
					DomainConstants.QUERY_WILDCARD, // revPattern
					DomainConstants.QUERY_WILDCARD, // ownerPattern
					DomainConstants.QUERY_WILDCARD, // vaultPatern
					strWhere, // whereExpression
					false, // expandType
					slSelectable);// objectSelects
			if (mlMEPs.size() == 1) {
				Map<String, String> mepMAP = (Map<String, String>) mlMEPs.get(0);
				strMEPID = (String) mepMAP.get(DomainConstants.SELECT_ID);
			}

			else if (mlMEPs.size() > 1) {
				_mxWriter.write("Duplicate Parts Found");
				return "Duplicate Parts Found";
			} else if (mlMEPs.size() == 0) {
				_mxWriter.write("No Parts Found");
				return "No Parts Found";
			}
			DomainObject doMEP = new DomainObject(strMEPID);
			// getting the connected classifications & Supplier
			MapList mlObjects = doMEP.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + ","
							+ LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
					DomainConstants.TYPE_COMPANY + "," + LibraryCentralConstants.TYPE_GENERAL_CLASS, // Type pattern
					selectStmts, // object selects
					null, // relationship selects
					true, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					DomainConstants.EMPTY_STRING, 0);
			Iterator<Map<String, String>> objItr = (Iterator) mlObjects.iterator();

			while (objItr.hasNext()) {
				objMap = (Map<String, String>) objItr.next();
				strType = (String) objMap.get(DomainConstants.SELECT_TYPE);
				if (strType.equalsIgnoreCase(DomainConstants.TYPE_COMPANY)) {
					strSupplierName = (String) objMap.get(DomainConstants.SELECT_NAME);
				} else if (strType.equalsIgnoreCase(LibraryCentralConstants.TYPE_GENERAL_CLASS)) {
					slClassification.add((String) objMap.get(DomainConstants.SELECT_NAME));
				}
			}
			// creating the inputfilestream from file

			InputStream inStream = new FileInputStream(strXMLPath);
			Reader inputStreamReader = new InputStreamReader(inStream);

			strReturn = getPartDetailsfromXML(context, inputStreamReader, doMEP, strSupplierName, slClassification, bw,
					prop);

			strReturn = strReturn.replace("\n\r", "");
			String replacedStr = strReturn.replace("Part sync with IHS done successfully. ", "SUCCESS");
			replacedStr = replacedStr.replace("Part sync with IHS done successfully", "SUCCESS");
			replacedStr = replacedStr.replace(
					"Invalid Characters found for below parameters. Please report the issue to your System Administrator",
					"|");

			_mxWriter.write(replacedStr);
		} catch (Exception E) {
			E.printStackTrace();
			bw.write(strMEPName + "|" + strSupplierName + "|" + "An Issue is encountered while Updating the IHS data.");
			bw.newLine();
			bw.close();
			return "An Issue is encountered while Updating the IHS data.";
		} finally {
			bw.close();
		}
		return strReturn;
	}

	// written by kranthikiranc
	// below method sends the MEP name & Supplier name unique combination details to
	// IHS
	// to retrieve corresponding XML InputStream
	public String retrieveIHSData(Context context, DomainObject doMEP, String strSupplierName,
			StringList slClassification) throws Exception {
		String strReturn = "";

		Proxy proxy = null;
		String strPageFileName = "XORGIHSIntegrationMapping";
		String MQLResult = "";
		Date date = new Date();
		String strIHSUser = "";
		String strIHSPswrd = "";
		String serverURL = "";
		String serverFile = "";
		String requestXML = "";
		URLConnection urlConn;
		String encoding = "";
		String strLog = "";

		String strPartName = "";
		String strConnectionTimeOut = "";
		String strReadTimeout = "";

		String strPartNameforIHSQuery = "";
		String strSupplierNameforIHSQuery = "";

		// String strSuccessFileHeader = "";
		SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");
		BufferedWriter bw = null;
		try {

			MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			InputStream input = new ByteArrayInputStream(bytes);
			Properties prop = new Properties();
			prop.load(input);

			String strLogFilePath = prop.getProperty("XORGIHS.Log.path");
			String strErrorLogHeader = prop.getProperty("XORGIHS.RealTime.Error.Log.header");
			File errorLog = new File(strLogFilePath + "IHS_Error" + "_" + dateForLog.format(date) + ".log");
			// to create folder structure
			errorLog.getParentFile().mkdirs();

			if (!errorLog.exists()) {
				FileWriter fw = new FileWriter(errorLog, true);
				bw = new BufferedWriter(fw);
				bw.write(strErrorLogHeader);
				bw.newLine();
			} else {
				FileWriter fw = new FileWriter(errorLog, true);
				bw = new BufferedWriter(fw);
			}

			strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);

			strIHSUser = prop.getProperty("IHS.credentials.username");
			strIHSPswrd = prop.getProperty("IHS.credentials.password");
			serverURL = prop.getProperty("IHS.credentials.serverURL");
			serverFile = prop.getProperty("IHS.credentials.serverFile");
			strConnectionTimeOut = prop.getProperty("IHS.RealTime.connection.timeout");
			strReadTimeout = prop.getProperty("IHS.RealTime.read.timeout");

			URL urlOrder = new URL("HTTPS", serverURL, 443, serverFile);
			urlConn = (proxy == null) ? urlOrder.openConnection() : urlOrder.openConnection(proxy);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestProperty("Content-Type", "text/xml");
			urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConn.connect();
			// setting connection timeout to 10 minutes
			urlConn.setConnectTimeout(Integer.parseInt(strConnectionTimeOut));
			urlConn.setReadTimeout(Integer.parseInt(strReadTimeout));
			strPartNameforIHSQuery = strPartName.replace("&", "&amp;");
			strSupplierNameforIHSQuery = strSupplierName.replace("&", "&amp;");

			requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<XMLQuery version=\"0.1\">"
					+ "<Login password=\"" + strIHSPswrd + "\" user-name=\"" + strIHSUser + "\"/>" +
					// Modified by Lalitha for #68 Isuess --starts
					// added all parameters to be retrieved from IHS- kranthi
					"<Criteria limit=\"20\" search-type=\"PART\" part-details=\"NSN,LIFE_CYCLE,HAZMAT,ALERTS,DOC,LATEST_DATASHEET,TRANSFERS,DETAILS,ALT,ALT_COMBINED\" alt-type=\"direct,similar,FFF,functional\">"
					+
					// Modified by Lalitha for #68 Isuess --Ends
					"<Criterion id=\"TEST\">" + "<Parameter match-type=\"EXACT\" name=\"mfg\">"
					+ strSupplierNameforIHSQuery + "</Parameter>"
					+ "<Parameter match-type=\"EXACT\" name=\"part-number\">" + strPartNameforIHSQuery + "</Parameter>"
					+ " </Criterion>" + " </Criteria>" + "</XMLQuery>";// Modified for Issue #360
			DataOutputStream dOut = new DataOutputStream(urlConn.getOutputStream());
			dOut.writeBytes(requestXML);
			dOut.flush();
			encoding = urlConn.getContentEncoding();
			InputStream inStream = null;
			// based on encoding define input stream type and get it
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				inStream = new GZIPInputStream(urlConn.getInputStream());
			} else {
				inStream = urlConn.getInputStream();
			}
			Reader inputStreamReader = new InputStreamReader(inStream);

			dOut.close();
			strReturn = getPartDetailsfromXML(context, inputStreamReader, doMEP, strSupplierName, slClassification, bw,
					prop);
			return strReturn;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			bw.write(strPartName + "|" + strSupplierName + "|"
					+ "An Issue is encountered while accessing the IHS data. Please try again later to see if it has been resolved. If not report the issue to your System Administrator");
			bw.newLine();
			bw.close();
			return "An Issue is encountered while accessing the IHS data. Please try again later to see if it has been resolved. If not report the issue to your System Administrator";
		} catch (FileNotFoundException E) {
			E.printStackTrace();
			return "Unable to Access IHS Logs. Please Contact Administrator!";
		} catch (Exception E) {
			bw.write(strPartName + "|" + strSupplierName + "|"
					+ "An Issue is encountered while accessing the IHS data. Please try again later to see if it has been resolved. If not report the issue to your System Administrator");
			bw.newLine();
			bw.close();

			E.printStackTrace();
			return "An Issue is encountered while accessing the IHS data. Please try again later to see if it has been resolved. If not report the issue to your System Administrator";
		}

	}

	// written by kranthikiranc
	// below method will get the neccessary IHS attributes info from response XML in
	// inputStreamReader
	// and store them inro ihsAttrInfoMap for further processing
	public String getPartDetailsfromXML(Context context, Reader inputStreamReader, DomainObject doMEP,
			String strSupplierName, StringList slClassification, BufferedWriter bw, Properties prop) throws Exception {
		String returnString = "";

		Map<String, String> partDetailsMap = null;
		String resultCount = "";
		generated.Partdetails partDetails = new generated.Partdetails();
		String strPartDetailsType = "";
		ArrayList<Details> detailsList = new ArrayList<Details>();
		// DomainObject doMEP = new DomainObject(sPartId);
		StringList slInterfaceAttrList = new StringList();
		Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
		// ihsAttrInfoMap is created to store the attribute key and values coming from
		// IHS XML
		String strClassName = "";

		// String strPageFileName = "XORGIHSIntegrationMapping";
		String strPropKey_Category = "IHS.Mapping.IHSCategory";
		String strPropKey_mfr_name = "IHS.Mapping.IHSManufacturer_Name";
		String strPropKey_Manufacturer_part_number = "IHS.Mapping.IHSManufacturer_Part_Number";
		String strPropKey_mfg_pkg_desc = "IHS.Mapping.IHSMfr_Package_Description";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";
		String strPropKey_cage_code = "IHS.Mapping.IHSCAGE_Code";
		String strPropKey_part_desc = "IHS.Mapping.IHSPart_Description";
		String strPropKey_part_status = "IHS.Mapping.IHSStatus";
		String strPropKey_doc_url = "IHS.Mapping.IHSDatasheet_URL";
		String strPropKey_latest_doc_url = "IHS.Mapping.IHSLatest_DataSheet_URL";// Added for Issue #360
		String strPropKey_response_xml = "IHS.Mapping.IHSResponse_XML";

		String strDocURL = "";
		// Added for Issue #360 Starts
		String strLatestDocURL = "";
		String strDocDate = "";
		Map<String, String> docURLDateMap = new HashMap<String, String>();
		// Added for Issue #360 Ends
		String strDocTitle = "";

		ArrayList<Doc> docList;
		ArrayList<Object> partList;
		generated.Doc doc = new generated.Doc();

		String strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);
		String sPartId = doMEP.getInfo(context, DomainConstants.SELECT_ID);

		String strIHSResponseXML = "";

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			strIHSResponseXML = IOUtils.toString(inputStreamReader);
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_response_xml), strIHSResponseXML);
			inputStreamReader = new StringReader(strIHSResponseXML);

			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
				generated.Result result = (generated.Result) xmlResultList.get(0);
				if ((Integer.parseInt(result.getCount())) > 0) {
					partList = (ArrayList<Object>) result.getPartOrMfrOrError();

					if (partList.size() == 1) {
						generated.Part part = (generated.Part) partList.get(0);
						// getting the classification info
						String strClassificationName = part.getPartType();
						String strPartDesc = part.getPartDescription();

						// get the attributes from Part element in XML and store in ihsAttrInfoMap

						/*
						 * String MQLResult = MqlUtil.mqlCommand(context,
						 * "print page $1 select content dump", strPageFileName); byte[] bytes =
						 * MQLResult.getBytes("UTF-8"); InputStream input = new
						 * ByteArrayInputStream(bytes); Properties prop = new Properties();
						 * prop.load(input);
						 */
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Category), part.getCategory());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfr_name), part.getMfrName());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Manufacturer_part_number),
								part.getManufacturerPartNumber());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfg_pkg_desc), part.getMfgPkgDesc());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_object_id), part.getObjectId());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_cage_code), part.getCageCode());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_desc), part.getPartDescription());
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_status), part.getPartStatus());

						// code to classify MEP with strClassification -- start
						StringList slSelect = new StringList();
						slSelect.addElement(DomainConstants.SELECT_ID);
						slSelect.addElement(DomainConstants.SELECT_NAME);
						slSelect.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
						// querying for the classification object
						MapList mlClassification = DomainObject.findObjects(context, // context
								LibraryCentralConstants.TYPE_GENERAL_CLASS, // typePattern
								strClassificationName, // namePattern
								DomainConstants.QUERY_WILDCARD, // revPattern
								DomainConstants.QUERY_WILDCARD, // ownerPattern
								DomainConstants.QUERY_WILDCARD, // vaultPatern
								DomainConstants.EMPTY_STRING, // whereExpression
								false, // expandType
								slSelect);// objectSelects
						if (mlClassification.size() == 0) {
							bw.write(strPartName + "|" + strSupplierName + "|"
									+ "No Appropriate IHS Classification Found to classify :" + strClassificationName);
							bw.newLine();
							bw.close();
							return "No Appropriate IHS Classification Found to classify :" + strClassificationName;
						} else if (mlClassification.size() > 1) {
							bw.write(strPartName + "|" + strSupplierName + "|" + "Multiple " + strClassificationName
									+ " classifications exist . Item can't be classified");
							bw.newLine();
							bw.close();
							return "Multiple " + strClassificationName
									+ " classifications exist . Item can't be classified";
						} else if (mlClassification.size() == 1) {
							Map<String, String> tempClassificationMap = (Map<String, String>) mlClassification.get(0);
							String strClassID = tempClassificationMap.get(DomainConstants.SELECT_ID);
							strClassName = tempClassificationMap.get(DomainConstants.SELECT_NAME);
							String interfaceName = tempClassificationMap
									.get("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
							// classify only if the part is not already classified with same classification
							if (!slClassification.contains(strClassName)) {
								DomainRelationship.connect(context, new DomainObject(strClassID),
										LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, doMEP);
							}
							// code to classify MEP with strClassification -- end
							// get the attributes on classification
							if (interfaceName != null && !"".equals(interfaceName)) {
								String strMQL = "print interface $1 select attribute dump";
								String resStr = MqlUtil.mqlCommand(context, strMQL, true, interfaceName);
								StringList slMQLResult = FrameworkUtil.split(resStr, ",");
								if (slMQLResult.size() > 0) {
									slInterfaceAttrList.addAll(slMQLResult);
								}
							}
						}
						ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
								.getPartdetails();
						for (int i = 0; i < partDetailsList.size(); i++) {
							partDetails = partDetailsList.get(i);
							strPartDetailsType = partDetails.getType();
							if (strPartDetailsType.equalsIgnoreCase("DOC")) {
								docList = (ArrayList<Doc>) partDetails.getDoc();
								for (int j = 0; j < docList.size(); j++) {
									doc = docList.get(j);
									if (doc.getDocType().equalsIgnoreCase("Datasheet")) {
										if (j == 0) {
											strDocTitle = doc.getDocTitle();
											strDocDate = doc.getPubDate();// Added for Issue #360
											// strDocURL = doc.getDocUrl();
											strDocURL = strDocTitle + ":" + "\n" + doc.getDocUrl();
											docURLDateMap.put(strDocDate, strDocURL);// Added for Issue #360

										} else {
											strDocTitle = doc.getDocTitle();
											strDocDate = doc.getPubDate();// Added for Issue #360
											// strDocURL = strDocURL +"\n"+doc.getDocUrl();
											strDocURL = strDocURL + "\n" + strDocTitle + ":" + "\n" + doc.getDocUrl();
											docURLDateMap.put(strDocDate, strDocTitle + ":" + "\n" + doc.getDocUrl());// Added
																														// for
																														// Issue
																														// #360
										}
									}
								}
							} else if (strPartDetailsType.equalsIgnoreCase("DETAILS")) {
								// code to retrieve part details info into a Map
								detailsList = (ArrayList<Details>) partDetails.getDetails();
								// Added for Issue #360 Starts
							}
							// Added for Issue #360 Ends
						}
						// Added for Issue #360 Starts
						StringList sSortedDateList = getSortedDateList(context, docURLDateMap);
						String strDocURLSorted = "";
						for (int kk = 0; kk < sSortedDateList.size(); kk++) {
							String strDateKey = (String) sSortedDateList.get(kk);
							String strUrl = (String) docURLDateMap.get(strDateKey);
							if (kk == 0) {
								strDocURLSorted = strUrl;
							} else {
								strDocURLSorted = strDocURLSorted + "\n" + strUrl;
							}
						}
						StringList strLatestDocURLLst = FrameworkUtil.split(strDocURLSorted, "\n");
						strLatestDocURL = (String) strLatestDocURLLst.get(0) + "\n"
								+ (String) strLatestDocURLLst.get(1);
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_doc_url), strDocURLSorted);
						ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_latest_doc_url), strLatestDocURL);
						// Added for Issue #360 Ends

						returnString = updateMEPAttributes(context, detailsList, doMEP, strSupplierName,
								slInterfaceAttrList, strClassName, ihsAttrInfoMap, bw, prop);
						// Added for Issue-192 by Preethi Rajaraman -- Starts

						MetricConversion(context, strClassName, doMEP, prop);

						// Added for Issue 207 and 233 by Ravindra ---Starts
						descriptionUpdateForMEP(context, doMEP, strSupplierName, strClassName, prop);
						updateECADReferenceAttributesAndDisconnectClass(context, doMEP, strSupplierName, strClassName,
								prop);

						// Added for Issue 207 and 233 by Ravindra ---Ends

						/*
						 * if (!sDescReturn.equals("")) {
						 * 
						 * doMEP.setDescription(context,sDescReturn);
						 * 
						 * }
						 */

						// Added for Issue-192 by Preethi Rajaraman -- Ends

						// Added for Issue 222 Starts
					} else if (partList.size() == 0) {

						bw.write(strPartName + "|" + strSupplierName + "|"
								+ "IHS contains No Parts with this MEP & Supplier combination");// Modified for Issue
																								// 222
						bw.newLine();
						bw.close();
						return "IHS contains No Parts with this MEP & Supplier combination";// Modified for Issue 222
						// Added for Issue 222 Ends
					} else {
						//Modified by Preethi rajaraman for IHS Response contains multiple parts information in response -- Starts 
						boolean bflag = false;
						for(int ii = 0; ii < partList.size(); ii++) {
							generated.Part part = (generated.Part) partList.get(ii);
							String sResponsePartName = part.getManufacturerPartNumber();
							if (sResponsePartName.equals(strPartName)) {
								String strClassificationName = part.getPartType();
								String strPartDesc = part.getPartDescription();

								// get the attributes from Part element in XML and store in ihsAttrInfoMap

								
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Category), part.getCategory());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfr_name), part.getMfrName());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Manufacturer_part_number),
										part.getManufacturerPartNumber());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfg_pkg_desc), part.getMfgPkgDesc());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_object_id), part.getObjectId());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_cage_code), part.getCageCode());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_desc), part.getPartDescription());
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_status), part.getPartStatus());

								// code to classify MEP with strClassification -- start
								StringList slSelect = new StringList();
								slSelect.addElement(DomainConstants.SELECT_ID);
								slSelect.addElement(DomainConstants.SELECT_NAME);
								slSelect.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
								// querying for the classification object
								MapList mlClassification = DomainObject.findObjects(context, // context
										LibraryCentralConstants.TYPE_GENERAL_CLASS, // typePattern
										strClassificationName, // namePattern
										DomainConstants.QUERY_WILDCARD, // revPattern
										DomainConstants.QUERY_WILDCARD, // ownerPattern
										DomainConstants.QUERY_WILDCARD, // vaultPatern
										DomainConstants.EMPTY_STRING, // whereExpression
										false, // expandType
										slSelect);// objectSelects
								if (mlClassification.size() == 0) {
									bw.write(strPartName + "|" + strSupplierName + "|"
											+ "No Appropriate IHS Classification Found to classify :" + strClassificationName);
									bw.newLine();
									bw.close();
									return "No Appropriate IHS Classification Found to classify :" + strClassificationName;
								} else if (mlClassification.size() > 1) {
									bw.write(strPartName + "|" + strSupplierName + "|" + "Multiple " + strClassificationName
											+ " classifications exist . Item can't be classified");
									bw.newLine();
									bw.close();
									return "Multiple " + strClassificationName
											+ " classifications exist . Item can't be classified";
								} else if (mlClassification.size() == 1) {
									Map<String, String> tempClassificationMap = (Map<String, String>) mlClassification.get(0);
									String strClassID = tempClassificationMap.get(DomainConstants.SELECT_ID);
									strClassName = tempClassificationMap.get(DomainConstants.SELECT_NAME);
									String interfaceName = tempClassificationMap
											.get("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
									// classify only if the part is not already classified with same classification
									if (!slClassification.contains(strClassName)) {
										DomainRelationship.connect(context, new DomainObject(strClassID),
												LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, doMEP);
									}
									// code to classify MEP with strClassification -- end
									// get the attributes on classification
									if (interfaceName != null && !"".equals(interfaceName)) {
										String strMQL = "print interface $1 select attribute dump";
										String resStr = MqlUtil.mqlCommand(context, strMQL, true, interfaceName);
										StringList slMQLResult = FrameworkUtil.split(resStr, ",");
										if (slMQLResult.size() > 0) {
											slInterfaceAttrList.addAll(slMQLResult);
										}
									}
								}
								ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
								.getPartdetails();
								for (int i = 0; i < partDetailsList.size(); i++) {
									partDetails = partDetailsList.get(i);
									strPartDetailsType = partDetails.getType();
									if (strPartDetailsType.equalsIgnoreCase("DOC")) {
										docList = (ArrayList<Doc>) partDetails.getDoc();
										for (int j = 0; j < docList.size(); j++) {
											doc = docList.get(j);
											if (doc.getDocType().equalsIgnoreCase("Datasheet")) {
												if (j == 0) {
													strDocTitle = doc.getDocTitle();
													strDocDate = doc.getPubDate();// Added for Issue #360
													// strDocURL = doc.getDocUrl();
													strDocURL = strDocTitle + ":" + "\n" + doc.getDocUrl();
													docURLDateMap.put(strDocDate, strDocURL);// Added for Issue #360

												} else {
													strDocTitle = doc.getDocTitle();
													strDocDate = doc.getPubDate();// Added for Issue #360
													// strDocURL = strDocURL +"\n"+doc.getDocUrl();
													strDocURL = strDocURL + "\n" + strDocTitle + ":" + "\n" + doc.getDocUrl();
													docURLDateMap.put(strDocDate, strDocTitle + ":" + "\n" + doc.getDocUrl());// Added
																																// for
																																// Issue
																																// #360
												}
											}
										}
									} else if (strPartDetailsType.equalsIgnoreCase("DETAILS")) {
										// code to retrieve part details info into a Map
										detailsList = (ArrayList<Details>) partDetails.getDetails();
										// Added for Issue #360 Starts
									}
									// Added for Issue #360 Ends
								}
								// Added for Issue #360 Starts
								StringList sSortedDateList = getSortedDateList(context, docURLDateMap);
								String strDocURLSorted = "";
								for (int kk = 0; kk < sSortedDateList.size(); kk++) {
									String strDateKey = (String) sSortedDateList.get(kk);
									String strUrl = (String) docURLDateMap.get(strDateKey);
									if (kk == 0) {
										strDocURLSorted = strUrl;
									} else {
										strDocURLSorted = strDocURLSorted + "\n" + strUrl;
									}
								}
								StringList strLatestDocURLLst = FrameworkUtil.split(strDocURLSorted, "\n");
								strLatestDocURL = (String) strLatestDocURLLst.get(0) + "\n"
										+ (String) strLatestDocURLLst.get(1);
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_doc_url), strDocURLSorted);
								ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_latest_doc_url), strLatestDocURL);
								// Added for Issue #360 Ends

								returnString = updateMEPAttributes(context, detailsList, doMEP, strSupplierName,
										slInterfaceAttrList, strClassName, ihsAttrInfoMap, bw, prop);
								// Added for Issue-192 by Preethi Rajaraman -- Starts

								MetricConversion(context, strClassName, doMEP, prop);

								// Added for Issue 207 and 233 by Ravindra ---Starts
								descriptionUpdateForMEP(context, doMEP, strSupplierName, strClassName, prop);
								updateECADReferenceAttributesAndDisconnectClass(context, doMEP, strSupplierName, strClassName,
										prop);

								// Added for Issue 207 and 233 by Ravindra ---Ends
								bflag=true;
								break;
						
								}
							}
						if(!bflag) {
							String sResponsePartNameList="";
							for(int ijk = 0; ijk < partList.size(); ijk++) {
								generated.Part part = (generated.Part) partList.get(ijk);
								if(ijk==0)
								{
									sResponsePartNameList = part.getManufacturerPartNumber();
								} else {
									sResponsePartNameList = sResponsePartNameList+","+part.getManufacturerPartNumber();	
								}
							}
							bw.write(strPartName + "|" + strSupplierName + "|"
								+ "IHS does not contain the part "+strPartName+".But similar parts exist are: "+sResponsePartNameList);
							bw.newLine();
							bw.close();
							return "IHS does not contain the part "+strPartName+".But similar parts exist are: "+sResponsePartNameList;
						}
						//Modified by Preethi rajaraman for IHS Response contains multiple parts information in response -- Ends 
					}
				} else {
					// Modified for Issue 222 Starts

					String strRespXML = retrieveIHSDataXMLOnly(context, strPartName);

					boolean isPartPresentInIHS = false;

					if (UIUtil.isNullOrEmpty(strRespXML))

					{

						isPartPresentInIHS = false;

					} else {

						isPartPresentInIHS = isPartPresentInIHS(context, strRespXML);

					}

					if (isPartPresentInIHS) {

						String strIHSManufacturerNames = getIHSManufacturerNames(context, strRespXML);

						bw.write(strPartName + "|" + strSupplierName + "|"
								+ "Manufacturer mismatch. Part has following MFRs in IHS :" + strIHSManufacturerNames
								+ ". Please correct & retry.");

						bw.newLine();

						bw.close();

						return "Manufacturer mismatch. Part has following Manufacturers in IHS :"
								+ strIHSManufacturerNames + ". Please correct & retry.";

					} else {

						bw.write(strPartName + "|" + strSupplierName + "|"
								+ "IHS does not contain Part Information for : " + strPartName);

						bw.newLine();

						bw.close();

						return "IHS does not contain Part Information for : " + strPartName;

					}

					/*
					 * MapList MEPsMaplist =retrieveIHSData2(context,strPartName);
					 * 
					 * 
					 * 
					 * if(MEPsMaplist.size()>0)
					 * 
					 * 
					 * 
					 * {
					 * 
					 * 
					 * 
					 * String strIHSMFRNames="";
					 * 
					 * 
					 * 
					 * for(int ij=0; ij<MEPsMaplist.size(); ij++) {
					 * 
					 * 
					 * 
					 * Map mlMEPsMap = (Map)MEPsMaplist.get(ij);
					 * 
					 * 
					 * 
					 * if(ij==0)
					 * 
					 * 
					 * 
					 * {
					 * 
					 * 
					 * 
					 * strIHSMFRNames=(String)mlMEPsMap.get("Mfr Name");
					 * 
					 * 
					 * 
					 * } else {
					 * 
					 * 
					 * 
					 * strIHSMFRNames=strIHSMFRNames+"~"+(String)mlMEPsMap.get("Mfr Name");
					 * 
					 * 
					 * 
					 * }
					 * 
					 * 
					 * 
					 * }
					 * 
					 * 
					 * 
					 * bw.write(strPartName+"|"+strSupplierName+
					 * "|"+"MFR mismatch. Part has following MFRs in IHS :"+strIHSMFRNames);
					 * 
					 * 
					 * 
					 * bw.newLine();
					 * 
					 * 
					 * 
					 * bw.close();
					 * 
					 * 
					 * 
					 * return "MFR mismatch. Part has following MFRs in IHS :"+strIHSMFRNames;
					 * 
					 * 
					 * 
					 * } else {
					 * 
					 * 
					 * 
					 * bw.write(strPartName+"|"+strSupplierName+
					 * "|"+"IHS does not contain Part Information for : "+strPartName);
					 * 
					 * 
					 * 
					 * bw.newLine();
					 * 
					 * 
					 * 
					 * bw.close();
					 * 
					 * 
					 * 
					 * return "IHS does not contain Part Information for : "+strPartName;
					 * 
					 * 
					 * 
					 * }
					 */

					// Modified for Issue 222 Ends
				}
			} else if (xmlResult.getStatus().equalsIgnoreCase("ERROR")) {
				String strErrorMSG = (String) xmlResult.getError().getValue();
				bw.write(strPartName + "|" + strSupplierName + "|" + strErrorMSG);
				bw.newLine();
				bw.close();
				return strErrorMSG;

			} else {
				bw.write(strPartName + "|" + strSupplierName + "|"
						+ "An Issue was encountered while accessing the IHS data. Please contact administrator");
				bw.newLine();
				bw.close();
				return "An Issue was encountered while accessing the IHS data. Please contact administrator";
			}
			return returnString;
		} catch (Exception e) {
			bw.write(strPartName + "|" + strSupplierName + "|" + "Exception in IHS data retrieval");
			bw.newLine();
			bw.close();
			e.printStackTrace();
			return "Exception in IHS data retrieval";
		} finally {
			bw.close();
		}
	}

	// written by kranthikiranc
	// below method updates the Part attributes info received from IHS
	// Also verify them against special characters
	public String updateMEPAttributes(Context context, ArrayList<Details> detailsList, DomainObject doMEP,
			String strSupplierName, StringList slInterfaceAttrList, String strClassName,
			Map<String, String> ihsAttrInfoMap, BufferedWriter bw, Properties prop) throws Exception {

		String strReturn = "";
		generated.Details details;
		String strAttrName = "";
		String strAttrID = "";
		String strAttrValue = "";
		Map<String, String> attrMap = new HashMap<String, String>();
		String strIHSAttrName = "";
		String strIHSAttrNameModified = "";
		String strmodifiedClassName = "";
		String strPropertyKey = "";
		String strLocale = context.getSession().getLanguage();
		String strPropertyKeyValue = "";
		String strIHSAttrValue = "";
		String strIHSObjectId = "";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";
		String strInterfaceName = PropertyUtil.getSchemaProperty(context, "interface_googPropsedDescription");
		String sgoogPropDesc = PropertyUtil.getSchemaProperty(context, "attribute_googPropsedDescription");

		String strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);
		// String strSupplierName = doMEP.getInfo(context,
		// DomainConstants.SELECT_REVISION);

		String strBadCharacters = "";
		boolean blnSpecialChar = false;

		StringList slAttrsWithSpecialChars = new StringList();

		Date date = new Date();
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(date);

		BufferedWriter bwSuccess = null;
		try {
			// Added for #199 : Remove IHSSubscription logic- Start
			SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");

			String strLogFilePath = prop.getProperty("XORGIHS.Log.path");
			String strSuccessFileHeader = prop.getProperty("XORGIHS.RealTime.Success.Log.header");
			File successLog = new File(strLogFilePath + "IHS_Success" + "_" + dateForLog.format(date) + ".log");

			if (!successLog.exists()) {
				FileWriter fw = new FileWriter(successLog, true);
				bwSuccess = new BufferedWriter(fw);
				bwSuccess.write(strSuccessFileHeader);
				bwSuccess.newLine();
			} else {
				FileWriter fw = new FileWriter(successLog, true);
				bwSuccess = new BufferedWriter(fw);
			}
			// Added for #199 : Remove IHSSubscription logic- End
			if (detailsList.size() > 0) {
				// Iterate through detailsList and add the key(attrbuteID),
				// Value(attributeValue) pairs to a Map
				Iterator<Details> itr = detailsList.iterator();
				while (itr.hasNext()) {
					details = (Details) itr.next();
					// strAttrName = (String)details.getName();
					strAttrID = (String) details.getId();
					strAttrValue = (String) details.getValue();

					if (strAttrID != "" && !strAttrID.equals(""))
						ihsAttrInfoMap.put(strAttrID, strAttrValue);
				}
			}
			/*
			 * String strPageFileName = "XORGIHSIntegrationMapping"; String MQLResult =
			 * MqlUtil.mqlCommand(context, "print page $1 select content dump",
			 * strPageFileName); byte[] bytes = MQLResult.getBytes("UTF-8"); InputStream
			 * input = new ByteArrayInputStream(bytes); Properties prop = new Properties();
			 * prop.load(input);
			 */
			// for loop to iterate through slInterfaceAttrList and get the corresponding
			// value from attrInfoMap
			for (int i = 0; i < slInterfaceAttrList.size(); i++) {
				strIHSAttrName = (String) slInterfaceAttrList.get(i);
				// renaming attribute by replacing " " with "_" & prefixing "IHS.Mapping." to
				// match with property file keys
				strIHSAttrNameModified = strIHSAttrName.replaceAll(" ", "_");
				strPropertyKey = "IHSAttrMapping_" + strClassName.replace(" ", "_") + "_" + strIHSAttrNameModified;

				strPropertyKeyValue = prop.getProperty(strPropertyKey);

				strIHSAttrValue = (String) ihsAttrInfoMap.get(strPropertyKeyValue);
				// validating special characters
				strBadCharacters = prop.getProperty("IHS.Attribute.Bad.characters");
				if (strIHSAttrValue != null && !"".equals(strIHSAttrValue)) {

					for (int j = 0; j < strIHSAttrValue.length(); j++) {
						if (!strPropertyKeyValue.equalsIgnoreCase("response_XML")) {
							if (strBadCharacters.contains(Character.toString(strIHSAttrValue.charAt(j)))) {
								blnSpecialChar = true;
								break;
							}
						}
					}
					if (!blnSpecialChar) {
						attrMap.put(strIHSAttrName, strIHSAttrValue);
						blnSpecialChar = false;
					} else {
						attrMap.put(strIHSAttrName, DomainConstants.EMPTY_STRING);
						slAttrsWithSpecialChars.add(strIHSAttrName);
						blnSpecialChar = false;
					}
				} else {
					attrMap.put(strIHSAttrName, DomainConstants.EMPTY_STRING);
				}
			}
			if (!(slAttrsWithSpecialChars.size() == 0)) {
				slInterfaceAttrList.removeAll(slAttrsWithSpecialChars);
				strReturn = "Invalid Characters found for below parameters. Please report the issue to your System Administrator"
						+ "\n\r" + slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "");
			}
			// DomainObject doMEP = new DomainObject(sPartId);
			// Setting attribute values on MEP
			// Added by Ravindra for Issue #64 Starts
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -1);
			Date date2 = cal.getTime();
			String strNewDueDate = _mxDateFormat.format(date2);
			doMEP.setAttributeValue(context, "IHSLast Sync Date", strNewDueDate);
			Iterator<String> iterator = attrMap.keySet().iterator();
			while (iterator.hasNext()) {
				String individualAttribute = iterator.next();
				String individualAttributeVal = (String)attrMap.get(individualAttribute);
				individualAttributeVal=individualAttributeVal.trim();
				if (individualAttribute.equals("IHSLast Sync Date")
						|| individualAttribute.equals("IHSResponse XML Previous")
						|| individualAttribute.equals("googLibraryRef")
						|| individualAttribute.equals("googFootPrintRef") || individualAttribute.equals("googPackType")
						|| individualAttribute.equals("googJEDECType") || individualAttribute.equals("googDBSheetName")
						|| individualAttribute.equals("googAltSymbols")
						|| individualAttribute.equals("googDFA_Dev_Class")) {
					iterator.remove();
				} else {
					if (UIUtil.isNullOrEmpty(individualAttributeVal) || individualAttributeVal.equals("0") || individualAttributeVal.equals("0.0"))
					{
						iterator.remove();
					}
				}
			}
			// Added by Ravindra for Issue #64 Ends
			String strExistingPrevRespXmlValue = doMEP.getAttributeValue(context, "IHSResponse XML Previous");
			doMEP.setAttributeValues(context, attrMap);
			
			String strExistingRespXmlValue="";
			//strExistingRespXmlValue = doMEP.getAttributeValue(context, "IHSResponse XML");
			strExistingRespXmlValue = attrMap.get("IHSResponse XML");
			if (UIUtil.isNullOrEmpty(strExistingRespXmlValue))
			{
				 strExistingRespXmlValue="";
			}
			if (UIUtil.isNullOrEmpty(strExistingPrevRespXmlValue))
			{
				doMEP.setAttributeValue(context, "IHSResponse XML Previous", strExistingRespXmlValue);
			}

			// below code will subscribe the Part for IHS Updates.- start
			strIHSObjectId = ihsAttrInfoMap.get((String) prop.getProperty(strPropKey_object_id));

			// --End
			if (strReturn != null && !"".equals(strReturn)) {
				strReturn = "Part sync with IHS done successfully. " + strReturn;
			} else {
				strReturn = "Part sync with IHS done successfully";
			}
			// Added for #199 : Remove IHSSubscription logic- Start
			String strLog = strPartName + "|" + strSupplierName + "|"
					+ slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "") + "|"
					+ "Part sync with IHS done successfully";
			bwSuccess.write(strLog);
			bwSuccess.newLine();
			bwSuccess.close();
			// Added for #199 : Remove IHSSubscription logic- End
			// String strSubscribeFlag =
			// subscribetoIHSUpdates(context,strPartName,strSupplierName,strIHSObjectId,slAttrsWithSpecialChars,prop);

			return strReturn;
		} catch (Exception E) {
			E.printStackTrace();
			bwSuccess.close();
			return "Unable to update Part attributes";
		} finally {
			bw.close();
			// modified for #199
			bwSuccess.close();
		}
	}

	// written by kranthikiranc
	// below method subscribes the Parts for IHS updates.
	public String subscribetoIHSUpdates(Context context, String strPartName, String strSupplierName, String strObjectID,
			StringList slAttrsWithSpecialChars, Properties prop) throws Exception {

		String returnString = "";
		String requestXML = "";
		String MQLResult = "";
		String strIHSUser = "";
		String strIHSPswrd = "";
		// String strPageFileName = "XORGIHSIntegrationMapping";
		String serverURL = "4donline.ihs.com";
		String serverFile = "/websvc/action/websvcaction";
		Proxy proxy = null;
		Date date = new Date();
		SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");

		String strLog = "";
		/*
		 * MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump",
		 * strPageFileName); byte[] bytes = MQLResult.getBytes("UTF-8"); InputStream
		 * input = new ByteArrayInputStream(bytes); Properties prop = new Properties();
		 * prop.load(input);
		 */
		BufferedWriter bw = null;
		String strLogFilePath = prop.getProperty("XORGIHS.Log.path");
		String strSuccessFileHeader = prop.getProperty("XORGIHS.RealTime.Success.Log.header");
		File successLog = new File(strLogFilePath + "IHS_Success" + "_" + dateForLog.format(date) + ".log");

		try {
			if (!successLog.exists()) {
				FileWriter fw = new FileWriter(successLog, true);
				bw = new BufferedWriter(fw);
				bw.write(strSuccessFileHeader);
				bw.newLine();
			} else {
				FileWriter fw = new FileWriter(successLog, true);
				bw = new BufferedWriter(fw);
			}
			String strSubscriptionStatus = "";

			// String strResponseXMLPath = prop.getProperty("XORGIHS.ResponseXMLFilePath");
			strIHSUser = prop.getProperty("IHS.credentials.username");
			strIHSPswrd = prop.getProperty("IHS.credentials.password");

			URL urlOrder = new URL("HTTPS", serverURL, 443, serverFile);
			URLConnection urlConn = (proxy == null) ? urlOrder.openConnection() : urlOrder.openConnection(proxy);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestProperty("Content-Type", "text/xml");
			urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConn.connect();

			requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<XMLQuery version=\"0.1\">"
					+ "<Login password=\"" + strIHSPswrd + "\" user-name=\"" + strIHSUser + "\"/>"
					+ "<Operation type=\"ADDOBJECTID\">" + "<Parameter name=\"obj-id\">" + strObjectID + "</Parameter>"
					+ " </Operation>" + "</XMLQuery>";
			// Get an output stream
			DataOutputStream dOut = new DataOutputStream(urlConn.getOutputStream());
			dOut.writeBytes(requestXML);
			dOut.flush();
			String encoding = urlConn.getContentEncoding();
			InputStream inStream = null;
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				inStream = new GZIPInputStream(urlConn.getInputStream());
			} else {
				inStream = urlConn.getInputStream();
			}
			Reader inputStreamReader = new InputStreamReader(inStream);
			// Reading XML reply to know subscription status-- start
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				strSubscriptionStatus = "SUBSCRIBED";
			} else if (xmlResult.getStatus().equalsIgnoreCase("ERROR")) {
				strSubscriptionStatus = (String) xmlResult.getError().getValue();
			}
			// --end
			inStream.close();
			dOut.close();
			strLog = strPartName + "|" + strSupplierName + "|"
					+ slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "") + "|"
					+ strSubscriptionStatus;

			bw.write(strLog);
			bw.newLine();
			bw.close();
			return returnString;
		} catch (Exception E) {
			strLog = strPartName + "|" + strSupplierName + "|"
					+ slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "") + "|";
			bw.write(strLog);
			bw.newLine();
			E.printStackTrace();
			return returnString;
		} finally {
			bw.close();
		}
	}

	// written by kranthikiranc
	// below method will be called for weekly IHS updates
	public String IHSWeeklyUpdatesOld(Context context, String[] args) throws Exception {
		String returnString = "";

		URLConnection urlConn;
		String encoding = "";
		Proxy proxy = null;

		String todate = "";
		String fromdate = "";
		String requestXML = "";
		String strPageFileName = "XORGIHSIntegrationMapping";
		String MQLResult = "";
		String strIHSUser = "";
		String strIHSPswrd = "";
		String nextpageid = "0";
		String serverURL = "";
		String serverFile = "";

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		String nextPageIdtmp = "0";
		String nextPageId = "";

		ArrayList<XMLResult> ListXMLResult = new ArrayList<XMLResult>();
		XMLResult xmlResult;

		MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
		byte[] bytes = MQLResult.getBytes("UTF-8");
		InputStream MQLinput = new ByteArrayInputStream(bytes);
		Properties prop = new Properties();
		prop.load(MQLinput);

		String strLogFileHeader = prop.getProperty("XORGIHS.Success.Log.header");

		String strSuccessLogFilePath = prop.getProperty("XORGIHS.Log.path");
		SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");

		File log = new File(strSuccessLogFilePath + "IHS_WeeklyUpdate" + "_" + dateForLog.format(date) + ".log");
		log.getParentFile().mkdirs();
		BufferedWriter bw = null;

		if (!log.exists()) {
			FileWriter fw = new FileWriter(log, true);
			bw = new BufferedWriter(fw);
			bw.write(strLogFileHeader);
			bw.newLine();
		} else {
			FileWriter fw = new FileWriter(log, true);
			bw = new BufferedWriter(fw);
		}

		String strNumberofDays = prop.getProperty("IHS.RegularUpdates.Frequency.NumberOfDays");

		String strConnectionTimeOut = "";
		String strReadTimeout = "";
		try {
			todate = dateFormat.format(date);

			cal.add(Calendar.DATE, Integer.parseInt("-" + strNumberofDays));
			Date todate1 = cal.getTime();
			fromdate = dateFormat.format(todate1);

			strIHSUser = prop.getProperty("IHS.credentials.username");
			strIHSPswrd = prop.getProperty("IHS.credentials.password");
			serverURL = prop.getProperty("IHS.credentials.serverURL");
			serverFile = prop.getProperty("IHS.credentials.serverFile");

			strConnectionTimeOut = prop.getProperty("IHS.WeeklyUpdate.connection.timeout");
			strReadTimeout = prop.getProperty("IHS.WeeklyUpdate.read.timeout");
			do {

				URL urlOrder = new URL("HTTPS", serverURL, 443, serverFile);
				urlConn = (proxy == null) ? urlOrder.openConnection() : urlOrder.openConnection(proxy);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestProperty("Content-Type", "text/xml");
				urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				urlConn.connect();
				urlConn.setConnectTimeout(Integer.parseInt(strConnectionTimeOut));
				urlConn.setReadTimeout(Integer.parseInt(strReadTimeout));

				requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<XMLQuery version=\"0.1\">"
						+ "<Login password=\"" + strIHSPswrd + "\" user-name=\"" + strIHSUser + "\"/>"
						+ "<Operation type=\"PAGEDUPDATE\">" + "<Parameter name=\"FROM\">" + fromdate + "</Parameter>"
						+ "<Parameter name=\"TO\">" + todate + "</Parameter>" + "<Parameter name=\"nextpageid\">"
						+ nextPageIdtmp + "</Parameter>" + "<Parameter name=\"rebuild\">false</Parameter>"
						+ " </Operation>" + "</XMLQuery>";
				DataOutputStream dOut = new DataOutputStream(urlConn.getOutputStream());
				dOut.writeBytes(requestXML);
				dOut.flush();
				encoding = urlConn.getContentEncoding();
				InputStream inStream = null;
				// based on encoding define input stream type and get it
				if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
					inStream = new GZIPInputStream(urlConn.getInputStream());
				} else {
					inStream = urlConn.getInputStream();
				}

				Reader inputStreamReader = new InputStreamReader(inStream);
				/*
				 * //start File targetFile = new
				 * File("C:/enoviaV6R2017x/IHSLogs/WeeklyUpdate.XML");
				 * //FileUtils.touch(targetFile); byte[] buffer =
				 * IOUtils.toByteArray(inputStreamReader);
				 * FileUtils.writeByteArrayToFile(targetFile, buffer);
				 * 
				 * //initialReader.close();
				 * 
				 * //end
				 */

				JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				XMLResult xmlResulttemp = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
				inputStreamReader.close();
				if (xmlResulttemp.getStatus().equalsIgnoreCase("SUCCESS")) {
					nextPageIdtmp = (String) xmlResulttemp.getNextpageid();
					ListXMLResult.add(xmlResulttemp);

					dOut.close();
				} else {
					bw.write("An Issue was encountered while accessing the IHS data. Please contact administrator");
					bw.newLine();
					bw.close();

					return "An Issue was encountered while accessing the IHS data. Please contact administrator";

				}
			} while ((Integer.parseInt(nextPageIdtmp)) > 0);
			for (int i = 0; i < ListXMLResult.size(); i++) {
				xmlResult = ListXMLResult.get(i);
				nextPageId = (String) xmlResult.getNextpageid();
				returnString = validateXMLResultforMEPExistance(context, xmlResult, bw, prop);

			}

			return returnString;
		} catch (Exception E) {
			bw.write("An Issue was encountered while accessing the IHS data. Please contact administrator");
			bw.newLine();
			bw.close();
			E.printStackTrace();
		} finally {
			bw.close();
		}

		return returnString;
	}

	public String validateXMLResultforMEPExistance(Context context, XMLResult xmlResult, BufferedWriter bw,
			Properties prop) throws Exception {
		String returnString = "";
		ArrayList<Object> PartOrMfrOrErrorList = new ArrayList<Object>();
		ArrayList<generated.Partdetails> ihsPartDetailsList = new ArrayList<generated.Partdetails>();
		ArrayList<generated.Result> ihsResultList = new ArrayList<generated.Result>();

		String strIIHSObjectID = "";
		String strIHSManufacturerPartNumber = "";

		// boolean strIHSDetailsTypeHAZMAT = false;
		boolean blnIHSDetailsTypeDOC = false;
		boolean blnIHSDetailsTypeDETAILS = false;
		String strWhere = "";
		String strIHSResultID = "";

		StringList slSelectable = new StringList(1);
		String attribute_IHSObjectID = PropertyUtil.getSchemaProperty(context, "attribute_IHSObjectID");

		String strMEPID = "";
		String strMEPName = "";
		// String strMEPRevision = "";
		String strSupplierName = "";
		String strIHSManufacturerFullName = "";

		// Added for Issue #360 Starts
		String strIHSDataSheetURL = "";
		String strIHSLatestDataSheetURL = "";
		// Added for Issue #360 Ends
		// kranthikiranc: Diverting to Ondemand IHSSync process - start
		String sError = "";
		Map<String, String> objMap = null;
		String[] methodargs = new String[1];
		// kranthikiranc: Diverting to Ondemand IHSSync process - end
		try {
			ihsResultList = (ArrayList<generated.Result>) xmlResult.getResult();
			for (int i = 0; i < ihsResultList.size(); i++) {
				generated.Result result = ihsResultList.get(i);
				strIHSResultID = result.getId();
				if (strIHSResultID.equalsIgnoreCase("DATAFEED")) {
					if ((Integer.parseInt(result.getCount())) > 0) {
						PartOrMfrOrErrorList = (ArrayList<Object>) result.getPartOrMfrOrError();

						for (int j = 0; j < PartOrMfrOrErrorList.size(); j++) {
							generated.Part part = (generated.Part) PartOrMfrOrErrorList.get(j);
							ihsPartDetailsList = (ArrayList<generated.Partdetails>) part.getPartdetails();

							// kranthikiranc: Diverting to Ondemand IHSSync process - start
							strIIHSObjectID = (String) part.getObjectId();
							strIHSManufacturerPartNumber = (String) part.getManufacturerPartNumber();
							strIHSManufacturerFullName = (String) part.getFullMfrName();
							strWhere = "policy=='Manufacturer Equivalent' && attribute[" + attribute_IHSObjectID + "]"
									+ "==" + strIIHSObjectID;
							slSelectable.add(DomainConstants.SELECT_ID);
							MapList mlMEPswithIHSObjID = DomainObject.findObjects(context, // context
									DomainConstants.TYPE_PART, // typePattern
									DomainConstants.QUERY_WILDCARD, // namePattern
									DomainConstants.QUERY_WILDCARD, // revPattern
									DomainConstants.QUERY_WILDCARD, // ownerPattern
									DomainConstants.QUERY_WILDCARD, // vaultPatern
									strWhere, // whereExpression
									false, // expandType
									slSelectable);// objectSelects

							if (mlMEPswithIHSObjID.size() == 1) {
								Map<String, String> mepMAPwithIHSObjID = (Map<String, String>) mlMEPswithIHSObjID
										.get(0);
								strMEPID = (String) mepMAPwithIHSObjID.get(DomainConstants.SELECT_ID);
								methodargs[0] = strMEPID;
								sError = (String) JPO.invoke(context, "XORGIHSIntegration", null, "syncWithIHS",
										methodargs, String.class);
								if (sError != null && !"".equals(sError)) {
									bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
											+ strIIHSObjectID + "|" + sError);
									bw.newLine();
								} else {
									bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
											+ strIIHSObjectID + "|" + "Success-Refer IHS Logs|");
									bw.newLine();
								}

							} else if (mlMEPswithIHSObjID.size() > 1) {
								// bw.write(strIHSManufacturerPartNumber+"|"+strIHSManufacturerFullName+"|"+strIIHSObjectID+"|"+"Duplicate
								// Parts Found");
								// bw.newLine();
								Iterator<Map<String, String>> objItr = (Iterator) mlMEPswithIHSObjID.iterator();
								while (objItr.hasNext()) {
									Map<String, String> mepMAPwithIHSObjID = (Map<String, String>) objItr.next();
									strMEPID = (String) mepMAPwithIHSObjID.get(DomainConstants.SELECT_ID);
									methodargs[0] = strMEPID;
									sError = (String) JPO.invoke(context, "XORGIHSIntegration", null, "syncWithIHS",
											methodargs, String.class);
									if (sError != null && !"".equals(sError)) {
										bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
												+ strIIHSObjectID + "|" + sError);
										bw.newLine();
									} else {
										bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
												+ strIIHSObjectID + "|" + "Success-Refer IHS Logs|");
										bw.newLine();
									}
								}
							} else if (mlMEPswithIHSObjID.size() == 0) {
								bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
										+ strIIHSObjectID + "|" + "Fail-No Parts Found" + "|");
								bw.newLine();
							}

							// kranthikiranc : Diverting to Ondemand IHSSync process - end
							// commenting code since it is not needed anymore due to above changes. - start
							/*
							 * for(int k=0;k<ihsPartDetailsList.size();k++) {
							 * 
							 * generated.Partdetails ihsPartDetails = ihsPartDetailsList.get(k);
							 * if(ihsPartDetails.getType().equalsIgnoreCase("DOC")) { blnIHSDetailsTypeDOC =
							 * true;
							 * 
							 * }else if(ihsPartDetails.getType().equalsIgnoreCase("DETAILS")) {
							 * blnIHSDetailsTypeDETAILS = true;
							 * 
							 * } }
							 * 
							 * if(blnIHSDetailsTypeDOC || blnIHSDetailsTypeDETAILS){ //Diverting to Ondemand
							 * IHSSync process - start //strIIHSObjectID =(String) part.getObjectId();
							 * //strIHSManufacturerPartNumber = (String) part.getManufacturerPartNumber();
							 * //strIHSManufacturerFullName = (String) part.getFullMfrName(); //Diverting to
							 * Ondemand IHSSync process - end //strWhere =
							 * "attribute["+attribute_IHSObjectID+"]"+"=="+strIIHSObjectID; strWhere =
							 * "name=="+"'"+strIHSManufacturerPartNumber+"'";
							 * slSelectable.add(DomainConstants.SELECT_ID);
							 * slSelectable.add(DomainConstants.SELECT_NAME);
							 * slSelectable.add(DomainConstants.SELECT_REVISION);
							 * slSelectable.add("attribute["+PropertyUtil.getSchemaProperty(context,
							 * "attribute_IHSDatasheetURL" )+"].value");//Added for Issue #360
							 * slSelectable.add("attribute["+PropertyUtil.getSchemaProperty(context,
							 * "attribute_IHSLatestDataSheetURL" )+"].value");//Added for Issue #360
							 * slSelectable.add("relationship["+DomainConstants.
							 * RELATIONSHIP_MANUFACTURING_RESPONSIBILITY+"].from.name");
							 * 
							 * MapList mlMEPs = DomainObject.findObjects(context, //context
							 * DomainConstants.TYPE_PART, //typePattern DomainConstants.QUERY_WILDCARD,
							 * //namePattern DomainConstants.QUERY_WILDCARD, //revPattern
							 * DomainConstants.QUERY_WILDCARD, //ownerPattern
							 * DomainConstants.QUERY_WILDCARD, //vaultPatern strWhere, //whereExpression
							 * false, //expandType slSelectable);//objectSelects if(mlMEPs.size()==1) {
							 * Map<String, String> mepMAP = (Map<String, String>)mlMEPs.get(0); strMEPID =
							 * (String)mepMAP.get(DomainConstants.SELECT_ID); strMEPName =
							 * (String)mepMAP.get(DomainConstants.SELECT_NAME); //strMEPRevision =
							 * (String)mepMAP.get(DomainConstants.SELECT_REVISION); strSupplierName =
							 * (String)mepMAP.get("relationship["+DomainConstants.
							 * RELATIONSHIP_MANUFACTURING_RESPONSIBILITY+"].from.name");
							 * mepMAP.put("strIIHSObjectID", strIIHSObjectID);
							 * mepMAP.put("strIHSManufacturerPartNumber", strIHSManufacturerPartNumber);
							 * mepMAP.put("strIHSManufacturerFullName", strIHSManufacturerFullName); //Added
							 * for Issue #360 Starts strIHSDataSheetURL =
							 * (String)mepMAP.get("attribute["+PropertyUtil.getSchemaProperty(context,
							 * "attribute_IHSDatasheetURL" )+"].value"); strIHSLatestDataSheetURL =
							 * (String)mepMAP.get("attribute["+PropertyUtil.getSchemaProperty(context,
							 * "attribute_IHSLatestDataSheetURL" )+"].value");
							 * mepMAP.put("strIHSDataSheetURL", strIHSDataSheetURL);
							 * mepMAP.put("strIHSLatestDataSheetURL", strIHSLatestDataSheetURL); //Added for
							 * Issue #360 Ends returnString =
							 * verifyMEPClassification(context,part,mepMAP,bw,prop);
							 * 
							 * } else if(mlMEPs.size()>1){
							 * //bw.write("Duplicate MEPs found with same IHS Object ID"+" "+strIIHSObjectID
							 * +"."+" "+"Please contact administrator"+"\n");
							 * bw.write(strIHSManufacturerPartNumber+"|"+strIHSManufacturerFullName+"|"+
							 * strIIHSObjectID+"|"+"Duplicate Parts Found"); bw.newLine();
							 * 
							 * } else if(mlMEPs.size()==0){
							 * //bw.write("NO MEPs found with IHS Object ID"+" "+strIIHSObjectID+"."+" "
							 * +"Please contact administrator."+"\n");
							 * bw.write(strIHSManufacturerPartNumber+"|"+strIHSManufacturerFullName+"|"+
							 * strIIHSObjectID+"|"+"Fail-No Parts Found"+"|"); bw.newLine();
							 * 
							 * } }
							 */
							// commenting code since it is not needed anymore due to above changes. - end
						}

					} else {

						bw.write("No updates found in IHS");
						bw.newLine();
						bw.close();
						return "No updates found in IHS ";
					}
				}

			}
			return returnString;
		} catch (Exception E) {
			E.printStackTrace();
			bw.write("Error while running IHS Weekly Updates");
			bw.newLine();
			bw.close();
			E.printStackTrace();
			return "Error while running IHS Weekly Updates";
		} finally {
			// bw.close();
		}
	}

	public String verifyMEPClassification(Context context, generated.Part part, Map<String, String> mepMAP,
			BufferedWriter bw, Properties prop) throws Exception {
		String returnString = "";
		Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
		// ArrayList<generated.Partdetails> ihsPartDetailsList = new
		// ArrayList<generated.Partdetails>();
		generated.Partdetails ihsPartDetails;
		String strIHSPartDetailsType = "";

		String strMEPID = (String) mepMAP.get(DomainConstants.SELECT_ID);
		String strMEPName = (String) mepMAP.get(DomainConstants.SELECT_NAME);
		ArrayList<Doc> docList;
		generated.Doc doc = new generated.Doc();

		String strIHSDocURL = "";
		String strIHSDocType = "";

		String strIHSClassification = "";
		String strClassificationObjectID = "";

		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value");
		String strSelect = "attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value";
		Map<String, String> objMap = null;
		StringList slClassification = new StringList();
		String strClassificationInterface = "";
		ArrayList<Details> detailsList = new ArrayList<Details>();
		StringList slInterfaceAttrList = new StringList();
		StringList slSelect = new StringList();
		slSelect.addElement(DomainConstants.SELECT_ID);
		// slSelect.addElement(DomainConstants.SELECT_NAME);
		slSelect.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
		Map<String, String> tempClassificationMap = new HashMap<String, String>();
		String strClassificationID = "";
		String sError = "";
		try {
			strIHSClassification = part.getPartType();
			if (strIHSClassification != null && !"".equals(strIHSClassification)) {
				DomainObject doMEP = new DomainObject(strMEPID);
				// strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);

				MapList mlObjects = doMEP.getRelatedObjects(context,
						LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
						LibraryCentralConstants.TYPE_GENERAL_CLASS, // object pattern
						selectStmts, // object selects
						null, // relationship selects
						true, // to direction
						true, // from direction
						(short) 1, // recursion level
						null, // object where clause
						DomainConstants.EMPTY_STRING, 0);
				if (mlObjects.size() == 0) {// If a MEP is not already classified with IHS classification,
											// redirecting to RealTime IHS Sync. Logs will be captured as part of IHS
											// Sync in UI.
					Map<String, String> programMap = new HashMap();
					// programMap.put("objectId", strMEPID);
					String[] methodargs = new String[1];
					methodargs[0] = strMEPID;
					sError = (String) JPO.invoke(context, "XORGIHSIntegration", null, "syncWithIHS", methodargs,
							String.class);
					if (sError != null && !"".equals(sError)) {
						bw.write((String) mepMAP.get("strIHSManufacturerPartNumber") + "|"
								+ (String) mepMAP.get("strIHSManufacturerFullName") + "|"
								+ (String) mepMAP.get("strIIHSObjectID") + "|" + sError);
						bw.newLine();
					} else {
						bw.write((String) mepMAP.get("strIHSManufacturerPartNumber") + "|"
								+ (String) mepMAP.get("strIHSManufacturerFullName") + "|"
								+ (String) mepMAP.get("strIIHSObjectID") + "|" + "Success|");
						bw.newLine();
					}
					return sError;
				}
				Iterator<Map<String, String>> objItr = (Iterator) mlObjects.iterator();
				while (objItr.hasNext()) {
					objMap = (Map<String, String>) objItr.next();
					slClassification.add((String) objMap.get(DomainConstants.SELECT_NAME));
					if (((String) objMap.get(DomainConstants.SELECT_NAME)).equalsIgnoreCase(strIHSClassification)) {
						// strClassificationObjectID = (String)objMap.get(DomainConstants.SELECT_ID);
						strClassificationInterface = (String) objMap
								.get("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value");
					}
				}

				if (!slClassification.contains(strIHSClassification)) {
					MapList mlClassification = DomainObject.findObjects(context, // context
							LibraryCentralConstants.TYPE_GENERAL_CLASS, // typePattern
							strIHSClassification, // namePattern
							DomainConstants.QUERY_WILDCARD, // revPattern
							DomainConstants.QUERY_WILDCARD, // ownerPattern
							DomainConstants.QUERY_WILDCARD, // vaultPatern
							DomainConstants.EMPTY_STRING, // whereExpression
							false, // expandType
							slSelect);// objectSelects

					if (mlClassification.size() == 1) {

						tempClassificationMap = (Map<String, String>) mlClassification.get(0);
						strClassificationObjectID = tempClassificationMap.get(DomainConstants.SELECT_ID);
						strClassificationInterface = tempClassificationMap
								.get("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");

						DomainRelationship.connect(context, new DomainObject(strClassificationObjectID),
								LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, new DomainObject(strMEPID));

					} else if (mlClassification.size() == 0) {
						// bw.write(strPartName+"|"+"No Appropriate Classification Found");
						bw.write((String) mepMAP.get("strIHSManufacturerPartNumber") + "|"
								+ (String) mepMAP.get("strIHSManufacturerFullName") + "|"
								+ (String) mepMAP.get("strIIHSObjectID") + "|" + "No Appropriate Classification Found"
								+ "|" + strIHSClassification);
						bw.newLine();
						// bw.close();
						return "No Appropriate IHS Classification Found to classify:" + strIHSClassification;
					}

				}
				// getting the interface attributes

				if (strClassificationInterface != null && !"".equals(strClassificationInterface)) {
					String strMQL = "print interface $1 select attribute dump";
					String resStr = MqlUtil.mqlCommand(context, strMQL, true, strClassificationInterface);
					StringList slMQLResult = FrameworkUtil.split(resStr, ",");
					if (slMQLResult.size() > 0) {
						slInterfaceAttrList.addAll(slMQLResult);

					}
				}
				returnString = WeeklyUpdateMEPAttributesFromIHS(context, part, mepMAP, slInterfaceAttrList,
						strIHSClassification, bw, prop);
				return returnString;
			} else {
				String[] methodargs = new String[1];
				methodargs[0] = strMEPID;
				sError = (String) JPO.invoke(context, "XORGIHSIntegration", null, "syncWithIHS", methodargs,
						String.class);
				if (sError != null && !"".equals(sError)) {
					bw.write((String) mepMAP.get("strIHSManufacturerPartNumber") + "|"
							+ (String) mepMAP.get("strIHSManufacturerFullName") + "|"
							+ (String) mepMAP.get("strIIHSObjectID") + "|" + sError);
					bw.newLine();
				} else {
					bw.write((String) mepMAP.get("strIHSManufacturerPartNumber") + "|"
							+ (String) mepMAP.get("strIHSManufacturerFullName") + "|"
							+ (String) mepMAP.get("strIIHSObjectID") + "|" + "Success|");
					bw.newLine();
				}
				return sError;
			}

		} catch (Exception E) {
			E.printStackTrace();
			bw.write("Exception While Updating IHS info.Please contact administrator");
			bw.newLine();
			// bw.close();
			return returnString;
		}
	}

	public String WeeklyUpdateMEPAttributesFromIHS(Context context, generated.Part part, Map<String, String> mepMAP,
			StringList slInterfaceAttrList, String strIHSClassification, BufferedWriter bw, Properties prop)
			throws Exception {
		String returnString = "";
		Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();

		String strMEPID = (String) mepMAP.get(DomainConstants.SELECT_ID);
		String strMEPName = (String) mepMAP.get(DomainConstants.SELECT_NAME);
		// String strMEPRevision = (String)mepMAP.get(DomainConstants.SELECT_REVISION);
		String strSupplierName = (String) mepMAP
				.get("relationship[" + DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name");

		// Added for Issue #360 Starts
		String strIHSDataSheetURL = (String) mepMAP.get("strIHSDataSheetURL");
		String strIHSLatestDataSheetURL = (String) mepMAP.get("strIHSLatestDataSheetURL");
		// Added for Issue #360 Ends

		String strPageFileName = "XORGIHSIntegrationMapping";

		String strPropKey_Category = "IHS.Mapping.IHSCategory";
		String strPropKey_mfr_name = "IHS.Mapping.IHSManufacturer_Name";
		String strPropKey_Manufacturer_part_number = "IHS.Mapping.IHSManufacturer_Part_Number";
		String strPropKey_mfg_pkg_desc = "IHS.Mapping.IHSMfr_Package_Description";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";
		String strPropKey_cage_code = "IHS.Mapping.IHSCAGE_Code";
		String strPropKey_part_desc = "IHS.Mapping.IHSPart_Description";
		String strPropKey_part_status = "IHS.Mapping.IHSStatus";
		String strPropKey_doc_url = "IHS.Mapping.IHSDatasheet_URL";
		String strPropKey_latest_doc_url = "IHS.Mapping.IHSLatest_DataSheet_URL";// Added for Issue #360

		generated.Partdetails partDetails = new generated.Partdetails();
		String strPartDetailsType = "";
		ArrayList<Details> IHSdetailsList = new ArrayList<Details>();
		generated.Details details;
		String strIHSAttrID = "";
		String strIHSAttrValue = "";
		ArrayList<Doc> IHSdocList;
		String strIHSDocURL = "";
		// Added for Issue #360 Starts
		String strDocURLSorted = "";
		String strIHSLatestDocURL = "";
		Map<String, String> docURLDateMap = new HashMap<String, String>();
		String strDocDate = "";
		// Added for Issue #360 Ends
		String strDocTitle = "";
		generated.Doc doc = new generated.Doc();

		Map<String, String> attrMap = new HashMap<String, String>();
		String strIHSAttrName = "";
		String strIHSAttrNameModified = "";
		String strPropertyKey = "";
		String strPropertyKeyValue = "";

		String strBadCharacters = "";
		boolean blnSpecialChar = false;

		StringList slAttrsWithSpecialChars = new StringList();

		// String strIHSClassification = part.getPartType();

		try {
			/*
			 * String MQLResult = MqlUtil.mqlCommand(context,
			 * "print page $1 select content dump", strPageFileName); byte[] bytes =
			 * MQLResult.getBytes("UTF-8"); InputStream input = new
			 * ByteArrayInputStream(bytes); Properties prop = new Properties();
			 * prop.load(input);
			 */
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Category), part.getCategory());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfr_name), part.getMfrName());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_Manufacturer_part_number),
					part.getManufacturerPartNumber());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_mfg_pkg_desc), part.getMfgPkgDesc());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_object_id), part.getObjectId());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_cage_code), part.getCageCode());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_desc), part.getPartDescription());
			ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_part_status), part.getPartStatus());

			ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part.getPartdetails();

			for (int i = 0; i < partDetailsList.size(); i++) {
				partDetails = partDetailsList.get(i);
				strPartDetailsType = partDetails.getType();

				if (strPartDetailsType.equalsIgnoreCase("DOC")) {
					IHSdocList = (ArrayList<Doc>) partDetails.getDoc();
					for (int j = 0; j < IHSdocList.size(); j++) {
						doc = IHSdocList.get(j);
						if (doc.getDocType().equalsIgnoreCase("Datasheet")) {
							if (j == 0) {
								strDocTitle = doc.getDocTitle();
								strDocDate = doc.getPubDate();// Added for Issue #360
								// strIHSDocURL = doc.getDocUrl();
								strIHSDocURL = strDocTitle + ":" + "\n" + doc.getDocUrl();
								if (strIHSDataSheetURL != null && !strIHSDataSheetURL.isEmpty()
										&& strIHSDataSheetURL.contains(strIHSDocURL)) {
									strIHSDocURL = "";
								} else {
									docURLDateMap.put(strDocDate, strIHSDocURL);// Added for Issue #360
								}

							} else {
								strDocTitle = doc.getDocTitle();
								strDocDate = doc.getPubDate();// Added for Issue #360
								// strIHSDocURL = strIHSDocURL +"\n"+doc.getDocUrl();
								// strIHSDocURL = strIHSDocURL +"\n"+strDocTitle+":"+"\n"+doc.getDocUrl();
								// docURLDateMap.put(strDocDate,strDocTitle+":"+"\n"+doc.getDocUrl());//Added
								// for Issue #360
								String stempstr = strDocTitle + ":" + "\n" + doc.getDocUrl();
								if (strIHSDataSheetURL != null && !strIHSDataSheetURL.isEmpty()
										&& strIHSDataSheetURL.contains(stempstr)) {
									// strIHSDocURL = strIHSDocURL;
								} else {
									strIHSDocURL = strIHSDocURL + "\n" + strDocTitle + ":" + "\n" + doc.getDocUrl();
									docURLDateMap.put(strDocDate, strDocTitle + ":" + "\n" + doc.getDocUrl());// Added
																												// for
																												// Issue
																												// #360
								}

							}
						}
					}
					// Added for Issue #360 Starts
					StringList sSortedDateList = getSortedDateList(context, docURLDateMap);

					for (int kk = 0; kk < sSortedDateList.size(); kk++) {
						String strDateKey = (String) sSortedDateList.get(kk);
						String strUrl = (String) docURLDateMap.get(strDateKey);
						if (kk == 0) {
							strDocURLSorted = strUrl;
						} else {
							strDocURLSorted = strDocURLSorted + "\n" + strUrl;
						}
					}
					if (strIHSDataSheetURL != null && !strIHSDataSheetURL.isEmpty()) {
						if (strDocURLSorted != null && !strDocURLSorted.isEmpty()) {
							strDocURLSorted = strDocURLSorted + "\n" + strIHSDataSheetURL;
						} else {
							strDocURLSorted = strIHSDataSheetURL;
						}

					}

					ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_doc_url), strDocURLSorted);

					StringList strLatestDocURLLst = FrameworkUtil.split(strDocURLSorted, "\n");
					strIHSLatestDocURL = (String) strLatestDocURLLst.get(0) + "\n" + (String) strLatestDocURLLst.get(1);

					ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_latest_doc_url), strIHSLatestDocURL);
					// Added for Issue #360 Ends
				} else if (strPartDetailsType.equalsIgnoreCase("DETAILS")) {
					// code to retrieve part details info into a Map
					IHSdetailsList = (ArrayList<Details>) partDetails.getDetails();
					if (IHSdetailsList.size() > 0) {
						// Iterate through detailsList and add the key(attrbuteID),
						// Value(attributeValue) pairs to a Map
						Iterator<Details> itr = IHSdetailsList.iterator();
						while (itr.hasNext()) {
							details = (Details) itr.next();
							// strAttrName = (String)details.getName();
							strIHSAttrID = (String) details.getId();
							strIHSAttrValue = (String) details.getValue();

							if (strIHSAttrID != "" && !strIHSAttrID.equals(""))
								ihsAttrInfoMap.put(strIHSAttrID, strIHSAttrValue);
						}
					}
				}
			}
			if (strIHSClassification != null && !"".equals(strIHSClassification)) {
				for (int i = 0; i < slInterfaceAttrList.size(); i++) {
					strIHSAttrName = (String) slInterfaceAttrList.get(i);
					// renaming attribute by replacing " " with "_" & prefixing "IHS.Mapping." to
					// match with property file keys
					strIHSAttrNameModified = strIHSAttrName.replaceAll(" ", "_");
					strPropertyKey = "IHSAttrMapping_" + strIHSClassification.replace(" ", "_") + "_"
							+ strIHSAttrNameModified;

					strPropertyKeyValue = prop.getProperty(strPropertyKey);

					strIHSAttrValue = (String) ihsAttrInfoMap.get(strPropertyKeyValue);
					strBadCharacters = prop.getProperty("IHS.Attribute.Bad.characters");

					if (strIHSAttrValue != null && !"".equals(strIHSAttrValue)) {
						for (int j = 0; j < strIHSAttrValue.length(); j++) {
							if (strBadCharacters.contains(Character.toString(strIHSAttrValue.charAt(j)))) {
								blnSpecialChar = true;
								break;
							}
						}
						if (!blnSpecialChar) {
							attrMap.put(strIHSAttrName, strIHSAttrValue);
							blnSpecialChar = false;
						} else {
							slAttrsWithSpecialChars.add(strIHSAttrName);
							blnSpecialChar = false;
						}
					}
				}
				if (!(slAttrsWithSpecialChars.size() == 0)) {
					slInterfaceAttrList.removeAll(slAttrsWithSpecialChars);
					returnString = slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "");
				}
			} else {
				// attrMap.put(PropertyUtil.getSchemaProperty(context,"attribute_IHSDatasheetURL"),strDocURLSorted);//Added
				// for issue #360
				// attrMap.put(PropertyUtil.getSchemaProperty(context,"attribute_IHSLatestDataSheetURL"),strIHSLatestDocURL);//Added
				// for issue #360
			}
			// Setting attribute values on MEP
			DomainObject doMEP = new DomainObject(strMEPID);
			// Added by Ravindra for Issue #64 Starts
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -1);
			Date date = cal.getTime();
			String strNewDueDate = _mxDateFormat.format(date);
			doMEP.setAttributeValue(context, "IHSLast Sync Date", strNewDueDate);
			Iterator<String> iterator = attrMap.keySet().iterator();
			while (iterator.hasNext()) {
				String individualAttribute = iterator.next();
				String individualAttributeVal = (String)attrMap.get(individualAttribute);
				individualAttributeVal=individualAttributeVal.trim();
				if (individualAttribute.equals("IHSLast Sync Date")
						|| individualAttribute.equals("IHSResponse XML Previous")
						|| individualAttribute.equals("googLibraryRef")
						|| individualAttribute.equals("googFootPrintRef") || individualAttribute.equals("googPackType")
						|| individualAttribute.equals("googJEDECType") || individualAttribute.equals("googDBSheetName")
						|| individualAttribute.equals("googAltSymbols")
						|| individualAttribute.equals("googDFA_Dev_Class")) {
					iterator.remove();
				} else {
					if (UIUtil.isNullOrEmpty(individualAttributeVal) || individualAttributeVal.equals("0") || individualAttributeVal.equals("0.0"))
					{
						iterator.remove();
					}
				}
			}
			// Added by Ravindra for Issue #64 Ends
			String strExistingPrevRespXmlValue = doMEP.getAttributeValue(context, "IHSResponse XML Previous");
			doMEP.setAttributeValues(context, attrMap);
			
			String strExistingRespXmlValue="";
			//strExistingRespXmlValue = doMEP.getAttributeValue(context, "IHSResponse XML");
			strExistingRespXmlValue = attrMap.get("IHSResponse XML");
			if (UIUtil.isNullOrEmpty(strExistingRespXmlValue))
			{
				 strExistingRespXmlValue="";
			}
			if (UIUtil.isNullOrEmpty(strExistingPrevRespXmlValue))
			{
				doMEP.setAttributeValue(context, "IHSResponse XML Previous", strExistingRespXmlValue);
			}
			if (returnString != null && !"".equals(returnString)) {
				returnString = strMEPName + "|" + strSupplierName + "|" + mepMAP.get("strIIHSObjectID") + "|"
						+ "Success" + "|" + returnString;
			} else
				returnString = strMEPName + "|" + strSupplierName + "|" + mepMAP.get("strIIHSObjectID") + "|"
						+ "Success" + "|";

			bw.write(returnString);
			bw.newLine();

			return returnString;
		} catch (Exception E) {
			E.printStackTrace();
			bw.write(strMEPName + "|" + strSupplierName + "|" + mepMAP.get("strIIHSObjectID") + "|" + "Fail:"
					+ E.toString() + "|");
			bw.newLine();
			return returnString;
		} finally {
			// bw.close();
		}
	}

	/**
	 * Method to return Obsoleted MEPs in IHS
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */

	@com.matrixone.apps.framework.ui.ProgramCallable
	public MapList getIHSObsoletedParts(Context context, String[] args) throws Exception {

		MapList result = new MapList();
		try {

			String sIHSState = PropertyUtil.getSchemaProperty(context, "attribute_IHSStatus");
			StringBuffer objectWhere = new StringBuffer();
			objectWhere.append("(attribute[");
			objectWhere.append(sIHSState);
			objectWhere.append("] == EOL || attribute[");
			objectWhere.append(sIHSState);
			objectWhere.append("] == Discontinued) && policy ==\"");
			objectWhere.append(POLICY_MANUFACTURER_EQUIVALENT);
			objectWhere.append("\"");
			objectWhere.append("&& current != 'Obsolete'");

			SelectList selectList = new SelectList(5);
			selectList.add(DomainObject.SELECT_TYPE);
			selectList.add(DomainObject.SELECT_NAME);
			selectList.add(DomainObject.SELECT_REVISION);
			selectList.add(DomainObject.SELECT_CURRENT);
			selectList.add(SELECT_DESCRIPTION);
			selectList.add(SELECT_ID);
			selectList.add("attribute[" + sIHSState + "].value");

			String strType = DomainConstants.TYPE_PART;
			String strOwnerCondition = null;
			result = findObjects(context, strType, null, null, strOwnerCondition, DomainConstants.QUERY_WILDCARD,
					objectWhere.toString(), true, selectList);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to generate a IHS
	 * updated details report for the time period specified by the user (Called from
	 * JSP)
	 * 
	 * @param context
	 * @param args
	 * @return String of HTML report table
	 * @throws Exception
	 */
	public String getIHSWeeklyUpdateReport(Context context, String[] args) throws Exception {
		ContextUtil.startTransaction(context, false);
		HashMap hmProgramMap = (HashMap) JPO.unpackArgs(args);
		String strFromDate = (String) hmProgramMap.get("strFromDate");
		String strToDate = (String) hmProgramMap.get("strToDate");
		String strExportToExcel = (String) hmProgramMap.get("exportToExcel");
		// Added by Lalitha --starts
		String strbackground = (String) hmProgramMap.get("backgroundSubmit");
		// Added by Lalitha --Ends
		// Added by Lalitha on 1/10/2019 --starts
		String strReturnHTML = "IHS Updated Part Report From : " + strFromDate + " To : " + strToDate + "\n";
		// Added by Lalitha on 1/10/2019 --Ends
		// Modified by Lalitha on 1/10/2019 --starts
		strReturnHTML += "<table border='1' class='list'>";
		// Modified by Lalitha on 1/10/2019 --Ends
		// Modified for Issue #281 Starts
		strReturnHTML += "<tr><th class='sorted' style='width: 200px;'>Name</th><th style='width: 200px;' class='sorted'>Description</th><th style='width: 400px;' class='sorted'>Attribute Details</th><th class='sorted' style='width: 200px;'>PLM Part Number</th><th class='sorted' style='width: 200px;'>Manufacturer</th></tr>";
		if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
			// Modified by Lalitha --starts
			strFromDate = strFromDate.replace(",", "");
			strToDate = strToDate.replace(",", "");
			strReturnHTML = "IHS Updated Part Report" + "\t" + "From:" + strFromDate + "\t" + "To :" + strToDate + "\n";
			strReturnHTML += "Name\tDescription\tAttribute Details\tPLM Part Number\tManufacturer\n";
			// Modified by Lalitha --Ends
		}
		// Modified for Issue #281 Ends
		String returnString = "";
		URLConnection urlConn;
		String encoding = "";
		Proxy proxy = null;
		String todate = "";
		String fromdate = "";
		String requestXML = "";
		String strPageFileName = "XORGIHSIntegrationMapping";
		String MQLResult = "";
		String strIHSUser = "";
		String strIHSPswrd = "";
		String nextpageid = "0";
		String serverURL = "";
		String serverFile = "";

		Date date = new Date(strToDate);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		String nextPageIdtmp = "0";
		String nextPageId = "";

		MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
		byte[] bytes = MQLResult.getBytes("UTF-8");
		InputStream MQLinput = new ByteArrayInputStream(bytes);
		Properties prop = new Properties();
		prop.load(MQLinput);

		// String strNumberofDays =
		// prop.getProperty("IHS.RegularUpdates.Frequency.NumberOfDays");
		String strConnectionTimeOut = "";
		String strReadTimeout = "";
		try {
			todate = dateFormat.format(date);
			// cal.add(Calendar.DATE, Integer.parseInt("-"+strNumberofDays));
			// Date todate1 = cal.getTime();
			Date dFromDate = new Date(strFromDate);
			fromdate = dateFormat.format(dFromDate);

			strIHSUser = prop.getProperty("IHS.credentials.username");
			strIHSPswrd = prop.getProperty("IHS.credentials.password");
			serverURL = prop.getProperty("IHS.credentials.serverURL");
			serverFile = prop.getProperty("IHS.credentials.serverFile");
			strConnectionTimeOut = prop.getProperty("IHS.WeeklyUpdate.connection.timeout");
			strReadTimeout = prop.getProperty("IHS.WeeklyUpdate.read.timeout");
			String strCSSClass = "even";
			do {
				URL urlOrder = new URL("HTTPS", serverURL, 443, serverFile);
				urlConn = (proxy == null) ? urlOrder.openConnection() : urlOrder.openConnection(proxy);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestProperty("Content-Type", "text/xml");
				urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				urlConn.connect();
				urlConn.setConnectTimeout(Integer.parseInt(strConnectionTimeOut));
				urlConn.setReadTimeout(Integer.parseInt(strReadTimeout));

				requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<XMLQuery version=\"0.1\">"
						+ "<Login password=\"" + strIHSPswrd + "\" user-name=\"" + strIHSUser + "\"/>"
						+ "<Operation type=\"PAGEDUPDATE\">" + "<Parameter name=\"FROM\">" + fromdate + "</Parameter>"
						+ "<Parameter name=\"TO\">" + todate + "</Parameter>" + "<Parameter name=\"nextpageid\">"
						+ nextPageIdtmp + "</Parameter>" + "<Parameter name=\"rebuild\">false</Parameter>"
						+ " </Operation>" + "</XMLQuery>";

				DataOutputStream dOut = new DataOutputStream(urlConn.getOutputStream());
				dOut.writeBytes(requestXML);
				dOut.flush();
				encoding = urlConn.getContentEncoding();
				InputStream inStream = null;
				// based on encoding define input stream type and get it
				if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
					inStream = new GZIPInputStream(urlConn.getInputStream());
				} else {
					inStream = urlConn.getInputStream();
				}
				String strXMLResult = IOUtils.toString(inStream, StandardCharsets.UTF_8);
				// Modified by Lalitha --starts
				if (strbackground != null && "bgSubmit".equals(strbackground)) {
					HashMap hmReportMap = parseXMLAndShowReport(context, strXMLResult, prop, strCSSClass,
							strExportToExcel, strbackground);
					nextPageIdtmp = (String) hmReportMap.get("nextPageIdtmp");
					strCSSClass = (String) hmReportMap.get("strCSSClass");
					strReturnHTML += (String) hmReportMap.get("strReturnHTML");
				} else {

					HashMap hmReportMap = parseXMLAndShowReport(context, strXMLResult, prop, strCSSClass,
							strExportToExcel);
					nextPageIdtmp = (String) hmReportMap.get("nextPageIdtmp");
					strCSSClass = (String) hmReportMap.get("strCSSClass");
					strReturnHTML += (String) hmReportMap.get("strReturnHTML");
				}
				// Modified by Lalitha --Ends
			} while ((Integer.parseInt(nextPageIdtmp)) > 0);
			if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML += "\n";
			} else {
				strReturnHTML += "</table>";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ContextUtil.commitTransaction(context);
		return strReturnHTML;
	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to parse the xml
	 * response received from IHS web service and generate the HTML to display in
	 * the UI
	 * 
	 * @param context
	 * @param args
	 * @return HashMap of HTML report, Next Page number for pagination, html css for
	 *         html table row
	 * @throws Exception
	 */
	public HashMap parseXMLAndShowReport(Context context, String strXMLData, Properties prop, String strCSSClass,
			String strExportToExcel) {
		HashMap hmReturnMap = new HashMap(2);
		String strReturnHTML = "";
		try {
			String ATTRIBUTE_IHSOOBJECT_ID = PropertyUtil.getSchemaProperty(context, "attribute_IHSObjectID");
			String ATTRIBUTE_IHSMANUFACTURER_PART_NUMBER = PropertyUtil.getSchemaProperty(context,
					"attribute_IHSManufacturerPartNumber");
			// String strProject_Already_StartedMessage =
			// EnoviaResourceBundle.getProperty(context,"emxProgramCentralStringResource",
			// new
			// Locale(context.getSession().getLanguage()),"emxProgramCentral.Notice.Project_Already_Started");
			StringList slSelectable = new StringList(2);
			slSelectable.add(DomainObject.SELECT_ID);
			// Added for Issue #281 Starts
			StringList slSelectable2 = new StringList(4);
			slSelectable2.add(DomainObject.SELECT_ID);
			slSelectable2.add("to[Manufacturing Responsibility].from.id");
			slSelectable2.add("to[Manufacturer Equivalent].from.id");
			// Added for Issue #281 Ends

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(strXMLData));

			Document doc = db.parse(is);
			NodeList nNodeXMLResult = doc.getElementsByTagName("XMLResult");

			for (int i = 0; i < nNodeXMLResult.getLength(); i++) {
				Element eElementXMLResult = (Element) nNodeXMLResult.item(i);
				String nextPageIdtmp = eElementXMLResult.getAttribute("nextpageid");
				hmReturnMap.put("nextPageIdtmp", nextPageIdtmp);
				NodeList nNodeResult = eElementXMLResult.getElementsByTagName("Result");
				for (int j = 0; j < nNodeResult.getLength(); j++) {
					Element eElementResult = (Element) nNodeResult.item(j);
					NodeList nNodePart = eElementResult.getElementsByTagName("Part");
					int iModifiedPartCount = nNodePart.getLength();
					if (iModifiedPartCount == 0) {
						if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
							strReturnHTML += "No Data modified in this time frame\n";
						} else {
							strReturnHTML += "<tr class='" + strCSSClass
									+ "'><td colspan='3'>No Data modified in this time frame</td></tr>";
						}
					} else {
						for (int k = 0; k < iModifiedPartCount; k++) {
							Element eElementPart = (Element) nNodePart.item(k);
							String strPartClassificationValue = eElementPart.getAttribute("part-type");
							String strPartNameValue = eElementPart.getAttribute("manufacturer-part-number");
							String strIHSPartObjectIdValue = eElementPart.getAttribute("object-id");
							String strWhere = "attribute[" + ATTRIBUTE_IHSOOBJECT_ID + "] == '"
									+ strIHSPartObjectIdValue + "' && attribute["
									+ ATTRIBUTE_IHSMANUFACTURER_PART_NUMBER + "] == '" + strPartNameValue + "'";

							// Check if Part is present in enovia
							MapList mlMEPs = DomainObject.findObjects(context, DomainConstants.TYPE_PART,
									DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
									DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, strWhere, false,
									slSelectable2);// Modified for Issue #281
							if (mlMEPs.size() == 0) {
								continue;
							}
							// Added for Issue #281 Starts
							StringList GPNPartsList = new StringList();
							String strManufacturer = "";
							String strPartIds = "";
							for (int ij = 0; ij < mlMEPs.size(); ij++) {
								Map mlMEPsMap = (Map) mlMEPs.get(ij);
								Object objGPNIds = (Object) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
								if (objGPNIds instanceof StringList) {
									GPNPartsList = (StringList) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
									for (int kl = 0; kl < GPNPartsList.size(); kl++) {
										strPartIds = strPartIds + "|" + (String) GPNPartsList.get(kl);
									}
								} else if (objGPNIds instanceof String) {
									strPartIds = (String) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
								}
								strManufacturer = (String) mlMEPsMap.get("to[Manufacturing Responsibility].from.id");
							}
							strPartIds = strPartIds.replaceFirst("|", "");
							// Added for Issue #281 Ends

							// Get the enovia attribute mapping for the IHS attributes
							Map mIHSEnoviaAttributeNameMapping = new HashMap(10);
							getIHSEnoviaMappingForClassification(context, strPartClassificationValue, slSelectable,
									mIHSEnoviaAttributeNameMapping, prop);

							if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
								strReturnHTML += getTextReportForExportToExcel(context, eElementPart, strPartNameValue,
										mIHSEnoviaAttributeNameMapping, strManufacturer, strPartIds);// Modified for
																										// Issue #281
							} else {
								strReturnHTML += getHTMLReportForUI(context, eElementPart, strCSSClass, hmReturnMap,
										strPartNameValue, mIHSEnoviaAttributeNameMapping, strManufacturer, strPartIds);// Modified
																														// for
																														// Issue
																														// #281
								strCSSClass = (String) hmReturnMap.get("strCSSClass");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		hmReturnMap.put("strReturnHTML", strReturnHTML);
		hmReturnMap.put("strCSSClass", strCSSClass);
		return hmReturnMap;
	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to generate the
	 * Text report for exporting to excel
	 * 
	 * @param context
	 * @param args
	 * @return String of Text report for Excel
	 * @throws Exception
	 */
	public String getTextReportForExportToExcel(Context context, Element eElementPart, String strPartNameValue,
			Map mIHSEnoviaAttributeNameMapping, String strManufacturer, String strPartIds) {
		String strReturnTextForExcel = "";
		try {
			// Added for Issue #281 Starts
			StringList slSelectable3 = new StringList(3);
			slSelectable3.add(DomainObject.SELECT_TYPE);
			slSelectable3.add(DomainObject.SELECT_NAME);
			slSelectable3.add(DomainObject.SELECT_REVISION);
			DomainObject domManufacturerObj = new DomainObject(strManufacturer);

			Map dataManufacturerMap = domManufacturerObj.getInfo(context, slSelectable3);
			String sObjManufacturerType = (String) dataManufacturerMap.get(DomainObject.SELECT_TYPE);
			String sObjManufacturerName = (String) dataManufacturerMap.get(DomainObject.SELECT_NAME);
			String sObjManufacturerRev = (String) dataManufacturerMap.get(DomainObject.SELECT_REVISION);
			// Added for Issue #281 Ends

			String strPartDescriptionValue = "\"" + eElementPart.getAttribute("part-description") + "\"";
			NodeList nNodePartDetails = eElementPart.getElementsByTagName("Partdetails");
			strReturnTextForExcel += strPartNameValue + "\t" + strPartDescriptionValue + "\t";
			int iPartDetailsSize = nNodePartDetails.getLength();
			boolean boolHasPartDetailsOrDocument = false;
			strReturnTextForExcel += "\"";
			for (int l = 0; l < iPartDetailsSize; l++) {
				Element eElementPartDetails = (Element) nNodePartDetails.item(l);
				String strTypeAttributeValue = eElementPartDetails.getAttribute("type");
				if ("DOC".equals(strTypeAttributeValue)) {
					// Setting Document attribute values
					NodeList nNodeDoc = eElementPartDetails.getElementsByTagName("Doc");
					String strDocURLLabelValue = (String) mIHSEnoviaAttributeNameMapping.get("doc_url");
					for (int n = 0; n < nNodeDoc.getLength(); n++) {
						Element eElementDoc = (Element) nNodeDoc.item(n);
						String strDocURLAttributeValue = eElementDoc.getAttribute("doc_url");
						strReturnTextForExcel += strDocURLLabelValue + ": " + strDocURLAttributeValue + "\r";

						String strDocTypeAttributeValue = eElementDoc.getAttribute("doc-type");
						strReturnTextForExcel += "IHS Document Type: " + strDocTypeAttributeValue + "\r";

						String strDocTitleAttributeValue = eElementDoc.getAttribute("doc_title");
						strReturnTextForExcel += "IHS Document Title: " + strDocTitleAttributeValue + "\r";
					}
					boolHasPartDetailsOrDocument = true;
				} else if ("DETAILS".equals(strTypeAttributeValue)) {
					// Update the Part's detailed attribute values in report
					NodeList nNodeDetails = eElementPartDetails.getElementsByTagName("Details");
					int iDetailsCount = nNodeDetails.getLength();
					if (iDetailsCount == 0) {
						strReturnTextForExcel += "No Data modified in this time frame\r";
					} else {
						for (int m = 0; m < iDetailsCount; m++) {
							Element eElementDetails = (Element) nNodeDetails.item(m);
							String strIHSPartDetailsUniqueAttributeId = eElementDetails.getAttribute("id");
							String strIHSAttributeMappedLabel = (String) mIHSEnoviaAttributeNameMapping
									.get(strIHSPartDetailsUniqueAttributeId);
							if (null != strIHSAttributeMappedLabel) {
								String strIHSAttributeValue = getCharacterDataFromElement(eElementDetails);
								strReturnTextForExcel += strIHSAttributeMappedLabel + ": " + strIHSAttributeValue
										+ "\r";
							}
						}
					}
					boolHasPartDetailsOrDocument = true;
				}
				// Added by Lalitha --starts
				if (!boolHasPartDetailsOrDocument) {
					strReturnTextForExcel += "No Data modified in this time frame\r";
				}
				// Added by Lalitha --Ends
			}
			strReturnTextForExcel += "\"";
			// Added for Issue #281 Starts
			StringList strPartIdsList = FrameworkUtil.split(strPartIds, "|");
			String strReturnTextForExcel2 = "\"";
			for (int ih = 0; ih < strPartIdsList.size(); ih++) {
				String strPartId = (String) strPartIdsList.get(ih);
				DomainObject domPartObj = new DomainObject(strPartId);
				Map dataGPNMap = domPartObj.getInfo(context, slSelectable3);
				String sObjPartType = (String) dataGPNMap.get(DomainObject.SELECT_TYPE);
				String sObjPartName = (String) dataGPNMap.get(DomainObject.SELECT_NAME);
				String sObjPartRev = (String) dataGPNMap.get(DomainObject.SELECT_REVISION);
				strReturnTextForExcel2 += sObjPartType + " " + sObjPartName + " " + sObjPartRev;
				if (strPartIdsList.size() > 1) {
					strReturnTextForExcel2 += "\n";
				}
			}
			strReturnTextForExcel2 += "\"";
			strReturnTextForExcel += "\t" + strReturnTextForExcel2 + "\t" + sObjManufacturerName;
			// Added for Issue #281 Ends
			strReturnTextForExcel += "\n";
			// Commented by Lalitha --starts
			/*
			 * if(!boolHasPartDetailsOrDocument) { strReturnTextForExcel +=
			 * "No Data modified in this time frame\r"; }
			 */
			// Commented by Lalitha --Ends
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturnTextForExcel;
	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to generate the
	 * HTML report to display in the UI
	 * 
	 * @param context
	 * @param args
	 * @return String of HTML report for UI
	 * @throws Exception
	 */
	public String getHTMLReportForUI(Context context, Element eElementPart, String strCSSClass, HashMap hmReturnMap,
			String strPartNameValue, Map mIHSEnoviaAttributeNameMapping, String strManufacturer, String strPartIds) {
		String strReturnHTML = "";
		strReturnHTML += "<script language=\"javascript\" src=\"../common/scripts/emxUITableUtil.js\"></script>";// Added
																													// for
																													// Issue
																													// #281

		try {
			// Added for Issue #281 Starts
			StringList slSelectable3 = new StringList(4);
			slSelectable3.add(DomainObject.SELECT_TYPE);
			slSelectable3.add(DomainObject.SELECT_ID);
			slSelectable3.add(DomainObject.SELECT_NAME);
			slSelectable3.add(DomainObject.SELECT_REVISION);
			DomainObject domManufacturerObj = new DomainObject(strManufacturer);

			Map dataManufacturerMap = domManufacturerObj.getInfo(context, slSelectable3);
			String sObjManufacturerType = (String) dataManufacturerMap.get(DomainObject.SELECT_TYPE);
			String sObjManufacturerName = (String) dataManufacturerMap.get(DomainObject.SELECT_NAME);
			String sObjManufacturerRev = (String) dataManufacturerMap.get(DomainObject.SELECT_REVISION);
			String sObjManufacturerId = (String) dataManufacturerMap.get(DomainObject.SELECT_ID);
			// Added for Issue #281 Ends

			String strPartDescriptionValue = eElementPart.getAttribute("part-description");
			NodeList nNodePartDetails = eElementPart.getElementsByTagName("Partdetails");
			strReturnHTML += "<tr class='" + strCSSClass + "'>";
			strReturnHTML += "<td>" + strPartNameValue + "</td><td>" + strPartDescriptionValue + "</td>";
			strReturnHTML += "<td>";
			int iPartDetailsSize = nNodePartDetails.getLength();
			if (iPartDetailsSize == 0) {
				strReturnHTML += "No Data modified in this time frame";
			} else {
				boolean boolHasPartDetailsOrDocument = false;
				for (int l = 0; l < iPartDetailsSize; l++) {
					Element eElementPartDetails = (Element) nNodePartDetails.item(l);
					String strTypeAttributeValue = eElementPartDetails.getAttribute("type");
					if ("DOC".equals(strTypeAttributeValue)) {
						// Setting Document attribute values
						NodeList nNodeDoc = eElementPartDetails.getElementsByTagName("Doc");
						String strDocURLLabelValue = (String) mIHSEnoviaAttributeNameMapping.get("doc_url");
						for (int n = 0; n < nNodeDoc.getLength(); n++) {
							Element eElementDoc = (Element) nNodeDoc.item(n);
							String strDocURLAttributeValue = eElementDoc.getAttribute("doc_url");
							// Modificed by Lalitha on 1/30/2019 --starts
							strReturnHTML += "<b>" + strDocURLLabelValue + "</b>" + ": " + strDocURLAttributeValue
									+ "<br/>";

							String strDocTypeAttributeValue = eElementDoc.getAttribute("doc-type");
							strReturnHTML += "<b>" + "IHS Document Type: " + "</b>" + strDocTypeAttributeValue
									+ "<br/>";

							String strDocTitleAttributeValue = eElementDoc.getAttribute("doc_title");
							strReturnHTML += "<b>" + "IHS Document Title: " + "</b>" + strDocTitleAttributeValue
									+ "<br/>";
							// Modificed by Lalitha on 1/30/2019 --starts
						}
						boolHasPartDetailsOrDocument = true;
					} else if ("DETAILS".equals(strTypeAttributeValue)) {
						// Update the Part's detailed attribute values in report
						NodeList nNodeDetails = eElementPartDetails.getElementsByTagName("Details");
						int iDetailsCount = nNodeDetails.getLength();
						if (iDetailsCount == 0) {
							strReturnHTML += "No Data modified in this time frame";
						} else {
							for (int m = 0; m < iDetailsCount; m++) {
								Element eElementDetails = (Element) nNodeDetails.item(m);
								String strIHSPartDetailsUniqueAttributeId = eElementDetails.getAttribute("id");
								String strIHSAttributeMappedLabel = (String) mIHSEnoviaAttributeNameMapping
										.get(strIHSPartDetailsUniqueAttributeId);
								if (null != strIHSAttributeMappedLabel) {
									String strIHSAttributeValue = getCharacterDataFromElement(eElementDetails);
									// Modified by Lalitha on 1/30/2019 --starts
									strReturnHTML += "<b>" + strIHSAttributeMappedLabel + "</b>" + ": "
											+ strIHSAttributeValue + "<br/>";
									// Modified by Lalitha on 1/30/2019 --Ends
								}
							}
						}
						boolHasPartDetailsOrDocument = true;
					}
				}
				if (!boolHasPartDetailsOrDocument) {
					strReturnHTML += "No Data modified in this time frame";
				}
			}
			strReturnHTML += "</td>";
			// Added for Issue #281 Starts
			String strReturnHTML2 = "<td><table>";
			StringList strPartIdsList = FrameworkUtil.split(strPartIds, "|");
			for (int ih = 0; ih < strPartIdsList.size(); ih++) {
				strReturnHTML2 += "<tr><td>";
				String strPartId = (String) strPartIdsList.get(ih);
				DomainObject domPartObj = new DomainObject(strPartId);
				Map dataGPNMap = domPartObj.getInfo(context, slSelectable3);
				String sObjPartType = (String) dataGPNMap.get(DomainObject.SELECT_TYPE);
				String sObjPartName = (String) dataGPNMap.get(DomainObject.SELECT_NAME);
				String sObjPartRev = (String) dataGPNMap.get(DomainObject.SELECT_REVISION);
				StringBuffer temp = new StringBuffer();
				temp.append(" <img border=\"0\" src=\"");
				temp.append("../common/images/iconSmallPart.png");
				temp.append("\" /> ");
				temp.append("<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=");
				temp.append("engineeringcentral");
				temp.append("&amp;suiteKey=");
				temp.append("EngineeringCentral");
				temp.append("&amp;objectId=");
				temp.append(strPartId);
				temp.append("', '450', '300', 'true', 'popup')\" class=\"object\">");
				temp.append(XSSUtil.encodeForHTML(context, sObjPartType + " " + sObjPartName + " " + sObjPartRev));
				temp.append("</A>");
				strReturnHTML2 += temp.toString() + "</td></tr>";
			}
			strReturnHTML2 += "</table></td><td>";
			StringBuffer temp2 = new StringBuffer();
			temp2.append(" <img border=\"0\" src=\"");
			temp2.append("../common/images/iconSmallCompany.gif");
			temp2.append("\" /> ");
			temp2.append("<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=");
			temp2.append("engineeringcentral");
			temp2.append("&amp;suiteKey=");
			temp2.append("EngineeringCentral");
			temp2.append("&amp;objectId=");
			temp2.append(sObjManufacturerId);
			temp2.append("', '450', '300', 'true', 'popup')\" class=\"object\">");
			temp2.append(XSSUtil.encodeForHTML(context, sObjManufacturerName));
			temp2.append("</A>");
			strReturnHTML += strReturnHTML2 + temp2.toString() + "</td>";
			// Added for Issue #281 Ends

			if ("even".equals(strCSSClass)) {
				strCSSClass = "odd";
			} else {
				strCSSClass = "even";
			}
			hmReturnMap.put("strCSSClass", strCSSClass);
			strReturnHTML += "</tr>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturnHTML;
	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to get the mapping
	 * of attributes between IHS and Enovia attributes
	 * 
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	public void getIHSEnoviaMappingForClassification(Context context, String strPartClassificationValue,
			StringList slSelectable, Map mIHSEnoviaAttributeNameMapping, Properties prop) {
		try {
			if (strPartClassificationValue != null && !"".equals(strPartClassificationValue)) {
				slSelectable.add("attribute[" + DomainObject.ATTRIBUTE_MXSYSINTERFACE + "]");
				MapList mlClassificationObjectList = DomainObject.findObjects(context,
						LibraryCentralConstants.TYPE_GENERAL_CLASS, strPartClassificationValue,
						DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
						"", false, slSelectable);
				if (mlClassificationObjectList.size() != 0) {
					String interfaceName = (String) ((Map) mlClassificationObjectList.get(0))
							.get("attribute[" + DomainObject.ATTRIBUTE_MXSYSINTERFACE + "]");
					StringList slInterfaceAttrList = new StringList(10);
					if (interfaceName != null && !"".equals(interfaceName)) {
						String strMQL = "print interface $1 select attribute dump";
						String resStr = MqlUtil.mqlCommand(context, strMQL, true, interfaceName);
						StringList slMQLResult = FrameworkUtil.split(resStr, ",");
						if (slMQLResult.size() > 0) {
							slInterfaceAttrList.addAll(slMQLResult);
						}
					}
					for (int i = 0; i < slInterfaceAttrList.size(); i++) {
						String strIHSEnoviaAttrName = (String) slInterfaceAttrList.get(i);
						// renaming attribute by replacing " " with "_" & prefixing "IHS.Mapping." to
						// match with property file keys
						String strIHSAttrNameModified = strIHSEnoviaAttrName.replaceAll(" ", "_");
						String strPropertyKey = "IHSAttrMapping_" + strPartClassificationValue.replace(" ", "_") + "_"
								+ strIHSAttrNameModified;
						String strPropertyKeyValue = prop.getProperty(strPropertyKey);
						if (strPropertyKeyValue != null && !"".equals(strPropertyKeyValue)) {
							if (!mIHSEnoviaAttributeNameMapping.containsKey(strPropertyKeyValue)) {
								mIHSEnoviaAttributeNameMapping.put(strPropertyKeyValue, strIHSEnoviaAttrName);
							}
						}
					}
				}
			} else {
				String strPropertyKey = "IHS.Mapping.IHSDatasheet_URL";
				String strPropertyKeyValue = prop.getProperty(strPropertyKey);

				if (strPropertyKeyValue != null && !"".equals(strPropertyKeyValue)) {
					if (!mIHSEnoviaAttributeNameMapping.containsKey(strPropertyKeyValue)) {
						mIHSEnoviaAttributeNameMapping.put(strPropertyKeyValue,
								PropertyUtil.getSchemaProperty(context, "attribute_IHSDatasheetURL"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method added by TCS (Shashank) for Requirement #UR464 Code to get the xml tag
	 * value from the xml returned from IHS web service
	 * 
	 * @param context
	 * @param args
	 * @return String of the xml tag value
	 * @throws Exception
	 */
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	// Added for Issue #281 Starts
	public StringList getEnoviaPartsList(Context context, String[] args) throws FrameworkException {
		StringList committedProd = new StringList();

		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList lstObjectIdsList = (MapList) programMap.get("objectList");
			HashMap paramMap = (HashMap) programMap.get("paramList");
			String suiteDir = (String) paramMap.get("SuiteDirectory");
			String suiteKey = (String) paramMap.get("suiteKey");
			String reportFormat = (String) paramMap.get("reportFormat");
			for (int i = 0; i < lstObjectIdsList.size(); i++) {
				String output = "";
				Map map = (Map) lstObjectIdsList.get(i);
				String strMEPId = (String) map.get("id");
				DomainObject domMEPObj = DomainObject.newInstance(context, strMEPId);
				StringList slObjectSle = new StringList(4);
				slObjectSle.addElement(DomainConstants.SELECT_ID);
				slObjectSle.addElement(DomainConstants.SELECT_TYPE);
				slObjectSle.addElement(DomainConstants.SELECT_NAME);
				slObjectSle.addElement(DomainConstants.SELECT_REVISION);
				StringList slRelSle = new StringList();
				MapList mlEnoviaPartsList = domMEPObj.getRelatedObjects(context,
						PropertyUtil.getSchemaProperty(context, "relationship_ManufacturerEquivalent"), "*", // object
																												// pattern
						slObjectSle, // object selects
						slRelSle, // relationship selects
						true, // to direction
						false, // from direction
						(short) 1, // recursion level
						null, // object where clause
						null);
				StringBuffer temp = new StringBuffer();
				if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat)) && reportFormat.length() > 0) {

				} else {
					temp.append(" <table>");
				}
				for (int i1 = 0; i1 < mlEnoviaPartsList.size(); i1++) {
					Map mItem = (Map) mlEnoviaPartsList.get(i1);

					String strEnoviaPartType = (String) mItem.get(DomainConstants.SELECT_TYPE);
					String strEnoviaPartName = (String) mItem.get(DomainConstants.SELECT_NAME);
					String strEnoviaPartRevision = (String) mItem.get(DomainConstants.SELECT_REVISION);
					String strEnoviaPartId = (String) mItem.get(DomainConstants.SELECT_ID);

					if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat)) && reportFormat.length() > 0) {
						if (i1 > 0) {
							temp.append('\n');
						}
						temp.append(strEnoviaPartName + " " + strEnoviaPartRevision);
					} else {
						temp.append("<tr>");
						temp.append("<td>");
						temp.append(" <img border=\"0\" src=\"");
						temp.append("images/iconSmallPart.png");
						temp.append("\" /> ");
						temp.append(
								"<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=");
						temp.append(suiteDir);
						temp.append("&amp;suiteKey=");
						temp.append(suiteKey);
						temp.append("&amp;objectId=");
						temp.append(strEnoviaPartId);
						temp.append("', '450', '300', 'true', 'popup')\" class=\"object\">");
						temp.append(XSSUtil.encodeForHTML(context, strEnoviaPartName + " " + strEnoviaPartRevision));
						temp.append("</A>");
						temp.append("</td>");
						temp.append("</tr>");
					}
				}
				if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat)) && reportFormat.length() > 0) {

				} else {
					temp.append(" </table>");
				}
				committedProd.add(temp.toString());

			}
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		return committedProd;
	}

	// Added for Issue #281 Ends

	// Added for Issue #360 Starts
	public StringList getSortedDateList(Context context, Map<String, String> docURLDateMap) throws Exception {

		ArrayList<Date> date = new ArrayList<Date>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyy");
		StringList strDateStringList = new StringList();

		for (Map.Entry<String, String> entry : docURLDateMap.entrySet()) {

			Date date1 = format.parse(entry.getKey());
			date.add(date1);

		}
		Collections.sort(date, Collections.reverseOrder());
		// List<Date> numbers = Arrays.asList(date);
		Iterator it = date.iterator();
		while (it.hasNext()) {
			Date date2 = (Date) it.next();
			String strDate = format.format(date2);
			strDateStringList.add(strDate);
		}
		return strDateStringList;

	}
	// Added for Issue #360 Ends

	// Added for Issue #381 Starts
	public String migrateExistingDataToUpdateDataSheetURLFromCmdPrompt(Context context, String[] args)
			throws Exception {

		String strReturn = "";
		// BufferedWriter bw = null;
		String returnString = "";

		generated.Partdetails partDetails = new generated.Partdetails();
		MatrixWriter _mxWriter = new MatrixWriter(context);
		Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
		String strMEPName = "";
		String strSupplierName = "";
		String strMEPID = "";
		StringList selectStmts = new StringList(1);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		Map<String, String> objMap = null;
		String strType = "";
		StringList slClassification = new StringList();
		String MQLResult = "";
		String strPageFileName = "XORGIHSIntegrationMapping";
		Date date = new Date();
		StringList slSelectable = new StringList();
		StringList slInterfaceAttrList = new StringList();
		ArrayList<Details> detailsList = new ArrayList<Details>();
		String strClassName = "";
		String strPropKey_doc_url = "IHS.Mapping.IHSDatasheet_URL";
		String strPropKey_latest_doc_url = "IHS.Mapping.IHSLatest_DataSheet_URL";
		String strDocURL = "";
		String strLatestDocURL = "";
		String strDocDate = "";
		Map<String, String> docURLDateMap = new HashMap<String, String>();
		String strDocTitle = "";
		generated.Doc doc = new generated.Doc();
		ArrayList<Doc> docList;
		ArrayList<Object> partList;
		String strPartDetailsType = "";
		try {
			String strMEPObjId = args[0];
			DomainObject doMEP = new DomainObject(strMEPObjId);
			SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");
			MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			InputStream input = new ByteArrayInputStream(bytes);
			Properties prop = new Properties();
			prop.load(input);
			// creating the error log file
			// String strLogFilePath = prop.getProperty("XORGIHS.Log.path");
			// String strErrorLogHeader =
			// prop.getProperty("XORGIHS.RealTime.Error.Log.header");
			// File errorLog = new
			// File(strLogFilePath+"IHS_Error"+"_"+dateForLog.format(date) + ".log") ;
			// to create folder structure
			// errorLog.getParentFile().mkdirs();
			/*
			 * if(!errorLog.exists()){ FileWriter fw = new FileWriter(errorLog,true); bw =
			 * new BufferedWriter(fw); bw.write(strErrorLogHeader); bw.newLine(); } else{
			 * FileWriter fw = new FileWriter(errorLog,true); bw = new BufferedWriter(fw); }
			 */
			slSelectable.add(DomainConstants.SELECT_ID);
			slSelectable.add(DomainConstants.SELECT_NAME);
			slSelectable.add(DomainConstants.SELECT_REVISION);
			slSelectable.add("attribute[IHSResponse XML].value");
			slSelectable
					.add("relationship[" + DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name");

			Map mlMEPMap = doMEP.getInfo(context, slSelectable);

			/*
			 * MapList mlMEPs = DomainObject.findObjects(context, //context
			 * DomainConstants.TYPE_PART, //typePattern DomainConstants.QUERY_WILDCARD,
			 * //namePattern DomainConstants.QUERY_WILDCARD, //revPattern
			 * DomainConstants.QUERY_WILDCARD, //ownerPattern
			 * DomainConstants.QUERY_WILDCARD, //vaultPatern strWhere, //whereExpression
			 * false, //expandType slSelectable);//objectSelects
			 */

			strLatestDocURL = "";
			String strResponseXML = (String) mlMEPMap.get("attribute[IHSResponse XML].value");

			if (strResponseXML != null && !strResponseXML.isEmpty()) {
				strSupplierName = (String) mlMEPMap.get(
						"relationship[" + DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name");

				MapList mlObjects = doMEP.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + ","
								+ LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
						DomainConstants.TYPE_COMPANY + "," + LibraryCentralConstants.TYPE_GENERAL_CLASS, // Type pattern
						selectStmts, // object selects
						null, // relationship selects
						true, // to direction
						true, // from direction
						(short) 1, // recursion level
						null, // object where clause
						DomainConstants.EMPTY_STRING, 0);
				Iterator<Map<String, String>> objItr = (Iterator) mlObjects.iterator();

				while (objItr.hasNext()) {
					objMap = (Map<String, String>) objItr.next();
					strType = (String) objMap.get(DomainConstants.SELECT_TYPE);
					if (strType.equalsIgnoreCase(DomainConstants.TYPE_COMPANY)) {
						strSupplierName = (String) objMap.get(DomainConstants.SELECT_NAME);
					} else if (strType.equalsIgnoreCase(LibraryCentralConstants.TYPE_GENERAL_CLASS)) {
						slClassification.add((String) objMap.get(DomainConstants.SELECT_NAME));
					}
				}

				JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Reader inputStreamReader = new StringReader(strResponseXML);
				XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
				inputStreamReader.close();
				if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
					ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
					generated.Result result = (generated.Result) xmlResultList.get(0);
					if ((Integer.parseInt(result.getCount())) > 0) {
						partList = (ArrayList<Object>) result.getPartOrMfrOrError();
						if (partList.size() == 1) {
							generated.Part part = (generated.Part) partList.get(0);
							// getting the classification info
							String strClassificationName = part.getPartType();
							String strPartDesc = part.getPartDescription();
							StringList slSelect = new StringList();
							slSelect.addElement(DomainConstants.SELECT_ID);
							slSelect.addElement(DomainConstants.SELECT_NAME);
							slSelect.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
							// querying for the classification object
							MapList mlClassification = DomainObject.findObjects(context, // context
									LibraryCentralConstants.TYPE_GENERAL_CLASS, // typePattern
									strClassificationName, // namePattern
									DomainConstants.QUERY_WILDCARD, // revPattern
									DomainConstants.QUERY_WILDCARD, // ownerPattern
									DomainConstants.QUERY_WILDCARD, // vaultPatern
									DomainConstants.EMPTY_STRING, // whereExpression
									false, // expandType
									slSelect);// objectSelects

							if (mlClassification.size() == 1) {

								Map<String, String> tempClassificationMap = (Map<String, String>) mlClassification
										.get(0);
								String strClassID = tempClassificationMap.get(DomainConstants.SELECT_ID);
								strClassName = tempClassificationMap.get(DomainConstants.SELECT_NAME);
								String interfaceName = tempClassificationMap
										.get("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");
								// classify only if the part is not already classified with same classification
								/*
								 * if(!slClassification.contains(strClassName)){
								 * DomainRelationship.connect(context, new DomainObject(strClassID),
								 * LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, doMEP); }
								 */
								// code to classify MEP with strClassification -- end
								// get the attributes on classification
								if (interfaceName != null && !"".equals(interfaceName)) {
									String strMQL = "print interface $1 select attribute dump";
									String resStr = MqlUtil.mqlCommand(context, strMQL, true, interfaceName);
									StringList slMQLResult = FrameworkUtil.split(resStr, ",");
									StringList slMQLResultMod = new StringList();
									for (int n = 0; n < slMQLResult.size(); n++) {
										String strAttrVal = (String) slMQLResult.get(n);
										if (strAttrVal.equals("IHSDatasheet URL")
												|| strAttrVal.equals("IHSLatest DataSheet URL")) {
											slMQLResultMod.add(strAttrVal);
										}

									}
									if (slMQLResultMod.size() > 0) {
										slInterfaceAttrList.addAll(slMQLResultMod);
									}
								}

							}
							ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
									.getPartdetails();
							for (int i = 0; i < partDetailsList.size(); i++) {
								partDetails = partDetailsList.get(i);
								strPartDetailsType = partDetails.getType();
								if (strPartDetailsType.equalsIgnoreCase("DOC")) {
									docList = (ArrayList<Doc>) partDetails.getDoc();
									for (int j = 0; j < docList.size(); j++) {
										doc = docList.get(j);
										if (doc.getDocType().equalsIgnoreCase("Datasheet")) {
											if (j == 0) {
												strDocTitle = doc.getDocTitle();
												strDocDate = doc.getPubDate();
												// strDocURL = doc.getDocUrl();
												strDocURL = strDocTitle + ":" + "\n" + doc.getDocUrl();
												docURLDateMap.put(strDocDate, strDocURL);
											} else {
												strDocTitle = doc.getDocTitle();
												strDocDate = doc.getPubDate();
												// strDocURL = strDocURL +"\n"+doc.getDocUrl();
												strDocURL = strDocURL + "\n" + strDocTitle + ":" + "\n"
														+ doc.getDocUrl();
												docURLDateMap.put(strDocDate,
														strDocTitle + ":" + "\n" + doc.getDocUrl());

											}
										}
									}

								}

							}
							StringList sSortedDateList = getSortedDateList(context, docURLDateMap);
							String strDocURLSorted = "";
							for (int kk = 0; kk < sSortedDateList.size(); kk++) {
								String strDateKey = (String) sSortedDateList.get(kk);

								String strUrl = (String) docURLDateMap.get(strDateKey);

								if (kk == 0) {
									strDocURLSorted = strUrl;
									strLatestDocURL = strDocURLSorted;

								} else {
									strDocURLSorted = strDocURLSorted + "\n" + strUrl;

								}

							}

							ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_doc_url), strDocURLSorted);
							StringList strLatestDocURLLst = FrameworkUtil.split(strLatestDocURL, "\n");

							ihsAttrInfoMap.put((String) prop.getProperty(strPropKey_latest_doc_url),
									(String) strLatestDocURLLst.get(0) + "\n" + (String) strLatestDocURLLst.get(1));// Added
																													// for
																													// Issue
																													// #360
							returnString = updateMEPHSDatasheetAttributesForMigration(context, detailsList, doMEP,
									strSupplierName, slInterfaceAttrList, strClassName, ihsAttrInfoMap, prop);

						} else {

							BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
							writer.write("IHS contains Multiple or No Parts with this MEP & Supplier combination");
							writer.flush();
							return "IHS contains Multiple or No Parts with this MEP & Supplier combination";

						}
					} else {

						BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
						writer.write("No Corresponding Supplier and/or Supplier Part Information found in IHS");
						writer.flush();
						return "No Corresponding Supplier and/or Supplier Part Information found in IHS";

					}
				} else if (xmlResult.getStatus().equalsIgnoreCase("ERROR")) {

					BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
					writer.write("An Issue was encountered while accessing the IHS data. Please contact administrator");
					writer.flush();
					return "An Issue was encountered while accessing the IHS data. Please contact administrator";

				}
			} else {
				BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
				writer.write("Unable to update Part attributes as the Part was not synced already");
				writer.flush();
				return "Unable to update Part attributes as the Part was not synced already";

			}
			BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
			writer.write(returnString);
			writer.flush();
			return returnString;
		} catch (Exception E) {
			E.printStackTrace();
			BufferedWriter writer = new BufferedWriter(new MatrixWriter(context));
			writer.write("Unable to update Part attributes");
			writer.flush();
			return "Unable to update Part attributes";
		} finally {

		}

	}

	public String updateMEPHSDatasheetAttributesForMigration(Context context, ArrayList<Details> detailsList,
			DomainObject doMEP, String strSupplierName, StringList slInterfaceAttrList, String strClassName,
			Map<String, String> ihsAttrInfoMap, Properties prop) throws Exception {

		String strReturn = "";
		generated.Details details;
		String strAttrName = "";
		String strAttrID = "";
		String strAttrValue = "";
		Map<String, String> attrMap = new HashMap<String, String>();
		String strIHSAttrName = "";
		String strIHSAttrNameModified = "";
		String strmodifiedClassName = "";
		String strPropertyKey = "";
		String strLocale = context.getSession().getLanguage();
		String strPropertyKeyValue = "";
		String strIHSAttrValue = "";
		String strIHSObjectId = "";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";

		String strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);
		// String strSupplierName = doMEP.getInfo(context,
		// DomainConstants.SELECT_REVISION);

		String strBadCharacters = "";
		boolean blnSpecialChar = false;

		StringList slAttrsWithSpecialChars = new StringList();

		Date date = new Date();
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(date);

		try {
			if (detailsList.size() > 0) {
				// Iterate through detailsList and add the key(attrbuteID),
				// Value(attributeValue) pairs to a Map
				Iterator<Details> itr = detailsList.iterator();
				while (itr.hasNext()) {
					details = (Details) itr.next();
					// strAttrName = (String)details.getName();
					strAttrID = (String) details.getId();
					strAttrValue = (String) details.getValue();

					if (strAttrID != "" && !strAttrID.equals(""))
						ihsAttrInfoMap.put(strAttrID, strAttrValue);
				}
			}
			/*
			 * String strPageFileName = "XORGIHSIntegrationMapping"; String MQLResult =
			 * MqlUtil.mqlCommand(context, "print page $1 select content dump",
			 * strPageFileName); byte[] bytes = MQLResult.getBytes("UTF-8"); InputStream
			 * input = new ByteArrayInputStream(bytes); Properties prop = new Properties();
			 * prop.load(input);
			 */
			// for loop to iterate through slInterfaceAttrList and get the corresponding
			// value from attrInfoMap
			for (int i = 0; i < slInterfaceAttrList.size(); i++) {
				strIHSAttrName = (String) slInterfaceAttrList.get(i);
				// renaming attribute by replacing " " with "_" & prefixing "IHS.Mapping." to
				// match with property file keys
				strIHSAttrNameModified = strIHSAttrName.replaceAll(" ", "_");
				strPropertyKey = "IHSAttrMapping_" + strClassName.replace(" ", "_") + "_" + strIHSAttrNameModified;

				strPropertyKeyValue = prop.getProperty(strPropertyKey);

				strIHSAttrValue = (String) ihsAttrInfoMap.get(strPropertyKeyValue);
				// validating special characters
				strBadCharacters = prop.getProperty("IHS.Attribute.Bad.characters");
				if (strIHSAttrValue != null && !"".equals(strIHSAttrValue)) {

					for (int j = 0; j < strIHSAttrValue.length(); j++) {
						if (!strPropertyKeyValue.equalsIgnoreCase("response_XML")) {
							if (strBadCharacters.contains(Character.toString(strIHSAttrValue.charAt(j)))) {
								blnSpecialChar = true;
								break;
							}
						}
					}
					if (!blnSpecialChar) {
						attrMap.put(strIHSAttrName, strIHSAttrValue);
						blnSpecialChar = false;
					} else {
						attrMap.put(strIHSAttrName, DomainConstants.EMPTY_STRING);
						slAttrsWithSpecialChars.add(strIHSAttrName);
						blnSpecialChar = false;
					}
				} else {
					attrMap.put(strIHSAttrName, DomainConstants.EMPTY_STRING);
				}
			}
			if (!(slAttrsWithSpecialChars.size() == 0)) {
				slInterfaceAttrList.removeAll(slAttrsWithSpecialChars);
				strReturn = "Invalid Characters found for below parameters. Please report the issue to your System Administrator"
						+ "\n\r" + slAttrsWithSpecialChars.toString().replace("[", "").replace("]", "");
			}
			// DomainObject doMEP = new DomainObject(sPartId);
			// Setting attribute values on MEP
			doMEP.setAttributeValues(context, attrMap);
			// below code will subscribe the Part for IHS Updates.- start
			strIHSObjectId = ihsAttrInfoMap.get((String) prop.getProperty(strPropKey_object_id));

			// --End
			if (strReturn != null && !"".equals(strReturn)) {
				strReturn = "Part sync with IHS done successfully. " + strReturn;
			} else {
				strReturn = "Part sync with IHS done successfully";
			}
			String strSubscribeFlag = subscribetoIHSUpdates(context, strPartName, strSupplierName, strIHSObjectId,
					slAttrsWithSpecialChars, prop);

			return strReturn;
		} catch (Exception E) {
			E.printStackTrace();
			return "Unable to update Part attributes";
		} finally {
			// bw.close();
		}
	}
	// Added for Issue #381 Ends

	// Added for Issue #66 by Preethi Rajaraman -- Starts
	public StringList getParentPartList(Context context, String[] args) throws FrameworkException {
		StringList committedProd = new StringList();

		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList lstObjectIdsList = (MapList) programMap.get("objectList");
			HashMap paramMap = (HashMap) programMap.get("paramList");
			String suiteDir = (String) paramMap.get("SuiteDirectory");
			String suiteKey = (String) paramMap.get("suiteKey");
			String reportFormat = (String) paramMap.get("reportFormat");
			for (int i = 0; i < lstObjectIdsList.size(); i++) {
				String output = "";
				Map map = (Map) lstObjectIdsList.get(i);
				String strMEPId = (String) map.get("id");
				DomainObject domMEPObj = DomainObject.newInstance(context, strMEPId);
				StringList slObjectSle = new StringList(4);
				slObjectSle.addElement(DomainConstants.SELECT_ID);
				slObjectSle.addElement(DomainConstants.SELECT_TYPE);
				slObjectSle.addElement(DomainConstants.SELECT_NAME);
				slObjectSle.addElement(DomainConstants.SELECT_REVISION);
				StringList slRelSle = new StringList();
				MapList mlEnoviaPartsList = domMEPObj.getRelatedObjects(context,
						PropertyUtil.getSchemaProperty(context, "relationship_ManufacturerEquivalent"), "*", // object
																												// pattern
						slObjectSle, // object selects
						slRelSle, // relationship selects
						true, // to direction
						false, // from direction
						(short) 1, // recursion level
						null, // object where clause
						null);
				StringBuffer temp = new StringBuffer();
				if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat)) && reportFormat.length() > 0) {

				} else {
					temp.append(" <table>");
				}
				for (int i1 = 0; i1 < mlEnoviaPartsList.size(); i1++) {
					Map mItem = (Map) mlEnoviaPartsList.get(i1);
					String strEnoviaPartId = (String) mItem.get(DomainConstants.SELECT_ID);
					DomainObject domPartObj = DomainObject.newInstance(context, strEnoviaPartId);
					MapList mlEnoviaParentList = domPartObj.getRelatedObjects(context,
							PropertyUtil.getSchemaProperty(context, "relationship_EBOM"), "*", // object pattern
							slObjectSle, // object selects
							slRelSle, // relationship selects
							true, // to direction
							false, // from direction
							(short) 1, // recursion level
							null, // object where clause
							null);

					for (int k = 0; k < mlEnoviaParentList.size(); k++) {
						Map mData = (Map) mlEnoviaParentList.get(k);
						String Strid = (String) mData.get(DomainConstants.SELECT_ID);
						String strEnoviaPartName = (String) mData.get(DomainConstants.SELECT_NAME);
						String strEnoviaPartRevision = (String) mData.get(DomainConstants.SELECT_REVISION);
						if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat))
								&& reportFormat.length() > 0) {
							if (i1 > 0) {
								temp.append('\n');
							}
							temp.append(strEnoviaPartName + " " + strEnoviaPartRevision);
						} else {
							temp.append("<tr>");
							temp.append("<td>");
							temp.append(" <img border=\"0\" src=\"");
							temp.append("images/iconSmallPart.png");
							temp.append("\" /> ");
							temp.append(
									"<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=");
							temp.append(suiteDir);
							temp.append("&amp;suiteKey=");
							temp.append(suiteKey);
							temp.append("&amp;objectId=");
							temp.append(Strid);
							temp.append("', '450', '300', 'true', 'popup')\" class=\"object\">");
							temp.append(
									XSSUtil.encodeForHTML(context, strEnoviaPartName + " " + strEnoviaPartRevision));
							temp.append("</A>");
							temp.append("                ");
							temp.append(
									"<A HREF=\"JavaScript:emxTableColumnLinkClick('../common/emxIndentedTable.jsp?header=emxEngineeringCentral.Common.WhereUsed&amp;sortColumnName=none&amp;partWhereUsed=true&amp;showMassUpdate=false&amp;table=PartWhereUsedTable&amp;program=emxPart:getPartWhereUsed&amp;expandProgram=emxPart:getPartWhereUsed&amp;showApply=false&amp;toolbar=ENCpartReviewWhereUsedSummaryToolBar,ENCPartWhereUsedFiltersToolbar1,ENCPartWhereUsedFiltersToolbar2&amp;HelpMarker=emxhelppartwhereused&amp;selection=multiple&amp;expandLevelFilter=false&amp;PrinterFriendly=true&amp;emxSuiteDirectory=");
							temp.append(suiteDir);
							temp.append("&amp;suiteKey=");
							temp.append(suiteKey);
							temp.append("&amp;objectId=");
							temp.append(Strid);
							temp.append("', '450', '300', 'true', 'popup')\" class=\"object\">");
							temp.append(XSSUtil.encodeForHTML(context, "more...."));
							temp.append("</A>");
							temp.append("</td>");
							temp.append("</tr>");
						}
					}
				}
				if (reportFormat != null && !("null".equalsIgnoreCase(reportFormat)) && reportFormat.length() > 0) {

				} else {
					temp.append(" </table>");
				}
				committedProd.add(temp.toString());

			}
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		return committedProd;
	}

	// Added for Issue #66 by Preethi Rajaraman -- Ends
	// Added by Lalitha --starts
	public String getIHSComparisonReport(Context context, String[] args) throws Exception {

		HashMap hmProgramMap = (HashMap) JPO.unpackArgs(args);
		String strFromDate = (String) hmProgramMap.get("strFromDate");
		String strToDate = (String) hmProgramMap.get("strToDate");
		String strExportToExcel = (String) hmProgramMap.get("exportToExcel");
		String sPartType = PropertyUtil.getSchemaProperty("type_Part");
		String strIHSLastSyncDate = PropertyUtil.getSchemaProperty("attribute_IHSLastSyncDate");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
		LocalDate lFromdate = LocalDate.parse(strFromDate, formatter);
		LocalDate lTodate = LocalDate.parse(strToDate, formatter);
		Month frmonth = lFromdate.getMonth();
		Month Tomonth = lTodate.getMonth();
		int iFromMonth = frmonth.getValue();
		int iToMonth = Tomonth.getValue();
		int iFromDate = lFromdate.getDayOfMonth();
		int iToDate = lTodate.getDayOfMonth();
		int iFromyear = lFromdate.getYear();
		int iToyear = lTodate.getYear();
		String sFromDate = iFromMonth + "/" + iFromDate + "/" + iFromyear + " 00:00:00 AM";
		String sToDate = iToMonth + "/" + iToDate + "/" + iToyear + " 11:59:59 PM";
		String sWhere = "attribute[" + strIHSLastSyncDate + "].value >= '" + sFromDate + "' && attribute["
				+ strIHSLastSyncDate + "].value <= '" + sToDate + "' ";
		DomainObject doMEP = new DomainObject();
		MapList mapListPartID = null;
		MapList mapClassifiedID = null;
		generated.Partdetails partDetails = new generated.Partdetails();
		generated.Details details;
		Map<String, String> objMap = null;
		StringList slInterfaceAttrList = new StringList();
		generated.Part part;
		ArrayList<Details> IHSdetailsList = new ArrayList<Details>();
		String sAttrName = "";
		String sAttrValue = "";
		String strMapKey = "";
		String strMapKey1 = "";
		String strMapValue = "";
		String strMapValue1 = "";
		String strIHSAttrID = "";
		String strIHSAttrValue = "";
		String sPartName = "";
		String scurrAttrDetails = "";
		String sPreAttrDetails = "";
		String sPartId = "";
		String sMEPId = "";
		String sMEPName = "";
		String sDescription = "";
		String sCIHSResponseXML = "";
		String sCManufacture = "";
		String sCManufactureId = "";
		String sCSyncdate = "";
		String strClassificationInterface = "";
		String strIHSAttrNameModified = "";
		String strPropertyKey = "";
		String strClassName = "";
		String strPropertyKeyValue = "";
		String strCSSClass = "even";
		String strIHSEnoviaAttrName = "";
		String strMQL = "";
		String resStr = "";
		String sEnoviakeys = "";
		StringList slMQLResult = new StringList();
		String sIHSResponseXMLPrevious = PropertyUtil.getSchemaProperty("attribute_IHSResponseXMLPrevious");
		String relMfrResponsibility = PropertyUtil.getSchemaProperty("relationship_ManufacturingResponsibility");
		// Added by Lalitha on 12/13/2018--starts
		String relMfrEquivalent = PropertyUtil.getSchemaProperty("relationship_ManufacturerEquivalent");
		String relClassifiedItems = PropertyUtil.getSchemaProperty("relationship_ClassifiedItem");
		// Added by Lalitha on 12/13/2018--Ends
		String sMEPUrl = "";
		String sPartUrl = "";
		String sMnufUrl = "";
		String sCurrentAttributes = "";
		String sPreviousAttributes = "";
		StringList slObjSel = new StringList();
		slObjSel.add("id");
		slObjSel.add("name");
		slObjSel.add("attribute[" + sIHSResponseXMLPrevious + "].value");
		slObjSel.add("to[" + relMfrResponsibility + "].from.name");
		slObjSel.add("to[" + relMfrResponsibility + "].from.id");
		// Added by Lalitha on 12/13/2018 --starts
		slObjSel.add("to[" + relMfrEquivalent + "].from.id");
		slObjSel.add("to[" + relMfrEquivalent + "].from.name");
		slObjSel.add("to[" + relClassifiedItems + "].from.name");
		slObjSel.add("to[" + relClassifiedItems + "].from.attribute["
				+ LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value");
		// Added by Lalitha on 12/13/2018 --ends
		slObjSel.add("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value");
		slObjSel.add("attribute[IHSLast Sync Date].value");
		Map<String, String> ihsPAttrInfoMap = new HashMap<String, String>();
		Map<String, String> ihsCAttrInfoMap = new HashMap<String, String>();
		ArrayList<Object> partList;
		Reader inputStreamReader = null;
		Map mIHSEnoviaAttributeNameMapping = new HashMap(10);
		String strIHSResponseXML = "";
		String MQLResult = "";
		String strPageFileName = "XORGIHSIntegrationMapping";
		MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
		byte[] bytes = MQLResult.getBytes("UTF-8");
		InputStream input = new ByteArrayInputStream(bytes);
		Properties prop = new Properties();
		prop.load(input);
		// Added by Lalitha on 1/10/2019 --starts
		String strReturnHTML = "IHS MEP Comparison Report From : " + strFromDate + " To : " + strToDate + "\n";
		// Added by Lalitha on 1/10/2019 --Ends
		// Modified by Lalitha on 1/10/2019 --starts
		strReturnHTML += "<table border='1' class='list'>";
		// Modified by Lalitha on 1/10/2019 --Ends
		strReturnHTML += "<tr><th style='width: 200px;' colspan=1>Name</th><th style='width: 200px;' colspan=1>Description</th><th style='width: 200px;' colspan=1 >IHS Sync date</th><th style='width: 200px;' colspan=2 >Attribute Details</th><th style='width: 200px;' colspan=1>PLM Part Number</th><th style='width: 200px;' colspan= 1 >Manufacturer</th></tr>";
		strReturnHTML += "<tr><th></th><th></th><th></th><th>Previous</th><th>Current</th><th></th><th></th></tr>";

		if (null != strExportToExcel && "YES".equals(strExportToExcel)) {

			// Modified by Lalitha on 1/10/2019 --starts
			strFromDate = strFromDate.replace(",", "");
			strToDate = strToDate.replace(",", "");
			strReturnHTML = "IHS MEP Comparison Report" + "\t" + "From :" + strFromDate + "\t" + "To :" + strToDate
					+ "\n";
			strReturnHTML += "Name\tDescription\tIHS Sync date\tAttribute Details\t\tPLM Part Number\tManufacturer\n\t\t\tPrevious\tCurrent\t\t\t\n";
			// Modified by Lalitha on 1/10/2019 --Ends

		}
		try {
			MapList mlMEPObjects = doMEP.findObjects(context, sPartType, "eService Production", sWhere, slObjSel);
			for (int i = 0; i < mlMEPObjects.size(); i++) {
				Map mMEPIDMap = (Map) mlMEPObjects.get(i);
				sMEPId = (String) mMEPIDMap.get("id");
				sMEPName = (String) mMEPIDMap.get("name");
				sCIHSResponseXML = (String) mMEPIDMap.get("attribute[IHSResponse XML Previous].value");
				sCManufacture = (String) mMEPIDMap.get("to[Manufacturing Responsibility].from.name");
				sCManufactureId = (String) mMEPIDMap.get("to[Manufacturing Responsibility].from.id");
				sCSyncdate = (String) mMEPIDMap.get("attribute[IHSLast Sync Date].value");
				// Added by Lalitha on 12/13/2018 --starts
				sPartName = (String) mMEPIDMap.get("to[Manufacturer Equivalent].from.name");
				sPartId = (String) mMEPIDMap.get("to[Manufacturer Equivalent].from.id");
				// Added by Lalitha on 12/13/2018--Ends
				DomainObject MEPdomainObject = new DomainObject(sMEPId);
				sDescription = MEPdomainObject.getDescription(context);

				strClassificationInterface = (String) mMEPIDMap.get("to[" + relClassifiedItems + "].from.attribute["
						+ LibraryCentralConstants.ATTRIBUTE_MXSYS_INTERFACE + "].value");
				strClassName = (String) mMEPIDMap.get("to[" + relClassifiedItems + "].from.name");

				// Commented by Lalitha on 12/13/2018--starts
				/*
				 * mapListPartID = MEPdomainObject.getRelatedObjects(
				 * context,"Manufacturer Equivalent","Part",slObjSel,null,true,true,(short)1,
				 * null,null); for (int k = 0; k < mapListPartID.size(); k++) { Map mPartIDMap =
				 * (Map) mapListPartID.get(k); sPartName = (String)mPartIDMap.get("name");
				 * sPartId = (String)mPartIDMap.get("id"); }
				 */
				// Commented by Lalitha on 12/13/2018--Ends
				/*
				 * mapClassifiedID = MEPdomainObject.getRelatedObjects( context,
				 * LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
				 * LibraryCentralConstants.TYPE_GENERAL_CLASS, // object pattern slObjSel, //
				 * object selects null, // relationship selects true, // to direction true, //
				 * from direction (short) 1, // recursion level null, // object where clause
				 * DomainConstants.EMPTY_STRING, 0);
				 * 
				 * Iterator<Map<String,String>> objItr =(Iterator) mapClassifiedID.iterator();
				 * while (objItr.hasNext()) { objMap = (Map<String, String>) objItr.next();
				 * strClassificationInterface =
				 * (String)objMap.get("attribute["+LibraryCentralConstants.
				 * ATTRIBUTE_MXSYS_INTERFACE+"].value"); strClassName = objMap.get("name"); }
				 */
				if (strClassificationInterface != null && !"".equals(strClassificationInterface)) {
					strMQL = "print interface $1 select attribute dump";
					resStr = MqlUtil.mqlCommand(context, strMQL, true, strClassificationInterface);
					slMQLResult = FrameworkUtil.split(resStr, ",");
					if (slMQLResult.size() > 0) {
						slInterfaceAttrList.addAll(slMQLResult);

					}
				}
				for (int n = 0; n < slInterfaceAttrList.size(); n++) {
					strIHSEnoviaAttrName = (String) slInterfaceAttrList.get(n);
					strIHSAttrNameModified = strIHSEnoviaAttrName.replaceAll(" ", "_");
					strPropertyKey = "IHSAttrMapping_" + strClassName.replace(" ", "_") + "_" + strIHSAttrNameModified;
					strPropertyKeyValue = prop.getProperty(strPropertyKey);
					if (strPropertyKeyValue != null && !"".equals(strPropertyKeyValue)) {
						if (!mIHSEnoviaAttributeNameMapping.containsKey(strPropertyKeyValue)) {
							mIHSEnoviaAttributeNameMapping.put(strPropertyKeyValue, strIHSEnoviaAttrName);
						}
					}
				}
				BusinessObject busObject = new BusinessObject(sMEPId);
				AttributeList attrList = busObject.getAttributeValues(context, slInterfaceAttrList);
				AttributeItr attrItr = new AttributeItr(attrList);
				while (attrItr.next()) {
					Attribute attribute = attrItr.obj();
					sAttrName = attribute.getName().trim();
					sAttrValue = attribute.getValue().trim();
					ihsCAttrInfoMap.put(sAttrName, sAttrValue);
				}
				if (sCIHSResponseXML != null && !"".equals(sCIHSResponseXML)) {

					inputStreamReader = new StringReader(sCIHSResponseXML);
					strIHSResponseXML = IOUtils.toString(inputStreamReader);
					inputStreamReader = new StringReader(strIHSResponseXML);
					JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
					if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
						ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
						generated.Result result = (generated.Result) xmlResultList.get(0);
						if ((Integer.parseInt(result.getCount())) > 0) {
							partList = (ArrayList<Object>) result.getPartOrMfrOrError();
							if (partList.size() == 1) {
								part = (generated.Part) partList.get(0);
								ihsPAttrInfoMap.put("IHSCategory", part.getCategory());
								ihsPAttrInfoMap.put("IHSManufacturer Name", part.getMfrName());
								ihsPAttrInfoMap.put("IHSManufacturer Part Number", part.getManufacturerPartNumber());
								ihsPAttrInfoMap.put("IHSMfr Package Description", part.getMfgPkgDesc());
								ihsPAttrInfoMap.put("IHSObjectID", part.getObjectId());
								ihsPAttrInfoMap.put("IHSCAGE Code", part.getCageCode());
								ihsPAttrInfoMap.put("IHSPart Description", part.getPartDescription());
								ihsPAttrInfoMap.put("IHSStatus", part.getPartStatus());

								ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
										.getPartdetails();

								for (int l = 0; l < partDetailsList.size(); l++) {
									partDetails = partDetailsList.get(l);
									IHSdetailsList = (ArrayList<Details>) partDetails.getDetails();
									if (IHSdetailsList.size() > 0) {
										Iterator<Details> itr = IHSdetailsList.iterator();
										while (itr.hasNext()) {
											details = (Details) itr.next();
											strIHSAttrID = (String) details.getId();
											sEnoviakeys = (String) mIHSEnoviaAttributeNameMapping.get(strIHSAttrID);
											strIHSAttrValue = (String) details.getValue();
											if (strIHSAttrID != "" && !strIHSAttrID.equals("")) {
												ihsPAttrInfoMap.put(sEnoviakeys, strIHSAttrValue);
											}
										}
									}
								}
							}

						}
					}
				}
				scurrAttrDetails = "";
				sPreAttrDetails = "";
				for (Map.Entry<String, String> entry : ihsPAttrInfoMap.entrySet()) {
					strMapKey = entry.getKey();
					strMapValue = entry.getValue();

					for (Map.Entry<String, String> entry1 : ihsCAttrInfoMap.entrySet()) {

						strMapKey1 = entry1.getKey();
						strMapValue1 = entry1.getValue();
						if (strMapKey != null && !"".equals(strMapKey) && strMapKey1 != null && !"".equals(strMapKey1)
								&& strMapKey.equalsIgnoreCase(strMapKey1)) {

							if (strMapValue != null && !"".equals(strMapValue) && strMapValue1 != null
									&& !"".equals(strMapValue1) && !strMapValue.equalsIgnoreCase(strMapValue1)) {
								if (scurrAttrDetails.length() == 0) {
									// Modified by Lalitha on 1/30/2019 --Starts
									if (null == strExportToExcel || "NO".equals(strExportToExcel)) {
										scurrAttrDetails = "<b>" + strMapKey1 + "</b>" + ":" + strMapValue1;
									} else {
										scurrAttrDetails = strMapKey1 + ":" + strMapValue1;
									}
								} else {
									if (null == strExportToExcel || "NO".equals(strExportToExcel)) {
										scurrAttrDetails = scurrAttrDetails + "<br>" + "<b>" + strMapKey1 + "</b>" + ":"
												+ strMapValue1;
									} else {
										scurrAttrDetails = scurrAttrDetails + "<br>" + strMapKey1 + ":" + strMapValue1;
									}
								}
								if (sPreAttrDetails.length() == 0) {

									if (null == strExportToExcel || "NO".equals(strExportToExcel)) {
										sPreAttrDetails = "<b>" + strMapKey + "</b>" + ":" + strMapValue;
									} else {
										sPreAttrDetails = strMapKey + ":" + strMapValue;
									}
								} else {
									if (null == strExportToExcel || "NO".equals(strExportToExcel)) {
										sPreAttrDetails = sPreAttrDetails + "<br>" + "<b>" + strMapKey + "</b>" + ":"
												+ strMapValue;
									} else {
										sPreAttrDetails = sPreAttrDetails + "<br>" + strMapKey + ":" + strMapValue;
									}
									// Modified by Lalitha on 1/30/2019 --Ends
								}
							}
						}
					}
				}
				if (null == strExportToExcel || "NO".equals(strExportToExcel)) {
					if (scurrAttrDetails == null || "".equals(scurrAttrDetails)) {
						// UnCommented by Lalitha on 12/13/2018 --starts
						// scurrAttrDetails = "No IHS Attributes Modified";
						// sPreAttrDetails = "No IHS Attributes Modified";
						// UnCommented by Lalitha on 12/13/2018--Ends
						// Commented by Lalitha on 12/13/2018 --starts
						continue;
						// Commented by Lalitha on 12/13/2018 --Ends
					}
					sMEPUrl = "<a href=\"javascript:void(0)\" onClick=\"javascript:showModalDialog('../common/emxTree.jsp?objectId="
							+ XSSUtil.encodeForJavaScript(context, sMEPId)
							+ "','860','520');\" style = 'color:#1E90FF' >" + XSSUtil.encodeForXML(context, sMEPName)
							+ "</a>";
					sPartUrl = "<a href=\"javascript:void(0)\" onClick=\"javascript:showModalDialog('../common/emxTree.jsp?objectId="
							+ XSSUtil.encodeForJavaScript(context, sPartId)
							+ "','860','520');\" style = 'color:#1E90FF' >" + XSSUtil.encodeForXML(context, sPartName)
							+ "</a>";
					sMnufUrl = "<a href=\"javascript:void(0)\" onClick=\"javascript:showModalDialog('../common/emxTree.jsp?objectId="
							+ XSSUtil.encodeForJavaScript(context, sCManufactureId)
							+ "','860','520');\" style = 'color:#1E90FF' >"
							+ XSSUtil.encodeForXML(context, sCManufacture) + "</a>";
					strReturnHTML += "<tr class='" + strCSSClass + "'><td class='sorted' style='width: 200px;'>"
							+ sMEPUrl + "</td><td style='width: 200px;' class='sorted'>" + sDescription
							+ "</td><td style='width: 200px;' class='sorted'>" + sCSyncdate
							+ "</td><td style='width: 200px;' class='sorted'>" + sPreAttrDetails
							+ "</td><td style='width: 200px;' class='sorted'>" + scurrAttrDetails
							+ "</td><td style='width: 200px;' class='sorted'><img src = '../common/images/iconSmallPart.png' border=3></img>"
							+ sPartUrl
							+ "</td><td style='width: 200px;' class='sorted'><img src = '../common/images/iconSmallCompany.gif' border=3></img>"
							+ sMnufUrl + "</td></tr>";
					if ("even".equals(strCSSClass)) {
						strCSSClass = "odd";
					} else {
						strCSSClass = "even";
					}
				} else if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
					sCurrentAttributes = scurrAttrDetails.replace("<br>", "\n");
					sCurrentAttributes = "\"" + sCurrentAttributes + "\"";
					sPreviousAttributes = sPreAttrDetails.replace("<br>", "\n ");
					sPreviousAttributes = "\"" + sPreviousAttributes + "\"";
					if (scurrAttrDetails == null || "".equals(scurrAttrDetails)) {
						// UnCommented by Lalitha on 12/13/2018--starts
						// sCurrentAttributes = "No IHS Attributes Modified";
						// sPreviousAttributes = "No IHS Attributes Modified";
						// UnCommented by Lalitha on 12/13/2018--Ends
						// Commented by Lalitha on 12/13/2018--starts
						continue;
						// Commented by Lalitha on 12/13/2018--starts
					}
					strReturnHTML += sMEPName + "\t" + sDescription + "\t" + sCSyncdate + "\t" + sPreviousAttributes
							+ "\t" + sCurrentAttributes + "\t" + sPartName + "\t" + sCManufacture + "\n";
				}
			}
			if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
				strReturnHTML += "\n";
			} else if (null == strExportToExcel && "NO".equals(strExportToExcel)) {
				strReturnHTML += "</table>";
			} else {
				strReturnHTML += "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturnHTML;
	}
	// Added by Lalitha --Ends

	public HashMap parseXMLAndShowReport(Context context, String strXMLData, Properties prop, String strCSSClass,
			String strExportToExcel, String strbackground) {
		HashMap hmReturnMap = new HashMap(2);
		String strReturnHTML = "";
		try {
			String ATTRIBUTE_IHSOOBJECT_ID = PropertyUtil.getSchemaProperty(context, "attribute_IHSObjectID");
			String ATTRIBUTE_IHSMANUFACTURER_PART_NUMBER = PropertyUtil.getSchemaProperty(context,
					"attribute_IHSManufacturerPartNumber");
			// String strProject_Already_StartedMessage =
			// EnoviaResourceBundle.getProperty(context,"emxProgramCentralStringResource",
			// new
			// Locale(context.getSession().getLanguage()),"emxProgramCentral.Notice.Project_Already_Started");
			StringList slSelectable = new StringList(2);
			slSelectable.add(DomainObject.SELECT_ID);
			// Added for Issue #281 Starts
			StringList slSelectable2 = new StringList(4);
			slSelectable2.add(DomainObject.SELECT_ID);
			slSelectable2.add("to[Manufacturing Responsibility].from.id");
			slSelectable2.add("to[Manufacturer Equivalent].from.id");
			// Added for Issue #281 Ends

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(strXMLData));

			Document doc = db.parse(is);
			NodeList nNodeXMLResult = doc.getElementsByTagName("XMLResult");

			for (int i = 0; i < nNodeXMLResult.getLength(); i++) {
				Element eElementXMLResult = (Element) nNodeXMLResult.item(i);
				String nextPageIdtmp = eElementXMLResult.getAttribute("nextpageid");
				hmReturnMap.put("nextPageIdtmp", nextPageIdtmp);
				NodeList nNodeResult = eElementXMLResult.getElementsByTagName("Result");
				for (int j = 0; j < nNodeResult.getLength(); j++) {
					Element eElementResult = (Element) nNodeResult.item(j);
					NodeList nNodePart = eElementResult.getElementsByTagName("Part");
					int iModifiedPartCount = nNodePart.getLength();
					if (iModifiedPartCount == 0) {
						if (null != strExportToExcel && "YES".equals(strExportToExcel)) {
							strReturnHTML += "No Data modified in this time frame\n";
						} else {
							strReturnHTML += "<tr class='" + strCSSClass
									+ "'><td colspan='3'>No Data modified in this time frame</td></tr>";
						}
					} else {
						for (int k = 0; k < iModifiedPartCount; k++) {
							Element eElementPart = (Element) nNodePart.item(k);
							String strPartClassificationValue = eElementPart.getAttribute("part-type");
							String strPartNameValue = eElementPart.getAttribute("manufacturer-part-number");
							String strIHSPartObjectIdValue = eElementPart.getAttribute("object-id");
							String strWhere = "attribute[" + ATTRIBUTE_IHSOOBJECT_ID + "] == '"
									+ strIHSPartObjectIdValue + "' && attribute["
									+ ATTRIBUTE_IHSMANUFACTURER_PART_NUMBER + "] == '" + strPartNameValue + "'";

							// Check if Part is present in enovia
							MapList mlMEPs = DomainObject.findObjects(context, DomainConstants.TYPE_PART,
									DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
									DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, strWhere, false,
									slSelectable2);// Modified for Issue #281
							if (mlMEPs.size() == 0) {
								continue;
							}
							// Added for Issue #281 Starts
							StringList GPNPartsList = new StringList();
							String strManufacturer = "";
							String strPartIds = "";
							for (int ij = 0; ij < mlMEPs.size(); ij++) {
								Map mlMEPsMap = (Map) mlMEPs.get(ij);
								Object objGPNIds = (Object) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
								if (objGPNIds instanceof StringList) {
									GPNPartsList = (StringList) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
									for (int kl = 0; kl < GPNPartsList.size(); kl++) {
										strPartIds = strPartIds + "|" + (String) GPNPartsList.get(kl);
									}
								} else if (objGPNIds instanceof String) {
									strPartIds = (String) mlMEPsMap.get("to[Manufacturer Equivalent].from.id");
								}
								strManufacturer = (String) mlMEPsMap.get("to[Manufacturing Responsibility].from.id");
							}
							strPartIds = strPartIds.replaceFirst("|", "");
							// Added for Issue #281 Ends

							// Get the enovia attribute mapping for the IHS attributes
							Map mIHSEnoviaAttributeNameMapping = new HashMap(10);
							getIHSEnoviaMappingForClassification(context, strPartClassificationValue, slSelectable,
									mIHSEnoviaAttributeNameMapping, prop);

							strReturnHTML += getHTMLReportForUI1(context, eElementPart, strCSSClass, hmReturnMap,
									strPartNameValue, mIHSEnoviaAttributeNameMapping, strManufacturer, strPartIds);// Modified
																													// for
																													// Issue
																													// #281
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		hmReturnMap.put("strReturnHTML", strReturnHTML);
		hmReturnMap.put("strCSSClass", strCSSClass);
		return hmReturnMap;
	}

	public String getHTMLReportForUI1(Context context, Element eElementPart, String strCSSClass, HashMap hmReturnMap,
			String strPartNameValue, Map mIHSEnoviaAttributeNameMapping, String strManufacturer, String strPartIds) {
		String strReturnHTML = "";
		strReturnHTML += "<script language=\"javascript\" src=\"../common/scripts/emxUITableUtil.js\"></script>";// Added
																													// for
																													// Issue
																													// #281

		try {
			// Added for Issue #281 Starts
			StringList slSelectable3 = new StringList(4);
			slSelectable3.add(DomainObject.SELECT_TYPE);
			slSelectable3.add(DomainObject.SELECT_ID);
			slSelectable3.add(DomainObject.SELECT_NAME);
			slSelectable3.add(DomainObject.SELECT_REVISION);
			DomainObject domManufacturerObj = new DomainObject(strManufacturer);

			Map dataManufacturerMap = domManufacturerObj.getInfo(context, slSelectable3);
			String sObjManufacturerType = (String) dataManufacturerMap.get(DomainObject.SELECT_TYPE);
			String sObjManufacturerName = (String) dataManufacturerMap.get(DomainObject.SELECT_NAME);
			String sObjManufacturerRev = (String) dataManufacturerMap.get(DomainObject.SELECT_REVISION);
			String sObjManufacturerId = (String) dataManufacturerMap.get(DomainObject.SELECT_ID);
			// Added for Issue #281 Ends

			String strPartDescriptionValue = eElementPart.getAttribute("part-description");
			NodeList nNodePartDetails = eElementPart.getElementsByTagName("Partdetails");
			strReturnHTML += "<tr class='" + strCSSClass + "'>";
			strReturnHTML += "<td>" + strPartNameValue + "</td><td>" + strPartDescriptionValue + "</td>";
			strReturnHTML += "<td>";
			int iPartDetailsSize = nNodePartDetails.getLength();
			if (iPartDetailsSize == 0) {
				strReturnHTML += "No Data modified in this time frame";
			} else {
				boolean boolHasPartDetailsOrDocument = false;
				for (int l = 0; l < iPartDetailsSize; l++) {
					Element eElementPartDetails = (Element) nNodePartDetails.item(l);
					String strTypeAttributeValue = eElementPartDetails.getAttribute("type");
					if ("DOC".equals(strTypeAttributeValue)) {
						// Setting Document attribute values
						NodeList nNodeDoc = eElementPartDetails.getElementsByTagName("Doc");
						String strDocURLLabelValue = (String) mIHSEnoviaAttributeNameMapping.get("doc_url");
						for (int n = 0; n < nNodeDoc.getLength(); n++) {
							Element eElementDoc = (Element) nNodeDoc.item(n);
							String strDocURLAttributeValue = eElementDoc.getAttribute("doc_url");
							// Modified by Lalitha on 1/30/2019 --starts
							strReturnHTML += "<b>" + strDocURLLabelValue + "</b>" + ": " + strDocURLAttributeValue
									+ "<br/>";

							String strDocTypeAttributeValue = eElementDoc.getAttribute("doc-type");
							strReturnHTML += "<b>" + "IHS Document Type: " + "</b>" + strDocTypeAttributeValue
									+ "<br/>";

							String strDocTitleAttributeValue = eElementDoc.getAttribute("doc_title");
							strReturnHTML += "<b>" + "IHS Document Title: " + "</b>" + strDocTitleAttributeValue
									+ "<br/>";
							// Modified by Lalitha on 1/30/2019 --starts
						}
						boolHasPartDetailsOrDocument = true;
					} else if ("DETAILS".equals(strTypeAttributeValue)) {
						// Update the Part's detailed attribute values in report
						NodeList nNodeDetails = eElementPartDetails.getElementsByTagName("Details");
						int iDetailsCount = nNodeDetails.getLength();
						if (iDetailsCount == 0) {
							strReturnHTML += "No Data modified in this time frame";
						} else {
							for (int m = 0; m < iDetailsCount; m++) {
								Element eElementDetails = (Element) nNodeDetails.item(m);
								String strIHSPartDetailsUniqueAttributeId = eElementDetails.getAttribute("id");
								String strIHSAttributeMappedLabel = (String) mIHSEnoviaAttributeNameMapping
										.get(strIHSPartDetailsUniqueAttributeId);
								if (null != strIHSAttributeMappedLabel) {
									String strIHSAttributeValue = getCharacterDataFromElement(eElementDetails);
									// Modified by Lalitha on 1/30/2019 --starts
									strReturnHTML += "<b>" + strIHSAttributeMappedLabel + "</b>" + ": "
											+ strIHSAttributeValue + "<br/>";
									// Modified by Lalitha on 1/30/2019 --Ends
								}
							}
						}
						boolHasPartDetailsOrDocument = true;
					}
				}
				if (!boolHasPartDetailsOrDocument) {
					strReturnHTML += "No Data modified in this time frame";
				}
			}
			strReturnHTML += "</td>";
			// Added for Issue #281 Starts
			String strReturnHTML2 = "<td><table>";
			StringList strPartIdsList = FrameworkUtil.split(strPartIds, "|");
			for (int ih = 0; ih < strPartIdsList.size(); ih++) {
				strReturnHTML2 += "<tr><td>";
				String strPartId = (String) strPartIdsList.get(ih);
				DomainObject domPartObj = new DomainObject(strPartId);
				Map dataGPNMap = domPartObj.getInfo(context, slSelectable3);
				String sObjPartType = (String) dataGPNMap.get(DomainObject.SELECT_TYPE);
				String sObjPartName = (String) dataGPNMap.get(DomainObject.SELECT_NAME);
				String sObjPartRev = (String) dataGPNMap.get(DomainObject.SELECT_REVISION);
				StringBuffer temp = new StringBuffer();
				temp.append(sObjPartName);
				strReturnHTML2 += temp.toString() + "</td></tr>";
			}
			strReturnHTML2 += "</table></td><td>";
			StringBuffer temp2 = new StringBuffer();
			temp2.append(XSSUtil.encodeForHTML(context, sObjManufacturerName));
			strReturnHTML += strReturnHTML2 + temp2.toString() + "</td>";
			// Added for Issue #281 Ends

			if ("even".equals(strCSSClass)) {
				strCSSClass = "odd";
			} else {
				strCSSClass = "even";
			}
			hmReturnMap.put("strCSSClass", strCSSClass);
			strReturnHTML += "</tr>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturnHTML;
	}

	public void IHSWeeklyUpdateBackGroundJob(Context context, String[] args) throws Exception {

		try {

			String sFormName = args[0];
			String sToName = args[1];
			String sExcel = args[2];
			String sbackground = args[3];
			String sbgsubmit = args[4];
			Map programMap = new HashMap();
			programMap.put("strFromDate", sFormName);
			programMap.put("strToDate", sToName);
			if (null != sExcel) {
				programMap.put("exportToExcel", sExcel);
			}
			programMap.put("backgroundSubmit", sbgsubmit);
			String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
			String[] strArgs = JPO.packArgs(programMap);
			String strReturnValue = getIHSWeeklyUpdateReport(context, strArgs);
			String strWorkspace = context.createWorkspace();
			StringBuffer filename = new StringBuffer(50);
			if (sbackground.equals("bgGenerateReport")) {
				filename.append(strWorkspace);
				filename.append("\\");
				filename.append("IHS_Updated_Parts_Report");
				filename.append("_");
				filename.append(fileCreateTimeStamp);
				filename.append(".html");
			} else {
				filename.append(strWorkspace);
				filename.append("\\");
				filename.append("IHS_Updated_Parts_Report");
				filename.append("_");
				filename.append(fileCreateTimeStamp);
				filename.append(".xls");
			}

			String sFilename = filename.toString();
			BufferedWriter bw = new BufferedWriter(new FileWriter(sFilename));
			bw.write(strReturnValue);
			bw.close();
			googCustomFunctions_mxJPO custom = new googCustomFunctions_mxJPO();
			String sPath = strWorkspace;
			String[] toList = new String[1];
			toList[0] = context.getUser();
			StringBuilder sbBody = new StringBuilder();
			sbBody.append("IHS Updated Parts Report");
			// passing fully qualified file name 'sFilename'- modified by kranthikiranc
			// Modified by Lalitha on 1/18/2019 --starts
			String sSubject = "IHS Updated Parts Report From : " + sFormName + " To : " + sToName;
			sendEMailToUser(context, sFilename, toList, toList, sSubject, sbBody);
			// Modified by Lalitha on 1/18/2019 --Ends

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Added by Lalitha --Ends

	// Added by kranthikiranc for 'IHS Updated Parts Report'- Start
	/*
	 * This method sends an email to the to users with the file attachment
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return nothing
	 * 
	 * @throws Exception if the operation fails.
	 */

	public void sendEMailToUser(Context context, String sAbsolutepath, String[] toList, String[] CCList,
			String sSubject, StringBuilder sbBody) throws Exception {
		try {
			// get the host
			String host = PropertyUtil.getEnvironmentProperty(context, "MX_SMTP_HOST");

			// get the from user: Using the Super User Context to send the
			// mail(BackGround Job)
			String from = PersonUtil.getEmail(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"));
			// TODO: Change this later, currently Hard Coded this to make sure
			// User Agent Mail is not Waymo but google
			from = "jackbbrown@google.com";

			// Prepare the To User List
			Address[] mailto = new Address[toList.length];

			for (int i = 0; i < toList.length; i++) {
				// sending to multiple person
				String sPersonEmail = MqlUtil.mqlCommand(context, "print person '$1' select $2 dump",
						toList[i].toString(), "email");
				mailto[i] = new InternetAddress(sPersonEmail);
			}

			// Prepare the CC User List
			Address[] cc = new Address[CCList.length];

			for (int j = 0; j < CCList.length; j++) {
				// sending to multiple person
				cc[j] = new InternetAddress(PersonUtil.getEmail(context, CCList[j].toString()));
			}

			// get the attachment file
			String fileAttachment = sAbsolutepath; // Complete Path
			String strPersonal = PersonUtil.getEmail(context,
					PropertyUtil.getSchemaProperty(context, "person_UserAgent")); // From User
			// Hard Coded this to make sure User Agent Mail is not Waymo but
			// google
			strPersonal = "jackbbrown@google.com";

			// Get system properties
			Properties props = System.getProperties();

			// Setup mail server
			props.put("mail.smtp.host", host);

			// Get session
			Session session = Session.getInstance(props, null);

			// Define message
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from, strPersonal));

			// Add TO List
			message.addRecipients(Message.RecipientType.TO, mailto);

			// commented 'cc' list: kranthikiranc
			// message.addRecipients(Message.RecipientType.CC, cc);

			message.setSubject(sSubject);

			// create the message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();

			// fill message/Body for the mail
			messageBodyPart.setText(sbBody.toString());

			Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);

			// for attachment
			messageBodyPart = new MimeBodyPart();

			DataSource source = new FileDataSource(fileAttachment);

			messageBodyPart.setDataHandler(new DataHandler(source));

			String fileName = "";

			if (fileAttachment.indexOf("\\") != -1) {

				fileName = fileAttachment.substring(fileAttachment.lastIndexOf("\\") + 1, fileAttachment.length());

			}

			messageBodyPart.setFileName(fileName);

			multipart.addBodyPart(messageBodyPart);
			// Put parts in message
			message.setContent(multipart);

			Transport transport = session.getTransport("smtp");

			transport.connect();
			// Send the message
			// javax.mail.Transport.send(message);
			transport.send(message);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}// end of send mail method
		// Added by kranthikiranc for 'IHS Updated Parts Report'- End

	// Added by Lalitha on 12/13/2018 -statrts
	public void googBackgroundJob(Context context, String[] args) throws Exception {

		try {
			String sFormName = args[0];
			String sToName = args[1];
			String sExcel = args[2];
			String sbackground = args[3];
			String sbgsubmit = args[4];
			Map programMap = new HashMap();
			programMap.put("strFromDate", sFormName);
			programMap.put("strToDate", sToName);
			programMap.put("backgroundSubmit", sbgsubmit);
			if (null != sExcel) {
				programMap.put("exportToExcel", sExcel);
			}
			String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
			String[] strArgs = JPO.packArgs(programMap);
			String strReturnValue = getIHSComparisonReport(context, strArgs);
			String strWorkspace = context.createWorkspace();
			StringBuffer filename = new StringBuffer(50);
			if (sbackground.equals("bgGenerateReport")) {
				filename.append(strWorkspace);
				filename.append("\\");
				filename.append("IHS_Comparision_Report");
				filename.append("_");
				filename.append(fileCreateTimeStamp);
				filename.append(".html");
			} else {
				filename.append(strWorkspace);
				filename.append("\\");
				filename.append("IHS_Comparision_Report");
				filename.append("_");
				filename.append(fileCreateTimeStamp);
				filename.append(".xls");
			}

			String sFilename = filename.toString();
			BufferedWriter bw = new BufferedWriter(new FileWriter(sFilename));
			bw.write(strReturnValue);
			bw.close();
			googCustomFunctions_mxJPO custom = new googCustomFunctions_mxJPO();
			String sPath = strWorkspace;
			String[] toList = new String[1];
			toList[0] = context.getUser();
			StringBuilder sbBody = new StringBuilder();
			sbBody.append("IHS MEP Comparision Report");
			// custom.sendEMailToUser(context,sPath,toList,toList,"IHS MEP Comparision
			// Report",sbBody);
			// Added by Lalitha on 1/10/2019 -starts
			// Modified by Lalitha on 1/18/2019 --starts
			String sSubject = "IHS MEP Comparision Report From : " + sFormName + " To : " + sToName;
			sendEMailToUser(context, sFilename, toList, toList, sSubject, sbBody);
			// Modified by Lalitha on 1/18/2019 --Ends
			// Added by Lalitha on 1/10/2019 -Ends
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Added by Lalitha on 12/13/2018--Ends

	// Added by Lalitha for Issue #68 --Starts
	public String displayProductChangeNotification(Context context, String[] args) throws FrameworkException {
		String strReturnValue = "";
		StringBuffer sbRes = new StringBuffer();
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap requestMap = (HashMap) programMap.get("requestMap");
			String strMEPOID = (String) requestMap.get("objectId");
			String strIHSResponseXML = PropertyUtil.getSchemaProperty("attribute_IHSResponseXML");
			String sIHSResponseXML = "";
			String strIHSResponse = "";
			String strPartDetailsType = "";
			String sAlertDesc = "";
			String sAlertIssuedate = "";
			String sDocType = "";
			String sDocURL = "";
			String strDocURLSorted = "";
			String sAlertDescDetails = "";
			String sAlertType = "";
			String sAlertIssueDetails = "";
			String sFirstDescValue = "";
			String sDescvalDisplay = "";
			String strIssuedate = "";
			String strDocUrl = "";
			Map<String, String> objMap = null;
			StringList sSortedDateList = new StringList();
			MapList mlAlertsDetails = new MapList();
			generated.Partdetails partDetails = new generated.Partdetails();
			generated.Alerts Alerts = new generated.Alerts();
			generated.Doc doc = new generated.Doc();
			Map<String, String> AlertsMap = null;
			Map<String, String> ImplementaionMap = new HashMap<String, String>();
			ArrayList<Alerts> AlertsList;
			ArrayList<Object> partList;
			ArrayList<Doc> IHSdocList;
			if (!UIUtil.isNullOrEmpty(strMEPOID)) {
				DomainObject Domobj = new DomainObject(strMEPOID);
				sIHSResponseXML = Domobj.getAttributeValue(context, strIHSResponseXML);
				if (!UIUtil.isNullOrEmpty(sIHSResponseXML)) {
					Reader inputStreamReader = new StringReader(sIHSResponseXML);
					strIHSResponse = IOUtils.toString(inputStreamReader);
					inputStreamReader = new StringReader(strIHSResponse);
					JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
					if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
						ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
						generated.Result result = (generated.Result) xmlResultList.get(0);
						if ((Integer.parseInt(result.getCount())) > 0) {
							partList = (ArrayList<Object>) result.getPartOrMfrOrError();
							if (partList.size() == 1) {
								generated.Part part = (generated.Part) partList.get(0);
								ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
										.getPartdetails();

								for (int i = 0; i < partDetailsList.size(); i++) {
									partDetails = partDetailsList.get(i);
									strPartDetailsType = partDetails.getType();
									if (strPartDetailsType.equalsIgnoreCase("ALERTS")) {
										AlertsList = (ArrayList<Alerts>) partDetails.getAlerts();
										for (int j = 0; j < AlertsList.size(); j++) {
											Alerts = AlertsList.get(j);
											sAlertType = Alerts.getAlertType();
											if (sAlertType.equalsIgnoreCase("PCN")) {
												AlertsMap = new HashMap<String, String>();
												sAlertDesc = Alerts.getChangeNotiDesc();
												sAlertIssuedate = Alerts.getChgNotiIssueDate();
												AlertsMap.put("AlertDesc", sAlertDesc);
												AlertsMap.put("AlertIssuedate", sAlertIssuedate);
												IHSdocList = (ArrayList<Doc>) Alerts.getDoc();
												for (int k = 0; k < IHSdocList.size(); k++) {
													doc = IHSdocList.get(k);
													sDocType = doc.getDocType();
													sDocURL = doc.getDocUrl();
													AlertsMap.put("DocType", sDocType);
													AlertsMap.put("DocURL", sDocURL);
												}
												mlAlertsDetails.add(AlertsMap);
											}
										}
									}
								}
								ImplementaionMap = new HashMap<String, String>();
								for (int ii = 0; ii < mlAlertsDetails.size(); ii++) {
									objMap = (Map<String, String>) mlAlertsDetails.get(ii);
									strIssuedate = (String) objMap.get("AlertIssuedate");
									strDocUrl = (String) objMap.get("DocURL");
									if (strIssuedate != null && !"".equalsIgnoreCase(strIssuedate)) {
										ImplementaionMap.put(strIssuedate, strDocUrl);
									}
								}
								sSortedDateList = getSortedDateList(context, ImplementaionMap);
								strDocURLSorted = "";
								for (int kk = 0; kk < sSortedDateList.size(); kk++) {
									String strDateKey = (String) sSortedDateList.get(kk);
									String strUrl = (String) ImplementaionMap.get(strDateKey);
									if (kk == 0) {
										strDocURLSorted = strUrl;
									} else {
										strDocURLSorted = strDocURLSorted + "\n" + strUrl;
									}
								}
								if (AlertsMap != null && !"".equals(AlertsMap)) {
									sAlertDescDetails = AlertsMap.get("AlertDesc");
									if (sAlertDescDetails != null && !"".equals(sAlertDescDetails)) {
										sAlertIssueDetails = AlertsMap.get("AlertIssuedate");
										if (sAlertDescDetails.length() <= 16) {
											sDescvalDisplay = sAlertDescDetails;
										} else {
											sFirstDescValue = sAlertDescDetails.substring(0, 15);
											sDescvalDisplay = sFirstDescValue + "(" + sAlertIssueDetails + ")";
										}
										// Modified by Lalitha for #68 Issue on 12/5/2018 --starts
										// Modified by Lalitha for #68 Issue on 12/5/2018 --Ends
										String finalizeURL = "../common/goog_MEPTableDisplay.jsp?objectId=" + strMEPOID;
										sbRes.append("<a href =" + strDocURLSorted
												+ " target=_blank data-toggle='tooltip' title='" + sAlertDescDetails
												+ "'>" + sDescvalDisplay + "</a>");
										sbRes.append("<a href=\"javascript:emxTableColumnLinkClick('" + finalizeURL
												+ "','875','550')\">");
										if (!UIUtil.isNullOrEmpty(sDescvalDisplay)
												&& !UIUtil.isNullOrEmpty(strDocURLSorted)) {
											sbRes.append(
													"<img border=\"0\" src=\"../common/images/More.png\" title=\"History\" /> </a>");
										}
										strReturnValue = sbRes.toString();
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		return strReturnValue;
	}

	public MapList getPCNResult(Context context, String[] args) throws FrameworkException {
		MapList mlAlertsDetails = new MapList();
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String strMEPOID = (String) programMap.get("objectId");
			String strIHSResponseXML = PropertyUtil.getSchemaProperty("attribute_IHSResponseXML");
			String sIHSResponseXML = "";
			String strIHSResponse = "";
			String strPartDetailsType = "";
			String sAlertType = "";
			String sAlertNumber = "";
			String sAlertDesc = "";
			String sAlertIssuedate = "";
			String sAlertImplementationdate = "";
			String sAlertInfoSource = "";
			String sAlertObjectId = "";
			String sDocType = "";
			String sDocURL = "";
			Map<String, String> AlertsMap = null;
			generated.Partdetails partDetails = new generated.Partdetails();
			generated.Alerts Alerts = new generated.Alerts();
			generated.Doc doc = new generated.Doc();
			Map<String, String> ImplementaionMap = new HashMap<String, String>();
			ArrayList<Alerts> AlertsList;
			ArrayList<Object> partList;
			ArrayList<Doc> IHSdocList;

			if (!UIUtil.isNullOrEmpty(strMEPOID)) {
				DomainObject Domobj = new DomainObject(strMEPOID);
				sIHSResponseXML = Domobj.getAttributeValue(context, strIHSResponseXML);
				if (!UIUtil.isNullOrEmpty(sIHSResponseXML)) {
					Reader inputStreamReader = new StringReader(sIHSResponseXML);
					strIHSResponse = IOUtils.toString(inputStreamReader);
					inputStreamReader = new StringReader(strIHSResponse);
					JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
					if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
						ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
						generated.Result result = (generated.Result) xmlResultList.get(0);
						if ((Integer.parseInt(result.getCount())) > 0) {
							partList = (ArrayList<Object>) result.getPartOrMfrOrError();
							if (partList.size() == 1) {
								generated.Part part = (generated.Part) partList.get(0);
								ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
										.getPartdetails();
								for (int i = 0; i < partDetailsList.size(); i++) {
									partDetails = partDetailsList.get(i);
									strPartDetailsType = partDetails.getType();
									if (strPartDetailsType.equalsIgnoreCase("ALERTS")) {
										AlertsList = (ArrayList<Alerts>) partDetails.getAlerts();
										for (int j = 0; j < AlertsList.size(); j++) {
											Alerts = AlertsList.get(j);
											sAlertType = Alerts.getAlertType();
											if (sAlertType.equalsIgnoreCase("PCN")) {
												AlertsMap = new HashMap<String, String>();
												sAlertNumber = Alerts.getChangeNotiNum();
												sAlertDesc = Alerts.getChangeNotiDesc();
												sAlertIssuedate = Alerts.getChgNotiIssueDate();
												sAlertImplementationdate = Alerts.getChgNotiImplementationDate();
												sAlertInfoSource = Alerts.getChgAlertInfoSource();
												sAlertObjectId = Alerts.getObjectId();
												AlertsMap.put("AlertType", sAlertType);
												AlertsMap.put("AlertNumber", sAlertNumber);
												AlertsMap.put("AlertDesc", sAlertDesc);
												AlertsMap.put("AlertImplementationdate", sAlertImplementationdate);
												AlertsMap.put("AlertIssuedate", sAlertIssuedate);
												AlertsMap.put("AlertInfoSource", sAlertInfoSource);
												AlertsMap.put("AlertObjectId", sAlertObjectId);
												AlertsMap.put("AlertComments", "");
												AlertsMap.put("AlertMfrAlertDate", "");
												IHSdocList = (ArrayList<Doc>) Alerts.getDoc();
												for (int k = 0; k < IHSdocList.size(); k++) {
													doc = IHSdocList.get(k);
													sDocType = doc.getDocType();
													sDocURL = doc.getDocUrl();
													AlertsMap.put("DocType", sDocType);
													AlertsMap.put("DocURL", sDocURL);
												}
												mlAlertsDetails.add(AlertsMap);
											}
										}
									}
								}
								mlAlertsDetails.sort("AlertIssuedate", "decending", "date");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		return mlAlertsDetails;
	}

	// Added by Lalitha for Issue #68 --Ends
	// Added for Issue-192 by Preethi Rajaraman -- Starts
	public void MetricConversion(Context context, String strClassName, DomainObject doMEP, Properties prop)
			throws Exception {
		try {
			String sXORGIHSValues = PropertyUtil.getSchemaProperty("attribute_XORGIHSValues");
			String strPropertyKey = "XORG_Attribute_Conversion";
			String strPropertyKeyValue = prop.getProperty(strPropertyKey);
			StringList slPropertyList = FrameworkUtil.split(strPropertyKeyValue, "|");
			StringList slSelects = new StringList();
			HashMap hmAttributesMap = new HashMap();
			for (int j = 0; j < slPropertyList.size(); j++) {
				String sValue = (String) slPropertyList.elementAt(j);
				slSelects.add("attribute[" + sValue + "].value");
			}
			Map mAttributeDetails = doMEP.getInfo(context, slSelects);
			String strXORGIHSValue = "";
			for (int jj = 0; jj < slPropertyList.size(); jj++) {
				String sAttribute = (String) slPropertyList.elementAt(jj);
				String sVal = (String) mAttributeDetails.get("attribute[" + sAttribute + "].value");
				if (sVal != null && !sVal.equals("")) {
					// Modified by Ravindra - Starts
					String strKey = sAttribute.replace(" ", "_") + "_DefaultUOMWithMetrics";
					String strValue = prop.getProperty(strKey);
					if (strValue != null && strValue != "") {
						String sIHSCapacitance_round = UnitMeasureConversion(context, sVal, sAttribute, prop);
						String IHSCapacitance = sVal + " " + strValue;
						hmAttributesMap.put(sAttribute, sIHSCapacitance_round);
						if (strXORGIHSValue != "") {
							strXORGIHSValue = strXORGIHSValue + "\n"
									+ EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",
											context.getLocale(),
											"emxFramework.Attribute." + sAttribute.replace(" ", "_"))
									+ "," + IHSCapacitance;
						} else {
							strXORGIHSValue = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",
									context.getLocale(), "emxFramework.Attribute." + sAttribute.replace(" ", "_")) + ","
									+ IHSCapacitance;

						}
					}
					// Modified by Ravindra - Ends
				}
			}
			if (strXORGIHSValue != "") {
				hmAttributesMap.put(sXORGIHSValues, strXORGIHSValue);
			}
			doMEP.setAttributeValues(context, hmAttributesMap);
		} catch (Exception e) {
			throw e;
		}
	}

	public String getMEPDesc(Context context, String strClassName, DomainObject doMEP, Properties prop)
			throws Exception {
		String sReturnDesc = "";
		String sRetDesc = "";// Modified by Preethi Rajaraman for desc Changes -- Starts
		try {
			StringList slSelects = new StringList();
			String strPropertyKey = "";
			String strPropertyKeyValue = "";
			// Added for Issue-207 by Preethi Rajaraman -- Starts
			if (!UIUtil.isNullOrEmpty(strClassName)) {
				strPropertyKey = "IHSDescription_" + strClassName.replace(" ", "_") + "_Desc";
				strPropertyKeyValue = prop.getProperty(strPropertyKey);
			}
			HashMap hmAttributesMap = new HashMap();
			// Added for Issue-207 by Preethi Rajaraman -- Ends
			if (!UIUtil.isNullOrEmpty(strPropertyKeyValue)) {
				String sResult = "";
				StringList AttrList = FrameworkUtil.split(strPropertyKeyValue, ",");
				for (int j = 0; j < AttrList.size(); j++) {
					String sValue = (String) AttrList.elementAt(j);
					if (j == 0) {
						sResult = MqlUtil.mqlCommand(context, "list attribute $1", sValue);
						if (sResult != null && !sResult.equals("")) {
							slSelects.add("attribute[" + sValue.trim() + "].value");
						} else {
							sReturnDesc = sValue;
						}

					} else {

						if (sValue.contains("|")) {
							StringList AttrStrList = FrameworkUtil.split(sValue, "|");
							String atr1 = (String) AttrStrList.elementAt(0);
							atr1 = atr1.trim();
							slSelects.add("attribute[" + atr1 + "].value");
						} else {
							slSelects.add("attribute[" + sValue.trim() + "].value");
						}

					}

				}
				int k = 0;
				if (!sReturnDesc.equals("")) {
					k = 1;
				}
				Map mAttributeDetails = doMEP.getInfo(context, slSelects);
				boolean isMinHasValue = false;
				boolean isMaxHasValue = false;
				for (int jj = k; jj < AttrList.size(); jj++) {
					String sValue2 = (String) AttrList.elementAt(jj);
					sValue2 = sValue2.trim();
					String sUOM = "";
					if (sValue2.contains("|")) {
						StringList AttrStrList2 = FrameworkUtil.split(sValue2, "|");
						sValue2 = (String) AttrStrList2.elementAt(0);
						sValue2 = sValue2.trim();
						sUOM = (String) AttrStrList2.elementAt(1);
						sUOM = sUOM.trim();
					}
					if (!sUOM.equals("")) {
						// Added for Issue-207 by Preethi Rajaraman -- Starts
						String IHSAttrValue = (String) mAttributeDetails.get("attribute[" + sValue2 + "].value");
						if (sValue2.equals("IHSCategory") || sValue2.equals("IHSInductor Application")) {
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)inductors", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)inductor", "");

							IHSAttrValue = IHSAttrValue.replaceAll("(?i) inductors", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i) inductor", "");

							IHSAttrValue = IHSAttrValue.replaceAll("(?i)inductors ", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)inductor ", "");
						}
						if (sValue2.equals("IHSCategory") || sValue2.equals("IHSSub Category")) {
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)connectors", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)connector", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)others", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)other", "");

							IHSAttrValue = IHSAttrValue.replaceAll("(?i) connectors", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i) connector", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i) others", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i) other", "");

							IHSAttrValue = IHSAttrValue.replaceAll("(?i)connectors ", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)connector ", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)others ", "");
							IHSAttrValue = IHSAttrValue.replaceAll("(?i)other ", "");
						}
						if (sValue2.equals("IHSOperating Temperature-Max")) {
							if (IHSAttrValue != null && !IHSAttrValue.isEmpty()) {
								isMaxHasValue = true;
							}
						}
						if (sValue2.equals("IHSOperating Temperature-Min")) {
							if (IHSAttrValue != null && !IHSAttrValue.isEmpty()) {
								isMinHasValue = true;
							}
						}
						if (!UIUtil.isNullOrEmpty(IHSAttrValue)) {
							if (UIUtil.isNullOrEmpty(sReturnDesc)) {
								if (sValue2.equals("IHSPositive Tolerance")) {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = "+" + IHSAttrValue;
									} else {
										sReturnDesc = "+" + IHSAttrValue + sUOM;
									}

								} else if (sValue2.equals("IHSNegative Tolerance")) {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = "-" + IHSAttrValue;
									} else {
										sReturnDesc = "-" + IHSAttrValue + sUOM;
									}
								} else if (sValue2.equals("IHSShielded")) {
									if (IHSAttrValue.equalsIgnoreCase("NO")) {
										sReturnDesc = "";
									} else if (IHSAttrValue.equalsIgnoreCase("YES")) {
										sReturnDesc = "SHLD";
									} else {
										// if(IHSAttrValue.endsWith(sUOM))
										if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
											sReturnDesc = IHSAttrValue;
										} else {
											sReturnDesc = IHSAttrValue + sUOM;
										}

									}
								} else {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = IHSAttrValue;
									} else {
										sReturnDesc = IHSAttrValue + sUOM;
									}

								}
							} else {
								if (sValue2.equals("IHSPositive Tolerance")) {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = sReturnDesc + "," + "+" + IHSAttrValue;
									} else {
										sReturnDesc = sReturnDesc + "," + "+" + IHSAttrValue + sUOM;
									}

								} else if (sValue2.equals("IHSNegative Tolerance")) {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = sReturnDesc + "," + "-" + IHSAttrValue;
									} else {
										sReturnDesc = sReturnDesc + "," + "-" + IHSAttrValue + sUOM;
									}

								} else if (sValue2.equals("IHSShielded")) {
									if (IHSAttrValue.equalsIgnoreCase("NO")) {
										// sReturnDesc = sReturnDesc +","+""+sUOM;
										sReturnDesc = sReturnDesc;
									} else if (IHSAttrValue.equalsIgnoreCase("YES")) {
										// if(IHSAttrValue.endsWith(sUOM))
										if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
											sReturnDesc = sReturnDesc + "," + "SHLD";
										} else {
											sReturnDesc = sReturnDesc + "," + "SHLD" + sUOM;
										}
									} else {
										// if(IHSAttrValue.endsWith(sUOM))
										if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
											sReturnDesc = sReturnDesc + "," + IHSAttrValue;
										} else {
											sReturnDesc = sReturnDesc + "," + IHSAttrValue + sUOM;
										}
									}
								} else {
									// if(IHSAttrValue.endsWith(sUOM))
									if (IHSAttrValue.toLowerCase().endsWith(sUOM.toLowerCase())) {
										sReturnDesc = sReturnDesc + "," + IHSAttrValue;
									} else {
										sReturnDesc = sReturnDesc + "," + IHSAttrValue + sUOM;
									}
								}
							}
						} else {
							if (UIUtil.isNullOrEmpty(sReturnDesc)) {
								if (!UIUtil.isNullOrEmpty(IHSAttrValue)) {
									if (sValue2.equals("IHSPositive Tolerance")) {
										sReturnDesc = "+" + IHSAttrValue;
									} else if (sValue2.equals("IHSNegative Tolerance")) {
										sReturnDesc = "-" + IHSAttrValue;
									} else if (sValue2.equals("IHSShielded")) {
										if (IHSAttrValue.equalsIgnoreCase("NO")) {
											sReturnDesc = "";
										} else if (IHSAttrValue.equalsIgnoreCase("YES")) {
											sReturnDesc = "SHLD";
										} else {
											sReturnDesc = IHSAttrValue;
										}
									} else {
										sReturnDesc = IHSAttrValue;
									}
								}
							} else {
								if (!UIUtil.isNullOrEmpty(IHSAttrValue)) {
									if (sValue2.equals("IHSPositive Tolerance")) {
										sReturnDesc = sReturnDesc + "," + "+" + IHSAttrValue;
									} else if (sValue2.equals("IHSNegative Tolerance")) {
										sReturnDesc = sReturnDesc + "," + "-" + IHSAttrValue;
									} else if (sValue2.equals("IHSShielded")) {
										if (IHSAttrValue.equalsIgnoreCase("NO")) {
											// sReturnDesc = sReturnDesc +","+"";
											sReturnDesc = sReturnDesc;
										} else if (IHSAttrValue.equalsIgnoreCase("YES")) {
											sReturnDesc = sReturnDesc + "," + "SHLD";
										} else {
											sReturnDesc = sReturnDesc + "," + IHSAttrValue;
										}
									} else {
										sReturnDesc = sReturnDesc + "," + IHSAttrValue;
									}
								}
							}
						}
						// Added for Issue-207 by Preethi Rajaraman -- Ends
					} else {
						String strAttrVal = (String) mAttributeDetails.get("attribute[" + sValue2 + "].value");
						if (sValue2.equals("IHSCategory") || sValue2.equals("IHSInductor Application")) {
							strAttrVal = strAttrVal.replaceAll("(?i)inductors", "");
							strAttrVal = strAttrVal.replaceAll("(?i)inductor", "");

							strAttrVal = strAttrVal.replaceAll("(?i) inductors", "");
							strAttrVal = strAttrVal.replaceAll("(?i) inductor", "");

							strAttrVal = strAttrVal.replaceAll("(?i)inductors ", "");
							strAttrVal = strAttrVal.replaceAll("(?i)inductor ", "");
						}
						if (sValue2.equals("IHSCategory") || sValue2.equals("IHSSub Category")) {
							strAttrVal = strAttrVal.replaceAll("(?i)connectors", "");
							strAttrVal = strAttrVal.replaceAll("(?i)connector", "");
							strAttrVal = strAttrVal.replaceAll("(?i)others", "");
							strAttrVal = strAttrVal.replaceAll("(?i)other", "");

							strAttrVal = strAttrVal.replaceAll("(?i) connectors", "");
							strAttrVal = strAttrVal.replaceAll("(?i) connector", "");
							strAttrVal = strAttrVal.replaceAll("(?i) others", "");
							strAttrVal = strAttrVal.replaceAll("(?i) other", "");

							strAttrVal = strAttrVal.replaceAll("(?i)connectors ", "");
							strAttrVal = strAttrVal.replaceAll("(?i)connector ", "");
							strAttrVal = strAttrVal.replaceAll("(?i)others ", "");
							strAttrVal = strAttrVal.replaceAll("(?i)other ", "");
						}
						if (sValue2.equals("IHSOperating Temperature-Max")) {
							if (strAttrVal != null && !strAttrVal.isEmpty()) {
								isMaxHasValue = true;
							}
						}
						if (sValue2.equals("IHSOperating Temperature-Min")) {
							if (strAttrVal != null && !strAttrVal.isEmpty()) {
								isMinHasValue = true;
							}
						}
						String strAttrValUpperCase = strAttrVal.toUpperCase();
						// sReturnDesc = sReturnDesc +","+(String)
						// mAttributeDetails.get("attribute["+sValue+"].value");
						if (UIUtil.isNullOrEmpty(sReturnDesc)) {
							if (!UIUtil.isNullOrEmpty(strAttrValUpperCase)) {
								if (sValue2.equals("IHSPositive Tolerance")) {
									sReturnDesc = "+" + strAttrValUpperCase;
								} else if (sValue2.equals("IHSNegative Tolerance")) {
									sReturnDesc = "-" + strAttrValUpperCase;
								} else if (sValue2.equals("IHSShielded")) {
									if (strAttrValUpperCase.equalsIgnoreCase("NO")) {
										sReturnDesc = "";
									} else if (strAttrValUpperCase.equalsIgnoreCase("YES")) {
										sReturnDesc = "SHLD";
									} else {
										sReturnDesc = strAttrValUpperCase;
									}
								} else {
									sReturnDesc = strAttrValUpperCase;
								}
							}
						} else {
							if (!UIUtil.isNullOrEmpty(strAttrValUpperCase)) {
								if (sValue2.equals("IHSPositive Tolerance")) {
									sReturnDesc = sReturnDesc + "," + "+" + strAttrValUpperCase;
								} else if (sValue2.equals("IHSNegative Tolerance")) {
									sReturnDesc = sReturnDesc + "," + "-" + strAttrValUpperCase;
								} else if (sValue2.equals("IHSShielded")) {
									if (strAttrValUpperCase.equalsIgnoreCase("NO")) {
										// sReturnDesc = sReturnDesc +","+"";
										sReturnDesc = sReturnDesc;
									} else if (strAttrValUpperCase.equalsIgnoreCase("YES")) {
										sReturnDesc = sReturnDesc + "," + "SHLD";
									} else {
										sReturnDesc = sReturnDesc + "," + strAttrValUpperCase;
									}
								} else {
									sReturnDesc = sReturnDesc + "," + strAttrValUpperCase;
								}
							}
						}
					}
				}
				int size = AttrList.size();
				String sTemp_max = (String) AttrList.elementAt(size - 1);
				sTemp_max = sTemp_max.trim();
				String sTemp_min = (String) AttrList.elementAt(size - 2);
				sTemp_min = sTemp_min.trim();
				if (sTemp_min.equals("IHSOperating Temperature-Min|C")
						&& sTemp_max.equals("IHSOperating Temperature-Max|C")) {
					if (isMinHasValue && isMaxHasValue) {
						StringList slReturnList = FrameworkUtil.split(sReturnDesc, ",");
						for (int kkk = 0; kkk < slReturnList.size() - 2; kkk++) {
							String sval = (String) slReturnList.elementAt(kkk);
							if (sRetDesc != null && !sRetDesc.equals("")) {
								sRetDesc = sRetDesc + "," + sval;
							} else {
								sRetDesc = sval;
							}
						}
						sRetDesc = sRetDesc + "," + (String) slReturnList.elementAt(slReturnList.size() - 2) + " to "
								+ (String) slReturnList.elementAt(slReturnList.size() - 1);
						sReturnDesc = sRetDesc;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		// sReturnDesc=sReturnDesc.toUpperCase();
		sReturnDesc = sReturnDesc.replaceAll(",", ", ");
		sReturnDesc = sReturnDesc.replaceAll(" ,", ",");
		return sReturnDesc;
	}
	// Added for Issue-192 by Preethi Rajaraman -- Ends

	// Added for Issue-207 by Preethi Rajaraman -- Starts
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	public String UnitMeasureConversion(Context context, String sValue, String spage, Properties prop) {
		String sIHSResistance_round = "";
		String sRound = "";
		boolean bConvert = false;
		boolean bInt = isInteger(sValue);
		double iIHSResistance = Double.parseDouble(sValue);
		double dIHSResistance = 0.0;
		String strPropertyKey = "";
		String strPropertyKeyValue = "";

		String strPropertyKeyValue2 = "";

		strPropertyKeyValue2 = prop.getProperty(spage.replace(" ", "_") + "_DefaultMetric");
		if (bInt) {
			int iIHS = sValue.length();
			if (iIHS > 3 && iIHS <= 6) {
				dIHSResistance = iIHSResistance / 1000;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_6_value";
			} else if (iIHS > 6 && iIHS <= 9) {
				dIHSResistance = iIHSResistance / 1000000;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_9_value";
			} else if (iIHS > 9 && iIHS <= 12) {
				dIHSResistance = iIHSResistance / 1000000000;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_12_value";
			} else {
				dIHSResistance = iIHSResistance;
				bConvert = true;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_3_value";
			}
		} else {
			String[] splitter = sValue.split("\\.");
			int FirstHalfValue = Integer.parseInt(splitter[0]);
			int Firsthalf = splitter[0].length();
			int Secondhalf = splitter[1].length();
			if (Firsthalf > 3 && Firsthalf <= 6) {
				dIHSResistance = iIHSResistance / 1000;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_6_value";
			} else if (Firsthalf > 6 && Firsthalf <= 9) {
				dIHSResistance = iIHSResistance / 1000000;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_9_value";
			} else if (Secondhalf <= 4 && FirstHalfValue < 1) {
				dIHSResistance = iIHSResistance * 1000;
				bConvert = true;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_-3_value";
			} else if (Secondhalf > 4 && Secondhalf <= 7 && FirstHalfValue < 1) {
				dIHSResistance = iIHSResistance * 1000 * 1000;
				bConvert = true;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_-6_value";
			} else if (Secondhalf > 7 && Secondhalf <= 10 && FirstHalfValue < 1) {
				dIHSResistance = iIHSResistance * 1000 * 1000 * 1000;
				bConvert = true;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_-9_value";
			} else if (Firsthalf <= 3 && FirstHalfValue >= 1) {
				dIHSResistance = iIHSResistance;
				bConvert = true;
				strPropertyKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_3_value";
			}
		}
		String IHSResFinal = String.format("%.2f", dIHSResistance);
		String[] splitResult = IHSResFinal.split("\\.");
		int First = splitResult[0].length();
		int Second = splitResult[1].length();
		int secondPart = Integer.parseInt(splitResult[1]);
		if (UIUtil.isNullOrEmpty(strPropertyKey)) {
			strPropertyKeyValue = "";
		} else {
			strPropertyKeyValue = prop.getProperty(strPropertyKey);
		}
		if (First >= 3) {
			sIHSResistance_round = splitResult[0] + strPropertyKeyValue;
		} else if (First == 2 && secondPart == 00) {
			sIHSResistance_round = splitResult[0] + strPropertyKeyValue;
		} else if (First == 1 && secondPart == 00) {
			sIHSResistance_round = splitResult[0] + strPropertyKeyValue;
		} else if (First == 2) {
			sRound = String.format("%.1f", dIHSResistance);
			String[] splitRound = sRound.split("\\.");
			int secondPart_round = Integer.parseInt(splitRound[1]);
			if (secondPart_round == 0) {
				sIHSResistance_round = splitRound[0] + strPropertyKeyValue;
			} else {
				sIHSResistance_round = String.format("%.1f", dIHSResistance) + strPropertyKeyValue;
			}

		} else if (First == 1) {
			int First_Val = Integer.parseInt(splitResult[0]);
			if (First_Val < 1 && bConvert) {
				StringList slproperty = FrameworkUtil.split(strPropertyKey, "_");
				String sNumber = (String) slproperty.get(3);
				int iNumber = Integer.parseInt(sNumber);
				int iTotal = iNumber + (-3);
				String sTotal = String.valueOf(iTotal);
				String sPropKey = "IHS_DefaultMetric_" + strPropertyKeyValue2 + "_" + sTotal + "_value";
				strPropertyKeyValue = prop.getProperty(sPropKey);
				dIHSResistance = dIHSResistance * 1000;
				String IHSCap = String.format("%.2f", dIHSResistance);
				String[] splitCap = IHSCap.split("\\.");
				int First_details = splitCap[0].length();
				int Second_details = splitCap[1].length();
				int secondPart_details = Integer.parseInt(splitCap[1]);
				if (First_details >= 3) {
					sIHSResistance_round = splitCap[0] + strPropertyKeyValue;
				} else if (First_details == 2 && secondPart_details == 00) {
					sIHSResistance_round = splitCap[0] + strPropertyKeyValue;
				} else if (First_details == 1 && secondPart_details == 00) {
					sIHSResistance_round = splitCap[0] + strPropertyKeyValue;
				} else if (First_details == 2) {
					sRound = String.format("%.1f", dIHSResistance);
					String[] splitRound = sRound.split("\\.");
					int secondPart_round = Integer.parseInt(splitRound[1]);
					if (secondPart_round == 0) {
						sIHSResistance_round = splitRound[0] + strPropertyKeyValue;
					} else {
						sIHSResistance_round = String.format("%.1f", dIHSResistance) + strPropertyKeyValue;
					}
				}
			} else {
				sIHSResistance_round = String.format("%.2f", dIHSResistance) + strPropertyKeyValue;
			}
		}
		return sIHSResistance_round;
	}

	public String getDynamicDescription(Context context, String[] args) throws Exception {

		String strDescription = "";
		Properties prop = new Properties();
		StringList objectSelects = new StringList();
		objectSelects.add(SELECT_ID);
		objectSelects.add(SELECT_NAME);
		MapList classificationList = new MapList();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap requestMap = (HashMap) programMap.get("requestMap");
		String partId = (String) requestMap.get("objectId");
		DomainObject dmPartId = new DomainObject(partId);
		String argsMQL[] = new String[1];
		argsMQL[0] = "XORGIHSIntegrationMapping";
		String MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", argsMQL);
		byte[] bytes = MQLResult.getBytes("UTF-8");
		InputStream input = new ByteArrayInputStream(bytes);
		prop.load(input);
		classificationList = dmPartId.getRelatedObjects(context, "Classified Item", "*", objectSelects, null, true,
				false, (short) 0, null, null, 0);
		int noOfClasses = classificationList.size();
		if (noOfClasses > 0) {
			Map tempMap = (Map) classificationList.get(0);
			String strClassName = (String) tempMap.get(SELECT_NAME);
			if (strClassName.equals("ECAD Reference Attributes")) {
				strDescription = "Not Synced With IHS";
			} else {
				strDescription = getMEPDesc(context, strClassName, dmPartId, prop);
			}
		} else {
			strDescription = "Not Synced With IHS";
		}
		return strDescription;
	}

	public void descriptionUpdateForMEP(Context context, DomainObject doMEP, String strSupplierName,
			String strClassName, Properties prop) throws Exception {

		String strReturn = "";
		generated.Details details;
		String strAttrName = "";
		String strAttrID = "";
		String strAttrValue = "";
		Map<String, String> attrMap = new HashMap<String, String>();
		String strIHSAttrName = "";
		String strIHSAttrNameModified = "";
		String strmodifiedClassName = "";
		String strPropertyKey = "";
		String strLocale = context.getSession().getLanguage();
		String strPropertyKeyValue = "";
		String strIHSAttrValue = "";
		String strIHSObjectId = "";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";
		String strInterfaceName = PropertyUtil.getSchemaProperty(context, "interface_googPropsedDescription");
		String sgoogPropDesc = PropertyUtil.getSchemaProperty(context, "attribute_googPropsedDescription");

		String strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);
		// String strSupplierName = doMEP.getInfo(context,
		// DomainConstants.SELECT_REVISION);

		String strBadCharacters = "";
		boolean blnSpecialChar = false;

		StringList slAttrsWithSpecialChars = new StringList();

		try {
			String strDescriptionForMEP = getMEPDesc(context, strClassName, doMEP, prop);
			
			if (strDescriptionForMEP != null && !"".equals(strDescriptionForMEP)) {
				strDescriptionForMEP= strDescriptionForMEP.toUpperCase();
				doMEP.setDescription(context, strDescriptionForMEP);
				StringList selectStmts11 = new StringList(6);
				selectStmts11.addElement(DomainConstants.SELECT_ID);
				selectStmts11.addElement(DomainConstants.SELECT_TYPE);
				selectStmts11.addElement(DomainConstants.SELECT_NAME);
				selectStmts11.addElement(DomainConstants.SELECT_REVISION);
				StringList selectRelStmts11 = new StringList(2);
				selectRelStmts11.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
				StringBuffer sbRelPattern = new StringBuffer(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
				StringBuffer typePattern = new StringBuffer(DomainConstants.TYPE_PART);
				StringBuffer sbTypePattern = new StringBuffer(typePattern.toString());
				MapList listManEquivECParts = doMEP.getRelatedObjects(context, sbRelPattern

						.toString(), // relationship pattern

						sbTypePattern.toString(), // object pattern

						selectStmts11, // object selects

						selectRelStmts11, // relationship selects

						true, // to direction

						false, // from direction

						(short) 1, // recursion level

						null, // object where clause

						null); // relationship where clause
				String sRelECPartId = "";
				for (int ii = 0; ii < listManEquivECParts.size(); ii++) {

					Map tempMap11 = (Map) listManEquivECParts.get(ii);

					sRelECPartId = (String) tempMap11.get(DomainConstants.SELECT_ID);
					DomainObject domECPartObj = new DomainObject(sRelECPartId);
					MapList listManEquivMEPs = domECPartObj.getRelatedObjects(context, sbRelPattern

							.toString(), // relationship pattern

							sbTypePattern.toString(), // object pattern

							selectStmts11, // object selects

							selectRelStmts11, // relationship selects

							false, // to direction

							true, // from direction

							(short) 1, // recursion level

							null, // object where clause

							null); // relationship where clause

					if (listManEquivMEPs.size() == 1) {

						String strMqlCommand = "trigger off";
						MqlUtil.mqlCommand(context, strMqlCommand, true);
						String sInterfaceQuery = "print bus $1 select interface dump $2";
						String sInterface = MqlUtil.mqlCommand(context, sInterfaceQuery, sRelECPartId, "|");
						StringList slReturnList = FrameworkUtil.split(sInterface, "|");
						if (!slReturnList.contains(strInterfaceName)) {
							String strAddInterface = "modify bus $1 add interface $2";
							MqlUtil.mqlCommand(context, strAddInterface, sRelECPartId, strInterfaceName);
						}
						domECPartObj.setAttributeValue(context, sgoogPropDesc, strDescriptionForMEP);
						strMqlCommand = "trigger on";
						MqlUtil.mqlCommand(context, strMqlCommand, true);

					}

				}

			}

		} catch (Exception E) {
			E.printStackTrace();

		} finally {
			// modified for #199

		}
	}

	public void updateECADReferenceAttributesAndDisconnectClass(Context context, DomainObject doMEP,
			String strSupplierName, String strClassName, Properties prop) throws Exception {

		String strReturn = "";
		generated.Details details;
		String strAttrName = "";
		String strAttrID = "";
		String strAttrValue = "";
		Map<String, String> attrMap = new HashMap<String, String>();
		String strIHSAttrName = "";
		String strIHSAttrNameModified = "";
		String strmodifiedClassName = "";
		String strPropertyKey = "";
		String strLocale = context.getSession().getLanguage();
		String strPropertyKeyValue = "";
		String strIHSAttrValue = "";
		String strIHSObjectId = "";
		String strPropKey_object_id = "IHS.Mapping.IHSObjectID";
		String strInterfaceName = PropertyUtil.getSchemaProperty(context, "interface_googPropsedDescription");
		String sgoogPropDesc = PropertyUtil.getSchemaProperty(context, "attribute_googPropsedDescription");

		String strPartName = doMEP.getInfo(context, DomainConstants.SELECT_NAME);
		// String strSupplierName = doMEP.getInfo(context,
		// DomainConstants.SELECT_REVISION);

		String strBadCharacters = "";
		boolean blnSpecialChar = false;

		StringList slAttrsWithSpecialChars = new StringList();

		try {
			StringList slSelect2 = new StringList();

			slSelect2.addElement(DomainConstants.SELECT_ID);

			slSelect2.addElement(DomainConstants.SELECT_NAME);

			slSelect2.addElement("attribute[" + LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE + "]");

			// querying for the classification object

			MapList mlECADRefClassification = DomainObject.findObjects(context, // context

					LibraryCentralConstants.TYPE_GENERAL_CLASS, // typePattern

					"ECAD Reference Attributes", // namePattern

					DomainConstants.QUERY_WILDCARD, // revPattern

					DomainConstants.QUERY_WILDCARD, // ownerPattern

					DomainConstants.QUERY_WILDCARD, // vaultPatern

					DomainConstants.EMPTY_STRING, // whereExpression

					false, // expandType

					slSelect2);// objectSelects
			Map<String, String> tempECADRefClassificationMap = (Map<String, String>) mlECADRefClassification.get(0);

			String strECADRefClassID = tempECADRefClassificationMap.get(DomainConstants.SELECT_ID);

			String strECADRefClassName = tempECADRefClassificationMap.get(DomainConstants.SELECT_NAME);

			// String ECADRefInterfaceName =
			// tempECADRefClassificationMap.get("attribute["+LibraryCentralConstants.ATTRIBUTE_MXSYSINTERFACE+"]");
			String ECADRefInterfaceName = "XORGECAD Reference Attributes";

			StringList slECADRefInterfaceAttrList = new StringList();
			if (ECADRefInterfaceName != null && !"".equals(ECADRefInterfaceName)) {
				String strMQL1 = "print interface $1 select attribute dump";
				String resStr1 = MqlUtil.mqlCommand(context, strMQL1, true, ECADRefInterfaceName);
				StringList slMQLResult1 = FrameworkUtil.split(resStr1, ",");
				if (slMQLResult1.size() > 0) {
					slECADRefInterfaceAttrList.addAll(slMQLResult1);

				}
			}
			for (int n = 0; n < slECADRefInterfaceAttrList.size(); n++) {
				String strECADRefAttrName = (String) slECADRefInterfaceAttrList.get(n);

				String strExistingECADRefAttrValue = doMEP.getAttributeValue(context, strECADRefAttrName);

				String strECADRefAttrNameModified = strECADRefAttrName.replaceAll(" ", "_");

				strPropertyKey = "ECADMapping_" + strECADRefAttrNameModified + "_" + strClassName.replace(" ", "_")
						+ "_DefaultValue";
				strPropertyKeyValue = prop.getProperty(strPropertyKey);

				if (UIUtil.isNullOrEmpty(strExistingECADRefAttrValue)) {
					if (strPropertyKeyValue != null && !"".equals(strPropertyKeyValue)) {
						attrMap.put(strECADRefAttrName, strPropertyKeyValue);

					}
				}

			}
			doMEP.setAttributeValues(context, attrMap);
			StringList selectStmts2 = new StringList(3);

			selectStmts2.addElement(DomainConstants.SELECT_NAME);
			selectStmts2.addElement(DomainConstants.SELECT_ID);
			selectStmts2.addElement(DomainConstants.SELECT_TYPE);

			MapList mlGenClassRelObjects = doMEP.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + ","
							+ LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, // relationship pattern
					DomainConstants.TYPE_COMPANY + "," + LibraryCentralConstants.TYPE_GENERAL_CLASS, // Type pattern
					selectStmts2, // object selects
					null, // relationship selects
					true, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					DomainConstants.EMPTY_STRING, 0);
			Iterator<Map<String, String>> mlGenClassRelObjectsItr = (Iterator) mlGenClassRelObjects.iterator();
			Map<String, String> mlGenClassRelobjMap = null;
			StringList slClassification2 = new StringList();
			String strType2 = "";
			String strClassId2 = "";
			String strClassName2 = "";

			while (mlGenClassRelObjectsItr.hasNext()) {
				mlGenClassRelobjMap = (Map<String, String>) mlGenClassRelObjectsItr.next();
				strType2 = (String) mlGenClassRelobjMap.get(DomainConstants.SELECT_TYPE);
				if (strType2.equalsIgnoreCase(DomainConstants.TYPE_COMPANY)) {
				} else if (strType2.equalsIgnoreCase(LibraryCentralConstants.TYPE_GENERAL_CLASS)) {
					strClassName2 = (String) mlGenClassRelobjMap.get(DomainConstants.SELECT_NAME);
					if (UIUtil.isNullOrEmpty(strClassId2) && strClassName2.equals("ECAD Reference Attributes")) {
						strClassId2 = (String) mlGenClassRelobjMap.get(DomainConstants.SELECT_ID);
					}
					slClassification2.add((String) mlGenClassRelobjMap.get(DomainConstants.SELECT_NAME));
				}
			}

			if (slClassification2.contains("ECAD Reference Attributes") && strClassId2 != null
					&& !"".equals(strClassId2)) {

				MqlUtil.mqlCommand(context, "disconnect bus $1 relationship $2 from $3", (String) doMEP.getId(context),
						LibraryCentralConstants.RELATIONSHIP_CLASSIFIED_ITEM, strClassId2);

			}

		} catch (Exception E) {
			E.printStackTrace();

		} finally {
			// modified for #199

		}
	}

	// Added for Issue-207 by Preethi Rajaraman -- Ends
	// Added for Issue 222 Starts

	public MapList retrieveMFRData(Context context, String Manufacturer) throws Exception {
		MapList IHSReturn = new MapList();
		Proxy proxy = null;
		URLConnection urlConn;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			String strPageFileName = "XORGIHSIntegrationMapping";
			String MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			input = new ByteArrayInputStream(bytes);
			prop.load(input);
			String strIHSUser = prop.getProperty("IHS.credentials.username");
			String strIHSPswrd = prop.getProperty("IHS.credentials.password");
			String serverURL = prop.getProperty("IHS.credentials.serverURL");
			String serverFile = prop.getProperty("IHS.credentials.serverFile");
			String strConnectionTimeOut = prop.getProperty("IHS.RealTime.connection.timeout");
			String strReadTimeout = prop.getProperty("IHS.RealTime.read.timeout");
			;
			URL urlOrder = new URL("HTTPS", serverURL, 443, serverFile);
			urlConn = (proxy == null) ? urlOrder.openConnection() : urlOrder.openConnection(proxy);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestProperty("Content-Type", "text/xml");
			urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConn.connect();
			urlConn.setConnectTimeout(Integer.parseInt(strConnectionTimeOut));
			urlConn.setReadTimeout(Integer.parseInt(strReadTimeout));
			String requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<XMLQuery version=\"0.1\">"
					+ "<Login password=\"" + strIHSPswrd + "\" user-name=\"" + strIHSUser + "\"/>"
					+ "<Criteria limit=\"200\" search-type=\"MFR\">" + "<Criterion id=\"TEST\">"
					+ "<Parameter name=\"mfg\" match-type=\"EXACT\">" + Manufacturer + "</Parameter>" + " </Criterion>"
					+ " </Criteria>" + "</XMLQuery>";
			DataOutputStream dOut = new DataOutputStream(urlConn.getOutputStream());
			dOut.writeBytes(requestXML);
			dOut.flush();
			String encoding = urlConn.getContentEncoding();
			InputStream inStream = null;
			// based on encoding define input stream type and get it
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				inStream = new GZIPInputStream(urlConn.getInputStream());
			} else {
				inStream = urlConn.getInputStream();
			}
			Reader inputStreamReader = new InputStreamReader(inStream);
			dOut.close();
			IHSReturn = getMFRDetailsfromXML(context, inputStreamReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return IHSReturn;
	}

	public MapList getMFRDetailsfromXML(Context context, Reader inputStreamReader) throws Exception {
		MapList IHSData = new MapList();
		String strIHSResponseXML = "";
		ArrayList<Object> mfrList;
		Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			strIHSResponseXML = IOUtils.toString(inputStreamReader);
			inputStreamReader = new StringReader(strIHSResponseXML);
			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
				generated.Result result = (generated.Result) xmlResultList.get(0);
				String sCount = result.getCount();
				sCount = sCount.replace("+", "");
				if ((Integer.parseInt(sCount)) > 0) {
					mfrList = (ArrayList<Object>) result.getPartOrMfrOrError();
					for (int i = 0; i < mfrList.size(); i++) {
						generated.Mfr MFR = (generated.Mfr) mfrList.get(i);
						String sAddress = MFR.getMfrAdd1();
						if (!(MFR.getMfrAdd2()).equals("")) {
							sAddress += "," + MFR.getMfrAdd2();
						}
						if (!(MFR.getMfrAdd3()).equals("")) {
							sAddress += "," + MFR.getMfrAdd3();
						}
						if (!(MFR.getMfrAdd4()).equals("")) {
							sAddress += "," + MFR.getMfrAdd4();
						}
						String sWebsite = MFR.getMfrSite();
						String sregion = MFR.getMfrRegion();
						String sCity = MFR.getMfrAddCity();
						String sPhone = MFR.getMfrPhone();
						ihsAttrInfoMap.put("Address", sAddress);
						ihsAttrInfoMap.put("Website", sWebsite);
						ihsAttrInfoMap.put("Region", sregion);
						ihsAttrInfoMap.put("City", sCity);
						ihsAttrInfoMap.put("Phone No", sPhone);
						IHSData.add(ihsAttrInfoMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return IHSData;
	}

	public MapList retrieveIHSData2(Context context, String paramString) throws Exception {
		MapList localMapList = new MapList();
		Proxy localProxy = null;
		InputStream localInputStream = null;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			String strPageFileName = "XORGIHSIntegrationMapping";
			String MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			input = new ByteArrayInputStream(bytes);
			prop.load(input);
			String str1 = paramString;
			String str2 = prop.getProperty("IHS.credentials.username");
			String str3 = prop.getProperty("IHS.credentials.password");
			String str4 = prop.getProperty("IHS.credentials.serverURL");
			String str5 = prop.getProperty("IHS.credentials.serverFile");
			String str6 = prop.getProperty("IHS.RealTime.connection.timeout");
			String str7 = prop.getProperty("IHS.RealTime.read.timeout");
			URL localURL = new URL("HTTPS", str4, 443, str5);
			URLConnection localURLConnection = localProxy == null ? localURL.openConnection()
					: localURL.openConnection(localProxy);
			localURLConnection.setDoInput(true);
			localURLConnection.setDoOutput(true);
			localURLConnection.setRequestProperty("Content-Type", "text/xml");
			localURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			localURLConnection.connect();
			localURLConnection.setConnectTimeout(Integer.parseInt(str6));
			localURLConnection.setReadTimeout(Integer.parseInt(str7));
			String str8 = str1.replace("&", "&amp;");
			String str9 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><XMLQuery version=\"0.1\"><Login password=\""
					+ str3 + "\" user-name=\"" + str2
					+ "\"/><Criteria limit=\"200\" search-type=\"PART\" part-details=\"DOC,LATEST_DATASHEET,DETAILS,ALT\" alt-type=\"direct,similar,FFF,functional\"><Criterion id=\"TEST\"><Parameter match-type=\"WILDCARD\" name=\"part-number\">"
					+ str8 + "*</Parameter> </Criterion> </Criteria></XMLQuery>";
			DataOutputStream localDataOutputStream = new DataOutputStream(localURLConnection.getOutputStream());
			localDataOutputStream.writeBytes(str9);
			localDataOutputStream.flush();
			String str10 = localURLConnection.getContentEncoding();
			Object localObject1 = null;
			if ((str10 != null) && (str10.equalsIgnoreCase("gzip"))) {
				localObject1 = new GZIPInputStream(localURLConnection.getInputStream());
			} else {
				localObject1 = localURLConnection.getInputStream();
			}
			InputStreamReader localInputStreamReader = new InputStreamReader((InputStream) localObject1);
			localDataOutputStream.close();
			return getPartDetailsfromXML2(localInputStreamReader);
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localInputStream != null) {
				try {
					localInputStream.close();
				} catch (IOException localIOException3) {
					localIOException3.printStackTrace();
				}
			}
			return new MapList();
		}
	}

	public static MapList getPartDetailsfromXML2(Reader inputStreamReader) throws Exception {
		MapList IHSData = new MapList();
		generated.Partdetails partDetails = new generated.Partdetails();
		ArrayList<Details> detailsList = new ArrayList<Details>();
		String strPartDetailsType = "";
		ArrayList<Object> partList;
		generated.Doc doc = new generated.Doc();
		ArrayList<Doc> docList;
		String strDocURL = "";
		Map<String, String> docURLDateMap = new HashMap<String, String>();
		String strIHSResponseXML = "";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			strIHSResponseXML = IOUtils.toString(inputStreamReader);
			inputStreamReader = new StringReader(strIHSResponseXML);
			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
				generated.Result result = (generated.Result) xmlResultList.get(0);
				String sCount = result.getCount();
				sCount = sCount.replace("+", "");
				if ((Integer.parseInt(sCount)) > 0) {
					partList = (ArrayList<Object>) result.getPartOrMfrOrError();
					for (int i = 0; i < partList.size(); i++) {
						Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
						String strLatestDocURL = "";
						generated.Part part = (generated.Part) partList.get(i);
						String sDocType = "";
						String sDocTitle = "";
						String sDocDate = "";
						ArrayList<generated.Partdetails> partDetailsList = (ArrayList<Partdetails>) part
								.getPartdetails();
						for (int k = 0; k < partDetailsList.size(); k++) {
							partDetails = partDetailsList.get(k);
							strPartDetailsType = partDetails.getType();
							if (strPartDetailsType.equalsIgnoreCase("DOC")) {
								docList = (ArrayList<Doc>) partDetails.getDoc();
								for (int j = 0; j < docList.size(); j++) {
									doc = docList.get(j);
									if (doc.getDocType().equalsIgnoreCase("Datasheet")) {
										docURLDateMap.put(doc.getPubDate(),
												doc.getDocTitle() + ":" + "\n" + doc.getDocUrl());
									}
								}
							} else if (strPartDetailsType.equalsIgnoreCase("DETAILS")) {
								detailsList = (ArrayList<Details>) partDetails.getDetails();
							}
						}
						String sSortedDateList = getSortedDateList2(docURLDateMap);
						String[] aSortedDate = sSortedDateList.split("\\|");
						String strDocURLSorted = "";
						for (int ii = 0; ii < aSortedDate.length; ii++) {
							String strDateKey = aSortedDate[ii];
							String strUrl = (String) docURLDateMap.get(strDateKey);
							if (ii == 0) {
								strDocURLSorted = strUrl;
								strLatestDocURL = strDocURLSorted;
							} else {
								strDocURLSorted = strDocURLSorted + "\n" + strUrl;

							}
						}
						String sURL = "";
						if (strDocURLSorted != null && !strDocURLSorted.equals("")) {
							String[] aDocUrl = strDocURLSorted.split("\n");
							strLatestDocURL = aDocUrl[0] + "\n" + aDocUrl[1];
							int lastindex = strLatestDocURL.length();
							int index = strLatestDocURL.indexOf("https");
							sURL = strLatestDocURL.substring(index, lastindex);
						}
						ihsAttrInfoMap.put("sURL", sURL);
						String sLCRisk = "";
						String sENVRisk = "";
						String sSCRisk = "";
						String sGeneric = "";
						String sDLAQual = "";
						String strAttrName = "";
						String strAttrValue = "";
						for (int jj = 0; jj < detailsList.size(); jj++) {
							generated.Details details = detailsList.get(jj);
							strAttrName = (String) details.getName();
							strAttrValue = (String) details.getValue();
							if (strAttrName.equals("LC Risk")) {
								sLCRisk = strAttrValue;
							} else if (strAttrName.equals("ENV Risk")) {
								sENVRisk = strAttrValue;
							} else if (strAttrName.equals("SC Risk")) {
								sSCRisk = strAttrValue;
							} else if (strAttrName.equals("Generic/Series")) {
								sGeneric = strAttrValue;
							} else if (strAttrName.equals("DLA Qualification")) {
								sDLAQual = strAttrValue;
							}
						}
						ihsAttrInfoMap.put("LC Risk", sLCRisk);
						ihsAttrInfoMap.put("ENV Risk", sENVRisk);
						ihsAttrInfoMap.put("SC Risk", sSCRisk);
						ihsAttrInfoMap.put("Part Number", part.getManufacturerPartNumber());
						ihsAttrInfoMap.put("Mfr Name", part.getMfrName());
						ihsAttrInfoMap.put("Cage Code", part.getCageCode());
						ihsAttrInfoMap.put("Part Status", part.getPartStatus());
						ihsAttrInfoMap.put("Generic", sGeneric);
						ihsAttrInfoMap.put("Part Desc", part.getPartDescription());
						ihsAttrInfoMap.put("Classification", part.getPartType());
						ihsAttrInfoMap.put("DLA Qualification", sDLAQual);
						IHSData.add(ihsAttrInfoMap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return IHSData;
	}

	public static String getSortedDateList2(Map<String, String> docURLDateMap) throws Exception {
		ArrayList<Date> date = new ArrayList<Date>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyy");
		String Date = "";
		for (Map.Entry<String, String> entry : docURLDateMap.entrySet()) {
			Date date1 = format.parse(entry.getKey());
			date.add(date1);
		}
		Collections.sort(date, Collections.reverseOrder());
		for (int i = 0; i < date.size(); i++) {
			if (i == 0) {
				Date = format.format(date.get(i));
			} else {
				Date = Date + "|" + format.format(date.get(i));
			}
		}
		return Date;
	}

	public static String getPartDetailsfromXMLOnly(Reader inputStreamReader) throws Exception {
		String strIHSResponseXML = "";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			strIHSResponseXML = IOUtils.toString(inputStreamReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strIHSResponseXML;
	}

	public static String retrieveIHSDataXMLOnly(Context context, String paramString) throws Exception {
		MapList localMapList = new MapList();
		Proxy localProxy = null;
		InputStream localInputStream = null;
		Properties prop = new Properties();
		InputStream input = null;
		String strIHSResponseXML = "";
		try {
			String strPageFileName = "XORGIHSIntegrationMapping";
			String MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
			byte[] bytes = MQLResult.getBytes("UTF-8");
			input = new ByteArrayInputStream(bytes);
			prop.load(input);
			String str1 = paramString;
			String str2 = prop.getProperty("IHS.credentials.username");
			String str3 = prop.getProperty("IHS.credentials.password");
			String str4 = prop.getProperty("IHS.credentials.serverURL");
			String str5 = prop.getProperty("IHS.credentials.serverFile");
			String str6 = prop.getProperty("IHS.RealTime.connection.timeout");
			String str7 = prop.getProperty("IHS.RealTime.read.timeout");
			URL localURL = new URL("HTTPS", str4, 443, str5);
			URLConnection localURLConnection = localProxy == null ? localURL.openConnection()
					: localURL.openConnection(localProxy);
			localURLConnection.setDoInput(true);
			localURLConnection.setDoOutput(true);
			localURLConnection.setRequestProperty("Content-Type", "text/xml");
			localURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			localURLConnection.connect();
			localURLConnection.setConnectTimeout(Integer.parseInt(str6));
			localURLConnection.setReadTimeout(Integer.parseInt(str7));
			String str8 = str1.replace("&", "&amp;");
			String str9 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><XMLQuery version=\"0.1\"><Login password=\""
					+ str3 + "\" user-name=\"" + str2
					+ "\"/><Criteria limit=\"200\" search-type=\"PART\" part-details=\"DOC,LATEST_DATASHEET,DETAILS,ALT\" alt-type=\"direct,similar,FFF,functional\"><Criterion id=\"TEST\"><Parameter match-type=\"EXACT\" name=\"part-number\">"
					+ str8 + "*</Parameter> </Criterion> </Criteria></XMLQuery>";
			DataOutputStream localDataOutputStream = new DataOutputStream(localURLConnection.getOutputStream());
			localDataOutputStream.writeBytes(str9);
			localDataOutputStream.flush();
			String str10 = localURLConnection.getContentEncoding();
			Object localObject1 = null;
			if ((str10 != null) && (str10.equalsIgnoreCase("gzip"))) {
				localObject1 = new GZIPInputStream(localURLConnection.getInputStream());
			} else {
				localObject1 = localURLConnection.getInputStream();
			}
			InputStreamReader localInputStreamReader = new InputStreamReader((InputStream) localObject1);
			localDataOutputStream.close();
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			strIHSResponseXML = IOUtils.toString(localInputStreamReader);
			return strIHSResponseXML;
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			if (localInputStream != null) {
				try {
					localInputStream.close();
				} catch (IOException localIOException3) {
					localIOException3.printStackTrace();
				}
			}
			return strIHSResponseXML;
		}
	}

	public static boolean isPartPresentInIHS(Context context, String strIHSResponseXML) {
		boolean isPartPresentInIHS = false;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Reader inputStreamReader = new StringReader(strIHSResponseXML);
			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
				generated.Result result = (generated.Result) xmlResultList.get(0);
				if (Integer.parseInt(result.getCount()) > 0) {
					isPartPresentInIHS = true;
					return isPartPresentInIHS;
				} else {
					isPartPresentInIHS = false;
					return isPartPresentInIHS;
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		} finally {
			return isPartPresentInIHS;
		}
	}

	public static String getIHSManufacturerNames(Context context, String strIHSResponseXML) throws Exception {
		MapList IHSData = new MapList();
		generated.Partdetails partDetails = new generated.Partdetails();
		ArrayList<Details> detailsList = new ArrayList<Details>();
		String strPartDetailsType = "";
		ArrayList<Object> partList;
		generated.Doc doc = new generated.Doc();
		ArrayList<Doc> docList;
		String strDocURL = "";
		String strIHSManufacturerNames = "";
		Map<String, String> docURLDateMap = new HashMap<String, String>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLResult.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Reader inputStreamReader = new StringReader(strIHSResponseXML);
			XMLResult xmlResult = (XMLResult) jaxbUnmarshaller.unmarshal(inputStreamReader);
			inputStreamReader.close();
			if (xmlResult.getStatus().equalsIgnoreCase("SUCCESS")) {
				ArrayList<Result> xmlResultList = (ArrayList<Result>) xmlResult.getResult();
				generated.Result result = (generated.Result) xmlResultList.get(0);
				if (Integer.parseInt(result.getCount()) > 0) {
					partList = (ArrayList<Object>) result.getPartOrMfrOrError();
					for (int i = 0; i < partList.size(); i++) {
						Map<String, String> ihsAttrInfoMap = new HashMap<String, String>();
						String strLatestDocURL = "";
						generated.Part part = (generated.Part) partList.get(i);
						if (strIHSManufacturerNames != null && strIHSManufacturerNames.isEmpty()) {
							strIHSManufacturerNames = (String) part.getFullMfrName();
						} else if (strIHSManufacturerNames != null && !strIHSManufacturerNames.isEmpty()) {
							strIHSManufacturerNames = strIHSManufacturerNames + " | " + (String) part.getFullMfrName();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strIHSManufacturerNames;
	}
	// Added for Issue 222 Ends
	
	public String IHSWeeklyUpdates(Context context, String[] args) throws Exception {
		String returnString = "";
		String[] methodargs = new String[1];
		StringList slSelectable = new StringList(4);
		String strPageFileName = "XORGIHSIntegrationMapping";
		String MQLResult = "";
		String strMEPID = "";
		String strIHSManufacturerPartNumber = "";
		String strIHSManufacturerFullName = "";
		String strIIHSObjectID = "";
		String sError = "";
		String attribute_IHSLastSyncDate = PropertyUtil.getSchemaProperty(context, "attribute_IHSLastSyncDate");
		String attribute_IHSObjectID = PropertyUtil.getSchemaProperty(context, "attribute_IHSObjectID");

		MQLResult = MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageFileName);
		byte[] bytes = MQLResult.getBytes("UTF-8");
		InputStream MQLinput = new ByteArrayInputStream(bytes);
		Properties prop = new Properties();
		prop.load(MQLinput);

		String strLogFileHeader = prop.getProperty("XORGIHS.Success.Log.header");

		String strSuccessLogFilePath = prop.getProperty("XORGIHS.Log.path");
		SimpleDateFormat dateForLog = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		File log = new File(strSuccessLogFilePath + "IHS_WeeklyUpdate" + "_" + dateForLog.format(date) + ".log");
		log.getParentFile().mkdirs();
		BufferedWriter bw = null;

		if (!log.exists()) {
			FileWriter fw = new FileWriter(log, true);
			bw = new BufferedWriter(fw);
			bw.write(strLogFileHeader);
			bw.newLine();
		} else {
			FileWriter fw = new FileWriter(log, true);
			bw = new BufferedWriter(fw);
		}
		String strNumberofDays = prop.getProperty("IHS.RegularUpdates.Frequency.NumberOfDaysAfterLastIHSSync");
		int ivariation=Integer.parseInt(strNumberofDays);
		Integer variation = new Integer(ivariation);
	
		try {
			Calendar calendar               = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, - (variation));
		String timePattern              = "MM/dd/yyyy hh:mm:ss a";
		SimpleDateFormat formatter      = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
		formatter.applyPattern(timePattern);
		String oneMonthBackTime              = formatter.format(calendar.getTime());
		String strWhere = "policy=='Manufacturer Equivalent' && attribute[" + attribute_IHSObjectID + "]!='' && attribute[" + attribute_IHSLastSyncDate + "]" + "<='" + oneMonthBackTime+"'";
		slSelectable.add(DomainConstants.SELECT_ID);
		slSelectable.add(DomainConstants.SELECT_NAME);
		slSelectable.add("to["+DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY+"].from.name");
		slSelectable.add("attribute["+attribute_IHSObjectID+"]");
		MapList mlMEPswithIHSObjID = DomainObject.findObjects(context, // context
									DomainConstants.TYPE_PART, // typePattern
									DomainConstants.QUERY_WILDCARD, // namePattern
									DomainConstants.QUERY_WILDCARD, // revPattern
									DomainConstants.QUERY_WILDCARD, // ownerPattern
									DomainConstants.QUERY_WILDCARD, // vaultPatern
									strWhere, // whereExpression
									false, // expandType
									slSelectable);// objectSelects
		for(int ijk=0;ijk<mlMEPswithIHSObjID.size();ijk++)
		{
			if(ijk<7500)
			{
				Map<String, String> mepMAPwithIHSObjID = (Map<String, String>) mlMEPswithIHSObjID
										.get(ijk);
				strMEPID = (String) mepMAPwithIHSObjID.get(DomainConstants.SELECT_ID);
				strIHSManufacturerPartNumber= (String) mepMAPwithIHSObjID.get(DomainConstants.SELECT_NAME);
				strIHSManufacturerFullName= (String) mepMAPwithIHSObjID.get("to["+DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY+"].from.name");
				strIIHSObjectID= (String) mepMAPwithIHSObjID.get("attribute["+attribute_IHSObjectID+"]");
				methodargs[0] = strMEPID;
				sError = (String) JPO.invoke(context, "XORGIHSIntegration", null, "syncWithIHS",
										methodargs, String.class);
				if (sError != null && !"".equals(sError)) {
					bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
											+ strIIHSObjectID + "|" + sError);
					bw.newLine();
				} else {
					bw.write(strIHSManufacturerPartNumber + "|" + strIHSManufacturerFullName + "|"
											+ strIIHSObjectID + "|" + "Success-Refer IHS Logs|");
					bw.newLine();
				}
			}
		}

			return returnString;
		} catch (Exception E) {
			bw.write("An Issue was encountered while accessing the IHS data. Please contact administrator");
			bw.newLine();
			bw.close();
			E.printStackTrace();
		} finally {
			bw.close();
		}

		return returnString;
	}

}