package data.scripts.shadowCrew;

import com.fs.starfarer.api.EveryFrameScript;

public class CrewReplacer_AutoClean implements EveryFrameScript {
    private boolean done = false;
    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (amount == 0) return;//don't use well paused.
        CrewReplacer_HideShowdoCrew_2.removeShadowCrewFromPlayersFleet();
        done = true;
    }
}
