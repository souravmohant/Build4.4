import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Element;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.FrameworkStringResource;
import com.matrixone.apps.domain.Job;
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
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UITableIndented;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.mxType;
import com.matrixone.jdom.Document;
import com.matrixone.apps.domain.DomainAccess;
import matrix.db.AttributeType;
import matrix.db.AttributeTypeList;
import matrix.db.BusinessType;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.db.Policy;
import matrix.db.PolicyItr;
import matrix.db.PolicyList;
import matrix.db.Relationship;
import matrix.db.RelationshipList;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;

import java.util.StringTokenizer;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import matrix.db.Page;
import java.io.InputStream;
public class googCustomFunctions_mxJPO extends googConstants_mxJPO {
	private StringList Basics;
	@SuppressWarnings("rawtypes")
	private static Map columnIndexMap;
	public static final String SYMB_state_Create = "state_Create";
	public static final String SYMB_state_Complete = "state_Complete";
	public static final String SYMB_state_Release = "state_Release";
	public static final String RELATIONSHIP_PART_REVISION = PropertyUtil
			.getSchemaProperty("relationship_PartRevision");
	public static final String TARGET_FILE_FOLDER = "C:/temp/";
	public static final String TARGET_FILE_SUFFIX = ".txt";
	public static final String TARGET_FILE_SUFFIX_CSV = ".csv";
	private HashMap<String, Element> _hmParts;

	private HashMap<String, HashMap<String, String>> _hmDataParts;
	public static final String XML_TAG_NODE1 = "Timestamp";
	public static final String XML_TAG_NODE2 = "Username";
	public static final String XML_TAG_NODE3 = "Ticket";
	public static final String XML_TAG_NODE4 = "Priority";
	public static final String XML_TAG_NODE5 = "Product";
	public static final String XML_TAG_NODE6 = "Part Type";
	public static final String XML_TAG_NODE7 = "Does this part require CAD";
	public static final String XML_TAG_NODE8 = "Part Number";
	public static final String XML_TAG_NODE9 = "Description";
	public static final String XML_TAG_NODE10 = "Manufacturer";
	public static final String XML_TAG_NODE11 = "Manufacturer P/N";
	public static final String XML_TAG_NODE12 = "Paste link to datasheets";
	public static final String XML_TAG_NODE13 = "Comments";
	public static final String XML_TAG_NODE14 = "Shipping or Receiving Overseas (Classification/Valuation)";
	public static final String XML_TAG_NODE15 = "Date Required";
	public static final String XML_TAG_NODE16 = "Cost Center";
	public static final String XML_TAG_NODE17 = "Functionality";
	public static final String XML_TAG_NODE18 = "Material / Composition";
	public static final String XML_TAG_NODE19 = "Imported as a complete assembly";
	public static final String XML_TAG_NODE20 = "The type of machine/equipment the GPN is designed for";
	public static final String XML_TAG_NODE21 = "Does this item include encryption or call on external encryption functionality?";
	public static final String XML_TAG_NODE22 = "If the item includes encryption or calls on external encryption functionality, is this Google technology, or something that was manufactured, customized or modified in any way specially for Google?";
	public static final String XML_TAG_NODE23 = "Requester Comments";
	public static final String XML_TAG_NODE24 = "Classification Ticket";

	private final String SELECT_COLLAB_SPACE = "project";
	private final String TYPE_VPMREFERENCE = "VPMReference";
	private final String RELATIONSHIP_VPMINSTANCE = "VPMInstance";

	private static final String sAttrReviewersComments = PropertyUtil
			.getSchemaProperty("attribute_ReviewersComments");
	private static final String sAttrReviewTask = PropertyUtil
			.getSchemaProperty("attribute_ReviewTask");
	private static final String sAttrReviewCommentsNeeded = PropertyUtil
			.getSchemaProperty("attribute_ReviewCommentsNeeded");
	private static final String sAttrRouteAction = PropertyUtil
			.getSchemaProperty("attribute_RouteAction");
	private static final String sAttrScheduledCompletionDate = PropertyUtil
			.getSchemaProperty("attribute_ScheduledCompletionDate");
	private static final String sAttrTitle = PropertyUtil
			.getSchemaProperty("attribute_Title");
	private static final String selTaskCompletedDate = PropertyUtil
			.getSchemaProperty("attribute_ActualCompletionDate");
	private static final String sTypeInboxTask = PropertyUtil
			.getSchemaProperty("type_InboxTask");
	private static final String sRelProjectTask = PropertyUtil
			.getSchemaProperty("relationship_ProjectTask");
	private static final String sRelRouteTask = PropertyUtil
			.getSchemaProperty("relationship_RouteTask");
	private static final String sRelRouteScope = PropertyUtil
			.getSchemaProperty("relationship_RouteScope");
	private static final String policyTask = PropertyUtil
			.getSchemaProperty("policy_InboxTask");
	private static final String strAttrRouteAction = "attribute["
			+ sAttrRouteAction + "]";
	private static final String strAttrCompletionDate = "attribute["
			+ sAttrScheduledCompletionDate + "]";
	private static final String strAttrTitle = "attribute[" + sAttrTitle + "]";
	private static final String strAttrTaskCompletionDate = "attribute["
			+ selTaskCompletedDate + "]";
	private static String routeIdSelectStr = "from[" + sRelRouteTask
			+ "].to.id";
	private static String routeTypeSelectStr = "from[" + sRelRouteTask
			+ "].to.type";
	private static String routeNameSelectStr = "from[" + sRelRouteTask
			+ "].to.name";
	private static String routeOwnerSelectStr = "from[" + sRelRouteTask
			+ "].to.owner";
	private static String objectNameSelectStr = "from[" + sRelRouteTask
			+ "].to.to[" + sRelRouteScope + "].from.name";
	private static String objectIdSelectStr = "from[" + sRelRouteTask
			+ "].to.to[" + sRelRouteScope + "].from.id";
	private static final String policyWorkflowTask = PropertyUtil
			.getSchemaProperty("policy_WorkflowTask");
	private static final String attrworkFlowDueDate = PropertyUtil
			.getSchemaProperty("attribute_DueDate");
	private static final String attrTaskEstinatedFinishDate = PropertyUtil
			.getSchemaProperty("attribute_TaskEstimatedFinishDate");
	private static final String attrworkFlowActCompleteDate = PropertyUtil
			.getSchemaProperty("attribute_ActualCompletionDate");
	private static final String attrworkFlowInstructions = PropertyUtil
			.getSchemaProperty("attribute_Instructions");
	private static final String attrTaskFinishDate = PropertyUtil
			.getSchemaProperty("attribute_TaskActualFinishDate");
	private static final String sRelAssignedTask = PropertyUtil
			.getSchemaProperty("relationship_AssignedTasks");
	private static final String sRelSubTask = PropertyUtil
			.getSchemaProperty("relationship_Subtask");
	private static final String sRelWorkflowTask = PropertyUtil
			.getSchemaProperty("relationship_WorkflowTask");
	private static final String sRelWorkflowTaskAssinee = PropertyUtil
			.getSchemaProperty("relationship_WorkflowTaskAssignee");
	private static final String sRelWorkflowTaskDeliverable = PropertyUtil
			.getSchemaProperty("relationship_TaskDeliverable");
	private static final String workflowIdSelectStr = "to[" + sRelWorkflowTask
			+ "].from.id";
	private static final String workflowNameSelectStr = "to["
			+ sRelWorkflowTask + "].from.name";
	private static final String workflowTypeSelectStr = "to["
			+ sRelWorkflowTask + "].from.type";

	// CA Summary View Page - Modified by Sara on 10/10/2017 - Start
	private static final String REL_CHANGE_AFFECTED_ITEM = PropertyUtil
			.getSchemaProperty("relationship_ChangeAffectedItem");
	// CA Summary View Page - Modified by Sara on 10/10/2017 - End
	
	/*
	 * Constructor
	 */
	@SuppressWarnings("rawtypes")
	public googCustomFunctions_mxJPO() {
		// TODO Auto-generated constructor stub

		Basics = new StringList();
		columnIndexMap = new HashMap();

		if (Basics.isEmpty()) {
			Basics.addElement("type");
			Basics.addElement("policy");
			Basics.addElement("name");
			Basics.addElement("owner");
			Basics.addElement("revision");
			Basics.addElement("description");
		}
	}

	/**
	 * This function is for fixing the gsheet to XLSX function Conversion
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public File reformatNumberstoText(Context context, String[] args)
			throws Exception {

		Map paramMap = (Map) JPO.unpackArgs(args);
		java.io.File oFile = (File) paramMap.get("oFile");
		HashMap hashmap = (HashMap) paramMap.get("parentMap");
		MapList maplist;
		HashMap hashmap1;
		maplist = new MapList();
		hashmap1 = new HashMap();
		hashmap.remove("BGProcess");
		hashmap.remove("ODT");
		if (hashmap.containsKey("id")) {
			String s2 = (String) hashmap.get("id");
			DomainObject domainobject = new DomainObject(s2);
			hashmap.put("name", domainobject.getInfo(context, "name"));
			hashmap.put("type", domainobject.getInfo(context, "type"));
			hashmap.put("revision", domainobject.getInfo(context, "revision"));
			hashmap1 = hashmap;
		}
		try {

			FileInputStream fis = new FileInputStream(oFile);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator iterator = sheet.rowIterator();

			long l2 = 0L;
			boolean flag = false;
			boolean flag1 = false;
			long l3 = 0L;
			do {
				if (!iterator.hasNext())
					break;
				Row row = (Row) iterator.next();

				if (flag) {
					Map map = getRowInfo(row);
					if (map == null) {
						throw new Exception(
								EnoviaResourceBundle
										.getProperty(
												context,
												"emxEngineeringCentralStringResource",
												context.getLocale(),
												"emxEngineeringCentral.ImportEBOM.FileNotFormatted"));
					} else if (row.getCell(0) != null) {
						break;
					}
					if (UIUtil.isNotNullAndNotEmpty(getStringValue(map, "level"))) {
						long l1 = Long.parseLong(getStringValue(map, "level"));
						if ((long) row.getRowNum() != l3 && l1 == 0L || l1 > l2
								&& l1 != l2 + 1L)
							throw new Exception(
									EnoviaResourceBundle
											.getProperty(
													context,
													"emxEngineeringCentralStringResource",
													context.getLocale(),
													"emxEngineeringCentral.ImportEBOM.LevelException"));
						l2 = l1;
						if (!hashmap.isEmpty() && l1 == 0L || hashmap.isEmpty()
								&& (long) row.getRowNum() == l3 && l1 != 0L)
							throw new Exception(
									EnoviaResourceBundle
											.getProperty(
													context,
													"emxEngineeringCentralStringResource",
													context.getLocale(),
													"emxEngineeringCentral.ImportEBOM.LevelException"));
					} else {
						throw new Exception(
								EnoviaResourceBundle
										.getProperty(
												context,
												"emxEngineeringCentralStringResource",
												context.getLocale(),
												"emxEngineeringCentral.ImportEBOM.LevelException"));
					}
					if ("0".equals(getStringValue(map, "level")))
						hashmap1.putAll(map);
					else
						maplist.add(map);
				} else {
					String s4 = "";
					if (row.getCell(0) != null)
						try {
							s4 = row.getCell(0).getStringCellValue();
						} catch (Exception exception2) {
							s4 = "";
						}
					if ("Import Data".equalsIgnoreCase(s4))
						flag1 = true;
					else if (flag1) {
						columnIndexMap.clear();
						for (int i = 0; i < row.getLastCellNum(); i++) {
							if (row.getCell(i) == null)
								continue;
							String s5 = row.getCell(i).getStringCellValue();
							if (Basics.contains(s5.toLowerCase()))
								s5 = s5.toLowerCase();
							columnIndexMap.put(s5, Integer.valueOf(i));
						}
						flag = true;
						l3 = row.getRowNum() + 1;
					}
				}
			} while (true);
			// Call the program to remove the extra number of rows and rewrite
			// the File...
			oFile = (File) rewriteFile(context, sheet, oFile);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return oFile;
	}


	/**
	 * This method will rewrite the file..
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public File rewriteFile(Context context, XSSFSheet sheet, File oFile)
			throws Exception {

		// Create a new Work Book and XLSX File to put only the rows which have
		// data to it
		XSSFWorkbook wb = new XSSFWorkbook();
		// just gave a dummy name - this would have no significance
		XSSFSheet sht = wb.createSheet("BOMImport");
		Row row = null;
		int lastIndex = sheet.getLastRowNum();

		// Create a new Index Map based on the Old One to inverse the Key Value
		// pairs
		HashMap<String, String> newcolumnIndexMap = new HashMap<String, String>();
		Iterator<String> iterator = columnIndexMap.keySet().iterator();
		while (iterator.hasNext()) {
			String strHeader = String.valueOf(iterator.next());
			newcolumnIndexMap.put(
					String.valueOf(columnIndexMap.get(strHeader)), strHeader);
		}
		String sSkipConverions = EnoviaResourceBundle.getProperty(context,
				"emxEngineeringCentral.GsheettoXLSXConversion.SkipConversion");
		List lSkipConColumns = (List) getTokenizedProperties(context,
				"emxEngineeringCentral.GsheettoXLSXConversion.SkipConversion.Column.name");

		for (int i = 0; i < lastIndex + 1; i++) {
			if (sheet.getRow(i).getRowNum() < sheet.getLastRowNum() + 1) {
				row = sheet.getRow(i);
				boolean bisemtyrow = (boolean) checkIfRowIsEmpty(row);
				if (!bisemtyrow) {

					XSSFRow rw = sht.createRow(i);

					for (int c = 0; c < row.getLastCellNum(); c++) {
						XSSFCell cell = rw.createCell(c);

						if (row.getCell(c) == null) {
							cell.setCellType(1);
							cell.setCellValue("");
						} else {
							String strcolumnIndexHeader = (String) newcolumnIndexMap
									.get(String.valueOf(row.getCell(c)
											.getColumnIndex()));

							if (row.getCell(c).getColumnIndex() == 0
									&& !row.getCell(c).toString()
											.equalsIgnoreCase("import data")
									&& !row.getCell(c).toString()
											.equalsIgnoreCase("level")) {
								// This is for Level Column to make sure that
								// the value is 1 and not 1.0
								cell.setCellType(1);
								String strValue = getFormattedString(row
										.getCell(c).toString());
								cell.setCellValue(strValue);

							} else if (lSkipConColumns
									.contains(strcolumnIndexHeader)
									&& sSkipConverions.equalsIgnoreCase("true")) {
								cell.setCellType(1);
								cell.setCellValue(row.getCell(c).toString());
							} else {
								if (row.getCell(c).getCellType() == 0) {
									cell.setCellType(1);
									String strValue = getFormattedString(row
											.getCell(c).toString());
									cell.setCellValue(strValue);

								} else if (row.getCell(c).getCellType() == 1) {
									cell.setCellType(1);
									cell.setCellValue(row.getCell(c).toString());
								} else {
									cell.setCellType(1);
									cell.setCellValue(row.getCell(c).toString());
								}
							}

						}
					}// end of for loop

				}
			} else {
				break;
			}
		}

		FileOutputStream fileOut = new FileOutputStream(oFile);
		wb.write(fileOut);
		fileOut.close();
		return oFile;
	}

	/**
	 * This method will Check if the rows are empty...
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param
	 * @return
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	private static boolean checkIfRowIsEmpty(Row row) {
		if (row == null) {
			return true;
		}
		if (row.getLastCellNum() <= 0) {
			return true;
		}
		boolean isEmptyRow = true;
		for (int cellNum = row.getFirstCellNum(); cellNum < row
				.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
				isEmptyRow = false;

			}
		}
		return isEmptyRow;
	}

	/**
	 * This method will iterate through the rows
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param
	 * @return
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	private static String getStringValue(Map map, String s) {
		return (String) map.get(s);
	}

	/**
	 * This method will iterate through the rows
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param
	 * @return
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	private static String getFormattedString(String s) {
		double number = Double.parseDouble(s);
		return String.valueOf((int) number);
	}

	/**
	 * This method will iterate through the rows
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param
	 * @return
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private static void setCellsAsString(Row row) throws Exception {
		Cell cell;
		for (Iterator iterator = row.cellIterator(); iterator.hasNext(); cell
				.setCellType(1))
			cell = (Cell) iterator.next();

	}

	/**
	 * This method will iterate through the rows
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param
	 * @return
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private static Map getRowInfo(Row row) throws Exception {
		HashMap hashmap = new HashMap();
		Iterator iterator = columnIndexMap.keySet().iterator();
		String s = "";
		String s2 = "";
		Object obj = null;
		String strformattedNumber;
		do {
			setCellsAsString(row);
			hashmap.put("id", "");
			strformattedNumber = "";
			hashmap.put("RowNum",
					(new StringBuilder()).append(row.getRowNum() + 1)
							.append("").toString());
			if (!iterator.hasNext())
				break;
			String s3 = "";
			String s1 = (String) iterator.next();
			if (UIUtil.isNotNullAndNotEmpty(s1)) {
				Cell cell = row.getCell(((Integer) columnIndexMap.get(s1))
						.intValue());
				if (cell != null) {
					String s4 = cell.getStringCellValue();
					hashmap.put(s1, s4);
					if ("level".equalsIgnoreCase(s1)) {
						hashmap.put("level", cell.getStringCellValue());
						hashmap.put("Level", cell.getStringCellValue());

					}
					if ("series".equalsIgnoreCase(s1))
						hashmap.put("series", cell.getStringCellValue());

				}
			}
		} while (true);
		return hashmap;
	}// end of method

	/**
	 * This method will get the Torque values from CATIA
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueMinValue(Context context, String[] args)
			throws Exception {
		return getTorqueColumnValues(
				context,
				args,
				"frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueMn].value");
	}

	/**
	 * This method will get the Torque Criticality values from CATIA
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueCriticalValue(Context context, String[] args)
			throws Exception {
		return getTorqueColumnValues(
				context,
				args,
				"frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueCriticality].value");
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueCritValueConsBOM(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String strSelect = "to[EBOM].frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueCriticality].value";

		ContextUtil.pushContext(context); // Make sure to be able to traverse
											// any relationship if the PP is in
											// a different CS
		Vector vblank = new Vector();
		MapList mlObjList = (MapList) programMap.get("objectList");

		if (null != mlObjList) {
			int nbObjects = mlObjList.size();
			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
			}

			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(strSelect);
			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					slSelects);

			ContextUtil.popContext(context);
			return torqueVector(context, mlResults, strSelect);
		} else {
			return vblank;
		}
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueMinValueConsBOM(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String strSelect = "to[EBOM].frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueMn].value";

		ContextUtil.pushContext(context); // Make sure to be able to traverse
											// any relationship if the PP is in
											// a different CS
		Vector vblank = new Vector();
		MapList mlObjList = (MapList) programMap.get("objectList");

		if (null != mlObjList) {
			int nbObjects = mlObjList.size();
			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
			}

			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(strSelect);
			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					slSelects);

			ContextUtil.popContext(context);
			return torqueVector(context, mlResults, strSelect);
		} else {
			return vblank;
		}
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueMaxValueConsBOM(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String strSelect = "to[EBOM].frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueMx].value";
		Vector vblank = new Vector();
		ContextUtil.pushContext(context); // Make sure to be able to traverse
											// any relationship if the PP is in
											// a different CS
		MapList mlObjList = (MapList) programMap.get("objectList");
		if (null != mlObjList) {
			int nbObjects = mlObjList.size();
			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
			}

			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(strSelect);
			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					slSelects);
			ContextUtil.popContext(context);
			return torqueVector(context, mlResults, strSelect);
		} else {
			return vblank;
		}
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getTorqueMaxValue(Context context, String[] args)
			throws Exception {
		return getTorqueColumnValues(
				context,
				args,
				"frommid[VPLMInteg-VPLMProjection].torel.attribute[googTorque.googTorqueMx].value");
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private static Vector getTorqueColumnValues(final Context context,
			final String[] args, final String strSelect) throws Exception {
		Vector vTorqueMax = new Vector();

		try {
			ContextUtil.pushContext(context); // Make sure to be able to
												// traverse any relationship if
												// the PP is in a different CS
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList mlObjList = (MapList) programMap.get("objectList");

			if (null != mlObjList) {
				int nbObjects = mlObjList.size();
				String[] arrayEBOMId = new String[nbObjects];
				for (int i = 0; i < nbObjects; i++) {
					Map mCurrent = (Map) mlObjList.get(i);
					//Back-end Exception Issue in EBOM Table : Modified by Shajil - Start
					//arrayEBOMId[i] = (String) mCurrent.get(DomainRelationship.SELECT_ID);
					String strRelId = (String) mCurrent
							.get(DomainRelationship.SELECT_ID);
					if(UIUtil.isNullOrEmpty(strRelId)) {
						strRelId = DomainConstants.EMPTY_STRING;
					}
					arrayEBOMId[i] = strRelId;
					//Back-end Exception Issue in EBOM Table : Modified by Shajil - Ends
				}

				StringList slSelects = new StringList();
				slSelects.add(strSelect);
				//Back-end Exception Issue in EBOM Table : Modified by Shajil - Start
				//MapList mlResults = DomainRelationship.getInfo(context,
						//arrayEBOMId, slSelects);
				MapList mlResults = new MapList();
				if(arrayEBOMId[0]!=DomainConstants.EMPTY_STRING) {
					mlResults = DomainRelationship.getInfo(context,
						arrayEBOMId, slSelects);
				}
				//Back-end Exception Issue in EBOM Table : Modified by Shajil - Ends
				vTorqueMax = torqueVector(context, mlResults, strSelect);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}

		return vTorqueMax;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	// Added this method to make sure consolidated BOM Item Works Well...for
	// Torque Value
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Vector torqueVector(Context context, MapList mlResults,
			final String strSelect) {
		Vector vTorqueMax = new Vector();
		if (null != mlResults) {
			String strLineSeparator = System.getProperty("line.separator");

			for (int i = 0; i < mlResults.size(); i++) {
				Map mCurrent = (Map) mlResults.get(i);

				Object objValue = mCurrent.get(strSelect);

				if (null != objValue) {
					String strValue = "";
					if (objValue instanceof StringList) {
						StringList slValues = (StringList) objValue;
						for (int j = 0; j < slValues.size(); j++) {
							String strCurrentValue = (String) slValues.get(j);
							if (null != strCurrentValue
									&& !strCurrentValue.isEmpty()) {
								if (!strValue.isEmpty()) {

									strValue += ",";
								}
								strValue += strCurrentValue;
							}
						}
					} else {
						strValue = (String) objValue;
					}

					if (strValue.indexOf("\u0007") == 0) {
						strValue = strValue.replace("\u0007", "");
					} else {
						strValue = strValue.replace("\u0007", ",");
					}

					vTorqueMax.add(strValue);

				} else {
					vTorqueMax.add("");
				}
			}
		}
		return vTorqueMax;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private static Vector getColumnValues(final Context context,
			final String[] args, final String strSelect) throws Exception {
		Vector vTorqueMax = new Vector();

		try {
			ContextUtil.pushContext(context); // Make sure to be able to
												// traverse any relationship if
												// the PP is in a different CS
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList mlObjList = (MapList) programMap.get("objectList");

			if (null != mlObjList) {
				int nbObjects = mlObjList.size();
				String[] arrayObjectId = new String[nbObjects];
				for (int i = 0; i < nbObjects; i++) {
					Map mCurrent = (Map) mlObjList.get(i);
					arrayObjectId[i] = (String) mCurrent
							.get(DomainConstants.SELECT_ID);
				}

				StringList slSelects = new StringList();
				slSelects.add(DomainConstants.SELECT_ID);
				slSelects.add(strSelect);
				MapList mlResults = DomainObject.getInfo(context,
						arrayObjectId, slSelects);

				if (null != mlResults) {
					for (int i = 0; i < mlResults.size(); i++) {
						Map mCurrent = (Map) mlResults.get(i);
						String strValue = (String) mCurrent.get(strSelect);
						if (null != strValue) {
							// ==========================================================
							// Replace BEL characters between values by line
							// separator
							// ==========================================================
							strValue = strValue.replace("\u0007", ",");
							vTorqueMax.add(strValue);
						} else {
							vTorqueMax.add("");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}

		return vTorqueMax;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private static Vector getColumnValues(final Context context,
			final String[] args, final StringList slSelect) throws Exception {
		Vector vTorqueMax = new Vector();

		try {
			ContextUtil.pushContext(context); // Make sure to be able to
												// traverse any relationship if
												// the PP is in a different CS
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList mlObjList = (MapList) programMap.get("objectList");

			if (null != mlObjList) {
				int nbObjects = mlObjList.size();
				String[] arrayObjectId = new String[nbObjects];
				for (int i = 0; i < nbObjects; i++) {
					Map mCurrent = (Map) mlObjList.get(i);
					arrayObjectId[i] = (String) mCurrent
							.get(DomainConstants.SELECT_ID);
				}

				MapList mlResults = DomainObject.getInfo(context,
						arrayObjectId, slSelect);

				if (null != mlResults) {
					for (int i = 0; i < mlResults.size(); i++) {
						Map mCurrent = (Map) mlResults.get(i);

						String strConcat = "";
						for (int j = 0; j < slSelect.size(); j++) {
							String strCurrentSelect = (String) slSelect.get(j);

							String strValue = (String) mCurrent
									.get(strCurrentSelect);

							if (null != strValue) {
								// ==========================================================
								// Replace BEL characters between values by line
								// separator
								// ==========================================================
								strValue = strValue.replace("\u0007", ",");

								// vTorqueMax.add(strValue);
								if (strConcat.isEmpty()) {
									strConcat = strValue;
								} else {
									strConcat += "," + strValue;
								}
							}
						}
						vTorqueMax.add(strConcat);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}

		return vTorqueMax;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getPartSpecificationCurrent(Context context, String[] args)
			throws Exception {
		StringList slSelects = new StringList();
		slSelects
				.addElement("from[Part Specification].to[VPMReference].current");
		slSelects
				.addElement("from[Part Specification].to[SW Component Instance For Team].current");
		slSelects
				.addElement("from[Part Specification].to[SW Assembly Instance For Team].current");

		Vector vectorSystemValues = getColumnValues(context, args, slSelects);

		Vector vectorToReturn = new Vector();

		for (int i = 0; i < vectorSystemValues.size(); i++) {
			String strValueToDisplay = "";
			try {
				strValueToDisplay = (String) vectorSystemValues.get(i);

				if (null != strValueToDisplay) {

					if (strValueToDisplay.contains(",")) {
						StringBuffer sb = new StringBuffer();
						String[] aValues = strValueToDisplay.split(",", -1);
						for (int j = 0; j < aValues.length; j++) {
							String strCurrentValue = aValues[j];

							if (0 < j) {
								sb.append(",");
							}

							sb.append(getDisplayValues(context, strCurrentValue));
						}

						strValueToDisplay = sb.toString();
					} else {
						strValueToDisplay = getDisplayValues(context,
								strValueToDisplay);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			vectorToReturn.add(strValueToDisplay);
		}

		return vectorToReturn;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getManufacturerPartName(Context context, String[] args)
			throws Exception {
		StringList slSelects = new StringList();
		slSelects.addElement("relationship["
				+ RELATIONSHIP_MANUFACTURER_EQUIVALENT + "].to.name");
		return getManufacturerColumnvalues(context, args, slSelects);
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getManufacturer(Context context, String[] args)
			throws Exception {
		StringList slSelects = new StringList();
		slSelects.addElement("from[" + RELATIONSHIP_MANUFACTURER_EQUIVALENT
				+ "].to.to[" + RELATIONSHIP_MANUFACTURING_RESPONSIBILITY
				+ "].from.name");
		return getManufacturerColumnvalues(context, args, slSelects);
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getManufacturerRDE(Context context, String[] args)
			throws Exception {
		StringList slSelects = new StringList();
		slSelects.addElement("from[" + RELATIONSHIP_MANUFACTURER_EQUIVALENT
				+ "].to.attribute[Responsible Design Engineer].value");
		return getManufacturerColumnvalues(context, args, slSelects);
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector getManufacturerColumnvalues(Context context,
			String[] args, StringList slSelects) throws Exception {

		@SuppressWarnings("rawtypes")
		Vector vectorSystemValues = getColumnValues(context, args, slSelects);

		Vector vectorToReturn = new Vector();

		for (int i = 0; i < vectorSystemValues.size(); i++) {
			String strValueToDisplay = "";
			try {
				strValueToDisplay = (String) vectorSystemValues.get(i);

				if (null != strValueToDisplay) {

					if (strValueToDisplay.contains(",")) {
						StringBuffer sb = new StringBuffer();
						String[] aValues = strValueToDisplay.split(",", -1);
						for (int j = 0; j < aValues.length; j++) {

							String strCurrentValue = aValues[j];
							// if not the only item ..
							if (0 < j) {
								sb.append(",");
							}

							sb.append(getDisplayValues(context, strCurrentValue));
						}

						strValueToDisplay = sb.toString();
					} else {
						strValueToDisplay = getDisplayValues(context,
								strValueToDisplay);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			vectorToReturn.add(strValueToDisplay);
		}
		return vectorToReturn;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getPartSpecificationVersion(Context context, String[] args)
			throws Exception {
		StringList slSelects = new StringList();
		slSelects
				.addElement("from[Part Specification].to[VPMReference].majorrevision");
		slSelects
				.addElement("from[Part Specification].to[SW Component Instance For Team].majorrevision");
		slSelects
				.addElement("from[Part Specification].to[SW Assembly Instance For Team].majorrevision");

		Vector vectorSystemValues = getColumnValues(context, args, slSelects);

		Vector vectorToReturn = new Vector();

		for (int i = 0; i < vectorSystemValues.size(); i++) {
			String strValueToDisplay = "";
			try {
				strValueToDisplay = (String) vectorSystemValues.get(i);

				if (null != strValueToDisplay) {

					if (strValueToDisplay.contains(",")) {
						StringBuffer sb = new StringBuffer();
						String[] aValues = strValueToDisplay.split(",", -1);
						for (int j = 0; j < aValues.length; j++) {
							String strCurrentValue = aValues[j];

							if (0 < j) {
								sb.append(",");
							}

							sb.append(getDisplayValues(context, strCurrentValue));
						}

						strValueToDisplay = sb.toString();
					} else {
						strValueToDisplay = getDisplayValues(context,
								strValueToDisplay);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			vectorToReturn.add(strValueToDisplay);
		}

		return vectorToReturn;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	private static String getDisplayValues(final Context context,
			final String strSystemName) {

		String strToReturn = strSystemName;
		try {
			String strPropertiesPrefix = "emxFramework.State.VPLM_SMB_Definition.";
			strToReturn = EnoviaResourceBundle.getProperty(context,
					"emxFrameworkStringResource", context.getSession()
							.getLocale(), strPropertiesPrefix + strSystemName);
			if (strToReturn.startsWith(strPropertiesPrefix)) {
				strToReturn = strSystemName;
			}
		} catch (Exception e) {
		}

		return strToReturn;
	}

	/**
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	public Vector<String> getParentDRE(Context context, String[] args) throws Exception {
		Vector<String> vTorqueMax = new Vector<String>();

		try {
			ContextUtil.pushContext(context); 
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList mlObjList = (MapList) programMap.get("objectList");
			if (null != mlObjList) {
				int nbObjects = mlObjList.size();
				String[] arrayEBOMId = new String[nbObjects];
				for (int i = 0; i < nbObjects; i++) {
					Map mCurrent = (Map) mlObjList.get(i);
					//Back-end Exception Issue in EBOM Table : Modified by Shajil - Start
					//arrayEBOMId[i] = (String) mCurrent.get(DomainRelationship.SELECT_ID);
					String strRelId = (String) mCurrent.get(DomainRelationship.SELECT_ID);
					if(UIUtil.isNullOrEmpty(strRelId)) {
						strRelId = DomainConstants.EMPTY_STRING;
					}
					arrayEBOMId[i] = strRelId;
					//Back-end Exception Issue in EBOM Table : Modified by Shajil - Ends
				}
				StringList slSelects = new StringList();
				slSelects
						.add("from.attribute["+ATTR_RESPONSIBLE_DESIGN_ENGINEER+"].value");
				// Doing a Null Check
				//Back-end Exception Issue in EBOM Table : Modified by Shajil - Start
				//if (null != arrayEBOMId || arrayEBOMId.length > 0) {
				if (null != arrayEBOMId && arrayEBOMId[0]!=DomainConstants.EMPTY_STRING) {
				//Back-end Exception Issue in EBOM Table : Modified by Shajil - Ends
					MapList mlResults = DomainRelationship.getInfo(context,
							arrayEBOMId, slSelects);
					if (null != mlResults) {
						for (int i = 0; i < mlResults.size(); i++) {
							Map mCurrent = (Map) mlResults.get(i);
							vTorqueMax
									.add((String) mCurrent
											.get("from.attribute["+ATTR_RESPONSIBLE_DESIGN_ENGINEER+"].value"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}

		return vTorqueMax;
	}

	/**
	 * gets the alternate parts
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public Vector getAlternateParts(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);

		MapList mlObjList = (MapList) programMap.get("objectList");
		HashMap sParamMap = (HashMap) programMap.get("paramList");
		String sExportFormat = (String) sParamMap.get("exportFormat");
		Vector vAltparts = new Vector();
		StringBuilder sbrAltPart;
		if (null != mlObjList) {
			StringList selectRelStmts = new StringList(1);
			selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);

			StringList selectStmts = new StringList(7);
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);

			MapList alternatePartList = null;

			Part part = (Part) DomainObject.newInstance(context,
					DomainConstants.TYPE_PART, DomainConstants.ENGINEERING);

			for (int i = 0; i < mlObjList.size(); i++) {
				sbrAltPart = new StringBuilder();
				Map mpPartDetails = (Map) mlObjList.get(i);
				String objectId = (String) mpPartDetails
						.get(DomainConstants.SELECT_ID);
				part.setId(objectId);

				alternatePartList = part.getAlternateParts(context,
						selectStmts, selectRelStmts, false);
				if (null != alternatePartList) {
					for (int j = 0; j < alternatePartList.size(); j++) {
						Map mpAltpart = (Map) alternatePartList.get(j);
						if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
							sbrAltPart.append(mpAltpart.get(DomainConstants.SELECT_NAME).toString());
							sbrAltPart.append(" ");
						} else {						
							sbrAltPart.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?mode=insert");
							sbrAltPart.append("&amp;objectId="
									+ mpAltpart.get(DomainConstants.SELECT_ID)
											.toString() + "'");
							sbrAltPart.append(", '800', '700', 'true', 'popup')\">");
							sbrAltPart.append(mpAltpart.get(DomainConstants.SELECT_NAME).toString());
							sbrAltPart.append("</a>");
							sbrAltPart.append(" ");
						}
					}// end of innerfor loop

				}
				// Add the Data to the Vector
				vAltparts.add(sbrAltPart.toString());
			}

		}
		return vAltparts;
	}

	/**
	 * gets the substitute parts
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	public Vector getSubstituteParts(Context context, String[] args)
			throws Exception {

		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String RELATIONSHIP_EBOM_SUBSTITUTE = PropertyUtil.getSchemaProperty(
				context, "relationship_EBOMSubstitute");
		Vector vSubparts = new Vector();
		
		MapList mlObjList = (MapList) programMap.get("objectList");
		//Modified by Sharad for Export Format issues - Starts
		HashMap sParamMap = (HashMap) programMap.get("paramList");
		String sExportFormat = (String) sParamMap.get("exportFormat");
		//Modified by Sharad for Export Format issues - Ends
		StringBuilder sbrSubstitutePart;

		if (null != mlObjList) {
			DomainObject domObj;
			StringList slEBOMSubs;
			// get the connection id for the EBOM
			for (int i = 0; i < mlObjList.size(); i++) {
				slEBOMSubs = new StringList();
				Map mpPartDetails = (Map) mlObjList.get(i);
				//Getting the EBOM Connection ID
				String strEBOMconnId = (String) mpPartDetails.get("id[connection]");
				
				if (UIUtil.isNotNullAndNotEmpty(strEBOMconnId)) {
					
					String strCommand = "print connection $1 select $2 dump $3";
					String strMessage = MqlUtil.mqlCommand(context, strCommand,
																	strEBOMconnId, 
																	"frommid["+ RELATIONSHIP_EBOM_SUBSTITUTE + "].to.id",
																	"|");
					if (UIUtil.isNotNullAndNotEmpty(strMessage)) {

						slEBOMSubs = FrameworkUtil.split(strMessage, "|");
						Iterator ebomsubsItr = slEBOMSubs.iterator();
						String sEBOMSubstituteRelid = "";
						sbrSubstitutePart = new StringBuilder();
						String strSubId = "";
						String strSubName = "";
						while (ebomsubsItr.hasNext()) {
							strSubId = ebomsubsItr.next().toString();
							domObj = DomainObject.newInstance(context, strSubId);
							strSubName = domObj.getInfo(context,
											DomainConstants.SELECT_NAME);
							if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
								sbrSubstitutePart.append(strSubName);
								sbrSubstitutePart.append(" ");
							} else {
									sbrSubstitutePart
											.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?mode=insert");
									sbrSubstitutePart
											.append("&amp;objectId=" + strSubId + "'");
									sbrSubstitutePart
											.append(", '800', '700', 'true', 'popup')\">");
									sbrSubstitutePart.append(strSubName);
									sbrSubstitutePart.append("</a>");
									sbrSubstitutePart.append(" ");
							}
						}
						vSubparts.add(sbrSubstitutePart.toString());
					} else {
						vSubparts.add("");
					}
				} else {
					vSubparts.add("");
				}
			} // end of for loop
		}
		return vSubparts;
	}// end of method getSubstituteParts

	/**
	 * This method used to enable the particular column cell for edit on the
	 * basis of access of user
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public StringList editAccess(Context context, String[] args)
			throws Exception {
		// Get the object id of the context object
		Map programMap = (HashMap) JPO.unpackArgs(args);
		Map paramMap = (Map) programMap.get("paramList");
		List lstobjectList = (MapList) programMap.get("objectList");
		Map objectMap = null;
		StringList result = new StringList();
		StringList lstObjSelects = new StringList();
		lstObjSelects.add(SELECT_OWNER);
		lstObjSelects.add(SELECT_CURRENT);

		for (int j = 0; j < lstobjectList.size(); j++) {
			objectMap = (Map) lstobjectList.get(j);
			String strObjectId = (String) objectMap
					.get(DomainConstants.SELECT_ID);

			DomainObject domObj = DomainObject
					.newInstance(context, strObjectId);
			ContextUtil.pushContext(context);
			Map mapPartInfo = (Map) domObj.getInfo(context, lstObjSelects);
			ContextUtil.popContext(context);
			//boolean bHasEditAccess = false;
			String bHasEditAccess = "false";
			// Checking the access to Edit
			String strCtxUser = context.getUser();
			boolean hasAdminRole = false;
			try {
				matrix.db.Person ctxPerson = new matrix.db.Person(strCtxUser);
				// Chek for Admin Role
				hasAdminRole = ctxPerson.isAssigned(context, PropertyUtil
						.getSchemaProperty(context, "role_googHideUser"));
				if (hasAdminRole) {
					bHasEditAccess = "true";
				}
				// }
				

			} catch (Exception e) {
				bHasEditAccess = "false";
			}

			result.add(bHasEditAccess);

		}// end of for loop for object List
		return result;
	}

	/**
	 * Displays the Range Values on Edit for Attribute Requested Change for
	 * Static and Dynamic Approval policy for ECR and ECO.
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object
	 * @param args
	 *            holds a HashMap containing the following entries: paramMap
	 *            hold a HashMap containing the following keys, "objectId"
	 * @return HashMap contains actual and display values
	 * @throws Exception
	 *             if operation fails
	 * @since EngineeringCentral X3
	 */
	public HashMap displayRequestedChangeRangeValues(Context context,
			String[] args) throws Exception {
		String strLanguage = context.getSession().getLanguage();

		StringList requestedChange = new StringList();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		String ChangeObjectId = (String) paramMap.get("objectId");
		DomainObject dom = new DomainObject(ChangeObjectId);

		// get all range values
		StringList strListRequestedChange = FrameworkUtil.getRanges(context,
				ATTRIBUTE_REQUESTED_CHANGE);

		HashMap rangeMap = new HashMap();

		StringList listChoices = new StringList();
		StringList listDispChoices = new StringList();

		String attrValue = "";
		String dispValue = "";

		boolean blnIsEC = false;
		boolean blnIsTBE = false;

		if (dom.isKindOf(context, DomainConstants.TYPE_ECO)
				|| dom.isKindOf(context, DomainConstants.TYPE_ECR)) {
			blnIsEC = true;

			// If the ECO is a "Team ECO" the Request For Change value
			// "For Obsolete" will not be displayed.
			String strPolicy = dom.getInfo(context,
					DomainConstants.SELECT_POLICY);
			String strPolicyClassification = FrameworkUtil
					.getPolicyClassification(context, strPolicy);
			if ("TeamCollaboration".equals(strPolicyClassification)) {
				blnIsTBE = true;
			}
		}

		for (int i = 0; i < strListRequestedChange.size(); i++) {
			attrValue = (String) strListRequestedChange.get(i);

			// For Update is not a Valid Option in EC.
			// Customised : Modified the loop to make sure the DECO has the
			// value for Update for Complete parts added as Affected Items
			if (blnIsEC
					&& attrValue.equals(RANGE_FOR_UPDATE)
					&& !dom.isKindOf(context, PropertyUtil.getSchemaProperty(
							context, "type_DECO"))) {
				continue;
			}

			// If the ECO is a "Team ECO" the Request For Change value
			// "For Obsolete" will not be displayed.
			if (blnIsTBE && attrValue.equals(RANGE_FOR_OBSOLETE)) {
				continue;
			}

			dispValue = i18nNow.getRangeI18NString(ATTRIBUTE_REQUESTED_CHANGE,
					attrValue, strLanguage);
			listDispChoices.add(dispValue);
			listChoices.add(attrValue);
		}

		rangeMap.put("field_choices", listChoices);
		rangeMap.put("field_display_choices", listDispChoices);

		return rangeMap;
	}

