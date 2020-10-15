package com.conversorback.api;



import com.conversorback.api.services.GetRequestBitso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConversorbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConversorbackApplication.class, args);

		try {
			//JsoupHtml.obtenerEuro();
			
			System.out.println(GetRequestBitso.getRequest());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
