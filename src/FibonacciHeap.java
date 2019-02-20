import java.util.Iterator;

/* Name:  Avital Friedland
 * ID: 315877126
 * Username: avitalfried
 * 
 * Name: Anat Lukach
 * ID: 301808911
 * Username: anatlukach
 */
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{	
	//min is like the root of the fibonacci heap:
	public HeapNode min;

	/*for potential function:
	 * need to be maintained in Insert & Delete
	 */
	public int numOfTrees;

	/* need to be maintained in Delete & Decrease Key */
	public int numOfMarked;

	/* for size: */
	public int numOfNodes;

	/* for totalLinks function */
	public static int numOfLinks;

	/* for totalCuts function */
	public static int numOfCuts;
	
	/* a doubly linked list of the roots of all trees in the heap	 */
	public CircularLinkedList rootList = new CircularLinkedList();


	/**
	 * public boolean empty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return this.min ==null; 
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
	 */
	public HeapNode insert(int key)
	{    
		HeapNode hNode = new HeapNode(key);
		rootList.insertAtEnd(hNode);
		numOfTrees++;
		numOfNodes++;
		if (this.empty()) {
			min = hNode;
		}
		else{
			if (min.key >hNode.key) {
				min = hNode;
			}
		}
		return hNode;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin()
	{
		numOfNodes -=1;
		numOfTrees--;

		
		HeapNode currentMin = min;
		if (currentMin !=null) { //if Heap is not empty
				if (currentMin.listOfChildren != null) {  //if minimum has children 
					for (HeapNode child : currentMin.listOfChildren) {  //add every child to rootList
						numOfTrees++;
						rootList.insertAtEnd(child);
						child.parent = null;
					}
				}
			}
			rootList.delete(currentMin);
			if(numOfTrees ==0) {  
				min = null;
			}
			else {
				min = rootList.start;
				consolidate();
			}
		}


	public void consolidate() {
		/* perform all links necessary 
		 * taken from Introduction To Algorithms Page 516
		 */
		HeapNode[] arrayOfTrees = new HeapNode[(int)(1.5*(Math.log(this.size())/Math.log(2)))+1];   //creating an array of approx. 1.5 log(n) 
		CircularLinkedList initial = this.rootList; 
		for(HeapNode treeRoot: initial) {
			HeapNode x = treeRoot;
			int curRank = treeRoot.rank;
			while(arrayOfTrees[curRank] !=null) {   //found another tree in curRank slot, need to link the trees - making sure we maintain heap order
				HeapNode another = arrayOfTrees[curRank];
				if (x.key > another.key ) {      //need to swap them
					HeapNode tmp = x;
					x = another;
					another = tmp;
				}
				numOfLinks++;

				performLink(another, x);  //making "another" a child of "x"
				numOfTrees--;
				arrayOfTrees[curRank] = null;
				curRank ++;
			}
			arrayOfTrees[curRank] = x;
		}
		/* update the root list - inserting every tree in arrayOfTrees to rootList & finding the new minimum */
		this.min = null;
		for (int i = 0; i < arrayOfTrees.length; i++) {
			if (arrayOfTrees[i] !=null) {
				if (min==null) {
					this.rootList = new CircularLinkedList();
					rootList.insertAtStart(arrayOfTrees[i]);
					min = arrayOfTrees[i];
				}
				else {
					rootList.insertAtEnd(arrayOfTrees[i]);
					if (arrayOfTrees[i].key <min.key) {
						min = arrayOfTrees[i];
					}
				}
			}
		}
	}

	/* preconditions:
	 *  y.key<x.key 
	 * y and x exist in rootList
	 * */

	public void performLink(HeapNode y, HeapNode x) {
		rootList.delete(y);
		addToListOfChild(x, y);
		x.rank ++;
		y.setMark(false);

	}

	public void addToListOfChild(HeapNode parent, HeapNode child) {
		child.parent = parent;
		if (parent.listOfChildren ==null) {
				parent.listOfChildren = new CircularLinkedList();
				parent.listOfChildren.insertAtEnd(child);
		}
			else {
				parent.listOfChildren.insertAtEnd(child);
			}
		}
	

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal. 
	 *
	 */
	public HeapNode findMin()
	{
		return min;
	} 

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld (FibonacciHeap heap2)
	{
		this.numOfTrees+= heap2.numOfTrees;
		this.numOfNodes += heap2.numOfNodes;

		if (heap2.empty()) {
			return;
		}
		if(this.empty()) {
			this.rootList.addAll(heap2.rootList);
			this.min = heap2.findMin();
			return;
		}
		else {
			if (this.findMin().getKey()>heap2.findMin().getKey()) {
				this.min = heap2.findMin();
			}
			this.rootList.addAll(heap2.rootList);		
			return;
		}
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return numOfNodes; 
	}


	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
	 * 
	 */
	public int[] countersRep(){
		int maxRank = getMaxRank();
		int [] countersArray = new int[maxRank+1]; 
		if(empty()) {
			return countersArray;
		}
		for(HeapNode x: rootList) {
			countersArray[x.getRank()]++;
		}
		return countersArray;
	}
	
	private int getMaxRank() {
		int maxRank =0;
		if(empty()) {
			return maxRank;
		}
		for(HeapNode x: rootList) {
			if(x.getRank()>maxRank) {
				maxRank = x.getRank();
			}
		}
		return maxRank;
	}
	

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap. 
	 *
	 */
	public void delete(HeapNode x) {    
		int delta = x.getKey()-min.getKey()+1;
		decreaseKey(x,delta);
		deleteMin(); //numOfNodes and numOfTrees are updated during deleteMin
		return; 
	}
	

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the heap should be updated
	 * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta){    
		try {
			x.setKey(delta);
		} catch (InvalidDeltaException e) {
			return;
		}
		HeapNode parent = x.getParent();
		if(parent!=null && x.getKey() < parent.getKey()) { // if x is root or x.key>parent.key heap order not violated
			cut(x,parent);
			cascadingCut(parent);
		}
		if(x.getKey()<min.getKey()) {
			min = x;
		}
		return;
	}
	
	public void cut(HeapNode child, HeapNode parent) {
		numOfCuts++;
		numOfTrees++;
		
		parent.listOfChildren.delete(child);
		parent.rank--;
		child.parent = null;
		child.setMark(false);
		rootList.insertAtEnd(child);
	}
	
	public void cascadingCut(HeapNode y) {
		HeapNode parent = y.getParent();
		if(parent!=null) {
			if(!y.isMarked()) {
				y.setMark(true);
			}
			else {
				cut(y,parent);
				cascadingCut(parent);
			}
		}
	}

	/**
	 * public int potential() 
	 *
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked
	 * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
	 */
	public int potential() 
	{    
		return numOfTrees +2*numOfMarked;
	}

	/**
	 * public static int totalLinks() 
	 *
	 * This static function returns the total number of link operations made during the run-time of the program.
	 * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
	 * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
	 * in its root.
	 */
	public static int totalLinks()
	{    
		return numOfLinks;
		}

	/**
	 * public static int totalCuts() 
	 *
	 * This static function returns the total number of cut operations made during the run-time of the program.
	 * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
	 */
	public static int totalCuts()
	{    
		return numOfCuts; // should be replaced by student code
	}

	public void Display(CircularLinkedList rootList) {
		System.out.println("Root list : " + rootList.toString());
		for(HeapNode node: rootList) {
			if(node.listOfChildren!= null) {displayNode(node);
				Display(node.listOfChildren);
				};
		}
	}
	
	public void displayNode(HeapNode node) {
		System.out.println(node.listOfChildren);
	}
	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap
	 * (for example HeapNode), do it in this file, not in 
	 * another file 
	 *  
	 */
	public class HeapNode{


		public int key;
		private int rank; //RANK= NUMBER OF CHILDREN
		private boolean mark;
		private HeapNode next;
		private HeapNode prev;
		public CircularLinkedList listOfChildren;
		private HeapNode parent;

		public HeapNode(int key) {
			this.key = key;
			this.mark = false;
			next=this;
			prev=this;
			parent=null;
		}

		public int getKey() {
			return this.key;
		}
		@Override
		public String toString() {
			return ""+ key;
		}
		
		public int getRank() {
			return rank;
		}
		
		public HeapNode getNext() {
			return next;
		}
		
		public HeapNode getPrev() {
			return prev;
		}
		
		public int setKey(int delta) throws InvalidDeltaException {
			if(key<delta) {
				throw new InvalidDeltaException(delta);
			}
			key = key - delta;
			return key;
		}
		
		public HeapNode getParent(){
			return parent;
		}
		
		public void setNext(HeapNode n) {
			next=n;
		}
		
		public void setPrev(HeapNode p) {
			prev=p;
		}
		
		public boolean isMarked() {
			return mark;
		}
		
		public void setMark(boolean val) {
			if(isMarked() && val==true) {
				return;
			}
			if(isMarked() && val==false) {
				numOfMarked--;
				mark = val;
			}
			if(!isMarked() && val ==true) {
				numOfMarked++;
				mark=val;
			}
			if(!isMarked() && val==false) {
				return;
			}
			

			mark = val;
		}
		
	}

	/* Class linkedList */
	class CircularLinkedList implements Iterable<HeapNode>
	{
		public HeapNode start;
		public HeapNode end ;
		public int size;

		/* return iterator*/
		public Iterator<HeapNode> iterator(){
			return new CircularLinkedListIterator(this);
		}        
		/* Constructor */
		public CircularLinkedList()
		{
			size = 0;
		}
		/*Function to insert all items of lst2 at the end of current list */
		public void addAll(CircularLinkedList lst2) {
			for (HeapNode node: lst2) {
				this.insertAtEnd(node);	
			}
		}
		/* Function to check if list is empty */
		public boolean isEmpty()
		{
			return start == null;
		}
		/* Function to get size of list */
		public int getSize()
		{
			return size;
		}
		/* Function to insert element at begining */
		public void insertAtStart(HeapNode node)
		{
			if (start == null)
			{   
				node.setNext(node);
				node.setPrev(node);
				start = node;
				end = start;           
			}
			else
			{
				node.setPrev(end);
				end.setNext(node);
				start.setPrev(node);
				node.setNext(start);
				start = node;       
			}
			size++ ;
		}
		/*Function to insert element at end */
		public void insertAtEnd(HeapNode node)
		{     
			if (start == null)
			{
				node.setNext(node);
				node.setPrev(node);
				start = node;
				end = start;
			}
			else
			{
				node.setPrev(end);
				end.setNext(node);
				start.setPrev(node);
				node.setNext(start);
				end = node;   
			}
			size++;
		}
		
		
		private void resetNode(HeapNode node) {
			node.setNext(node);
			node.setPrev(node);
		}

		/* Function to delete node at position  */

		public void delete(HeapNode node){
			
			if(size==1) {
				start = null;
				end = null;
				size = 0;
				resetNode(node);
				return;
			}
			if(start==node) {
				start = start.getNext();
				start.setPrev(end);
				end.setNext(start);
				size--;
				resetNode(node);
				return ;
			}
			
			HeapNode p = node.getPrev();
			HeapNode n = node.getNext();
			p.setNext(n);
			n.setPrev(p);
			if(node==end) {
				end=p;
			}
			resetNode(node);
			size--;
		}
		
		@Override
		public String toString() {
			String result ="[";
			for(HeapNode node:this) {
				result = result+node+",";
			}
			result = result+"]";
			return result;
		}
	}

	/*Class CircularLinkedListIterator- an Iterator for our circular-doubly -linked list, 
	 * iteration is finished when the counter reaches the size of the list
	 */
	class CircularLinkedListIterator implements Iterator<HeapNode>{
		private HeapNode curr;
		private int size;
		private int counter;

		public CircularLinkedListIterator(CircularLinkedList list){
			this.curr = list.start;
			this.size = list.size;
			this.counter =1;
		}
		public boolean hasNext() {
			return counter <=size;
		}

		public HeapNode next() {
			HeapNode result  = curr;
			this.curr = curr.next;
			counter ++;
			return result;
		}
	}
	
	class InvalidDeltaException extends Exception{
		private static final long serialVersionUID = 1L;

		public InvalidDeltaException(int delta){
			super("Illegal delta value: " + delta);
		}
	}
}
