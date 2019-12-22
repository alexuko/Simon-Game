package com.example.simongame;
/**
 * Imports
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * CustomView class will extend View class an it is necessary for the creation of customized view
 * this class will create our Simon game panel
 *
 * @author  Alejandro Rivera
 * @version 1.0
 * @since   2019-11-24
 */
public class CustomView extends View {
    // variables
    private static final String TAG = "CustomView";
    private Paint red, green, blue, yellow;
    private Rect square_red,square_green,square_blue,square_yellow;
    private MainActivity mainActivity = (MainActivity) this.getContext();
    private int alphaValue = 125;
    private boolean gameStart;
    private int gameMode;
    private int playerNumber;
    private int level;
    private int speedInMillis = 1000;
    private boolean isTimerRunning;
    private Random random;
    private static final long START_TIME_IN_MLLSEC = 10000;
    private long timeLeft = START_TIME_IN_MLLSEC;
    private CountDownTimer countDownTimer;
    private ArrayList<Integer> arrListSequence = new ArrayList<Integer>();
    private int currentCard;
    private int current_sequence_index;
    private int player_sequence_index;


    /**
     * CustomView first constructor will be used when initialising your CustomView entirely
     * through Java code with no XML interaction
     * @param context allows access to application-specific resources and classes
     */
    public CustomView(Context context) {
        super(context);
        init();
    }

    /**
     * The second constructor  form should parse and apply
     * any attributes defined in the layout file.
     * @param context allows access to application-specific resources and classes
     * @param attrs set of attributes
     */
    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * CustomView third constructor will be used if the view has been declared in an XML file with a
     * set of attributes and also a style attribute set.
     * @param context allows access to application-specific resources and classes
     * @param attrs set of attributes
     * @param defStyleAttr defined set of attributes
     */
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     *
     * All three constructors will share the same init initialisation.
     * Here we will initialize the 4 squares and assign them a color
     */
    public void init(){
        // create the paint objects for rendering our rectangles
        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        blue = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellow = new Paint(Paint.ANTI_ALIAS_FLAG);

        red.setColor(getResources().getColor(R.color.color_Red_secondary));
        green.setColor(getResources().getColor(R.color.color_Green_secondary));
        blue.setColor(getResources().getColor(R.color.color_Blue_secondary));
        yellow.setColor(getResources().getColor(R.color.color_Yellow_secondary));

    }

