package com.examples;

import org.aopalliance.intercept.*;

public class TrackOperation {
	public void myadvice(Joinpoint jp) {
		System.out.println("After the Bean method im executed");

  }
}