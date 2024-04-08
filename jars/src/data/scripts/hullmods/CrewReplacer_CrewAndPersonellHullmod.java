package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.modifications.crewAndPersonellChanger.CrewReplacer_CAPChanger_ModFinder;

public class CrewReplacer_CrewAndPersonellHullmod extends BaseHullMod {
    public float[] status = {500,500};
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id/*, MutableCharacterStatsAPI c*/) {
        /*float MinCrew = stats.getVariant().getHullSpec().getMinCrew();
        float MaxCrew = stats.getVariant().getHullSpec().getMaxCrew();*/
        float[] status = this.status;
        try {
            status = CrewReplacer_CAPChanger_ModFinder.getShipStats(stats.getFleetMember().getId());
        }catch (Exception e){

        }
        stats.getMinCrewMod().modifyFlat(id,status[0]);
        stats.getMaxCrewMod().modifyFlat(id,status[1]);
        this.status = status;
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        switch (index){
            case 0:
                return ""+(int)(-1*status[0]);
            case 1:
                return ""+(int)(-1*status[1]);
            default:
                return "";
        }
    }
}
