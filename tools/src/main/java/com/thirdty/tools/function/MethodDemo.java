package com.thirdty.tools.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import com.google.common.base.Supplier;

public class MethodDemo {

	public static void main(String[] args) {
		Dog dog = new Dog();
		
		// 方法引用
		Consumer<String> consumer = System.err::println;
		consumer.accept("接收参数");
		
		// 静态方法引用   直接类名::静态方法
		Consumer<Dog> consumer2 = Dog::bark;
		consumer2.accept(dog);
		
		// 非静态方法引用  eat有输入输出 使用function
		// Function<Integer, Integer> function = dog::eat;
		// 输入输出类型一样  可以使用一元函数接口
		// UnaryOperator<Integer> function = dog::eat;
		// System.err.println("还剩" + function.apply(2));
		IntUnaryOperator function = dog::eat;
		System.err.println("还剩" + function.applyAsInt(2));
		
		// 非静态方法使用类名调用
		// jdk在非静态方法中会默认传入当前对象实例this，为方法的第一个参数
		// 两个入参 一个返回
		BiFunction<Dog, Integer, Integer> biFunction = Dog::eat;
		System.err.println("还剩" + biFunction.apply(dog, 1));
		
		// 构造函数方法引用
		Supplier<Dog> supplier = Dog::new;
		System.err.println("创建对象" + supplier.get());
		
		// 带参构造函数
		Function<String, Dog> function2 = Dog::new;
		System.err.println("带参构造函数创建对象" + function2.apply("拉布拉多"));
	}
}

class Dog{
	private String name = "二哈";
	private int num = 10;
	
	public Dog(){
		
	}
	
	public Dog(String name){
		this.name = name;
	}
	
	public static void bark(Dog dog){
		System.err.println(dog + "汪汪汪。。。");
	}
	
	public int eat(int i){
		System.err.println("吃了" + i);
		this.num -= i;
		return this.num;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}