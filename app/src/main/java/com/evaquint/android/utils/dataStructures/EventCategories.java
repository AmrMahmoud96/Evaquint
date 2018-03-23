package com.evaquint.android.utils.dataStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by amrmahmoud on 2018-03-23.
 */

public class EventCategories {
    private HashMap<String,ArrayList<String>> categories;
    public EventCategories(String[] categoryArray){
        this.categories = new HashMap<>();
        String category;
        ArrayList<String> subCategories;
        for(String cat: categoryArray){
            String[] splitItem = cat.split("\\|");
            category = splitItem[0];
            if(splitItem.length<=1){
                subCategories = null;
            }else{
                subCategories = new ArrayList<>(Arrays.asList(splitItem[1].split(",")));
            }
            categories.put(category,subCategories);
        }
    }

    public HashMap<String, ArrayList<String>> getCategories() {
        return categories;
    }
}
