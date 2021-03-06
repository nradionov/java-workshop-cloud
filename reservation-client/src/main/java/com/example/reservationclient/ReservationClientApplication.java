package com.example.reservationclient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableBinding(Source.class)
@EnableAutoConfiguration
@EnableZuulProxy
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

	@Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}



@RestController
@RequestMapping("/reservations")
class ReservationsApiGatewayRestController {

    @Autowired
	private RestTemplate restTemplate;


    @Autowired
    private Source outputSource;

    @RequestMapping(method = RequestMethod.POST)
    public void write(@RequestBody Reservation reservation) {
        MessageChannel messageChannel = outputSource.output();
        messageChannel.send(MessageBuilder.withPayload(reservation.getName()).build());
    }


    public List<String> geNamesFallback(){
        return Collections.emptyList();
    }

    @HystrixCommand(fallbackMethod = "geNamesFallback")
    @RequestMapping(value = "/names", method = RequestMethod.GET)
    public List<String> geNames() {

        ParameterizedTypeReference<Resources<Reservation>> reference = new ParameterizedTypeReference<Resources<Reservation>>() {
        };
        ResponseEntity<Resources<Reservation>> exchange = restTemplate.exchange("http://reservation-service/reservations",
                HttpMethod.GET,
                null,
                reference
        );
        return exchange
                .getBody()
                .getContent()
                .stream()
                .map(Reservation::getName)
                .collect(Collectors.toList());
    }
}

class Reservation {

	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
