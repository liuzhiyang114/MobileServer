package com.smartlab.net.grizzly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
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
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.grizzly.utils.StringFilter;



public class MobileServer {

	public Connection connection = null;
//	FutureImpl<String> resultMessageFuture = SafeFutureImpl.create();
	Map<Long,FutureImpl<SMARTMessage>> taskSet=new HashMap<Long,FutureImpl<SMARTMessage>>();
	private static Long taskId=0L;
	public static MobileServer ms;
	private static final Logger logger = Logger.getLogger(MobileServer.class.getName());
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ms=new MobileServer();
		FutureImpl<Connection> resDBconFuture = SafeFutureImpl.create();
		//启动连接DBServer线程
		ConnectToDBServer connectToDBThread=new ConnectToDBServer(ms,resDBconFuture);
		connectToDBThread.start();
		
		try {
			ms.connection=resDBconFuture.get(5, TimeUnit.SECONDS);
			
			//启动对Mobile端的服务
			// Create a FilterChain using FilterChainBuilder
	        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

	        // Add TransportFilter, which is responsible
	        // for reading and writing data to the connection
	        filterChainBuilder.add(new TransportFilter());

	        // StringFilter is responsible for Buffer <-> String conversion
//	        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
	        filterChainBuilder.add(new SMARTFilter());
	        
	        // EchoFilter is responsible for echoing received messages
	        filterChainBuilder.add(new MobileServerFilter(ms));
	        
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
	            transport.bind("localhost", 7777);

	            // start the transport
	            transport.start();

	            logger.info("Press any key to stop the MobileServer...");
	            System.in.read();
	            //System.out.println(ms);
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	            logger.info("Stopping MobileServerTransport...");
	            // stop the transport
	            try {
					transport.stop();
					logger.info("Stopped MobileServerTransport...");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	public Long getNewTaskId(){
		taskId=taskId+1L;
		if(taskId>100000000L){
			taskId=1L;
		}
		return taskId;
	}

}
class ConnectToDBServer extends Thread{
	private static final Logger logger = Logger.getLogger(ConnectToDBServer.class.getName());
	Connection _connection;
	MobileServer _ms;
	FutureImpl<Connection> _resDBconFuture;
	
	public ConnectToDBServer(MobileServer ms,FutureImpl<Connection> resDBconFuture){
		_ms=ms;
		_resDBconFuture=resDBconFuture;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		// Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        
        // StringFilter is responsible for Buffer <-> String conversion
//        filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
        filterChainBuilder.add(new SMARTFilter());
        
        // ClientFilter is responsible for redirecting server responses to the standard output
        filterChainBuilder.add(new ServerForDBFilter(_ms));
        
     // Create TCP transport
        final TCPNIOTransport transport =
                TCPNIOTransportBuilder.newInstance().build();
        transport.setProcessor(filterChainBuilder.build());

        try {
            // start the transport
            transport.start();

            // perform async. connect to the server
            Future<Connection> future = transport.connect("localhost",
                    7776);
            // wait for connect operation to complete
            _connection = future.get(10, TimeUnit.SECONDS);

            assert _connection != null;
            
            _resDBconFuture.result(_connection);
            
            System.out.println("Ready... connected to DBServer!");
            logger.info("Press any key to stop the ConnectedToDBserverThread...");
            System.in.read();
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
            if (_connection != null) {
                _connection.closeSilently();
            }
            // stop the transport
            try {
				transport.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}
class ServerForDBFilter extends BaseFilter{
	MobileServer _ms=null;
	private ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(5, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					final Thread thread = new Thread(r);
					thread.setDaemon(true);
					return thread;
				}
			});
	
	public ServerForDBFilter(MobileServer ms){
		_ms=ms;
	}
	
	@Override
	public NextAction handleRead(FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		SMARTMessage message = ctx.getMessage();
		if(message!=null){
			Long revTaskId=message.getTaskId();
			FutureImpl<SMARTMessage> f=_ms.taskSet.get(revTaskId);
			System.out.println("MS rev DB:"+new String(message.getBody(),"UTF-8"));
			if(f!=null){
				f.result(message);
				_ms.taskSet.remove(revTaskId);
			}
			
			
		}else{
			System.out.println("MS rev DB:null");
		}
		
		
        return ctx.getStopAction();
	}

	@Override
	public void exceptionOccurred(FilterChainContext ctx, Throwable error) {
		// TODO Auto-generated method stub
		super.exceptionOccurred(ctx, error);
		System.out.println("exceptionOccurred");
	}

	@Override
	public NextAction handleClose(final FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub
		
		System.out.println("handleClose");
//		scheduler.schedule(new Runnable() {
//
//			@Override
//			public void run() {
//				(TCPNIOTransport)(ctx.getConnection().getTransport());
//				
//			}
//			
//		}, 10, TimeUnit.MILLISECONDS);
		System.in.close();
		return super.handleClose(ctx);
	}
	
	
	
	
	
}