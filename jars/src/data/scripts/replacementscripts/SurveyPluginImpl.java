package data.scripts.replacementscripts;
//i have no idea at all the location of crew chacks for this one. the only thing here is a drop in crew costs.
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
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
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew;
//import data.scripts.crew_replacer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
/*
changed getRequired to reduce crew cost based on survey cost reduction.
changed getRequired to change heavy matchenery cost to match lost crew cost (rases cost to match the amount that crew cost was lowerd)

 */
public class SurveyPluginImpl implements SurveyPlugin {
    private String[] showdows = CrewReplacer_HideShowdoCrew.getShowdos("survey");
    private String crewJob = "survey_crew";
    private String supplyJob = "survey_supply";
    private String heavy_matchnearyJob = "survey_heavyMachinery";

    public boolean Survey_RunningCrewUse = false;
    protected float crewUsed;
    protected float machineryUsed;
    protected float supplyUsed;

    public static int FLAT_SUPPLIES = 10;

    public static int BASE_MACHINERY = 10;
    public static int BASE_CREW = 50;
    public static int BASE_SUPPLIES = 10;
    public static int MIN_SUPPLIES_OR_MACHINERY = 5;

    public static float MIN_PLANET_RADIUS = 70;
    public static float MAX_PLANET_RADIUS = 250;
    public static float MULT_AT_MAX_PLANET_RADIUS = 5;


    private CampaignFleetAPI fleet;
    private PlanetAPI planet;

    private MutableStat costMult = new MutableStat(1f);
    private MutableStat xpMult = new MutableStat(1f);

    public void init(CampaignFleetAPI fleet, PlanetAPI planet) {
        this.fleet = fleet;
        this.planet = planet;

        float hazard = getHazardMultiplier();
        if (hazard != 1f) {
            costMult.modifyMult("planet_hazard", hazard, "Hazard rating");
        }
        float size = getSizeMultiplier();
        if (size != 1f) {
            costMult.modifyMult("planet_size", size, "Planet size");
        }

        xpMult.applyMods(costMult);

        if (fleet != null) {
            MutableFleetStatsAPI stats = fleet.getStats();
            MutableStat stat = stats.getDynamic().getStat(Stats.SURVEY_COST_MULT);
            for (MutableStat.StatMod mod : stat.getMultMods().values()) {
                costMult.modifyMult(mod.source, mod.value, mod.desc);
            }
        }

    }

    protected float getHazardMultiplier() {
        //CrewReplacer_Log.loging("getHazardMultiplier",this,true);
        float hazard = planet.getMarket().getHazardValue();
        return hazard;
    }

    protected float getSizeMultiplier() {
        //CrewReplacer_Log.loging("getSizeMultiplier",this,true);
        float radius = planet.getRadius();
        float range = MAX_PLANET_RADIUS - MIN_PLANET_RADIUS;
        if (range <= 0) return 1f;
        if (radius < MIN_PLANET_RADIUS) radius = MIN_PLANET_RADIUS;
        if (radius > MAX_PLANET_RADIUS) radius = MAX_PLANET_RADIUS;

        float mult = 1f + ((radius - MIN_PLANET_RADIUS) / range) * (MULT_AT_MAX_PLANET_RADIUS - 1f);

        mult = (int)(mult * 20) / 20f;

        return mult;
    }


