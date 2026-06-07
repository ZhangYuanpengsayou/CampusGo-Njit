package com.zane.common.constant;

public final class StatusConstants {

    public static final class Leave {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String CANCELED = "CANCELED";
        public static final String RETURNED = "RETURNED";

        private Leave() {
        }
    }

    public static final class Repair {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
        public static final String CANCELED = "CANCELED";
        public static final String REPAIRING = "REPAIRING";
        public static final String COMPLETED = "COMPLETED";
        public static final String RATED = "RATED";

        private Repair() {
        }
    }

    private StatusConstants() {
    }
}
