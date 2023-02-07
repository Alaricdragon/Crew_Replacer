package com.fs.starfarer.api.impl.campaign.rulecmd.salvage;

import java.awt.Color;
import java.util.*;

import data.scripts.CrewReplacer_Log;
import data.scripts.crewReplacer_Job;
import data.scripts.crewReplacer_Main;
import data.scripts.replacementscripts.CrewReplacer_PlayerFleetPersonnelTracker;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.BattleAPI.BattleSide;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.GroundRaidTargetPickerDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.RuleBasedDialog;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI.ActionType;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.listeners.GroundRaidObjectivesListener.RaidResultData;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableStat.StatMod;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.BaseFIDDelegate;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig;
import com.fs.starfarer.api.impl.campaign.MilitaryResponseScript;
import com.fs.starfarer.api.impl.campaign.MilitaryResponseScript.MilitaryResponseParams;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.econ.RecentUnrest;
import com.fs.starfarer.api.impl.campaign.econ.impl.PopulationAndInfrastructure;
import com.fs.starfarer.api.impl.campaign.graid.DisruptIndustryRaidObjectivePluginImpl;
import com.fs.starfarer.api.impl.campaign.graid.GroundRaidObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Sounds;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.population.CoreImmigrationPluginImpl;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.SetStoryOption;
import com.fs.starfarer.api.impl.campaign.rulecmd.SetStoryOption.BaseOptionStoryPointActionDelegate;
import com.fs.starfarer.api.impl.campaign.rulecmd.SetStoryOption.StoryOptionParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.ShowDefaultVisual;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.StatModValueGetter;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
/*
PROLDOM:
the code is not runing (i think).
why? it has the same function names as the base functions.
solution? add a 2 to function names, in this and in the rules.csv.
this will seperate the names
 */
/*
       Everything function thats commeted out in its function is like that because i cant get it to work.
       this has nothing to do with salvage. this has everything to do with raiding, if i recall.
 */
/*
    found in rules.scv:
        -raidMenu
        -raidNonMarket
        -goBackToDefenses
        -raidRare
        -raidValuable
        -raidDisrupt
        -raidConfirm
        -raidConfirmContinue
        -raidNeverMind
        -raidResult
        -addContinueToRaidResultOption
   found with a reference to crew in them: HERE
        -raidNonMarket
        -raidMenu
        -raidValuable
            -pickedGroundRaidTargets
            -getProjectedMarineLosses
            -getMarineLossesColor
        -raidDisrupt
            -pickedGroundRaidTargets
            -getProjectedMarineLosses
            -getMarineLossesColor
        -getMarineLossesStat
        -getAverageMarineLosses
        -raidConfirm
        -raidConfirmContinue


    found something of interest here:
        -checkDebtEffect
        -applyDebtEffect
 */
public class CrewReplacerMarketCMD extends MarketCMD{//BaseCommandPlugin {
    static private boolean logsActive = true;
    static private String jobmain = "raiding_marines";//
    /*
    public static enum RaidType {
        CUSTOM_ONLY,
        VALUABLE,
        DISRUPT,
    }

    public static enum BombardType {
        TACTICAL,
        SATURATION,
    }

    public static enum RaidDangerLevel {
        NONE("None", "None", Misc.getPositiveHighlightColor(), 0f, 60f, 1),
        MINIMAL("Minimal", "Minimal", Misc.getPositiveHighlightColor(), 0.02f, 50f, 1),
        LOW("Low", "Light", Misc.getPositiveHighlightColor(), 0.04f, 40f, 2),
        MEDIUM("Medium", "Moderate", Misc.getHighlightColor(), 0.08f, 30f, 3),
        HIGH("High", "Heavy", Misc.getNegativeHighlightColor(), 0.16f, 20f, 5),
        EXTREME("Extreme", "Extreme", Misc.getNegativeHighlightColor(), 0.32f, 10f, 7);

        private static RaidDangerLevel [] vals = values();

        public String name;
        public String lossesName;
        public Color color;
        public float marineLossesMult;
        public int marineTokens;
        public float disruptionDays;
        private RaidDangerLevel(String name, String lossesName, Color color, float marineLossesMult, float disruptionDays, int marineTokens) {
            this.name = name;
            this.lossesName = lossesName;
            this.color = color;
            this.marineLossesMult = marineLossesMult;
            this.disruptionDays = disruptionDays;
            this.marineTokens = marineTokens;
        }

        public RaidDangerLevel next() {
            int index = this.ordinal() + 1;
            if (index >= vals.length) index = vals.length - 1;
            return vals[index];
        }
        public RaidDangerLevel prev() {
            int index = this.ordinal() - 1;
            if (index < 0) index = 0;
            return vals[index];
        }
    }

    public static class TempData {
        //public boolean canSurpriseRaid;
        //public boolean isSurpriseRaid;
        public boolean canRaid;
        public boolean canBombard;

        public int bombardCost;

        public int marinesLost;

        //public boolean canFail = false;
        //public float failProb = 0f;

        public float raidMult;

        public float attackerStr;
        public float defenderStr;

        public boolean nonMarket = false;

        public RaidType raidType = null;
        public BombardType bombardType = null;
        public CargoAPI raidLoot;
        public int xpGained;
        public Industry target = null;
        public List<FactionAPI> willBecomeHostile = new ArrayList<FactionAPI>();
        public List<Industry> bombardmentTargets = new ArrayList<Industry>();
        public List<GroundRaidObjectivePlugin> objectives = new ArrayList<GroundRaidObjectivePlugin>();
        public String contText;
        public String raidGoBackTrigger;
        public String raidContinueTrigger;
    }

    public static int HOSTILE_ACTIONS_TIMEOUT_DAYS = 60;
    public static int TACTICAL_BOMBARD_TIMEOUT_DAYS = 120;
    public static int SATURATION_BOMBARD_TIMEOUT_DAYS = 365;

    public static int MIN_MARINE_TOKENS = 1;
    public static float RE_PER_MARINE_TOKEN = 0.1f;
    public static int MAX_MARINE_TOKENS = 10;
    public static float LOSS_REDUCTION_PER_RESERVE_TOKEN = 0.05f;
    public static float LOSS_INCREASE_PER_RAID = 0.5f;
    public static float MAX_MARINE_LOSSES = 0.8f;

    public static float MIN_RE_TO_REDUCE_MARINE_LOSSES = 0.5f;
    public static float MAX_MARINE_LOSS_REDUCTION_MULT = 0.05f;

    // for causing deficit; higher value means less units need to be raided to cause same deficit
    public static float ECON_IMPACT_MULT = 1f;

    public static float QUANTITY_MULT_NORMAL = 1f;
    public static float QUANTITY_MULT_EXCESS = 2f;
    public static float QUANTITY_MULT_DEFICIT = -0.5f;
    public static float QUANTITY_MULT_OVERALL = 0.1f;


    public static String ENGAGE = "mktEngage";

    public static String RAID = "mktRaid";
    public static String RAID_NON_MARKET = "mktRaidNonMarket";
    //public static String RAID_SURPRISE = "mktRaidSurprise";
    //public static String RAID_RARE = "mktRaidRare";
    public static String RAID_VALUABLE = "mktRaidValuable";
    public static String RAID_DISRUPT = "mktRaidDisrupt";
    public static String RAID_GO_BACK = "mktRaidGoBack";
    public static String RAID_CONFIRM_CONTINUE = "mktRaidConfirmContinue";

    public static String RAID_CONFIRM = "mktRaidConfirm";
    public static String RAID_CONFIRM_STORY = "mktRaidConfirmStory";
    public static String RAID_NEVER_MIND = "mktRaidNeverMind";
    public static String RAID_RESULT = "mktRaidResult";

    public static String INVADE = "mktInvade";
    public static String GO_BACK = "mktGoBack";

    public static String BOMBARD = "mktBombard";
    public static String BOMBARD_TACTICAL = "mktBombardTactical";
    public static String BOMBARD_SATURATION = "mktBombardSaturation";
    public static String BOMBARD_CONFIRM = "mktBombardConfirm";
    public static String BOMBARD_NEVERMIND = "mktBombardNeverMind";
    public static String BOMBARD_RESULT = "mktBombardResult";

    public static String DEBT_RESULT_CONTINUE = "marketCmd_checkDebtContinue";




    public static float DISRUPTION_THRESHOLD = 0.25f;
    public static float VALUABLES_THRESHOLD = 0.05f;

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected MarketAPI market;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected FactionAPI faction;

    protected TempData temp = new TempData();*/
    //execute might not be needed at all.
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        CrewReplacer_Log.loging("running part of the raid plugin",this,logsActive);
        CrewReplacer_Log.push();
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        entity = dialog.getInteractionTarget();
        init(entity);

        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        CrewReplacer_Log.loging("running command: " + command,logsActive);
        CrewReplacer_Log.push();
        if (command.equals("showDefenses")) {
            clearTemp();
            //new ShowDefaultVisual().execute(null, dialog, Misc.tokenize(""), memoryMap);
            showDefenses(true);
        } else if (command.equals("goBackToDefenses")) {
            if (temp.nonMarket) {
                String trigger = temp.raidGoBackTrigger;
                if (trigger == null || trigger.isEmpty()) trigger = "PopulateOptions";
                clearTemp();
                FireAll.fire(null, dialog, memoryMap, trigger);
                return true;
            }
            clearTemp();
            //new ShowDefaultVisual().execute(null, dialog, Misc.tokenize(""), memoryMap);
            showDefenses(true);
            //dialog.getVisualPanel().finishFadeFast();
        } else if (command.equals("engage")) {
            engage();
        } else if (command.equals("raidMenu")) {
//			boolean surprise = "mktRaidSurprise".equals(memory.get("$option"));
//			temp.isSurpriseRaid = surprise;
            raidMenu();
//		} else if (command.equals("raidRare")) {
//			raidRare();
        } else if (command.equals("raidNonMarket")) {
            raidNonMarket();
        } else if (command.equals("raidValuable")) {
            raidValuable();
        } else if (command.equals("raidDisrupt")) {
            raidDisrupt();
        } else if (command.equals("raidConfirm")) {
            raidConfirm(false);
        } else if (command.equals("raidConfirmContinue")) {
            raidConfirmContinue();
        } else if (command.equals("raidNeverMind")) {
            raidNeverMind();
        } else if (command.equals("addContinueToRaidResultOption")) {
            addContinueOption(temp.contText);
        } else if (command.equals("raidResult")) {
            raidResult();
        } else if (command.equals("bombardMenu")) {
            bombardMenu();
        } else if (command.equals("bombardTactical")) {
            bombardTactical();
        } else if (command.equals("bombardSaturation")) {
            bombardSaturation();
        } else if (command.equals("bombardConfirm")) {
            bombardConfirm();
        } else if (command.equals("bombardNeverMind")) {
            bombardNeverMind();
        } else if (command.equals("bombardResult")) {
            bombardResult();
        } else if (command.equals("checkDebtEffect")) {
            return checkDebtEffect();
        } else if (command.equals("applyDebtEffect")) {
            applyDebtEffect();
        } else if (command.equals("checkMercsLeaving")) {
            return checkMercsLeaving();
        } else if (command.equals("convinceMercToStay")) {
            convinceMercToStay();
        } else if (command.equals("mercLeaves")) {
            mercLeaves();
        }
        CrewReplacer_Log.pop();
        CrewReplacer_Log.pop();
        return true;
    }
    //never used. why do i have this here?
    /*public static float getRaidStr(CampaignFleetAPI fleet) {
        float attackerStr = fleet.getCargo().getMaxPersonnel() * 0.25f;//HERE//for real what the hell is this?
        float support = Misc.getFleetwideTotalMod(fleet, Stats.FLEET_GROUND_SUPPORT, 0f);
        attackerStr += Math.min(support, attackerStr);

        StatBonus stat = fleet.getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD);
        attackerStr = stat.computeEffective(attackerStr);

        return attackerStr;
    }*/
