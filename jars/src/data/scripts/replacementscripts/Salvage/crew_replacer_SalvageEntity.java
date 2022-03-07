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

import data.scripts.crewReplacer_Main;
//import data.scripts.crew_replacer;
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
public class crew_replacer_SalvageEntity extends BaseCommandPlugin {
    private static String JobName = "salvage_main";//the name of the job in discription here
    private static String SecondJobName = "salvage_Secondary";
    private static String ItemName = "crew";//every time i look for / remove or add this item, get crew_replacer.getPower(JobName);
    public static float SALVAGE_DETECTION_MOD_FLAT = 1000;

    public static int FIELD_RADIUS_FOR_BASE_REQ = 200;
    public static int FIELD_RADIUS_FOR_MAX_REQ = 1000;
    public static int FIELD_RADIUS_MAX_REQ_MULT = 10;
    public static float FIELD_MIN_SALVAGE_MULT = 0.01f;



    //public static float FIELD_SALVAGE_FRACTION_PER_ATTEMPT = 0.5f;
    public static float FIELD_SALVAGE_FRACTION_PER_ATTEMPT = 1f;

    public static float FIELD_CONTENT_MULTIPLIER_AFTER_SALVAGE = 0.25f;
    //public static float FIELD_CONTENT_MULTIPLIER_AFTER_DEMOLITION = 0.65f;
    public static float FIELD_CONTENT_MULTIPLIER_AFTER_DEMOLITION = 1f;

    public static int BASE_MACHINERY = 10;
    public static int BASE_CREW = 30;
    public static int MIN_MACHINERY = 5;

    public static float COST_HEIGHT = 67;


    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected SalvageEntityGenDataSpec spec;
    protected CargoAPI cargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    private DebrisFieldTerrainPlugin debris;
    private Map<String, MemoryAPI> memoryMap;


    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();

