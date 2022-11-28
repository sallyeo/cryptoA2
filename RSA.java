/*
Name	: Sally
UOW ID	: 4603229
*/

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.xml.namespace.QName;

import java.io.IOException;


public class RSA
{
    static SecureRandom random = new SecureRandom ();
    static Scanner input = new Scanner(System.in);
    static BigInteger one = new BigInteger("1");
    static String fileName = "";
	static String enter = "";
    
    // Default constructor
    public RSA ()
    {
        
    }

    void option() throws IOException
	{
		int choice;
        int bitLength = 0;
		
		System.out.println("\n\nRSA Algorithm");
		System.out.println("-------------------------------------------------");
		System.out.println("1) Key Generator");
		System.out.println("2) Sign");
		System.out.println("3) Verify");
		System.out.println("4) Exit");
		System.out.print("Please enter your choice: ");
		
		// Loop to keep prompting user to enter the correct input
		do
		{			
			choice = input.nextInt();
            input.nextLine (); // Clear buffer
			
			// Check for valid user input ranges from 1-4 only
			if (choice < 1 || choice > 6)
			{
				System.out.print("Please enter your choice: ");
			}
			else if (choice == 1 && choice < 6)
			{
                System.out.println("===================================");
                System.out.println("+++++++++RSA KEY GENERATOR+++++++++");
                System.out.println("===================================");
				System.out.print("Please enter prime bit length: ");
				bitLength = input.nextInt();				
			}

			switch (choice)
			{
                case 1: generateKeys(bitLength);
						break;
				
				case 2: sign();
						break;
                
				case 3: verify();
						break;
                
				case 4: System.out.println("\nExiting program...");
						System.exit(0);
						break;
			}
		} while (choice < 1 || choice > 4);
    }	// End option

    BigInteger[] generateKeys(int bitLength) throws IOException
    {
        BigInteger p; 
        BigInteger q; 
        BigInteger e;       // Public Key  
        BigInteger d;	    // Private Key  
        BigInteger n;       // Modulus
        BigInteger phi;
        BigInteger[] keys = new BigInteger[5];

		do
		{
			p = BigInteger.probablePrime(bitLength, random);
			q = BigInteger.probablePrime(bitLength, random);
		}while(p.compareTo(q) == 0 );

		n = p.multiply(q);
		phi = (p.subtract(one)).multiply(q.subtract(one));	// phi(n) = (p - 1)(q - 1)
		
		BigInteger temp = BigInteger.probablePrime(bitLength, random);
        long tempLong = temp.longValue();
        e = BigInteger.valueOf(tempLong);		// Generate e (Public Key) such that e is coprime of phi

		while (phi.gcd(e).intValue() != 1)
		{
            e = e.add(BigInteger.valueOf(2)); // Check next odd number to see if e is coprime of phi
		}
		
		d = e.modInverse(phi);	// Compute d (Private Key)
        
        keys[0] = p;
        keys[1] = q;
        keys[2] = n;    // Modulus
        keys[3] = e;    // Public Key
		keys[4] = d;    // Private Key
		

		// Store public keys
		fileName = "pk.txt";
		FileWriter pubKeyWriter = new FileWriter(new File(fileName));
		pubKeyWriter.write(n + "," + e);
		pubKeyWriter.close();
		System.out.println("Public key saved to " + fileName);
		
		// Store secret keys
		fileName = "sk.txt";
		FileWriter privKeyWriter = new FileWriter(new File(fileName));
		privKeyWriter.write(n + "," + p + "," + q + "," + d);
		privKeyWriter.close();	
		System.out.println("Secret key saved to " + fileName + "\n\n");
	
		
		System.out.println("RSA Key Generation Info Tracing");
		System.out.println("-------------------------------------------------");
		System.out.printf("%-15s: %d%n", "p", p);
		System.out.printf("%-15s: %d%n", "q", q);
		System.out.printf("%-15s: %d%n", "Modulus N", n);
		System.out.printf("%-15s: %d%n", "Public Key e", e);
		System.out.printf("%-15s: %d%n", "Private Key d", d);
        
        option();
        return keys;
    }   // End generateKeys

