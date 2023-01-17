package data.scripts.replacementscripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.fs.starfarer.api.impl.campaign.graid.GroundRaidObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;

import java.util.List;
import java.util.Map;

public class CrewReplacer_PlayerFleetPersonnelTracker /*/extends PlayerFleetPersonnelTracker{/*/implements GroundRaidObjectivesListener {/**/
    /**/@Override
    public void modifyRaidObjectives(MarketAPI market, SectorEntityToken entity, List<GroundRaidObjectivePlugin> objectives, MarketCMD.RaidType type, int marineTokens, int priority) {

    }/**/

    static final private String jobName = "raiding_marines";
    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap){
        //PlayerFleetPersonnelTracker
        CrewReplacer_Log.loging("runing raid Objective listinger. removing crew and adding XP...",this);
        CrewReplacer_Log.push();
            XPNeutralizer( data,  dialog,memoryMap);
            crewReplacer_Job job = crewReplacer_Main.getJob(jobName);
            job.applyExtraDataToCrewAndJob(data);
            CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();//10, 5. =: 10 - 5 = 5. 5 / 10 = 0.5
            int crewPowerUsed = (int) (job.getAvailableCrewPower(playerCargo) * ((data.marinesTokens - data.marinesTokensInReserve)/ data.marinesTokens));
            CrewReplacer_Log.loging("scanning: marinesTokens: " + data.marinesTokens,this);//total number of tokens
            CrewReplacer_Log.loging("scanning: marinesTokensInReserve: " + data.marinesTokensInReserve,this);//tokens not used (when equal to marines tokens, no marrines used. zero, all marines used. a / b = used percent.)
            CrewReplacer_Log.loging("scanning: marinesLost: " + data.marinesLost,this);//crew power to lose.
            CrewReplacer_Log.loging("using " + crewPowerUsed + " out of a possible " + job.getAvailableCrewPower(playerCargo),this);
            job.automaticlyGetDisplayAndApplyCrewLost(playerCargo,crewPowerUsed,data.marinesLost,dialog.getTextPanel());
            job.resetExtraDataToCrewsAndJob();
            CrewReplacer_Log.loging("removing XP added by the base game to marines...",this);
        CrewReplacer_Log.pop();
        /*function would be easterly difficult to remove or add*/
        //data.

    }
    /**/
    public void XPNeutralizer(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        //remove the XP gained from the PlayerFleetPersonnelTracker class. so i can add it somewere else.
		PlayerFleetPersonnelTracker thing = PlayerFleetPersonnelTracker.getInstance();
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		CargoAPI cargo = fleet.getCargo();
		float marines = cargo.getMarines();

        //thing.getMarineData().remove(data.marinesLost, true);

		float total = marines + data.marinesLost;
		float xpGain = 1f - data.raidEffectiveness;
		xpGain *= total;
		xpGain *= thing.XP_PER_RAID_MULT;
		if (xpGain < 0) xpGain = 0;
		xpGain*=-1;//this is were XP gets neutralized.
        thing.getMarineData().addXP(xpGain);

        thing.update();
	}/**/
    /*
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
    }*/
}