        String specId = entity.getCustomEntityType();
        if (specId == null || entity.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPEC_ID_OVERRIDE)) {
            specId = entity.getMemoryWithoutUpdate().getString(MemFlags.SALVAGE_SPEC_ID_OVERRIDE);
        }
        spec = SalvageEntityGeneratorOld.getSalvageSpec(specId);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        cargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        Object test = entity.getMemoryWithoutUpdate().get(MemFlags.SALVAGE_DEBRIS_FIELD);
        if (test instanceof DebrisFieldTerrainPlugin) {
            debris = (DebrisFieldTerrainPlugin) test;
        }

        if (command.equals("showCost")) {
            //text.addParagraph("->zero<-");
            if (debris == null) {
                showCost();
            } else {
                //showCost();
                showCostDebrisField();
            }
        } else if (command.equals("performSalvage")) {
            //text.addParagraph("->one<-");
            performSalvage();
        } else if (command.equals("descDebris")) {
            //text.addParagraph("->two<-");
            showDebrisDescription();
        } else if (command.equals("checkAccidents")) {
            //text.addParagraph("->three<-");
            checkAccidents();
        } else if (command.equals("demolish")) {
            //text.addParagraph("->four<-");
            demolish();
        } else if (command.equals("canBeMadeRecoverable")) {
            //text.addParagraph("->five<-");
            return canBeMadeRecoverable();
        } else if (command.equals("showRecoverable")) {
            //text.addParagraph("->six<-");
            showRecoverable();
        }

        return true;
    }

    private void demolish() {
        boolean isDebrisField = Entities.DEBRIS_FIELD_SHARED.equals(entity.getCustomEntityType());
        if (!isDebrisField) {
            convertToDebrisField(FIELD_CONTENT_MULTIPLIER_AFTER_DEMOLITION);

            Global.getSoundPlayer().playSound("hit_heavy", 1, 1, Global.getSoundPlayer().getListenerPos(), new Vector2f());

            dialog.dismiss();

//			text.addParagraph("Salvage crews set targeting beacons at key points in the structure, " +
//					"and you give the order to fire once everyone is safely off.");
//			text.addParagraph("Salvage crews set targeting beacons at key points in the structure.");
//			options.clearOptions();
//			options.addOption("Leave", "defaultLeave");
//			options.setShortcut("defaultLeave", Keyboard.KEY_ESCAPE, false, false, false, true);
        }
    }

    private float getAccidentProbability() {
        if (debris == null) return 0f;
        float accidentProbability = 0.2f + 0.8f * (1f - debris.getParams().density);
        if (accidentProbability > 0.9f) accidentProbability = 0.9f;
        return accidentProbability;
    }

    private void checkAccidents() {
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
        options.addOption("Continue", "salPerform");
        //FireBest.fire(null, dialog, memoryMap, "PerformSalvage");
        //FireBest.fire(null, dialog, memoryMap, "PerformSalvage");
    }

    private void showDebrisDescription() {
        if (debris == null) return;

        float daysLeft = debris.getDaysLeft();
        if (daysLeft >= 1000) {
            text.addParagraph("The field appears stable and will not drift apart any time soon.");
        } else {
            String atLeastTime = Misc.getAtLeastStringForDays((int) daysLeft);
            text.addParagraph("The field is unstable, but should not drift apart for " + atLeastTime + ".");
        }

//		boolean stillHot = debris.getGlowDaysLeft() > 0;
//		switch (debris.getParams().source) {
//		case BATTLE:
//			text.addParagraph("Pieces of ships, weapons, and escape pods litter the starscape.");
//			break;
//		case MIXED:
//			text.addParagraph("Pieces of ships, weapons, and escape pods litter the starscape.");
//			break;
//		case PLAYER_SALVAGE:
//			break;
//		case SALVAGE:
//			break;
//		}

//		if (stillHot) {
//			text.appendToLastParagraph(" Some of the pieces of debris are still radiating heat, making any salvage operations more dangerous.");
//		}

        float lootValue = 0;
        for (DropData data : debris.getEntity().getDropValue()) {
            lootValue += data.value;
        }
        for (DropData data : debris.getEntity().getDropRandom()) {
            if (data.value > 0) {
                lootValue += data.value;
            } else {
                lootValue += 500; // close enough
            }
        }
        float d = debris.getParams().density;

        lootValue *= d;

        // doesn't work because "extra" expires
//		ExtraSalvage extra = BaseSalvageSpecial.getExtraSalvage(memoryMap);
//		if (extra != null) {
//			for (CargoStackAPI stack : extra.cargo.getStacksCopy()) {
//				lootValue += stack.getBaseValuePerUnit() * stack.getSize();
//			}
//		}

        if (lootValue < 500) {
            text.appendToLastParagraph(" Long-range scans indicate it's unlikely anything of much value would be found inside.");
            text.highlightLastInLastPara("unlikely", Misc.getNegativeHighlightColor());
        } else if (lootValue < 2500) {
            text.appendToLastParagraph(" Long-range scans indicate it's possible something of value could be found inside.");
            text.highlightLastInLastPara("possible", Misc.getHighlightColor());
        } else {
            text.appendToLastParagraph(" Long-range scans indicate it's likely something of value could be found inside.");
            text.highlightLastInLastPara("likely", Misc.getPositiveHighlightColor());
        }

        float accidentProbability = getAccidentProbability();
        if (accidentProbability <= 0.2f) {
            //text.addParagraph("There are indications of some easy pickings to be had, and the risk of an accident during a salvage operation is low.");
            text.addPara("There are indications of some easy pickings to be had, and the risk of an accident during a salvage operation is low.",
                    Misc.getPositiveHighlightColor(), "low");
        } else if (accidentProbability < 0.7f) {
            text.addPara("There are indications that what salvage is to be had may not be easy to get to, " +
                    "and there's %s risk involved in running a salvage operation.", Misc.getHighlightColor(), "significant");
        } else {
            text.addPara("The salvage that remains is extremely difficult to get to, " +
                    "and there's %s risk involved in running a salvage operation.", Misc.getNegativeHighlightColor(), "high");
        }
    }

    public static Map<String, Integer> computeRequiredToSalvage(SectorEntityToken entity) {
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();

        String specId = entity.getCustomEntityType();
        if (specId == null || entity.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPEC_ID_OVERRIDE)) {
            specId = entity.getMemoryWithoutUpdate().getString(MemFlags.SALVAGE_SPEC_ID_OVERRIDE);
        }
        SalvageEntityGenDataSpec spec = SalvageEntityGeneratorOld.getSalvageSpec(specId);
        float mult = 1f + spec.getSalvageRating() * 9f;

        Object test = entity.getMemoryWithoutUpdate().get(MemFlags.SALVAGE_DEBRIS_FIELD);
        if (test instanceof DebrisFieldTerrainPlugin) {
            DebrisFieldTerrainPlugin debris = (DebrisFieldTerrainPlugin) test;
            mult = getDebrisReqMult(debris);
        }

        int crew = Math.round((int) (BASE_CREW * mult) / 10f) * 10;
        int machinery = Math.round((int) (BASE_MACHINERY * mult) / 10f) * 10;
        CampaignFleetAPI Fleet = Global.getSector().getPlayerFleet();
        int temp = crew;
        crew -= (int) Misc.getFleetwideTotalMod(Fleet, Stats.getSurveyCostReductionId(Commodities.CREW), 0);
        if(crew < 0){
            //temp -= crew;
            crew = 0;
        }else{
            temp = (int) Misc.getFleetwideTotalMod(Fleet, Stats.getSurveyCostReductionId(Commodities.CREW), 0);
        }
        result.put(Commodities.CREW, crew);
        result.put(Commodities.HEAVY_MACHINERY, machinery + temp);

        return result;
    }

    protected MutableStat getValueRecoveryStat(boolean withSkillMultForRares) {
        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);
        MutableStat valueRecovery = new MutableStat(1f);
        int i = 0;

        float machineryContrib = 0.75f;
        valueRecovery.modifyPercent("base", -100f);
        if (machineryContrib < 1f) {
            valueRecovery.modifyPercent("base_positive", (int) Math.round(100f - 100f * machineryContrib), "Base effectiveness");
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
                valueRecovery.modifyPercentAlways("" + i++, percent, Misc.ucFirst(spec.getLowerCaseName()) + " available");
            } else {
                Color highlight = Misc.getHighlightColor();//DONE this is temp code, no idea what color this should be
                //crewReplacer_Main.getJob(JobName).displayCrewAvailable(playerFleet,text,highlight);
                valueRecovery.modifyMultAlways("" + i++, val, Misc.ucFirst(spec.getLowerCaseName()) + " available");//DONE?? displays crew available?
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
            valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(0f), "Salvaging skill");
        }

        float fleetSalvageShips = getPlayerShipsSalvageModUncapped();
        valueRecovery.modifyPercentAlways("" + i++, (int) Math.round(fleetSalvageShips * 100f), "Fleetwide salvaging capability");

        return valueRecovery;
    }

