package data.scripts.shadowCrew;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.listeners.CommodityIconProvider;
import data.scripts.CrewReplacer_Log;

public class CrewReplacer_HideShowdoCrew implements CommodityIconProvider {
    public static String[][] showdos = {{
            "CrewReplacer_Shawdo_crew",
            "CrewReplacer_Shawdo_supplies",
            "CrewReplacer_Shawdo_heavy_machinery"    }};
    public static String[] showdoTypes = {
            "survey"
    };
    public static void addShowdoCrewToPlayerFleet(String commodityID,int amount){
        //CrewReplacer_Log.loging("adding showdows to player fleet of ID, amount: "+commodityID+", "+amount,getInstance(),true);
        Global.getSector().getPlayerFleet().getCargo().addCommodity(commodityID,amount);
        //CrewReplacer_Log.loging("player fleet now has: "+Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity(commodityID)+" showdo crew of this commodityID",getInstance(),true);
    }
    public static void removeShowdoCrewFromPlayersFleet(){
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        for(String[] a : showdos){
            for(String b : a){
                cargo.removeCommodity(b,cargo.getCommodityQuantity(b));
            }
        }
    }
    public static String[] getShowdos(String type){
        for(int a = 0; a < showdoTypes.length; a++){
            if(showdoTypes[a].equals(type)){
                return showdos[a];
            }
        }
        return null;
    }
    private static CrewReplacer_HideShowdoCrew instance = null;
    public static CrewReplacer_HideShowdoCrew getInstance(){
        if(instance == null){
            instance = new CrewReplacer_HideShowdoCrew();
        }
        return instance;
    }
    public CrewReplacer_HideShowdoCrew() {
        super();
        GenericPluginManagerAPI plugins = Global.getSector().getGenericPlugins();
        plugins.addPlugin(this, true);
        Global.getSector().getListenerManager().addListener(this);
    }
    @Override
    public String getRankIconName(CargoStackAPI stack) {
        for(String[] b : showdos) {
            for (String a : b) {
                if (stack.getCommodityId().equals(a)) {
                    CrewReplacer_Log.loging("attempting to remove a showdo crew named: "+a, this);
                    try {
                        stack.getCargo().removeStack(stack);
                    }catch (Exception e){
                        CrewReplacer_Log.loging("failed to remove a showdow crew for unknown reasons... please contact the mod author of crew replacer and get them to fix...",this,true);
                    }
                    CrewReplacer_Log.pop();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public String getIconName(CargoStackAPI stack) {
        /*
        CrewReplacer_Log.loging("trying to hide a showdo...",this,true);
        CrewReplacer_Log.push();
        for(String a : showdos) {
            CrewReplacer_Log.loging("is this "+a+"?",this,true);
            if (stack.getCommodityId().equals(a)) {
                CrewReplacer_Log.loging("yes. trying to remove...",this,true);
                stack.getCargo().removeStack(stack);
                CrewReplacer_Log.pop();
                return null;
            }
            CrewReplacer_Log.loging("no. not removing...",this,true);
        }
        CrewReplacer_Log.pop();*/
        return null;
    }

    @Override
    public int getHandlingPriority(Object params) {
        return 0;
    }
}
