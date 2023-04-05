package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;
import nomadic_survival.campaign.OperationInteractionDialogPlugin;
import nomadic_survival.campaign.intel.OperationIntel;
import nomadic_survival.campaign.rulecmd.SUN_NS_ExploitPlanetDialogPlugin;

import java.util.List;
import java.util.Map;

public class CrewReplacer_normadicSurvivalB extends SUN_NS_ExploitPlanetDialogPlugin {
    public OperationInteractionDialogPlugin createInteractionPlugin(InteractionDialogPlugin formerPlugin, OperationIntel intel) {
        return new CrewReplacer_normadicSurvivalA(formerPlugin, intel);
    }
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        return super.execute(ruleId,dialog,params,memoryMap);
    }
}
