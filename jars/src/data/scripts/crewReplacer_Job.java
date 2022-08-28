package data.scripts;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.ResourceCostPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.campaign.fleet.CampaignFleet;
import com.fs.starfarer.ui.S;
import org.lwjgl.Sys;

import java.awt.*;
import java.util.ArrayList;

public class crewReplacer_Job {
    /*
        requirements:
        (done)way to display and apply crew lost
        -when doing a job
            -for example: the crew lost salvage module only triggers if any crew were lost. might want to trigger always for min loss percent factors.
        -when applying other loss factors.
        -both

        (done?)way to display crew available
        -i don't want to rebuild the crew display 40 times thanks
     */
    public String name;
    //ArrayList<String> crew;
    //ArrayList<Integer> crewID;
    //ArrayList<Float> crewPriority;//this will be organized.
    public ArrayList<ArrayList<Integer>> crewPriority;//organized greatest to lowest.
    public ArrayList<crewReplacer_Crew> Crews = new ArrayList<crewReplacer_Crew>();

    //public Color defalthighlihgt = Color.RED;
    public crewReplacer_Crew getCrew(String crew){
        boolean out = true;
        crewReplacer_Crew output = null;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                output = Crews.get(a);
                out = false;
                break;
            }
        }
        if(out){
            output = new crewReplacer_Crew();
            output.name = crew;
            Crews.add(output);
        }
        return output;
    }
    public boolean removeCrew(String crew){
        boolean output = false;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                Crews.remove(a);
                organizePriority();//for now. will have to change later?
                output = true;
                break;
            }
        }
        return output;
    }
    public boolean addCrew(crewReplacer_Crew crew){
        boolean output = true;
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew.name)){
                output = false;
                mergeCrew(Crews.get(a),crew.crewPower,crew.crewPriority/*,crew.maxLosePercent,crew.minLosePercent,crew.NormalLossRules*/);
                break;
            }
        }
        if(output){
            Crews.add(crew);
        }
        return output;
    }
    public boolean addNewCrew(String crew,float crewPower,float crewPriority/*,float crewMaxLosePercent,float crewMinLosePercent,boolean crewNormalLoseRules*/){
        boolean output = true;
        crewReplacer_Crew temp = new crewReplacer_Crew();
        for(int a = 0; a < Crews.size(); a++){
            if(Crews.get(a).name.equals(crew)){
                output = false;
                temp = Crews.get(a);
                break;
            }
        }
        if(output){
            temp.name = crew;
            Crews.add(temp);
        }
        mergeCrew(temp,crewPower,crewPriority/*,crewMaxLosePercent,crewMinLosePercent,crewNormalLoseRules*/);
        return output;
    }
    private void mergeCrew(crewReplacer_Crew crew,float crewPower,float crewPriority/*,float crewMaxLosePercent,float crewMinLosePercent,boolean crewNormalLoseRules*/){
        crew.crewPower = crewPower;
        crew.crewPriority = crewPriority;
        //crew.maxLosePercent = crewMaxLosePercent;
        //crew.maxLosePercent = crewMinLosePercent;
        //crew.NormalLossRules = crewNormalLoseRules;
    }


    void organizePriority(){
        crewPriority = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(0);
        crewPriority.add(temp);
        int min;
        int max;
        float look;
        Boolean done;
        for(int a = 1; a < Crews.size(); a++){
            //System.out.println("processing crew: " + a + "with a priority of: " + Crews.get(a).crewPriority);
            float priority = Crews.get(a).crewPriority;
            min = 0;
            max = crewPriority.size();
            done = false;
            while(max != min){
                look = (min + max) / 2;
                temp = crewPriority.get((int)look);
                float priority1 = Crews.get(temp.get(0)).crewPriority;
                if(priority < priority1){
                    int last = min;
                    min = (int)look;
                    if(last == min){
                        min++;
                    }
                }else if(priority > priority1){
                    max = (int)look;
                }else{
                    //System.out.println(a + " added to " + (int) look + " by 5");
                    temp = crewPriority.get((int)look);
                    temp.add(a);
                    crewPriority.set((int)look,temp);
                    done = true;
                    break;
                }
            }
            if(!done){
                temp = new ArrayList<Integer>();
                temp.add(a);
                if(max == crewPriority.size()){
                    float priority1 = Crews.get(crewPriority.get(max - 1).get(0)).crewPriority;
                    if(priority < priority1){
                        //System.out.println(a + " added at " + (max) + " by 6");
                        crewPriority.add(temp);
                    }else{
                        //System.out.println(a + " added at " + (max - 1) + " by 7");
                        crewPriority.add(max - 1,temp);
                    }
                }else{
                    float priority1 = Crews.get(crewPriority.get(max).get(0)).crewPriority;
                    if(priority < priority1){
                        //System.out.println(a + " added at " + (max + 1) + " by 8");
                        crewPriority.add(max + 1,temp);
                    }else{
                        //System.out.println(a + " added at " + (max) + " by 9");
                        crewPriority.add(max,temp);
                    }
                }
            }
        }
    }
    public float getAvailableCrewPower(CampaignFleetAPI fleet){
        float output = 0;
        float number_of_crew;
        for(int a = 0; a < Crews.size(); a++){
            output += Crews.get(a).getCrewPowerInFleet(fleet);
            //output += (50);//462(50)//512(0)
        }
        return output;
    }
    public float[] getAvailableCrew(CampaignFleetAPI fleet){
        float[] output = new float[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            output[a] = Crews.get(a).getCrewInFleet(fleet);
        }
        return output;
    }


    public void automaticlyGetDisplayAndApplyCrewLost(CampaignFleetAPI fleet,int crewPowerRequired, float crew_power_to_lose,TextPanelAPI text){
        ArrayList<Float> crewUsed = getCrewForJob(fleet,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(crewUsed,crew_power_to_lose);
        displayCrewLost(crewLost,text);
        applyCrewLost(crewLost,fleet);
    }
    /*public void automaticlyGetDisplayAndApplyCrewLost(CampaignFleetAPI fleet,int crewPowerRequired, float crew_power_to_lose,TextPanelAPI text){
        ArrayList<Float> crewUsed = getCrewForJob(fleet,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(crewUsed,crew_power_to_lose);
        //Color highlight = defalthighlihgt;
        displayCrewLost(crewLost,text);
        applyCrewLost(crewLost,fleet);
    }*/
    public ArrayList<Float> automaticlyGetAndApplyCrewLost(CampaignFleetAPI fleet, int crewPowerRequired, float crew_power_to_lose){
        ArrayList<Float> crewUsed = getCrewForJob(fleet,crewPowerRequired);
        ArrayList<Float> crewLost = getCrewLost(crewUsed,crew_power_to_lose);
        //displayCrewLost(crewLost,text,highlight);
        applyCrewLost(crewLost,fleet);
        return crewLost;
    }
    public ArrayList<Float> getCrewLost(ArrayList<Float> crewUsed, float crew_power_to_lose){
        //crewUsed is an array of floats, each one equal to the amound of crew used on a given ID.
        //gets the number of crew lost, based on crew power. chocen randomly from an array of crew.
        //priority of crew dose not matter here. this is the crew lost in this task, form the crew that worked it.
        //outputs [crewid0 lost crew,crewid1 lost crew,crewid2 lost crew,extra]
        ArrayList<Float> output = new ArrayList<Float>();
       /* int JID = getID(jobs,job);
        if(JID == -1){
            return output;
        }*/
        ArrayList<Float> temp2 = new ArrayList<Float>();
        for(int a = 0; a < Crews.size(); a++){
            temp2.add(Crews.get(a).crewPower);
        }
        output = getRandomNumberList(crewUsed,temp2,crew_power_to_lose);
        float temp;
        for(int a = 0; a < output.size(); a++){
            output.set(a,Crews.get(a).getCrewToLose(crewUsed.get(a),output.get(a)));//new system, to allow more contol.
            //temp = (float) Crews.get(a).getCrewLostPercent();
            //1,15,0 == 15//1,15,30 == 0
            //output.set(a,output.get(a) + (temp * Math.max(crewUsed.get(a) - output.get(a),0)));
        }
        return output;
    }
    public void displayCrewLost(ArrayList<Float> crewLost, TextPanelAPI text){
        //text.appendToLastParagraph(" runing display for:  " + name);//for testing.
        //text.appendToLastParagraph(" sizes: " + crewLost.size() + ", " + Crews.size());
        boolean last = false;
        for(int a = 0; a < crewLost.size(); a++){
            //String displayName = Crews.get(a).name;
            if(last && crewLost.get(a) >= 1){
                text.appendToLastParagraph(", ");
            }
            if(crewLost.get(a) != 0){
                last = true;
                Crews.get(a).DisplayedCrewNumbers(crewLost.get(a),text);
            }
            /*if(crewLost.get(a) > 1){
                last = true;
                text.appendToLastParagraph(message[0] + "" + crewLost.get(a) + " " + displayName + "s");
                text.highlightInLastPara(highlight, displayName + "s");
            }else if(crewLost.get(a) == 1){
                last = true;
                text.appendToLastParagraph(message[1] + " " + displayName);
                text.highlightInLastPara(highlight, displayName);
            }*/
        }
    }
    public void applyCrewLost(ArrayList<Float> crewLost,CampaignFleetAPI fleet){
        for(int a = 0; a < Crews.size(); a++) {
            Crews.get(a).removeCrew(fleet,crewLost.get(a));
            //String ComonadyName = Crews.get(a).name;
            //fleet.getCargo().removeCommodity(ComonadyName, crewLost.get(a));
        }

    }
    public ArrayList<Float> getCrewForJob(CampaignFleetAPI fleet, float crewPowerRequired){
        /*
            this is my changed getCrewForJob. it is compleat and somewhat tested..
            it worked in 3 repeating stages (starting from the second for loop):
            1: it gets the available crew in this priority.
            2: it sees if the crew power in this prioirity is more then what is required
            3a:it is more then required, in this case it randomly choses the required crew
            3b:it is less or equal to what is required. in this case, it adds available crew on this priority to the output.
         */
        ArrayList<Float> output = new ArrayList<Float>();
        for(int a = 0; a < Crews.size(); a++){
            output.add((float)0);//set output.size to the same length of crews, and make all values zero.
        }
        ArrayList<Integer> tempArray = new ArrayList<Integer>();
        for(int a = 0; a < crewPriority.size() && crewPowerRequired > 0; a++){
            //sets availbe crew power of this priority to output
            tempArray = crewPriority.get(a);
            ArrayList<Float> crewTemp = new ArrayList<Float>();
            float power = 0;
            //1
            for(int b = 0; b < tempArray.size(); b++) {
                int index = tempArray.get(b);
                crewTemp.add(Crews.get(index).getCrewInFleet(fleet));
                power += Crews.get(index).getCrewPowerInFleet(fleet);
            }
            //2
            if (power > crewPowerRequired){
                //3a
                ArrayList<Float> powerTemp = new ArrayList<Float>();
                for(int b = 0; b < tempArray.size(); b++) {
                    int index = tempArray.get(b);
                    powerTemp.add(Crews.get(index).crewPower);
                }
                crewTemp = getRandomNumberList(crewTemp,powerTemp,crewPowerRequired);
                crewPowerRequired = 0;
            }
            //3b
            crewPowerRequired -= power;
            for(int b = 0; b < tempArray.size(); b++){
                int index = tempArray.get(b);
                output.set(index,crewTemp.get(b));
            }
        }
        //already have all other items in array set to zero, so this ends here.
        return output;
    }
    /*public ArrayList<Float> getCrewForJobold(CampaignFleetAPI fleet, float crewPowerRequired){
        ArrayList<Float> output = new ArrayList<Float>();
        for(int a = 0; a < Crews.size(); a++){
            output.add((float)0);//set output.size to the same length of crews, and make all values zero.
        }
        ArrayList<Integer> tempArray = new ArrayList<Integer>();
        ArrayList<Float> tempArray2 = new ArrayList<Float>();
        Boolean overflow = false;
        int temp;
        //ArrayList<ArrayList<Integer>> crewToDoJob = crewPriority;
        //get crew that could be used in this job by priority, until i have more or equal crew power, to crewPowerRequired
        for(int a = 0; a < crewPriority.size() && crewPowerRequired > 0; a++){
            //sets availbe crew power of this priority to output
            tempArray = crewPriority.get(a);
            boolean crewOverflow = false;
            //System.out.println("priority " + a + " runing");
            for(int b = 0; b < tempArray.size() && crewPowerRequired > 0; b++){
                temp = tempArray.get(b);
                float temp1 = Crews.get(temp).getCrewPowerInFleet(fleet);
                int rquestedCrew = (int)(crewPowerRequired / Crews.get(temp).crewPower);
                float tempCrew = Crews.get(temp).getCrewInFleet(fleet);
                if(rquestedCrew < tempCrew && tempCrew != 0){
                    //System.out.println("        crew: " + temp + " overpowerd: " + rquestedCrew + "requested. " + tempCrew + " available, being of " + Crews.get(temp).crewPower + "power eatch. " + crewPowerRequired + " required");
                    if(b != tempArray.size() - 1){
                        crewOverflow = true;
                        //System.out.println("    OVERFLOW");
                    }
                    output.set(temp,(float)rquestedCrew);
                }else {
                    output.set(temp,Math.min(tempCrew,rquestedCrew));
                }
                //System.out.println("    adding " + output.get(temp) + " crew. " + Crews.get(temp).crewPower + " eatch");
                //System.out.println("    still required power: " + crewPowerRequired + " crew power removed here: " + output.get(temp) * Crews.get(temp).crewPower);
                crewPowerRequired-= output.get(temp) * Crews.get(temp).crewPower;
            }
            if(crewOverflow && crewPowerRequired > 0){
                for(int b = tempArray.size() - 1; b >= 0 && crewPowerRequired > 0; b--){
                    //System.out.println("    " + b + " runing: ");
                    temp = tempArray.get(b);
                    int rquestedCrew = (int)Math.max(crewPowerRequired / Crews.get(temp).crewPower,1);
                    float tempCrew = Crews.get(temp).getCrewInFleet(fleet) - output.get(temp);
                    float outcrew = Math.min(rquestedCrew,tempCrew);
                    output.set(temp,output.get(temp) + outcrew);
                    //System.out.println("        still required power: " + crewPowerRequired + " crew power removed here: " + outcrew * Crews.get(temp).crewPower);
                    crewPowerRequired -= outcrew * Crews.get(temp).crewPower;
                }
            }
            if(crewPowerRequired <= 0) {
                //everythings done break;
                break;
            }
        }
        //already have all other items in array set to zero, so this ends here.
        return output;
    }*/

    public String[] GetCrewNames(){
        String[] output = new String[Crews.size()];
        for(int a = 0; a < Crews.size(); a++){
            output[a] = Crews.get(a).name;
        }
        return output;
    }

    public void displayCrewAvailable(CampaignFleetAPI fleet,TextPanelAPI text){
        for(int a = 0; a < Crews.size(); a++){
            Crews.get(a).DisplayedCrewNumbers(Crews.get(a).getCrewInFleet(fleet),text);
            /*float crew = Crews.get(a).getCrewInFleet(fleet);
            if(crew == 1){
                text.appendToLastParagraph(message[0] + "" + crew + " " + Crews.get(a).name + "s");
            }else if(crew != 0){
                text.appendToLastParagraph(message[1] + "" + crew + " " + Crews.get(a).name);
            }*/
        }
    }
    private ArrayList<Float> getRandomNumberList(ArrayList<Float> maxCrew,ArrayList<Float> power,float input){
        //edited, inproved, and tested. only returns hole numbers now. still in flpat formthough cause im lazzy.

        //can missround numbers sometimes, so it ca be not 100% random, but close enuth
        //the recursive loop might be ponitless. i have never seen it do anything since i changes this loop to take hole numbers.
        //input: an array of numbers (crew),an array of more numbers(crew power) and an float(required power).
        //looks at the array of numbers, and randomly removes numbers untill input numbers have ben removed.
        //the randomness is basses on number of items in loop, hence the complecity.

        //proldom: the end while loop (to add numbers to the output) can fire a lot, if i only have two maxcrew types and get unlucky.
        //proldom: at high numbers of maxCrew types, i can overrull by one or two numbers of crew, dew to rounding. dont know how to fix that safely
        //never saw more then 2
        //init data
        float total = 0;
        float remainder = 0;
        float totalrandom = 0;
        float randomness = 0;
        ArrayList<Float> output = new ArrayList<Float>();
        ArrayList<Float> temp = new ArrayList<Float>();
        //get combined total of all numbers in maxCrew
        //get random numbers for every max number. set to zero if there is nothing for said max number (also remember total of all the random numbers)
        for(int a = 0; a < maxCrew.size(); a++){
            total += maxCrew.get(a) * power.get(a);

            if(maxCrew.get(a) == 0){
                temp.add((float)0);
            }else{
                temp.add((float)Math.random());
            }
            totalrandom += temp.get(a);
        }
        //min = power.get((int)min);
        //if maxCrew is grater then input, output maxCrew.
        if(total <= input){
            for(int a = 0; a < maxCrew.size(); a++){
                output.add(maxCrew.get(a));
            }
            return output;
        }
        //get random numbers for every max number. set to zero if there is nothing for said max number (also remember total of all the random numbers)
        //get the prodect of random numbers and input numbers, then devide by there total (get the prodect of a given slots % of there given values)
        for(int a = 0; a < temp.size(); a++){
            temp.set(a,((temp.get(a)) / totalrandom) * (((maxCrew.get(a) * power.get(a))) / total));
            randomness += temp.get(a);
        }
        //set temp to a % value of its self, so the sum is equal to 1. (or 100%), and multaply by input, to get required power from eatch crew type
        for(int a = 0; a < temp.size(); a++){
            temp.set(a,(temp.get(a) / randomness) * input);
        }
        //sets the output. if any output is grater then its input, change the output to be == to its input. remeber all outputs
        remainder = input;
        //total = 0;
        //int itemsCheack = 0;
        for(int a = 0; a < temp.size(); a++){
            float temp1 = temp.get(a) / power.get(a);//crew i want
            float temp2 = (float)Math.floor(temp1);
            temp1 = temp1 - temp2;//temp2 == crew here, temp1 == extra crew here to spend
            float temp3 = maxCrew.get(a);
            //if crew i want is graer then crew i have
            if((int)temp3 < temp1 + temp2){
                //System.out.println("path a");
                output.add((float)((int)temp3));
            }else{
                output.add(temp2);
                //System.out.println("reducing cost by: " + (temp2));
            }
            remainder -= (output.get(a) * power.get(a));
        }
        //if my code failed to do ANYTHING AT ALL, it gets one crew from each category that has crew remaining.
        while (remainder > 0) {//added as a test to try and remove the recursive loop
            //System.out.println("runing that loop again hahah");
            //total = 0;
            for (int a = 0; a < output.size() && remainder > 0; a++) {
                if (maxCrew.get(a) - output.get(a) != 0) {
                    output.set(a, output.get(a) + 1);
                    remainder -= (power.get(a));
                    //System.out.println("    " + a);

                    //System.out.println("remainder:" + remainder);
                    //temp.set(a,(maxCrew.get(a) - output.get(a)));
                    /*total += (maxCrew.get(a) - output.get(a)) * power.get(a);*/
                }
            }
            /*for(int a = 0;a < output.size(); a++){
                // crewpower / total    my_computation/remainder
                float temp1 = (float)(((remainder * ((maxCrew.get(a) - output.get(a)) * power.get(a))) / total) / power.get(a));
                output.set(a,output.get(a) + temp1);
                remainder -= temp1 * (temp1 * power.get(a));
            }*/
        }
        //}
        //if i didnt get enuth crew, run this code again, with the leftover crew, and leftover required input
        /*if(remainder > 0){
            temp = getRandomNumberList(temp,power,remainder);
            for(int a = 0; a < output.size(); a++){
                output.set(a,output.get(a) + temp.get(a));
            }
        }*/
        return output;
    }
}
