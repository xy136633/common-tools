package com.thirdty.tools.poi.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.Setter;

/**
 * Excel单sheet页解析工具类，支持使用注解和配置文件，支持100万数据
 * 
 * @author whk00104/金豆-小蝴蝶
 * @since 2018-04
 */
public class SaxExcelHandler extends DefaultHandler {

    /**
     * Excel中的xml开始加载
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void startDocument() throws SAXException {
        if (supplier == null) {
            throw new SAXException("Supplier is null!");
        }
    }

    /**
     * Excel中的xml结束加载
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void endDocument() throws SAXException {
        supplier.over();
    }

    /**
     * xml元素开始解析
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        switch (name) {
            case "row":
            	rowIndex++;
				if (rowIndex > 1){
					data = new HashMap<String, String>();
					data.put("rowid", rowIndex+"");
				}
                break;
            case "c":
            	valueBuffer = new StringBuffer();
            	cellIndex = getColIndex(attributes.getValue("r"));
            	//判断单元格的值是SST 的索引，不能直接characters方法取值
                if (attributes.getValue("t")!=null && attributes.getValue("t").equals("s")){
                    isSSTIndex = true;
                }else{
                    isSSTIndex = false;
                }
                break;
            default:
                break;
        }
    }

    /**
     * xml元素结束解析
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void endElement(String uri, String localName, String name) throws SAXException {
        switch (name) {
            case "row":
                if (null != data) {
					supplier.put(data);
				}
                break;
            case "c":
            	String value = null;
            	if (isSSTIndex){
            		int idx = Integer.parseInt(valueBuffer.toString());
            		if (idx < sst.getCount()){
            			value = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            		}
                }else {
                   value = valueBuffer.toString();
                }
            	
            	if (rowIndex == 1) {
        			head.put(cellIndex, value);
        		} else if (null != head.get(cellIndex)) {
        			data.put(head.get(cellIndex), value);
        		}
                break;
            default:
                break;
        }
    }

    /**
     * xml元素中的内容
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
    	if (null != valueBuffer){
    		valueBuffer.append(new String(ch, start, length));
    	}
    }

    /**
     * 根据Excel列索引字母获取列索引数字
     * 
     * @author whk00104/金豆-小蝴蝶
     */
    private String getColIndex(String r) {
        return r.replaceAll("[^a-z^A-Z]", "");
    }

    /**
     * 将从Excel中读取的字符串转化为数据字段的格式
     * 
     * @author whk00104/金豆-小蝴蝶
     * @param valueStr
     * @param type
     * @return
     */
    private Object castValue(String valueStr, Class<?> type) {
        if (Integer.class.isAssignableFrom(type)) {
            return Integer.parseInt(valueStr);
        } else if (Double.class.isAssignableFrom(type)) {
            return Double.parseDouble(valueStr);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return Boolean.parseBoolean(valueStr);
        } else if (Date.class.isAssignableFrom(type)) {
            return parseDate(valueStr);
        } else if (String.class.isAssignableFrom(type)) {
            return valueStr;
        } else {
            Constructor<?>[] constructors = type.getClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes().length == 1
                        && constructor.getParameterTypes()[0].equals(String.class)) {
                    try {
                        return constructor.newInstance(valueStr);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {}
                }
            }
        }
        return null;
    }

    /**
     * 解析时间，只处理少数格式
     * 
     * @author whk00104/金豆-小蝴蝶
     * @param str
     * @return
     */
    private Date parseDate(String str) {
        Date date = null;
        try {
            DateUtil.getJavaDate(Double.parseDouble(str));
        } catch (NumberFormatException e) {}
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy/MM/dd").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-ddZ").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").parse(str);
            } catch (ParseException e) {}
        }
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy年MM月dd日").parse(str);
            } catch (ParseException e) {}
        }
        return date;
    }

    // 放置解析数据的队列
    @Setter
    private BlockQueueSupplier<Map<String, String>> supplier;
    @Setter
    private SharedStringsTable sst;

    // 当前单元格数据
    private Map<String, String> data = null;
    // 当前选择单元格的值
    private StringBuffer valueBuffer = null;
    // 行号
    private int rowIndex = 0;
    private boolean isSSTIndex;
    private String cellIndex = null;
    private Map<String, String> head = new HashMap<>();
}
