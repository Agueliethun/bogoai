package com.atomicobject.games.rts.strategies;

import com.atomicobject.games.rts.communication.AICommand;
import com.atomicobject.games.rts.mapping.Pathfinder;
import com.atomicobject.games.rts.state.Map;
import com.atomicobject.games.rts.state.MapDirections;
import com.atomicobject.games.rts.state.Unit;
import com.atomicobject.games.rts.state.UnitManager;
import com.atomicobject.games.rts.updates.Location;
import com.atomicobject.games.rts.updates.UnitUpdate;

import java.util.List;

public class AttackStrategy implements IUnitStrategy {
    private final Map map;
    private final UnitManager unitManager;
    private AIStrategy gameStrat;
    private IUnitStrategy nextStrategy;

    public AttackStrategy(Map map, Unit unit, UnitManager unitManager) {
        this.map = map;
        this.unitManager = unitManager;
        this.gameStrat = AIStrategy.getInstance();
    }

    public IUnitStrategy getNextStrategy() {
        return nextStrategy;
    }

    @Override
    public void setNextStrategy(IUnitStrategy strat) {
        nextStrategy = strat;
    }

    public AICommand buildCommand(Unit unit) {
        var direction = MapDirections.randomDirection();

        List<Location> targetTiles = map.enemyLocationsInRange(unit.getLocation(), unitManager.getRange(unit));
        Location targetTile = null;
        if (!targetTiles.isEmpty()) {
            targetTile = targetTiles.get(0);
            for (var tile : targetTiles) {
                List<UnitUpdate> enemies = map.getTile(tile).getTileUpdate().getUnits();
                for (UnitUpdate enemy : enemies) {
                    if (enemy.isTank()) {
                        targetTile = new Location(enemy.getX(), enemy.getY());
                    }
                }
            }
        }

        if (targetTile == null) {
            return AICommand.buildMoveCommand(unit, unit.nextMove());
        }

//        if (unit.isWorker()) { // worker type
        if (unit.getUnitUpdate().canAttack()) {
            return AICommand.buildShootCommand(unit, targetTile);
        } else {
            if (unit.getLocation().getX() + unit.getLocation().getY() < 64) { // chase
                unit.setPath(gameStrat.pathfinder.findPath(unit.getLocation(), targetTile, 0));
                return AICommand.buildMoveCommand(unit, unit.nextMove());
            } else { // towards home
                unit.setPath(gameStrat.pathfinder.findPath(unit.getLocation(), map.homeBaseLocation(), 0));
                return AICommand.buildMoveCommand(unit, unit.nextMove());
            }
        }
    }
}
