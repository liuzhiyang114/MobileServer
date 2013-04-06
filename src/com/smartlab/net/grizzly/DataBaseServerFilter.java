package com.smartlab.net.grizzly;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.impl.SafeFutureImpl;

public class DataBaseServerFilter extends BaseFilter {

	// Create Scheduled thread pool
	private ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(100, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					final Thread thread = new Thread(r);
					thread.setDaemon(true);
					return thread;
				}
			});

	@Override
	public NextAction handleRead(final FilterChainContext ctx) throws IOException {
		// TODO Auto-generated method stub


		final SMARTMessage message = ctx.getMessage();

        if (message != null) {
            // If message is not null - it's first time the filter is getting called
            // and we need to init async thread, which will reply
            
            // Peer address is used for non-connected UDP Connection :)
            final Object peerAddress = ctx.getAddress();

            // Get the SuspendAction in advance, cause once we execute LongLastTask in the
            // custom thread - we lose control over the context
            final NextAction suspendActipn = ctx.getSuspendAction();
            
            // suspend the current execution
            ctx.suspend();
            
            // schedule async work
            scheduler.schedule(new Runnable() {

                @Override
                public void run() {
                    // write the response
                	String msg;
					try {
						msg = new String(message.getBody(), "UTF-8");
						
						// test
	            		msg = "DBtoMS:{" + msg + "}";
	            		// System.out.println(msg);
	            		message.setBody(msg.getBytes("UTF-8"));

	            		ctx.write(message);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                    // set the message null, to let our filter to distinguish resumed context
                    ctx.setMessage(null);

                    // resume the context
                    ctx.resume();
                }
            }, 10, TimeUnit.MILLISECONDS);

            // return suspend status
            return suspendActipn;
        }

        // If message is null - it means async thread completed the execution
        // and resumed the context

        return ctx.getStopAction();
	}

}
