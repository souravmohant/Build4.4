/* 

 ** 

 **********************************************************
 ** JPO name : googRangeAttribute
 ** Version : 0.1
 **
 ** Version, Date, Auteur, Description
 ** 0.1, May 4th 2020, XPLORIA, Creation
 ** 
 **********************************************************
 */
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.util.StringList;


/**
 * Comment for googRangeAttribute
 * 
 * @author ley
 */
public class googRangeAttribute_mxJPO {

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public googRangeAttribute_mxJPO(Context context, String[] args) throws Exception {
    }
    
    /**
     * args 0 : value list
     * @param context
     * @param args
     * @throws Exception
     */
    public int mxMain(Context context, String[] args) throws Exception {
        try {
        	String attrValueList = "";
        	if(args.length >= 1) {
        		attrValueList = args[0];
        	} else {
        		attrValueList = MqlUtil.mqlCommand(context,"get env ATTRNAME");
        	}
        	attrValueList = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Attribute." + attrValueList.replace(" ", "_"), new Locale(context.getSession().getLanguage()));
          
            StringList slValues = new StringList();
            StringBuffer valueList = new StringBuffer ();
            if(!attrValueList.isEmpty()) {
            	StringList objectSelect = new StringList(2);
		        objectSelect.add(DomainObject.SELECT_ID);
		        DomainObject.MULTI_VALUE_LIST.add("from[Subclass].to.name");
		        objectSelect.add("from[Subclass].to.name");
		        ContextUtil.pushContext(context);
            	MapList resultsList = DomainObject.findObjects(context,                    
                        "General Class",                 	
                        attrValueList,             	
                          "*",           				
                          "*",           				
                         "eService Production",      
                          null,                   
                          true,       			   	
                          objectSelect);
            	ContextUtil.popContext(context);
            	DomainObject.MULTI_VALUE_LIST.remove("from[Subclass].to.name");
            	if(!resultsList.isEmpty()) {
            		Map resultMap = (Map)resultsList.get(0);
            		slValues = (StringList)resultMap.get("from[Subclass].to.name");
            		slValues.sort();
            	     }
            	for(int i =0;i<slValues.size();i++) {
            		 valueList.append("{");
                     valueList.append(slValues.get(i));
                     valueList.append("} ");
            	}
            }
            
           PropertyUtil.setGlobalRPEValue( context,"googRangeAttribute",valueList.toString());
        } catch (FrameworkException Ex) {
            Ex.printStackTrace();
            throw Ex;
        }
        return 0;
    }

}
