package com.thirdty.tools.poi.util;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
 
public class ReadExcelUtils {
	private int headCount = 1;
	private List<String> head = new ArrayList<String>();
	private List<List<String>> rowLists = new ArrayList<List<String>>();
	public String excelPath;
 
	public ReadExcelUtils(String excelPath) {
		this.excelPath = excelPath;
		try {
			processSAXReadSheet();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
 
	// 返回表头信息
	public List<String> getHead() {
		return head;
	}
 
	// 返回数据
	public List<List<String>> getList() {
		return rowLists;
	}
 
	public void processSAXReadSheet() throws IOException, OpenXML4JException, SAXException {
 
		OPCPackage pkg = OPCPackage.open(excelPath);
		XSSFReader xssfReader = new XSSFReader(pkg);
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
 
		Iterator<InputStream> sheets = xssfReader.getSheetsData();
		//循环读取sheets
		while (sheets.hasNext()) {
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
	}
 
	private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		// 利用XMLReaderFactory工厂类，创建XMLReader对象。
		// System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
		XMLReader parser = XMLReaderFactory.createXMLReader();
		// XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ContentHandler handler = new SheetHandler(sst);
		parser.setContentHandler(handler);
		return parser;
	}
 
	/**
	 * SAX 解析excel
	 */
	private class SheetHandler extends DefaultHandler {
		private SharedStringsTable sst;
		private String cellContent;
		private boolean isNewRow;
		private boolean isString;
		private int rowIndex = 0;
		private List<String> rowContent = new ArrayList<String>();
		// cell位置，如A8
		private String preRef = null;
		private String ref = null;
		private String maxRef = null;
 
		private SheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}
 
		/*
		 * cell为空的两种情况： 1. cell中原来有数据，把数据清空后，cell为空，xml为：<c r="B2" />
		 * 
		 * 2.cell原本就为空，xml为：不存在此节点 ， 如下不存在<c r="B1"></c>节点 <c r="A1" s="1"
		 * t="s"> <v>0</v> </c> <c r="C1" s="1" t="s"> <v>2</v> </c>
		 */
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			if (name.equals("row")) {
				rowIndex++;
				isNewRow = true;
			}
			// c : cell
			else if (name.equals("c")) {
				if (isNewRow == true) {
					preRef = ""+(char)('A' - 1) + (rowLists.size() + 1);
					//preRef = attributes.getValue("r");
				} else {
					preRef = ref;
				}
				ref = attributes.getValue("r");
 
				String cellType = attributes.getValue("t");
				if (cellType == null) {
					isString = false;
				} else {
					isString = true;
				}
				// 清空cellContent
				cellContent = "";
			}
 
		}
 
		public void characters(char[] ch, int start, int length) throws SAXException {
			cellContent += new String(ch, start, length);
		}
 
		public void endElement(String uri, String localName, String name) throws SAXException {
			if (name.equals("row")) {
				if (rowIndex == headCount) {
					head = rowContent;
					maxRef = ref;
				} else if (rowIndex > headCount) {
					if (rowContent != null) {
						// 处理空单元格
						while ((maxRef.charAt(0) - ref.charAt(0)) > 0) {
							rowContent.add(null);
							ref = (char) (ref.charAt(0) + 1) + ref.substring(1, ref.length());
						}
						rowLists.add(rowContent);
					}
				}
				rowContent = null;
			} else if (name.equals("c")) {
				//新的row 
				if (isNewRow == true) {
					rowContent = new ArrayList<String>();
					isNewRow = false;
				}
				
				// 处理空单元格
				while ((ref.charAt(0) - preRef.charAt(0)) > 1) {
					rowContent.add(null);
					preRef = (char) (preRef.charAt(0) + 1) + preRef.substring(1, preRef.length());
				}
				//cellContent为String
				if (isString) {
					int idx = Integer.parseInt(cellContent);
					cellContent = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
				} else {   ///cellContent为int
					// cell为空的第一种情况
					if (cellContent.equals("")) {
						cellContent = null;
					} else {
 
					}
				}
				
				rowContent.add(cellContent);
			}
 
		}
	}
}