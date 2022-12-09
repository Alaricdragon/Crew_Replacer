package data.scripts.starfarer.api.impl.campaign.rulemd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;

import java.util.List;
import java.util.Map;

public class crewReplacer_cTapRepairSel extends BaseCommandPlugin {
    String metalsJob = "repairHyperRelayMetals";
    String rare_metalsJob = "repairHyperRelayRare_metals";
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        int req0 = params.get(0).getInt(memoryMap);
        crewReplacer_Main.getJob(metalsJob).automaticlyGetDisplayAndApplyCrewLost(Global.getSector().getPlayerFleet(),req0,req0,dialog.getTextPanel());

        int req1 = params.get(1).getInt(memoryMap);
        crewReplacer_Main.getJob(rare_metalsJob).automaticlyGetDisplayAndApplyCrewLost(Global.getSector().getPlayerFleet(),req1,req1,dialog.getTextPanel());
        return true;
    }
}