    BigInteger sign() throws IOException
	{
        BigInteger p; 
        BigInteger q;        
        BigInteger d;	    // Private Key  
        BigInteger n;       // Modulus
        BigInteger m;       // Message
        BigInteger s;		// Signature

        System.out.println("===================================");
        System.out.println("+++++++RSA SIGNING ALGORITHM+++++++");
        System.out.println("===================================");

		// Read from sk.txt
		System.out.print("Please enter filename to read secret keys <press ENTER for default sk.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "sk.txt";
		}
		else
		{
			fileName = enter;
		}
		String skFile = new String(Files.readAllBytes(Paths.get(fileName)));
		String msgArr [] = skFile.split(",");
		n = new BigInteger(msgArr[0]);
		p = new BigInteger(msgArr[1]);
		q = new BigInteger(msgArr[2]);
		d = new BigInteger(msgArr[3]);
        System.out.println("Reading secret key from " + fileName);

		// Read from msg.txt
		System.out.print("Please enter filename to read message <press ENTER for default msg.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "msg.txt";
		}
		else
		{
			fileName = enter;
		}
		String message = new String(Files.readAllBytes(Paths.get(fileName)));
		m = new BigInteger(message);
        System.out.println("Reading message from " + fileName);
		
		System.out.print("Please enter filename to store signature <press ENTER for default sig.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "sig.txt";
		}
		else
		{
			fileName = enter;
		}
		s = m.modPow(d,n); // Signing (S = m^d (mod n))
		FileWriter sigWriter = new FileWriter(new File(fileName));
		sigWriter.write(s.toString());
		sigWriter.close();
		System.out.println("Signature saved to " + fileName);

		System.out.println("\n+++Message have been signed+++\n");
		System.out.println("RSA Signing Info Tracing");
		System.out.println("-------------------------------------------------");
		System.out.printf("%-20s: {p=%d, q=%d, d=%d, N=%d}%n", "sk", p, q, d, n);
		System.out.printf("%-20s: %d%n", "Original message m", m);
		System.out.printf("%-20s: %d%n", "Signature s", s);
		System.out.printf("%-20s: %d%n", "Signing Key d", d);
		System.out.printf("%-20s: %d%n", "Modulus N", n);

        option();
        return s;
    }	// End sign
    
    void verify() throws IOException
	{ 
        BigInteger e;       // Public Key  
        BigInteger n;       // Modulus
        BigInteger m;       // Message
        BigInteger s;		// Signature

        System.out.println("=====================================");
        System.out.println("+++++++RSA VERIFYING ALGORITHM+++++++");
        System.out.println("=====================================");

		// Reading public key
		System.out.print("Please enter filename to read public keys <press ENTER for default pk.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "pk.txt";
		}
		else
		{
			fileName = enter;
		}
		String pkFile = new String(Files.readAllBytes(Paths.get(fileName)));
		String msgArr [] = pkFile.split(",");
		n = new BigInteger(msgArr[0]);
		e = new BigInteger(msgArr[1]);
        System.out.println("Reading public key from " + fileName);
		
		// Reading signature
		System.out.print("Please enter filename to read signature <press ENTER for default sig.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "sig.txt";
		}
		else
		{
			fileName = enter;
		}
		String sigFile = new String(Files.readAllBytes(Paths.get(fileName)));
		s = new BigInteger(sigFile);
        System.out.println("Reading signature from " + fileName);
		
		// Reading message
		System.out.print("Please enter filename to read message <press ENTER for default msg.txt>: ");
		enter = input.nextLine();
		if (enter.isEmpty()|| enter == null)
		{
			fileName = "msg.txt";
		}
		else
		{
			fileName = enter;
		}
		String mssgFile = new String(Files.readAllBytes(Paths.get(fileName)));
		m = new BigInteger(mssgFile);
        System.out.println("Reading message from " + fileName);
		
		// Calculate the m from the input read
        BigInteger mPrime = s.modPow(e,n);
        
        System.out.println("\n================================================");
		System.out.print("The verification of the signature returns: ");
		
		if (mPrime.compareTo(m) == 0)
		{
			System.out.println("True");
		}
		else
		{
			System.out.println("False");
        }
        System.out.println("================================================");

		System.out.println("\nRSA Verification Info Tracing");
		System.out.println("-------------------------------------------------");
		System.out.printf("%-20s: {e=%d, N=%d}%n", "pk", e, n);
		System.out.printf("%-20s: %d%n", "Original message m", mPrime);
		System.out.printf("%-20s: %d%n", "Verified message m", mPrime);
		System.out.printf("%-20s: %d%n", "Signature s", s);
		System.out.printf("%-20s: %d%n", "Verification Key e", e);
		System.out.printf("%-20s: %d%n", "Modulus N", n);

		option();
    } // End verify

    public static void main (String[] args) throws IOException
    {
        RSA Obj = new RSA ();
		Obj.option();
		
	}	// End main function
	
}	// End RSA class