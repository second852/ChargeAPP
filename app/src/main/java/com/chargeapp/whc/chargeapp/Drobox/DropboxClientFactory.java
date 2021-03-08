package com.chargeapp.whc.chargeapp.Drobox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import static android.content.Context.MODE_PRIVATE;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;
    private static Context context;


    public static void per(Context context){
        boolean isNewContext=DropboxClientFactory.context!=context;
        DropboxClientFactory.context=context;
        SharedPreferences prefs = context.getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String serailizedCredental = prefs.getString("credential", null);
        if (serailizedCredental == null||isNewContext) {
            DbxCredential credential = Auth.getDbxCredential();
            if (credential != null) {
                prefs.edit().putString("credential", credential.toString()).apply();
                initAndLoadData(credential);
            }
        } else {
            try {
                DbxCredential credential = DbxCredential.Reader.readFully(serailizedCredental);
                initAndLoadData(credential);
            } catch (JsonReadException e) {
                throw new IllegalStateException("Credential data corrupted: " + e.getMessage());
            }
        }
    }

    private static void initAndLoadData(String accessToken) throws DbxException {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(context, DropboxClientFactory.getClient());
    }

    private static void initAndLoadData(DbxCredential dbxCredential) {
        DropboxClientFactory.init(dbxCredential);
        PicassoClient.init(context, DropboxClientFactory.getClient());
    }



    public static void init(String accessToken) throws DbxException {
        if (sDbxClient == null) {
            sDbxClient = new DbxClientV2(DbxRequestConfigFactory.getRequestConfig(), accessToken);
            ListFolderBuilder listFolderBuilder = sDbxClient.files().listFolderBuilder("");
            ListFolderResult result = listFolderBuilder.withRecursive(true).start();
            while (true) {
                if (result != null) {
                    for (Metadata entry : result.getEntries()) {
                        if (entry instanceof FileMetadata) {
                            Log.d("XX", "Added file: " + entry.getPathLower());
                        }
                    }
                    if (!result.getHasMore()) {
                        break;
                    }
                    try {
                        result = sDbxClient.files().listFolderContinue(result.getCursor());
                    } catch (DbxException e) {
                        e.fillInStackTrace();
                    }
                }
            }

        }
    }

    public static void init(DbxCredential credential) {
        credential = new DbxCredential(credential.getAccessToken(), -1L, credential.getRefreshToken(), credential.getAppKey());
        if (sDbxClient == null) {
            sDbxClient = new DbxClientV2(DbxRequestConfigFactory.getRequestConfig(), credential);

        }
    }

    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return sDbxClient;
    }
}
