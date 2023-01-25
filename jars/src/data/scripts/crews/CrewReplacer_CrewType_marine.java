package data.scripts.crews;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Crew;

public class CrewReplacer_CrewType_marine extends crewReplacer_Crew {//no idea if this is needed at all =(
    @Override
    public float getCrewPowerInCargo(CargoAPI cargo){
        float temp = super.getCrewPowerInCargo(cargo);
        CrewReplacer_Log.loging("get crew power in cargo",this);
        return temp;
    }
    @Override
    public float getCrewToLose(CargoAPI cargo,float crewUsed,float crewLost){//,CargoAPI cargo){
        addMarineXP(cargo,crewUsed,crewLost);
        return crewLost;
    }
    @Override
    public void removeCrew(CargoAPI cargo, float CrewToLost){
        cargo.removeMarines((int) CrewToLost);
    }
    @Override
    public float getCrewPower(CargoAPI cargo){
        CrewReplacer_Log.loging("infinity crew power? " + (crewPower * getXPPowerMulti(cargo)),this);
        return crewPower * getXPPowerMulti(cargo);//PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel();
        //return crewPower * Global.getSector().getPlayerFleet().getCommanderStats().getMarineEffectivnessMult().;
    }
    private void addMarineXP(CargoAPI cargo,float CrewUsed,float CrewLost){
        try{
            Object[] ObjectTemp = (Object[])ExtraData;
            GroundRaidObjectivesListener.RaidResultData data = (GroundRaidObjectivesListener.RaidResultData) ObjectTemp[0];
            float ratio = (int)ObjectTemp[1] / CrewUsed;
            PlayerFleetPersonnelTracker thing = PlayerFleetPersonnelTracker.getInstance();
            float marines = cargo.getMarines();

            float total = marines + CrewLost;//data.marinesLost;
            float xpGain = 1f - data.raidEffectiveness;
            xpGain *= total;
            xpGain *= thing.XP_PER_RAID_MULT;
            if (xpGain < 0) xpGain = 0;
            CrewReplacer_Log.loging("getting XP as: " + ratio * 100 + "% of " + xpGain,this);
            xpGain *= ratio;//this reduces the XP gained down to the amount of XP you should earn, based on how many marines were in your fleet? hopefully?
            CrewReplacer_Log.loging("got " + xpGain + " XP",this);
            thing.getMarineData().addXP(xpGain);

            thing.update();
        }catch (Exception E){
            CrewReplacer_Log.loging("ERROR: failed to add XP to marines",this,true);
        }
    }
    @Override
    public void displayCrewAvailable(CargoAPI cargo, float numberOfItems, TextPanelAPI text){
        String displayName = getDisplayName(cargo);

        TooltipMakerAPI tt = text.beginTooltip();
        TooltipMakerAPI iwt = tt.beginImageWithText(getCrewIcon(cargo), 24);
        String numberStr = (int) numberOfItems + "";
        LabelAPI label = iwt.addPara(numberStr + " " + displayName, 0, Misc.getHighlightColor(), numberStr);
        //cargo.getFleetData().getFleet().getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD).getMultBonus()
        float xpTemp = (int)(100 * (getXPPowerMulti(cargo) - 1));//PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel());
        if(xpTemp != 0 || true) {
            String temp = "%";
            String XP = xpTemp+"";// + "%";
            XP+=temp;
            iwt.addPara("   + %s power from marine XP", 0, Misc.getHighlightColor(), XP);
        }
        tt.addImageWithText(0);

        text.addTooltip();
    }
    @Override
    public void displayCrewLost(CargoAPI cargo,float numberOfItems, TextPanelAPI text){
        DisplayedCrewNumbers(cargo,numberOfItems,text);
    }
    private float getXPPowerMulti(CargoAPI cargo){
        //cargo.getFleetData().getFleet()
        float a = 1 + PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel();
        //a = Global.getSector().getPlayerFleet().getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD).getMultBonus("core_marines").value;//marineXP?
        CrewReplacer_Log.loging("getting marine XP power bonus:" + a,this);
        return a;
    }
    private float getXPDefenceMulti(CargoAPI cargo){
        float a = 1 + PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel();
        //a = Global.getSector().getPlayerFleet().getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD).getMultBonus("core_marines").value;
        CrewReplacer_Log.loging("getting marine XP defence bonus:" + a,this);
        return a;
    }
}
