package com.smartlab.net.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;

public class CharFilter extends BaseFilter {

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		Buffer sourceBuffer = ctx.getMessage();
		if(sourceBuffer.hasArray()){
//			byte[] arr = sourceBuffer.array();
//			byte[] signarr = ToSignCharArray(arr);
//			
//			// Retrieve the memory manager
//			final MemoryManager memoryManager = ctx.getConnection().getTransport()
//					.getMemoryManager();
//
//			// allocate the buffer of required size
//			final Buffer output = memoryManager.allocate(signarr.length);
//			output.put(signarr);
//			// Set the Buffer as a context message
//			ctx.setMessage(output);
//			
//			sourceBuffer.dispose();
			byte[] arr = sourceBuffer.array();
			arr=ToSignCharArray(arr);
		}
		
		return ctx.getInvokeAction();
	}
	
	

	@Override
	public NextAction handleWrite(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		return super.handleWrite(ctx);
	}



	public byte[] ToUnsignedCharArray(byte[] signChar) {
		for (int i = 0; i < signChar.length; i++) {
			int x = ((byte) signChar[i]) >= 0 ? signChar[i]
					: ((byte) signChar[i]) + 256;
			signChar[i] = (byte) x;
		}
		return signChar;
	}

	public byte[] ToSignCharArray(byte[] unsignChar) {
		for (int i = 0; i < unsignChar.length; i++) {
			int x = ((char) unsignChar[i]) <= 127 ? unsignChar[i]
					: ((char) unsignChar[i]) - 256;
			unsignChar[i] = (byte) x;
		}
		return unsignChar;
	}


}
