package com.xxnr.operation.protocol;


import com.xxnr.operation.utils.RndLog;

public class NetworkDataService {
	private static final String TAG = "NetworkServiceImpl";
	private ServerAsyncRequester mRequester;
	private static NetworkDataService networkDataService;

	/**
	 * Must new a network service implement on UI thread.
	 */
	NetworkDataService() {
		mRequester = new ServerAsyncRequester(new ServerInterface());
	}

	public static NetworkDataService getNetworkDataService() {

		if (networkDataService == null) {
			networkDataService = new NetworkDataService();
		}
		return networkDataService;

	}

	public void callServerInterface(Request req) {
		RndLog.d(TAG, "callServerInterface. api=[" + req.getApi() + "]");
		mRequester.request(req);
	}

}
