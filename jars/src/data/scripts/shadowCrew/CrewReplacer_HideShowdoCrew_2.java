package data.scripts.shadowCrew;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.CargoScreenListener;
import data.scripts.CrewReplacer_Log;

import java.util.List;

public class CrewReplacer_HideShowdoCrew_2 implements CargoScreenListener {
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
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        for(String[] remove : showdos) {
            for (String a : remove) {
                removeItem(cargo, a);
            }
        }

        /*
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
        clean = true;*/
    }
    public static void removeShowdoCrewFromPlayersFleet(String[] remove){
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        for (String a : remove){
            removeItem(cargo,a);
        }
        /*
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
        CrewReplacer_Log.pop();*/

    }
    public static void removeItem(CargoAPI cargo,String itemID){
        cargo.removeCommodity(itemID,cargo.getCommodityQuantity(itemID));
    }
    public static String[] getShowdos(String type){
        for(int a = 0; a < showdoTypes.length; a++){
            if(showdoTypes[a].equals(type)){
                return showdos[a];
            }
        }
        return null;
    }
    /*private static CrewReplacer_HideShowdoCrew instance = null;
    public static CrewReplacer_HideShowdoCrew getInstance(){
        if(instance == null){
            instance = new CrewReplacer_HideShowdoCrew();
            CrewReplacer_Log.loging("creating new instance",instance);
        }
        CrewReplacer_Log.loging("returning instance...",instance);
        initThings(instance);
        return instance;
    }
    protected static void initThings(CrewReplacer_HideShowdoCrew instance){
        GenericPluginManagerAPI plugins = Global.getSector().getGenericPlugins();
        plugins.addPlugin(instance, true);
        Global.getSector().getListenerManager().addListener(instance,true);
    }*/
    public static void addListener(){
        Global.getSector().getListenerManager().addListener(new CrewReplacer_HideShowdoCrew_2(), true);
    }
    @Override
    public void reportCargoScreenOpened() {
        removeShowdoCrewFromPlayersFleet();
    }

    @Override
    public void reportPlayerLeftCargoPods(SectorEntityToken entity) {

    }

    @Override
    public void reportPlayerNonMarketTransaction(PlayerMarketTransaction transaction, InteractionDialogAPI dialog) {

    }

    @Override
    public void reportSubmarketOpened(SubmarketAPI submarket) {
        removeShowdoCrewFromPlayersFleet();
    }
}
