package com.smarthuman.drstock;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by yuxuangu on 2018/4/22.
 */

public class InvestmentPlan {

    public String getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    public double getBaseVolumn() {
        return baseVolumn;
    }

    public void setBaseVolumn(double baseVolumn) {
        this.baseVolumn = baseVolumn;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String frequency;
    public String name;
    public String enquiryId;
    public double baseVolumn;
    public static TreeMap<String, InvestmentPlan> planTreeMap = new TreeMap<>();

    public InvestmentPlan(String eId, String n, double bv, String f) {
        // initialize the planTreeMap of current user
        enquiryId = eId;
        name = n;
        baseVolumn = bv;
        frequency = f;
    }

    public static boolean addPlan(InvestmentPlan plan) {
        planTreeMap.put(plan.enquiryId, plan);
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
            ip.baseVolumn = bv;
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
