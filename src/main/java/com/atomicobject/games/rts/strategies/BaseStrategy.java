package com.atomicobject.games.rts.strategies;

import com.atomicobject.games.rts.communication.AICommand;
import com.atomicobject.games.rts.state.Map;
import com.atomicobject.games.rts.state.Unit;
import com.atomicobject.games.rts.state.UnitManager;

public class BaseStrategy implements IUnitStrategy {
    private final Map map;
    private final UnitManager unitManager;
    private AIStrategy gameStrat;
    private IUnitStrategy nextStrategy;

    private int buildCooldown = 0;
    public boolean emergency;

    public BaseStrategy(Map map, Unit unit, UnitManager unitManager) {
        this.map = map;
        this.unitManager = unitManager;
        this.gameStrat = AIStrategy.getInstance();
    }

    @Override
    public AICommand buildCommand(Unit unit) {
//        if (buildCooldown <= 0) {
            if (emergency && unitManager.getTankCount() < 3 && unit.getAvailableResources() > unitManager.getTankInfo().getCost()) {
                return AICommand.buildUnitCommand("tank");
            }

            if (unitManager.getScoutCount() < 1 && unit.getAvailableResources() > unitManager.getScoutInfo().getCost()) {
                buildCooldown += unitManager.getScoutInfo().getCreateTime();
                return AICommand.buildUnitCommand("scout");
            }

            if (unitManager.getWorkerCount() < 15 && unit.getAvailableResources() > unitManager.getWorkerInfo().getCost()) {
                buildCooldown += unitManager.getWorkerInfo().getCreateTime();
                return AICommand.buildUnitCommand("worker");
            }


//        }

//        buildCooldown--;

        return null;
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
