package testsAndStuff;

public class TestInheritanceSuper {
	
	public void printSomething(){
		System.out.println(stringThing());
	}
	
	public String stringThing(){
		return "SuperStringThing";
	}
	
	public static void main(String[] args){
		TestInheritanceSuper superTest = new TestInheritanceSuper();
		System.out.println("superTest.printSomething()");
		superTest.printSomething();
		System.out.println();
		
		TestInheritance subTest = new TestInheritance();
		System.out.println("subTest.printSomething()");
		subTest.printSomething();
		System.out.println();
		
		TestInheritanceSuper subAsSuperTest = new TestInheritance();
		System.out.println("subAsSuperTest.printSomething()");
		subAsSuperTest.printSomething();
		System.out.println();
	}
}
