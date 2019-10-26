package com.atomicobject.games.rts.strategies;

import com.atomicobject.games.rts.communication.AICommand;
import com.atomicobject.games.rts.state.Map;
import com.atomicobject.games.rts.state.MapDirections;
import com.atomicobject.games.rts.state.Unit;
import com.atomicobject.games.rts.state.UnitManager;
import com.atomicobject.games.rts.updates.Location;

import java.util.Random;

public class ExploreStrategy implements IUnitStrategy {
    private final Map map;
    private final UnitManager unitManager;
    private IUnitStrategy nextStrategy;
    private AIStrategy gameStrat;

    private MapDirections.Direction currDirection;

    public ExploreStrategy(Map map, Unit unit, UnitManager unitManager) {
        this.map = map;
        this.unitManager = unitManager;
        gameStrat = AIStrategy.getInstance();
        currDirection = MapDirections.randomDirection();
    }

    public AICommand buildCommand(Unit unit) {
        Random r = new Random();

        if (!map.canMove(unit.getLocation(), currDirection)) {
            int turnAmt = (r.nextDouble() < .5) ? 1 : 3;
            for (int i = 0; i < turnAmt; i++) {
                MapDirections.Direction newDir = MapDirections.turn(currDirection);
                currDirection = newDir;
            }
        }

        return AICommand.buildMoveCommand(unit, currDirection);
    }

    @Override
    public IUnitStrategy getNextStrategy() {
        return nextStrategy;
    }

    @Override
    public void setNextStrategy(IUnitStrategy strat) {
        nextStrategy = strat;
    }

}