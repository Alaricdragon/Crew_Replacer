package data.scripts.starfarer.api.impl.campaign.rulemd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.combatabilityPatches.forgeProdPatches.CrewReplacer_ForgeProd_Patch;

import java.util.List;
import java.util.Map;

public class crewReplacer_ForgeprodEnabled extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        return CrewReplacer_ForgeProd_Patch.isModActive();
    }
}
