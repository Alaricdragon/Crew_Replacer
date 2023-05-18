package data.scripts;

import java.util.ArrayList;

public class CrewReplacer_organizeArrayCode {
    public static void addAItemToSortedArray(ArrayList<Float> inArray, ArrayList<Float> valueArray, float newItem, float newValue){
        //console.log("runing addAITemToSortedArray");
        //console.log("current values: " + valueArray);
        //console.log("new value: " + newValue);
        float value = newValue;
        int a = inArray.size();
        int b = (int)Math.floor((a)/2);
        int lastB = -1;
        int max = a;
        int min = 0;

        boolean done = false;
        float compaireValue;
        while(max != min){
            b = (int)Math.floor((max + min) / 2);
            compaireValue = valueArray.get(b);
            //console.log("data scaned here: " + compaireValue + " at " + b + " in size of " + valueArray.length + ". sizes are: " + min + ", " + max);
            //console.log("looking at index " + b + " value " + compaireValue + "...");
            if(value > compaireValue){
                min = b;
                //values greater. move b up by 50% remaining space.
            }else if(value < compaireValue){
                max = b;

                //values less. move b down by 50% remaining space
            }else{
                //values match. set this item next to b
                //console.log("   value match found with at index " + b + ", of value " + compaireValue + " to value " + value + ". match type: same. current value list: " + valueArray );
                inArray.add(b,newItem);
                valueArray.add(b,value);
                done = true;
                return;
            }
            if(lastB == b){
                //console.log("b never moved. runing error handler...");
                if(value > compaireValue){
                    max--;
                }else{
                    min++;
                }
                //data didnt move.. what now?
            }
            lastB = b;
        }
        if(min == max && !done){
            if(value > valueArray.get(max)){
                //value needs to go after b.
                //console.log("   value match found with at index " + (b+1) + ", of value " + compaireValue + " to value " + value + ". match type: end: more. current value list: " + valueArray);
                inArray.add(b+1,newItem);
                valueArray.add(b+1,value);
            }else{
                //value needs to go before b.
                //console.log("   value match found with at index " + b + ", of value " + compaireValue + " to value " + value + ". match type: end: less. current value list: " + valueArray);
                inArray.add(b,newItem);
                valueArray.add(b,value);
            }
            return;
        }
        //console.log("ERROR in addAItemToSortedArray. no item added...");
    };
    public static ArrayList<ArrayList<Float>> sortArray(ArrayList<Float>inArray,ArrayList<Float>valueArray){
        /* input is:
        array = array im organizing.
        valueArray = a array of all the values of each item from inArray
        leftRight, wether or not im organizing them largest to lowest, or lowest to largest. */
        /*changes the inputed array's to be organized from gratest to lowest*/
        //console.log("inputed values: " + valueArray);

        ArrayList<Float> newArray = new ArrayList<Float>();
        newArray.add(inArray.get(0));
        ArrayList<Float> newValueArray = new ArrayList<Float>();
        newValueArray.add(valueArray.get(0));
        //console.log("sorting " + inArray.length + " and " + valueArray.length + "items");
        for(int a = 1; a < inArray.size(); a++){
            //console.log("adding item to new array.");
            float itemTemp = inArray.get(a);
            float valueTemp = valueArray.get(a);
            addAItemToSortedArray(newArray,newValueArray,itemTemp,valueTemp);
            //console.log(newArray.length + " items in newArray");
        }
        //console.log("outputed values: " + newValueArray);
        ArrayList<ArrayList<Float>> output = new ArrayList<>();
        CrewReplacer_Log.loging("output: " + newArray.toString(),new CrewReplacer_organizeArrayCode().getClass(),true);
        CrewReplacer_Log.loging("value: " + valueArray.toString(),new CrewReplacer_organizeArrayCode().getClass(),true);
        output.add(newArray);
        output.add(newValueArray);
        return output;//new Object[]{newArray,newValueArray};
    }
}
