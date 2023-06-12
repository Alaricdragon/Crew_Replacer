package data.scripts;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.listeners.*;
import com.fs.starfarer.api.impl.PlayerFleetPersonnelTracker;
import com.thoughtworks.xstream.XStream;
import data.scripts.combatabilityPatches.CrewReplacer_InitCombatabilityPatches;
import data.scripts.crews.CrewReplacer_CrewType_marine;
import data.scripts.replacementscripts.CrewReplacer_PlayerFleetPersonnelTracker;

import java.util.List;

/*
 */
public class crew_replacer_startup extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        startup2();
        CrewReplacer_InitCombatabilityPatches.onApplicationLoad();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        crewReplacer_Main.organizePriority();
        //replacePlayerFleetPersonnelTracker();
        //replacePlayerFleetPersonnelTracker2();
        addListinger();
        super.onGameLoad(newGame);
        CrewReplacer_InitCombatabilityPatches.onGameLoad(newGame);
    }
    @Override
    public void configureXStream(XStream x) {
        super.configureXStream(x);
        //x.alias("CrewReplacerPlayerFleetPersonnelTracker", CrewReplacer_PlayerFleetPersonnelTracker.class);
        //x.alias("PlayerFleetPersonnelTracker", CrewReplacer_PlayerFleetPersonnelTracker.class);
    }
    private void startup2() {
        this.addDefaultCrew();
    }
    private void addDefaultCrew(){
        String jobSet_crew = "crew";
        String jobSet_supplies = "supplies";
        String jobSet_heavy_machinery = "heavy_machinery";
        String jobSet_marines = "marines";
        String jobSet_metals = "metals";
        String jobSet_rare_metals = "rare_metals";

        crewReplacer_Job tempJob = crewReplacer_Main.getJob("salvage_crew");
        tempJob.addNewCrew("crew",1,10);
        tempJob.addCrewSet(jobSet_crew);

        tempJob = crewReplacer_Main.getJob("salvage_heavyMachinery");
        tempJob.addNewCrew("heavy_machinery",1,10);
        tempJob.addCrewSet(jobSet_heavy_machinery);

        /*tempJob = crewReplacer_Main.getJob("survey_crew");
        tempJob.addNewCrew("crew",1,10);
        tempJob.addCrewSet(jobSet_crew);*/


        tempJob = crewReplacer_Main.getJob("raiding_marines");
        CrewReplacer_CrewType_marine tempcrew = new CrewReplacer_CrewType_marine();
        tempcrew.crewPower = 1;
        tempcrew.crewPriority = 10;
        tempcrew.name = "marines";
        tempJob.addCrew(tempcrew);
        tempJob.addCrewSet(jobSet_marines);
        //supplyDemandChangeInit();


        tempJob = crewReplacer_Main.getJob("Mission_hijack_marines");
        tempJob.addNewCrew("marines",1,10);
        //tempJob.addNewCrew("crew",1,10);
        tempJob.addCrewSet(jobSet_marines);



        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_Metals");
        tempJob.addNewCrew("metals",1,10);
        tempJob.addCrewSet(jobSet_metals);

        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_RareMetals");
        tempJob.addNewCrew("rare_metals",1,10);
        tempJob.addCrewSet(jobSet_rare_metals);

        tempJob = crewReplacer_Main.getJob("CoronalHyperShunt_repair_Crew");
        tempJob.addNewCrew("crew",1,10);
        tempJob.addCrewSet(jobSet_crew);


        tempJob = crewReplacer_Main.getJob("survey_crew");
        tempJob.addCrewSet(jobSet_crew);
        tempJob = crewReplacer_Main.getJob("survey_supply");
        tempJob.addCrewSet(jobSet_supplies);
        tempJob = crewReplacer_Main.getJob("survey_heavyMachinery");
        tempJob.addCrewSet(jobSet_heavy_machinery);


        //crewReplacer_Main.getCrewSet("normadicSurvival_metals").addNewCrew("cat",1,10);
        //crewReplacer_Main.getCrewSet("normadicSurvival_metals").addNewCrew("crew",1,11);
        //crewReplacer_Main.getCrewSet("normadicSurvival_metals").addNewCrew("AIretrofit_Omega_SurveyDrone",50f,9);

    }
    private void addDefaultCrewSets(){
        //crewReplacer_CrewSet set = crewReplacer_Main.getCrewSet(jobSet_crew);
        //crewReplacer_Job job = crewReplacer_Main.getJob()
    }
    private void addListiner(){
        //(GroundRaidObjectivesListener)CrewReplacer_PlayerFleetPersonnelTracker;
    }
    private void replacePlayerFleetPersonnelTracker(){
        CrewReplacer_Log.loging("removing the PlayerFleetPersonnelTracker so i can replace it with my own...",this);
        CrewReplacer_Log.push();
        //Global.getSector().getGenericPlugins().
        /*List<GenericPluginManagerAPI.GenericPlugin> a = Global.getSector().getGenericPlugins().getPluginsOfClass(PlayerFleetPersonnelTracker.class);
        for(GenericPluginManagerAPI.GenericPlugin b:a) {
            CrewReplacer_Log.loging("removing plugin: " + b.getClass().getCanonicalName(),this);
            Global.getSector().getGenericPlugins().removePlugin(b);
        }
        Global.getSector().getGenericPlugins().addPlugin(new CrewReplacer_PlayerFleetPersonnelTracker());
        CrewReplacer_Log.loging("removing PlayerFleetPersonnelTracker from listner: ",this);
        Global.getSector().getListenerManager().removeListener(PlayerFleetPersonnelTracker.class);
        Global.getSector().getListenerManager().addListener(new CrewReplacer_PlayerFleetPersonnelTracker(),true);
        */

        int count = 0;
        CrewReplacer_Log.loging("trying to remove generic trackers. removing",this);
        CrewReplacer_Log.push();
        List<GenericPluginManagerAPI.GenericPlugin> a = Global.getSector().getGenericPlugins().getPluginsOfClass(PlayerFleetPersonnelTracker.class);
        for(GenericPluginManagerAPI.GenericPlugin b:a) {
            CrewReplacer_Log.loging("removing plugin: " + b.getClass().getCanonicalName(),this);
            Global.getSector().getGenericPlugins().removePlugin(b);
            count++;
        }
        CrewReplacer_Log.loging("removed a total of " + count + " plugging. should be one",this);
        CrewReplacer_Log.pop();

        List<GroundRaidObjectivesListener> RaidListiner = Global.getSector().getListenerManager().getListeners(GroundRaidObjectivesListener.class);
        String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: GroundRaidObjectivesListener..",this);
        CrewReplacer_Log.push();
        for(GroundRaidObjectivesListener b:RaidListiner){
            if(b.getClass().getCanonicalName().equals(removeName)){
                RaidListiner.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);


        List<CommodityTooltipModifier> CommodityTooltipModifier = Global.getSector().getListenerManager().getListeners(com.fs.starfarer.api.campaign.listeners.CommodityTooltipModifier.class);
        //String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: CommodityTooltipModifier..",this);
        CrewReplacer_Log.push();
        for(CommodityTooltipModifier b:CommodityTooltipModifier){
            if(b.getClass().getCanonicalName().equals(removeName)){
                CommodityTooltipModifier.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);
        //CrewReplacer_Log.loging("adding replacement traker...",this);
        CrewReplacer_Log.pop();


        List<CommodityIconProvider> CommodityIconProvider = Global.getSector().getListenerManager().getListeners(CommodityIconProvider.class);
        //String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: CommodityIconProvider..",this);
        CrewReplacer_Log.push();
        for(CommodityIconProvider b:CommodityIconProvider){
            if(b.getClass().getCanonicalName().equals(removeName)){
                CommodityIconProvider.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);
        //CrewReplacer_Log.loging("adding replacement traker...",this);
        CrewReplacer_Log.pop();


        List<CargoScreenListener> CargoScreenListener = Global.getSector().getListenerManager().getListeners(com.fs.starfarer.api.campaign.listeners.CargoScreenListener.class);
        //String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: CargoScreenListener..",this);
        CrewReplacer_Log.push();
        for(CargoScreenListener b:CargoScreenListener){
            if(b.getClass().getCanonicalName().equals(removeName)){
                CargoScreenListener.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);
        //CrewReplacer_Log.loging("adding replacement traker...",this);
        CrewReplacer_Log.pop();



        List<ColonyInteractionListener> ColonyInteractionListener = Global.getSector().getListenerManager().getListeners(com.fs.starfarer.api.campaign.listeners.ColonyInteractionListener.class);
        //String removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: ColonyInteractionListener..",this);
        CrewReplacer_Log.push();
        for(ColonyInteractionListener b:ColonyInteractionListener){
            if(b.getClass().getCanonicalName().equals(removeName)){
                ColonyInteractionListener.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);
        //CrewReplacer_Log.loging("adding replacement traker...",this);
        CrewReplacer_Log.pop();



        List<PlayerFleetPersonnelTracker> temp = Global.getSector().getListenerManager().getListeners(PlayerFleetPersonnelTracker.class);
        removeName = PlayerFleetPersonnelTracker.class.getCanonicalName();
        count = 0;
        CrewReplacer_Log.loging("removing trakers of removing class: PlayerFleetPersonnelTracker..",this);
        CrewReplacer_Log.push();
        for(PlayerFleetPersonnelTracker b:temp){
            if(b.getClass().getCanonicalName().equals(removeName)){
                temp.remove(removeName);
                count++;
                CrewReplacer_Log.loging("removed a tracker of the name: " + b.getClass().getCanonicalName(),this);
            }
        }
        CrewReplacer_Log.loging("removed a total of " + count + " trackers. should be one?",this);


        //Global.getSector().getGenericPlugins().addPlugin(new CrewReplacer_PlayerFleetPersonnelTracker());
        Global.getSector().getListenerManager().addListener(new CrewReplacer_PlayerFleetPersonnelTracker(),true);
    }
    private void replacePlayerFleetPersonnelTracker2(){
        CrewReplacer_Log.loging("removing the PlayerFleetPersonnelTracker so i can replace it with my own...",this);
        CrewReplacer_Log.push();
        //PlayerFleetPersonnelTracker.getInstance()
        List<GroundRaidObjectivesListener> list = Global.getSector().getListenerManager().getListeners(GroundRaidObjectivesListener.class);
        int b = 0;
        for(int a = 0; a < list.size(); a++){
            if(list.get(a).getClass().getCanonicalName().equals(PlayerFleetPersonnelTracker.getInstance().getClass().getCanonicalName())){
                Global.getSector().getListenerManager().removeListener(list.get(a));
                CrewReplacer_Log.loging("removed: " + list.get(a).getClass().getCanonicalName(),this);
                b++;
            }
        }
        Global.getSector().getGenericPlugins().removePlugin(Global.getSector().getGenericPlugins().getPluginsOfClass(PlayerFleetPersonnelTracker.class).get(0));
        CrewReplacer_Log.loging("removed " + b + " listiners. should be 1.",this);
        Global.getSector().getListenerManager().addListener(new CrewReplacer_PlayerFleetPersonnelTracker(),true);

        List<PlayerFleetPersonnelTracker> list3 = Global.getSector().getListenerManager().getListeners(PlayerFleetPersonnelTracker.class);
        CrewReplacer_Log.loging(list3.size()  + "listener remaining. should be not zero? ",this);
        CrewReplacer_Log.pop();
    }
    private void addListinger(){
        Global.getSector().getListenerManager().addListener(new CrewReplacer_PlayerFleetPersonnelTracker(),true);
    }
}