//this is something i need
    @Override
    protected void raidNonMarket() {
        CrewReplacer_Log.loging("runing function: raidNonMarket",logsActive);
        CrewReplacer_Log.push();
        float width = 350;
        float opad = 10f;
        float small = 5f;

        Color h = Misc.getHighlightColor();

        temp.nonMarket = true;

        float difficulty = memory.getFloat("$raidDifficulty");
        temp.raidGoBackTrigger = memory.getString("$raidGoBackTrigger");
        temp.raidContinueTrigger = memory.getString("$raidContinueTrigger");

        dialog.getVisualPanel().showImagePortion("illustrations", "raid_prepare", 640, 400, 0, 0, 480, 300);

        float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleet);//playerFleet.getCargo().getMarines();//doneHERE
        float support = Misc.getFleetwideTotalMod(playerFleet, Stats.FLEET_GROUND_SUPPORT, 0f);
        if (support > marines) support = marines;
        CrewReplacer_Log.loging("gathered marines and support: " + marines + ", " + support,this,logsActive);
        StatBonus attackerBase = new StatBonus();
        StatBonus defenderBase = new StatBonus();

        //defenderBase.modifyFlatAlways("base", baseDef, "Base value for a size " + market.getSize() + " colony");

        attackerBase.modifyFlatAlways("core_marines", marines, "Ground troops on board");//HERE
        attackerBase.modifyFlatAlways("core_support", support, "Fleet capability for ground support");

        StatBonus attacker = playerFleet.getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD);
        StatBonus defender = new StatBonus();

        CrewReplacer_PlayerFleetPersonnelTracker.saveMarineData();

        StatBonus attackerTemp = cutMarrineXPToNewStatbounus(attacker);//attackerTemp.createCopy();
        //attacker.unmodifyPercent("marineXP");//"core_marines");//remove globally bonus that marine XP provides.

        if (market != null && difficulty <= 0) defender = market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD);

        defender.modifyFlat("difficulty", difficulty, "Expected resistance");

        String surpriseKey = "core_surprise";
