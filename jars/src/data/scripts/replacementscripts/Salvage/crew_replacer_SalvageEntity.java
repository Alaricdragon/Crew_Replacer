package data.scripts.replacementscripts.Salvage;
/*
    the ONLY changes here are to computeRequiredToSalvage. i changed:
    the crew cost, so it is reduced depending on survey reduction
        -(done?)MIGHT change this, so crew is replaced by heavy matcheanery. so you can lose something while salvageing.
        -(how?)or, i could try and change this so i lose cr from the ships with this mod instaled.
    the Heavy Matchenery cost, so it increases by the number of crew decreased.



 */
/*
    //i think all instances of changing crew are here
    checkAccidents              //(untested)YES here
    computeRequiredToSalvage    //only gets required crew to salvage, wish can easly be read as required crew power to salvage.
    getValueRecoveryStat        //(untested)YES here
    showCost                    //(untested)YES here
    showCostDebrisField         //YES here
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import data.scripts.CrewReplacer_Log;
import data.scripts.CrewReplacer_StringHelper;
import data.scripts.crewReplacer_Main;
//import data.scripts.crew_replacer;
import data.scripts.replacedScripts.crew_replacer_SalvageEntity_Base;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.ResourceCostPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableStat.StatMod;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.RepairGantry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.DropGroupRow;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec.DropData;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageEntityGeneratorOld;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipRecoverySpecialData;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.StatModValueGetter;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;

/**
 * NotifyEvent $eventHandle <params>
 *
 */
public class crew_replacer_SalvageEntity extends crew_replacer_SalvageEntity_Base {//SalvageEntity {
    protected static final String className = "crew_replacer_SalvageEntity";
    private static String JobName = "salvage_crew";//the name of the job in discription here
    private static String SecondJobName = "salvage_heavyMachinery";
    private static String ItemName = "crew";//every time i look for / remove or add this item, get crew_replacer.getPower(JobName);
    //REQUIRED
    @Override
    protected void checkAccidents() {
        Color highlight = Misc.getHighlightColor();
        ArrayList<Float> crewLosses = new ArrayList<Float>();
        ArrayList<Float> secondaryJobLosses = new ArrayList<Float>();

        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);
        float reqCrew = (int) requiredRes.get(Commodities.CREW);
        float reqMachinery = (int) requiredRes.get(Commodities.HEAVY_MACHINERY);

        if (debris == null) {
            text.setFontInsignia();
            crewLosses = crewReplacer_Main.getJob(JobName).automaticlyGetAndApplyCrewLost(playerFleet,(int)reqCrew,0);//DONE
            secondaryJobLosses = crewReplacer_Main.getJob(SecondJobName).automaticlyGetAndApplyCrewLost(playerFleet,(int)reqMachinery,0);
            DisplayLosses(0,0,crewLosses,secondaryJobLosses,highlight);
            memory.set("$option", "salPerform");
            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
            return;
        }
        text.setFontInsignia();
        float accidentProbability = getAccidentProbability();
        //accidentProbability = 1f;

        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        Random random = Misc.getRandom(seed, 175);
        if (random.nextFloat() > accidentProbability) {
            crewLosses = crewReplacer_Main.getJob(JobName).automaticlyGetAndApplyCrewLost(playerFleet,(int) reqCrew,0);//DONE
            secondaryJobLosses = crewReplacer_Main.getJob(SecondJobName).automaticlyGetAndApplyCrewLost(playerFleet,(int) reqMachinery,0);
            DisplayLosses(0,0,crewLosses,secondaryJobLosses,highlight);
            memory.set("$option", "salPerform");
            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
            return;
        }
        Color color = playerFaction.getColor();
        Color bad = Misc.getNegativeHighlightColor();

        float crew = crewReplacer_Main.getJob(JobName).getAvailableCrewPower(playerFleet);////DONE for getting crew power
        float machinery = crewReplacer_Main.getJob(SecondJobName).getAvailableCrewPower(playerFleet);//playerFleet.getCargo().getCommodityQuantity(Commodities.HEAVY_MACHINERY);
        float fCrew = crew / reqCrew;
        if (fCrew < 0) fCrew = 0;
        if (fCrew > 1) fCrew = 1;

