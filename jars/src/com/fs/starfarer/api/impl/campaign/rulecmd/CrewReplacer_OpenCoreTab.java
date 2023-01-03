package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.CrewReplacer_Log;

import java.util.List;
import java.util.Map;

public class CrewReplacer_OpenCoreTab extends BaseCommandPlugin implements CoreInteractionListener {

    private Map<String, MemoryAPI> memoryMap;
    private InteractionDialogAPI dialog;

    /**
     * OpenCoreUI <CoreUITabId>
     */
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        CrewReplacer_Log.loging("HERE getting dialog 'getCanonicalName' as: " + dialog.getClass().getCanonicalName(),this);
        CrewReplacer_Log.loging("HERE getting dialog 'getName' as: " + dialog.getClass().getName(),this);
        CrewReplacer_Log.loging("HERE getting dialog 'getSimpleName' as: " + dialog.getClass().getSimpleName(),this);
        //CrewReplacer_Log.loging("HERE getting dialog 'getCanonicalName' as: " + dialog.getClass().getPackageName(),this);
        //CrewReplacer_Log.loging("HERE getting dialog 'getCanonicalName' as: " + dialog.getClass().getCanonicalName(),this);

        this.dialog = dialog;
        this.memoryMap = memoryMap;
        String tabIdStr = params.get(0).getString(memoryMap);

        CoreUITabId tabId = CoreUITabId.valueOf(tabIdStr);
        dialog.getOptionPanel().clearOptions();

        CampaignUIAPI.CoreUITradeMode mode = CampaignUIAPI.CoreUITradeMode.OPEN;
        if (params.size() > 1) {
            mode = CampaignUIAPI.CoreUITradeMode.valueOf(params.get(1).getString(memoryMap));
        }

        //memoryMap.get(MemKeys.LOCAL).set("$lastTradeMode", mode.name(), 0);
        dialog.getVisualPanel().showCore(tabId, dialog.getInteractionTarget(), mode, this);

        Misc.stopPlayerFleet();

        return true;
    }

    public void coreUIDismissed() {
        // update player memory - supplies/fuel the player has, etc
        Global.getSector().getCharacterData().getMemory();


        FireAll.fire(null, dialog, memoryMap, "PopulateOptions");
    }
}