//		if (temp.isSurpriseRaid) {
//			//defender.modifyMult(surpriseKey, 0.1f, "Surprise raid");
//			attacker.modifyMult(surpriseKey, SURPRISE_RAID_STRENGTH_MULT, "Surprise raid");
//		}

        String increasedDefensesKey = "core_addedDefStr";
        float added = 0;
        if (market != null) added = getDefenderIncreaseValue(market);
        if (added > 0) {
            defender.modifyFlat(increasedDefensesKey, added, "Increased defender preparedness");
        }

        float attackerStr = (int) Math.round(attackerTemp.computeEffective(attackerBase.computeEffective(0f)));
        float defenderStr = (int) Math.round(defender.computeEffective(defenderBase.computeEffective(0f)));

        temp.attackerStr = attackerStr;
        temp.defenderStr = defenderStr;
        CrewReplacer_Log.loging("getting attacker and defender str as: " + attackerStr + ", " + defenderStr,this,logsActive);
        TooltipMakerAPI info = text.beginTooltip();

        info.setParaSmallInsignia();

        String has = faction.getDisplayNameHasOrHave();
        String is = faction.getDisplayNameIsOrAre();
        boolean hostile = faction.isHostileTo(Factions.PLAYER);
        boolean tOn = playerFleet.isTransponderOn();
        float initPad = 0f;
        if (!hostile && !faction.isNeutralFaction()) {
            if (tOn) {
                info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                                " not currently hostile. Your fleet's transponder is on, and carrying out a raid " +
                                "will result in open hostilities.",
                        initPad, faction.getBaseUIColor(), faction.getDisplayNameWithArticleWithoutArticle());
            } else {
                info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                                " not currently hostile. Your fleet's transponder is off, and carrying out a raid " +
                                "will only result in a minor penalty to your standing.",
                        initPad, faction.getBaseUIColor(), faction.getDisplayNameWithArticleWithoutArticle());
            }
            initPad = opad;
        }

        float sep = small;
        sep = 3f;
        info.addPara("Raid strength: %s", initPad, h, "" + (int)attackerStr);
        info.addStatModGrid(width, 50, opad, small, attackerBase, true, statPrinter(false));
        if (!attackerTemp.isUnmodified()) {
            info.addStatModGrid(width, 50, opad, sep, attackerTemp, true, statPrinter(true));
        }


        info.addPara("Operation difficulty: %s", opad, h, "" + (int)defenderStr);
        //info.addStatModGrid(width, 50, opad, small, defenderBase, true, statPrinter());
        //if (!defender.isUnmodified()) {
        info.addStatModGrid(width, 50, opad, small, defender, true, statPrinter(true));
        //}

        defender.unmodifyFlat(increasedDefensesKey);
        defender.unmodifyMult(surpriseKey);
        attacker.unmodifyMult(surpriseKey);//HERE. this was attaker. so i turned it back to attacker temp

        text.addTooltip();

        boolean hasForces = true;
        temp.raidMult = attackerStr / Math.max(1f, (attackerStr + defenderStr));
        temp.raidMult = Math.round(temp.raidMult * 100f) / 100f;

        {
            Color eColor = h;
            if (temp.raidMult < DISRUPTION_THRESHOLD && temp.raidMult < VALUABLES_THRESHOLD) {
                eColor = Misc.getNegativeHighlightColor();
            }
            text.addPara("Projected raid effectiveness: %s",
                    eColor,
                    "" + (int)(temp.raidMult * 100f) + "%");
            //"" + (int)Math.round(temp.raidMult * 100f) + "%");
            if (temp.raidMult < VALUABLES_THRESHOLD) {
                text.addPara("You do not have the forces to carry out an effective raid.");
                hasForces = false;
            }
        }

        options.clearOptions();

        options.addOption("Designate raid objectives", RAID_VALUABLE);

        if (!hasForces) {
            options.setEnabled(RAID_VALUABLE, false);
        }

        options.addOption("Go back", RAID_GO_BACK);
        options.setShortcut(RAID_GO_BACK, Keyboard.KEY_ESCAPE, false, false, false, true);

        //repairStatBounus(attacker,attackerTemp);//HERE. trying to repair my modifiers. this might work?
        CrewReplacer_Log.pop();
        //attackerTemp = attacker.;
    }



    //this is something i need
    @Override
    protected void raidMenu() {
            CrewReplacer_Log.loging("running function: raidMenu",this,logsActive);
            CrewReplacer_Log.push();
            float width = 350;
            float opad = 10f;
            float small = 5f;

    //		if (true) {
    //			Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.CARGO);
    //			return;
    //		}

            Color h = Misc.getHighlightColor();

            temp.nonMarket = false;

    //		dialog.getVisualPanel().showPlanetInfo(market.getPrimaryEntity());
    //		dialog.getVisualPanel().finishFadeFast();
            dialog.getVisualPanel().showImagePortion("illustrations", "raid_prepare", 640, 400, 0, 0, 480, 300);

            float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleet);//playerFleet.getCargo().getMarines();//doneHERE
            float support = Misc.getFleetwideTotalMod(playerFleet, Stats.FLEET_GROUND_SUPPORT, 0f);
            if (support > marines) support = marines;
            CrewReplacer_Log.loging("gathered marines and support: " + marines + ", " + support,this,logsActive);

            StatBonus attackerBase = new StatBonus();
            StatBonus defenderBase = new StatBonus();

            //defenderBase.modifyFlatAlways("base", baseDef, "Base value for a size " + market.getSize() + " colony");

            attackerBase.modifyFlatAlways("core_marines", marines, "Ground troops on board");//HERE this looks like its a veribal assinment, as apposed to an displayed text.?
            attackerBase.modifyFlatAlways("core_support", support, "Fleet capability for ground support");

            StatBonus attacker = playerFleet.getStats().getDynamic().getMod(Stats.PLANETARY_OPERATIONS_MOD);
            StatBonus defender = market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD);

            CrewReplacer_PlayerFleetPersonnelTracker.saveMarineData();

            StatBonus attackerTemp = cutMarrineXPToNewStatbounus(attacker);//attackerTemp.createCopy();
            //attacker.unmodifyPercent("marineXP");//"core_marines");//remove globally bonus that marine XP provides.
            String surpriseKey = "core_surprise";
    //		if (temp.isSurpriseRaid) {
    //			//defender.modifyMult(surpriseKey, 0.1f, "Surprise raid");
    //			attacker.modifyMult(surpriseKey, SURPRISE_RAID_STRENGTH_MULT, "Surprise raid");
    //		}

            String increasedDefensesKey = "core_addedDefStr";
            float added = getDefenderIncreaseValue(market);
            if (added > 0) {
                defender.modifyFlat(increasedDefensesKey, added, "Increased defender preparedness");
            }
            tempdebug(attackerTemp);
            tempdebug(attackerBase);
            tempdebug(attacker);
            //tempdebug(attacker);
            //tempdebug(attacker);
            float attackerStr = (int) Math.round(attackerTemp.computeEffective(attackerBase.computeEffective(0f)));//notHERE?
            float defenderStr = (int) Math.round(defender.computeEffective(defenderBase.computeEffective(0f)));

            temp.attackerStr = attackerStr;
            temp.defenderStr = defenderStr;
            CrewReplacer_Log.loging("getting attacker and defender str as: " + attackerStr + ", " + defenderStr,this,logsActive);

            TooltipMakerAPI info = text.beginTooltip();

            info.setParaSmallInsignia();

            String has = faction.getDisplayNameHasOrHave();
            String is = faction.getDisplayNameIsOrAre();
            boolean hostile = faction.isHostileTo(Factions.PLAYER);
            boolean tOn = playerFleet.isTransponderOn();
            float initPad = 0f;
            if (!hostile) {
                if (tOn) {
                    info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                                    " not currently hostile. Your fleet's transponder is on, and carrying out a raid " +
                                    "will result in open hostilities.",
                            initPad, faction.getBaseUIColor(), faction.getDisplayNameWithArticleWithoutArticle());
                } else {
                    info.addPara(Misc.ucFirst(faction.getDisplayNameWithArticle()) + " " + is +
                                    " not currently hostile. Your fleet's transponder is off, and carrying out a raid " +
                                    "will only result in a minor penalty to your standing.",
                            initPad, faction.getBaseUIColor(), faction.getDisplayNameWithArticleWithoutArticle());
                }
                initPad = opad;
            }

            float sep = small;
            sep = 3f;
            info.addPara("Raid strength: %s", initPad, h, "" + (int)attackerStr);
            info.addStatModGrid(width, 50, opad, small, attackerBase, true, statPrinter(false));
            if (!attackerTemp.isUnmodified()) {
                info.addStatModGrid(width, 50, opad, sep, attackerTemp, true, statPrinter(true));
            }


            info.addPara("Ground defense strength: %s", opad, h, "" + (int)defenderStr);
            //info.addStatModGrid(width, 50, opad, small, defenderBase, true, statPrinter());
            //if (!defender.isUnmodified()) {
            info.addStatModGrid(width, 50, opad, small, defender, true, statPrinter(true));
            //}

            defender.unmodifyFlat(increasedDefensesKey);
            defender.unmodifyMult(surpriseKey);
            attacker.unmodifyMult(surpriseKey);//HERE. this was attacker. so i turned to back to attacker temp

            text.addTooltip();

            boolean hasForces = true;
            boolean canDisrupt = true;
            temp.raidMult = attackerStr / Math.max(1f, (attackerStr + defenderStr));
            temp.raidMult = Math.round(temp.raidMult * 100f) / 100f;
            //temp.raidMult = 1f;




            {
                //temp.failProb = 0f;
                Color eColor = h;
                if (temp.raidMult < DISRUPTION_THRESHOLD && temp.raidMult < VALUABLES_THRESHOLD) {
                    eColor = Misc.getNegativeHighlightColor();
                }
                if (temp.raidMult < DISRUPTION_THRESHOLD) {
                    //eColor = Misc.getNegativeHighlightColor();
                    canDisrupt = false;
                    //temp.canFail = true;
                } else if (temp.raidMult >= 0.7f) {
                    //eColor = Misc.getPositiveHighlightColor();
                }
    //			text.addPara("Projected raid effectiveness: %s. " +
    //					"This will determine the outcome of the raid, " +
    //					"as well as the casualties suffered by your forces, if any.",
    //					eColor,
    //					"" + (int)Math.round(temp.raidMult * 100f) + "%");
                text.addPara("Projected raid effectiveness: %s",
                        eColor,
                        "" + (int)(temp.raidMult * 100f) + "%");
                //"" + (int)Math.round(temp.raidMult * 100f) + "%");
                if (!canDisrupt) {
                    text.addPara("The ground defenses are too strong for your forces to be able to cause long-term disruption.");
                }
                if (temp.raidMult < VALUABLES_THRESHOLD) {
                    text.addPara("You do not have the forces to carry out an effective raid to acquire valuables or achieve other objectives.");
                    hasForces = false;
                }
    //			if (canDisrupt) {
    //			} else {
    //				text.addPara("Projected raid effectiveness: %s. " +
    //						"This will determine the outcome of the raid, " +
    //						"as well as the casualties suffered by your forces, if any.",
    //						eColor,
    //						"" + (int)Math.round(temp.raidMult * 100f) + "%");
    //			}
            }

            if (DebugFlags.MARKET_HOSTILITIES_DEBUG) {
                canDisrupt = true;
            }

            options.clearOptions();

            //options.addOption("Try to acquire rare items, such as blueprints", RAID_RARE);
            //options.addOption("Try to acquire valuables, such as commodities, blueprints, and other items", RAID_VALUABLE);
            options.addOption("Try to acquire valuables, such as commodities or blueprints, or achieve other objectives", RAID_VALUABLE);
            options.addOption("Disrupt the operations of a specific industry or facility", RAID_DISRUPT);

            if (!hasForces) {
                options.setEnabled(RAID_VALUABLE, false);
                //options.setEnabled(RAID_RARE, false);
            }

            if (!hasForces || !canDisrupt) {
                options.setEnabled(RAID_DISRUPT, false);
                if (!canDisrupt) {
                    String pct = "" + (int)Math.round(DISRUPTION_THRESHOLD * 100f) + "%";
                    options.setTooltip(RAID_DISRUPT, "Requires at least " + pct + " raid effectiveness.");
                    options.setTooltipHighlights(RAID_DISRUPT, pct);
                    options.setTooltipHighlightColors(RAID_DISRUPT, h);
                }
            }

            options.addOption("Go back", RAID_GO_BACK);
            options.setShortcut(RAID_GO_BACK, Keyboard.KEY_ESCAPE, false, false, false, true);
            //repairStatBounus(attacker,attackerTemp);//HERE were im trying to fix the thing. god this is patchwork.
            CrewReplacer_Log.pop();
            //CrewReplacer_Log.loging("HERE, FINAL Test Thing. temp -> base -> blank",this);
            //tempdebug(attackerTemp);
            //tempdebug(attackerBase);
            //tempdebug(attacker);
        }
    //this is something i need
    @Override
    protected void raidValuable() {
        CrewReplacer_Log.loging("running function raidValuble",this,logsActive);
        CrewReplacer_Log.push();
        temp.raidType = RaidType.VALUABLE;

        List<GroundRaidObjectivePlugin> obj = new ArrayList<GroundRaidObjectivePlugin>();

        // See: StandardGroundRaidObjectivesCreator; it creates the standard objectives with priority 0 below
        final RaidType useType = !temp.nonMarket ? temp.raidType : RaidType.CUSTOM_ONLY;
        //if (temp.nonMarket) useType = RaidType.CUSTOM_ONLY;
        for (int i = 0; i < 10; i++) {
            ListenerUtil.modifyRaidObjectives(market, entity, obj, useType, getNumMarineTokens(), i);
        }

        if (obj.isEmpty()) {
            text.addPara("After careful consideration, there do not appear to be any targets " +
                    "likely to yield anything of value.");
            addNeverMindOption();
            return;
        }

        final CampaignFleetAPI playerFleettemp = playerFleet;
        final TempData temptemp = temp;
        final TextPanelAPI texttemp = text;
        dialog.showGroundRaidTargetPicker("Select raid objectives", "Select", market, obj,

                new GroundRaidTargetPickerDelegate() {
                    public void pickedGroundRaidTargets(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: pickedGroundRaidTargets",this,logsActive);
                        CrewReplacer_Log.push();
                        float value = 0;
                        for (GroundRaidObjectivePlugin curr : data) {
                            value += curr.getProjectedCreditsValue();
                        }
                        Color h = Misc.getHighlightColor();
                        List<String> names = new ArrayList<String>();
                        for (GroundRaidObjectivePlugin curr : data) {
                            names.add(curr.getNameOverride() != null ? curr.getNameOverride() : curr.getName());
                        }
                        String list = Misc.getAndJoined(names);
                        String item = "objective";
                        if (names.size() > 1) {
                            item = "objectives";
                        }
                        /*doneHERE removed the old display of marines, and added a new one.
                        String isOrAre = "are";
                        String marinesStr = "marines";
                        if (playerCargo.getMarines() == 1) {
                            isOrAre = "is";
                            marinesStr = "marine";
                        }

                         */
                        //float losses = getProjectedMarineLossesFloat();//doneHERE change mirine commander to somethingelse? randomly sellect a comander from available crews?
                        /*
                        LabelAPI label = text.addPara("Your marine commander submits a plan for your approval. Losses during this " +
                                        "operation are projected to be %s. There " + isOrAre + " a total of %s " +
                                        marinesStr + " in your fleet.",
                                getMarineLossesColor(data), getProjectedMarineLosses(data).toLowerCase(),//doneHERE?
                                Misc.getWithDGS(playerCargo.getMarines()));//doneHERE


                        label.setHighlightColors(getMarineLossesColor(data), Misc.getHighlightColor());
                         */
                        /*LabelAPI label = text.addPara("Your marine commander submits a plan for your approval. Losses during this " +
                                        "operation are projected to be %s. There " + isOrAre + " a total of %s " +
                                        marinesStr + " in your fleet.",
                                getMarineLossesColor(data), getProjectedMarineLosses(data).toLowerCase(),//doneHERE?
                                Misc.getWithDGS(playerCargo.getMarines()));*/
                        /*
                        LabelAPI label = texttemp.addPara("Your marine commander submits a plan for your approval. Losses during this " +
                        "operation are projected to be %s. There is a total of %s ground combat ability in your fleet",
                                getMarineLossesColor(data), getProjectedMarineLosses(data).toLowerCase(),
                                Float.toString(crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp)));*/
                        float combat = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp);
                        float support = Misc.getFleetwideTotalMod(playerFleettemp, Stats.FLEET_GROUND_SUPPORT, 0f);
                        LabelAPI label = texttemp.addPara("Your marine commander submits a plan for your approval. Losses during this " +
                                        "operation are projected to be %s. There is a effective strength of %s ground troop power available in your fleet",
                                getMarineLossesColor(data), getProjectedMarineLosses(data).toLowerCase(),
                                Float.toString(combat));//,Float.toString(support));
                        texttemp.addPara("your total available ground combat force is consisted of: ");
                        crewReplacer_Main.getJob(jobmain).displayCrewAvailable(playerFleettemp,texttemp);//doneHERE
                        texttemp.addPara(Misc.ucFirst(item) + " targeted: " + list + ".", h,
                                names.toArray(new String[0]));

                        if (value > 0) {
                            texttemp.addPara("The estimated value of the items obtained is projected to be around %s.",
                                    h, Misc.getDGSCredits(value));
                        }

//				text.addPara("The marines are ready to go, awaiting your final confirmation. There are a total of %s " +
//						"marines in your fleet.", Misc.getHighlightColor(), Misc.getWithDGS(playerCargo.getMarines()));
                        texttemp.addPara("The combat force are ready to go, awaiting your final confirmation.");//doneHERE
                        temptemp.objectives = data;
                        addConfirmOptions();
                        CrewReplacer_Log.pop();
                    }

                    public boolean isDisruptIndustryMode() {
                        CrewReplacer_Log.loging("running sub-function: isDistrupIndustryMod",this,logsActive);
                        return false;
                    }

                    public boolean isCustomOnlyMode() {
                        CrewReplacer_Log.loging("running sub-function: isCustomOnlyMode",this,logsActive);
                        return useType == RaidType.CUSTOM_ONLY;
                    }

                    public void cancelledGroundRaidTargetPicking() {
                        CrewReplacer_Log.loging("running sub-function: cancelledGroundRaidTargetPicking",this,logsActive);
                    }

                    public int getCargoSpaceNeeded(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getCargoSpaceNeeded",this,logsActive);
                        float total = 0;
                        for (GroundRaidObjectivePlugin curr : data) {
                            total += curr.getCargoSpaceNeeded();
                        }
                        return (int) total;
                    }

                    public int getFuelSpaceNeeded(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getFuelSpaceNeeded",this,logsActive);
                        float total = 0;
                        for (GroundRaidObjectivePlugin curr : data) {
                            total += curr.getFuelSpaceNeeded();
                        }
                        return (int) total;
                    }

                    public int getProjectedCreditsValue(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getProjectedCreditsValue",this,logsActive);
                        float total = 0;
                        for (GroundRaidObjectivePlugin curr : data) {
                            total += curr.getProjectedCreditsValue();
                        }
                        return (int) total;
                    }

                    public int getNumMarineTokens() {
                        CrewReplacer_Log.loging("running sub-function: getNumMarineTokens",this,logsActive);
                        return CrewReplacerMarketCMD.this.getNumMarineTokens();
                    }

                    public MutableStat getMarineLossesStat(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getMarineLossesStat",this,logsActive);
                        return CrewReplacerMarketCMD.this.getMarineLossesStat(data);
                    }

                    public String getProjectedMarineLosses(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getProjectMarineLosses",this,logsActive);
                        CrewReplacer_Log.push();
                        //return "" + (int) Math.round(getProjectedMarineLossesFloat());
                        float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp);//float marines = playerFleet.getCargo().getMarines();//doneHERE
                        float losses = getAverageMarineLosses(data);

                        float f = losses / Math.max(1f, marines);
                        CrewReplacer_Log.pop();
                        for (RaidDangerLevel level : RaidDangerLevel.values()) {
                            float test = level.marineLossesMult + (level.next().marineLossesMult - level.marineLossesMult) * 0.5f;
                            if (level == RaidDangerLevel.NONE) test = RaidDangerLevel.NONE.marineLossesMult;
                            if (test >= f) {
                                return level.lossesName;
                            }
                        }
                        return RaidDangerLevel.EXTREME.lossesName;
                    }//doneHERE

                    public float getAverageMarineLosses(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getAverageMarineLosses",this,logsActive);
                        return CrewReplacerMarketCMD.this.getAverageMarineLosses(data);
                    }

                    public Color getMarineLossesColor(List<GroundRaidObjectivePlugin> data) {
                        CrewReplacer_Log.loging("running sub-function: getMarineLossesColor",this,logsActive);
                        CrewReplacer_Log.push();
                        float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp);//playerFleet.getCargo().getMarines();//doneHERE
                        float losses = getAverageMarineLosses(data);


                        float f = losses / Math.max(1f, marines);
                        CrewReplacer_Log.pop();
                        if (f <= 0 && data.isEmpty())  return Misc.getGrayColor();

                        for (RaidDangerLevel level : RaidDangerLevel.values()) {
                            float test = level.marineLossesMult + (level.next().marineLossesMult - level.marineLossesMult) * 0.5f;
                            if (test >= f) {
                                return level.color;
                            }
                        }
                        return RaidDangerLevel.EXTREME.color;
                    }
                    public String getRaidEffectiveness() {
                        CrewReplacer_Log.loging("running sub-function: getRaidEffectiveness",this,logsActive);
                        return "" + (int)(temptemp.raidMult * 100f) + "%";
                    }
                });
        CrewReplacer_Log.pop();
    }