	/**
	 * Updates the Range Values for Attribute RequestedChange Based on User
	 * Selection
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object
	 * @param args
	 *            holds a HashMap containing the following entries: paramMap - a
	 *            HashMap containing the following keys,
	 *            "relId","RequestedChange"
	 * @return int
	 * @throws Exception
	 *             if operation fails
	 * @since Common X3
	 */
	public int updateRequestedChangeValues(Context context, String[] args)
			throws Exception {
		int intReturn = 0;
		String strLanguage = context.getSession().getLanguage();
		i18nNow i18nnow = new i18nNow();
		String strAlertMessage = EnoviaResourceBundle.getProperty(context,
				RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
				"emxComponents.Common.Alert.EditAll");
		String strAlertMessage2 = EnoviaResourceBundle.getProperty(context,
				RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),
				"emxComponents.Common.Alert.EditAllWithNewRevision");
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		HashMap requestMap = (HashMap) programMap.get("requestMap");
		String objId = (String) paramMap.get("objectId");
		String changeObjId = (String) requestMap.get("objectId");
		DomainObject domObj = new DomainObject(objId);
		String currentState = domObj.getInfo(context,
				DomainConstants.SELECT_CURRENT);
		String sAttRequestedChange = PropertyUtil.getSchemaProperty(context,
				"attribute_RequestedChange");
		String sRelId = (String) paramMap.get("relId");
		String strNewRequestedChangeValue = (String) paramMap
				.get(SELECT_NEW_VALUE);
		DomainRelationship domRelObj = new DomainRelationship(sRelId);
		strLanguage = (String) programMap.get("languageStr");
		Locale strLocale = context.getLocale();

		emxChange_mxJPO ecChange = new emxChange_mxJPO(context, args);

		if (currentState.equalsIgnoreCase(STATE_ECPART_RELEASE)
				|| currentState
						.equalsIgnoreCase(STATE_DEVELOPMENT_PART_COMPLETE)) {
			String[] inputArgs = new String[2];

			inputArgs[0] = objId;
			inputArgs[1] = changeObjId;
			String strNewPartId = (String) ecChange.getIndirectAffectedItems(
					context, inputArgs);
			if (domObj.isLastRevision(context) && strNewPartId == null) {
				if (strNewRequestedChangeValue
						.equalsIgnoreCase(EnoviaResourceBundle
								.getProperty(context,
										"emxFrameworkStringResource",
										strLocale,
										"emxFramework.Range.Requested_Change.For_Update"))
						|| strNewRequestedChangeValue
								.equalsIgnoreCase(EnoviaResourceBundle
										.getProperty(context,
												"emxFrameworkStringResource",
												strLocale,
												"emxFramework.Range.Requested_Change.For_Revise"))
						|| strNewRequestedChangeValue
								.equalsIgnoreCase(EnoviaResourceBundle
										.getProperty(context,
												"emxFrameworkStringResource",
												strLocale,
												"emxFramework.Range.Requested_Change.None"))
						|| strNewRequestedChangeValue
								.equalsIgnoreCase(EnoviaResourceBundle
										.getProperty(context,
												"emxFrameworkStringResource",
												strLocale,
												"emxFramework.Range.Requested_Change.For_Obsolescence"))) {
					domRelObj.setAttributeValue(context, sAttRequestedChange,
							strNewRequestedChangeValue);
					intReturn = 0;
				}
				if (strNewRequestedChangeValue
						.equalsIgnoreCase(EnoviaResourceBundle
								.getProperty(context,
										"emxFrameworkStringResource",
										strLocale,
										"emxFramework.Range.Requested_Change.For_Release"))) {
					emxContextUtil_mxJPO.mqlNotice(context, strAlertMessage);
					intReturn = 1;
				}
			} else {
				emxContextUtil_mxJPO.mqlNotice(context, strAlertMessage2);
				intReturn = 1;
			}
		}

		if (!(currentState.equalsIgnoreCase(STATE_ECPART_RELEASE))) {
			// if(strNewRequestedChangeValue.equalsIgnoreCase(i18nNow.getI18nString("emxFramework.Range.Requested_Change.For_Release","emxFrameworkStringResource",strLocale))||strNewRequestedChangeValue.equalsIgnoreCase(i18nNow.getI18nString("emxFramework.Range.Requested_Change.None","emxFrameworkStringResource",strLocale)))
			if (strNewRequestedChangeValue
					.equalsIgnoreCase(EnoviaResourceBundle.getProperty(context,
							"emxFrameworkStringResource", strLocale,
							"emxFramework.Range.Requested_Change.For_Release"))) {
				domRelObj.setAttributeValue(context, sAttRequestedChange,
						strNewRequestedChangeValue);
				intReturn = 0;
			}
			// if(strNewRequestedChangeValue.equalsIgnoreCase(i18nNow.getI18nString("emxFramework.Range.Requested_Change.For_Revise","emxFrameworkStringResource",strLocale))||strNewRequestedChangeValue.equalsIgnoreCase(i18nNow.getI18nString("emxFramework.Range.Requested_Change.For_Obsolescence","emxFrameworkStringResource",strLocale)))
			if (strNewRequestedChangeValue
					.equalsIgnoreCase(EnoviaResourceBundle.getProperty(context,
							"emxFrameworkStringResource", strLocale,
							"emxFramework.Range.Requested_Change.For_Revise"))
					|| strNewRequestedChangeValue
							.equalsIgnoreCase(EnoviaResourceBundle
									.getProperty(context,
											"emxFrameworkStringResource",
											strLocale,
											"emxFramework.Range.Requested_Change.For_Obsolescence"))
					|| strNewRequestedChangeValue
							.equalsIgnoreCase(EnoviaResourceBundle.getProperty(
									context, "emxFrameworkStringResource",
									strLocale,
									"emxFramework.Range.Requested_Change.None"))) {
				emxContextUtil_mxJPO.mqlNotice(context, strAlertMessage);
				intReturn = 1;
			}
		}

