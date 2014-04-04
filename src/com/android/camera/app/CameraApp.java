/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.app;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.android.camera.MediaSaverImpl;
import com.android.camera.processing.ProcessingServiceManager;
import com.android.camera.session.CaptureSessionManager;
import com.android.camera.session.CaptureSessionManagerImpl;
import com.android.camera.session.PlaceholderManager;
import com.android.camera.session.SessionStorageManager;
import com.android.camera.session.SessionStorageManagerImpl;
import com.android.camera.util.CameraUtil;
import com.android.camera.util.SessionStatsCollector;
import com.android.camera.util.UsageStatistics;

/**
 * The Camera application class containing important services and functionality
 * to be used across modules.
 */
public class CameraApp extends Application implements CameraServices {
    private MediaSaver mMediaSaver;
    private CaptureSessionManager mSessionManager;
    private SessionStorageManager mSessionStorageManager;
    private MemoryManagerImpl mMemoryManager;
    private PlaceholderManager mPlaceHolderManager;

    @Override
    public void onCreate() {
        super.onCreate();

        UsageStatistics.instance().initialize(this);
        SessionStatsCollector.instance().initialize(this);
        CameraUtil.initialize(this);

        Context context = getApplicationContext();
        ProcessingServiceManager.initSingleton(context);

        mMediaSaver = new MediaSaverImpl();
        mPlaceHolderManager = new PlaceholderManager(context);
        mSessionStorageManager = SessionStorageManagerImpl.create(this);
        mSessionManager = new CaptureSessionManagerImpl(mMediaSaver, getContentResolver(),
                mPlaceHolderManager, mSessionStorageManager);
        mMemoryManager = MemoryManagerImpl.create(getApplicationContext(), mMediaSaver);

        clearNotifications();
    }

    @Override
    public CaptureSessionManager getCaptureSessionManager() {
        return mSessionManager;
    }

    @Override
    public MemoryManager getMemoryManager() {
        return mMemoryManager;
    }

    @Override
    @Deprecated
    public MediaSaver getMediaSaver() {
        return mMediaSaver;
    }

    /**
     * Clears all notifications. This cleans up notifications that we might have
     * created earlier but remained after a crash.
     */
    private void clearNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
    }
}
