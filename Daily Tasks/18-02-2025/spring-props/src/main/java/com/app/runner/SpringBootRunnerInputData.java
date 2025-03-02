package com.app.runner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SpringBootRunnerInputData implements CommandLineRunner {
	
	@Value("${my.info.product.id}")
	private int prodId;
	@Value("${my.info.product.code}")
	private String prodCode;
	@Value("${my.info.product.model-version}")
	private double modelver;
	@Value("${my.info.product.release_enable}")
	private boolean isDetEnable;
	@Value("${my.info.product.start-key}")
	private char startKey;
	
	@Override
	public String toString() {
		return "SpringBootRunnerInputData [prodid=" + prodId + ",prodCode=" + prodCode + ",modelVer=" + modelver + ",isDetEnable=" + isDetEnable + ",startKey=" + startKey + "]";
	}
	
	public void run(String...args) throws Exception{
		System.out.println(this);
		//System.out.println(this.toString());
	}

}
