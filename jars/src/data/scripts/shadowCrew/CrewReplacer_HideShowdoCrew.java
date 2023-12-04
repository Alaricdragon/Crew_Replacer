package data.scripts.shadowCrew;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.listeners.CommodityIconProvider;
import data.scripts.CrewReplacer_Log;

import java.util.List;

public class CrewReplacer_HideShowdoCrew implements CommodityIconProvider {
    public static CrewReplacer_HideShowdoCrew log = new CrewReplacer_HideShowdoCrew();
    public static boolean clean = false;
    public static String[][] showdos = {{
            "CrewReplacer_Shawdo_crew",
            "CrewReplacer_Shawdo_heavy_machinery"},
            {"CrewReplacer_Shawdo_supplies"}
    };
    public static String[] showdoTypes = {
            "survey",
            "survey2"
    };
    public static void addShowdoCrewToPlayerFleet(String commodityID,int amount){
        clean = false;
        //CrewReplacer_Log.loging("adding showdows to player fleet of ID, amount: "+commodityID+", "+amount,getInstance(),true);
        Global.getSector().getPlayerFleet().getCargo().addCommodity(commodityID,amount);
        //CrewReplacer_Log.loging("player fleet now has: "+Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity(commodityID)+" showdo crew of this commodityID",getInstance(),true);
    }
    public static void removeShowdoCrewFromPlayersFleet(){
        if (clean&&false)return;
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        List<CargoStackAPI> stakes = cargo.getStacksCopy();
        boolean gone = false;
        CrewReplacer_Log.loging("starting to check "+stakes.size()+" stacks for showdows to purfiy",log);
        CrewReplacer_Log.push();
        for(int c = 0; c < stakes.size(); c++) {
            CrewReplacer_Log.loging("Stack "+c+" of size "+stakes.size(),log);
            CrewReplacer_Log.push();
            gone = false;
            for (String[] a : showdos) {
                CrewReplacer_Log.loging("showdotype: "+a,log);
                CrewReplacer_Log.push();
                for (String b : a) {
                    CrewReplacer_Log.loging("RE - SE: "+stakes.get(c).getResourceIfResource()+", "+stakes.get(c).getSpecialItemSpecIfSpecial(),log);
                    //stakes.get(c).getResourceIfResource().getId();
                    //stakes.get(c).getSpecialItemSpecIfSpecial().getId();
                    if((stakes.get(c).getResourceIfResource()!=null && stakes.get(c).getResourceIfResource().getId().equals(b))|| (stakes.get(c).getSpecialItemSpecIfSpecial() != null && stakes.get(c).getSpecialItemSpecIfSpecial().getId().equals(b))){
                        //float size = stakes.get(c).getSize();
                        //stakes.get(c).subtract(size);
                        //stakes.get(c).setCargo(null);
                        cargo.removeStack(stakes.get(c));
                        stakes.remove(c);
                        c--;
                        gone = true;
                        CrewReplacer_Log.loging("removed stack! (on showdow: "+b+")",log);
                        break;
                    }
                    //cargo.removeCommodity(b, cargo.getCommodityQuantity(b));
                }
                CrewReplacer_Log.pop();
                if (gone) break;
            }
            CrewReplacer_Log.pop();
        }
        CrewReplacer_Log.pop();
        clean = true;
    }
    public static void removeShowdoCrewFromPlayersFleet(String[] remove){
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        List<CargoStackAPI> stakes = cargo.getStacksCopy();
        CrewReplacer_Log.loging("starting to check "+stakes.size()+" stacks for showdows to purfiy",log);
        CrewReplacer_Log.push();
        for(int c = 0; c < stakes.size(); c++) {
            CrewReplacer_Log.loging("Stack "+c+" of size "+stakes.size(),log);
            CrewReplacer_Log.push();
            for (String b : remove) {
                CrewReplacer_Log.loging("RE - SE: "+stakes.get(c).getResourceIfResource()+", "+stakes.get(c).getSpecialItemSpecIfSpecial(),log);
                //stakes.get(c).getResourceIfResource().getId();
                    //stakes.get(c).getSpecialItemSpecIfSpecial().getId();
                if((stakes.get(c).getResourceIfResource()!=null && stakes.get(c).getResourceIfResource().getId().equals(b))|| (stakes.get(c).getSpecialItemSpecIfSpecial() != null && stakes.get(c).getSpecialItemSpecIfSpecial().getId().equals(b))){
                    //float size = stakes.get(c).getSize();
                        //stakes.get(c).subtract(size);
                        //stakes.get(c).setCargo(null);
                    cargo.removeStack(stakes.get(c));
                    stakes.remove(c);
                    c--;
                    CrewReplacer_Log.loging("removed stack! (on showdow: "+b+")",log);
                    break;
                }
                //cargo.removeCommodity(b, cargo.getCommodityQuantity(b));
            }
            CrewReplacer_Log.pop();
        }
        CrewReplacer_Log.pop();

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
            CrewReplacer_Log.loging("creating new instance",instance);
        }
        CrewReplacer_Log.loging("returning instance...",instance);
        initThings(instance);
        return instance;
    }
    public CrewReplacer_HideShowdoCrew() {
        super();
    }
    protected static void initThings(CrewReplacer_HideShowdoCrew instance){
        GenericPluginManagerAPI plugins = Global.getSector().getGenericPlugins();
        plugins.addPlugin(instance, true);
        Global.getSector().getListenerManager().addListener(instance,true);
    }
    @Override
    public String getRankIconName(CargoStackAPI stack) {
        //removeShowdoCrewFromPlayersFleet();
        /**/
        for(String[] b : showdos) {
            for (String a : b) {
                if (stack.getCommodityId().equals(a)) {
                    CrewReplacer_Log.loging("attempting to remove a showdo crew named: "+a, this);
                    try {
                        stack.getCargo().removeStack(stack);
                    }catch (Exception e){
                        CrewReplacer_Log.loging("failed to remove a showdow crew for unknown reasons... please contact the mod author of crew replacer and get them to fix...",this);
                    }
                    return null;
                }
            }
        }/**/
        return null;
    }

    @Override
    public String getIconName(CargoStackAPI stack) {
        //removeShowdoCrewFromPlayersFleet();
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
