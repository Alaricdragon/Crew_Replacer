package data.scripts.supplyDemandLibary;


import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;

import java.util.ArrayList;
import java.util.List;

public class crewReplacer_SupplyDemandChange {
    public boolean supply;//false = demand. true = supply.
    public String name;
    public boolean onOff = true;
    private ArrayList<String> in = new ArrayList<>();
    private ArrayList<String> out = new ArrayList<>();
    private ArrayList<String> exception = new ArrayList<>();
    private ArrayList<String> requirement = new ArrayList<>();
    public crewReplacer_SupplyDemandChange(String nametemp,boolean supplytemp){
        name = nametemp;
        supply = supplytemp;
    }
    public void add(String inTemp,String outTemp){
        in.add(inTemp);
        out.add(outTemp);
    }
    public void addException(String industryName){
        exception.add(industryName);
    }
    public void addRequirement(String industryName){
        requirement.add(industryName);
    }
    public boolean rightIndustry(String industryName){
        for (String a: requirement){
            if(a.equals(industryName)){
                return true;
                //break;
            }
        }
        if(requirement.size() != 0){
            return false;
        }
        for (String a: exception){
            if(a.equals(industryName)){
                return false;
                //break;
            }
        }
        return true;
    }
    public boolean canDo(Industry industry){//for overwriting. need to add some things into here though.
        return true;
    }
    public void remove(int ID){
        in.remove(ID);
        out.remove(ID);
    }
    public void replace(Industry industry,String ID){
        List<MutableCommodityQuantity> s;
        if(supply){
            s = industry.getAllSupply();
        }else{
            s = industry.getAllDemand();
        }
        for(int a = 0; a < s.size(); a++){
            for (int b = 0; b < in.size(); b++){
                if(s.get(a).getCommodityId().equals(in.get(b))){
                    //float temp = s.get(a).getQuantity().getBaseValue();//return zero always?
                    //float temp = s.get(a).getQuantity().base;
                    float temp = s.get(a).getQuantity().getModifiedValue();
                    s.get(a).getQuantity().modifyMult(ID,0);
                    //industry.supply(ID,out.get(a),(int)5,"thing");
                    if(supply){
                        //industry.supply(ID,out.get(a),(int)temp,industry.getCurrentName());
                        industry.getSupply(out.get(b)).getQuantity().modifyFlat(ID,temp);
                        //industry.getSupply(out.get(b)).getQuantity().modifyFlat(ID,temp);//temp + 5);
                    }else{
                        //industry.(ID,out.get(a),temp,"thing");
                        industry.getDemand(out.get(b)).getQuantity().modifyFlat(ID,temp);//temp + 5);
                    }
                }
            }
        }
    }
    public void undo(Industry industry,String ID){
        List<MutableCommodityQuantity> s = industry.getAllSupply();
        for(int a = 0; a < s.size(); a++){
            //for (int b = 0; b < in.size(); b++){
                //if(s.get(a).getCommodityId().equals(in.get(a))){
                    s.get(a).getQuantity().unmodifyMult(ID);
                    s.get(a).getQuantity().unmodifyFlat(ID);
                    //industry.getSupply(ID).getQuantity().unmodifyFlat(ID);//HERE first ID is mod ID but should be commoditie ID
                    //industry.suppl
                    //float temp = s.get(a).getQuantity().getBaseValue();
                    //s.get(a).getQuantity().modifyFlat(ID,-temp);
                    //industry.supply(ID,out.get(a),(int)temp,"thing");
                //}
            //}
        }
        s = industry.getAllDemand();
        for(int a = 0; a < s.size(); a++){
            s.get(a).getQuantity().unmodifyMult(ID);
            s.get(a).getQuantity().unmodifyFlat(ID);
            //industry.getDemand(ID).getQuantity().unmodifyFlat(ID);
        }
    }

    public void applyExtraData(Industry industry, String ID){

    }
    public void undoExtraData(Industry industry, String ID){

    }
}
