package com.atomicobject.games.rts.strategies;

import com.atomicobject.games.rts.communication.AICommand;
import com.atomicobject.games.rts.state.Map;
import com.atomicobject.games.rts.state.Unit;
import com.atomicobject.games.rts.state.UnitManager;
import com.atomicobject.games.rts.updates.Location;

public class GatherStrategy implements IUnitStrategy {
    private final Map map;
    private final UnitManager unitManager;
    private AIStrategy gameStrat;
    private IUnitStrategy nextStrategy;

    public GatherStrategy(Map map, Unit unit, UnitManager unitManager) {
        this.map = map;
        this.unitManager = unitManager;
        this.gameStrat = AIStrategy.getInstance();
    }

    public IUnitStrategy getNextStrategy() {
        return nextStrategy;
    }

    public AICommand buildCommand(Unit unit) {
        int resource = unit.getUnitUpdate().getResource();

        Location myLoc = unit.getLocation();

        if (resource > 0) {
            unit.setPath(gameStrat.pathfinder.findPath(myLoc, gameStrat.base.getLocation(), 1));

            return AICommand.buildMoveCommand(unit, unit.nextMove());
        } else {
            if (unit.isAdjacentToResource(map)) {
                return AICommand.buildGatherCommand(unit, unit.nextMove());
            }

            if (gameStrat.base == null) {
                return AICommand.buildMoveCommand(unit, unit.nextMove());
            }

            if (!unit.hasPath() && map.getResources().size() > 0) {
                Location destLoc = map.resourceLocationsNearest(myLoc).get(0);

                unit.setPath(gameStrat.pathfinder.findPath(myLoc, destLoc, 1));

                return AICommand.buildMoveCommand(unit, unit.nextMove());
            }

            return AICommand.buildMoveCommand(unit, unit.nextMove());
        }
    }
}
