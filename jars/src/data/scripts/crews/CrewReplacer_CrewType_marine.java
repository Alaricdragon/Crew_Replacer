package data.scripts.crews;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Crew;

public class CrewReplacer_CrewType_marine extends crewReplacer_Crew {//no idea if this is needed at all =(
    private static final boolean logsActive = Global.getSettings().getBoolean("CrewReplacerDisplayMarineLogs");
    private float[] XPGainData = new float[]{0f,0f};
    private float MarinesLossMultiTemp= 1;
    @Override
    public float getCrewPowerInCargo(CargoAPI cargo){
        float temp = super.getCrewPowerInCargo(cargo);
        return temp;
    }
    @Override
    public float getCrewToLose(CargoAPI cargo,float crewUsed,float crewLost){//,CargoAPI cargo){
        XPGainData[0] = crewUsed;
        XPGainData[1] = crewLost;
        PlayerFleetPersonnelTracker.getInstance().update();//this is here to handle a error that happens sometimes, the data removed in marketCMD will not be readded as it should be.
        MarinesLossMultiTemp = getXPDefenceMulti(cargo);
        return super.getCrewToLose(cargo,crewUsed,crewLost) * MarinesLossMultiTemp;//getXPDefenceMulti(cargo);
    }
    @Override
    public void removeCrew(CargoAPI cargo, float CrewToLost){
        cargo.removeCommodity(name, CrewToLost);
        PlayerFleetPersonnelTracker.getInstance().getMarineData().remove((int) CrewToLost,true);
        addMarineXP(cargo);
    }
    @Override
    public float getCrewPower(CargoAPI cargo){
        return crewPower * getXPPowerMulti(cargo);
    }
    private void addMarineXP(CargoAPI cargo){
        float CrewUsed = XPGainData[0];
        float CrewLost = XPGainData[1];

        if(CrewUsed == 0){
            PlayerFleetPersonnelTracker thing = PlayerFleetPersonnelTracker.getInstance();
            thing.update();
            CrewReplacer_Log.loging("no marrines used. exiting XP gain code...",this);
            return;
        }
        try{
            Object[] ObjectTemp = (Object[])ExtraData;
            GroundRaidObjectivesListener.RaidResultData data = (GroundRaidObjectivesListener.RaidResultData) ObjectTemp[0];
            float ratio = (float)Math.floor(CrewUsed * getXPPowerMulti(cargo)) / (int)ObjectTemp[1];
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

            CrewReplacer_Log.loging("after calculations, you have " + PlayerFleetPersonnelTracker.getInstance().getMarineData().xp + "xp",this);
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
        float xpTemp = Math.round(100 * (getXPPowerMulti(cargo) - 1));//PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel());

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
        String displayName = getDisplayName(cargo);

        TooltipMakerAPI tt = text.beginTooltip();
        TooltipMakerAPI iwt = tt.beginImageWithText(getCrewIcon(cargo), 24);
        String numberStr = (int) numberOfItems + "";
        LabelAPI label = iwt.addPara(numberStr + " " + displayName, 0, Misc.getHighlightColor(), numberStr);
        //cargo.getFleetData().getFleet().getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD).getMultBonus()
        float xpTemp = Math.round(100 * (1 - MarinesLossMultiTemp));//PlayerFleetPersonnelTracker.getInstance().getMarineData().getXPLevel());
        if(xpTemp != 0 || true) {
            String temp = "%";
            String XP = xpTemp+"";// + "%";
            XP+=temp;
            iwt.addPara("   - %s losses from marine XP", 0, Misc.getHighlightColor(),XP);
            CrewReplacer_Log.loging("loss multi that was saved is: " + MarinesLossMultiTemp,this);
        }
        tt.addImageWithText(0);

        text.addTooltip();
    }
    private float getXPPowerMulti(CargoAPI cargo){
        try {
            float a = 1 + (Global.getSector().getPlayerFleet().getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD).getPercentBonus("marineXP").getValue() / 100);
            CrewReplacer_Log.loging("getting marine XP power bonus:" + a, this);
            return a;
        }catch (Exception E){
            CrewReplacer_Log.loging("failed to get marine XP power bonus. setting to default value", this);
            return 1;
        }
    }
    private float getXPDefenceMulti(CargoAPI cargo){
        try {
            MutableStat.StatMod b = Global.getSector().getPlayerFleet().getStats().getDynamic().getStat("ground_attack_casualties_mult").getMultStatMod("marineXP");
            float a = b.getValue();
            CrewReplacer_Log.loging("getting marine XP defence bonus:" + a, this);
            return a;
        }catch (Exception e){
            CrewReplacer_Log.loging("failed to get marine defence bonus. setting to default value", this);
            return 1;
        }
    }
}
