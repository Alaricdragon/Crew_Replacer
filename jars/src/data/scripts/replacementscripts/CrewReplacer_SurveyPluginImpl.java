package data.scripts.replacementscripts;
//i have no idea at all the location of crew chacks for this one. the only thing here is a drop in crew costs.
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.SurveyPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.procgen.ConditionGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.themes.DerelictThemeGenerator;
import com.fs.starfarer.api.plugins.SurveyPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;
import data.scripts.replacedScripts.CrewReplacer_SurveyPluginImpl_Base;
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew;
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew_2;
//import data.scripts.crew_replacer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
/*
changed getRequired to reduce crew cost based on survey cost reduction.
changed getRequired to change heavy matchenery cost to match lost crew cost (rases cost to match the amount that crew cost was lowerd)

 */
public class CrewReplacer_SurveyPluginImpl extends CrewReplacer_SurveyPluginImpl_Base{//implements SurveyPlugin{//extends SurveyPluginImpl {
    public static final String[] showdows = CrewReplacer_HideShowdoCrew_2.getShowdos("survey");
    public static final String[] showdows2 = CrewReplacer_HideShowdoCrew_2.getShowdos("survey2");
    public static final String crewJob = "survey_crew";
    public static final String supplyJob = "survey_supply";
    public static final String heavy_matchnearyJob = "survey_heavyMachinery";


    public static final String crewJob2 = "colony_crew";
    public static final String supplyJob2 = "colony_supply";
    public static final String heavy_matchnearyJob2 = "colony_heavyMachinery";

    public boolean Survey_RunningCrewUse = false;
    public PlanetAPI planetLookingAt = null;
    public PlanetAPI planetLookingAt2 = null;
    protected float crewUsed;
    protected float machineryUsed;
    protected float supplyUsed;

    public static PlanetAPI colonyPlanetTemp = null;
    @Override
    public String getSurveyDataType(PlanetAPI planet) {
        if (planet.getMarket() != null) {
            this.Survey_RunningCrewUse = true;
            this.planetLookingAt = planet;
        }
        return super.getSurveyDataType(planet);
    }
    //REQUIRED
    @Override
    public Map<String, Integer> getRequired() {
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(showdows);
        //CrewReplacer_Log.loging("getRequired",this,true);
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();

        float mult = getCostMult().getModifiedValue();

        int machinery = (int)Math.round(BASE_MACHINERY * mult);
        machinery -= (int) Misc.getFleetwideTotalMod(fleet, Stats.getSurveyCostReductionId(Commodities.HEAVY_MACHINERY), 0);
        machinery = Math.round(machinery / 10f) * 10;
        if (machinery < MIN_SUPPLIES_OR_MACHINERY) machinery = MIN_SUPPLIES_OR_MACHINERY;
        int crew = (int)Math.round(BASE_CREW * mult);
        int temp = crew;

        crewReplacer_Job crewJobTemp = crewReplacer_Main.getJob(crewJob);
        crewReplacer_Job machineryJobTemp = crewReplacer_Main.getJob(heavy_matchnearyJob);

        /*crew -= crewJobTemp.getAvailableCrewPower(fleet);
        machinery -= machineryJobTemp.getAvailableCrewPower(fleet);
        crew = Math.max(0,crew);
        machinery = Math.max(0,machinery);*/

        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows[0],(int)crewJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows[1],(int)machineryJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        this.crewUsed = crew;
        this.machineryUsed = machinery;

        result.put(showdows[0],crew);
        result.put(showdows[1], machinery);

        //CrewReplacer_HideShowdoCrew.removeShowdoCrewFromPlayersFleet(showdows);
        return result;
    }
    //REQUIRED
    @Override
    public Map<String, Integer> getConsumed() {
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(showdows2);
        //CrewReplacer_Log.loging("getConsumed",this,true);
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();

        float mult = getCostMult().getModifiedValue();
        int supplies = (int)Math.round(BASE_SUPPLIES * mult);
        supplies += FLAT_SUPPLIES;
        supplies = Math.round((int) supplies / 10f) * 10;
        supplies -= (int) Misc.getFleetwideTotalMod(fleet, Stats.getSurveyCostReductionId(Commodities.SUPPLIES), 0);
        if (supplies < MIN_SUPPLIES_OR_MACHINERY) supplies = MIN_SUPPLIES_OR_MACHINERY;
        crewReplacer_Job suppliesJobTemp = crewReplacer_Main.getJob(supplyJob);

        /*supplies -= suppliesJobTemp.getAvailableCrewPower(fleet);
        supplies = Math.max(0,supplies);*/


        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows2[0],(int)suppliesJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        this.supplyUsed = supplies;
        result.put(showdows2[0], supplies);

        //CrewReplacer_HideShowdoCrew.removeShowdoCrewFromPlayersFleet(showdows2);
        return result;
    }
    //REQUIRED
    @Override
    public long getXP() {
        //CrewReplacer_Log.loging("getXP",this,true);
        try {
            CrewReplacer_Log.loging("current status: "+this.Survey_RunningCrewUse+", "+this.planetLookingAt+", "+this.planetLookingAt2,this,true);
            if (this.Survey_RunningCrewUse && !this.planetLookingAt.equals(this.planetLookingAt2)) {
                //if(Global.getSector().getPlayerFleet().getInteractionTarget().getId().equals(this.planetLookingAt.getId())){

                //}
                CrewReplacer_Log.loging("applying crew used / lost from survey...", this, true);
                CrewReplacer_Log.loging("getting crew, machinery, supplys as: " + crewUsed + ", " + machineryUsed + ", " + supplyUsed, this);
                this.Survey_RunningCrewUse = false;
                this.planetLookingAt2 = this.planetLookingAt;
                crewReplacer_Job suppliesJobTemp = crewReplacer_Main.getJob(supplyJob);
                crewReplacer_Job crewJobTemp = crewReplacer_Main.getJob(crewJob);
                crewReplacer_Job machineryJobTemp = crewReplacer_Main.getJob(heavy_matchnearyJob);

                crewJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(), (int) this.crewUsed, 0);
                machineryJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(), (int) this.machineryUsed, 0);

                suppliesJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(), (int) this.supplyUsed, (int) this.supplyUsed);
                CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet();
            }
        }catch (Exception e){
            CrewReplacer_Log.loging("failed to get or compare player fleet to surveyed world and/or failed to remove crew as intended. Exception: "+e,this,true);
        }
        float xp = 0;

        if (planet.getMarket() != null) {
            for (MarketConditionAPI mc : planet.getMarket().getConditions()) {
                xp += getBaseXPForCondition(mc.getId());
            }
        }
        xp *= getXPMult().getModifiedValue();
        return (long) xp;
    }
    //required
    @Override
    public Map<String, Integer> getOutpostConsumed() {
        //CrewReplacer_Log.loging("getOutpostConsumed",this,true);
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(showdows);
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(showdows2);
        crewReplacer_Job a = crewReplacer_Main.getJob(crewJob2);
        crewReplacer_Job b = crewReplacer_Main.getJob(heavy_matchnearyJob2);
        crewReplacer_Job c = crewReplacer_Main.getJob(supplyJob2);
        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows[0],(int)a.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows[1],(int)b.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        CrewReplacer_HideShowdoCrew_2.addShowdoCrewToPlayerFleet(showdows2[0],(int)c.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        result.put(showdows[0], 1000);
        result.put(showdows[1], 100);
        result.put(showdows2[0], 200);

        this.colonyPlanetTemp = this.planet;
        return result;
    }
}



