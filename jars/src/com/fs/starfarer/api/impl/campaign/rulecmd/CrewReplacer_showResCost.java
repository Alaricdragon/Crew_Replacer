package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.ResourceCostPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import data.scripts.crewReplacer_Main;
import data.scripts.crew_replacer_startup;

import java.awt.*;
import java.util.*;
import java.util.List;


public class CrewReplacer_showResCost extends BaseCommandPlugin {

    public static class ResData {
        String displayID;
        String jobID;
        int qty;
        boolean consumed;
    }
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        crew_replacer_startup.loging("running: crewReplacer_showResCost",this);
        List<CrewReplacer_showResCost.ResData> data = new ArrayList<CrewReplacer_showResCost.ResData>();
        int step = 0;
        /*
            3-4 steps:
            0) icon and name image id.
            1) job name
            2) required amount
            3) (optional) consumed yes or no.
            -)repeat
            ?)optional final step: get width override.

            order:
            String.
            String.
            int.
            optional boolean
            -repeat
            float.
         */
        float widthOverride = -1f;
        for (int i = 0; i < params.size(); i++) {
            Misc.Token t = params.get(i);//for each prarm, get parm.
            if(i == params.size() - 1) {
                widthOverride = t.getFloat(memoryMap);
                crew_replacer_startup.loging("  widthOverride: " + widthOverride,this);
            }

            //step 1
            crew_replacer_startup.loging("  step: " + i,this);
            CrewReplacer_showResCost.ResData curr = new CrewReplacer_showResCost.ResData();
            curr.displayID = t.getString(memoryMap);
            crew_replacer_startup.loging("      display: " + t.getString(memoryMap),this);
            i++;
            t = params.get(i);
            curr.jobID = t.getString(memoryMap);
            crew_replacer_startup.loging("      job: " + t.getString(memoryMap),this);
            i++;
            t = params.get(i);
            curr.qty = (int) t.getFloat(memoryMap);
            crew_replacer_startup.loging("      qut: " + t.getFloat(memoryMap),this);
            if (params.size() > i + 1) {
                t = params.get(i + 1);
                if (t.isBoolean(memoryMap)) {
                    curr.consumed = t.getBoolean(memoryMap);
                    crew_replacer_startup.loging("      consumed override: " + t.getBoolean(memoryMap),this);
                    i++;
                }
            }
            data.add(curr);
            continue;
        }


        String [] displayID = new String [data.size()];
        String [] jobID = new String [data.size()];
        int [] qty = new int [data.size()];
        boolean [] consumed = new boolean [data.size()];

        for (int i = 0; i < data.size(); i++) {
            CrewReplacer_showResCost.ResData curr = data.get(i);
            displayID[i] = curr.displayID;
            jobID[i] = curr.jobID;
            qty[i] = curr.qty;
            consumed[i] = curr.consumed;
        }

        showCost(dialog.getTextPanel(), "Resources: required (available)", true, widthOverride, null, null, displayID,jobID, qty, consumed);
        return true;
    }



    /*
    public static void showCost(TextPanelAPI text, Color color, Color dark, String [] res, int [] quantities) {
        showCost(text, "Resources: consumed (available)", true, color, dark, res, quantities);
    }
    public static void showCost(TextPanelAPI text, String title, boolean withAvailable, Color color, Color dark, String [] res,String[] jobs, int [] quantities) {
        showCost(text, title, withAvailable, -1f, color, dark, res,jobs, quantities, null);
    }*/
    public static void showCost(TextPanelAPI text, String title, boolean withAvailable, float widthOverride, Color color, Color dark, String [] res,String[] jobs, int [] quantities, boolean [] consumed) {
        if (color == null) color = Misc.getBasePlayerColor();
        if (dark == null) dark = Misc.getDarkPlayerColor();

        Set<String> unmet = new HashSet<String>();
        Set<String> all = new LinkedHashSet<String>();

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();

        for (int i = 0; i < res.length; i++) {
            //loging("crew replacer job name: " + jobs[i]);
            String commodityId = res[i];
            int quantity = quantities[i];
            if (quantity > crewReplacer_Main.getJob(jobs[i]).getAvailableCrewPower(Global.getSector().getPlayerFleet())){//cargo.getQuantity(CargoAPI.CargoItemType.RESOURCES, commodityId)) {//HERE
                unmet.add(commodityId);
            }
            all.add(commodityId);
        }

        float costHeight = 67;
        ResourceCostPanelAPI cost = text.addCostPanel(title, costHeight,
                color, dark);
        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);

        if (widthOverride > 0) {
            cost.setComWidthOverride(widthOverride);
        }

        boolean dgs = true;
        for (int i = 0; i < res.length; i++) {
            String commodityId = res[i];
            int required = quantities[i];
            int available = (int) crewReplacer_Main.getJob(jobs[i]).getAvailableCrewPower(Global.getSector().getPlayerFleet());       //cargo.getCommodityQuantity(commodityId);//HERE
            Color curr = color;
            if (withAvailable && required > crewReplacer_Main.getJob(jobs[i]).getAvailableCrewPower(Global.getSector().getPlayerFleet())){//cargo.getQuantity(CargoAPI.CargoItemType.RESOURCES, commodityId)) {//HERE
                curr = Misc.getNegativeHighlightColor();
            }
            if (dgs) {
                if (withAvailable) {
                    cost.addCost(commodityId, Misc.getWithDGS(required) + " (" + Misc.getWithDGS(available) + ")", curr);
                } else {
                    cost.addCost(commodityId, Misc.getWithDGS(required), curr);
                }
                if (consumed != null && consumed[i]) {
                    cost.setLastCostConsumed(true);
                }
            } else {
                if (withAvailable) {
                    cost.addCost(commodityId, "" + required + " (" + available + ")", curr);
                } else {
                    cost.addCost(commodityId, "" + required, curr);
                }
                if (consumed != null && consumed[i]) {
                    cost.setLastCostConsumed(true);
                }
            }
        }
        cost.update();
    }
}
