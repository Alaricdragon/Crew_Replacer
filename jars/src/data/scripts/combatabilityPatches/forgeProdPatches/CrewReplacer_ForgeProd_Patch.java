package data.scripts.combatabilityPatches.forgeProdPatches;

import com.fs.starfarer.api.Global;
import data.scripts.combatabilityPatches.CrewReplacer_PatchBase;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;

public class CrewReplacer_ForgeProd_Patch extends CrewReplacer_PatchBase {
    public static final String taskName = "ForgeProd_MotherShip_";
    public static String[] jobs = {"crew","heavy_machinery","gamma_core"};
    @Override
    public void apply() {
        for (String a : jobs){
            setDefaltJob(a);
        }
    }
    public static boolean isModActive(){
        return Global.getSettings().getModManager().isModEnabled("forge_production");
    }
    public static void setDefaltJob(String commodity){
        crewReplacer_Job job = getJobFromCommodity(commodity);
        job.addNewCrew(commodity,1,10);
        job.addCrewSet(commodity);
    }
    public static crewReplacer_Job getJobFromCommodity(String commodity){
        return crewReplacer_Main.getJob(taskName+commodity);
    };
}
