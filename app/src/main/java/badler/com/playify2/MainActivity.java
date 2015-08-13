package badler.com.playify2;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
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
    private ArrayList<Bitmap> bitmapArrFull;
    private Bitmap[] bitmapConvert;
    private Bitmap[] bitmapArr2;
    Bitmap[] smallArr;
    private String[] smallAlbumArr;
    private Handler handler;
    private String[] songList;
    private GridView mostPlayed;
    private ListView songListView;
    private ArrayList<FileDescriptor> fdArr;
    private ArrayList<Uri> uriArr;
    private int tracker;
    int mLastFirstVisibleItem;
    int mLastVisibleItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmapArr = new ArrayList<Bitmap>();
        fdArr = new ArrayList<FileDescriptor>();
        bitmapArrFull = new ArrayList<Bitmap>();
        uriArr=new ArrayList<Uri>();
        // = new ArrayList<String>();

        mostPlayed = (GridView) findViewById(R.id.gridView);
        songListView = (ListView) findViewById(R.id.songListView);
        setUpLayout();




    }

    boolean playing;
    final MediaPlayer mMediaPlayer = new MediaPlayer();

    private void playSong(String path) throws IllegalArgumentException,
            IllegalStateException, IOException
    {
        Log.d("ringtone", "playSong :: " + path);
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(path);
//mMediaPlayer.setLooping(true);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        playing = true;
        /*if(playing)
        {
            changePlayButton2();
        }*/
    }


    public void setUpLayout() {
        songList = getAudioList();


        smallArr = new Bitmap[3];
        smallArr[0] = bitmapArrFull.get(bitmapArrFull.size() - 1);
        smallArr[1] = bitmapArrFull.get(bitmapArrFull.size() - 2);
        smallArr[2] = bitmapArrFull.get(bitmapArrFull.size() - 3);

        smallAlbumArr = new String[3];
        smallAlbumArr[0] = albums[albums.length - 1];
        smallAlbumArr[1] = albums[albums.length - 2];
        smallAlbumArr[2] = albums[albums.length - 3];


        bitmapConvert = new Bitmap[bitmapArrFull.size()];

        for (int x = 0; x < bitmapArrFull.size(); x++) {
            bitmapConvert[x] = bitmapArrFull.get(x);
        }
        mostPlayed.setAdapter(new CustomGrid(MainActivity.this, smallArr, smallAlbumArr));

        songListView.setAdapter(new CustomList(MainActivity.this, bitmapConvert, songList));
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Add new on click listener

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    System.out.print("Hello!");
                    playSong(mAudioPath[arg2]);
                    tracker = arg2;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
                    uriArr.add(uri);
                    ParcelFileDescriptor pfd = this.getContentResolver()
                            .openFileDescriptor(uri, "r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                    bitmapArrFull.add(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return songs;
    }

}


