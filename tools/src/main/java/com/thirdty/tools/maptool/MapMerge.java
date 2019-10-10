package com.thirdty.tools.maptool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.thirdty.tools.maptool.model.UserAccount;

public class MapMerge {

	private static List<UserAccount> initDatas(){
		List<UserAccount> userAccountList = new ArrayList<UserAccount>();
		String ua1Id = UUID.randomUUID().toString();
		String ua2Id = UUID.randomUUID().toString();
		String ua3Id = UUID.randomUUID().toString();
		userAccountList.add(UserAccount.builder().userId(ua1Id).userName("张三").money(100l).build());
		userAccountList.add(UserAccount.builder().userId(ua1Id).userName("张三").money(200l).build());
		userAccountList.add(UserAccount.builder().userId(ua1Id).userName("张三").money(300l).build());
		userAccountList.add(UserAccount.builder().userId(ua2Id).userName("李四").money(100l).build());
		userAccountList.add(UserAccount.builder().userId(ua2Id).userName("李四").money(100l).build());
		userAccountList.add(UserAccount.builder().userId(ua3Id).userName("王五").money(300l).build());
		userAccountList.add(UserAccount.builder().userId(ua3Id).userName("王五").money(100l).build());
		return userAccountList;
	}
	
	// 常规方法
	private static void sumAccountMoney(List<UserAccount> userAccountList){
		Map<String, Long> resultMap = new HashMap<String, Long>();
		userAccountList.stream().forEach(item -> {
			if (resultMap.containsKey(item.getUserId())){
				resultMap.put(item.getUserId(), resultMap.get(item.getUserId()) + item.getMoney());
			}else{
				resultMap.put(item.getUserId(), item.getMoney());
			}
		});
				
		resultMap.entrySet().stream().forEach(item -> System.err.println(item.getKey() + " = " + item.getValue()));
	}
	
	// merge方法
	private static void sumAccountMoneyMerge(List<UserAccount> userAccountList){
		Map<String, Long> resultMap = new HashMap<String, Long>();
		userAccountList.stream().forEach(item -> resultMap.merge(item.getUserId(), item.getMoney(), Long::sum));
		
		resultMap.entrySet().stream().forEach(item -> System.err.println(item.getKey() + " = " + item.getValue()));
	}
	
	public static void main(String[] args) {
		List<UserAccount> userAccountList = initDatas();
		sumAccountMoney(userAccountList);
		sumAccountMoneyMerge(userAccountList);
	}
}