        float fMachinery = machinery / reqMachinery;
        if (fMachinery < 0) fMachinery = 0;
        if (fMachinery > 1) fMachinery = 1;

//		CommoditySpecAPI crewSpec = Global.getSector().getEconomy().getCommoditySpec(Commodities.CREW);
//		CommoditySpecAPI machinerySpec = Global.getSector().getEconomy().getCommoditySpec(Commodities.HEAVY_MACHINERY);

        float lossValue = reqCrew * fCrew * 5f;
        lossValue += (1f - debris.getParams().density / debris.getParams().baseDensity) * 500f;
        lossValue *= 0.5f + random.nextFloat();
        //lossValue *= StarSystemGenerator.getNormalRandom(random, 0.5f, 1.5f);

        WeightedRandomPicker<String> lossPicker = new WeightedRandomPicker<String>(random);
        lossPicker.add(Commodities.CREW, 10f + 100f * (1f - fMachinery));//HERE is getting Commodities.CREW, then looking for said crew in below while loop. when there is no crew, cant get crew losses.//only desides how mush crew power i will lose latter. gets crew lost number?
        lossPicker.add(Commodities.HEAVY_MACHINERY, 10f + 100f * fMachinery);
        CargoAPI losses = Global.getFactory().createCargo(true);
        float loss = 0;
        while (loss < lossValue) {//DONE this should be working. i looked at WeightedRandomPicker and it never looks at the number of items you have in your fleet, only at the weigts, and number of difrent types of items.
            String id = lossPicker.pick();
            CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(id);
            loss += spec.getBasePrice();
            losses.addCommodity(id, 1f);
        }
       /* String id = lossPicker.pick();
        String[] idTemp = {Commodities.CREW,Commodities.HEAVY_MACHINERY};//notHERE notHERE notHERE: THERE IS NO WAY IN HELL THIS WORKS RIGHT. I tried to keep it fathfull to the origanal, but i dont know how losspicker works.
        float[] losses2 = {10f + 100f * (1f - fMachinery),10f + 100f * fMachinery};
        float[] powerTemp = {crew,machinery};
        float totalLosses = losses2[0] + losses2[1];
        while(loss < lossValue){
            int a = 1;
            double ran = Math.random() * totalLosses;
            if(ran <= losses2[0]) {
                a = 0;
            }
            CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(idTemp[a]);
            loss += spec.getBasePrice();
            losses.addCommodity(idTemp[a],1f);
            powerTemp[a]--;
            if(powerTemp[a] <= 0){
                a++;
                a %= 2;
                Global.getSector().getEconomy().getCommoditySpec(idTemp[a]);
                while(loss < lossValue){
                    loss += spec.getBasePrice();
                    losses.addCommodity(idTemp[a],1f);
                }
            }
        }*/
        losses.sort();
        int crewLost = losses.getCrew();
        if (crewLost > 0) {
            losses.removeCrew(crewLost);
            crewLost *= playerFleet.getStats().getDynamic().getValue(Stats.NON_COMBAT_CREW_LOSS_MULT);
            if (crewLost < 1) crewLost = 1;
            losses.addCrew(crewLost);
        }
        int machineryLost = (int) losses.getCommodityQuantity(Commodities.HEAVY_MACHINERY);
        if (crewLost > crew) crewLost = (int) crew;
        if (machineryLost > machinery) machineryLost = (int) machinery;

        //old
        for (CargoStackAPI stack : losses.getStacksCopy()) {
            if(stack.getCommodityId().equals(ItemName)){
                crewLosses = crewReplacer_Main.getJob(JobName).automaticlyGetAndApplyCrewLost(playerFleet,(int)reqCrew,stack.getSize());////DONE remove items here here
            }else {
                secondaryJobLosses = crewReplacer_Main.getJob(SecondJobName).automaticlyGetAndApplyCrewLost(playerFleet,(int)reqMachinery,stack.getSize());
                //cargo.removeCommodity(stack.getCommodityId(), stack.getSize());//DONE. remove items here here
            }
        }
        //HERE might not need//NO IDEA if that was wize or not (to cut this set out)
        /*if(crewLosses.size() == 0){
            crewLosses = crew_replacer.automaticlyGetAndApplyCrewLosses(JobName,reqCrew,0,playerFleet);
        }*/
        crewLost = 0;
        for(int a = 0; a < crewLosses.size(); a++){
            if(crewLosses.get(a) > 0) {
                crewLost = 1;
                break;
            }
        }
        if (crewLost <= 0 && machineryLost <= 0) {//<-what? why <= 0? did i write this? HERE
            DisplayLosses(crewLost,machineryLost,crewLosses,secondaryJobLosses,highlight);
            memory.set("$option", "salPerform");
            FireBest.fire(null, dialog, memoryMap, "DialogOptionSelected");
        }

