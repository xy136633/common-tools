package com.thirdty.tools.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import lombok.Setter;

public class SaxExcelUtil {

	@Setter
	public String excelPath;
	
	// 解析Excel使用的线程池
    private ThreadPoolExecutor executors = new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
	
	public BlockQueueSupplier<Map<String, String>> processSAXReadSheet() throws IOException, OpenXML4JException, SAXException {
		BlockQueueSupplier<Map<String, String>> supplier = new BlockQueueSupplier<Map<String, String>>();
		
		executors.submit(() -> {
			
			try {
				OPCPackage pkg = OPCPackage.open(excelPath);
				XSSFReader xssfReader = new XSSFReader(pkg);
				XMLReader parser = XMLReaderFactory.createXMLReader();
				SharedStringsTable sst = xssfReader.getSharedStringsTable();
				SaxExcelHandler handler = new SaxExcelHandler();
				handler.setSupplier(supplier);
				handler.setSst(sst);
				parser.setContentHandler(handler);
				
				Iterator<InputStream> sheets = xssfReader.getSheetsData();
				//循环读取sheets
				while (sheets.hasNext()) {
					InputStream sheet = sheets.next();
					InputSource sheetSource = new InputSource(sheet);
					parser.parse(sheetSource);
					sheet.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return supplier;
	}
}
