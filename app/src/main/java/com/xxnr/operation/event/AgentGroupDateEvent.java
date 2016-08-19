package com.xxnr.operation.event;

/**
 * Created by CAI on 2016/8/12.
 */
public class AgentGroupDateEvent {
    public String dateStart;
    public String dateEnd;

    public AgentGroupDateEvent(String dateStart, String dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }
}
