package com.selme.presenter.interfaces;

import android.net.Uri;

public interface PictureLoaderCallback {

    void onPictureDownloaded(Uri uri, int requestCode, int pos);

}
