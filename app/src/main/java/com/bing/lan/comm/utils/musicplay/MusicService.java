package com.bing.lan.comm.utils.musicplay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bing.lan.comm.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.bing.lan.comm.utils.musicplay.MusicServiceCons.STATUS_CODE;
import static com.bing.lan.comm.utils.musicplay.MusicServiceCons.STATUS_LIST_CHANGE;
import static com.bing.lan.comm.utils.musicplay.MusicServiceCons.STATUS_LIST_INIT;
import static com.bing.lan.comm.utils.musicplay.MusicServiceCons.STATUS_NEXT_CUSTOM;

/**
 * @author 蓝兵
 * @time 2017/3/1  16:48
 */
public class MusicService extends Service {

    // private void setBePlaying(boolean value) {
    //     if (mIsPlaying != value) {
    //         mIsPlaying = value;
    //     }
    // }
    protected final LogUtil log = LogUtil.getLogUtil(getClass(), LogUtil.LOG_VERBOSE);
    public MultiPlayer mPlayer;
    private boolean mIsPlaying = false;
    private int mCurrentPlaylistPos = -1;
    private int mNextPlayPos = -1;
    private MusicPlayerHandler mPlayerHandler;
    private HandlerThread mHandlerThread;

    public List<Music> getPlaylist() {
        return mPlaylist;
    }

