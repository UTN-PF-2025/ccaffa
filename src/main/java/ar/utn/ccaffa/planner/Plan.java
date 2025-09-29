package ar.utn.ccaffa.planner;

public class Plan<A,B> {
    public final A ordenesDeTrabajo;
    public final B rollosHijos;
    public Plan(A a, B b){
        ordenesDeTrabajo =a;
        rollosHijos =b;}
}

