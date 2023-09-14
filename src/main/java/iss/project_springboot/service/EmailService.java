package iss.project_springboot.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

import iss.project_springboot.model.EmailDetails;


@Service
public class EmailService implements EmailInterface {

	@Autowired private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}") 
	private String sender;

	public void sendSimpleMail(EmailDetails details)
	{

		try {

			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setFrom(sender);
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(details.getSubject());

			javaMailSender.send(mailMessage);
            System.out.println("success mail");
		}

		
		catch (Exception e) {
			System.out.println("error mail");
		}
	}


}


