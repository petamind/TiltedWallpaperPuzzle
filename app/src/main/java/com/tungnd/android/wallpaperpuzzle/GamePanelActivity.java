package com.tungnd.android.wallpaperpuzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tungnd.android.wallpaperpuzzle.utils.Direction;
import com.tungnd.android.wallpaperpuzzle.utils.Fonts;
import com.tungnd.android.wallpaperpuzzle.widget.HelpDialog;
import com.tungnd.android.wallpaperpuzzle.utils.ImageUtils;
import com.tungnd.android.wallpaperpuzzle.utils.MusicManager;
import com.tungnd.android.wallpaperpuzzle.utils.ThisApp;

import java.security.SecureRandom;
import java.util.ArrayList;


public class GamePanelActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean music;
    private int boardsize;
    private ArrayList<View> tileFlips;
    private int[] logicBoard;
    private int clickCount;
    private int tileCount;
    private boolean won = false;
    private TextView countText;
    private TextView tileText;
    private TextView hintText;

    private ImageButton musicButton;
    private ImageButton setWallButton;
//    private ImageButton helpButton;
    private boolean landscapeMode;
    private int bgID = -1;
    private MediaPlayer mediaPlayer;
    private AudioAttributes audioAttributes;
    private Bitmap[] tileBitmaps;
    private static final SecureRandom random = new SecureRandom();
    private Bitmap thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_panel);
        if (savedInstanceState != null) {
            logicBoard = savedInstanceState.getIntArray("BOARD");
            clickCount = savedInstanceState.getInt("CLICK_COUNT");
            tileCount = savedInstanceState.getInt("TILE_COUNT");
            bgID = savedInstanceState.getInt("BG_ID");
            boardsize = savedInstanceState.getInt("SIZE");
            won = savedInstanceState.getBoolean("WON");
        } else {
            boardsize = getIntent().getIntExtra("SIZE", 3);
        }

        music = ThisApp.getSharedPreferences().getBoolean(getString(R.string.music_on), true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.will_load, Snackbar.LENGTH_LONG)
                        .setAction(getString(android.R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(GamePanelActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                setupBoard();
                                drawBoard();
                            }
                        }).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        countText = (TextView) findViewById(R.id.click_count_text);
        countText.setTypeface(Fonts.STAT_FONT.getFont());
        tileText = (TextView) findViewById(R.id.correct_tiles_text);
        tileText.setTypeface(Fonts.STAT_FONT.getFont());

        hintText = (TextView) findViewById(R.id.info);
        hintText.setTypeface(Fonts.STAT_FONT.getFont());

        musicButton = (ImageButton) findViewById(R.id.music_button);
        musicButton.setOnClickListener(this);

        setWallButton = (ImageButton) findViewById(R.id.setwallpaper_btn);
