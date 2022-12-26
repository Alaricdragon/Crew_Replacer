package data.scripts.crews;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import data.scripts.crewReplacer_Crew;

public class crew extends crewReplacer_Crew {
    @Override
    public void removeCrew(CargoAPI cargo, float CrewToLost){
        cargo.removeCommodity(name, CrewToLost);
        //?fleet.getCargo().removeCrew((int)CrewToLost);
    }
}
