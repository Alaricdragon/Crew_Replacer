package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import data.scripts.CrewReplacer_Log;
import nomadic_survival.campaign.OperationInteractionDialogPlugin;
import nomadic_survival.campaign.intel.OperationIntel;

public class CrewReplacer_normadicSurvivalA extends OperationInteractionDialogPlugin {

    public CrewReplacer_normadicSurvivalA(InteractionDialogPlugin formerPlugin, OperationIntel intel) {
        super(formerPlugin, intel);
    }
    public static void test(){
        CrewReplacer_Log.loging("initing a combatability thing2", CrewReplacer_normadicSurvivalA.class,true);
    }
    @Override
    protected void removeCommodity(CargoAPI cargo, String commodityId, int amountLost) {
        //this.intel
        cargo.removeCommodity(commodityId, amountLost);
        AddRemoveCommodity.addCommodityLossText(commodityId, amountLost, this.text);
        CrewReplacer_Log.loging("'remove'",this,true);
    }
    @Override
    protected float getAvailableCommodityAmount(CargoAPI cargo, String commodity) {
        CrewReplacer_Log.loging("'get'",this,true);
        return cargo.getCommodityQuantity(commodity);
    }
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
    }
}
