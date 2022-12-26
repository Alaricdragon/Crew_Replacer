package data.scripts.crews;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import data.scripts.crewReplacer_Crew;

public class marine extends crewReplacer_Crew {//no idea if this is needed at all =(
    @Override
    public void removeCrew(CargoAPI cargo, float CrewToLost){
        cargo.removeCommodity(name, CrewToLost);
    }
}