    /**
     *
     * This method  will draw the contents of your widget using a canvas
     * here we will draw our 4 different color squares assigning them colors and position on the canvas
     * using width and height
     * @param canvas area to draw
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        square_red = new Rect(0, 0, width/2, height/2);
        square_green = new Rect(width/2, 0, width, height/2);
        square_blue = new Rect(0, height/2, width/2, height);
        square_yellow = new Rect(width/2, height/2, width, height);

        canvas.drawRect(square_red,red);
        canvas.drawRect(square_green,green);
        canvas.drawRect(square_blue,blue);
        canvas.drawRect(square_yellow,yellow);

    }

    /**
     *
     * Method will check firstly if there is a game taking place otherwise it wont allow any touch events to happen
     * <p>Every card color has an area that will be specified by the X and Y coordinates</p>
     *
     * <p>ACTION_DOWN will play a sound for the selected card and dim the color, so it is visible for the user and changes the value of the touchedCardCode variable
     * depending on the card touched.
     * Once the card was selected we check the gameMode 1 or 2 for single player or 2 players respectively </p>
     *
     * <p>ACTION_UP will restore the card colors to their original values with the method restoreCardsState() once the uses lifts the finger from the screen </p>
     *
     * <h4>Single GameMode</h4>
     * <p>If game mode is for a single player, the player will have to mimic each sequence of cards that simon displayed on the control previously
     * player will keep pressing the cards until the number of pressed cards is equals that the size of the arrListSequence which stores the a sequence of touches.
     * a level is incremented in 1 every time that a sequence is completed successfully.
     * Once the sequence is equals to array size, we will increment the speed depending on the level that the player has reached/.
     * displaying the new level on the TextView level restoring timer to original values and switching turn with simon
     * if player wrong pressed a card a method playerLose() will be executed to show player that he/she has lost</p>
     * <h4>2 Players mode</h4>
     * <p> To get into this part of the code we need to be On 2 players mode, then we will check which player has the control
     * of the panels, the very beginning It will be player 1 who will start with the first press card, to begin with,
     * the sequence of movements. Once he has touched a card the control will be passed to player 2 setting up a timer for Player 2.
     * Player 2 will try to mimic the sequence with every touch, once the sequence has been completed successfully Player to will press
     * another card of his choice incrementing the array size by one and timer for player 2 will stop, and will pass the control of the panels
     * to player 1 who will do exactly the same a player 2 until one of them fails, here there is no max level to reach.  </p>
     *
     * @param event takes in a MotionEvent.ACTION_DOWN or MotionEvent.ACTION_UP
     * @return true after the event happens
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        int touchedCardCode = 0;
        //check if there is a game taking place
        if(gameStart){
            int action = event.getActionMasked();
            // get coordinates
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch(action) {

                case (MotionEvent.ACTION_DOWN) :
                    Log.d(TAG,"Action was DOWN");
                    if(x >= 0 && x <= 400 && y >=0 && y <= 400){
                        Log.d(TAG, "Red Square: " + x + " " + y);
                        playSound("red");
                        red.setAlpha(alphaValue);
                        touchedCardCode = 1;

                    }else if(x > 400 && x <= 800 && y >=0 && y <= 400){
                        Log.d(TAG, "Green Square: " + x + " " + y);
                        playSound("green");
                        green.setAlpha(alphaValue);
                        touchedCardCode = 2;
                    }else if(x > 0 && x <= 400 && y >400 && y <= 800){
                        Log.d(TAG, "Blue Square: " + x + " " + y);
                        playSound("blue");
                        blue.setAlpha(alphaValue);
                        touchedCardCode = 3;
                    }else if(x > 400 && x <= 800 && y >400 && y <= 800){
                        Log.d(TAG, "Yellow Square: " + x + " " + y);
                        playSound("yellow");
                        yellow.setAlpha(alphaValue);
                        touchedCardCode = 4;
                    }

                    invalidate();

                    //Players Game Logic
                        //one player mode = 1

                        if(gameMode == 1){
                            Log.d(TAG, "1 player mode: " + gameMode);
                            currentCard = arrListSequence.get(player_sequence_index);

                            if(player_sequence_index < arrListSequence.size()){
                                Log.d(TAG, "player_sequence_index < arrListSequence.size()" +player_sequence_index + " "+ arrListSequence.size());
                                //check if the press card number matches with the number in the sequence
                                if(touchedCardCode == currentCard){
                                    Log.d(TAG, "touch card matches: ");
                                    player_sequence_index++;
                                    if(player_sequence_index == arrListSequence.size()){
                                        pausedTimer();
                                        //stop it for 750 millisecond  so user can see simon first flashed card
                                        SystemClock.sleep(750);
                                        level++;
                                        //increment speed depending on the current level
                                        if      (level == 3){
                                            speedInMillis = 650;
                                        }else if(level == 6){
                                            speedInMillis = 500;
                                        }else if(level == 9){
                                            speedInMillis = 350;
                                        }else if(level == 12){
                                            speedInMillis = 250;
                                        }else if(level == 15){
                                            speedInMillis = 150;
                                        }
                                        update_Tv_Level(level);
                                        resetTimer();
                                        simonTurn();
                                        player_sequence_index = 0;
                                    }
                                }else{
                                    //if the press card number does NOT match with the number in the sequence
                                    playerLose(1);
                                }

                            }

                        //two player mode = 2
                        }else if(gameMode == 2){
                            Log.d(TAG, "2 player mode: " + gameMode);

                            if(playerNumber == 1){
                                Log.d(TAG, "Player 1 touched card --> " + touchedCardCode);
                                Log.d(TAG, "player_sequence_index < arrListSequence.size() "+player_sequence_index+" "+arrListSequence.size());
                                //if it is the first touch of the game
                                if(arrListSequence.isEmpty()){
                                    pausedTimer();
                                    resetTimer();
                                    Log.d(TAG, "arrListSequence.isEmpty()"+arrListSequence.size());
                                    //add pressed card number to the sequence
                                    arrListSequence.add(touchedCardCode);
                                    //change to player 2
                                    playerNumber = 2;
                                    update_Tv_Player("PLAYER " + playerNumber);
                                    mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Yellow_secondary));
                                    //start timer
                                    startTimer();


                                }else if(player_sequence_index < arrListSequence.size() && touchedCardCode == arrListSequence.get(player_sequence_index)){
                                    Log.d(TAG, "plySeq, arrSize, touchedCard, current card: "+ player_sequence_index + arrListSequence.size() + touchedCardCode + arrListSequence.get(player_sequence_index));
                                    //increment by 1 our sequence so we can check the next card
                                    player_sequence_index++;

                                }else if(player_sequence_index == arrListSequence.size()){
                                    pausedTimer();
                                    //increment level and display it
                                    level ++;
                                    update_Tv_Level(level);
                                    //add number to the array
                                    arrListSequence.add(touchedCardCode);
                                    resetTimer();
                                    //change to player 1
                                    playerNumber = 2;
                                    //reset player_sequence_index
                                    player_sequence_index = 0;
                                    //change to player 2
                                    update_Tv_Player("PLAYER " + playerNumber);
                                    mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Yellow_secondary));
                                    //start timer
                                    startTimer();

                                }else {
                                    playerLose(1);

                                }

                            }else if(playerNumber ==2){
                                Log.d(TAG, "Player 2 touched card --> " + touchedCardCode);
                                Log.d(TAG, "player_sequence_index < arrListSequence.size() "+player_sequence_index+" "+arrListSequence.size());

                                if(player_sequence_index < arrListSequence.size() && touchedCardCode == arrListSequence.get(player_sequence_index)){
                                    Log.d(TAG, " plySeq < arrSize, touchedCard, current card: "+ player_sequence_index + arrListSequence.size() + touchedCardCode + arrListSequence.get(player_sequence_index));
                                    //increment by 1 our sequence so we can check the next card
                                    player_sequence_index++;

                                    //if all touches were successful
                                }else if(player_sequence_index == arrListSequence.size()){
                                    pausedTimer();
                                    resetTimer();
                                    //increment level and display it
                                    level ++;
                                    update_Tv_Level(level);
                                    //add number to the array
                                    arrListSequence.add(touchedCardCode);
                                    //change to player 1
                                    playerNumber = 1;
                                    //reset player_sequence_index
                                    player_sequence_index = 0;
                                    //change to player 2
                                    update_Tv_Player("PLAYER " + playerNumber);
                                    mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_white));
                                    startTimer();

                                }else {
                                    playerLose(2);

                                }
                            }

                        }


                    return true;

                case (MotionEvent.ACTION_UP) :
                    Log.d(TAG,"Action was UP");
                    restoreCardsState();
                    return true;

            }
        }else{
            return false;
        }

        return super.onTouchEvent(event);

    }

    /**
     * Method executed when a player did not press the right panel, takes in a player number.
     * plays a sound that makes the user aware that has lost, highlighting the name in a red color
     * and making the mode 2 players button active again.
     * pauses timer first and then reset it
     * @param playerNum number of the current player
     */
    private void playerLose(int playerNum){
        Log.d(TAG, "WRONG CARD: ");
        pausedTimer();
        //play a lose sound
        playSound("lose");
        //update the player name
        update_Tv_Player("PLAYER "+playerNum+" LOSE");
        Toast.makeText(getContext(), "PLAYER "+ playerNum + " LOSE", Toast.LENGTH_LONG).show();
        mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Red_secondary));
        //set button back to be active
        if(gameMode == 1){
            mainActivity.getMode1().setEnabled(true);
            mainActivity.getMode1().setText("1 PLAYER MODE");
        }else if(gameMode == 2){
            mainActivity.getMode2().setEnabled(true);
            mainActivity.getMode2().setText("2 PLAYER MODE");
        }
        //freeze the main cards panel
        gameStart = false;
        //reset timer
        resetTimer();
    }

    /**
     * Method starts the game so the user can touch the panel
     * @param gameStart take in a boolean true when there is a active game, and false otherwise
     */
    public void setGameStart(boolean gameStart) {
        Log.d(TAG, "setGameStart: " + gameStart);
        this.gameStart = gameStart;
    }

    /**
     * Method restores the card colors to their original full color value
     */
    public void restoreCardsState(){
        red.setAlpha(255);
        green.setAlpha(255);
        blue.setAlpha(255);
        yellow.setAlpha(255);
        invalidate();
    }

    /**
     * creates a new random instance and returns a random number
     * @return a random number between 1 and 4
     */
    public int genRandomNum(){
        random = new Random();
        return random.nextInt(4) + 1;
    }

    /**
     * sets the text of the current player in the UI
     * to its new name value passed
     * @param name take a string as the new name
     */
    private void update_Tv_Player(String name) {
        mainActivity.getCurrent_player().setText(name);

    }

    /**
     * sets the text of the currentLevel in the UI
     * to its new level value
     * @param name takes in an int as the new level
     */
    private void update_Tv_Level(int level) {
        mainActivity.getCurrLevel().setText("LEVEL: "+ level);
    }

    /**
     * Method flashes a card depending on the parameter cardNumber that has been passed.
     * <p>A card wil be set with setAlpha(alphaValue) then card will return to its original full color value after a time specified on the variable speedInMillis</p>
     * <p>For this to happen Method a handler with a postDelayed and a runnable method are necessary to make it visible to the user as without them
     * the code executes so fast that the human eye cannot see the flashing card</p>
     * @param cardNumber a card color from 1 - 4 {red, green, blue, and yellow}
     */
    public void flashCard(final int cardNumber){
        Log.d(TAG, "flashCard: ");
        final Handler flash_Card_handler = new Handler();

        if(cardNumber == 1){
            playSound("red");
            red.setAlpha(alphaValue);
        }else if(cardNumber == 2){
            playSound("green");
            green.setAlpha(alphaValue);
        }else if(cardNumber == 3){
            playSound("blue");
            blue.setAlpha(alphaValue);
        }else if(cardNumber == 4){
            playSound("yellow");
            yellow.setAlpha(alphaValue);
        }
        invalidate();

        final Runnable runnable =new Runnable() {
            @Override
            public void run() {
                if(cardNumber == 1){
                    red.setAlpha(255);
                }else if(cardNumber == 2){
                    green.setAlpha(255);
                }else if(cardNumber == 3){
                    blue.setAlpha(255);
                }else if(cardNumber == 4){
                    yellow.setAlpha(255);
                }
                invalidate();
            }
        };
        flash_Card_handler.postDelayed(runnable,speedInMillis);

    }

    /**
     * Method plays a sequence of numbers automatically.
     * Starting by setting the current player name to be simon, and disabling the cards panel, thus the player cannot touch it
     * while simon is playing, level of the game is update on the UI TextView, and it checks if the winning level has been reached.
     * if it does! current player is notified by highlighting the name in blue, timer will stop as well as the cards panel and method will return, so there is nothing
     * else to do, otherwise
     * <p>A sequence of numbers stored in arrListSequence will be played by simon if any. And at the end of the sequence simon will add another random movement to
     * the sequence at a specified speed (speedInMillis) depending on the level that the player has reached and using the method flashCard() to show last movement</p>
     * <p>A handler with a postDelayed will be implemented so the user can see the flashing between cards
     * a card will dim with setAlpha taking it form alphaValue global variable and returning to its original full color value (255)
     * once simon has completed the sequence, it enables the user to play the by touching the panels (gameStart = true)
     * current player TextView will change to Player 1 and a timer will be set.
     * current_sequence_index will be set to 0, so next sequence will be read from the beginning
     *
     */
    public void simonTurn(){
        Log.d(TAG, "simonTurn: ");
        update_Tv_Player("SIMON");
        mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Yellow_secondary));

        Log.d(TAG, "array size: " +  arrListSequence.size());
        //when simon is playing user cannot touch the cards
        gameStart = false;
        update_Tv_Level(level);
        //check if the user reach the winning level = 18
        if(level == 18){
            Log.d(TAG, "touch card matches and player wins");
            player_sequence_index = 0;
            gameStart = false;
            update_Tv_Player("PLAYER WINS");
            mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Blue_secondary));
            Toast.makeText(getContext(), "PLAYER WINS", Toast.LENGTH_LONG).show();
            pausedTimer();
            mainActivity.getMode1().setVisibility(View.VISIBLE);
            //leave method
            return;

        }

        final Handler handler = new Handler();  // create a handler to link to our runnable
        handler.post(new Runnable() {           // create the runnable
            @Override
            public void run() {
                Log.i(TAG, "current_sequence_index:" + current_sequence_index);

                red.setAlpha(255);
                green.setAlpha(255);
                blue.setAlpha(255);
                yellow.setAlpha(255);
                invalidate();

                if(current_sequence_index < arrListSequence.size()) {  // if there are more elements in the sequence to process
                    int currentPanel = arrListSequence.get(current_sequence_index);
                    if (currentPanel == 1) {                // if the current panel of the sequence is 1
                        Log.d(TAG, "set alpha red card:  50");
                        red.setAlpha(alphaValue);               // set red be transparent
                        playSound("red");
                    } else if (currentPanel == 2)  {          // if the current panel of the sequence is 2
                        Log.d(TAG, "set alpha green card:  50");
                        green.setAlpha(alphaValue);
                        playSound("green");
                    }else if(currentPanel==3) {           // if the current panel of the sequence is 2
                        Log.d(TAG, "set alpha blue card:  50");
                        blue.setAlpha(alphaValue);
                        playSound("blue");
                    }else if(currentPanel==4) {          // if the current panel of the sequence is 2
                        Log.d(TAG, "set alpha yellow card:  50");
                        yellow.setAlpha(alphaValue);
                        playSound("yellow");
                    }

                    current_sequence_index++;
                    handler.postDelayed(this, speedInMillis); // execute this runnable again
                }else if(current_sequence_index == arrListSequence.size()){
                    //once simon has completed to play the previous numbers, It does add nother to the list and flashes the card
                    int myRandNumb = genRandomNum();
                    Log.d(TAG, "myRandNumb: " + myRandNumb);
                    arrListSequence.add(myRandNumb);
                    playerNumber = 1;
                    update_Tv_Player("PLAYER " + playerNumber);
                    mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_white));
                    //allow user to touch the panels
                    gameStart = true;
                    flashCard(myRandNumb);

                    startTimer();

                }
            }
        });
        current_sequence_index=0; // reset the index back to 0 so the sequence can be played again


    }

    /**
     * Method used to play a sound once a view has been touched or game has started, takes in a String as required sound that the MediaPlayer will use.
     * Solfège sounds for the different card panels are used, plus a sound when the game starts and  when a player lose
     * @param sound String required sound to match the required sound to be played
     */
    public void playSound(String sound){
        //function that play sound according to sound String name
        int audioRes = 0;
        switch (sound) {
            case "red":
                audioRes = R.raw.fa;
                break;
            case "green":
                audioRes = R.raw.mi;
                break;
            case "blue":
                audioRes = R.raw.si;
                break;
            case "yellow":
                audioRes = R.raw.sol;
                break;
            case "lose":
                audioRes = R.raw.lose;
                break;
            case "start":
                audioRes = R.raw.game_start;
                break;
        }
        MediaPlayer p = MediaPlayer.create(getContext(), audioRes);
        p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        p.start();

    }

    /**
     * Method reset all of the variables needed to start a new game to their original values, so another game can take place
     * if a timer is running it pause and reset it and set buttons to their original state.
     */
    public void resetValues(){
        //if the timer is running then stop it and reset it
        if(isTimerRunning){
            pausedTimer();
            resetTimer();
        }
        if(gameMode == 1){
            mainActivity.getMode1().setEnabled(true);
            mainActivity.getMode1().setText("1 PLAYER MODE");
        }else if(gameMode == 2){
            mainActivity.getMode2().setEnabled(true);
            mainActivity.getMode2().setText("2 PLAYER MODE");
        }
        restoreCardsState();
        level = 0;
        mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_white));
        speedInMillis = 1000;
        player_sequence_index = 0;
        current_sequence_index = 0;
        arrListSequence.clear();
        update_Tv_Level(0);

    }

    /**
     * Method will take 1 or 2 as their possible parameters as there are only 2 game modes and
     * this will be necessary fot the onTouchEvent to identify who is the current player using the card panels
     * @param gameModeCode parameter needed to to identify the game mode
     */
    public void setGameMode(int gameModeCode) {
        if(gameModeCode == 1){
            Log.d(TAG, "Player VS Simon ");
            //clear variables, array of movements and text views
        }else if(gameModeCode == 2){
            Log.d(TAG, "Player_1 VS Player_2 ");
        }
        this.gameMode = gameModeCode;
    }

    /**
     * Method will tell android how big the CustomView will be depending on the constrains of the parent
     * <p>EXACTLY means the layout_width or layout_height value was set to a specific value.</p>
     * <p>AT_MOST means the layout_width or layout_height value was set to match_parent or wrap_content where a maximum size is needed</p>
     * <p>setMeasuredDimension() is called at the end with the size I want the view to be in this case 800</p>
     * <p>we will get the widthSize and heightSize and check which widthMode and heightMode is being used, set the min desire size
     * to 800, so the view will be always a square</p>
     * @param widthMeasureSpec parameter specified on the activity_main.xml layout_width
     * @param heightMeasureSpec parameter specified on the activity_main.xml layout_height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //set the min desire size
        int size = 800;
        //int variables to store width and height
        int width;
        int height;

        //int variables get width and height
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than
            width = Math.min(size, widthSize);
        } else {
            //whatever I want
            width = size;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than
            height = Math.min(size, heightSize);
        } else {
            //Be whatever size I want
            height = size;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);


    }

    /**
     * Method starts the timer, uses a handler to start the timer with a postDelay of 500 milliseconds
     * this is a small pause before the counter starts. It takes a runnable as a param that contains
     * the method that will be executed and the milliseconds that it will wait
     * a CountDownTimer will be used for the execution of the method, every 1 sec or 1000 milliseconds
     * the clock will tick until it reaches 0 and then check current player using the timer
     * It will disable the main cards panel so cannot be touched after someone has lose,
     * sets to false isTimerRunning and make the appropriate button visible
     */
    public void startTimer(){
        Log.i("Timer","StartTimer()");
        mainActivity.getTimer().setText("10");

        final Handler myTimeHandler = new Handler();

        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                mainActivity.getTimer().setText("10");
                Log.d(TAG, "timer running : ");
                countDownTimer = new CountDownTimer(timeLeft,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft = millisUntilFinished;
                        update_Tv_Timer();
                    }

                    @Override
                    public void onFinish() {
                        if(playerNumber == 1){
                            mainActivity.getCurrent_player().setText("PLAYER 1 LOSE");
                        }
                        else if(playerNumber == 2){
                            mainActivity.getCurrent_player().setText("PLAYER 2 LOSE");
                        }
                        mainActivity.getCurrent_player().setTextColor(getResources().getColor(R.color.color_Red_secondary));
                        Toast.makeText(getContext(), "PLAYER "+ playerNumber + " LOSE", Toast.LENGTH_LONG).show();
                        //disable panel
                        gameStart = false;
                        //timer will stop running
                        isTimerRunning = false;
                        //make the button Active
                        if(gameMode == 1){
                            mainActivity.getMode1().setEnabled(true);
                            mainActivity.getMode1().setText("1 PLAYER MODE");
                        }else if(gameMode == 2){
                            mainActivity.getMode2().setEnabled(true);
                            mainActivity.getMode2().setText("2 PLAYER MODE");
                        }
                        playSound("lose");
                        resetTimer();
                    }
                }.start();
                isTimerRunning = true;

            }
        };
        //waits for a 500 milliseconds to execute the timer
        myTimeHandler.postDelayed(timerRunnable,500);
    }

    /**
     * Method sets all of the timer values to its original values
     * timeLeft its 10 secs again, the TextView timer is set to 10,
     * isTimerRunning is set to false and we call the update_Tv_Timer method to
     * display the correct values in the UI.
     */
    public void resetTimer(){
        Log.i(TAG,"ResetTimer()");
        //reset time left to 10 seconds
        timeLeft = START_TIME_IN_MLLSEC;
        mainActivity.getTimer().setText("10");
        isTimerRunning = false;
        update_Tv_Timer();

    }

    /**
     * Method sets to false the timer, and cancel its execution
     * freezing the timer
     */
    public void pausedTimer(){
        Log.i(TAG,"PauseTimer()");
        isTimerRunning = false;
        countDownTimer.cancel();

    }

    /**
     *  Method updates the time after every second, a format of 2 single numbers is used "00"
     * for the timer instead of the traditional clock layout "00:00"
     * as the timer belongs to the main activity a call to the main activity to get the
     * text view is necessary, thus we set the time passed to the textView timer
     */
    public void update_Tv_Timer(){
        Log.i(TAG,"update_Tv_Timer()");
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d", seconds);
        mainActivity.getTimer().setText(timeLeftFormatted);

    }

    /**
     * Method is used only when starting from main activity for 2Player mode
     * so we can set player one as the one that initiates the P1 VS P2
     * @param playerNumber takes a integer 1 = Player1, 2 = Player2
     */
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }
}
