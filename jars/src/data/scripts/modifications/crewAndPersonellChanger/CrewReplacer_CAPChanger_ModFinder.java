package data.scripts.modifications.crewAndPersonellChanger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Main;

public class CrewReplacer_CAPChanger_ModFinder {
    protected static String crewJob = "crewShips", personnelJob = "crewStorage";
    public static float[] crewMod,personnelMod;
    public static String[] ShipID = {};
    private static int time = 0;
    public static int MiCA,MiCR,MaCA,MaCR;
    public static void setCrewStats(){
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        MiCA = (int)crewReplacer_Main.getJob(crewJob).getAvailableCrewPower(fleet.getCargo());
        MiCA += fleet.getCargo().getCrew();
        MaCA = (int)crewReplacer_Main.getJob(personnelJob).getAvailableCrewPower(fleet.getCargo());
        MaCA += fleet.getCargo().getCrew();
        MaCA += fleet.getCargo().getMarines();

        MiCR = 0;
        MaCR = 0;
        for(int a = 0; a < fleet.getFleetData().getMembersInPriorityOrder().size(); a++) {
            FleetMemberAPI ship = fleet.getFleetData().getMembersInPriorityOrder().get(a);
            MiCR += ship.getMinCrew();
            MaCR += ship.getMaxCrew();
        }
    };
    public static void apply(){
        CrewReplacer_Log.push();
        setCrewStats();
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        ShipID = new String[fleet.getFleetData().getMembersInPriorityOrder().size()];
        checkPersonnel(fleet);
        checkCrew(fleet);
        CrewReplacer_Log.pop();
    }
    public static void checkPersonnel(CampaignFleetAPI fleet){
        float power = crewReplacer_Main.getJob(personnelJob).getAvailableCrewPower(fleet.getCargo());
        personnelMod = new float[fleet.getFleetData().getMembersInPriorityOrder().size()];
        for(int a = 0; a < fleet.getFleetData().getMembersInPriorityOrder().size(); a++){
            FleetMemberAPI ship = fleet.getFleetData().getMembersInPriorityOrder().get(a);
            float shipC = ship.getMaxCrew();
            float change = Math.min(shipC,power);
            personnelMod[a] = -change;
            CrewReplacer_Log.loging("adding ID to list: "+ship.getId(),new CrewReplacer_Log(),true);
            CrewReplacer_Log.loging("adding max crew mod to list: "+change,new CrewReplacer_Log(),true);
            ShipID[a] = ship.getId();
            power -= change;
        }
    }
    public static void checkCrew(CampaignFleetAPI fleet){
        float power = crewReplacer_Main.getJob(crewJob).getAvailableCrewPower(fleet.getCargo());
        crewMod = new float[fleet.getFleetData().getMembersInPriorityOrder().size()];
        for(int a = 0; a < fleet.getFleetData().getMembersInPriorityOrder().size(); a++){
            FleetMemberAPI ship = fleet.getFleetData().getMembersInPriorityOrder().get(a);
            float shipC = ship.getMinCrew();
            float change = Math.min(shipC,power);
            crewMod[a] = -change;
            CrewReplacer_Log.loging("adding min crew mod to list: "+change,new CrewReplacer_Log(),true);
            //ShipID[a] = ship.getId();
            power -= change;
        }
    }


    public static float[] getShipStats(String ID){
        CrewReplacer_Log.loging("trying to get a ships stats of ID: "+ID,new CrewReplacer_Log(),true);
        if (/*Global.getSector(). != time || */ShipID.length == 0){
            //time = ???;
            CrewReplacer_Log.loging("IM DOING IT MAN!!!",new CrewReplacer_Log(),true);
            apply();
            CrewReplacer_Log.loging("got "+ShipID.length+" ships",new CrewReplacer_Log(),true);
        }
        for (int a = 0; a < ShipID.length; a++){
            if (ShipID[a].equals(ID)){
                return new float[]{crewMod[a],personnelMod[a]};
            }
        }
        CrewReplacer_Log.loging("FAILED TO GET SHIP ID!!!",new CrewReplacer_Log(),true);
        return new float[]{0,0};
    }
}
