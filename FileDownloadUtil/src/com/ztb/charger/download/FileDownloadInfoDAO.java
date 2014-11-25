
package com.ztb.charger.download;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FileDownloadInfoDAO implements FileStateListener {
    private Context mCtx;

    private DbHelper mDbHelper;

    private SQLiteDatabase mDb;

    private static FileDownloadInfoDAO instance;

    private ReentrantLock mLock = new ReentrantLock();

    public FileDownloadInfoDAO() {
    }

    private FileDownloadInfoDAO(Context ctx) {
        this.mCtx = ctx;
    }

    public static FileDownloadInfoDAO getInstance(Context context) {
        if (instance == null)
            instance = new FileDownloadInfoDAO(context);
        return instance;
    }

    public void open() {
        if (null == mDb || !mDb.isOpen()) {
            mDbHelper = new DbHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
        }
    }

    private void close() {
        if (mDb.isOpen()) {
            mDb.close();
            mDbHelper.close();
        }
    }

    protected void add(NetFile info) {
        mLock.lock();
        open();
        if (hasRecord(info.url)) {
            update(info);
        } else {
            String sql = "insert into " + NetFile.TABLE + "(" + NetFile.COL_url + ","
                    + NetFile.COL_progress + "," + NetFile.COL_state
                    + ") values (?,?,?)";
            Object[] bindArgs = {
                    info.url, info.progress, info.state
            };
            mDb.execSQL(sql, bindArgs);
        }
        close();
        mLock.unlock();
    }

    public void update(NetFile file) {
        if (!hasRecord(file.url)) {
            add(file);
            return;
        }
        mLock.lock();
        open();
        String sql = "update " + NetFile.TABLE + " set " + NetFile.COL_progress
                + "=? ," + NetFile.COL_state + "=? where " + NetFile.COL_url + "=? ";
        Object[] bindArgs = {
                file.progress, file.state, file.url
        };
        mDb.execSQL(sql, bindArgs);
        close();
        mLock.unlock();
    }

    public void delete(String url) {
        mLock.lock();
        open();
        mDb.execSQL("delete from " + NetFile.TABLE + " where " + NetFile.COL_url
                + "=?", new Object[] {
            url
        });
        close();
        mLock.unlock();
    }

    public NetFile query(String url) {
        mLock.lock();
        open();
        String querySQL = "select " + NetFile.COL_url + "," + NetFile.COL_progress
                + "," + NetFile.COL_state + " from " + NetFile.TABLE + " where "
                + NetFile.COL_url + "=? ";
        Cursor cursor = null;
        NetFile info = null;
        cursor = mDb.rawQuery(querySQL, new String[] {
            url
        });
        if (cursor.moveToFirst()) {
            info = new NetFile();
            info.url = cursor.getString(0);
            info.progress = cursor.getInt(1);
            info.state = cursor.getInt(2);
        }
        cursor.close();
        close();
        mLock.unlock();
        return info;
    }

    public List<NetFile> queryForState(int state) {
        mLock.lock();
        open();
        String querySQL = "select " + NetFile.COL_url + "," + NetFile.COL_progress
                + "," + NetFile.COL_state + " from " + NetFile.TABLE + " where "
                + NetFile.COL_state + "=? ";
        Cursor cursor = null;
        List<NetFile> list = new ArrayList<NetFile>();
        cursor = mDb.rawQuery(querySQL, new String[] {
            state + ""
        });
        while (cursor.moveToNext()) {
            NetFile info = new NetFile();
            info.url = cursor.getString(0);
            info.progress = cursor.getInt(1);
            info.state = cursor.getInt(2);
            list.add(info);
        }
        cursor.close();
        close();
        mLock.unlock();
        return list;
    }

    public void deleteAllWithState(int state) {
        open();
        mDb.execSQL("delete from " + NetFile.TABLE + " where " + NetFile.COL_state
                + "=?", new Object[] {
            state
        });
        close();
    }

    private boolean hasRecord(String url) {
        return query(url) == null;
    }

    @Override
    public void stateChanged(NetFile info, Context context) {
        mLock.lock();
        switch (info.state) {
            case DownloadState.inDownloadQueue:
                add(info);
                break;
            case DownloadState.fileDeleted:
                delete(info.url);
                break;
            case DownloadState.downloading:// 防止频繁更新数据库，性能开销太大
                break;
            default:
                update(info);
                break;
        }
        mLock.unlock();
    }

}
