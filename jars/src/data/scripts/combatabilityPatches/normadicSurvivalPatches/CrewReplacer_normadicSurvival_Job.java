package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import data.scripts.crewReplacer_Crew;
import data.scripts.crewReplacer_Job;

public class CrewReplacer_normadicSurvival_Job extends crewReplacer_Job {
    @Override
    public crewReplacer_Crew createDefaultCrew() {
        return new CrewReplacer_normadicSurvival_crew();
    }
}
