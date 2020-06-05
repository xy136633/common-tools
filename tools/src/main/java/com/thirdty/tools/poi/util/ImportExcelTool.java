package com.thirdty.tools.poi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ImportExcelTool<T> {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");// 格式化 number为整

    private static final DecimalFormat DECIMAL_FORMAT_PERCENT = new DecimalFormat("##.00%");//格式化分比格式，后面不足2位的用0补齐

//	private static final DecimalFormat df_per_ = new DecimalFormat("0.00%");//格式化分比格式，后面不足2位的用0补齐,比如0.00,%0.01%

//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // 格式化日期字符串

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy/MM/dd");

    private static final DecimalFormat DECIMAL_FORMAT_NUMBER = new DecimalFormat("0.00E000"); //格式化科学计数器

    private static final Pattern POINTS_PATTERN = Pattern.compile("0.0+_*[^/s]+"); //小数匹配

	public List<T> readExcelContent(Workbook wb,Sheet sheet,Row row, Class c, Map<String, String> fieldRefMap)throws  Exception{
        List<T> list = new ArrayList<>();
        sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        //获取表头
        Row rows = sheet.getRow(0);
        int colNum = rows.getPhysicalNumberOfCells();
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int j = 0;
            T yb = (T) c.newInstance();
            while (j < colNum) {
                //获取字段名
                Object name = getCellFormatValue(rows.getCell(j));
                if (null == fieldRefMap.get(String.valueOf(name))){
                	j++;
                	continue;
                }
                //获取
                Object obj = getCellFormatValue(row.getCell(j));
                //利用反射 对属性进行赋值
                Class clazz = yb.getClass();
                Field field = clazz.getDeclaredField((fieldRefMap.get(String.valueOf(name))));
                field.setAccessible(true);// 要设置属性可达，不然会抛出IllegalAccessException异常
                if(!"".equals(obj) && obj!=null){
                	if (obj instanceof Date){
                		field.set(yb,(Date)obj);
                	}else if (obj instanceof String){
                		field.set(yb,(String)obj);
                	}
                }
                j++;
            }
            list.add(yb);
        }
        return list;
    }
	
	public static void readExcelContent(String filePath, BlockingQueue<Map<String,String>> queue)throws  Exception{
		InputStream is = new FileInputStream(new File(filePath));
		Workbook wb = WorkbookFactory.create(is);
//		if(filePath.endsWith(".xls")){
//            wb = new HSSFWorkbook(is);
//        }else if(filePath.endsWith(".xlsx")){
//            wb= new XSSFWorkbook(is);
//        }
		 
		
        Sheet sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        //获取表头
        Row rows = sheet.getRow(0);
        int colNum = rows.getPhysicalNumberOfCells();
        Row row;
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("rowid", i+1+"");
            int j = 0;
            while (j < colNum) {
                //获取字段名
            	Cell fieldCell = rows.getCell(j);
            	Cell valueCell = row.getCell(j);
            	dataMap.put((String)getCellValue(fieldCell), (String)getCellValue(valueCell));
                j++;
            }
            queue.put(dataMap);
			TimeUnit.MILLISECONDS.sleep(20);
        }
    }
	
	private Object getCellFormatValue(Cell cell) {
        Object cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式
                        // data格式是带时分秒的：2013-7-10 0:00:00
                        // cellvalue = cell.getDateCellValue().toLocaleString();
                        // data格式是不带带时分秒的：2013-7-10
                        Date date = cell.getDateCellValue();
                        cellvalue = date;
                    } else {// 如果是纯数字

                        // 取得当前Cell的数值
                        cellvalue =  StringUtils.removeEnd(String.valueOf(cell.getNumericCellValue()),".0");
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                default:// 默认的Cell值
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }
	
	private static Object getCellValue(Cell cell) {
        Object value = null;
        switch (cell.getCellTypeEnum()) {
            case _NONE:
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) { //日期
//                    value = FAST_DATE_FORMAT.format(DateUtil.getJavaDate(cell.getNumericCellValue()));//统一转成 yyyy/MM/dd
                    value = DateUtil.getJavaDate(cell.getNumericCellValue()); //转成Date格式
                } else if ("@".equals(cell.getCellStyle().getDataFormatString())
                        || "General".equals(cell.getCellStyle().getDataFormatString())
                        || "0_ ".equals(cell.getCellStyle().getDataFormatString())) {
                    //文本  or 常规 or 整型数值
                    value = DECIMAL_FORMAT.format(cell.getNumericCellValue());
                } else if (POINTS_PATTERN.matcher(cell.getCellStyle().getDataFormatString()).matches()) { //正则匹配小数类型
                    value = cell.getNumericCellValue();  //直接显示
                } else if ("0.00E+00".equals(cell.getCellStyle().getDataFormatString())) {//科学计数
                    value = cell.getNumericCellValue();    //待完善
                    value = DECIMAL_FORMAT_NUMBER.format(value);
                } else if ("0.00%".equals(cell.getCellStyle().getDataFormatString())) {//百分比
                    value = cell.getNumericCellValue(); //待完善
                    value = DECIMAL_FORMAT_PERCENT.format(value);
                } else if ("# ?/?".equals(cell.getCellStyle().getDataFormatString())) {//分数
                    value = cell.getNumericCellValue(); ////待完善
                } else { //货币
                    value = cell.getNumericCellValue();
                    value = DecimalFormat.getCurrencyInstance().format(value);
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case BLANK:
                //value = ",";
                break;
            default:
                value = cell.toString();
        }
        return value;
    }
}
