package com.thirdty.tools.poi.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.thirdty.tools.poi.util.BlockQueueSupplier;
import com.thirdty.tools.poi.util.SaxExcelUtil;

/**
 * 
* @ClassName: BaCheckTestController  
* @Description: 质控预处理     满足医院提供excel病案数据进行病案质控（注意：标题名要与质控接口文档中字段名称一致）
*               质控结果报错到test_ba_base_fail_log（基础）  test_ba_rule_fail_log（规则校验）
*               通过row_id关联excel数据行号
* @author wangxiang  
* @date 2020年6月2日  
*
 */
@Component
public class BaCheckTestController{
	
//	private ExecutorService baseCheckThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
//	
//	@Autowired
//	private QcRuleCheckController abstractCheck;
//	@Autowired
//	private TestBaCheckService testBaCheckService;
//	
//	/**
//	 * 质控预处理
//	 * @throws InterruptedException 
//	 */
//	@RequestMapping(value = "/nologin/temp/check/v2", method = RequestMethod.GET)
//	public void baTempGroupV2(Integer queueSize) throws InterruptedException {
//		// 默认队列大小10000
//		if (null == queueSize || queueSize <= 0){
//			queueSize = 10000;
//		}
//		
//		BlockingQueue<Map<String,String>> queue = new LinkedBlockingQueue<>(queueSize);
//		// 分页查询数据线程
//		Thread dataThread = new Thread(new BaDataRunnable(queue));
//		// 基础校验及质控线程
////		Thread checkThread = new Thread(new BaCheckRunnable(queue,  abstractCheck, testBaCheckService));
//		dataThread.start();
//		TimeUnit.SECONDS.sleep(1);
//		try {
//			testBaCheckService.baDataCheck(queue);
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.err.println("============================================================");
//	}
//	
//	/**
//	 * 
//	* @Title: baTempCheckV1  
//	* @Description: 病案质控预处理  
//	* @param @param checkThreadNum 校验线程数量
//	* @param @param filePath excel文件路径
//	* @return void    返回类型  
//	* @throws
//	 */
//	@RequestMapping(value = "/nologin/temp/check/v1", method = RequestMethod.GET)
//	public void baTempCheckV1(Integer checkThreadNum, String filePath) {
//		BlockQueueSupplier<Map<String, String>> supplier = new BlockQueueSupplier<Map<String, String>>();
//		new Thread(new SaxBaDataRunnable(supplier, filePath)).start();
//		for (int i=0; i<checkThreadNum; i++){
//			baseCheckThreadPool.submit(new SaxBaCheckRunnable(supplier, abstractCheck, testBaCheckService));
//		}
//	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		try {
//			InputStream is = null;
//			ReadExcelUtils reu = new ReadExcelUtils("C:\\Users\\wangxiang\\Desktop\\tempwork\\四川青白江妇幼保健院\\2019-v1.xlsx");
//			reu.processSAXReadSheet();
//			List<String> heads = reu.getHead();
//			List<List<String>> dataList = reu.getList();
//			System.err.println(heads);
//			System.err.println(dataList);
			
			SaxExcelUtil saxUtil = new SaxExcelUtil();
			saxUtil.setExcelPath("C:\\Users\\wangxiang\\Desktop\\tempwork\\测试数据-20200603.xlsx");
			BlockQueueSupplier<Map<String, String>> supplier = saxUtil.processSAXReadSheet();
			
			Map<String, String> data;
			int count = 0;
			while ((data = supplier.get()) != null || !supplier.isQueueOver()){
				count++;
				System.err.println(data);
				System.err.println(count+"===================");
			}
			
			
//			Supplier<PoiExcelModel> supplier = POIExcelUtils.parseExcel(PoiExcelModel.class, is);
//			Stream.generate(supplier).peek((data) -> {
//				System.err.println(data);
//	        });
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
