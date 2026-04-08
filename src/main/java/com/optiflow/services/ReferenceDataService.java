package com.optiflow.services;

import java.util.List;

public class ReferenceDataService {

    public List<String> getTaskPriorities() {
        return List.of("Low", "Medium", "High", "Critical");
    }

    public String getDefaultTaskPriority() {
        return getTaskPriorities().get(1);
    }

    public String getDefaultTaskStatus() {
        return "Pending";
    }

    public String getDefaultProjectStatus() {
        return "PLANNED";
    }
}