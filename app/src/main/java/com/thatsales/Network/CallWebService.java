package com.thatsales.Network;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.R;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;


public class CallWebService extends AsyncTask<String, String, String> {

    String requestApi;
    Context mContext;
    boolean showProcessing;
    HttpURLConnection connection;
    Constants.SERVICE_TYPE mType;
    OnWebServiceResult webServiceResult;
    Hashtable<String, String> postParameters;
    ProgressDialog prog;
    boolean value = true;

    /**
     * Initizalize web address calling object
     *
     * @param mContext
     * @param url
     * @param parameters
     * @param type
     * @param webResultInterface
     */
    public CallWebService(Context mContext, String url, Hashtable<String, String> parameters, Constants.SERVICE_TYPE type,
                          OnWebServiceResult webResultInterface) {
        this.mContext = mContext;
        this.requestApi = url;
        this.mType = type;
        this.postParameters = parameters;
        this.webServiceResult = webResultInterface;
    }

    public CallWebService(Context mContext, String url, Hashtable<String, String> parameters, Constants.SERVICE_TYPE type,
                          OnWebServiceResult webResultInterface, boolean value) {
        this.mContext = mContext;
        this.requestApi = url;
        this.mType = type;
        this.postParameters = parameters;
        this.webServiceResult = webResultInterface;
        this.value = value;//check progress bar status
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (value) {
            prog = new ProgressDialog(mContext);
            prog.setMessage(mContext.getResources().getString(R.string.loading));
            prog.setIndeterminate(false);
            prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prog.setCancelable(false);
            prog.show();
        }

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(this.requestApi);
            System.out.println("REQUEST : " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setConnectTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            if (postParameters != null) {
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(getPostParamString(postParameters));
                outputStream.flush();
                outputStream.close();
            }

            //Get Response
            InputStream inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            //put breakpoint here..
            StringBuffer response = new StringBuffer();
            System.out.println("response: " + response.toString());
            while ((line = bufferedReader.readLine()) != null) {
                //put breakpoint here...
                response.append(line);
                response.append('\r');
            }
            bufferedReader.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * Pass Hashtable of key value pair we need to post
     *
     * @param params
     * @return
     */
    private String getPostParamString(Hashtable<String, String> params) {
        if (params.size() == 0)
            return "";
        StringBuffer buf = new StringBuffer();
        Enumeration<String> keys = params.keys();
        while (keys.hasMoreElements()) {
            buf.append(buf.length() == 0 ? "" : "&");
            String key = keys.nextElement();
            buf.append(key).append("=").append(params.get(key));
        }
        System.out.println("aaaaa: " + buf.toString());
        return buf.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            if (value) {
                if (prog.isShowing()) {
                    prog.dismiss();
                }
            }
            if (!(result == null)) {
                webServiceResult.onWebServiceResult(result, mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (prog.isShowing()) {
                prog.dismiss();
            }
        }
    }
}
