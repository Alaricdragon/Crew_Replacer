package data.scripts.modifications.crewAndPersonellChanger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.VariantSource;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Main;
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew_2;

public class CrewReplacer_crewAndPersonelChanger {
    protected static String crewJob = "crewShips", personnelJob = "crewStorage";
    protected static String crewMod = "crewReplacer_CrewMod";//, personnelMod = "crewReplacer_PersonnelMod";
    public float lastPowerPersonel = 0;
    public float lastPowerCrew = 0;
    public static void apply(CampaignFleetAPI fleet){
        CrewReplacer_Log.loging("DOING DA THING",new CrewReplacer_Log(),true);
        checkCrew(fleet);
        checkPersonell(fleet);
    }
    public static void checkPersonell(CampaignFleetAPI fleet){
        CrewReplacer_Log.loging("checking personell",new CrewReplacer_Log(),true);
        CrewReplacer_Log.push();
        float power = crewReplacer_Main.getJob(personnelJob).getAvailableCrewPower(fleet.getCargo());
        for(int a = 0; a < fleet.getFleetData().getMembersInPriorityOrder().size(); a++){
            FleetMemberAPI ship = fleet.getFleetData().getMembersInPriorityOrder().get(a);
            CrewReplacer_Log.loging("trying the thing with "+power+" crew left to add.",new CrewReplacer_Log(),true);
            /*ShipVariantAPI ship2 = ship.getVariant().clone();
            ship2.setSource(VariantSource.REFIT);
            ship2.getStats().getMaxCrewMod().unmodifyFlat(crewMod);
            power -= modify(ship2.getStats().getMaxCrewMod(),power,ship2.getMaxCrew());
            ship.setVariant(ship2,true,true);*/
        }
        CrewReplacer_Log.pop();
    }











    public static void checkCrew(CampaignFleetAPI fleet){
        CrewReplacer_Log.loging("checking crew",new CrewReplacer_Log(),true);
        CrewReplacer_Log.push();
        float power = crewReplacer_Main.getJob(crewJob).getAvailableCrewPower(fleet.getCargo());
        for(int a = 0; a < fleet.getFleetData().getMembersInPriorityOrder().size(); a++){
            FleetMemberAPI ship = fleet.getFleetData().getMembersInPriorityOrder().get(a);
            CrewReplacer_Log.loging("trying the thing with "+power+" crew left to add.",new CrewReplacer_Log(),true);
            ship.getStats().getMinCrewMod().unmodifyFlat(crewMod);
            power -= modify(ship.getStats().getMinCrewMod(),power,ship.getMinCrew());
        }
        CrewReplacer_Log.pop();
    }
    protected static float modify(StatBonus mod,float power,float maxPersonnel){
        //mod.unmodifyFlat(crewMod);
        if (power > 0) {
            int crewLoss = (int)Math.min(maxPersonnel,power);
            mod.modifyFlat(crewMod,-crewLoss,"hgvgghhgv");
            //CrewReplacer_Log.loging("getting mod of: "+mod.getFlatBonus(crewMod).getValue(),new CrewReplacer_Log(),true);
            return crewLoss;
        }
        return 0;
    }
}
