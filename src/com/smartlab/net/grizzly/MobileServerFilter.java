package com.smartlab.net.grizzly;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.impl.SafeFutureImpl;

public class MobileServerFilter extends BaseFilter {

	private Connection _conDBServer = null;
	private MobileServer _ms;

	// Create Scheduled thread pool
	// private ScheduledExecutorService scheduler =
	// Executors.newScheduledThreadPool(5, new ThreadFactory() {
	//
	// @Override
	// public Thread newThread(Runnable r) {
	// final Thread thread = new Thread(r);
	// thread.setDaemon(true);
	// return thread;
	// }
	// });

	public MobileServerFilter(MobileServer ms) {
		_ms = ms;
		if (ms.connection != null) {
			_conDBServer = ms.connection;
		} else {
			System.out
					.println("MobileServerFilter cannot get DBServer Connection");
		}
	}

	@Override
	public NextAction handleRead(final FilterChainContext ctx)
			throws IOException {
		// TODO Auto-generated method stub
		final SMARTMessage message = ctx.getMessage();

		if (message != null) {
			// If message is not null - it's first time the filter is getting
			// called
			// and we need to init async thread, which will reply

			// Peer address is used for non-connected UDP Connection :)
			final Object peerAddress = ctx.getAddress();

			// write the response
			if (_conDBServer != null) {
				try {
					SMARTMessage msg = message;
					System.out.println("MS rev:"
							+ new String(msg.getBody(), "UTF-8"));
					Long newTaskId = _ms.getNewTaskId();
					msg.setTaskId(newTaskId);

					// test
					String newBody = "MStoDB:{"
							+ new String(msg.getBody(), "UTF-8") + "}";
					byte[] newBytes = newBody.getBytes("UTF-8");
					msg.setBody(newBytes);

					FutureImpl<SMARTMessage> resultMessageFuture = SafeFutureImpl
							.create();
					_ms.taskSet.put(newTaskId, resultMessageFuture);

					_conDBServer.write(msg);
					try {
						SMARTMessage rcvMessage = resultMessageFuture.get(5,
								TimeUnit.SECONDS);

						System.out.println("MS to MB(pro1):{我是手机服务器"
								+ new String(rcvMessage.getBody(), "UTF-8"));

						// test
						String newBody2 = "MStoMB:{我是手机服务器"
								+ new String(rcvMessage.getBody(), "UTF-8")
								+ "}";
						byte[] newBytes2 = newBody2.getBytes("UTF-8");
						msg.setBody(newBytes2);

						ctx.write(msg);
						resultMessageFuture.recycle();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						// ctx.write("InterruptedException for DBServer to get data..");
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block

						e.printStackTrace();
						// ctx.write("ExecutionException for DBServer to get data..");
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						SMARTMessage sentMessage = new SMARTMessage((byte) 1,
								(byte) 2, (byte) 0x0F, (byte) 0, 0L, null);
						ctx.write(sentMessage);
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("cannot connect to DBServer");
			}

		}

		return ctx.getStopAction();
	}

}
