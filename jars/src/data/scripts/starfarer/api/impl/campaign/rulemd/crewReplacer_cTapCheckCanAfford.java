package data.scripts.starfarer.api.impl.campaign.rulemd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.crewReplacer_Main;

import java.util.List;
import java.util.Map;

public class crewReplacer_cTapCheckCanAfford extends BaseCommandPlugin {
    String metalsJob = "repairHyperRelayMetals";
    String rare_metalsJob = "repairHyperRelayRare_metals";
    String crewsJob = "repairHyperRelayCrew";
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        float available0 = crewReplacer_Main.getJob(metalsJob).getAvailableCrewPower(Global.getSector().getPlayerFleet());
        int req0 = params.get(0).getInt(memoryMap);

        float available1 = crewReplacer_Main.getJob(rare_metalsJob).getAvailableCrewPower(Global.getSector().getPlayerFleet());
        int req1 = params.get(1).getInt(memoryMap);

        float available2 = crewReplacer_Main.getJob(crewsJob).getAvailableCrewPower(Global.getSector().getPlayerFleet());
        int req2 = params.get(2).getInt(memoryMap);
        //int[] a = {};
        //a[available]=1;
        return available0 < req0 && available1 < req1 && available2 < req2;
    }
}