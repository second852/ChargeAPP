package com.chargeapp.whc.chargeapp.Drobox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;

import java.util.Arrays;
import java.util.List;

public class DbxRequestConfigFactory {


    private static DbxRequestConfig sDbxRequestConfig;

    public static List<String> scope= Arrays.asList("account_info.read", "files.content.write","files.content.read","contacts.write","file_requests.write","sharing.write","files.metadata.read");

    public static DbxRequestConfig getRequestConfig() {
        if (sDbxRequestConfig == null) {
            sDbxRequestConfig = DbxRequestConfig.newBuilder("examples-v2-demo")
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();
        }
        return sDbxRequestConfig;
    }
}
