/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import snake.GameState;
import snake.Snake;

/**
 *
 * @author steven
 */
public class RandomPlayer extends SnakePlayer {

    static Random rand = new Random();

    public RandomPlayer(GameState state, int index, Snake game) {
        super(state, index, game);
    }

    public void doMove() {
        doRandomMove();
    }

    /*
     *  This player first determines which moves are possible (without killing itself instantly),
     *  then randomly selects one of these moves.
     */
    public void doRandomMove() {
        List<Integer> options = new ArrayList();
        for (int i = 1; i <= 4; i++) {
            if (state.isLegalMove(index, i)) {
                options.add(i);
            }
        }
        if (options.size() > 0) {
            state.setOrientation(index, options.get(rand.nextInt(options.size())));
        }
    }
}
