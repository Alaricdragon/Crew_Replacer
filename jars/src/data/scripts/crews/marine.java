package data.scripts.crews;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import data.scripts.crewReplacer_Crew;

public class marine extends crewReplacer_Crew {//no idea if this is needed at all =(
    @Override
    public void removeCrew(CampaignFleetAPI fleet, float CrewToLost){
        fleet.getCargo().removeCommodity(name, CrewToLost);
        fleet.getCargo().removeMarines((int)CrewToLost);
    }
}
