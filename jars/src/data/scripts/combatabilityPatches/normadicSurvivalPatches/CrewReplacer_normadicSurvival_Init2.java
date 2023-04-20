package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import data.scripts.starfarer.api.impl.campaign.rulemd.crewReplacer_normadicSurvivalC;

public class CrewReplacer_normadicSurvival_Init2 {
    public static void apply(){
        crewReplacer_normadicSurvivalC.plugin = new CrewReplacer_normadicSurvivalB();
    }
}
