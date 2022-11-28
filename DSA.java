/*
Name: Sally
UOW ID: 4603229
*/

import java.math.*; 
import java.util.Scanner;  
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.SecureRandom;


class DSA
{ 
    static SecureRandom random = new SecureRandom ();
    static Scanner input = new Scanner(System.in);
    static BigInteger one = new BigInteger("1");
    static BigInteger h;
    static final int pSizeInBits = 512;
    static final int qSizeInBits = 160;

    BigInteger p;              // a 512-1024 bit prime
    BigInteger q;              // 160-bit prime and a factor of p-1
    
    BigInteger g;              // generator
    BigInteger privateKey;     // private key < q
    BigInteger publicKey;      // public key
    BigInteger k;              // random k such that k < q
    BigInteger r;
    BigInteger signature;       // (r, s)
    BigInteger m;              // message
    BigInteger hashedM;        // hashed using SHA-1
    boolean verify;
    boolean checkPrime;

    BigInteger[] qAndP = new BigInteger[2];
    
    

    // Default Constructor
    public DSA() throws IOException
    {
    
    }

    public DSA(BigInteger p, BigInteger q, BigInteger g, 
                    BigInteger privateKey, BigInteger publicKey) throws IOException
    {
        this.p = p;
        this.q = q;
        this.g = g;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    // Accessor Method
    BigInteger getP ()
    {
        return p;
    }

    BigInteger getQ ()
    {
        return q;
    }

    BigInteger getG ()
    {
        return g;
    }

    BigInteger getK ()
    {
        return k;
    }

    BigInteger getPrivateKey ()
    {
        return privateKey;
    }

    BigInteger getPublicKey ()
    {
        return publicKey;
    }

    BigInteger getMsg ()
    {
        return m;
    }

    BigInteger getHM ()
    {
        return hashedM;
    }

    BigInteger getR ()
    {
        return r;
    }

    BigInteger getSignature ()
    {
        return signature;
    }

    boolean getVerify ()
    {
        return verify;
    }

    // Mutator Method
    void setP (BigInteger p)
    {
        this.p = p;
    }

    void setQ (BigInteger q)
    {
        this.q = q;
    }

    void setG (BigInteger g)
    {
        this.g = g;
    }

    void setK (BigInteger k)
    {
        this.k = k;
    }

    void setPrivateKey (BigInteger privateKey)
    {
        this.privateKey = privateKey;
    }

    void setPublicKey (BigInteger publicKey)
    {
        this.publicKey = publicKey;
    }
    
    void setMsg (BigInteger m)
    {
        this.m = m;
    }

    void setHM (BigInteger hashedM)
    {
        this.hashedM = hashedM;
    }

    void setR (BigInteger r)
    {
        this.r = r;
    }

    void setSignature (BigInteger signature)
    {
        this.signature = signature;
    }

    void setVerify (boolean verify)
    {
        this.verify = verify;
    }

    // Ref: https://www.geeksforgeeks.org/find-largest-prime-factor-number/
    // function to find largest prime factor
    long primeFactor(long n)
    {
        // Initialize the maximum prime
        // factor variable with the
        // lowest one
        long maxPrime = -1;
 
        // Print the number of 2s that divide n
        while (n % 2 == 0) 
        {
            maxPrime = 2;
 
            // equivalent to n /= 2
            n >>= 1;
        }

        // n must be odd at this point
        while (n % 3 == 0) 
        {
            maxPrime = 3;
            n = n / 3;
        }
 
        // now we have to iterate only for integers
        // who does not have prime factor 2 and 3
        for (int i = 5; i <= Math.sqrt(n); i += 6) 
        {
            while (n % i == 0) 
            {
                maxPrime = i;
                n = n / i;
            }
            while (n % (i + 2) == 0) 
            {
                maxPrime = i + 2;
                n = n / (i + 2);
            }
        }
 
        // This condition is to handle the case
        // when n is a prime number greater than 4
        if (n > 4)
            maxPrime = n;
 
        return maxPrime;
    }   // End primeFactor

    // Generate k-bit prime
    BigInteger generateKBitPrime(int k)
    {
        BigInteger bigPrime = BigInteger.probablePrime(k, random);
        checkPrime = bigPrime.isProbablePrime(1);    // check prime value

        while (checkPrime == false)
        {
            bigPrime = BigInteger.probablePrime(k, random);
        } 
        
        return bigPrime;
    }   // End generateKBitPrime

    BigInteger generateP()
    {
        // Generate p
        BigInteger p = generateKBitPrime(6);
        return p;
    }   // End generateP

    BigInteger generateQ()
    {
        BigInteger q, p, pMin1;
        p = getP();

        // Compute p-1
        pMin1 = p.subtract(one);
   
        // q divides (p-1), means q is a prime factor of (p-1)
        // Let say p = 3 bit = 7
        // so your p-1 = 7-1 = 6, so your q can be 3 or 2
        long n = pMin1.longValue();
        long qLong = primeFactor(n);    // get largest prime factor of (p-1) to be q

        q = BigInteger.valueOf(qLong);

        return q;
    }   // End generateQ

    // Ref: https://stackoverflow.com/questions/53780454/dsa-digital-signature-alghoritm-implementation-key-generation/
    BigInteger[] generatePAndQ() 
    {
        BigInteger q = BigInteger.probablePrime(qSizeInBits, random);
        BigInteger k = BigInteger.ONE.shiftLeft(pSizeInBits - qSizeInBits); // k = 2**(pSizeInBits - qSizeInBits);

        BigInteger probablyPrime = q.multiply(k).add(BigInteger.ONE); // probablyPrime = q * k + 1
        while (!probablyPrime.isProbablePrime(50)) 
        {
            q = BigInteger.probablePrime(qSizeInBits, random);
            probablyPrime = q.multiply(k).add(BigInteger.ONE);
        }

        qAndP = new BigInteger[2];
        qAndP[0] = q;
        qAndP[1] = probablyPrime;

        return qAndP;
    }

    BigInteger generateG()
    {
        BigInteger g, pMin1, pMin1DivideQ, p;

        p = getP();

        // Compute p-1
        pMin1 = p.subtract(one);

        // Compute (p-1)/q
        pMin1DivideQ = pMin1.divide(q);

        // Generate h where h < p-1 such that g > 1
        do
        {
            h = generateKBitPrime(qSizeInBits);   
            checkPrime = h.isProbablePrime(1);       
            // Compute g
            g = h.modPow(pMin1DivideQ,p);
            //System.out.println("Inside do-while h: " + h + " and correspond g: " + g);
        }while((checkPrime == false) || (h.compareTo(pMin1) == 1) || ((one.compareTo(g) == 1)) || ((one.compareTo(g)) == 0));
                
        return g;
    }   // End generateG

    BigInteger generateK()
    {
        BigInteger q;

        q = getQ();

        // Generate k such that k < q
        do
        {
            k = generateKBitPrime(qSizeInBits);
            checkPrime = k.isProbablePrime(1);
            //System.out.println("Inside do-while k: " + getK());
        }while (checkPrime == false || (k.compareTo(q) == 1) || (k.compareTo(q) == 0));

        return k;
    }   // End generateK

    BigInteger generateR()
    {
        BigInteger r, p, q, g, k;

        p = getP();
        q = getQ();
        g = getG();
        k = getK();

        // Compute r
        r = (g.modPow(k,p)).mod(q);

        return r;
    }   // End generateR

    BigInteger generatePrivateKey()
    {
        BigInteger privateKey, q;

        q = getQ();
        
        // Generate KeyGen private signing key, private key < q
        do
        {
            privateKey = generateKBitPrime(qSizeInBits);
            checkPrime = privateKey.isProbablePrime(1);
            //System.out.println("Inside do-while privateKey: " + privateKey);
        }while (checkPrime == false || (privateKey.compareTo(q) == 1) || (privateKey.compareTo(q) == 0));
        
        return privateKey;
    }   // End generatePrivateKey

    BigInteger generatePublicKey()
    {
        BigInteger publicKey, privateKey, g, p;

        g = getG();
        p = getP();
        privateKey = getPrivateKey();

        // Compute corresponding public key
        publicKey = g.modPow(privateKey,p);

        return publicKey;
    }   // End generatePublicKey

    BigInteger generateMsg(int bitLength) throws IOException
	{
        BigInteger m;
        String fileName = "";
		
		do 
		{
			m = new BigInteger(bitLength, random);
        } while (m.compareTo(q) > -1); 	// while m <= q
        
        // Storing message to textfile
        System.out.println("Random message generated: " + m);
        System.out.print("Please enter filename to store random message generated <press ENTER for default msg.txt>: ");
        String enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "msg.txt";
		}
		else
		{
			fileName = enter;
        }
		FileWriter msgWriter = new FileWriter(new File(fileName));
		msgWriter.write(m.toString());
        msgWriter.close();
        System.out.println("Message is successfully stored in " + fileName + "\n");
    
        return m;
    }	// End generateMsg
    