        //text.setFontInsignia();
        //text.addParagraph("An accident during the operation has resulted in the loss of ");
        //text.appendToLastParagraph("aaaaaaaaaaa");
        Global.getSoundPlayer().playSound("hit_solid", 1, 1, Global.getSoundPlayer().getListenerPos(), new Vector2f());
        DisplayLosses(crewLost,machineryLost,crewLosses,secondaryJobLosses,highlight);//this runs way i guess?

        options.clearOptions();
        options.addOption(CrewReplacer_StringHelper.getString(className,"checkAccidents",0), "salPerform");
        //FireBest.fire(null, dialog, memoryMap, "PerformSalvage");
        //FireBest.fire(null, dialog, memoryMap, "PerformSalvage");
    }

    //REQUIRED
    @Override
    protected MutableStat getValueRecoveryStat(boolean withSkillMultForRares) {
        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);
        MutableStat valueRecovery = new MutableStat(1f);
        int i = 0;

        float machineryContrib = 0.75f;
        valueRecovery.modifyPercent("base", -100f);
        if (machineryContrib < 1f) {
            valueRecovery.modifyPercent("base_positive", (int) Math.round(100f - 100f * machineryContrib), CrewReplacer_StringHelper.getString(className,"getValueRecoveryStat",0));
        }
        //valueRecovery.modifyPercent("base", -75f);

        float per = 0.5f;
        per = 1f;
        for (String commodityId : requiredRes.keySet()) {
            float required = requiredRes.get(commodityId);//notHERE gets crew power and matchenery required here
            float available = 0;
            if(commodityId.equals(ItemName)){
                available = (int) crewReplacer_Main.getJob(JobName).getAvailableCrewPower(playerFleet);//DONE
            }else {
                available = (int) crewReplacer_Main.getJob(SecondJobName).getAvailableCrewPower(playerFleet);
                //available = (int) cargo.getCommodityQuantity(commodityId);//DONE gets crew power in fleet, and matchenery in fleet.
            }
            if (required <= 0) continue;
            CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(commodityId);

            float val = Math.min(available / required, 1f) * per;
            int percent = (int) Math.round(val * 100f);
            //valueRecovery.modifyPercent("" + i++, percent, Misc.ucFirst(spec.getLowerCaseName()) + " requirements met");
            if (Commodities.HEAVY_MACHINERY.equals(commodityId)) {
                val = Math.min(available / required, machineryContrib) * per;
                percent = (int) Math.round(val * 100f);
                Color highlight = Misc.getHighlightColor();//DONE this is temp code, no idea what color this should be
                //crewReplacer_Main.getJob(SecondJobName).displayCrewAvailable(playerFleet,text,highlight);
                valueRecovery.modifyPercentAlways("" + i++, percent,CrewReplacer_StringHelper.getString(className,"getValueRecoveryStat",1, Misc.ucFirst(spec.getLowerCaseName())));
            } else {
                Color highlight = Misc.getHighlightColor();//DONE this is temp code, no idea what color this should be
                //crewReplacer_Main.getJob(JobName).displayCrewAvailable(playerFleet,text,highlight);
                valueRecovery.modifyMultAlways("" + i++, val, CrewReplacer_StringHelper.getString(className,"getValueRecoveryStat",2,Misc.ucFirst(spec.getLowerCaseName())));//DONE?? displays crew available?
            }
//			float val = Math.max(1f - available / required, 0f) * per;
//			int percent = -1 * (int) Math.round(val * 100f);
//			valueRecovery.modifyPercent("" + i++, percent, "Insufficient " + spec.getLowerCaseName());
        }

        boolean modified = false;
        if (withSkillMultForRares) {
            for (StatMod mod : playerFleet.getStats().getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).getFlatMods().values()) {
                modified = true;
                valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(mod.value * 100f), mod.desc);
            }
        }

        {
            for (StatMod mod : playerFleet.getStats().getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).getFlatMods().values()) {
                modified = true;
                valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(mod.value * 100f), mod.desc);
            }
        }
        if (!modified) {
            valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(0f),CrewReplacer_StringHelper.getString(className,"getValueRecoveryStat",3));
        }

        float fleetSalvageShips = getPlayerShipsSalvageModUncapped();
        valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(fleetSalvageShips * 100f),CrewReplacer_StringHelper.getString(className,"getValueRecoveryStat",4));

        return valueRecovery;
    }
    //REQUIRED
    @Override
    public void showCost() {
        //text.addParagraph("        -0");
        Color color = playerFaction.getColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color highlight = Misc.getHighlightColor();

        float pad = 3f;
        float opad = 10f;
        float small = 5f;

        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);

        text.addParagraph(CrewReplacer_StringHelper.getString(className,"showCost",0));

        ResourceCostPanelAPI cost = text.addCostPanel(CrewReplacer_StringHelper.getString(className,"showCost",1), COST_HEIGHT,
                color, playerFaction.getDarkUIColor());
        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);
        //text.addParagraph("        -1");
        for (String commodityId : requiredRes.keySet()) {
            int required = requiredRes.get(commodityId);
            int available = 0;
            if(commodityId.equals(ItemName)){
                //text.addParagraph("" + crew_replacer.getCrewPower(JobName));//why is this crosed out here? id it do something?
                available = (int) crewReplacer_Main.getJob(JobName).getAvailableCrewPower(playerFleet);//DONE//this DOSE NOT display every type of crew i have, but rather the overall crew. this is fine.

            }else {
                available = (int) crewReplacer_Main.getJob(SecondJobName).getAvailableCrewPower(playerFleet);
                //available = (int) cargo.getCommodityQuantity(commodityId);//DONE gets crew and machinery available, replace with crewpower available.
            }
            //text.addParagraph("        -2");
            Color curr = color;
            if (required > available) {//dont know why old line was like it was, so im keeping it there for now.
            //if (required > cargo.getQuantity(CargoItemType.RESOURCES, commodityId)) {//notHERE replace with availabe? it trys to get crew and machinery again why?
                curr = bad;
            }
            //text.addParagraph("        -3");
            cost.addCost(commodityId,CrewReplacer_StringHelper.getString(className,"showCost",2,""+required,""+available), curr);
        }
        cost.update();
        //text.addParagraph("        -4");

        MutableStat valueRecovery = getValueRecoveryStat(true);

        //rareRecovery.unmodify();
        int valuePercent = (int)Math.round(valueRecovery.getModifiedValue() * 100f);
        if (valuePercent < 0) valuePercent = 0;
        String valueString =CrewReplacer_StringHelper.getString(className,"showCost",3,""+valuePercent);
        Color valueColor = highlight;

        if (valuePercent < 100) {
            valueColor = bad;
        }

        TooltipMakerAPI info = text.beginTooltip();
        info.setParaSmallInsignia();
        info.addPara(CrewReplacer_StringHelper.getString(className,"showCost",4), 0f, valueColor, valueString);
        if (!valueRecovery.isUnmodified()) {
            info.addStatModGrid(300, 50, opad, small, valueRecovery, true, getModPrinter());
        }
        text.addTooltip();

        printSalvageModifiers();
        //text.addParagraph("        -5");
    }
    //REQUIRED
    @Override
    public void showCostDebrisField() {
        Color color = playerFaction.getColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color highlight = Misc.getHighlightColor();

        float pad = 3f;
        float opad = 10f;
        float small = 5f;

        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);

        //text.addParagraph("You receive a preliminary assessment of a potential salvage operation from the exploration crews.");

        ResourceCostPanelAPI cost = text.addCostPanel(CrewReplacer_StringHelper.getString(className,"showCostDebrisField",0), COST_HEIGHT,
                color, playerFaction.getDarkUIColor());
        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);

        for (String commodityId : requiredRes.keySet()) {
            int required = requiredRes.get(commodityId);
            int available;
            if(commodityId.equals(ItemName)){
                available = (int) crewReplacer_Main.getJob(JobName).getAvailableCrewPower(playerFleet);//DONE
            }else {
                available = (int) crewReplacer_Main.getJob(SecondJobName).getAvailableCrewPower(playerFleet);//DONE
                //available = (int) cargo.getCommodityQuantity(commodityId);//DONE gets available crew and machinery
            }
            Color curr = color;
            if (required > available) {//dont know why next line is as it is, so i replaced it
            //if (required > cargo.getQuantity(CargoItemType.RESOURCES, commodityId)) {//notHERE replace get crew with available? again why dose it get it a second time?
                curr = bad;
            }
            cost.addCost(commodityId,CrewReplacer_StringHelper.getString(className,"showCostDebrisField",1,""+required,""+available), curr);//DONE display crew power available
        }
        cost.update();


        MutableStat valueRecovery = getValueRecoveryStat(true);
        float overallMult = computeOverallMultForDebrisField();
        valueRecovery.modifyMult("debris_mult", overallMult,CrewReplacer_StringHelper.getString(className,"showCostDebrisField",2));
        //rareRecovery.unmodify();
        int valuePercent = (int)Math.round(valueRecovery.getModifiedValue() * 100f);
        if (valuePercent < 0) valuePercent = 0;
        String valueString =CrewReplacer_StringHelper.getString(className,"showCostDebrisField",3,""+valuePercent);
        Color valueColor = highlight;

        if (valuePercent < 100) {
            valueColor = bad;
        }

        TooltipMakerAPI info = text.beginTooltip();
        info.setParaSmallInsignia();
        info.addPara(CrewReplacer_StringHelper.getString(className,"showCostDebrisField",4), 0f, valueColor, valueString);
        if (!valueRecovery.isUnmodified()) {
            info.addStatModGrid(300, 50, opad, small, valueRecovery, true, getModPrinter());
        }
        text.addTooltip();

