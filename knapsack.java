/*
Name: Sally
UOW ID: 4603229
*/

import java.util.*;

public class knapsack
{
    static Scanner input = new Scanner (System.in);
    ArrayList <Integer> knapsack = new ArrayList <Integer> ();
    ArrayList <Integer> publicKeys = new ArrayList <Integer> ();
    ArrayList <Character> decryptedMessage = new ArrayList <Character> ();
    int size;    // size of super increasing knapsack
    int totalSumOfKnapsack;   // total sum of al the a
    int p;       // modulus
    int w;       // Multiplier
    int invW;

    int[] encryptedMsgIntArr;
    
    //char [] decryptedBinaryInCharArr;
    //int [] decryptedBinaryInIntArr;

    // Class constructor
    public knapsack ()
    {

    }

    // Acessor method
    int getSize ()
    {
        return size;
    }

    int getTotalSumOfKnapsack ()
    {
        return totalSumOfKnapsack;
    }

    int getP ()
    {
        return p;
    }

    int getW ()
    {
        return w;
    }

    int getInvW ()
    {
        return invW;
    }

    ArrayList <Integer> getKnapsackArrList ()
    {
        return knapsack;
    }

    ArrayList <Integer> getPublicKeys ()
    {
        return publicKeys;
    }

    int[] getEncryptedMsgIntArr ()
    {
        return encryptedMsgIntArr;
    }

    ArrayList <Character> getDecryptedMessage ()
    {
        return decryptedMessage;
    }

    // Mutator method
    void setSize (int size)
    {
        this.size = size;
    }

    void setTotalSumOfKnapsack (int totalSumOfKnapsack)
    {
        this.totalSumOfKnapsack = totalSumOfKnapsack;
    }

    void setP (int p)
    {
        this.p = p;
    }

    void setW (int w)
    {
        this.w = w;
    }

    void setInvW (int invW)
    {
        this.invW = invW;
    }

    void setKnapsackArrList (ArrayList <Integer> knapsack)
    {
        this.knapsack = knapsack;
    }

    void setPublicKeys (ArrayList <Integer> publicKeys)
    {
        this.publicKeys = publicKeys;
    }

    void setEncryptedMsgIntArray(int[] encryptedMsgIntArr)
    {
        this.encryptedMsgIntArr = encryptedMsgIntArr;
    }

    void setDecryptedMessage(ArrayList <Character> decryptedMessage)
    {
        this.decryptedMessage = decryptedMessage;
    }

