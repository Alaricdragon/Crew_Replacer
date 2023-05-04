package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
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
        String jobName = normadicSurvivalJobName + this.intel.getType().getId() + "_" + commodityId;
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
        crewReplacer_Job job = crewReplacer_Main.getJob(jobName);

        CrewReplacer_Log.loging("adding crew,crewset,and extra data to job...",this,logs);
        job.addNewCrew(commodityId,1,10);//does not use custom crew because it always returns 1 power and defence anyways
        job.addCrewSet(crewSetName);
        crewReplacer_Main.getCrewSet(crewSetName).addCrewSet(crewReplacer_crewSet + commodityId);
        job.applyCrewSets();
        job.ExtraData = commodityId;

        CrewReplacer_Log.loging("organizing jobs priority....",this,logs);
        job.organizePriority();

        CrewReplacer_Log.loging("displaying crew in job at first time job setup...",this,logs);
        String temp = "";
        for(String a : job.getCrewDisplayNames(Global.getSector().getPlayerFleet().getCargo())){
            temp += a + ", ";
        }
        CrewReplacer_Log.loging(temp,this,logs);

        CrewReplacer_Log.loging("remembering prepared job....",this,logs);
        CrewReplacer_Log.pop();
        createdJobs.add(jobName);
        return job;
    }

    @Override
    protected void removeCommodity(CargoAPI cargo, String commodityId, int amountLost) {
        //this.intel
        CrewReplacer_Log.loging("running function 'removeCommodity'...",this,logs);
        CrewReplacer_Log.push();
        crewReplacer_Job job = getAndSetJob(commodityId);
        job.automaticlyGetDisplayAndApplyCrewLost(cargo,amountLost,amountLost,this.text);
        //cargo.removeCommodity(commodityId, amountLost);
        //AddRemoveCommodity.addCommodityLossText(commodityId, amountLost, this.text);

        CrewReplacer_Log.pop();
    }
    @Override
    protected float getAvailableCommodityAmount(CargoAPI cargo, String commodity) {
        CrewReplacer_Log.loging("running function 'getAvailableCommodityAmount'...",this,logs);
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
        return super.getCargoSpace(spec);//spec.getCargoSpace();
    }*/
    /*
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
    protected int CCLType = 0;
    @Override
    protected boolean checkCapacityLimit(float perBatch, float capacity) {
        /*if (capacity <= 0.0F)
            return false;
        if (perBatch > 0.0F && perBatch * this.maxBatchesPlayerCanStore > capacity) {
            //this.intel.getInputs().
            float limit = capacity / perBatch;//HERE. This is what i need to change.
            this.maxBatchesPlayerCanStore = (int)Math.ceil(limit);
            return (this.maxBatchesPlayerCanStore - limit > 0.0F && this.maxBatchesPlayerCanStore < this.maxBatchesPlayerCanAfford && this.maxBatchesPlayerCanStore < this.maxBatchesAvailableInAbundance);
        }
        return false;*/
        CCLType++;
        switch (CCLType){
            case 1:
                return checkCapacityLimitCargo(perBatch, capacity);
            case 2:
                return checkCapacityLimitCrew(perBatch, capacity);
            case 3:
                return checkCapacityLimitFuel(perBatch, capacity);
            default:
                return super.checkCapacityLimit(perBatch, capacity);
        }
    }
    protected float checkCapacityLimitGetMaxNumber(float capacity,String cargoType){
        CrewReplacer_Log.loging("running function 'checkCapacityLimitGetMaxNumber'...",this,logs);
        CrewReplacer_Log.push();
        /*what does this do and what do i need to do?
        * what this does is gets the amount of cargo space that's going to be used from a given cargo type, at the maximum number of capacity.
        * so what i need to do:
        * 1) get each crewReplacer_Job from inputs.
        * 2) get the amount of power of each job per batch.
        * 3) calculated how many batches i can hold. how? im going to first run crewReplacer_jog.getCargoSpaceRange() on each job
        *   -this.intel.getInputs().get(0).getCountPerBatch(false) * number of batches that i can afford right now;
        * 4) add the outputed values together to get total cargo use.
        *   -keep in mind, it outputs bouth the min possable cargo value, and the max possible cargo value.
        * 5) lower until the cargo use is below the required amount of cargo use.*/
        //this.intel.getInputs().get(0).getCountPerBatch(false);//? false or true is unknown.
        ArrayList<crewReplacer_Job> Jobs = new ArrayList<>();
        ArrayList<Integer> items = new ArrayList<>();
        for(OperationIntel.Input input : this.intel.getInputs()){
            Jobs.add(getAndSetJob(input.getCommodityID()));
            items.add(input.getCountPerBatch(false));
        }
        CrewReplacer_Log.pop();
        return 0;
    }
    protected boolean checkCapacityLimitCargo(float perBatch, float capacity) {
        if (capacity <= 0.0F)
            return false;
        if (perBatch > 0.0F && perBatch * this.maxBatchesPlayerCanStore > capacity) {
            //this.intel.getInputs().
            float limit = checkCapacityLimitGetMaxNumber(capacity,crewReplacer_Job.CARGO_CARGO);//capacity / perBatch;//HERE. This is what i need to change.
            this.maxBatchesPlayerCanStore = (int)Math.ceil(limit);
            return (this.maxBatchesPlayerCanStore - limit > 0.0F && this.maxBatchesPlayerCanStore < this.maxBatchesPlayerCanAfford && this.maxBatchesPlayerCanStore < this.maxBatchesAvailableInAbundance);
        }
        return false;
    }
    protected boolean checkCapacityLimitFuel(float perBatch, float capacity) {
        if (capacity <= 0.0F)
            return false;
        if (perBatch > 0.0F && perBatch * this.maxBatchesPlayerCanStore > capacity) {
            //this.intel.getInputs().
            float limit = checkCapacityLimitGetMaxNumber(capacity,crewReplacer_Job.CARGO_FUEL);//HERE. This is what i need to change.
            this.maxBatchesPlayerCanStore = (int)Math.ceil(limit);
            return (this.maxBatchesPlayerCanStore - limit > 0.0F && this.maxBatchesPlayerCanStore < this.maxBatchesPlayerCanAfford && this.maxBatchesPlayerCanStore < this.maxBatchesAvailableInAbundance);
        }
        return false;
    }
    protected boolean checkCapacityLimitCrew(float perBatch, float capacity) {
        if (capacity <= 0.0F)
            return false;
        if (perBatch > 0.0F && perBatch * this.maxBatchesPlayerCanStore > capacity) {
            //this.intel.getInputs().
            float limit = checkCapacityLimitGetMaxNumber(capacity,crewReplacer_Job.CARGO_CREW);//HERE. This is what i need to change.
            this.maxBatchesPlayerCanStore = (int)Math.ceil(limit);
            return (this.maxBatchesPlayerCanStore - limit > 0.0F && this.maxBatchesPlayerCanStore < this.maxBatchesPlayerCanAfford && this.maxBatchesPlayerCanStore < this.maxBatchesAvailableInAbundance);
        }
        return false;
    }
    @Override
    protected void recalculateBatchLimit() {
        this.CCLType = 0;
        super.recalculateBatchLimit();
        /*
        this.maxCapacityReduction = 0;
        float crewPerBatch = 0.0F, cargoPerBatch = 0.0F, fuelPerBatch = 0.0F;
        CommoditySpecAPI out = this.type.getOutput();
        CargoAPI inputCargo = getCargo(false);
        this.maxBatches = 50;
        this.maxBatchesPlayerCanAfford = this.maxBatches;
        this.maxBatchesPlayerCanStore = this.maxBatches;
        this.maxBatchesAvailableInAbundance = this.maxBatches;
        cargoPerBatch += (!out.isFuel() && !out.isPersonnel()) ? (out.getCargoSpace() * this.type.getOutputCountPerBatch()) : 0.0F;
        fuelPerBatch += out.isFuel() ? this.type.getOutputCountPerBatch() : 0.0F;
        crewPerBatch += out.isPersonnel() ? this.type.getOutputCountPerBatch() : 0.0F;
        for (OperationIntel.Input input : this.intel.getInputs()) {
            double perBatch = input.getCountPerBatch(this.useAbundance);
            int limit = (perBatch > 0.0D) ? (int)Math.floor(getAvailableCommodityAmount(inputCargo, input.getCommodityID()) / perBatch) : Integer.MAX_VALUE;
            cargoPerBatch = (float)(cargoPerBatch - getCargoSpace(input.getCommodity()) * perBatch);
            fuelPerBatch = (float)(fuelPerBatch - getFuelSpace(input.getCommodity()) * perBatch);
            crewPerBatch = (float)(crewPerBatch - getCrewSpace(input.getCommodity()) * perBatch);
            if (this.maxBatchesPlayerCanAfford > limit)
                this.maxBatchesPlayerCanAfford = limit;
        }
        if (this.useAbundance)
            this.maxBatchesAvailableInAbundance = this.intel.getCurrentAbundanceBatches();
        if (!this.outputToColony) {
            CargoAPI outputCargo = getCargo(true);
            if (checkCapacityLimit(cargoPerBatch, outputCargo.getSpaceLeft()))
                this.maxCapacityReduction = 1;
            if (checkCapacityLimit(crewPerBatch, outputCargo.getFreeCrewSpace()))
                this.maxCapacityReduction = 1;
            if (checkCapacityLimit(fuelPerBatch, outputCargo.getFreeFuelSpace()))
                this.maxCapacityReduction = 1;
        }
        if (this.maxBatches > this.maxBatchesPlayerCanAfford)
            this.maxBatches = this.maxBatchesPlayerCanAfford;
        if (this.maxBatches > this.maxBatchesPlayerCanStore)
            this.maxBatches = this.maxBatchesPlayerCanStore;
        if (this.maxBatches > this.maxBatchesAvailableInAbundance)
            this.maxBatches = this.maxBatchesAvailableInAbundance;
        if (this.selectedBatches <= 0) {
            if (this.maxBatches == 1) {
                this.selectedBatches = 1;
            } else if (this.maxBatches > 1) {
                this.selectedBatches = shouldDefaultToMax() ? Math.max(1, this.maxBatches - this.maxCapacityReduction) : 1;
            }
        } else {
            this.selectedBatches = Math.min(this.selectedBatches, this.maxBatches);
        }*/
    }

}
