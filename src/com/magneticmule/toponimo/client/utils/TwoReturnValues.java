package com.magneticmule.toponimo.client.utils;

public class TwoReturnValues<F, S> {

    public F firstVal;
    public S secondVal;

    public F getFirstVal() {
	return this.firstVal;
    }

    public S getSecondVal() {
	return this.secondVal;
    }

}
