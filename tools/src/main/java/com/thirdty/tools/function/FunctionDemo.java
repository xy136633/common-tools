package com.thirdty.tools.function;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionDemo {

	public static void main(String[] args) {
		new MyAccount(new BigDecimal("999999999999999.999")).printMoney1(i -> new DecimalFormat("#,###.####").format(i));
		new MyAccount(new BigDecimal("999999999999999.999")).printMoney2(i -> new DecimalFormat("#,###.####").format(i));
		
		// 函数接口链式操作
		Function<BigDecimal, String> function = i -> new DecimalFormat("#,###.####").format(i);
		MyAccount myAccount = new MyAccount(new BigDecimal("999999999999999.999"));
		myAccount.printMoney2(function.andThen(s -> "人民币 " + s));
		
		// 断言接口函数
		Predicate<Integer> predicate = i -> i>0;
		System.err.println(predicate.test(8));
		
		// 消费者函数接口
		Consumer<String> consumer = s -> System.err.println(s);
		consumer.accept("输入数据");
		
		// 方法引用
		Consumer<String> consumer1 = s -> System.err.println(s);
		consumer1.accept("方法引用1");
		Consumer<String> consumer2 = System.err::println;
		consumer2.accept("方法引用2");
	}
}

interface IMoneyFormat{
	String format(BigDecimal monery);
}

class MyAccount{
	private final BigDecimal money;
	
	public MyAccount(BigDecimal money){
		this.money = money;
	}
	
	public void printMoney1(IMoneyFormat moneyFormat){
		System.err.println("我的账户余额：" + moneyFormat.format(this.money));
	}
	
	public void printMoney2(Function<BigDecimal, String> function){
		System.err.println("我的账户余额：" + function.apply(this.money));
	}
}