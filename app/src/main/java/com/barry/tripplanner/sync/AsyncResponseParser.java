package com.barry.tripplanner.sync;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

abstract public class AsyncResponseParser implements Callback {

    protected Context m_context;
    protected PostProcess m_postProcess;
    protected NetError m_netError;

    public AsyncResponseParser(Context c, NetError netError) {
        m_netError = netError;
        m_context = c;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if (m_netError != null) m_netError.onNetError(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (null == response) return;

        if (response.code() == 401) {
            onAuthFail();
        }

        if (response.code() == 200) {
            try {
                JSONObject object = new JSONObject(response.body().string());
                if (null != object) parseResponse(object);
                if (null != m_postProcess) m_postProcess.onPostProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (m_netError != null) m_netError.onResponseError(response.code());
        }
    }

    public void onAuthFail() {
    }

    protected abstract void parseResponse(JSONObject jsonObject) throws Exception;

    public interface PostProcess {
        void onPostProcess();
    }

    public interface NetError {
        void onNetError(IOException e);
        void onResponseError(int error);
    }
}