//why is this here? (no crew code here)
    /*
    protected void addBombardConfirmOptions() {
        options.clearOptions();
        options.addOption("Launch bombardment", BOMBARD_CONFIRM);
        options.addOption("Never mind", BOMBARD_NEVERMIND);
        options.setShortcut(BOMBARD_NEVERMIND, Keyboard.KEY_ESCAPE, false, false, false, true);

        List<FactionAPI> nonHostile = new ArrayList<FactionAPI>();
        for (FactionAPI faction : temp.willBecomeHostile) {
            boolean hostile = faction.isHostileTo(Factions.PLAYER);
            if (!hostile) {
                nonHostile.add(faction);
            }
        }

        if (nonHostile.size() == 1) {
            FactionAPI faction = nonHostile.get(0);
            options.addOptionConfirmation(BOMBARD_CONFIRM,
                    "The " + faction.getDisplayNameLong() +
                            " " + faction.getDisplayNameIsOrAre() +
                            " not currently hostile, and will become hostile if you carry out the bombardment. " +
                            "Are you sure?", "Yes", "Never mind");
        } else if (nonHostile.size() > 1) {
            options.addOptionConfirmation(BOMBARD_CONFIRM,
                    "Multiple factions that are not currently hostile " +
                            "will become hostile if you carry out the bombardment. " +
                            "Are you sure?", "Yes", "Never mind");
        }
    }*/
    //this is something i need
    @Override
    protected void raidDisrupt() {
            CrewReplacer_Log.loging("running function: raidDisrupt",this,logsActive);
            CrewReplacer_Log.push();
            temp.raidType = RaidType.DISRUPT;

            // See: StandardGroundRaidObjectivesCreator; it creates the standard objectives with priority 0 below
            List<GroundRaidObjectivePlugin> obj = new ArrayList<GroundRaidObjectivePlugin>();
            for (int i = 0; i < 10; i++) {
                ListenerUtil.modifyRaidObjectives(market, entity, obj, temp.raidType, getNumMarineTokens(), i);
            }

            if (obj.isEmpty()) {
                text.addPara("There are no industries or facilities present that could be disrupted by a raid.");
                addNeverMindOption();
                CrewReplacer_Log.pop();
                return;
            }

            final CampaignFleetAPI playerFleettemp = playerFleet;
            final TempData temptemp = temp;
            final TextPanelAPI texttemp = text;
            dialog.showGroundRaidTargetPicker("Select raid objectives", "Select", market, obj,
                    new GroundRaidTargetPickerDelegate() {
                        public void pickedGroundRaidTargets(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: pickedGroundRaidTargets",this,logsActive);
                            float value = 0;
                            for (GroundRaidObjectivePlugin curr : data) {
                                value += curr.getProjectedCreditsValue();
                            }
                            Color h = Misc.getHighlightColor();
                            List<String> names = new ArrayList<String>();
                            for (GroundRaidObjectivePlugin curr : data) {
                                names.add(curr.getNameOverride() != null ? curr.getNameOverride() : curr.getName());
                            }
                            String list = Misc.getAndJoined(names);
                            String item = "objective";
                            if (names.size() > 1) {
                                item = "objectives";
                            }

                            //float losses = getProjectedMarineLossesFloat();

                            LabelAPI label = texttemp.addPara("Your marine commander submits a plan for your approval. Losses during this " +
                                            "operation are projected to be %s.",
                                    getMarineLossesColor(data), getProjectedMarineLosses(data).toLowerCase());
                            texttemp.addPara(Misc.ucFirst(item) + " targeted: " + list + ".", h,
                                    names.toArray(new String[0]));

                            if (value > 0) {
                                texttemp.addPara("The estimated value of the items obtained is projected to be around %s.",
                                        h, Misc.getDGSCredits(value));
                            }
                            texttemp.addPara("The combat force are ready to go, awaiting your final confirmation. There are a total of %s " +
                                    "ground troop power in your fleet.", Misc.getHighlightColor(), Float.toString(crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp)));//Misc.getWithDGS(playerCargo.getMarines()));//mabyedoneHERE number of crew for discription? or crew power maybe?
                            crewReplacer_Main.getJob(jobmain).displayCrewAvailable(playerFleettemp,texttemp);
                            temptemp.objectives = data;
                            addConfirmOptions();
                        }

                        public boolean isDisruptIndustryMode() {
                            CrewReplacer_Log.loging("running sub-function: isDisruptIndustryMode",this,logsActive);
                            return true;
                        }

                        public void cancelledGroundRaidTargetPicking() {
                            CrewReplacer_Log.loging("running sub-function: cancelledGroundRaidTargetPicking",this,logsActive);

                        }

                        public int getCargoSpaceNeeded(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getCargoSpaceNeeded",this,logsActive);
                            float total = 0;
                            for (GroundRaidObjectivePlugin curr : data) {
                                total += curr.getCargoSpaceNeeded();
                            }
                            return (int) total;
                        }

                        public int getFuelSpaceNeeded(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getFuelSpaceNeeded",this,logsActive);
                            float total = 0;
                            for (GroundRaidObjectivePlugin curr : data) {
                                total += curr.getFuelSpaceNeeded();
                            }
                            return (int) total;
                        }

                        public int getProjectedCreditsValue(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getProjectedCreditsValue",this,logsActive);
                            float total = 0;
                            for (GroundRaidObjectivePlugin curr : data) {
                                total += curr.getProjectedCreditsValue();
                            }
                            return (int) total;
                        }

                        public int getNumMarineTokens() {
                            CrewReplacer_Log.loging("running sub-function: getNumMarineTokens",this,logsActive);
                            return CrewReplacerMarketCMD.this.getNumMarineTokens();
                        }

                        public MutableStat getMarineLossesStat(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getMarineLossesStat",this,logsActive);
                            return CrewReplacerMarketCMD.this.getMarineLossesStat(data);
                        }

                        public String getProjectedMarineLosses(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getProjectedMarineLosses",this,logsActive);
                            CrewReplacer_Log.push();
                            //return "" + (int) Math.round(getProjectedMarineLossesFloat());
                            float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp);//playerFleet.getCargo().getMarines();//doneHERE
                            float losses = getAverageMarineLosses(data);

                            float f = losses / Math.max(1f, marines);
                            CrewReplacer_Log.pop();
                            for (RaidDangerLevel level : RaidDangerLevel.values()) {
                                float test = level.marineLossesMult + (level.next().marineLossesMult - level.marineLossesMult) * 0.5f;
                                if (level == RaidDangerLevel.NONE) test = RaidDangerLevel.NONE.marineLossesMult;
                                if (test >= f) {
                                    return level.lossesName;
                                }
                            }
                            return RaidDangerLevel.EXTREME.lossesName;
                        }

                        public float getAverageMarineLosses(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getAverageMarineLosses",this,logsActive);
                            return CrewReplacerMarketCMD.this.getAverageMarineLosses(data);
                        }

                        public Color getMarineLossesColor(List<GroundRaidObjectivePlugin> data) {
                            CrewReplacer_Log.loging("running sub-function: getMarineLossesColor",this,logsActive);
                            CrewReplacer_Log.push();
                            float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleettemp);//playerFleet.getCargo().getMarines();//doneHERE
                            float losses = getAverageMarineLosses(data);

                            CrewReplacer_Log.pop();
                            float f = losses / Math.max(1f, marines);
                            if (f <= 0)  return Misc.getGrayColor();

                            for (RaidDangerLevel level : RaidDangerLevel.values()) {
                                float test = level.marineLossesMult + (level.next().marineLossesMult - level.marineLossesMult) * 0.5f;
                                if (test >= f) {
                                    return level.color;
                                }
                            }
                            return RaidDangerLevel.EXTREME.color;
                        }
                        public String getRaidEffectiveness() {
                            CrewReplacer_Log.loging("running sub-function: getRaidEffectiveness",this,logsActive);
                            return "" + (int)(temptemp.raidMult * 100f) + "%";
                        }

                        public boolean isCustomOnlyMode() {
                            CrewReplacer_Log.loging("running sub-function: isCustomOnlyMode",this,logsActive);
                            // TODO Auto-generated method stub
                            return false;
                        }
                    });
            CrewReplacer_Log.pop();

    //		dialog.showIndustryPicker("Select raid target", "Select", market, targets, new IndustryPickerListener() {
    //			public void pickedIndustry(Industry industry) {
    //				raidDisruptIndustryPicked(industry);
    //			}
    //			public void cancelledIndustryPicking() {
    //
    //			}
    //		});
        }
    //this is needed or the code tries to kill me.
    @Override
    protected float getAverageMarineLosses(List<GroundRaidObjectivePlugin> data) {
        CrewReplacer_Log.loging("running function: getAverageMarineLosses",this,logsActive);
        CrewReplacer_Log.push();
        MutableStat stat = getMarineLossesStat(data);//HEREHERE
        //tempdebug(stat.);
        float mult = stat.getModifiedValue();
        if (mult > MAX_MARINE_LOSSES) {
            mult = MAX_MARINE_LOSSES;
        }

        float marines = crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleet);//playerFleet.getCargo().getMarines();//doneHERE
        CrewReplacer_Log.pop();
        return marines * mult;
    }
    //this is needed of the code tries to kill me
    @Override
    protected MutableStat getMarineLossesStat(List<GroundRaidObjectivePlugin> data) {
            CrewReplacer_Log.loging("running function: getMarineLossesStat",this,logsActive);
            CrewReplacer_Log.push();
            MutableStat stat = new MutableStat(1.0F);
            float total = 0.0F;
            float assignedTokens = 0.0F;

            GroundRaidObjectivePlugin curr;
            for(Iterator var6 = data.iterator(); var6.hasNext(); assignedTokens += (float)curr.getMarinesAssigned()) {
                curr = (GroundRaidObjectivePlugin)var6.next();
                MarketCMD.RaidDangerLevel danger = curr.getDangerLevel();
                total += danger.marineLossesMult * (float)curr.getMarinesAssigned();
            }

            float danger = total / Math.max(1.0F, assignedTokens);
            float hazard = 1.0F;
            if (this.market != null) {
                hazard = this.market.getHazardValue();
            }

            float reMult = 1.0F;
            float reservesMult;
            if (this.temp.raidMult > MIN_RE_TO_REDUCE_MARINE_LOSSES) {
                reservesMult = (this.temp.raidMult - MIN_RE_TO_REDUCE_MARINE_LOSSES) / (1.0F - MIN_RE_TO_REDUCE_MARINE_LOSSES);
                reservesMult = MAX_MARINE_LOSS_REDUCTION_MULT + (1.0F - MAX_MARINE_LOSS_REDUCTION_MULT) * (1.0F - reservesMult);
                reMult = reservesMult;
            } else if (this.temp.raidMult < RE_PER_MARINE_TOKEN) {
                reservesMult = 1.0F + (RE_PER_MARINE_TOKEN - this.temp.raidMult) / RE_PER_MARINE_TOKEN;
                reMult = reservesMult;
            }

            float maxTokens;
            if (this.market != null && reMult < 1.0F) {
                reservesMult = (float)getMarinesFor(this.market, Math.round(assignedTokens));
                maxTokens = (float)crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleet.getCargo());//Global.getSector().getPlayerFleet().getCargo().getMarines();//HERE
                if (maxTokens > reservesMult && maxTokens > 0.0F) {
                    reMult *= 0.5F + 0.5F * reservesMult / maxTokens;
                }
            }

            reservesMult = 1.0F;
            maxTokens = (float)this.getNumMarineTokens();
            if (maxTokens > assignedTokens) {
                reservesMult = 1.0F - (maxTokens - assignedTokens) * LOSS_REDUCTION_PER_RESERVE_TOKEN;
                reservesMult = Math.max(0.5F, reservesMult);
            }

            float e = getDefenderIncreaseRaw(this.market);
            float per = getRaidDefenderIncreasePerRaid();
            float prep = e / per * LOSS_INCREASE_PER_RAID;
            stat.modifyMultAlways("danger", danger, "Danger level of objectives");
            stat.modifyMult("hazard", hazard, "Colony hazard rating");
            if (reMult < 1.0F) {
                stat.modifyMultAlways("reMult", reMult, "High raid effectiveness");
            } else if (reMult > 1.0F) {
                stat.modifyMultAlways("reMult", reMult, "Low raid effectiveness");
            }

            if (reservesMult < 1.0F && assignedTokens > 0.0F) {
                stat.modifyMultAlways("reservesMult", reservesMult, "Forces held in reserve");
            }

            stat.modifyMult("prep", 1.0F + prep, "Increased defender preparedness");
            MutableStat a = this.playerFleet.getStats().getDynamic().getStat("ground_attack_casualties_mult");
            CrewReplacer_Log.loging("getting loss modifiers",this,logsActive);
            CrewReplacer_Log.push();
            CrewReplacer_Log.loging("getting full list...",this,logsActive);
            CrewReplacer_Log.push();
            tempdebug(a);
            CrewReplacer_Log.pop();
            a = cutMarrineXpToNewMutableStat(a);
            CrewReplacer_Log.loging("getting cut list...",this,logsActive);
            tempdebug(a);
            CrewReplacer_Log.pop();
            CrewReplacer_Log.pop();
            stat.applyMods(a);
            ListenerUtil.modifyMarineLossesStatPreRaid(this.market, data, stat);
            CrewReplacer_Log.pop();
            return stat;
        }
    //this is needed. also needs a combatability patch with nex
    @Override
    protected void raidConfirm(boolean secret) {
        CrewReplacer_Log.loging("running function: raidConfirm",this,logsActive);
        CrewReplacer_Log.push();
        if (temp.raidType == null) {
            raidNeverMind();
            CrewReplacer_Log.pop();
            return;
        }

//		if (temp.raidType == RaidType.VALUABLE) {
//			dialog.getVisualPanel().showImagePortion("illustrations", "raid_valuables_result", 640, 400, 0, 0, 480, 300);
//		} else if (temp.raidType == RaidType.DISRUPT) {
//			dialog.getVisualPanel().showImagePortion("illustrations", "raid_disrupt_result", 640, 400, 0, 0, 480, 300);
//		}

        Random random = getRandom();
        //random = new Random();

        if (!DebugFlags.MARKET_HOSTILITIES_DEBUG) {
            Misc.increaseMarketHostileTimeout(market, HOSTILE_ACTIONS_TIMEOUT_DAYS);
        }

        addMilitaryResponse();


        if (market != null) {
            applyDefenderIncreaseFromRaid(market);
        }

        setRaidCooldown(getRaidCooldownMax());

        //RecentUnrest.get(market).add(3, Misc.ucFirst(reason));
        int stabilityPenalty = 0;
        if (!temp.nonMarket) {
            String reason = "Recently raided";
            if (Misc.isPlayerFactionSetUp()) {
                reason = playerFaction.getDisplayName() + " raid";
            }
            float raidMultForStabilityPenalty = temp.raidMult;
            if (temp.objectives != null) {
                float assignedTokens = 0f;
                for (GroundRaidObjectivePlugin curr : temp.objectives) {
                    assignedTokens += curr.getMarinesAssigned();
                }
                raidMultForStabilityPenalty = assignedTokens * 0.1f;
            }

            stabilityPenalty = applyRaidStabiltyPenalty(market, reason, raidMultForStabilityPenalty);
            Misc.setFlagWithReason(market.getMemoryWithoutUpdate(), MemFlags.RECENTLY_RAIDED,
                    Factions.PLAYER, true, 30f);
            Misc.setRaidedTimestamp(market);
        }

        int marines = (int)crewReplacer_Main.getJob(jobmain).getAvailableCrewPower(playerFleet);//playerFleet.getCargo().getMarines();//doneHERE
        float probOfLosses = 1f;

        int losses = 0;
        if (random.nextFloat() < probOfLosses) {
            float averageLosses = getAverageMarineLosses(temp.objectives);
            float variance = averageLosses / 4f;

            //float randomizedLosses = averageLosses - variance + variance * 2f * random.nextFloat();
            float randomizedLosses = StarSystemGenerator.getNormalRandom(
                    random, averageLosses - variance, averageLosses + variance);
            if (randomizedLosses < 1f) {
                randomizedLosses = random.nextFloat() < randomizedLosses ? 1f : 0f;
            }
            randomizedLosses = Math.round(randomizedLosses);
            losses = (int) randomizedLosses;

            if (losses < 0) losses = 0;
            if (losses > marines) losses = marines;
        }

        //losses = random.nextInt(marines / 2);

        if (losses <= 0) {
            text.addPara("Your forces have not suffered any casualties.");
            temp.marinesLost = 0;
        } else {
            text.addPara("You forces have suffered casualties during the raid.", Misc.getHighlightColor(), "" + losses);
            //playerFleet.getCargo().removeMarines(losses);//doneHERE remove crew important
            crewReplacer_Job tempjob = crewReplacer_Main.getJob(jobmain);
            //tempjob.automaticlyGetDisplayAndApplyCrewLost(playerFleet,(int)tempjob.getAvailableCrewPower(playerFleet),losses,text);//HERE. I require the number of actual deployed crew here...
            temp.marinesLost = losses;//doneHERE?
            //AddRemoveCommodity.addCommodityLossText(Commodities.MARINES, losses, text);//doneHERE losses display text
        }


        if (!secret) {
            boolean tOn = playerFleet.isTransponderOn();
            boolean hostile = faction.isHostileTo(Factions.PLAYER);
            CustomRepImpact impact = new CustomRepImpact();
            if (market != null) {
                impact.delta = market.getSize() * -0.01f * 1f;
            } else {
                impact.delta = -0.01f;
            }
            if (!hostile && tOn) {
                impact.ensureAtBest = RepLevel.HOSTILE;
            }
            if (impact.delta != 0 && !faction.isNeutralFaction()) {
                Global.getSector().adjustPlayerReputation(
                        new RepActionEnvelope(RepActions.CUSTOM,
                                impact, null, text, true, true),
                        faction.getId());
            }
        }

        if (stabilityPenalty > 0) {
            text.addPara("Stability of " + market.getName() + " reduced by %s.",
                    Misc.getHighlightColor(), "" + stabilityPenalty);
        }

//		if (!temp.nonMarket) {
//			if (temp.raidType == RaidType.VALUABLE || true) {
//				text.addPara("The raid was successful in achieving its objectives.");
//			}
//		}

        CargoAPI result = performRaid(random, temp.raidMult);

        if (market != null) market.reapplyIndustries();

        result.sort();
        result.updateSpaceUsed();

        temp.raidLoot = result;

//		int raidCredits = (int)result.getCredits().get();
//		if (raidCredits < 0) raidCredits = 0;
//
//		//result.clear();
//		if (raidCredits > 0) {
//			AddRemoveCommodity.addCreditsGainText(raidCredits, text);
//			playerFleet.getCargo().getCredits().add(raidCredits);
//		}

        if (temp.xpGained > 0) {
            Global.getSector().getPlayerStats().addXP(temp.xpGained, dialog.getTextPanel());
        }
        if (temp.raidType == RaidType.VALUABLE) {
            if (result.getTotalCrew() + result.getSpaceUsed() + result.getFuel() < 10) {
                dialog.getVisualPanel().showImagePortion("illustrations", "raid_covert_result", 640, 400, 0, 0, 480, 300);
            } else {
                dialog.getVisualPanel().showImagePortion("illustrations", "raid_valuables_result", 640, 400, 0, 0, 480, 300);
            }
        } else if (temp.raidType == RaidType.DISRUPT) {
            dialog.getVisualPanel().showImagePortion("illustrations", "raid_disrupt_result", 640, 400, 0, 0, 480, 300);
        }

        boolean withContinue = false;

        for (GroundRaidObjectivePlugin curr : temp.objectives) {
            if (curr.withContinueBeforeResult()) {
                withContinue = true;
                break;
            }
        }

//		if (market.getMemoryWithoutUpdate().getBoolean("$raid_showContinueBeforeResult"))
//		withContinue = true;

        if (withContinue) {
            options.clearOptions();
            options.addOption("Continue", RAID_CONFIRM_CONTINUE);
        } else {
            raidConfirmContinue();
        }
        CrewReplacer_Log.pop();
    }