		return intReturn;
	}// end of method

	/**
	 * Query all EC Part or Dev Parts and print all Parts that don't have the
	 * same name as their Part Master or if they have no Part Master
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int checkPartName(Context context, String[] args) throws Exception {

		File file = new File(TARGET_FILE_FOLDER + context.getUser() + "_"
				+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date())
				+ "_REPORT_PM_MISMATCH_" + TARGET_FILE_SUFFIX);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("#User: " + context.getUser());
		bw.newLine();
		bw.write("#------------------Part and Part Master Mismatch---------------------------# ");
		bw.newLine();
		bw.newLine();

		try {
			ContextUtil.pushContext(context); // User Agent to have full access

			String strWhere = "policy == '"
					+ DomainObject.POLICY_DEVELOPMENT_PART + "' || policy == '"
					+ DomainObject.POLICY_EC_PART + "'";

			StringList slSelects = new StringList();
			slSelects.addElement(DomainObject.SELECT_ID);
			slSelects.addElement(DomainObject.SELECT_NAME);
			slSelects.addElement(DomainObject.SELECT_REVISION);
			slSelects.addElement("to[Part Revision].from.name");

			MapList mlPart = DomainObject.findObjects(context,
					DomainObject.TYPE_PART, // Type
					DomainObject.QUERY_WILDCARD, // name
					DomainObject.QUERY_WILDCARD, // revision
					DomainObject.QUERY_WILDCARD, // owner
					DomainObject.QUERY_WILDCARD, // Vault
					strWhere, // Where
					true, // expand sub type
					slSelects); // Selects
			if (null != mlPart) {
				int nbPart = mlPart.size();
				if (0 == nbPart) {
				} else {
					for (int i = 0; i < nbPart; i++) {
						Map mCurrentPR = (Map) mlPart.get(i);
						String strCurrentObjectID = (String) mCurrentPR
								.get(DomainObject.SELECT_ID);
						String strName = (String) mCurrentPR
								.get(DomainObject.SELECT_NAME);
						String strRevision = (String) mCurrentPR
								.get(DomainObject.SELECT_REVISION);
						String strPartMasterName = (String) mCurrentPR
								.get("to[" + RELATIONSHIP_PART_REVISION
										+ "].from.name");

						if (null == strPartMasterName
								|| 0 != strPartMasterName.compareTo(strName)) {

							bw.write("Part|" + strName + "|"
									+ strCurrentObjectID + "|" + "Part Mater|"
									+ strPartMasterName + " ");
							bw.newLine();

						}
					}
				}
			}

			return 0;
		} finally {
			// Close the File Writer
			if (null != bw) {
				bw.close();
			}
			ContextUtil.popContext(context);

		}
	}// end of method

	/**
	 * Query all part master to Check if they have the GPN Assigned and No Agile
	 * Connector Values
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int validateandMigratePartMaster(Context context, String[] args)
			throws Exception {

		File file = new File(TARGET_FILE_FOLDER + context.getUser() + "_"
				+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date())
				+ "_REPORT_PM_GPNMigration_" + TARGET_FILE_SUFFIX);
		if (!file.exists()) {
			file.createNewFile();
		}
		StringList slManuf = new StringList();
		slManuf.add("from[Manufacturer Equivalent].to.to[Manufacturing Responsibility].from.name");
		slManuf.add("from[Manufacturer Equivalent].to.name");
		slManuf.add(DomainConstants.SELECT_DESCRIPTION);

		StringList slSelects = new StringList();
		slSelects.addElement(DomainObject.SELECT_ID);
		slSelects.addElement(DomainObject.SELECT_NAME);
		slSelects.addElement(DomainObject.SELECT_REVISION);
		slSelects.addElement("interface[" + INTERFACE_googGPNRequest + "]");

		StringList selectList = new StringList("from[" + REL_PART_REVISION
				+ "].to.id");

		String sPartID = "";

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("#User: " + context.getUser());
		bw.newLine();
		bw.write("#------------------Part Master ---------------------------# ");
		bw.newLine();
		bw.newLine();
		bw.write("TYPE|NAME|ID|DESCRIPTION|MPN|MANUFACTURER");
		bw.newLine();

		try {
			ContextUtil.pushContext(context); // User Agent to have full access

			DomainObject dobPartMaster;

			// =================================================================
			// sample: 710-16127-01
			// =================================================================
			MapList mlPart = DomainObject.findObjects(context, "Part Master", // Type
					DomainObject.QUERY_WILDCARD, // name
					"Part", // revision
					DomainObject.QUERY_WILDCARD, // owner
					DomainObject.QUERY_WILDCARD, // Vault
					null, // Where
					true, // expand sub type
					slSelects); // Selects

			if (null != mlPart) {
				int nbPart = mlPart.size();
				if (0 == nbPart) {
				} else {
					StringBuilder sbpartMasterDetails;
					for (int i = 0; i < nbPart; i++) {
						sbpartMasterDetails = new StringBuilder();

						Map mCurrentPR = (Map) mlPart.get(i);

						String strCurrentObjectID = (String) mCurrentPR
								.get(DomainObject.SELECT_ID);
						String strName = (String) mCurrentPR
								.get(DomainObject.SELECT_NAME);
						String strRevision = (String) mCurrentPR
								.get(DomainObject.SELECT_REVISION);
						String strHasInterfaceGPNR = (String) mCurrentPR
								.get("interface[" + INTERFACE_googGPNRequest
										+ "]");

						dobPartMaster = DomainObject.newInstance(context,
								strCurrentObjectID);

						// =================================================================
						// Validate if GPN is assigned - have agile part
						// attribute?
						// =================================================================
						if (strName
								.matches(googConstants_mxJPO.GPN_REGEX_VALIDATION)) {
							sPartID = "";
							DomainObject dobPartID;

							if (!"TRUE".equalsIgnoreCase(strHasInterfaceGPNR)) {
								sbpartMasterDetails.append("Part Master|");
								sbpartMasterDetails.append(strName);
								sbpartMasterDetails.append("|");
								sbpartMasterDetails.append(strCurrentObjectID);
								// =================================================================
								//
								// =================================================================
								Map relMap = new DomainObject(
										strCurrentObjectID).getInfo(context,
										selectList);

								if (relMap.get("from[" + REL_PART_REVISION
										+ "].to.id") instanceof String) {
									sPartID = (String) relMap.get("from["
											+ REL_PART_REVISION + "].to.id");
								}

								dobPartID = DomainObject.newInstance(context,
										sPartID);

								String latestElementRevId = dobPartID
										.getLastRevision(context).getObjectId();

								MapList mlResults = DomainObject.getInfo(
										context,
										new String[] { latestElementRevId },
										slManuf);

								if (null != mlResults || !mlResults.isEmpty()) {

									Map mFirst = (Map) mlResults.get(0);

									String strManfuacturer = (String) mFirst
											.get("from[Manufacturer Equivalent].to.to[Manufacturing Responsibility].from.name");
									String strMPN = (String) mFirst
											.get("from[Manufacturer Equivalent].to.name");
									String sDesc = (String) mFirst
											.get(DomainConstants.SELECT_DESCRIPTION);
									// ===========================
									// Add Description
									// ===========================
									if (UIUtil.isNotNullAndNotEmpty(sDesc)) {
										sbpartMasterDetails.append("|");
										sbpartMasterDetails.append(sDesc);
										sbpartMasterDetails.append("|");
									} else {
										sbpartMasterDetails.append("||");
									}
									// ===========================
									// Add MPN and manufacturer
									// ===========================
									if (UIUtil.isNotNullAndNotEmpty(strMPN)
											&& UIUtil
													.isNotNullAndNotEmpty(strManfuacturer)) {
										sbpartMasterDetails.append(strMPN);
										sbpartMasterDetails.append("|");
										sbpartMasterDetails
												.append(strManfuacturer);
									} else {

										sbpartMasterDetails.append("|");

									}
								}

							}

						}
						// ===========================
						// Write Data to File
						// ===========================
						if (UIUtil.isNotNullAndNotEmpty(sbpartMasterDetails
								.toString())) {
							bw.write(sbpartMasterDetails.toString());
							bw.newLine();
						}
					}
				}
			}

			return 0;
		} finally {
			// Close the File Writer
			if (null != bw) {
				bw.close();
			}
			ContextUtil.popContext(context);
		}
	}// end of method

	/**
	 * This method will be used to create a batch Job to Send the Excel Sheet as
	 * attachment Classification Team - Club the data based on DRE and 1 excel
	 * for Each DRE. 1 mail for Each DRE - OTS - If no DRE then skip the Part in
	 * the excel sheet
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	public void createBJforClassficationReq(Context context, String[] args)
			throws Exception {
		Date dte = new Date();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
		String stringDate = simpleDateFormat.format(dte);

		Job job = new Job("googCustomFunctions",
				"processClassificationRequestBJ", args, true);

		job.setTitle("Send Request Classification Mails: " + stringDate);
		job.setActionOnCompletion("None");
		job.create(context);
		job.setOwner(context, context.getUser());
		job.setAllowreexecute("No");
		String sAttrPP = PropertyUtil.getSchemaProperty(context,
				"attribute_ProgressPercent");

		job.setAttributeValue(context, sAttrPP, "0");
		// TODO: make the Code to Change the Attribute value to set the start
		// date
		// job.setAttributeValue(context, "Start Date", "2/17/2017 6:45:00 PM");
		job.setStartDate(context);
		job.submit(context);

	}

	/**
	 * This method will be used to create a batch Job to Send the Excel Sheet as
	 * attachment Classification Team - Club the data based on DRE and 1 excel
	 * for Each DRE. 1 mail for Each DRE - OTS - If no DRE then skip the Part in
	 * the excel sheet
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return nothing
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	public void processClassificationRequestBJ(Context context, String[] args)
			throws Exception {
		// ==============================================================
		// Query Parts with CR Raised and In Process
		// ==============================================================

		StringList selects = new StringList();
		selects.addElement(DomainConstants.SELECT_TYPE);
		selects.addElement(DomainConstants.SELECT_NAME);
		selects.addElement(DomainConstants.SELECT_REVISION);
		selects.addElement(DomainConstants.SELECT_ID);
		selects.addElement(DomainConstants.SELECT_DESCRIPTION);
		selects.addElement("to[Part Revision].from.id");

		List lAttributeforVerify = (List) getTokenizedProperties(context,
				"emxEngineeringCentral.GoogleCustomReport.BOMCompleteCheck.Attributes");

		for (int i = 0; i < lAttributeforVerify.size(); i++) {
			selects.addElement("attribute["
					+ PropertyUtil.getSchemaProperty(context,
							lAttributeforVerify.get(i).toString().trim())
					+ "].value");
		}
		// get the Parts from Which Classification Request had been raised..
		MapList mplClassReqList = DomainObject
				.findObjects(context,
						DomainObject.TYPE_PART, // Type
						DomainObject.QUERY_WILDCARD, // name
						"01", // revision
						DomainObject.QUERY_WILDCARD, // owner
						DomainObject.QUERY_WILDCARD, // Vault
						"to[Part Revision].from.attribute[googClassificationRequestStatus].value == 'In Process' && latest == true",// Where
						true, // expand sub type
						selects);

		// return the Maplist ATTR_RESPONSIBLE_DESIGN_ENGINEER
		mplClassReqList.addSortKey(DomainConstants.SELECT_NAME, "ascending",
				"String");
		mplClassReqList.sort();

		if (mplClassReqList != null) {
			// Call the Process Classification method
			processClassificationRequest(context, mplClassReqList);
		}

	}// end of method processClassificationRequestBJ

	/*
	 * This method will group the Classification Request for DRE based send it
	 * through a batch Job
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return nothing
	 * 
	 * @throws Exception if the operation fails.
	 */
	public void processClassificationRequest(Context context, MapList mplCR)
			throws Exception {
		// ===============================================================
		// This method will create the final Maplist based on the
		// DRE Values Associated to the part Objects and send mail
		// ===============================================================
		List<String> lsDRENames = new ArrayList<String>();
		HashMap hmptemp = new HashMap();

		for (int i = 0; i < mplCR.size(); i++) {
			// For the Unique list of DRE to process the Classification Request
			hmptemp = (HashMap) mplCR.get(i);
			String sDRE = (String) hmptemp.get("attribute["
					+ ATTR_RESPONSIBLE_DESIGN_ENGINEER + "].value");
			if (!lsDRENames.contains(sDRE)) {
				lsDRENames.add(sDRE);
			}
		}

		// =======================================================================
		// Iterate through the DRE list
		// Create a CSV File for Each DRE and Add the parts to the Sheet
		// =======================================================================
		// TODO: Read this from Propoerties File
		String sToList = "googClassificationUser1,googClassificationUser2";

		String[] toList = sToList.split(",", -1);

		String[] CCList = new String[] { "tlangevin" };

		for (int j = 0; j < lsDRENames.size(); j++) {
			// For Each DRE will iterate throght the maplist to find the parts
			_hmParts = new HashMap<String, Element>();
			_hmDataParts = new HashMap<String, HashMap<String, String>>();

			for (int k = 0; k < mplCR.size(); k++) {
				// form the base list
				HashMap hmptempCR = (HashMap) mplCR.get(k);
				String sDRE = (String) hmptempCR.get("attribute["
						+ ATTR_RESPONSIBLE_DESIGN_ENGINEER + "].value");

				if (lsDRENames.get(j).toString().equalsIgnoreCase(sDRE)) {
					// Add this part to the CSV List
					addPartsInfo(context, hmptempCR);
				}
			}
			// ========================================================================
			// Adding the File to Temp Folder
			// ========================================================================
			String strWorkspace = context.createWorkspace();
			String strFilename = lsDRENames.get(j).toString()
					+ "_"
					+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss")
							.format(new Date()) + ".csv";
			String CSV_OUT_PATH = TARGET_FILE_FOLDER + strFilename;

			// generate the CSV File
			generateCSVFile(CSV_OUT_PATH);
			// Prepare the CC List : This would be the DRE associated to the
			// Part
			// String[] CCList= new String[]{lsDRENames.get(j).toString()};

			// Mail the File Generated to the Classification Team and DRE as CC
			sendEMailToUser(context, CSV_OUT_PATH, toList, CCList,
					getSubjectLine(context, lsDRENames.get(j).toString()),
					getMailBody(lsDRENames.get(j).toString()));

		}// end of for loop

	}// end of method

	/*
	 * This method will Prepare Header for the File
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return String
	 * 
	 * @throws Exception if the operation fails.
	 */
	public String getSubjectLine(Context context, String sDREName)
			throws Exception {
		// =============================================
		// Prepare the Subject Line for the mail
		// =============================================
		Date dte = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
		String stringDate = simpleDateFormat.format(dte);
		// TODO: Read this from Propoerties File
		String sSubject = "Classification Request Raised By : " + sDREName
				+ " on " + stringDate + " ";

		return sSubject;

	}// end of method

	/*
	 * This method will Prepare Header for the File
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return StringBuilder
	 * 
	 * @throws Exception if the operation fails.
	 */

	public StringBuilder getMailBody(String sDREName) throws Exception {
		// =============================================
		// Prepare the mail Body/Message
		// =============================================
		StringBuilder sbMailMessage = new StringBuilder();
		// TODO: Read this from Propoerties File
		sbMailMessage.append("Greetings Classification Team,");
		sbMailMessage.append("\n");
		sbMailMessage.append("\n");
		sbMailMessage
				.append("Please refer the attached File for Parts which have been assigned for Waymo by "
						+ sDREName
						+ ". Please help to classify the requested Parts ");
		sbMailMessage.append("\n");
		sbMailMessage.append("\n");
		sbMailMessage.append("Thanks and Regards, ");
		sbMailMessage.append("\n");
		sbMailMessage.append("PLM Team");

		// return the message
		return sbMailMessage;
	}// end of method

	/*
	 * This method will Add the Parts to the HashMap from Writing in CSV
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return nothing
	 * 
	 * @throws Exception if the operation fails.
	 */
	public void addPartsInfo(Context context, HashMap hmpPart) throws Exception {

		// ==================================================
		// Process the parts Info
		// ===================================================

		String sPartId = (String) hmpPart.get(DomainConstants.SELECT_ID);

		String sPartMasterID = (String) hmpPart
				.get("to[Part Revision].from.id");

		DomainObject dobPartMaster = DomainObject.newInstance(context,
				sPartMasterID);
		// ==================================================
		// Getting Manufacturer info
		// ==================================================
		StringList slManuf = new StringList();
		slManuf.add("from[Manufacturer Equivalent].to.to[Manufacturing Responsibility].from.name");
		slManuf.add("from[Manufacturer Equivalent].to.name");
		slManuf.add("from[Manufacturer Equivalent].to.description");

		String strManfuacturer = "";
		String strMPN = "";
		String strMPNDesc = "";
		MapList mlManufacurterInfo = DomainObject.getInfo(context,
				new String[] { sPartId }, slManuf);

		if (null != mlManufacurterInfo || !mlManufacurterInfo.isEmpty()) {
			Map mFirst = (Map) mlManufacurterInfo.get(0);
			strManfuacturer = (String) mFirst
					.get("from[Manufacturer Equivalent].to.to[Manufacturing Responsibility].from.name");
			strMPN = (String) mFirst
					.get("from[Manufacturer Equivalent].to.name");
			strMPNDesc = (String) mFirst
					.get("from[Manufacturer Equivalent].to.description");
		}

		HashMap<String, String> hmpdata = new LinkedHashMap<String, String>();

		_hmDataParts.put(sPartId, hmpdata);

		Map hmpPMAttribute = (HashMap) dobPartMaster.getAttributeMap(context);

		// ====================
		// Timestamp
		// ====================
		hmpdata.put(XML_TAG_NODE1,
				hmpPMAttribute.get(ATTRIBUTE_CLASSIFICATION_REQUESTED_DATE)
						.toString());

		// ====================
		// Username/Requestor
		// ====================
		hmpdata.put(XML_TAG_NODE2,
				hmpPMAttribute.get(ATTRIBUTE_CLASSIFICATION_REQUESTOR)
						.toString());

		// ====================
		// Ticket
		// ====================
		hmpdata.put(XML_TAG_NODE3, "");

		// ====================
		// Priority
		// ====================
		hmpdata.put(XML_TAG_NODE4, (String) hmpPMAttribute
				.get(ATTRIBUTE_CLASSIFICATION_REQ_PRIORITY));

		// ====================
		// Product
		// ====================
		hmpdata.put(XML_TAG_NODE5, "");

		// ====================
		// Part Type/Commodity Code
		// ====================
		String strGPT = (String) hmpPart.get(DomainObject
				.getAttributeSelect(googConstants_mxJPO.ATTR_GOOGLE_PART_TYPE));

		hmpdata.put(XML_TAG_NODE6, strGPT);

		// ====================
		// Does this part require CAD
		// ====================
		hmpdata.put(XML_TAG_NODE7, "");

		// ====================
		// Part Number/GPN
		// ====================
		hmpdata.put(XML_TAG_NODE8,
				(String) hmpPart.get(DomainConstants.SELECT_NAME));

		// ====================
		// Description
		// ====================
		hmpdata.put(XML_TAG_NODE9,
				hmpPart.get(DomainConstants.SELECT_DESCRIPTION).toString());

		// ====================
		// Manufacturer
		// ====================
		hmpdata.put(XML_TAG_NODE10, strManfuacturer);

		// ====================
		// Manufacturer P/N
		// ====================
		hmpdata.put(XML_TAG_NODE11, strMPN);

		// ====================
		// Paste link to datasheets
		// ====================
		hmpdata.put(XML_TAG_NODE12, "");

		// ====================
		// Comments
		// ====================
		hmpdata.put(XML_TAG_NODE13, "");

		// ====================
		// Shipping or Receiving Overseas (Classification/Valuation)
		// ====================
		hmpdata.put(XML_TAG_NODE14, "");

		// ====================
		// Date Required
		// ====================
		hmpdata.put(XML_TAG_NODE15, hmpPMAttribute.get(ATTRIBUTE_CLASS_REQDATE)
				.toString());

		// ====================
		// Cost Center
		// ====================
		hmpdata.put(XML_TAG_NODE16, "");

		// ====================
		// Functionality
		// ====================
		hmpdata.put(XML_TAG_NODE17,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_FUNCTIONALITY));

		// ====================
		// Material / Composition
		// ====================
		hmpdata.put(XML_TAG_NODE18,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_MATERIAL));

		// ====================
		// Imported as a complete assembly
		// ====================
		hmpdata.put(XML_TAG_NODE19,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_IMPORTEDASASSEMBLY));

		// ====================
		// The type of machine/equipment the GPN is designed for
		// ====================
		hmpdata.put(XML_TAG_NODE20,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_MACHINEDEF));

		// ====================
		// Does this item include encryption or call on external encryption
		// functionality?
		// ====================
		hmpdata.put(XML_TAG_NODE21,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_ENCRYPTREQ));

		// ====================
		// If the item includes encryption or calls on external encryption
		// functionality, is this Google technology,
		// or something that was manufactured, customized or modified in any way
		// specially for Google?
		// ====================
		hmpdata.put(XML_TAG_NODE22, (String) hmpPMAttribute
				.get(ATTRIBUTE_CLASS_ISENCRYPGOOGLESPECIFIC));

		// ====================
		// Requester Comments
		// ====================
		hmpdata.put(XML_TAG_NODE23,
				(String) hmpPMAttribute.get(ATTRIBUTE_CLASS_REQCOMMENTS));

		// ====================
		// Classification Ticket
		// ====================
		hmpdata.put(XML_TAG_NODE24, "");

	}// end of method

	/*
	 * This method will generate CSV File based on the Inputs provided
	 * 
	 * @param context the eMatrix <code>Context</code> object.
	 * 
	 * @param args contains a packed HashMap with the following entries:
	 * 
	 * @return nothing
	 * 
	 * @throws Exception if the operation fails.
	 */

	private void generateCSVFile(final String strFileOutputPath)
			throws Exception {

		File file = new File(strFileOutputPath);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		try {
			List<String> lsStringColumnTitles = null; // Keep same order

			for (Entry<String, HashMap<String, String>> entry : _hmDataParts
					.entrySet()) {

				HashMap<String, String> hmCurrentValue = entry.getValue();

				Collection<?> keys = hmCurrentValue.keySet();

				boolean bFirst = true;
				// TODO: based on the entry set iterate the Values:
				if (null == lsStringColumnTitles) {

					lsStringColumnTitles = new ArrayList<String>();

					for (Object key : keys) {
						if (!bFirst) {
							bw.write(",");
						}

						bw.write("\"" + key.toString() + "\"");
						lsStringColumnTitles.add(key.toString());

						bFirst = false;

					}

					bw.newLine();
				}

				bFirst = true;

				for (Object key : keys) {
					String strCurrentColumnValue = hmCurrentValue.get(key);
					if (!bFirst) {
						bw.write(",");
					}

					bw.write("\"");
					if (null != strCurrentColumnValue) {
						bw.write(strCurrentColumnValue);
					}
					bw.write("\"");

					bFirst = false;
				}

				bw.newLine();
			}
		} finally {
			bw.close();
		}
	}// end of method to write in CSV

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

	public void sendEMailToUser(Context context, String sAbsolutepath,
			String[] toList, String[] CCList, String sSubject,
			StringBuilder sbBody) throws Exception {
		try {
			// get the host
			String host = PropertyUtil.getEnvironmentProperty(context,
					"MX_SMTP_HOST");

			// get the from user: Using the Super User Context to send the
			// mail(BackGround Job)
			String from = PersonUtil
					.getEmail(context, PropertyUtil.getSchemaProperty(context,
							"person_UserAgent"));
			// TODO: Change this later, currently Hard Coded this to make sure
			// User Agent Mail is not Waymo but google
			from = "jackbbrown@google.com";

			// Prepare the To User List
			Address[] mailto = new Address[toList.length];

			for (int i = 0; i < toList.length; i++) {
				// sending to multiple person
				String sPersonEmail = MqlUtil.mqlCommand(context,
						"print person '$1' select $2 dump",
						toList[i].toString(), "email");
				mailto[i] = new InternetAddress(sPersonEmail);
			}

			// Prepare the CC User List
			Address[] cc = new Address[CCList.length];

			for (int j = 0; j < CCList.length; j++) {
				// sending to multiple person
				cc[j] = new InternetAddress(PersonUtil.getEmail(context,
						CCList[j].toString()));
			}

			// get the attachment file
			String fileAttachment = sAbsolutepath; // Complete Path
			String strPersonal = PersonUtil
					.getEmail(context, PropertyUtil.getSchemaProperty(context,
							"person_UserAgent")); // From User
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
			// Add CC list
			// Commented the CC to make Sure changes as discussed on 05-30-2017
			// can be incorporated, by Sharad
			// TODO: Make Changes to the CC List
			message.addRecipients(Message.RecipientType.CC, cc);

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

			if (fileAttachment.indexOf("/") != -1) {

				fileName = fileAttachment.substring(
						fileAttachment.lastIndexOf("/") + 1,
						fileAttachment.length());

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

	/**
	 * Query and create a Report for the Physical Product and all its children
	 * with connected parts, and add all the project space to them.
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public void getPRDCollabspace(Context context, String[] args)
			throws FrameworkException {

		if (2 != args.length) {
		} else {
			// ========================================================
			// Create the file parameter
			// ========================================================
			File file = new File(
					TARGET_FILE_FOLDER
							+ context.getUser()
							+ "_"
							+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss")
									.format(new Date()) + "_REPORT_"
							+ args[0].toString() + "_COLLABSPACE"
							+ TARGET_FILE_SUFFIX);
			StringBuilder sbpartInfo = new StringBuilder();

			StringList slSelects = new StringList();
			slSelects.addElement(DomainObject.SELECT_ID);
			slSelects.addElement(DomainObject.SELECT_NAME);
			slSelects.addElement(DomainObject.SELECT_TYPE);
			slSelects.addElement(DomainObject.SELECT_REVISION);
			slSelects.addElement(SELECT_COLLAB_SPACE);
			slSelects.add("to[Part Specification].from.id");
			slSelects.add("to[Part Specification].from.name");
			slSelects.add("to[Part Specification].from.revision");
			slSelects.add("to[Part Specification].from.type");
			slSelects.add("to[Part Specification].from.project");

			// ====================
			// Get VPMReferences
			// ====================
			MapList mlVPMReferences = DomainObject.findObjects(context,
					TYPE_VPMREFERENCE, // Type
					args[0], // name
					args[1], // revision
					DomainObject.QUERY_WILDCARD, // owner
					DomainObject.QUERY_WILDCARD, // Vault
					DomainObject.EMPTY_STRING, // Where
					true, // expand
					slSelects); // Selects

			if (null == mlVPMReferences || 0 == mlVPMReferences.size()) {
				return;
			}

			Map mCurrent = (Map) mlVPMReferences.get(0);
			// =========================================
			// This will be the parent PRD details
			// =========================================
			String strObjectId = (String) mCurrent.get(DomainObject.SELECT_ID);
			sbpartInfo.append((String) mCurrent.get(DomainObject.SELECT_TYPE));
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent.get(DomainObject.SELECT_NAME));
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent
					.get(DomainObject.SELECT_REVISION));
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent.get(SELECT_COLLAB_SPACE));
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent
					.get("to[Part Specification].from.name"));
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent
					.get("to[Part Specification].from.revision"));
			sbpartInfo.append("|");
			sbpartInfo.append("|");
			sbpartInfo.append((String) mCurrent
					.get("to[Part Specification].from.project"));
			sbpartInfo.append("|");
			// =========================================
			//
			// =========================================
			DomainObject dobprdobj = DomainObject.newInstance(context,
					strObjectId);
			StringList slPartselect = new StringList();
			slPartselect.add(DomainConstants.SELECT_ID);
			slPartselect.add(DomainConstants.SELECT_TYPE);
			slPartselect.add(DomainConstants.SELECT_NAME);
			slPartselect.add(SELECT_PROJECT);
			// part details
			slPartselect.add("to[Part Specification].from.id");
			slPartselect.add("to[Part Specification].from.name");
			slPartselect.add("to[Part Specification].from.type");
			slPartselect.add("to[Part Specification].from.project");

			slPartselect.add("to[Part Specification].from.to["
					+ RELATIONSHIP_PART_REVISION + "].from.name");
			slPartselect.add("to[Part Specification].from.to["
					+ RELATIONSHIP_PART_REVISION + "].from.type");
			slPartselect.add("to[Part Specification].from.to["
					+ RELATIONSHIP_PART_REVISION + "].from.project");

			// part Master details
			StringList slPMselect = new StringList();
			slPMselect.add("from[" + RELATIONSHIP_PART_SPECIFICATION + "].to["
					+ RELATIONSHIP_PART_REVISION + "].from.name");
			slPMselect.add("from[" + RELATIONSHIP_PART_SPECIFICATION + "].to["
					+ RELATIONSHIP_PART_REVISION + "].from.project");
			slPMselect.add("from[" + RELATIONSHIP_PART_SPECIFICATION + "].to["
					+ RELATIONSHIP_PART_REVISION + "].from.id");

			MapList mlPhysicalProduct = dobprdobj.getRelatedObjects(context, // Context
					RELATIONSHIP_VPMINSTANCE, // Relationship Pattern
					TYPE_VPMREFERENCE, // Type Pattern
					slSelects, // Bus Selects
					new StringList(), // Rel Selects
					true, // Get To
					true, // Get From
					(short) 0, // Recurse Level - set to all right now
					DomainConstants.EMPTY_STRING, // Bus Where
					DomainConstants.EMPTY_STRING, // Rel Where
					0); // Max Limit

			HashMap<String, HashMap<String, String>> hmPhysicalProducts = new HashMap<String, HashMap<String, String>>();
			if (null != mlPhysicalProduct) {
				DomainObject dobvpm = null;
				for (int i = 0; i < mlPhysicalProduct.size(); i++) {
					Map mprdchild = (Map) mlPhysicalProduct.get(i);

					String strCurrentId = (String) mprdchild
							.get(DomainObject.SELECT_ID);
					dobvpm = DomainObject.newInstance(context, strCurrentId);
					MapList mlPart = dobprdobj.getRelatedObjects(context, // Context
							"Part Specification", // Relationship Pattern
							"Part", // Type Pattern
							slPartselect, // Bus Selects
							new StringList(), // Rel Selects
							true, // Get To
							false, // Get From
							(short) 0, // Recurse Level - set to all right now
							DomainConstants.EMPTY_STRING, // Bus Where
							DomainConstants.EMPTY_STRING, // Rel Where
							0);
				}
			}
			try {
				generateLogFile(context, sbpartInfo, file);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}// end of else loop

	}// end of Method getPRDCollabspace

	/**
	 * Query all part master to Check if they have the GPN Assigned and No Agile
	 * Connector Values
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @return
	 * @throws Exception
	 */
	public void generateLogFile(Context context, StringBuilder sbString,
			File file) throws Exception {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		try {

			// ==================LOG File Writing============================\\
			bw.write("PHYSICAL PRODUCT|NAME|REVISION|PROJECT|PART NAME|REVISION|COLLABSPACE|PART MASTER|COLLABSPACE");
			bw.newLine();
			bw.write(sbString.toString());
			bw.newLine();
		} finally {
			// Close the File Writer
			if (null != bw) {
				bw.close();
			}
		}

	}

	/**
	 * This method will be used to create a batch Job to Send the Excel Sheet as
	 * attachment Classification Team - Club the data based on DRE and 1 excel
	 * for Each DRE. 1 mail for Each DRE - OTS - If no DRE then skip the Part in
	 * the excel sheet
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */
	public void createBJforBOMStructure(Context context, String[] args)
			throws Exception {
		Date dte = new Date();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
		String stringDate = simpleDateFormat.format(dte);

		Job job = new Job("googCustomFunctions", "getPartBOMStructure", args,
				true);

		job.setTitle("Create EBOM Report and eMail: " + stringDate);
		job.setActionOnCompletion("None");
		job.create(context);
		job.setOwner(context, context.getUser());
		job.setAllowreexecute("No");
		String sAttrPP = PropertyUtil.getSchemaProperty(context,
				"attribute_ProgressPercent");

		job.setAttributeValue(context, sAttrPP, "0");
		job.setStartDate(context);
		job.submit(context);

	}

	/**
	 * Query and create a Report for the Physical Product and all its children
	 * with connected parts, and add all the project space to them.
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public void getPartBOMStructure(Context context, String[] args)
			throws FrameworkException {
		// ==================================================
		// Args :
		// 1) GPN/Part Number
		// 2) Revision for the Part
		// 3) Level to which Expansion has to happen
		// ==================================================
		if (3 != args.length) {
		} else {
			try {
				String sExpLevel = args[2].toString();
				int iExpLevel = 0;
				if (UIUtil.isNotNullAndNotEmpty(sExpLevel)) {
					iExpLevel = Integer.parseInt(sExpLevel);
				}
				MQLCommand mqlCommand = new MQLCommand();
				mqlCommand.executeCommand(context,
						"print bus Part $1 $2 select $3 dump $4",
						args[0].toString(), args[1].toString(), "id", "|");
				String sObjectId = mqlCommand.getResult().trim();
				if (null == sObjectId) {
					return;
				}
				// ========================================================
				// Create the file parameter
				// ========================================================
				File file = new File(
						TARGET_FILE_FOLDER
								+ context.getUser()
								+ "_"
								+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss")
										.format(new Date()) + "_EBOM_"
								+ args[0].toString() + TARGET_FILE_SUFFIX_CSV);
				StringBuilder sbpartInfo = new StringBuilder();

				// Header Inputs for the Report

				StringList slSelects = new StringList();
				StringList selectRelStmts = new StringList();

				// Bus Attributes
				slSelects.addElement(DomainConstants.SELECT_ID);
				slSelects.addElement(DomainConstants.SELECT_TYPE);
				slSelects.addElement(DomainConstants.SELECT_NAME);
				slSelects.addElement(DomainConstants.SELECT_REVISION);
				slSelects.addElement(DomainConstants.SELECT_DESCRIPTION);
				slSelects.addElement(EngineeringConstants.SELECT_END_ITEM);
				slSelects.addElement("attribute["
						+ googConstants_mxJPO.ATTRIBUTE_MAKE_BUY + "]");
				slSelects.addElement("attribute["
						+ googConstants_mxJPO.ATTRIBUTE_UNIT_OF_MEASURE + "]");
				slSelects.addElement("attribute[" + ATTR_DESIGN_PURCHASE + "]");
				slSelects.addElement("attribute["
						+ ATTR_RESPONSIBLE_DESIGN_ENGINEER + "].value");

				// Relationship Attributes
				selectRelStmts
						.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
				selectRelStmts.addElement(SELECT_ATTRIBUTE_QUANTITY);
				selectRelStmts.addElement(SELECT_ATTRIBUTE_FIND_NUMBER);

				// ====================
				// Get EBOM
				// ====================
				DomainObject partObj = DomainObject.newInstance(context,
						sObjectId);

				MapList ebomList = partObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_EBOM, // relationship
															// pattern
						DomainConstants.TYPE_PART, // object pattern
						slSelects, // object selects
						selectRelStmts, // relationship selects
						false, // to direction
						true, // from direction
						(short) iExpLevel, // recursion level
						null, null); // relationship where clause

				if (null == ebomList || 0 == ebomList.size()) {
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}// end of method

	/**
	 * getMyDeskTasks - gets the list of Tasks the user has access
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object
	 * @param args
	 *            holds the following input arguments: 0 - objectList MapList
	 * @returns Object
	 * @throws Exception
	 *             if the operation fails
	 * @since AEF Rossini
	 * @grade 0
	 */
	@com.matrixone.apps.framework.ui.ProgramCallable
	public MapList getConnectedRoutetask(Context context, String[] args)
			throws Exception {

		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			DomainObject routeObject = DomainObject.newInstance(context);

			DomainObject changeObject = DomainObject.newInstance(context);

			// HashMap hmParam = (HashMap) programMap.get("paramMap");
			String sObjectId = (String) programMap.get("objectId");
			changeObject.setId(sObjectId);

			StringList selectTypeStmts = new StringList();
			StringList selectRelStmts = new StringList();
			selectTypeStmts.add(routeObject.SELECT_NAME);
			selectTypeStmts.add(routeObject.SELECT_ID);
			selectTypeStmts.add(routeObject.SELECT_DESCRIPTION);
			selectTypeStmts.add(routeObject.SELECT_OWNER);
			selectTypeStmts.add(routeObject.SELECT_CURRENT);
			selectTypeStmts.add(strAttrRouteAction);
			selectTypeStmts.add(strAttrCompletionDate);
			selectTypeStmts.add(strAttrTaskCompletionDate);
			selectTypeStmts.add("attribute["
					+ DomainObject.ATTRIBUTE_ROUTE_INSTRUCTIONS + "]");
			selectTypeStmts.add(strAttrTitle);
			selectTypeStmts.add(objectIdSelectStr);
			selectTypeStmts.add(objectNameSelectStr);
			selectTypeStmts.add(routeIdSelectStr);
			selectTypeStmts.add(routeNameSelectStr);
			selectTypeStmts.add(routeOwnerSelectStr);

			Pattern relPattern = new Pattern(sRelProjectTask);
			relPattern.addPattern(sRelAssignedTask);
			relPattern.addPattern(sRelWorkflowTaskAssinee);

			Pattern typePattern = new Pattern(sTypeInboxTask);
			typePattern.addPattern(DomainObject.TYPE_TASK);

			SelectList selectStmts = new SelectList();

			String busWhere = null;

			MapList mlRouteObjects = changeObject.getRelatedObjects(context, // Context
					DomainConstants.RELATIONSHIP_OBJECT_ROUTE, // Relationship
																// Pattern
					DomainConstants.TYPE_ROUTE, // Type Pattern
					selectTypeStmts, // Bus Selects
					null, // Rel Selects
					false, // Get To
					true, // Get From
					(short) 1, // Recurse Level
					DomainConstants.EMPTY_STRING, // Bus Where
					DomainConstants.EMPTY_STRING, // Rel Where
					0);
			com.matrixone.apps.domain.util.MapList taskMapList = null;

			MapList finalTaskMapList = new MapList();

			if (null != mlRouteObjects && mlRouteObjects.size() > 0) {

				for (int i = 0; i < mlRouteObjects.size(); i++) {
					Map hmpRoute = (Map) mlRouteObjects.get(i);

					routeObject.setId(hmpRoute.get(routeObject.SELECT_ID)
							.toString());
					taskMapList = routeObject.getRelatedObjects(context, // Context
							DomainConstants.RELATIONSHIP_ROUTE_TASK, // Relationship
																		// Pattern
							typePattern.getPattern(), // Type Pattern
							selectTypeStmts, // Bus Selects
							null, // Rel Selects
							true, // Get To
							true, // Get From
							(short) 1, // Recurse Level
							DomainConstants.EMPTY_STRING, // Bus Where
							DomainConstants.EMPTY_STRING, // Rel Where
							0);
					if (null != taskMapList && taskMapList.size() > 0) {
						Iterator objectListItr = taskMapList.iterator();
						while (objectListItr.hasNext()) {
							Map objectMap = (Map) objectListItr.next();
							finalTaskMapList.add(objectMap);
						}

					}

				}

			}
			return finalTaskMapList;
		}

		catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * gets the Weight Estimate
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public Vector isEstimatedPrice(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		MapList mlObjList = (MapList) programMap.get("objectList");
		Vector vEstimates = new Vector();
		StringBuilder sbrAltPart;
		if (null != mlObjList) {
			StringList selectRelStmts = new StringList(1);
			selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);

			StringList selectStmts = new StringList();
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement("attribute[googPiecePriceDelta].value");
			int nbObjects = mlObjList.size();
			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
			}
			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					selectStmts);

			for (int i = 0; i < mlObjList.size(); i++) {
				Map mEstValue = (Map) mlResults.get(i);
				Object sval = mEstValue
						.get("attribute[googPiecePriceDelta].value");
				if (UIUtil.isNotNullAndNotEmpty(sval.toString())) {
					vEstimates.add("No");
				} else {
					vEstimates.add("Unassigned");
				}

			}
		}
		return vEstimates;
	}

	/**
	 * gets the Weight Estimate
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public Vector isEstimatedWeight(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);

		String sgoogWeighedMass = PropertyUtil.getSchemaProperty(context,
				"attribute_googWeighedMass");
		String sEstWeight = PropertyUtil.getSchemaProperty(context,
				"attribute_Weight");
		MapList mlObjList = (MapList) programMap.get("objectList");
		Vector vEstimates = new Vector();
		StringBuilder sbrAltPart;
		if (null != mlObjList) {
			StringList selectRelStmts = new StringList(1);
			selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);

			StringList selectStmts = new StringList();
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement("attribute[" + sgoogWeighedMass + "].value");
			selectStmts.addElement("attribute[" + sEstWeight + "].value");

			int nbObjects = mlObjList.size();
			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
			}

			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					selectStmts);

			double dbaseline = Double.parseDouble("0.0");
			double dWeighedMass;
			double dEstWeight;

			for (int i = 0; i < mlObjList.size(); i++) {
				Map mEstValue = (Map) mlResults.get(i);

				Object sval = mEstValue.get("attribute[" + sgoogWeighedMass
						+ "].value");
				Object sEstWeightval = mEstValue.get("attribute[" + sEstWeight
						+ "].value");
				if(UIUtil.isNullOrEmpty(sval.toString())){
					dWeighedMass  = Double.parseDouble("0.0");
				} else {
					dWeighedMass = Double.parseDouble(sval.toString());
				}
				if(UIUtil.isNullOrEmpty(sEstWeightval.toString())){
					dEstWeight  = Double.parseDouble("0.0");
				} else {				
					dEstWeight = Double.parseDouble(sEstWeightval.toString());
				}

				// if (UIUtil.isNotNullAndNotEmpty(sval.toString()) &&
				// !sval.toString().equalsIgnoreCase("0.0"))
				if (dWeighedMass > dbaseline) {
					vEstimates.add("Weighed");
				} else if (dEstWeight > dbaseline) {
					vEstimates.add("Estimated");
				} else {
					vEstimates.add("Unassigned");
				}

			}
		}
		return vEstimates;
	}

	/**
	 * gets the Column Values based on the Settings mentioned in the Column
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public Vector getColumnValuesbasedOnSettings(Context context, String[] args)
			throws Exception {
		// ====================================================================================
		// This method will help to get the related Objects Data in Context of
		// Object being
		// viewed
		// ====================================================================================
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		// get the Parammap
		HashMap parammap = (HashMap) programMap.get("paramList");
		// get the columnMap
		HashMap columnMap = (HashMap) programMap.get("columnMap");
		// get the settingsMap
		HashMap settingsMap = (HashMap) columnMap.get("settings");
		// get the Object id
		String sObjectid = (String) parammap.get("objectId");
		// get the Object list
		MapList mlObjList = (MapList) programMap.get("objectList");

		Vector _vattribute = new Vector();

		if (null != mlObjList) {
			StringList selectStmts = new StringList(); // bus select
			StringList selectrelStmts = new StringList(); // rel select
			String typepattern = DomainConstants.TYPE_PART; // Type Pattern

			// This would be used for the settings map
			// //googCustomSettingRelationShipName googCustomSettingAppliesTo,
			// googCustomSettingAdminType (Symbolic name),
			String sAppliesTo = (String) settingsMap
					.get("googCustomSettingAppliesTo"); // Wheher bus or Rel
			String sAdminType = (String) settingsMap
					.get("googCustomSettingAdminType"); // Symbolic name of
														// attribute

			// Made for Relationship
			String sdirection = (String) settingsMap
					.get("googCustomSettingRelationShipdirection"); // Direction
																	// if its to
																	// and From
			String sRelationshipName = (String) settingsMap
					.get("googCustomSettingRelationShipName"); // Symbolic name
																// of
																// Relationship

			// Direction
			boolean boolTo = sdirection.equalsIgnoreCase("TO") ? true : false; // when
																				// the
																				// objects
																				// are
																				// parent
			boolean boolFrom = sdirection.equalsIgnoreCase("FROM") ? true
					: false; // when the objects are children

			// bus select
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement(DomainConstants.SELECT_TYPE);
			if (UIUtil.isNotNullAndNotEmpty(sAppliesTo)
					&& !sAppliesTo.equalsIgnoreCase("Relationships")) {
				selectStmts.addElement("attribute["
						+ PropertyUtil.getSchemaProperty(sAdminType)
						+ "].value");
			}

			// rel select
			selectrelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
			if (UIUtil.isNotNullAndNotEmpty(sAppliesTo)
					&& sAppliesTo.equalsIgnoreCase("Relationships")) {
				selectrelStmts.addElement("attribute["
						+ PropertyUtil.getSchemaProperty(sAdminType)
						+ "].value");
			}

			// ============================================================================================
			// below code is to get the values for the bus objects for table -
			// if needed we can uncomment
			// commented now - Need to make sure the attribute set is
			// differentiated for Rel and Object set
			/*
			 * int nbObjects = mlObjList.size(); String[] arrayObjectId = new
			 * String[nbObjects]; for(int i=0; i<nbObjects; i++){ Map mCurrent =
			 * (Map) mlObjList.get(i); arrayObjectId[i] = (String)
			 * mCurrent.get(DomainConstants.SELECT_ID); }
			 * 
			 * MapList mlObjectResults = DomainObject.getInfo(context,
			 * arrayObjectId, selectStmts); for(int j=0;
			 * j<mlObjectResults.size(); j++){ Map mObjData = (Map)
			 * mlObjectResults.get(j); String sObjId =(String)
			 * mObjData.get(DomainConstants.SELECT_ID);
			 * 
			 * if (sObjId.equalsIgnoreCase((String)mObjData.get(DomainConstants.
			 * SELECT_ID))){
			 * 
			 * _vattribute.add((String) mData.get("attribute["+
			 * PropertyUtil.getSchemaProperty(sAdminType) + "].value"));
			 * 
			 * break; } }
			 */
			// ===========================================================================================

			// Expand the Object to get the Relationship affected items
			// attributes and its values
			DomainObject dobObj = DomainObject.newInstance(context, sObjectid); // This
																				// is
																				// the
																				// DECO
																				// Object
																				// Id

			MapList getRelatedData = dobObj.getRelatedObjects(context, // Context
					PropertyUtil.getSchemaProperty(sRelationshipName), // Relationship
																		// Pattern
					typepattern, // Type Pattern
					selectStmts, // Bus Selects
					selectrelStmts, // Rel Selects
					boolTo, // Get To
					boolFrom, // Get From
					(short) 1, // Recurse Level
					DomainConstants.EMPTY_STRING, // Bus Where
					DomainConstants.EMPTY_STRING, // Rel Where
					0);

			int nbObjects = mlObjList.size();
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);

				// Check with the Related Object Maplist and get the Values

				for (int j = 0; j < getRelatedData.size(); j++) {
					Map mData = (Map) getRelatedData.get(j);
					String sObjectId = (String) mData
							.get(DomainConstants.SELECT_ID);
					if (sObjectId.equalsIgnoreCase((String) mCurrent
							.get(DomainConstants.SELECT_ID))) {
						_vattribute.add((String) mData.get("attribute["
								+ PropertyUtil.getSchemaProperty(sAdminType)
								+ "].value"));
						break;
					}
				}
			}

		}
		return _vattribute;
	} // end of method

	/**
	 * gets the Topp level part Number for Affected Items
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public Vector geTopLvlpartforAffectedItems(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		MapList mlObjList = (MapList) programMap.get("objectList");
		Set sUniqueNamesAffectedItems = new HashSet();

		Vector vTopLevel = new Vector();
		StringBuilder sbrAltPart;
		if (null != mlObjList) {

			StringList selectStmts = new StringList();
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement("to[EBOM].from.name");
			selectStmts.addElement("attribute[End Item].value");

			int nbObjects = mlObjList.size();

			String[] arrayObjectId = new String[nbObjects];
			for (int i = 0; i < nbObjects; i++) {
				Map mCurrent = (Map) mlObjList.get(i);
				arrayObjectId[i] = (String) mCurrent
						.get(DomainConstants.SELECT_ID);
				sUniqueNamesAffectedItems.add((String) mCurrent
						.get(DomainConstants.SELECT_ID));
			}

			// We need to Check if the Current Item - End Item
			MapList mlResults = DomainObject.getInfo(context, arrayObjectId,
					selectStmts);
			for (int i = 0; i < arrayObjectId.length; i++) {

				String sTopLevelPart = findTopLevelpart(context,
						arrayObjectId[i].toString());
				vTopLevel.add(sTopLevelPart);
			}
		}
		return vTopLevel;
	}

	/**
	 * gets the Topp level part Number for Affected Items
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return Vector
	 * @throws Exception
	 *             if the operation fails.
	 * 
	 */

	public String findTopLevelpart(Context context, String sObjectID)
			throws Exception {
		StringList slselect = new StringList();
		slselect.add(DomainConstants.SELECT_NAME);
		String sTemp = "";
		DomainObject dobObj = null;
		// =========================================
		// Get The Top level part Number
		// =========================================
		dobObj = DomainObject.newInstance(context, sObjectID);
		StringList selectStmts = new StringList();
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement("to[EBOM].from.name");
		selectStmts.addElement("to[EBOM].from.attribute[End Item].value");
		selectStmts.addElement("to[EBOM].from.id");
		selectStmts.addElement("to[EBOM].from.to[EBOM].from.id");
		selectStmts.addElement("attribute[End Item].value");
		String isPartEndItem;
		String hasparentPart;
		Map mppartInfo = dobObj.getInfo(context, selectStmts);
		String sTOPlevelPart = "";
		String sImmediateParentAsTopLevel = "";
		String isParentPartEndItem = "";

		if (null != mppartInfo
				&& mppartInfo.get("to[EBOM].from.attribute[End Item].value") != null) {
			isPartEndItem = "";
			hasparentPart = "";

			isPartEndItem = (String) mppartInfo
					.get("attribute[End Item].value");
			isParentPartEndItem = (String) mppartInfo
					.get("to[EBOM].from.attribute[End Item].value");
			hasparentPart = (String) mppartInfo.get("to[EBOM].from.id");

			if (UIUtil.isNullOrEmpty((String) mppartInfo
					.get("to[EBOM].from.to[EBOM].from.id"))) {
				sImmediateParentAsTopLevel = (String) mppartInfo
						.get("to[EBOM].from.name");
			}
			// Validate if this is end item = Yes (Means not a TOP Level) call
			// the method again
			// Is the context part an End Item if yes then get the parent and
			// display,
			// if not then get the parent part and check it
			// If the part itself is a top level part then we need to return
			// blank
			if (!UIUtil.isNotNullAndNotEmpty(hasparentPart.toString())) {
				return "";
			}
			// If the part is End Item then we are returning the immediate
			// Parent
			// TODO : need to check for the nested End Items and Valiudate if
			// one call

			if (UIUtil.isNotNullAndNotEmpty(isPartEndItem.toString())
					&& isPartEndItem.toString().equalsIgnoreCase("Yes")) {
				// if the item is an End Item then it might be a case where the
				// immediate parent is a Top Level
				// Also add that part in the display, as we are returning nested
				// End Items
				sTOPlevelPart = getTopLevelpartwithEndItem(context, sObjectID,
						isPartEndItem, isParentPartEndItem);

				if (UIUtil.isNotNullAndNotEmpty(sImmediateParentAsTopLevel)
						&& UIUtil.isNotNullAndNotEmpty(sTOPlevelPart)) {
					sImmediateParentAsTopLevel = sImmediateParentAsTopLevel
							+ "|";
				}
				if (sTOPlevelPart.equalsIgnoreCase("No End Item")) {
					sTOPlevelPart = (String) mppartInfo
							.get("to[EBOM].from.name");
				}

				return sImmediateParentAsTopLevel + sTOPlevelPart;
			} else if ((isPartEndItem.toString().equalsIgnoreCase("No") || isPartEndItem
					.toString().equalsIgnoreCase("Unassigned"))
					&& isParentPartEndItem.toString().equalsIgnoreCase("Yes")) {
				// this was the loop to handle when the part itself is not a end
				// Item but pareent is
				sTOPlevelPart = (String) mppartInfo.get("to[EBOM].from.name");
				String stempdata = getTopLevelpartwithEndItem(context,
						sObjectID, isPartEndItem, isParentPartEndItem);
				if (stempdata.indexOf(sTOPlevelPart) != -1) {
					sTOPlevelPart = stempdata;
				} else {
					sTOPlevelPart = sTOPlevelPart + "|" + stempdata;
				}
			} else {

				// get the nested End Item Parts
				sTOPlevelPart = getTopLevelpartwithEndItem(context, sObjectID,
						isPartEndItem, isParentPartEndItem);
				// This could contain duplicate values inside
				if (UIUtil.isNotNullAndNotEmpty(sTOPlevelPart.toString())
						&& !sTOPlevelPart.equalsIgnoreCase("No End Item")) {
					return sTOPlevelPart;
				} else {
					// If the parent have no End Item Assigned then we return
					// the Immediate parent of the part
					sTOPlevelPart = (String) mppartInfo
							.get("to[EBOM].from.name");
				}
			}
		}// end of if statement

		return sTOPlevelPart;
	}

	public String getTopLevelpartwithEndItem(Context context, String sObjectID,
			String isctxPartEndItem, String isParentPartEndItem)
			throws Exception {
		// This method will be used for getting the Top Level part number based
		// on End Item
		// - if there is no parent part where end item is defined then, just
		// return the immediate parent.
		// else get the highest level of End Item and return immediate parent of
		// that
		Part partObj = new Part(sObjectID);
		StringList selectStmts = new StringList();
		StringList selectRelStmts = new StringList();
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_TYPE);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(DomainConstants.SELECT_REVISION);
		selectStmts.addElement(DomainConstants.SELECT_DESCRIPTION);
		selectStmts.addElement("attribute[End Item].value");

		selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_REFERENCE_DESIGNATOR);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_QUANTITY);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_FIND_NUMBER);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_COMPONENT_LOCATION);
		selectRelStmts.addElement(SELECT_ATTRIBUTE_USAGE);
		String sTopLevel = "";
		StringBuffer sObjWhereCond = new StringBuffer();
		int nExpandLevel = 0;

		ContextUtil.pushContext(context);
		MapList ebomparentList = partObj.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_EBOM, // relationship pattern
				DomainConstants.TYPE_PART, // object pattern
				selectStmts, // object selects
				selectRelStmts, // relationship selects
				true, // to direction
				false, // from direction
				(short) 0, // recursion level
				"", // object where clause
				null); // relationship where claus
		ContextUtil.popContext(context);

		Hashtable hmpTemp;
		HashMap hmpToplevel = new HashMap();
		String sFinalEndItem = "";
		StringBuffer sbTopLevel = new StringBuffer();
		ArrayList al = new ArrayList();

		String stempval = "";
		// Validate if the Maplist is not null
		if (ebomparentList != null && ebomparentList.size() > 0) {
			for (int i = 0; i < ebomparentList.size(); i++) {
				hmpTemp = (Hashtable) ebomparentList.get(i);

				String sParentLevel = (String) hmpTemp.get("level");

				if (!hmpToplevel.containsKey(sParentLevel)) {
					// Add to this map only if the attribute End Item is Yes
					if (hmpTemp.get("attribute[End Item].value").toString()
							.equalsIgnoreCase("Yes")) {

						hmpToplevel
								.put(sParentLevel,
										(String) hmpTemp
												.get(DomainConstants.SELECT_NAME)
												+ ";"
												+ (String) hmpTemp
														.get(DomainConstants.SELECT_ID));
					}

				} else {
					// This loop would mean that the part was used in another
					// assembly at the same level
					// get the value and replace item
					// Add to this map only if the attribute End Item is Yes
					if (hmpTemp.get("attribute[End Item].value").toString()
							.equalsIgnoreCase("Yes")) {
						stempval = hmpToplevel.get(sParentLevel).toString()
								+ ","
								+ (String) hmpTemp
										.get(DomainConstants.SELECT_NAME)
								+ ";"
								+ (String) hmpTemp
										.get(DomainConstants.SELECT_ID);
						hmpToplevel.remove(sParentLevel);
						// put it back again
						hmpToplevel.put(sParentLevel, stempval);
					}
				}

			}
		} else {

			sbTopLevel.append("No End Item");

		}
		if (hmpToplevel.size() > 0) {

			TreeMap<String, String> treeMap = new TreeMap<String, String>(
					hmpToplevel);

			// Get a set of the entries
			Set<Entry<String, String>> set = treeMap.entrySet();
			// Get an iterator
			Iterator it = set.iterator();

			// add the last entry of the map to the list
			al.add(treeMap.lastEntry().getValue().toString());

			String slevel = "";
			// Find through the list if there are multiple values in key and get
			// the highest level with multiple values
			// This could be a case where the highest level might have been used
			// in other Assembly
			while (it.hasNext()) {
				// Iterate through all the map
				Map.Entry me = (Map.Entry) it.next();
				if (me.getValue().toString().indexOf(",") > 0) {
					slevel = me.getKey().toString();
				}
			}// end of while loop
			if (UIUtil.isNotNullAndNotEmpty(slevel)) {
				if (Integer.parseInt(treeMap.lastEntry().getKey().toString()) > Integer
						.parseInt(slevel)) {
					// This is to make sure list does not contain Duplicate
					// levels
					al.add(treeMap.get(slevel));
				}

			}

			// ==========================================================
			// process the final End item
			// ==========================================================

			if (al.size() > 0) {
				Iterator<String> iterator = al.iterator();

				while (iterator.hasNext()) {

					sFinalEndItem = iterator.next().toString();

					if (sFinalEndItem.indexOf(",") != -1) {
						String[] stemp = sFinalEndItem.split(",");
						// To make sure if there are multiple parts

						for (String sFLEndItem : stemp) {
							if (sbTopLevel.length() > 0) {
								sbTopLevel.append("|");
							}
							sbTopLevel.append(getProcessedString(context,
									sFLEndItem, isctxPartEndItem,
									isParentPartEndItem));
						}

					} else {
						if (sbTopLevel.length() > 0) {
							sbTopLevel.append("|");
						}
						sbTopLevel.append(getProcessedString(context,
								sFinalEndItem, isctxPartEndItem,
								isParentPartEndItem));

					}

				}
			}
		} else {
			return "No End Item";
		}

		return sbTopLevel.toString();

	}// end of method

	/*
	 * This method will get the Processed String values for Display
	 */
	public String getProcessedString(Context context, String sEndItem,
			String isctxPartEndItem, String isParentPartEndItem)
			throws Exception {
		DomainObject dobparent = null;
		String sTPLVL = "";
		String[] stemp2 = sEndItem.split(";");
		String sObjId = stemp2[1].toString();

		if (isctxPartEndItem.equalsIgnoreCase("Yes")) {
			sTPLVL = stemp2[0].toString();
		} else if (isParentPartEndItem.equalsIgnoreCase("Yes")) {
			sTPLVL = stemp2[0].toString();
		} else {
			dobparent = DomainObject.newInstance(context, sObjId);
		}
		if (dobparent != null) {
			sTPLVL = (String) dobparent.getInfo(context, "to[EBOM].from.name");
			if (sTPLVL == null) {
				sTPLVL = "";
			}

		}// end of if loop
		return sTPLVL;
	}// end f ethod

	/**
	 * Query and create a Report for the Physical Product and all its children
	 * with connected parts, and add all the project space to them.
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int getPartRelatedSpecs(Context context, String[] args)
			throws Exception {
		// ==================================================
		// Args :
		// ==================================================
		if (4 != args.length) {
		} else {
			// ========================================================
			// Create the file parameter
			// ========================================================
			File file = new File(
					TARGET_FILE_FOLDER
							+ context.getUser()
							+ "_"
							+ new SimpleDateFormat("yyyyMMdd_HH-mm-ss")
									.format(new Date()) + "_"
							+ args[0].toString() + TARGET_FILE_SUFFIX_CSV);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("#User: " + context.getUser());
			bw.newLine();
			try {
				// validate the
				if (UIUtil.isNullOrEmpty(args[3].toString())) {
					return 1;
				}
				String sExpLevel = args[2].toString();
				// validate the expand level
				if (UIUtil.isNullOrEmpty(sExpLevel)) {
					return 1;
				}
				int iExpLevel = 0;
				if (UIUtil.isNotNullAndNotEmpty(sExpLevel)) {
					iExpLevel = Integer.parseInt(sExpLevel);
				}

				MQLCommand mqlCommand = new MQLCommand();
				mqlCommand.executeCommand(context,
						"print bus Part $1 $2 select $3 dump $4",
						args[0].toString(), args[1].toString(), "id", "|");
				String sObjectId = mqlCommand.getResult().trim();
				if (null == sObjectId) {
					return 1;
				}
				StringBuilder sbpartInfo = new StringBuilder();

				// Header Inputs for the Report
				StringBuilder sbr = new StringBuilder();
				// Header Info
				bw.write("Level,Type,Name,Revision," + args[3].toString()
						+ ":Name|Revision");
				bw.newLine();

				StringList slSelects = new StringList();
				StringList selectRelStmts = new StringList();

				// Bus Attributes
				slSelects.addElement(DomainConstants.SELECT_ID);
				slSelects.addElement(DomainConstants.SELECT_TYPE);
				slSelects.addElement(DomainConstants.SELECT_NAME);
				slSelects.addElement(DomainConstants.SELECT_REVISION);
				slSelects.addElement(DomainConstants.SELECT_DESCRIPTION);
				slSelects.addElement("from[Part Specification].to.id");

				StringList slObjSelects = new StringList();
				// Bus Attributes
				slObjSelects.addElement(DomainConstants.SELECT_TYPE);
				slObjSelects.addElement(DomainConstants.SELECT_NAME);
				slObjSelects.addElement(DomainConstants.SELECT_REVISION);

				// Relationship Attributes

				// ================================================================
				// Get CAD MODEL and DRAWING
				// ================================================================
				DomainObject partObj = DomainObject.newInstance(context,
						sObjectId);
				ContextUtil.pushContext(context);
				MapList relatedmapList = partObj
						.getRelatedObjects(
								context,
								DomainConstants.RELATIONSHIP_EBOM
										+ ","
										+ DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // relationship
																							// pattern
								DomainConstants.TYPE_PART, // object pattern
								slSelects, // object selects
								selectRelStmts, // relationship selects
								false, // to direction
								true, // from direction
								(short) iExpLevel, // recursion level
								null, null); // relationship where clause

				ContextUtil.popContext(context);
				if (null == relatedmapList || 0 == relatedmapList.size()) {
					return 1;
				} else {
					DomainObject dobSpec = null;
					// Add the part and put generate a CSV for that
					for (int i = 0; i < relatedmapList.size(); i++) {
						Map mCurrent = (Map) relatedmapList.get(i);
						sbpartInfo.append(mCurrent.get("level"));
						sbpartInfo.append(",");
						sbpartInfo.append(mCurrent
								.get(DomainConstants.SELECT_TYPE));
						sbpartInfo.append(",");
						sbpartInfo.append(mCurrent
								.get(DomainConstants.SELECT_NAME));
						sbpartInfo.append(",");
						sbpartInfo.append(mCurrent
								.get(DomainConstants.SELECT_REVISION));
						sbpartInfo.append(",");
						// get the Specs
						Object objValue = mCurrent
								.get("from[Part Specification].to.id");
						String sTempSpec = "";
						if (null != objValue) {
							String strValue = "";
							if (objValue instanceof StringList) {
								StringList slValues = (StringList) objValue;
								sTempSpec = "";
								int count = 0;
								for (int j = 0; j < slValues.size(); j++) {
									String strObjID = (String) slValues.get(j);
									if (null != strObjID && !strObjID.isEmpty()) {

										// Get the Info for the DomainObject
										dobSpec = DomainObject.newInstance(
												context, strObjID);
										Map mapPartInfo = (Map) dobSpec
												.getInfo(context, slObjSelects);
										if (mapPartInfo
												.get(DomainConstants.SELECT_TYPE)
												.toString()
												.equalsIgnoreCase(
														args[3].toString())) {
											if (count > 0) {
												sTempSpec = sTempSpec + "; ";
											}
											sTempSpec = sTempSpec
													+ mapPartInfo
															.get(DomainConstants.SELECT_NAME)
													+ "|"
													+ mapPartInfo
															.get(DomainConstants.SELECT_REVISION);
											count++;
										}
									}
								}
								sbpartInfo.append(sTempSpec);
							} else {
								strValue = (String) objValue;
								// Get the Info for the DomainObject
								dobSpec = DomainObject.newInstance(context,
										strValue);
								Map mapPartInfo = (Map) dobSpec.getInfo(
										context, slObjSelects);
								if (mapPartInfo
										.get(DomainConstants.SELECT_TYPE)
										.toString()
										.equalsIgnoreCase(args[3].toString())) {
									sTempSpec = mapPartInfo
											.get(DomainConstants.SELECT_NAME)
											+ "|"
											+ mapPartInfo
													.get(DomainConstants.SELECT_REVISION);
									sbpartInfo.append(sTempSpec);
								}
							}
						}// end of if for null
						sbpartInfo.append("\n");
					}// end of for loop
				}
				bw.write(sbpartInfo.toString());
			} finally {
				// Close the File Writer
				if (null != bw) {
					bw.close();
				}
			}
		}
		return 0;

	}// end of method

	// CA Summary View Page - Modified by Sara on 10/11/2017 - Start
	/**
	 * To display Affected Item Table values in CA Summary view page
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return MapList
	 * @throws Exception
	 *             if the operation fails.
	 */
	public MapList getChangeAffectedItems(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String sObjectId = (String) programMap.get("objectId");
		String sAdminType = (String) programMap.get("type");
		String sTypeName = PropertyUtil.getSchemaProperty(context, sAdminType);
		MapList sAffectedItemList = new MapList();
		
		if(sTypeName.equals("DECO")){
			 sAffectedItemList = new emxECO_mxJPO(context, args).getAffectedItems(context, args);
		} else if (sTypeName.equals("Change Action")) {
			sAffectedItemList = new enoECMChangeAction_mxJPO(context,args).getAffectedItems(context, args);
		} else {
		DomainObject doj = new DomainObject(sObjectId);
		StringList objectSelects = new StringList(SELECT_ID);
		StringList relSelects = new StringList(SELECT_RELATIONSHIP_ID);
		Pattern relPattern = new Pattern(REL_CHANGE_AFFECTED_ITEM);
        relPattern.addPattern(REL_AFFECTED_ITEM); 

		try {
			sAffectedItemList = doj.getRelatedObjects(context,
					relPattern.getPattern(), // relationship pattern
					TYPE_PART, // object pattern
					objectSelects, // object selects
					relSelects, // relationship selects
					false, // to direction
					true, // from direction
					(short) 1, // recursion level
					null, // object where clause
					null, 0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		}
		return sAffectedItemList;
	}
	// CA Summary View Page - Modified by Sara on 10/11/2017 - End

	// CA Content - Proposed Changes Page - Modified by Sara on 10/10/2017 -
	// Start
	/**
	 * To get Range Values
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return HashMap
	 * @throws Exception
	 *             if the operation fails.
	 */
	public HashMap populateRangeValues(Context context, String[] args)
			throws Exception {
		HashMap rangeMap = new HashMap();
		try {
			String strLanguage = context.getSession().getLanguage();
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap sColumnMap = (HashMap) programMap.get("columnMap");
			HashMap settings = (HashMap) sColumnMap.get("settings");
			String sAdminType = (String) settings.get("Admin Type");
			boolean isContains = false;
			StringList strListAttributeValues = FrameworkUtil.getRanges(
					context,
					PropertyUtil.getSchemaProperty(context, sAdminType));
			if (!strListAttributeValues.contains("")) {
				isContains = true;
			}
			if (strListAttributeValues.contains("Unassigned")
					|| strListAttributeValues.contains(" ")) {
				isContains = false;
			}
			if (isContains) {
				strListAttributeValues.add(0, " ");
			}
			StringList listChoices = new StringList();
			StringList listDispChoices = new StringList();
			listDispChoices.addAll(strListAttributeValues);
			listChoices.addAll(strListAttributeValues);
			rangeMap.put("field_choices", listChoices);
			rangeMap.put("field_display_choices", listDispChoices);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rangeMap;
	}

	/**
	 * To update the attribute values in the Affected Items table
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return void
	 * @throws Exception
	 *             if the operation fails.
	 */
	public void updateAttributeValue(Context context, String[] args)
			throws Exception {
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap sColumnMap = (HashMap) programMap.get("columnMap");
			HashMap settings = (HashMap) sColumnMap.get("settings");
			String sAdminType = (String) settings.get("Admin Type");
			String sAdminTypeSysmbolicName = PropertyUtil.getSchemaProperty(
					context, sAdminType);
			HashMap paramMap = (HashMap) programMap.get("paramMap");
			String sRelId = (String) paramMap.get("relId");
			String strNewValue = (String) paramMap
					.get(ChangeConstants.NEW_VALUE);
			DomainRelationship rel = new DomainRelationship(sRelId);
			rel.setAttributeValue(context, sAdminTypeSysmbolicName, strNewValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// CA Content - Proposed Changes Page - Modified by Sara on 10/10/2017 - End

	// CA Edit Page - Modified by Sara on 11/10/2017 - Start
	/**
	 * To exclude the parent name in search results
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return StringList
	 * @throws Exception
	 *             if the operation fails.
	 */

	public StringList excludeContextCA(Context context, String[] args)
			throws Exception {
		StringList excludeList = new StringList();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String sParentOID = (String) programMap.get("parentOID");
		excludeList.add(sParentOID);
		return excludeList;
	}

	// CA Edit Page - Modified by Sara on 11/10/2017 - End

	// EBOM - Add New Part Page - Modified by Sara on 24/10/2017 - Start
	/**
	 * To get the Range values for Design Collaboration attribute
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return HashMap
	 * @throws Exception
	 *             if the operation fails.
	 */
	public HashMap getDesignCollaborationValue(Context context, String[] args)
			throws Exception {
		HashMap rangeMap = new HashMap();

		try {
			String strLanguage = context.getSession().getLanguage();
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap srequestMap = (HashMap) programMap.get("requestMap");
			String sCreateMode = (String) srequestMap.get("createMode");
			HashMap sFieldMap = (HashMap) programMap.get("fieldMap");
			HashMap settings = (HashMap) sFieldMap.get("settings");
			String sAdminType = (String) settings.get("Admin Type");

			StringList strListAttributeValues = FrameworkUtil.getRanges(
					context,
					PropertyUtil.getSchemaProperty(context, sAdminType));

			StringList listChoices = new StringList();
			StringList listDispChoices = new StringList();
			//Commented for displaying default Design Collaboration  by Lalitha --starts
			/* if(UIUtil.isNotNullAndNotEmpty(sCreateMode)){
			if(sCreateMode.equals("EBOM")){
			  Collections.reverse(strListAttributeValues);
			}
			} */
			//Commented for displaying default Design Collaboration  by Lalitha --Ends
			listDispChoices.addAll(strListAttributeValues);
			listChoices.addAll(strListAttributeValues);
			rangeMap.put("field_choices", listChoices);
			rangeMap.put("field_display_choices", listDispChoices);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rangeMap;
	}
	
	/**
	 * To update Design Collaboration attribute in EBOM relationship
	 * 
	 * @param context
	 *            the eMatrix <code>Context</code> object.
	 * @param args
	 *            contains a packed HashMap with the following entries:
	 * @return void
	 * @throws Exception
	 *             if the operation fails.
	 */
	public void updateDCValue(Context context, String[] args)
			throws Exception {
		try { 
			String fromObjectId = args[0];
			String toObjectId = args[1];
			String sRelID = args[3];
			DomainObject doj = new DomainObject(toObjectId);
			String strisVPMVisibleValue = (String) doj.getInfo(context, "attribute[isVPMVisible]");
			DomainRelationship domRel = new DomainRelationship(sRelID);
			domRel.setAttributeValue(context, "isVPMVisible", strisVPMVisibleValue);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// EBOM - Add New Part Page - Modified by Sara on 24/10/2017 - End


	/* This method used to check access privileges for CA Edit/Cancel/Hold commands.
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 * To resolve issue(29597454) by Shajil on 20/11/2017
	 */
	public Boolean hasCAModifyAccess(Context context, String[] args)
			throws Exception {
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		boolean bHasModifyAccess = false;
		String role_GoogAdminUser = PropertyUtil
				.getSchemaProperty(context,"role_GoogAdminUser");
		String role_googPLMAnalyst = PropertyUtil
				.getSchemaProperty(context,"role_googPLMAnalyst");
		String objectId = (String) paramMap.get("objectId");
		StringList objectList = new StringList();
		objectList.addElement(SELECT_ID);
		objectList.addElement(SELECT_CURRENT);
		objectList.addElement(SELECT_OWNER);
		Map caInfo = DomainObject.newInstance(context, objectId).getInfo(
				context, objectList);
		String changeActionState = (String) caInfo.get(SELECT_CURRENT);
		String owner = (String) caInfo.get(SELECT_OWNER);
		if ((changeActionState.equals(STATE_CHANGEACTION_PREPARE) || changeActionState
				.equals(STATE_CHANGEACTION_INWORK))
				&& (owner.equals(context.getUser())
						|| context.isAssigned(role_GoogAdminUser) || context
							.isAssigned(role_googPLMAnalyst))) {
			bHasModifyAccess = true;
		} else if (changeActionState.equals(STATE_CHANGEACTION_INAPPROVAL) && context
							.isAssigned(role_googPLMAnalyst)) {
			bHasModifyAccess = true;
		//Modified for Edit in Complete State by Preethi Rajaraman -- Starts
		} else if (changeActionState.equals(STATE_CHANGEACTION_COMPLETE) && context
							.isAssigned(role_googPLMAnalyst)) {
			bHasModifyAccess = true;
		//Modified for Edit in Complete State by Preethi Rajaraman -- Ends
		}
		return bHasModifyAccess;
	}
	//end of method
	/**
	 * This method used to display CA and DECO object in EBOM table column.
	 * @param context
	 * @param args
	 * @return Vectot
	 * @throws Exception
	 * To resolve issue(29475709) by Shajil on 17/11/2017
	 */
	
	public Vector getChanges(Context context, String[] args) throws Exception{
		
		Vector changes = new Vector<>();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		// EBOM Page Export issue - Modified by Sara - 23/11/2017 - Start
		HashMap sParamMap = (HashMap) programMap.get("paramList");
		String sExportFormat = (String) sParamMap.get("exportFormat");
		// EBOM Page Export issue - Modified by Sara - 23/11/2017 - End
		
		MapList changeObjects = (MapList) programMap.get("objectList");
		Iterator itr = changeObjects.iterator();
		String objId = null;
		DomainObject partObj = null;
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.addElement(SELECT_NAME);
		
		while(itr.hasNext()){
			Map dataMap = (Map)itr.next();
			objId = (String)dataMap.get("id");
			partObj = new DomainObject(objId);
			MapList affectedObjects = getConnectedChanges(context, objId);
			Iterator changeItr = affectedObjects.iterator();
			StringBuilder htmlOut = new StringBuilder();
			while(changeItr.hasNext()){
				Map affectedData = (Map)changeItr.next();
				String itemName = (String)affectedData.get("name");
				// EBOM Page Export issue - Modified by Sara - 23/11/2017 - Start
				if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
					htmlOut.append(itemName);
					htmlOut.append("  ");
				} else {
				// EBOM Page Export issue - Modified by Sara - 23/11/2017 - End
				htmlOut.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?");
				htmlOut.append("objectId=" + affectedData.get(DomainConstants.SELECT_ID).toString() + "'");
				htmlOut.append(", '800', '700', 'true', 'popup')\">");
				htmlOut.append(itemName);
				htmlOut.append("</a>");
				htmlOut.append("<br/> ");
				}
			}
			changes.add(htmlOut.toString());	
		}
		return changes;
	}
	
	/**
	 * This method used to display CA and DECO state info in EBOM table column.
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws Exception
	 * To resolve issue(29475709) by Shajil on 17/11/2017
	 */
	
	public Vector getChangesState(Context context, String[] args)
			throws Exception {

		Vector changes = new Vector<>();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		// EBOM Page Export issue - Modified by Sara - 23/11/2017 - Start
		HashMap sParamMap = (HashMap) programMap.get("paramList");
		String sExportFormat = (String) sParamMap.get("exportFormat");
		// EBOM Page Export issue - Modified by Sara - 23/11/2017 - End
		
		MapList changeObjects = (MapList) programMap.get("objectList");
		Iterator itr = changeObjects.iterator();
		String objId = null;
		DomainObject partObj = null;
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.addElement(SELECT_NAME);
		while (itr.hasNext()) {
			Map dataMap = (Map) itr.next();
			objId = (String) dataMap.get("id");
			partObj = new DomainObject(objId);
			MapList affectedObjects = getConnectedChanges(context, objId);
			Iterator changeItr = affectedObjects.iterator();
			StringBuilder htmlOut = new StringBuilder();
			while (changeItr.hasNext()) {
				Map affectedData = (Map) changeItr.next();
				String state = (String) affectedData.get(SELECT_CURRENT);
				String itemName = (String)affectedData.get("name");
				String itemId = (String) affectedData.get("id");
				// EBOM Page Export issue - Modified by Sara - 23/11/2017 - Start
				if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
					htmlOut.append(state);
					htmlOut.append(" [");
					htmlOut.append(itemName);
					htmlOut.append(" ]");
					htmlOut.append("  ");
				} else {
				// EBOM Page Export issue - Modified by Sara - 23/11/2017 - End
				htmlOut.append(state);
				htmlOut.append(" [");
				htmlOut.append(itemName);
				htmlOut.append("]");
				htmlOut.append("<br/> ");
				}
				
				/*if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
					htmlOut.substring(arg0, arg1)
				}*/
			}
			changes.add(htmlOut.toString());

		}
		return changes;
	}

	/**
	 * This method used to get CA and DECO object from Part context.
	 * @param context
	 * @param objId
	 * @return MapList
	 * @throws Exception
	 * To resolve issue(29475709) by Shajil on 17/11/2017
	 */
	
	public MapList getConnectedChanges(Context context, String objId)
			throws Exception {
		StringList busSelect = new StringList(SELECT_ID);
		busSelect.addElement(SELECT_NAME);
		busSelect.addElement(SELECT_CURRENT);
		StringList relSelect = new StringList(SELECT_ID);
		relSelect.addElement(SELECT_ATTRIBUTE_REQUESTED_CHANGE);
		DomainObject partObj = new DomainObject(objId);
		MapList affectedObjects = partObj.getRelatedObjects(context,
				RELATIONSHIP_AFFECTED_ITEM+","+"Change Affected Item",
				TYPE_DECO+","+TYPE_CHANGEACTION,
				busSelect,
				relSelect,
				true,
				true,
				(short) 0,
				DomainConstants.EMPTY_STRING,
				DomainConstants.EMPTY_STRING,
				0);
		affectedObjects.addAll(getRelatedCA(context,objId));
		return affectedObjects;
	}

	/**
	 * This method used to get CA object from Part context.
	 * @param context
	 * @param objId
	 * @return MapList
	 * @throws Exception
	 * To resolve issue(29475709) by Shajil on 17/11/2017
	 */
	
	public MapList getRelatedCA(Context context, String objId) throws FrameworkException {
		String partPhysicalId = (String)DomainObject.newInstance(context, objId).getInfo(context, "physicalid");
		String queryPath = "Proposed Activity.Where";
		String relChangeAction = PropertyUtil.getSchemaProperty(context,"relationship_ChangeAction");
		String typeChangeAction = PropertyUtil.getSchemaProperty(context, "type_ChangeAction");
		String relProposedActivities = PropertyUtil.getSchemaProperty(context,"relationship_ProposedActivities");
		StringList objSel = new StringList("to[" + relProposedActivities + "].from.id");
		objSel.addElement(SELECT_NAME);
		objSel.addElement(SELECT_ID);
		objSel.addElement(SELECT_CURRENT);
		String attrRequestedChange = PropertyUtil.getSchemaProperty(context,"attribute_RequestedChange");
		String arg6 = PropertyUtil.getSchemaProperty(context,"attribute_RealizedActivityStatus");
		String attrReasonForChange = PropertyUtil.getSchemaProperty(context,"attribute_ReasonForChange");
		StringList relSel = getBasicRelSelects();
		MapList relatedCAList = new MapList();
		relSel.addElement("attribute[" + attrRequestedChange + "]");
		relSel.addElement("attribute[" + attrReasonForChange + "]");
		relSel.addElement("to.id");//Modified for Proposed Activity Rel to Object Migration - By Ravindra
		String queryResult = MqlUtil.mqlCommand(context,
													"query path type $1 endswithany $2 select owner dump $3", 
																new String[]{queryPath, partPhysicalId, "|"});
		StringList resultList = FrameworkUtil.split(queryResult, "\n");
		for (int count = 0; count < resultList.size(); ++count) {
			String rowDetails = (String) resultList.get(count);
			StringList result = FrameworkUtil.split(rowDetails, "|");
			if (result.size() > 0) {
				StringList rowInfo = FrameworkUtil.split((String)result.get(1), " ");
				String proposedActId = null;
				try {
					//[Google Custom] Vault Issue : Modified by Syed 06/03/2019 -Starts
					matrix.db.BusinessObject proposedObj = new matrix.db.BusinessObject("Proposed Activity",(String)rowInfo.get(2),(String)rowInfo.get(3),"");
					//[Google Custom] Vault Issue : Modified by Syed 06/03/2019 -Ends
					proposedActId = proposedObj.getObjectId(context);
					DomainObject proposedActivity = DomainObject.newInstance(context,proposedActId);
					relatedCAList.addAll( proposedActivity.getRelatedObjects(context, relProposedActivities,
							typeChangeAction, objSel, relSel, true, false,
							(short) 0, (String) null, (String) null, 0));
				} catch (MatrixException e) {
					e.printStackTrace();
				}
				
			}
		}
		return relatedCAList;
	}
	
	public static StringList getBasicRelSelects() {
		StringList arg = new StringList();
		arg.add("id[connection]");
		arg.add("type[connection]");
		arg.add("name[connection]");
		arg.add("physicalid[connection]");
		return arg;
	}
	

	// [Google Custom]: Cost Multiplier Attribute - Modified by Sara on 21/11/2017 - Start
		/**
		 * This method used to restrict the edit access for Cost Multiplier attribute
		 * @param context
		 * @param args
		 * @return String
		 * @throws Exception
		 * Cost Multiplier Attribute - Modified by Sara on 21/11/2017
		 */
		public String getCostMultiplierValue(Context context, String[] args)
				throws Exception {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap srequestMap = (HashMap) programMap.get("requestMap");
			String sObjectId = (String) srequestMap.get("objectId");
			String sMode = (String) srequestMap.get("mode");
			StringBuffer sbReturnString = new StringBuffer();
			String sAttrName = PropertyUtil.getSchemaProperty(context,"attribute_googMfgCostMultiplier");
			String roleGoogAdminUser = PropertyUtil.getSchemaProperty(context,"role_GoogAdminUser");
			
			DomainObject doj = new DomainObject(sObjectId);
			String sCostMultiplierValue = doj.getInfo(context, "attribute[" + sAttrName + "]");
			
			if(!srequestMap.containsKey("mode")){
				sbReturnString.append(sCostMultiplierValue);
			}else if(sMode.equalsIgnoreCase("view")) {
				sbReturnString.append("<input type=\"hidden\" readonly=\"true\" name=\"googMfgCostMultiplier\" id=\"googMfgCostMultiplier\" value=\"");
				sbReturnString.append(sCostMultiplierValue);
				sbReturnString.append("\" />");
				sbReturnString.append(sCostMultiplierValue);
			}else if(sMode.equalsIgnoreCase("edit")) {
				if (context.isAssigned(roleGoogAdminUser)){
				sbReturnString.append("<input type=\"text\" name=\"googMfgCostMultiplier\" id=\"googMfgCostMultiplier\" value=\"");
				sbReturnString.append(sCostMultiplierValue);
				sbReturnString.append("\" />");
				} else {
					sbReturnString.append("<input type=\"hidden\" readonly=\"true\" name=\"googMfgCostMultiplier\" id=\"googMfgCostMultiplier\" value=\"");
					sbReturnString.append(sCostMultiplierValue);
					sbReturnString.append("\" />");
					sbReturnString.append("<p>");
					sbReturnString.append(sCostMultiplierValue);
					sbReturnString.append("</p>");
				}
			} else{
				sbReturnString.append(sCostMultiplierValue);
			}
			return sbReturnString.toString();
		}
		
		/**
		 * To update Cost Multiplier attribute in CA
		 * @param context the eMatrix <code>Context</code> object.
		 * @param args contains a packed HashMap with the following entries:
		 * @return void
		 * @throws Exception if the operation fails.
		 * Cost Multiplier Attribute - Modified by Sara on 21/11/2017
		 */
		public void updateCostMutliplier(Context context, String[] args) throws Exception {
			try {
				HashMap programMap = (HashMap) JPO.unpackArgs(args);
				HashMap sparamMap = (HashMap) programMap.get("paramMap");				
				String sId = (String) sparamMap.get("objectId");
				String sNewValue = (String) sparamMap.get("New Value");
				String sAttrName = PropertyUtil.getSchemaProperty(context,"attribute_googMfgCostMultiplier");
				DomainObject doj = new DomainObject(sId);
				doj.setAttributeValue(context, sAttrName, sNewValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// [Google Custom]: Cost Multiplier Attribute - Modified by Sara on 21/11/2017 - End
		
		//Ticket - t/29763170 - Notes in EBOM Search Refinements - Modified by Sara on 22/11/2017 - Start
		/**
		 * To update Notes attribute in EBOM relationship
		 * @param context the eMatrix <code>Context</code> object.
		 * @param args contains a packed HashMap with the following entries:
		 * @return void
		 * @throws Exception if the operation fails.
		 * Notes Attribute on EBOM Relationship - Modified by Sara on 22/11/2017
		 */
		public void updateNotesAttribute(Context context, String[] args) throws Exception {
			try {
				HashMap programMap = (HashMap) JPO.unpackArgs(args);
				HashMap sparamMap = (HashMap) programMap.get("paramMap");	
				String sRelID = (String) sparamMap.get("relId");
				String sNewValue = (String) sparamMap.get("New Value");
				String sAttrName = PropertyUtil.getSchemaProperty(context,"attribute_Notes");
				
				DomainRelationship domRel = new DomainRelationship(sRelID);
				domRel.setAttributeValue(context, sAttrName, sNewValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Ticket - t/29763170 - Notes in EBOM Search Refinements - Modified by Sara on 22/11/2017 - End
		
	/**
	 * This method used for Import EBOM structure in Part context
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * Created by Shajil on 20/11/2017
	 * Modified by Subbu on 31/07/2018
	 */
		public MapList importBOMStructure(Context context, String[] args) throws Exception {
			Map paramMap = (Map) JPO.unpackArgs(args);

			java.io.File oFile = (File) paramMap.get("oFile");

			// [Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Start
			String fileExt = getFileExtension(oFile);
			if (fileExt.equalsIgnoreCase("csv")) {
				oFile = readAndWriteFile(oFile);
			}
			// [Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Ends
			Map parentMap = (Map) paramMap.get("parentMap");
			String parentPartId = (String) parentMap.get("id");
			String bgProcess = (String) parentMap.get("BGProcess");
			int count = getTotalRows(oFile);
			MapList resultList = null;
			// [Google Custom]: CSV- Import EBOM Structure - Modified by Subbu on 31/07/2018 - Starts
			if (count > 500 || bgProcess.equalsIgnoreCase("True")) {
				FileInputStream fis = new FileInputStream(oFile);
				XSSFWorkbook workbook = new XSSFWorkbook(fis);
				XSSFSheet sheet = workbook.getSheetAt(0);
				resultList = getPartDetails(sheet, workbook);
				MapList partDetails = updateMEPDetails(context, resultList);
				boolean isValidList = isValidMEPList(partDetails);
				if (isValidList) {
					batchBOMImport(context, args);
				} else {
					return partDetails;
				}
				// [Google Custom]: CSV- Import EBOM Structure - Modified by Subbu on 31/07/2018 - Ends
			} else {
				resultList = importBOMDataStructure(context, oFile, parentPartId);
			}
			return resultList;
		}
	
	/**
	 * This method used for Import EBOM structure in batch mode
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 * Created by Shajil on 27/11/2017
	 */
	public void batchBOMImport(Context context, String[] args) throws Exception {
		Map paramMap = (Map) JPO.unpackArgs(args);
		java.io.File oFile = (File) paramMap.get("oFile");
		
		//[Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Start
		String fileExt = getFileExtension(oFile);
		if(fileExt.equalsIgnoreCase("csv")){
			oFile = readAndWriteFile(oFile);
		}
		//[Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Ends
		
		Map parentMap = (Map) paramMap.get("parentMap");
		String parentPartId = (String) parentMap.get("id");
		Job job = null;
		try {
			String[] aStrJobArgs = new String[2];
			aStrJobArgs[0] = parentPartId;
			aStrJobArgs[1] = oFile.getPath();
			job = new Job("googCustomFunctions", "batchBOMUpdate", aStrJobArgs);
			job.setTitle("Import EBOM Structure ");
			job.setActionOnCompletion("None");
			job.setContextObject(parentPartId);
			job.setAllowreexecute("No");
			job.setDescription("Updating the BOM");
			job.createAndSubmit(context);
			job.setStartDate(context);
			job.finish(context, "Succeeded");
			job.setCompletionStatus("Succeeded");
			job.setAttributeValue(context, "Completion Status", "Succeeded");
		} catch (Exception e) {
			job.finish(context, "Failed");
			job.setCompletionStatus("Failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method used for Import EBOM structure in batch mode
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 * Created by Shajil on 27/11/2017
	 */
	public void batchBOMUpdate(Context context, String[] args) throws Exception {
		String strRootId = args[0];
		File oFile = new File(args[1]);
		String parentPartId = args[0];
		importBOMDataStructure(context, oFile, parentPartId);
	}
	
	/**
	 * This method used to create BOM structure and update the history.
	 * @param context
	 * @param inputFile
	 * @param parentPartId
	 * @return MapList
	 * @throws Exception
	 * Created by Shajil on 24/11/2017
	 */
	public MapList importBOMDataStructure(Context context, File inputFile,
			String parentPartId) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		ArrayList<String> headerDetails = getHeaderDetails(sheet);
		MapList resultList = new MapList();
		String language = context.getLocale().getLanguage();
		try {
			DomainObject parentPart = DomainObject.newInstance(context,
					parentPartId);
			MapList partDetails = getPartAvailability(context,
					getPartDetails(sheet, workbook));
			
			// [Google Custom]: MEP BOM Update  - Modified by Shajil on 08/05/2018 - Starts
			partDetails = updateMEPDetails(context,partDetails);
			boolean isValidList = isValidMEPList(partDetails);
			if (isValidList) {
				resultList = createBOMStructure(context, parentPart, partDetails, headerDetails);
				updateBOMWithMEP(context,resultList);
				String strGoogPromoteMsg = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
						"emxFramework.common.ImportEBOMStructureMessage", new Locale(language));
				strGoogPromoteMsg = strGoogPromoteMsg.replace("${context}", context.getUser());
				String strhistory = "modify bus " + parentPartId + " add history 'Connect' comment '"
						+ strGoogPromoteMsg + "';";
				MqlUtil.mqlCommand(context, strhistory);
			}else {
				return partDetails;
			}
			// [Google Custom]: MEP BOM Update  - Modified by Shajil on 08/05/2018 - Ends
			
		} catch (MatrixException e) {
			e.printStackTrace();
		}
		return resultList;
		
	}

	/**
	 * This method used to create and connect BOM structure.
	 * @param context
	 * @param inputFile
	 * @param parentPart
	 * @param partDetails
	 * @param headerDetails
	 * @return MapList
	 * @throws Exception
	 * Created by Shajil on 24/11/2017
	 */
	public MapList createBOMStructure(Context context, DomainObject parentPart,
			MapList partDetails, ArrayList<String> headerDetails)
			throws MatrixException {
		Iterator itr = partDetails.iterator();
		MapList resultList = new MapList();
		while (itr.hasNext()) {
			Map dataMap = (Map) itr.next();
			String action = (String) dataMap.get("Action");
			if (action.equalsIgnoreCase("Connect")) {
				resultList.add(connectBOMStructure(context, parentPart, dataMap));
			} else if (action.equalsIgnoreCase("Create")) {
				resultList.add(createBOMPart(context, parentPart, dataMap));
			}
		}
		return resultList;
	}

	

	/**
	 * This method used to create and connect BOM object.
	 * @param context
	 * @param parentPart
	 * @param dataMap
	 * @return Map
	 * @throws Exception
	 * Created by Shajil on 24/11/2017
	 */
	public Map createBOMPart(Context context, DomainObject parentPart,
			Map<String, String> dataMap) throws FrameworkException {
		Part part = (Part) DomainObject.newInstance(context,
				DomainConstants.TYPE_PART, DomainConstants.ENGINEERING);
		try {
			RelationshipType ebom = new RelationshipType("EBOM");
			AttributeTypeList attrTypeList = ebom.getAttributeTypes(context);
			BusinessType partType = new BusinessType(DomainConstants.TYPE_PART,
					context.getVault());
			AttributeTypeList partAttrTypeList = partType
					.getAttributeTypes(context);
			String name = (String) dataMap.get("Name");
			String revision = (String) dataMap.get("Revision");
			String policy = (String) dataMap.get("Policy");
		// [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/20188 : Starts	
			String strDescription = (String) dataMap.get("Description");
		// [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/20188 : Ends		
			try {
				String strPartId = part.createPartAndConnectRDO(context,
						DomainConstants.TYPE_PART, name, revision, policy,
						"eService Production", context.getUser(), null, null,
						null, null);
				Map<String, String> attributeInfo = createAttributeMap(dataMap,
						partAttrTypeList, new ArrayList<String>());
				DomainObject.newInstance(context, strPartId)
						.setAttributeValues(context, attributeInfo);
				if (UIUtil.isNotNullAndNotEmpty(strDescription)) {
					DomainObject.newInstance(context, strPartId)
						.setDescription(context, strDescription);
				}
				dataMap.put("id", strPartId);
				connectBOM(context, parentPart, attrTypeList, dataMap);

				
		// [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/2018 : Starts
				
		// [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/2018 : Ends						
				dataMap.put("Status", "Success");
			} catch (Exception e) {
				dataMap.put("Status", "Error while Part creation");
				e.printStackTrace();
			}
		} catch (MatrixException e) {
			dataMap.put("Status", "Error while Part creation");
			e.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * This method used to connect BOM object.
	 * @param context
	 * @param parentPart
	 * @param dataMap
	 * @return Map
	 * @throws Exception
	 * Created by Shajil on 24/11/2017
	 */
	public Map connectBOMStructure(Context context, DomainObject parentPart,
			Map dataMap) throws MatrixException, FrameworkException {
		RelationshipType ebom = new RelationshipType("EBOM");
		AttributeTypeList attrTypeList = ebom.getAttributeTypes(context);
		return connectBOM(context, parentPart, attrTypeList, dataMap);
	}

	/**
	 * This method used to connect BOM object.
	 * @param context
	 * @param parentPart
	 * @param attrTypeList
	 * @param dataMap
	 * @return Map
	 * @throws Exception
	 * Created by Shajil on 24/11/2017
	 */
	public Map connectBOM(Context context, DomainObject parentPart,
			AttributeTypeList attrTypeList, Map dataMap)
			throws FrameworkException {
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add(ATTRIBUTE_ISVPMVISIBLE);
		excludeList.add(ATTR_PART_VNAME);
	  // [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/2018 : Starts
		excludeList.add(ATTRIBUTE_GOOG_COMMODITY_CODE);
	  // [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/2018 : Ends	

		String objectId = (String) dataMap.get("id");
		Map<String, String> attributeInfo = createAttributeMap(dataMap,
				attrTypeList, excludeList);
		try {
			DomainRelationship ebomRel = DomainRelationship.connect(context,
					parentPart, DomainConstants.RELATIONSHIP_EBOM,
					DomainObject.newInstance(context, objectId));
			ebomRel.setAttributeValues(context, attributeInfo);
			dataMap.put("Status", "Success");
		} catch (Exception e) {
			dataMap.put("Status", "Error while EBOM connection");
		}
		return dataMap;
	}

	/**
	 * This method used to check the attribute information.
	 * @param dataMap
	 * @param attrTypeList
	 * @param excludeList
	 * @return Map
	 * Created by Shajil on 24/11/2017
	 */
	public Map<String, String> createAttributeMap(Map dataMap,
			AttributeTypeList attrTypeList, ArrayList<String> excludeList) {
		Map<String, String> attributeInfo = new HashMap<String, String>();
		Iterator itr = attrTypeList.iterator();
		String googlePartType = "Google Part Type";
		String specTitle = "Specification Title";
		String uom = "UOM";
		while (itr.hasNext()) {
			AttributeType attrType = (AttributeType) itr.next();
			String attrName = attrType.getName();
			if (dataMap.containsKey(attrName)
					&& (!excludeList.contains(attrName))) {
				String attrVal = (String) dataMap.get(attrName.trim());
				if (UIUtil.isNotNullAndNotEmpty(attrVal)) {
					attributeInfo.put(attrName.trim(),
							(String) dataMap.get(attrName.trim()));
				}
			} else {
				if (dataMap.containsKey(uom)) {
					attributeInfo.put(ATTRIBUTE_UNIT_OF_MEASURE, (String) dataMap.get(uom));
				}
			}
			if (dataMap.containsKey(specTitle) && (!excludeList.contains(attrName))) {
				attributeInfo.put(ATTR_PART_VNAME, (String) dataMap.get(specTitle));
				// [Google Custom]: Update Description while creating new Part/GPN - Modified by
				// Subbu on 07/02/2018 : Starts
			}
			if (dataMap.containsKey(googlePartType)) {
				attributeInfo.put(ATTRIBUTE_GOOG_COMMODITY_CODE, (String) dataMap.get(googlePartType));
			}
            // [Google Custom]: Update Description while creating new Part/GPN - Modified by Subbu on 07/02/2018 : Ends		
		}
		for (String excludeKey : excludeList) {
			attributeInfo.remove(excludeKey);
		}
		return attributeInfo;
	}

	/**
	 * This method used to get the Excel header information.
	 * @param sheet
	 * @return ArrayList
	 * Created by Shajil on 24/11/2017
	 */
	public ArrayList<String> getHeaderDetails(XSSFSheet sheet) {
		int rowCount = getHeaderRowCount(sheet);
		Row row = (Row) sheet.getRow(rowCount);
		ArrayList<String> headerName = new ArrayList<String>();
		if (null != row) {
			for (int i = 0; i < row.getLastCellNum(); i++) {

				Cell cell = row.getCell(i);
				String cellValue = cell.getStringCellValue();
				if (UIUtil.isNotNullAndNotEmpty(cellValue)) {
					headerName.add(cell.getStringCellValue());
				}
			}
		}
		return headerName;
	}

	/**
	 * This method used to get the Excel header row count.
	 * @param sheet
	 * @return int
	 * Created by Shajil on 24/11/2017
	 */
	public int getHeaderRowCount(XSSFSheet sheet) {
		int headrCont = 0;
		for (int rowCount = 0; rowCount < sheet.getLastRowNum() + 1; rowCount++) {
			Row row = (Row) sheet.getRow(rowCount);
			if (null != row) {
				Cell cell = row.getCell(0);
				if (null != cell
						&& cell.getStringCellValue().equalsIgnoreCase(
								"import data")) {
					headrCont = rowCount;
					break;
				}
			}
		}
		return headrCont + 1;
	}

	/**
	 * This method used to get the part details.
	 * @param sheet
	 * @param workbook
	 * @return MapList
	 * Created by Shajil on 24/11/2017
	 */
	public MapList getPartDetails(XSSFSheet sheet, XSSFWorkbook workbook) {
		MapList partDetails = new MapList();
		Iterator iterator = sheet.rowIterator();
		ArrayList<String> headerName = getHeaderDetails(sheet);
		FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator(
				(XSSFWorkbook) workbook);
		DataFormatter objDefaultFormat = new DataFormatter();
		for (int rowCount = getHeaderRowCount(sheet) + 1; rowCount < sheet
				.getLastRowNum() + 1; rowCount++) {
			Map<String, String> partData = new HashMap<String, String>();
			Row row = (Row) sheet.getRow(rowCount);
			for (int i = 0; i < headerName.size(); i++) {
				Cell cell = row.getCell(i);
				objFormulaEvaluator.evaluate(cell);
				// cell.setCellType(1);
				partData.put(headerName.get(i), objDefaultFormat
						.formatCellValue(cell, objFormulaEvaluator));
			}
			partDetails.add(partData);
		}
		return partDetailsRefinement(partDetails);
	}

	/**
	 * This method used to check the part availability.
	 * @param context
	 * @param partDetails
	 * @return MapList
	 * Created by Shajil on 24/11/2017
	 */
	public MapList getPartAvailability(Context context, MapList partDetails)
			throws MatrixException {
		Iterator partItr = partDetails.iterator();
		MapList partActionDetails = new MapList();
		while (partItr.hasNext()) {
			Map partData = (Map) partItr.next();
			String name = (String) partData.get("Name");
			String type = (String) partData.get("Type");
			String rev = (String) partData.get("Revision");
			matrix.db.BusinessObject partObj = new matrix.db.BusinessObject(
					type, name, rev, null);
			if (partObj.exists(context)) {
				partData.put("Action", "Connect");
				partData.put("id", partObj.getObjectId(context));
			} else {
				partData.put("Action", "create");
			}
			partActionDetails.add(partData);
		}
		return partActionDetails;
	}

	/**
	 * This method used to refine the input data.
	 * @param partDetails
	 * @return MapList
	 * Created by Shajil on 24/11/2017
	 */
	public MapList partDetailsRefinement(MapList partDetails) {
		Iterator itr = partDetails.iterator();
		MapList refinedList = new MapList();
		while (itr.hasNext()) {
			Map dataMap = (Map) itr.next();
			String level = (String) dataMap.get("Level");
			String revision = (String) dataMap.get("Revision");
			String refDesignator = (String) dataMap.get("Reference Designator");
			String googRefDesignator = (String) dataMap
					.get("googReferenceDesignator");

			String name = (String) dataMap.get("Name");
			String type = (String) dataMap.get("Type");

			if (UIUtil.isNullOrEmpty(level)) {
				level = "1";
				dataMap.put("Level", level);
			}
			if (revision.length() < 2) {
				revision = "0" + revision;
				dataMap.put("Revision", revision);
			}
			if (UIUtil.isNotNullAndNotEmpty(refDesignator)) {
				refDesignator = refDesignator.replaceAll("\\s+", "");
				dataMap.put("Reference Designator", refDesignator);
			}
			if (UIUtil.isNotNullAndNotEmpty(googRefDesignator)) {
				googRefDesignator = googRefDesignator.replaceAll("\\s+", "");
				dataMap.put("googReferenceDesignator", googRefDesignator);
			}
			if (UIUtil.isNotNullAndNotEmpty(name)
					&& UIUtil.isNotNullAndNotEmpty(type)
					&& UIUtil.isNotNullAndNotEmpty(revision)) {
				refinedList.add(dataMap);
			}
		}
		
		return refinedList;
	}

	/**
	 * This method used to convert the gSheet to Excel file.
	 * @param context
	 * @param oFile
	 * @return File
	 * Created by Shajil on 24/11/2017
	 */
	public File gsheetToExcelConvertion(Context context, File oFile) throws Exception {
		FileInputStream fis = new FileInputStream(oFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		ArrayList<String> headerDetails = getHeaderDetails(sheet);
		MapList partDetails = getPartAvailability(context,getPartDetails(sheet,workbook));
		return createExcelData(oFile, partDetails, headerDetails);
	}

	/**
	 * This method used to convert the gSheet to Excel file.
	 * @param oFile
	 * @param partList
	 * @param headerDetails
	 * @return File
	 * Created by Shajil on 24/11/2017
	 */
	public File createExcelData(File oFile, MapList partList,
			ArrayList<String> headerDetails) throws FileNotFoundException,
			IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sht = wb.createSheet("BOMImport");
		Row row = sht.createRow(2);
		Cell importCell = row.createCell(0);
		importCell.setCellValue("import data");
		row = insertCellValues(sht, row, partList, headerDetails);
		FileOutputStream fileOut = new FileOutputStream(oFile);
		wb.write(fileOut);
		fileOut.close();
		return oFile;
	}

	/**
	 * This method used to insert cell value to Excel file.
	 * @param sheet
	 * @param row
	 * @param partList
	 * @param headerDetails
	 * @return Row
	 * Created by Shajil on 24/11/2017
	 */
	public Row insertCellValues(XSSFSheet sheet, Row row, MapList partList,
			ArrayList<String> headerDetails) {
		row = createHeader(sheet, row, headerDetails);
		row = createPartDetails(sheet, row, headerDetails, partList);
		return row;
	}

	/**
	 * This method used to create Header.
	 * @param sheet
	 * @param row
	 * @param headerDetails
	 * @return Row
	 * Created by Shajil on 24/11/2017
	 */
	public Row createHeader(XSSFSheet sheet, Row row,
			ArrayList<String> headerDetails) {
		row = sheet.createRow(row.getRowNum() + 1);
		for (int count = 0; count < headerDetails.size(); count++) {
			Cell headerCell = row.createCell(count);
			headerCell.setCellType(1);
			headerCell.setCellType(Cell.CELL_TYPE_STRING);
			headerCell.setCellValue(headerDetails.get(count));
		}
		return row;
	}

	/**
	 * This method used to create Part details in Excel.
	 * @param sheet
	 * @param row
	 * @param headerDetails
	 * @param partList
	 * @return Row
	 * Created by Shajil on 24/11/2017
	 */
	public Row createPartDetails(XSSFSheet sheet, Row row,
			ArrayList<String> headerDetails, MapList partList) {
		Iterator itr = partList.iterator();
		while (itr.hasNext()) {
			Map partInfo = (Map) itr.next();
			row = sheet.createRow(row.getRowNum() + 1);
			for (int count = 0; count < headerDetails.size(); count++) {
				Cell headerCell = row.createCell(count);
				headerCell.setCellType(1);
				headerCell.setCellValue((String) partInfo.get(headerDetails
						.get(count)));
			}
		}

		return row;
	}

	/**
	 * This method used to get the total row count.
	 * @param inputFile
	 * @return int
	 * Created by Shajil on 24/11/2017
	 */
	public int getTotalRows(File inputFile) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		MapList dataList = getPartDetails(sheet, workbook);
		return dataList.size();
	}
	
	
	
			//[Google Custom]: Export to CSV command in CA Summary View - Modified by Sara on 06/12/2017 - Start
		/**
		 * To generate CA Summary view report in CSV format
		 * 
		 * @param context
		 *            the eMatrix <code>Context</code> object.
		 * @param args
		 *            contains a packed HashMap with the following entries:
		 * @return HashMap
		 * @throws Exception
		 *             if the operation fails.
		 */
	public HashMap generateSummaryViewReport(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap sparamMap = (HashMap) programMap.get("paramMap");
		String sParsedHeader = (String) sparamMap.get("parsedHeader");
		String sCAObjectId = (String) sparamMap.get("objectId");

		HashMap rangeMap = new HashMap();
		HashMap sfieldMap = (HashMap) programMap.get("fieldMap");
		MapList sfieldvalues = (MapList) sfieldMap.get("fieldvalues");
		StringBuilder sb = new StringBuilder();
		StringList sGroupInfoList = new StringList();
		HashMap stableMap = (HashMap) programMap.get("ColumnMapList");

		StringList busSelectionList = new StringList();
		busSelectionList.add(DomainObject.SELECT_TYPE);
		busSelectionList.add(DomainObject.SELECT_NAME);
		busSelectionList.add(DomainObject.SELECT_REVISION);
		busSelectionList.add(DomainObject.SELECT_DESCRIPTION);
		busSelectionList.add(DomainObject.SELECT_CURRENT);
		busSelectionList.add(SELECT_ATTRIBUTE_EFFECTIVITY_DATE);
		busSelectionList.add(SELECT_ATTRIBUTE_WEIGHT);
		busSelectionList.add(SELECT_ATTRIBUTE_ENDITEM);
		busSelectionList.add(SELECT_ATTRIBUTE_DESIGNPURCHASE);
		busSelectionList.add(SELECT_SUPPLIER);

		StringList relSelectionList = new StringList();
		//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Starts
		relSelectionList.add("to."+SELECT_ATTRIBUTE_ACTION);
		relSelectionList.add(SELECT_ATTRIBUTE_REQUESTED_CHANGE);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googTrueEndItem);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googQuantityPerInstance);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googQtyInInventory);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googQtyToModify);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googPiecePriceDelta);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googPiecePriceDeltaEstimate);
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googQtyInField);
		// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 - Start
		relSelectionList.add("to."+SELECT_ATTRIBUTE_googInstancePerVehicle);
		// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 - End

		// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 - Start
		relSelectionList.add("to."+SELECT_ATTRIBUTE_IS_TORQUE_AFFECTED);
		// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 - End
		
		// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - Start
		relSelectionList.add("to."+SELECT_ATTRIBUTE_TORQUE_CRITICALITY);
		// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - End
		//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Ends		
																					
		StringList sBusList = new StringList();
		sBusList.add(SELECT_ROUTE_NAME);
		sBusList.add(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
		sBusList.add(SELECT_ATTRIBUTE_COMMENTS);

		// Add the header for the file
		sb.append("\"" + sParsedHeader + "\"");

		for (int i = 0; i < sfieldvalues.size(); i++) {
			HashMap field = (HashMap) sfieldvalues.get(i);
			HashMap settingsMap = (HashMap) field.get("settings");
			String sFieldType = (String) settingsMap.get("Field Type");
			String sGroupName = (String) settingsMap.get("Group Name");
			String sLabel = (String) field.get("label");
			StringList sValues = (StringList) field.get("field_display_value");
			
			if (sLabel != null) {
				//[Google Custom]: To fix CA custom Export Issue  - Modified by Sara on 03/04/2018 - Start
				String sExport = (String) settingsMap.get("Export");
				if (sFieldType != null && sExport!=null) {
				if(sFieldType.equalsIgnoreCase("programHTMLOutput") && sExport.equalsIgnoreCase("false")) {
					sLabel = " ";
				}}
				//[Google Custom]: To fix CA custom Export Issue  - Modified by Sara on 03/04/2018 - End
			
				formReader(sb, sGroupInfoList, sFieldType, sGroupName, sLabel, sValues);
			} else {
				if (sFieldType != null && sFieldType.equals("emxTable")) {
					String sTable = (String) settingsMap.get("table");
					MapList sColumnList = (MapList) stableMap.get(sTable);
					sb.append("\n");
					for (int k = 0; k < sColumnList.size(); k++) {
						Map childMap = (Map) sColumnList.get(k);
						HashMap settings = (HashMap) childMap.get("settings");
						String sTableLabel = (String) childMap.get("label");

				   // [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 12/03/2018 - Starts
						boolean isExport = true;
						if (settings.containsKey("Export")) {
							isExport = BooleanUtils.toBoolean((String) settings.get("Export"));
						}

						if (isExport) {
							if (!sTableLabel.equals("&nbsp;")) {
								sb.append("\"" + sTableLabel + "\"");
								sb.append(",");
							}
						}
					}
					// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 12/03/2018 - Starts

					String sFieldName = (String) field.get("name");
					MapList sTableValues = new MapList();
					StringBuilder sb1 = new StringBuilder();
					if (sTable.equals("googAffectedItemSummary")) {
						sTableValues = (MapList) stableMap.get(sFieldName);

						// To get isEstimateWeight
						programMap.put("objectList", sTableValues);
						Vector isEstimatedWeightValues = isEstimatedWeight(context, args);
						Vector sUpLevelPartNumberValues = geTopLvlpartforAffectedItems(context, args);

						HashMap sParamList = new HashMap();
						sParamList.put("sTableValues", sTableValues);
						sParamList.put("exportFormat", "CSV");
						programMap.put("paramList", sParamList);

						for (int b = 0; b < sTableValues.size(); b++) {
							Map sMap1 = (Map) sTableValues.get(b);
							String sObjectId = (String) sMap1.get(SELECT_ID);

							// To fetch business object details
							DomainObject doj = new DomainObject(sObjectId);
							Map sObjectDetail = doj.getInfo(context, busSelectionList);
							String sName = (String) sObjectDetail.get(SELECT_NAME);
							String sRevision = (String) sObjectDetail.get(SELECT_REVISION);
							String sType = (String) sObjectDetail.get(SELECT_TYPE);
							String sCurrent = (String) sObjectDetail.get(SELECT_CURRENT);
							String sDescription = (String) sObjectDetail.get(SELECT_DESCRIPTION);
							String sEffectivityDate = (String) sObjectDetail.get(SELECT_ATTRIBUTE_EFFECTIVITY_DATE);
							String sWeight = (String) sObjectDetail.get(SELECT_ATTRIBUTE_WEIGHT);
							String sEndItem = (String) sObjectDetail.get(SELECT_ATTRIBUTE_ENDITEM);
							String sCustomOffshelf = (String) sObjectDetail.get(SELECT_ATTRIBUTE_DESIGNPURCHASE);
							String supplierName = (String) sObjectDetail.get(SELECT_SUPPLIER);

							if (supplierName == null) {
								supplierName = "";
							}

							// To fetch relationship details
							String sRelId = (String) sMap1.get(SELECT_RELATIONSHIP_ID);
							String sRelIds[] = new String[1];
							sRelIds[0] = sRelId;

							MapList sRelationshipDetail = DomainRelationship.getInfo(context, sRelIds,
									relSelectionList);
							Map sRelMap = (Map) sRelationshipDetail.get(0);
							//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Starts
							String sAction = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_ACTION);
							String sRequestedChange = (String) sRelMap.get(SELECT_ATTRIBUTE_REQUESTED_CHANGE);
							String googQtyInInventory = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googQtyInInventory);
							String sQtyInField = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googQtyInField);
							String googPiecePriceDelta = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googPiecePriceDelta);
							String googQuantityPerInstance = (String) sRelMap
									.get("to."+SELECT_ATTRIBUTE_googQuantityPerInstance);
							String googQtyToModify = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googQtyToModify);
							String sTrueEndItem = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googTrueEndItem);
							String isEstimate = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googPiecePriceDeltaEstimate);
							Object sEstimate = isEstimatedWeightValues.get(b);
							Object sUpLevelPartNumber = sUpLevelPartNumberValues.get(b);
							// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 -
							// Start
							String sInstancePerVehicle = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_googInstancePerVehicle);
							// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 -
							// End

							// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 -
							// Start
							String isTorqueAffected = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_IS_TORQUE_AFFECTED);
							// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 -
							// End

							// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - Start
							String sTorqueCriticality = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_TORQUE_CRITICALITY);
							// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - End
							//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Ends							
							
							sb1.append("\"" + sAction + "\"");
							sb1.append(",");
							sb1.append("\"" + sName + "\"");
							sb1.append(",");
							sb1.append("\"" + sRevision + "\"");
							sb1.append(",");
							sb1.append("\"" + sType + "\"");
							sb1.append(",");
							sb1.append("\"" + sCurrent + "\"");
							sb1.append(",");
							sb1.append("\"" + sDescription + "\"");
							sb1.append(",");
							sb1.append("\"" + supplierName + "\"");
							sb1.append(",");
							sb1.append("\"" + sRequestedChange + "\"");
							sb1.append(",");
							sb1.append("\"" + sUpLevelPartNumber + "\"");
							sb1.append(",");
							sb1.append("\"" + sEndItem + "\"");
							sb1.append(",");
							sb1.append("\"" + sCustomOffshelf + "\"");
							sb1.append(",");
							sb1.append("\"" + sTrueEndItem + "\"");
							sb1.append(",");
							sb1.append("\"" + googQuantityPerInstance + "\"");
							sb1.append(",");
							// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 -
							// Start
							sb1.append("\"" + sInstancePerVehicle + "\"");
							sb1.append(",");
							// [Google Custom]: "Instance Per Vehicle" - Modified by Sara on 08/12/2017 -
							// End
							sb1.append("\"" + googQtyInInventory + "\"");
							sb1.append(",");
							sb1.append("\"" + sQtyInField + "\"");
							sb1.append(",");
							sb1.append("\"" + googQtyToModify + "\"");
							sb1.append(",");
							sb1.append("\"" + googPiecePriceDelta + "\"");
							sb1.append(",");
							sb1.append("\"" + isEstimate + "\"");
							sb1.append(",");
							sb1.append("\"" + sEffectivityDate + "\"");
							sb1.append(",");
							sb1.append("\"" + sWeight + "\"");
							sb1.append(",");
							sb1.append("\"" + sEstimate + "\"");
							sb1.append(",");

							// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 -
							// Start
							sb1.append("\"" + isTorqueAffected + "\"");
							sb1.append(",");
							// [Google Custom]: "Is Torque Affected" - Modified by Shajil on 18/12/2017 -
							// End

							// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - Start
							sb1.append("\"" + sTorqueCriticality + "\"");
							sb1.append(",");
							// [Google Custom]: "Torque Criticality Impacted?" - Modified by Sara on 05/04/2018 - End
							
							sb1.append("\n");
						}
					}

					if (sTable.equals("googAPPTaskSummary")) {
						sTableValues = (MapList) stableMap.get(sFieldName);
						for (int a = 0; a < sTableValues.size(); a++) {
							Map sMap = (Map) sTableValues.get(a);
							String sObjectId = (String) sMap.get(SELECT_ID);
							DomainObject doj = new DomainObject(sObjectId);
							Map sTaskDetails = doj.getInfo(context, sBusList);
							String sRoute = (String) sTaskDetails.get(SELECT_ROUTE_NAME);
							String sApprovalComments = (String) sTaskDetails.get(SELECT_ATTRIBUTE_COMMENTS);
							// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 16/03/2018 - Starts
							sMap.put(SELECT_ROUTE_NAME, sRoute);
							sMap.put(SELECT_ATTRIBUTE_COMMENTS, sApprovalComments);
							generateTaskDetails(sb1,sMap);				
							// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 16/03/2018 - Ends
						}
					}
					// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 12/03/2018 - Starts
					if (sTable.equals("IssueReportedAgainstList")) {
						sTableValues = (MapList) stableMap.get(sFieldName);
						Iterator itr = sTableValues.iterator();
						getReportedAgainstContents(context,sb1, sTableValues);
					}
					// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 12/03/2018 - Ends
					sb.append("\n");
					sb.append(sb1);
				}
			}
		}
		// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 16/03/2018 - Starts
		boolean isDeviationModule = false;
		if(sparamMap.containsKey("exportModule")) {
			isDeviationModule = BooleanUtils.toBoolean(sparamMap.get("exportModule").equals("DeviationExport"));
		}
		if(isDeviationModule) {			
			sb.append("\n");
			getReportedAgainstContents(context,sb,getReportedAgainstTableData(context,sCAObjectId));
			sb.append("\n");
			getTaskTableData(context,sb,sCAObjectId);
		}
		// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil on 16/03/2018 - Ends
		String sLabelValues = sb.toString();

		// To fetch CA name
		DomainObject dj = new DomainObject(sCAObjectId);
		String sCAName = dj.getName(context);

		File sNewFile = generateSummaryReportCSV(context, sLabelValues, sCAName);
		rangeMap.put("file", sNewFile.getPath());
		rangeMap.put("GeneratedFileName", sNewFile.getName());
		return rangeMap;
	}		
	
		/**
		 * To read webform values to export
		 * 
		 * @param context
		 *            the eMatrix <code>Context</code> object.
		 * @param args
		 *            contains the field values
		 * @return StringBuffer
		 * @throws Exception
		 *             if the operation fails.
		 */
	private StringBuilder formReader(StringBuilder sb, StringList sGroupInfoList, String sFieldType, String sGroupName,
			String sLabel, StringList sValues) {

		if (null != sFieldType && "Section Header".equals(sFieldType)) {
			sb.append("\n");
			sb.append("\n");
		}

		// Grouping the fields
		if (UIUtil.isNotNullAndNotEmpty(sGroupName)) {
			if (sGroupInfoList.contains(sGroupName)) {
				sb.append(",");
			} else {
				sb.append("\n");
				sGroupInfoList.add(sGroupName);
			}
		} else {
			sb.append("\n");
		}

		// Append Field Name and Field Value
		sb.append("\"" + sLabel + "\"");
		if (null != sValues && sValues.size() > 0) {
			String sDisplayValue = (String) sValues.get(0);
			sb.append(",");
			// [Google Custom] Used Generic Methods - Modified by Sandeep on 10-03-2018 - Starts
			sb.append(formatStringValues(sDisplayValue));
			// [Google Custom] Modified to Fix Description Export Issue if value Contains new Line (\n) - Modified by Sandeep on 08-20-2018 - Starts
			/*if (sDisplayValue.indexOf(',') == -1 && sDisplayValue.indexOf('\n') == -1) {
				sb.append(sDisplayValue);
			} else {
				sb.append("\'" + sDisplayValue + "\'");
			}*/
			// [Google Custom] Modified to Fix Description Export Issue if value Contains new Line (\n) - Modified by Sandeep on 08-20-2018 - Ends
			// [Google Custom] Used Generic Methods - Modified by Sandeep on 10-03-2018 - Ends
		} else {
			sb.append(",");
		}
		return sb;
	}
		
		/**
	     * To generate the CSV file for CA summary view
	     * 
	     * @param 
	     * 		context the ENOVIA <code>Context</code> object
	     * @param args
	     * 		String array containing the names of form and table values
	     * @return java.io.File, 
	     * 		contains the Exported Form and Table values and formatted in CSV format
	     * @throws Exception
	     *      if the operation fails.
	     */
		public File generateSummaryReportCSV (Context context, String LabelValues, String sCAName)throws Exception {

			String sLabelvalue = LabelValues;
			String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
			String sHeader = sCAName;
			String strWorkspace = context.createWorkspace();
			
			StringBuffer filename = new StringBuffer(50);
			filename.append(strWorkspace);
			filename.append("\\");
			filename.append(sHeader);
			filename.append("_");
			filename.append(fileCreateTimeStamp);
			filename.append(".csv");
			String sFilename = filename.toString();
			
			// File Creation Part
			File file = new File(sFilename);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
				try{
					bw.write(sLabelvalue);
				} finally{
					bw.close();
				}
			return file;
		}
		
		//[Google Custom]: Export to CSV command in CA Summary View - Modified by Sara on 06/12/2017 - End
		/**
		 * This method used to override the Owner/Contributer validation in CA Add Existing functionality
		 * @param context
		 * @param args
		 * @return Map
		 * @throws Exception
		 * @author shajil
		 */
		public Map connectAffectedItems(Context context, String[] args)
				throws Exception {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			StringList selectedItemsList = (StringList)programMap.get("selectedItemsList");
			String objectId = (String)programMap.get("objectId");
			Map mpInvalidObjects = new HashMap();
			boolean isAssignee = false;
			boolean isConnected = false;
			String role_GoogAdminUser = PropertyUtil.getSchemaProperty(context,"role_GoogAdminUser");
			String role_googChangeMgtAdmin = PropertyUtil.getSchemaProperty(context,"role_googChangeMgtAdmin");
			DomainRelationship techAssigneeRel = new DomainRelationship();
			try{
				String userName = context.getUser();
				StringList busSelect = new StringList(SELECT_ID);
				busSelect.addElement(SELECT_NAME);
				DomainObject ChangeObject = DomainObject.newInstance(context, objectId);
				MapList personList = ChangeObject.getRelatedObjects(context, 				// Context
						ChangeConstants.RELATIONSHIP_TECHNICAL_ASSIGNEE,	// Relationship Pattern
						TYPE_PERSON,								// Type Pattern
						busSelect, 											// Bus Selects
						null, 											// Rel Selects
						false, 													// Get To 
						true, 													// Get From
						(short) 1,												// Recurse Level
						DomainConstants.EMPTY_STRING,							// Bus Where
						DomainConstants.EMPTY_STRING,							// Rel Where
						0);	
				Iterator personItr = personList.iterator();
				while(personItr.hasNext()){
					Map personInfo = (Map)personItr.next();
					String name = (String)personInfo.get(SELECT_NAME);
					if(name.equals(userName)){
						isAssignee = true;
						break;
					}
				}
				
				
				if((context.isAssigned(role_GoogAdminUser) || context
							.isAssigned(role_googChangeMgtAdmin)) && !isAssignee){
					String personObjectID = PersonUtil.getPersonObjectID(context);
					DomainObject personObj = new DomainObject(personObjectID);
					techAssigneeRel = DomainRelationship.connect(context, ChangeObject, ChangeConstants.RELATIONSHIP_TECHNICAL_ASSIGNEE, personObj);
					//[Google Custom]: Edit access for CA Content(t/30019806) - Modified by Shajil on 03/05/2018 - Start
					DomainAccess.createObjectOwnership(context, objectId, personObjectID,  "read,fromconnect,fromdisconnect,show", "follower");
					//[Google Custom]: Edit access for CA Content(t/30019806) - Modified by Shajil on 03/05/2018 - Ends
					isConnected = true;
				}
				
				Map partDetails = partInfoList(context,selectedItemsList);
				StringList releasedPartIds = (StringList)partDetails.get("releasedPartList");
				StringList partList = (StringList)partDetails.get("partList");
				try{
					//[Google Custom]: Minor Revise Issue - Modified by Shajil on 05/04/2018 - Start
					int releasedPartCount = releasedPartIds.size();
					if(releasedPartCount>0){
						for(int i=0;i<releasedPartCount;i++) {
							StringList partListId = new StringList((String)releasedPartIds.get(i));
							mpInvalidObjects = new ChangeAction(objectId).connectAffectedItems(context, partListId,true,"For Update",null,null);
						}
					}
					
					int partCount = partList.size();
					if(partCount>0){
						for(int i=0;i<partCount;i++) {
							StringList partListId = new StringList((String)partList.get(i));
							mpInvalidObjects = new ChangeAction(objectId).connectAffectedItems(context, partListId,true,"For Release",null,null);		
						}
					}
					//[Google Custom]: Minor Revise Issue - Modified by Shajil on 05/04/2018 - Ends
				}catch(Exception e){
					mpInvalidObjects.put("strErrorMSG", e.getMessage());
					//ContextUtil.abortTransaction(context);
					emxContextUtil_mxJPO.mqlNotice(context, e.getMessage());
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(isConnected){
					//techAssigneeRel.remove(context);
				}
			}
			return mpInvalidObjects;
		}

		/**
		 * This method to list out the Released and and other part objects in seperate list
		 * @param context
		 * @param selectedItemsList
		 * @return Map
		 * @throws FrameworkException
		 * @author shajil
		 */
		public Map<String,StringList> partInfoList(Context context, StringList selectedItemsList) throws FrameworkException {
			Map<String,StringList> resultMap = new HashMap<String,StringList>();
			String partObjectIds [] = new String[selectedItemsList.size()];
			StringList busSelect = new StringList(SELECT_ID);
			busSelect.addElement(SELECT_CURRENT);
			busSelect.addElement(SELECT_NAME);
			for(int i=0;i<selectedItemsList.size();i++){
				partObjectIds[i] = (String)selectedItemsList.get(i);
			}
			MapList partInfo = DomainObject.newInstance(context).getInfo(context,partObjectIds , busSelect);
			Iterator partItr = partInfo.iterator();
			StringList releasedPartIds = new StringList();
			StringList partIds = new StringList();
			while(partItr.hasNext()){
				Map partDetals = (Map)partItr.next();
				String state = (String)partDetals.get(SELECT_CURRENT);
				String id = (String)partDetals.get(SELECT_ID);
				if(state.equals(STATE_ECPART_RELEASE)){
					releasedPartIds.addElement(id);
				}else{
					partIds.addElement(id);
				}
			}
			resultMap.put("releasedPartList", releasedPartIds);
			resultMap.put("partList", partIds);
			return resultMap;
		}
		
		//[Google Custom]: 30813114 - Enovia Issues Physical Collaboration defaulting to 'true' - Created by Sara on 30/01/2018 - Start
		/**
		 * To update the Design Collaboration field value - 30813114
		 * @param context
		 * @param args
		 * @throws Exception
		 * @author Sara
		 */
		public void updateDesignCollaborationValue(Context context, String[] args) throws Exception {
			try {
				HashMap programMap = (HashMap) JPO.unpackArgs(args);
				HashMap paramMap = (HashMap) programMap.get("paramMap");
				String sObjectId = (String) paramMap.get("objectId");
				String sNewValue = (String) paramMap.get("New Value");
				String sAdminName = PropertyUtil.getSchemaProperty(context, "attribute_isVPMVisible");
				DomainObject doj = new DomainObject(sObjectId);
				doj.setAttributeValue(context, sAdminName, sNewValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//[Google Custom]: 30813114 - Enovia Issues Physical Collaboration defaulting to 'true' - Created by Sara on 30/01/2018 - End
		
		//[Google Custom]: 30997479 - Part Specification hyperlink in Table view (CA content/CA Summary View/Deviation Content) - Created by Sara on 13/02/2018 - Start
		public Vector getPartSpecifications(Context context, String[] args) throws Exception{
			Vector specifications = new Vector<>();
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList sPartList = (MapList) programMap.get("objectList");
			HashMap sParamMap = (HashMap) programMap.get("paramList");
			String sExportFormat = (String) sParamMap.get("exportFormat");
			StringBuilder htmlOut = new StringBuilder();
			StringList objectSelects = new StringList();
			objectSelects.add(SELECT_ID);
			objectSelects.add(SELECT_NAME);
			
			Iterator itr = sPartList.iterator();
			String sObjId=null;
			while(itr.hasNext()){
				Map dataMap = (Map)itr.next();
				sObjId = (String)dataMap.get(SELECT_ID);
				getSpecList(context, sExportFormat, objectSelects, htmlOut, sObjId);
			}
			specifications.add(htmlOut.toString());
			return specifications;
		}

		private void getSpecList(Context context, String sExportFormat, StringList objectSelects, StringBuilder htmlOut, String sObjId) throws Exception, FrameworkException {
			DomainObject doj = new DomainObject(sObjId);
			MapList specList = doj.getRelatedObjects(context,
													RELATIONSHIP_PART_SPECIFICATION, // relationship pattern
													"*", // object pattern
													objectSelects, // object selects
													null, // relationship selects
													false, // to direction
													true, // from direction
													(short) 1, // recursion level
													null, // object where clause
													null, 
													0);
			
			if(specList.size()>0){
				Iterator itr1 = specList.iterator();
				while(itr1.hasNext()){
					Map childMap = (Map)itr1.next();
					String specName = (String)childMap.get(SELECT_NAME);
					String specId = (String)childMap.get(SELECT_ID);
					
					if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
						htmlOut.append(specName);
						htmlOut.append("  ");
					} else {
					htmlOut.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?");
					htmlOut.append("objectId=" + specId + "'");
					htmlOut.append(", '800', '700', 'true', 'popup')\">");
					htmlOut.append(specName);
					htmlOut.append("</a>");
					htmlOut.append("<br/> ");
					}
				}
			}
		}
		//[Google Custom]: 30997479 - Part Specification hyperlink in Table view (CA content/CA Summary View/Deviation Content) - Created by Sara on 13/02/2018 - End
		
		//[Google Custom]: 30763665 - Alternate MPN/Substitute MPN - Created by Sara on 14/02/2018 - Start
		/**
		 * To add columns in EBOM view -  Alternate MPN/Alternate Manufacturer/Substitute MPN/Substitute Manufacturer
		 * @param context
		 * @param args
		 * @return Vector
		 * @throws Exception
		 */
		public Vector getSubstitutePartsDetails(Context context, String[] args) throws Exception{
			Vector subPartMEPs = new Vector<>();
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap sColumnList = (HashMap)programMap.get("columnMap"); 
			HashMap settingsMap= (HashMap)sColumnList.get("settings");
			String sFieldName = (String)settingsMap.get("FieldName");
			MapList sPartList = (MapList) programMap.get("objectList");
			HashMap sParamMap = (HashMap) programMap.get("paramList");
			String sExportFormat = (String) sParamMap.get("exportFormat");
			StringBuilder sb = new StringBuilder();
			StringList slEBOMSubs = new StringList();
			String strSelectStmt=null;
			
            try{
				Iterator itr = sPartList.iterator();
				while(itr.hasNext()){
					Map childMap = (Map)itr.next();
					String sConnectionId = (String)childMap.get(SELECT_RELATIONSHIP_ID);
					
					if (UIUtil.isNotNullAndNotEmpty(sConnectionId)) {
						//Added By Sharad to Fix Issue for multiple display of MPN Info - Starts
						sb = new StringBuilder();
						//Added By Sharad to Fix Issue for multiple display of MPN Info - Ends
						if(sFieldName.equalsIgnoreCase("SubstituteMPN")){
							strSelectStmt = SELECT_MANUFACTURER_EQUIVALENT_ID;
						}
						
						if(sFieldName.equalsIgnoreCase("SubstituteManufacturer")){
							strSelectStmt = SELECT_MANUFACTURING_RESPONSIBILITY_ID;
						}
						
						if(UIUtil.isNotNullAndNotEmpty(strSelectStmt)) {
						String strCommand = "print connection $1 select $2 dump $3";
						String strResult = MqlUtil.mqlCommand(context, strCommand, sConnectionId, strSelectStmt ,"|");
						
								if (UIUtil.isNotNullAndNotEmpty(strResult)) {
										slEBOMSubs = FrameworkUtil.split(strResult, "|");
										Iterator ebomsubsItr = slEBOMSubs.iterator();
										int icountMPNSub=0;
										while(ebomsubsItr.hasNext()){
											String strSubId = ebomsubsItr.next().toString();
											DomainObject doj = new DomainObject(strSubId);
											String sObjectName = doj.getInfo(context,DomainConstants.SELECT_NAME);
											
											if ((!UIUtil.isNullOrEmpty(sExportFormat)) && sExportFormat.equalsIgnoreCase("CSV")){
												//sb.append(sObjectName);
												//sb.append("  ");
												//Added By Sharad to Fix Issue for multiple display of MPN Info - Starts
												
												if(icountMPNSub > 0 ){
													sb.append("\r\n");
													sb.append(sObjectName.toString().trim());
												} else {
													sb.append(sObjectName.toString().trim());
												}
												icountMPNSub++;
												//Added By Sharad to Fix Issue for multiple display of MPN Info - Ends
												
											} else {
												sb.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?");
												sb.append("objectId=" + strSubId + "'");
												sb.append(", '800', '700', 'true', 'popup')\">");
												sb.append(sObjectName);
												sb.append("</a>");
												sb.append("<br/> ");
											}
										}
										subPartMEPs.add(sb.toString());
								} else {
										subPartMEPs.add("");
								} 
						} else {
							subPartMEPs.add("");
						}
					} else {
						subPartMEPs.add("");
					}
				}
            } catch(Exception e){
            	e.printStackTrace();
            }
			return subPartMEPs;
			}
		//[Google Custom]: 30763665 - Alternate MPN/Substitute MPN - Created by Sara on 14/02/2018 - End
		
		//[Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Start
		/**
		 * This method to get the extenstion of a file.
		 * @param inputFile
		 * @return String
		 * @author shajil
		 */
		public static String getFileExtension(File file){
			String fileExtension="";
			String fileName=file.getName();
			if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0){
				fileExtension=fileName.substring(fileName.lastIndexOf(".")+1);
			}
			return fileExtension;
		}
		
		/**
		 * This method will read csv file and convert to xls file.
		 * @param inputFile
		 * @return file
		 * @throws IOException
		 * @author shajil
		 */
		public static File readAndWriteFile(File inputFile) throws IOException {
			String line = null;
			FileReader fileReader = new FileReader(inputFile);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("BOMImport");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String filePath = inputFile.getParent();
			Date date = new Date();
			File xlsFile = new File(filePath+"\\Excel_BOM_"+date.getTime()+".xls");
			Row xlsRow = null;
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {
				xlsRow = sheet.createRow(count);
				String cells[] = line.split(",");
				boolean isStarted = false;
				boolean isEnded = false;
				StringBuilder concalVal = new StringBuilder();
				int cellCount = 0;
				for (String cellVal : cells) {
					String cellValue = "";
					Cell cell = null;
					if (UIUtil.isNotNullAndNotEmpty(cellVal)) {
						if (cellVal.contains("\"") && !isStarted) {
							isStarted = true;
							concalVal.append(cellVal).append(",");
						} else if (isStarted && !isEnded
								&& (!cellVal.contains("\""))) {
							concalVal.append(cellVal).append(",");
						} else if (cellVal.contains("\"") && isStarted) {
							concalVal.append(cellVal).append(",");
							isEnded = true;
							cell = xlsRow.createCell(cellCount);
							cellValue = concalVal.substring(0,concalVal.length() - 1).replaceAll("\"", "");
							cell.setCellValue(cellValue);
							cellCount = cellCount + 1;
							concalVal = new StringBuilder();
						} else if (isStarted && isEnded) {
							cell = xlsRow.createCell(cellCount);
							cellValue = cellVal;
							cell.setCellValue(cellValue);
							cellCount = cellCount + 1;
							isStarted = false;
							isEnded = false;
						} else {
							cellValue = cellVal;
							cell = xlsRow.createCell(cellCount);
							cell.setCellValue(cellValue);
							cellCount = cellCount + 1;
						}
					} else {
						cellValue = cellVal;
						cell = xlsRow.createCell(cellCount);
						cell.setCellValue("");
						cellCount = cellCount + 1;
						// Modified by Subbu for EBOM import issue -- starts 
						isStarted = false;
						isEnded = false;
						// Modified by Subbu for EBOM import issue -- ends 
					}
				}
				count = count + 1;
			}
			bufferedReader.close();
			FileOutputStream fileOut = new FileOutputStream(xlsFile);
			wb.write(fileOut);
			fileOut.close();
			return xlsFile;
		}
		//[Google Custom]: CSV- Import EBOM Structure - Modified by Shajil on 14/02/2018 - Ends
		
		/**
		 * This method used to check access privileges for CA Edit Properties commands.
		 * @param context
		 * @param args
		 * @return boolean
		 * @throws Exception
		 */
		public Boolean hasCAConnectAccess(Context context, String[] args)
				throws Exception {
			HashMap paramMap = (HashMap) JPO.unpackArgs(args);
			boolean bHasModifyAccess = false;
			String role_GoogAdminUser = PropertyUtil
					.getSchemaProperty(context,"role_GoogAdminUser");
			String role_googChangeMgtAdmin = PropertyUtil
					.getSchemaProperty(context,"role_googChangeMgtAdmin");
			String objectId = (String) paramMap.get("objectId");
			StringList objectList = new StringList();
			objectList.addElement(SELECT_ID);
			objectList.addElement(SELECT_CURRENT);
			objectList.addElement(SELECT_OWNER);
			Map caInfo = DomainObject.newInstance(context, objectId).getInfo(
					context, objectList);
			String changeActionState = (String) caInfo.get(SELECT_CURRENT);
			String owner = (String) caInfo.get(SELECT_OWNER);
			if ((changeActionState.equals(STATE_CHANGEACTION_PREPARE) || changeActionState
					.equals(STATE_CHANGEACTION_INWORK))
					&& (owner.equals(context.getUser())
							|| context.isAssigned(role_GoogAdminUser) || context
								.isAssigned(role_googChangeMgtAdmin))) {
				bHasModifyAccess = true;
			} 
			return bHasModifyAccess;
		}
		

	// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil
	// on 12/03/2018 - Starts
	/**
	 * This method used to generate Task table in Deviation CSV export
	 * 
	 * @param context
	 * @param taskInfo
	 * @param deviationId
	 * @throws FrameworkException
	 * @author shajil
	 */
	public void getTaskTableData(Context context, StringBuilder taskInfo, String deviationId)
			throws FrameworkException {
		taskInfo.append("\n");
		taskInfo.append("Tasks");
		taskInfo.append("\n");
		generateReportAgainstHeader(context, taskInfo, "Task");
		DomainObject deviationObj = DomainObject.newInstance(context, deviationId);
		googCustomIssue_mxJPO customFun = new googCustomIssue_mxJPO();
		MapList taskList = customFun.getDeviationTasks(context, deviationId);

		Iterator itr = taskList.iterator();
		while (itr.hasNext()) {
			Map dataMap = (Map) itr.next();
			generateTaskDetails(taskInfo, dataMap);
		}
	}

	/**
	 * This method used to generate Task table in Deviation CSV export
	 * 
	 * @param taskInfo
	 * @param dataMap
	 * @author shajil
	 */
	public void generateTaskDetails(StringBuilder taskInfo, Map dataMap) {
		String name = (String) dataMap.get(SELECT_NAME);
		String title = (String) dataMap.get(SELECT_ATTRIBUTE_TITLE);
		String state = (String) dataMap.get(SELECT_CURRENT);
		String instruction = (String) dataMap.get(SELECT_ATTRIBUTE_ROUTE_INSTRUCTIONS);
		String routeName = (String) dataMap.get(SELECT_ROUTE_NAME);
		String owner = (String) dataMap.get(SELECT_OWNER);
		String completionDate = (String) dataMap.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
		String actualDate = (String) dataMap.get(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
		String comments = (String) dataMap.get(SELECT_ATTRIBUTE_COMMENTS);

		taskInfo.append("\"" + name + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + title + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + state + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + instruction + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + routeName + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + owner + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + completionDate + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + actualDate + "\"");
		taskInfo.append(",");
		taskInfo.append("\"" + comments + "\"");
		taskInfo.append(",");
		taskInfo.append("\n");
	}

	/**
	 * This method used to generate Content table in Deviation CSV export
	 * 
	 * @param context
	 * @param partInfo
	 * @param sTableValues
	 * @author shajil
	 */
	public void getReportedAgainstContents(Context context, StringBuilder partInfo, MapList sTableValues) {
		partInfo.append("\n");
		partInfo.append("Content");
		partInfo.append("\n");
		generateReportAgainstHeader(context, partInfo, "ReportedAgainst");
		Iterator itr = sTableValues.iterator();
		while (itr.hasNext()) {
			Map partDetails = (Map) itr.next();
			String name = (String) partDetails.get(SELECT_NAME);
			String type = (String) partDetails.get(SELECT_TYPE);
			String rev = (String) partDetails.get(SELECT_REVISION);
			String description = (String) partDetails.get(SELECT_DESCRIPTION);
			String owner = (String) partDetails.get(SELECT_OWNER);
			String state = (String) partDetails.get(SELECT_CURRENT);
			String action = (String) partDetails.get(SELECT_ATTRIBUTE_ACTION);
			// [Google Custom]: Deviation Export Issue - Action value is exporting wrong Value - Modified by Sandeep on 23/08/2018 - Start
			if(UIUtil.isNotNullAndNotEmpty(action) && (!action.equalsIgnoreCase("Unassigned"))){
				action = EnoviaResourceBundle.getProperty(context, RESOURCE_BUNDLE_COMPONENTS_STR, context.getLocale(),"emxComponents.AffectedItemAction.Display."+action);
			}
			// [Google Custom]: Deviation Export Issue - Action value is exporting wrong Value - Modified by Sandeep on 23/08/2018 - End
			String trueEndItem = (String) partDetails.get(SELECT_ATTRIBUTE_googTrueEndItem);
			String qtyPerInst = (String) partDetails.get(SELECT_ATTRIBUTE_googQuantityPerInstance);
			String instPerVeh = (String) partDetails.get(SELECT_ATTRIBUTE_googInstancePerVehicle);
			String qtyInIntventory = (String) partDetails.get(SELECT_ATTRIBUTE_googQtyInInventory);
			String qtyInField = (String) partDetails.get(SELECT_ATTRIBUTE_googQtyInField);
			String qtyToMod = (String) partDetails.get(SELECT_ATTRIBUTE_googQtyToModify);
			String dispInStock = (String) partDetails.get(SELECT_ATTRIBUTE_DISPOSITION_INSTOCK);
			String disInField = (String) partDetails.get(SELECT_ATTRIBUTE_DISPOSITION_INFIELD);
			String dispOnOrder = (String) partDetails.get(SELECT_ATTRIBUTE_DISPOSITION_INORDER);
			String deltaEst = (String) partDetails.get(SELECT_ATTRIBUTE_googPiecePriceDeltaEstimate);
			String piecePriceDelta = (String) partDetails.get(SELECT_ATTRIBUTE_googPiecePriceDelta);
			String changeName = (String) partDetails.get("ChangeName");
			String changeStates = (String) partDetails.get("ChangeState");
			String requestedChange = (String) partDetails.get("RequestedChange");
			partInfo.append("\"" + name + "\"");
			partInfo.append(",");
			partInfo.append("\"" + type + "\"");
			partInfo.append(",");
			partInfo.append("\"" + rev + "\"");
			partInfo.append(",");
			partInfo.append("\"" + description + "\"");
			partInfo.append(",");
			partInfo.append("\"" + state + "\"");
			partInfo.append(",");
			partInfo.append("\"" + owner + "\"");
			partInfo.append(",");
			partInfo.append("\"" + changeName + "\"");
			partInfo.append(",");
			partInfo.append("\"" + changeStates + "\"");
			partInfo.append(",");
			partInfo.append("\"" + requestedChange + "\"");
			partInfo.append(",");
			partInfo.append("\"" + action + "\"");
			partInfo.append(",");
			partInfo.append("\"" + trueEndItem + "\"");
			partInfo.append(",");
			partInfo.append("\"" + qtyPerInst + "\"");
			partInfo.append(",");
			partInfo.append("\"" + instPerVeh + "\"");
			partInfo.append(",");
			partInfo.append("\"" + qtyInIntventory + "\"");
			partInfo.append(",");
			partInfo.append("\"" + qtyInField + "\"");
			partInfo.append(",");
			partInfo.append("\"" + qtyToMod + "\"");
			partInfo.append(",");
			partInfo.append("\"" + dispInStock + "\"");
			partInfo.append(",");
			partInfo.append("\"" + disInField + "\"");
			partInfo.append(",");
			partInfo.append("\"" + dispOnOrder + "\"");
			partInfo.append(",");
			partInfo.append("\"" + piecePriceDelta + "\"");
			partInfo.append(",");
			partInfo.append("\"" + deltaEst + "\"");
			partInfo.append(",");
			partInfo.append("\n");

		}
	}

	/**
	 * This method used to generate Headers Deviation CSV export
	 * 
	 * @param context
	 * @param partInfo
	 * @param tableName
	 * @author shajil
	 */
	public void generateReportAgainstHeader(Context context, StringBuilder partInfo, String tableName) {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		try {
			Map<String, String> pageInfo = customIssue.getPageInfo(context, "googIssueMapping");
			String tableSettingName = pageInfo.get("Deviation.Columns." + tableName);
			FrameworkUtil.split(tableName, ",");
			String columns[] = tableSettingName.split(",");
			for (String columnName : columns) {
				partInfo.append("\"" + columnName + "\"");
				partInfo.append(",");
			}
			partInfo.append("\n");
		} catch (FrameworkException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method used to generate Content table in Deviation CSV export
	 * 
	 * @param context
	 * @param sCAObjectId
	 * @return MapList
	 * @throws Exception
	 * @author shajil
	 */
	public MapList getReportedAgainstTableData(Context context, String sCAObjectId) throws Exception {
		StringList relSelects = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		StringList objectSelects = new StringList(4);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
		objectSelects.add(DomainConstants.SELECT_OWNER);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add("vcfile");
		objectSelects.add("vcfolder");
		// [Google Custom]: Deviation Export Issue - Declared Mass & Weighted Mass Column value is not Exporting - Modified by Sandeep on 21/08/2018 - Start
		objectSelects.add(SELECT_ATTRIBUTE_DECLAREDMASS_INPUTVALUE);
		objectSelects.add(SELECT_ATTRIBUTE_WEIGHTEDMASS_INPUTVALUE);
		objectSelects.add(SELECT_ATTRIBUTE_WEIGHTEDMASS_INPUTUNIT);
		objectSelects.add(SELECT_ATTRIBUTE_DECLAREDMASS_INPUTUNIT);
		// [Google Custom]: Deviation Export Issue - Declared Mass & Weighted Mass Column value is not Exporting - Modified by Sandeep on 21/08/2018 - End
		relSelects.add(SELECT_ATTRIBUTE_ACTION);
		relSelects.add(SELECT_ATTRIBUTE_googTrueEndItem);
		relSelects.add(SELECT_ATTRIBUTE_googQuantityPerInstance);
		relSelects.add(SELECT_ATTRIBUTE_googInstancePerVehicle);
		relSelects.add(SELECT_ATTRIBUTE_googQtyInInventory);
		relSelects.add(SELECT_ATTRIBUTE_googQtyInField);
		relSelects.add(SELECT_ATTRIBUTE_googQtyToModify);
		relSelects.add(SELECT_ATTRIBUTE_DISPOSITION_INFIELD);
		relSelects.add(SELECT_ATTRIBUTE_DISPOSITION_INSTOCK);
		relSelects.add(SELECT_ATTRIBUTE_DISPOSITION_INORDER);
		relSelects.add(SELECT_ATTRIBUTE_googPiecePriceDelta);
		relSelects.add(SELECT_ATTRIBUTE_googPiecePriceDeltaEstimate);

		DomainObject deviationObj = DomainObject.newInstance(context, sCAObjectId);
		short sh = 1;
		// [Google Custom]: "Deviation Module - Export issue" - Modified by Shajil/Sara on 24/04/2018 - Start
		MapList relBusObjPageList = deviationObj.getRelatedObjects(context, REL_ISSUE, DomainObject.QUERY_WILDCARD, objectSelects,
				relSelects, false, true, sh, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, 0);
		// [Google Custom]: "Deviation Module - Export issue" - Modified by Shajil/Sara on 24/04/2018 - End
		StringBuilder partDetails = new StringBuilder();
		Iterator itr = relBusObjPageList.iterator();

		while (itr.hasNext()) {
			Map partInfo = (Map) itr.next();
			String objectId = (String) partInfo.get(SELECT_ID);
			MapList affectedObjects = getConnectedChanges(context, objectId);
			Iterator changeItr = affectedObjects.iterator();
			StringBuilder stateNames = new StringBuilder();
			StringBuilder names = new StringBuilder();
			StringBuilder reguestedChanges = new StringBuilder();
			while (changeItr.hasNext()) {
				Map affectedData = (Map) changeItr.next();
				String state = (String) affectedData.get(SELECT_CURRENT);
				String itemName = (String) affectedData.get(SELECT_NAME);
				String changeObjId = (String) affectedData.get(SELECT_ID);
				stateNames.append(state);
				stateNames.append(" [");
				stateNames.append(itemName);
				stateNames.append(" ]");
				stateNames.append("  ");
				names.append(itemName);
				names.append("  ");

				String type = (String) affectedData.get(SELECT_TYPE);
				String proposedRelId = (String) affectedData.get(SELECT_RELATIONSHIP_ID);
				if (type.equals(TYPE_CHANGEACTION)) {
					MapList proposedList = new ChangeAction(changeObjId).getAffectedItems(context);
					Iterator proposedItr = proposedList.iterator();
					while (proposedItr.hasNext()) {
						Map proposedInfo = (Map) proposedItr.next();
						String relId = (String) proposedInfo.get(SELECT_RELATIONSHIP_ID);
						if (relId.equals(proposedRelId)) {
							String requestedChange = (String) proposedInfo.get(ATTRIBUTE_REQUESTED_CHANGE);
							reguestedChanges.append(requestedChange);
							reguestedChanges.append(" [");
							reguestedChanges.append(itemName);
							reguestedChanges.append("] ");
							reguestedChanges.append(" ");
							break;
						}
					}
				}
			}
			partInfo.put("ChangeName", names.toString());
			partInfo.put("ChangeState", stateNames.toString());
			partInfo.put("RequestedChange", reguestedChanges.toString());
		}
		return relBusObjPageList;
	}
	// [Google Custom]: 31375193 - Deviation Summary and Export - Created by Shajil
	// on 12/03/2018 - Ends
	
	// [Google Custom]: 31240889 CA Add Download Package Utility - Created by Sara on 29/03/2018 - Start
	/**
	 * To resolve 31240889 - CA Add Download Package Utility
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Sara
	 */
	public HashMap getZippedPackageForCA(Context context, String[] args)throws Exception {
		HashMap returnMap=new HashMap();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		MapList displaymapList = (MapList) programMap.get("displaymapList");
		String fileObjs[] =(String[]) programMap.get("fileObjs");
		String strWorkspace = context.createWorkspace();
		StringList objectSelects = new StringList(SELECT_NAME);
		String sChangeId=(String)programMap.get("sChangeId");
		DomainObject doj=new DomainObject(sChangeId);
		String sCAName  = doj.getInfo(context,SELECT_NAME);
		String outZipFolder=null;
		String sourceFolder=null;
		
		try{
		for (int i=0;i<fileObjs.length;i++){
			String sFilePath = fileObjs[i];
		    int index=sFilePath.lastIndexOf('\\');
		    String sOut=sFilePath.substring(0,index);
			StringBuilder sb = new StringBuilder();
			sb.append(strWorkspace);
			sb.append("\\");
			sb.append(sCAName);
			sourceFolder = sb.toString();
			sb.append("\\");
			sb.append(sOut);
			sb.append("\\");
			String sOutputPath = sb.toString();
			File file = new File(sOutputPath);
			file.mkdirs();
			
			if(displaymapList.size()>0) {
				Iterator itr = displaymapList.iterator();
				while(itr.hasNext()) {
				Map childMap = (Map) itr.next();
				String checkbox = (String) childMap.get("checkbox");
				if(sFilePath.equalsIgnoreCase(checkbox)){
					String specId =  (String) childMap.get("specId");
					String sFileName =  (String) childMap.get("name");
					DomainObject doSpec = new DomainObject(specId);
					// [Google Custom]: 31240889 CA Add Download Package Utility (Format Issue) - Modified by Sara on 22/05/2018 - Start
					String sFormat =  (String) childMap.get("format");
					//doSpec.checkoutFile(context, false, DomainConstants.FORMAT_GENERIC, sFileName, sOutputPath);
					doSpec.checkoutFile(context, false, sFormat ,sFileName, sOutputPath);
					// [Google Custom]: 31240889 CA Add Download Package Utility (Format Issue) - Modified by Sara on 22/05/2018 - End
				}
			    }
			}
		}} catch(Exception e){
			e.printStackTrace();
		} 
		
		
		outZipFolder = sourceFolder + ".zip";
		returnMap.put("outZipFolder", outZipFolder);
		returnMap.put("sourceFolder", sourceFolder);
		returnMap.put("Workspace", strWorkspace);

		// To Retrieve name of the folder
		int index = outZipFolder.lastIndexOf("\\");
		String strWsName = outZipFolder.substring(index + 1);
		returnMap.put("strWsName", strWsName);

		// To zip the files and folders
		File inputDirectory = new File(sourceFolder);
		File outputZip = new File(outZipFolder);
		outputZip.getParentFile().mkdirs();
		List listFiles = new ArrayList();
		listFiles(listFiles, inputDirectory);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZip));
		createZipFile(listFiles, inputDirectory, zipOutputStream);
		return returnMap;
	}
	
	/**
	 * To resolve 31240889 - CA Add Download Package Utility
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Sara
	 */
	public HashMap getActionBasedPartList(Context context, String[] args) throws Exception {
		HashMap returnMap = new HashMap();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		StringList slRelIdList = (StringList) programMap.get("slRelIdList");
		StringList sPartList = (StringList) programMap.get("sPartList");
		String[] relIds = new String[slRelIdList.size()];
		StringList relSelectionList = new StringList();
		relSelectionList.add("to."+SELECT_ATTRIBUTE_ACTION);//Modified for Proposed Activity Rel to Object Migration - By Ravindra
		StringList sIncludeList = new StringList();
		StringList sExcludeList = new StringList();
		StringList sExcludePartNames = new StringList();

		for (int j = 0; j < slRelIdList.size(); j++) {
			relIds[j] = (String) slRelIdList.get(j);
		}

		MapList sRelationshipDetail = DomainRelationship.getInfo(context, relIds, relSelectionList);
		for (int k = 0; k < sPartList.size(); k++) {
			try {
				
				Map sRelMap = (Map) sRelationshipDetail.get(k);
				String sAction = (String) sRelMap.get("to."+SELECT_ATTRIBUTE_ACTION);//Modified for Proposed Activity Rel to Object Migration - By Ravindra
				String sPartId = (String) sPartList.get(k);
				if (!sAction.equals("Delete") && !sAction.equals("")) {
					sIncludeList.add(sPartId);
				} else {
					sExcludeList.add(sPartId);
					DomainObject doj = new DomainObject(sPartId);
					String sPartName = doj.getInfo(context, SELECT_NAME);
					sExcludePartNames.add(sPartName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		returnMap.put("sIncludeList", sIncludeList);
		returnMap.put("sExcludeList", sExcludeList);
		returnMap.put("sExcludePartNames", sExcludePartNames);
		return returnMap;
	}
	
	/**
	 * To resolve 31240889 - CA Add Download Package Utility
	 * @param listFiles
	 * @param inputDirectory
	 * @param zipOutputStream
	 * @throws IOException
	 * @author Sara
	 */
	private static void createZipFile(List<File> listFiles, File inputDirectory, ZipOutputStream zipOutputStream)
			throws IOException {
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
	}

	/**
	 * To resolve 31240889 - CA Add Download Package Utility
	 * @param listFiles
	 * @param inputDirectory
	 * @return List
	 * @throws IOException
	 * @author Sara
	 */
	private static List listFiles(List listFiles, File inputDirectory) throws IOException {
		File[] allFiles = inputDirectory.listFiles();
		for (File file : allFiles) {
			if (file.isDirectory()) {
				listFiles(listFiles, file);
			} else {
				listFiles.add(file);
			}
		}
		return listFiles;
	}
	// [Google Custom]: 31240889 CA Add Download Package Utility - Created by Sara on 29/03/2018 - End
	
	
	
	
	/**
	 * This method used to block the Release part modification - Ticket# :31790104.
	 * @param context
	 * @param args
	 * @return HashMap
	 * @throws Exception
	 * @author Shajil/Sara
	 */
	public HashMap isPartReleased(Context context, String[] args) throws Exception {
		//Added for Edit in Complete State by Preethi Rajaraman -- Starts
		String role_googPLMAnalyst = PropertyUtil
				.getSchemaProperty(context,"role_googPLMAnalyst");
		//Added for Edit in Complete State by Preethi Rajaraman -- Ends
		HashMap returnMap = new HashMap();
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap requestMap = (HashMap) programMap.get("requestMap");
		HashMap tableData = (HashMap) programMap.get("tableData");
		MapList columnsList = (MapList) tableData.get("columns");
		Document doc = (Document) programMap.get("XMLDoc");
		com.matrixone.jdom.Element rootElement = doc.getRootElement();
		MapList changeMapList = UITableIndented.getChangedRowsMapFromElement(context, rootElement);
		Iterator itr = changeMapList.iterator();
		
		while (itr.hasNext()) {
			Map changeInfo = (Map) itr.next();
			String objectId = (String) changeInfo.get("oid");
			String state = DomainObject.newInstance(context, objectId).getInfo(context, SELECT_CURRENT);
			//Modified for Edit in Complete State by Preethi Rajaraman -- Starts
			if (state.equals(STATE_ECPART_RELEASE) && !context
							.isAssigned(role_googPLMAnalyst)) {
			//Modified for Edit in Complete State by Preethi Rajaraman -- Ends	
				Map coulumnns = (Map) changeInfo.get("columns");
				Set keyName = coulumnns.keySet();
				Iterator keyItr = keyName.iterator();
				while (keyItr.hasNext()) {
					String keyValName = (String) keyItr.next();
					Iterator columnItr = columnsList.iterator();
					while (columnItr.hasNext()) {
						Map column = (Map) columnItr.next();
						String colKeyName = (String) column.get("name");
						if (colKeyName.equals(keyValName)) {
							if (column.containsKey("expression_businessobject")) {
								String error = EnoviaResourceBundle.getProperty(context,
												"emxFrameworkStringResource",
												context.getLocale(),
												"emxFramework.Message.BlockReleasePart");
								returnMap.put("Action", "STOP");
								returnMap.put("Message", error);
								return returnMap;
							}
						}
					}
				}
			}
		}
		return returnMap;
	}
	
	// [Google Custom]: 31240889 CA Add Download Package Utility - Created by Sara on 03/05/2018 - Start
	/**
	 * To Resolve the Add Download Package utility for New Parts command - 31240889
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Sara
	 */
	public String[] getConnectedPartList(Context context, String[] args) throws Exception {
		MapList sConnectedPartList = new enoECMChangeAction_mxJPO(context, args).getAffectedItems(context, args);
		int size = sConnectedPartList.size();
		String[] emxTableRowIds = new String[size];
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				HashMap childMap = (HashMap) sConnectedPartList.get(i);
				String sRELId = (String) childMap.get(SELECT_RELATIONSHIP_ID);
				String sObjectId = (String) childMap.get(SELECT_ID);
				emxTableRowIds[i] = sRELId + "|" + sObjectId;
			}
		}
		return emxTableRowIds;
	}
	// [Google Custom]: 31240889 CA Add Download Package Utility - Created by Sara on 03/05/2018 - End
	
	// [Google Custom]: MEP BOM Update  - Created by Shajil on 08/05/2018 - Starts
	
	/**
	 * This method used to update MEP details with Part.
	 * @param context
	 * @param resultList
	 * @throws MatrixException
	 * @author shajil
	 */
	public void updateBOMWithMEP(Context context, MapList resultList) throws MatrixException {

		Iterator resultItr = resultList.iterator();
		boolean isRollBack = false;
		StringBuilder manufacturerMsg = new StringBuilder("Error on Parts(");
		while (resultItr.hasNext()) {
			Map bomData = (Map) resultItr.next();
			String mepACtionStatus = (String) bomData.get("ManufactureStatus");
			if (UIUtil.isNotNullAndNotEmpty(mepACtionStatus) && mepACtionStatus.equals("MEPConnect")) {
				connectMEP(context, bomData);

			} else if (UIUtil.isNotNullAndNotEmpty(mepACtionStatus) && mepACtionStatus.equals("MEPCreation")) {
				createAndConnectMEP(context, bomData);
			}
		}
	}

	/**
	 * This method used to validate the the Import details
	 * @param partDetails
	 * @return boolean
	 * @author shajil
	 */
	public boolean isValidMEPList(MapList partDetails) {
		if (partDetails.size() == 1) {
			Map details = (Map) partDetails.get(0);
			if (details.containsKey("ManufactureStatus")) {
				if (details.get("ManufactureStatus").equals("Error")) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method used to update MEP/Manufacturer details in the list
	 * @param context
	 * @param resultList
	 * @return MApList
	 * @throws MatrixException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @author shajil
	 */
	public MapList updateMEPDetails(Context context, MapList resultList)
			throws MatrixException, UnsupportedEncodingException, IOException {

		MapList bomList = new MapList();
		Iterator resultItr = resultList.iterator();
		boolean isValidManuFacturer = true;
		boolean isValidName = true;
		StringBuilder manufacturerMsg = new StringBuilder("Invalid Manufactures(");
		StringBuilder mepNameValidationMsg = new StringBuilder("Invalid MEP Name (");
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomValidation");
		String emxNameBadChars = pageInfo.get("emxFramework.Javascript.MEP.NameBadChars");
		while (resultItr.hasNext()) {
			Map bomData = (Map) resultItr.next();
			if (bomData.containsKey("MEP") && bomData.containsKey("Manufacturer")) {
				String mepName = (String) bomData.get("MEP");
				String manufacture = (String) bomData.get("Manufacturer");
				String partName = (String) bomData.get("Name");
				StringList slSelects = new StringList();
				slSelects.addElement(DomainObject.SELECT_ID);
				slSelects.addElement(DomainObject.SELECT_NAME);
				slSelects.addElement(DomainObject.SELECT_POLICY);
				slSelects.addElement(DomainObject.SELECT_REVISION);
				// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Starts
				slSelects.addElement(SELECT_ATTRIBUTE_ORGANIZATION_ID);
				// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Ends
				if (!isValidMEPName(mepName, emxNameBadChars)) {
					isValidName = false;
					if(!mepNameValidationMsg.toString().contains(mepName)) {
						mepNameValidationMsg.append(mepName).append(",");
					}
				}
				if (UIUtil.isNotNullAndNotEmpty(manufacture) || UIUtil.isNotNullAndNotEmpty(mepName)) {
					// [Google Custom : Issue :229]: Allow special characters in Manufacturer field  Modified by Shajil on 15/04/2019 - Starts
					//String refinedManufacture = manufacture.replaceAll("[^\\dA-Za-z. ]","").replaceAll("\\s+", "*");
					String refinedManufacture =manufacture.replace(" ", "*");
					// [Google Custom : Issue :229]: Allow special characters in Manufacturer field  Modified by Shajil on 15/04/2019 - Ends
					MapList objectList = DomainObject.findObjects(context, TYPE_PART + "," + TYPE_COMPANY, // Type
							mepName + "," + refinedManufacture, // name
							DomainObject.QUERY_WILDCARD, // revision
							DomainObject.QUERY_WILDCARD, // owner
							DomainObject.QUERY_WILDCARD, // Vault
							null, // Where
							true, // expand sub type
							slSelects); // Selects

					Iterator itr = objectList.iterator();
					String companyId = null;
					String mepId = null;
				// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Starts
					String organizationId = DomainConstants.EMPTY_STRING;
				// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Ends	
				Map objectInfo;
				String type,policy,name;
					while (itr.hasNext()) {
						 objectInfo = (Map) itr.next();
						 type = (String) objectInfo.get(SELECT_TYPE);
						 policy = (String) objectInfo.get(SELECT_POLICY);
						name = (String) objectInfo.get(SELECT_NAME);
						//If there are more than one manufacturer then select the one given by the user in the input file.
						if (type.equals(TYPE_COMPANY) && manufacture.toUpperCase().equals(name.toUpperCase())) {
							companyId 		= (String) objectInfo.get(SELECT_ID);
			   // [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Starts			
							organizationId 	= (String) objectInfo.get(SELECT_ATTRIBUTE_ORGANIZATION_ID);
			  // [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - End				
						} else if (type.equals(TYPE_PART) && policy.equals(POLICY_MANUFACTURER_EQUIVALENT)) {
							mepId = (String) objectInfo.get(SELECT_ID);
						}

					}
					bomData.put("MEPId", mepId);
					bomData.put("ManufacturerId", companyId);
			// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Starts			
					bomData.put("organizationId", organizationId);
			// [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Ends		
					if (UIUtil.isNullOrEmpty(companyId)) {
						bomData.put("ManufactureStatus", "Error");
						if(!manufacturerMsg.toString().contains(manufacture)) {
							manufacturerMsg.append(manufacture).append(",");
						}
						bomData.put("ManufactureMessage",
								"Error on " + partName + " validate Manufacturer field value");
						isValidManuFacturer = false;
					} else if (UIUtil.isNullOrEmpty(mepId) && UIUtil.isNotNullAndNotEmpty(mepName)
							&& UIUtil.isNotNullAndNotEmpty(companyId)) {
						bomData.put("ManufactureStatus", "MEPCreation");
					} else if (UIUtil.isNotNullAndNotEmpty(mepId) && UIUtil.isNotNullAndNotEmpty(companyId)) {
						bomData.put("ManufactureStatus", "MEPConnect");
					}

				}
				bomList.add(bomData);
			} else {
				return resultList;
			}
		}
		if (!isValidManuFacturer) {
			manufacturerMsg.deleteCharAt(manufacturerMsg.length() - 1);
			manufacturerMsg.append(").");
			Map resultData = new HashMap();
			if (!isValidName) {
				mepNameValidationMsg.deleteCharAt(mepNameValidationMsg.length() - 1);
				mepNameValidationMsg.append(").");
				manufacturerMsg.append("\\n").append(mepNameValidationMsg.toString());
			}
			resultData.put("ManufactureMessage", manufacturerMsg.toString());
			resultData.put("ManufactureStatus", "Error");
			MapList resltList = new MapList();
			resltList.add(resultData);
			return resltList;
		}
		if (!isValidName) {
			mepNameValidationMsg.deleteCharAt(mepNameValidationMsg.length() - 1);
			mepNameValidationMsg.append(").");
			Map resultData = new HashMap();
			resultData.put("ManufactureMessage", mepNameValidationMsg.toString());
			resultData.put("ManufactureStatus", "Error");
			MapList resltList = new MapList();
			resltList.add(resultData);
			return resltList;
		}
		return bomList;
	}

	/**
	 * This method for MEP name validation
	 * @param mepName
	 * @param emxNameBadChars
	 * @return boolean
	 * @author shajil
	 */
	public boolean isValidMEPName(String mepName, String emxNameBadChars) {

		StringBuilder characterList = new StringBuilder();
		String[] badCharacterList = emxNameBadChars.split(" ");
		for (int i = 0; i < badCharacterList.length; i++) {
			if (mepName.indexOf(badCharacterList[i]) > -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method used to create MEP and connect with its Parts.
	 * @param context
	 * @param bomData
	 * @throws MatrixException
	 * @author shajil
	 */
	public void createAndConnectMEP(Context context, Map bomData) throws MatrixException {

		String mepName = (String) bomData.get("MEP");
		String partId = (String) bomData.get(SELECT_ID);
	   // [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Starts
		String organizationId =  "";
		if(bomData.containsKey("organizationId")) {
			organizationId = (String) bomData.get("organizationId");
		}
		String manufacturerId = (String) bomData.get("ManufacturerId");
		matrix.db.BusinessObject partObj = new matrix.db.BusinessObject(TYPE_PART, mepName, organizationId, null);
	  // [Google Custom]: MEP Revision issue - Modified by Sandeep on 07/02/2018 - Ends	 	
		try {
			String mepId = null;
			if (partObj.exists(context)) {
				mepId = partObj.getObjectId(context);
			} else {
				partObj.create(context, POLICY_MANUFACTURER_EQUIVALENT);
				mepId = partObj.getObjectId(context);
				//Added by Kalim to set the MEP description as PLM Part desc by default.
				 DomainObject domPartObj = DomainObject.newInstance(context,mepId);
				domPartObj.setDescription(context,(String) bomData.get("Description"));
				DomainRelationship.connect(context, manufacturerId, RELATIONSHIP_ALLOCATION_RESPONSIBILITY, mepId,
						true);
				DomainRelationship.connect(context, manufacturerId, RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, mepId,
						true);
			}
			DomainRelationship.connect(context, partId, RELATIONSHIP_MANUFACTURER_EQUIVALENT, mepId, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method used to connect MEP with Parts.
	 * @param context
	 * @param bomData
	 * @throws FrameworkException
	 */
	public void connectMEP(Context context, Map bomData) throws FrameworkException {

		String partId = (String) bomData.get(SELECT_ID);
		String partName = (String) bomData.get("Name");
		String mepId = (String) bomData.get("MEPId");
		StringList partList = DomainObject.newInstance(context, mepId).getInfoList(context,
				SELECT_RELATIONSHIP_MANUFACTURER_EQUIVALENT_FROM_NAME);
		if (!partList.contains(partName)) {
			DomainRelationship manufacturerEquivalentRel = DomainRelationship.connect(context,
					DomainObject.newInstance(context, partId), DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT,
					DomainObject.newInstance(context, mepId));
		}
	}

	// [Google Custom]: MEP BOM Update  - Created by Shajil on 08/05/2018 - Starts
	
		
	// [Google Custom]: 32670705 - CAD Model Policy - Created by Sara on 11/05/2018 - Start
	/**
	 * To resolve CAD Model Policy display - 32670705
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Sara
	 */
	public String getCADModelPolicies(Context context, String[] args)
			throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		StringBuffer sbPolicy = new StringBuffer(64);
		sbPolicy.append("<select name=\"Policy\">");
		HashMap srequestMap = (HashMap) programMap.get("requestMap");
		String sType = (String) srequestMap.get("type");
		String sObjectId = (String) srequestMap.get("objectId");
		if (UIUtil.isNotNullAndNotEmpty(sObjectId)){
			try{
				String name[] = sType.split(",");
				String sValue = name[0];
					if(sValue.contains(":")){
						String s1[] =  sValue.split(":");
						sType =  s1[(s1.length) - 1];
					} else {
						sType = sValue;
					}
			}catch(Exception e){
				e.printStackTrace();
			}
		} else{
		try {
			if (sType.contains(",")) {
				String name[] = sType.split(",");
				sType = name[(name.length) - 1];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

		String sPolicy = (String) srequestMap.get("policy");
		String sAdminTypePolicySysmbolicName = PropertyUtil.getSchemaProperty(context, sPolicy);
		String sAdminPolicy = null;
		String sPolicySelected = "selected='true'";
		String specPolicyName = "";
		String strPolicy = "";
		String sTypeSysmbolicName = null;

	
			if(sType.contains("type_")){
				 sTypeSysmbolicName = PropertyUtil.getSchemaProperty(context,sType);
			}else{
				 sTypeSysmbolicName = sType;
			}
			
			// Get the policies associated with the specification
			BusinessType boj = new BusinessType(sTypeSysmbolicName,context.getVault());
			boj.open(context);
			
			// Get the policies of that Object
			PolicyList specPolicyList = boj.getPolicies(context);
			List policies = specPolicyList.getTypedList();
			
			boolean sFlag = false;
			PolicyItr specPolicyItr = new PolicyItr(specPolicyList);
			while (specPolicyItr.next()) {
				Policy specPolicy1 = specPolicyItr.obj();
				String specPolicyName1 = specPolicy1.getName();
				if(specPolicyName1.equals("CAD Model")){
					sAdminPolicy = specPolicyName1;
					sFlag = true;
					break;
				}
				
				if(specPolicyName1.equals("CAD Drawing")){
					sAdminPolicy = specPolicyName1;
					sFlag = true;
					break;
				}
				
			}

			if(sFlag){
				sbPolicy.append("<option value=\""+sAdminPolicy+"\""+">"+ sAdminPolicy + "</option>");
			} else {
			PolicyItr specPolicyItr1 = new PolicyItr(specPolicyList);
			while (specPolicyItr1.next()) {
				Policy specPolicy = specPolicyItr1.obj();
				specPolicyName = specPolicy.getName();
				sbPolicy.append("<option value=\""
						+ specPolicyName
						+ "\" "
						+ ((sAdminTypePolicySysmbolicName.equals(specPolicyName)) ? sPolicySelected : "" )
						+ " >" + specPolicyName + "</option>");
			}
			}
			boj.close(context);

		sbPolicy.append("</select>");
		strPolicy = sbPolicy.toString();
		return strPolicy;
	}
	// [Google Custom]: 32670705 - CAD Model Policy - Created by Sara on 11/05/2018 - End
	
	// [Google Custom]: 32940147 - Companies Related Parts - Created by Syed on 25/05/2018 - Starts
	/**
	 * To display Companies Related Parts - 32940147
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Syed
	 */
	public MapList getRelatedMEPParts(Context context, String[] args) throws Exception {
		MapList mepPartsObjectList = new MapList();
		try {
			HashMap programMap = JPO.unpackArgs(args);
			String companyId = (String) programMap.get("objectId");
			if (UIUtil.isNotNullAndNotEmpty(companyId)) {
				DomainObject companyObj = DomainObject.newInstance(context, companyId);
				StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_NAME);
				Pattern relPattern = new Pattern(RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);
				Pattern typePattern = new Pattern(DomainConstants.TYPE_PART);
				String objwhere = DomainConstants.SELECT_POLICY + "=='" + POLICY_MANUFACTURER_EQUIVALENT + "'";
				mepPartsObjectList = companyObj.getRelatedObjects(context, relPattern.getPattern(),
						typePattern.getPattern(), objectSelects, null, false, true, (short) 1, objwhere, null, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mepPartsObjectList;
	}

	/**
	 * To fetch column values for State for export issue - 32940147
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 * @author Syed
	 */
	public Vector getRelatedStateName(Context context, String[] args) throws Exception {
		Vector columnValues = new Vector();
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap objectParam = (HashMap) programMap.get("paramList");
			MapList objectList = (MapList) programMap.get("objectList");
			String languageStr = (String) objectParam.get("languageStr");
			String exportFormat = (String) objectParam.get("exportFormat");
			String tempPolicy = "";
			for (int j = 0; j < objectList.size(); j++) {
				Map mepObject = null;
				mepObject = (Map) objectList.get(j);
				String manufacturerId = (String) mepObject.get("id");
				if (UIUtil.isNotNullAndNotEmpty(manufacturerId)) {
					DomainObject mepObj = DomainObject.newInstance(context, manufacturerId);
					StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
					objectSelects.add(DomainConstants.SELECT_NAME);
					objectSelects.add(DomainConstants.SELECT_CURRENT);
					objectSelects.add(DomainConstants.SELECT_POLICY);
					Pattern relPattern = new Pattern(RELATIONSHIP_MANUFACTURER_EQUIVALENT);
					Pattern typePattern = new Pattern(DomainConstants.TYPE_PART);

					MapList gpnObjectList = mepObj.getRelatedObjects(context, relPattern.getPattern(),
							typePattern.getPattern(), objectSelects, null, true, false, (short) 0, null, null, 0);

					String gpnState = "";
					String gpnPolicy = "";
					Map gpnObjectMap = null;
					StringBuilder gpnBuilder = new StringBuilder();

					if (gpnObjectList.size() > 0) {
						for (int i = 0; i < gpnObjectList.size(); i++) {
							gpnObjectMap = (Map) gpnObjectList.get(i);
							gpnState = (String) gpnObjectMap.get(DomainConstants.SELECT_CURRENT);
							gpnPolicy = (String) gpnObjectMap.get(DomainConstants.SELECT_POLICY);
							gpnBuilder.append(i18nNow.getStateI18NString(gpnPolicy, gpnState, languageStr));
							if ((!UIUtil.isNullOrEmpty(exportFormat)) && exportFormat.equalsIgnoreCase("CSV")) {
								gpnBuilder.append("\r\n");
							} else {
								gpnBuilder.append("<br/>");
							}
						}
					}
					columnValues.addElement(gpnBuilder.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columnValues;
	}
	// [Google Custom]: 32940147 - Companies Related Parts - Created by Syed on 25/05/2018 - End

	// [Google Custom]: 32917138 | Change CA Build call out from a range to a string or multi-select - Modified by Sara - 14/06/2018 - Start	
	/**
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * @author Sara
	 */
	public Object getBuildValuesFromPageObject(Context context, String[] args) throws Exception {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googBuildEventList");
		String attrRangeValues = pageInfo.get("googBuildEventListOptions");
		StringList attrRangeList = FrameworkUtil.split(attrRangeValues, ",");
		MapList attrList = new MapList();
		Map childMap = null;
		for (int j = 0; j < attrRangeList.size(); j++) {
			String arrName = (String) attrRangeList.get(j);
			childMap = new HashMap();
			childMap.put("Name", arrName);
			childMap.put("Description", "Build Event Attribute");
			childMap.put("id", arrName);
			attrList.add(childMap);
		}
		return attrList;
	}
	
	/**
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws Exception
	 * @author Syed
	 */
	public Vector getBuildEventNameColumnValue(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		Map paramList = (HashMap) programMap.get("paramList");
		HashMap columnMap = (HashMap) programMap.get("columnMap");
		HashMap settingsMap = (HashMap) columnMap.get("settings");
		String selectable = (String) settingsMap.get("SelectExp");
		MapList changeObjectList = (MapList) programMap.get("objectList");
		String changeObjectId = (String) paramList.get("objectId");
		Vector changeAttrValues = new Vector();
		if (UIUtil.isNotNullAndNotEmpty(changeObjectId)) {
			DomainObject changeObj = new DomainObject(changeObjectId);
			String changeAttributeValue = changeObj.getInfo(context, SELECT_ATTRIBUTE_BUILD_EVENT);
			StringList changeAttrList = FrameworkUtil.split(changeAttributeValue, ",");
			String checked = "checked";
			Iterator changeItr = changeObjectList.iterator();
			while (changeItr.hasNext()) {
				StringBuilder strOptions = new StringBuilder();
				Map childMap = (Map) changeItr.next();
				String attrName = (String) childMap.get(selectable);
				strOptions.append("<input type=\"checkbox\" ").append("  id=\"").append(attrName).append("\" name=\"")
						.append(attrName).append("\" value=\"").append(attrName).append("\"");
				if (changeAttrList.contains(attrName)) {
					strOptions.append(" checked=\"").append(checked).append("\"");
				}
				strOptions.append("/>");
				strOptions.append(attrName);
				strOptions.append("<br/>");
				changeAttrValues.addElement(strOptions.toString());
			}
		}
		return changeAttrValues;
	}
	
	/**
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws Exception
	 * @author Sara
	 */
	public Vector getBuildEventAttributeColumnValue(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		HashMap columnMap = (HashMap) programMap.get("columnMap");
		HashMap settingsMap = (HashMap) columnMap.get("settings");
		String selectable = (String) settingsMap.get("SelectExp");
		MapList attrObjectList = (MapList) programMap.get("objectList");
		Vector attrValues = new Vector();
		Iterator itr = attrObjectList.iterator();
		while (itr.hasNext()) {
			Map childMap = (Map) itr.next();
			String attrName = (String) childMap.get(selectable);
			attrValues.add(attrName);
		}
		return attrValues;
	}
	// [Google Custom]: 32917138 | Change CA Build call out from a range to a string or multi-select - Modified by Sara - 14/06/2018 - End
	

	// [Google Custom]:  used to format the String value if the String Value contain "," or "\n" - Created by Sanddep - 24/08/2018 - Starts
	/**
	 * this Method is used to format the String value if the String Value contains"," or "\n" 
	 * @param String displayValues
	 * @return String displayValues
	 * @author Sandeep 
	 */
	public String formatStringValues(String displayValues) {
		if (UIUtil.isNotNullAndNotEmpty(displayValues)) {
			if (!(displayValues.indexOf(',') == -1 && displayValues.indexOf('\n') == -1)) {
				if (displayValues.contains("\"")) {
					displayValues = FrameworkUtil.findAndReplace(displayValues, "\"", "\"\"");
				}
				displayValues = "\"" + displayValues + "\"";
			}
		} else {
			displayValues = DomainConstants.EMPTY_STRING;
		}
		return displayValues;
	}

	/**
	 * this Method is used to format the String value if the String Value contains"," or "\n" 
	 * @param Map
	 * @return Map returnMap
	 * @author Sandeep 
	 */
	public Map formatMapStringValues(Map displayValueMap) {
		Map returnMap = new HashMap();
		//[Google Custom]#34626910 Modified for EBOM CSV Export issue by Syed -Start 
		String displayValue = "";
		Set keyName = displayValueMap.keySet();
		googCustomReports_mxJPO customReports = new googCustomReports_mxJPO();
		Iterator keyItr = keyName.iterator();
		while (keyItr.hasNext()) {
			String keyValName = (String) keyItr.next();
			customReports.updateMapDetails(displayValueMap, keyValName);
			//[Google Custom]#34626910 Modified for EBOM CSV Export issue by Syed -End 	
			displayValue = formatStringValues((String)displayValueMap.get(keyValName));
			returnMap.put(keyValName, displayValue);
		}
		return returnMap;
	}


	/**
	 * this Method is used to format the String value if the String Value contains"," or "\n" 
	 * @param MapList
	 * @return MapList returnMapList
	 * @author Sandeep 
	 */
	public MapList formatMaplistStringValues(MapList displayValueList) {
		MapList returnMapList = new MapList();
		for (int listSize = 0; listSize < displayValueList.size(); listSize++) {
			Map displayValueMap = formatMapStringValues((Map) displayValueList.get(listSize));
			returnMapList.add(displayValueMap);
		}
		return returnMapList;
	}
	//[Google Custom]#34626910 Added for Command googMassPromotePartConnectedObjects by Subbu @ 27/08/2018 -Start  
	/**
	 * This method is used to mass promote all Specs and MEPs connected to selected Parts
	 * @return Map statusMap
	 * Created by Subbu on 27/08/2018
	 * Modified by Subbu on 03/10/2018
	 */
	public Map massPromoteSpecsAndMEP(Context context, String[] args) throws Exception {
		Map statusMap = new HashMap();
		try {
			MapList finalList = new MapList();
			Map paramMap = (Map) JPO.unpackArgs(args);
			String selectedObjIds = (String) paramMap.get("partMap");
			StringList selectedObjectList = (StringList) FrameworkUtil.split(selectedObjIds, "|");
			String rootId = (String) paramMap.get("RootId");
			Locale locale = context.getSession().getLocale();
			String selectedPartName = DomainConstants.EMPTY_STRING;
			String objectPolicy = DomainConstants.EMPTY_STRING;
			String objectState = DomainConstants.EMPTY_STRING;
			String connectedObjId = DomainConstants.EMPTY_STRING;
			String objectType = DomainConstants.EMPTY_STRING;
			String objectName = DomainConstants.EMPTY_STRING;
			Map objectMap = null;
			boolean failureCase = false;
			boolean skippedCase = false;
			boolean promotedSucessfully = false;
			boolean isAlreadyReleased = false;
			String objType = DomainConstants.EMPTY_STRING;
			String objPolicy = DomainConstants.EMPTY_STRING;
			String objName = DomainConstants.EMPTY_STRING;
			String objID = DomainConstants.EMPTY_STRING;
			String objLevel = DomainConstants.EMPTY_STRING;
			String objState = DomainConstants.EMPTY_STRING;
			Map relatedMap = new HashMap();
			MapList processedList = new MapList();
			MapList failedList = new MapList();
			Map failedObjectsMap = new HashMap();
			MapList partList = new MapList();
			String keyPromotion = "Promotion";
			String keyStatus = "Status";
			String valueFailed = "Failed";
			String valueSuccess = "Sucess";
			String valueSkipped = "Skipped";
			String valueAlreadyReleased = "Objects Already Released";
			String keyFailedList = "FailedList";
			String failureReason = DomainConstants.EMPTY_STRING;
			Map statusInfo = new HashMap();
			String status = DomainConstants.EMPTY_STRING;
			String skippedMessage = EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource",
					locale, "emxEngineeringCentral.alertMessage.Skipped");
			MapList partConnectedObjects = null;

			StringList objectSelects = new StringList();
			objectSelects.add(SELECT_ID);
			objectSelects.add(SELECT_CURRENT);
			objectSelects.add(SELECT_POLICY);
			objectSelects.add(SELECT_TYPE);
			objectSelects.add(SELECT_NAME);
			objectSelects.add(SELECT_REVISION);

			Pattern relPattern = new Pattern(REL_PART_SPECIFICATION);
			relPattern.addPattern(REL_MFG_EQUIVALENT);
			relPattern.addPattern(RELATIONSHIP_EBOM);

			StringList relSelect = new StringList(SELECT_RELATIONSHIP_ID);
			String objId = DomainConstants.EMPTY_STRING;
			Map dataMap = new HashMap();

			StringList objectSelectables = new StringList();
			DomainObject domParentObj = DomainObject.newInstance(context, rootId);

			partConnectedObjects = domParentObj.getRelatedObjects(context, relPattern.getPattern(), // relationship
					// pattern
					QUERY_WILDCARD, // TYPE pattern
					objectSelects, // object selects
					relSelect, // relationship selects
					false, // to direction
					true, // from direction
					(short) 0, // recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING, // rel where clause
					0);// Max limit

			if (partConnectedObjects.size() > 0) {
				Iterator<Map> partitr = partConnectedObjects.iterator();
				while (partitr.hasNext()) {
					objectMap = (Map) partitr.next();
					objectPolicy = (String) objectMap.get(SELECT_POLICY);
					objectType = (String) objectMap.get(SELECT_TYPE);
					objectName = (String) objectMap.get(SELECT_NAME);
					connectedObjId = (String) objectMap.get(SELECT_ID);
					objLevel = (String) objectMap.get(SELECT_LEVEL);
					objectState = (String) objectMap.get(SELECT_CURRENT);

					if (selectedObjectList.contains(rootId)) {
						if (objLevel.equals("1") && !objectPolicy.equalsIgnoreCase(POLICY_EC_PART)
								&& !(TYPE_VPMREFERENCE.equalsIgnoreCase(objectType))) {
							if (!(STATE_DOCUMENTRELEASE_RELEASED.equalsIgnoreCase(objectState)
									|| STATE_MFGEQUIVALENT_RELEASED.equalsIgnoreCase(objectState))) {
								statusInfo = massPromoteSpecsMEP(context, objectMap);
								status = (String) statusInfo.get("Status");
								if ("Sucess".equalsIgnoreCase(status)) {
									promotedSucessfully = true;
								} else {
									failureCase = true;
									if (!failedList.contains(connectedObjId)) {
										failedList.add(connectedObjId);
										failureReason = (String) statusInfo.get("FailureReason");
										objectMap.put(keyPromotion, failureReason);
										partList.add(objectMap);
									}
								}
							} else {
								skippedCase = true;
								isAlreadyReleased = true;

							}
						} else {
							objectMap.put(keyPromotion, skippedMessage);
							skippedCase = true;
						}
						if (!selectedObjectList.contains(connectedObjId)) {
							selectedObjectList.add(connectedObjId);
						}
					}
					if (selectedObjectList.contains(connectedObjId)) {
						selectedObjectList.remove(connectedObjId);
						if (objectPolicy.equalsIgnoreCase(POLICY_EC_PART)) {
							objectMap.put(keyPromotion, skippedMessage);
							DomainObject domPart = DomainObject.newInstance(context, connectedObjId);
							MapList partRelatedInfo = domPart.getRelatedObjects(context, relPattern.getPattern(), // relationship
									// pattern
									QUERY_WILDCARD, // TYPE pattern
									objectSelects, // object selects
									relSelect, // relationship selects
									false, // to direction
									true, // from direction
									(short) 0, // recursion level
									DomainConstants.EMPTY_STRING, // object where clause
									DomainConstants.EMPTY_STRING, // rel where clause
									0);// Max limit
							if (partRelatedInfo.size() > 0) {
								Iterator<Map> relatedItr = partRelatedInfo.iterator();
								objType = DomainConstants.EMPTY_STRING;
								objPolicy = DomainConstants.EMPTY_STRING;
								objName = DomainConstants.EMPTY_STRING;
								objID = DomainConstants.EMPTY_STRING;
								objState = DomainConstants.EMPTY_STRING;
								relatedMap = null;
								while (relatedItr.hasNext()) {
									relatedMap = (Map) relatedItr.next();
									objType = (String) relatedMap.get(SELECT_TYPE);
									objPolicy = (String) relatedMap.get(SELECT_POLICY);
									objName = (String) relatedMap.get(SELECT_NAME);
									objID = (String) relatedMap.get(SELECT_ID);
									objState = (String) relatedMap.get(SELECT_CURRENT);
									if (!TYPE_VPMREFERENCE.equalsIgnoreCase(objType)
											&& !objPolicy.equalsIgnoreCase(POLICY_EC_PART)) {
										if (!(STATE_DOCUMENTRELEASE_RELEASED.equalsIgnoreCase(objState)
												|| STATE_MFGEQUIVALENT_RELEASED.equalsIgnoreCase(objState))) {
											statusInfo = massPromoteSpecsMEP(context, relatedMap);
											status = (String) statusInfo.get("Status");
											if ("Sucess".equalsIgnoreCase(status)) {
												promotedSucessfully = true;
											} else {
												failureCase = true;
												if (!failedList.contains(objID)) {
													failedList.add(objID);
													failureReason = (String) statusInfo.get("FailureReason");
													relatedMap.put(keyPromotion, failureReason);
													partList.add(relatedMap);
												}
											}
										} else {
											skippedCase = true;
											isAlreadyReleased = true;
										}
									} else {
										relatedMap.put(keyPromotion, skippedMessage);
										skippedCase = true;
									}
								}
							} else {
								skippedCase = true;
							}
						} else {
							if (!(TYPE_VPMREFERENCE.equalsIgnoreCase(objectType))) {
								if (!(STATE_DOCUMENTRELEASE_RELEASED.equalsIgnoreCase(objectState)
										|| STATE_MFGEQUIVALENT_RELEASED.equalsIgnoreCase(objectState))) {
									statusInfo = massPromoteSpecsMEP(context, objectMap);
									status = (String) statusInfo.get("Status");
									if ("Sucess".equalsIgnoreCase(status)) {
										promotedSucessfully = true;
									} else {
										failureCase = true;
										if (!failedList.contains(connectedObjId)) {
											failedList.add(connectedObjId);
											failureReason = (String) statusInfo.get("FailureReason");
											objectMap.put(keyPromotion, failureReason);
											partList.add(objectMap);
										}
									}
								} else {
									skippedCase = true;
									isAlreadyReleased = true;
								}
							} else {
								objectMap.put(keyPromotion, skippedMessage);
								skippedCase = true;
							}
						}
					}
				}
			} else {
				skippedCase = true;
			}
			if (failureCase) {
				statusMap.put(keyStatus, valueFailed);
			} else if (promotedSucessfully) {
				statusMap.put(keyStatus, valueSuccess);
			} else if (skippedCase) {
				if (isAlreadyReleased) {
					statusMap.put(keyStatus, valueAlreadyReleased);
				} else {
					statusMap.put(keyStatus, valueSkipped);
				}
			}
			statusMap.put(keyFailedList, partList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusMap;
	}// end of method
	
	
	/**
	 * This method is used to promote the Specifications & MEPs 
	 * @return Map
	 * Created by Subbu on 05/09/2018
	 */ 
	public Map massPromoteSpecsMEP(Context context, Map objectMap) throws Exception {
		boolean isObjectPromoted = false;
		Map statusMap = new HashMap();
		Locale locale = context.getSession().getLocale();
		String failedMessage = EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", locale,
				"emxEngineeringCentral.alertMessage.Failed");
		String failedPolicyRestricted = EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource",
				locale, "emxEngineeringCentral.alertMessage.policyRestricted");
		String failedPolicyNotConfiguredInPageObject = EnoviaResourceBundle.getProperty(context,
				"emxEngineeringCentralStringResource", locale,
				"emxEngineeringCentral.alertMessage.policyNotConfigured");
		String updateHistory = EnoviaResourceBundle.getProperty(context, "emxEngineeringCentralStringResource", locale,
				"emxEngineeringCentral.updateHistoryOnObjects.googMassPromotePartConnectedObjects");
		String objectState = (String) objectMap.get(SELECT_CURRENT);
		String objectID = (String) objectMap.get(SELECT_ID);
		String objectPolicy = (String) objectMap.get(SELECT_POLICY);
		String objectType = (String) objectMap.get(SELECT_TYPE);
		String pageObjectName = "googMassPromoteSpecsMEP";
		String statusKey = "Status";
		String success = "Sucess";
		String failed = "Failed";
		String failureReason = "FailureReason";
		try {
			String contextUser = context.getUser();
			ContextUtil.pushContext(context);
			MqlUtil.mqlCommand(context, "trigger off");

			googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
			Map<String, String> pageInfo = customIssue.getPageInfo(context, pageObjectName);

			DomainObject domObject = DomainObject.newInstance(context, objectID);
			if (!(POLICY_DESIGNPOLICY.equals(objectPolicy) || POLICY_SOLIDWORKS_DESIGN.equals(objectPolicy))) {
				objectPolicy = objectPolicy.replaceAll(" ", "_");
				String objState = (String) pageInfo.get(objectPolicy);
				if (UIUtil.isNotNullAndNotEmpty(objState)) {
					String historyCmd = "modify bus " + objectID + "  current " + objState
							+ " add history 'promote' comment '" + updateHistory + "  " + contextUser + "';";
					MqlUtil.mqlCommand(context, historyCmd);
					isObjectPromoted = true;
					statusMap.put(statusKey, success);
				} else {
					isObjectPromoted = false;
					statusMap.put(statusKey, failed);
					statusMap.put(failureReason, failedPolicyNotConfiguredInPageObject);
				}
			} else {
				isObjectPromoted = false;
				statusMap.put(statusKey, failed);
				statusMap.put(failureReason, failedPolicyRestricted);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isObjectPromoted = false;
			statusMap.put(statusKey, failed);
			statusMap.put(failureReason, failedMessage);

		} finally {
			MqlUtil.mqlCommand(context, "trigger on");
			ContextUtil.popContext(context);
		}
		return statusMap;
	}	

	/**
	 * This method is used show custom Mass promote cmd 'googMassPromotePartConnectedObjects' 
	 * @return boolean
	 * Created by Subbu on 27/08/2018
	 */ 
	public boolean isMassPromoteAvailable(Context context, String[] args) throws Exception {
		
		String googAdminRoleKey = "googAccessList.MassPromote.AdminRole";
		String allowedUsersKey = "googAccessList.MassPromote.AllowedUsers";
		String pageObjectName = "googAccessList";
		
		return accessValidation(context, pageObjectName, googAdminRoleKey, allowedUsersKey);
	}

	public boolean accessValidation(Context context,String pageObjectName, String googAdminRoleKey, String allowedUsersKey)
			throws FrameworkException, UnsupportedEncodingException, IOException, Exception {
		String contextUser = context.getUser();
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context,pageObjectName );
		String allowedRoles = pageInfo.get(googAdminRoleKey);
		String allowedUsers = pageInfo.get(allowedUsersKey);
		StringList users = FrameworkUtil.split(allowedUsers, ",");
		StringList roles = FrameworkUtil.split(allowedRoles, ",");
		
		if(users.isEmpty() && roles.isEmpty()) {
			return true;
		} else if (users.contains(contextUser) || isRoleListed(context,roles)) {
			return true;
		}
		return false;
	}
	/**
	 * This method is used check if listed roles in page object are assigned to context user 
	 * @return boolean
	 * Created by Subbu
	 */
	public boolean isRoleListed (Context context, StringList roles) throws Exception {
		boolean isAssigned = false;
		String contextUser = context.getUser();
		
		if(roles.size()>0) {
			for(int i=0;i<roles.size();i++) {
				String assignedRole = (String) roles.get(i);
				try {
					isAssigned = context.isAssigned(assignedRole);
					if(isAssigned) {
						return true;
					 }
				} catch(Exception e) {
				}	
			}
		}		
		return false;
	}
	//[Google Custom]#34626910 Added for Command googMassPromotePartConnectedObjects by Subbu @ 27/08/2018 -End
	
	// [Google Custom]#34626910 Added for Command Validate and Promote by Syed on 30/08/2018 - Starts
	/**
	 * To validate and Promote MEP and Specs
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * @author Syed
	 */
	public MapList getConnectedPartsandSpecs(Context context, String[] args) throws Exception {
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		String sExpandLevels = getStringValue(paramMap, "emxExpandFilter");
		MapList retList = expandEBOMSpecsMEP(context, paramMap, args);

		return retList;
	}	
	
	/**
	 * To Validate and Promote MEP and Specs
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * @author Syed
	 */
	public MapList expandEBOMSpecsMEP(Context context, HashMap paramMap, String[] args) throws Exception {

		int expandLevel = 0;
		String partId = getStringValue(paramMap, "objectId");
		String expandLevels = getStringValue(paramMap, "emxExpandFilter");
		Pattern relPattern = new Pattern(RELATIONSHIP_EBOM);
		relPattern.addPattern(RELATIONSHIP_MANUFACTURER_EQUIVALENT);
		relPattern.addPattern(RELATIONSHIP_PART_SPECIFICATION);

		if (!isValidData(expandLevels)) {
			expandLevels = getStringValue(paramMap, "ExpandFilter");
		}

		StringList objectSelect = new StringList(SELECT_ID);
		objectSelect.addElement(SELECT_REVISION);
		objectSelect.addElement(SELECT_POLICY);
		objectSelect.addElement(SELECT_CURRENT);
		objectSelect.addElement(SELECT_TYPE);

		// BOM UI Performance: Attributes required for Related Physical title column
		String attrVPLMVName = PropertyUtil.getSchemaProperty(context, "attribute_PLMEntity.V_Name");
		attrVPLMVName = "attribute[" + attrVPLMVName + "]";
		String typeVPLMProd = PropertyUtil.getSchemaProperty(context, "type_PLMEntity");

		String selectPartVName = "attribute[" + EngineeringConstants.ATTRIBUTE_V_NAME + "]";
		String selectProdctIdSel = "from[" + DomainConstants.RELATIONSHIP_PART_SPECIFICATION + "].to[" + typeVPLMProd
				+ "]." + DomainConstants.SELECT_ID;

		objectSelect.add(selectProdctIdSel);
		objectSelect.add(selectPartVName);

		// BOM UI Performance: Attributes required for Related Physical title column
		StringList relSelect = new StringList(SELECT_RELATIONSHIP_ID);
		relSelect.addElement(SELECT_ATTRIBUTE_FIND_NUMBER);
		if (!isValidData(expandLevels)) {
			expandLevel = 1;
			partId = getStringValue(paramMap, "partId") == null ? partId : getStringValue(paramMap, "partId");
		} else if ("All".equalsIgnoreCase(expandLevels)) {
			expandLevel = 0;
		} else {
			expandLevel = Integer.parseInt(expandLevels);
		}
		Part partObj = new Part(partId);

		MapList ebomList = partObj.getRelatedObjects(context, relPattern.getPattern(), DomainObject.QUERY_WILDCARD,
				objectSelect, relSelect, false, true, (short) expandLevel, null, null, 0);

		if (ebomList.size() > 0) {
			Iterator<Map> partitr = ebomList.iterator();
			while (partitr.hasNext()) {
				Map objectMap = (Map) partitr.next();
				String objPolicy = (String) objectMap.get(SELECT_POLICY);
				String objState = (String) objectMap.get(SELECT_CURRENT);
				String objType = (String) objectMap.get(SELECT_TYPE);
				//[Google Custom] To Enable check box for Parts : Added by Syed 18/10/2018 -Starts
				if ((TYPE_VPMREFERENCE.equals(objType)) || !POLICY_EC_PART.equals(objPolicy)) {
				//[Google Custom] To Enable check box for Parts : Added by Syed 18/10/2018 -Ends	
					objectMap.put("disableSelection", "true");
				}
			}
		}
		return ebomList;
	}
	
	/**
	 * To Validate and Promote MEP and Specs
	 * @param context
	 * @param string
	 * @return
	 * @throws Exception
	 * @author Syed
	 */
	private boolean isValidData(String data) {
		return ((data == null || "null".equals(data)) ? 0 : data.trim().length()) > 0;
	}
	
	// [Google Custom]:#34626910 Added for Command Validate and Promote by Syed on 30/08/2018 - Ends
	
	// [Google Custom]: - Referential- to get Reference Documents connected- Added by Syed on 13/09/2018 - Start
	/**
	 * To display Reference Documents attached to CA
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * @author Syed
	 */
	public MapList getReferentialDocuments(Context context, String[] args) throws Exception {
		MapList docObjectList = new MapList();
		try {
			HashMap programMap = JPO.unpackArgs(args);
			String changeId = (String) programMap.get("objectId");
			if (UIUtil.isNotNullAndNotEmpty(changeId)) {
				DomainObject changeObj = DomainObject.newInstance(context, changeId);
				StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_NAME);
				Pattern relPattern = new Pattern(RELATIONSHIP_REFERENCE_DOCUMENT);
				relPattern.addPattern(RELATIONSHIP_RELATED_ITEM);

				docObjectList = changeObj.getRelatedObjects(context, relPattern.getPattern(),
						DomainConstants.QUERY_WILDCARD, objectSelects, null, false, true, (short) 1, null, null, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return docObjectList;
	}
	// [Google Custom]: - Referential- to get Reference Documents connected- Added by Syed on 13/09/2018 - End
	
	// [Google Custom]: - Access check for EBOM CSV Export- Added by Syed on 24/09/2018 - Start
	
	/**
	 * This method is used to provide access to 'EBOM CSV Export' column for Admin users 
	 * @return boolean
	 * Created by Syed on 24/09/2018
	 */
	
	public boolean checkAccessForEBOMCSVExport(Context context, String[] args) throws Exception {

		String googAdminRoleKey = "googAccessList.EBOMCSVExport.AdminRole";
		String allowedUsersKey = "googAccessList.EBOMCSVExport.AllowedUsers";
		String pageObjectName = "googAccessList";

		return accessValidation(context, pageObjectName, googAdminRoleKey, allowedUsersKey);
	}
	
	/**
	 * This method used to run batch file to generate EBOM report
	 * @param context
	 * @param args
	 * @return string
	 * @throws Exception
	 * @author Syed
	 */
	
	public String batchBOMCSVExport(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		Map argMap = new HashMap();
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		String objectId = (String) paramMap.get("objectId");
		// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - Start
		String process = (String) paramMap.get("process");
		String excludedColumns = (String) paramMap.get("excludedColumnsList");
		String expandLevel = (String) paramMap.get("expandLevel");
		Job job = null;
		String jobName = "";
		try {
			String[] aStrJobArgs = new String[4];
			aStrJobArgs[0] = objectId;
			aStrJobArgs[1] = process;
			aStrJobArgs[2] = excludedColumns;
			aStrJobArgs[3] = expandLevel;
		// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - End	
			job = new Job("googCustomFunctions", "batchBOMCSVExportUpdate", aStrJobArgs);
			job.setTitle("EBOM CSV Export ");
			job.setActionOnCompletion("None");
			job.setContextObject(objectId);
			job.setAllowreexecute("No");
			job.setDescription("EBOM CSV Export");
			job.createAndSubmit(context);
			job.setStartDate(context);
			job.finish(context, "Succeeded");
			job.setCompletionStatus("Succeeded");
			job.setAttributeValue(context, "Completion Status", "Succeeded");
			jobName = job.getName();
		} catch (Exception e) {
			job.finish(context, "Failed");
			job.setCompletionStatus("Failed");
			e.printStackTrace();
		}

		return jobName;
	}
	
	/**
	 * This method used for Export EBOM structure in batch mode
	 * @param context
	 * @param args
	 * @return void
	 * @throws Exception
	 * @author Syed
	 */
	// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - Start
	public void batchBOMCSVExportUpdate(Context context, String[] args) throws Exception {
		googCustomReports_mxJPO googCustomReports = new googCustomReports_mxJPO();
		Map argMap = new HashMap();
		argMap.put("objectId", args[0]);
		argMap.put("process", args[1]);
		argMap.put("excludedColumnsList", args[2]);
		argMap.put("expandLevel", args[3]);
		googCustomReports.generateEBOMReport(context, argMap);
	}
	
	// [Google Custom]: - Access check for EBOM CSV Export- Added by Syed on 24/09/2018 - End
	
	// [Google Custom]: - EBOM CSV Export Report - Created by Syed on 03/06/2019 - Start
	/**
	 * To generate EBOM CSV Export
	 * @return String
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */
	public File generateEBOMCSVExportReport(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		Map argMap = new HashMap();
		HashMap paramMap = (HashMap) programMap.get("paramMap");
		String objectId = (String) paramMap.get("objectId");
		String process = (String) paramMap.get("process");
		String excludedColumns = (String) paramMap.get("excludedColumnsList");
		String expandLevel = (String) paramMap.get("expandLevel");
		argMap.put("objectId", objectId);
		argMap.put("process", process);
		argMap.put("excludedColumnsList", excludedColumns);
		argMap.put("expandLevel", expandLevel);
		googCustomReports_mxJPO googCustomReports = new googCustomReports_mxJPO();
		File outPutcsv = googCustomReports.generateEBOMReport(context, argMap);
		return outPutcsv;
	}
	// [Google Custom]: - EBOM CSV Export Report Code Review Comments - Modified by Syed on 03/07/2019 - End
	
	/**
	 * To read page object
	 * @return Object
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author Syed
	 */	
	public Object getColumnValuesFromPageObject(Context context, String[] args) throws Exception {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googEBOMReportMapping");
		String attrRangeValues = pageInfo.get("EBOM.Report.Header");
		StringList childMapList = convertStringToStringList(attrRangeValues);
		return childMapList;
	}
	
	/**
	 * This method used to send mail with attachment in background process
	 * @param context
	 * @param args
	 * @return MapList
	 * @throws Exception
	 * Created by Syed on 04/15/2019
	 */
	public void sendGenericMailToUser(Context context, String sAbsolutepath, String[] toList, String[] CCList,
			String sSubject, StringBuilder sbBody, String fileName) throws Exception {
		try {
			String host = PropertyUtil.getEnvironmentProperty(context, "MX_SMTP_HOST");

			// get the from user: Using the Super User Context to send the
			// mail(BackGround Job)
			String from = PersonUtil.getEmail(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"));

			// TODO: Change this later, currently Hard Coded this to make sure
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
			String fileAttachment = sAbsolutepath;
			String strPersonal = PersonUtil.getEmail(context,
					PropertyUtil.getSchemaProperty(context, "person_UserAgent")); // From User

			// Hard Coded this to make sure User Agent Mail is not Waymo but google
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
			// Add CC list
			// TODO: Make Changes to the CC List
			message.addRecipients(Message.RecipientType.CC, cc);

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
	// [Google Custom]: - EBOM CSV Export Report - Created by Syed on 03/06/2019 - End
	
	/**
	 * This method used to display Name and Title in Request WPN column
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws Exception
	 * Created by Syed on 04/26/2019
	 */
	public Vector getRequestWPNNameColumn(Context context, String args[]) throws Exception {
		Vector columnValues = new Vector();
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			MapList objectList = (MapList) programMap.get("objectList");
			Iterator objectListIterator = objectList.iterator();
			while (objectListIterator.hasNext()) {
				StringBuilder nameBuilder = new StringBuilder();
				Map partMap = (Map) objectListIterator.next();
				String partDesc = (String) partMap.get(SELECT_DESCRIPTION);
				if (UIUtil.isNotNullAndNotEmpty(partDesc) && partDesc.length() >= 20) {
					partDesc = partDesc.substring(0, 20);
					partDesc = partDesc + "..";
				}
				partDesc = "Desc: "+partDesc;
				String partName = (String) partMap.get(DomainConstants.SELECT_NAME);
				nameBuilder.append(XSSUtil.encodeForHTML(context, partName));
				nameBuilder.append("</br>");
				nameBuilder.append(XSSUtil.encodeForHTML(context, partDesc));
				columnValues.add(nameBuilder.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return columnValues;
	}
	// [Google Custom]: - Remove Production Make Buy Code, Design Release Engineer columns and Title - Created by Syed on 04/30/2019 - Ends
	
	/**
	 * This method is used to get all the CA and Related Route information
	 * @param context
	 * @param args
	 * @return caDetails
	 * @author shajil
	 * @throws Exception 
	 */
	public String getCAReportData(Context context,String args[]) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		
		String format = (String)programMap.get("format");
		String taskType = (String)programMap.get("taskType");
		String typeName = (String)programMap.get("type");
		String [] typeNames = typeName.split(",");
		String whereCondition = null;
		//String type = null;
		String state = (String)programMap.get("state");
		if(typeNames.length>1) {
			if(state.equals("InApproval")){
				whereCondition = "current == 'In Approval' || current == 'Assign'";
			}else if(state.equals("Complete") ) {
				whereCondition = "current == 'Closed' || current == 'Complete'";
			}
			typeName = "Change Action,Issue";
		}else if(state.equals("InApproval") && typeName.equals("ChangeAction")) {
			whereCondition = "current == 'In Approval'";
		}else if(state.equals("InApproval") && typeName.equals("Issue")) {
			whereCondition = "current == 'Assign'";
		}else if(state.equals("Complete") && typeName.equals("Issue")) {
			whereCondition = "current == 'Closed'";
		}else if(state.equals("Complete") && typeName.equals("ChangeAction")) {
			whereCondition = "current == 'Complete'";
		}
		
		if(typeName.equals("ChangeAction")) {
			typeName = "Change Action";
		}
		String fileExtension = null;
		String outputfilePath = null;
		boolean isCSV;
		
		if (format.equalsIgnoreCase("csv")) {
			fileExtension = ".csv";
			isCSV = true;
		} else {
			fileExtension = ".xlsx";
			isCSV = false;
		}
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_REVISION);
		objectSelects.addElement(SELECT_CURRENT);
		objectSelects.addElement(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);
		objectSelects.addElement("project");
		objectSelects.addElement("from[Object Route|(to.type==Route)].to.id");
		objectSelects.addElement("from[Object Route|(to.type==Route)].to.name");
		MapList caDetails = DomainObject.findObjects(context, typeName,
				DomainObject.QUERY_WILDCARD,
				DomainObject.QUERY_WILDCARD,
				DomainObject.QUERY_WILDCARD,
				DomainObject.QUERY_WILDCARD,
				whereCondition,
				false,
				objectSelects);
		MapList caReports = updateRouteDetails(context,caDetails,taskType,format);
		String fileCreateTimeStamp = Long.toString(System.currentTimeMillis());
		String strWorkspace = context.createWorkspace();
		
		StringBuffer filename = new StringBuffer(50);
		filename.append("CA_Deviation_Report");
		filename.append("_");
		filename.append(fileCreateTimeStamp);
		filename.append(fileExtension);
		String sFilename = filename.toString();
		
		googCustomIssue_mxJPO customFun = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customFun.getPageInfo(context, "googCAReport");
		if(isCSV) {
			outputfilePath = createCSVFile(sFilename,caReports,pageInfo);
		}else {
			outputfilePath = createExcelFile(sFilename,caReports,pageInfo);
		}
		return outputfilePath;
	}
	

	public String createExcelFile(String sFilename, MapList caReports, Map<String, String> pageInfo) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("CA_Deviation Report");
		Row row = createHeaderCell(sheet, (short) 0, pageInfo);
		row = updateExcelRow(sheet,row,caReports,pageInfo);
		try (FileOutputStream outputStream = new FileOutputStream(new File(sFilename))) {
	            workbook.write(outputStream);
	            outputStream.close();
	        }
		return sFilename;
	}

	public Row updateExcelRow(XSSFSheet sheet, Row row, MapList caReports, Map<String, String> pageInfo) {

		MapList resultList = new MapList();
		Iterator itr = caReports.iterator();
		String[] headerNames = pageInfo.get("googReport.Header").split(",");
		while (itr.hasNext()) {
			Map reportInfo = (Map) itr.next();
			row = sheet.createRow(row.getRowNum() + 1);
			StringBuilder rowValues = new StringBuilder();
			for (int i = 0; i < headerNames.length; i++) {
				Cell cell = row.createCell(i);
				if (reportInfo.containsKey(headerNames[i])) {
					String cellValue = (String) reportInfo.get(headerNames[i]);
					if (UIUtil.isNotNullAndNotEmpty(cellValue)) {
						cell.setCellValue(cellValue);
					} else {
						cell.setCellValue("");
					}
				} else {
					cell.setCellValue("");
				}
			}

		}
		return row;
	}

	public  Row createHeaderCell(XSSFSheet sheet,
	         short col, Map<String, String> pageInfo) {
		String[] headerNames = pageInfo.get("googReport.Header").split(",");
		Row row = sheet.createRow(col);
		for(int i=0;i<headerNames.length;i++){
		Cell cell = row.createCell(i);
		 cell.setCellValue(headerNames[i]);
		}
	      return row;
	}
	
	public String createCSVFile(String filePath, MapList reportDetails, Map<String, String> pageInfo) throws IOException {
		
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		String header = pageInfo.get("googReport.Header");
		try {
			bw.write(header);
			bw.write(NEW_LINE_SEPARATOR);
			Iterator itr = reportDetails.iterator();
			while (itr.hasNext()) {
				Map reportInfo = (Map) itr.next();
				String rowDetails = updateRow(reportInfo,pageInfo);
				bw.write(rowDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
		return file.getPath();
		
	}

	public String updateRow(Map reportInfo, Map<String, String> pageInfo) {
		String[] headerNames = pageInfo.get("googReport.Header").split(",");
		StringBuilder rowValues = new StringBuilder();
		for (String headerName : headerNames) {
			if (reportInfo.containsKey(headerName)) {
				String cellValue = (String)reportInfo.get(headerName);
				if (UIUtil.isNotNullAndNotEmpty(cellValue)) {
					rowValues.append(cellValue).append(",");
				}else {
					rowValues.append("").append(",");
				}
			}else {
				rowValues.append("").append(",");
			}
		}
		rowValues.append(NEW_LINE_SEPARATOR);
		
		return rowValues.toString();
	}

	/**
	 * This method is used to update Route related information
	 * @param context
	 * @param caDetails
	 * @return caDetails
	 * @throws FrameworkException
	 * @author shajil
	 */
	public MapList updateRouteDetails(Context context, MapList caDetails, String taskType,String format) throws FrameworkException {
		MapList resultList = new MapList();
		Iterator itr = caDetails.iterator();
		while (itr.hasNext()) {
			Map caInfo = (Map) itr.next();
			String routeSelectStatement = "from[Object Route].to.id";
			String caName = (String) caInfo.get(SELECT_NAME);
			String caId = (String) caInfo.get(SELECT_ID);
			String caState = (String) caInfo.get(SELECT_CURRENT);;
			String caCollabSpace = (String) caInfo.get("project");;
			String caDRE = (String) caInfo.get(SELECT_ATTR_RESPONSIBLE_DESIGN_ENGINEER);;
			
			String routeName = null;
			String routeId = null;
			StringBuilder caHTMLFormatName = new StringBuilder();
			caHTMLFormatName.append("<a href=\"javascript:void(0)\" onClick=\"javascript:showModalDialog('../common/emxTree.jsp?objectId=").append(XSSUtil.encodeForJavaScript(context, caId));
			caHTMLFormatName.append("','860','520');\" style = 'color:#1E90FF' > <img src = '../common/images/iconSmallCompany.gif' border=3></img> ");
			caHTMLFormatName.append(XSSUtil.encodeForXML(context, caName)).append( "</a>");
			caInfo.put("Name", caName);
			caInfo.put("State", caState);
			caInfo.put("CollabSpace", caCollabSpace);
			caInfo.put("DRE", caDRE);
			if (caInfo.containsKey(routeSelectStatement)) {
				routeId = (String) caInfo.get(routeSelectStatement);
				routeName = (String) caInfo.get("from[Object Route].to.name");
				MapList taskList = new MapList();
				if (routeId.contains("")) {
					StringList routeIds = FrameworkUtil.split(routeId, "");
					StringList routeNames = FrameworkUtil.split(routeName, "");
					StringBuilder routes = new StringBuilder();
					for (int j = 0; j < routeIds.size(); j++) {
						String routeObjId = (String) routeIds.get(j);
						//routes.append(routeNames.get(j)).append("<br/>");
						routes.append(routeNames.get(j)).append(" | ");
						caInfo.put("RouteName", routes.toString());
						caInfo.put("RouteId",  routeIds.get(j));
						//getTaskInfo(context,routeObjId,caInfo,taskType,format);
						taskList.addAll(getApprovedAndUnAssignedTsks(context,routeObjId,caInfo,taskType,format));
						
					}
					updateTaskDetails(context,taskList,caInfo,format);
				}else {
					caInfo.put("RouteName", routeName);
					caInfo.put("RouteId", routeId);
					//getTaskInfo(context,routeId,caInfo,taskType,format);
					updateTaskDetails(context,getApprovedAndUnAssignedTsks(context,routeId,caInfo,taskType,format),caInfo,format);
				}
			}else {
				caInfo.put("RouteName", "");
				caInfo.put("RouteId", "");
				caInfo.put("RouteName", "");
				caInfo.put("RouteId", "");
			}
		}
		return caDetails;
	}

	
	public MapList getApprovedAndUnAssignedTsks(Context context, String routeId,Map caInfo,String taskType,String format) throws FrameworkException {
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_CURRENT);
		objectSelects.addElement(SELECT_ATTRIBUTE_TITLE);
		objectSelects.addElement(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
		objectSelects.addElement(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
		objectSelects.addElement(SELECT_ATTRIBUTE_COMMENTS);
		objectSelects.addElement(SELECT_ATTRIBUTE_SEQUENCE_ORDER);
		objectSelects.addElement(SELECT_ATTRIBUTE_ROUTE_ACTION);
		objectSelects.addElement("attribute["+ATTRIBUTE_APPROVAL_STATUS+"]");
		objectSelects.addElement("attribute["+ATTRIBUTE_ROUTE_NODE_ID+"]");
		objectSelects.addElement("from[Project Task].to.name");	
		
		
		StringList relSelects = new StringList(DomainRelationship.SELECT_ID);
		relSelects.addElement(SELECT_ATTRIBUTE_SEQUENCE_ORDER);
		relSelects.addElement(SELECT_ATTRIBUTE_ROUTE_ACTION);
		relSelects.addElement(SELECT_ATTRIBUTE_TITLE);
		relSelects.addElement("physicalid");
		relSelects.addElement("attribute["+ATTRIBUTE_ROUTE_TASK_USER+"]");
		relSelects.addElement("attribute["+ATTRIBUTE_ROUTE_SEQUENCE+"]");
		DomainObject routeObj = DomainObject.newInstance(context, routeId);
		MapList taskList =routeObj.getRelatedObjects(context, // Context
				RELATIONSHIP_ROUTE_TASK,
				TYPE_INBOX_TASK,
				objectSelects, 
				null,
				true,
				false,
				(short) 1,
				"attribute[Route Action]=='"+taskType+"'", // Bus Where
				EMPTY_STRING, // Rel Where
				0);
		MapList personList =routeObj.getRelatedObjects(context, // Context
				RELATIONSHIP_ROUTE_NODE,
				TYPE_PERSON+","+TYPE_ROUTE_TASK_USER,
				objectSelects, 
				relSelects,
				false,
				true,
				(short) 1,
				EMPTY_STRING, // Bus Where
				"attribute[Route Action]=='"+taskType+"'", // Rel Where
				0);
		
		MapList completeTaskList = getUnAssignedTasks(taskList,personList);
		return completeTaskList;//updateTaskDetails(context,completeTaskList,caInfo,format);
	}

	public MapList getUnAssignedTasks(MapList taskList, MapList personList) {
		Iterator itr = personList.iterator();
		MapList unAssignedTasks = new MapList();
		while(itr.hasNext()) {
			Map personInfo = (Map)itr.next();
			String relPhysicalId = (String)personInfo.get("physicalid");
			String type = (String)personInfo.get(SELECT_TYPE);
			
			Iterator taskItr = taskList.iterator();
			boolean isAssigned = false;
			while(taskItr.hasNext()) {
				Map taskInfo = (Map)taskItr.next();
				String relId = (String)taskInfo.get("attribute["+ATTRIBUTE_ROUTE_NODE_ID+"]");
				if(relId.equals(relPhysicalId)) {
					isAssigned = true;
					break;
				}
			}
			if(!isAssigned) {
				String name = (String)personInfo.get(SELECT_NAME);
				String title = (String)personInfo.get(SELECT_ATTRIBUTE_TITLE);
				String sequenceOrder = "PendingOrder"+(String)personInfo.get("attribute["+ATTRIBUTE_ROUTE_SEQUENCE+"]");
				personInfo.put(SELECT_NAME, title);
				if(type.equals(TYPE_PERSON)) {
					personInfo.put("from[Project Task].to.name", name);
				}else {
					String groupTask = (String)personInfo.get("attribute["+ATTRIBUTE_ROUTE_TASK_USER+"]");
					personInfo.put("from[Project Task].to.name", groupTask);
				}
				personInfo.put(SELECT_CURRENT, sequenceOrder);
				personInfo.put("attribute["+ATTRIBUTE_APPROVAL_STATUS+"]", sequenceOrder);
				unAssignedTasks.add(personInfo);
			}			
		}
		taskList.addAll(unAssignedTasks);
		return  taskList;
	}

	/**
	 * This mehod is used to get Inbox Task information
	 * @param context
	 * @param routeId
	 * @param caInfo
	 * @return caInfo
	 * @throws FrameworkException
	 * @author shajil
	 */
	public Map getTaskInfo(Context context, String routeId, Map caInfo,String taskType,String format) throws FrameworkException {
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_CURRENT);
		objectSelects.addElement(SELECT_ATTRIBUTE_TITLE);
		objectSelects.addElement(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
		objectSelects.addElement(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
		objectSelects.addElement(SELECT_ATTRIBUTE_COMMENTS);
		objectSelects.addElement("from[Project Task].to.name");
		DomainObject routeObj = DomainObject.newInstance(context, routeId);
		MapList taskList =routeObj.getRelatedObjects(context, // Context
				RELATIONSHIP_ROUTE_TASK,
				TYPE_INBOX_TASK,
				objectSelects, 
				null,
				true,
				false,
				(short) 1,
				"attribute[Route Action]=='"+taskType+"'", // Bus Where
				EMPTY_STRING, // Rel Where
				0);
		return updateTaskDetails(context,taskList,caInfo,format);
	}

	/**
	 * This method is used to update the list with Inbox Task information
	 * @param context
	 * @param taskList
	 * @param caInfo
	 * @return caInfo
	 * @author shajil
	 */
	public Map updateTaskDetails(Context context, MapList taskList, Map caInfo,String format) {
		Iterator itr = taskList.iterator();
		StringBuilder taskName = new StringBuilder("\"");
		StringBuilder taskTitle = new StringBuilder("\"");
		StringBuilder taskState = new StringBuilder("\"");
		StringBuilder taskSchedCompDate = new StringBuilder("\"");
		StringBuilder taskActualCompDate = new StringBuilder("\"");
		StringBuilder taskComments = new StringBuilder("\"");
		StringBuilder taskAssignee = new StringBuilder("\"");
		
		StringBuilder unApprovedTaskName = new StringBuilder("\"");
		StringBuilder unApprovedTaskTitle = new StringBuilder("\"");
		StringBuilder unApprovedTaskState = new StringBuilder("\"");
		StringBuilder unApprovedTaskSchedCompDate = new StringBuilder("\"");
		StringBuilder unApprovedTaskActualCompDate = new StringBuilder("\"");
		StringBuilder unApprovedTaskComments = new StringBuilder("\"");
		StringBuilder unApprovedTaskAssignee = new StringBuilder("\"");
		
		if (!format.equalsIgnoreCase("csv")) {
			taskName = new StringBuilder("");
			taskTitle = new StringBuilder("");
			taskState = new StringBuilder("");
			taskSchedCompDate = new StringBuilder("");
			taskActualCompDate = new StringBuilder("");
			taskComments = new StringBuilder("");
			taskAssignee = new StringBuilder("");
			unApprovedTaskName = new StringBuilder("");
			unApprovedTaskTitle = new StringBuilder("");
			unApprovedTaskState = new StringBuilder("");
			unApprovedTaskSchedCompDate = new StringBuilder("");
			unApprovedTaskActualCompDate = new StringBuilder("");
			unApprovedTaskComments = new StringBuilder("");
			unApprovedTaskAssignee = new StringBuilder("");
		}
		
		while (itr.hasNext()) {
			Map taskInfo = (Map) itr.next();
			String state = (String)taskInfo.get(SELECT_CURRENT);
			if(state.equals("Complete")) {
				taskName.append(taskInfo.get(SELECT_NAME)).append("\n");
				taskTitle.append(taskInfo.get(SELECT_ATTRIBUTE_TITLE)).append("\n");
				taskState.append(taskInfo.get(SELECT_CURRENT)).append("\n");
				taskSchedCompDate.append(taskInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE)).append("\n");
				taskActualCompDate.append(taskInfo.get(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE)).append("\n");
				taskComments.append(taskInfo.get(SELECT_ATTRIBUTE_COMMENTS)).append("\n");
				taskAssignee.append(taskInfo.get("from[Project Task].to.name")).append("\n");
			}else {
				unApprovedTaskName.append(taskInfo.get(SELECT_NAME)).append("\n");
				unApprovedTaskTitle.append(taskInfo.get(SELECT_ATTRIBUTE_TITLE)).append("\n");
				unApprovedTaskState.append(taskInfo.get(SELECT_CURRENT)).append("\n");
				unApprovedTaskSchedCompDate.append(taskInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE)).append("\n");
				unApprovedTaskActualCompDate.append(taskInfo.get(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE)).append("\n");
				unApprovedTaskComments.append(taskInfo.get(SELECT_ATTRIBUTE_COMMENTS)).append("\n");
				unApprovedTaskAssignee.append(taskInfo.get("from[Project Task].to.name")).append("\n");
			}
			
		}
		
		if (format.equalsIgnoreCase("csv")) {
			taskName.append("\"");
			taskTitle.append("\"");
			taskAssignee.append("\"");
			taskState.append("\"");
			taskSchedCompDate.append("\"");
			taskActualCompDate.append("\"");
			taskComments.append("\"");	
			unApprovedTaskName.append("\"");
			unApprovedTaskTitle.append("\"");
			unApprovedTaskAssignee.append("\"");
			unApprovedTaskState.append("\"");
			unApprovedTaskSchedCompDate.append("\"");
			unApprovedTaskActualCompDate.append("\"");
			unApprovedTaskComments.append("\"");
		}
		
		caInfo.put("TaskName", taskName.toString().trim());
		caInfo.put("TaskTitle", taskTitle.toString().trim());
		caInfo.put("TaskAssignee", taskAssignee.toString().trim());
		caInfo.put("TaskState", taskState.toString().trim());
		caInfo.put("TaskScheduleCompDate", taskSchedCompDate.toString().trim());
		caInfo.put("TaskActualCompDate", taskActualCompDate.toString().trim());
		caInfo.put("TaskComments", taskComments.toString().trim());	
		
		caInfo.put("UnApprovedTaskName", unApprovedTaskName.toString().trim());
		caInfo.put("UnApprovedTaskTitle", unApprovedTaskTitle.toString().trim());
		caInfo.put("UnApprovedTaskAssignee", unApprovedTaskAssignee.toString().trim());
		caInfo.put("UnApprovedTaskState", unApprovedTaskState.toString().trim());
		caInfo.put("UnApprovedTaskScheduleCompDate", unApprovedTaskSchedCompDate.toString().trim());
		caInfo.put("UnApprovedTaskActualCompDate", unApprovedTaskActualCompDate.toString().trim());
		caInfo.put("UnApprovedTaskComments", unApprovedTaskComments.toString().trim());	
		
		
		return caInfo;
	}
	// [Google Custom]: - #247-Ability to edit released parts(ie BoM,CA & Part properties) - Created by Syed on 03/06/2019 - Starts
	/**
	 * This method used to show edit icon for released parts
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws Exception
	 * Created by Syed on 04/25/2019
	 */
	public Vector getPartAttributesToEdit(Context context, String[] args) throws Exception {
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		Vector showEditButton = new Vector();
		MapList objectList = (MapList) paramMap.get("objectList");
		StringList objectSelects = new StringList();
		objectSelects.addElement(SELECT_LAST_ID);
		objectSelects.addElement(SELECT_CURRENT);
		objectSelects.addElement(SELECT_POLICY);
		if (objectList != null && objectList.size() > 0) {
			// construct array of ids
			int objectListSize = objectList.size();
			for (int i = 0; i < objectListSize; i++) {
				String hrefLink = "";
				String partObjId = "";
				String partState = "";
				String latestRevisionId = "";
				String partPolicy = "";
				Map dataMap = (Map) objectList.get(i);
				partObjId = (String) dataMap.get("id");

				if (dataMap.containsKey(DomainConstants.SELECT_CURRENT)
						&& dataMap.containsKey(DomainConstants.SELECT_LAST_ID)) {
					partState = (String) dataMap.get(DomainConstants.SELECT_CURRENT);
					latestRevisionId = (String) dataMap.get(DomainConstants.SELECT_LAST_ID);
					partPolicy = (String) dataMap.get(SELECT_POLICY);
				} else if (UIUtil.isNotNullAndNotEmpty(partObjId)) {
					DomainObject partObj = DomainObject.newInstance(context, partObjId);
					Map partMap = partObj.getInfo(context, objectSelects);
					partState = (String) partMap.get(SELECT_CURRENT);
					latestRevisionId = (String) partMap.get(SELECT_LAST_ID);
					partPolicy = (String) partMap.get(SELECT_POLICY);
				}
				//Modified for 88 by Preethi Rajaraman -- Starts
				//if (STATE_ECPART_RELEASE.equals(partState) && POLICY_EC_PART.equals(partPolicy)) {
				if (POLICY_EC_PART.equals(partPolicy)) {
				//Modified for 88 by Preethi Rajaraman -- Ends
					if (UIUtil.isNotNullAndNotEmpty(latestRevisionId) && latestRevisionId.equals(partObjId)) {
						hrefLink = "<a href=\"javascript:emxTableColumnLinkClick('../common/emxForm.jsp?form=googReleasedPartEditForm&postProcessURL=../engineeringcentral/goog_partEditProcess.jsp&suiteKey=EngineeringCentral&StringResourceFileId=emxEngineeringCentralStringResource&SuiteDirectory=engineeringcentral&mode=edit&preProcessJavaScript=preProcessWaymoType&submitAction=refreshCaller&targetLocation=slidein&objectId="
								+ partObjId
								+ "', '800', '575', 'false', 'slidein')\"><img src=\"../common/images/iconActionEdit.gif\" alt=\"\" border=\"0\" /></a>";
						hrefLink = hrefLink.replaceAll("&", "&amp;");
					}
				}
				showEditButton.addElement(hrefLink);
			}
		}
		return showEditButton;
	}
	
	/**
	 * This method used to get latest released revision
	 * @param context
	 * @param args
	 * @return String
	 * @throws Exception
	 * Created by Syed on 04/30/2019
	 */
	public String getLatestRevisionIdOfPart(Context context,String partName,String[] args) throws Exception {
		StringBuilder objectWhere = new StringBuilder();
		objectWhere.append("policy !=\"");
		objectWhere.append(POLICY_MANUFACTURER_EQUIVALENT);
		objectWhere.append("\"");
		objectWhere.append("&& current == 'Release'");
		StringList objectSelect = new StringList();
		objectSelect.addElement(DomainConstants.SELECT_ID);
		objectSelect.addElement(DomainConstants.SELECT_DESCRIPTION);
		objectSelect.addElement(DomainConstants.SELECT_TYPE);
		objectSelect.addElement(DomainConstants.SELECT_POLICY);
		objectSelect.addElement(DomainConstants.SELECT_NAME);
		objectSelect.addElement(SELECT_LAST_ID);
		objectSelect.addElement(SELECT_LATEST_REVISION);
		objectSelect.addElement(SELECT_REVISION);
		emxPart_mxJPO customPart = new emxPart_mxJPO(context,args);
		MapList partDetails = DomainObject.findObjects(context, DomainObject.TYPE_PART, // Type
				partName, // name
				DomainObject.QUERY_WILDCARD, // revision
				DomainObject.QUERY_WILDCARD, // owner
				DomainObject.QUERY_WILDCARD, // Vault
				objectWhere.toString(), // Where
				true, // expand sub type
				objectSelect);
		
		Map latestReleasedRevisionMap = customPart.getLatestRevisionPartDetails(partDetails);
		
		return (String)latestReleasedRevisionMap.get("id");
	}
	/**
	 * This method used to check Description and UOM Editable if Design collaboration FALSE
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 * Created by Syed on 04/25/2019
	 */
	public boolean isUOMFieldEditable(Context context, String[] args) throws Exception {
		Map argMap = (Map) JPO.unpackArgs(args);
		Map settingsMap = (Map) argMap.get("SETTINGS");
		String objectId = (String) argMap.get("objectId");
		String designCollab = "";
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			DomainObject partObj = DomainObject.newInstance(context, objectId);
			designCollab = partObj.getInfo(context, "attribute[isVPMVisible].value");
			if (UIUtil.isNotNullAndNotEmpty(designCollab) && "FALSE".equals(designCollab)) {
				settingsMap.put("Editable", "true");
				return true;
			} else {
				settingsMap.put("Editable", "false");
				return true;
			}
		}
		return true;
	}
	/**
	 * This method used to provide edit access check for latest released parts
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 * Created by Syed on 04/30/2019
	 */
	public boolean checkEditIconDisplay(Context context, String[] args) throws Exception {
		Map argMap = (Map) JPO.unpackArgs(args);
		String partObjId = (String) argMap.get("objectId");
		StringList objectSelects = new StringList();
		objectSelects.addElement(SELECT_LAST_ID);
		objectSelects.addElement(SELECT_CURRENT);
		objectSelects.addElement(SELECT_POLICY);
		String partName = "";
		String latestRevisionId = "";
		if (UIUtil.isNotNullAndNotEmpty(partObjId)) {
			DomainObject partObj = DomainObject.newInstance(context, partObjId);
			Map partMap = partObj.getInfo(context, objectSelects);
			String partState = (String)partMap.get(SELECT_CURRENT);
			String lastId = (String)partMap.get(SELECT_LAST_ID);
			String partPolicy = (String)partMap.get(SELECT_POLICY);
			if (UIUtil.isNotNullAndNotEmpty(lastId) && lastId.equals(partObjId) && STATE_ECPART_RELEASE.equals(partState) && POLICY_EC_PART.equals(partPolicy)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	/**
	 * This method used to provide ranges for attributes
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 * Created by Syed on 06/20/2019
	 */
	public HashMap getRangesForPartAttributes(Context context, String[] args) throws Exception {
		HashMap tempMap = new HashMap();
		Map paramMap = (Map) JPO.unpackArgs(args);
		HashMap fieldMap = (HashMap) paramMap.get("fieldMap");
		HashMap settingsMap = (HashMap) fieldMap.get("settings");
		String appliesTo = (String) settingsMap.get("AttributeName");
		AttributeType atriType = new AttributeType(appliesTo);
		atriType.open(context);
		StringList attrList = atriType.getChoices(context);
		atriType.close(context);
		tempMap.put("field_choices", attrList);
		tempMap.put("field_display_choices", attrList);

		return tempMap;
	}
	// [Google Custom]: - #247-Ability to edit released parts(ie BoM,CA & Part properties) - Created by Syed on 03/06/2019 - Ends
	
	/**
	 * This method used in different modes visibility
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 * Created by Syed on 06/21/2019
	 */
	public boolean isFollowerFieldViewable(Context context, String[] args) throws Exception {
		Map argMap = (Map) JPO.unpackArgs(args);
		String currentMode = (String) argMap.get("mode");
		Map settingsMap = (Map) argMap.get("SETTINGS");
		String settingMode = (String) settingsMap.get("mode");
		if (UIUtil.isNotNullAndNotEmpty(settingMode) && settingMode.equalsIgnoreCase(currentMode)) {
			return true;
		} else {
			return false;
		}

	}
	
	/**
	 * This method used to get Follower List attribute values
	 * @param context
	 * @param args
	 * @return String
	 * @throws Exception
	 * Created by Syed on 06/21/2019
	 */
	public String getDeviationFollowerList(Context context, String[] args) throws Exception {
		StringBuilder followerListBuilder = new StringBuilder();
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap requestMap = (HashMap) programMap.get("requestMap");
			String deviationId = (String) requestMap.get("objectId");
			DomainObject issueObj = new DomainObject(deviationId);
			String followerList = issueObj.getAttributeValue(context, ATTR_FOLLOWER_LIST);
			googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
			Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomMailDetails");
			String followerGroup = pageInfo.get("Deviation.Group.FollowerList");
			followerListBuilder.append(followerGroup);
			followerListBuilder.append("\n");
			followerListBuilder.append(followerList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return followerListBuilder.toString();
	}
	
		public void updateWeightForPLMAnalyst(Context context, String[] args) throws Exception {
		/* boolean isContextPushed = false;
			try {
				ContextUtil.pushContext(context);
				isContextPushed = true;
				HashMap programMap = (HashMap) JPO.unpackArgs(args);
				HashMap paramMap = (HashMap) programMap.get("paramMap");
				HashMap requestMap = (HashMap) programMap.get("requestMap");
				String[] units_Weight = (String[]) requestMap.get("units_Weight");
				String sObjectId = (String) paramMap.get("objectId");
				String sNewValue = (String) paramMap.get("New Value");
				String sAdminName = PropertyUtil.getSchemaProperty(context, "attribute_Weight");
				DomainObject doj = new DomainObject(sObjectId);
				doj.setAttributeValue(context, sAdminName, sNewValue + " "

                                                                                                + units_Weight[0]);
																								
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(isContextPushed){
					ContextUtil.popContext(context);
				}
			} */
		}
		
		public void updateGoogWeighedMassForPLMAnalyst(Context context, String[] args) throws Exception {
		/* boolean isContextPushed = false;
			try {
				ContextUtil.pushContext(context);
				isContextPushed = true;
				HashMap programMap = (HashMap) JPO.unpackArgs(args);
				HashMap paramMap = (HashMap) programMap.get("paramMap");
				HashMap requestMap = (HashMap) programMap.get("requestMap");
				String[] units_Weight = (String[]) requestMap.get("units_Weighed Mass");
				String sObjectId = (String) paramMap.get("objectId");
				String sNewValue = (String) paramMap.get("New Value");
				String sAdminName = PropertyUtil.getSchemaProperty(context, "attribute_googWeighedMass");
				DomainObject doj = new DomainObject(sObjectId);
				doj.setAttributeValue(context, sAdminName, sNewValue + " "

                                                                                                + units_Weight[0]);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(isContextPushed){
					ContextUtil.popContext(context);
				}
			} */
		}
	
	public boolean isPersonAssigned(Context context, String[] args) throws Exception {
		StringBuilder followerListBuilder = new StringBuilder();
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		String objectId = (String) paramMap.get("objectId");
		StringList objectSelects = new StringList(DomainObject.SELECT_ID);
		objectSelects.addElement(SELECT_NAME);
		objectSelects.addElement(SELECT_TYPE);
		StringList relSelects = new StringList(DomainRelationship.SELECT_ID);
		relSelects.addElement("attribute[" + ATTRIBUTE_ROUTE_TASK_USER + "]");
		DomainObject routeObj = DomainObject.newInstance(context, objectId);
		String owner = routeObj.getInfo(context,SELECT_OWNER);
		String contextUser = context.getUser();
		if(owner.equals(contextUser)) {
			return true;
		}
		
		MapList personList = routeObj.getRelatedObjects(context, // Context
				RELATIONSHIP_ROUTE_NODE, TYPE_PERSON + "," + TYPE_ROUTE_TASK_USER, objectSelects, relSelects, false,
				true, (short) 1, EMPTY_STRING,
				null,
				0);
		Iterator itr = personList.iterator();
		
		while (itr.hasNext()) {
			Map personInfo = (Map) itr.next();
			String personName = (String) personInfo.get(SELECT_NAME);
			String type = (String) personInfo.get(SELECT_TYPE);
			if (type.equals(TYPE_ROUTE_TASK_USER)) {
				String taskRole = (String) personInfo.get("attribute[" + ATTRIBUTE_ROUTE_TASK_USER + "]");
				if (context.isAssigned(PropertyUtil.getSchemaProperty(context, taskRole))) {
					return true;
				}
			}
			if (personName.equals(contextUser)) {
				return true;
			}
		}

		return false;
	}
	
	public void updateAttributeValueForCAProposedActivity(Context context, String[] args)
			throws Exception {
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			HashMap sColumnMap = (HashMap) programMap.get("columnMap");
			HashMap settings = (HashMap) sColumnMap.get("settings");
			String sAdminType = (String) settings.get("Admin Type");
			String sAdminTypeSysmbolicName = PropertyUtil.getSchemaProperty(
					context, sAdminType);
			HashMap paramMap = (HashMap) programMap.get("paramMap");
			//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Starts
			String sRelId = (String) paramMap.get("relId");
			//String sObjId = (String) paramMap.get("id");
			//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Ends
			String strNewValue = (String) paramMap
					.get(ChangeConstants.NEW_VALUE);
			//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Starts
			//DomainRelationship rel = new DomainRelationship(sRelId);
			String[] relationshipIds=new String[1];
			  StringList selectable=new StringList( DomainRelationship.SELECT_TYPE);
			  selectable.add(DomainRelationship.SELECT_TO_ID);
			selectable.add(DomainRelationship.SELECT_FROM_ID);
			  relationshipIds[0]=sRelId;
			   DomainRelationship ProposedActivityRel =new DomainRelationship(sRelId);
			  MapList mapList=ProposedActivityRel.getInfo(context, relationshipIds,selectable);
			  Map map=(Map)mapList.get(0);
			String sObjId = (String) map.get(DomainRelationship.SELECT_TO_ID);
			DomainObject doj = new DomainObject(sObjId);
			doj.setAttributeValue(context, sAdminTypeSysmbolicName, strNewValue);
			//Modified for Proposed Activity Rel to Object Migration - By Ravindra - Ends
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used to Search Configuration UI for getting Excluded/Included type list
	 * @param context
	 * @param args
	 * @return Map
	 * @throws Exception
	 * @author shajil
	 */
	public Map<String, Object> excludedSearchTypes(Context context, String[] args) throws Exception {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomSearchConfiguration");
		String excludedTypes = pageInfo.get("googCustomExcludedTypeDisplay");
		StringList excludedList = convertStringToStringList(excludedTypes);
		StringList includedList = getPersonIncludedTypeNmaes(context);
		excludedList = refineList(excludedList, includedList);
		includedList = refineList(includedList, excludedList);
		Map<String, Object> resultList = new HashMap<String, Object>();
		excludedList.sort();
		includedList.sort();
		resultList.put("excludedList", excludedList);
		resultList.put("includedList", includedList);
		resultList.put("pageInfo", setDisplayName(context, pageInfo));
		return resultList;
	}

	public Map<String, String> setDisplayName(Context context, Map<String, String> pageInfo) {
		String groupName = pageInfo.get("GroupTypeNames");
		String[] groupNames = groupName.split(",");
		Locale locale = context.getSession().getLocale();
		for (String name : groupNames) {
			String[] groups = name.split(",");
			name = name.replaceAll(" ", "");
			StringBuilder displayNames = new StringBuilder();
			String typeName = null;
			for (String type : groups) {
				typeName = pageInfo.get(name.replaceAll(" ", ""));
				String[] typeNames = typeName.split(",");
				for (String types : typeNames) {
					String strToReturn = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", locale,
							"emxFramework.Type." + types.replaceAll(" ", "_"));
					displayNames.append(strToReturn).append(",");
				}
			}
			pageInfo.put(name, displayNames.substring(0, displayNames.lastIndexOf(",")).toString());
		}
		return pageInfo;
	}

	public StringList getPersonIncludedTypeNmaes(Context context) throws Exception {
		StringList typeNames = new StringList();
		try {
			String contextUser = context.getUser();
			ContextUtil.pushContext(context);
			String excludedTypeNames = MqlUtil.mqlCommand(context, "print person '$1' select $2 dump", contextUser,
					"property[CustomFilteredTypes].value");
			if (UIUtil.isNullOrEmpty(excludedTypeNames)) {
				typeNames = convertStringToStringList(
						getDefaultExcludedTypeNames(context, "googCustomExcludedTypeDisplay"));
			} else {
				typeNames = getDisplayNames(context, excludedTypeNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
		return typeNames;
	}

	public StringList getDisplayNames(Context context, String excludedTypes) throws Exception {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomSearchConfiguration");
		String groupName = pageInfo.get("GroupTypeNames");
		String[] excludedTypeNames = excludedTypes.split(",");
		StringBuilder excludedDisplayTypes = new StringBuilder();
		StringList excludedTypeList = new StringList();
		for (String excludedType : excludedTypeNames) {
			String[] groupNames = groupName.split(",");
			boolean existInGroup = false;
			if (groupNames.length > 0) {
				for (String name : groupNames) {
					String typeName = pageInfo.get(name.replaceAll(" ", ""));
					if (typeName.contains(excludedType)) {
						existInGroup = true;
						if (!excludedTypeList.contains(name)) {
							excludedTypeList.add(name);
						}
					}
				}
			} else {
				return convertStringToStringList(excludedTypes);
			}
			if (!existInGroup) {
				excludedTypeList.add(excludedType);
			}
		}
		return excludedTypeList;
	}

	/**
	 * This method will compare the StringList and refine the data
	 * 
	 * @param excludedList
	 * @param includedList
	 * @return StringList
	 * @author shajil
	 */
	public StringList refineList(StringList excludedList, StringList includedList) {
		for (int i = 0; i < includedList.size(); i++) {
			String listVal = (String) includedList.get(i);
			if (excludedList.contains(listVal)) {
				excludedList.remove(listVal);
			}
		}
		return excludedList;
	}

	/**
	 * This method is used get the default excluded type in Global search
	 * 
	 * @param context
	 * @param args
	 * @return StringList
	 * @throws Exception
	 * @author shajil
	 */
	/*
	 * public StringList getDefaultExcludedList(Context context, String[] args)
	 * throws Exception { String excludedTypes =
	 * getDefaultExcludedTypeNames(context); StringList excludedList =
	 * convertStringToStringList(excludedTypes); return excludedList; }
	 */

	/**
	 * This method is used get the default excluded type in Global search
	 * 
	 * @param context
	 * @return String
	 * @throws FrameworkException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @author shajil
	 */
	public String getDefaultExcludedTypeNames(Context context, String keyName)
			throws FrameworkException, UnsupportedEncodingException, IOException {
		googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
		Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomSearchConfiguration");
		String excludedTypes = pageInfo.get(keyName);
		return excludedTypes;
	}

	/**
	 * This method used to convert String values to StringList
	 * @param excludedTypes
	 * @return StringList
	 * @author shajil
	 */
	public StringList convertStringToStringList(String excludedTypes) {
		StringList typeList = FrameworkUtil.split(excludedTypes, ",");
		StringList childMapList = new StringList();
		for (int j = 0; j < typeList.size(); j++) {
			String arrName = (String) typeList.get(j);
			childMapList.add(arrName);
		}
		return childMapList;
	}

	/**
	 * This method is used to update Filtered types for Person
	 * 
	 * @param context
	 * @param args
	 * @throws Exception
	 * @author shajil
	 */
	public void updatePersonSearchTypes(Context context, String[] args) throws Exception {
		try {
			String contextUser = context.getUser();
			ContextUtil.pushContext(context);
			googCustomIssue_mxJPO customIssue = new googCustomIssue_mxJPO();
			Map<String, String> pageInfo = customIssue.getPageInfo(context, "googCustomSearchConfiguration");
			String excludedTypes = pageInfo.get("googCustomExcludedTypes");
			StringList excludeList = convertStringToStringList(excludedTypes);
			String typeNames = "";
			StringList selectedTypes = getSelectedTypes(pageInfo, args);
			for (int i = 0; i < selectedTypes.size(); i++) {
				String typeVal = (String) selectedTypes.get(i);
				if (excludeList.contains(typeVal)) {
					excludeList.remove(typeVal);
				}
			}
			for (int count = 0; count < excludeList.size(); count++) {
				if (UIUtil.isNotNullAndNotEmpty(typeNames)) {
					typeNames = typeNames + ",";
				}
				typeNames = typeNames + excludeList.get(count);
			}
			MqlUtil.mqlCommand(context, "mod person '$1' property $2 value $3 ", contextUser, "CustomFilteredTypes",
					typeNames);

		} catch (Exception exception) {
			throw exception;
		} finally {
			ContextUtil.popContext(context);
		}
	}

	public StringList getSelectedTypes(Map<String, String> pageInfo, String[] args) throws Exception {
		StringList selectedTypes = new StringList();
		for (String selectedType : args) {
			String typeName = selectedType.replaceAll(" ", "");
			if (pageInfo.containsKey(typeName)) {
				String groupTypes = pageInfo.get(typeName);
				selectedTypes.addAll(FrameworkUtil.split(groupTypes, ","));
			}else {
				selectedTypes.add(selectedType);
			}
		}
		return selectedTypes;
	}

	/**
	 * This method is used to get the Global search types for Person
	 * @param context
	 * @param args
	 * @return String
	 * @throws Exception
	 * @author shajil
	 */
	public String getPersonExcludedSearchTypes(Context context, String[] args) throws Exception {
		String typeNames = "";
		String key = "googCustomExcludedTypes";
		try {
			String contextUser = context.getUser();
			ContextUtil.pushContext(context);
			typeNames = MqlUtil.mqlCommand(context, "print person '$1' select $2 dump", contextUser,
					"property[CustomFilteredTypes].value");
			if (UIUtil.isNullOrEmpty(typeNames)) {
				typeNames = getDefaultExcludedTypeNames(context, key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
		return typeNames;
	}
	
	
//Added by Xploria to add Specification Column in EBOM Table Starts here
	public Vector getConnectedDrawingDetailsOfECParts(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		MapList mlObjList = (MapList) programMap.get("objectList");
		Vector vSpecs = new Vector();
		String REL_PART_SPECIFICATION = PropertyUtil.getSchemaProperty("relationship_PartSpecification");
		String TYPE_CAD_MODEL = PropertyUtil.getSchemaProperty("type_CADModel");
		String TYPE_CAD_DRAWING = PropertyUtil.getSchemaProperty("type_CADDrawing");
		String TYPE_GOOG_CAD_MODEL = PropertyUtil.getSchemaProperty("type_googCADModel");
		String TYPE_PATTERN = TYPE_CAD_MODEL + ","+TYPE_CAD_DRAWING+","+TYPE_GOOG_CAD_MODEL;
		StringBuilder sbSpecBuilder;
		if (null != mlObjList) {
			StringList selectRelStmts = new StringList(1);
			selectRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);

			StringList selectStmts = new StringList(4);
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement(DomainConstants.SELECT_TYPE);
			selectStmts.addElement(DomainConstants.SELECT_REVISION);
			selectStmts.addElement("format.file.name");
			MapList cadList = null;
			for (int i = 0; i < mlObjList.size(); i++) {
				sbSpecBuilder = new StringBuilder();
				Map mpPartDetails = (Map) mlObjList.get(i);
				String objectId = (String) mpPartDetails.get(DomainConstants.SELECT_ID);
				DomainObject dobject = new DomainObject(objectId);
				DomainConstants.MULTI_VALUE_LIST.add("format.file.name");
				MapList mlSpecList = dobject.getRelatedObjects(context,
					                                                REL_PART_SPECIFICATION,
					                                                TYPE_PATTERN, 
					                                                selectStmts,
					                                                selectRelStmts,
					                                                false, 
					                                                true,
					                                                (short) 0, 
					                                                DomainConstants.EMPTY_STRING, 
					                                                DomainConstants.EMPTY_STRING, 
					                                                0);
															
					
		
				sbSpecBuilder.append("<table>");
				for (int j = 0; j < mlSpecList.size(); j++) {
					boolean bhasFile = false;
					StringList slFormatList = new StringList();
					Map mpSpec = (Map) mlSpecList.get(j);
					String strSpecName = (String)mpSpec.get(DomainConstants.SELECT_NAME);
					String strSpecID = (String)mpSpec.get(DomainConstants.SELECT_ID);
					String strSpecType = (String)mpSpec.get(DomainConstants.SELECT_TYPE);
					String strSpecRevision = (String)mpSpec.get(DomainConstants.SELECT_REVISION);
					Object formatList = (Object)mpSpec.get("format.file.name");
					if(formatList instanceof String){
						slFormatList.add((String)formatList);
					}else if(formatList instanceof StringList){
						slFormatList = (StringList)formatList;
					}
					
					
					for(int iFile = 0;iFile<slFormatList.size();iFile++){
						String slFormat = (String)slFormatList.get(iFile);
						if(slFormat.indexOf(".stp") > -1 || slFormat.indexOf(".STP") > -1 || slFormat.indexOf(".pdf") > -1 || slFormat.indexOf(".PDF") > -1){
							bhasFile = true;
							break;
						}
					}
					
				if (null != strSpecName && !strSpecName.isEmpty() && null != strSpecID && !strSpecID.isEmpty() && !slFormatList.isEmpty() && bhasFile) {
					
				        strSpecRevision = "[" + strSpecRevision + "]";
					    sbSpecBuilder.append("<tr>");
					    sbSpecBuilder.append("<td>");
						sbSpecBuilder.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?mode=insert");
						sbSpecBuilder.append("&amp;objectId="+ strSpecID + "'");
					    sbSpecBuilder.append(", '800', '700', 'true', 'popup')\">");
					    sbSpecBuilder.append(strSpecName);
						sbSpecBuilder.append("</a>");
						sbSpecBuilder.append(strSpecRevision);
						sbSpecBuilder.append("</td>");
                        sbSpecBuilder.append("</tr>");

				         					
						}
				}
				sbSpecBuilder.append("</table>");
				vSpecs.add(sbSpecBuilder.toString());
			}

		}
		return vSpecs;
	}

//Added by Xploria to add Specification Column in EBOM Table Ends here
	
	
}// end of class
