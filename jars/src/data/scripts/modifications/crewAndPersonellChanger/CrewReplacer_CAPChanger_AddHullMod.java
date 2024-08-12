package data.scripts.modifications.crewAndPersonellChanger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;

public class CrewReplacer_CAPChanger_AddHullMod {
    static private String hullmod = "CrewReplacer_ShipCrewDisplay";
    public static void apply(){
        for(int a = 0; a < Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().size(); a++){
            FleetMemberAPI ship2 = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().get(a);
            ShipVariantAPI ship = ship2.getVariant().clone();
            ship.setSource(VariantSource.REFIT);
            if (!ship.hasHullMod(hullmod)) {
                ship.addMod(hullmod);
                ship2.setVariant(ship, true, true);
            }
        };

    }
    public void apply(FleetMemberAPI fleetMemberAPI){

    }
}
