package com.atomicobject.games.rts.strategies;

import com.atomicobject.games.rts.mapping.Pathfinder;
import com.atomicobject.games.rts.state.Unit;
import com.atomicobject.games.rts.state.UnitManager;
import com.atomicobject.games.rts.communication.AICommand;
import com.atomicobject.games.rts.state.Map;
import com.atomicobject.games.rts.updates.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AIStrategy {
    private UnitManager unitManager;
    private Map map;

    public static AIStrategy instance;

    /*
     * 0 == beginning
     * 1 == defense
     * 2 == attack
     * 3 == TBD
     */
    private int mode = 0;
    private boolean emergency = false;
    private boolean scoutMade = false;
    Unit base;
    Pathfinder pathfinder;
    UnitStrategyFactory uf = new UnitStrategyFactory();

    public AIStrategy(UnitManager unitManager, Map map) {
        instance = this;
        this.unitManager = unitManager;
        this.map = map;
        pathfinder = new Pathfinder(map);
    }

    public static AIStrategy getInstance() {
        return instance;
    }

    public List<AICommand> buildCommandList() {
        if (base == null) {
            unitManager.getUnits().forEach((k, v) -> {
                if (v.isBase()) {
                    base = v;
                }
            });
        }

        if (map.enemyLocationsInRange(base.getLocation(), 4).size() > 0) {
            emergency = true;
        } else {
            emergency =  false;
        }

        unitManager.getUnits().forEach((k, v) -> {
            if (v.getStrategy().getNextStrategy() != null) {
                v.setStrategy(v.getStrategy().getNextStrategy());
                v.getStrategy().setNextStrategy(null);
            }

            if (!emergency) {
                uf.assignStrategy(map, v, unitManager);
            }

            if (emergency && !v.isScout() && !v.isBase()) {
                v.setStrategy(uf.buildAttackStrategy(map, v, unitManager));
            }

            if (emergency && v.isBase()) {
                ((BaseStrategy)(v.getStrategy())).emergency = true;
            }

            if (v.isWorker() && map.getResources().size() == 0) {
                v.setStrategy(uf.buildExploreStrategy(map, v, unitManager));
            } else if (v.isWorker() && map.getResources().size() > 0) {
                v.setStrategy(uf.buildGatherStrategy(map, v, unitManager));
            }
        });

        return unitManager.getUnits().values()
                .stream()
                .map(u -> u.buildCommand())
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public boolean isScoutMade() {
        return scoutMade;
    }

    public void setScoutMade(boolean scoutMade) {
        this.scoutMade = scoutMade;
    }
}
