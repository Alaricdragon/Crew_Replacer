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
    private static final boolean logsActive = Global.getSettings().getBoolean("crewReplacerDisplayRaidLogs");
    public static float[] marineCopyTemp = {0f,0f};
    /**/@Override
    public void modifyRaidObjectives(MarketAPI market, SectorEntityToken entity, List<GroundRaidObjectivePlugin> objectives, MarketCMD.RaidType type, int marineTokens, int priority) {

    }/**/

    static final private String jobName = "raiding_marines";
    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap){
        //PlayerFleetPersonnelTracker
        CrewReplacer_Log.loging("runing raid Objective listinger. removing crew and adding XP...",this,logsActive);
        CrewReplacer_Log.push();
            CrewReplacer_Log.loging("removing XP added by the base game to marines...",this,logsActive);

            //XPNeutralizer( data,  dialog,memoryMap);
            tryToReloadMarineData();
            crewReplacer_Job job = crewReplacer_Main.getJob(jobName);
            CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();//10, 5. =: 10 - 5 = 5. 5 / 10 = 0.5
            int crewPowerUsed = (int) (job.getAvailableCrewPower(playerCargo) * (((float)data.marinesTokens - (float)data.marinesTokensInReserve)/ data.marinesTokens));
            Object[] a = {data,crewPowerUsed};
            job.applyExtraDataToCrewAndJob(a);
            CrewReplacer_Log.loging("scanning: used crew power: "+crewPowerUsed,this,logsActive);
            CrewReplacer_Log.loging("scanning: marinesTokens: " + data.marinesTokens,this,logsActive);//total number of tokens
            CrewReplacer_Log.loging("scanning: marinesTokensInReserve: " + data.marinesTokensInReserve,this,logsActive);//tokens not used (when equal to marines tokens, no marrines used. zero, all marines used. a / b = used percent.)
            CrewReplacer_Log.loging("scanning: marinesLost: " + data.marinesLost,this,logsActive);//crew power to lose.
            CrewReplacer_Log.loging("scanning: XP gained: " + data.xpGained,this,logsActive);//crew power to lose.
            CrewReplacer_Log.loging("scanning: raidEffectiveness" + data.raidEffectiveness,this,logsActive);
            CrewReplacer_Log.loging("using " + crewPowerUsed + " out of a possible " + job.getAvailableCrewPower(playerCargo),this,logsActive);
            job.automaticlyGetDisplayAndApplyCrewLost(playerCargo,crewPowerUsed,data.marinesLost,dialog.getTextPanel());
            job.resetExtraDataToCrewsAndJob();
        CrewReplacer_Log.pop();
        /*function would be easterly difficult to remove or add*/
        //data.

    }
    /**/
    public void XPNeutralizerOld(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        /*
        * objective is to reverse XP loss and XP gain from that other listiner.
        * data i have: current status of marrin XP gain.
        * how many marnes were lossed, and how mush XP was gained.
        * and the process the data went through before being added / remove. i just need to revere the data from this.
        * Notes:
        * removeXP does NOTHING. why do i use it? i shouldent.
        * looking at this, i should be able to use math to reverse the data using the data i have.. but not today i dont think*/
        CrewReplacer_Log.push();
        CrewReplacer_Log.loging("playerFleetXP: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().savedXP + "",this,logsActive);

        //remove the XP gained from the PlayerFleetPersonnelTracker class. so i can add it somewere else.
		PlayerFleetPersonnelTracker thing = PlayerFleetPersonnelTracker.getInstance();
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		CargoAPI cargo = fleet.getCargo();
		float marines = cargo.getMarines();

        //thing.getMarineData().remove(data.marinesLost, true);
        //thing.getMarineData().add(data.marinesLost);//???

		float total = marines + data.marinesLost;
		float xpGain = 1f - data.raidEffectiveness;
		xpGain *= total;
		xpGain *= thing.XP_PER_RAID_MULT;
		if (xpGain < 0) xpGain = 0;
		//xpGain*=-1;//this is were XP gets neutralized.
        thing.getMarineData().removeXP(xpGain);
        thing.update();
        CrewReplacer_Log.loging("playerFleetXP: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().savedXP + "",this,logsActive);
        CrewReplacer_Log.loging("removedXP: " + xpGain,this,logsActive);
        CrewReplacer_Log.pop();
	}/**/
    public void NRemove(int remove, boolean removeXP) {
        PlayerFleetPersonnelTracker a1 = PlayerFleetPersonnelTracker.getInstance();
        //PlayerFleetPersonnelTracker a2 = new PlayerFleetPersonnelTracker();
        //a2.getMarineData().num = a1.getMarineData().num;
        //a2.getMarineData().xp = a1.getMarineData().xp;
        //a2.getMarineData().remove(remove,removeXP);


        if (!a1.KEEP_XP_DURING_TRANSFERS) removeXP = true;

        if (remove > a1.getMarineData().num) remove = (int) a1.getMarineData().num;
        if (removeXP) a1.getMarineData().xp *= (a1.getMarineData().num - remove) / Math.max(1f, a1.getMarineData().num);
        //num -= remove;
        if (removeXP) {
            float maxXP = a1.getMarineData().num;
            a1.getMarineData().xp = Math.min(a1.getMarineData().xp, maxXP);
        }
    }


    public void tryToReloadMarineData(){
        CrewReplacer_Log.push();
        CrewReplacer_Log.loging("marrineXP: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().xp + "",this,logsActive);
        CrewReplacer_Log.loging("marineNum: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().num + "",this,logsActive);
        reloadMarineData();
        CrewReplacer_Log.loging("marrineXP: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().xp + "",this,logsActive);
        CrewReplacer_Log.loging("marineNum: " + PlayerFleetPersonnelTracker.getInstance().getMarineData().num + "",this,logsActive);
        CrewReplacer_Log.pop();
    }
    public static void saveMarineData(){
        PlayerFleetPersonnelTracker.PersonnelData a = PlayerFleetPersonnelTracker.getInstance().getMarineData();
        marineCopyTemp[0] = a.xp;
        marineCopyTemp[1] = a.num;
    }
    public static void reloadMarineData(){
        PlayerFleetPersonnelTracker.PersonnelData a = PlayerFleetPersonnelTracker.getInstance().getMarineData();
        a.xp = marineCopyTemp[0];
        a.num = marineCopyTemp[1];
    }
}
