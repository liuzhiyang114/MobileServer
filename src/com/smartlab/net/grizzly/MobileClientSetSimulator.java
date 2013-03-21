package com.smartlab.net.grizzly;

public class MobileClientSetSimulator {

	public static void main(String[] args) {

		for (int i = 0; i < 400; i++) {
			final int ii = i;
			new Thread() {
				public void run() {
					super.run();
					MobileClient mc = new MobileClient(ii);
					String rev=mc.write(ii+"hello");
//					mc.write("2hello12345678901234567890123456789012345678901234567890");
					System.out.println(rev);
//					while(true){
//						try {
//							sleep(2000);
//							String ss=mc.write(j+"hello");
//							System.out.println(ss);
//							j++;
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					
				}
			}.start();

		}

	}

}

class MobileClient {
	int _i;

	public MobileClient(int i) {
		_i = i;
	}

	public String write(String msg) {
		String back = MobileClientService.WriteMsgToMSForCallBack("127.0.0.1",
				7777, msg);

//		System.out.println("client" + _i + " rev:" + back);
		return "client" + _i + " rev:" + back;
	}
}