package entities.Users;

import storage.Storable;

public class Individual extends Customer {
    
    public Individual(String vat, String legalName, String userName, String password, String userType){
        
        super(vat, legalName, userName, password, "Individual");
        
    }

    public Individual(){}

    @Override
    public String marshal(){
        return "type:" + this.getClass().getSimpleName() +
                ",legalName:" + legalName +
                ",userName:" + userName +
                ",password:" + password +
                ",vatNumber:" + vat;

    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "legalName":
                    this.legalName = value;
                    break;
                case "userName":
                    this.userName = value;
                    break;
                case "password":
                    this.password = value;
                    break;
                case "vatNumber":
                    this.vat = value;
                    break;
                case "type":
                    this.userType = value;
                    break;
                // "type" is ignored
            }
        }
    }

}
