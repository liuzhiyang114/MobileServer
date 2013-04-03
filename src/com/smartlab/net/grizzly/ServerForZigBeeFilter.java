package com.smartlab.net.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

public class ServerForZigBeeFilter extends BaseFilter{

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		return super.handleRead(ctx);
		
	}
	
	

}
