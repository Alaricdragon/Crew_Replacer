package data.scripts.combatabilityPatches;

import com.fs.starfarer.api.Global;
import data.scripts.CrewReplacer_Log;
import data.scripts.combatabilityPatches.normadicSurvivalPatches.CrewReplacer_normadicSurvival_init;

public class CrewReplacer_InitCombatabilityPatches {
    public static String[] modNames = {
            "sun_nomadic_survival",
    };
    public static CrewReplacer_PatchBase[] patches = {
           new CrewReplacer_normadicSurvival_init(),
    };
    public static void onApplicationLoad() {
        for(int a = 0; a < modNames.length; a++) {
            if (Global.getSettings().getModManager().isModEnabled(modNames[a])) {
                CrewReplacer_Log.loging("Crew Replacer is attempting to add a compatibility patch for the mod '" + modNames[a] + "' ...", CrewReplacer_InitCombatabilityPatches.class, true);
                patches[a].apply();
            }
        }
    }
    public static void onGameLoad(boolean newGame) {
        if(Global.getSettings().getModManager().isModEnabled(modNames[0])) {
        }
    }
}