    private List<Music> mPlaylist = new ArrayList<>();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            handleCommandIntent(intent);
        }
    };

    public int getCurrentPlaylistPos() {
        return mCurrentPlaylistPos;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceStub(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MusicServiceCons.TAG, "Create service");

        new Thread() {
            @Override
            public void run() {
                initPlayerHandler();
                registerCmdReceiver();
                initPlayer();
            }
        }.start();
    }

    private void registerCmdReceiver() {
        // Initialize the intent filter and each action
        //注册广播接收者,接受外界的播放,暂停等等的广播
        final IntentFilter filter = new IntentFilter();
        filter.addAction(MusicServiceCons.SERVICECMD);
        filter.addAction(MusicServiceCons.TOGGLEPAUSE_ACTION);
        filter.addAction(MusicServiceCons.PAUSE_ACTION);
        filter.addAction(MusicServiceCons.STOP_ACTION);
        filter.addAction(MusicServiceCons.NEXT_ACTION);
        filter.addAction(MusicServiceCons.PREVIOUS_ACTION);
        filter.addAction(MusicServiceCons.PREVIOUS_FORCE_ACTION);
        filter.addAction(MusicServiceCons.REPEAT_ACTION);
        filter.addAction(MusicServiceCons.SHUFFLE_ACTION);

        filter.addAction(MusicServiceCons.PLAY_ACTION);
        filter.addAction(MusicServiceCons.SET_PLAYLIST_ACTION);
        // Attach the broadcast listener
        registerReceiver(mIntentReceiver, filter);
    }

    private void initPlayer() {
        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(mPlayerHandler);
    }

    private void initPlayerHandler() {
        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        log.v("Destroying service");
        super.onDestroy();
        if (mPlayerHandler != null) {
            mPlayerHandler.removeCallbacksAndMessages(null);
            mPlayerHandler = null;
        }

        if (mHandlerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                mHandlerThread.quitSafely();
            else
                mHandlerThread.quit();
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mIntentReceiver != null) {
            unregisterReceiver(mIntentReceiver);
            mIntentReceiver = null;
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null) {
            handleCommandIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //广播启动播放的方法
    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = intent.getStringExtra(MusicServiceCons.CMD_NAME);

        log.v("handleCommandIntent: action = " + action + ", command = " + command);

        if (MusicServiceCons.CMD_SET_PLAYLIST.equals(command) || MusicServiceCons.SET_PLAYLIST_ACTION.equals(action)) {
            ArrayList<Music> serializableExtra = (ArrayList<Music>) intent.getSerializableExtra(MusicServiceCons.PLAYLIST_URL);
            setPlaylist(serializableExtra);
        } else if (MusicServiceCons.CMD_NEXT.equals(command) || MusicServiceCons.NEXT_ACTION.equals(action)) {
            //播放下一首
            gotoNext();
        } else if (MusicServiceCons.CMD_PREVIOUS.equals(command) || MusicServiceCons.PREVIOUS_ACTION.equals(action)) {
            //播放上一首
            gotoPrev();
        } else if (MusicServiceCons.CMD_TOGGLEPAUSE.equals(command) || MusicServiceCons.TOGGLEPAUSE_ACTION.equals(action)) {
            //暂停/播放
            if (isPlaying()) {
                pause();
            } else {
                play();
            }
        } else if (MusicServiceCons.CMD_PAUSE.equals(command) || MusicServiceCons.PAUSE_ACTION.equals(action)) {
            //暂停
            pause();
        } else if (MusicServiceCons.CMD_PLAY.equals(command) || MusicServiceCons.PLAY_ACTION.equals(action)) {
            //播放
            play();
        } else if (MusicServiceCons.CMD_STOP.equals(command) || MusicServiceCons.STOP_ACTION.equals(action)) {
            //停止
            pause();
            seek(0);
        }
    }

    public boolean addPlaylist(List<Music> playlist) {
        if (playlist != null && playlist.size() > 0) {
            mPlaylist.addAll(playlist);
            sendStatusBroadcast(STATUS_LIST_CHANGE);
            log.i("addPlaylist(): 添加到播放列表成功 ");
            return true;
        }
        log.e("addPlaylist(): 添加到播放列表成功 ");
        return false;
    }

    public boolean addPlaylist(Music play) {
        if (play != null) {
            mPlaylist.add(play);
            sendStatusBroadcast(STATUS_LIST_CHANGE);
            log.i("addPlaylist(): 添加到播放列表失败 ");
            return true;
        }
        log.e("addPlaylist(): 添加到播放列表失败 ");
        return false;
    }

    public boolean setPlaylist(List<Music> playlist) {
        if (playlist != null && playlist.size() > 0) {
            mPlaylist.clear();
            mPlaylist.addAll(playlist);

            mCurrentPlaylistPos = 0;
            mNextPlayPos = mCurrentPlaylistPos + 1;
            if (!mPlayer.isInitialized()) {
                //首次设置的操作
                mPlayer.setDataSource(mPlaylist.get(mCurrentPlaylistPos).url);
                mPlayer.setNextDataSource(mPlaylist.get(mNextPlayPos).url);
                sendStatusBroadcast(STATUS_LIST_INIT);
                log.i("setPlaylist(): 设置播放列表成功 ");
            } else {
                //更改列表时的操作
                stop();
                play();
                sendStatusBroadcast(STATUS_LIST_CHANGE);
                log.i("setPlaylist(): 更改播放列表成功 ");
            }

            return true;
        }
        log.e("setPlaylist(): 设置播放列表失败 ");
        return false;
    }

    public void sendStatusBroadcast(int statusCode) {
        Intent intent = new Intent(MusicServiceCons.MUSIC_SERVICE_STATUS_CHANGES_BROADCAST);
        if (statusCode >= 0) {
            intent.putExtra(STATUS_CODE, statusCode);
        }
        sendBroadcast(intent);
    }

    public void play() {
        if (!mPlayer.isInitialized()) {
            mPlayer.setDataSource(mPlaylist.get(mCurrentPlaylistPos).url);
        }

        if (mPlayer.isInitialized()) {
            mPlayer.start();
            mIsPlaying = true;
            mPlayerHandler.removeMessages(MusicServiceCons.FADEDOWN);
            mPlayerHandler.sendEmptyMessage(MusicServiceCons.FADEUP);
        }
    }

    public void setNextTrack() {
        // mCurrentPlaylistPos++;
        mNextPlayPos = mCurrentPlaylistPos + 1;
        //默认情况就是列表的下一首
        setNextTrack(mNextPlayPos);
    }

    private void setNextTrack(int position) {
        // mNextPlayPos = position;
        log.v("setNextTrack: next play position = " + position);
        if (position >= 0 && mPlaylist != null && position < mPlaylist.size()) {
            mPlayer.setNextDataSource(mPlaylist.get(position).url);
        }
    }

    private int getNextPosition() {
        if (mPlaylist == null || mPlaylist.isEmpty()) {
            return -1;
        }
        return mNextPlayPos;
    }

    public void stop() {

        if (mPlayer.isInitialized()) {
            mIsPlaying = false;
            mPlayer.stop();
        }
    }

    public void pause() {
        log.v("Pausing playback");
        synchronized (this) {
            mPlayerHandler.removeMessages(MusicServiceCons.FADEUP);
            mIsPlaying = false;
            mPlayer.pause();
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setPlaying(boolean playing) {
        mIsPlaying = playing;
    }

    public void gotoNext() {
        goToPosition(mCurrentPlaylistPos + 1);
    }

    public void gotoPrev() {
        goToPosition(mCurrentPlaylistPos - 1);
    }

    //如果没启动也能启动
    public void goToPosition(int pos) {
        synchronized (this) {
            int size = mPlaylist.size();
            if (size <= 0) {
                Log.i(MusicServiceCons.TAG, "No play queue");
                return;
            }
            if (pos < 0) {
                Log.i(MusicServiceCons.TAG, "currentPlayPos: " + mCurrentPlaylistPos);
                return;
            }
            if (pos > size) {
                Log.i(MusicServiceCons.TAG, "Playlist.Size: " + size + ", currentPlayPos: " + mCurrentPlaylistPos);
                return;
            }

            if (pos == mCurrentPlaylistPos) {
                if (!isPlaying()) {
                    play();
                }
                return;
            }
            //更新当前播放位置
            mCurrentPlaylistPos = pos;
            mNextPlayPos = mCurrentPlaylistPos + 1;
            //将要播放的歌曲重新设置为下一首歌曲
            setNextTrack(pos);
            //播放指定的某一首歌曲
            mPlayer.gotoPosition();
            //直接启动播放下一首(在服务中已经设置)
            play();
            //启动后再次设置下一首
            mPlayerHandler.sendEmptyMessage(MusicServiceCons.TRACK_WENT_TO_NEXT);
            mPlayerHandler.obtainMessage(MusicServiceCons.MUSIC_SERVICE_STATUS_CHANGES, STATUS_NEXT_CUSTOM, 0).sendToTarget();
        }
    }

    public long seek(long position) {
        if (mPlayer.isInitialized()) {
            if (position < 0) {
                position = 0;
            } else if (position > mPlayer.duration()) {
                position = mPlayer.duration();
            }
            return mPlayer.seek(position);
        }
        return -1;
    }

    public void seekRelative(long deltaInMs) {
        synchronized (this) {
            if (mPlayer.isInitialized()) {
                final long newPos = position() + deltaInMs;
                final long duration = duration();
                if (newPos < 0) {
                    gotoPrev();
                    // seek to the new duration + the leftover position
                    seek(duration() + newPos);
                } else if (newPos >= duration) {
                    gotoNext();
                    // seek to the leftover duration
                    seek(newPos - duration);
                } else {
                    seek(newPos);
                }
            }
        }
    }

    public long position() {
        if (mPlayer.isInitialized()) {

            return mPlayer.position();
        }
        return -1;
    }

    public long duration() {
        if (mPlayer.isInitialized()) {
            // TODO: 2017/3/8 ???
            return mPlayer.duration();
        }
        return -1;
    }

    // public void saveMusic2Db() {
    //     // MusicPlayDao.saveMusicInfo(mPlaylist.get(mCurrentPlaylistPos));
    // }

    public Music getCurrentPlayMusic() {
        if (mPlaylist != null && mPlaylist.size() > 0 &&
                mCurrentPlaylistPos >= 0 && mCurrentPlaylistPos <= mPlaylist.size()) {

            Music music = mPlaylist.get(mCurrentPlaylistPos);
            music.lastPlayPosition = position();
            music.lastPlayDateMillis = System.currentTimeMillis();

            // Music music1 = new Music();
            // music1.lastPlayPosition = position();
            //
            // music1.url        =  music .url    ;
            // music1.duration   =  music .duration;
            // music1.albumId    =  music .albumId ;

            return music;
        }
        return null;
    }

    void changCurrentPlayPosition() {
        mCurrentPlaylistPos++;
        mNextPlayPos = mCurrentPlaylistPos + 1;
    }

    void resetCurrentPlayPosition() {
        mCurrentPlaylistPos = 0;
        mNextPlayPos = mCurrentPlaylistPos + 1;
    }
}