//	protected StatBonus getRareRecoveryStat() {
//		StatBonus rareRecovery = new StatBonus();
//		int i = 0;
//		for (StatMod mod : playerFleet.getStats().getDynamic().getMod(Stats.SALVAGE_MAX_RATING).getFlatBonuses().values()) {
//			rareRecovery.modifyPercent("" + i++, (int) Math.round(mod.value * 100f), mod.desc);
//		}
//		return rareRecovery;
//	}

    public void showCost() {
        //text.addParagraph("        -0");
        Color color = playerFaction.getColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color highlight = Misc.getHighlightColor();

        float pad = 3f;
        float opad = 10f;
        float small = 5f;

        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);

        text.addParagraph("You receive a preliminary assessment of a potential salvage operation from the exploration crews.");

        ResourceCostPanelAPI cost = text.addCostPanel("Crew & machinery: required (available)", COST_HEIGHT,
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
            cost.addCost(commodityId, "" + required + " (" + available + ")", curr);
        }
        cost.update();
        //text.addParagraph("        -4");

        MutableStat valueRecovery = getValueRecoveryStat(true);

        //rareRecovery.unmodify();
        int valuePercent = (int)Math.round(valueRecovery.getModifiedValue() * 100f);
        if (valuePercent < 0) valuePercent = 0;
        String valueString = "" + valuePercent + "%";
        Color valueColor = highlight;

        if (valuePercent < 100) {
            valueColor = bad;
        }

        TooltipMakerAPI info = text.beginTooltip();
        info.setParaSmallInsignia();
        info.addPara("Resource recovery effectiveness: %s", 0f, valueColor, valueString);
        if (!valueRecovery.isUnmodified()) {
            info.addStatModGrid(300, 50, opad, small, valueRecovery, true, getModPrinter());
        }
        text.addTooltip();

        printSalvageModifiers();
        //text.addParagraph("        -5");
    }

    protected StatModValueGetter getModPrinter() {
        return new StatModValueGetter() {
            boolean percent = false;
            public String getPercentValue(StatMod mod) {
                percent = true;

                // should make it not shown; it's a "base" value that has to be applied to make the calculations work with multipliers
                if (mod.desc == null || mod.desc.isEmpty()) return "";

                String prefix = mod.getValue() >= 0 ? "+" : "";
                return prefix + (int)(mod.getValue()) + "%";
            }
            public String getMultValue(StatMod mod) {percent = false; return null;}
            public String getFlatValue(StatMod mod) {percent = false; return null;}
            public Color getModColor(StatMod mod) {
                if ((!percent && mod.getValue() < 1f) || mod.getValue() < 0) return Misc.getNegativeHighlightColor();
                return null;
            }
        };
    }

    protected void printSalvageModifiers() {

        float fuelMult = playerFleet.getStats().getDynamic().getValue(Stats.FUEL_SALVAGE_VALUE_MULT_FLEET);
        String fuelStr = "" + (int)Math.round((fuelMult - 1f) * 100f) + "%";

        float rareMult = playerFleet.getStats().getDynamic().getValue(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE);
        String rareStr = "" + (int)Math.round((rareMult - 1f) * 100f) + "%";

        if (fuelMult > 1f && rareMult > 1f) {
            text.addPara("Your fleet also has a %s bonus to the amount of fuel recovered, and " +
                            "a %s bonus to the number of rare items found.",
                    Misc.getHighlightColor(), fuelStr, rareStr);
        } else if (fuelMult > 1) {
            text.addPara("Your fleet also has a %s bonus to the amount of fuel recovered.",
                    Misc.getHighlightColor(), fuelStr);
        } else if (rareMult > 1) {
            text.addPara("Your fleet also has a %s bonus to the number of rare items found.",
                    Misc.getHighlightColor(), rareStr);
        }

        if (debris != null) {
            text.addParagraph("The density of the debris field affects both the amount resources and the number of rare items found.");
        } else {
            text.addPara("The recovery effectiveness does not affect the chance of finding rare and valuable items.");
        }

    }

    public void showCostDebrisField() {
        Color color = playerFaction.getColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color highlight = Misc.getHighlightColor();

        float pad = 3f;
        float opad = 10f;
        float small = 5f;

        Map<String, Integer> requiredRes = computeRequiredToSalvage(entity);

        //text.addParagraph("You receive a preliminary assessment of a potential salvage operation from the exploration crews.");

        ResourceCostPanelAPI cost = text.addCostPanel("Crew & machinery: required (available)", COST_HEIGHT,
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
            cost.addCost(commodityId, "" + required + " (" + available + ")", curr);//DONE display crew power available
        }
        cost.update();


        MutableStat valueRecovery = getValueRecoveryStat(true);
        float overallMult = computeOverallMultForDebrisField();
        valueRecovery.modifyMult("debris_mult", overallMult, "Debris field density");
        //rareRecovery.unmodify();
        int valuePercent = (int)Math.round(valueRecovery.getModifiedValue() * 100f);
        if (valuePercent < 0) valuePercent = 0;
        String valueString = "" + valuePercent + "%";
        Color valueColor = highlight;

        if (valuePercent < 100) {
            valueColor = bad;
        }

        TooltipMakerAPI info = text.beginTooltip();
        info.setParaSmallInsignia();
        info.addPara("Scavenging effectiveness: %s", 0f, valueColor, valueString);
        if (!valueRecovery.isUnmodified()) {
            info.addStatModGrid(300, 50, opad, small, valueRecovery, true, getModPrinter());
        }
        text.addTooltip();

//		text.addParagraph("The density of the debris field affects both the amount resources and the number of rare items found.");

//		text.addParagraph("It's possible to scavenge using fewer crew and less machinery than required, but using fewer crew will reduce " +
//						  "the amount of salvage recovered, while having less machinery will increase the danger to crew.");

        printSalvageModifiers();

    }

    protected float computeOverallMultForDebrisField() {
        float overallMult = 1f;
        if (debris != null) {
//			Map<String, Integer> reqs = computeRequiredToSalvage(entity);
//			float crewMax = 1f;
//			if (reqs.get(Commodities.CREW) != null) {
//				crewMax = reqs.get(Commodities.CREW);
//			}
//			float crew = playerFleet.getCargo().getCrew();
//			float f = crew / crewMax;
//			if (f < 0) f = 0;
//			if (f > 1) f = 1;
//
//			//if (Global.getSettings().isDevMode()) f = 1f;

            float f = 1f;
            DebrisFieldParams params = debris.getParams();
            if (params.baseDensity > 0) {
                overallMult = params.density / params.baseDensity * f * FIELD_SALVAGE_FRACTION_PER_ATTEMPT;
            } else {
                overallMult = 0f;
            }
            if (overallMult < FIELD_MIN_SALVAGE_MULT) overallMult = FIELD_MIN_SALVAGE_MULT;
        }
        return overallMult;
    }


    public void performSalvage() {
        //text.addParagraph("         -0");
        //text.addParagraph("TESTTESTTESTTESTTESTTESTTESTTEST");
        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        Random random = Misc.getRandom(seed, 100);

        Misc.stopPlayerFleet();
        //text.addParagraph("         -1");
//		if (Global.getSettings().isDevMode()) {
//			random = Misc.random;
//		}

//		float salvageRating = spec.getSalvageRating();
//		float valueMultFleet = playerFleet.getStats().getDynamic().getValue(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE);
//		float valueModShips = getPlayerShipsSalvageMod(salvageRating);

        MutableStat valueRecovery = getValueRecoveryStat(true);
        float valueMultFleet = valueRecovery.getModifiedValue();
        float rareItemSkillMult = playerFleet.getStats().getDynamic().getValue(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE);
        //text.addParagraph("         -2");
        List<DropData> dropValue = new ArrayList<DropData>(spec.getDropValue());
        List<DropData> dropRandom = new ArrayList<DropData>(spec.getDropRandom());
        dropValue.addAll(entity.getDropValue());
        dropRandom.addAll(entity.getDropRandom());

//		DropData d = new DropData();
//		d.group = "misc_test";
//		d.chances = 1500;
//		dropRandom.add(d);

        //text.addParagraph("         -3");
        float overallMult = computeOverallMultForDebrisField();
        if (debris != null) {
            // to avoid same special triggering over and over while scavenging through
            // the same debris field repeatedly
            BaseCommandPlugin.getEntityMemory(memoryMap).unset(MemFlags.SALVAGE_SPECIAL_DATA);
        }
        //text.addParagraph("         -4");
        float fuelMult = playerFleet.getStats().getDynamic().getValue(Stats.FUEL_SALVAGE_VALUE_MULT_FLEET);
        CargoAPI salvage = generateSalvage(random, valueMultFleet, rareItemSkillMult, overallMult, fuelMult, dropValue, dropRandom);

        //ExtraSalvage extra = BaseSalvageSpecial.getExtraSalvage(memoryMap);
        CargoAPI extra = BaseSalvageSpecial.getCombinedExtraSalvage(memoryMap);
        salvage.addAll(extra);
        BaseSalvageSpecial.clearExtraSalvage(memoryMap);
        if (!extra.isEmpty()) {
            ListenerUtil.reportExtraSalvageShown(entity);
        }
        //text.addParagraph("         -5");
        //salvage.addCommodity(Commodities.ALPHA_CORE, 1);

        if (debris != null) {
            debris.getParams().density -= overallMult;
            if (debris.getParams().density < 0) debris.getParams().density = 0;

            debris.getEntity().getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, random.nextLong());
            //System.out.println("Post-salvage density: " + debris.getParams().density);
            debris.setScavenged(true);
        }
        //text.addParagraph("         -6");
        //if (loot)
        if (!salvage.isEmpty()) {
            dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, new CoreInteractionListener() {
                public void coreUIDismissed() {
                    long xp = 0;
                    if (entity.hasSalvageXP()) {
                        xp = (long) (float) entity.getSalvageXP();
                    } else if (spec != null && spec.getXpSalvage() > 0) {
                        xp = (long) spec.getXpSalvage();
                    }
                    if (!memory.contains("$doNotDismissDialogAfterSalvage")) {
                        dialog.dismiss();
                        dialog.hideTextPanel();
                        dialog.hideVisualPanel();

                        if (xp > 0) {
                            Global.getSector().getPlayerPerson().getStats().addXP(xp);
                        }
                    } else {
                        if (xp > 0) {
                            Global.getSector().getPlayerPerson().getStats().addXP(xp, dialog.getTextPanel());
                        }
                    }
//					if (entity.hasSalvageXP()) {
//						Global.getSector().getPlayerPerson().getStats().addXP((long) (float) entity.getSalvageXP());
//					} else if (spec != null && spec.getXpSalvage() > 0) {
//						Global.getSector().getPlayerPerson().getStats().addXP((long) spec.getXpSalvage());
//					}
                    //Global.getSector().setPaused(false);
                }
            });
            options.clearOptions();
            dialog.setPromptText("");
        } else {
            text.addParagraph("Operations conclude with nothing of value found.");
            options.clearOptions();
            String leave = "Leave";
            if (memory.contains("$salvageLeaveText")) {
                leave = memory.getString("$salvageLeaveText");
            }
            options.addOption(leave, "defaultLeave");
            options.setShortcut("defaultLeave", Keyboard.KEY_ESCAPE, false, false, false, true);
        }
        //text.addParagraph("         -7");

        boolean isDebrisField = Entities.DEBRIS_FIELD_SHARED.equals(entity.getCustomEntityType());
        if (!isDebrisField) {
            if (!spec.hasTag(Tags.SALVAGE_ENTITY_NO_DEBRIS)) {
                convertToDebrisField(FIELD_CONTENT_MULTIPLIER_AFTER_SALVAGE);
            } else {
                if (!spec.hasTag(Tags.SALVAGE_ENTITY_NO_REMOVE)) {
                    Misc.fadeAndExpire(entity, 1f);
                }
            }
        }

        if (playerFleet != null) {
            playerFleet.getStats().addTemporaryModFlat(0.25f, "salvage_ops",
                    "Recent salvage operation", SALVAGE_DETECTION_MOD_FLAT,
                    playerFleet.getStats().getDetectedRangeMod());
            Global.getSector().addPing(playerFleet, "noticed_player");
        }
    }


    public void convertToDebrisField(float valueMult) {
        convertToDebrisField(null, valueMult);
    }

    public void convertToDebrisField(Random random, float valueMult) {
        if (random == null) random = new Random();

        Misc.fadeAndExpire(entity, 1f);

        float salvageRating = spec.getSalvageRating();
        //entity.addTag(Tags.NON_CLICKABLE);

        float debrisFieldRadius = 200f + salvageRating * 400f;

        float density = 0.5f + salvageRating * 0.5f;
        density = 1f;
        if (valueMult <= FIELD_CONTENT_MULTIPLIER_AFTER_SALVAGE) {
            density = 0.5f + salvageRating * 0.5f;
        }

        float duration = 10f + salvageRating * 20f;

        DebrisFieldParams params = new DebrisFieldParams(debrisFieldRadius, density, duration, duration * 0.5f);
        params.source = DebrisFieldSource.PLAYER_SALVAGE;

//		params.minSize = 12;
//		params.maxSize = 16;
//		params.defenderProb = 1;
//		params.minStr = 20;
//		params.maxStr = 30;
//		params.maxDefenderSize = 1;

        float xp = spec.getXpSalvage() * 0.25f;
        if (entity.hasSalvageXP()) {
            xp = entity.getSalvageXP() * 0.25f;
        }
        if (xp >= 10) {
            params.baseSalvageXP = (long) xp;
        }

        SectorEntityToken debris = Misc.addDebrisField(entity.getContainingLocation(), params, null);

        //ExtraSalvage extra = BaseSalvageSpecial.getExtraSalvage(memoryMap);
        CargoAPI extra = BaseSalvageSpecial.getCombinedExtraSalvage(memoryMap);
        if (extra != null && !extra.isEmpty()) {
            // don't prune extra cargo - it could have come from not recovering ships,
            // and so could've been gotten by recovering and then stripping/scuttling them
            // so shouldn't punish shortcutting that process
            // (this can happen when "pound into scrap" vs ship derelict)
//			CargoAPI extraCopy = Global.getFactory().createCargo(true);
//			for (CargoStackAPI stack : extra.cargo.getStacksCopy()) {
//				float qty = stack.getSize();
//				qty *= valueMult;
//				if (qty < 1) {
//					if (random.nextFloat() >= qty) continue;
//					qty = 1;
//				} else {
//					qty = (int) qty;
//				}
//				extraCopy.addItems(stack.getType(), stack.getData(), qty);
//			}
//			BaseSalvageSpecial.setExtraSalvage(extraCopy, debris.getMemoryWithoutUpdate(), -1f);
            //BaseSalvageSpecial.addExtraSalvage(extra.cargo, debris.getMemoryWithoutUpdate(), -1f);
            BaseSalvageSpecial.addExtraSalvage(extra, debris.getMemoryWithoutUpdate(), -1f);
        }

//		int count = 0;
//		for (CampaignTerrainAPI curr : entity.getContainingLocation().getTerrainCopy()) {
//			if (curr.getPlugin() instanceof DebrisFieldTerrainPlugin) {
//				count++;
//			}
//		}
        //System.out.println("DEBRIS: " + count);

        debris.setSensorProfile(null);
        debris.setDiscoverable(null);
        //debris.setDiscoveryXP(123f);

        debris.setFaction(entity.getFaction().getId());

        debris.getDropValue().clear();
        debris.getDropRandom().clear();

        for (DropData data : spec.getDropValue()) {
            DropData copy = data.clone();
            copy.valueMult = valueMult;
            debris.addDropValue(data.clone());
        }
        for (DropData data : spec.getDropRandom()) {
            DropData copy = data.clone();
            copy.valueMult = valueMult;
            debris.addDropRandom(copy);
        }

        for (DropData data : entity.getDropValue()) {
            DropData copy = data.clone();
            copy.valueMult = valueMult;
            debris.addDropValue(data.clone());
        }
        for (DropData data : entity.getDropRandom()) {
            DropData copy = data.clone();
            copy.valueMult = valueMult;
            debris.addDropRandom(copy);
        }
        //debris.addDropRandom("weapons_test", 10);

        if (entity.getOrbit() != null) {
            debris.setOrbit(entity.getOrbit().makeCopy());
        } else {
            debris.getLocation().set(entity.getLocation());
        }

        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        if (seed != 0) {
            debris.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, Misc.getRandom(seed, 150).nextLong());
        }
    }




    public static float getPlayerShipsSalvageModUncapped() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        //float valueModShips = Misc.getFleetwideTotalMod(playerFleet, Stats.SALVAGE_VALUE_MULT_MOD, 0f);
        float valueModShips = RepairGantry.getAdjustedGantryModifier(playerFleet, null, 0);
        return valueModShips;
    }
