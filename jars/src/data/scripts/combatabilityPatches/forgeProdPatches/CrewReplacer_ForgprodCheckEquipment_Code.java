package data.scripts.combatabilityPatches.forgeProdPatches;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.rulecmd.ForgprodCheckEquipment;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;

import java.util.List;
import java.util.Map;

public class CrewReplacer_ForgprodCheckEquipment_Code  extends ForgprodCheckEquipment {

    private static SectorEntityToken mothership;
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        CrewReplacer_Log.loging("running code for forge prod (checkEquipment). opps!",this,true);
        mothership = dialog.getInteractionTarget();
        return super.execute(ruleId, dialog, params, memoryMap);
    }
    protected boolean playerHasEquipment() {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        Map<String, Integer> requiredRes = SalvageEntity.computeRequiredToSalvage(mothership);
        for (String commodityId : requiredRes.keySet()) {
            int cost = requiredRes.get(commodityId);
            if (commodityId.equals(Commodities.CREW)) {
                cost = Math.round(cost * 1.4f);
            } else {
                cost = Math.round(cost * 4.8f);
            }
            requiredRes.put(commodityId, cost);
        }
        requiredRes.put(Commodities.GAMMA_CORE, 3);
        boolean hasCrew = hasCommodity(requiredRes, cargo, Commodities.CREW);
        boolean hasMachinery = hasCommodity(requiredRes, cargo, Commodities.HEAVY_MACHINERY);
        boolean hasCores = hasCommodity(requiredRes, cargo, Commodities.GAMMA_CORE);
        return hasCrew && hasMachinery && hasCores;
    }

    private static boolean hasCommodity(Map<String, Integer> requiredRes, CargoAPI cargo, String commodity) {
        return CrewReplacer_ForgeProd_Patch.getJobFromCommodity(commodity).getAvailableCrewPower(cargo) >= requiredRes.get(commodity);//cargo.getCommodityQuantity(commodity) >= requiredRes.get(commodity);
    }
}