package com.smarthuman.drstock;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by yuxuangu on 2018/4/22.
 */

public class InvestmentPlan {

    String enquiryId;
    double baseVolum;
    public static TreeMap<String, InvestmentPlan> planTreeMap;

    private InvestmentPlan(String eId, double bv) {
        // initialize the planTreeMap of current user
        enquiryId = eId;
        baseVolum = bv;
    }

    public static boolean addPlan(String eId, double bv) {
        planTreeMap.put(eId, new InvestmentPlan(eId, bv));
        return true;
    }

    public static boolean deletePlan(String eId) {
        InvestmentPlan ip = planTreeMap.get(eId);
        if (ip != null) {
            planTreeMap.remove(eId);
            return true;
        }
        return false;
    }

    public static boolean changePlan(String eId, double bv) {
        InvestmentPlan ip = planTreeMap.get(eId);
        if (ip != null) {
            ip.baseVolum = bv;
            return true;
        }
        return false;
    }

    public static boolean check() {
        Collection<InvestmentPlan> plans = planTreeMap.values();
        for (InvestmentPlan plan : plans) {
            check(plan);
        }
        return true;
    }

    public static boolean check(InvestmentPlan ip) {
        return true;
    }
}
