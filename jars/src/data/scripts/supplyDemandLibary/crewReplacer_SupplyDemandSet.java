package data.scripts.supplyDemandLibary;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.ArrayList;

public class crewReplacer_SupplyDemandSet {
    public boolean onOff = true;
    public String name = "";
    public crewReplacer_SupplyDemandSet(String nametemp){
        name = nametemp;
    }
    public boolean active(MarketAPI market){//for user to override.
        return true;
    }
    public ArrayList<data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange> list= new ArrayList<>();
    public void merge(crewReplacer_SupplyDemandSet set){
        for (data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange rule: set.list){
            list.add(rule);
        }
    }
    //HERE change this so i merge inems instad. forceing then to all have names
    public void addItem(data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange SDC){
        for (int a = 0; a < list.size(); a++) {
            if (SDC.name.equals(list.get(a).name)){
                list.set(a,SDC);
                return;
            }
        }
        SDC.name = name;
        list.add(SDC);
    }
    public void removeItem(String name){
        for (data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange rule: list){
            if (rule.name.equals(name)){
                list.remove(rule);
                return;
            }
        }
    }
    public data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange getItem(String name){
        for(int a = 0; a < list.size(); a++){
            if(name.equals(list.get(a).name)){
                return list.get(a);
            }
        }
        return null;
    }
    public void RunAll(Industry industry, String ID){
        industry.getId();
        for(data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange i: list){
            if(i.canDo(industry) && i.rightIndustry(industry.getSpec().getId())){
                i.replace(industry,ID);
                i.applyExtraData(industry,ID);
            }
        }
    }
    public void resetAll(Industry industry, String ID){
        for(data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange i: list){
            i.undo(industry,ID);
            i.undoExtraData(industry,ID);
        }
    }
    public void run(String name,Industry industry, String ID){
        for(data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange a : list){
            if(a.name.equals(name)){
                a.replace(industry, ID);
                a.applyExtraData(industry,ID);
            }
        }
    }
    public void applyMarket(MarketAPI market,boolean remove){
        if(onOff && active(market) && !remove) {
            for (Industry industry : market.getIndustries()) {
                int a = 0;
                for (data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange i : list) {
                    String ID = a + "_" + name + "_" + i.name;//HERE. this is bad coding, to put it simply. if someone has an fre sets with names that start with numbers... need to fix the ID issue.
                    /*why is ID wrong? the i.names are all the same... every single one of them is the same. I don't know why.
                     in fact, they cant be the same. i don't understand how. but they are. frustrating.
                     */
                    if (i.onOff && i.canDo(industry) && i.rightIndustry(industry.getSpec().getId())) {
                        i.replace(industry, ID);
                        i.applyExtraData(industry, ID);
                    } else {
                        i.undo(industry, ID);
                        i.undoExtraData(industry, ID);
                    }
                    a++;
                }
            }
        }else{
            for (Industry industry : market.getIndustries()) {
                for (data.scripts.supplyDemandLibary.crewReplacer_SupplyDemandChange i : list) {
                    String ID = i.name;
                    i.undo(industry, ID);
                    i.undoExtraData(industry, ID);
                }
            }
        }
    }
}
