
public class Measurments {
	
	public enum sequenceRef{Sequence1,Sequence2}
	
	public static void main(String[] args) {
		
		sequence1(1000);
		sequence1(2000);
		sequence1(3000);

		sequence2(3000);
		sequence2(2000);
		sequence2(1000);
		
		
		
		double sum = 0;
		for (int i = 0; i < 100; i++) {
			sum += sequence2(30000);
		}
		double avg = sum/100;
		System.out.println(avg);

	}
	
	public static double sequence1(int m) {
	
		FibonacciHeap fibHeap = new FibonacciHeap();
		double startTime = System.currentTimeMillis();
		for(int i = m; i>0;i--) {
			fibHeap.insert(i);
		}
		double stopTime = System.currentTimeMillis();
		double runTime = (stopTime - startTime);
		int links = FibonacciHeap.totalLinks();
		int cuts = FibonacciHeap.totalCuts();
		int potential = fibHeap.potential();
		
		printResults(runTime,links,cuts,potential,m,sequenceRef.Sequence1);
		return (runTime);
	}
	
	public static double sequence2(int m) {
		FibonacciHeap fibHeap2 = new FibonacciHeapPrintable();
		fibHeap2.numOfLinks =0;
		double startTime = System.currentTimeMillis();
		for(int i = m; i>0;i--) {
			fibHeap2.insert(i);
		}
		for(int i =0; i<m/2; i++) {
			fibHeap2.deleteMin();
		}
		double stopTime = System.currentTimeMillis();
		double runTime = (stopTime - startTime);
		int links = FibonacciHeap.totalLinks();
		int cuts = FibonacciHeap.totalCuts();
		int potential = fibHeap2.potential();
		
		printResults(runTime,links,cuts,potential,m,sequenceRef.Sequence2);
		return runTime;
	}
	
	public static void printResults(double runTime, int links, int cuts, int potential, int m, sequenceRef ref) {
		System.out.println("********************************************");
		System.out.println("Test: "+ ref+"\n" + "m = " + m);
		System.out.println("******************");
		System.out.println("Run Time: " + runTime + "\n"+ "Total Links: " + links+ "\n"+ 
							"Total Cuts: " + cuts+ "\n"+ "Potential: " + potential);
		System.out.println("********************************************");
	}

}