//	public static float getPlayerShipsSalvageMod(float salvageRating) {
//		CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
//		float valueModShips = Misc.getFleetwideTotalMod(playerFleet, Stats.SALVAGE_VALUE_MULT_MOD, 0f);
//		if (valueModShips > salvageRating) {
//			valueModShips = salvageRating;
//		}
//		return valueModShips;
//	}

    public static float getDebrisReqMult(DebrisFieldTerrainPlugin field) {
//		public static int FIELD_RADIUS_FOR_BASE_REQ = 200;
//		public static int FIELD_RADIUS_FOR_MAX_REQ = 1000;
//		public static int FIELD_RADIUS_MAX_REQ_MULT = 10;
        float r = field.getParams().bandWidthInEngine;
        float f = (r - FIELD_RADIUS_FOR_BASE_REQ) / (FIELD_RADIUS_FOR_MAX_REQ - FIELD_RADIUS_FOR_BASE_REQ);
        if (f < 0) f = 0;
        if (f > 1) f = 1;

        float mult = 1f + (FIELD_RADIUS_MAX_REQ_MULT - 1f) * f;
        return mult;
    }

    //	public static CargoAPI generateSalvage(Random random, float valueMult, List<DropData> dropValue, List<DropData> dropRandom) {
//		return generateSalvage(random, valueMult, 1f, dropValue, dropRandom);
//	}
    public static CargoAPI generateSalvage(Random random, float valueMult, float overallMult, float fuelMult, List<DropData> dropValue, List<DropData> dropRandom) {
        return generateSalvage(random, valueMult, 1f, overallMult, fuelMult, dropValue, dropRandom);
    }
    public static CargoAPI generateSalvage(Random random, float valueMult, float randomMult,
                                           float overallMult, float fuelMult, List<DropData> dropValue, List<DropData> dropRandom) {
        if (random == null) random = new Random();
        CargoAPI result = Global.getFactory().createCargo(true);


        if (Misc.isEasy()) {
            overallMult *= Global.getSettings().getFloat("easySalvageMult");
        }
//		CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        //overallMult = 1f;

//		float valueMultFleet = playerFleet.getStats().getDynamic().getValue(Stats.SALVAGE_VALUE_MULT_FLEET);
//		float valueModShips = getPlayerShipsSalvageMod(salvageRating);

        // check dropRandom first so that changing the drop value by dropping off crew/machinery
        // does not change the RNG for dropRandom
        if (dropRandom != null) {
            for (DropData data : dropRandom) {
                //if (random.nextFloat() < data.valueMult) continue;

                int chances = data.chances;
                if (data.maxChances > chances) {
                    chances = chances + random.nextInt(data.maxChances - chances + 1);
                }
//				if (data.group.endsWith("misc_test")) {
//					System.out.println("fewfwefwe");
//				}
                //WeightedRandomPicker<DropGroupRow> picker = DropGroupRow.getPicker(data.group);

                float modifiedChances = chances;
                modifiedChances *= overallMult;
                if (data.value <= 0) {
                    modifiedChances *= randomMult;
                }
                modifiedChances *= data.valueMult;
                float rem = modifiedChances - (int) modifiedChances;

                chances = (int) modifiedChances + (random.nextFloat() < rem ? 1 : 0);

                WeightedRandomPicker<DropGroupRow> picker = data.getCustom();
                if (picker == null && data.group == null) continue; // meant for custom, but empty
                if (picker == null) {
                    picker = DropGroupRow.getPicker(data.group);
                }

                Random innerRandom = Misc.getRandom(random.nextLong(), 5);
                //innerRandom = random;
                picker.setRandom(innerRandom);
                for (int i = 0; i < chances; i++) {
//					if (random.nextFloat() > overallMult) continue;
//					if (random.nextFloat() > data.valueMult) continue;

                    DropGroupRow row = picker.pick();
                    if (row.isMultiValued()) {
                        row = row.resolveToSpecificItem(innerRandom);
                    }

                    if (row.isNothing()) continue;

                    float baseUnitValue = row.getBaseUnitValue();

                    float qty = 1f;
                    if (data.value > 0) {
                        float randMult = StarSystemGenerator.getNormalRandom(innerRandom, 0.5f, 1.5f);
                        //qty = (data.value * randMult * valueMult * overallMult) / baseUnitValue;
                        // valueMult and overallMult are considered in figuring out number of chances to roll
                        qty = (data.value * valueMult * randMult) / baseUnitValue;
                        qty = (int) qty;
                        if (valueMult <= 0) continue;
                        if (qty < 1) qty = 1;
                    }


                    if (row.isWeapon()) {
                        result.addWeapons(row.getWeaponId(), (int) qty);
//					} else if (row.isHullMod()) {
//						result.addItems(CargoItemType.MOD_SPEC, row.getHullModId(), (int) qty);
                    } else if (row.isFighterWing()) {
                        result.addItems(CargoItemType.FIGHTER_CHIP, row.getFighterWingId(), (int) qty);
                    } else if (row.isSpecialItem()) {
                        if (Items.MODSPEC.equals(row.getSpecialItemId()) &&
                                result.getQuantity(CargoItemType.SPECIAL,
                                        new SpecialItemData(row.getSpecialItemId(), row.getSpecialItemData())) > 0) {
                            continue;
                        }
                        result.addItems(CargoItemType.SPECIAL,
                                new SpecialItemData(row.getSpecialItemId(), row.getSpecialItemData()), (int) qty);
                    } else {
                        result.addCommodity(row.getCommodity(), qty);
                    }
                }
            }
        }


        if (dropValue != null) {

            for (DropData data : dropValue) {
                //if (random.nextFloat() < data.valueMult) continue;

                float maxValue = data.value;

                // if value is 1, it's a "guaranteed pick one out of this usually-dropRandom group"
                // so still allow it even if valueMult is 0 due to a lack of heavy machinery
                // since dropRandom works w/ no machinery, too
                if (data.value > 1) {
                    maxValue *= valueMult;
                }

                maxValue *= overallMult;
                maxValue *= data.valueMult;

                float randMult = StarSystemGenerator.getNormalRandom(random, 0.5f, 1.5f);
                maxValue *= randMult;


                WeightedRandomPicker<DropGroupRow> picker = data.getCustom();
                if (picker == null && data.group == null) continue; // meant for custom, but empty
                if (picker == null) {
                    picker = DropGroupRow.getPicker(data.group);
                }
                picker.setRandom(random);
                float value = 0f;
                int nothingInARow = 0;
                while (value < maxValue && nothingInARow < 10) {
                    DropGroupRow row = picker.pick();
                    if (row.isMultiValued()) {
                        row = row.resolveToSpecificItem(random);
                    }
                    if (row.isNothing()) {
                        nothingInARow++;
                        continue;
                    } else {
                        nothingInARow = 0;
                    }
                    //System.out.println(nothingInARow);

                    float baseUnitValue = row.getBaseUnitValue();

                    float qty = 1f;
                    float currValue = baseUnitValue * qty;
                    value += currValue;

                    if (row.isWeapon()) {
                        if (value <= maxValue) {
                            result.addWeapons(row.getWeaponId(), (int) qty);
                        }
//					} else if (row.isHullMod()) {
//						if (value <= maxValue) {
//							result.addHullmods(row.getHullModId(), (int) qty);
//						}
                    } else if (row.isFighterWing()) {
                        if (value <= maxValue) {
                            result.addItems(CargoItemType.FIGHTER_CHIP, row.getFighterWingId(), (int) qty);
                        }
                    } else if (row.isSpecialItem()) {
                        if (Items.MODSPEC.equals(row.getSpecialItemId()) &&
                                result.getQuantity(CargoItemType.SPECIAL,
                                        new SpecialItemData(row.getSpecialItemId(), row.getSpecialItemData())) > 0) {
                            continue;
                        }
                        result.addItems(CargoItemType.SPECIAL,
                                new SpecialItemData(row.getSpecialItemId(), row.getSpecialItemData()), (int) qty);
                    } else {
                        if (value <= maxValue) {
                            result.addCommodity(row.getCommodity(), qty);
                        }
                    }
                }
            }
        }


        float fuel = result.getFuel();
        if (fuelMult > 1f) {
            result.addFuel((int) Math.round(fuel * (fuelMult - 1f)));
        }

        result.sort();

        return result;
    }


    public boolean canBeMadeRecoverable() {
        if (entity.getCustomPlugin() instanceof DerelictShipEntityPlugin) {

            //if (Misc.getSalvageSpecial(entity) != null) return false;

            if (Misc.getSalvageSpecial(entity) instanceof ShipRecoverySpecialData) {
                return false;
            }

//			int room = Global.getSettings().getMaxShipsInFleet() -
//			   		   Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().size();
//			if (room < 1) return false;

            DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) entity.getCustomPlugin();
            ShipVariantAPI variant = plugin.getData().ship.getVariant();
            if (variant != null && !Misc.isUnboardable(variant.getHullSpec())) {
                return true;
            }
        }
        return false;
    }


    public void showRecoverable() {

        Object prev = Misc.getSalvageSpecial(entity);
        if (prev != null) {
            Misc.setPrevSalvageSpecial(entity, prev);
        }

        ShipRecoverySpecialData data = new ShipRecoverySpecialData(null);
        DerelictShipEntityPlugin plugin = (DerelictShipEntityPlugin) entity.getCustomPlugin();
        data.addShip(plugin.getData().ship.clone());
        data.storyPointRecovery = true;
        Misc.setSalvageSpecial(entity, data);

        long seed = Misc.getSalvageSeed(entity);
        entity.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, seed);
    }

    private void DisplayLosses(int crewLost,int machineryLost,ArrayList<Float> crewLosses,ArrayList<Float> secondaryJobLosses,Color highlight){
        if(crewLost != 0 || machineryLost != 0) {
            text.addParagraph("An accident during the operation has resulted in the loss of ");
        }
        //if(crewLost >= 0){
            text.addParagraph("");
            crewReplacer_Main.getJob(JobName).displayCrewLost(crewLosses,text);
        //}
        //if(machineryLost >= 0){
            text.addParagraph("");
            crewReplacer_Main.getJob(SecondJobName).displayCrewLost(secondaryJobLosses,text);
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