//		text.addParagraph("The density of the debris field affects both the amount resources and the number of rare items found.");

//		text.addParagraph("It's possible to scavenge using fewer crew and less machinery than required, but using fewer crew will reduce " +
//						  "the amount of salvage recovered, while having less machinery will increase the danger to crew.");

        printSalvageModifiers();

    }
    //requerd
    private void DisplayLosses(int crewLost,int machineryLost,ArrayList<Float> crewLosses,ArrayList<Float> secondaryJobLosses,Color highlight){
        if(crewLost != 0 || machineryLost != 0) {
            text.addParagraph(CrewReplacer_StringHelper.getString(className,"DisplayLosses",0));
        }
        //if(crewLost >= 0){
            //text.addParagraph("");
            crewReplacer_Main.getJob(JobName).displayCrewLost(playerFleet.getCargo(),crewLosses,text);
        //}
        //if(machineryLost >= 0){
            //text.addParagraph("");
            crewReplacer_Main.getJob(SecondJobName).displayCrewLost(playerFleet.getCargo(),secondaryJobLosses,text);
        //}
        /*if (crewLost <= 0) {
            text.appendToLastParagraph("" + machineryLost + " heavy machinery.");
            text.highlightInLastPara(highlight, "" + machineryLost);
        } else if (machineryLost <= 0) {//DONE display crew lost
            //crew_replacer.dislayCrewLosses(crewLosses,text,highlight);
            crew_replacer.dislayCrewLosses(crewLosses,text);
            //text.appendToLastParagraph("" + crewLost + " crew.");//old code
            //text.highlightInLastPara(highlight, "" + crewLost);//old code
        } else {//DONE display crew lost
            //crew_replacer.dislayCrewLosses(crewLosses,text,highlight);
            crew_replacer.dislayCrewLosses(crewLosses,text);
            text.appendToLastParagraph("and " + machineryLost + " heavy machinery.");
            text.highlightInLastPara(highlight, "" + machineryLost);
            //text.appendToLastParagraph("" + crewLost + " crew and " + machineryLost + " heavy machinery.");//old code
            //text.highlightInLastPara(highlight, "" + crewLost, "" + machineryLost);//old code
        }*/
    }

}