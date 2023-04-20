package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;
import data.scripts.crewReplacer_CrewSet;
import nomadic_survival.campaign.OperationInteractionDialogPlugin;
import nomadic_survival.campaign.intel.OperationIntel;

import java.util.ArrayList;

public class CrewReplacer_normadicSurvivalA extends OperationInteractionDialogPlugin {

    public CrewReplacer_normadicSurvivalA(InteractionDialogPlugin formerPlugin, OperationIntel intel) {
        super(formerPlugin, intel);
    }
    public static final String normadicSurvivalCrewSetName = "normadicSurvival_";
    public static final String normadicSurvivalJobName = "normadicSurvival_";
    public static final String crewReplacer_crewSet = "";
    protected static ArrayList<String> createdJobs = new ArrayList<>();
    public static boolean logs = Global.getSettings().getBoolean("crewReplacerDisplay_normadicSurvival_logs");
    protected crewReplacer_Job getAndSetJob(String commodityId){
        String jobName = normadicSurvivalJobName + commodityId + this.intel.getType().getId();
        String crewSetName = normadicSurvivalCrewSetName + commodityId;
        CrewReplacer_Log.loging("trying to get or set a job in normadicsurvival, with the job and crewSet named: " + jobName + ", " + crewSetName + ": ",this,logs);
        CrewReplacer_Log.push();
        for(String a : createdJobs){
            if(a.equals(jobName)){
                CrewReplacer_Log.loging("job already prepared. fetching job..",this,logs);
                crewReplacer_Job b = crewReplacer_Main.getJob(jobName);
                CrewReplacer_Log.loging("adding exstra data to job...",this,logs);
                CrewReplacer_Log.pop();
                b.ExtraData = commodityId;
                return b;
            }
        }
        CrewReplacer_Log.loging("job is not prepared. getting new job...",this,logs);
        crewReplacer_Job job = crewReplacer_Main.getJob(normadicSurvivalJobName);

        CrewReplacer_Log.loging("adding crew,crewset,and exstra data to job...",this,logs);
        job.addNewCrew(commodityId,1,10);//does not use custom crew because it always returns 1 power and defence anyways
        job.addCrewSet(crewSetName);
        crewReplacer_Main.getCrewSet(crewSetName).addCrewSet(crewReplacer_crewSet + commodityId);
        job.applyCrewSets();
        job.ExtraData = commodityId;

        CrewReplacer_Log.loging("organizing jobs priority....",this,logs);
        job.organizePriority();

        CrewReplacer_Log.loging("remembering prepared job....",this,logs);
        CrewReplacer_Log.pop();
        createdJobs.add(normadicSurvivalJobName);
        return job;
    }
    @Override
    protected void removeCommodity(CargoAPI cargo, String commodityId, int amountLost) {
        //this.intel
        CrewReplacer_Log.loging("running function ''...",this,logs);
        CrewReplacer_Log.push();
        crewReplacer_Job job = getAndSetJob(commodityId);
        job.automaticlyGetDisplayAndApplyCrewLost(cargo,amountLost,amountLost,this.text);
        //cargo.removeCommodity(commodityId, amountLost);
        //AddRemoveCommodity.addCommodityLossText(commodityId, amountLost, this.text);

        CrewReplacer_Log.pop();
    }
    @Override
    protected float getAvailableCommodityAmount(CargoAPI cargo, String commodity) {
        CrewReplacer_Log.loging("running function ''...",this,logs);
        CrewReplacer_Log.push();
        crewReplacer_Job job = getAndSetJob(commodity);
        int out = (int) job.getAvailableCrewPower(cargo);
        CrewReplacer_Log.pop();
        return out;
    }
    /*
    @Override
    protected float getCargoSpace(CommoditySpecAPI spec) {
        CrewReplacer_Log.loging("'get space'",this,true);
        return spec.getCargoSpace();
    }

    @Override
    protected float getCrewSpace(CommoditySpecAPI spec) {
        CrewReplacer_Log.loging("'get crew space'",this,true);
        return spec.isPersonnel() ? 1.0F : 0.0F;
    }
    @Override
    protected float getFuelSpace(CommoditySpecAPI spec) {
        CrewReplacer_Log.loging("'get fuel space'",this,true);
        return spec.isFuel() ? 1.0F : 0.0F;
    }*/
}
