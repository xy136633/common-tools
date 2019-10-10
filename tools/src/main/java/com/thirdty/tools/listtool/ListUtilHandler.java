package com.thirdty.tools.listtool;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.common.collect.Lists;

public class ListUtilHandler {
	
	public static void main(String[] args) {
		List<String> list1 = Lists.newArrayList("a", "b", "c");
		List<String> list2 = Lists.newArrayList("a", "b", "c", "d", "e");
		List<String> list3 = Lists.newArrayList("d", "c", "e", "c", "f");
		
		// list1 与  list2的交集
		System.err.println("list1 与  list2的交集 " + ListUtils.retainAll(list1, list2));
		// list1 与  list3的差集
		System.err.println("list1 与  list3的差集 " + ListUtils.subtract(list1, list3));
		// list3 与  list1的差集
		System.err.println("list3 与  list1的差集 " + ListUtils.subtract(list3, list1));
	}
}
