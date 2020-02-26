package ru.trueengineering;

import java.util.HashMap;
import java.util.Map;

public class Mapping {

    private static Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("Target MS_PORTAL Method AvailabilityController.load", "Availability");
        map.put("Target MS_PORTAL Method SellFlightController.pricing", "Pricing");
        map.put("Target MS_PORTAL Method BookingController.bookAndSave", "Booking");
        map.put("Target MS_PORTAL Method TicketController.issueAll", "Issue");
    }

    public static String getSemantic(String logMessage) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (logMessage.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static boolean hasSemantic(String logMessage) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (logMessage.startsWith(entry.getKey())) {
                return true;
            }
        }
        return false;
    }
}
