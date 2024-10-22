package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.crewReplacer_Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrewReplacer_CheckCrewRequirements extends BaseCommandPlugin{
    public static class ResData {
        String jobID;
        int qty;
    }
    static CargoAPI cargo;
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        List<CrewReplacer_CheckCrewRequirements.ResData> data = getData(params, memoryMap);
        for (int a = 0; a < data.size(); a++){
            if (crewReplacer_Main.getJob(data.get(a).jobID).getAvailableCrewPower(cargo) < data.get(a).qty){
                return false;
            }
        }
        return true;
    }
    protected List<CrewReplacer_CheckCrewRequirements.ResData> getData(List<Misc.Token> params,Map<String, MemoryAPI> memoryMap){
        Misc.Token temp = params.get(0);
        int start = 0;
        if (temp.getObject(memoryMap) instanceof CargoAPI) {
            cargo = (CargoAPI) temp.getObject(memoryMap);
            start = 1;
        }else{
            cargo = Global.getSector().getPlayerFleet().getCargo();
        }

        List<CrewReplacer_CheckCrewRequirements.ResData> data = new ArrayList<CrewReplacer_CheckCrewRequirements.ResData>();
        for (int i = start; i < params.size(); i++) {
            Misc.Token t = params.get(i);//for each prarm, get parm.
            //step 1
            CrewReplacer_CheckCrewRequirements.ResData curr = new CrewReplacer_CheckCrewRequirements.ResData();
            curr.jobID = t.getString(memoryMap);
            i++;
            t = params.get(i);
            curr.qty = t.getInt(memoryMap);
            data.add(curr);
        }
        return data;
    }
}