//why is this here? (no crew code here.)
    /*/
    public void raidConfirmContinue() {
        //Random random = getRandom();
        String contText = null;
        if (temp.raidType == RaidType.VALUABLE || true) {
            if (!temp.nonMarket) {
                if (temp.raidType == RaidType.VALUABLE || true) {
                    //text.addPara("The raid was successful in achieving its objectives.");
                }
            }

//			CargoAPI result = performRaid(random, temp.raidMult);
//
//			if (market != null) market.reapplyIndustries();
//
//			result.sort();
//
//			temp.raidLoot = result;

            int raidCredits = (int)temp.raidLoot.getCredits().get();
            if (raidCredits < 0) raidCredits = 0;

            //result.clear();
            if (raidCredits > 0) {
                AddRemoveCommodity.addCreditsGainText(raidCredits, text);
                playerFleet.getCargo().getCredits().add(raidCredits);
            }

//			if (temp.xpGained > 0) {
//				Global.getSector().getPlayerStats().addXP(temp.xpGained, dialog.getTextPanel());
//			}

            if (!temp.raidLoot.isEmpty()) {
                contText = "Pick through the spoils";
            }
            temp.contText = contText;

            float assignedTokens = 0f;
            List<Industry> disrupted = new ArrayList<Industry>();
            for (GroundRaidObjectivePlugin curr : temp.objectives) {
                assignedTokens += curr.getMarinesAssigned();
                if (curr instanceof DisruptIndustryRaidObjectivePluginImpl && curr.getSource() != null) {
                    disrupted.add(curr.getSource());
                }
            }

            RaidResultData data = new RaidResultData();
            data.market = market;
            data.entity = entity;
            data.objectives = temp.objectives;
            data.type = temp.raidType;
            data.raidEffectiveness = temp.raidMult;
            data.xpGained = temp.xpGained;
            data.marinesTokensInReserve = (int) Math.round(getNumMarineTokens() - assignedTokens);
            data.marinesTokens = getNumMarineTokens();
            data.marinesLost = temp.marinesLost;

            ListenerUtil.reportRaidObjectivesAchieved(data, dialog, memoryMap);

            if (temp.raidType == RaidType.VALUABLE) {
                ListenerUtil.reportRaidForValuablesFinishedBeforeCargoShown(dialog, market, temp, temp.raidLoot);
            } else if (temp.raidType == RaidType.DISRUPT) {
                for (Industry curr : disrupted) {
                    ListenerUtil.reportRaidToDisruptFinished(dialog, market, temp, curr);
                }
            }

        }

        Global.getSoundPlayer().playUISound("ui_raid_finished", 1f, 1f);

        FireBest.fire(null, dialog, memoryMap, "PostGroundRaid");
    }/**/
