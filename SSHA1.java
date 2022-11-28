/*
Name: Sally
UOW ID: 4603229
*/

import java.util.*;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
public class SSHA1
{
	// Ref: https://www.geeksforgeeks.org/sha-1-hash-in-java/
	public static String getHash (String input)
	{
		StringBuilder sb = new StringBuilder ();
		
		try
		{
			MessageDigest msgDigest = MessageDigest.getInstance("SHA1");
			byte result [] = msgDigest.digest(input.getBytes());
			
			// Ref: https://stackoverflow.com/questions/36491665/byte-to-integer-and-then-to-string-conversion-in-java
			// Convert byte to integer then to String
			for (int i = 0; i < result.length; ++ i)
			{
				sb.append(Integer.toString((result [i] & 0xFF) + 0x100, 16).substring (1));
			}		
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return sb.toString();
	}	// End getHash

	public static void main (String [] args)
	{
		//Scanner input = new Scanner(System.in);
		SecureRandom random = new SecureRandom ();
		
		int x;
		String msg;
		String hashedMsg = "";
		boolean match = false;
		
		// Ref: https://www.geeksforgeeks.org/hashtable-get-method-in-java/
		// Declare an empty Hashtable, String is hash key, Integer is the x 
		Hashtable <String, Integer> numbers = new Hashtable <String, Integer>();
		
		do
		{
			// Generate secure random amount of dollar
			x = random.nextInt();

			if (x > 0)
			{
				msg = "The Cat-In-The-Hat owes Sally " + x + " dollars.";

				// Generate the hashed message
				hashedMsg = getHash(msg).substring(0,9);	// 36-bit
				
				// Check if hash key of message exists in Hashtable, it return true
				match = numbers.containsKey(hashedMsg);
				
				// If hash key is not in the Hastable yet, 
				if (match == false)
				{
					// Insert the hash key and x into Hashtable
					numbers.put(hashedMsg, x);
				}

				// if x' = x, continue the while loop
				if (numbers.get(hashedMsg) == x)
				{
					match = false;
				}

				if (match == true)
				{
					System.out.println(msg);
					System.out.println("The Cat-In-The-Hat owes Sally " + numbers.get(hashedMsg) + " dollars.");
					System.out.println("Their 36-bit hash value is: " + hashedMsg);
				}
			}
		} while (match == false);

	}	// End main function

}	// End SSHA1

