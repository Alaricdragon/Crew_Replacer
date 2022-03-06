package data.scripts.crews;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import data.scripts.crewReplacer_Crew;

public class crew extends crewReplacer_Crew {
    @Override
    public void removeCrew(CampaignFleetAPI fleet, float CrewToLost){
        fleet.getCargo().removeCommodity(name, CrewToLost);
        //?fleet.getCargo().removeCrew((int)CrewToLost);
    }
}
