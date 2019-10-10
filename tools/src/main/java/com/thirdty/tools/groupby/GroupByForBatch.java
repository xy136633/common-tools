package com.thirdty.tools.groupby;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupByForBatch {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		for (int i=1; i<=55; i++){
			list.add(String.valueOf(i));
		}
		
		int batch = 10;
		int[] index = {0};
		Map<Integer, List<String>> map = list.stream()
				                             .collect(Collectors.groupingBy(item -> index[0]++/batch, Collectors.toList()));
		
		map.entrySet().stream().forEach(item -> System.err.println(item.getKey() + " = " + item.getValue()));
	}
}
