package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrewReplacer_ApplyAndDisplayLosses extends BaseCommandPlugin{
    public static class ResData {
        String jobID;
        int losses;
        int required;
    }
    static CargoAPI cargo;
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        List<CrewReplacer_ApplyAndDisplayLosses.ResData> data = getData(params, memoryMap);
        //CrewReplacer_Log.loging("execute...",this,true);
        for (int a = 0; a < data.size(); a++){
            CrewReplacer_ApplyAndDisplayLosses.ResData b = data.get(a);
            //CrewReplacer_Log.loging("running display for index, jobID, required, losses: "+a+", "+b.jobID+", "+b.required+", "+b.losses,this,true);
            crewReplacer_Main.getJob(b.jobID).automaticlyGetDisplayAndApplyCrewLost(cargo,b.required,b.losses,dialog.getTextPanel());
        }
        //CrewReplacer_Log.loging("ending",this,true);
        return true;
    }
    protected List<CrewReplacer_ApplyAndDisplayLosses.ResData> getData(List<Misc.Token> params,Map<String, MemoryAPI> memoryMap){
        //CrewReplacer_Log.loging("getData...",this,true);
        Misc.Token temp = params.get(0);
        int start = 0;
        if (temp.getObject(memoryMap) instanceof CargoAPI) {
            cargo = (CargoAPI) temp.getObject(memoryMap);
            start = 1;
        }else{
            cargo = Global.getSector().getPlayerFleet().getCargo();
        }

        List<CrewReplacer_ApplyAndDisplayLosses.ResData> data = new ArrayList<CrewReplacer_ApplyAndDisplayLosses.ResData>();
        //CrewReplacer_Log.loging("start is: "+start,this,true);
        for (int i = start; i < params.size(); i++) {
            //CrewReplacer_Log.loging("getting item. total items already gotten: "+data.size(),this,true);
            Misc.Token t = params.get(i);//for each prarm, get parm.
            //step 1
            CrewReplacer_ApplyAndDisplayLosses.ResData curr = new CrewReplacer_ApplyAndDisplayLosses.ResData();
            curr.jobID = t.getString(memoryMap);
            i++;
            t = params.get(i);
            curr.required = t.getInt(memoryMap);
            i++;
            t = params.get(i);
            curr.losses = t.getInt(memoryMap);
            data.add(curr);
        }
        //CrewReplacer_Log.loging("returning a total of "+data.size()+" items",this,true);
        return data;
    }
}
