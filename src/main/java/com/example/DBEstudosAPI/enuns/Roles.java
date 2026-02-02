package com.example.DBEstudosAPI.enuns;

public enum Roles {

    ADMIN("admin"),
    USER("user");

    private final String role;

    Roles(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public static Roles value(String roles){
        for(Roles value : Roles.values()){
            if(roles.equals(value.getRole())){
                return value;
            }
        }
        throw new IllegalArgumentException("Erro na role.");
    }
}
