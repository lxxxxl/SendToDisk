package lxx.ru.sendtodisk;

import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.OkHttpClientFactory;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.json.Link;

import java.io.File;

public class YandexDiskHelper {

    interface OnApiCallFinishListener {
        void onApiCallFinish(boolean success);
    }

    private RestClient restClient;
    private OnApiCallFinishListener onApiCallFinishListener;

    public YandexDiskHelper(final Credentials credentials, OnApiCallFinishListener onApiCallFinishListener) {
        OkHttpClient client = OkHttpClientFactory.makeClient();
        this.restClient = new RestClient(credentials, client);
        this.onApiCallFinishListener = onApiCallFinishListener;

    }

    public void saveFromUrl(final String url, final String destPath) {
        new Thread(new Runnable() {
            @Override
            public void run () {
                boolean success = false;
                try {
                    restClient.makeFolder(destPath);
                } catch (Exception ex) {
                }
                try {
                    restClient.saveFromUrl(url, destPath);
                    success = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (onApiCallFinishListener != null){
                    onApiCallFinishListener.onApiCallFinish(success);
                }
            }
        }).start();
    }

    public void uploadFile(final File sourceFile, final String destFilename) {
        new Thread(new Runnable() {
            @Override
            public void run () {
                boolean success = false;

                try {
                    String remotePath = destFilename.substring(0, destFilename.lastIndexOf("/"));
                    restClient.makeFolder(remotePath);
                } catch (Exception ex) {
                }

                try {
                    Link link = restClient.getUploadLink(destFilename, true);
                    restClient.uploadFile(link, true, sourceFile, null);
                    success = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (onApiCallFinishListener != null) {
                    onApiCallFinishListener.onApiCallFinish(success);
                }
            }
        }).start();
    }
}