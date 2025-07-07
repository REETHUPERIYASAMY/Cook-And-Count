// File: WhistleDetector.java (Optional helper class for detection)
// This file can be expanded in future for AI/ML-based detection logic
package com.example.cookandcount;

public class WhistleDetector {
    public static boolean isWhistle(int amplitude, int threshold) {
        return amplitude > threshold;
    }
}
