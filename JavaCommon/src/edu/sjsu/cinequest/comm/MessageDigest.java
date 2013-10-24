package edu.sjsu.cinequest.comm;

public abstract class MessageDigest {
	public abstract void update(byte[] input);
	public abstract byte[] digest();
}
