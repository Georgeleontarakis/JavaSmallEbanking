package entities.Users;

import storage.Storable;

public class Admin extends User implements Storable {

    private static Admin instance;

    private Admin(String legalName, String userName, String password) {
        super(legalName, userName, password, "Admin");
    }

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin("Bank Administrator", "admin", "123456");
        }
        return instance;
    }

    @Override
    public String marshal(){
        return "type:" + this.getClass().getSimpleName() +
                ",legalName:" + legalName +
                ",userName:" + userName +
                ",password:" + password;

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
                // "type" is ignored
            }
        }
    }


    public static void resetInstance() {
        instance = null;
    }
}
