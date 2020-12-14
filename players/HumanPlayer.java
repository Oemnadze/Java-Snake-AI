//Ran Snake.java 20 times and results were like that: average time elapsed for each move
//was 44.3ms. The player won 12 times and died just once.
package players;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snake.GameDisplay;
import snake.GameState;
import snake.Snake;


/**
 *
 * @author steven
 */
public class HumanPlayer extends SnakePlayer implements KeyListener {

    public HumanPlayer(GameState state, int index, Snake game) {
        super(state, index, game);      
    }

    public void setDisplay(GameDisplay display) {
        super.setDisplay(display);
          display.addKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

   /*
    * Remeber the last arrow key that was pressed to determine the direction of the next move
    * If this key corresponds to the opposite of the current direction, it is ignored as that 
    * would mean a guaranteed death (it suggests the player pressed the key to fast when trying
    * to make a u-turn).
    */
    public void keyPressed(KeyEvent e) {
        int lastOrientation = state.getLastOrientation(index);
        if (e.getKeyCode() == KeyEvent.VK_UP && lastOrientation!= GameState.SOUTH) {            
            state.setOrientation(index,GameState.NORTH);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && lastOrientation!= GameState.NORTH) {
            state.setOrientation(index,GameState.SOUTH);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && lastOrientation!= GameState.WEST) {
            state.setOrientation(index,GameState.EAST);                   
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && lastOrientation!= GameState.EAST) {            
            state.setOrientation(index,GameState.WEST);                   
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    private int recDepth = 6;
    private final int foodReward = 200;

    private MyPair max_value(GameState state, int depth, int indx) {
        indx %= state.getNrPlayers();
        if (state.isDead(indx)) {
            return new MyPair(-40, 0);
        }
        if (depth == 0) {
            int ans = Math.abs(state.getTargetX() - state.getPlayerX(indx).get(0)) +
                      Math.abs(state.getTargetY() - state.getPlayerY(indx).get(0));
            return new MyPair(-ans, 0);
        }
        MyPair ans = new MyPair(Integer.MIN_VALUE, 0);
        int orientation = state.getLastOrientation(indx);
        for (int i = 1; i <= 4; i++) {
            if ((orientation + i) % 2 != 0 || orientation == i) {
                GameState newState = new GameState(state);
                newState.setOrientation(indx, i);
                newState.updatePlayerPosition(indx);
                if (!newState.isDead(indx) && newState.getTargetX() == -1 && newState.getTargetY() == -1) {
                    MyPair newPair = new MyPair(foodReward, i);
                    for (int j = 0; j < 5; j++) {
                        GameState newerState = new GameState(newState);
                        newerState.chooseNextTarget();
                        MyPair cur = min_value(newerState, depth, indx + 1);
                        newPair.value += 0.2 * cur.value;
                    }
                    if (newPair.value > ans.value) {
                        ans.value = newPair.value;
                        ans.action = i;
                    }
                }
                else {
                    MyPair cur = min_value(newState, depth, indx + 1);
                    if (cur.value > ans.value) {
                        ans.value = cur.value;
                        ans.action = i;
                    }
                }
            }
        }
        return ans;
    }

    private MyPair min_value(GameState state, int depth, int indx) {
        indx %= state.getNrPlayers();
        if (state.isDead(indx)) {
            MyPair ans;
            if ((indx + 1) % state.getNrPlayers() == this.index) {
                ans = max_value(state, depth - 1, indx + 1);
            }
            else {
                ans = min_value(state, depth, indx + 1);
            }
            return ans;
        }
        GameState newState = new GameState(state);
        SnakePlayer newPlayer;
        if (game.getPlayers()[indx].getClass().equals(AStarPlayer.class)) {
            newPlayer = new AStarPlayer(newState, indx, game);
        }
        else {
            newPlayer = new RandomPlayer(newState, indx, game);
        }
        newPlayer.doMove();
        newState.updatePlayerPosition(indx);
        if (!newState.isDead(indx) && newState.getTargetX() == -1 && newState.getTargetY() == -1) {
            MyPair newPair = new MyPair(0, 0);
            for (int j = 0; j < 5; j++) {
                GameState newerState = new GameState(newState);
                newerState.chooseNextTarget();
                MyPair cur;
                if ((indx + 1) % state.getNrPlayers() == this.index)
                    cur = max_value(newerState, depth - 1, indx + 1);
                else
                    cur = min_value(newerState, depth, indx + 1);
                newPair.value += 0.2 * cur.value;
            }
            return newPair;
        }
        MyPair ans;
        if ((indx + 1) % state.getNrPlayers() == this.index) {
            ans = max_value(newState, depth - 1, indx + 1);
        }
        else {
            ans = min_value(newState, depth, indx + 1);
        }
        return ans;
    }

    private long sm = 0;
    private long cnt = 0;

    public void doMove() {
        if (state.isDead(index))
            return;
        long start = System.currentTimeMillis();
        MyPair ans = max_value(state, recDepth, index);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        sm += timeElapsed;
        cnt++;
        System.out.println((double)sm / (double)cnt);
        state.setOrientation(index, ans.action);
    }
}
