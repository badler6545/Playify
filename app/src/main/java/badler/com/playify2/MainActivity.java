package badler.com.playify2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
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
    private boolean playingMusic;
    private int tracker;
    int mLastFirstVisibleItem;
    int mLastVisibleItemCount;

    Button songButton;
    Button albumButton;
    Button artistButton;
    Button playlistButton;
    ImageButton playButton;

    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        songButton = (Button) findViewById(R.id.songButton);
        albumButton = (Button) findViewById(R.id.albumButton);
        artistButton = (Button) findViewById(R.id.artistButton);
        playlistButton = (Button) findViewById(R.id.playlistButton);
        playButton = (ImageButton) findViewById(R.id.playbutton);

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
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        playing = true;
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


        bitmapConvert = new Bitmap[bitmapArrFull.size()+100];

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
                    playingMusic = true;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        songButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        albumButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (playingMusic)
                {
                    mMediaPlayer.pause();
                    changeButtonToPause();
                    playingMusic =false;
                }
                else
                {
                    int pausePosition = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(pausePosition);
                    mMediaPlayer.start();
                    changeButtonToPlay();
                    playingMusic=true;

                }
            }
        });
    }


    public void changeButtonToPause()
    {

    }
    public void changeButtonToPlay()
    {

    }
    /*
    <fragment xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools" android:id="@+id/fragmentMain"
            android:name="badler.com.playify2.MainActivityFragment" tools:layout="@layout/fragment_main"
            android:layout_width="match_parent" android:layout_height="320dp"
            android:layout_below="@+id/buttonlayout" />
     */



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

                    /*ParcelFileDescriptor pfd = this.getContentResolver()
                            .openFileDescriptor(uri, "r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inJustDecodeBounds = true;
                    options.inSampleSize = 8;
                    //int scaleFactor = Math.min(options.outWidth/64, options.outHeight/64);
[
                    //options.inJustDecodeBounds=false;
                    //options.inSampleSize=scaleFactor;
                    //options.inPurgeable=true;
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                    bitmapArrFull.add(bm);*/
                    new Thread(new newThread()).start();


                } catch (Exception e) {
                    uriArr.add(null);
                    /*Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.exclamation);
                    bitmapArrFull.add(icon);*/
                    e.printStackTrace();
                }
                i++;

            } while (mCursor.moveToNext());
        }
        mCursor.close();


        return songs;
    }

    //new thread to efficiently load bitmaps
    class newThread implements Runnable{
        @Override
        public void run() {
               try {
                    ParcelFileDescriptor pfd = getApplicationContext().getContentResolver()
                            .openFileDescriptor(uriArr.get(uriArr.size()-1), "r");
                    FileDescriptor fd = pfd.getFileDescriptor();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
                    bitmapArrFull.add(bm);
                } catch (IOException e) {
                   Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.exclamation);
                   bitmapArrFull.add(icon);
                    e.printStackTrace();
                }
            }

    }

}


