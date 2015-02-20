package com.matthewbulat.monitorapplication;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Enc_Dec {
	
	
	protected byte[] Encrypt(byte[] data){
		byte[] encdata=null;
		try {
		//Key and cipher
	final String key ="Bar12345Bar12345";
	Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	Cipher cipher = Cipher.getInstance("AES");
	//encrypt text
	cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	encdata = cipher.doFinal(data);
	
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return encdata;
	}
	protected byte[] Decrypt(byte[] data){
		byte[] decdata=null;
		try {
			final String key = "Bar12345Bar12345";
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decdata = cipher.doFinal(data);
            }catch(Exception e) {
         e.printStackTrace();
      }
		return decdata;
	}
	

}
