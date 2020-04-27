/* AUTHOR: Isabela Hutchings
 * Assignment: Program #4: Mash
 * 
 * Course: Csc 345 Spring 2019
 * Instructor: L. McCann
 * Due Date: April 17 1:00
 * 
 * PURPOSE: This program is the main implementation of the Message Digest Mash algorithm
 * the program reads input from the command line and create a hashing of a message digest from 2 prime numbers and one string.
 *  it then implements steps 1-9 by calling function methods that implement each step
 *  it then prints out the message digest as both decimal and binary
 * LANGUEG: Java
 */
import java.math.BigInteger;

public class Prog4 {
public static void main(String [] args) {
	
	if(args.length !=3) {
		if(args.length<3)System.out.println("ERROR: not enough inputs provided");
		else System.out.println("ERROR: too many inputs provided");
		System.exit(1);
	}
	int p=Integer.parseInt(args[0]);
	int q=Integer.parseInt(args[1]);
	BigInteger P= BigInteger.valueOf(p);
	BigInteger Q= BigInteger.valueOf(q);
	if((P.isProbablePrime(10)==false) || (Q.isProbablePrime(10)==false)) {
		System.out.println("ERROR: Detected a non-Prime number");
		System.exit(1);
	}
	BigInteger X= stringToBinary(args[2]);
	int b= 7*args[2].length();
	
	int m=numOfBits(p,q);
	int n=largestMultipleOf16(m);
	BigInteger H0=BigInteger.valueOf(0);
	BigInteger A=setIntA(n);
	
	BigInteger[] array =appendBits(X,b,n);
	X= array[0];
	int newB= array[1].intValue();

	BigInteger [] blocks=makeBlocks(X,b,n, newB);
	BigInteger[] y=createY1(blocks,n);
	BigInteger h= getmessageDigest(y,H0,A,P.multiply(Q),n);
	String s=h.toString(2);
	if(s.length()<n) {
		for(int i=0;i<n-s.length();i++) {
			s="0"+s;
		}
	}
	System.out.println("Binary: "+s);
	System.out.println("Decimal: "+h.toString());
	
}
/*---------------------------------------------------------------------
|  stringToBinary
|
|  Purpose:  This implements takes a string and turns it into a bigInteger 
|            in order to have easy a binary format
|
|  Pre-condition:  the string must not be empty
|
|  Post-condition: the bigInteger xBit should be the binary format of the string
|
|  Parameters:
|      String x -- the message given from the command line
|
|  Returns: a BigInteger version of String x
*-------------------------------------------------------------------*/
private static BigInteger stringToBinary(String x) {
	BigInteger xBit=BigInteger.valueOf(0);
	for(int i=0;i<x.length();i++) {
		xBit= xBit.shiftLeft(7);
		BigInteger temp= BigInteger.valueOf(x.charAt(i));
		xBit=xBit.or(temp);
	}
	return xBit;
}
/*---------------------------------------------------------------------
|  numOfBits
|
|  Purpose:  This gives us m where M= p*q of m bit.
|
|  Pre-condition:  the string must not be empty
|
|  Post-condition: the bigInteger xBit should be the binary format of the string
|
|  Parameters:
|      int prime_one -- one of the parameters from 
|
|  Returns: the bit Length of M
*-------------------------------------------------------------------*/
private static int numOfBits(int prime_one,int prime_two) {
	BigInteger P=BigInteger.valueOf(prime_one);
	BigInteger Q=BigInteger.valueOf(prime_two);
	BigInteger M= P.multiply(Q);
	return M.bitLength();
}
/*---------------------------------------------------------------------
|  largestMultipleOf16
|
|  Purpose:  This function calculates the closest muiple of 16 that is <= to m
|
|  Pre-condition:  m must've been calculated/ assigned a value
|
|  Post-condition: the bigInteger xBit should be the binary format of the string
|
|  Parameters:
|      int m -- this is the number of bytes in M=p*q
|
|  Returns: an int of the closest 16 multiple
*-------------------------------------------------------------------*/
private static int largestMultipleOf16(int m) {
	if(m<16) return 16;
	else {
		int multiple=m/16;
		return 16*multiple;
	}
}
/*---------------------------------------------------------------------
|  setIntA
|
|  Purpose:  This function calculates the closest multiple of 16 that is <= to m
|
|  Pre-condition:  m must've been calculated/ assigned a value
|
|  Post-condition: the bigInteger xBit should be the binary format of the string
|
|  Parameters:
|      int m -- this is the number of bytes in M=p*q
|
|  Returns: an int of the closest 16 multiple
*-------------------------------------------------------------------*/
private static BigInteger setIntA(int n) {
	int shift =n-4;
	BigInteger A=BigInteger.valueOf(15);
	A=A.shiftLeft(shift);
	return A;
}
/*---------------------------------------------------------------------
|  largestMultipleOf16
|
|  Purpose:  This function applies step 3: appends a certain number of bits onto BigInteger X
|
|  Pre-condition: X,b,n must be assigned values. X is the binary of the message
|                 n is the largest multiple of 16 <=m. b is the binary of the bit size of X
|
|  Post-condition: X byte length = 
|
|  Parameters:
|      int m -- this is the number of bytes in M=p*q
|
|  Returns: an int of the closest 16 multiple
*-------------------------------------------------------------------*/
private static BigInteger[] appendBits(BigInteger X, int b, int n) {
	BigInteger [] ar= new BigInteger [2];
	int div=n/2;
	int shiftAmt;
	if(div > b) shiftAmt=div-b;
	else {
		shiftAmt=div-(b%div);
	}
	X=X.shiftLeft(shiftAmt);
	
	ar[0]=X;
	ar[1]= BigInteger.valueOf(b+shiftAmt);
	return ar;
}
/*---------------------------------------------------------------------
|  largestMultipleOf16
|
|  Purpose:  This function implements step 5
|
|  Pre-condition: X,b,n must be assigned values. X is the binary of the message
|                 n is the largest multiple of 16 <=m. b is the binary of the bit size of X
|				  steps 1-4 must've been successfully implemented.
|
|  Post-condition: 
|
|  Parameters:
|      X-- the binary representation of the message
|      b-- the originally length of X bits in binary
|      n -- this is the largest multiple of 16 that is <= to m
|      newB-- is the updated length of X bit that where append
|
|  Returns: BigInteger [] of blocks of X
*-------------------------------------------------------------------*/
private static BigInteger [] makeBlocks(BigInteger X,int b,int n, int newB) {
	Integer shift=n/2;
	Integer len=newB/shift;
	BigInteger bitMask=(BigInteger.valueOf(2)).pow(shift);
	bitMask= BigInteger.valueOf(bitMask.intValue()-1);
	Integer shiftStart=newB-shift;
	BigInteger  [] x = new BigInteger  [len+1];
	for(int i=0;i<len;i++) {
		BigInteger temp= X.shiftRight(shiftStart-(i*shift));
		temp= temp.and(bitMask);
		x[i]=temp;
	}
	//this adds the bit that represents the binary of b
	x[len]=BigInteger.valueOf(b);
	return x;
}
/*---------------------------------------------------------------------
|  createY1
|
|  Purpose:  This function implements step 7 and 8. It creates n-bit block of code from 
|             the block[]
|
|  Pre-condition: step 1-6 must've been implemented before hand
|
|  Post-condition: we can now call step 9, 
|
|  Parameters:
|      arr -- this is the array of n/2 blocks of x and n/2 of b
|      n -- this is the largest multiple of 16 that is <= to m
|
|  Returns: returns an BigInteger[] of y
*-------------------------------------------------------------------*/
private static BigInteger[] createY1(BigInteger arr[],int n) {
	Integer shift=n/2;
	BigInteger temp;
	Integer len =shift/4;
	BigInteger [] yi= new BigInteger [arr.length];
	for(int i=0;i<arr.length-1;i++) {
		BigInteger sum=BigInteger.valueOf(0);
		for(int j=len;j>0;j--) {
			sum=sum.or(BigInteger.valueOf(15));
			sum=sum.shiftLeft(4);
			BigInteger mask= (arr[i].shiftRight((j-1)*4)).and(BigInteger.valueOf(15)); 
			sum=sum.or(mask);
			if(j>1)sum=sum.shiftLeft(4);
		}
		yi[i]=sum;
	}
	yi[arr.length-1]=setY2(arr[arr.length-1],n);
	return yi;
}
/*---------------------------------------------------------------------
|  createY2
|
|  Purpose:  This function is simliar to Y1, it does step 8
|
|  Pre-condition: b is a non-empty array. Step 1-7 have been properly executed
|
|  Post-condition: the array
|
|  Parameters:
|      b -- this is the length of x message in bits
|      n -- this is the closest multiple of 16 <=m that we are using as our n-bits length
|
|  Returns: a new BigInteger that is a partition of b with 1010 bit inserted before each nybble 
|           to make a n-block of yt+1
*-------------------------------------------------------------------*/
private static BigInteger setY2(BigInteger b,int n) {
	BigInteger sum=BigInteger.valueOf(0);
	Integer shift=(n/2)/4;
	for(int i=shift;i>0;i--) {
		sum=sum.or(BigInteger.valueOf(0xa));
		sum=sum.shiftLeft(4);
		BigInteger mask=(b.shiftRight((i-1)*4)).and(BigInteger.valueOf(15));
		sum=sum.or(mask);
		if(i>1)sum=sum.shiftLeft(4);
	}
	return sum;
}
/*---------------------------------------------------------------------
|  printArray
|
|  Purpose:  This function prints out arrays of BigIntegers for debugging purposes
|
|  Pre-condition: ar is a non-empty array of BigIntegers
|
|  Post-condition: n/a
|
|  Parameters:
|      ar -- this is the array of BigIntegers we print out of
|
|  Returns: void
*-------------------------------------------------------------------*/
private static void printArray(BigInteger [] ar) {
	
	for(int i=0;i<ar.length;i++) {
		System.out.print(ar[i].toString(2)+ " ");
	}
	System.out.println();
}
/*---------------------------------------------------------------------
|  createY1
|
|  Purpose:  This function does step 9 of the message digest
|
|  Pre-condition: all the parameters are non-empty, steps 1-8 have been run
|
|  Post-condition: we are done with the message digest so our return value is our message Digest
|
|  Parameters:
|      y  -- this is the array of n-bit blocks of yt+1 from step 8
|      h0 -- this is the first input from h0  where h0=0
|      a  -- this is the n-bits of A from step 3
|      m -- this is the binary representation of M = p*q
|      n --- this is the largest multiple of 16 that is <= to m
|
|  Returns: BigInteger that is the binary representation of Ht+1
*-------------------------------------------------------------------*/
private static BigInteger getmessageDigest (BigInteger [] y, BigInteger h0,BigInteger a,BigInteger m, int n) {
	BigInteger [] bigInts= new BigInteger [y.length+1];
	bigInts[0]=h0;
	BigInteger nbitMask=(BigInteger.valueOf(2)).pow(n);
	nbitMask=nbitMask.subtract(BigInteger.valueOf(1));

	for(int i=0;i<y.length;i++) {
		BigInteger f= (((bigInts[i].xor(y[i])).or(a)).pow(257)).mod(m);
		BigInteger g=f.and(nbitMask);
		bigInts[i+1]=g.xor(bigInts[i]);
	}
	//printArray(bigInts);
	return bigInts[y.length];
}

}
