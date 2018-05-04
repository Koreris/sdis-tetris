package com.sdis.tetris.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Utils {
	
	public static byte[] combineByteArrays(byte[] array1, byte[]array2) {
		byte[] combined = new byte[array1.length + array2.length];

		for (int i = 0; i < combined.length; ++i)
		{
		    combined[i] = i < array1.length ? array1[i] : array2[i - array1.length];
		}
		
		return combined;
	}
	
	public static byte[] convertToBytes(Object object) throws IOException {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
	         ObjectOutput out = new ObjectOutputStream(bos)) 
	    {
	        out.writeObject(object);
	        return bos.toByteArray();
	    } 
	}
	
	public static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	         ObjectInput in = new ObjectInputStream(bis)) 
	    {
	        return in.readObject();
	    } 
	}

}
