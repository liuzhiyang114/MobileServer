package com.smartlab.net.grizzly;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.impl.SafeFutureImpl;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;


public class MobileClientService {
	private static final Logger logger = Logger.getLogger(MobileClientService.class.getName());
	
	public static String WriteMsgToMSForCallBack(String HostAdress,int port,String Message){
		
		final FutureImpl<SMARTMessage> resultMessageFuture = SafeFutureImpl.create();
		Connection _conn=null;
		String returnMessage=null;
		
		// Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        
        // StringFilter is responsible for Buffer <-> String conversion
//        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
        filterChainBuilder.add(new SMARTFilter());
        
        // ClientFilter is responsible for redirecting server responses to the standard output
        filterChainBuilder.add(new ServerForMSFilter(resultMessageFuture));
        
     // Create TCP transport
        final TCPNIOTransport transport =
                TCPNIOTransportBuilder.newInstance().build();
        transport.setIOStrategy(org.glassfish.grizzly.strategies.SameThreadIOStrategy.getInstance());
        transport.setProcessor(filterChainBuilder.build());

        try {
            // start the transport
            transport.start();

            // perform async. connect to the server
            Future<Connection> future = transport.connect(HostAdress,
            		port);
            // wait for connect operation to complete
            _conn = future.get(5, TimeUnit.SECONDS);

            assert _conn != null;
            
//            System.out.println("Ready... connected to MobileServer!");
            
            byte[] testMessage = Message.getBytes("UTF-8");
            
            SMARTMessage sentMessage = new SMARTMessage((byte) 1, (byte) 2,
                    (byte) 0x0F, (byte) 0, 0L, testMessage);
            _conn.write(sentMessage);
            
            returnMessage=new String(resultMessageFuture.get(10, TimeUnit.SECONDS).getBody(),"UTF-8");
            
            resultMessageFuture.recycle();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            // close the client connection
            if (_conn != null) {
            	_conn.closeSilently();
            }
            // stop the transport
            try {
				transport.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		return returnMessage;
	}

}
class ServerForMSFilter extends BaseFilter{
	
	FutureImpl<SMARTMessage> _resultMessageFuture;
	
	public ServerForMSFilter(FutureImpl<SMARTMessage> resultMessageFuture){
		_resultMessageFuture=resultMessageFuture;
	}

	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		SMARTMessage message = ctx.getMessage();
		_resultMessageFuture.result(message);

        return ctx.getStopAction();
	}
	
	
}
