package dev.controller;

import java.util.Properties;

import javax.xml.bind.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.configuration.MailConf;
import dev.entite.User;

@RestController
@RequestMapping(value = "/sendemail")
public class MailController {

	private MailConf mailConf;

	public MailController(MailConf mailConf) {
		this.mailConf = mailConf;
	}

	@PostMapping
	public ResponseEntity<?> sendFeedback(@RequestBody User user, BindingResult bindingResult)
			throws ValidationException {
		if (bindingResult.hasErrors()) {
			throw new ValidationException("Feedback is not valid");
		}

		// Create a mail sender
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(this.mailConf.getHost());
		mailSender.setPort(this.mailConf.getPort());
		mailSender.setUsername(this.mailConf.getUsername());
		mailSender.setPassword(this.mailConf.getPassword());

		Properties prop = mailSender.getJavaMailProperties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.socketFactory.port", "465"); // SSL Port
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL
																						// Factory
																						// Class
		prop.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		prop.put("mail.smtp.port", "465"); // SMTP Port
		prop.put("mail.debug", "true");

		// Create an email instance
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(user.getEmail());
		mailMessage.setTo("contact.maitrevogt@gmail.com");
		mailMessage.setSubject("message de la part de: " + user.getNom() + " " + user.getPrenom() + " - "
				+ user.getEmail() + " - " + user.getTelephone());
		mailMessage.setText(user.getMessage());

		// Send mail
		mailSender.send(mailMessage);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("message envoyé");
	}
}