//why is this here? (NEEDED to avoid crashes.)(no crew code here.)
    /**/

    @Override
    protected void addConfirmOptions() {
        CrewReplacer_Log.loging("running function: addConfirmOptions",this,logsActive);
        CrewReplacer_Log.push();
        options.clearOptions();
//		if (temp.isSurpriseRaid) {
//			options.addOption("Launch surprise raid", RAID_CONFIRM);
//		} else {
        //options.addOption("Launch full-scale raid", RAID_CONFIRM);
        options.addOption("Launch raid", RAID_CONFIRM);
//		}

        boolean tOn = playerFleet.isTransponderOn();

        //if (!temp.nonMarket) {
        if (market != null && !market.isPlanetConditionMarketOnly()) {
            options.addOption("Make special efforts to keep your preparations secret, then proceed", RAID_CONFIRM_STORY);
            String req = "";
            if (tOn) {
                req = "\n\nRequires transponder to be turned off";
                options.setEnabled(RAID_CONFIRM_STORY, false);
            }
            options.setTooltip(RAID_CONFIRM_STORY, "Suffer no penalty to your standing with " + market.getFaction().getDisplayNameWithArticle() + ". " +
                    "Will not help if forced to turn your transponder on by patrols arriving to investigate the raid." + req);
            options.setTooltipHighlightColors(RAID_CONFIRM_STORY, market.getFaction().getBaseUIColor(), Misc.getNegativeHighlightColor());
            options.setTooltipHighlights(RAID_CONFIRM_STORY, market.getFaction().getDisplayNameWithArticleWithoutArticle(), req.isEmpty() ? req : req.substring(2));
            StoryOptionParams params = new StoryOptionParams(RAID_CONFIRM_STORY, 1, "noRepPenaltyRaid", Sounds.STORY_POINT_SPEND_LEADERSHIP,
                    "Secretly raided " + market.getName() + "");
            SetStoryOption.set(dialog, params,
                    new BaseOptionStoryPointActionDelegate(dialog, params) {
                        @Override
                        public void confirm() {
                            super.confirm();
                            raidConfirm(true);
                        }
                    });
        }


        options.addOption("Never mind", RAID_NEVER_MIND);
        options.setShortcut(RAID_NEVER_MIND, Keyboard.KEY_ESCAPE, false, false, false, true);

        boolean hostile = faction.isHostileTo(Factions.PLAYER);
        if (tOn && !hostile && !faction.isNeutralFaction()) {
            options.addOptionConfirmation(RAID_CONFIRM,
                    "The " + faction.getDisplayNameLong() +
                            " " + faction.getDisplayNameIsOrAre() +
                            " not currently hostile, and you have been positively identified. " +
                            "Are you sure you want to engage in open hostilities?", "Yes", "Never mind");
        }
        CrewReplacer_Log.pop();
    }/**/
    static final private String[] removeStatBounuses = {
            "marineXP",
            "marineXP"
    };
    private StatBonus cutMarrineXPToNewStatbounus(StatBonus input){
        StatBonus other = new StatBonus();
        //percent mods
        HashMap<String, StatMod> a = input.getPercentBonuses();
        for(Object b : a.keySet().toArray()){
            if(!a.get(b).getSource().equals(removeStatBounuses[0])) {
                other.modifyPercent(a.get(b).getSource(), a.get(b).getValue(), a.get(b).getDesc());
            }
        }
        //multi mods
        a = input.getMultBonuses();
        for(Object b : a.keySet().toArray()){
            other.modifyMult(a.get(b).getSource(), a.get(b).getValue(), a.get(b).getDesc());
        }
        //flat mods
        a = input.getFlatBonuses();
        for(Object b : a.keySet().toArray()){
            other.modifyFlat(a.get(b).getSource(), a.get(b).getValue(), a.get(b).getDesc());
        }
        //other.unmodifyPercent(removeStatBounuses[0]);
        return other;
    }
    private MutableStat cutMarrineXpToNewMutableStat(MutableStat input){
        MutableStat output = input.createCopy();
        output.unmodify(removeStatBounuses[1]);
        return output;
    }
    private void repairStatBounus(StatBonus changeStat,StatBonus orgin){
        StatMod a = orgin.getPercentBonus(removeStatBounuses[0]);
        //changeStat.modifyPercent(a.getSource(),a.getValue());//,a.getDesc());
    }
    private void tempdebug(StatBonus temp){
        if(!logsActive){return;}
        CrewReplacer_Log.loging("HERE: statThing: = " + temp,this,logsActive);
        CrewReplacer_Log.loging("flat",this,logsActive);
        tempdebug2(temp.getFlatBonuses(),logsActive);
        CrewReplacer_Log.loging("multi",this,logsActive);
        tempdebug2(temp.getMultBonuses(),logsActive);
        CrewReplacer_Log.loging("percent",this,logsActive);
        tempdebug2(temp.getPercentBonuses(),logsActive);

        CrewReplacer_Log.loging("caluclated effectiveness (with a base of 1): " + temp.computeEffective(1),this,logsActive);
    }
    private void tempdebug(MutableStat temp){
        if(!logsActive){return;}
        CrewReplacer_Log.loging("HERE: statThing: = " + temp,this,logsActive);
        CrewReplacer_Log.loging("flat",this,logsActive);
        tempdebug2(temp.getFlatMods(),logsActive);
        CrewReplacer_Log.loging("multi",this,logsActive);
        tempdebug2(temp.getMultMods(),logsActive);
        CrewReplacer_Log.loging("percent",this,logsActive);
        tempdebug2(temp.getPercentMods(),logsActive);

        CrewReplacer_Log.loging("caluclated effectiveness (with a base of 1): " + temp.getModifiedValue(),this,logsActive);
    }

    private void tempdebug2(HashMap<String, StatMod> a,boolean display){
        CrewReplacer_Log.loging("   size of: " + a.keySet().size(),this,display);
        for(int b = 0; b < a.keySet().toArray().length; b++){
            try {
                CrewReplacer_Log.loging("       " + a.keySet().toArray()[b], this, display);
                CrewReplacer_Log.loging("       " + a.get(a.keySet().toArray()[b]).getSource(), display, true);
                CrewReplacer_Log.loging("       " + a.get(a.keySet().toArray()[b]).getValue(), display, true);
                CrewReplacer_Log.loging("       " + a.get(a.keySet().toArray()[b]).getDesc(), display, true);
                CrewReplacer_Log.loging("       " , this, true);

            }catch (Exception E){
                CrewReplacer_Log.loging("       error: failed to get data. ",this,display);
            }


        }
    }
}