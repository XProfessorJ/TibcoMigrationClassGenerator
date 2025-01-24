package com.example.tibcomigrationclassgenerator.model;


public class APIHeader {
    private ClientDetails clientDetails;  // 确保字段名是 clientDetails

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    public class ClientDetails {
        private String terminalID;  // 确保字段名是 terminalID
        private String userID;      // 确保字段名是 userID
        private String destCountryCode;

        public String getDestCountryCode() {
            return destCountryCode;
        }

        public void setDestCountryCode(String destCountryCode) {
            this.destCountryCode = destCountryCode;
        }

        public String getTerminalID() {

            return terminalID;
        }

        public void setTerminalID(String terminalID) {
            this.terminalID = terminalID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }
    }
}

