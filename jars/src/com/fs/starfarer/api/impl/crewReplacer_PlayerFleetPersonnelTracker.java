package com.fs.starfarer.api.impl;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;

import java.util.Map;

public class crewReplacer_PlayerFleetPersonnelTracker extends PlayerFleetPersonnelTracker{
    @Override
    public void reportRaidObjectivesAchieved(RaidResultData data, InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap){
        /*add crew loss data and XP gain data here.
         *remove it from the crewReplacer_MarketCMD
         *also add the 'number of marrine tokens used to not used' ratio for crew used here.*/
        update();
    }
}
