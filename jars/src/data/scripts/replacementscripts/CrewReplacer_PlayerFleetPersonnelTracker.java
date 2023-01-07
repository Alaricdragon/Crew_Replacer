package data.scripts.replacementscripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.fs.starfarer.api.impl.campaign.graid.GroundRaidObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import data.scripts.CrewReplacer_Log;

import java.util.List;
import java.util.Map;

public class CrewReplacer_PlayerFleetPersonnelTracker /**/extends PlayerFleetPersonnelTracker{/*/implements GroundRaidObjectivesListener {/**/
    /*@Override
    public void modifyRaidObjectives(MarketAPI market, SectorEntityToken entity, List<GroundRaidObjectivePlugin> objectives, MarketCMD.RaidType type, int marineTokens, int priority) {

    }*/

    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap){
        //PlayerFleetPersonnelTracker
        CrewReplacer_Log.loging("runing raid Objective listinger. removing crew and adding XP...",this);
        CrewReplacer_Log.push();
        countShouldBeGoneRaidListiners();
        CrewReplacer_Log.pop();
        /*function would be easterly difficult to remove or add*/
    }

    /*private void negitiveXP(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap){
        data.xpGained
        List<GroundRaidObjectivesListener> RaidListiner = Global.getSector().getListenerManager().getListeners(GroundRaidObjectivesListener.class);
        String getName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        for(int a = 0; a < RaidListiner.size(); a++){
            if(RaidListiner.get(a).getClass().getCanonicalName().equals(getName)){
                RaidListiner.get(a).reportRaidObjectivesAchieved(data,dialog,memoryMap);
            }
        }
    }*/
    private void countShouldBeGoneRaidListiners(){
        int count = 0;
        if(Global.getSector().getGenericPlugins().hasPlugin(PlayerFleetPersonnelTracker.class)){
            CrewReplacer_Log.loging("found pluggin of removed class. counting number of errors...",this);
            CrewReplacer_Log.push();
            List<GenericPluginManagerAPI.GenericPlugin> a = Global.getSector().getGenericPlugins().getPluginsOfClass(PlayerFleetPersonnelTracker.class);
            for(GenericPluginManagerAPI.GenericPlugin b:a) {
                CrewReplacer_Log.loging("counting plugin...: " + b.getClass().getCanonicalName(),this);
                count++;
            }
            CrewReplacer_Log.loging("removed a total of " + count + " plugging. should be zero",this);
            CrewReplacer_Log.pop();
        }



        List<GroundRaidObjectivesListener> RaidListiner = Global.getSector().getListenerManager().getListeners(GroundRaidObjectivesListener.class);
        String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("counting trackers of removing class..",this);
        CrewReplacer_Log.push();
        for(GroundRaidObjectivesListener b:RaidListiner){
            if(b.getClass().getCanonicalName().equals(removeName)){
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be none.",this);
        CrewReplacer_Log.pop();
    }
}
