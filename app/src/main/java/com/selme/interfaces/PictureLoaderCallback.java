package com.selme.interfaces;

import android.net.Uri;

public interface PictureLoaderCallback {

    void onPictureDownloaded(Uri uri, int requestCode, int pos);

}
