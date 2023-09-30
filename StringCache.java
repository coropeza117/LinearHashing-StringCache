package edu.uwm.cs351;

import java.util.function.Consumer;

import edu.uwm.cs.util.Primes;

/**
 * @author * CHRISTIAN OROPEZA CS-351 ...RECIEVED HELP FROM BOOK, CS LIBRARY TUTORS, ONLINE CS TUTOR, AND ADVICE FROM FRIENDS ON HOW TO APPROACH FIXING PERSISTENT BUGS.
 * COLLABORATORS: JOSHUA KNIGHT, JULLIAN MURENO, BIJAY PANTA, JIAHUI YANG , MIGUEL GARCIA, MARAWAN SALAMA, ESTELLE BRADY (WHILE IN TUTORING LIBRARY SECTION) BUT NO CODE WAS SHARED.
 * Online Sources: prime length -> https://www.geeksforgeeks.org/java-program-to-check-if-a-number-is-prime-or-not/

/**
 * A class to manage string instances.
 * All equal string instances that are interned will be identical.
 */
public class StringCache extends Primes
{
	// even with a Spy, we still use "private":
	private String[] table;
	private int numEntries;

	 // TODO: hash helper function used by wellFormed and intern
	//	used a while loop like in the text book and got the Math.abs code from Lab 11
	private int hash(String key)
	{
		int hasher = (Math.abs(key.hashCode( ) % table.length));

		while(table[hasher] != null && table[hasher].equals(key) == false)
		{
			hasher = nextIndex(hasher);
		}

		return hasher;
	}

	
	 //	got from lab 11 but modified for reverse linear probing
	 private int nextIndex(int i)
	 {
		 if(i+1 == 1)	return table.length - 1;

		 else	return i-1;
	 }

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	private boolean wellFormed() 
	{
		// 1. table is non-null and prime length
		if(table == null || isPrime(table.length) == false)	return report("bad");
		
		// 2. number of entries is never more half the table size
		if(numEntries > (table.length / 2))	return report("man");
		
		// 3. number of non-null entries in the table is numEntries
		// 4. every string in the array is hashed into the correct place using backward linear probing
		int count = 0;
		
		for(int i = 0; i < table.length; ++i)
		{	
			if(table[i] != null)
			{
				if(i != hash(table[i]))	return report("hash");
				
				++count;
			}
		}
		
		if(count != numEntries)	return report("oof");

		return true;
	}
	
	private StringCache(boolean ignored) {} // do not change
	
	/**
	 * Create an empty string cache.
	 */
	public StringCache() 
	{
		numEntries = 0;
		table = new String[2];
		assert wellFormed() : "invariant broken in constructor"; 
	}
	
	/**
	 * Return a string equal to the argument.  
	 * For equal strings, the same (identical) result is always returned.
	 * As a special case, if null is passed, it is returned.
	 * store values hash into an integer then we check if the table array 
	 * using hash value is null. if so set it to the value being passed &
	 * increment the number of entries.
	 * if number of entries is greater than half of the table array then we rehash
	 * finally we return the value being passed.
	 * @param value string, may be null
	 * @return a string equal to the argument (or null if the argument is null).
	 * @piazza-713	rehash logic line 137-146
	 */
	public String intern(String value) 
	{
		assert wellFormed() : "invariant broken before intern";

		if(value == null)	return value;
		
		int hashVal = hash(value);
		
		if(table[hashVal] == null)
		{
			table[hashVal] = value;
			
			++numEntries;
			
			if(numEntries > table.length/2)	
			{
				String[] oldTable = table;
				
				int rehash = nextPrime(4 * numEntries);
				
				table = new String[rehash];
				
				for(int i = 0; i < oldTable.length; ++i)
				{
					if(oldTable[i] != null)	table[hash(oldTable[i])] = oldTable[i]; 
				}
			}
		}
		
		else value = table[hashVal];

		assert wellFormed() : "invariant broken after intern";
		
		return value;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class Spy 
	{ // do not modify (or use!) this class
		/**
		 * Create a String Cache with the given data structure,
		 * that has not been checked.
		 * @return new debugging version of a StringCache
		 */
		public StringCache create(String[] t, int c) 
		{
			StringCache sc = new StringCache(false);
			sc.table = t;
			sc.numEntries = c;
			return sc;
		}
		
		/**
		 * Return the number of entries in the string cache
		 * @param sc string cache, must not be null
		 * @return number of entries in the cache.
		 */
		public int getSize(StringCache sc) 
		{
			return sc.numEntries;
		}
		
		/**
		 * Return capacity of the table in the cache
		 * @param sc cache to examine, must not be null
		 * @return capacity
		 */
		public int getCapacity(StringCache sc) 
		{
			return sc.table.length;
		}
		
		public boolean wellFormed(StringCache sc) 
		{
			return sc.wellFormed();
		}
		
		public Consumer<String> getReporter() 
		{
			return reporter;
		}
		
		public void setReporter(Consumer<String> c) 
		{
			reporter = c;
		}

	}
}
