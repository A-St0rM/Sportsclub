package DTO;

import entities.Member;
import entities.Sport;

import java.util.ArrayList;
import java.util.List;

public class MemberAndSportsDTO {

    private Member member;
    private Sport sport;
    private List<Sport> sports;

    public MemberAndSportsDTO(Member member, Sport sport, List<Sport> sportList) {
        this.member = member;
        this.sport = sport;
        this.sports = sportList;
    }

    public MemberAndSportsDTO(Member member, List<Sport> sports){
        this.member = member;
        this.sports = sports;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<Sport> getSportList() {
        return sports;
    }

    public void setSportList(List<Sport> sportList) {
        this.sports = sportList;
    }

    @Override
    public String toString() {
        return "MemberAndSportsDTO{" +
                "member=" + member +
                ", sports=" + sports +
                '}';
    }
}