    boolean gcd (int num1, int num2) 
	{
		int gcd = 0;
		
		for(int i = 1; i <= num1 && i <= num2; i++)
		{
            if(num1 % i==0 && num2 % i==0)
            {
                gcd = i;
            }	    
		}
		
		if (gcd == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}   // End gcd
	
	boolean isPrime (int num1)
	{
		int num = num1;
        boolean checkPrime = true;

        for(int i = 2; i <= num/2; i++)
        {
            // if even
            if(num % i == 0)
            {
                checkPrime = false;
			}
		}
		
		return checkPrime;
    }   // End isPrime

    int calculateInverse(int w)
	{
        int p;  // Modulus
        
        p = getP();

		//Code reference: https://www.geeksforgeeks.org/multiplicative-inverse-under-modulo-m/
		w = w % p; 
		for (int x = 1; x < p; x++) 
		   if ((w * x) % p == 1) 
			  return x; 
		return 1; 
	}   // End calculateInverse

    int setKnapsackSize()
	{
        int size;   // Size of knapsack

        // ASCII require minimum 7-bits to be represented
        do
		{
            System.out.print("Enter size of super-increasing knapsack (7 or more): "); 
            size = input.nextInt();
        }while (size < 7);
        
        return size;
    }   // End setKnapsackSize()

    // Initialize values of a
    ArrayList <Integer> setA()
	{
        ArrayList <Integer> knapsack = new ArrayList <Integer> ();
        int size;   // Size of knapsack
        int value;  // value of a in the knapsack
        
        size = getSize();
        
        // when a1, knapsack size = 0
        // when a2, size = 1
        // when a3, size = 2
        // when a4, size = 3
		for (int i = 1; i <= size; i++)
		{
			if (knapsack.size() < size)
			{
				int sum = 0;
                
                // Sum up all the knapsack in each loop
				for (int item : knapsack)
				{
					sum = sum + item;
                }

                do
                {
                    System.out.print("Enter value of a" + i + " (must be more than " + sum + "): ");
                    value = input.nextInt();
                }while((value < sum) || (value == sum));

                // if correct a is entered, add value to knapsack
                if (value > sum)
                {	
                    knapsack.add(value);    // knapsack.size() increases here
                }
			}
        }
        
        return knapsack;

    }   // End setA

    int computeTotalSumOfKnapsack(ArrayList <Integer> knapsack)
    {
        int totalSumOfKnapsack = 0;

        for (int item : knapsack)
        {
            totalSumOfKnapsack = totalSumOfKnapsack + item;
            setTotalSumOfKnapsack (totalSumOfKnapsack);
        }
        return totalSumOfKnapsack;
    }   // End computeTotalSumOfKnapsack
    
    void printKnapsackValues(ArrayList <Integer> knapsack)
    {
        System.out.println("Your super-increasing knapsack values are: ");

        for (int item : knapsack)
        {
            System.out.print(item + " ");
        }
        System.out.println();
    }   // End printKnapsackValues

    int setModulus()
	{
        int p;          // Modulus
        int total;      // Total sum of knapsack
        boolean Prime;  // To check whether a number is prime or not

        total = getTotalSumOfKnapsack();

		System.out.print("Enter a modulus (Must be greater than " + total + " and must be a prime number): ");
		p = input.nextInt();	
		Prime = isPrime(p);
		while (Prime == false || p <= total)
		{
            // Calculate the next prime number that's bigger than total sum of knapsack
            int hint = 0;
            
            // start looping from total sum
			for (int start = total; start >= total; start++)
			{
                // Start checking for prime number when start > total sum of knapsack
				if (start != total)
				{
					boolean test = isPrime(start);
					if (test == true)      // if prime
					{
						hint = start;
						break;
					}
				}
			}
            System.out.println("(Hint: The smallest prime number greater than " + total + " is " + hint + ")");
            System.out.print("Enter a modulus (Must be greater than " + total + " and must be a prime number): ");
            p = input.nextInt();
            Prime = isPrime(p);
        }

        System.out.println("Your modulus p is: " + p);
        return p;
    }   // End setModulus

    int setMultiplier()
	{
        int w;  // Multiplier
        int p;  // Modulus
        boolean relativePrime;

        p = getP();

		System.out.print("Enter a multiplier (Must be relatively prime and lesser than " + p + "): ");
		w = input.nextInt();
		relativePrime = gcd (w, p);
        
        // if value entered is not relative prime to p or more than p
		while (relativePrime == false || w >= p)
		{
            int hint = 0;
            // Start looping from value p, minus one each loop
			for (int start = p; start <= p; start--)
			{
				if (start != p)
				{
					boolean test = gcd(start, p);
					if (test == true)
					{
						hint = start;
						break;
					}
				}
			}
			System.out.println("(Hint: The largest number relative to " + p + " is " + hint + ")");
			System.out.print("Enter a multiplier (Must be relatively prime and lesser than " + p + "): ");
            w = input.nextInt();
            relativePrime = gcd (w, p);
        }
        
        return w;
    }   // End setMultiplier
    
    ArrayList <Integer> generatePublicKeys(ArrayList <Integer> knapsack)
	{
        int p;  // Modulus
        int w;  // Multiplier       
        ArrayList <Integer> publicKeys = new ArrayList <Integer> ();

        w = getW();
        p = getP();

		for (int item : knapsack)
		{
			int key = (w * item) % p;
			publicKeys.add(key);
        }
        
        return publicKeys;
    }   // End generatePublicKeys

    void printPublicKeys (ArrayList <Integer> publicKeys)
    {
        System.out.println("Your public key values are: ");
		for (int key : publicKeys)
		{
			System.out.print(key + " ");
		}
    }   // End printPublicKeys

    int[] encryption ()
    {
        int size;
        String str;
        char[] messageCharArr;
        int[] asciiMsgIntArr;
        String[] binaryMsgStrArr;   // To store final binary message (spaces are removed)
        int[] encryptedMsgIntArr;

        size = getSize();
        
        System.out.print("\nEnter your message to be encrypted: ");
		input.nextLine(); //Clear input buffer
        str = input.nextLine();
        
        // Count length of messageCharArr
		messageCharArr = new char[str.length()];
		asciiMsgIntArr = new int[str.length()];
        binaryMsgStrArr = new String[str.length()];
        
        // Loop thru each letter of message to initialize the message array
        System.out.println("-----------------------------------------------------");
        System.out.println("Loop thru each letter of message to convert to binary");
        System.out.println("-----------------------------------------------------");
		for (int i = 0; i < str.length(); i++) 
		{ 
            messageCharArr[i] = str.charAt(i); 
            asciiMsgIntArr[i] = (int)(messageCharArr[i]);   // Cast char to int to convert to decimal
            String s = String.format("%" + size + "s" , Integer.toBinaryString(asciiMsgIntArr[i]).toString());
            // format it to be same length as knapsack size
            s = (s.replaceAll(" ", "0"));   // replace all the empty spaces to 0

            System.out.println("Message Char[" + i + "]   : " + messageCharArr[i]);
            System.out.println("Message Dec[" + i + "]    : " + asciiMsgIntArr[i]);
            System.out.println ("Message Binary[" + i + "] : " + s + "\n");
			binaryMsgStrArr[i] = s; // each letter in binary form where the length = size of knapsack
        }
		
        encryptedMsgIntArr = new int[str.length()];
        System.out.println("--------------------------------------------------");
        System.out.println("Loop thru each letter of message (the binary form)");
        System.out.println("--------------------------------------------------");
        // Loop thru each letter of message (the binary form)
		for (int i = 0; i < binaryMsgStrArr.length; i++) // To loop thru the whole message
		{
            int sumT = 0;
            
            System.out.println("Letter       : " + messageCharArr[i]);
            System.out.println("Binary String: " + binaryMsgStrArr[i]);
            // loop thru public key & each binary digit of each letter            
			for (int j = 0; j < size; j++)  
			{                
                System.out.println("Binary String[" + j + "]: " + ((binaryMsgStrArr[i].charAt(j) - '0')) + 
                                    "\tPublic Key[" + j + "]: "  + publicKeys.get(j));
                // mXPublic here is digit[idx] x public key[idx]
                int mXPublic = ((binaryMsgStrArr[i].charAt(j) - '0') * publicKeys.get(j));
                sumT = sumT + mXPublic;
                System.out.println((binaryMsgStrArr[i].charAt(j) - '0') + " x " + publicKeys.get(j)+ "= " + mXPublic);               
			}
            encryptedMsgIntArr[i] = sumT;
            System.out.println("Sum: " + encryptedMsgIntArr[i]);
            System.out.println("--------------------------------------------------");
		}

		System.out.println("Your encrypted message is: ");
        System.out.print(Arrays.toString(encryptedMsgIntArr));
        System.out.println();
        
        return encryptedMsgIntArr;
    }   // End encryption

    ArrayList <Character> decryption ()
	{
        int w;
        int invW;
        int y = 0;              // Decrypted value of each letter of message
        int intInput;           // Encrypted value of each letter of message
        int size;               // Size of knapsack
        int totalSumOfKnapsack;
        
        ArrayList <Integer> knapsack = new ArrayList <Integer> ();
        ArrayList <Character> decryptedMessage = new ArrayList <Character> ();
        char [] decryptedBinaryInCharArr;
        int [] decryptedBinaryInIntArr;
        String strInput = "";

        w = getW();
        invW = calculateInverse(w);
        knapsack = getKnapsackArrList();
        size = getSize();       
		
		decryptedBinaryInCharArr = new char[size];
		decryptedBinaryInIntArr = new int[size];       
        
		do
		{
			System.out.print("Enter value to be decrypted ONE BY ONE (Enter 'decrypt' to stop appending): ");
			strInput = input.nextLine(); // input as String
			if (!strInput.equals("decrypt"))   // if not entering 'decrypt'
			{
				intInput = Integer.parseInt(strInput);  // convert String input to Integer
				y = (invW * intInput) % p;  // decrypted value in int form
				totalSumOfKnapsack = 0;
                
                // Loop thru the knapsack, start from index 6 down to index 0
				for (int i = size-1; i > -1; i--)
				{
					totalSumOfKnapsack = totalSumOfKnapsack + knapsack.get(i);
					if (totalSumOfKnapsack > y)
					{
						decryptedBinaryInCharArr[i] = 0;
						totalSumOfKnapsack = totalSumOfKnapsack - knapsack.get(i);
					}
					else if (totalSumOfKnapsack <= y)
					{
						decryptedBinaryInCharArr[i] = 1;
                    }    
                }		
			}
            
            // Show the decrypted letter in binary form
            System.out.print("Decrypted letter in binary : ");	
			for (int i = 0; i < size; i++)
			{
                decryptedBinaryInIntArr[i] = decryptedBinaryInCharArr[i];
                System.out.print(Integer.toString(decryptedBinaryInIntArr[i]));
            }
            System.out.println();
            
            
            // Convert binary in int array into int
            // Ref: https://stackoverflow.com/questions/21416315/int-array-to-int-number-in-java/21416523
			int result = 0;
			for( int temp = 0; temp < decryptedBinaryInIntArr.length; temp++)
			{
                //System.out.println ("loop " + temp);
                result *= 10;
                //System.out.println ("result after *10:                 " + result);
                result += decryptedBinaryInIntArr[temp];
                //System.out.println ("result after + each binary digit: " + result + "\n");
            }
            
            // Convert int to binary in String form
            String s = Integer.toString(result);    
            
            // Convert to Decimal ASCII value
            // Ref: https://www.javatpoint.com/java-binary-to-decimal
            int decimalValue = Integer.parseInt(s, 2);  
            System.out.println ("Decrypted letter in decimal: " + decimalValue);

            // Convert from Decimal to Character to get the plaintext
			char c = (char) decimalValue;
			if (!strInput.equals("decrypt"))    // if not entering 'decrypt' add character to arraylist
			{
				decryptedMessage.add(c);
			}
        } while (!strInput.equals("decrypt")); // if not entering 'decrypt'
        
        System.out.println("===============================================================================");
        System.out.print("Your decrypted message is: ");
		
		for (int i = 0; i < decryptedMessage.size(); i++)
		{
			System.out.print(decryptedMessage.get(i));
        }

        return decryptedMessage;
	}   // End decryption

    public static void main (String[] args)
    {
        // Local variables
        ArrayList <Integer> knapsack = new ArrayList <Integer> ();      // To store all the a values
        ArrayList <Integer> publicKeys = new ArrayList <Integer> ();    // To store public keys
        ArrayList <Character> decryptedMessage = new ArrayList <Character> ();  // To store decrypted letters
        int size;    // size of super increasing knapsack
        int totalSumOfKnapsack;   // total sum of al the a
        int p;       // modulus
        int w;       // Multiplier

        int[] encryptedMsgIntArr;   // To store encrypted letters

        knapsack Obj = new knapsack ();

        // Set the size of knapsack
        size = Obj.setKnapsackSize();
        Obj.setSize(size);
        System.out.println("Size of super-increasing knapsack has been initialised as " + size);
        System.out.println("\nPlease enter " + size + " values of a: ");
        
        // Set the values of a in the knapsack & print out the values
        knapsack = Obj.setA();
        Obj.setKnapsackArrList(knapsack);
        Obj.printKnapsackValues(knapsack);

        // Calculate the total sum of all a
        totalSumOfKnapsack = Obj.computeTotalSumOfKnapsack(knapsack);
        Obj.setTotalSumOfKnapsack(totalSumOfKnapsack);

        // Set the value of p (modulus)
        p = Obj.setModulus();
        Obj.setP(p);

        // Set the value of w (multiplier)
        w = Obj.setMultiplier();
        Obj.setW(w);

        // Generate public keys
        publicKeys = Obj.generatePublicKeys(knapsack);
        Obj.setPublicKeys(publicKeys);
        Obj.printPublicKeys(publicKeys);

        // Encrypt Message
        encryptedMsgIntArr = Obj.encryption();
        Obj.setEncryptedMsgIntArray(encryptedMsgIntArr);

        // Decrypt Message
        decryptedMessage = Obj.decryption();
        Obj.setDecryptedMessage(decryptedMessage);

    }   // End main function

}   // End knapsack class