    public Map<String, Integer> getRequired() {
        CrewReplacer_HideShowdoCrew.removeShowdoCrewFromPlayersFleet();
        this.Survey_RunningCrewUse = true;
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

        CrewReplacer_HideShowdoCrew.addShowdoCrewToPlayerFleet(showdows[0],(int)crewJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        CrewReplacer_HideShowdoCrew.addShowdoCrewToPlayerFleet(showdows[2],(int)machineryJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        this.crewUsed = crew;
        this.machineryUsed = machinery;

        result.put(showdows[0],crew);
        result.put(showdows[2], machinery);

        return result;
    }

    public Map<String, Integer> getConsumed() {
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


        CrewReplacer_HideShowdoCrew.addShowdoCrewToPlayerFleet(showdows[1],(int)suppliesJobTemp.getAvailableCrewPower(Global.getSector().getPlayerFleet().getCargo()));
        this.supplyUsed = supplies;
        result.put(showdows[1], supplies);

        return result;
    }

    public MutableStat getCostMult() {
        //CrewReplacer_Log.loging("getCostMult",this,true);
        return costMult;
    }

    public long getXP() {
        //CrewReplacer_Log.loging("getXP",this,true);
        if(this.Survey_RunningCrewUse){
            CrewReplacer_Log.loging("applying crew used / lost from survey...",this,true);
            this.Survey_RunningCrewUse = false;
            crewReplacer_Job suppliesJobTemp = crewReplacer_Main.getJob(supplyJob);
            crewReplacer_Job crewJobTemp = crewReplacer_Main.getJob(crewJob);
            crewReplacer_Job machineryJobTemp = crewReplacer_Main.getJob(heavy_matchnearyJob);

            crewJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(), (int) this.crewUsed,0);
            machineryJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(),(int)this.machineryUsed,0);

            suppliesJobTemp.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(),(int)this.supplyUsed,(int)this.supplyUsed);
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

    public long getBaseXPForCondition(String conditionId) {
        //CrewReplacer_Log.loging("getBaseXPForCondition",this,true);
        long xp = 0;

        float base = Global.getSettings().getFloat("baseSurveyXP");
        ConditionGenDataSpec data = (ConditionGenDataSpec) Global.getSettings().getSpec(ConditionGenDataSpec.class, conditionId, true);
        if (data != null) {
            xp += base * data.getXpMult();
        }
        return xp;
    }

    public MutableStat getXPMult() {
        //CrewReplacer_Log.loging("getXPMult",this,true);
        return xpMult;
    }


    public String getImageCategory() {
        //CrewReplacer_Log.loging("getImageCategory",this,true);
        return "illustrations";
    }

    public String getImageKey() {
        //CrewReplacer_Log.loging("getImageKey",this,true);
        return "survey";
    }

    public String getSurveyDataType(PlanetAPI planet) {
        //CrewReplacer_Log.loging("getSurveyDataType",this,true);
        if (planet.getMarket() == null) return null;

        int count = 0;
        float value = 0;
        for (MarketConditionAPI mc : planet.getMarket().getConditions()) {
            if (DerelictThemeGenerator.interestingConditionsWithRuins.contains(mc.getId())) {
                count++;
            }
            if (mc.getGenSpec() != null) {
                //value += mc.getGenSpec().getXpMult();
                value += mc.getGenSpec().getRank();
            }
        }

        if (planet.getMarket().hasCondition(Conditions.HABITABLE)) {
            value += 4f;
        }

        float hazard = planet.getMarket().getHazardValue();
        value -= (hazard - 1f) * 2f;

        if (value <= 5) return Commodities.SURVEY_DATA_1;
        if (value <= 8) return Commodities.SURVEY_DATA_2;
        if (value <= 11 && count <= 1) return Commodities.SURVEY_DATA_3;
        if (value <= 14 && count <= 2) return Commodities.SURVEY_DATA_4;
        return Commodities.SURVEY_DATA_5;

//		if (value <= 10 && count <= 0) return Commodities.SURVEY_DATA_1;
//		if (value <= 20 && count <= 0) return Commodities.SURVEY_DATA_2;
//		if (value <= 30 && count <= 1) return Commodities.SURVEY_DATA_3;
//		if (value <= 40 && count <= 2) return Commodities.SURVEY_DATA_4;
//		return Commodities.SURVEY_DATA_5;

// 		too few class V with below approach
//		if (count <= 0) return Commodities.SURVEY_DATA_1;
//		if (count <= 1) return Commodities.SURVEY_DATA_2;
//		if (count <= 2) return Commodities.SURVEY_DATA_3;
//		if (count <= 3) return Commodities.SURVEY_DATA_4;
//		return Commodities.SURVEY_DATA_5;
    }


    public Map<String, Integer> getOutpostConsumed() {
        //CrewReplacer_Log.loging("getOutpostConsumed",this,true);
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();

        result.put(Commodities.CREW, 1000);
        result.put(Commodities.HEAVY_MACHINERY, 100);
        result.put(Commodities.SUPPLIES, 200);
        //result.put(Commodities.METALS, 200);

        return result;
    }
}



