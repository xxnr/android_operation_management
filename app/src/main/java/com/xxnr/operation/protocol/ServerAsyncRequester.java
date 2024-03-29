package com.xxnr.operation.protocol;

import android.os.AsyncTask;

import com.xxnr.operation.developTools.NetUtil;
import com.xxnr.operation.developTools.app.App;
import com.xxnr.operation.utils.RndLog;


final class ServerAsyncRequester {
	private static final String TAG = "ServerAsyncRequester";
	private ServerInterface mServerInterface;

	/***********************************************
	 * Must new a ServerAsyncRequester on UI thread.
	 * 
	 * @param serverInterface
	 */
	ServerAsyncRequester(ServerInterface serverInterface) {
		mServerInterface = serverInterface;
		// make sure InteralHandler in AsyncTask is new on ui thread.

	}

	private class AsyncRequester extends
			AsyncTask<Object, Void, ResponseResult> {
		Request req = new Request();

		@Override
		protected ResponseResult doInBackground(Object... params) {
			req = (Request) params[0];

			// 判断网络
			if (!NetUtil.isConnected(App.getApp())) {
				return new ResponseResult(Request.NO_NETWORK_ERROR, "设备未连网");
			}

			final ApiType api = req.getApi();
			final RequestParams reqParams = req.getParams();
			RndLog.d(TAG, "doInBackground");
			try {
				return mServerInterface.request(api, reqParams);
			} catch (final NetworkException e) {
				RndLog.e(TAG, e.toString());
				return new ResponseResult(Request.HTTP_ERROR, "");
			}
		}

		@Override
		protected void onPostExecute(ResponseResult result) {
			req.setData(result);
			req.done();
		}
	}

	public void request(Request req) {
		RndLog.d(TAG, "request. api=" + req.getApi());
		new AsyncRequester().execute(req);
	}

}