    // Ref: https://www.geeksforgeeks.org/sha-1-hash-in-java/
    // SHA-1
    BigInteger hashFn(BigInteger m)
    {
        //setMsg (m);
        System.out.println("+++++++++Inside hashFn+++++++++");
        System.out.println("m being hashed: " + m);
        String mString = m.toString();

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] messageDigest = md.digest(mString.getBytes());

            BigInteger no = new BigInteger (1, messageDigest);

            System.out.println("hashedM: " + no);
            System.out.println("+++++++++End hashFn+++++++++\n");

            return no;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException (e);
        }

    }   // End hashFn

    BigInteger signMsg (BigInteger m, BigInteger privateKey) throws IOException
    {
        BigInteger q, k, r;
        BigInteger hashedM, signature, k_inv, data;
        String fileName = "";

        q = getQ();
        k = getK();
        r = getR();

        hashedM = hashFn(m);    // Calculate H(m)
        setHM (hashedM);

        // Compute k inverse mod q
        k_inv = k.modInverse(q);
        
        // Compute (H(m) + (privateKey * r)) mod q
        data = (hashedM.add(privateKey.multiply(r))).mod(q);
        signature = (k_inv.multiply(data)).mod(q);
        
        // Storing signature to textfile
        System.out.print("Please enter filename to store signature <press ENTER for default sig.txt>: ");
        String enter = input.nextLine();
		if (enter.isEmpty() || enter == "\n")
		{
			fileName = "sig.txt";
		}
		else
		{
			fileName = enter;
        }

		FileWriter sigWriter = new FileWriter(new File(fileName));
		sigWriter.write(signature.toString());
		sigWriter.close();
        System.out.println("Signature saved to " + fileName);
        System.out.println("r: " + r);
        System.out.println("s: " + signature);

        System.out.println("+++++++++End signMsg+++++++++\n");

        return signature;
    }   // End signMsg

    boolean verifySig (BigInteger m, BigInteger r, BigInteger signature) throws IOException
    {
        boolean verify = true;
        BigInteger p, q, g, hashedM;
        BigInteger w, v, t1, t2, g1, y1;     
        
        p = getP();
        q = getQ();
        g = getG();
        hashedM = getHM(); // this hashedM was calculated when signing signature

        w = signature.modInverse(q);    // verify the value of signature
        t1 = (hashedM.multiply(w)).mod(q);
        t2 = (r.multiply(w)).mod(q);
        g1 = g.modPow(t1,p);
        y1 = publicKey.modPow(t2, p);
        
        v = ((g1.multiply(y1)).mod(p)).mod(q);

        System.out.println("w: " + w);
        System.out.println("t1: " + t1);
        System.out.println("t2: " + t2);
        System.out.println("v: " + v);
        System.out.println("r: " + r);
        
        if (v.compareTo(r) == 0)
        {
            verify = true;
        }
        else
        {
            verify = false;
        }

        System.out.println("Verification Result: " + verify);
        return verify;
    }   // End verifySig

    void printKeys()
    {
        // Printing result
        System.out.println("++++++Key Generated++++++");
        System.out.println("p: " + getP());
        System.out.println("q: " + getQ()); 
        System.out.println("g: " + getG());
        System.out.println("u: " + getPrivateKey());
        System.out.println("y: " + getPublicKey() + "\n");
    }   // End printKeys

    void option() throws IOException
	{
		int choice;
		
		System.out.println("\n\nDSA Algorithm");
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
			if (choice < 1 || choice > 4)
			{
				System.out.print("Please enter your choice: ");
			}

			switch (choice)
			{
                case 1: 
                        generatePAndQ();
                        p = qAndP[1];
                        setP(p);
                        q = qAndP[0];
                        setQ(q);
                        g = generateG();
                        setG(g);
                        privateKey = generatePrivateKey();
                        setPrivateKey(privateKey);
                        publicKey = generatePublicKey();
                        setPublicKey(publicKey);
                        
                        printKeys();
                        System.out.println("h: " + h);
                        option();
						break;
				
                case 2: 
                        System.out.println("++++++Generating message & store to file++++++");   
                        generateMsg(32); 
                        setK(generateK()); 
                        setR(generateR()); 
                        
                        System.out.println("++++++Message to be signed++++++");  
                        // Reading message
                        System.out.print("Please enter message input filename <press ENTER for default msg.txt>: ");
                        String enter = input.nextLine();
                        String fileName = "";
                        if (enter.isEmpty() || enter == null)
                        {
                            fileName = "msg.txt";
                        }
                        else
                        {
                            fileName = enter;
                        }
                        String mssgFile = new String(Files.readAllBytes(Paths.get(fileName)));
                        m = new BigInteger(mssgFile);
                        System.out.println("Finished reading message from " + fileName);
       
                        signMsg(m, privateKey); // Signing message
                        System.out.println("++++++Message is signed and stored++++++");   
                        option();
						break;
				
                case 3:
                        System.out.println("++++++Signature to be verified++++++");
                        // Reading message
                        System.out.print("Please enter message filename <press ENTER for default msg.txt>: ");
                        enter = input.nextLine();
                        if (enter.isEmpty() || enter == null)
                        {
                            fileName = "msg.txt";
                        }
                        else
                        {
                            fileName = enter;
                        }
                        mssgFile = new String(Files.readAllBytes(Paths.get(fileName)));
                        m = new BigInteger(mssgFile);
                        System.out.println("Finished reading message from " + fileName);
                        System.out.println ("m: " + m + "\n");

                        // Reading signature
                        System.out.print("Please enter signature filename <press ENTER for default sig.txt>: ");
                        enter = input.nextLine();
                        if (enter.isEmpty() || enter == null)
                        {
                            fileName = "sig.txt";
                        }
                        else
                        {
                            fileName = enter;
                        }
                        String sigFile = new String(Files.readAllBytes(Paths.get(fileName)));
                        signature = new BigInteger(sigFile);
                        System.out.println("Finished reading signature from " + fileName);
                        System.out.println ("signature: " + signature + "\n");
                        
                        verifySig(m, r, signature);
                        System.out.println("++++++Signature is verified++++++");
                        option();
						break;

				case 4: System.out.println("\nExiting program...");
						System.exit(0);
						break;
			}
		} while (choice < 1 || choice > 4);
    }	// End option
    
    public static void main(String args[]) throws IOException
    {   
        DSA obj = new DSA();
        obj.option();
   
    }   // End main

}   // End class DSA1