/*
Name: Sally
UOW ID: 4603229
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class TransactionBlock
{
    String txn;
    String hashOfTxn;
    String previousTransactionHash;
    long Nonce;

    public TransactionBlock (String txn, String hashOfTxn, String PrevHash, long Nonce)
    {
        this.txn = txn;
        this.hashOfTxn = hashOfTxn;
        this.previousTransactionHash = PrevHash;
        this.Nonce = Nonce; 
    }

    public String getHashOfTxn()
    {
        return hashOfTxn;
    }

    public long getNonce()
    {
        return Nonce;
    }

    public String getTransaction()
    {
        return txn;
    }
}   // End TransactionBlock class

public class hashcash
{
    static SecureRandom random = new SecureRandom();

    static String LedgerFileName = "";
    static String outputFileName = "output.txt";
    static String transactionFileName = "Transactions.txt";

    // For storing stuff in the Ledger.txt
    static ArrayList <String> ledgerTransactionStrArrayList = new ArrayList<>();
    static ArrayList <String> ledgerHashStrArrayList = new ArrayList<>();

    // For storing stuff in the output.txt
    static ArrayList <String> outputTransactionStrArrayList = new ArrayList<>();
    static ArrayList <String> outputNonceStrArrayList = new ArrayList<>();

    // For storing stuff in the Transactions.txt
    static ArrayList <String> transactionStrArrayList = new ArrayList<>();
    
    static ArrayList <String> firstTxnAndHash = new ArrayList<>();  // to store first txn & hashed value
    static ArrayList <String> arrayListOfTransactions = new ArrayList<>();  // to store transactions from Transaction.txt
    
    static ArrayList <TransactionBlock> transactionBlocksArrayList = new ArrayList<>();
    
    static String zero = "000000000000000000000000000000000000"; 
    static int limit = 0;

    // For font color purpose
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_RESET = "\u001B[0m";
    
    // Ref: https://gist.github.com/itarato/abef95871756970a9dad
    static String AES_CBC_Encrypt(String input, byte[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException 
    {      
        byte[] clean = input.getBytes();

        // Converting static IV to bytes
        byte[] iv = "cryptohurtsbrain".getBytes();
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        // Key
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        // Encrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
        byte[] encrypted = cipher.doFinal(clean);

        // Convert to String
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < encrypted.length; i++) 
        {
            sb.append(Integer.toString((encrypted[i] & 0xff) + 0x100, 16).substring(1));
        }

        String result = sb.toString().substring(0,32);  // 128 bit
        return result;
    }   // End AES_CBC_Encrypt

    static ArrayList<String> readFile (String FileName)
    {
        ArrayList <String> data = new ArrayList<>();

        try
        {
            File readFile = new File(FileName);
            Scanner input = new Scanner(readFile);
            while(input.hasNextLine())
            {
                data.add(input.nextLine());
            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return data;
    }   // End readFile

    static void readAllFiles() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException 
    {
        int count;

        // Read the Ledger.txt
        try
        {
            count = 0;
            
            File readFile = new File(LedgerFileName);
            Scanner input = new Scanner(readFile);
            while(input.hasNextLine())
            {
                count++;
                if(count % 2 == 1)
                {
                    ledgerTransactionStrArrayList.add(input.nextLine());
                }
                if (count % 2 == 0)
                {
                    ledgerHashStrArrayList.add(input.nextLine());
                }

            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        // Read output file
        try
        {
            count = 0;
            File readFile = new File(outputFileName);
            Scanner input = new Scanner(readFile);

            // odd is transaction, even is Nonce
            while(input.hasNextLine())
            {
                count++;
                if (count % 2 == 1)
                {
                    outputTransactionStrArrayList.add(input.nextLine());
                }
                if (count % 2 == 0)
                {
                    outputNonceStrArrayList.add(input.nextLine());
                }

            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        // Read transactions file
        try
        {
            File readFile = new File(transactionFileName);
            Scanner input = new Scanner(readFile);
            while(input.hasNextLine())
            {
                transactionStrArrayList.add(input.nextLine());
            }
            input.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }   // End readAllFiles

    // Write transaction and hash to Ledger.txt
    static void writeToLedger (ArrayList<TransactionBlock> Blocks)
    {
        String txnAndHash = "";
        for (int i = 0; i < Blocks.size(); i++)
        {
            txnAndHash = txnAndHash + Blocks.get(i).getTransaction() + "\n" + 
                                            Blocks.get(i).getHashOfTxn() + "\n";
        }

        try
        {
            FileWriter writeLedger = new FileWriter(LedgerFileName);
            writeLedger.write(txnAndHash);
            writeLedger.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }   // End writeToLedger

    // Write transaction & Nonce to output.txt
    static void writeToOutput (ArrayList<TransactionBlock> Blocks)
    {
        String txnAndNonce = "";
        for (int i = 1; i < Blocks.size(); i++)
        {
            txnAndNonce = txnAndNonce  + Blocks.get(i).getTransaction() + "\n" + 
                                                Blocks.get(i).getNonce() + "\n";
        }

        try
        {
            FileWriter writeOutput = new FileWriter(outputFileName);
            writeOutput.write(txnAndNonce);
            writeOutput.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }   // End writeToOutput

    static void generateBlocks(String [] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException
    { 
        LedgerFileName = args[3];
        firstTxnAndHash = readFile(LedgerFileName);   // to store first txn and hashed value
        arrayListOfTransactions = readFile(transactionFileName);    // to store all other transactions

        // A transaction block with hashed value read from Ledger.txt
        // Transaction = "Anne pays 50 dollars to Bob" inside the Ledger.txt
        // Previous Hash = null
        transactionBlocksArrayList.add(new TransactionBlock(firstTxnAndHash.get(0), firstTxnAndHash.get(1), null, 0));
        
        // For debugging
        //System.out.println("Inside Genesis Block - Transaction: " + firstTxnAndHash.get(0));
        //System.out.println("Inside Genesis Block - currentHash: " + firstTxnAndHash.get(1));
        //System.out.println();

        for (int i = 0; i < arrayListOfTransactions.size(); i++)
        {
            long Nonce = 0;
            String currentHash = "";
            String prevHash = transactionBlocksArrayList.get(i).getHashOfTxn();
            String transaction = arrayListOfTransactions.get(i);

            // Key from first 16 bytes (128 bits) of transaction
            byte[] key = new byte[16];
            key = (transaction+prevHash).substring(0,16).getBytes();

            // Mining Nonce that produce the hash value that starts with 4 zeros in front
            do
            {
                Nonce++;
                currentHash = AES_CBC_Encrypt(String.valueOf(Nonce)+prevHash+transaction, key);
            }
            while (!currentHash.startsWith(zero.substring(0,limit)));

            transactionBlocksArrayList.add(new TransactionBlock(transaction, currentHash, prevHash, Nonce));

            // For debugging
            //System.out.println("Inside Subsequent Block - Transaction: " + arrayListOfTransactions.get(i));
            //System.out.println("Inside Subsequent Block - Nonce: " + Nonce);   
            //System.out.println ("Inside Subsequent Block - prevHash: " + prevHash);
            //System.out.println ("Inside Subsequent Block - currentHash: " + currentHash + "\n");

        } // End for loop
        
        System.out.println("Genesis Block Hash : " + transactionBlocksArrayList.get(0).getHashOfTxn());
        System.out.println();
        System.out.printf("%-45s %-35s %-10s", "Transactions","Hash Values", "Nonce");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------");

        for (int i = 1; i < transactionBlocksArrayList.size();i++)
        {
            System.out.printf("%-45s %-35s %-10s \n", transactionBlocksArrayList.get(i).getTransaction(),transactionBlocksArrayList.get(i).getHashOfTxn(), transactionBlocksArrayList.get(i).getNonce());
        }

        writeToLedger(transactionBlocksArrayList);
        writeToOutput(transactionBlocksArrayList);
    }   // End generateBlocks

    public static void main(String args []) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException  
    {
        limit = Integer.valueOf(args[1]);

        generateBlocks(args);
        readAllFiles();

        // Loop every transaction in the Transactions.txt
        int j;

        // Starts from second transaction: Bob pays 100 dollars to Charlie
        for (int i = 0; i < transactionStrArrayList.size(); i++)
        {
            j = i + 1;
            System.out.println();
            System.out.println(ANSI_RED + "Transaction Block " + j + ANSI_RESET);

            String validation;

            // Creating key from first 16 bytes (128 bits) of transaction
            byte[] key = new byte[16];
            key = transactionStrArrayList.get(i).substring(0,16).getBytes();

            String currentHash = ledgerHashStrArrayList.get(j);
            String currentNonce = outputNonceStrArrayList.get(i);
            String prevHash = ledgerHashStrArrayList.get(i);
            String transaction = transactionStrArrayList.get(i);
            String previousTransaction = ledgerTransactionStrArrayList.get(i);

            validation = AES_CBC_Encrypt((String.valueOf(currentNonce)+prevHash+transaction), key);
        
            System.out.printf("%-35s: %s%n", "Previous Transaction", previousTransaction);
            System.out.printf("%-35s: %s%n%n", "Previous Hashed", prevHash);

            System.out.printf("%-35s: %s%n", "Current Transaction", transaction);
            System.out.printf("%-35s: %s%n", "Secret Nonce", currentNonce);
            System.out.printf("%-35s: %s%n", "Computed Hash with Secret Nonce", validation);
            System.out.printf("%-35s: %s%n", "Hash value in the ledger", currentHash);

            if (currentHash.equals(validation))
            {
                System.out.printf("%-35s: %s%n", "Hash validation is", "True");
            }
            else
            {
                System.out.printf("%-35s: %s%n", "Hash validation is", "False");
            }
            System.out.println("====================================================================================");
        }
    }   // End main
}   // End POF class
