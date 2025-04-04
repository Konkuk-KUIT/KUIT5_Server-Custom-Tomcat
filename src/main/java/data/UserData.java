package data;

public enum UserData {
    ID("userId"),Password("password"),Email("email"),Username("name");

    final String data;
    UserData(String data){
        this.data=data;
    }
    public String getData(){
        return data;
    }
}
