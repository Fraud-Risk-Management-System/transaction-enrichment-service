package com.fraudrisk.enrichment.service;

import com.fraudrisk.enrichment.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeoAnalysisService {

    public Map<String, Object> analyzeLocation(Transaction transaction) {
        log.debug("Analyzing geo location data for transaction: {}", transaction.getTransactionId());

        Map<String, Object> geoFeatures = new HashMap<>();

        try {
            String location = transaction.getLocation();
            String ipAddress = transaction.getIpAddress();

            // Parse location data (assuming format like "country:city:lat:long")
            if (location != null && !location.isEmpty()) {
                String[] locationParts = location.split(":");
                if (locationParts.length >= 4) {
                    String country = locationParts[0];
                    String city = locationParts[1];
                    double latitude = Double.parseDouble(locationParts[2]);
                    double longitude = Double.parseDouble(locationParts[3]);

                    geoFeatures.put("country", country);
                    geoFeatures.put("city", city);
                    geoFeatures.put("latitude", latitude);
                    geoFeatures.put("longitude", longitude);

                    // Determine if this is a high-risk country
                    geoFeatures.put("isHighRiskCountry", isHighRiskCountry(country));

                    // Add timezone info based on location
                    geoFeatures.put("timezone", estimateTimezone(longitude));
                }
            } else {
                geoFeatures.put("locationMissing", true);
            }

            // IP address analysis
            if (ipAddress != null && !ipAddress.isEmpty()) {
                // In a real implementation, this would use a geo-IP database or service
                // Here we're doing a simple check for demo purposes
                geoFeatures.put("ipType", determineIpType(ipAddress));
                geoFeatures.put("isTorExit", isTorExitNode(ipAddress));
                geoFeatures.put("isProxy", isProxy(ipAddress));
                geoFeatures.put("ipRiskScore", calculateIpRiskScore(ipAddress));
            } else {
                geoFeatures.put("ipMissing", true);
            }

            log.debug("Geo analysis completed for transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error in geo analysis for transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
            geoFeatures.put("error", e.getMessage());
            geoFeatures.put("isHighRiskCountry", false);
            geoFeatures.put("ipRiskScore", 0.5);
        }

        return geoFeatures;
    }

    private boolean isHighRiskCountry(String country) {
        // In a real implementation, this would check against a list of high-risk countries
        // For demo purposes, we're checking a few examples
        if (country == null) return false;

        String upperCountry = country.toUpperCase();
        return upperCountry.equals("NG") || upperCountry.equals("UA") ||
                upperCountry.equals("RU") || upperCountry.equals("BY");
    }

    private String estimateTimezone(double longitude) {
        // Simple timezone estimation based on longitude
        // In a real implementation, would use a proper timezone database
        int timezoneHours = (int) Math.round(longitude / 15.0);
        return "GMT" + (timezoneHours >= 0 ? "+" : "") + timezoneHours;
    }

    private String determineIpType(String ipAddress) {
        if (ipAddress.startsWith("192.168.") ||
                ipAddress.startsWith("10.") ||
                ipAddress.startsWith("172.16.")) {
            return "PRIVATE";
        }
        return "PUBLIC";
    }

    private boolean isTorExitNode(String ipAddress) {
        // In a real implementation, this would check against a Tor exit node database
        // For demo purposes, we're returning false
        return false;
    }

    private boolean isProxy(String ipAddress) {
        // In a real implementation, this would check against a proxy database
        // For demo purposes, we're doing a simple check
        return ipAddress.startsWith("34.") || ipAddress.contains(".tor.");
    }

    private double calculateIpRiskScore(String ipAddress) {
        // In a real implementation, this would calculate a risk score based on IP reputation
        // For demo purposes, we're returning a random score
        return 0.2; // Default low score
    }
}