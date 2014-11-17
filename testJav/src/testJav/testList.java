package testJav;

import java.util.ArrayList;
import java.util.List;

public class testList {

	public static void main(String[] args) {
//		testContainString();
		testContainInteger();
	}

	static void testContainString(){
		List<String> list = new ArrayList<String>();
		list.add(new String("a"));
		list.add(new String("b"));
		list.add(new String("c"));
		System.out.println(list.contains("a"));
		list.remove("a");
		list.remove("d");
		for(String s:list){
			System.out.println(s);
		}
	}
	
	static void testContainInteger(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(new Integer(3));
		System.out.println(list.contains(3));
		for(int s:list){
			System.out.println(s);
		}
	}
}
