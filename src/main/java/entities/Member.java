package entities;

import java.util.Objects;

public class Member {

    private int memberId;
    private String name;
    private String address;
    private int zip;
    private String city;
    private String gender;
    private int year;

    public Member(String name, String address, int zip, String gender, int year) {
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.gender = gender;
        this.year = year;
    }

    public Member(String name, String address, int zip, String city, String gender, int year) {
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.city = city;
        this.gender = gender;
        this.year = year;
    }

    public Member(int memberId, String name, String address, int zip, String city, String gender, int year) {
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.gender = gender;
        this.year = year;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCity(){
        return city;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return getMemberId() == member.getMemberId() && getZip() == member.getZip() && getYear() == member.getYear() && getName().equals(member.getName()) && getAddress().equals(member.getAddress()) && getCity().equals(member.getCity()) && getGender().equals(member.getGender());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMemberId(), getName(), getAddress(), getZip(), getCity(), getGender(), getYear());
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", zip=" + zip +
                ", gender='" + gender + '\'' +
                ", year=" + year +
                '}';
    }
}