//        helpButton = (ImageButton) findViewById(R.id.help_button);
        findViewById(R.id.share_button)
                .setOnClickListener(this);

        setupBoard();
        landscapeMode = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    private void updateUI() {
        if (tileBitmaps != null) {
            tileCount = 0;
            for (int i = 0; i < tileBitmaps.length; i++) {
                if (logicBoard[i] == i) {
                    tileCount++;
                }
            }
            Log.d("Parnel", tileCount+"" );
        }
        countText.setText(getString(R.string.clicked) + " " + clickCount);
        tileText.setText(getString(R.string.tile_count) + " " + tileCount);
        if (ThisApp.getSharedPreferences().getBoolean(getString(R.string.music_on), true)) {
            musicButton.setImageResource(R.drawable.ic_music);
            MusicManager.start(this, MusicManager.MUSIC_GAME);
        } else {
            musicButton.setImageResource(R.drawable.ic_music_off);
            MusicManager.pause();
        }
        if (won) {
            setWallButton.setVisibility(View.INVISIBLE);
        } else {
            setWallButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logicBoard = savedInstanceState.getIntArray("BOARD");
        clickCount = savedInstanceState.getInt("CLICK_COUNT");
        tileCount = savedInstanceState.getInt("TILE_COUNT");
        boardsize = savedInstanceState.getInt("SIZE");
        bgID = savedInstanceState.getInt("BG_ID");
        won = savedInstanceState.getBoolean("WON");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("BOARD", logicBoard);
        outState.putInt("SIZE", boardsize);
        outState.putInt("CLICK_COUNT", clickCount);
        outState.putInt("TILE_COUNT", tileCount);
        outState.putInt("BG_ID", bgID);
        outState.putBoolean("WON", won);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            tileFlips.clear();
            drawBoard();
        }
    }

    /**
     * Clear array to settup new board
     */
    private void setupBoard() {
        if (won) {
            bgID = -1;
            setWallButton.setVisibility(View.INVISIBLE);
        }
        this.won = false;
        this.clickCount = 0;
        this.tileCount = 0;
        this.logicBoard = new int[boardsize * boardsize];
        try {
            setboardLogic(logicBoard);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///------------------------------------------

        if (tileFlips == null) {
            tileFlips = new ArrayList<>();
        } else {
            tileFlips.clear();
        }
        //drawBoard();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(GamePanelActivity.this, R.raw.blop_sound);
        //mediaPlayer.setVolume(1, 1);
        if (audioAttributes == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    /**
     * draw the tiles in the board based on logic matrix
     */
    private void drawBoard() {
        final ViewGroup board = (ViewGroup) findViewById(R.id.board);

        try {
            board.removeAllViews();
            board.setBackgroundDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        final int width = Math.min(((ViewGroup) board.getParent()).getWidth(), ((ViewGroup) board.getParent()).getHeight());
        board.setMinimumWidth(width - 4);
        board.setMinimumHeight(width - 4);
        final LayoutInflater layoutInflater = getLayoutInflater();
        final TypedArray hdBG = getResources().obtainTypedArray(R.array.hd_background_list);

        //------------------------prepare the image
        if (bgID == -1) {
            bgID = hdBG.getResourceId(random.nextInt(hdBG.length()), R.drawable.hd_wall_1);
        }

        Bitmap boardBg = ImageUtils.getFitBitmapSquare(this, bgID, width);
        this.thumbnail = ImageUtils.resize(boardBg, width/3, width/3);

        //-------------------------------------------------------------
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            board.setBackground(new BitmapDrawable(getResources(), boardBg));
        } else {
            board.setBackgroundDrawable(new BitmapDrawable(getResources(), boardBg));
        }*/
        //--------------------------------------------
        if (!won) {
            tileBitmaps = ImageUtils.cutBitmapToTiles(boardBg, boardsize, boardsize);
            for (int i = 0; i < boardsize; i++) {
                LinearLayout boardrow = (LinearLayout) getLayoutInflater().inflate(R.layout.board_row, board, false);
                for (int j = 0; j < boardsize; j++) {
                    View view = layoutInflater.inflate(R.layout.tile, boardrow, false);//.findViewById(R.id.tile);
                    view.setMinimumHeight(width / boardsize - 2);
                    view.setMinimumWidth(width / boardsize - 2);
                    if (logicBoard[i * boardsize + j] == -1) {
                        //empty cell no bg
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(ImageUtils.cutBitmapToRoundTile(this
                                , tileBitmaps[logicBoard[i * boardsize + j]]
                                , width / boardsize / 10.0f));
                    } else {
                        view.setBackgroundDrawable(ImageUtils.cutBitmapToRoundTile(this
                                , tileBitmaps[logicBoard[i * boardsize + j]]
                                , width / boardsize / 10.0f));
                    }

                    view.setId(i * boardsize + j);
                    view.setTag(logicBoard[i * boardsize + j]);

                    view.setOnClickListener(this);
                    boardrow.addView(view);

                    tileFlips.add(view);
                }
                board.addView(boardrow);
            }
        } else {
            setWallButton.setVisibility(View.VISIBLE);
            setBackground(board, boardBg);
        }

//        root.addView(board);
    }

    private void setBackground(View v, Bitmap drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(new BitmapDrawable(getResources(), drawable));
        } else {
            v.setBackgroundDrawable(new BitmapDrawable(drawable));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    /**
     * setup board logic
     */
    private void setboardLogic(int[] logicBoard) throws Exception {
        for (int i = 0; i < logicBoard.length; i++) {
            logicBoard[i] = i;
        }
        logicBoard[logicBoard.length - 1] = -1;//empty cell stup
        for(int i = 0; i<boardsize; i++){
            moveLogicalTile(logicBoard, Direction.UP);
            moveLogicalTile(logicBoard, Direction.LEFT);
        }

        for (int j = 0; j < boardsize * boardsize * boardsize * 2+1; j++) {//odd number to make sure there is no solved board
            moveLogicalTile(logicBoard, Direction.getRandomDirection());
        }
    }

    /**
     * Find location of empty cell and move it to other place using direction
     * <p/>
     * (swap empty tile with its adjacent tile direction)
     *
     * @param direction
     */
    private boolean moveLogicalTile(int[] logicBoard, Direction direction) throws Exception {
        //find empty cell id
        int emptyCellID = findEmptyCell(logicBoard);

        int row = emptyCellID / boardsize;
        int col = emptyCellID % boardsize;
        switch (direction) {
            case DOWN:
                if (row < boardsize - 1) {
                    row++;
                } else {
                    return false;
                }
                break;
            case LEFT:
                if (col > 0) {
                    col--;
                } else {
                    return false;
                }
                break;
            case UP:
                if (row > 0) {
                    row--;

                } else {
                    return false;
                }
                break;
            case RIGHT:
                if (col < boardsize - 1) {
                    col++;

                } else {
                    return false;
                }
                break;
        }
        logicBoard[emptyCellID] = logicBoard[row * boardsize + col];
        logicBoard[row * boardsize + col] = -1;

        return true;
    }

    private int findEmptyCell(int[] logicBoard) throws Exception {
        for (int i = 0; i < logicBoard.length; i++) {
            if (logicBoard[i] < 0) {
                return i;
            }
        }

        throw new Exception("Cannot find empty cell");
    }


    /**
     * Called when a view has been clickCount.
     *
     * @param v The view that was clickCount.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_button:
                music = !music;
                ThisApp.getSharedPreferences().edit().putBoolean(getString(R.string.music_on), music).commit();
                updateUI();
                return;
            case R.id.share_button:
                ThisApp.share(this);
                return;
            case R.id.setwallpaper_btn:
                if (won) {
                    setWallpaper();
                }
                return;
            case R.id.correct_tiles_text:
            case R.id.click_count_text:
                new HelpDialog(this, R.style.DialogFloatFullScreen, HelpDialog.MODE.THUMBNAIL_MODE, thumbnail).show();
                return;
        }


        if (!won) {
            mediaPlayer.start();
            testMoveTile(v);
            updateUI();
        } else {
            showWinDialog();
        }
    }

    /**
     * for testing method
     */
    private void testMoveTile(View v) {
        int emptyID = movable(v);
        System.out.println("empty cell: " + emptyID);
        if (emptyID >= 0) {
            clickCount++;
            View emptyCell = findViewById(emptyID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                emptyCell.setBackground(v.getBackground());
            } else {
                emptyCell.setBackgroundDrawable(v.getBackground());
            }
            v.setBackgroundDrawable(null);

            logicBoard[emptyID] = logicBoard[v.getId()];
            logicBoard[v.getId()] = -1;
            if (isWin()) {
                showWinDialog();
            }
            ;
        } else {
            Toast.makeText(this, R.string.cannot_move, Toast.LENGTH_SHORT).show();
        }
        //printBoard();
    }

    private void printBoard() {
        for (int i = 0; i < boardsize; i++) {
            for (int j = 0; j < boardsize; j++) {
                System.out.print(logicBoard[i * boardsize + j] + "\t");
            }
            System.out.println();
        }
        System.out.println("--------------------");
    }

    /**
     * check if v is adjacent to empty cell
     *
     * @param v
     * @return id of empty cell to move to<br/> -1 means not found empty cell around
     */
    private int movable(View v) {
        int row = v.getId() / boardsize;
        int col = v.getId() % boardsize;
        if (row > 0 && logicBoard[(row - 1) * boardsize + col] == -1)
            return (row - 1) * boardsize + col;
        if ((row < boardsize - 1) && logicBoard[(row + 1) * boardsize + col] == -1)
            return (row + 1) * boardsize + col;
        if (col > 0 && logicBoard[row * boardsize + col - 1] == -1)
            return row * boardsize + col - 1;
        if (col < boardsize - 1 && logicBoard[row * boardsize + col + 1] == -1)
            return row * boardsize + col + 1;
        return -1;
    }

    private void setWallpaper() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.willsetwall_title)
                .setIcon(android.R.drawable.ic_menu_gallery)
                .setMessage(R.string.willsetwall)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ThisApp.setWallPaper(GamePanelActivity.this
                                , bgID
                                , (float) (ThisApp.BACKGROUND_SCALE_FACTOR + (boardsize > 4 ? 0 : 32.0f / Math.pow(2, boardsize))));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void showWinDialog() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(GamePanelActivity.this, R.raw.magic_sound);
        mediaPlayer.start();

        if (tileFlips.size() != 0) {
            for (int i = 0; i < tileFlips.size(); i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        View v = tileFlips.remove(0);
                        RoundedBitmapDrawable bg = (RoundedBitmapDrawable) v.getBackground();
                        if (bg != null) {
                            //bg.getBitmap().recycle();
                        }
                    }
                }).start();
            }
            //tileFlips.clear();

            System.gc();
            new HelpDialog(this, R.style.DialogFloatFullScreen, HelpDialog.MODE.WIN_MODE).show();
        } else {

        }
    }

    private void flipNeighbour(View v) {
        int id = v.getId();
        int row = id / boardsize;
        int col = id % boardsize;

        final ArrayList<View> neighbor = new ArrayList<>();
//        System.out.println(row + "----" + col);
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if (r < 0 || c < 0 || c >= boardsize || r >= boardsize) {
                    continue;
                } else {
                    if (c != col || r != row) {
                        neighbor.add(tileFlips.get(r * boardsize + c));
//                        System.out.println(r + "-" + c);
                    }
                }
            }
        }
        int i = 0;
        for (View f : neighbor
                ) {
//            f.startAnimation(f.getFlipAnimation());

        }
        if (isWin()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showWinDialog();

                }
            }, 1000);
        }
        ;
    }

    private boolean isWin() {
        for (int i = 0; i < logicBoard.length - 1; i++) {
            if (logicBoard[i] != i) {
                return false;
            }
        }
        won = true;
        return won;
    }

    private int[] getIndice(int id) {
        return new int[]{id / boardsize, id % boardsize};
    }
}
