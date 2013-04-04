package com.smartlab.net.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

public class ServerForZigBeeFilter extends BaseFilter{

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		final CToJavaMessage message = ctx.getMessage();

		if (message != null) {
			byte[] body=message.getBody();
			
			System.out.println(toHex(body));
			
		}
		
		
		return ctx.getStopAction();
		
	}
	
	 /**

	    * 将字节数组转换为16进制字符串

	    *

	    * @param buffer

	    * @return

	    */

	   public static String toHex(byte[] buffer) {

	      StringBuffer sb = new StringBuffer(buffer.length * 2);

	      for (int i = 0; i < buffer.length; i++) {

	       sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));

	       sb.append(Character.forDigit(buffer[i] & 15, 16));

	      }

	      return sb.toString();

	   }

}
