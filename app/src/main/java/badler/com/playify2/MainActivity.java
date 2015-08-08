package badler.com.playify2;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;


public class MainActivity extends Activity {
    private String[] mAudioPath;
    private ArrayList<Bitmap> bitmapArr;
    private Bitmap[] bitmapConvert;
    private Bitmap[] bitmapArr2;
    Bitmap[] smallArr;
    private String[] smallAlbumArr;
    private Handler handler;
    private String[] songList;
    private GridView mostPlayed;
    private ListView songListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmapArr = new ArrayList<Bitmap>();
        // = new ArrayList<String>();

        mostPlayed = (GridView) findViewById(R.id.gridView);
        songListView = (ListView) findViewById(R.id.songListView);
        setUpLayout();

    }
    boolean threadComplete = false;
    public void setUpLayout() {
        songList = getAudioList();
        smallArr = new Bitmap[3];
        smallArr[0] = bitmapArr.get(bitmapArr.size() - 1);
        smallArr[1] = bitmapArr.get(bitmapArr.size() - 2);
        smallArr[2] = bitmapArr.get(bitmapArr.size() - 3);

        smallAlbumArr = new String[3];
        smallAlbumArr[0] = albums[albums.length - 2];
        smallAlbumArr[1] = albums[albums.length - 2];
        smallAlbumArr[2] = albums[albums.length - 3];


        bitmapConvert = new Bitmap[bitmapArr.size()];

        for (int x = 0; x < bitmapArr.size(); x++) {
            bitmapConvert[x] = bitmapArr.get(x);
        }
        mostPlayed.setAdapter(new CustomGrid(MainActivity.this, smallArr, smallAlbumArr));
        songListView.setAdapter(new CustomList(MainActivity.this, bitmapConvert, songList));
        Thread thread = new Thread() {
            @Override
            public void run() {
                loadFull();
            }
        };
        thread.start();
        if (!thread.isAlive())
        {
            mostPlayed.setAdapter(new CustomGrid(MainActivity.this, smallArr, smallAlbumArr));
            songListView.setAdapter(new CustomList(MainActivity.this, bitmapConvert, songList));
        }

        //mostPlayed.setAdapter(new CustomGrid(MainActivity.this, smallArr, smallAlbumArr));
        //songListView.setAdapter(new CustomList(MainActivity.this, bitmapConvert, songList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    String[] albums;
    String nextPath = "";
    String[] artists;
    String[] copyArtist;
    String[] songs;
    Bitmap done;

    public String[] getAudioList() {
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Media.ALBUM}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        songs = new String[count];
        copyArtist = new String[count];
        albums = new String[count];
        mAudioPath = new String[count];
        artists = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                albums[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                artists[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                mAudioPath[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                copyArtist[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                final Long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                int duration = mCursor.getInt(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));


                try {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    ParcelFileDescriptor pfd = this.getContentResolver()
                            .openFileDescriptor(uri, "r");

                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                        bitmapArr.add(bm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i == count / 8) {
                    return songs;
                }

                i++;
            } while (mCursor.moveToNext());
        }


        mCursor.close();

        return songs;
    }
    public void loadFull() {
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Media.ALBUM}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        songs = new String[count];
        copyArtist = new String[count];
        albums = new String[count];
        mAudioPath = new String[count];
        artists = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                albums[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                artists[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                mAudioPath[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                copyArtist[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                final Long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                int duration = mCursor.getInt(mCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));


                try {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    ParcelFileDescriptor pfd = this.getContentResolver()
                            .openFileDescriptor(uri, "r");

                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                        bitmapArr.add(bm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                i++;
            } while (mCursor.moveToNext());
        }
        System.out.println("Here!!!!!");
        smallArr = new Bitmap[3];
        smallArr[0] = bitmapArr.get(bitmapArr.size() - 1);
        smallArr[1] = bitmapArr.get(bitmapArr.size() - 2);
        smallArr[2] = bitmapArr.get(bitmapArr.size() - 3);

        smallAlbumArr = new String[3];
        smallAlbumArr[0] = albums[albums.length - 2];
        smallAlbumArr[1] = albums[albums.length - 2];
        smallAlbumArr[2] = albums[albums.length - 3];


        bitmapConvert = new Bitmap[bitmapArr.size()];

        for (int x = 0; x < bitmapArr.size(); x++) {
            bitmapConvert[x] = bitmapArr.get(x);
        }
        mCursor.close();
    }

}


