package com.chargeapp.whc.chargeapp.Model;

/**
 * Created by 1709008NB01 on 2017/12/21.
 */

public class CarrierVO {

    private String CarNul;
    private String password;
    private int id;
    private int FirstMonth;
    private int FirstYear;
    private boolean SecondMonth;
    private boolean ThirdMonth;
    private boolean FourthMonth;
    private boolean FifthMonth;
    private boolean SixthMonth;

    public int getFirstMonth() {
        return FirstMonth;
    }

    public void setFirstMonth(int firstMonth) {
        FirstMonth = firstMonth;
    }

    public int getFirstYear() {
        return FirstYear;
    }

    public void setFirstYear(int firstYear) {
        FirstYear = firstYear;
    }

    public boolean isSecondMonth() {
        return SecondMonth;
    }

    public void setSecondMonth(boolean secondMonth) {
        SecondMonth = secondMonth;
    }

    public boolean isThirdMonth() {
        return ThirdMonth;
    }

    public void setThirdMonth(boolean thirdMonth) {
        ThirdMonth = thirdMonth;
    }

    public boolean isFourthMonth() {
        return FourthMonth;
    }

    public void setFourthMonth(boolean fourthMonth) {
        FourthMonth = fourthMonth;
    }

    public boolean isFifthMonth() {
        return FifthMonth;
    }

    public void setFifthMonth(boolean fifthMonth) {
        FifthMonth = fifthMonth;
    }

    public boolean isSixthMonth() {
        return SixthMonth;
    }

    public void setSixthMonth(boolean sixthMonth) {
        SixthMonth = sixthMonth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarNul() {
        return CarNul;
    }

    public void setCarNul(String carNul) {
        CarNul = carNul;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
