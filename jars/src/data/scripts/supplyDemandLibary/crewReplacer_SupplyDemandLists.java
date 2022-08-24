package data.scripts.supplyDemandLibary;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.ArrayList;

public class crewReplacer_SupplyDemandLists {
    public static ArrayList<crewReplacer_SupplyDemandSet> list= new ArrayList<>();
    public static void runAll(MarketAPI market){
        for(crewReplacer_SupplyDemandSet set:list){
            set.applyMarket(market,false);
        }
    }
    public static void addOrMergeRuleSet(crewReplacer_SupplyDemandSet set){
        for(crewReplacer_SupplyDemandSet set2:list) {
            if (set2.name.equals(set.name)){
                //mergesets?
                set.merge(set2);
                return;
            }
        }
        list.add(set);
    }
    public static void removeRuleSet(String name){
        for(crewReplacer_SupplyDemandSet set:list) {
            if (set.name.equals(name)){
                list.remove(set);
                return;
            }
        }
    }
    public static void setRuleSetOnOrOff(String name,boolean onOrOff){
        for(crewReplacer_SupplyDemandSet set:list) {
            if (set.name.equals(name)){
                set.onOff = onOrOff;
            }
        }
    }
    public static crewReplacer_SupplyDemandSet getRuleSet(String name){
        for(crewReplacer_SupplyDemandSet set:list){
            if(set.name.equals(name)){
                return set;
            }
        }
        return null;
    }
}
