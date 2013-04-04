package com.smartlab.net.grizzly;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

public class DataBaseServer {

	
	private static final Logger logger = Logger.getLogger(DataBaseServer.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//启动为ZigBee端服务线程
		ServerForZigBee serverForZigBeeThread=new ServerForZigBee();
		serverForZigBeeThread.start();
		
		// Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());

        // StringFilter is responsible for Buffer <-> String conversion
        //filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
        filterChainBuilder.add(new SMARTFilter());

        // EchoFilter is responsible for echoing received messages
        filterChainBuilder.add(new DataBaseServerFilter());
        
        // Create TCP transport
        final TCPNIOTransport transport =
                TCPNIOTransportBuilder.newInstance().build();
        
        transport.setProcessor(filterChainBuilder.build());

        try {
            // binding transport to start listen on certain host and port

            transport.bind("10.84.199.239", 7776);// IP为实验室的局域网IP


            // start the transport
            transport.start();

            logger.info("Press any key to stop the DataBaseServer...");
            System.in.read();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            logger.info("Stopping DataBaseServerTransport...");
            // stop the transport
            try {
				transport.stop();
				logger.info("Stopped DataBaseServerTransport...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

	}

}
class ServerForZigBee extends Thread{
	
	private static final Logger logger = Logger.getLogger(ServerForZigBee.class.getName());

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		//启动对ZigBee端的服务
		// Create a FilterChain using FilterChainBuilder
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

		// Add TransportFilter, which is responsible
		// for reading and writing data to the connection
		filterChainBuilder.add(new TransportFilter());

//		filterChainBuilder.add(new CharFilter());
		// StringFilter is responsible for Buffer <-> String conversion
//	        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
		filterChainBuilder.add(new CToJavaMessageFilter());
		
		// EchoFilter is responsible for echoing received messages
		filterChainBuilder.add(new ServerForZigBeeFilter());
		
		final TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance(); 
		final ThreadPoolConfig config = builder.getWorkerThreadPoolConfig(); 
		config.setCorePoolSize(50).setMaxPoolSize(200).setQueueLimit(1000).setKeepAliveTime(10, TimeUnit.SECONDS); 
		builder.setServerConnectionBackLog(1000);
		final TCPNIOTransport transport = builder.build();
		// Create TCP transport
//	        final TCPNIOTransport transport =
//	                TCPNIOTransportBuilder.newInstance().build();
		
		
		transport.setIOStrategy(org.glassfish.grizzly.strategies.WorkerThreadIOStrategy.getInstance());
		transport.setServerSocketSoTimeout(10);
		
//	        transport.setKernelThreadPoolConfig(ThreadPoolConfig.defaultConfig().setCorePoolSize(100).setMaxPoolSize(400).setQueueLimit(500));
		
		transport.setProcessor(filterChainBuilder.build());
		
		try {
		    // binding transport to start listen on certain host and port
		    transport.bind("localhost", 7775);

		    // start the transport
		    transport.start();

		    logger.info("Press any key to stop the ServerForZigBeeThread...");
		    System.in.read();
		    //System.out.println(ms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    logger.info("Stopping ServerForZigBeeThread...");
		    // stop the transport
		    try {
				transport.stop();
				logger.info("Stopped ServerForZigBeeThread...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
