package testsAndStuff;

public class TestStuff {

	public static void main(String[] args) {
		System.out.println("-1 % 7 = " + (-1%7));
		System.out.println("0+6 & 7 = " + (6));
		
		int loop1 = 0;
		int loop2 = 0;
		while (true){
			loop1++;
			loop2 = 0;
			while (true){
				loop2++;
				if (loop2 == 9){
					break;
				}
			}
		}
	}

}
