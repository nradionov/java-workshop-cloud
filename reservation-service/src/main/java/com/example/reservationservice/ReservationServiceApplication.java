package com.example.reservationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.stream.Stream;

@EnableBinding(Sink.class)
@EnableAutoConfiguration
@IntegrationComponentScan
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}
@Component
class ReservationInitializer implements CommandLineRunner {

	private ReservationRepository reservationRepository;

	@Autowired
	public ReservationInitializer(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of("John", "Bob")
				.map(Reservation::new)
				.forEach(reservationRepository::save);

		reservationRepository.findAll().forEach(System.out::println);
	}
}


@MessageEndpoint
class ReservationMessageAcceptor {

	@Autowired
	private ReservationRepository reservationRepository;

	@ServiceActivator(inputChannel = Sink.INPUT)
	public void accept(String name) {
		reservationRepository.save(new Reservation(name));
	}
}

@RestController
@Scope("request")
class MessageController {

	@Value("${message}")
	private String message;

	@RequestMapping("/message")
	public String message(){
		return this.message;
	}
}

@RepositoryRestResource
@Repository
interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@RestResource(path = "reservations")
	List<Reservation> findByName(String name);

}

@Entity
class Reservation {

	public Reservation() {
	}

	public Reservation(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
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

	@Override
	public String toString() {
		return "Reservation{" +
				"name='" + name + '\'' +
				'}';
	}
}
