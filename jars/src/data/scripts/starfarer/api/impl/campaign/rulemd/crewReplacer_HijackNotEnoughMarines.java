package data.scripts.starfarer.api.impl.campaign.rulemd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.crewReplacer_Main;

import java.util.List;
import java.util.Map;

public class crewReplacer_HijackNotEnoughMarines extends BaseCommandPlugin {
    String marinesJob = "Mission_hijack_marines";
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        //was: $player.marines < $hijack_marines
        //crewReplacer_HijackNotEnoughMarines $hijack_marines

        float available = crewReplacer_Main.getJob(marinesJob).getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo());
        int req = params.get(0).getInt(memoryMap);
        //int[] a = {};
        //a[available]=1;
        return available < req;
    }
}

