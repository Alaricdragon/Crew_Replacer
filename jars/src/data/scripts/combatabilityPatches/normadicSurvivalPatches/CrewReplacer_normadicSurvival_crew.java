package data.scripts.combatabilityPatches.normadicSurvivalPatches;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import data.scripts.crewReplacer_Crew;

public class CrewReplacer_normadicSurvival_crew extends crewReplacer_Crew {
    @Override
    public float getCrewPower(CargoAPI cargo){
        return crewPower * getEffectiveCrewPowerRelative(cargo);
    }
    @Override
    public float getCrewDefence(CargoAPI cargo){
        return crewDefence * getEffectiveCrewPowerRelative(cargo);
    }
    public float getEffectiveCrewPowerRelative(CargoAPI cargo){
        return  Global.getSector().getEconomy().getCommoditySpec((String) name).getBasePrice() / Global.getSector().getEconomy().getCommoditySpec((String) this.ExtraData).getBasePrice();
        // 25 / 50 = 0.5 (one of this crew equal to 0.5 of normal)
        //100 / 50 = 2
    }
}
