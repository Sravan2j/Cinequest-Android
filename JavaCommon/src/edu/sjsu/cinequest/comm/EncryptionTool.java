package edu.sjsu.cinequest.comm;

import java.io.UnsupportedEncodingException;

public class EncryptionTool
{
	private static String convertToHex(byte[] data) 
	{
       String result = "";
	   for (int i=0; i < data.length; i++)
	   {
	      result += Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1);
	   }
	   return result;
	}

	public static String SHA1(String text) throws UnsupportedEncodingException
	{
	   MessageDigest md = Platform.getInstance().getMessageDigestInstance("SHA-1");
	   md.update(text.getBytes("iso-8859-1"));
	   byte[] sha1Hash =md.digest();
	   return convertToHex(sha1Hash);
	}
}
