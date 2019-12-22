package com.example.simongame;
/**
 *Imports of the class
 */
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The MainActivity class
 * This is the activity that the user will interact with
 * as part of the Simon game I have built.
 * <h5>Global variables</h5>
 * <p>All of the necessary variable to initialize and use our MainActivity</p>
 * @author  Alejandro Rivera
 * @version 1.0
 * @since   2019-11-24
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CustomView simonControl;
    private TextView current_player;
    private TextView currLevel;
    private Button mode1;
    private Button mode2;
    private TextView timer;

    /**
     * Method on create must be implemented when creating an activity and the first line
     * of the class needs to be a call tho superclass then we request android to get a view by its identifier
     * from the layout that was set on this activity activity_main
     * referencing  the variables to their views
     * once the Main activity has been created a sound will play to welcome the user(s)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display views, Timer, CurrentPlayer and Level
        timer= (TextView) findViewById(R.id.tv_timer);
        current_player = (TextView) findViewById(R.id.tv_who_is_playing);
        currLevel = (TextView) findViewById(R.id.tv_level);
        mode1 = (Button) findViewById(R.id.btn_1_player);
        mode2 = (Button) findViewById(R.id.btn_2_player);
        simonControl = (CustomView) findViewById(R.id.simon_panel);
        //starting app sound
        simonControl.playSound("start");
    }


    /**
     * This method is set a result of a onClick action on the Button view to start the mode of Simon vs player
     * resets all of the values, starts a new game, mode to 1 (1 player),disable the button once the game has started
     * and changes btn label to Playing, so the user can see that the game is on place, first movement is made by simon.
     * @param view uses the view as a param to identify that uses a element from the layout
     */
    public void gameSingleMode(View view){
        Log.d(TAG, "gameSingleMode: ");
        simonControl.resetValues();
        simonControl.setGameStart(true);
        simonControl.setGameMode(1);
        simonControl.simonTurn();
        mode1.setEnabled(false);
        mode1.setText("PLAYING");

     }

    /**
     * This method is set a result of a onClick action on the Button view to start the mode of 2 players
     * resets all of the values, starts a new game, mode to 2 (2 player),disable the button once the game has started
     * and changes btn label to Playing, so the user can see that the game is on place
     * first player to play is player 1, and a timer is set.
     * @param view uses the view as a param to identify that uses a element from the layout
     */
    public void gameTwoPlayersMode(View view){
        Log.d(TAG, "gameTwoPlayersMode: ");
        simonControl.resetValues();
        simonControl.setGameStart(true);
        simonControl.setGameMode(2);
        mode2.setEnabled(false);
        mode2.setText("PLAYING");
        simonControl.setPlayerNumber(1);
        current_player.setText("PLAYER 1");
        simonControl.startTimer();

    }

    /**
     * Method gets the getCurrent_player TextView from the activity_main.xml
     * @return current_player TextView
     */
    public TextView getCurrent_player() {
        return current_player;
    }

    /**
     * Method gets the getCurrLevel TextView from the activity_main.xml
     * @return the TextView currLevel
     */
    public TextView getCurrLevel() {
        return currLevel;
    }

    /**
     * Method gets the button that is for the 1 player mode
     * @return the Button mode1
     */
    public Button getMode1() {
        return mode1;
    }

    /**
     * Method gets the button that is for the 2  player mode
     * @return the Button mode2
     */
    public Button getMode2() {
        return mode2;
    }

    /**
     * this method gets the timer TextView from the activity_main.xml
     * @return the TextView of the timer
     */
    public TextView getTimer() {
        return timer;
    }



}
