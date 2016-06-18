package com.bob.test.game;

import com.bob.game.inputs.Block;
import com.bob.game.inputs.InputsManager;
import com.bob.game.levels.Level;
import com.bob.game.levels.WriteLevel;
import com.bob.game.world.WorldController;
import com.bob.test.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class WorldControllerTest {

    @Test
    public void testLevels() throws Exception {
        testLevel(
                WriteLevel.level1.getLevel(),
                new Block[][] {{Block.WHITE, Block.IMPLY, Block.RIGHT}}
        );

        testLevel(
                WriteLevel.level2.getLevel(),
                new Block[][] {{Block.WHITE, Block.IMPLY, Block.RIGHT},{Block.RED, Block.IMPLY, Block.DOWN}}
        );

        testLevel(
                WriteLevel.level3.getLevel(),
                new Block[][] {{Block.NOT, Block.WHITE, Block.IMPLY, Block.RIGHT}}
        );

        testLevel(
                WriteLevel.level4.getLevel(),
                new Block[][] {
                        {Block.WHITE, Block.AND, Block.WHITE_PREV, Block.IMPLY, Block.RIGHT},
                        {Block.RED, Block.IMPLY, Block.DOWN},
                        {Block.YELLOW, Block.IMPLY, Block.LEFT},
                        {Block.GREEN_PREV, Block.IMPLY, Block.UP},
                        {Block.GREEN, Block.IMPLY, Block.UP}
                }
        );

    }

    public void testLevel(Level lvl, Block[][] rules) {

        WorldController wc = new WorldController();
        wc.setupWorld(lvl);


        InputsManager im = new InputsManager();
        im.resetRules(rules);

        wc.startLPSAnimation(lvl, im.getRulesString());
        for (int i=0; i < 100; i++) {
            wc.updateBob(0.6f);
            if (wc.isLevelWon()) break;
        }

        assertTrue(wc.isLevelWon());
    }
}