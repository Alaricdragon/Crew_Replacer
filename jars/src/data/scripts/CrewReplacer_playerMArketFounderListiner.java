package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.scripts.replacementscripts.CrewReplacer_SurveyPluginImpl;
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew_2;

public class CrewReplacer_playerMArketFounderListiner implements PlayerColonizationListener {
    private String crewJob2 = "colony_crew";
    private String supplyJob2 = "colony_supply";
    private String heavy_matchnearyJob2 = "colony_heavyMachinery";
    public int costCrew = 1000, costHM = 100, costSupply = 200;
    public static void addListiner() {
        if (!Global.getSettings().getModManager().isModEnabled("aaamarketRetrofits")) {
            Global.getSector().getListenerManager().addListener(new CrewReplacer_playerMArketFounderListiner(), true);
        }
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {

        crewReplacer_Job a = crewReplacer_Main.getJob(crewJob2);
        crewReplacer_Job b = crewReplacer_Main.getJob(heavy_matchnearyJob2);
        crewReplacer_Job c = crewReplacer_Main.getJob(supplyJob2);
        a.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(),costCrew,costCrew);
        b.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(),costHM,costHM);
        c.automaticlyGetAndApplyCrewLost(Global.getSector().getPlayerFleet().getCargo(),costSupply,costSupply);
        CrewReplacer_Log.loging("removing crew because you made a market. or so i think.",this,true);
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(CrewReplacer_SurveyPluginImpl.showdows);
        CrewReplacer_HideShowdoCrew_2.removeShowdoCrewFromPlayersFleet(CrewReplacer_SurveyPluginImpl.showdows2);
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {

    }
}
